package com.akcreation.gitsilent.fileeditor.texteditor.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.akcreation.gitsilent.constants.IndentChar
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.fileeditor.texteditor.view.SearchPos
import com.akcreation.gitsilent.fileeditor.texteditor.view.SearchPosResult
import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.MyCodeEditor
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.StylesResult
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.StylesUpdateRequest
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.SyntaxHighlightResult
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.scopeInvalid
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.EditCache
import com.akcreation.gitsilent.utils.EncodingUtil
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.doActIfIndexGood
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import com.akcreation.gitsilent.utils.generateRandomString
import com.akcreation.gitsilent.utils.getNextIndentByCurrentStr
import com.akcreation.gitsilent.utils.isGoodIndexForList
import com.akcreation.gitsilent.utils.isGoodIndexForStr
import com.akcreation.gitsilent.utils.parseLongOrDefault
import com.akcreation.gitsilent.utils.tabToSpaces
import io.github.rosemoe.sora.text.CharPosition
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.OutputStream

private const val TAG = "TextEditorState"
private const val lb = "\n"
private fun targetIndexValidOrThrow(targetIndex:Int, listSize:Int) {
    if (targetIndex < 0 || targetIndex >= listSize) {
        throw IndexOutOfBoundsException("targetIndex '$targetIndex' out of range '[0, $listSize)'")
    }
}
@Immutable
class TextEditorState(
    val fieldsId: String = newId(),
    val fields: List<MyTextFieldState> = listOf(),
    val selectedIndices: List<Int> = listOf(),
    val isMultipleSelectionMode: Boolean = false,
    val focusingLineIdx: Int? = null,
    val isContentEdited: MutableState<Boolean>,  
    val editorPageIsContentSnapshoted:MutableState<Boolean>,
    private val onChanged: suspend (newState:TextEditorState, trueSaveToUndoFalseRedoNullNoSave:Boolean?, clearRedoStack:Boolean, caller: TextEditorState, from: EditorStateOnChangeCallerFrom?) -> Unit,
    val codeEditor: MyCodeEditor?,
) {
    private val lock = Mutex()
    fun copy(
        fieldsId: String = this.fieldsId,
        fields: List<MyTextFieldState> = this.fields,
        selectedIndices: List<Int> = this.selectedIndices,
        isMultipleSelectionMode: Boolean = this.isMultipleSelectionMode,
        focusingLineIdx: Int? = this.focusingLineIdx,
        isContentEdited: MutableState<Boolean> = this.isContentEdited,
        editorPageIsContentSnapshoted:MutableState<Boolean> = this.editorPageIsContentSnapshoted,
        onChanged: suspend (newState:TextEditorState, trueSaveToUndoFalseRedoNullNoSave:Boolean?, clearRedoStack:Boolean, caller: TextEditorState, from: EditorStateOnChangeCallerFrom?) -> Unit = this.onChanged,
        codeEditor: MyCodeEditor? = this.codeEditor,
    ) = TextEditorState(
        fieldsId = fieldsId,
        fields = fields,
        selectedIndices = selectedIndices,
        isMultipleSelectionMode = isMultipleSelectionMode,
        focusingLineIdx = focusingLineIdx,
        isContentEdited = isContentEdited,
        editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
        onChanged = onChanged,
        codeEditor = codeEditor,
    )
    suspend fun undo(undoStack: UndoStack) {
        undoOrRedo(undoStack, true)
    }
    suspend fun redo(undoStack: UndoStack) {
        undoOrRedo(undoStack, false)
    }
    private suspend fun undoOrRedo(undoStack: UndoStack, trueUndoFalseRedo:Boolean) {
        lock.withLock {
            val lastState = if(trueUndoFalseRedo) undoStack.undoStackPop() else undoStack.redoStackPop()
            if(lastState != null) {
                isContentEdited.value = isContentEdited.value || fieldsId != lastState.fieldsId
                editorPageIsContentSnapshoted?.value=false
                val clearRedoStack = false
                onChanged(lastState, !trueUndoFalseRedo, clearRedoStack, this, null)
            }
        }
    }
    fun doSearch(keyword:String, toNext:Boolean, startPos: SearchPos): SearchPosResult {
        val funName="doSearch"
        fun getCurTextOfIndex(idx:Int, list:List<MyTextFieldState>):String{
            return list[idx].value.text.lowercase()
        }
        try {
            val f = fields
            if(f.isEmpty() || keyword.isEmpty()) {
                return SearchPosResult.NotFound
            }
            val goodIndex = isGoodIndexForList(startPos.lineIndex, f)
            val curPos = startPos.copy(lineIndex = if(goodIndex) startPos.lineIndex else {if(toNext) 0 else f.size-1})
            var curText = getCurTextOfIndex(curPos.lineIndex, f)
            curPos.columnIndex = if(goodIndex) startPos.columnIndex else {if(toNext) 0 else curText.length-1}
            val endPosOverThis = curPos.copy()  
            var curIndexOfKeyword= if(toNext) 0 else keyword.length-1
            curPos.columnIndex = if(goodIndex && isGoodIndexForStr(curPos.columnIndex, curText)){
                curPos.columnIndex
            }else{
                if(toNext){
                    0
                }else {
                    curText.length-1
                }
            }
            var char = if(curText.isEmpty()) null else curText[curPos.columnIndex]
            var charOfKeyword = keyword[curIndexOfKeyword]
            var looped = false
            val invalidIndex = -1
            var resetIndex = invalidIndex
            while(true) {
                if(char != null && char == charOfKeyword) {
                    if(toNext){
                        curIndexOfKeyword++
                    }else {
                        curIndexOfKeyword--
                    }
                    if(!isGoodIndexForStr(curIndexOfKeyword, keyword)) {
                        val foundLineIdx = curPos.lineIndex
                        val foundColumnIdx = if(toNext) curPos.columnIndex+1-keyword.length else curPos.columnIndex
                        var nextColumn = if(toNext) foundColumnIdx+keyword.length else foundColumnIdx-1
                        var nextLineIdx = foundLineIdx
                        if(!isGoodIndexForStr(nextColumn, curText)) {
                            nextLineIdx = if(toNext) nextLineIdx+1 else nextLineIdx-1
                            if(!isGoodIndexForList(nextLineIdx, f)) {
                                nextLineIdx = if(toNext) 0 else f.size-1
                            }
                            nextColumn = if(toNext) 0 else getCurTextOfIndex(nextLineIdx, f).length-1
                        }
                        return SearchPosResult(
                            foundPos = curPos.copy(lineIndex = foundLineIdx, columnIndex = foundColumnIdx),
                            nextPos = SearchPos(lineIndex =  nextLineIdx, columnIndex = nextColumn)
                        )
                    }
                    if(toNext) {
                        curPos.columnIndex++
                    }else {
                        curPos.columnIndex--
                    }
                    if(resetIndex == invalidIndex) {
                        resetIndex = curPos.columnIndex
                    }
                }else {  
                    curIndexOfKeyword= if(toNext) 0 else keyword.length-1
                    if(resetIndex == invalidIndex) {  
                        if(toNext) {
                            curPos.columnIndex++
                        }else {
                            curPos.columnIndex--
                        }
                    }else {  
                        curPos.columnIndex = resetIndex
                        resetIndex = invalidIndex
                    }
                }
                charOfKeyword = keyword[curIndexOfKeyword]
                if(!isGoodIndexForStr(curPos.columnIndex, curText)) {  
                    if(looped) {  
                        return SearchPosResult.NotFound
                    }
                    resetIndex = invalidIndex
                    curIndexOfKeyword= if(toNext) 0 else keyword.length-1
                    charOfKeyword = keyword[curIndexOfKeyword]
                    if(toNext) curPos.lineIndex++ else curPos.lineIndex--
                    if(!isGoodIndexForList(curPos.lineIndex, f)){
                        curPos.lineIndex = if(toNext) 0 else f.size-1
                    }
                    curText = getCurTextOfIndex(curPos.lineIndex, f)
                    curPos.columnIndex = if(toNext) 0 else curText.length-1
                    if(curPos.lineIndex==endPosOverThis.lineIndex) { 
                        looped = true
                    }
                }
                char = if(curText.isEmpty()) null else curText[curPos.columnIndex]
            }
        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: "+e.localizedMessage)
            MyLog.e(TAG, "#$funName err: keyword=$keyword, toNext=$toNext, startPos=$startPos, err=${e.stackTraceToString()}")
            return SearchPosResult.NotFound
        }
    }
    suspend fun splitNewLine(
        targetIndex: Int,
        textFieldValue: TextFieldValue,
        updater:((newLinesRange: IntRange, newFields: MutableList<MyTextFieldState>, newSelectedIndices: MutableList<Int>) -> Unit)? = null
    ) {
        lock.withLock {
            try {
                targetIndexValidOrThrow(targetIndex, fields.size)
            }catch (e: Exception) { 
                MyLog.d(TAG, "TextEditorState.splitNewLine() err: ${e.stackTraceToString()}")
                return
            }
            if (!textFieldValue.text.contains(lb)) {
                return
            }
            val oldLine = fields[targetIndex]
            val newText = textFieldValue.text
            val (oldTextLeft, oldTextRight) = oldLine.value.let {
                it.text.let { text ->
                    Pair(text.substring(0, it.selection.min), text.substring(it.selection.max))
                }
            }
            val oldText = oldTextLeft + oldTextRight
            val splitFieldValues = splitTextsByNL(newText, oldText)
            val newFields = fields.toMutableList()
            val splitFirstLine = splitFieldValues.first()
            val newLineAtOldLineHead = oldLine.value.text.isNotEmpty() && splitFirstLine.text.isEmpty()
            newFields[targetIndex] = oldLine.copy(value = splitFirstLine).apply {
                if(newLineAtOldLineHead) {  
                    updateLineChangeType(LineChangeType.NEW)
                }else if(oldLine.changeType == LineChangeType.NONE && this.value.text.let { t ->
                        oldLine.value.text.let { oldT ->
                            t.length != oldT.length || t != oldT
                        }
                    }
                ) {
                    updateLineChangeType(LineChangeType.UPDATED)
                } 
            }
            val newSplitFieldValues = splitFieldValues.subList(1, splitFieldValues.size)
            val newSplitFieldStates = newSplitFieldValues.map { MyTextFieldState(value = it, changeType = LineChangeType.NEW) }
            if(newLineAtOldLineHead) {
                newSplitFieldStates[newSplitFieldStates.size - 1].apply {
                    updateLineChangeType(oldLine.changeType)
                    if(oldLine.changeType == LineChangeType.NONE && this.value.text != oldLine.value.text) {
                        updateLineChangeType(LineChangeType.UPDATED)
                    }
                }
            }
            newFields.addAll(targetIndex + 1, newSplitFieldStates)
            val lastNewSplitFieldIndex = targetIndex + newSplitFieldValues.size
            val oldTargetText = fields[targetIndex].value.text  
            val newTargetFirstText = splitFirstLine.text  
            val newTargetLastText = splitFieldValues.last().text  
            val newSelectedIndices = selectedIndices.toMutableList()
            val sfiRet = selectFieldInternal(
                init_fields = newFields,
                init_selectedIndices = newSelectedIndices,
                isMutableFields = true,
                isMutableSelectedIndices = true,
                targetIndex = lastNewSplitFieldIndex,
                option = SelectionOption.CUSTOM,
                columnStartIndexInclusive = newTargetLastText.length - oldTextRight.length,
            )
            val newContentRangeInNewFields = IntRange(targetIndex, targetIndex + newSplitFieldStates.size)
            updater?.invoke(newContentRangeInNewFields, newFields, newSelectedIndices)
            isContentEdited?.value=true
            editorPageIsContentSnapshoted?.value=false
            EditCache.writeToFile(newText)
            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = newId(),
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )
            updateStyles(splitFieldValues.size, newState) { baseStyles ->
                updateStylesAfterDeleteLine(
                    baseFields = fields,
                    stylesResult = baseStyles,
                    startLineIndex = targetIndex,
                    ignoreThis = true,
                    newTextEditorState = newState
                )
                updateStylesAfterInsertLine(
                    baseFields = fields,
                    stylesResult = baseStyles,
                    ignoreThis = false,
                    insertedContent = splitFieldValues.joinToString(lb) { it.text },
                    insertedContentRangeInNewFields = newContentRangeInNewFields,
                    newTextEditorState = newState
                )
            }
            onChanged(newState, true, true, this, null)
        }
    }
    suspend fun updateField(
        targetIndex: Int,
        textFieldValue: TextFieldValue,
        textChanged: Boolean? = null,
        requireLock: Boolean = true,
        closePairIfNeed: Boolean = SettingsUtil.isEditorAutoCloseSymbolPairEnabled(),
        updater: ((newLinesRange: IntRange, newFields: MutableList<MyTextFieldState>, newSelectedIndices: MutableList<Int>) -> Unit)? = null
    ) {
        val act = suspend p@{
            try {
                targetIndexValidOrThrow(targetIndex, fields.size)
            }catch (e: Exception) { 
                MyLog.d(TAG, "TextEditorState.updateField() err: ${e.stackTraceToString()}")
                return@p
            }
            if (textFieldValue.text.contains(lb)) {
                return@p
            }
            val newText = textFieldValue.text
            val oldField = fields[targetIndex]
            val textChanged = textChanged ?: oldField.value.text.let { it.length != newText.length || it != newText }  
            var maybeNewId = fieldsId
            val newFields = fields.toMutableList()
            val updatedField = newFields[targetIndex].copy(value = textFieldValue)
            newFields[targetIndex] = if(textChanged) {
                isContentEdited?.value = true
                editorPageIsContentSnapshoted?.value = false
                maybeNewId = newId()
                EditCache.writeToFile(newText)
                updatedField.updateLineChangeTypeIfNone(LineChangeType.UPDATED)
                if(closePairIfNeed) {
                    appendClosePairIfNeed(oldField, updatedField)
                }else {
                    updatedField
                }
            }else {
                updatedField
            }
            val newSelectedIndices = selectedIndices.toMutableList()
            val newState = internalCreate(
                fields = newFields,
                fieldsId = maybeNewId,
                selectedIndices = newSelectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = if(isMultipleSelectionMode) focusingLineIdx else targetIndex,
            )
            val newContentRangeInNewFields = IntRange(targetIndex, targetIndex)
            updater?.invoke(newContentRangeInNewFields, newFields, newSelectedIndices)
            if(textChanged) {
                updateStyles(1, newState) { baseStyles->
                    updateStylesAfterDeleteLine(
                        baseFields = fields,
                        stylesResult = baseStyles,
                        startLineIndex = targetIndex,
                        ignoreThis = true,
                        newTextEditorState = newState
                    )
                    updateStylesAfterInsertLine(
                        baseFields = fields,
                        stylesResult = baseStyles,
                        ignoreThis = false,
                        insertedContent = newFields[targetIndex].value.text,
                        insertedContentRangeInNewFields = newContentRangeInNewFields,
                        newTextEditorState = newState
                    )
                }
            }
            onChanged(newState, if(textChanged) true else null, textChanged, this, null)
        }
        if(requireLock) {
            lock.withLock {
                act()
            }
        }else {
            act()
        }
    }
    private fun appendClosePairIfNeed(
        oldField: MyTextFieldState,
        newField: MyTextFieldState,
    ) : MyTextFieldState {
        return try {
            appendClosePairIfNeedNoCatch(oldField, newField)
        }catch (e: Exception) {
            MyLog.e(TAG, "#appendClosePairIfNeed err: ${e.localizedMessage}")
            e.printStackTrace()
            newField
        }
    }
    private fun appendClosePairIfNeedNoCatch(
        oldField: MyTextFieldState,
        newField: MyTextFieldState,
    ) : MyTextFieldState {
        val newText = newField.value.text
        if(newText.isEmpty()) {
            return newField
        }
        val oldText = oldField.value.text
        val oldSelection = oldField.value.selection
        if(
            (oldSelection.collapsed && newText.length <= oldText.length)
            || (!oldSelection.collapsed && (oldText.length - oldSelection.end + oldSelection.start == newText.length))
        ) {
            return newField
        }
        val newSelection = newField.value.selection
        val newCursorAt = newSelection.start
        if(!newSelection.collapsed || newCursorAt <= 0) {
            return newField
        }
        val leftTextOfCursor = newText.substring(0, newSelection.start)
        var closedCharOfPair = resolveClosedStringOfPair(leftTextOfCursor)
        if(closedCharOfPair == null) {
            val openedCharOfPair = newText.get(newCursorAt - 1)
            closedCharOfPair = resolveClosedCharOfPair(openedCharOfPair)
            if(closedCharOfPair == null) {
                return newField
            }
        }
        var newSelectionRange = newSelection
        var midText = closedCharOfPair
        val rightText = newText.substring(newSelection.start)
        if(!oldSelection.collapsed) {
            val oldSelectedText = oldText.substring(oldSelection.min, oldSelection.max)
            midText = oldSelectedText + midText
            newSelectionRange = TextRange(start = newSelectionRange.start, end = newSelectionRange.end + oldSelectedText.length)
            if(oldSelection.start > oldSelection.end) {  
                newSelectionRange = TextRange(start = newSelectionRange.end, end = newSelectionRange.start)
            }
        }
        val textAddedClosedPair = StringBuilder().let {
            it.append(leftTextOfCursor)
            it.append(midText)
            it.append(rightText)
            it.toString()
        }
        return newField.copy(value = newField.value.copy(text = textAddedClosedPair, selection = newSelectionRange))
    }
    private fun resolveClosedStringOfPair(text: String) : String? {
        if(text.endsWith(""
        }
        return null
    }
    private fun resolveClosedCharOfPair(openedCharOfPair: Char) : String? {
        var closedPair = codeEditor?.myLang?.symbolPairs?.matchBestPairBySingleChar(openedCharOfPair)?.close
        if(closedPair == null) {
            closedPair = symbolPairsMap.get(openedCharOfPair.toString())
        }
        return closedPair
    }
    private fun updateStyles(
        howManyLinesNeedUpdate: Int,
        nextState: TextEditorState,
        act: (baseStyles: StylesResult) -> Unit
    ) {
        if(codeEditor.scopeInvalid()) {
            return
        }
        try {
            val halfLines = (fields.size / 2).coerceAtLeast(1)
            if(howManyLinesNeedUpdate >= halfLines
                || howManyLinesNeedUpdate > SettingsUtil.editorThresholdLinesCountOfIncrementAnalyze()
            ) {
                codeEditor?.analyze(nextState)
                return
            }
            val baseStyles = tryGetStylesResult()
            if(baseStyles == null) {
                MyLog.d(TAG, "#updateStyles: Styles of current field '$fieldsId' not found, maybe not exists or theme/languageScop are not matched, will re-run analyze after user stop input for a while")
                codeEditor?.startAnalyzeWhenUserStopInputForAWhile(nextState)
            }else {
                act(baseStyles)
            }
        }catch (e: Exception) {
            MyLog.e(TAG, "#updateStyles err: ${e.stackTraceToString()}")
        }
    }
    private suspend fun appendOrReplaceFields(targetIndex: Int, text: String, trueAppendFalseReplace:Boolean) {
        if(!isGoodIndexForList(targetIndex, fields)) {
            MyLog.d(TAG, "#appendOrReplaceFields(): invalid index: $targetIndex, list.size=${fields.size}")
            return
        }
        val updater = { newLinesRange: IntRange, newFields: MutableList<MyTextFieldState>, newSelectedIndices: MutableList<Int> ->
            newSelectedIndices.clear()
            for(i in newFields.indices) {
                if(!(((trueAppendFalseReplace && i <= newLinesRange.start) || (trueAppendFalseReplace.not() && i < newLinesRange.start)) || i > newLinesRange.endInclusive)) {
                    newSelectedIndices.add(i)
                }
            }
        }
        if(trueAppendFalseReplace) {
            splitNewLine(targetIndex, TextFieldValue(fields[targetIndex].value.text + lb + text), updater = updater)
        }else {
            if(text.contains(lb)) {
                splitNewLine(targetIndex, TextFieldValue(text), updater = updater)
            }else {
                updateField(
                    targetIndex = targetIndex,
                    textFieldValue = TextFieldValue(text),
                    updater = updater
                )
            }
        }
    }
    suspend fun deleteNewLine(targetIndex: Int) {
        lock.withLock {
            try {
                targetIndexValidOrThrow(targetIndex, fields.size)
            }catch (e: Exception) { 
                MyLog.d(TAG, "TextEditorState.deleteNewLine() err: ${e.stackTraceToString()}")
            }
            if (targetIndex <= 0) {
                return
            }
            val newFields = fields.toMutableList()
            val toLineIdx = targetIndex - 1
            val toField = newFields[toLineIdx]
            val fromField = newFields[targetIndex]
            val toText = toField.value.text
            val fromText = fromField.value.text
            val concatText = StringBuilder(toText.length + fromText.length).append(toText).append(fromText).toString()
            val concatSelection = TextRange(toText.length)  
            val concatTextFieldValue = TextFieldValue(text = concatText, selection = concatSelection)
            val toTextFieldState = newFields[targetIndex - 1].copy(value = concatTextFieldValue).apply {
                val newChangeType = if(toText.isEmpty() && fromText.isNotEmpty()) {
                    fromField.changeType
                }else if(toText.isNotEmpty() && fromText.isEmpty()) {
                    toField.changeType
                }else if(toText.isEmpty() && fromText.isEmpty()) {
                    toField.changeType
                }else { 
                    if(toField.changeType == LineChangeType.NONE) LineChangeType.UPDATED else toField.changeType
                }
                updateLineChangeType(newChangeType)
            }
            newFields[targetIndex - 1] = toTextFieldState
            newFields.removeAt(targetIndex)
            val sfiRet = selectFieldInternal(
                init_fields = newFields,
                init_selectedIndices = selectedIndices,
                isMutableFields = true,
                isMutableSelectedIndices = false,
                targetIndex = targetIndex - 1
            )
            isContentEdited?.value=true
            editorPageIsContentSnapshoted?.value= false
            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = newId(),
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )
            updateStyles(2, newState) { baseStyles ->
                updateStylesAfterDeletedLineBreak(fields, baseStyles, toLineIdx, ignoreThis = false, newState)
            }
            onChanged(newState, true, true, this, null)
        }
    }
    suspend fun selectField(
        targetIndex: Int,
        option: SelectionOption = SelectionOption.NONE,
        columnStartIndexInclusive:Int=0,
        columnEndIndexExclusive:Int=columnStartIndexInclusive,
        requireSelectLine: Boolean=true, 
        requireLock: Boolean = true,
        forceAdd: Boolean = false,
        provideTextFieldValue: TextFieldValue? = null,
    ) {
        val act = suspend {
            val sfiRet = selectFieldInternal(
                init_fields = fields,
                init_selectedIndices = selectedIndices,
                isMutableFields = false,
                isMutableSelectedIndices = false,
                targetIndex = targetIndex,
                option = option,
                columnStartIndexInclusive=columnStartIndexInclusive,
                columnEndIndexExclusive=columnEndIndexExclusive,
                requireSelectLine = requireSelectLine,
                forceAdd = forceAdd,
                provideTextFieldValue = provideTextFieldValue,
            )
            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = fieldsId,  
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )
            onChanged(newState, null, false, this, null)
        }
        if(requireLock) {
            lock.withLock {
                act()
            }
        }else {
            act()
        }
    }
    suspend fun selectFieldSpan(targetIndex: Int) {
        lock.withLock {
            var sfiRet:SelectFieldInternalRet? = null
            if(selectedIndices.isEmpty()) {  
                sfiRet = selectFieldInternal(
                    init_fields = fields,
                    init_selectedIndices = selectedIndices,
                    isMutableFields = false,
                    isMutableSelectedIndices = false,
                    targetIndex = targetIndex,
                    forceAdd = true
                )
            }else {  
                val lastSelectedIndex = focusingLineIdx ?: selectedIndices.last()
                val startIndex = targetIndex.coerceAtMost(lastSelectedIndex)
                val endIndexInclusive = targetIndex.coerceAtLeast(lastSelectedIndex)
                val range = IntRange(startIndex, endIndexInclusive)
                if(range.isEmpty()) {
                    return
                }
                val newSelectedIndices = selectedIndices.toMutableList()
                for(i in range) {
                    sfiRet = selectFieldInternal(
                        init_fields = fields,
                        init_selectedIndices = newSelectedIndices,
                        isMutableFields = false,
                        isMutableSelectedIndices = true,
                        targetIndex = i,
                        forceAdd = true
                    )
                }
            }
            val newState = internalCreate(
                fields = sfiRet?.fields ?: fields,
                fieldsId = fieldsId,
                selectedIndices = sfiRet?.selectedIndices ?: selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = targetIndex
            )
            onChanged(newState, null, false, this, null)
        }
    }
    suspend fun selectPrevOrNextField(
        isNext:Boolean,
        updateLastCursorAtColumn:(Int)->Unit,
        getLastCursorAtColumnValue:()->Int,
    ) {
        lock.withLock {
            val selectedIndex = focusingLineIdx ?: return
            if((isNext && selectedIndex >= fields.lastIndex)
                || (isNext.not() && selectedIndex <= 0)
            ) {
                return
            }
            val moveTargetIndex = if(isNext) selectedIndex + 1 else (selectedIndex - 1)
            val curField = fields.getOrNull(selectedIndex) ?: return
            val targetColumn = curField.value.selection.max.let {
                updateLastCursorAtColumn(it)
                getLastCursorAtColumnValue()
            }
            val sfiRet = selectFieldInternal(
                init_fields = fields,
                init_selectedIndices = selectedIndices,
                isMutableFields = false,
                isMutableSelectedIndices = false,
                targetIndex = moveTargetIndex,
                option = SelectionOption.CUSTOM,
                columnStartIndexInclusive = targetColumn
            )
            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = fieldsId,
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )
            onChanged(newState, null, false, this, null)
        }
    }
    private fun selectFieldInternal(
        init_fields:List<MyTextFieldState>,
        init_selectedIndices:List<Int>,
        isMutableFields:Boolean,
        isMutableSelectedIndices:Boolean,
        targetIndex: Int,
        option: SelectionOption = SelectionOption.NONE,
        forceAdd:Boolean=false,  
        columnStartIndexInclusive:Int=0,
        columnEndIndexExclusive:Int=columnStartIndexInclusive,
        requireSelectLine:Boolean = true,
        provideTextFieldValue: TextFieldValue? = null,
    ):SelectFieldInternalRet {
        try {
            targetIndexValidOrThrow(targetIndex, init_fields.size)
        }catch (e: Exception) { 
            MyLog.d(TAG, "TextEditorState.selectFieldInternal() err: ${e.localizedMessage}")
        }
        val targetIndex = targetIndex.coerceAtMost(init_fields.lastIndex).coerceAtLeast(0)
        var ret_fields = init_fields
        var ret_selectedIndices = init_selectedIndices
        val ret_focusingLineIdx = targetIndex
        val out_fileds = Unit
        val out_selectedIndices = Unit
        val init_focusingLineIdx = Unit
        val targetInFieldList = ret_fields[targetIndex]
        val selection = provideTextFieldValue?.selection ?: run {
            val textLenOfTarget = targetInFieldList.value.text.length
            val columnStartIndexInclusive = columnStartIndexInclusive.coerceAtMost(textLenOfTarget).coerceAtLeast(0)
            val columnEndIndexExclusive = columnEndIndexExclusive.coerceAtMost(textLenOfTarget).coerceAtLeast(0)
            when (option) {
                SelectionOption.CUSTOM -> {
                    TextRange(columnStartIndexInclusive, columnEndIndexExclusive)
                }
                SelectionOption.NONE -> targetInFieldList.value.selection
                SelectionOption.FIRST_POSITION -> TextRange.Zero
                SelectionOption.LAST_POSITION -> {
                    TextRange(textLenOfTarget)
                }
            }
        }
        if(isMultipleSelectionMode && requireSelectLine) {  
            ret_selectedIndices = if(isMutableSelectedIndices) (ret_selectedIndices as MutableList) else ret_selectedIndices.toMutableList()
            if(forceAdd) {  
                if(!ret_selectedIndices.contains(targetIndex)) {
                    ret_selectedIndices.add(targetIndex)
                }
            }else {  
                if (!ret_selectedIndices.remove(targetIndex)) {
                    ret_selectedIndices.add(targetIndex)
                }
            }
        }else {  
            if(provideTextFieldValue != null) {
                if(provideTextFieldValue != targetInFieldList.value) {
                    ret_fields = if(isMutableFields) (ret_fields as MutableList) else ret_fields.toMutableList()
                    ret_fields[targetIndex] = targetInFieldList.copy(
                        value = provideTextFieldValue
                    )
                }
            }else { 
                ret_fields = if(isMutableFields) (ret_fields as MutableList) else ret_fields.toMutableList()
                val selectionRangeChanged = selection != targetInFieldList.value.selection
                if(selectionRangeChanged) {
                    ret_fields[targetIndex] = targetInFieldList.copy(
                        value = targetInFieldList.value.copy(selection = selection)
                    )
                }
            }
        }
        return SelectFieldInternalRet(
            fields = ret_fields,
            selectedIndices = ret_selectedIndices,
            focusingLineIdx = ret_focusingLineIdx
        )
    }
    private fun splitTextsByNL(
        text: String,
        oldText: String,
    ): List<TextFieldValue> {
        val lines = text.lines()
        val maybeIsPaste = lines.size > 2 || (
            lines.get(0).let { first ->
                lines.get(1).let { second ->
                    first.length + second.length != oldText.length || first + second != oldText
                }
            }
        )
        if(AppModel.devModeOn) {
            MyLog.d(TAG, "maybeIsPaste=$maybeIsPaste")
        }
        val ret = mutableListOf<TextFieldValue>()
        val tabIndentSpaceCount = SettingsUtil.editorTabIndentCount()
        var autoIndentSpacesCount = ""
        for((index, text) in lines.withIndex()) {
            ret.add(
                if(maybeIsPaste) {  
                    TextFieldValue(text, TextRange(text.length))
                } else if (index == 0) {  
                    autoIndentSpacesCount = getNextIndentByCurrentStr(text, tabIndentSpaceCount)
                    TextFieldValue(text, TextRange(text.length))
                } else {  
                    val currentLineIndentCount = getNextIndentByCurrentStr(text, tabIndentSpaceCount)
                    val diff = autoIndentSpacesCount.length - currentLineIndentCount.length
                    val text = if(diff > 0) {
                        val sb = StringBuilder()
                        for (i in 0 until diff) {
                            sb.append(IndentChar.SPACE.char)
                        }
                        sb.append(text).toString()
                    }else {
                        text
                    }
                    TextFieldValue(text)
                }
            )
        }
        return ret
    }
    suspend fun getKeywordCount(keyword: String): Int {
        val f = fields
        var count = 0
        f.forEachBetter {
            val text = it.value.text
            var startIndex=0
            while (true) {
                if(!isGoodIndexForStr(startIndex, text)) {
                    break
                }
                val indexOf = text.indexOf(string=keyword, startIndex=startIndex, ignoreCase=true)
                if(indexOf == -1) {  
                    break
                }else {  
                    count++
                    startIndex = indexOf+keyword.length
                }
            }
        }
        return count
    }
    fun getCharsAndLinesCount(): Pair<Int, Int> {
        val f= fields
        val lines = f.size
        var chars = 0
        f.forEachBetter { chars+=it.value.text.length }
        return Pair(chars, lines)
    }
    fun indexAndValueOf(startIndex:Int, direction: FindDirection, predicate: (text:String) -> Boolean, includeStartIndex:Boolean): Pair<Int, String> {
        val list = fields
        var retPair = Pair(-1, "")
        try {
            val range = if(direction == FindDirection.UP) {
                val endIndex = if(includeStartIndex) startIndex else (startIndex-1)
                if(!isGoodIndexForList(endIndex, list)) {
                    throw RuntimeException("bad endIndex: $endIndex , list.size: ${list.size}")
                }
                (0..endIndex).reversed()
            }else {
                val tempStartIndex =if(includeStartIndex) startIndex else (startIndex+1)
                if(!isGoodIndexForList(tempStartIndex, list)) {
                    throw RuntimeException("bad tempStartIndex: $tempStartIndex , list.size: ${list.size}")
                }
                tempStartIndex..list.lastIndex
            }
            for(i in range) {
                val item = list[i]
                val text = item.value.text
                if(predicate(text)) {
                    retPair = Pair(i, text)
                    break
                }
            }
        }catch (e:Exception) {
            MyLog.d(TAG, "TextEditorState.indexAndValueOf() err: ${e.stackTraceToString()}")
        }
        return retPair
    }
    suspend fun deleteLineByIndices(
        indices:List<Int>,
        baseFields:List<MyTextFieldState>? = null,
        trueClearAllSelectedIndicesFalseOnlyClearWhichDeleted:Boolean = true,
    ) {
        if(indices.isEmpty()) {
            return
        }
        lock.withLock {
            val newFields = mutableListOf<MyTextFieldState>();
            (baseFields ?: fields).forEachIndexedBetter { index, field ->
                if(!indices.contains(index)) {
                    newFields.add(field)
                }
            }
            var focusingLineIdx = focusingLineIdx
            if(newFields.isEmpty()) {
                newFields.add(MyTextFieldState())
                if(focusingLineIdx != null) {
                    focusingLineIdx = 0
                }
            }else if(focusingLineIdx != null) {
                val lastDeletedIndex = indices.lastOrNull()
                val newLinesCoveredOldIndex = if (lastDeletedIndex != null && lastDeletedIndex < newFields.size) { 
                    focusingLineIdx = lastDeletedIndex
                    true
                }else {  
                    focusingLineIdx = newFields.lastIndex
                    false
                }
                newFields[focusingLineIdx] = newFields[focusingLineIdx].let {
                    it.copy(
                        value = it.value.copy(selection = if(newLinesCoveredOldIndex) TextRange.Zero else TextRange(it.value.text.length))
                    )
                }
            }
            isContentEdited?.value = true
            editorPageIsContentSnapshoted?.value = false
            val newState = internalCreate(
                fields = newFields,
                fieldsId = newId(),
                selectedIndices = if(trueClearAllSelectedIndicesFalseOnlyClearWhichDeleted) {
                    listOf()
                } else {
                    selectedIndices.filter { !indices.contains(it) }
                },
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = focusingLineIdx
            )
            if(newFields.size <= indices.size) {
                codeEditor?.analyze(newState)
            }else {
                updateStyles(indices.size, newState) { baseStyles ->
                    val indices = indices.sortedDescending()
                    val lastIdx = indices.size - 1
                    indices.forEachIndexed { idx, lineIdxWillDel ->
                        updateStylesAfterDeleteLine(fields, baseStyles, lineIdxWillDel, ignoreThis = idx != lastIdx, newState, keepLine = false)
                    }
                }
            }
            onChanged(newState, true, true, this, null)
        }
    }
    suspend fun createMultipleSelectionModeState(targetIndex:Int) {
        lock.withLock {
            val newSelectedIndices = mutableListOf<Int>()
            if(targetIndex >= 0 && targetIndex < fields.size) {
                newSelectedIndices.add(targetIndex)
            }
            val newState = internalCreate(
                fieldsId = fieldsId,  
                fields = fields,  
                selectedIndices = newSelectedIndices,  
                isMultipleSelectionMode = true,
                focusingLineIdx = targetIndex
            )
            onChanged(newState, null, false, this, null)
        }
    }
    suspend fun createSelectAllState() {
        lock.withLock {
            val selectedIndexList = fields.indices.toList()
            val newState = internalCreate(
                fieldsId = fieldsId,
                fields = fields,
                selectedIndices = selectedIndexList,
                isMultipleSelectionMode = true,
                focusingLineIdx = focusingLineIdx
            )
            onChanged(newState, null, false, this, null)
        }
    }
    suspend fun deleteSelectedLines() {
        if(selectedIndices.isEmpty()) {
            return
        }
        deleteLineByIndices(selectedIndices, trueClearAllSelectedIndicesFalseOnlyClearWhichDeleted = true)
    }
    suspend fun clearSelectedFields(){
        if(selectedIndices.isEmpty()) {
            return
        }
        lock.withLock {
            val newFields = fields.mapIndexed { index, field ->
                if(selectedIndices.contains(index)) {
                    field.copy(value = field.value.copy(text = ""))
                }else {
                    field
                }
            }
            val newState = internalCreate(
                fieldsId = newId(),
                fields = newFields,
                selectedIndices = selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,  
                focusingLineIdx = focusingLineIdx
            )
            isContentEdited?.value = true
            editorPageIsContentSnapshoted?.value = false
            updateStyles(selectedIndices.size, newState) { baseStyles  ->
                val selectedIndices = selectedIndices.sortedDescending()
                val lastIdx = selectedIndices.size - 1
                selectedIndices.forEachIndexed { idx, lineIdxWillDel ->
                    updateStylesAfterDeleteLine(fields, baseStyles, lineIdxWillDel, ignoreThis = idx != lastIdx, newState)
                }
            }
            onChanged(newState, true, true, this, null)
        }
    }
    suspend fun quitSelectionMode(keepSelectionModeOn:Boolean = false){
        lock.withLock {
            val newState = internalCreate(
                fieldsId = fieldsId,
                fields = fields,
                selectedIndices = emptyList(),
                isMultipleSelectionMode = keepSelectionModeOn,
                focusingLineIdx = focusingLineIdx
            )
            onChanged(newState, null, false, this, null)
        }
    }
    fun clearSelectedItemList(){
        runBlocking { quitSelectionMode(keepSelectionModeOn = true) }
    }
    fun lineIdxToPx(lineIndex: Int, fontSizeInPx:Float, screenWidthInPx:Float, screenHeightInPx:Float):Float {
        if(lineIndex == 0) {
            return  0f
        }
        var count = 0
        val lineHeight = UIHelper.guessLineHeight(fontSizeInPx)
        val (oneLineHowManyChars, luckyOffset) = getOneLineHowManyCharsAndLuckyOffset(lineHeight = lineHeight, screenWidthInPx = screenWidthInPx, screenHeightInPx = screenHeightInPx, indexToPx = true)
        var targetPx = luckyOffset
        val iterator = fields.iterator()
        while(iterator.hasNext()) {
            if(count++ > lineIndex) {
                break
            }
            val it = iterator.next()
            val lineChars = it.value.text.length
            val softLineCount = (lineChars / oneLineHowManyChars).coerceAtLeast(1)
            targetPx += (softLineCount * lineHeight)
        }
        return targetPx
    }
    fun pxToLineIdx(targetPx: Int, fontSizeInPx:Float, screenWidthInPx:Float, screenHeightInPx:Float):Int {
        if(targetPx == 0) {
            return  0
        }
        val lineHeight = UIHelper.guessLineHeight(fontSizeInPx)
        val (oneLineHowManyChars, luckyOffset) = getOneLineHowManyCharsAndLuckyOffset(lineHeight = lineHeight, screenWidthInPx = screenWidthInPx, screenHeightInPx = screenHeightInPx, indexToPx = false)
        var pos = luckyOffset
        var targetLineIndex = 0
        val iterator = fields.iterator()
        while (iterator.hasNext()) {
            if(pos >= targetPx) {
                break
            }
            targetLineIndex++
            val it = iterator.next()
            val lineChars = it.value.text.length
            val softLineCount = (lineChars / oneLineHowManyChars).coerceAtLeast(1)
            pos += (softLineCount * lineHeight)
        }
        return targetLineIndex
    }
    private fun getOneLineHowManyCharsAndLuckyOffset(lineHeight: Float, screenWidthInPx: Float, screenHeightInPx: Float, indexToPx:Boolean): Pair<Int, Float> {
        val oneLineHowManyChars = (screenWidthInPx / lineHeight).toInt()
        val luckyOffset = UIHelper.getLuckyOffset(indexToPx = indexToPx, screenWidthInPx = screenWidthInPx, screenHeightInPx = screenHeightInPx)
        return Pair(oneLineHowManyChars, luckyOffset)
    }
    fun focusingLineIndexInvalid() = focusingLineIdx == null || focusingLineIdx < 0 || focusingLineIdx >= fields.size
    suspend fun appendTextToLastSelectedLine(
        text: String,
        forceAppend: Boolean = true,
        afterAppendThenDoAct: ((targetIndex: Int) -> Unit)? = null
    ) {
        val selectedIndices = selectedIndices
        val focusedLineInvalid = focusingLineIndexInvalid()
        if(focusedLineInvalid && selectedIndices.isEmpty()) {
            return
        }
        val lastSelectedIndexOfLine = if(focusedLineInvalid) selectedIndices.last() else (focusingLineIdx!!)
        if(lastSelectedIndexOfLine < 0 || lastSelectedIndexOfLine >= fields.size) {
            MyLog.w(TAG, "#appendTextToLastSelectedLine: invalid index '$lastSelectedIndexOfLine' of `fields`")
            return
        }
        val trueAppendFalseReplace = if(forceAppend) true else fields[lastSelectedIndexOfLine].value.text.isNotEmpty()
        appendOrReplaceFields(targetIndex = lastSelectedIndexOfLine, text = text, trueAppendFalseReplace = trueAppendFalseReplace)
        afterAppendThenDoAct?.invoke(lastSelectedIndexOfLine)
    }
    fun getContentOfLineIndex(lineIndex: Int): String {
        val f = fields
        return if(lineIndex >= 0 && lineIndex < f.size) {
            f[lineIndex].value.text
        }else {
            ""
        }
    }
    private fun lbForDump() = codeEditor?.lineBreak?.value ?: lb;
    fun getAllText(): String {
        val sb = StringBuilder()
        val lineBreak = lbForDump()
        fields.forEachBetter { sb.append(it.value.text).append(lineBreak) }
        return sb.removeSuffix(lineBreak).toString()
    }
    fun dumpLinesAndGetRet(output: OutputStream): Ret<Unit?> {
        try {
            dumpLines(output)
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage ?: "dump lines err", exception = e)
        }
    }
    fun dumpLines(output: OutputStream) {
        val lineBreak = lbForDump()
        val fieldsSize = fields.size
        var count = 0
        val charsetName = codeEditor?.editorCharset?.value
        EncodingUtil.addBomIfNeed(output, charsetName)
        output.bufferedWriter(EncodingUtil.resolveCharset(charsetName)).use { bw ->
            for(f in fields) {
                bw.write(f.value.text)
                if(++count != fieldsSize) {
                    bw.write(lineBreak)
                }
            }
        }
    }
    fun getSelectedText(indices: List<Int>? = null, keepEndLineBreak: Boolean = false): String {
        val sb = StringBuilder()
        (indices ?: selectedIndices).toSortedSet().forEach { selectedLineIndex->
            doActIfIndexGood(selectedLineIndex, fields) { field ->
                sb.append(field.value.text).append(lb)
            }
        }
        return if(keepEndLineBreak) sb.toString() else sb.removeSuffix(lb).toString()
    }
    fun getSelectedCount():Int{
        return selectedIndices.size  
    }
    fun contentIsEmpty(): Boolean {
        return fields.isEmpty() || (fields.size == 1 && fields[0].value.text.isEmpty())
    }
    fun maybeNotEquals(other: TextEditorState):Boolean {
        return this.fieldsId != other.fieldsId
    }
    private fun internalCreate(
        fields: List<MyTextFieldState>,
        fieldsId: String,
        selectedIndices: List<Int>,
        isMultipleSelectionMode: Boolean,
        focusingLineIdx: Int?,
    ): TextEditorState {
        return TextEditorState(
            fieldsId= fieldsId,
            fields = fields,
            selectedIndices = selectedIndices,
            isMultipleSelectionMode = isMultipleSelectionMode,
            focusingLineIdx = focusingLineIdx,
            codeEditor = codeEditor,
            isContentEdited = isContentEdited,
            editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
            onChanged = onChanged,
        )
    }
    suspend fun goToEndOrTopOfFile(goToTop:Boolean) {
        lock.withLock {
            val targetIndex = if(goToTop) 0 else fields.lastIndex.coerceAtLeast(0)
            val sfiRet = selectFieldInternal(
                init_fields = fields,
                init_selectedIndices = selectedIndices,
                isMutableFields = false,
                isMutableSelectedIndices = false,
                targetIndex = targetIndex,
                option = if(goToTop) SelectionOption.FIRST_POSITION else SelectionOption.LAST_POSITION,
            )
            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = fieldsId,
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )
            onChanged(newState, null, false, this, null)
        }
    }
    suspend fun setChangeTypeToFields(
        indices: List<Int>,
        changeType: LineChangeType,
        baseFields:List<MyTextFieldState>?,
        applyNewSate:Boolean,
    ):List<MyTextFieldState>? {
        if(indices.isEmpty()) return null;
        lock.withLock {
            val newFields = (baseFields ?: fields).toMutableList()
            indices.forEachBetter forEach@{ idx ->
                val textField = newFields.getOrNull(idx) ?: return@forEach;
                newFields[idx] = textField.copy(changeType = changeType)
            }
            if(applyNewSate) {
                val newState = internalCreate(
                    fields = newFields,
                    fieldsId = fieldsId,
                    selectedIndices = selectedIndices,
                    isMultipleSelectionMode = isMultipleSelectionMode,
                    focusingLineIdx = focusingLineIdx
                )
                onChanged(newState, null, false, this, null)
            }
            return newFields
        }
    }
    fun getFieldByIdx(idx: Int?) = Pair(idx, idx?.let { fields.getOrNull(idx) })
    fun getCurrentField() = getFieldByIdx(focusingLineIdx)
    suspend fun handleTabIndent(idx:Int, f:MyTextFieldState, tabIndentSpacesCount:Int, trueTabFalseShiftTab:Boolean):Boolean {
        return lock.withLock {
            val handled =  try {
                val handleTabRet = if(trueTabFalseShiftTab) {
                    doTab(tabIndentSpacesCount,  f)
                }else {
                    doShiftTab(tabIndentSpacesCount, f)
                }
                if(handleTabRet.changed) {
                    updateField(
                        targetIndex = idx,
                        textFieldValue = f.value.copy(
                            text = handleTabRet.newText,
                            selection = handleTabRet.newSelection
                        ),
                        requireLock = false
                    )
                    true
                }else {
                    false
                }
            }catch (e: Exception) {
                MyLog.e(TAG, "$TAG#handleTabIndent err: replace ${if(trueTabFalseShiftTab) "TAB" else "SHIFT+TAB"} to indent spaces err: ${e.stackTraceToString()}")
                false
            }
            if(!handled) {
                selectField(idx, requireLock = false)
            }
            true
        }
    }
    private fun doTab(tabIndentSpacesCount: Int, f: MyTextFieldState): HandleTabRet {
        val fv = f.value
        val cursorAt = if (fv.selection.collapsed) fv.selection.start else 0
        val sb = StringBuilder(fv.text.substring(0, cursorAt))
        val newText = sb.append(tabToSpaces(tabIndentSpacesCount)).append(fv.text.substring(cursorAt, fv.text.length)).toString()
        val cursorOffset = tabIndentSpacesCount.coerceAtLeast(1)
        val newSelection = if (fv.selection.collapsed) TextRange(cursorAt + cursorOffset)
        else TextRange(start = fv.selection.start + cursorOffset, end = fv.selection.end + cursorOffset)
        return HandleTabRet(newText, newSelection, true)
    }
    private fun doShiftTab(
        tabIndentSpacesCount: Int,
        f: MyTextFieldState
    ): HandleTabRet {
        val fv = f.value
        if(fv.text.let { it.isEmpty() || (it.startsWith(IndentChar.SPACE.char).not() && it.startsWith(IndentChar.TAB.char).not()) }) {
            return HandleTabRet(newText = fv.text, newSelection = fv.selection, changed = false)
        }
        var useSubString = false
        var leftSub = ""
        val text = if(fv.selection.collapsed && fv.selection.start.let { it > 0 && it < fv.text.length }) {
            leftSub = fv.text.substring(0, fv.selection.start)
            val rightSub = fv.text.substring(fv.selection.start)
            rightSub.let {
                if(leftSub.isNotBlank() || it.isBlank() || (it.startsWith(IndentChar.SPACE.char).not() && it.startsWith(IndentChar.TAB.char).not())) {
                    useSubString = false
                    fv.text
                } else {
                    useSubString = true
                    it
                }
            }
        } else {
            useSubString = false
            fv.text
        }
        val (newTextTmp, removedCount) = if (tabIndentSpacesCount < 1 || text.startsWith(IndentChar.TAB.char)) {  
            Pair(text.substring(1, text.length), 1)
        } else {  
            var removed = 0
            for (i in text) {
                if (i == IndentChar.SPACE.char) {
                    if (++removed >= tabIndentSpacesCount) {
                        break
                    }
                } else {
                    break
                }
            }
            Pair(if (removed == 0) text else text.substring(removed, text.length), removed)
        }
        val newText = if(useSubString) {
            StringBuilder().apply {
                append(leftSub)
                append(newTextTmp)
            }.toString()
        }else {
            newTextTmp
        }
        return if (removedCount < 1) {
            HandleTabRet(newText = newText, newSelection = fv.selection, changed = false)
        } else {
            val newSelection = if (fv.selection.collapsed) {
                if(useSubString) {
                    TextRange(fv.selection.start)
                } else {
                    TextRange((fv.selection.start - removedCount).coerceAtLeast(0))
                }
            } else {
                TextRange(start = (fv.selection.start - removedCount).coerceAtLeast(0), end = (fv.selection.end - removedCount).coerceAtLeast(0))
            }
            HandleTabRet(newText, newSelection, changed = true)
        }
    }
    suspend fun indentLines(
        tabIndentSpacesCount: Int,
        targetIndices:List<Int>,
        trueTabFalseShiftTab: Boolean,
    ) {
        lock.withLock {
            val fields = fields
            val newFields = mutableListOf<MyTextFieldState>()
            val targetIndices = targetIndices
            fields.forEachIndexedBetter { i, f ->
                val newF = if(targetIndices.contains(i)) {
                    val f = f.copy(value = f.value.copy(selection = TextRange(0)))
                    val handleTabRet = if(trueTabFalseShiftTab) {
                        doTab(tabIndentSpacesCount, f)
                    }else {
                        doShiftTab(tabIndentSpacesCount, f)
                    }
                    f.copy(value = f.value.copy(text = handleTabRet.newText, selection = handleTabRet.newSelection))
                }else {
                    f
                }
                newFields.add(newF)
            }
            val newState = internalCreate(
                fields = newFields,
                fieldsId = newId(),
                selectedIndices = selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = focusingLineIdx
            )
            isContentEdited?.value = true
            editorPageIsContentSnapshoted?.value = false
            updateStyles(targetIndices.size, newState) { baseStyles ->
                val targetIndices = targetIndices.sortedDescending()
                val lastIdx = targetIndices.size - 1
                targetIndices.forEachIndexed { idx, targetIndex ->
                    updateStylesAfterDeleteLine(fields, baseStyles, targetIndex, ignoreThis = true, newState)
                    updateStylesAfterInsertLine(
                        baseFields = fields,
                        stylesResult = baseStyles,
                        ignoreThis = idx != lastIdx,
                        insertedContent = newState.fields.get(targetIndex).value.text,
                        insertedContentRangeInNewFields = IntRange(targetIndex, targetIndex),
                        newTextEditorState = newState
                    )
                }
            }
            onChanged(newState, true, true, this, null)
        }
    }
    suspend fun applySyntaxHighlighting(stylesResult: StylesResult, requireCallOnChange: Boolean = true) {
        val funName = "applySyntaxHighlighting"
        val spansLineCount = stylesResult.styles.spans.lineCount
        MyLog.d(TAG, "#$funName: StylesResult may will be apply: $stylesResult, spans.LineCount=$spansLineCount, fields.size=${fields.size}")
        if(codeEditor == null) {
            MyLog.w(TAG, "#$funName: codeEditor is null, will not apply styles")
            return
        }
        val expectedFieldsId = stylesResult.fieldsId
        if(expectedFieldsId.isBlank()) {
            return
        }
        val styles = stylesResult.styles
        val inDarkTheme = stylesResult.inDarkTheme
        val isForThisInstance = expectedFieldsId == fieldsId && inDarkTheme == Theme.inDarkTheme
        if(!isForThisInstance) {
            return
        }
        if(!stylesResult.applied.compareAndSet(false, true)) {
            MyLog.d(TAG, "styles already applied, uniqueId=${stylesResult.uniqueId}")
            return
        }
        lock.withLock {
            val syntaxHighlightStorage = mutableMapOf<String, AnnotatedString>()
            try {
                val spansReader = styles.spans.read()
                fields.forEachIndexedBetter { idx, value ->
                    val spans = spansReader.getSpansOnLine(idx)
                    val annotatedString = codeEditor.generateAnnotatedStringForLine(value, spans).let {
                        codeEditor.ifSameReturnCachedAnnotatedString(value.syntaxHighlightId, it)
                    }
                    syntaxHighlightStorage.put(
                        value.syntaxHighlightId,
                        annotatedString
                    )
                }
            }catch (e: Exception) {
                MyLog.e(TAG, "#$funName: apply styles err: fieldsId=$fieldsId, stylesResult=$stylesResult, err=${e.localizedMessage}")
                e.printStackTrace()
                return
            }
            codeEditor?.putSyntaxHighlight(fieldsId, SyntaxHighlightResult(inDarkTheme = inDarkTheme, storage = syntaxHighlightStorage))
        }
        if(requireCallOnChange) {
            codeEditor?.doActWithLatestEditorState("#applySyntaxHighlighting") { latestState ->
                if(latestState.fieldsId == fieldsId) {
                    MyLog.d(TAG, "will call `onChanged()` for fieldsId: $fieldsId")
                    onChanged(latestState.copy(), null, false, this, EditorStateOnChangeCallerFrom.APPLY_SYNTAX_HIGHLIGHTING)
                }else {
                    MyLog.d(TAG, "editor state already changed, ignore style update request by previous style's apply syntax highlighting method, newStateFieldsId=${latestState.fieldsId}, currentAppliedFieldsId=${fieldsId}")
                }
            }
        }
    }
    fun tryGetStylesResult(): StylesResult? {
        if(codeEditor.scopeInvalid()) {
            return null
        }
        return codeEditor?.getLatestStylesResultIfMatch(this)
    }
    fun obtainHighlightedTextField(raw: MyTextFieldState): MyTextFieldState {
        if(codeEditor.scopeInvalid()) {
            return raw
        }
        return codeEditor?.obtainSyntaxHighlight(fieldsId, raw) ?: raw
    }
    private fun updateStylesAfterDeletedLineBreak(
        baseFields: List<MyTextFieldState>,
        stylesResult: StylesResult,
        startLineIndex: Int,
        ignoreThis: Boolean,
        newTextEditorState: TextEditorState
    ) {
        val funName = "updateStylesAfterDeletedLineBreak"
        MyLog.d(TAG, "$funName: $stylesResult")
        if(reAnalyzeBetterThanIncremental(newTextEditorState.fields, ignoreThis)) {
            codeEditor?.analyze(newTextEditorState)
            return
        }
        val endLineIndexInclusive = startLineIndex + 1
        if(!isGoodIndexForList(startLineIndex, baseFields) || !isGoodIndexForList(endLineIndexInclusive, baseFields)) {
            MyLog.d(TAG, "$funName: bad index: start=$startLineIndex, end=$endLineIndexInclusive, list.size=${baseFields.size}")
            return
        }
        val startIdxOfText = getIndexOfText(baseFields, startLineIndex, trueStartFalseEnd = false)
        val endIdxOfText = startIdxOfText + 1
        if(startIdxOfText == -1 || endIdxOfText == -1) {
            return
        }
        val start = CharPosition(startLineIndex, baseFields[startLineIndex].value.text.length, startIdxOfText)
        val end = CharPosition(endLineIndexInclusive, 0, endIdxOfText)
        MyLog.d(TAG, "#$funName: start=$start, end=$end, baseFields.size=${baseFields.size}, newState.fields.size=${newTextEditorState.fields.size}, spansCount=${stylesResult.styles.spans.lineCount}")
        stylesResult.styles.adjustOnDelete(start, end)
        MyLog.d(TAG, "#$funName: adjusted on delete, spans.lineCount = ${stylesResult.styles.spans.lineCount}, baseFields.size = ${baseFields.size}")
        val selectedText = lb
        val lang = codeEditor?.myLang
        if(lang != null) {
            codeEditor.sendUpdateStylesRequest(
                StylesUpdateRequest(
                    ignoreThis = ignoreThis,
                    targetEditorState = newTextEditorState,
                    act = { lang.analyzeManager.delete(start, end, selectedText, it) }
                )
            )
        }
    }
    private fun updateStylesAfterDeleteLine(
        baseFields: List<MyTextFieldState>,
        stylesResult: StylesResult,
        startLineIndex: Int,
        ignoreThis: Boolean,
        newTextEditorState: TextEditorState,
        keepLine: Boolean = true,
    ) {
        val funName = "updateStylesAfterDeleteLine"
        MyLog.d(TAG, "$funName: $stylesResult")
        if(reAnalyzeBetterThanIncremental(newTextEditorState.fields, ignoreThis)) {
            codeEditor?.analyze(newTextEditorState)
            return
        }
        val startLineIndex = if(keepLine) startLineIndex else (startLineIndex - 1).coerceAtLeast(0)
        val endLineIndex = if(keepLine) startLineIndex else (startLineIndex + 1).coerceAtMost(baseFields.lastIndex)
        MyLog.d(TAG, "$funName: startLineIndex: $startLineIndex, endLineIndex=$endLineIndex, list.size=${baseFields.size}")
        if(!isGoodIndexForList(startLineIndex, baseFields) || !isGoodIndexForList(endLineIndex, baseFields)) {
            MyLog.d(TAG, "$funName: bad range: startLineIndex: $startLineIndex, endLineIndex=$endLineIndex, list.size=${baseFields.size}")
            return
        }
        val startIdxOfText = getIndexOfText(baseFields, startLineIndex, trueStartFalseEnd = keepLine)
        val endIdxOfText = getIndexOfText(baseFields, endLineIndex, trueStartFalseEnd = false)
        if(startIdxOfText == -1 || endIdxOfText == -1) {
            return
        }
        val start = CharPosition(startLineIndex, if(keepLine) 0 else baseFields[startLineIndex].value.text.length, startIdxOfText)
        val end = CharPosition(endLineIndex, baseFields[endLineIndex].value.text.length, endIdxOfText)
        val deletedContent = if(keepLine) {  
            baseFields.get(startLineIndex).value.text
        } else {  
            baseFields.get(endLineIndex).value.text + lb
        }
        MyLog.d(TAG, "#$funName: start=$start, end=$end, baseFields.size=${baseFields.size - 1}, newState.fields.size=${newTextEditorState.fields.size}, spansCount=${stylesResult.styles.spans.lineCount}")
        stylesResult.styles.adjustOnDelete(start, end)
        MyLog.d(TAG, "#$funName: adjusted on delete, spans.lineCount = ${stylesResult.styles.spans.lineCount}, baseFields.size = ${baseFields.size - 1}")
        val lang = codeEditor?.myLang
        if(lang != null) {
            codeEditor.sendUpdateStylesRequest(
                StylesUpdateRequest(
                    ignoreThis = ignoreThis,
                    targetEditorState = newTextEditorState,
                    act = { lang.analyzeManager.delete(start, end, deletedContent, it) }
                )
            )
        }
    }
    private fun updateStylesAfterInsertLine(
        baseFields: List<MyTextFieldState>,
        stylesResult: StylesResult,
        ignoreThis: Boolean,
        insertedContent: String,
        insertedContentRangeInNewFields: IntRange,
        newTextEditorState: TextEditorState,
    ) {
        val funName = "updateStylesAfterInsertLine"
        val startLineIndex = insertedContentRangeInNewFields.start
        MyLog.d(TAG, "$funName: startLineIndex=$startLineIndex, baseFields.size=${baseFields.size}, $stylesResult")
        if(reAnalyzeBetterThanIncremental(newTextEditorState.fields, ignoreThis)) {
            codeEditor?.analyze(newTextEditorState)
            return
        }
        if(startLineIndex >= baseFields.size) {
            MyLog.d(TAG, "$funName: startLineIndex '$startLineIndex' is invalid, baseFields.size=${baseFields.size}")
            return
        }
        val startIdxOfText = getIndexOfText(baseFields, startLineIndex, trueStartFalseEnd = true)
        if(startIdxOfText < 0) {
            MyLog.w(TAG, "`startIndexOfText` invalid: $startIdxOfText")
            return
        }
        val start = CharPosition(startLineIndex, 0, startIdxOfText)
        val insertIndex = insertedContentRangeInNewFields.endInclusive
        val end = CharPosition(insertIndex, newTextEditorState.fields.get(insertIndex).value.text.length, startIdxOfText + insertedContent.length)
        MyLog.d(TAG, "#$funName: start=$start, end=$end, baseFields.size=${baseFields.size}, newState.fields.size=${newTextEditorState.fields.size}, spansCount=${stylesResult.styles.spans.lineCount}")
        stylesResult.styles.adjustOnInsert(start, end)
        MyLog.d(TAG, "#$funName: adjusted on insert, spans.lineCount = ${stylesResult.styles.spans.lineCount}, baseFields.size = ${baseFields.size}")
        val selectedText = insertedContent
        val lang = codeEditor?.myLang
        if(lang != null) {
            codeEditor.sendUpdateStylesRequest(
                StylesUpdateRequest(
                    ignoreThis = ignoreThis,
                    targetEditorState = newTextEditorState,
                    act = {
                        lang.analyzeManager.insert(start, end, selectedText, it)
                    }
                )
            )
        }
    }
    private fun reAnalyzeBetterThanIncremental(baseFields: List<MyTextFieldState>, ignoreThis: Boolean) : Boolean {
        return ignoreThis.not() && (baseFields.isEmpty() || (baseFields.size == 1 && baseFields[0].value.text.isBlank()))
    }
    fun getIndexOfText(
        baseFields: List<MyTextFieldState>,
        lineIdx: Int,
        trueStartFalseEnd: Boolean,
    ):Int {
        if(lineIdx < 0 || baseFields.isEmpty()) {
            return 0
        }
        if(lineIdx >= baseFields.size) {
            return baseFields.sumOf { it.value.text.length + 1 } - 1
        }
        val lineIdx = if(trueStartFalseEnd) lineIdx - 1 else lineIdx
        var li = -1
        var charIndex = 0
        while (++li <= lineIdx) {
            val f = baseFields.getOrNull(li) ?: return -1
            charIndex += (f.value.text.length + 1)
        }
        return charIndex + (if(lineIdx == baseFields.lastIndex) -1 else 0)
    }
    suspend fun moveCursor(
        trueToLeftFalseRight: Boolean,
        textFieldState: MyTextFieldState,
        targetFieldIndex: Int,
        headOrTail: Boolean
    ) {
        if(isMultipleSelectionMode) {
            return
        }
        if(targetFieldIndex < 0 || targetFieldIndex >= fields.size) {
            return
        }
        val textRange = textFieldState.value.selection
        if(textRange.collapsed.not()) {
            return
        }
        val atLineHead = textRange.start == 0
        val atLineTail = textRange.start == textFieldState.value.text.length
        val atFileHead = targetFieldIndex == 0
        val atFileTail = targetFieldIndex == fields.lastIndex
        if(trueToLeftFalseRight && ((headOrTail && atLineHead) || (!headOrTail && atLineHead && atFileHead))) {
            return
        }
        if(!trueToLeftFalseRight && ((headOrTail && atLineTail) || (!headOrTail && atLineTail && atFileTail))) {
            return
        }
        if(atLineHead && trueToLeftFalseRight) {
            selectField(
                targetIndex = targetFieldIndex - 1,
                option = SelectionOption.LAST_POSITION
            )
            return
        }
        if(atLineTail && !trueToLeftFalseRight) {
            selectField(
                targetIndex = targetFieldIndex + 1,
                option = SelectionOption.FIRST_POSITION
            )
            return
        }
        lock.withLock {
            val newTextRange = if(trueToLeftFalseRight) {
                if(headOrTail) TextRange(0) else TextRange((textRange.start-1).coerceAtLeast(0))
            }else {
                if(headOrTail) TextRange(textFieldState.value.text.length) else TextRange((textRange.start+1).coerceAtMost(textFieldState.value.text.length))
            }
            val newFields = fields.toMutableList()
            newFields[targetFieldIndex] = newFields[targetFieldIndex].copy(value = textFieldState.value.copy(selection = newTextRange))
            val newState = internalCreate(
                fields = newFields,
                fieldsId = fieldsId,
                selectedIndices = selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = focusingLineIdx
            )
            onChanged(newState, null, false, this, null)
        }
    }
    suspend fun paste(
        text: String,
        afterReplacedAllThenDoAct: ((newFields: List<MyTextFieldState>) -> Unit)? = null
    ) {
        if(isSelectedAllFields()) {  
            lock.withLock {
                val newFields = textToFields(text)
                val newSelectedIndices = newFields.indices.toList()
                val newState = copy(
                    fieldsId = newId(),
                    fields = newFields,
                    selectedIndices = newSelectedIndices,
                    focusingLineIdx = newFields.lastIndex
                )
                isContentEdited?.value=true
                editorPageIsContentSnapshoted?.value=false
                EditCache.writeToFile(text)
                onChanged(newState, true, true, this, null)
                codeEditor?.startAnalyzeWhenUserStopInputForAWhile(newState)
                afterReplacedAllThenDoAct?.invoke(newFields)
            }
        }else {  
            appendTextToLastSelectedLine(text)
        }
    }
    fun isSelectedAllFields() = isMultipleSelectionMode && selectedIndices.size == fields.size
    fun isFieldSelected(idx: Int) = selectedIndices.contains(idx)
    suspend fun selectFieldValue(
        targetIndex: Int,
        textFieldValue: TextFieldValue,
    ) {
        selectField(targetIndex, provideTextFieldValue = textFieldValue)
    }
    suspend fun asUnsaved() {
        lock.withLock {
            isContentEdited?.value=true
            editorPageIsContentSnapshoted?.value=false
            onChanged(copy(fieldsId = newId()), null, false, this, null)
        }
    }
    companion object {
        fun linesToFields(lines: List<String>) = createInitTextFieldStates(lines)
        fun fuckSafFileToFields(file: FuckSafFile, charsetName: String?) = linesToFields(FsUtils.readLinesFromFile(file, charsetName, addNewLineIfFileEmpty = true))
        fun textToFields(text: String) = linesToFields(text.lines())
        fun newId():String {
            return FieldsId.generateString()
        }
    }
}
private fun createInitTextFieldStates(list: List<String>) = if(list.isEmpty()) {
    listOf(MyTextFieldState())
}else {
    list.map { s ->
        MyTextFieldState(
            value = TextFieldValue(s),
        )
    }
}
data class FieldsId(
    val id: String = generateRandomString(20),
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        val EMPTY = FieldsId("", 0L)
        fun generateString():String {
            return FieldsId().toString()
        }
        fun parse(fieldsId: String?):FieldsId {
            if(fieldsId == null) {
                return EMPTY
            }
            return fieldsId.split(":").let {
                FieldsId(
                    id = it.get(0),
                    timestamp = it.getOrNull(1)?.let { parseLongOrDefault(it, null) } ?: 0L
                )
            }
        }
    }
    override fun toString(): String {
        return "$id:$timestamp"
    }
}
enum class SelectionOption {
    FIRST_POSITION,
    LAST_POSITION,
    NONE,
    CUSTOM, 
}
enum class FindDirection {
    UP,
    DOWN
}
private class SelectFieldInternalRet(
    val fields: List<MyTextFieldState>,
    val selectedIndices: List<Int>,
    val focusingLineIdx:Int?,
)
private data class HandleTabRet(
    val newText:String,
    val newSelection: TextRange,
    val changed: Boolean
)
enum class EditorStateOnChangeCallerFrom {
    APPLY_SYNTAX_HIGHLIGHTING,
}
val symbolPairsMap = mapOf<String, String>(
    "\"" to "\"",
    "{" to "}",
    "(" to ")",
    "[" to "]",
    "\'" to "\'",
    "<" to ">",
    "“" to "”",
    "（" to "）",
    "《" to "》",
    "〈" to "〉",
    "‘" to "’",
    "【" to "】",
    "『" to "』",
    "「" to "」",
    "［" to "］",
)
