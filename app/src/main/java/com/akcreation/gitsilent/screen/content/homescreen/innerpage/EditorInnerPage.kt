package com.akcreation.gitsilent.screen.content.homescreen.innerpage

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.akcreation.gitsilent.compose.BottomBar
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.FileChangeListener
import com.akcreation.gitsilent.compose.FileChangeListenerState
import com.akcreation.gitsilent.compose.FullScreenScrollableColumn
import com.akcreation.gitsilent.compose.LoadingTextSimple
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.OpenAsAskReloadDialog
import com.akcreation.gitsilent.compose.OpenAsDialog
import com.akcreation.gitsilent.compose.PageCenterIconButton
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.SelectEncodingDialog
import com.akcreation.gitsilent.compose.SelectLineBreakDialog
import com.akcreation.gitsilent.compose.SelectSyntaxHighlightingDialog
import com.akcreation.gitsilent.compose.SelectedItemDialog
import com.akcreation.gitsilent.compose.SelectionRow
import com.akcreation.gitsilent.compose.SetTabSizeDialog
import com.akcreation.gitsilent.compose.rememberFileChangeListenerState
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.LineNum
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.dev.soraEditorComposeTestPassed
import com.akcreation.gitsilent.dto.FileDetail
import com.akcreation.gitsilent.dto.FileSimpleDto
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.etc.PathType
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.fileeditor.texteditor.view.ScrollEvent
import com.akcreation.gitsilent.fileeditor.ui.composable.FileEditor
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.editor.FileDetailList
import com.akcreation.gitsilent.screen.functions.getEditorStateOnChange
import com.akcreation.gitsilent.screen.functions.goToFileHistory
import com.akcreation.gitsilent.screen.shared.EditorPreviewNavStack
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.screen.shared.MainActivityLifeCycle
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.screen.shared.doActIfIsExpectLifeCycle
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.MyCodeEditor
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.EncodingUtil
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.doActWithLockIfFree
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.fileopenhistory.FileOpenHistoryMan
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.getFileNameFromCanonicalPath
import com.akcreation.gitsilent.utils.getFormattedLastModifiedTimeOfFile
import com.akcreation.gitsilent.utils.getHumanReadableSizeStr
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.isFileSizeOverLimit
import com.akcreation.gitsilent.utils.parseIntOrDefault
import com.akcreation.gitsilent.utils.showToast
import com.akcreation.gitsilent.utils.snapshot.SnapshotFileFlag
import com.akcreation.gitsilent.utils.snapshot.SnapshotUtil
import com.akcreation.gitsilent.utils.state.CustomStateListSaveable
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.withMainContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileInputStream

