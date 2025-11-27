package com.akcreation.gitsilent.fileeditor.ui.composable

import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_DPAD_UP
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.FormatIndentDecrease
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PanToolAlt
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.compose.BottomBar
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MarkDownContainer
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.SelectedItemDialog3
import com.akcreation.gitsilent.compose.SwipeIcon
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.fileeditor.texteditor.state.MyTextFieldState
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.fileeditor.texteditor.view.ScrollEvent
import com.akcreation.gitsilent.fileeditor.texteditor.view.TextEditor
import com.akcreation.gitsilent.fileeditor.texteditor.view.lineNumOffsetForGoToEditor
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.getClipboardText
import com.akcreation.gitsilent.screen.shared.EditorPreviewNavStack
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.settings.FileEditedPos
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.syntaxhighlight.base.PLFont
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.syntaxhighlight.base.PLTheme
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.addTopPaddingIfIsFirstLine
import com.akcreation.gitsilent.utils.appendCutSuffix
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.paddingLineNumber
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

private const val TAG = "FileEditor"
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun FileEditor(
    stateKeyTag:String,
    plScope: State<PLScope>,
    disableSoftKb: MutableState<Boolean>,
    updateLastCursorAtColumn:(Int)->Unit,
    getLastCursorAtColumnValue:()->Int,
    ignoreFocusOnce: MutableState<Boolean>,
    requireEditorScrollToPreviewCurPos:MutableState<Boolean>,
    requirePreviewScrollToEditorCurPos:MutableState<Boolean>,
    isSubPageMode:Boolean,
    previewNavBack:()->Unit,
    previewNavAhead:()->Unit,
    previewNavStack:CustomStateSaveable<EditorPreviewNavStack>,
    refreshPreviewPage:()->Unit,
    previewLoading:Boolean,
    mdText:MutableState<String>,
    basePath:MutableState<String>,
    previewLinkHandler:(link:String)->Boolean,
    isPreviewModeOn:MutableState<Boolean>,
    quitPreviewMode:()->Unit,
    initPreviewMode:()->Unit,
    openDrawer:()->Unit,
    requestFromParent:MutableState<String>,
    fileFullPath:FilePath,
    lastEditedPos:FileEditedPos,
    textEditorState:CustomStateSaveable<TextEditorState>,
    contentPadding:PaddingValues,
    isContentEdited:MutableState<Boolean>,
    editorLastScrollEvent:CustomStateSaveable<ScrollEvent?>,
    editorListState: LazyListState,
    editorPageIsInitDone:MutableState<Boolean>,
    editorPageIsContentSnapshoted:MutableState<Boolean>,
    goToLine:Int,
    readOnlyMode:Boolean,
    searchMode:MutableState<Boolean>,
    searchKeyword:String,
    mergeMode:Boolean,
    patchMode:Boolean,
    showLineNum:MutableState<Boolean>,
    lineNumFontSize:MutableIntState,
    fontSize:MutableIntState,
    undoStack: UndoStack,
    tabIndentSpacesCount: State<Int>,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val deviceConfiguration = AppModel.getCurActivityConfig()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val scope = rememberCoroutineScope()
    val inDarkTheme = Theme.inDarkTheme
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val firstLineTopPaddingInPx = with(density) { -(MyStyleKt.Padding.firstLineTopPaddingValuesInDp.toPx()) }
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val contentPaddingValues = contentPadding
    val enableSelectMode = { index:Int ->
        keyboardController?.hide()
        textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#createMultipleSelectionModeState") { textEditorState ->
            textEditorState.createMultipleSelectionModeState(index)
        }
    }
    if(requestFromParent.value == PageRequest.editorSwitchSelectMode) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            if(textEditorState.value.isMultipleSelectionMode) {  
                textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#quitSelectionMode") { textEditorState ->
                    textEditorState.quitSelectionMode()
                }
            }else {  
                enableSelectMode(-1)
            }
        }
    }
    val deleteSelectedLines = {
        textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#deleteSelectedLines") { textEditorState ->
            textEditorState.deleteSelectedLines()
        }
    }
    if(showDeleteDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.delete_lines),
            text = replaceStringResList(stringResource(R.string.will_delete_n_lines_ask), listOf(textEditorState.value.getSelectedCount().toString())) ,
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDeleteDialog.value=false }
        ) {
            showDeleteDialog.value=false
            deleteSelectedLines()
            Msg.requireShow(activityContext.getString(R.string.deleted))
        }
    }
    val onLeftToRight = {
        if(isPreviewModeOn.value) {
            previewNavBack()
        } else if(isSubPageMode.not()) {
            openDrawer()
        }
    }
    val onRightToLeft = {
        if(isPreviewModeOn.value) {
            previewNavAhead()
        } else {
            initPreviewMode()
        }
    }
    val swipeIconModifier = Modifier.padding(top = contentPadding.calculateTopPadding(), bottom = contentPadding.calculateBottomPadding()).padding(horizontal = 10.dp);
    val leftToRightAct = SwipeAction(
        icon = {
            if(isPreviewModeOn.value) {
                SwipeIcon(
                    modifier = swipeIconModifier,
                    imageVector = runBlocking {
                        if (previewNavStack.value.backStackIsEmpty()) Icons.Filled.Edit else Icons.AutoMirrored.Filled.ArrowBackIos
                    },
                    contentDescription = null
                )
            }
        },
        background = Color.Unspecified,
        enableAnimation = isPreviewModeOn.value,
        enableAct = isSubPageMode.not() || isPreviewModeOn.value,
        enableVibration = isPreviewModeOn.value,  
        onSwipe = { onLeftToRight() }
    )
    val supportSwipeToEnablePreview = isPreviewModeOn.value.not() && PLScope.isSupportPreview(plScope.value)
    val enableRightToLeftAct = supportSwipeToEnablePreview || (isPreviewModeOn.value && runBlocking { previewNavStack.value.aheadStackIsNotEmpty() })
    val rightToLeftAct = SwipeAction(
        icon = {
            if(enableRightToLeftAct) {
                SwipeIcon(
                    modifier = swipeIconModifier,
                    imageVector = if(isPreviewModeOn.value) Icons.AutoMirrored.Filled.ArrowForwardIos else Icons.Filled.RemoveRedEye,
                    contentDescription = null
                )
            }
        },
        background = Color.Unspecified,
        enableAnimation = supportSwipeToEnablePreview || isPreviewModeOn.value,
        enableAct = enableRightToLeftAct,
        onSwipe = { onRightToLeft() },
    )
    val fontColor = remember(inDarkTheme) { UIHelper.getFontColor(inDarkTheme) }
    SwipeableActionsBox(
        startActions = listOf(leftToRightAct),
        endActions = listOf(rightToLeftAct),
    ) {
        val curPreviewScrollState = runBlocking { previewNavStack.value.getCurrentScrollState() }
        val scrollIfIndexInvisible = { index:Int ->
            try {
                val first = editorListState.firstVisibleItemIndex
                val end = editorListState.layoutInfo.visibleItemsInfo.maxByOrNull { it.index }?.index
                if (end != null && (index < first || index > end)) {
                    UIHelper.scrollToItem(scope, editorListState, index + lineNumOffsetForGoToEditor)
                }
            }catch (e: Exception) {
                MyLog.d(TAG, "#scrollIfIndexInvisible err: ${e.stackTraceToString()}")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if(isPreviewModeOn.value) {
                        Modifier
                    }else {
                        Modifier
                            .background(PLTheme.getBackground(inDarkTheme))
                            .onPreviewKeyEvent opke@{ keyEvent ->
                                if (keyEvent.type != KeyEventType.KeyDown) {
                                    return@opke false
                                }
                                val textEditorState = textEditorState.value
                                val lastScrollEvent = editorLastScrollEvent
                                if(readOnlyMode.not() && textEditorState.isMultipleSelectionMode.not()) {
                                    val (focusedLineIndex, textFieldState) = textEditorState.getCurrentField()
                                    if(focusedLineIndex != null && textFieldState != null) {
                                        val textFieldValue = textFieldState.value
                                        val selection = textFieldValue.selection
                                        val index = focusedLineIndex
                                        val event = keyEvent
                                        if (backspacePressed(event, selection) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#onBackspacePressed") { textEditorState ->
                                                    try {
                                                        textEditorState.deleteNewLine(targetIndex = index)
                                                        scrollIfIndexInvisible(index - 1)
                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onDeleteNewLine err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onDeleteNewLine err: "+e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }
                                        if (forwardDeletePressed(event, selection, textFieldValue) {
                                                val index = index + 1
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#onForwardDeletePressed") { textEditorState ->
                                                    try {
                                                        textEditorState.deleteNewLine(targetIndex = index)
                                                        scrollIfIndexInvisible(index - 1)
                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("forward delete err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onDeleteNewLine err: forward delete err: "+e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }
                                        if (goUpKeyPressed(event) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectPrevOrNextField") { textEditorState ->
                                                    try {
                                                        textEditorState.selectPrevOrNextField(
                                                            isNext = false,
                                                            updateLastCursorAtColumn,
                                                            getLastCursorAtColumnValue,
                                                        )
                                                        scrollIfIndexInvisible(index - 1)
                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onUpFocus err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onUpFocus err: "+e.stackTraceToString())
                                                    }
                                                }
                                            }) {
                                            return@opke true
                                        }
                                        if (goDownKeyPressed(event) {
                                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectPrevOrNextField") { textEditorState ->
                                                try {
                                                        textEditorState.selectPrevOrNextField(
                                                            isNext = true,
                                                            updateLastCursorAtColumn,
                                                            getLastCursorAtColumnValue,
                                                        )
                                                        scrollIfIndexInvisible(index + 1)
                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onDownFocus err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onDownFocus err: "+e.stackTraceToString())
                                                    }
                                                }
                                            }) {
                                            return@opke true
                                        }
                                        if (goLeftPressed(event, textFieldState) { lineSwitched ->
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#moveCursor") { textEditorState ->
                                                    try {
                                                        textEditorState.moveCursor(
                                                            trueToLeftFalseRight = true,
                                                            textFieldState = textFieldState,
                                                            targetFieldIndex = index,
                                                            headOrTail = false,
                                                        )
                                                        scrollIfIndexInvisible(if(lineSwitched) index - 1 else index)
                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onLeftPressed err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onLeftPressed err: "+e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }
                                        if (goRightPressed(event, textFieldState) { lineSwitched ->
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#moveCursor") { textEditorState ->
                                                    try {
                                                        textEditorState.moveCursor(
                                                            trueToLeftFalseRight = false,
                                                            textFieldState = textFieldState,
                                                            targetFieldIndex = index,
                                                            headOrTail = false,
                                                        )
                                                        scrollIfIndexInvisible(if(lineSwitched) index + 1 else index)
                                                    } catch (e: Exception) {
                                                        Msg.requireShowLongDuration("#onRightPressed err: " + e.localizedMessage)
                                                        MyLog.e(TAG, "#onRightPressed err: " + e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }
                                        if (goHomePressed(event, textFieldState) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#moveCursor") { textEditorState ->
                                                    try {
                                                        textEditorState.moveCursor(
                                                            trueToLeftFalseRight = true,
                                                            textFieldState = textFieldState,
                                                            targetFieldIndex = index,
                                                            headOrTail = true,
                                                        )
                                                        scrollIfIndexInvisible(index)
                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onLeftPressed err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onLeftPressed err: "+e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }
                                        if (goEndPressed(event, textFieldState) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#moveCursor") { textEditorState ->
                                                    try {
                                                        textEditorState.moveCursor(
                                                            trueToLeftFalseRight = false,
                                                            textFieldState = textFieldState,
                                                            targetFieldIndex = index,
                                                            headOrTail = true,
                                                        )
                                                        scrollIfIndexInvisible(index)
                                                    } catch (e: Exception) {
                                                        Msg.requireShowLongDuration("#onRightPressed err: " + e.localizedMessage)
                                                        MyLog.e(TAG, "#onRightPressed err: " + e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }
                                    }
                                }
                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.S) { 
                                    requestFromParent.value = PageRequest.requireSave
                                    return@opke true
                                }
                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.W) { 
                                    requestFromParent.value = PageRequest.requireClose
                                    return@opke true
                                }
                                if ((keyEvent.isCtrlPressed && keyEvent.key == Key.Y)
                                    || (keyEvent.isCtrlPressed && keyEvent.isShiftPressed && keyEvent.key == Key.Z)
                                ) { 
                                    requestFromParent.value = PageRequest.requestRedo
                                    return@opke true
                                }
                                if (keyEvent.isShiftPressed.not() && keyEvent.isCtrlPressed && keyEvent.key == Key.Z) { 
                                    requestFromParent.value = PageRequest.requestUndo
                                    return@opke true
                                }
                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.F) { 
                                    requestFromParent.value = PageRequest.requireSearch  
                                    return@opke true
                                }
                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.G) {
                                    requestFromParent.value = PageRequest.goToLine
                                    return@opke true
                                }
                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.MoveHome) { 
                                    lastScrollEvent.value = ScrollEvent(0)
                                    textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#goToEndOrTopOfFile") { textEditorState ->
                                        textEditorState.goToEndOrTopOfFile(goToTop = true)
                                    }
                                    return@opke true
                                }
                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.MoveEnd) { 
                                    lastScrollEvent.value = ScrollEvent(textEditorState.fields.lastIndex.coerceAtLeast(0))
                                    textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#goToEndOrTopOfFile") { textEditorState ->
                                        textEditorState.goToEndOrTopOfFile(goToTop = false)
                                    }
                                    return@opke true
                                }
                                val isCtrlAndC = keyEvent.isCtrlPressed && keyEvent.key == Key.C
                                val isCtrlAndX = keyEvent.isCtrlPressed && keyEvent.key == Key.X
                                if(isCtrlAndC || isCtrlAndX) {
                                    if(textEditorState.isMultipleSelectionMode) {
                                        clipboardManager.setText(AnnotatedString(textEditorState.getSelectedText()))
                                        Msg.requireShow(
                                            replaceStringResList(
                                                activityContext.getString(R.string.n_lines_copied),
                                                listOf(textEditorState.getSelectedCount().toString())
                                            ).let { if(isCtrlAndX) it.appendCutSuffix() else it }
                                        )
                                        if(isCtrlAndX) {
                                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#deleteSelectedLines") { textEditorState ->
                                                textEditorState.deleteSelectedLines()
                                            }
                                        }
                                        return@opke true
                                    }else {
                                        val (currentIndex, currentField) = textEditorState.getCurrentField()
                                        if(currentIndex != null && currentField != null && currentField.value.selection.collapsed) {
                                            clipboardManager.setText(AnnotatedString(currentField.value.text))
                                            if(isCtrlAndX) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#deleteLineByIndices") { textEditorState ->
                                                    textEditorState.deleteLineByIndices(listOf(currentIndex))
                                                }
                                            }
                                            return@opke true
                                        }
                                    }
                                }
                                if(textEditorState.isMultipleSelectionMode && keyEvent.isCtrlPressed && keyEvent.key == Key.A) {
                                    textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#createSelectAllState") { textEditorState ->
                                        textEditorState.createSelectAllState()
                                    }
                                    return@opke true
                                }
                                if(textEditorState.isMultipleSelectionMode && textEditorState.selectedIndices.isNotEmpty() && keyEvent.isCtrlPressed && keyEvent.key == Key.V) {
                                    val clipboardText = getClipboardText(clipboardManager)
                                    if(clipboardText == null) {
                                        Msg.requireShowLongDuration(activityContext.getString(R.string.clipboard_is_empty))
                                    }else {
                                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#appendTextToLastSelectedLine") { textEditorState ->
                                            textEditorState.appendTextToLastSelectedLine(clipboardText)
                                        }
                                    }
                                    return@opke true
                                }
                                if(keyEvent.key == Key.Tab && keyEvent.isShiftPressed) {
                                    if(textEditorState.isMultipleSelectionMode) {
                                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#indentLines") { textEditorState ->
                                            textEditorState.let { it.indentLines(tabIndentSpacesCount.value, it.selectedIndices, trueTabFalseShiftTab = false) }
                                        }
                                        return@opke true
                                    }else {
                                        val (idx, f) = textEditorState.getCurrentField()
                                        if(idx != null && f != null) {
                                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#handleTabIndent") { textEditorState ->
                                                textEditorState.handleTabIndent(idx, f, tabIndentSpacesCount.value, trueTabFalseShiftTab = false)
                                            }
                                            return@opke true
                                        }
                                    }
                                }
                                if(keyEvent.key == Key.Tab && !keyEvent.isShiftPressed) {
                                    if(textEditorState.isMultipleSelectionMode) {
                                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#indentLines") { textEditorState ->
                                            textEditorState.let { it.indentLines(tabIndentSpacesCount.value, it.selectedIndices, trueTabFalseShiftTab = true) }
                                        }
                                        return@opke true
                                    }else {
                                        val (idx, f) = textEditorState.getCurrentField()
                                        if(idx != null && f != null) {
                                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#handleTabIndent") { textEditorState ->
                                                textEditorState.handleTabIndent(idx, f, tabIndentSpacesCount.value, trueTabFalseShiftTab = true)
                                            }
                                            return@opke true
                                        }
                                    }
                                }
                                if(keyEvent.key == Key.F3 && !keyEvent.isShiftPressed) {
                                    requestFromParent.value = if(searchMode.value) {
                                        PageRequest.findNext
                                    }else {
                                        PageRequest.requireSearch
                                    }
                                    return@opke true
                                }
                                if(keyEvent.key == Key.F3 && keyEvent.isShiftPressed) {
                                    requestFromParent.value = PageRequest.findPrevious
                                    return@opke true
                                }
                                return@opke false
                            }
                    }
                )
        ) {
            if(isPreviewModeOn.value) {
                PullToRefreshBox(
                    contentPadding = contentPadding,
                    onRefresh = { refreshPreviewPage() }
                ) {
                    Column(
                        modifier = Modifier
                            .baseVerticalScrollablePageModifier(contentPadding, curPreviewScrollState)
                        ,
                    ) {
                        Spacer(Modifier.addTopPaddingIfIsFirstLine(0))
                        MarkDownContainer(
                            content = mdText.value,
                            modifier = Modifier.padding(horizontal = 10.dp),
                            basePathNoEndSlash = basePath.value,
                            style = LocalTextStyle.current.copy(fontSize = fontSize.intValue.sp, color = fontColor, fontFamily = PLFont.editorCodeFont()),
                            onLinkClicked = { link ->
                                previewLinkHandler(link)
                            }
                        )
                        Spacer(Modifier.height(30.dp))
                    }
                }
                LaunchedEffect(Unit) {
                    try {
                        if(requirePreviewScrollToEditorCurPos.value) {
                            requirePreviewScrollToEditorCurPos.value = false
                            val fontSizeInPx = UIHelper.spToPx(sp = fontSize.intValue, density = density)
                            val screenWidthInPx = UIHelper.dpToPx(dp = deviceConfiguration.screenWidthDp, density = density)
                            val screenHeightInPx = UIHelper.dpToPx(dp = deviceConfiguration.screenHeightDp, density = density)
                            val editorCurLineIndex = editorListState.firstVisibleItemIndex
                            val targetPos = textEditorState.value.lineIdxToPx(lineIndex=editorCurLineIndex, fontSizeInPx=fontSizeInPx, screenWidthInPx=screenWidthInPx, screenHeightInPx=screenHeightInPx)
                            UIHelper.scrollTo(scope, curPreviewScrollState, targetPos.toInt())
                        }
                    }catch (e:Exception) {
                        MyLog.d(TAG, "let preview scroll to current editor position failed: ${e.stackTraceToString()}")
                    }
                }
            } else {
                val kbVisible = rememberUpdatedState(UIHelper.isSoftkeyboardVisible())
                DisposableEffect(Unit) {
                    onDispose {
                        if(AppModel.devModeOn) {
                            MyLog.d(TAG, "FileEditor#DisposableEffect#onDispose: called, imeVisible=${kbVisible.value}")
                        }
                        val kbHidden = !kbVisible.value
                        ignoreFocusOnce.value = kbHidden
                        if(kbHidden && !disableSoftKb.value) {
                            textEditorState.value = textEditorState.value.copy(focusingLineIdx = null)
                        }
                        if(AppModel.devModeOn) {
                            MyLog.d(TAG, "FileEditor#DisposableEffect#onDispose: called, ignoreFocusOnce=${ignoreFocusOnce.value}")
                        }
                    }
                }
                val showLineNum = showLineNum.value
                val changeTypeWidth = remember(showLineNum) { if(showLineNum) 5.dp else 10.dp }
                TextEditor(
                    stateKeyTag = stateKeyTag,
                    disableSoftKb = disableSoftKb,
                    updateLastCursorAtColumn = updateLastCursorAtColumn,
                    getLastCursorAtColumnValue = getLastCursorAtColumnValue,
                    ignoreFocusOnce = ignoreFocusOnce,
                    undoStack = undoStack,
                    curPreviewScrollState = curPreviewScrollState,
                    requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
                    requestFromParent = requestFromParent,
                    fileFullPath = fileFullPath,
                    lastEditedPos = lastEditedPos,
                    textEditorState = textEditorState.value,
                    contentPaddingValues = contentPaddingValues,
                    lastScrollEvent =editorLastScrollEvent,
                    listState =editorListState,
                    editorPageIsInitDone = editorPageIsInitDone,
                    goToLine=goToLine,
                    readOnlyMode=readOnlyMode,
                    searchMode = searchMode,
                    searchKeyword =searchKeyword,
                    mergeMode=mergeMode,
                    patchMode=patchMode,
                    fontSize=fontSize,
                    fontColor = fontColor,
                    scrollIfIndexInvisible = scrollIfIndexInvisible,
                ) { index, size, isSelected, currentField, focusingIdx, isMultiSelectionMode, innerTextField ->
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Max)
                            .fillMaxWidth()
                            .background(
                                getBackgroundColor(
                                    isSelected = isSelected,
                                    isMultiSelectionMode = isMultiSelectionMode,
                                    currentIdx = index,
                                    focusingIdx = focusingIdx,
                                    inDarkTheme = inDarkTheme,
                                )
                            )
                            .padding(end = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(MyStyleKt.TextColor.lineNumBgColor(inDarkTheme))
                                .fillMaxHeight()
                                .combinedClickable(
                                    onLongClick = {
                                        if (textEditorState.value.isMultipleSelectionMode) {
                                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectFieldSpan") { textEditorState ->
                                                textEditorState.selectFieldSpan(targetIndex = index)
                                            }
                                        }
                                    }
                                ) {
                                    if (textEditorState.value.isMultipleSelectionMode) {
                                        textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                            textEditorState.selectField(targetIndex = index)
                                        }
                                    } else { 
                                        enableSelectMode(index)
                                    }
                                }
                                .addTopPaddingIfIsFirstLine(index)
                            ,
                            horizontalArrangement = Arrangement.End,
                        ) {
                            if(showLineNum) {
                                val expectLength = size.toString().length
                                Text(
                                    text = paddingLineNumber((index+1).toString(), expectLength),
                                    color = MyStyleKt.TextColor.lineNumColor(inDarkTheme, textEditorState.value.focusingLineIdx == index),
                                    fontSize = lineNumFontSize.intValue.sp,
                                    fontFamily = PLFont.codeFont,  
                                    modifier = Modifier.padding(start = 5.dp, end = changeTypeWidth)
                                )
                            }
                            Row(modifier = Modifier
                                .width(changeTypeWidth)
                                .fillMaxHeight()
                                .changeTypeIndicator(
                                    changeTypeLineWidth = changeTypeWidth,
                                    changeTypeColor = currentField.getColorOfChangeType(inDarkTheme),
                                    yStartOffsetInPx = if(index == 0) firstLineTopPaddingInPx else 0f
                                )
                            ){
                            }
                        }
                        innerTextField(
                            Modifier
                                .weight(0.9f, true) 
                                .align(Alignment.CenterVertically)
                                .addTopPaddingIfIsFirstLine(index)
                        )
                    }
                }
                if (textEditorState.value.isMultipleSelectionMode) {
                    val quitSelectionMode = {
                        textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#quitSelectionMode") { textEditorState ->
                            textEditorState.quitSelectionMode()
                        }
                        Unit
                    }
                    val iconList = listOf(
                        Icons.AutoMirrored.Filled.FormatIndentDecrease,  
                        Icons.AutoMirrored.Filled.FormatIndentIncrease,  
                        Icons.Filled.Delete,
                        Icons.Filled.ContentCut,
                        Icons.Filled.ContentCopy,
                        Icons.Filled.ContentPaste,
                        Icons.Filled.CleaningServices,  
                        Icons.AutoMirrored.Filled.KeyboardReturn,  
                        Icons.Filled.SelectAll
                    )
                    val iconTextList = listOf(
                        "Shift + Tab",
                        "Tab",
                        stringResource(R.string.delete),
                        stringResource(R.string.cut),
                        stringResource(R.string.copy),
                        stringResource(R.string.paste),
                        stringResource(R.string.clear),
                        stringResource(R.string.append_a_line),
                        stringResource(R.string.select_all),
                    )
                    val iconOnClickList = listOf(
                        onShiftTab@{
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#indentLines") { textEditorState ->
                                textEditorState.let { it.indentLines(tabIndentSpacesCount.value, it.selectedIndices, trueTabFalseShiftTab = false) }
                            }
                            Unit
                        },
                        onTab@{
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#indentLines") { textEditorState ->
                                textEditorState.let { it.indentLines(tabIndentSpacesCount.value, it.selectedIndices, trueTabFalseShiftTab = true) }
                            }
                            Unit
                        },
                        onDelete@{
                            if (readOnlyMode) {
                                Msg.requireShow(activityContext.getString(R.string.readonly_cant_edit))
                                return@onDelete
                            }
                            val selectedLinesNum = textEditorState.value.getSelectedCount();
                            if (selectedLinesNum < 1) {
                                Msg.requireShow(activityContext.getString(R.string.no_line_selected))
                                return@onDelete
                            }
                            showDeleteDialog.value = true
                        },
                        onCut@{
                            val selectedLinesNum = textEditorState.value.getSelectedCount();
                            if (selectedLinesNum < 1) {
                                Msg.requireShow(activityContext.getString(R.string.no_line_selected))
                                return@onCut
                            }
                            clipboardManager.setText(AnnotatedString(textEditorState.value.getSelectedText()))
                            Msg.requireShow(replaceStringResList(activityContext.getString(R.string.n_lines_copied), listOf(selectedLinesNum.toString())).appendCutSuffix())
                            deleteSelectedLines()
                        },
                        onCopy@{
                            val selectedLinesNum = textEditorState.value.getSelectedCount();
                            if (selectedLinesNum < 1) {
                                Msg.requireShow(activityContext.getString(R.string.no_line_selected))
                                return@onCopy
                            }
                            clipboardManager.setText(AnnotatedString(textEditorState.value.getSelectedText()))
                            Msg.requireShow(replaceStringResList(activityContext.getString(R.string.n_lines_copied), listOf(selectedLinesNum.toString())))
                        },
                        onPaste@{
                            if (readOnlyMode) {
                                Msg.requireShow(activityContext.getString(R.string.readonly_cant_edit))
                                return@onPaste
                            }
                            val clipboardText = getClipboardText(clipboardManager)
                            if(clipboardText == null) {
                                Msg.requireShowLongDuration(activityContext.getString(R.string.clipboard_is_empty))
                                return@onPaste
                            }
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#paste") { textEditorState ->
                                textEditorState.paste(
                                    text = clipboardText,
                                    afterReplacedAllThenDoAct = { newFields ->
                                        val scrollTarget = newFields.lastIndex
                                        doJobThenOffLoading {
                                            delay(200)
                                            editorLastScrollEvent.value = ScrollEvent(scrollTarget)
                                        }
                                    }
                                )
                            }
                            Unit
                        },
                        onClear@{
                            if (readOnlyMode) {
                                Msg.requireShow(activityContext.getString(R.string.readonly_cant_edit))
                                return@onClear
                            }
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#clearSelectedFields") { textEditorState ->
                                textEditorState.clearSelectedFields()
                            }
                            Unit
                        },
                        onAppendALine@{
                            if (readOnlyMode) {
                                Msg.requireShow(activityContext.getString(R.string.readonly_cant_edit))
                                return@onAppendALine
                            }
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#appendTextToLastSelectedLine") { textEditorState ->
                                textEditorState.appendTextToLastSelectedLine(
                                        text = "",
                                        afterAppendThenDoAct = { targetIndex ->
                                            doJobThenOffLoading {
                                                delay(100)
                                                scrollIfIndexInvisible(targetIndex)
                                            }
                                        }
                                    )
                                }
                                Unit
                        },
                        onSelectAll@{
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#createSelectAllState") { textEditorState ->
                                textEditorState.createSelectAllState()
                            }
                            Unit
                        }
                    )
                    val selectedLines = textEditorState.value.getSelectedCount()
                    val hasLineSelected = selectedLines > 0
                    val hasLineSelectedAndNotReadOnly = hasLineSelected && readOnlyMode.not()
                    val iconEnableList = listOf(
                        onShiftTab@{ hasLineSelectedAndNotReadOnly },  
                        onTab@{ hasLineSelectedAndNotReadOnly },  
                        onDelete@{ hasLineSelectedAndNotReadOnly },  
                        onCut@{ hasLineSelectedAndNotReadOnly },  
                        onCopy@{ hasLineSelected },  
                        onPaste@{ hasLineSelectedAndNotReadOnly },  
                        onClear@{ hasLineSelectedAndNotReadOnly },  
                        onAppendALine@{ hasLineSelectedAndNotReadOnly },  
                        onSelectAll@{ true },  
                    )
                    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false) }
                    if(showSelectedItemsShortDetailsDialog.value) {
                        val trailingIconSize = MyStyleKt.defaultIconSize
                        val trailIconsWidth = trailingIconSize * 2
                        val closeDialog = { showSelectedItemsShortDetailsDialog.value = false }
                        SelectedItemDialog3(
                            selectedItems = textEditorState.value.selectedIndices,
                            textPadding = PaddingValues(start = 5.dp, end = trailIconsWidth),
                            text = {
                                Text(text = "${it+1}: ${textEditorState.value.getContentOfLineIndex(it)}", softWrap = false, overflow = TextOverflow.Ellipsis)
                            },
                            textFormatterForCopy = { textEditorState.value.getContentOfLineIndex(it) },
                            clearAll = {
                                textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#clearSelectedItemList") { textEditorState ->
                                    textEditorState.clearSelectedItemList()
                                }
                            },
                            closeDialog = closeDialog,
                            customTrailIcon = {
                                Row(
                                    modifier = Modifier.size(height = trailingIconSize, width = trailIconsWidth).align(Alignment.CenterEnd),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    LongPressAbleIconBtn(
                                        tooltipText = "",
                                        icon = Icons.Filled.PanToolAlt,
                                        iconContentDesc = "go to line button: click to go to line, long click to select span",
                                        onClick = {
                                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                                textEditorState.selectField(targetIndex = it, forceAdd = true)
                                                scrollIfIndexInvisible(it)
                                                closeDialog()
                                            }
                                        },
                                        onLongClick = {
                                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectFieldSpan") { textEditorState ->
                                                textEditorState.selectFieldSpan(targetIndex = it)
                                                scrollIfIndexInvisible(it)
                                                closeDialog()
                                            }
                                        }
                                    )
                                    IconButton(
                                        onClick = {
                                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                                textEditorState.selectField(targetIndex = it)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeleteOutline,
                                            contentDescription = stringResource(R.string.trash_bin_icon_for_delete_item)
                                        )
                                    }
                                }
                            }
                        )
                    }
                    BottomBar(
                        quitSelectionMode=quitSelectionMode,
                        iconList=iconList,
                        iconTextList=iconTextList,
                        iconDescTextList=iconTextList,
                        iconOnClickList=iconOnClickList,
                        iconEnableList=iconEnableList,
                        moreItemTextList= listOf(),
                        moreItemOnClickList= listOf(),
                        moreItemEnableList = listOf(),
                        getSelectedFilesCount = {selectedLines},
                        countNumOnClickEnabled = true,
                        countNumOnClick = {showSelectedItemsShortDetailsDialog.value = true}
                    )
                }
            }
        }
    }
}
@Composable
private fun getBackgroundColor(
    isSelected: Boolean,
    isMultiSelectionMode:Boolean,
    currentIdx:Int,
    focusingIdx:Int,
    inDarkTheme:Boolean
): Color {
    return if (isMultiSelectionMode && isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    }else if(isMultiSelectionMode.not() && currentIdx == focusingIdx) {
        MyStyleKt.LastClickedItem.getEditorLastClickedLineBgColor(inDarkTheme)
    } else {
        Color.Unspecified
    }
}
@Composable
private fun Modifier.bottomLine(
    bottomLineWidth: Dp,
    color: Color
) :Modifier {
    val density = LocalDensity.current
    val bottomLineWidthPx = with(density) { bottomLineWidth.toPx() }
    return drawBehind {
        val width = size.width
        val height = size.height
        val bottomLineHeight = height - bottomLineWidthPx / 2
        drawLine(
            color = color,
            start = Offset(x = 0f, y = bottomLineHeight),
            end = Offset(x = width, y = bottomLineHeight),
            strokeWidth = bottomLineWidthPx
        )
    }
}
@Composable
private fun Modifier.changeTypeIndicator(
    changeTypeLineWidth:Dp,
    changeTypeColor: Color,
    yStartOffsetInPx:Float,
) :Modifier {
    val isRtl = UIHelper.isRtlLayout()
    return drawBehind {
        val width = size.width
        val height = size.height
        val startX = if(isRtl) width else 0f
        drawLine(
            color = changeTypeColor,
            strokeWidth = changeTypeLineWidth.toPx(),  
            start = Offset(startX, yStartOffsetInPx),
            end = Offset(startX, height),
        )
    }
}
private fun goHomePressed(event: KeyEvent, textFieldState: MyTextFieldState, invoke: () -> Unit): Boolean {
    return onPreviewHomeOrEndKeyEvent(event, textFieldState, trueHomeFalseEnd = true, invoke)
}
private fun goEndPressed(event: KeyEvent, textFieldState: MyTextFieldState, invoke: () -> Unit): Boolean {
    return onPreviewHomeOrEndKeyEvent(event, textFieldState, trueHomeFalseEnd = false, invoke)
}
private fun goLeftPressed(event: KeyEvent, textFieldState: MyTextFieldState, invoke: (lineSwitched: Boolean) -> Unit): Boolean {
    return onPreviewLeftOrRightKeyEvent(event, textFieldState, trueLeftFalseRight = true, invoke)
}
private fun goRightPressed(event: KeyEvent, textFieldState: MyTextFieldState, invoke: (lineSwitched: Boolean) -> Unit): Boolean {
    return onPreviewLeftOrRightKeyEvent(event, textFieldState, trueLeftFalseRight = false, invoke)
}
private fun goUpKeyPressed(event: KeyEvent, invoke: () -> Unit): Boolean {
    return onPreviewUpOrDownKeyEvent(event, trueUpFalseDown = true, invoke)
}
private fun goDownKeyPressed(event: KeyEvent, invoke: () -> Unit): Boolean {
    return onPreviewUpOrDownKeyEvent(event, trueUpFalseDown = false, invoke)
}
private fun backspacePressed(
    event: KeyEvent,
    selection: TextRange,
    invoke: () -> Unit
): Boolean {
    if (event.key != Key.Backspace) return false
    if (selection != TextRange.Zero) return false
    invoke()
    return true
}
private fun forwardDeletePressed(
    event: KeyEvent,
    selection: TextRange,
    field: TextFieldValue,
    invoke: () -> Unit
): Boolean {
    if (event.key != Key.Delete || selection.collapsed.not() || selection.start != field.text.length) {
        return false
    }
    invoke()
    return true
}
private fun onPreviewUpOrDownKeyEvent(
    event: KeyEvent,
    trueUpFalseDown: Boolean,
    invoke: () -> Unit,
): Boolean {
    if(event.isCtrlPressed || event.isShiftPressed || event.isAltPressed || event.isMetaPressed) return false
    val expectedKey = if (trueUpFalseDown) KEYCODE_DPAD_UP else KEYCODE_DPAD_DOWN
    if (event.nativeKeyEvent.keyCode != expectedKey) return false
    invoke()
    return true
}
private fun onPreviewLeftOrRightKeyEvent(
    event: KeyEvent,
    field: MyTextFieldState,
    trueLeftFalseRight: Boolean,
    invoke: (lineSwitched: Boolean) -> Unit,
): Boolean {
    if(event.isCtrlPressed || event.isShiftPressed || event.isAltPressed || event.isMetaPressed || field.value.selection.collapsed.not()) return false
    val expectedKey = if(trueLeftFalseRight) KEYCODE_DPAD_LEFT else KEYCODE_DPAD_RIGHT
    if (event.nativeKeyEvent.keyCode != expectedKey) return false
    invoke((trueLeftFalseRight && field.value.selection == TextRange.Zero) || (trueLeftFalseRight.not() && field.value.selection.start == field.value.text.length))
    return true
}
private fun onPreviewHomeOrEndKeyEvent(
    event: KeyEvent,
    field: MyTextFieldState,
    trueHomeFalseEnd: Boolean,
    invoke: () -> Unit,
): Boolean {
    if(event.isCtrlPressed || event.isShiftPressed || event.isAltPressed || event.isMetaPressed || field.value.selection.collapsed.not()) return false
    val expectedKey = if(trueHomeFalseEnd) Key.MoveHome else Key.MoveEnd
    if (event.key != expectedKey) return false
    invoke()
    return true
}
