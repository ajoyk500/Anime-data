package com.akcreation.gitsilent.fileeditor.texteditor.view

import android.os.Parcelable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.AcceptButtons
import com.akcreation.gitsilent.compose.ClickableText
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.DefaultPaddingRow
import com.akcreation.gitsilent.compose.DisableSoftKeyboard
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.LineNum
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.dev.bug_Editor_GoToColumnCantHideKeyboard_Fixed
import com.akcreation.gitsilent.dev.bug_Editor_SelectColumnRangeOfLine_Fixed
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.fileeditor.texteditor.state.FindDirection
import com.akcreation.gitsilent.fileeditor.texteditor.state.LineChangeType
import com.akcreation.gitsilent.fileeditor.texteditor.state.MyTextFieldState
import com.akcreation.gitsilent.fileeditor.texteditor.state.SelectionOption
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.FileEditedPos
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.PatchUtil
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.fileopenhistory.FileOpenHistoryMan
import com.akcreation.gitsilent.utils.isGoodIndexForStr
import com.akcreation.gitsilent.utils.parseLineAndColumn
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

private const val TAG = "TextEditor"
internal const val lineNumOffsetForGoToEditor = Cons.scrollToItemOffset
@Parcelize
class ExpectConflictStrDto(
    var conflictStartStr: String = "",
    var conflictSplitStr: String = "",
    var conflictEndStr: String = "",
    var curConflictStr: String = conflictStartStr,
    var curConflictStrMatched: Boolean = false,
):Parcelable {
    fun reset() {
        curConflictStr = conflictStartStr
        curConflictStrMatched = false
    }
    fun getNextExpectConflictStr():String{
        return if(curConflictStr == conflictStartStr) {
            conflictSplitStr
        }else if(curConflictStr == conflictSplitStr) {
            conflictEndStr
        }else { 
            conflictStartStr
        }
    }
    fun getCurAndNextExpect():Pair<Int,Int> {
        val curExpect = if(curConflictStr.startsWith(conflictStartStr)){
            0
        }else if(curConflictStr.startsWith(conflictSplitStr)) {
            1
        }else {
            2
        }
        val nextExcept = if(curExpect + 1 > 2) 0 else curExpect+1
        return Pair(curExpect, nextExcept)
    }
}
typealias DecorationBoxComposable = @Composable (
    index: Int,
    size: Int,  
    isSelected: Boolean,
    currentField: MyTextFieldState,
    focusingIdx:Int,
    isMultiSelectionMode: Boolean,
    innerTextField: @Composable (modifier: Modifier) -> Unit
) -> Unit
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextEditor(
    stateKeyTag:String,
    disableSoftKb: MutableState<Boolean>,
    updateLastCursorAtColumn:(Int)->Unit,
    getLastCursorAtColumnValue:()->Int,
    ignoreFocusOnce: MutableState<Boolean>,
    undoStack:UndoStack,
    curPreviewScrollState: ScrollState,
    requireEditorScrollToPreviewCurPos:MutableState<Boolean>,
    requestFromParent:MutableState<String>,
    fileFullPath:FilePath,
    lastEditedPos: FileEditedPos,
    textEditorState: TextEditorState,
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues = PaddingValues(),
    lastScrollEvent: CustomStateSaveable<ScrollEvent?>,
    listState: LazyListState,
    editorPageIsInitDone:MutableState<Boolean>,
    goToLine:Int,  
    readOnlyMode:Boolean,
    searchMode:MutableState<Boolean>,
    mergeMode:Boolean,
    patchMode:Boolean,
    searchKeyword:String,
    fontSize: MutableIntState,
    fontColor: Color,
    scrollIfIndexInvisible: (index: Int) -> Unit,
    decorationBox: DecorationBoxComposable = { _, _, _, _, _,_, innerTextField -> innerTextField(Modifier) },
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val density = LocalDensity.current
    val deviceConfiguration = AppModel.getCurActivityConfig()
    val clipboardManager = LocalClipboardManager.current
    val conflictOursBlockBgColor = MyStyleKt.ConflictBlock.getConflictOursBlockBgColor()
    val conflictTheirsBlockBgColor = MyStyleKt.ConflictBlock.getConflictTheirsBlockBgColor()
    val conflictStartLineBgColor = MyStyleKt.ConflictBlock.getConflictStartLineBgColor()
    val conflictSplitLineBgColor = MyStyleKt.ConflictBlock.getConflictSplitLineBgColor()
    val conflictEndLineBgColor = MyStyleKt.ConflictBlock.getConflictEndLineBgColor()
    val acceptOursBtnColor = MyStyleKt.ConflictBlock.getAcceptOursIconColor()
    val acceptTheirsBtnColor = MyStyleKt.ConflictBlock.getAcceptTheirsIconColor()
    val acceptBothBtnColor = MyStyleKt.ConflictBlock.getAcceptBothIconColor()
    val rejectBothBtnColor = MyStyleKt.ConflictBlock.getRejectBothIconColor()
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val inDarkTheme = Theme.inDarkTheme
    val (virtualWidth, virtualHeight) = UIHelper.Size.editorVirtualSpace()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val conflictKeyword = remember(settings.editor.conflictStartStr) { mutableStateOf(settings.editor.conflictStartStr) }
    val focusingLineIdx = rememberUpdatedState(textEditorState.focusingLineIdx)
    val showGoToLineDialog  = rememberSaveable { mutableStateOf(false) }
    val goToLineValue  = mutableCustomStateOf(stateKeyTag, "goToLineValue") { TextFieldValue("") }
    val focusWhenGoToLine = rememberSaveable { mutableStateOf(false) }
    val lastVisibleLineState  = rememberSaveable { mutableStateOf(0) }
    val expectConflictStrDto = rememberSaveable(settings.editor.conflictStartStr, settings.editor.conflictSplitStr, settings.editor.conflictEndStr) {
        mutableStateOf(
            ExpectConflictStrDto(
                conflictStartStr = settings.editor.conflictStartStr,
                conflictSplitStr = settings.editor.conflictSplitStr,
                conflictEndStr = settings.editor.conflictEndStr
            )
        )
    }
    fun getFocusedLineIdxOrFirstVisibleLine() = focusingLineIdx.value ?: listState.firstVisibleItemIndex;
    fun resolveSearchStartPos(toNext:Boolean): SearchPos {
        var newLineIndex = getFocusedLineIdxOrFirstVisibleLine()
        var newColumnIndex = 0
        val (curIndex, curField) = textEditorState.getFieldByIdx(newLineIndex)
        if(curIndex != null && curField != null) {
            newLineIndex = curIndex
            newColumnIndex = curField.value.selection.let { if(toNext) it.max else it.min - 1 }
            if(!isGoodIndexForStr(newColumnIndex, curField.value.text)) {
                newColumnIndex = -1
                if(toNext) newLineIndex++ else newLineIndex--
            }
        }
        return SearchPos(newLineIndex, newColumnIndex)
    }
    fun jumpToLineIndex(
        lineIndex:Int,
        goColumn: Boolean=false,
        columnStartIndex:Int=0,
        columnEndIndexExclusive:Int=columnStartIndex,
        requireHideKeyboard:Boolean = false
    ){
        lastScrollEvent.value = ScrollEvent(
            index = lineIndex,
            forceGo = true,
            goColumn = goColumn,
            columnStartIndexInclusive = columnStartIndex,
            columnEndIndexExclusive = columnEndIndexExclusive,
            requireHideKeyboard = requireHideKeyboard
        )
    }
    suspend fun doSearchNoCatch(keyword:String, toNext:Boolean) {
        if(keyword.isEmpty() || textEditorState.fields.isEmpty()) {
            return
        }
        val funName = "doSearchNoCatch"
        val startPos = resolveSearchStartPos(toNext)
        if(AppModel.devModeOn) {
            MyLog.w(TAG, "$TAG#$funName(): will use: toNext=$toNext, startPos=$startPos")
        }
        val keyWordLen = keyword.length
        val posResult = textEditorState.doSearch(keyword.lowercase(), toNext = toNext, startPos = startPos)
        val foundPos = posResult.foundPos
        if(foundPos == SearchPos.NotFound) {
            if(!searchMode.value && mergeMode) {
                Msg.requireShow(activityContext.getString(R.string.no_conflict_found))
            }else {
                Msg.requireShow(activityContext.getString(R.string.not_found))
            }
        }else {  
            val keywordStartAtLine = foundPos.columnIndex
            val keywordEndExclusiveAtLine = foundPos.columnIndex + keyWordLen
            jumpToLineIndex(
                lineIndex = foundPos.lineIndex,
                goColumn = true,
                columnStartIndex = keywordEndExclusiveAtLine, 
                columnEndIndexExclusive = keywordStartAtLine,
                requireHideKeyboard = false,  
            )
        }
        if(AppModel.devModeOn) {
            MyLog.w(TAG, "$TAG#$funName(): found result: toNext=$toNext, foundPos=$foundPos")
        }
    }
    suspend fun doSearch(keyword:String, toNext:Boolean) {
        try {
            doSearchNoCatch(
                keyword = keyword,
                toNext = toNext,
            )
        }catch (e: Exception) {
            MyLog.e(TAG, "text editor `doSearch()` err: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
    val isInitDone = editorPageIsInitDone
    if(requestFromParent.value==PageRequest.hideKeyboardForAWhile) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
        }
    }
    if(requestFromParent.value==PageRequest.editorQuitSelectionMode) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#quitSelectionMode") { textEditorState ->
                textEditorState.quitSelectionMode()
            }
        }
    }
    if(requestFromParent.value==PageRequest.requestUndo) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                textEditorState.codeEditor?.doActWithLatestEditorState("#undo") { textEditorState ->
                    textEditorState.undo(undoStack = undoStack)
                }
            }
        }
    }
    if(requestFromParent.value==PageRequest.requestRedo) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                textEditorState.codeEditor?.doActWithLatestEditorState("#redo") { textEditorState ->
                    textEditorState.redo(undoStack=undoStack)
                }
            }
        }
    }
    if(requestFromParent.value==PageRequest.goToTop) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            lastScrollEvent.value = ScrollEvent(0, forceGo = true)
        }
    }
    if(requestFromParent.value==PageRequest.goToLine) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            focusWhenGoToLine.value = true
            goToLineValue.value = goToLineValue.value.let { it.copy(selection = TextRange(0, it.text.length)) }
            showGoToLineDialog.value = true
        }
    }
    if(requestFromParent.value==PageRequest.requireSearch) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            searchMode.value = true
        }
    }
    if(requestFromParent.value==PageRequest.findPrevious) {
        PageRequest.clearStateThenDoAct(requestFromParent) findPrevious@{
            if(searchKeyword.isEmpty()) {
                return@findPrevious
            }
            doJobThenOffLoading {
                doSearch(searchKeyword, toNext = false)
            }
        }
    }
    if(requestFromParent.value==PageRequest.findNext) {
        PageRequest.clearStateThenDoAct(requestFromParent) findNext@{
            if(searchKeyword.isEmpty()) {
                return@findNext
            }
            doJobThenOffLoading {
                doSearch(searchKeyword, toNext = true)
            }
        }
    }
    if(requestFromParent.value==PageRequest.showFindNextAndAllCount) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                val allCount = textEditorState.getKeywordCount(searchKeyword)
                Msg.requireShow(replaceStringResList(activityContext.getString(R.string.find_next_all_count), listOf(allCount.toString())))
            }
        }
    }
    if(requestFromParent.value==PageRequest.previousConflict) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                val currentLineIdx = getFocusedLineIdxOrFirstVisibleLine()
                val nextSearchLine = textEditorState.fields.get(currentLineIdx).value.text
                if(nextSearchLine.startsWith(settings.editor.conflictStartStr)) {
                    conflictKeyword.value = settings.editor.conflictStartStr
                }else if(nextSearchLine.startsWith(settings.editor.conflictSplitStr)) {
                    conflictKeyword.value = settings.editor.conflictSplitStr
                }else if(nextSearchLine.startsWith(settings.editor.conflictEndStr)) {
                    conflictKeyword.value = settings.editor.conflictEndStr
                }
                val previousKeyWord = getPreviousKeyWordForConflict(conflictKeyword.value, settings)
                conflictKeyword.value = previousKeyWord
                doSearch(previousKeyWord, toNext = false)
            }
        }
    }
    if(requestFromParent.value==PageRequest.nextConflict) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                val currentLineIdx = getFocusedLineIdxOrFirstVisibleLine()
                val nextSearchLine = textEditorState.fields.get(currentLineIdx).value.text
                if(nextSearchLine.startsWith(settings.editor.conflictStartStr)) {
                    conflictKeyword.value = settings.editor.conflictStartStr
                }else if(nextSearchLine.startsWith(settings.editor.conflictSplitStr)) {
                    conflictKeyword.value = settings.editor.conflictSplitStr
                }else if(nextSearchLine.startsWith(settings.editor.conflictEndStr)) {
                    conflictKeyword.value = settings.editor.conflictEndStr
                }
                val nextKeyWord = getNextKeyWordForConflict(conflictKeyword.value, settings)
                conflictKeyword.value = nextKeyWord
                doSearch(nextKeyWord, toNext = true)
            }
        }
    }
    if(requestFromParent.value==PageRequest.showNextConflictAndAllConflictsCount) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                val allCount = textEditorState.getKeywordCount(settings.editor.conflictStartStr)
                Msg.requireShow(replaceStringResList(activityContext.getString(R.string.next_conflict_all_count), listOf(allCount.toString())))
            }
        }
    }
    if(requestFromParent.value==PageRequest.switchBetweenFirstLineAndLastEditLine) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val notAtTop = listState.firstVisibleItemIndex != 0
            val position = if(notAtTop) 0 else getFocusedLineIdxOrFirstVisibleLine()
            lastScrollEvent.value = ScrollEvent(position, forceGo = true)
        }
    }
    if(requestFromParent.value==PageRequest.switchBetweenTopAndLastPosition) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val lastVisibleLine = listState.firstVisibleItemIndex
            val notAtTop = lastVisibleLine != 0
            val position = if(notAtTop) {
                lastVisibleLineState.value = lastVisibleLine
                0
            } else {
                lastVisibleLineState.value
            }
            lastScrollEvent.value = ScrollEvent(position, forceGo = true)
        }
    }
    fun doGoToLine(line:String, focus:Boolean = true) {
        val linNumParseResult = parseLineAndColumn(line)
        val targetLineIdx = linNumParseResult.lineNumToIndex(
            curLineIndex = focusingLineIdx.value ?: listState.firstVisibleItemIndex,
            maxLineIndex = textEditorState.fields.size
        )
        if(focus) {
            lastScrollEvent.value = ScrollEvent(
                index = targetLineIdx,
                forceGo = true,
                goColumn = true,
                columnStartIndexInclusive = linNumParseResult.columnNumToIndex(),
            )
        }else {  
            UIHelper.scrollToItem(scope, listState, targetLineIdx + lineNumOffsetForGoToEditor)
        }
    }
    if(showGoToLineDialog.value) {
        val onOK = {
            showGoToLineDialog.value = false
            doGoToLine(goToLineValue.value.text, focusWhenGoToLine.value)
        }
        val focusRequester = remember { FocusRequester() }
        val firstLine = "1"
        val lastLine = ""+textEditorState.fields.size
        val lineNumRange = "$firstLine-$lastLine"
        ConfirmDialog(
            title = stringResource(R.string.go_to_line),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier= Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                    ,
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MyStyleKt.defaultItemPadding)
                            .focusRequester(focusRequester)
                            .onPreviewKeyEvent { event ->
                                if (event.type != KeyEventType.KeyDown) {
                                    false
                                } else if (event.key == Key.Enter) {
                                    onOK()
                                    true
                                } else {
                                    false
                                }
                            }
                        ,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            onOK()
                        }),
                        singleLine = true,
                        value = goToLineValue.value,
                        onValueChange = {
                            goToLineValue.value=it
                        },
                        label = {
                            Text(stringResource(R.string.line_number)+"($lineNumRange)")
                        },
                        placeholder = {
                            Text("e.g. 1:5")
                        }
                    )
                    DefaultPaddingRow {
                        MyCheckBox(stringResource(R.string.focus_line), focusWhenGoToLine)
                    }
                    Column(
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, end = 10.dp)
                        ,
                        horizontalAlignment = Alignment.End
                    ) {
                        ClickableText(
                            text = stringResource(R.string.first_line),
                            modifier = MyStyleKt.ClickableText.modifier.clickable {
                                goToLineValue.value = firstLine.let { TextFieldValue(it, selection = TextRange(it.length)) }
                            },
                            fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(15.dp))
                        ClickableText(
                            text = stringResource(R.string.last_line),
                            modifier = MyStyleKt.ClickableText.modifier.clickable {
                                goToLineValue.value = lastLine.let { TextFieldValue(it, selection = TextRange(it.length)) }
                            },
                            fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            },
            okBtnEnabled = goToLineValue.value.text.isNotBlank(),
            okBtnText = stringResource(id = R.string.go),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showGoToLineDialog.value = false }
        ) {
            onOK()
        }
        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }
    val delStartIndex = rememberSaveable { mutableIntStateOf(-1) }
    val delEndIndex = rememberSaveable { mutableIntStateOf(-1) }
    val startConflictLineIndexState = rememberSaveable { mutableIntStateOf(-1) }
    val splitConflictLineIndexState = rememberSaveable { mutableIntStateOf(-1) }
    val endConflictLineIndexState = rememberSaveable { mutableIntStateOf(-1) }
    val delSingleIndex = rememberSaveable { mutableIntStateOf(-1) }
    val acceptOursState = rememberSaveable { mutableStateOf(false) }
    val acceptTheirsState = rememberSaveable { mutableStateOf(false) }
    val showAcceptConfirmDialog = rememberSaveable { mutableStateOf(false) }
    val prepareAcceptBlock= label@{acceptOurs: Boolean, acceptTheirs:Boolean, index: Int, curLineText: String ->
        val curStartsWithStart = curLineText.startsWith(settings.editor.conflictStartStr)
        val curStartsWithSplit = curLineText.startsWith(settings.editor.conflictSplitStr)
        val curStartsWithEnd = curLineText.startsWith(settings.editor.conflictEndStr)
        if(!(curStartsWithStart || curStartsWithSplit || curStartsWithEnd)) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_conflict_block))
            return@label
        }
        val firstFindDirection = if(curStartsWithStart) {
            FindDirection.DOWN
        }else {
            FindDirection.UP
        }
        val firstExpectStr = if(curStartsWithStart) {
            settings.editor.conflictSplitStr
        }else if(curStartsWithSplit) {
            settings.editor.conflictStartStr
        }else {  
            settings.editor.conflictSplitStr
        }
        val (firstIndex, _) = textEditorState.indexAndValueOf(startIndex=index, direction=firstFindDirection, predicate={it.startsWith(firstExpectStr)}, includeStartIndex = false)
        if(firstIndex == -1) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_conflict_block))
            return@label
        }
        val secondFindDirection = if(curStartsWithEnd) {
            FindDirection.UP
        }else {
            FindDirection.DOWN
        }
        val secondExpectStr = if(curStartsWithEnd) {
            settings.editor.conflictStartStr
        }else {
            settings.editor.conflictEndStr
        }
        val secondStartFindIndexAt =if(curStartsWithSplit) {
            index
        }else {
            firstIndex
        }
        val (secondIndex, _) = textEditorState.indexAndValueOf(startIndex=secondStartFindIndexAt, direction=secondFindDirection, predicate={it.startsWith(secondExpectStr)}, includeStartIndex = false)
        if(secondIndex == -1) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_conflict_block))
            return@label
        }
        val startConflictLineIndex = if(curStartsWithStart) index else if(curStartsWithSplit) firstIndex else secondIndex  
        val splitConflictLineIndex = if(curStartsWithStart || curStartsWithEnd) firstIndex else index  
        val endConflictLineIndex = if(curStartsWithStart || curStartsWithSplit) secondIndex else index  
        startConflictLineIndexState.value = startConflictLineIndex
        splitConflictLineIndexState.value = splitConflictLineIndex
        endConflictLineIndexState.value = endConflictLineIndex
        if(acceptOurs && acceptTheirs.not()) {
            acceptOursState.value = true
            acceptTheirsState.value = false
            delSingleIndex.value = startConflictLineIndex
            delStartIndex.value = splitConflictLineIndex
            delEndIndex.value = endConflictLineIndex
        }else if(acceptOurs.not() && acceptTheirs) {
            acceptOursState.value = false
            acceptTheirsState.value = true
            delSingleIndex.value = endConflictLineIndex
            delStartIndex.value = startConflictLineIndex
            delEndIndex.value = splitConflictLineIndex
        }else if(acceptOurs && acceptTheirs) {  
            acceptOursState.value = true
            acceptTheirsState.value = true
            delSingleIndex.value = startConflictLineIndex
            delStartIndex.value= splitConflictLineIndex
            delEndIndex.value = endConflictLineIndex
        }else { 
            acceptOursState.value = false
            acceptTheirsState.value = false
            delStartIndex.value = startConflictLineIndex
            delEndIndex.value = endConflictLineIndex
        }
        showAcceptConfirmDialog.value = true
    }
    if(showAcceptConfirmDialog.value) {
        ConfirmDialog2(
            title = if(acceptOursState.value && acceptTheirsState.value.not()) stringResource(R.string.accept_ours)
            else if(acceptOursState.value.not() && acceptTheirsState.value) stringResource(R.string.accept_theirs)
            else if(acceptOursState.value && acceptTheirsState.value) stringResource(R.string.accept_both)
            else stringResource(R.string.reject_both),
            text = if(acceptOursState.value && acceptTheirsState.value.not()) replaceStringResList(stringResource(R.string.will_accept_ours_and_delete_lines_line_indexs), listOf(""+(delSingleIndex.value + 1), ""+(delStartIndex.value + 1), ""+(delEndIndex.value + 1)))
            else if(acceptOursState.value.not() && acceptTheirsState.value) replaceStringResList(stringResource(R.string.will_accept_theirs_and_delete_lines_line_indexs), listOf(""+(delSingleIndex.value + 1), ""+(delStartIndex.value + 1), ""+(delEndIndex.value + 1)))
            else if(acceptOursState.value && acceptTheirsState.value) replaceStringResList(stringResource(R.string.will_accept_both_and_delete_lines_line_indexs), listOf(""+(delSingleIndex.value + 1), ""+(delStartIndex.value + 1), ""+(delEndIndex.value + 1)))
            else replaceStringResList(stringResource(R.string.will_reject_both_and_delete_lines_line_indexs), listOf(""+(delStartIndex.value + 1), ""+(delEndIndex.value + 1))),
            onCancel = {showAcceptConfirmDialog.value = false}
        ) {
            showAcceptConfirmDialog.value=false
            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#deleteLineByIndices") { textEditorState ->
                try {
                    var baseFields:List<MyTextFieldState>? = null
                    if(acceptOursState.value) {
                        baseFields = textEditorState.setChangeTypeToFields(
                            IntRange(start = startConflictLineIndexState.value, endInclusive = splitConflictLineIndexState.value).toList(),
                            LineChangeType.ACCEPT_OURS,
                            baseFields,
                            applyNewSate = false,
                        )
                    }
                    if(acceptTheirsState.value) {
                        baseFields = textEditorState.setChangeTypeToFields(
                            IntRange(start = splitConflictLineIndexState.value, endInclusive = endConflictLineIndexState.value).toList(),
                            LineChangeType.ACCEPT_THEIRS,
                            baseFields,
                            applyNewSate = false,
                        )
                    }
                    val indicesWillDel = if((acceptOursState.value && acceptTheirsState.value.not()) || (acceptOursState.value.not() && acceptTheirsState.value)){
                        val tmp = mutableListOf(delSingleIndex.value)
                        tmp.addAll(IntRange(start = delStartIndex.value, endInclusive = delEndIndex.value).toList())
                        tmp
                    }else if(acceptOursState.value && acceptTheirsState.value) {  
                        val tmp = mutableListOf(delSingleIndex.value)
                        tmp.add(delStartIndex.value)
                        tmp.add(delEndIndex.value)
                        tmp
                    }else {  
                        IntRange(start = delStartIndex.value, endInclusive = delEndIndex.value).toList()
                    }
                    textEditorState.deleteLineByIndices(indicesWillDel, baseFields)
                }catch (e: Exception) {
                    val errPrefix = if(acceptOursState.value && acceptTheirsState.value) "Accept Both err"
                    else if(acceptOursState.value.not() && acceptTheirsState.value) "Accept Theirs err"
                    else if(acceptOursState.value && acceptTheirsState.value.not()) "Accept Ours err"
                    else "Reject Both err";
                    Msg.requireShowLongDuration("$errPrefix: ${e.localizedMessage}")
                    MyLog.e(TAG, "TextEditor#AcceptConfirmDialog: $errPrefix: ${e.stackTraceToString()}")
                }
            }
        }
    }
    if(requireEditorScrollToPreviewCurPos.value) {
        requireEditorScrollToPreviewCurPos.value = false
        try {
            val fontSizeInPx = UIHelper.spToPx(sp = fontSize.intValue, density = density)
            val screenWidthInPx = UIHelper.dpToPx(dp = deviceConfiguration.screenWidthDp, density = density)
            val screenHeightInPx = UIHelper.dpToPx(dp = deviceConfiguration.screenHeightDp, density = density)
            val previewCurAt = curPreviewScrollState.value
            val targetLineIndex = textEditorState.pxToLineIdx(targetPx=previewCurAt, fontSizeInPx=fontSizeInPx, screenWidthInPx=screenWidthInPx, screenHeightInPx=screenHeightInPx)
            doGoToLine((targetLineIndex + 1).toString()) 
        }catch (e:Exception) {
            MyLog.d(TAG, "let editor scroll to current preview position failed: ${e.stackTraceToString()}")
        }
    }
    val lastValidFocusingLineIdx = rememberSaveable { mutableIntStateOf(lastEditedPos.lineIndex) }
    val lastValidEditedColumnLineIndex = rememberSaveable { mutableIntStateOf(lastEditedPos.columnIndex) }
    LaunchedEffect(focusingLineIdx.value) {
        val (lineIdx, field) = textEditorState.getCurrentField()
        if(lineIdx != null && field != null) {
            lastValidFocusingLineIdx.intValue = lineIdx
            if(textEditorState.isMultipleSelectionMode) {
                lastValidEditedColumnLineIndex.intValue = field.value.selection.end
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose TextEditorOnDispose@{
            try {
                val lastEditedLineIdx = lastValidFocusingLineIdx.intValue
                val oldLinePos = FileOpenHistoryMan.get(fileFullPath.ioPath)
                val needUpdateLastEditedLineIndex = oldLinePos?.lineIndex != lastEditedLineIdx
                val currentFirstVisibleIndex = listState.firstVisibleItemIndex
                val needUpdateFirstVisibleLineIndex = oldLinePos?.firstVisibleLineIndex != currentFirstVisibleIndex
                val editedColumnIndex = lastValidEditedColumnLineIndex.intValue
                val needUpdateLastEditedColumnIndex = oldLinePos?.columnIndex != editedColumnIndex
                if((needUpdateLastEditedLineIndex || needUpdateFirstVisibleLineIndex || needUpdateLastEditedColumnIndex)) {
                    val pos = oldLinePos
                    if(needUpdateLastEditedLineIndex) {
                        pos.lineIndex = lastEditedLineIdx
                    }
                    if(needUpdateFirstVisibleLineIndex) {
                        pos.firstVisibleLineIndex = currentFirstVisibleIndex
                    }
                    if(needUpdateLastEditedColumnIndex) {
                        pos.columnIndex = editedColumnIndex
                    }
                    FileOpenHistoryMan.set(fileFullPath.ioPath, pos)
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#TextEditorOnDispose@ save last edit position err: "+e.stackTraceToString())
            }
        }
    }
    CompositionLocalProvider(
        LocalTextSelectionColors provides MyStyleKt.TextSelectionColor.customTextSelectionColors_cursorHandleVisible,
    ) {
        expectConflictStrDto.value.reset()
        val size = textEditorState.fields.size
        val lastIndexOfFields = size - 1
        val createItem: LazyListScope.(Int)->Unit = ci@{ index:Int ->
            val textFieldState = textEditorState.fields.getOrNull(index) ?: return@ci
            val curLineText = textFieldState.value.text
            val patchColor = if(patchMode) PatchUtil.getColorOfLine(curLineText, inDarkTheme) else null;
            val bgColor = if(patchMode && patchColor != null) {
                patchColor
            } else if(mergeMode) {
                UIHelper.getBackgroundColorForMergeConflictSplitText(
                    text = curLineText,
                    settings = settings,
                    expectConflictStrDto = expectConflictStrDto.value,
                    oursBgColor = conflictOursBlockBgColor,
                    theirsBgColor = conflictTheirsBlockBgColor,
                    startLineBgColor= conflictStartLineBgColor,
                    splitLineBgColor= conflictSplitLineBgColor,
                    endLineBgColor= conflictEndLineBgColor
                )
            } else {
                Color.Unspecified
            }
            item {
                if(mergeMode && curLineText.startsWith(settings.editor.conflictStartStr)) {
                    AcceptButtons(
                        lineIndex = index,
                        lineText = curLineText,
                        acceptOursColor = acceptOursBtnColor,
                        acceptTheirsColor = acceptTheirsBtnColor,
                        acceptBothColor = acceptBothBtnColor,
                        rejectBothColor = rejectBothBtnColor,
                        prepareAcceptBlock = prepareAcceptBlock,
                    )
                }
                decorationBox(
                    index,
                    size,
                    textEditorState.isFieldSelected(index),
                    textFieldState,
                    focusingLineIdx.value ?: -1,
                    textEditorState.isMultipleSelectionMode,
                ) { modifier ->
                    val textFieldState = textEditorState.obtainHighlightedTextField(textFieldState).let { remember(it) { it } }
                    Box(
                        modifier = Modifier
                            .background(bgColor)
                            .combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onLongClick = clickable@{
                                    if (!textEditorState.isMultipleSelectionMode) return@clickable
                                    textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectFieldSpan") { textEditorState ->
                                        textEditorState.selectFieldSpan(index)
                                    }
                                }
                            ) clickable@{
                                if (!textEditorState.isMultipleSelectionMode) return@clickable
                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                    textEditorState.selectField(targetIndex = index)
                                }
                            }
                            .then(modifier)
                    ) {
                        MyTextField(
                            scrollIfInvisible = {
                                scrollIfIndexInvisible(index)
                            },
                            readOnly = readOnlyMode,
                            focusThisLine = index == focusingLineIdx.value,
                            textFieldState = textFieldState,
                            enabled = !textEditorState.isMultipleSelectionMode,
                            fontSize = fontSize.intValue,
                            fontColor = fontColor,
                            onUpdateText = { newTextFieldValue ->
                                doJobThenOffLoading {
                                    textEditorState.codeEditor?.doActWithLatestEditorState("#onUpdateText") { textEditorState ->
                                        try{
                                            if(!textEditorState.isMultipleSelectionMode) {
                                                lastValidEditedColumnLineIndex.intValue = newTextFieldValue.selection.end
                                            }
                                            textEditorState.updateField(
                                                targetIndex = index,
                                                textFieldValue = newTextFieldValue,
                                            )
                                        }catch (e:IndexOutOfBoundsException) {
                                            MyLog.w(TAG, "#onUpdateText index out of bounds err, usually is ok, maybe the file changed after last save, so the line and column not match: "+e.localizedMessage)
                                        }catch (e:Exception) {
                                            Msg.requireShowLongDuration("#onUpdateText err: "+e.localizedMessage)
                                            MyLog.e(TAG, "#onUpdateText err: "+e.stackTraceToString())
                                        }
                                    }
                                }
                            },
                            onContainNewLine = cb@{ newTextFieldValue ->
                                doJobThenOffLoading {
                                    textEditorState.codeEditor?.doActWithLatestEditorState("#onContainNewLine") { textEditorState ->
                                        try {
                                            textEditorState.splitNewLine(
                                                targetIndex = index,
                                                textFieldValue = newTextFieldValue,
                                                updater = { newLinesRange, newFields, newSelectedIndices ->
                                                    doJobThenOffLoading {
                                                        delay(200)
                                                        scrollIfIndexInvisible(newLinesRange.endInclusive)
                                                    }
                                                }
                                            )
                                        }catch (e:Exception) {
                                            Msg.requireShowLongDuration("#onContainNewLine err: "+e.localizedMessage)
                                            MyLog.e(TAG, "#onContainNewLine err: "+e.stackTraceToString())
                                        }
                                    }
                                }
                            },
                            onFocus = { newTextFieldValue: TextFieldValue ->
                                doJobThenOffLoading {
                                    textEditorState.codeEditor?.doActWithLatestEditorState("#onFocus") { textEditorState ->
                                        if(!textEditorState.isMultipleSelectionMode) {
                                            lastValidEditedColumnLineIndex.intValue = newTextFieldValue.selection.end
                                        }
                                        try {
                                            textEditorState.selectFieldValue(
                                                targetIndex = index,
                                                textFieldValue = newTextFieldValue
                                            )
                                        }catch (e:Exception) {
                                            Msg.requireShowLongDuration("#onFocus err: "+e.localizedMessage)
                                            MyLog.e(TAG, "#onFocus err: "+e.stackTraceToString())
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
                if(mergeMode && curLineText.startsWith(settings.editor.conflictEndStr)) {
                    AcceptButtons(
                        lineIndex = index,
                        lineText = curLineText,
                        acceptOursColor = acceptOursBtnColor,
                        acceptTheirsColor = acceptTheirsBtnColor,
                        acceptBothColor = acceptBothBtnColor,
                        rejectBothColor = rejectBothBtnColor,
                        prepareAcceptBlock = prepareAcceptBlock,
                    )
                }
            }
            if(index == lastIndexOfFields) {
                item {
                    Spacer(modifier = Modifier
                        .width(virtualWidth)
                        .height(virtualHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                if (textEditorState.isMultipleSelectionMode.not()) {
                                    textEditorState.selectField(
                                        textEditorState.fields.lastIndex,
                                        SelectionOption.LAST_POSITION
                                    )
                                    keyboardController?.show()
                                }
                            }
                        }
                        ,
                    )
                }
            }
        }
        DisableSoftKeyboard(disableSoftKb.value) {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = contentPaddingValues
            ) {
                for(index in 0 until size) {
                    createItem(index)
                }
            }
        }
        LaunchedEffect(lastScrollEvent.value) TextEditorLaunchedEffect@{
            try {
                val lastScrollEvent = lastScrollEvent.value
                if(lastScrollEvent==null && !isInitDone.value) {
                    isInitDone.value = true
                    if(mergeMode) {
                        val fields = textEditorState.fields
                        for(idx in fields.indices) {
                            val value = fields.getOrNull(idx) ?: continue
                            if(value.value.text.startsWith(settings.editor.conflictStartStr)) {
                                UIHelper.scrollToItem(scope, listState, idx + lineNumOffsetForGoToEditor)
                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                    textEditorState.selectField(
                                        idx,
                                        option = SelectionOption.FIRST_POSITION,
                                    )
                                }
                                return@TextEditorLaunchedEffect
                            }
                        }
                    }
                    val useLastEditPos = LineNum.shouldRestoreLastPosition(goToLine)
                    var targetFocusLineIndexWhenGoToLine = 0
                    UIHelper.scrollToItem(
                        coroutineScope = scope,
                        listState = listState,
                        index = if(useLastEditPos) {
                            lastEditedPos.firstVisibleLineIndex
                        } else if(goToLine == LineNum.EOF.LINE_NUM) {
                            targetFocusLineIndexWhenGoToLine = textEditorState.fields.size - 1
                            targetFocusLineIndexWhenGoToLine + lineNumOffsetForGoToEditor
                        } else {
                            targetFocusLineIndexWhenGoToLine = goToLine - 1
                            targetFocusLineIndexWhenGoToLine + lineNumOffsetForGoToEditor
                        }
                    )
                    if(bug_Editor_GoToColumnCantHideKeyboard_Fixed && useLastEditPos && settings.editor.restoreLastEditColumn && !readOnlyMode) {
                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                            delay(300)
                            val lastEditedLineIdx = lastEditedPos.lineIndex
                            scrollIfIndexInvisible(lastEditedLineIdx)
                            textEditorState.selectField(
                                lastEditedLineIdx,
                                option = SelectionOption.CUSTOM,
                                columnStartIndexInclusive = lastEditedPos.columnIndex
                            )
                            requestFromParent.value = PageRequest.hideKeyboardForAWhile
                        }
                    }else {  
                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                            textEditorState.selectField(
                                targetFocusLineIndexWhenGoToLine,
                                option = SelectionOption.FIRST_POSITION,
                            )
                        }
                    }
                    return@TextEditorLaunchedEffect
                }else if(lastScrollEvent?.isConsumed == false) {  
                    lastScrollEvent?.consume()
                    val forceGo = lastScrollEvent?.forceGo == true
                    lastScrollEvent?.index?.let { index ->
                        if(forceGo) {
                            UIHelper.scrollToItem(scope, listState, index + lineNumOffsetForGoToEditor)
                            if(lastScrollEvent?.goColumn==true) {
                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                    textEditorState.selectField(
                                        targetIndex = index,
                                        option = SelectionOption.CUSTOM,
                                        columnStartIndexInclusive = lastScrollEvent!!.columnStartIndexInclusive,
                                        columnEndIndexExclusive = if (bug_Editor_SelectColumnRangeOfLine_Fixed) lastScrollEvent!!.columnEndIndexExclusive else lastScrollEvent!!.columnStartIndexInclusive,
                                        requireSelectLine = false,
                                    )
                                }
                                if(lastScrollEvent.requireHideKeyboard) {
                                    keyboardController?.hide()
                                }
                            }
                        }else {
                            scrollIfIndexInvisible(index)
                        }
                    }
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "TextEditor#LaunchedEffect err: "+e.stackTraceToString())
            }
        }
    }
}
fun getNextKeyWordForConflict(curKeyWord:String, settings: AppSettings):String {
    if(curKeyWord == settings.editor.conflictStartStr) {
        return settings.editor.conflictSplitStr
    }else if(curKeyWord == settings.editor.conflictSplitStr) {
        return settings.editor.conflictEndStr
    }else { 
        return settings.editor.conflictStartStr
    }
}
fun getPreviousKeyWordForConflict(curKeyWord:String, settings: AppSettings):String {
    if(curKeyWord == settings.editor.conflictStartStr) {
        return settings.editor.conflictEndStr
    }else if(curKeyWord == settings.editor.conflictEndStr) {
        return settings.editor.conflictSplitStr
    }else { 
        return settings.editor.conflictStartStr
    }
}
class ScrollEvent(
    val index: Int = -1,
    val forceGo:Boolean = false,
    val goColumn:Boolean=false,
    val columnStartIndexInclusive:Int=0,
    val columnEndIndexExclusive:Int=columnStartIndexInclusive,
    val requireHideKeyboard:Boolean = false
) {
    var isConsumed: Boolean = false
        private set
    fun consume() {
        isConsumed = true
    }
}
@Parcelize
data class SearchPos(var lineIndex:Int=-1, var columnIndex:Int=-1):Parcelable {
    companion object{
        val NotFound = SearchPos(-1, -1)
    }
}
data class SearchPosResult(val foundPos: SearchPos = SearchPos.NotFound, val nextPos: SearchPos = SearchPos.NotFound) {
    companion object{
        val NotFound = SearchPosResult(foundPos = SearchPos.NotFound, nextPos = SearchPos.NotFound)
    }
}
