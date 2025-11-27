package com.akcreation.gitsilent.screen

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dto.FileItemDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.FilesInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.FilesPageActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.FilesTitle
import com.akcreation.gitsilent.screen.functions.getFilesGoToPath
import com.akcreation.gitsilent.screen.shared.FileChooserType
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import java.io.File

private const val TAG = "FileChooserScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileChooserScreen(
    type: FileChooserType,
    naviUp: () -> Unit
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val isFileChooser = remember { true }
    val updateSelectedPath = { path:String->
        if(type == FileChooserType.SINGLE_DIR) {
            SharedState.fileChooser_DirPath.value = path
        }else {
            SharedState.fileChooser_FilePath.value = path
        }
    }
    val activityContext = LocalContext.current
    val allRepoParentDir = AppModel.allRepoParentDir
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val scope = rememberCoroutineScope()
    val settings = remember {SettingsUtil.getSettingsSnapshot()}
    val needRefreshFilesPage = rememberSaveable { mutableStateOf("") }
    val refreshPage = {
        changeStateTriggerRefreshPage(needRefreshFilesPage)
    }
    val filesPageIsFileSelectionMode = rememberSaveable { mutableStateOf(false)}
    val filesPageIsPasteMode = rememberSaveable { mutableStateOf(false)}
    val filesPageSelectedItems = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageSelectedItems", initValue = listOf<FileItemDto>())
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
    val filesPageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons)}
    val filesPageListState = mutableCustomStateOf(stateKeyTag, "filesPageListState", initValue = LazyListState(0,0))
    val filesPageSimpleFilterOn = rememberSaveable { mutableStateOf(false)}
    val filesPageSimpleFilterKeyWord = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filesPageSimpleFilterKeyWord",
        initValue = TextFieldValue("")
    )
    val filesPageCurrentPath = rememberSaveable {
        val initPath = runCatching {
            val tmpPath = if(type == FileChooserType.SINGLE_DIR) {
                    SharedState.fileChooser_DirPath.value
                }else {
                    SharedState.fileChooser_FilePath.value
                }
            if(tmpPath.isBlank()) {
                ""
            }else {
                File(tmpPath).let { (if(it.isFile) it.canonicalFile.parent else it.canonicalPath) ?: "" }
            }
        }.getOrDefault("");
        initPath.ifBlank { settings.files.lastOpenedPath.ifBlank { FsUtils.getExternalStorageRootPathNoEndsWithSeparator() } }
        mutableStateOf(initPath)
    }
    val filesGetCurrentPath = {
        filesPageCurrentPath.value
    }
    val filesUpdateCurrentPath = { path:String ->
        filesPageCurrentPath.value = path
    }
    val filesPageLastPathByPressBack = rememberSaveable { mutableStateOf("")}
    val showCreateFileOrFolderDialog = rememberSaveable { mutableStateOf(false)}
    val filesPageCurPathFileItemDto = mutableCustomStateOf(stateKeyTag, "filesPageCurPathFileItemDto") { FileItemDto() }
    val filesPageCurrentPathBreadCrumbList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageCurrentPathBreadCrumbList", initValue = listOf<FileItemDto>())
    val filesPageKeepFilterResultOnce = rememberSaveable { mutableStateOf(false) }
    val filesLastPosition = rememberSaveable { mutableStateOf(0) }
    val fileListFilterLastPosition = rememberSaveable { mutableStateOf(0) }
    val filesPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val filesPageFilterList = mutableCustomStateListOf(stateKeyTag, "filesPageFilterList", listOf<FileItemDto>())
    val filesFilterListState = rememberLazyListState()
    val filesPageRequireImportFile = rememberSaveable { mutableStateOf( false)}
    val filesPageRequireImportUriList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageRequireImportUriList", initValue = listOf<Uri>())
    val filesPageCurrentPathFileList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageCurrentPathFileList", initValue = listOf<FileItemDto>()) 
    val filesPageRequestFromParent = rememberSaveable { mutableStateOf("")}
    val filesPageCheckOnly = rememberSaveable { mutableStateOf(false)}
    val filesPageSelectedRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "filesPageSelectedRepo", RepoEntity(id="") )
    val currentHomeScreen = rememberSaveable { mutableIntStateOf(Cons.selectedItem_Files) }
    val editorPageShowingFilePath = rememberSaveable { mutableStateOf(FilePath("")) }
    val editorPageShowingFileIsReady = rememberSaveable { mutableStateOf(false) }
    val requireInnerEditorOpenFile = {filepath:String, expectReadOnly:Boolean ->}
    val filesPageErrScrollState = rememberScrollState()
    val filesErrLastPosition = rememberSaveable { mutableStateOf(0) }
    val filesPageOpenDirErr = rememberSaveable { mutableStateOf("") }
    val filesPageGetErr = { filesPageOpenDirErr.value }
    val filesPageSetErr = { errMsg:String -> filesPageOpenDirErr.value = errMsg }
    val filesPageHasErr = { filesPageOpenDirErr.value.isNotBlank() }
    val filesGoToPath = getFilesGoToPath(filesPageLastPathByPressBack, filesGetCurrentPath, filesUpdateCurrentPath, needRefreshFilesPage)
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
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
                },
                navigationIcon = {
                    if(filesPageGetFilterMode() != 0 || filesPageSimpleFilterOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon =  Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),
                        ) {
                            resetFilesSearchVars()
                            filesPageSimpleFilterOn.value = false
                        }
                    }else {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.back),
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            iconContentDesc = stringResource(R.string.back),
                        ) {
                            naviUp()
                        }
                    }
                },
                actions = {
                    FilesPageActions(
                        isFileChooser = isFileChooser,
                        refreshPage = refreshPage,
                        filterOn = filesPageFilterOn,
                        filesPageGetFilterMode = filesPageGetFilterMode,
                        doFilter = filesPageDoFilter,
                        requestFromParent = filesPageRequestFromParent,
                        filesPageSimpleFilterOn = filesPageSimpleFilterOn,
                        filesPageSimpleFilterKeyWord = filesPageSimpleFilterKeyWord
                    )
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(filesPageScrolled.value) {
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
            }
        }
    ) { contentPadding ->
        FilesInnerPage(
            stateKeyTag = stateKeyTag,
            errScrollState = filesPageErrScrollState,
            getErr = filesPageGetErr,
            setErr = filesPageSetErr,
            hasErr = filesPageHasErr,
            naviUp = naviUp,
            updateSelectedPath = updateSelectedPath,
            isFileChooser = isFileChooser,
            fileChooserType = type,
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
            showCreateFileOrFolderDialog=showCreateFileOrFolderDialog,
            requireImportFile=filesPageRequireImportFile,
            requireImportUriList=filesPageRequireImportUriList,
            filesPageGetFilterMode=filesPageGetFilterMode,
            filesPageFilterKeyword=filesPageFilterKeyword,
            filesPageFilterModeOff = filesPageFilterOff,
            currentPathFileList = filesPageCurrentPathFileList,
            updateCurrentPath = filesUpdateCurrentPath,
            filesPageRequestFromParent = filesPageRequestFromParent,
            requireInnerEditorOpenFile = requireInnerEditorOpenFile,
            filesPageSimpleFilterOn = filesPageSimpleFilterOn,
            filesPageSimpleFilterKeyWord = filesPageSimpleFilterKeyWord,
            filesPageScrolled = filesPageScrolled,
            curListState = filesPageListState,
            filterListState = filesFilterListState,
            openDrawer = {},
            isFileSelectionMode= filesPageIsFileSelectionMode,
            isPasteMode = filesPageIsPasteMode,
            selectedItems = filesPageSelectedItems,
            checkOnly = filesPageCheckOnly,
            selectedRepo = filesPageSelectedRepo,
            goToRepoPage={},
            goToChangeListPage={},
            lastPathByPressBack=filesPageLastPathByPressBack,
            curPathFileItemDto=filesPageCurPathFileItemDto,
            currentPathBreadCrumbList=filesPageCurrentPathBreadCrumbList,
            enableFilterState = filesPageEnableFilterState,
            filterList = filesPageFilterList,
            lastPosition = filesLastPosition,
            keepFilterResultOnce = filesPageKeepFilterResultOnce,  
            goToPath = filesGoToPath
        )
    }
}
