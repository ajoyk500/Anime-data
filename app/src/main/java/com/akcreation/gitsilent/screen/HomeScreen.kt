package com.akcreation.gitsilent.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.akcreation.gitsilent.compose.ChangelogDialog
import com.akcreation.gitsilent.compose.ConfirmDialog3
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SmallFab
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.IntentCons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.constants.SingleSendHandleMethod
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dto.FileDetail
import com.akcreation.gitsilent.dto.FileItemDto
import com.akcreation.gitsilent.dto.FileSimpleDto
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.fileeditor.texteditor.view.ScrollEvent
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.editor.FileDetailListActions
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.AboutInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.AutomationInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.ChangeListInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.EditorInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.FilesInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.RepoInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.ServiceInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.SettingsInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.SubscriptionPage
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.ChangeListPageActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.EditorPageActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.FilesPageActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.RefreshActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.RepoPageActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.SubscriptionActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.drawer.drawerContent
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.ChangeListTitle
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.EditorTitle
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.FilesTitle
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.ReposTitle
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.ScrollableTitle
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.SimpleTitle
import com.akcreation.gitsilent.screen.functions.ChangeListFunctions
import com.akcreation.gitsilent.screen.functions.getFilesGoToPath
import com.akcreation.gitsilent.screen.functions.getInitTextEditorState
import com.akcreation.gitsilent.screen.shared.EditorPreviewNavStack
import com.akcreation.gitsilent.screen.shared.FileChooserType
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.screen.shared.IntentHandler
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsCons
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.MyCodeEditor
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.generateRandomString
import com.akcreation.gitsilent.utils.pref.PrefMan
import com.akcreation.gitsilent.utils.saf.SafUtil
import com.akcreation.gitsilent.utils.state.mutableCustomBoxOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository.StateT
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "HomeScreen"
private const val stateKeyTag = TAG
private val refreshLock = Mutex()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    drawerState: DrawerState,
    currentHomeScreen: MutableIntState,
    repoPageListState: LazyListState,
    editorPageLastFilePath: MutableState<String>,
) {
    val exitApp = AppModel.exitApp
    val navController = AppModel.navController
    val scope = rememberCoroutineScope()
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current  
    val inDarkTheme = Theme.inDarkTheme
    val allRepoParentDir = AppModel.allRepoParentDir
    val settingsSnapshot = remember { mutableStateOf(SettingsUtil.getSettingsSnapshot()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val repoPageCurRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "repoPageCurRepo", initValue = RepoEntity(id=""))  
    val repoPageCurRepoIndex = rememberSaveable { mutableIntStateOf(-1)}
    val repoPageRepoList = mutableCustomStateListOf(stateKeyTag, "repoPageRepoList", listOf<RepoEntity>())
    val changeListRefreshRequiredByParentPage = rememberSaveable { SharedState.homeChangeList_Refresh }
    val changeListRequireRefreshFromParentPage = { whichRepoRequestRefresh:RepoEntity ->
        ChangeListFunctions.changeListDoRefresh(changeListRefreshRequiredByParentPage, whichRepoRequestRefresh)
    }
    val changeListCurRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "changeListCurRepo", initValue = RepoEntity(id=""))  
    val changeListCachedEditorPath = mutableCustomBoxOf(stateKeyTag, "changeListCachedEditorPath") { "" }
    val changeListIsFileSelectionMode = rememberSaveable { mutableStateOf(false)}
    val changeListPageNoRepo = rememberSaveable { mutableStateOf(false)}
    val changeListPageHasNoConflictItems = rememberSaveable { mutableStateOf(false)}
    val changeListPageRebaseCurOfAll = rememberSaveable { mutableStateOf("")}
    val changeListNaviTarget = rememberSaveable { mutableStateOf(Cons.ChangeListNaviTarget_InitValue)}
    val changeListPageFilterKeyWord = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "changeListPageFilterKeyWord",
        initValue = TextFieldValue("")
    )
    val changeListPageFilterModeOn = rememberSaveable { mutableStateOf(false) }
    val repoPageFilterKeyWord =mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "repoPageFilterKeyWord",
        initValue = TextFieldValue("")
    )
    val repoPageFilterModeOn = rememberSaveable { mutableStateOf(false) }
    val repoPageShowImportRepoDialog = rememberSaveable { mutableStateOf(false) }
    val repoPageGoToId = rememberSaveable { mutableStateOf("") }
    val reposLastSearchKeyword = rememberSaveable { mutableStateOf("") }
    val reposSearchToken = rememberSaveable { mutableStateOf("") }
    val reposSearching = rememberSaveable { mutableStateOf(false) }
    val resetReposSearchVars = {
        reposSearching.value = false
        reposSearchToken.value = ""
        reposLastSearchKeyword.value = ""
    }
    val changeListLastSearchKeyword = rememberSaveable { mutableStateOf("") }
    val changeListSearchToken = rememberSaveable { mutableStateOf("") }
    val changeListSearching = rememberSaveable { mutableStateOf(false) }
    val resetChangeListSearchVars = {
        changeListSearching.value = false
        changeListSearchToken.value = ""
        changeListLastSearchKeyword.value = ""
    }
    val subscriptionPageNeedRefresh = rememberSaveable { mutableStateOf("") }
    val needRefreshHome = rememberSaveable { SharedState.homeScreenNeedRefresh }
    val swapForChangeListPage = rememberSaveable { mutableStateOf(false) }
    val needRefreshFilesPage = rememberSaveable { mutableStateOf("") }
    val needRefreshSettingsPage = rememberSaveable { mutableStateOf("") }
    val refreshSettingsPage = { changeStateTriggerRefreshPage(needRefreshSettingsPage) }
    val needRefreshServicePage = rememberSaveable { mutableStateOf("") }
    val refreshServicePage = { changeStateTriggerRefreshPage(needRefreshServicePage) }
    val needRefreshAutomationPage = rememberSaveable { mutableStateOf("") }
    val refreshAutomationPage = { changeStateTriggerRefreshPage(needRefreshAutomationPage) }
    val settingsListState = rememberScrollState()
    val serviceListState = rememberScrollState()
    val aboutListState = rememberScrollState()
    val automationListState = rememberLazyListState()
    val filesPageIsFileSelectionMode = rememberSaveable { mutableStateOf(false)}
    val filesPageIsPasteMode = rememberSaveable { mutableStateOf(false)}
    val filesPageSelectedItems = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageSelectedItems", initValue = listOf<FileItemDto>())
    val reposPageIsSelectionMode = rememberSaveable { mutableStateOf(false)}
    val reposPageSelectedItems = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "reposPageSelectedItems", initValue = listOf<RepoEntity>())
    val reposPageUnshallowItems = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "reposPageUnshallowItems", initValue = listOf<RepoEntity>())
    val reposPageDeleteItems = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "reposPageDeleteItems", initValue = listOf<RepoEntity>())
    val reposPageUserInfoRepoList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "reposPageUserInfoRepoList", initValue = listOf<RepoEntity>())
    val reposPageUpstreamRemoteOptionsList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "reposPageUpstreamRemoteOptionsList", initValue = listOf<String>())
    val reposPageSpecifiedRefreshRepoList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "reposPageSpecifiedRefreshRepoList", initValue = listOf<RepoEntity>())
    val filesPageLastKeyword = rememberSaveable{ mutableStateOf("") }
    val filesPageSearchToken = rememberSaveable{ mutableStateOf("") }
    val filesPageSearching = rememberSaveable{ mutableStateOf(false) }
    val resetFilesSearchVars = {
        filesPageSearching.value = false
        filesPageSearchToken.value = ""
        filesPageLastKeyword.value = ""
    }
    val filesPageFilterMode = rememberSaveable{mutableIntStateOf(0)}  
    val filesPageFilterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filesPageFilterKeyword",
        initValue = TextFieldValue("")
    )
    val filesPageFilterTextFieldFocusRequester = remember { FocusRequester() }
    val filesPageFilterOn = {
        filesPageFilterMode.intValue = 1
        val text = filesPageFilterKeyword.value.text
        if(text.isNotEmpty()) {
            filesPageFilterKeyword.value = filesPageFilterKeyword.value.copy(
                selection = TextRange(0, text.length)
            )
        }
    }
    val filesPageFilterOff = {
        filesPageFilterMode.intValue = 0
        changeStateTriggerRefreshPage(needRefreshFilesPage)
    }
    val filesPageGetFilterMode = {
        filesPageFilterMode.intValue
    }
    val filesPageDoFilter= doFilter@{ keyWord:String ->
        var needUpdateFieldState = true  
        var key = keyWord
        if(key.isEmpty()) {  
            key = filesPageFilterKeyword.value.text
            if(key.isEmpty()) {
                Msg.requireShow(activityContext.getString(R.string.keyword_is_empty))
                return@doFilter
            }
            needUpdateFieldState = false  
        }
        if(needUpdateFieldState){
            filesPageFilterKeyword.value = TextFieldValue(key)  
        }
        filesPageFilterMode.intValue=2  
        changeStateTriggerRefreshPage(needRefreshFilesPage)  
    }
    val automationPageScrolled = rememberSaveable { mutableStateOf(settingsSnapshot.value.showNaviButtons)}
    val filesPageScrolled = rememberSaveable { mutableStateOf(settingsSnapshot.value.showNaviButtons)}
    val filesPageListState = mutableCustomStateOf(stateKeyTag, "filesPageListState", initValue = LazyListState(0,0))
    val filesPageSimpleFilterOn = rememberSaveable { mutableStateOf(false)}
    val filesPageSimpleFilterKeyWord = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filesPageSimpleFilterKeyWord",
        initValue = TextFieldValue("")
    )
    val filesPageCurrentPath = rememberSaveable { mutableStateOf("") }
    val filesGetCurrentPath = {
        filesPageCurrentPath.value
    }
    val filesUpdateCurrentPath = { path:String ->
        filesPageCurrentPath.value = path
    }
    val filesPageLastPathByPressBack = rememberSaveable { mutableStateOf("") }
    val showCreateFileOrFolderDialog = rememberSaveable { mutableStateOf(false) }
    val filesPageCurPathFileItemDto = mutableCustomStateOf(stateKeyTag, "filesPageCurPathFileItemDto") { FileItemDto() }
    val filesPageCurrentPathBreadCrumbList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageCurrentPathBreadCrumbList", initValue = listOf<FileItemDto>())
    val repoPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val filesPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val changeListPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val reposPageFilterList = mutableCustomStateListOf(stateKeyTag, "reposPageFilterList", listOf<RepoEntity>() )
    val filesPageFilterList = mutableCustomStateListOf(stateKeyTag, "filesPageFilterList", listOf<FileItemDto>())
    val changeListFilterList = mutableCustomStateListOf(stateKeyTag,"changeListFilterList", listOf<StatusTypeEntrySaver>())
    val changeListLastClickedItemKey = rememberSaveable{ SharedState.homeChangeList_LastClickedItemKey }
    val changelistFilterListState = rememberLazyListState()
    val filesFilterListState = rememberLazyListState()
    val repoFilterListState = rememberLazyListState()
    val editorDisableSoftKb = rememberSaveable { mutableStateOf(settingsSnapshot.value.editor.disableSoftwareKeyboard) }
    val editorRecentFileList = mutableCustomStateListOf(stateKeyTag, "recentFileList") { listOf<FileDetail>() }
    val editorSelectedRecentFileList = mutableCustomStateListOf(stateKeyTag, "editorSelectedRecentFileList") { listOf<FileDetail>() }
    val editorRecentFileListSelectionMode = rememberSaveable { mutableStateOf(false) }
    val editorRecentListState = rememberLazyStaggeredGridState()
    val editorInRecentFilesPage = rememberSaveable { mutableStateOf(false) }
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
    val editorPreviewFileDto = mutableCustomStateOf(stateKeyTag, "editorPreviewFileDto") { FileSimpleDto() }
    val editorPageShowingFilePath = rememberSaveable { mutableStateOf(FilePath("")) }
    val editorPageShowingFileIsReady = rememberSaveable { mutableStateOf(false) }
    val editorPageIsEdited = rememberSaveable { mutableStateOf(false)}
    val editorPageIsContentSnapshoted = rememberSaveable{ mutableStateOf(false) }  
    val editorPageTextEditorState = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "editorPageTextEditorState",
        initValue = getInitTextEditorState()
    )
    val needRefreshEditorPage = rememberSaveable { mutableStateOf("")}
    val editorPageIsSaving = rememberSaveable { mutableStateOf(false)}
    val showReloadDialog = rememberSaveable { mutableStateOf(false)}
    val changeListHasIndexItems = rememberSaveable { mutableStateOf(false)}
    val changeListRequireDoActFromParent = rememberSaveable { mutableStateOf(false)}
    val changeListRequireDoActFromParentShowTextWhenDoingAct = rememberSaveable { mutableStateOf("")}
    val changeListEnableAction = rememberSaveable { mutableStateOf(true)}
    val changeListCurRepoState = rememberSaveable{mutableIntStateOf(StateT.NONE.bit)}  
    val changeListPageFromTo = Cons.gitDiffFromIndexToWorktree
    val changeListPageItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListPageItemListState = rememberLazyListState()
    val changeListPageSelectedItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageSelectedItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changelistNewestPageId = rememberSaveable { mutableStateOf("") }
    val changeListPageDropDownMenuItemOnClick={item:RepoEntity->
        if(changeListCurRepo.value.id != item.id) {
            changeListPageSelectedItemList.value.clear()
        }
        changeListCurRepo.value=item
        changeListRequireRefreshFromParentPage(item)
    }
    val editorPageRequestFromParent = rememberSaveable { mutableStateOf("")}
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
    val editorQuitPreviewMode = {
        editorBasePath.value = ""
        editorMdText.value = ""
        editorIsPreviewModeOn.value = false
        editorPageRequestFromParent.value = PageRequest.reloadIfChanged
    }
    val editorInitPreviewMode = {
        editorPageRequestFromParent.value = PageRequest.requireInitPreview
    }
    val filesPageRequireImportFile = rememberSaveable { mutableStateOf(false) }
    val intentConsumed = rememberSaveable { IntentHandler.intentConsumed }  
    val filesPageRequireImportUriList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageRequireImportUriList", initValue = listOf<Uri>())
    val filesPageCurrentPathFileList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageCurrentPathFileList", initValue = listOf<FileItemDto>()) 
    val filesPageRequestFromParent = rememberSaveable { mutableStateOf("")}
    val filesPageCheckOnly = rememberSaveable { mutableStateOf(false)}
    val filesPageSelectedRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "filesPageSelectedRepo", RepoEntity(id="") )
    val howToDealWithSingleSend = rememberSaveable { mutableStateOf(SingleSendHandleMethod.NEED_ASK.code) }
    val showAskHandleSingleSendMethod = rememberSaveable { mutableStateOf(false)}
    val cancelAskHandleSingleSendMethod = {
        intentConsumed.value = true
        showAskHandleSingleSendMethod.value = false
    }
    val initAskHandleSingleSendMethodDialog = {
        intentConsumed.value = false
        showAskHandleSingleSendMethod.value = true
    }
    val okAskHandleSingleSendMethod = { handleMethod:SingleSendHandleMethod ->
        howToDealWithSingleSend.value = handleMethod.code
        showAskHandleSingleSendMethod.value = false
        changeStateTriggerRefreshPage(needRefreshHome)
    }
    if(showAskHandleSingleSendMethod.value) {
        ConfirmDialog3(
            title = stringResource(R.string.ask),
            text = stringResource(R.string.do_you_want_to_edit_the_file_or_import_it),
            onCancel = cancelAskHandleSingleSendMethod,
            customOk = {
                ScrollableRow {
                    TextButton(
                        onClick = {
                            okAskHandleSingleSendMethod(SingleSendHandleMethod.EDIT)
                        }
                    ) {
                        Text(stringResource(id = R.string.edit))
                    }
                    TextButton(
                        onClick = {
                            okAskHandleSingleSendMethod(SingleSendHandleMethod.IMPORT)
                        }
                    ) {
                        Text(stringResource(id = R.string.import_str))
                    }
                }
            }
        ) { }
    }
    val initDone = rememberSaveable { mutableStateOf(false)}
    val editorPageShowCloseDialog = rememberSaveable { mutableStateOf(false)}
    val editorPageCloseDialogCallback = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "editorPageCloseDialogCallback",
        initValue = { requireSave:Boolean -> }
    )
    val initLoadingText = activityContext.getString(R.string.loading)
    val loadingText = rememberSaveable { mutableStateOf(initLoadingText)}
    val editorPageIsLoading = rememberSaveable { mutableStateOf(false)}
    val editorPagePreviewLoading = rememberSaveable { mutableStateOf(false) }
    val ignoreFocusOnce = rememberSaveable { mutableStateOf(false) }
    val editorPageLoadingOn = {msg:String ->
        loadingText.value = msg
        editorPageIsLoading.value=true
    }
    val editorPageLoadingOff = {
        editorPageIsLoading.value=false
        loadingText.value = initLoadingText
    }
    val editorPageShowingFileDto = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageShowingFileDto",FileSimpleDto() )
    val editorPageSnapshotedFileInfo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageSnapshotedFileInfo",FileSimpleDto())
    val editorPageLastScrollEvent = mutableCustomStateOf<ScrollEvent?>(keyTag = stateKeyTag, keyName = "editorPageLastScrollEvent") { null }  
    val editorPageLazyListState = rememberLazyListState()
    val editorPageIsInitDone = rememberSaveable{mutableStateOf(false)}  
    val editorPageSearchMode = rememberSaveable{mutableStateOf(false)}
    val editorPageSearchKeyword = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageSearchKeyword", TextFieldValue(""))
    val editorPageMergeMode = rememberSaveable{mutableStateOf(false)}
    val editorPagePatchMode = rememberSaveable(settingsSnapshot.value.editor.patchModeOn) { mutableStateOf(settingsSnapshot.value.editor.patchModeOn) }
    val editorReadOnlyMode = rememberSaveable{mutableStateOf(false)}
    val editorShowLineNum = rememberSaveable{mutableStateOf(settingsSnapshot.value.editor.showLineNum)}
    val editorLineNumFontSize = rememberSaveable { mutableIntStateOf( settingsSnapshot.value.editor.lineNumFontSize)}
    val editorLastSavedLineNumFontSize = rememberSaveable { mutableIntStateOf( editorLineNumFontSize.intValue) } 
    val editorFontSize = rememberSaveable { mutableIntStateOf( settingsSnapshot.value.editor.fontSize)}
    val editorLastSavedFontSize = rememberSaveable { mutableIntStateOf(editorFontSize.intValue)}
    val editorAdjustFontSizeMode = rememberSaveable{mutableStateOf(false)}
    val editorAdjustLineNumFontSizeMode = rememberSaveable{mutableStateOf(false)}
    val editorOpenFileErr = rememberSaveable{mutableStateOf(false)}
    val editorShowUndoRedo = rememberSaveable{mutableStateOf(settingsSnapshot.value.editor.showUndoRedo)}
    val editorUndoStack = mutableCustomStateOf(stateKeyTag, "editorUndoStack") { UndoStack("") }
    val editorLoadLock = mutableCustomBoxOf(stateKeyTag, "editorLoadLock") { Mutex() }.value
    val editorCharset = rememberSaveable { mutableStateOf<String?>(null) }
    val editorPlScope = rememberSaveable { mutableStateOf(PLScope.AUTO) }
    val codeEditor = mutableCustomStateOf(stateKeyTag, "codeEditor") {
        MyCodeEditor(
            editorState = editorPageTextEditorState,
            undoStack = editorUndoStack,
            plScope = editorPlScope,
            editorCharset = editorCharset,
        )
    }.apply {
        SharedState.updateHomeCodeEditor(this.value)
    }
    val requireInnerEditorOpenFileWithFileName = r@{ fullPath:String, expectReadOnly:Boolean ->
        editorQuitPreviewMode()
        editorPageShowingFileIsReady.value=false
        editorPageShowingFilePath.value = FilePath(fullPath)
        editorPageShowingFileDto.value.fullPath = ""
        currentHomeScreen.intValue = Cons.selectedItem_Editor
        editorPageMergeMode.value = false  
        editorReadOnlyMode.value = expectReadOnly
        changeStateTriggerRefreshPage(needRefreshEditorPage)  
    }
    val requireInnerEditorOpenFile = { fullPath:String, expectReadOnly:Boolean ->
        requireInnerEditorOpenFileWithFileName(fullPath, expectReadOnly)
    }
    val needRefreshRepoPage = rememberSaveable { mutableStateOf("") }
    val lastSavedFieldsId = rememberSaveable { mutableStateOf("") }
    val doSave: suspend () -> Unit = FsUtils.getDoSaveForEditor(
        editorPageShowingFilePath = editorPageShowingFilePath,
        editorPageLoadingOn = editorPageLoadingOn,
        editorPageLoadingOff = editorPageLoadingOff,
        activityContext = activityContext,
        editorPageIsSaving = editorPageIsSaving,
        needRefreshEditorPage = needRefreshEditorPage,
        editorPageTextEditorState = editorPageTextEditorState,
        pageTag = TAG,
        editorPageIsEdited = editorPageIsEdited,
        requestFromParent = editorPageRequestFromParent,
        editorPageFileDto = editorPageShowingFileDto,
        isSubPageMode = false,
        isContentSnapshoted = editorPageIsContentSnapshoted,
        snapshotedFileInfo = editorPageSnapshotedFileInfo,
        lastSavedFieldsId = lastSavedFieldsId,
    )
    val goToServicePage = {
        currentHomeScreen.intValue = Cons.selectedItem_Service
        refreshServicePage()
    }
    val goToRepoPage = { targetIdIfHave:String ->
        repoPageFilterModeOn.value = false  
        repoPageEnableFilterState.value = false  
        repoPageGoToId.value = targetIdIfHave
        currentHomeScreen.intValue = Cons.selectedItem_Repos
        changeStateTriggerRefreshPage(needRefreshRepoPage)
    }
    val goToFilesPage = {path:String ->
        filesPageSimpleFilterOn.value = false  
        filesPageEnableFilterState.value = false  
        filesUpdateCurrentPath(path)
        currentHomeScreen.intValue = Cons.selectedItem_Files
        changeStateTriggerRefreshPage(needRefreshFilesPage)
    }
    val goToChangeListPage = { repoWillShowInChangeListPage: RepoEntity ->
        changeListPageFilterModeOn.value = false  
        changeListPageEnableFilterState.value = false  
        changeListCurRepo.value = repoWillShowInChangeListPage
        currentHomeScreen.intValue = Cons.selectedItem_ChangeList
        changeListRequireRefreshFromParentPage(repoWillShowInChangeListPage)
    }
    val filesPageKeepFilterResultOnce = rememberSaveable { mutableStateOf(false) }
    val filesGoToPath = getFilesGoToPath(filesPageLastPathByPressBack, filesGetCurrentPath, filesUpdateCurrentPath, needRefreshFilesPage)
    val requireEditorScrollToPreviewCurPos = rememberSaveable { mutableStateOf(false) }
    val requirePreviewScrollToEditorCurPos = rememberSaveable { mutableStateOf(false) }
    val editorPreviewPageScrolled = rememberSaveable { mutableStateOf(settingsSnapshot.value.showNaviButtons) }
    val changelistPageScrolled = rememberSaveable { mutableStateOf(settingsSnapshot.value.showNaviButtons) }
    val repoPageScrolled = rememberSaveable { mutableStateOf(settingsSnapshot.value.showNaviButtons) }
    val editorRecentListScrolled = rememberSaveable { mutableStateOf(settingsSnapshot.value.showNaviButtons) }
    val needReQueryRepoListForChangeListTitle = rememberSaveable { mutableStateOf("")}
    val changeListRepoList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListRepoList", initValue = listOf<RepoEntity>())
    val showWelcomeToNewUser = rememberSaveable { mutableStateOf(PrefMan.isFirstUse(activityContext))}
    val showSetGlobalGitUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false)}
    val closeWelcome = {
        PrefMan.updateFirstUse(activityContext, false)
        showWelcomeToNewUser.value=false
    }
    val drawTextList = listOf(
        stringResource(id = R.string.repos),
        stringResource(id = R.string.files),
        stringResource(id = R.string.editor),
        stringResource(id = R.string.changelist),
        stringResource(id = R.string.service),
        stringResource(id = R.string.automation),
        stringResource(id = R.string.settings),
        stringResource(id = R.string.about),
    )
    val drawIdList = listOf(
        Cons.selectedItem_Repos,
        Cons.selectedItem_Files,
        Cons.selectedItem_Editor,
        Cons.selectedItem_ChangeList,
        Cons.selectedItem_Service,
        Cons.selectedItem_Automation,
        Cons.selectedItem_Settings,
        Cons.selectedItem_About,
    )
    val drawIconList = listOf(
        Icons.Filled.Inventory,
        Icons.Filled.Folder,
        Icons.Filled.EditNote,
        Icons.Filled.Difference,
        Icons.Filled.Cloud,
        Icons.Filled.AutoFixHigh,
        Icons.Filled.Settings,
        Icons.Filled.Info,
    )
    val drawerItemOnClick = listOf(
        clickRepoPage@{ changeStateTriggerRefreshPage(needRefreshRepoPage) },
        clickFilesPage@{ changeStateTriggerRefreshPage(needRefreshFilesPage) },
        clickEditorPage@{ editorPageShowingFileIsReady.value=false; changeStateTriggerRefreshPage(needRefreshEditorPage) },
        clickChangeListPage@{
            doJobThenOffLoading {
                val editorShowingPath = editorPageShowingFilePath.value.ioPath
                val editorPageShowingFilePath = Unit  
                val oldChangeListCachedEditorPath = changeListCachedEditorPath.value
                changeListCachedEditorPath.value = editorShowingPath
                val changeListCachedEditorPath = Unit
                if(editorShowingPath.isNotBlank() && oldChangeListCachedEditorPath != editorShowingPath) {
                    Libgit2Helper.findRepoByPath(editorShowingPath)?.use { repo ->
                        val repoPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                        val repoFromDb = AppModel.dbContainer.repoRepository.getByFullSavePath(repoPath, onlyReturnReadyRepo = true, requireSyncRepoInfoWithGit = false)
                        repoFromDb?.let { changeListCurRepo.value = it }
                    }
                }
                changeListRequireRefreshFromParentPage(changeListCurRepo.value)
            }
            Unit
        },
        clickServicePage@{ refreshServicePage() },
        clickAutomationPage@{ refreshAutomationPage() },
        clickSettingsPage@{ refreshSettingsPage() },
        clickAboutPage@{}, 
    )
    val openDrawer = {  
        scope.launch {
            drawerState.apply {
                if (isClosed) open()
            }
        }
        Unit
    }
    val menuButton:@Composable ()->Unit = {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.menu),
            icon = Icons.Filled.Menu,
            iconContentDesc = stringResource(R.string.menu),
        ) {
            scope.launch {
                drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier= Modifier
                    .fillMaxHeight()
                    .widthIn(max = 320.dp)
                    .verticalScroll(rememberScrollState())
                ,
                drawerShape = RectangleShape,
                content = drawerContent(
                    currentHomeScreen = currentHomeScreen,
                    scope = scope,
                    drawerState = drawerState,
                    drawerItemShape = RectangleShape,
                    drawTextList = drawTextList,
                    drawIdList = drawIdList,
                    drawIconList = drawIconList,
                    drawerItemOnClick = drawerItemOnClick,
                    showExit = true,
                    filesPageKeepFilterResultOnce = filesPageKeepFilterResultOnce,
                )
            )
        },
    ) {
        val repoListFilterLastPosition = rememberSaveable { mutableStateOf(0) }
        val fileListFilterLastPosition = rememberSaveable { mutableStateOf(0) }
        val changeListFilterLastPosition = rememberSaveable { mutableStateOf(0) }
        val changeListLastPosition = rememberSaveable { mutableStateOf(0) }
        val editorPreviewLastScrollPosition = rememberSaveable { mutableStateOf(0) }
        val reposLastPosition = rememberSaveable { mutableStateOf(0) }
        val filesLastPosition = rememberSaveable { mutableStateOf(0) }
        val settingsLastPosition = rememberSaveable { mutableStateOf(0) }
        val aboutLastPosition = rememberSaveable { mutableStateOf(0) }
        val serviceLastPosition = rememberSaveable { mutableStateOf(0) }
        val automationLastPosition = rememberSaveable { mutableStateOf(0) }
        val filesErrLastPosition = rememberSaveable { mutableStateOf(0) }
        val changeListErrLastPosition = rememberSaveable { mutableStateOf(0) }
        val filesPageErrScrollState = rememberScrollState()
        val filesPageOpenDirErr = rememberSaveable { mutableStateOf("") }
        val filesPageGetErr = { filesPageOpenDirErr.value }
        val filesPageSetErr = { errMsg:String -> filesPageOpenDirErr.value = errMsg }
        val filesPageHasErr = { filesPageOpenDirErr.value.isNotBlank() }
        val changeListErrScrollState = rememberScrollState()
        val changeListHasErr = rememberSaveable { mutableStateOf(false) }
        val editorNeedSave = { editorPageShowingFileIsReady.value && editorPageIsEdited.value && !editorPageIsSaving.value && !editorReadOnlyMode.value && lastSavedFieldsId.value != editorPageTextEditorState.value.fieldsId }
        Scaffold(
            modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    colors = MyStyleKt.TopBar.getColors(),
                    title = {
                        if(currentHomeScreen.intValue == Cons.selectedItem_Repos){
                            if(repoPageFilterModeOn.value) {
                                FilterTextField(filterKeyWord = repoPageFilterKeyWord, loading = reposSearching.value)
                            }else {
                                ReposTitle(
                                    listState = repoPageListState,
                                    scope = scope,
                                    allRepoCount = repoPageRepoList.value.size,
                                    lastPosition = reposLastPosition
                                )
                            }
                        } else if(currentHomeScreen.intValue == Cons.selectedItem_Files){
                            FilesTitle(
                                stateKeyTag = stateKeyTag,
                                currentPath = filesGetCurrentPath,
                                goToPath = filesGoToPath,
                                allRepoParentDir = allRepoParentDir,
                                needRefreshFilesPage = needRefreshFilesPage,
                                filesPageGetFilterMode = filesPageGetFilterMode,
                                filterKeyWord = filesPageFilterKeyword,
                                filterModeOn = filesPageFilterOn,
                                doFilter = filesPageDoFilter,
                                requestFromParent = filesPageRequestFromParent,
                                filterKeywordFocusRequester = filesPageFilterTextFieldFocusRequester,
                                filesPageSimpleFilterOn = filesPageSimpleFilterOn.value,
                                filesPageSimpleFilterKeyWord = filesPageSimpleFilterKeyWord,
                                curPathItemDto = filesPageCurPathFileItemDto.value,
                                searching = filesPageSearching.value
                            )
                        } else if (currentHomeScreen.intValue == Cons.selectedItem_Editor) {
                            EditorTitle(
                                disableSoftKb = editorDisableSoftKb,
                                recentFileListIsEmpty = editorRecentFileList.value.isEmpty(),
                                recentFileListFilterModeOn = editorFilterRecentListOn.value,
                                recentListFilterKeyword = editorFilterRecentListKeyword,
                                getActuallyRecentFilesListState=getActuallyRecentFilesListState,
                                getActuallyRecentFilesListLastPosition=getActuallyRecentFilesListLastPosition,
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
                                showCloseDialog = editorPageShowCloseDialog,
                                editorNeedSave = editorNeedSave,
                            )
                        } else if (currentHomeScreen.intValue == Cons.selectedItem_ChangeList) {
                            if(changeListPageFilterModeOn.value) {
                                FilterTextField(filterKeyWord = changeListPageFilterKeyWord, loading = changeListSearching.value)
                            }else{
                                ChangeListTitle(
                                    changeListCurRepo = changeListCurRepo,
                                    dropDownMenuItemOnClick = changeListPageDropDownMenuItemOnClick,
                                    repoState = changeListCurRepoState,
                                    isSelectionMode = changeListIsFileSelectionMode,
                                    listState = changeListPageItemListState,
                                    scope = scope,
                                    enableAction = changeListEnableAction.value,
                                    repoList = changeListRepoList,
                                    needReQueryRepoList=needReQueryRepoListForChangeListTitle,
                                    goToChangeListPage = goToChangeListPage,
                                )
                            }
                        } else if (currentHomeScreen.intValue == Cons.selectedItem_Settings) {
                            ScrollableTitle(text = stringResource(R.string.settings), listState = settingsListState, lastPosition = settingsLastPosition)
                        } else if (currentHomeScreen.intValue == Cons.selectedItem_About) {
                            ScrollableTitle(text = stringResource(R.string.about), listState = aboutListState, lastPosition = aboutLastPosition)
                        } else if(currentHomeScreen.intValue == Cons.selectedItem_Subscription) {
                            SimpleTitle(text = stringResource(R.string.subscription))
                        } else if(currentHomeScreen.intValue == Cons.selectedItem_Service){
                            ScrollableTitle(text = stringResource(R.string.service), listState = serviceListState, lastPosition = serviceLastPosition)
                        }  else if(currentHomeScreen.intValue == Cons.selectedItem_Automation){
                            ScrollableTitle(text = stringResource(R.string.automation), listState = automationListState, lastPosition = automationLastPosition)
                        } else {
                            SimpleTitle()
                        }
                    },
                    navigationIcon = {
                        if(currentHomeScreen.intValue == Cons.selectedItem_Files
                            && (filesPageGetFilterMode() != 0 || filesPageSimpleFilterOn.value)
                        ) {
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.close),
                                icon =  Icons.Filled.Close,
                                iconContentDesc = stringResource(R.string.close),
                            ) {
                                resetFilesSearchVars()
                                filesPageSimpleFilterOn.value = false
                            }
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_ChangeList && changeListPageFilterModeOn.value){
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.close),
                                icon =  Icons.Filled.Close,
                                iconContentDesc = stringResource(R.string.close),
                            ) {
                                resetChangeListSearchVars()
                                changeListPageFilterModeOn.value=false
                            }
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_Repos && repoPageFilterModeOn.value){
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.close),
                                icon =  Icons.Filled.Close,
                                iconContentDesc = stringResource(R.string.close),
                            ) {
                                resetReposSearchVars()
                                repoPageFilterModeOn.value=false
                            }
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_Editor
                            && (editorIsPreviewModeOn.value || editorPageSearchMode.value
                                    || editorAdjustFontSizeMode.value || editorAdjustLineNumFontSizeMode.value
                                    || (editorInRecentFilesPage.value && editorFilterRecentListOn.value)
                            )
                        ){
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.close),
                                icon =  Icons.Filled.Close,
                                iconContentDesc = stringResource(R.string.close),
                            ) {
                                if(editorIsPreviewModeOn.value){
                                    editorQuitPreviewMode()
                                }else if(editorPageSearchMode.value) {
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
                            menuButton()
                        }
                    },
                    actions = {
                        if(currentHomeScreen.intValue == Cons.selectedItem_Repos) {
                            if(!repoPageFilterModeOn.value){
                                RepoPageActions(
                                    navController = navController,
                                    curRepo = repoPageCurRepo,
                                    showGlobalUsernameAndEmailDialog = showSetGlobalGitUsernameAndEmailDialog,
                                    needRefreshRepoPage = needRefreshRepoPage,
                                    repoPageFilterModeOn = repoPageFilterModeOn,
                                    repoPageFilterKeyWord = repoPageFilterKeyWord,
                                    showImportRepoDialog = repoPageShowImportRepoDialog
                                )
                            }
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_Files) {
                            FilesPageActions(
                                isFileChooser = false,
                                refreshPage = {
                                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                                },
                                filterOn = filesPageFilterOn,
                                filesPageGetFilterMode = filesPageGetFilterMode,
                                doFilter = filesPageDoFilter,
                                requestFromParent = filesPageRequestFromParent,
                                filesPageSimpleFilterOn = filesPageSimpleFilterOn,
                                filesPageSimpleFilterKeyWord = filesPageSimpleFilterKeyWord
                            )
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_Editor && !editorOpenFileErr.value) {
                            val notOpenFile = !editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isBlank()
                            if(notOpenFile && editorRecentFileList.value.isNotEmpty()) {
                                FileDetailListActions(
                                    request = editorPageRequestFromParent,
                                    filterModeOn = editorFilterRecentListOn.value,
                                    initFilterMode = editorInitRecentFilesFilterMode,
                                )
                            }else {
                                EditorPageActions(
                                    disableSoftKb = editorDisableSoftKb,
                                    initPreviewMode = editorInitPreviewMode,
                                    requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
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
                                    showCloseDialog=editorPageShowCloseDialog,
                                    closeDialogCallback = editorPageCloseDialogCallback,
                                    doSave = doSave,
                                    loadingOn = editorPageLoadingOn,
                                    loadingOff = editorPageLoadingOff,
                                    editorPageRequest = editorPageRequestFromParent,
                                    editorPageSearchMode=editorPageSearchMode,
                                    editorPageMergeMode=editorPageMergeMode,
                                    editorPagePatchMode=editorPagePatchMode,
                                    readOnlyMode = editorReadOnlyMode,
                                    editorSearchKeyword = editorPageSearchKeyword.value.text,
                                    isSubPageMode = false,
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
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_ChangeList) {
                            if(!changeListPageFilterModeOn.value){
                                ChangeListPageActions(
                                    changeListCurRepo,
                                    changeListRequireRefreshFromParentPage,
                                    changeListHasIndexItems,
                                    changeListRequireDoActFromParent,
                                    changeListRequireDoActFromParentShowTextWhenDoingAct,
                                    changeListEnableAction,
                                    changeListCurRepoState,
                                    fromTo = changeListPageFromTo,
                                    changeListPageItemListState,
                                    scope,
                                    changeListPageNoRepo=changeListPageNoRepo,
                                    hasNoConflictItems = changeListPageHasNoConflictItems.value,
                                    changeListPageFilterModeOn= changeListPageFilterModeOn,
                                    changeListPageFilterKeyWord=changeListPageFilterKeyWord,
                                    rebaseCurOfAll = changeListPageRebaseCurOfAll.value,
                                    naviTarget = changeListNaviTarget
                                )
                            }
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_Settings) {
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_Subscription) {
                            SubscriptionActions { 
                                changeStateTriggerRefreshPage(subscriptionPageNeedRefresh)
                            }
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_Service) {
                            RefreshActions(refreshServicePage)
                        }else if(currentHomeScreen.intValue == Cons.selectedItem_Automation){
                            RefreshActions(refreshAutomationPage)
                        }
                    },
                    scrollBehavior = homeTopBarScrollBehavior,
                )
            },
            floatingActionButton = {
                if(currentHomeScreen.intValue == Cons.selectedItem_Editor && editorIsPreviewModeOn.value && editorPreviewPageScrolled.value) {
                    GoToTopAndGoToBottomFab(
                        scope = scope,
                        listState = runBlocking { editorPreviewNavStack.value.getCurrentScrollState() },
                        listLastPosition = editorPreviewLastScrollPosition,
                        showFab = editorPreviewPageScrolled
                    )
                } else if(currentHomeScreen.intValue == Cons.selectedItem_Editor && editorNeedSave()) {
                    SmallFab(modifier = MyStyleKt.Fab.getFabModifierForEditor(editorPageTextEditorState.value.isMultipleSelectionMode, UIHelper.isPortrait()),
                        icon = Icons.Filled.Save, iconDesc = stringResource(id = R.string.save)
                    ) {
                        editorPageRequestFromParent.value = PageRequest.requireSave
                    }
                }else if(currentHomeScreen.intValue == Cons.selectedItem_Editor && editorInRecentFilesPage.value && editorRecentListScrolled.value) {
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
                }else if(currentHomeScreen.intValue == Cons.selectedItem_ChangeList && changelistPageScrolled.value) {
                    if(changeListHasErr.value) {
                        GoToTopAndGoToBottomFab(
                            scope = scope,
                            listState = changeListErrScrollState,
                            listLastPosition = changeListErrLastPosition,
                            showFab = changelistPageScrolled
                        )
                    }else {
                        GoToTopAndGoToBottomFab(
                            filterModeOn = changeListPageEnableFilterState.value,
                            scope = scope,
                            filterListState = changelistFilterListState,
                            listState = changeListPageItemListState,
                            filterListLastPosition = changeListFilterLastPosition,
                            listLastPosition = changeListLastPosition,
                            showFab = changelistPageScrolled
                        )
                    }
                }else if(currentHomeScreen.intValue == Cons.selectedItem_Repos && repoPageScrolled.value) {
                    GoToTopAndGoToBottomFab(
                        filterModeOn = repoPageEnableFilterState.value,
                        scope = scope,
                        filterListState = repoFilterListState,
                        listState = repoPageListState,
                        filterListLastPosition = repoListFilterLastPosition,
                        listLastPosition = reposLastPosition,
                        showFab = repoPageScrolled
                    )
                }else if(currentHomeScreen.intValue == Cons.selectedItem_Files && filesPageScrolled.value) {
                    if(filesPageHasErr()) {
                        GoToTopAndGoToBottomFab(
                            scope = scope,
                            listState = filesPageErrScrollState,
                            listLastPosition = filesErrLastPosition,
                            showFab = filesPageScrolled
                        )
                    }else {
                        GoToTopAndGoToBottomFab(
                            filterModeOn = filesPageEnableFilterState.value,
                            scope = scope,
                            filterListState = filesFilterListState,
                            listState = filesPageListState.value,
                            filterListLastPosition = fileListFilterLastPosition,
                            listLastPosition = filesLastPosition,
                            showFab = filesPageScrolled
                        )
                    }
                }else if(currentHomeScreen.intValue == Cons.selectedItem_Automation && automationPageScrolled.value) {
                    GoToTopAndGoToBottomFab(
                        scope = scope,
                        listState = automationListState,
                        listLastPosition = automationLastPosition,
                        showFab = automationPageScrolled
                    )
                }
            }
        ) { contentPadding ->
            if(AppModel.showChangelogDialog.value) {
                ChangelogDialog(
                    onClose = { AppModel.showChangelogDialog.value = false }
                )
            }
            if(currentHomeScreen.intValue == Cons.selectedItem_Repos) {
                RepoInnerPage(
                    stateKeyTag = stateKeyTag,
                    requireInnerEditorOpenFile = requireInnerEditorOpenFile,
                    lastSearchKeyword=reposLastSearchKeyword,
                    searchToken=reposSearchToken,
                    searching=reposSearching,
                    resetSearchVars=resetReposSearchVars,
                    showBottomSheet = showBottomSheet,
                    sheetState = sheetState,
                    curRepo = repoPageCurRepo,
                    curRepoIndex = repoPageCurRepoIndex,
                    contentPadding = contentPadding,
                    repoPageListState = repoPageListState,
                    showSetGlobalGitUsernameAndEmailDialog = showSetGlobalGitUsernameAndEmailDialog,
                    needRefreshRepoPage = needRefreshRepoPage,
                    repoList = repoPageRepoList,
                    goToFilesPage = goToFilesPage,
                    goToChangeListPage = goToChangeListPage,
                    repoPageScrolled=repoPageScrolled,
                    repoPageFilterModeOn=repoPageFilterModeOn,
                    repoPageFilterKeyWord= repoPageFilterKeyWord,
                    filterListState = repoFilterListState,
                    openDrawer = openDrawer,
                    showImportRepoDialog = repoPageShowImportRepoDialog,
                    goToThisRepoId = repoPageGoToId,
                    enableFilterState = repoPageEnableFilterState,
                    filterList = reposPageFilterList,
                    isSelectionMode = reposPageIsSelectionMode,
                    selectedItems = reposPageSelectedItems,
                    unshallowList = reposPageUnshallowItems,
                    deleteList = reposPageDeleteItems,
                    userInfoRepoList = reposPageUserInfoRepoList,
                    upstreamRemoteOptionsList = reposPageUpstreamRemoteOptionsList,
                    specifiedRefreshRepoList = reposPageSpecifiedRefreshRepoList,
                    showWelcomeToNewUser = showWelcomeToNewUser,
                    closeWelcome = closeWelcome,
                )
            }
            else if(currentHomeScreen.intValue== Cons.selectedItem_Files) {
                FilesInnerPage(
                    stateKeyTag = stateKeyTag,
                    errScrollState = filesPageErrScrollState,
                    getErr = filesPageGetErr,
                    setErr = filesPageSetErr,
                    hasErr = filesPageHasErr,
                    naviUp = {},
                    updateSelectedPath = {},
                    isFileChooser = false,
                    fileChooserType = FileChooserType.SINGLE_DIR, 
                    filesPageLastKeyword=filesPageLastKeyword,
                    filesPageSearchToken=filesPageSearchToken,
                    filesPageSearching=filesPageSearching,
                    resetFilesSearchVars=resetFilesSearchVars,
                    contentPadding = contentPadding,
                    currentHomeScreen=currentHomeScreen,
                    editorPageShowingFilePath = editorPageShowingFilePath,
                    editorPageShowingFileIsReady = editorPageShowingFileIsReady,
                    needRefreshFilesPage = needRefreshFilesPage,
                    currentPath=filesGetCurrentPath,
                    updateCurrentPath=filesUpdateCurrentPath,
                    showCreateFileOrFolderDialog=showCreateFileOrFolderDialog,
                    requireImportFile=filesPageRequireImportFile,
                    requireImportUriList=filesPageRequireImportUriList,
                    filesPageGetFilterMode=filesPageGetFilterMode,
                    filesPageFilterKeyword=filesPageFilterKeyword,
                    filesPageFilterModeOff = filesPageFilterOff,
                    currentPathFileList = filesPageCurrentPathFileList,
                    filesPageRequestFromParent = filesPageRequestFromParent,
                    requireInnerEditorOpenFile = requireInnerEditorOpenFile,
                    filesPageSimpleFilterOn = filesPageSimpleFilterOn,
                    filesPageSimpleFilterKeyWord = filesPageSimpleFilterKeyWord,
                    filesPageScrolled = filesPageScrolled,
                    curListState = filesPageListState,
                    filterListState = filesFilterListState,
                    openDrawer = openDrawer,
                    isFileSelectionMode= filesPageIsFileSelectionMode,
                    isPasteMode = filesPageIsPasteMode,
                    selectedItems = filesPageSelectedItems,
                    checkOnly = filesPageCheckOnly,
                    selectedRepo = filesPageSelectedRepo,
                    goToRepoPage=goToRepoPage,
                    goToChangeListPage=goToChangeListPage,
                    lastPathByPressBack=filesPageLastPathByPressBack,
                    curPathFileItemDto=filesPageCurPathFileItemDto,
                    currentPathBreadCrumbList=filesPageCurrentPathBreadCrumbList,
                    enableFilterState = filesPageEnableFilterState,
                    filterList = filesPageFilterList,
                    lastPosition = filesLastPosition,
                    keepFilterResultOnce = filesPageKeepFilterResultOnce,
                    goToPath = filesGoToPath,
                )
            }
            else if(currentHomeScreen.intValue == Cons.selectedItem_Editor) {
                EditorInnerPage(
                    stateKeyTag = stateKeyTag,
                    editorCharset = editorCharset,
                    lastSavedFieldsId = lastSavedFieldsId,
                    codeEditor = codeEditor,
                    plScope = editorPlScope,
                    editorRecentListScrolled = editorRecentListScrolled,
                    disableSoftKb = editorDisableSoftKb,
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
                    currentHomeScreen = currentHomeScreen,
                    editorPageShowingFilePath=editorPageShowingFilePath,
                    editorPageShowingFileIsReady=editorPageShowingFileIsReady,
                    editorPageTextEditorState=editorPageTextEditorState,
                    needRefreshEditorPage=needRefreshEditorPage,
                    isSaving = editorPageIsSaving,
                    isEdited = editorPageIsEdited,
                    showReloadDialog=showReloadDialog,
                    isSubPageMode = false,
                    showCloseDialog = editorPageShowCloseDialog,
                    closeDialogCallback = editorPageCloseDialogCallback,
                    loadingOn = editorPageLoadingOn,
                    loadingOff = editorPageLoadingOff,
                    saveOnDispose = true,  
                    doSave=doSave,
                    naviUp = {},  
                    requestFromParent = editorPageRequestFromParent,
                    editorPageShowingFileDto = editorPageShowingFileDto,
                    lastFilePath = editorPageLastFilePath,
                    editorLastScrollEvent = editorPageLastScrollEvent,
                    editorListState = editorPageLazyListState,
                    editorPageIsInitDone = editorPageIsInitDone,
                    editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
                    goToFilesPage = goToFilesPage,
                    drawerState = drawerState,
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
                    openDrawer = openDrawer,
                    editorOpenFileErr = editorOpenFileErr,
                    undoStack = editorUndoStack.value,
                    loadLock = editorLoadLock
                )
            }
            else if(currentHomeScreen.intValue == Cons.selectedItem_ChangeList) {
                ChangeListInnerPage(
                    stateKeyTag = stateKeyTag,
                    errScrollState = changeListErrScrollState,
                    hasError = changeListHasErr,
                    commit1OidStr = Cons.git_IndexCommitHash,
                    commit2OidStr = Cons.git_LocalWorktreeCommitHash,
                    lastSearchKeyword=changeListLastSearchKeyword,
                    searchToken=changeListSearchToken,
                    searching=changeListSearching,
                    resetSearchVars=resetChangeListSearchVars,
                    contentPadding = contentPadding,
                    fromTo = changeListPageFromTo,
                    curRepoFromParentPage = changeListCurRepo,
                    isFileSelectionMode = changeListIsFileSelectionMode,
                    refreshRequiredByParentPage = changeListRefreshRequiredByParentPage.value,
                    changeListRequireRefreshFromParentPage = changeListRequireRefreshFromParentPage,
                    changeListPageHasIndexItem = changeListHasIndexItems,
                    requireDoActFromParent = changeListRequireDoActFromParent,
                    requireDoActFromParentShowTextWhenDoingAct = changeListRequireDoActFromParentShowTextWhenDoingAct,
                    enableActionFromParent = changeListEnableAction,
                    repoState = changeListCurRepoState,
                    naviUp = {},  
                    itemList = changeListPageItemList,
                    itemListState = changeListPageItemListState,
                    selectedItemList = changeListPageSelectedItemList,
                    changeListPageNoRepo=changeListPageNoRepo,
                    hasNoConflictItems = changeListPageHasNoConflictItems,
                    goToFilesPage = goToFilesPage,
                    changelistPageScrolled=changelistPageScrolled,
                    changeListPageFilterModeOn= changeListPageFilterModeOn,
                    changeListPageFilterKeyWord=changeListPageFilterKeyWord,
                    filterListState = changelistFilterListState,
                    swap=swapForChangeListPage.value,
                    commitForQueryParents = "",
                    rebaseCurOfAll=changeListPageRebaseCurOfAll,
                    openDrawer = openDrawer,
                    goToRepoPage = goToRepoPage,
                    changeListRepoList= changeListRepoList,
                    goToChangeListPage=goToChangeListPage,
                    needReQueryRepoList = needReQueryRepoListForChangeListTitle,
                    newestPageId = changelistNewestPageId,
                    naviTarget = changeListNaviTarget,
                    enableFilterState = changeListPageEnableFilterState,
                    filterList = changeListFilterList,
                    lastClickedItemKey = changeListLastClickedItemKey
                )
            }else if(currentHomeScreen.intValue == Cons.selectedItem_Settings) {
                SettingsInnerPage(
                    stateKeyTag = stateKeyTag,
                    contentPadding = contentPadding,
                    needRefreshPage = needRefreshSettingsPage,
                    openDrawer = openDrawer,
                    exitApp = exitApp,
                    listState = settingsListState,
                    goToFilesPage = goToFilesPage,
                )
            }else if(currentHomeScreen.intValue == Cons.selectedItem_About) {
                AboutInnerPage(aboutListState, contentPadding, openDrawer = openDrawer)
            }else if(currentHomeScreen.intValue == Cons.selectedItem_Subscription) {
                SubscriptionPage(contentPadding = contentPadding, needRefresh = subscriptionPageNeedRefresh, openDrawer = openDrawer)
            }else if(currentHomeScreen.intValue == Cons.selectedItem_Service) {
                ServiceInnerPage(
                    stateKeyTag = stateKeyTag,
                    contentPadding = contentPadding,
                    needRefreshPage = needRefreshServicePage,
                    openDrawer = openDrawer,
                    exitApp = exitApp,
                    listState = serviceListState,
                )
            }else if(currentHomeScreen.intValue == Cons.selectedItem_Automation) {
                AutomationInnerPage(
                    stateKeyTag = stateKeyTag,
                    contentPadding = contentPadding,
                    needRefreshPage = needRefreshAutomationPage,
                    listState = automationListState,
                    pageScrolled = automationPageScrolled,
                    refreshPage = refreshAutomationPage,
                    openDrawer = openDrawer,
                    exitApp = exitApp,
                )
            }
        }
    }
    LaunchedEffect(initDone.value, currentHomeScreen.intValue) {
        val currentHomeScreen = currentHomeScreen.intValue
        val newSettings = SettingsUtil.getSettingsSnapshot()
        settingsSnapshot.value = newSettings  
        val settingsSnapshot = newSettings
        if(settingsSnapshot.startPageMode == SettingsCons.startPageMode_rememberLastQuit
            && currentHomeScreen != settingsSnapshot.lastQuitHomeScreen
            && initDone.value && currentHomeScreen != Cons.selectedItem_Never
            && currentHomeScreen != Cons.selectedItem_Exit
        ) {
            settingsSnapshot.lastQuitHomeScreen = currentHomeScreen
            SettingsUtil.update {
                it.lastQuitHomeScreen = currentHomeScreen
            }
        }
    }
    LaunchedEffect(needRefreshHome.value) {
        doJobThenOffLoading {
            refreshLock.withLock {
                try {
                    if(showWelcomeToNewUser.value) {
                        val newSettings = SettingsUtil.getSettingsSnapshot()
                        if(PrefMan.isFirstUse(activityContext) && newSettings.globalGitConfig.username.isEmpty() && newSettings.globalGitConfig.email.isEmpty()) {
                            showSetGlobalGitUsernameAndEmailDialog.value = true
                        }else {
                            closeWelcome()
                        }
                    }
                    val startPageMode = settingsSnapshot.value.startPageMode
                    if (startPageMode == SettingsCons.startPageMode_rememberLastQuit) {
                        currentHomeScreen.intValue = settingsSnapshot.value.lastQuitHomeScreen
                        initDone.value = true
                    }
                    if (true) {  
                        val intent = IntentHandler.intent.value  
                        MyLog.d(TAG, "intent==null: ${intent == null}")
                        if (intent != null) {
                            val curIntentConsumed = intentConsumed.value
                            MyLog.d(TAG, "curIntentConsumed: $curIntentConsumed, intent.action: ${intent.action}, intent.extras==null: ${intent.extras == null}, intent.data==null: ${intent.data == null}")
                            intentConsumed.value = true
                            val intentConsumed = Unit 
                            val requireEditFile = intent.action.let { it == Intent.ACTION_VIEW || it == Intent.ACTION_EDIT }
                            val extras = intent.extras
                            if (requireEditFile || extras != null) {
                                if (!curIntentConsumed) {  
                                    if(requireEditFile) {
                                        val uri: Uri? = intent.data
                                        if(uri != null) {
                                            val expectReadOnly = (intent.flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION) == 0
                                            val contentResolver = activityContext.contentResolver
                                            if(expectReadOnly) {
                                                SafUtil.takePersistableReadOnlyPermission(contentResolver, uri)
                                            }else {
                                                SafUtil.takePersistableRWPermission(contentResolver, uri)
                                            }
                                            requireInnerEditorOpenFile(uri.toString(), expectReadOnly)
                                            return@doJobThenOffLoading
                                        }
                                    }
                                    if(extras == null) {
                                        return@doJobThenOffLoading
                                    }
                                    var importFiles = false  
                                    filesPageRequireImportUriList.value.clear()
                                    val uri = try {
                                        extras.getParcelable<Uri>(Intent.EXTRA_STREAM)  
                                    } catch (e: Exception) {
                                        null
                                    }
                                    val howToDealWithSingleSend = SingleSendHandleMethod.IMPORT.code
                                    if (uri != null) {
                                        if(howToDealWithSingleSend == SingleSendHandleMethod.NEED_ASK.code) {
                                            initAskHandleSingleSendMethodDialog()
                                            return@doJobThenOffLoading
                                        }else if(howToDealWithSingleSend == SingleSendHandleMethod.EDIT.code) {
                                            val uriStr = uri.toString()
                                            val expectReadOnly = false
                                            requireInnerEditorOpenFileWithFileName(uriStr, expectReadOnly)
                                            return@doJobThenOffLoading
                                        }else if(howToDealWithSingleSend == SingleSendHandleMethod.IMPORT.code) {
                                            filesPageRequireImportUriList.value.add(uri)
                                        }
                                    }
                                    val uriList = try {
                                        extras.getParcelableArrayList<Uri>(Intent.EXTRA_STREAM) ?: listOf()
                                    } catch (e: Exception) {
                                        listOf()
                                    }
                                    if (uriList.isNotEmpty()) {
                                        filesPageRequireImportUriList.value.addAll(uriList)
                                    }
                                    if (filesPageRequireImportUriList.value.isNotEmpty()) {
                                        filesPageRequireImportFile.value = true
                                        currentHomeScreen.intValue = Cons.selectedItem_Files  
                                        changeStateTriggerRefreshPage(needRefreshFilesPage)
                                        importFiles = true

                                    }
                                    if(importFiles) {
                                        return@doJobThenOffLoading
                                    }
                                    val startPage = extras.getString(IntentCons.ExtrasKey.startPage) ?: ""
                                    val startRepoId = extras.getString(IntentCons.ExtrasKey.startRepoId) ?: ""
                                    if (startPage.isNotBlank()) {
                                        if (startPage == Cons.selectedItem_ChangeList.toString()) {
                                            var startRepo = changeListCurRepo.value
                                            if (startRepoId.isNotBlank()) {  
                                                startRepo = AppModel.dbContainer.repoRepository.let { it.getById(startRepoId) ?: it.getByName(startRepoId) ?: startRepo }
                                            }
                                            goToChangeListPage(startRepo)
                                        }else if(startPage == Cons.selectedItem_Repos.toString()) {
                                            goToRepoPage(startRepoId)
                                        }else if(startPage == Cons.selectedItem_Service.toString()) {
                                            goToServicePage()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    MyLog.e(TAG, "#LaunchedEffect: init home err: " + e.stackTraceToString())
                    Msg.requireShowLongDuration("init home err: " + e.localizedMessage)
                }
            }
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        doJobThenOffLoading {
            Cache.clearAllSubPagesStates()
        }
    }
}
