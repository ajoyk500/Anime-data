package com.akcreation.gitsilent.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.SmallFab
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.dto.FileDetail
import com.akcreation.gitsilent.dto.FileSimpleDto
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.fileeditor.texteditor.view.ScrollEvent
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.editor.FileDetailListActions
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.EditorInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.EditorPageActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.EditorTitle
import com.akcreation.gitsilent.screen.functions.getInitTextEditorState
import com.akcreation.gitsilent.screen.shared.EditorPreviewNavStack
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.MyCodeEditor
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.cache.NaviCache
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.generateRandomString
import com.akcreation.gitsilent.utils.state.mutableCustomBoxOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

private const val TAG = "SubPageEditor"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPageEditor(
    goToLine:Int,  
    initMergeMode:Boolean,
    initReadOnly:Boolean,
    editorPageLastFilePath:MutableState<String>,
    filePathKey:String,
    naviUp:()->Unit
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val navController = AppModel.navController
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current  
    val scope = rememberCoroutineScope()
    val inDarkTheme = Theme.inDarkTheme
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val editorPageShowingFilePath = rememberSaveable { mutableStateOf(FilePath(NaviCache.getByType<String>(filePathKey) ?: ""))} 
    val editorPageShowingFileIsReady = rememberSaveable { mutableStateOf(false)} 
    val editorPageIsEdited = rememberSaveable { mutableStateOf(false)}
    val editorPageIsContentSnapshoted = rememberSaveable{mutableStateOf(false)}  
    val editorPageTextEditorState = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "editorPageTextEditorState",
        initValue = getInitTextEditorState()
    )
    val needRefreshEditorPage = rememberSaveable { mutableStateOf("")}
    val editorPageIsSaving = rememberSaveable { mutableStateOf(false)}
    val showReloadDialog = rememberSaveable { mutableStateOf(false)}
    val editorPageShowingFileDto = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageShowingFileDto",FileSimpleDto() )
    val editorPageSnapshotedFileInfo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageSnapshotedFileInfo",FileSimpleDto() )
    val editorShowUndoRedo = rememberSaveable{mutableStateOf(settings.editor.showUndoRedo)}
    val editorUndoStack = mutableCustomStateOf(stateKeyTag, "editorUndoStack") { UndoStack("") }
    val editorCharset = rememberSaveable { mutableStateOf<String?>(null) }
    val editorPlScope = rememberSaveable { mutableStateOf(PLScope.AUTO) }
    val codeEditor = mutableCustomStateOf(stateKeyTag, "codeEditor") {
        MyCodeEditor(
            editorState = editorPageTextEditorState,
            undoStack = editorUndoStack,
            plScope = editorPlScope,
            editorCharset = editorCharset,
        )
    }
    val naviUp = {
        codeEditor.value.releaseAndClearUndoStack()
        naviUp()
    }
    val editorPageLastScrollEvent = mutableCustomStateOf<ScrollEvent?>(stateKeyTag, "editorPageLastScrollEvent") { null }  
    val editorPageLazyListState = rememberLazyListState()
    val editorPageIsInitDone = rememberSaveable{mutableStateOf(false)}  
    val editorPageSearchMode = rememberSaveable{mutableStateOf(false)}
    val editorPageSearchKeyword = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageSearchKeyword", TextFieldValue("") )
    val editorReadOnlyMode = rememberSaveable{mutableStateOf(initReadOnly)}
    val editorPageMergeMode = rememberSaveable{mutableStateOf(initMergeMode)}
    val editorPagePatchMode = rememberSaveable(settings.editor.patchModeOn) { mutableStateOf(settings.editor.patchModeOn) }
    val editorPageRequestFromParent = rememberSaveable { mutableStateOf("")}
    val requireEditorScrollToPreviewCurPos = rememberSaveable { mutableStateOf(false) }
    val requirePreviewScrollToEditorCurPos = rememberSaveable { mutableStateOf(false) }
    val editorPreviewPageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val editorPreviewLastScrollPosition = rememberSaveable { mutableStateOf(0) }
    val editorIsPreviewModeOn = rememberSaveable { mutableStateOf(false) }
    val editorMdText = rememberSaveable { mutableStateOf("") }
    val editorBasePath = rememberSaveable { mutableStateOf("") }
    val (editorPreviewPath, updatePreviewPath_Internal) = rememberSaveable { mutableStateOf("") }
    val editorPreviewPathChanged = rememberSaveable { mutableStateOf("") }  
    val updatePreviewPath = { newPath:String ->
        updatePreviewPath_Internal(newPath)
        editorPreviewPathChanged.value = generateRandomString()
    }
    val editorPreviewNavStack = mutableCustomStateOf(stateKeyTag, "editorPreviewNavStack") { EditorPreviewNavStack("") }
    val editorPagePreviewLoading = rememberSaveable { mutableStateOf(false) }
    val editorQuitPreviewMode = {
        editorBasePath.value = ""
        editorMdText.value = ""
        editorIsPreviewModeOn.value = false
        editorPageRequestFromParent.value = PageRequest.reloadIfChanged
    }
    val editorInitPreviewMode = {
        editorPageRequestFromParent.value = PageRequest.requireInitPreviewFromSubEditor
    }
    val editorPreviewFileDto = mutableCustomStateOf(stateKeyTag, "editorPreviewFileDto") { FileSimpleDto() }
    val editorDisableSoftKb = rememberSaveable { mutableStateOf(settings.editor.disableSoftwareKeyboard) }
    val editorRecentFileList = mutableCustomStateListOf(stateKeyTag, "recentFileList") { listOf<FileDetail>() }
    val editorSelectedRecentFileList = mutableCustomStateListOf(stateKeyTag, "editorSelectedRecentFileList") { listOf<FileDetail>() }
    val editorRecentFileListSelectionMode = rememberSaveable { mutableStateOf(false) }
    val editorRecentListState = rememberLazyStaggeredGridState()
    val editorInRecentFilesPage = rememberSaveable { mutableStateOf(false) }
    val ignoreFocusOnce = rememberSaveable { mutableStateOf(false) }
    val settingsTmp = settings  
    val editorShowLineNum = rememberSaveable{mutableStateOf(settingsTmp.editor.showLineNum)}
    val editorLineNumFontSize = rememberSaveable { mutableIntStateOf(settingsTmp.editor.lineNumFontSize)}
    val editorFontSize = rememberSaveable { mutableIntStateOf(settingsTmp.editor.fontSize)}
    val editorAdjustFontSizeMode = rememberSaveable{mutableStateOf(false)}
    val editorAdjustLineNumFontSizeMode = rememberSaveable{mutableStateOf(false)}
    val editorLastSavedLineNumFontSize = rememberSaveable { mutableIntStateOf(editorLineNumFontSize.intValue) } 
    val editorLastSavedFontSize = rememberSaveable { mutableIntStateOf(editorFontSize.intValue)}
    val editorOpenFileErr = rememberSaveable{mutableStateOf(false)}
    val editorLoadLock = mutableCustomBoxOf(stateKeyTag, "editorLoadLock") { Mutex() }.value
    val showCloseDialog = rememberSaveable { mutableStateOf(false)}
    val closeDialogCallback = mutableCustomStateOf<(Boolean)->Unit>(
        keyTag = stateKeyTag,
        keyName = "closeDialogCallback",
        initValue = { requireSave:Boolean -> Unit}
    )
    val initLoadingText = activityContext.getString(R.string.loading)
    val loadingText = rememberSaveable { mutableStateOf(initLoadingText)}
    val isLoading = rememberSaveable { mutableStateOf(false)}
    val loadingOn = {msg:String ->
        loadingText.value=msg
        isLoading.value=true
    }
    val loadingOff = {
        isLoading.value=false
        loadingText.value=initLoadingText
    }
    val lastSavedFieldsId = rememberSaveable { mutableStateOf("") }
    val doSave:suspend ()->Unit = FsUtils.getDoSaveForEditor(
        editorPageShowingFilePath = editorPageShowingFilePath,
        editorPageLoadingOn = loadingOn,
        editorPageLoadingOff = loadingOff,
        activityContext = activityContext,
        editorPageIsSaving = editorPageIsSaving,
        needRefreshEditorPage = needRefreshEditorPage,
        editorPageTextEditorState = editorPageTextEditorState,
        pageTag = TAG,
        editorPageIsEdited = editorPageIsEdited,
        requestFromParent = editorPageRequestFromParent,
        editorPageFileDto = editorPageShowingFileDto,
        isSubPageMode = true,
        isContentSnapshoted =editorPageIsContentSnapshoted,
        snapshotedFileInfo = editorPageSnapshotedFileInfo,
        lastSavedFieldsId = lastSavedFieldsId,
    )
    val editorFilterRecentListState = rememberLazyStaggeredGridState()
    val editorFilterRecentList = mutableCustomStateListOf(stateKeyTag, "editorFilterRecentList") { listOf<FileDetail>() }
    val editorFilterRecentListOn = rememberSaveable { mutableStateOf(false) }
    val editorEnableRecentListFilter = rememberSaveable { mutableStateOf(false) }
    val editorFilterRecentListKeyword = mutableCustomStateOf(stateKeyTag, "editorFilterRecentListKeyword") { TextFieldValue("") }
    val editorFilterRecentListLastSearchKeyword = rememberSaveable { mutableStateOf("") }
    val editorFilterRecentListResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val editorFilterRecentListSearching = rememberSaveable { mutableStateOf(false) }
    val editorFilterRecentListSearchToken = rememberSaveable { mutableStateOf("") }
    val editorFilterResetSearchValues = {
        editorFilterRecentListSearching.value = false
        editorFilterRecentListSearchToken.value = ""
        editorFilterRecentListLastSearchKeyword.value = ""
    }
    val editorInitRecentFilesFilterMode = {
        editorFilterRecentListKeyword.value = TextFieldValue("")
        editorFilterRecentListOn.value = true
    }
    val editorRecentFilesQuitFilterMode = {
        editorFilterResetSearchValues()
        editorFilterRecentListOn.value = false
    }
    val editorRecentListLastScrollPosition = rememberSaveable { mutableStateOf(0) }
    val editorRecentListFilterLastScrollPosition = rememberSaveable { mutableStateOf(0) }
    val getActuallyRecentFilesList = {
        if(editorEnableRecentListFilter.value) editorFilterRecentList.value else editorRecentFileList.value
    }
    val getActuallyRecentFilesListState = {
        if(editorEnableRecentListFilter.value) editorFilterRecentListState else editorRecentListState
    }
    val getActuallyRecentFilesListLastPosition = {
        if(editorEnableRecentListFilter.value) editorRecentListFilterLastScrollPosition else editorRecentListLastScrollPosition
    }
    val editorRecentListScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val editorNeedSave = { editorPageShowingFileIsReady.value && editorPageIsEdited.value && !editorPageIsSaving.value && !editorReadOnlyMode.value && lastSavedFieldsId.value != editorPageTextEditorState.value.fieldsId }
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    EditorTitle(
                        disableSoftKb = editorDisableSoftKb,
                        recentFileListIsEmpty = editorRecentFileList.value.isEmpty(),
                        recentFileListFilterModeOn = editorFilterRecentListOn.value,
                        recentListFilterKeyword = editorFilterRecentListKeyword,
                        getActuallyRecentFilesListState = getActuallyRecentFilesListState,
                        getActuallyRecentFilesListLastPosition = getActuallyRecentFilesListLastPosition,
                        patchModeOn = editorPagePatchMode,
                        previewNavStack = editorPreviewNavStack.value,
                        previewingPath = editorPreviewPath,
                        isPreviewModeOn = editorIsPreviewModeOn.value,
                        previewLastScrollPosition = editorPreviewLastScrollPosition,
                        scope = scope,
                        editorPageShowingFilePath = editorPageShowingFilePath,
                        editorPageRequestFromParent = editorPageRequestFromParent,
                        editorSearchMode = editorPageSearchMode.value,
                        editorSearchKeyword = editorPageSearchKeyword,
                        editorPageMergeMode = editorPageMergeMode,
                        readOnly = editorReadOnlyMode,
                        editorPageShowingFileIsReady = editorPageShowingFileIsReady,
                        isSaving = editorPageIsSaving,
                        isEdited = editorPageIsEdited,
                        showReloadDialog = showReloadDialog,
                        showCloseDialog = showCloseDialog,
                        editorNeedSave = editorNeedSave,
                    )
                },
                navigationIcon = {
                    if(editorIsPreviewModeOn.value || editorPageSearchMode.value
                        || editorAdjustFontSizeMode.value || editorAdjustLineNumFontSizeMode.value
                        || (editorInRecentFilesPage.value && editorFilterRecentListOn.value)
                    ) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),
                        ) {
                            if(editorIsPreviewModeOn.value) {
                                editorQuitPreviewMode()
                            }else if(editorPageSearchMode.value){
                                editorPageSearchMode.value = false
                            }else if(editorAdjustFontSizeMode.value) {
                                editorPageRequestFromParent.value = PageRequest.requireSaveFontSizeAndQuitAdjust
                            }else if(editorAdjustLineNumFontSizeMode.value) {
                                editorPageRequestFromParent.value = PageRequest.requireSaveLineNumFontSizeAndQuitAdjust
                            }else if(editorInRecentFilesPage.value && editorFilterRecentListOn.value) {
                                editorRecentFilesQuitFilterMode()
                            }
                        }
                    }else {
                        val (tooltipText, icon, iconContentDesc) = if(editorNeedSave()) {
                            Triple(stringResource(R.string.save), Icons.Filled.Save, stringResource(R.string.save))
                        }else {
                            Triple(stringResource(R.string.back), Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }
                        LongPressAbleIconBtn(
                            tooltipText = tooltipText,
                            icon = icon,
                            iconContentDesc = iconContentDesc,
                        ) {
                            doJobThenOffLoading {
                                if(editorNeedSave()) {
                                    editorPageRequestFromParent.value = PageRequest.requireSave
                                    return@doJobThenOffLoading
                                }
                                withContext(Dispatchers.Main) {
                                    naviUp()
                                }
                            }
                        }
                    }
                },
                actions = {
                    if(!editorOpenFileErr.value) {
                        val notOpenFile = !editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isBlank()
                        if(notOpenFile && editorRecentFileList.value.isNotEmpty()) {
                            FileDetailListActions(
                                request = editorPageRequestFromParent,
                                filterModeOn = editorFilterRecentListOn.value,
                                initFilterMode = editorInitRecentFilesFilterMode,
                            )
                        }else  {
                            EditorPageActions(
                                disableSoftKb = editorDisableSoftKb,
                                requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
                                initPreviewMode = editorInitPreviewMode,
                                previewNavStack = editorPreviewNavStack.value,
                                previewPath = editorPreviewPath,
                                previewPathChanged = editorPreviewPathChanged.value,
                                isPreviewModeOn = editorIsPreviewModeOn.value,
                                editorPageShowingFilePath = editorPageShowingFilePath,
                                editorPageShowingFileIsReady = editorPageShowingFileIsReady,
                                needRefreshEditorPage = needRefreshEditorPage,
                                editorPageTextEditorState = editorPageTextEditorState,
                                isSaving = editorPageIsSaving,
                                isEdited = editorPageIsEdited,
                                showReloadDialog = showReloadDialog,
                                showCloseDialog = showCloseDialog,
                                closeDialogCallback=closeDialogCallback,
                                doSave = doSave,
                                loadingOn = loadingOn,
                                loadingOff = loadingOff,
                                editorPageRequest = editorPageRequestFromParent,
                                editorPageSearchMode = editorPageSearchMode,
                                editorPageMergeMode=editorPageMergeMode,
                                editorPagePatchMode=editorPagePatchMode,
                                readOnlyMode = editorReadOnlyMode,
                                editorSearchKeyword = editorPageSearchKeyword.value.text,
                                isSubPageMode=true,
                                fontSize=editorFontSize,
                                lineNumFontSize=editorLineNumFontSize,
                                adjustFontSizeMode=editorAdjustFontSizeMode,
                                adjustLineNumFontSizeMode=editorAdjustLineNumFontSizeMode,
                                showLineNum = editorShowLineNum,
                                undoStack = editorUndoStack.value,
                                showUndoRedo = editorShowUndoRedo,
                                editorNeedSave = editorNeedSave,
                            )
                        }
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(editorIsPreviewModeOn.value && editorPreviewPageScrolled.value) {
                GoToTopAndGoToBottomFab(
                    scope = scope,
                    listState = runBlocking { editorPreviewNavStack.value.getCurrentScrollState() },
                    listLastPosition = editorPreviewLastScrollPosition,
                    showFab = editorPreviewPageScrolled
                )
            }else if(editorNeedSave()) {
                SmallFab(
                    modifier= MyStyleKt.Fab.getFabModifierForEditor(editorPageTextEditorState.value.isMultipleSelectionMode, UIHelper.isPortrait()),
                    icon = Icons.Filled.Save, iconDesc = stringResource(id = R.string.save)
                ) {
                    editorPageRequestFromParent.value = PageRequest.requireSave
                }
            }else if(editorInRecentFilesPage.value && editorRecentListScrolled.value) {
                GoToTopAndGoToBottomFab(
                    filterModeOn = editorEnableRecentListFilter.value,
                    scope = scope,
                    filterListState = editorFilterRecentListState,
                    listState = editorRecentListState,
                    filterListLastPosition = editorRecentListFilterLastScrollPosition,
                    listLastPosition = editorRecentListLastScrollPosition,
                    showFab = editorRecentListScrolled,
                    listSize = getActuallyRecentFilesList().size,
                )
            }
        }
    ) { contentPadding ->
        EditorInnerPage(
            stateKeyTag = stateKeyTag,
            editorCharset = editorCharset,
            lastSavedFieldsId = lastSavedFieldsId,
            codeEditor = codeEditor,
            plScope = editorPlScope,
            disableSoftKb = editorDisableSoftKb,
            editorRecentListScrolled = editorRecentListScrolled,
            recentFileList = editorRecentFileList,
            selectedRecentFileList = editorSelectedRecentFileList,
            recentFileListSelectionMode = editorRecentFileListSelectionMode,
            recentListState = editorRecentListState,
            inRecentFilesPage = editorInRecentFilesPage,
            editorFilterRecentListState = editorFilterRecentListState,
            editorFilterRecentList = editorFilterRecentList.value,
            editorFilterRecentListOn = editorFilterRecentListOn,
            editorEnableRecentListFilter = editorEnableRecentListFilter,
            editorFilterRecentListKeyword = editorFilterRecentListKeyword,
            editorFilterRecentListLastSearchKeyword = editorFilterRecentListLastSearchKeyword,
            editorFilterRecentListResultNeedRefresh = editorFilterRecentListResultNeedRefresh,
            editorFilterRecentListSearching = editorFilterRecentListSearching,
            editorFilterRecentListSearchToken = editorFilterRecentListSearchToken,
            editorFilterResetSearchValues = editorFilterResetSearchValues,
            editorRecentFilesQuitFilterMode = editorRecentFilesQuitFilterMode,
            ignoreFocusOnce = ignoreFocusOnce,
            previewLoading = editorPagePreviewLoading,
            editorPreviewFileDto = editorPreviewFileDto,
            requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
            requirePreviewScrollToEditorCurPos = requirePreviewScrollToEditorCurPos,
            previewPageScrolled = editorPreviewPageScrolled,
            previewPath = editorPreviewPath,
            updatePreviewPath = updatePreviewPath,
            previewNavStack = editorPreviewNavStack,
            isPreviewModeOn = editorIsPreviewModeOn,
            mdText = editorMdText,
            basePath = editorBasePath,
            quitPreviewMode = editorQuitPreviewMode,
            initPreviewMode = editorInitPreviewMode,
            contentPadding = contentPadding,
            currentHomeScreen = remember { mutableIntStateOf(Cons.selectedItem_Repos)},
            editorPageShowingFilePath=editorPageShowingFilePath,
            editorPageShowingFileIsReady=editorPageShowingFileIsReady,
            editorPageTextEditorState=editorPageTextEditorState,
            needRefreshEditorPage=needRefreshEditorPage,
            isSaving = editorPageIsSaving,
            isEdited = editorPageIsEdited,
            showReloadDialog=showReloadDialog,
            isSubPageMode=true,
            showCloseDialog=showCloseDialog,
            closeDialogCallback=closeDialogCallback,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            saveOnDispose = false,  
            doSave = doSave,  
            naviUp=naviUp,
            requestFromParent = editorPageRequestFromParent,
            editorPageShowingFileDto = editorPageShowingFileDto,
            lastFilePath = editorPageLastFilePath,
            editorLastScrollEvent = editorPageLastScrollEvent,
            editorListState = editorPageLazyListState,
            editorPageIsInitDone = editorPageIsInitDone,
            editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
            goToFilesPage = {},  
            goToLine=goToLine,
            editorSearchMode = editorPageSearchMode,
            editorSearchKeyword = editorPageSearchKeyword,
            readOnlyMode = editorReadOnlyMode,
            editorMergeMode = editorPageMergeMode,
            editorPatchMode = editorPagePatchMode,
            editorShowLineNum=editorShowLineNum,
            editorLineNumFontSize=editorLineNumFontSize,
            editorFontSize=editorFontSize,
            editorAdjustLineNumFontSizeMode = editorAdjustLineNumFontSizeMode,
            editorAdjustFontSizeMode = editorAdjustFontSizeMode,
            editorLastSavedLineNumFontSize = editorLastSavedLineNumFontSize,
            editorLastSavedFontSize = editorLastSavedFontSize,
            openDrawer = {}, 
            editorOpenFileErr = editorOpenFileErr,
            undoStack = editorUndoStack.value,
            loadLock = editorLoadLock
        )
    }
}