private const val TAG = "EditorInnerPage"
private var justForSaveFileWhenDrawerOpen = getShortUUID()
@Composable
fun EditorInnerPage(
    stateKeyTag:String,
    editorCharset: MutableState<String?>,
    lastSavedFieldsId: MutableState<String>,
    codeEditor: CustomStateSaveable<MyCodeEditor>,
    plScope: MutableState<PLScope>,
    disableSoftKb: MutableState<Boolean>,
    editorRecentListScrolled: MutableState<Boolean>,
    recentFileList: CustomStateListSaveable<FileDetail>,
    selectedRecentFileList: CustomStateListSaveable<FileDetail>,
    recentFileListSelectionMode: MutableState<Boolean>,
    recentListState: LazyStaggeredGridState,
    inRecentFilesPage: MutableState<Boolean>,
    editorFilterRecentListState: LazyStaggeredGridState,
    editorFilterRecentList: MutableList<FileDetail>,
    editorFilterRecentListOn: MutableState<Boolean>,  
    editorEnableRecentListFilter: MutableState<Boolean>,  
    editorFilterRecentListKeyword: CustomStateSaveable<TextFieldValue>,
    editorFilterRecentListLastSearchKeyword: MutableState<String>,
    editorFilterRecentListResultNeedRefresh: MutableState<String>,
    editorFilterRecentListSearching: MutableState<Boolean>,
    editorFilterRecentListSearchToken: MutableState<String>,
    editorFilterResetSearchValues: ()->Unit,
    editorRecentFilesQuitFilterMode: ()->Unit,
    loadLock:Mutex,  
    ignoreFocusOnce: MutableState<Boolean>,
    previewLoading:MutableState<Boolean>,
    editorPreviewFileDto: CustomStateSaveable<FileSimpleDto>,
    requireEditorScrollToPreviewCurPos:MutableState<Boolean>,
    requirePreviewScrollToEditorCurPos:MutableState<Boolean>,
    previewPageScrolled:MutableState<Boolean>,
    previewPath:String,
    updatePreviewPath:(String)->Unit,
    previewNavStack:CustomStateSaveable<EditorPreviewNavStack>,
    isPreviewModeOn:MutableState<Boolean>,
    mdText:MutableState<String>,
    basePath:MutableState<String>,
    quitPreviewMode:()->Unit,
    initPreviewMode:()->Unit,
    contentPadding: PaddingValues,
    currentHomeScreen: MutableIntState,
    editorPageShowingFilePath:MutableState<FilePath>,
    editorPageShowingFileIsReady:MutableState<Boolean>,
    editorPageTextEditorState:CustomStateSaveable<TextEditorState>,
    needRefreshEditorPage:MutableState<String>,
    isSaving:MutableState<Boolean>,  
    isEdited:MutableState<Boolean>,
    showReloadDialog: MutableState<Boolean>,
    isSubPageMode:Boolean,
    showCloseDialog:MutableState<Boolean>,
    closeDialogCallback:CustomStateSaveable<(Boolean)->Unit>,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    saveOnDispose:Boolean,
    doSave: suspend ()->Unit,
    naviUp: () -> Unit,
    requestFromParent:MutableState<String>,
    editorPageShowingFileDto:CustomStateSaveable<FileSimpleDto>,
    lastFilePath:MutableState<String>,
    editorLastScrollEvent:CustomStateSaveable<ScrollEvent?>,
    editorListState:LazyListState,
    editorPageIsInitDone:MutableState<Boolean>,
    editorPageIsContentSnapshoted:MutableState<Boolean>,
    goToFilesPage:(path:String) -> Unit,
    drawerState: DrawerState? = null,  
    goToLine:Int = LineNum.lastPosition,  
    editorSearchMode:MutableState<Boolean>,
    editorSearchKeyword:CustomStateSaveable<TextFieldValue>,
    readOnlyMode:MutableState<Boolean>,
    editorMergeMode:MutableState<Boolean>,
    editorPatchMode:MutableState<Boolean>,
    editorShowLineNum:MutableState<Boolean>,
    editorLineNumFontSize:MutableIntState,
    editorFontSize:MutableIntState,
    editorAdjustLineNumFontSizeMode:MutableState<Boolean>,
    editorAdjustFontSizeMode:MutableState<Boolean>,
    editorLastSavedLineNumFontSize:MutableIntState,
    editorLastSavedFontSize:MutableIntState,
    openDrawer:()->Unit,
    editorOpenFileErr:MutableState<Boolean>,
    undoStack: UndoStack,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val scope = rememberCoroutineScope()
    val activityContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val inDarkTheme = Theme.inDarkTheme
    val exitApp = {
        AppModel.exitApp()
        Unit
    }
    val settings = remember(isPreviewModeOn.value) {
        val s = SettingsUtil.getSettingsSnapshot()
        previewPageScrolled.value = s.showNaviButtons
        editorRecentListScrolled.value = s.showNaviButtons
        s
    }
    val recentFilesLimit = remember(settings.editor.recentFilesLimit) { settings.editor.recentFilesLimit }
    val saveLock = remember(editorPageShowingFilePath.value.ioPath) { Cache.getSaveLockOfFile(editorPageShowingFilePath.value.ioPath) }
    val editorPageShowingFileHasErr = rememberSaveable { mutableStateOf(false) }  
    val editorPageShowingFileErrMsg = rememberSaveable { mutableStateOf("") }  
    val unknownErrStrRes = stringResource(R.string.unknown_err)
    val curPreviewFileUsedCharset = rememberSaveable { mutableStateOf<String?>(null) }
    val lastCursorAtColumn = rememberSaveable { mutableStateOf(0) }
    val updateLastCursorAtColumn = { newValue:Int ->
        if(newValue > lastCursorAtColumn.value) {
            lastCursorAtColumn.value = newValue
        }
    }
    val resetLastCursorAtColumn = {
        lastCursorAtColumn.value = 0
    }
    val getLastCursorAtColumnValue = {
        lastCursorAtColumn.value
    }
    val editorPageSetShowingFileErrWhenLoading:(errMsg:String)->Unit = { errMsg->
        editorPageShowingFileHasErr.value=true
        editorPageShowingFileErrMsg.value=errMsg
    }
    val editorPageClearShowingFileErrWhenLoading = {
        editorPageShowingFileHasErr.value=false
        editorPageShowingFileErrMsg.value=""
    }
    val hasError = {
        editorPageShowingFileHasErr.value
    }
    val saveFontSizeAndQuitAdjust = {
        editorAdjustFontSizeMode.value = false
        if(editorLastSavedFontSize.intValue != editorFontSize.intValue) {
            editorLastSavedFontSize.intValue = editorFontSize.intValue
            SettingsUtil.update {
                it.editor.fontSize = editorFontSize.intValue
            }
        }
        Unit
    }
    val saveLineNumFontSizeAndQuitAdjust = {
        editorAdjustLineNumFontSizeMode.value = false
        if(editorLastSavedLineNumFontSize.intValue != editorLineNumFontSize.intValue) {
            editorLastSavedLineNumFontSize.intValue = editorLineNumFontSize.intValue
            SettingsUtil.update {
                it.editor.lineNumFontSize = editorLineNumFontSize.intValue
            }
        }
        Unit
    }
    val appPaused = rememberSaveable { mutableStateOf(false) }
    val fileChangeListenerState = rememberFileChangeListenerState(editorPageShowingFilePath.value.ioPath)
    editorOpenFileErr.value = remember {
        derivedStateOf {editorPageShowingFileHasErr.value && !editorPageShowingFileIsReady.value}
    }.value
    val needAndReadyDoSave:()->Boolean = { isEdited.value && !readOnlyMode.value && editorPageTextEditorState.value.fieldsId != lastSavedFieldsId.value }  
    val justForSave = remember {
        derivedStateOf {
            val drawIsOpen = drawerState?.isOpen == true
            val needRequireSave = drawIsOpen && needAndReadyDoSave()
            if (needRequireSave) {
                requestFromParent.value = PageRequest.requireSave
            }
            "justForSave: "+"uuid="+getShortUUID() + ", drawerIsOpen=" + drawIsOpen + ", isEdited=" + isEdited.value +", needRequireSave="+needRequireSave
        }
    }
    justForSaveFileWhenDrawerOpen = justForSave.value  
    val doSaveInCoroutine = {
        doJobThenOffLoading {
            saveLock.withLock {
                if(needAndReadyDoSave()) {
                    FileChangeListenerState.ignoreOnce(fileChangeListenerState)
                    doSave()
                    MyLog.d(TAG, "#doSaveInCoroutine: file saved")
                }else{
                    MyLog.w(TAG, "#doSaveInCoroutine: will not save file, cause maybe other job already saved or saving")
                }
            }
        }
    }
    val doSaveNoCoroutine = suspend {
        saveLock.withLock {
            if(needAndReadyDoSave()) {
                FileChangeListenerState.ignoreOnce(fileChangeListenerState)
                doSave()
                MyLog.d(TAG, "#doSaveNoCoroutine: file saved")
            }else{
                MyLog.w(TAG, "#doSaveNoCoroutine: will not save file, cause maybe other job already saved or saving")
            }
        }
    }
    val doSimpleSafeFastSaveInCoroutine = { requireShowMsgToUser:Boolean, requireBackupContent:Boolean, requireBackupFile:Boolean, contentSnapshotFlag:SnapshotFileFlag, fileSnapshotFlag:SnapshotFileFlag ->
        doJobThenOffLoading {
            saveLock.withLock {
                if(needAndReadyDoSave()) {
                    try {
                        FileChangeListenerState.ignoreOnce(fileChangeListenerState)
                        isSaving.value=true
                        val filePath = editorPageShowingFilePath.value
                        val editorState = editorPageTextEditorState.value
                        val ret = FsUtils.simpleSafeFastSave(
                            context = activityContext,
                            content = null,
                            editorState = editorState,
                            trueUseContentFalseUseEditorState = false,
                            targetFilePath = filePath,
                            requireBackupContent = requireBackupContent,
                            requireBackupFile = requireBackupFile,
                            contentSnapshotFlag = contentSnapshotFlag,
                            fileSnapshotFlag = fileSnapshotFlag
                        )
                        if(ret.success()) {
                            isEdited.value=false
                            lastSavedFieldsId.value = editorState.fieldsId
                            editorPageShowingFileDto.value = FileSimpleDto.genByFile(editorPageShowingFilePath.value.toFuckSafFile(activityContext))
                            MyLog.d(TAG, "#doSimpleSafeFastSaveInCoroutine: file saved")
                            if(requireShowMsgToUser){
                                Msg.requireShow(activityContext.getString(R.string.file_saved))
                            }
                        }else {
                            isEdited.value=true
                            MyLog.e(TAG, "#doSimpleSafeFastSaveInCoroutine: save file err: ${ret.msg}")
                            if(requireShowMsgToUser) {
                                Msg.requireShow(ret.msg)
                            }
                        }
                    }finally {
                        isSaving.value=false
                    }
                }else{
                    MyLog.w(TAG, "#doSimpleSafeFastSaveInCoroutine: will not save file, cause maybe other job already saved or saving")
                }
            }
        }
    }
    val saveLastOpenPath = {path:String->
        if(path.isNotBlank() && lastFilePath.value != path) {
            lastFilePath.value = path
            SettingsUtil.update {
                it.editor.lastEditedFilePath = path
            }
        }
    }
    val closeFile = {
        isEdited.value = false
        isSaving.value=false
        saveLastOpenPath(editorPageShowingFilePath.value.ioPath)
        val emptyPath = FilePath("")
        editorPageShowingFilePath.value = emptyPath
        editorPageShowingFileDto.value.fullPath=""
        editorPageClearShowingFileErrWhenLoading()  
        editorPageShowingFileIsReady.value = false
        codeEditor.value.reset(FuckSafFile(AppModel.realAppContext, emptyPath), force = true)
        doJobThenOffLoading {
            undoStack.reset("", force = true)
        }
    }
    if(showCloseDialog.value) {
        if(!needAndReadyDoSave()) {
            showCloseDialog.value=false
            closeFile()
        }else {
            ConfirmDialog(
                title = stringResource(id = R.string.close),
                text = stringResource(id = R.string.will_close_file_are_u_sure),
                okTextColor = MyStyleKt.TextColor.danger(),
                onCancel = { showCloseDialog.value=false }
            ) {
                showCloseDialog.value=false
                closeFile()
            }
        }
    }
    val reloadFile = r@{ force:Boolean ->
        if(force) {
            editorPageShowingFileDto.value.fullPath = ""
            codeEditor.value.releaseAndClearUndoStack()
        }else { 
            val requireOpenFilePath = editorPageShowingFileDto.value.fullPath
            try {
                val newDto = FileSimpleDto.genByFile(FuckSafFile(activityContext, FilePath(requireOpenFilePath)))
                val oldDto = editorPageShowingFileDto.value
                if (newDto == oldDto) {
                    MyLog.d(TAG,"EditorInnerPage#reloadFile(force=false): file may not changed, skip reload, file path is '${requireOpenFilePath}'")
                    editorPageShowingFileIsReady.value = true
                    return@r
                }
            }catch (e: Exception) {
                MyLog.d(TAG,"EditorInnerPage#reloadFile(force=false): check file changes err, will reload file '${requireOpenFilePath}', err=${e.stackTraceToString()}")
            }
        }
        isEdited.value = false
        isSaving.value = false
        editorPageShowingFileIsReady.value = false  
        changeStateTriggerRefreshPage(needRefreshEditorPage)
    }
    val forceReloadFile={
        val force = true
        reloadFile(force)
    }
    val forceReloadFilePath = { path: FilePath ->
        editorPageShowingFilePath.value = path
        forceReloadFile()
    }
    val showInFiles = { path:FilePath ->
        if(path.ioPathType == PathType.ABSOLUTE) {  
            goToFilesPage(path.ioPath)
        }else {  
            Msg.requireShowLongDuration(activityContext.getString(R.string.file_path_invalid)+": "+path.ioPath)
        }
    }
    val getTheLastOpenedFilePath = {
        lastFilePath.value.ifBlank {
            SettingsUtil.getSettingsSnapshot().editor.lastEditedFilePath
        }
    }
    val reloadOnOkBeforeCb = remember { mutableStateOf<(()->Unit)?>(null) }
    val initReloadDialogWithCallback = { onOkBeforeCb: (()->Unit)? ->
        reloadOnOkBeforeCb.value = onOkBeforeCb
        showReloadDialog.value = true
    }
    if(showReloadDialog.value) {
        if(!needAndReadyDoSave()) {
            showReloadDialog.value = false  
            val newDto = FileSimpleDto.genByFile(editorPageShowingFilePath.value.toFuckSafFile(activityContext))
            if (newDto != editorPageShowingFileDto.value) {
                val fileName = editorPageShowingFileDto.value.name
                MyLog.d(TAG,"#showReloadDialog: file '${fileName}' may changed by external, will save content snapshot before reload")
                val editorState = editorPageTextEditorState.value
                doJobThenOffLoading {
                    val snapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                        srcFileName = fileName,
                        fileContent = null,
                        editorState = editorState,
                        trueUseContentFalseUseEditorState = false,
                        flag = SnapshotFileFlag.editor_content_BeforeReloadFoundSrcFileChanged
                    )
                    if(snapRet.hasError()) {
                        MyLog.e(TAG, "#showReloadDialog: save content snapshot before reload, err: "+snapRet.msg)
                    }
                }
            }
            reloadOnOkBeforeCb.value?.invoke()
            reloadOnOkBeforeCb.value = null
            forceReloadFile()
        }else {
            ConfirmDialog(
                title = stringResource(R.string.reload_file),
                text = stringResource(R.string.will_reload_file_are_u_sure),
                okTextColor = MyStyleKt.TextColor.danger(),
                onCancel = {
                    showReloadDialog.value = false
                    reloadOnOkBeforeCb.value = null
                }
            ) {
                showReloadDialog.value = false
                reloadOnOkBeforeCb.value?.invoke()
                reloadOnOkBeforeCb.value = null
                forceReloadFile()
            }
        }
    }
    val showClearRecentFilesDialog = rememberSaveable { mutableStateOf(false) }
    if(showClearRecentFilesDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.clear),
            text = stringResource(R.string.clear_recent_files_confirm_text),
            okBtnText = stringResource(R.string.clear),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showClearRecentFilesDialog.value = false}
        ) {
            showClearRecentFilesDialog.value = false
            doJobThenOffLoading {
                try {
                    FileOpenHistoryMan.reset()
                    Msg.requireShow(activityContext.getString(R.string.cleared))
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#ClearRecentFilesListFromEditor err: ${e.stackTraceToString()}")
                }
            }
        }
    }
    val editorAsUnsaved = {
        codeEditor.value.doActWithLatestEditorStateInCoroutine("#asUnsaved") {
            it.asUnsaved()
        }
    }
    val showLineBreakDialog = rememberSaveable { mutableStateOf(false) }
    if(showLineBreakDialog.value) {
        SelectLineBreakDialog(
            current = codeEditor.value.lineBreak,
            closeDialog = { showLineBreakDialog.value = false }
        ) {
            if(it != codeEditor.value.lineBreak) {
                codeEditor.value.lineBreak = it
                editorAsUnsaved()
            }
        }
    }
    val showBackFromExternalAppAskReloadDialog = rememberSaveable { mutableStateOf(false) }
    if(showBackFromExternalAppAskReloadDialog.value) {
        OpenAsAskReloadDialog(
            onCancel = { showBackFromExternalAppAskReloadDialog.value=false }
        ) {  
            val newDto = FileSimpleDto.genByFile(editorPageShowingFilePath.value.toFuckSafFile(activityContext))
            if (newDto != editorPageShowingFileDto.value) {
                val fileName = editorPageShowingFileDto.value.name
                MyLog.d(TAG,"#showBackFromExternalAppAskReloadDialog: file '${fileName}' may changed by external, will save content snapshot before reload")
                val editorState = editorPageTextEditorState.value
                doJobThenOffLoading {
                    val snapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                        srcFileName = fileName,
                        fileContent = null,
                        editorState = editorState,
                        trueUseContentFalseUseEditorState = false,
                        flag = SnapshotFileFlag.editor_content_BeforeReloadFoundSrcFileChanged_ReloadByBackFromExternalDialog
                    )
                    if(snapRet.hasError()) {
                        MyLog.e(TAG, "#showBackFromExternalAppAskReloadDialog: save content snapshot before reload, err: "+snapRet.msg)
                    }
                }
            }
            showBackFromExternalAppAskReloadDialog.value=false
            forceReloadFile()
        }
    }
    val showOpenAsDialog = rememberSaveable { mutableStateOf(false) }
    val readOnlyForOpenAsDialog = rememberSaveable { mutableStateOf(false) }
    val openAsDialogFilePath = rememberSaveable { mutableStateOf("") }
    val openAsDialogFileName = remember(openAsDialogFilePath.value) { derivedStateOf { getFileNameFromCanonicalPath(openAsDialogFilePath.value) } }
    val showReloadDialogForOpenAs = rememberSaveable { mutableStateOf(false) }
    fun initOpenAsDialog(filePath:String, showReloadDialog:Boolean = true) {
        showReloadDialogForOpenAs.value = showReloadDialog
        openAsDialogFilePath.value = filePath
        showOpenAsDialog.value = true
    }
    if(showOpenAsDialog.value) {
        OpenAsDialog(readOnly = readOnlyForOpenAsDialog, fileName = openAsDialogFileName.value, filePath = openAsDialogFilePath.value,
            openSuccessCallback = {
                if(showReloadDialogForOpenAs.value) {
                    showBackFromExternalAppAskReloadDialog.value = true  
                }
            }
        ) {
            showOpenAsDialog.value = false
        }
    }
    val checkPathThenGoToFilesPage = {
        val path = editorPageShowingFilePath.value
        if(path.isBlank()) {
            Msg.requireShow(activityContext.getString(R.string.invalid_path))
        }else {
            val fuckSafFile = FuckSafFile(activityContext, path)
            if(!fuckSafFile.exists()) {
                Msg.requireShow(activityContext.getString(R.string.file_doesnt_exist))
            }else {  
                goToFilesPage(fuckSafFile.canonicalPath)
            }
        }
    }
    val keepPreviewNavStackOnce = rememberSaveable { mutableStateOf(false) }
    val previewLoadingOn = {
        previewLoading.value = true
    }
    val previewLoadingOff = {
        previewLoading.value = false
    }
    val previewNavBack = {
        runBlocking {
            val last = previewNavStack.value.back()
            if(last == null) {
                quitPreviewMode()
            }else {
                requestFromParent.value = if(isSubPageMode) PageRequest.requireInitPreviewFromSubEditor else PageRequest.requireInitPreview
            }
        }
        Unit
    }
    val previewNavAhead = {
        runBlocking {
            val next = previewNavStack.value.ahead()
            if(next != null){
                requestFromParent.value = if(isSubPageMode) PageRequest.requireInitPreviewFromSubEditor else PageRequest.requireInitPreview
            }
        }
        Unit
    }
    val previewLinkHandler:(link:String)->Boolean = { link ->
        if(FsUtils.maybeIsRelativePath(link)) {
            val previewNavStack = previewNavStack.value
            runBlocking {
                val previewingFileFullPath = previewNavStack.previewingPath
                val linkFullPath = FsUtils.getAbsolutePathIfIsRelative(path = link, basePathNoEndSlash = FsUtils.getParentPath(previewingFileFullPath))
                val aheadFirstPath = previewNavStack.getAheadStackFirst()?.path
                if(aheadFirstPath == linkFullPath) {  
                    previewNavAhead()
                    true
                }else { 
                    val pushSuccess = previewNavStack.push(linkFullPath)
                    if(pushSuccess) {
                        previewNavAhead()
                        true
                    }else{
                        false
                    }
                }
            }
        }else {
            false
        }
    }
    val updatePreviewDto = { previewPath:String ->
        editorPreviewFileDto.value = FileSimpleDto.genByFile(FuckSafFile(activityContext, FilePath(previewPath)))
    }
    val refreshPreviewPageNoCoroutine = { previewPath:String, force:Boolean ->
        val needRefresh = if(force) {
            true
        }else {
            val newDto = FileSimpleDto.genByFile(FuckSafFile(activityContext, FilePath(previewPath)))
            val oldDto = editorPreviewFileDto.value
            if (newDto == oldDto) {
                MyLog.d(TAG,"EditorInnerPage#refreshPreviewPageNoCoroutine: file may not changed, skip reload, file path is '${previewPath}'")
                false
            }else {
                true
            }
        }
        if(needRefresh) {
            val encoding = EncodingUtil.detectEncoding(newInputStream = { FileInputStream(previewPath) })
            curPreviewFileUsedCharset.value = encoding
            mdText.value = FsUtils.readFile(previewPath, EncodingUtil.resolveCharset(encoding))
            updatePreviewDto(previewPath)
        }
    }
    val refreshPreviewPage = { previewPath:String, force:Boolean ->
        doJobThenOffLoading(
            loadingOn = { previewLoadingOn() },
            loadingOff = { previewLoadingOff() }
        ) {
            refreshPreviewPageNoCoroutine(previewPath, force)
        }
    }
    val loadingRecentFiles = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val loadingTextForRecentFiles = rememberSaveable { mutableStateOf("") }
    val loadingOnForRecentFileList = { msg:String ->
        loadingTextForRecentFiles.value = msg
        loadingRecentFiles.value = true
    }
    val loadingOffForRecentFileList = {
        loadingRecentFiles.value = false
    }
    val needRefreshRecentFileList = rememberSaveable { mutableStateOf("") }
    val reloadRecentFileList = {
        changeStateTriggerRefreshPage(needRefreshRecentFileList)
    }
    val tabIndentSpacesCount = rememberSaveable { mutableStateOf(SettingsUtil.editorTabIndentCount()) }
    val tabSizeBuf = mutableCustomStateOf(stateKeyTag, "tabSizeBuf") { TextFieldValue("") }
    val showSetTabSizeDialog = rememberSaveable { mutableStateOf(false) }
    val initSetTabSizeDialog = {
        tabSizeBuf.value = tabIndentSpacesCount.value.toString().let { TextFieldValue(it, selection = TextRange(0, it.length)) }
        showSetTabSizeDialog.value = true
    }
    if(showSetTabSizeDialog.value) {
        val closeDialog = { showSetTabSizeDialog.value = false }
        SetTabSizeDialog(
            tabSizeBuf = tabSizeBuf,
            onCancel = closeDialog,
            onOk = ok@{ newSize ->
                val newSize = parseIntOrDefault(newSize, null)
                if(newSize == null) {
                    Msg.requireShow(activityContext.getString(R.string.invalid_number))
                    return@ok
                }
                closeDialog()
                tabIndentSpacesCount.value = newSize
                SettingsUtil.update {
                    it.editor.tabIndentSpacesCount = newSize
                }
            }
        )
    }
    val showSelectEncodingDialog = rememberSaveable { mutableStateOf(false) }
    val isConvertEncoding = rememberSaveable { mutableStateOf(false) }
    val initSelectEncodingDialog = { convert: Boolean ->
        isConvertEncoding.value = convert
        showSelectEncodingDialog.value = true
    }
    if(showSelectEncodingDialog.value) {
        SelectEncodingDialog(
            currentCharset = editorCharset.value,
            closeDialog = { showSelectEncodingDialog.value = false },
        ) { newCharset ->
            showSelectEncodingDialog.value = false
            if(newCharset != editorCharset.value) {
                if(isConvertEncoding.value) {  
                    editorCharset.value = newCharset
                    editorAsUnsaved()
                }else {  
                    initReloadDialogWithCallback {
                        editorCharset.value = newCharset
                    }
                }
            }
        }
    }
    val showSelectSyntaxHighlightDialog = rememberSaveable { mutableStateOf(false) }
    val initSelectSyntaxHighlightingDialog = {
        doJobThenOffLoading {
            doSaveNoCoroutine()
            showSelectSyntaxHighlightDialog.value = true
        }
    }
    if(showSelectSyntaxHighlightDialog.value) {
        SelectSyntaxHighlightingDialog(
            plScope = plScope.value,
            closeDialog = { showSelectSyntaxHighlightDialog.value = false }
        ) {
            showSelectSyntaxHighlightDialog.value = false
            if(it != plScope.value) {
                plScope.value = it
                doJobThenOffLoading {
                    doSaveNoCoroutine()
                    forceReloadFile()
                }
            }
        }
    }
    if(requestFromParent.value == PageRequest.showLineBreakDialog) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            showLineBreakDialog.value = true
        }
    }
    if(requestFromParent.value == PageRequest.showSelectEncodingDialog) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            initSelectEncodingDialog(false)
        }
    }
    if(requestFromParent.value == PageRequest.convertEncoding) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            initSelectEncodingDialog(true)
        }
    }
    if(requestFromParent.value == PageRequest.selectSyntaxHighlighting) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            initSelectSyntaxHighlightingDialog()
        }
    }
    if(requestFromParent.value == PageRequest.showSetTabSizeDialog) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            initSetTabSizeDialog()
        }
    }
    if(requestFromParent.value == PageRequest.reloadRecentFileList) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            reloadRecentFileList()
        }
    }
    if(requestFromParent.value == PageRequest.reloadIfChanged) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val force = false
            reloadFile(force)
        }
    }
    if(requestFromParent.value == PageRequest.requireSave) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            if(needAndReadyDoSave()){
                doSaveInCoroutine()
            }
        }
    }
    if(requestFromParent.value == PageRequest.requireClose) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            showCloseDialog.value=true
        }
    }
    if(requestFromParent.value == PageRequest.editorPreviewPageGoBack) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            previewNavBack()
        }
    }
    if(requestFromParent.value == PageRequest.editorPreviewPageGoForward) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            previewNavAhead()
        }
    }
    if(requestFromParent.value == PageRequest.editor_RequireRefreshPreviewPage) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val force = true
            refreshPreviewPage(previewPath, force)
        }
    }
    if(requestFromParent.value == PageRequest.requireInitPreview || requestFromParent.value == PageRequest.requireInitPreviewFromSubEditor) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val editorPageShowingFilePath = editorPageShowingFilePath.value.ioPath
            val previewNavStack = previewNavStack.value
            doJobThenOffLoading {
                doSaveNoCoroutine()
                previewLoadingOn()
                val switchFromEditPage = isPreviewModeOn.value.not()
                var pathWillPreview = previewNavStack.getCurrent().path
                if(switchFromEditPage && pathWillPreview != editorPageShowingFilePath && previewNavStack.backStackOrAheadStackIsNotEmpty()) {
                    val pushSuccess = previewNavStack.push(editorPageShowingFilePath)
                    if(pushSuccess) {
                        previewNavStack.ahead()
                    }
                    requirePreviewScrollToEditorCurPos.value = true
                    pathWillPreview = editorPageShowingFilePath
                }
                previewNavStack.previewingPath = pathWillPreview
                updatePreviewPath(pathWillPreview)
                basePath.value = FsUtils.getParentPath(pathWillPreview)
                mdText.value = if(pathWillPreview == editorPageShowingFilePath) {
                    curPreviewFileUsedCharset.value = editorCharset.value
                    editorPageTextEditorState.value.getAllText()
                } else {
                    val encoding = EncodingUtil.detectEncoding(newInputStream = { FileInputStream(pathWillPreview) })
                    curPreviewFileUsedCharset.value = encoding
                    FsUtils.readFile(pathWillPreview, EncodingUtil.resolveCharset(encoding))
                }
                updatePreviewDto(pathWillPreview)
                isPreviewModeOn.value = true
                previewLoadingOff()
            }
        }
    }
    if(requestFromParent.value == PageRequest.editorPreviewPageGoToTop) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val scrollState = runBlocking { previewNavStack.value.getCurrentScrollState() }
            UIHelper.scrollTo(scope, scrollState, 0)
        }
    }
    if(requestFromParent.value == PageRequest.editorPreviewPageGoToBottom) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val scrollState = runBlocking { previewNavStack.value.getCurrentScrollState() }
            UIHelper.scrollTo(scope, scrollState, Int.MAX_VALUE)
        }
    }
    if(requestFromParent.value == PageRequest.requireSaveFontSizeAndQuitAdjust) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            saveFontSizeAndQuitAdjust()
        }
    }
    if(requestFromParent.value == PageRequest.requireSaveLineNumFontSizeAndQuitAdjust) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            saveLineNumFontSizeAndQuitAdjust()
        }
    }
    if(requestFromParent.value == PageRequest.doSaveIfNeedThenSwitchReadOnly) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                if(needAndReadyDoSave()) {
                    doSaveNoCoroutine()
                }
                readOnlyMode.value = !readOnlyMode.value
            }
        }
    }
    if(requestFromParent.value == PageRequest.requireOpenAs) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            if(editorPageShowingFilePath.value.isNotBlank()) {
                doJobThenOffLoading {
                    doSaveNoCoroutine()
                    initOpenAsDialog(editorPageShowingFilePath.value.toFuckSafFile(activityContext).canonicalPath)
                }
            }else{
                Msg.requireShow(activityContext.getString(R.string.file_path_invalid))
            }
        }
    }
    if(requestFromParent.value == PageRequest.showInFiles) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            checkPathThenGoToFilesPage()
        }
    }
    if(requestFromParent.value == PageRequest.requireGoToFileHistory) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            goToFileHistory(editorPageShowingFilePath.value, activityContext)
        }
    }
    if(requestFromParent.value == PageRequest.requireEditPreviewingFile) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            runBlocking {
                val previewingPath = previewNavStack.value.previewingPath  
                previewNavStack.value.editingPath = previewingPath
                if(previewingPath != editorPageShowingFilePath.value.ioPath) {
                    keepPreviewNavStackOnce.value = true  
                    forceReloadFilePath(FilePath(previewingPath))
                }
                quitPreviewMode()
            }
        }
    }
    if(requestFromParent.value == PageRequest.requireBackToHome) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            runBlocking {
                previewNavStack.value.backToHome()
                if(previewNavStack.value.backStackIsNotEmpty()) {
                    previewNavBack()
                }
            }
        }
    }
    val quitRecentListSelectionMode = {
        recentFileListSelectionMode.value = false
        selectedRecentFileList.value.clear()
    }
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }
    val backHandlerOnBack = getBackHandler(
        inRecentFilesPage = inRecentFilesPage,
        recentFileListSelectionMode = recentFileListSelectionMode,
        quitRecentListSelectionMode = quitRecentListSelectionMode,
        editorFilterRecentListOn = editorFilterRecentListOn,
        editorRecentFilesQuitFilterMode = editorRecentFilesQuitFilterMode,
        previewNavBack = previewNavBack,
        isPreviewModeOn = isPreviewModeOn,
        quitPreviewMode = quitPreviewMode,
        activityContext = activityContext,
        textEditorState = editorPageTextEditorState,
        isSubPage = isSubPageMode,
        isEdited = isEdited,
        readOnlyMode = readOnlyMode,
        doSaveNoCoroutine = doSaveNoCoroutine,
        searchMode = editorSearchMode,
        needAndReadyDoSave = needAndReadyDoSave,
        naviUp = naviUp,
        adjustFontSizeMode=editorAdjustFontSizeMode,
        adjustLineNumFontSizeMode=editorAdjustLineNumFontSizeMode,
        saveFontSizeAndQuitAdjust = saveFontSizeAndQuitAdjust,
        saveLineNumFontSizeAndQuitAdjust = saveLineNumFontSizeAndQuitAdjust,
        exitApp = exitApp,
        openDrawer=openDrawer,
        requestFromParent = requestFromParent
    )
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    val showDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val detailsStr = rememberSaveable { mutableStateOf("") }
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = stringResource(R.string.details),
            text = detailsStr.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsStr.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    if(requestFromParent.value==PageRequest.showDetails) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val editorPageShowingFilePath = editorPageShowingFilePath.value
            val currentFile = FuckSafFile(activityContext, if(isPreviewModeOn.value) FilePath(previewPath) else editorPageShowingFilePath)
            val isEditingFile = currentFile.path.ioPath == editorPageShowingFilePath.ioPath
            val fileReadable = true
            val fileName = currentFile.name
            val fileSize = if(fileReadable) getHumanReadableSizeStr(currentFile.length()) else 0
            val showLinesCharsCount = isEditingFile && fileReadable
            val (charsCount, linesCount) = if(showLinesCharsCount) editorPageTextEditorState.value.getCharsAndLinesCount() else Pair(0, 0)
            val lastModifiedTimeStr = if(fileReadable) getFormattedLastModifiedTimeOfFile(currentFile) else ""
            val sb = StringBuilder()
            val suffix = "\n\n"
            sb.append(activityContext.getString(R.string.file_name)+": "+fileName).append(suffix)
            sb.append(activityContext.getString(R.string.path)+": "+ currentFile.path.ioPath).append(suffix)
            if(showLinesCharsCount) {
                sb.append(activityContext.getString(R.string.chars)+": "+charsCount).append(suffix)
                sb.append(activityContext.getString(R.string.lines) +": "+linesCount).append(suffix)
            }
            if(fileReadable) {
                sb.append(activityContext.getString(R.string.file_size)+": "+fileSize).append(suffix)
                sb.append(activityContext.getString(R.string.last_modified)+": "+lastModifiedTimeStr).append(suffix)
            }
            sb.append(activityContext.getString(R.string.encoding)+": "+(if(isPreviewModeOn.value) curPreviewFileUsedCharset.value else editorCharset.value)).append(suffix)
            sb.append(activityContext.getString(R.string.line_break)+": "+codeEditor.value.lineBreak.visibleValue).append(suffix)
            detailsStr.value = sb.removeSuffix(suffix).toString()
            showDetailsDialog.value = true
        }
    }
    val notOpenFile = !editorPageShowingFileHasErr.value && !editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isBlank()
    val loadingFile = !editorPageShowingFileHasErr.value && !editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isNotBlank()
    val somethingWrong = editorPageShowingFileHasErr.value || !editorPageShowingFileIsReady.value || editorPageShowingFilePath.value.isBlank()
    if (notOpenFile) {  
        val selectionMode = recentFileListSelectionMode
        val quitSelectionMode = quitRecentListSelectionMode
        val switchItemSelected = { item: FileDetail ->
            UIHelper.selectIfNotInSelectedListElseRemove(item, selectedRecentFileList.value)
            selectionMode.value = true
        }
        val selectItem = { item:FileDetail ->
            selectionMode.value = true
            UIHelper.selectIfNotInSelectedListElseNoop(item, selectedRecentFileList.value)
        }
        val isItemInSelected= { item:FileDetail ->
            selectedRecentFileList.value.contains(item)
        }
        LaunchedEffect(needRefreshRecentFileList.value) {
            doJobThenOffLoading(loadingOnForRecentFileList, loadingOffForRecentFileList, activityContext.getString(R.string.loading)) {
                try {
                    val historyMap = FileOpenHistoryMan.getHistory().storage
                    val recentFiles = historyMap
                        .toSortedMap({ k1, k2 ->
                            val v1 = historyMap.get(k1)!!
                            val v2 = historyMap.get(k2)!!
                            if (v1.lastUsedTime > v2.lastUsedTime) -1 else 1
                        }).let { sortedMap ->
                            val list = mutableListOf<FileDetail>()
                            for((k, _lastEditPos) in sortedMap) {
                                if(list.size >= recentFilesLimit) {
                                    break
                                }
                                val file = FuckSafFile(activityContext, FilePath(k))
                                if(file.name.isNotEmpty()) {
                                    val fileShortContent = FsUtils.readShortContent(file)
                                    list.add(
                                        FileDetail(
                                            file = file,
                                            shortContent = fileShortContent,
                                        )
                                    )
                                }
                            }
                            list
                        };
                    recentFileList.value.let {
                        it.clear()
                        it.addAll(recentFiles)
                    }
                    if(recentFileListSelectionMode.value) {
                        val selectedList = selectedRecentFileList.value
                        val newSelectedList = mutableListOf<FileDetail>()
                        selectedList.forEachBetter {
                            for(f in recentFiles) {
                                if(f.file.path.ioPath == it.file.path.ioPath) {
                                    newSelectedList.add(f)
                                }
                            }
                        }
                        selectedList.clear()
                        selectedList.addAll(newSelectedList)
                        if(selectedList.isEmpty()) {
                            quitSelectionMode()
                        }
                    }
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?: "get recent files err")
                    MyLog.e(TAG, "Recent Files onClick err: ${e.stackTraceToString()}")
                }
            }
        }
        if(loadingRecentFiles.value) {
            LoadingTextSimple(loadingTextForRecentFiles.value, contentPadding)
        }else {
            if(recentFileList.value.isNotEmpty()) {
                LaunchedEffect(Unit) {
                    inRecentFilesPage.value = true
                }
                DisposableEffect(Unit) {
                    onDispose {
                        inRecentFilesPage.value = false
                        if(recentFileList.value.isEmpty()) {
                            quitSelectionMode()
                        }
                    }
                }
                PullToRefreshBox(
                    contentPadding = contentPadding,
                    onRefresh = { reloadRecentFileList() }
                ) {
                    FileDetailList(
                        filterListState = editorFilterRecentListState,
                        filterList = editorFilterRecentList,
                        filterOn = editorFilterRecentListOn,
                        enableFilterState = editorEnableRecentListFilter,
                        filterKeyword = editorFilterRecentListKeyword,
                        lastSearchKeyword = editorFilterRecentListLastSearchKeyword,
                        filterResultNeedRefresh = editorFilterRecentListResultNeedRefresh,
                        searching = editorFilterRecentListSearching,
                        searchToken = editorFilterRecentListSearchToken,
                        resetSearchVars = editorFilterResetSearchValues,
                        contentPadding = contentPadding,
                        state = recentListState,
                        isItemSelected = isItemInSelected,
                        list = recentFileList.value,
                        onClick = {
                            if(selectionMode.value) {
                                switchItemSelected(it)
                            }else {
                                forceReloadFilePath(it.file.path)
                            }
                        },
                        itemOnLongClick = {idx, it->
                            if(selectionMode.value) {
                                UIHelper.doSelectSpan(
                                    itemIdxOfItemList = idx,
                                    item = it,
                                    selectedItems = selectedRecentFileList.value,
                                    itemList = recentFileList.value,
                                    switchItemSelected = switchItemSelected,
                                    selectIfNotInSelectedListElseNoop = selectItem
                                )
                            }else {
                                switchItemSelected(it)
                            }
                        },
                    )
                }
            }else {
                if(!isSubPageMode) {
                    PageCenterIconButton(
                        contentPadding = contentPadding,
                        icon = Icons.Filled.Folder,
                        text = stringResource(R.string.select_file),
                        onClick = {
                            currentHomeScreen.intValue = Cons.selectedItem_Files
                        }
                    )
                } else {  
                    PageCenterIconButton(
                        contentPadding = contentPadding,
                        icon = ImageVector.vectorResource(R.drawable.outline_reopen_window_24),
                        text = stringResource(R.string.reopen),
                        onClick = {
                            forceReloadFilePath(FilePath(getTheLastOpenedFilePath()))
                        }
                    )
                }
            }
            val showDeleteRecentFilesDialog = rememberSaveable { mutableStateOf(false) }
            val deleteFileOnDisk = rememberSaveable { mutableStateOf(false) }
            val initDeleteRecentFilesDialog = {
                deleteFileOnDisk.value = false
                showDeleteRecentFilesDialog.value = true
            }
            if(showDeleteRecentFilesDialog.value) {
                ConfirmDialog(
                    title = stringResource(R.string.delete),
                    requireShowTextCompose = true,
                    textCompose = {
                        ScrollableColumn {
                            SelectionRow {
                                Text(stringResource(R.string.will_delete_selected_items_are_u_sure), fontSize = MyStyleKt.TextSize.medium)
                            }
                            Spacer(Modifier.height(20.dp))
                            MyCheckBox(stringResource(R.string.del_files_on_disk), deleteFileOnDisk)
                        }
                    },
                    onCancel = { showDeleteRecentFilesDialog.value = false },
                    okBtnText = stringResource(R.string.delete),
                    okTextColor = if(deleteFileOnDisk.value) MyStyleKt.TextColor.danger() else Color.Unspecified,
                ) {
                    showDeleteRecentFilesDialog.value = false
                    val deleteFileOnDisk = deleteFileOnDisk.value
                    val targetList = selectedRecentFileList.value.toList()
                    doJobThenOffLoading {
                        targetList.forEachBetter {
                            recentFileList.value.remove(it)
                            selectedRecentFileList.value.remove(it)
                            FileOpenHistoryMan.remove(it.file.path.ioPath)
                            if(deleteFileOnDisk) {
                                if(it.file.path.ioPathType == PathType.ABSOLUTE) {
                                    try {
                                        File(it.file.path.ioPath).delete()
                                    }catch (e: Exception) {
                                        MyLog.w(TAG, "remove file failed: ioPath=${it.file.path.ioPath}, err=${e.localizedMessage}")
                                    }
                                }
                            }
                        }
                        Msg.requireShow(activityContext.getString(R.string.deleted))
                    }
                }
            }
            val iconList = listOf(
                Icons.Filled.Delete,
                Icons.Filled.DocumentScanner,  
                Icons.AutoMirrored.Filled.OpenInNew, 
                Icons.Filled.SelectAll,
            )
            val iconTextList = listOf(
                stringResource(R.string.delete),
                stringResource(R.string.show_in_files),
                stringResource(R.string.open_as),
                stringResource(R.string.select_all),
            )
            val iconOnClickList = listOf(
                delete@{
                    initDeleteRecentFilesDialog()
                },
                showInFiles@{
                    selectedRecentFileList.value.firstOrNull()?.let { showInFiles(it.file.path) }
                    Unit
                },
                openAs@{
                    selectedRecentFileList.value.firstOrNull()?.let { initOpenAsDialog(it.file.path.ioPath, showReloadDialog = false) }
                    Unit
                },
                selectAll@{
                    selectedRecentFileList.value.let {
                        it.clear()
                        it.addAll(recentFileList.value)
                    }
                    Unit
                }
            )
            val getSelectedFilesCount = {
                selectedRecentFileList.value.size
            }
            val iconEnableList = listOf(
                delete@{ true },
                showInFiles@{ isSubPageMode.not() && selectedRecentFileList.value.size == 1 },
                openAs@{ selectedRecentFileList.value.size == 1 },
                selectAll@{ true },
            )
            val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false) }
            if(showSelectedItemsShortDetailsDialog.value) {
                SelectedItemDialog(
                    selectedItems = selectedRecentFileList.value,
                    formatter = {it.file.name},
                    switchItemSelected = switchItemSelected,
                    clearAll = {selectedRecentFileList.value.clear()},
                    closeDialog = {showSelectedItemsShortDetailsDialog.value = false}
                )
            }
            val countNumOnClickForBottomBar = {
                showSelectedItemsShortDetailsDialog.value = true
            }
            if(selectionMode.value) {
                BottomBar(
                    quitSelectionMode=quitSelectionMode,
                    iconList=iconList,
                    iconTextList=iconTextList,
                    iconDescTextList=iconTextList,
                    iconOnClickList=iconOnClickList,
                    iconEnableList=iconEnableList,
                    moreItemTextList=listOf(),
                    moreItemOnClickList=listOf(),
                    moreItemEnableList = listOf(),
                    getSelectedFilesCount = getSelectedFilesCount,
                    countNumOnClickEnabled = true,
                    countNumOnClick = countNumOnClickForBottomBar,
                    reverseMoreItemList = true
                )
            }
        }
    }
    if(
        ((editorOpenFileErr.value) 
                || (loadingFile))  
        && somethingWrong  
        && !notOpenFile
    ){
        FullScreenScrollableColumn(contentPadding) {
            val fontSize = MyStyleKt.TextSize.default
            val iconModifier = MyStyleKt.Icon.modifier
            if (editorOpenFileErr.value) {  
                MySelectionContainer {
                    Row {
                        Text(
                            text = stringResource(id = R.string.open_file_failed)+"\n"+editorPageShowingFileErrMsg.value,
                            color = MyStyleKt.TextColor.error(),
                            fontSize = fontSize
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row {
                    LongPressAbleIconBtn(
                        enabled = true,
                        iconModifier = iconModifier,
                        tooltipText = stringResource(R.string.open_as),
                        icon = Icons.AutoMirrored.Filled.OpenInNew,
                        iconContentDesc = stringResource(id = R.string.open_as),
                    ) {
                        requestFromParent.value = PageRequest.requireOpenAs
                    }
                    LongPressAbleIconBtn(
                        enabled = true,
                        iconModifier = iconModifier,
                        tooltipText = stringResource(R.string.reload),
                        icon =  Icons.Filled.Refresh,
                        iconContentDesc = stringResource(id = R.string.reload),
                    ) {
                        forceReloadFile()
                    }
                    if(!isSubPageMode){
                        LongPressAbleIconBtn(
                            enabled = true,
                            iconModifier = iconModifier,
                            tooltipText = stringResource(R.string.show_in_files),
                            icon =  Icons.Filled.DocumentScanner,
                            iconContentDesc = stringResource(id = R.string.show_in_files),
                        ) {
                            checkPathThenGoToFilesPage()
                        }
                    }
                    LongPressAbleIconBtn(
                        enabled = true,
                        iconModifier = iconModifier,
                        tooltipText = stringResource(R.string.close),
                        icon =  Icons.Filled.Close,
                        iconContentDesc = stringResource(id = R.string.close),
                    ) {
                        closeFile()
                    }
                }
            }
            if(loadingFile) {
                Text(stringResource(R.string.loading))
            }
        }
    }
    val isTimeShowEditor = !editorPageShowingFileHasErr.value && editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isNotBlank()
    if(isTimeShowEditor) {
        if(soraEditorComposeTestPassed) {
        }else {
            val fileFullPath = editorPageShowingFilePath.value
            val fileEditedPos = FileOpenHistoryMan.get(fileFullPath.ioPath)
            FileEditor(
                stateKeyTag = stateKeyTag,
                plScope = plScope,
                disableSoftKb = disableSoftKb,
                updateLastCursorAtColumn = updateLastCursorAtColumn,
                getLastCursorAtColumnValue = getLastCursorAtColumnValue,
                ignoreFocusOnce = ignoreFocusOnce,
                requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
                requirePreviewScrollToEditorCurPos = requirePreviewScrollToEditorCurPos,
                isSubPageMode = isSubPageMode,
                previewNavBack = previewNavBack,
                previewNavAhead = previewNavAhead,
                previewNavStack = previewNavStack,
                refreshPreviewPage = { requestFromParent.value = PageRequest.editor_RequireRefreshPreviewPage },
                previewLinkHandler = previewLinkHandler,
                previewLoading = previewLoading.value,
                mdText = mdText,
                basePath = basePath,
                isPreviewModeOn = isPreviewModeOn,
                quitPreviewMode = quitPreviewMode,
                initPreviewMode = initPreviewMode,
                openDrawer = openDrawer,
                requestFromParent = requestFromParent,
                fileFullPath = fileFullPath,
                lastEditedPos = fileEditedPos,
                textEditorState = editorPageTextEditorState,
                contentPadding = contentPadding,
                isContentEdited = isEdited,   
                editorLastScrollEvent=editorLastScrollEvent,
                editorListState=editorListState,
                editorPageIsInitDone = editorPageIsInitDone,
                editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
                goToLine=goToLine,
                readOnlyMode=readOnlyMode.value,
                searchMode = editorSearchMode,
                searchKeyword=editorSearchKeyword.value.text,
                mergeMode=editorMergeMode.value,
                showLineNum=editorShowLineNum,
                lineNumFontSize=editorLineNumFontSize,
                fontSize=editorFontSize,
                undoStack = undoStack,
                patchMode = editorPatchMode.value,
                tabIndentSpacesCount = tabIndentSpacesCount,
            )
        }
        if(appPaused.value.not()) {
            FileChangeListener(
                state = fileChangeListenerState,
                context = activityContext,
                path = editorPageShowingFilePath.value.ioPath,
            ) {
                val printFilePath = "filePath = '${editorPageShowingFilePath.value.ioPath}'"
                if(
                    showBackFromExternalAppAskReloadDialog.value.not()
                    && isPreviewModeOn.value.not() 
                    && editorPageShowingFilePath.value.isNotBlank()
                    && isEdited.value.not()  
                    && isSaving.value.not()  
                ) {
                    MyLog.d(TAG, "file is changed by external, will reload it, $printFilePath")
                    doJobThenOffLoading {
                        undoStack.clearRedoStackThenPushToUndoStack(editorPageTextEditorState.value, force = true)
                    }
                    val force = false
                    reloadFile(force)
                }else {
                    MyLog.d(TAG, "file is changed by external, but currently app was modified file also, so will not reload it, $printFilePath")
                }
            }
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        appPaused.value = true
        val requireShowMsgToUser = true
        val requireBackupContent = true
        val requireBackupFile = true
        val contentSnapshotFlag = SnapshotFileFlag.editor_content_OnPause
        val fileSnapshotFlag = SnapshotFileFlag.editor_file_OnPause
        doSimpleSafeFastSaveInCoroutine(
            requireShowMsgToUser,
            requireBackupContent,
            requireBackupFile,
            contentSnapshotFlag,
            fileSnapshotFlag
        )
        MyLog.d(TAG, "#Lifecycle.Event.ON_PAUSE: will save file: ${editorPageShowingFilePath.value}")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        appPaused.value = false
        codeEditor.value.updatePlScopeThenAnalyze()
        doActIfIsExpectLifeCycle(MainActivityLifeCycle.ON_RESUME) {
            if(isPreviewModeOn.value) {
                val force = false
                refreshPreviewPage(previewPath, force)
                MyLog.d(TAG, "#Lifecycle.Event.ON_RESUME: Preview Mode is On, will reload file: $previewPath")
            }
        }
        MyLog.d(TAG, "#Lifecycle.Event.ON_RESUME: called")
    }
    LaunchedEffect(needRefreshEditorPage.value) {
        doJobThenOffLoading {
            try {
                doActWithLockIfFree(loadLock, "EditorInnerPage#Init#${needRefreshEditorPage.value}#${editorPageShowingFilePath.value.ioPath}") {
                    doInit(
                        editorCharset = editorCharset,
                        lastSavedFieldsId = lastSavedFieldsId,
                        codeEditor = codeEditor.value,
                        resetLastCursorAtColumn = resetLastCursorAtColumn,
                        requirePreviewScrollToEditorCurPos = requirePreviewScrollToEditorCurPos,
                        ignoreFocusOnce = ignoreFocusOnce,
                        isPreviewModeOn = isPreviewModeOn,
                        previewPath = previewPath,
                        updatePreviewPath = updatePreviewPath,
                        keepPreviewStack = keepPreviewNavStackOnce,
                        previewNavStack = previewNavStack,
                        activityContext = activityContext,
                        editorPageShowingFilePath = editorPageShowingFilePath,
                        editorPageShowingFileIsReady = editorPageShowingFileIsReady,
                        editorPageClearShowingFileErrWhenLoading = editorPageClearShowingFileErrWhenLoading,
                        editorPageTextEditorState = editorPageTextEditorState,
                        unknownErrStrRes = unknownErrStrRes,
                        editorPageSetShowingFileErrWhenLoading = editorPageSetShowingFileErrWhenLoading,
                        pageRequest = requestFromParent,
                        editorPageShowingFileDto = editorPageShowingFileDto,
                        isSubPage = isSubPageMode,
                        editorLastScrollEvent = editorLastScrollEvent,
                        editorPageIsInitDone = editorPageIsInitDone,
                        isEdited = isEdited,
                        isSaving = isSaving,
                        isContentSnapshoted = editorPageIsContentSnapshoted,
                        undoStack = undoStack,
                        hasError = hasError
                    )
                }
            } catch (e:Exception) {
                Msg.requireShowLongDuration("init Editor err: ${e.localizedMessage}")
                MyLog.e(TAG, "#init Editor page err: ${e.stackTraceToString()}")
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            editorPageShowingFileIsReady.value = false
            if(saveOnDispose) {
                doSaveInCoroutine()
            }
            saveLastOpenPath(editorPageShowingFilePath.value.ioPath)
        }
    }
}
private suspend fun doInit(
    editorCharset: MutableState<String?>,
    lastSavedFieldsId: MutableState<String>,
    codeEditor: MyCodeEditor,
    resetLastCursorAtColumn: ()->Unit,
    requirePreviewScrollToEditorCurPos: MutableState<Boolean>,
    ignoreFocusOnce: MutableState<Boolean>,
    isPreviewModeOn: MutableState<Boolean>,
    previewPath: String,
    updatePreviewPath: (String)->Unit,
    keepPreviewStack:MutableState<Boolean>,
    previewNavStack:CustomStateSaveable<EditorPreviewNavStack>,
    activityContext:Context,
    editorPageShowingFilePath: MutableState<FilePath>,
    editorPageShowingFileIsReady: MutableState<Boolean>,
    editorPageClearShowingFileErrWhenLoading: () -> Unit,
    editorPageTextEditorState: CustomStateSaveable<TextEditorState>,
    unknownErrStrRes: String,
    editorPageSetShowingFileErrWhenLoading: (errMsg: String) -> Unit,
    pageRequest:MutableState<String>,
    editorPageShowingFileDto: CustomStateSaveable<FileSimpleDto>,
    isSubPage: Boolean,
    editorLastScrollEvent:CustomStateSaveable<ScrollEvent?>,
    editorPageIsInitDone:MutableState<Boolean>,
    isEdited:MutableState<Boolean>,
    isSaving:MutableState<Boolean>,
    isContentSnapshoted:MutableState<Boolean>,
    undoStack:UndoStack,
    hasError:()->Boolean,
) {
    MyLog.d(TAG, "#doInit: editorPageShowingFilePath=${editorPageShowingFilePath.value}")
    if (!editorPageShowingFileIsReady.value) {  
        val editorPageShowingFilePath = editorPageShowingFilePath.value
        val requireOpenFilePath = editorPageShowingFilePath.ioPath
        if (requireOpenFilePath.isBlank()) {
            return
        }
        try {
            undoStack.reset(requireOpenFilePath, force = false)
            codeEditor.reset(FuckSafFile(AppModel.realAppContext, FilePath(requireOpenFilePath)), force = false)
            val file = FuckSafFile(activityContext, editorPageShowingFilePath)
            if(hasError().not()) {
                val newDto = FileSimpleDto.genByFile(FuckSafFile(activityContext, FilePath(requireOpenFilePath)))
                val oldDto = editorPageShowingFileDto.value
                if (newDto == oldDto) {
                    MyLog.d(TAG,"EditorInnerPage#loadFile: file may not changed, skip reload, file path is '${requireOpenFilePath}'")
                    editorPageShowingFileIsReady.value = true
                    return
                }
            }
            editorPageClearShowingFileErrWhenLoading()
            if(!file.isActuallyReadable()) {
                if(editorPageTextEditorState.value.contentIsEmpty().not() && !isContentSnapshoted.value) {
                    MyLog.w(TAG, "#loadFile: file doesn't exist anymore, but content is not empty, will create snapshot for content")
                    val fileName = file.name
                    val editorState = editorPageTextEditorState.value
                    doJobThenOffLoading {
                        val snapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                            srcFileName = fileName,
                            fileContent = null,
                            editorState = editorState,
                            trueUseContentFalseUseEditorState = false,
                            flag = SnapshotFileFlag.editor_content_FileNonExists_Backup
                        )
                        if (snapRet.hasError()) {
                            MyLog.e(TAG, "#loadFile: create content snapshot for '$requireOpenFilePath' err: ${snapRet.msg}")
                            Msg.requireShowLongDuration("save content snapshot for '$fileName' err: " + snapRet.msg)
                        }else {
                            isContentSnapshoted.value=true
                        }
                    }
                }
                throw RuntimeException(activityContext.getString(R.string.err_file_doesnt_exist_anymore))
            }
            val settings = SettingsUtil.getSettingsSnapshot()
            val maxSizeLimit = settings.editor.maxFileSizeLimit
            if (isFileSizeOverLimit(file.length(), limit = maxSizeLimit)) {
                throw RuntimeException(activityContext.getString(R.string.err_doesnt_support_open_file_over_limit) + "(" + getHumanReadableSizeStr(maxSizeLimit) + ")")
            }
            MyLog.d(TAG,"EditorInnerPage#loadFile: will load file '${requireOpenFilePath}'")
            isEdited.value=false
            isSaving.value=false
            editorPageIsInitDone.value=false
            editorLastScrollEvent.value=null
            ignoreFocusOnce.value = false
            resetLastCursorAtColumn()
            if(soraEditorComposeTestPassed) {
            }else {
                if (editorCharset.value == null) {
                    editorCharset.value = file.detectEncoding()
                }
                val newState = TextEditorState(
                    codeEditor = codeEditor,
                    fields = TextEditorState.fuckSafFileToFields(file, editorCharset.value),
                    isContentEdited = isEdited,
                    editorPageIsContentSnapshoted = isContentSnapshoted,
                    isMultipleSelectionMode = false,
                    focusingLineIdx = null,
                    onChanged = getEditorStateOnChange(
                        editorPageTextEditorState = editorPageTextEditorState,
                        lastSavedFieldsId = lastSavedFieldsId,
                        undoStack = undoStack,
                        resetLastCursorAtColumn = resetLastCursorAtColumn,
                    ),
                )
                editorPageTextEditorState.value = newState
                codeEditor.lineBreak = file.detectLineBreak(editorCharset.value)
                codeEditor.updatePlScopeThenAnalyze()
                lastSavedFieldsId.value = newState.fieldsId
            }
            isContentSnapshoted.value=false
            editorPageShowingFileIsReady.value = true
            editorPageShowingFileDto.value = FileSimpleDto.genByFile(file)
            FileOpenHistoryMan.touch(requireOpenFilePath)
            val keepPreviewStackOnce = keepPreviewStack.value
            keepPreviewStack.value = false  
            val keepPreviewStack = Unit 
            if(previewNavStack.value.editingPath != requireOpenFilePath && keepPreviewStackOnce.not()) {
                previewNavStack.value.reset(requireOpenFilePath)
                requirePreviewScrollToEditorCurPos.value = true
            }
            if(previewPath.isBlank()) {
                var newPreviewPath = previewNavStack.value.getCurrent().path
                if(newPreviewPath.isBlank()) {
                    newPreviewPath = requireOpenFilePath
                }
                updatePreviewPath(newPreviewPath)
            }
        } catch (e: Exception) {
            editorPageShowingFileIsReady.value = false
            editorPageSetShowingFileErrWhenLoading(e.localizedMessage ?: unknownErrStrRes)
            MyLog.e(TAG, "EditorInnerPage#loadFile(): " + e.stackTraceToString())
        }
    }
}
@Composable
private fun getBackHandler(
    previewNavBack: ()->Unit,
    inRecentFilesPage: MutableState<Boolean>,
    recentFileListSelectionMode: MutableState<Boolean>,
    quitRecentListSelectionMode: ()->Unit,
    editorFilterRecentListOn: MutableState<Boolean>,
    editorRecentFilesQuitFilterMode:()->Unit,
    isPreviewModeOn:MutableState<Boolean>,
    quitPreviewMode:()->Unit,
    activityContext: Context,
    textEditorState: CustomStateSaveable<TextEditorState>,
    isSubPage: Boolean,
    isEdited: MutableState<Boolean>,
    readOnlyMode: MutableState<Boolean>,
    doSaveNoCoroutine:suspend ()->Unit,
    searchMode:MutableState<Boolean>,
    needAndReadyDoSave:()->Boolean,
    naviUp:()->Unit,
    adjustFontSizeMode:MutableState<Boolean>,
    adjustLineNumFontSizeMode:MutableState<Boolean>,
    saveFontSizeAndQuitAdjust:()->Unit,
    saveLineNumFontSizeAndQuitAdjust:()->Unit,
    exitApp: () -> Unit,
    openDrawer:()->Unit,
    requestFromParent: MutableState<String>
): () -> Unit {
    val backStartSec = rememberSaveable { mutableLongStateOf( 0) }
    val pressBackAgainForExitText = stringResource(R.string.press_back_again_to_exit);
    val showTextAndUpdateTimeForPressBackBtn = {
        openDrawer()
        showToast(activityContext, pressBackAgainForExitText, Toast.LENGTH_SHORT)
        backStartSec.longValue = getSecFromTime() + Cons.pressBackDoubleTimesInThisSecWillExit
    }
    val backHandlerOnBack = {
        if(inRecentFilesPage.value && recentFileListSelectionMode.value) {
            quitRecentListSelectionMode()
        }else if(inRecentFilesPage.value && editorFilterRecentListOn.value) {
            editorRecentFilesQuitFilterMode()
        }else if(isPreviewModeOn.value) {
            previewNavBack()
        }else if(textEditorState.value.isMultipleSelectionMode) {  
            requestFromParent.value = PageRequest.editorQuitSelectionMode
        }else if(searchMode.value){
            searchMode.value = false
        }else if(adjustFontSizeMode.value){
            saveFontSizeAndQuitAdjust()
        }else if(adjustLineNumFontSizeMode.value){
            saveLineNumFontSizeAndQuitAdjust()
        }else {  
            doJobThenOffLoading {
                if(needAndReadyDoSave()) {  
                    doSaveNoCoroutine()
                    return@doJobThenOffLoading
                }
                if(isSubPage) {  
                    withMainContext {
                        naviUp()
                    }
                }else {  
                    if (backStartSec.longValue > 0 && getSecFromTime() <= backStartSec.longValue) {  
                        exitApp()
                    } else {
                        withMainContext {
                            showTextAndUpdateTimeForPressBackBtn()
                        }
                    }
                }
            }
        }
        Unit
    }
    return backHandlerOnBack
}
