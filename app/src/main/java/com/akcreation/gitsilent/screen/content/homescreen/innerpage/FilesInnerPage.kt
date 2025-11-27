package com.akcreation.gitsilent.screen.content.homescreen.innerpage

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.akcreation.gitsilent.compose.ApplyPatchDialog
import com.akcreation.gitsilent.compose.BottomBar
import com.akcreation.gitsilent.compose.CardButton
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.CopyScrollableColumn
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CreateFileOrFolderDialog2
import com.akcreation.gitsilent.compose.DefaultPaddingText
import com.akcreation.gitsilent.compose.GitIgnoreDialog
import com.akcreation.gitsilent.compose.GrantManageStoragePermissionClickableText
import com.akcreation.gitsilent.compose.LoadingTextCancellable
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.compose.MyCheckBox2
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.OpenAsDialog
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.SingleLineCardButton
import com.akcreation.gitsilent.compose.SingleSelection
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.DevFeature
import com.akcreation.gitsilent.dev.applyPatchTestPassed
import com.akcreation.gitsilent.dev.importReposFromFilesTestPassed
import com.akcreation.gitsilent.dev.initRepoFromFilesPageTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dto.FileItemDto
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.IgnoreItem
import com.akcreation.gitsilent.git.ImportRepoResult
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.FileListItem
import com.akcreation.gitsilent.screen.content.listitem.SelectedFileItemsDialog
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.goToFileHistory
import com.akcreation.gitsilent.screen.functions.initSearch
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.FileChooserType
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.DirViewAndSort
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.settings.enums.dirviewandsort.SortMethod
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.DirSearchUtil
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.FsUtils.PasteResult
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.RegexUtil
import com.akcreation.gitsilent.utils.StateRequestType
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.cache.ThumbCache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.checkFileOrFolderNameAndTryCreateFile
import com.akcreation.gitsilent.utils.compareStringAsNumIfPossible
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJob
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.filterAndMap
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import com.akcreation.gitsilent.utils.getFileExtOrEmpty
import com.akcreation.gitsilent.utils.getFileNameFromCanonicalPath
import com.akcreation.gitsilent.utils.getFileNameOrEmpty
import com.akcreation.gitsilent.utils.getFilePathUnderParent
import com.akcreation.gitsilent.utils.getHumanReadableSizeStr
import com.akcreation.gitsilent.utils.getRangeForRenameFile
import com.akcreation.gitsilent.utils.getRequestDataByState
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.getViewAndSortForPath
import com.akcreation.gitsilent.utils.isPathExists
import com.akcreation.gitsilent.utils.mime.MimeType
import com.akcreation.gitsilent.utils.mime.guessFromFileName
import com.akcreation.gitsilent.utils.mime.intentType
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.requestStoragePermissionIfNeed
import com.akcreation.gitsilent.utils.saf.MyOpenDocumentTree
import com.akcreation.gitsilent.utils.saf.SafAndFileCmpUtil
import com.akcreation.gitsilent.utils.saf.SafAndFileCmpUtil.OpenInputStreamFailed
import com.akcreation.gitsilent.utils.saf.SafUtil
import com.akcreation.gitsilent.utils.showToast
import com.akcreation.gitsilent.utils.state.CustomStateListSaveable
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.state.mutableCustomBoxOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.storagepaths.StoragePaths
import com.akcreation.gitsilent.utils.storagepaths.StoragePathsMan
import com.akcreation.gitsilent.utils.trimLineBreak
import com.akcreation.gitsilent.utils.withMainContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "FilesInnerPage"
private const val showImportForBottomBar = false
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesInnerPage(
    stateKeyTag:String,
    errScrollState: ScrollState,
    getErr: ()->String,
    setErr: (String)->Unit,
    hasErr:()->Boolean,  
    naviUp:()->Unit,
    updateSelectedPath:(path:String) -> Unit,
    isFileChooser:Boolean,
    fileChooserType:FileChooserType,  
    contentPadding: PaddingValues,
    currentHomeScreen: MutableIntState,
    editorPageShowingFilePath: MutableState<FilePath>,
    editorPageShowingFileIsReady: MutableState<Boolean>,
    needRefreshFilesPage: MutableState<String>,
    currentPath: ()->String,
    updateCurrentPath:(String)->Unit,
    showCreateFileOrFolderDialog: MutableState<Boolean>,
    requireImportFile: MutableState<Boolean>,
    requireImportUriList: CustomStateListSaveable<Uri>,
    filesPageGetFilterMode:()->Int,
    filesPageFilterKeyword:CustomStateSaveable<TextFieldValue>,
    filesPageFilterModeOff:()->Unit,
    currentPathFileList:CustomStateListSaveable<FileItemDto>,
    filesPageRequestFromParent:MutableState<String>,
    requireInnerEditorOpenFile:(filePath:String, expectReadOnly:Boolean)->Unit,
    filesPageSimpleFilterOn:MutableState<Boolean>,
    filesPageSimpleFilterKeyWord:CustomStateSaveable<TextFieldValue>,
    filesPageLastKeyword:MutableState<String>,
    filesPageSearchToken:MutableState<String>,
    filesPageSearching:MutableState<Boolean>,
    resetFilesSearchVars:()->Unit,
    filesPageScrolled:MutableState<Boolean>,
    curListState:CustomStateSaveable<LazyListState>,
    filterListState:LazyListState,
    openDrawer:()->Unit,
    isFileSelectionMode:MutableState<Boolean>,
    isPasteMode:MutableState<Boolean>,
    selectedItems:CustomStateListSaveable<FileItemDto>,
    checkOnly:MutableState<Boolean>,
    selectedRepo:CustomStateSaveable<RepoEntity>,
    goToRepoPage:(targetIdIfHave:String)->Unit,
    goToChangeListPage:(repoWillShowInChangeListPage:RepoEntity)->Unit,
    lastPathByPressBack: MutableState<String>,
    curPathFileItemDto:CustomStateSaveable<FileItemDto>,
    currentPathBreadCrumbList:CustomStateListSaveable<FileItemDto>,
    enableFilterState:MutableState<Boolean>,
    filterList:CustomStateListSaveable<FileItemDto>,
    lastPosition:MutableState<Int>,
    keepFilterResultOnce:MutableState<Boolean>,
    goToPath:(String)->Unit,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val inDarkTheme = Theme.inDarkTheme
    val allRepoParentDir = AppModel.allRepoParentDir;
    val activityContext = LocalContext.current;
    val exitApp = AppModel.exitApp
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val settingsSnapshot = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "settingsSnapshot") {
        val s = SettingsUtil.getSettingsSnapshot()
        filesPageScrolled.value = s.showNaviButtons
        s
    }
    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val fileAlreadyExistStrRes = stringResource(R.string.file_already_exists)
    val successStrRes = stringResource(R.string.success)
    val errorStrRes = stringResource(R.string.error)
    val defaultLoadingText = activityContext.getString(R.string.loading)
    val isLoading = rememberSaveable { mutableStateOf(false) }
    val loadingText = rememberSaveable { mutableStateOf(defaultLoadingText) }
    val loadingOn = { msg: String ->
        loadingText.value = msg
        isLoading.value = true
    }
    val loadingOff = {
        isLoading.value = false
        loadingText.value = defaultLoadingText
    }
    val requireCancelAct = rememberSaveable { mutableStateOf(false) }
    val cancellableActRunning = rememberSaveable { mutableStateOf(false) }
    val resetAct = {
        requireCancelAct.value = false
    }
    val cancelAct = {
        requireCancelAct.value = true
    }
    val startCancellableAct = {
        resetAct()
        cancellableActRunning.value = true
    }
    val stopCancellableAct = {
        cancellableActRunning.value = false
        resetAct()
    }
    val loadingOnCancellable: suspend (String) -> Unit = { loadingText: String ->
        withMainContext {
            loadingOn(loadingText)
            startCancellableAct()
        }
    }
    val loadingOffCancellable: suspend () -> Unit = {
        withMainContext {
            loadingOff()
            stopCancellableAct()
        }
    }
    val containsForSelected = { srcList: List<FileItemDto>, item: FileItemDto ->
        srcList.indexOfFirst { it.equalsForSelected(item) } != -1
    }
    val filesPageQuitSelectionMode = {
        isFileSelectionMode.value = false  
        isPasteMode.value = false  
        selectedItems.value.clear()  
    }
    val switchItemSelected = { item: FileItemDto ->
        isFileSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseRemove(item, selectedItems.value, contains = containsForSelected)
    }
    val selectItem = { item: FileItemDto ->
        isFileSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseNoop(item, selectedItems.value, contains = containsForSelected)
    }
    val getSelectedFilesCount = {
        selectedItems.value.size
    }
    val isItemInSelected = { f: FileItemDto ->
        selectedItems.value.indexOfFirst { it.equalsForSelected(f) } != -1
    }
    val renameFileItemDto = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "renameFileItemDto",
        initValue = FileItemDto()
    )
    val renameFileName = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "renameFileName",
        initValue = TextFieldValue("")
    )
    val renameHasErr = rememberSaveable { mutableStateOf(false) }
    val renameErrText = rememberSaveable { mutableStateOf("") }
    val showRenameDialog = rememberSaveable { mutableStateOf(false) }
    val updateRenameFileName: (TextFieldValue) -> Unit = {
        val newVal = it
        val oldVal = renameFileName.value
        if (oldVal.text != newVal.text) {
            renameHasErr.value = false
        }
        renameFileName.value = newVal
    }
    val showGoToPathDialog = rememberSaveable { mutableStateOf(false) }
    val pathToGo = mutableCustomStateOf(stateKeyTag, "pathToGo") { TextFieldValue("") }
    if (showGoToPathDialog.value) {
        val focusRequester = remember { FocusRequester() }
        val goToDialogOnOk = {
            showGoToPathDialog.value = false
            val pathToGoRaw = pathToGo.value.text
            val currentPath = currentPath()
            doJobThenOffLoading {
                val pathToGo = trimLineBreak(pathToGoRaw)
                val pathToGoRaw = Unit 
                val finallyPath = FsUtils.internalExternalPrefixPathToRealPath(pathToGo)
                val f = File(finallyPath)
                if (f.canRead()) {
                    goToPath(f.canonicalPath)
                } else { 
                    val f = File(currentPath, pathToGo)
                    if (f.canRead()) {
                        goToPath(f.canonicalPath)
                    } else {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.cant_read_path))
                    }
                }
            }
            Unit
        }
        ConfirmDialog(
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onPreviewKeyEvent { event ->
                                if (event.type != KeyEventType.KeyDown) {
                                    false
                                } else if (event.key == Key.Enter) {
                                    goToDialogOnOk()
                                    true
                                } else {
                                    false
                                }
                            },
                        value = pathToGo.value,
                        onValueChange = {
                            pathToGo.value = it
                        },
                        label = {
                            Text(stringResource(R.string.path))
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            goToDialogOnOk()
                        })
                    )
                }
            },
            okBtnEnabled = pathToGo.value.text.isNotBlank(),
            okBtnText = stringResource(id = R.string.go),
            cancelBtnText = stringResource(id = R.string.cancel),
            title = stringResource(R.string.go_to),
            onCancel = { showGoToPathDialog.value = false }
        ) {
            goToDialogOnOk()
        }
        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }
    val showApplyAsPatchDialog = rememberSaveable { mutableStateOf(false) }
    val errMsgForApplyPatch = rememberSaveable { mutableStateOf("") }
    val loadingRepoList = rememberSaveable { mutableStateOf(false) }
    val loadingRepoListJob = mutableCustomBoxOf<Job?>(stateKeyTag, "loadingRepoListJob") { null }  
    val fileFullPathForApplyAsPatch = rememberSaveable { mutableStateOf("") }
    val allRepoList = mutableCustomStateListOf(stateKeyTag, "allRepoList", listOf<RepoEntity>())
    val initApplyAsPatchDialog = { item: FileItemDto ->
        errMsgForApplyPatch.value = ""
        val patchFileFullPath = item.fullPath
        val parentName = item.toFile().parentFile?.name ?: ""
        loadingRepoList.value = true
        loadingRepoListJob.value = doJob job@{
            val result = runCatching {
                val repoDb = AppModel.dbContainer.repoRepository
                val listFromDb = repoDb.getReadyRepoList(requireSyncRepoInfoWithGit = false)
                delay(1)  
                allRepoList.value.apply {
                    clear()
                    addAll(listFromDb)
                }
                if (listFromDb.isNotEmpty()) {
                    val selectedRepoId = selectedRepo.value.id
                    val repoNameMatchedDirNameIdx = listFromDb.indexOfFirst { it.repoName == parentName }.let { if (it < 0) listFromDb.indexOfFirst { selectedRepoId == it.id } else it }
                    selectedRepo.value = listFromDb[repoNameMatchedDirNameIdx.coerceAtLeast(0)]
                    delay(1)
                }
                loadingRepoList.value = false
            }
            if (result.isFailure) {
                val errMsg = result.exceptionOrNull()?.localizedMessage ?: "load repos err"
                errMsgForApplyPatch.value = errMsg
                loadingRepoList.value = false
                MyLog.e(TAG, "loading repo list for ApplyAsPatchDialog err: $errMsg")
            }
        }
        fileFullPathForApplyAsPatch.value = patchFileFullPath
        showApplyAsPatchDialog.value = true
    }
    if (showApplyAsPatchDialog.value) {
        ApplyPatchDialog(
            errMsg = errMsgForApplyPatch.value,
            checkOnly = checkOnly,
            selectedRepo = selectedRepo,
            patchFileFullPath = fileFullPathForApplyAsPatch.value,
            repoList = allRepoList.value,
            loadingRepoList = loadingRepoList.value,
            onCancel = {
                showApplyAsPatchDialog.value = false;
                runCatching { loadingRepoListJob.value?.cancel() }
            },
            onErrCallback = { e, selectedRepoId ->
                val errMsgPrefix = "apply patch err: err="
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(selectedRepoId, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#ApplyPatchDialog err: $errMsgPrefix${e.stackTraceToString()}")
            },
            onFinallyCallback = {
                showApplyAsPatchDialog.value = false
                if (enableFilterState.value.not()) {
                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                }
            },
            onOkCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        )
    }
    val showOpenAsDialog = rememberSaveable { mutableStateOf(false) }
    val readOnlyForOpenAsDialog = rememberSaveable { mutableStateOf(false) }
    val openAsDialogFilePath = rememberSaveable { mutableStateOf("") }
    val showOpenInEditor = rememberSaveable { mutableStateOf(false) }
    val fileNameForOpenAsDialog = remember { derivedStateOf { getFileNameFromCanonicalPath(openAsDialogFilePath.value) } }
    val initOpenAsDialog = { fileFullPath: String, showOpenInInnerTextEditor: Boolean ->
        openAsDialogFilePath.value = fileFullPath
        showOpenInEditor.value = showOpenInInnerTextEditor
        showOpenAsDialog.value = true
    }
    if (showOpenAsDialog.value) {
        OpenAsDialog(
            readOnly = readOnlyForOpenAsDialog, fileName = fileNameForOpenAsDialog.value, filePath = openAsDialogFilePath.value, showOpenInEditor = showOpenInEditor.value,
            openInEditor = { expectReadOnly: Boolean ->
                requireInnerEditorOpenFile(openAsDialogFilePath.value, expectReadOnly)
            }
        ) {
            showOpenAsDialog.value = false
        }
    }
    val fileMenuKeyTextList = listOf(
        if (isFileChooser) "" else stringResource(R.string.open),  
        if (isFileChooser) "" else stringResource(R.string.open_as),
        stringResource(R.string.rename),
        if (isFileChooser) "" else if (proFeatureEnabled(applyPatchTestPassed)) stringResource(R.string.apply_as_patch) else "",  
        if (isFileChooser) "" else stringResource(R.string.file_history),
        stringResource(R.string.copy_full_path),
        if (isFileChooser) "" else stringResource(R.string.copy_repo_relative_path),
        stringResource(R.string.details),
        stringResource(R.string.share),
        stringResource(R.string.delete),
    )
    val dirMenuKeyTextList = listOf(
        stringResource(R.string.rename),
        stringResource(R.string.copy_full_path),
        if (isFileChooser) "" else stringResource(R.string.copy_repo_relative_path),
        if (isFileChooser) "" else stringResource(R.string.import_as_repo),
        if (isFileChooser) "" else stringResource(R.string.init_repo),
        stringResource(R.string.add_storage_path),
        stringResource(R.string.details),
        stringResource(R.string.delete),
    )
    val copyThenShowCopied = { text: String ->
        clipboardManager.setText(AnnotatedString(text))
        Msg.requireShow(activityContext.getString(R.string.copied))
    }
    val copyRepoRelativePath = { realFullPath: String ->
        try {
            val repo = Libgit2Helper.findRepoByPath(realFullPath)
            if (repo == null) {
                Msg.requireShow(activityContext.getString(R.string.no_repo_found))
            } else {
                repo.use {
                    val realtivePath = Libgit2Helper.getRelativePathUnderRepo(Libgit2Helper.getRepoWorkdirNoEndsWithSlash(it), realFullPath)
                    if (realtivePath == null) {
                        Msg.requireShow(activityContext.getString(R.string.path_not_under_repo))
                    } else {
                        copyThenShowCopied(realtivePath)
                    }
                }
            }
        } catch (e: Exception) {
            Msg.requireShowLongDuration(e.localizedMessage ?: "err")
            MyLog.e(TAG, "#copyRepoRelativePath err: ${e.stackTraceToString()}")
        }
    }
    val showDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val details_ItemsSize = rememberSaveable { mutableLongStateOf(0L) }  
    val details_AllCount = rememberSaveable { mutableIntStateOf(0) }  
    val details_FilesCount = rememberSaveable { mutableIntStateOf(0) }  
    val details_FoldersCount = rememberSaveable { mutableIntStateOf(0) }  
    val details_CountingItemsSize = rememberSaveable { mutableStateOf(false) }  
    val details_itemList = mutableCustomStateListOf(stateKeyTag, "details_itemList", listOf<FileItemDto>())
    val initDetailsDialog = { list: List<FileItemDto> ->
        val list = if (list.size == 1 && list.first().isDir) {
            try {
                var filesCount = 0
                var folderCount = 0
                val first = list.first()
                first.toFile().listFiles()!!.forEachBetter {
                    if (it.isDirectory) {
                        folderCount++
                    } else {
                        filesCount++
                    }
                }
                listOf(first.copy(folderCount = folderCount, fileCount = filesCount))
            } catch (_: Exception) {
                list
            }
        } else {
            list
        }
        details_FoldersCount.intValue = list.count { it.isDir }
        details_FilesCount.intValue = list.size - details_FoldersCount.intValue
        details_AllCount.intValue = list.size
        doJobThenOffLoading {
            details_CountingItemsSize.value = true
            details_ItemsSize.longValue = 0
            list.forEachBetter {
                if (it.isDir) {
                    FsUtils.calculateFolderSize(it.toFile(), details_ItemsSize)
                } else {
                    details_ItemsSize.longValue += it.sizeInBytes
                }
            }
            details_CountingItemsSize.value = false
        }
        details_itemList.value.clear()
        details_itemList.value.addAll(list)
        showDetailsDialog.value = true
    }
    val showImportAsRepoDialog = rememberSaveable { mutableStateOf(false) }
    val importAsRepoList = mutableCustomStateListOf(stateKeyTag, "importAsRepoList", listOf<String>())
    val isReposParentFolderForImport = rememberSaveable { mutableStateOf(false) }
    val initImportAsRepoDialog = { fullPathList: List<String> ->
        importAsRepoList.value.clear()
        importAsRepoList.value.addAll(fullPathList)

        isReposParentFolderForImport.value = false
        showImportAsRepoDialog.value = true
    }
    val showInitRepoDialog = rememberSaveable { mutableStateOf(false) }
    val initRepoList = mutableCustomStateListOf(stateKeyTag, "initRepoList", listOf<String>())
    val initInitRepoDialog = { pathList: List<String> ->
        initRepoList.value.clear()
        initRepoList.value.addAll(pathList)
        showInitRepoDialog.value = true
    }
    fun addStoragePath(
        newPath: String,
        storagePaths: StoragePaths = StoragePathsMan.get(),
        callSave: Boolean = true,
        showMsg: Boolean = true,
    ) : Boolean {
        try {
            if (File(newPath).isDirectory.not()) {
                if(showMsg) {
                    Msg.requireShowLongDuration("err: " + activityContext.getString(R.string.path_is_not_a_dir))
                }
                return false
            }
            val currentList = storagePaths.storagePaths
            if (StoragePathsMan.allowAddPath(newPath) && !currentList.contains(newPath)) {
                currentList.add(newPath)
                if(callSave) {
                    StoragePathsMan.save(storagePaths)
                }
            }
            if(showMsg) {
                Msg.requireShow(activityContext.getString(R.string.success))
            }
            return true
        } catch (e: Exception) {
            if(showMsg) {
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            }
            MyLog.e(TAG, "add storage path '$newPath' at `$TAG` err: ${e.localizedMessage}")
            return false
        }
    }
    val showDelFileDialog = rememberSaveable { mutableStateOf(false) }
    val allCountForDelDialog = rememberSaveable { mutableStateOf(0) }
    val listForDeleteDialog = mutableCustomStateListOf(stateKeyTag, "listForDeleteDialog") { listOf<FileItemDto>() }
    val initDelFileDialog = { list: List<FileItemDto> ->
        allCountForDelDialog.value = list.size
        listForDeleteDialog.value.apply {
            clear()
            addAll(list)
        }
        showDelFileDialog.value = true
    }
    if (showDelFileDialog.value) {
        ConfirmDialog2(
            title = stringResource(id = R.string.delete),
            text = replaceStringResList(stringResource(R.string.n_items_will_be_deleted), listOf("" + allCountForDelDialog.value)),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDelFileDialog.value = false }
        ) {
            showDelFileDialog.value = false
            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff) {
                val targets = listForDeleteDialog.value.toList()
                if (targets.isEmpty()) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_item_selected))
                    return@doJobThenOffLoading  
                }
                targets.forEachBetter {
                    val file = File(it.fullPath)
                    file.deleteRecursively()
                }
                Msg.requireShow(activityContext.getString(R.string.success))
                filesPageQuitSelectionMode()
                if (enableFilterState.value) {  
                    val filterList = filterList.value
                    targets.forEachBetter { filterList.remove(it) }
                } else {
                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                }
            }
        }
    }
    val renameFile = { item: FileItemDto ->
        renameFileItemDto.value = item  
        renameFileName.value = TextFieldValue(item.name, selection = getRangeForRenameFile(item.name))  
        renameHasErr.value = false  
        renameErrText.value = ""  
        showRenameDialog.value = true  
    }
    val shareFiles = { files: List<FileItemDto> ->
        try {
            FsUtils.shareFiles(activityContext, files)
        }catch (e: Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "$TAG#shareFiles err: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
    val fileMenuKeyActList = listOf<(FileItemDto) -> Unit>(
        open@{ item: FileItemDto ->
            val expectReadOnly = false
            requireInnerEditorOpenFile(item.fullPath, expectReadOnly)
            Unit
        },
        openAs@{ item: FileItemDto ->
            val showInnerEditor = false
            initOpenAsDialog(item.fullPath, showInnerEditor)
            Unit
        },
        renameFile,
        applyAsPatch@{ item: FileItemDto ->
            initApplyAsPatchDialog(item)
        },
        fileHistory@{
            goToFileHistory(it.fullPath, activityContext)
        },
        copyFullPath@{
            copyThenShowCopied(it.fullPath)
        },
        copyRepoRelativePath@{
            copyRepoRelativePath(it.fullPath)
        },
        details@{
            initDetailsDialog(listOf(it))
        },
        share@{
            shareFiles(listOf(it))
        },
        delete@{
            initDelFileDialog(listOf(it))
        }
    )
    val dirMenuKeyActList = listOf(
        renameFile,
        copyFullPath@{
            copyThenShowCopied(it.fullPath)
        },
        copyRepoRelativePath@{
            copyRepoRelativePath(it.fullPath)
        },
        importAsRepo@{

            initImportAsRepoDialog(listOf(it.fullPath))
        },
        initRepo@{
            initInitRepoDialog(listOf(it.fullPath))
        },
        addStoragePath@{
            addStoragePath(it.fullPath)
            Unit
        },
        details@{
            initDetailsDialog(listOf(it))
        },
        delete@{
            initDelFileDialog(listOf(it))
        }
    )
    val isImportMode = rememberSaveable { mutableStateOf(false) }
    val showImportResultDialog = rememberSaveable { mutableStateOf(false) }
    val failedImportListStr = rememberSaveable { mutableStateOf("") }
    val successImportCount = rememberSaveable { mutableIntStateOf(0) }
    val failedImportCount = rememberSaveable { mutableIntStateOf(0) }
    val getListState: (String) -> LazyListState = { path: String ->
        Cache.getFilesListStateByPath(path)
    }
    val breadCrumbListState = rememberLazyListState()
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }
    val backHandlerOnBack = getBackHandler(
        naviUp = naviUp,
        isFileChooser = isFileChooser,
        appContext = activityContext,
        isFileSelectionMode = isFileSelectionMode,
        filesPageQuitSelectionMode = filesPageQuitSelectionMode,
        currentPath = currentPath,
        allRepoParentDir = allRepoParentDir,
        needRefreshFilesPage = needRefreshFilesPage,
        exitApp = exitApp,
        getFilterMode = filesPageGetFilterMode,
        filesPageFilterModeOff = filesPageFilterModeOff,
        filesPageSimpleFilterOn = filesPageSimpleFilterOn,
        openDrawer = openDrawer,
        goToPath = goToPath,
        resetSearchVars = resetFilesSearchVars,
        )
    BackHandler(enabled = isBackHandlerEnable.value, onBack = { backHandlerOnBack() })
    if (showImportResultDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.import_has_err),
            cancelBtnText = stringResource(R.string.close),
            showOk = false,
            requireShowTextCompose = true,
            textCompose = {
                MySelectionContainer {
                    ScrollableColumn {
                        Row {
                            Text(text = stringResource(R.string.import_success) + ": " + successImportCount.value)
                        }
                        Row {
                            Text(text = stringResource(R.string.import_failed) + ": " + failedImportCount.value)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.clickable {
                            clipboardManager.setText(AnnotatedString(failedImportListStr.value))
                            Msg.requireShow(activityContext.getString(R.string.copied))  
                        }
                        ) {
                            Text(
                                text = stringResource(R.string.you_can_click_here_copy_err_msg),
                                style = MyStyleKt.ClickableText.getStyle(),
                                color = MyStyleKt.ClickableText.getColor(),
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Text(text = stringResource(R.string.err_msg) + ":")
                        }
                        Row {
                            Text(text = failedImportListStr.value, color = MyStyleKt.TextColor.error())
                        }
                    }
                }
            },
            onCancel = { showImportResultDialog.value = false }
        ) {
            showImportResultDialog.value = false
        }
    }
    if (showRenameDialog.value) {
        val focusRequester = remember { FocusRequester() }
        val onOk = {
            try {
                val oldFileItemDto = renameFileItemDto.value 
                val renameFileItemDto = Unit 
                val newFileName = renameFileName.value.text
                val fileOrFolderNameCheckRet = checkFileOrFolderNameAndTryCreateFile(newFileName, activityContext)
                if (fileOrFolderNameCheckRet.hasError()) {  
                    renameHasErr.value = true
                    renameErrText.value = fileOrFolderNameCheckRet.msg
                } else if (newFileName == oldFileItemDto.name || isPathExists(File(oldFileItemDto.fullPath).parent, newFileName)) {
                    renameHasErr.value = true
                    renameErrText.value = activityContext.getString(R.string.file_already_exists)
                } else {  
                    showRenameDialog.value = false  
                    doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff) {
                        try {
                            val oldFile = File(oldFileItemDto.fullPath)
                            val newFile = File(File(oldFileItemDto.fullPath).parent, newFileName)
                            val renameSuccess = oldFile.renameTo(newFile)
                            if (renameSuccess) {
                                val newNameDto = FileItemDto.genFileItemDtoByFile(newFile, activityContext)
                                val selectedItems = selectedItems.value
                                selectedItems.toList().forEachIndexedBetter { idx, item ->
                                    if (item == oldFileItemDto) {
                                        selectedItems[idx] = newNameDto
                                    }
                                }
                                if (enableFilterState.value.not()) {
                                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                                } else {
                                    val filterList = filterList.value
                                    filterList.toList().forEachIndexedBetter { idx, item ->
                                        if (item == oldFileItemDto) {
                                            filterList[idx] = newNameDto
                                        }
                                    }
                                }
                                Msg.requireShow(activityContext.getString(R.string.success))
                            } else {
                                Msg.requireShow(activityContext.getString(R.string.error))
                            }
                        } catch (e: Exception) {
                            Msg.requireShowLongDuration("rename failed: " + e.localizedMessage)
                            MyLog.e(TAG, "rename failed: oldFile=${oldFileItemDto.fullPath}, newFileName=${newFileName}, err=${e.stackTraceToString()}")
                        }
                    }
                }
            } catch (outE: Exception) {
                renameHasErr.value = true
                renameErrText.value = outE.localizedMessage ?: errorStrRes
                MyLog.e(TAG, "#RenameDialog err: " + outE.stackTraceToString())
            }
        }
        ConfirmDialog(
            okBtnEnabled = !renameHasErr.value,
            cancelBtnText = stringResource(id = R.string.cancel),
            okBtnText = stringResource(id = R.string.ok),
            title = stringResource(R.string.rename),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MyStyleKt.defaultItemPadding)
                            .focusRequester(focusRequester),
                        value = renameFileName.value,
                        singleLine = true,
                        isError = renameHasErr.value,
                        supportingText = {
                            if (renameHasErr.value) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = renameErrText.value,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (renameHasErr.value) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = renameErrText.value,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        onValueChange = {
                            updateRenameFileName(it)
                        },
                        label = {
                            Text(stringResource(R.string.file_name))
                        },
                        placeholder = {
                            Text(stringResource(R.string.input_new_file_name))
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            onOk()
                        }),
                    )
                }
            },
            onCancel = { showRenameDialog.value = false }
        ) {
            onOk()
        }
        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }
    val createFileOrFolderErrMsg = rememberSaveable { mutableStateOf("") }
    val fileNameForCreateDialog = mutableCustomStateOf(stateKeyTag, "fileNameForCreateDialog") { TextFieldValue("") }
    if (showCreateFileOrFolderDialog.value) {
        CreateFileOrFolderDialog2(
            fileName = fileNameForCreateDialog,
            errMsg = createFileOrFolderErrMsg,
            onOk = f@{ fileOrFolderName: String, isDir: Boolean ->
                try {
                    if (!File(currentPath()).exists()) {
                        throw RuntimeException(activityContext.getString(R.string.current_dir_doesnt_exist_anymore))
                    }
                    val fileOrFolderNameCheckRet = checkFileOrFolderNameAndTryCreateFile(fileOrFolderName, activityContext)
                    if (fileOrFolderNameCheckRet.hasError()) {
                        createFileOrFolderErrMsg.value = fileOrFolderNameCheckRet.msg
                        return@f false
                    } else {  
                        val file = File(currentPath(), fileOrFolderName)
                        if (file.exists()) {  
                            createFileOrFolderErrMsg.value = fileAlreadyExistStrRes
                            return@f false
                        } else {  
                            val isCreateSuccess = if (isDir) {  
                                file.mkdir()  
                            } else {  
                                file.createNewFile()
                            }
                            if (isCreateSuccess) {  
                                Msg.requireShow(successStrRes)  
                                createFileOrFolderErrMsg.value = ""  
                                fileNameForCreateDialog.value = TextFieldValue("")  
                                if (isDir) {
                                    goToPath(file.canonicalPath)
                                } else {
                                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                                }
                                return@f true
                            } else { 
                                createFileOrFolderErrMsg.value = errorStrRes  
                                return@f false
                            }
                        }
                    }
                } catch (e: Exception) {
                    createFileOrFolderErrMsg.value = e.localizedMessage ?: errorStrRes
                    MyLog.e(TAG, "CreateFileOrFolderDialog in Files Page err: " + e.stackTraceToString())
                    return@f false
                }
            },
            onCancel = {
                showCreateFileOrFolderDialog.value = false
                createFileOrFolderErrMsg.value = ""
            }
        )
    }
    val findRepoThenGoToReposOrChangList = { fullPath: String, trueGoToReposFalseGoToChangeList: Boolean ->
        doJobThenOffLoading job@{
            try {
                val repo = Libgit2Helper.findRepoByPath(fullPath)
                if (repo == null) {
                    Msg.requireShow(activityContext.getString(R.string.not_found))
                } else {
                    val repoWorkDir = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                    val onlyReturnReadyRepo = !trueGoToReposFalseGoToChangeList  
                    val target = AppModel.dbContainer.repoRepository.getByFullSavePath(
                        repoWorkDir,
                        onlyReturnReadyRepo = onlyReturnReadyRepo,
                        requireSyncRepoInfoWithGit = false
                    )
                    if (target == null) {
                        Msg.requireShow(activityContext.getString(R.string.not_found))
                    } else {
                        if (trueGoToReposFalseGoToChangeList) {
                            goToRepoPage(target.id)
                        } else {
                            goToChangeListPage(target)
                        }
                    }
                }
            } catch (e: Exception) {
                Msg.requireShowLongDuration(e.localizedMessage ?: "err")
                MyLog.e(TAG, "#findRepoThenGoToReposOrChangList err: fullPath=$fullPath, trueGoToReposFalseGoToChangeList=$trueGoToReposFalseGoToChangeList, err=${e.localizedMessage}")
            }
        }
    }
    val showInRepos = { fullPath: String ->
        findRepoThenGoToReposOrChangList(fullPath, true)
    }
    val showInChangeList = { fullPath: String ->
        findRepoThenGoToReposOrChangList(fullPath, false)
    }
    val showViewAndSortDialog = rememberSaveable { mutableStateOf(false) }
    val viewAndSortState = mutableCustomStateOf(stateKeyTag, "viewAndSortState") { settingsSnapshot.value.files.defaultViewAndSort }
    val viewAndSortStateBuf = mutableCustomStateOf(stateKeyTag, "viewAndSortStateBuf") { settingsSnapshot.value.files.defaultViewAndSort }
    val onlyForThisFolderState = rememberSaveable { mutableStateOf(false) }
    val onlyForThisFolderStateBuf = rememberSaveable { mutableStateOf(false) }
    if (showViewAndSortDialog.value) {
        val height = 10.dp
        ConfirmDialog2(
            title = stringResource(R.string.view_and_sort),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    SingleSelection(
                        itemList = SortMethod.entries,
                        selected = { idx, item -> viewAndSortStateBuf.value.sortMethod == item.code },
                        text = { idx, item -> SortMethod.getText(item, activityContext) },
                        onClick = { idx, item -> viewAndSortStateBuf.value = viewAndSortStateBuf.value.copy(sortMethod = item.code) },
                        minHeight = MyStyleKt.RadioOptions.middleHeight,
                    )
                    Spacer(Modifier.height(height))
                    MyCheckBox2(stringResource(R.string.ascend), viewAndSortStateBuf.value.ascend) { newValue ->
                        viewAndSortStateBuf.value = viewAndSortStateBuf.value.copy(ascend = newValue)
                    }
                    MyCheckBox2(stringResource(R.string.folder_first), viewAndSortStateBuf.value.folderFirst) { newValue ->
                        viewAndSortStateBuf.value = viewAndSortStateBuf.value.copy(folderFirst = newValue)
                    }
                    Spacer(Modifier.height(height))
                    MyHorizontalDivider()
                    Spacer(Modifier.height(height))
                    MyCheckBox(stringResource(R.string.only_for_this_folder), onlyForThisFolderStateBuf)
                }
            },
            onCancel = { showViewAndSortDialog.value = false }
        ) {
            showViewAndSortDialog.value = false
            doJobThenOffLoading {
                if (onlyForThisFolderStateBuf.value != onlyForThisFolderState.value || viewAndSortStateBuf.value.equals(viewAndSortState.value).not()) {
                    onlyForThisFolderState.value = onlyForThisFolderStateBuf.value
                    val newViewAndSort = viewAndSortStateBuf.value.copy()
                    viewAndSortState.value = newViewAndSort.copy()
                    settingsSnapshot.value = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                        if (onlyForThisFolderStateBuf.value) {
                            it.files.dirAndViewSort_Map.set(currentPath(), newViewAndSort)
                        } else {  
                            it.files.dirAndViewSort_Map.remove(currentPath())
                            it.files.defaultViewAndSort = newViewAndSort
                        }
                    }!!
                }
                changeStateTriggerRefreshPage(needRefreshFilesPage)
            }
        }
    }
    PullToRefreshBox(
        contentPadding = contentPadding,
        onRefresh = { changeStateTriggerRefreshPage(needRefreshFilesPage) },
        ) {
        if (isLoading.value) {
            LoadingTextCancellable(
                text = loadingText.value,
                contentPadding = contentPadding,
                showCancel = cancellableActRunning.value,
                onCancel = cancelAct,  
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                if (currentPathBreadCrumbList.value.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(5.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(5.dp),
                        state = breadCrumbListState,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val breadList = currentPathBreadCrumbList.value.toList()
                        breadList.forEachIndexedBetter { idx, it ->
                            item {
                                val separator = Cons.slash
                                val breadCrumbDropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
                                val curItemIsRoot = idx == 0  
                                val textColor = if (it.fullPath.startsWith(currentPath() + separator)) Color.Gray else Color.Unspecified
                                if (curItemIsRoot.not()) {
                                    Text(text = Cons.arrowToRight, color = textColor, fontWeight = FontWeight.Light)
                                }
                                Text(
                                    text = it.name,
                                    color = textColor,
                                    fontWeight = if (it.fullPath == currentPath()) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier
                                        .combinedClickable(
                                            onLongClick = {  
                                                breadCrumbDropDownMenuExpandState.value = true
                                            }
                                        ) { 
                                            filesPageSimpleFilterKeyWord.value = TextFieldValue("")
                                            goToPath(it.fullPath)
                                        }
                                        .padding(horizontal = 10.dp)  
                                )
                                if (breadCrumbDropDownMenuExpandState.value) {
                                    Column {
                                        val enableMenuItem = true
                                        DropdownMenu(
                                            offset = DpOffset(x = 0.dp, y = 20.dp),
                                            expanded = breadCrumbDropDownMenuExpandState.value,
                                            onDismissRequest = { breadCrumbDropDownMenuExpandState.value = false }
                                        ) {
                                            DropdownMenuItem(
                                                enabled = enableMenuItem,
                                                text = { Text(stringResource(R.string.copy_full_path)) },
                                                onClick = {
                                                    breadCrumbDropDownMenuExpandState.value = false
                                                    copyThenShowCopied(it.fullPath)
                                                }
                                            )
                                            if (isFileChooser.not()) {
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.copy_repo_relative_path)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        copyRepoRelativePath(it.fullPath)
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.import_as_repo)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        initImportAsRepoDialog(listOf(it.fullPath))
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.init_repo)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        initInitRepoDialog(listOf(it.fullPath))
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.show_in_repos)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        showInRepos(it.fullPath)
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.show_in_changelist)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        showInChangeList(it.fullPath)
                                                    }
                                                )
                                            }
                                            DropdownMenuItem(
                                                enabled = enableMenuItem,
                                                text = { Text(stringResource(R.string.add_storage_path)) },
                                                onClick = {
                                                    breadCrumbDropDownMenuExpandState.value = false
                                                    addStoragePath(it.fullPath)
                                                }
                                            )
                                            DropdownMenuItem(
                                                enabled = enableMenuItem,
                                                text = { Text(stringResource(R.string.details)) },
                                                onClick = {
                                                    breadCrumbDropDownMenuExpandState.value = false
                                                    initDetailsDialog(listOf(FileItemDto.genFileItemDtoByFile(File(it.fullPath), activityContext)))
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    val scrollToCurPath = remember {
                        derivedStateOf {
                            val indexOfCurPath = currentPathBreadCrumbList.value.indexOfFirst { it.fullPath == currentPath() }
                            if (indexOfCurPath != -1) {
                                UIHelper.scrollToItem(scope, breadCrumbListState, indexOfCurPath - 2)
                            }
                        }
                    }.value;  
                }
                val folderIsEmpty = currentPathFileList.value.isEmpty()
                val hasErr = hasErr()
                if (hasErr || folderIsEmpty) {
                    Column(
                        modifier = Modifier
                            .baseVerticalScrollablePageModifier(contentPadding, errScrollState)
                            .padding(MyStyleKt.defaultItemPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        MySelectionContainer {
                            if (hasErr) {
                                Text(getErr(), color = MyStyleKt.TextColor.error())
                            } else if (folderIsEmpty) {
                                Text(stringResource(R.string.folder_is_empty))
                            }  
                        }
                    }
                } else {
                    val keyword = filesPageSimpleFilterKeyWord.value.text  
                    val enableFilter = filterModeActuallyEnabled(filterOn = filesPageSimpleFilterOn.value, keyword = keyword)
                    val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
                    val currentPathFileList = filterTheList(
                        needRefresh = filterResultNeedRefresh.value,
                        lastNeedRefresh = lastNeedRefresh,
                        enableFilter = enableFilter,
                        keyword = keyword,
                        lastKeyword = filesPageLastKeyword,
                        searching = filesPageSearching,
                        token = filesPageSearchToken,
                        activityContext = activityContext,
                        filterList = filterList.value,
                        list = currentPathFileList.value,
                        resetSearchVars = resetFilesSearchVars,
                        match = { idx, it -> true },
                        customTask = {
                            val curDir = File(currentPath())
                            if (curDir.canRead().not()) {  
                                Msg.requireShow(activityContext.getString(R.string.err_read_path_failed))
                            } else { 
                                val canceled = initSearch(keyword = keyword, lastKeyword = filesPageLastKeyword, token = filesPageSearchToken)
                                val match = { idx: Int, it: File ->
                                    it.name.contains(keyword, ignoreCase = true)
                                            || RegexUtil.matchWildcard(it.name, keyword)
                                }
                                filterList.value.clear()
                                filesPageSearching.value = true
                                DirSearchUtil.realBreadthFirstSearch(
                                    dir = curDir,
                                    match = match,
                                    matchedCallback = { idx, item -> filterList.value.add(FileItemDto.genFileItemDtoByFile(item, activityContext)) },
                                    canceled = canceled
                                )
                            }
                        }
                    )
                    val listState = if (enableFilter) filterListState else curListState.value
                    enableFilterState.value = enableFilter
                    MyLazyColumn(
                        contentPadding = PaddingValues(0.dp),  
                        list = currentPathFileList,
                        listState = listState,
                        requireForEachWithIndex = true,
                        requirePaddingAtBottom = true
                    ) { index, it ->
                        FileListItem(
                            fullPathOfTopNoEndSlash = if (enableFilter) currentPath() else "",
                            item = it,
                            lastPathByPressBack = lastPathByPressBack,
                            menuKeyTextList = if (it.isFile) fileMenuKeyTextList else dirMenuKeyTextList,
                            menuKeyActList = if (it.isFile) fileMenuKeyActList else dirMenuKeyActList,
                            iconOnClick = {  
                                if (isFileChooser.not() && !isPasteMode.value && !isImportMode.value) {
                                    switchItemSelected(it)
                                }
                            },
                            isItemInSelected = isItemInSelected,
                            itemOnLongClick = {
                                if (isFileChooser.not()) {
                                    if (!isFileSelectionMode.value && !isPasteMode.value && !isImportMode.value) {
                                        switchItemSelected(it)
                                    } else if (isFileSelectionMode.value && !isPasteMode.value && !isImportMode.value) {
                                        UIHelper.doSelectSpan(
                                            index, it,
                                            selectedItems.value.toList(), currentPathFileList.toList(),
                                            switchItemSelected,
                                            selectItem
                                        )
                                    }
                                }
                            }
                        ) itemOnClick@{  
                            if (isFileSelectionMode.value) {  
                                switchItemSelected(it)
                            } else {  
                                if (it.isFile) {
                                    if (isFileChooser) {
                                        if (fileChooserType == FileChooserType.SINGLE_FILE) {
                                            updateSelectedPath(it.fullPath)
                                            naviUp()
                                        }
                                        return@itemOnClick
                                    }
                                    if (isPasteMode.value || isImportMode.value) {
                                        return@itemOnClick
                                    }
                                    if (RegexUtil.equalsOrEndsWithExt(it.name, SettingsUtil.obtainEditorFileAssociationList())) {
                                        val expectReadOnly = false
                                        requireInnerEditorOpenFile(it.fullPath, expectReadOnly)
                                    } else {  
                                        val mimeType = MimeType.guessFromFileName(it.name).intentType
                                        MyLog.d(TAG, "fileName=${it.name}, guessed mimeType=$mimeType")
                                        if(mimeType.startsWith("text/")) {  
                                            val expectReadOnly = false
                                            requireInnerEditorOpenFile(it.fullPath, expectReadOnly)
                                        }else if (mimeType == MimeType.ANY.intentType) { 
                                            val openSuccess = FsUtils.openFile(
                                                activityContext,
                                                it.toFile(),
                                                mimeType,
                                                readOnly = false
                                            )
                                            if (!openSuccess) {
                                                val showInnerEditor = true
                                                initOpenAsDialog(it.fullPath, showInnerEditor)
                                            }
                                        }
                                    }
                                } else {  
                                    if (isPasteMode.value && isItemInSelected(it)) {
                                        return@itemOnClick
                                    }
                                    filesPageSimpleFilterKeyWord.value = TextFieldValue("")  
                                    goToPath(it.fullPath)
                                }
                            }
                        }
                        MyHorizontalDivider()
                    }
                }
            }
        }
    }
    val showRemoveFromGitDialog = rememberSaveable { mutableStateOf(false) }
    if (showRemoveFromGitDialog.value) {
        ConfirmDialog(
            title = stringResource(id = R.string.remove_from_git),
            text = stringResource(R.string.will_remove_selected_items_from_git_are_u_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showRemoveFromGitDialog.value = false }
        ) {
            showRemoveFromGitDialog.value = false
            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff) {
                val selectedItems = selectedItems.value.toList()
                if (selectedItems.isEmpty()) {
                    Msg.requireShow(activityContext.getString(R.string.no_item_selected))
                    return@doJobThenOffLoading  
                }
                var repoWillUse = Libgit2Helper.findRepoByPath(selectedItems[0].fullPath)
                if (repoWillUse == null) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_dir_is_not_a_git_repo))
                    return@doJobThenOffLoading  
                }
                repoWillUse.use { repo ->
                    val repoWorkDirFullPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                    MyLog.d(TAG, "#RemoveFromGitDialog: will remove files from repo workdir: '${repoWorkDirFullPath}'")
                    val repoIndex = repo.index()
                    selectedItems.forEachBetter {
                        val relativePathUnderRepo = getFilePathUnderParent(repoWorkDirFullPath, it.fullPath)
                        Libgit2Helper.removeFromGit(repoIndex, relativePathUnderRepo, it.isFile)
                    }
                    repoIndex.write()
                }
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        }
    }
    val showCopyOrMoveOrExportFileErrDialog = rememberSaveable { mutableStateOf(false) }
    val copyOrMoveOrExportFileErrMsg = rememberSaveable { mutableStateOf("") }
    val initCopyOrMoveOrExportFileErrDialog = { errList: List<PasteResult> ->
        val sb = StringBuilder()
        val suffix = "\n\n--------------------\n\n"
        sb.append("got ${errList.size} error(s):").append(suffix)
        errList.forEachBetter {
            sb.append("srcPath: ${it.srcPath}").append("\n\n")
            sb.append("targetPath: ${it.targetPath}").append("\n\n")
            sb.append("errMsg: ${it.exception?.localizedMessage ?: "unknow err"}").append(suffix)
        }
        copyOrMoveOrExportFileErrMsg.value = sb.removeSuffix(suffix).toString()
        showCopyOrMoveOrExportFileErrDialog.value = true
    }
    if (showCopyOrMoveOrExportFileErrDialog.value) {
        val closeAndRefreshPage = {
            showCopyOrMoveOrExportFileErrDialog.value = false
            copyOrMoveOrExportFileErrMsg.value = ""
            filesPageQuitSelectionMode()  
            changeStateTriggerRefreshPage(needRefreshFilesPage)
        }
        val copyOrMoveOrExportFileErrMsg = copyOrMoveOrExportFileErrMsg.value
        CopyableDialog(
            title = stringResource(R.string.error),
            text = copyOrMoveOrExportFileErrMsg,
            onCancel = closeAndRefreshPage
        ) {
            clipboardManager.setText(AnnotatedString(copyOrMoveOrExportFileErrMsg))
            Msg.requireShow(activityContext.getString(R.string.copied))
            closeAndRefreshPage()
        }
    }
    val copyOrMoveOrExportFile = copyOrMoveOrExportFile@{ srcList: List<FileItemDto>, targetFullPath: String, requireDeleteSrc: Boolean ->
        doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff) {
            val ret = FsUtils.copyOrMoveOrExportFile(srcList.map { it.toFile() }, File(targetFullPath), requireDeleteSrc)
            if (ret.hasError()) {
                if (ret.code == Ret.ErrCode.srcListIsEmpty) {
                    Msg.requireShow(activityContext.getString(R.string.no_item_selected))
                } else if (ret.code == Ret.ErrCode.targetIsFileButExpectDir) {
                    Msg.requireShow(activityContext.getString(R.string.err_target_is_file_but_expect_dir))
                } else {  
                    val errList = ret.data
                    if (errList != null && errList.isNotEmpty()) {
                        initCopyOrMoveOrExportFileErrDialog(errList)
                    }
                }
                return@doJobThenOffLoading
            }
            Msg.requireShow(activityContext.getString(R.string.success))
            filesPageQuitSelectionMode()
            changeStateTriggerRefreshPage(needRefreshFilesPage)
        }
    }
    val pasteMode_Move = 1
    val pasteMode_Copy = 2
    val pasteMode_None = 0  
    val pasteMode = rememberSaveable { mutableIntStateOf(pasteMode_None) }
    val setPasteModeThenShowPasteBar = { pastModeVal: Int ->
        pasteMode.intValue = pastModeVal
        isFileSelectionMode.value = false
        isPasteMode.value = true
    }
    val itemListForExport = mutableCustomStateListOf(stateKeyTag, "itemListForExport") { listOf<FileItemDto>() }
    val showSafImportDialog = rememberSaveable { mutableStateOf(false) }
    val showSafExportDialog = rememberSaveable { mutableStateOf(false) }
    val initSafExportDialog = { itemList: List<FileItemDto> ->
        itemListForExport.value.clear()
        itemListForExport.value.addAll(itemList)
        showSafImportDialog.value = false
        showSafExportDialog.value = true
    }
    val initSafImportDialog = {
        val curPathReadable = try {
            File(currentPath()).canRead()
        } catch (e: Exception) {
            false
        }
        if (curPathReadable) {
            showSafExportDialog.value = false
            showSafImportDialog.value = true
        } else {
            Msg.requireShow(activityContext.getString(R.string.err_read_path_failed))
        }
    }
    val safImportExportOverwrite = rememberSaveable { mutableStateOf(false) }
    val choosenSafUri = mutableCustomStateOf<Uri?>(stateKeyTag, "choosenSafUri") { null }
    val chooseDirLauncher = rememberLauncherForActivityResult(MyOpenDocumentTree()) { uri ->
        if (uri != null) {
            SafUtil.takePersistableRWPermission(activityContext.contentResolver, uri)
            choosenSafUri.value = uri
        }
    }
    val showImportExportErrorDialog = rememberSaveable { mutableStateOf(false) }
    val importExportErrorMsg = rememberSaveable { mutableStateOf("") }
    if (showSafExportDialog.value || showSafImportDialog.value) {
        val importOrExportText = if (showSafExportDialog.value) stringResource(R.string.export) else stringResource(R.string.import_str)
        val closeDialog = { showSafExportDialog.value = false; showSafImportDialog.value = false }
        ConfirmDialog2(
            title = importOrExportText,
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        CardButton(
                            onClick = {
                                chooseDirLauncher.launch(null)
                            },
                            content = {
                                Text(
                                    text = if (choosenSafUri.value == null) stringResource(R.string.select_path) else choosenSafUri.value.toString(),
                                    color = UIHelper.getCardButtonTextColor(enabled = true, inDarkTheme = inDarkTheme)
                                )
                            },
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    MyCheckBox(stringResource(R.string.overwrite_if_exist), safImportExportOverwrite)
                    MySelectionContainer {
                        DefaultPaddingText(stringResource(R.string.overwrite_files_note))
                    }
                }
            },
            onCancel = closeDialog,
            okBtnEnabled = choosenSafUri.value != null,
            okBtnText = importOrExportText
        ) {
            val trueExportFalseImport = showSafExportDialog.value
            closeDialog()
            val loadingText = activityContext.getString(if (trueExportFalseImport) R.string.exporting else R.string.importing)
            doJobThenOffLoading {
                val uri = choosenSafUri.value!!
                val conflictStrategy = if (safImportExportOverwrite.value) FsUtils.CopyFileConflictStrategy.OVERWRITE_FOLDER_AND_FILE else FsUtils.CopyFileConflictStrategy.SKIP
                val chosenDir = DocumentFile.fromTreeUri(activityContext, uri)
                if (chosenDir == null) {
                    Msg.requireShow(activityContext.getString(R.string.err_documentfile_is_null))
                    return@doJobThenOffLoading
                }
                                try {
                                    safDiffResultStr.value = "Comparing..."
                                    val startAt = System.currentTimeMillis()
                                    val result = SafAndFileCmpUtil.SafAndFileCompareResult()
                                    SafAndFileCmpUtil.recursiveCompareFiles_Saf(
                                        contentResolver = activityContext.contentResolver,
                                        safFiles = chosenDir.listFiles() ?: arrayOf(),
                                        files = File(currentPath()).listFiles() ?: arrayOf(),
                                        result = result,
                                        canceled = { requireCancelAct.value }
                                    )
                                    val spentTime = (System.currentTimeMillis() - startAt) / 1000  
                                    safDiffResultStr.value = "spent time: $spentTime second(s)\n\n------------\n\n$result"
                                    Msg.requireShow(activityContext.getString(R.string.done))
                                } catch (cancelled: CancellationException) {
                                    safDiffResultStr.value = ""
                                    Msg.requireShow(activityContext.getString(R.string.canceled))
                                } catch (openInputStreamFailed: OpenInputStreamFailed) {
                                    val errMsg = "open input stream for uri failed"
                                    safDiffResultStr.value = openInputStreamFailed.localizedMessage ?: errMsg
                                    Msg.requireShow(errMsg)
                                } catch (e: Exception) {
                                    MyLog.e(TAG, "#SafDiffDialog err: " + e.stackTraceToString())
                                    val errorMsg = "err: ${e.localizedMessage}"
                                    safDiffResultStr.value = errorMsg
                                }
                            }
                        }
                    }
                    if (safDiffResultStr.value.isNotBlank()) {
                        Spacer(Modifier.height(20.dp))
                        MySelectionContainer {
                            CardButton(
                                enabled = false,
                                maxHeight = 500,
                                onClick = {},
                                content = {
                                    Text(safDiffResultStr.value)
                                },
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            },
            onCancel = closeDialog,
            cancelBtnText = stringResource(R.string.close),
            showOk = false
        ) {  
        }
    }
    val chooseDirLauncherThenExport = rememberLauncherForActivityResult(MyOpenDocumentTree()) exportSaf@{ uri ->
        if (uri != null) {
            SafUtil.takePersistableRWPermission(activityContext.contentResolver, uri)
            val loadingText = activityContext.getString(R.string.exporting)
            doJobThenOffLoading {
                val chosenDir = DocumentFile.fromTreeUri(activityContext, uri)
                if (chosenDir == null) {
                    Msg.requireShow(activityContext.getString(R.string.err_get_export_dir_failed))
                    return@doJobThenOffLoading
                }
                try {
                    loadingOnCancellable(loadingText)
                    FsUtils.recursiveExportFiles_Saf(
                        contentResolver = activityContext.contentResolver,
                        targetDir = chosenDir,
                        srcFiles = selectedItems.value.map<FileItemDto, File> { it.toFile() }.toTypedArray(),
                        canceled = { requireCancelAct.value }
                    )
                    Msg.requireShow(activityContext.getString(R.string.export_success))
                } catch (cancelled: CancellationException) {
                    Msg.requireShow(activityContext.getString(R.string.canceled))
                } catch (e: Exception) {
                    MyLog.e(TAG, "#exportSaf@ err: " + e.stackTraceToString())
                    val exportErrStrRes = activityContext.getString(R.string.export_err)
                    Msg.requireShow(exportErrStrRes)
                    importExportErrorMsg.value = "$exportErrStrRes: " + e.localizedMessage

                    showImportExportErrorDialog.value = true
                } finally {
                    loadingOffCancellable()
                }
            }
        } else {  
            Msg.requireShow(activityContext.getString(R.string.export_canceled))
        }
    }
    if (showInitRepoDialog.value) {
        val selctedDirs = initRepoList.value
        if (selctedDirs.isEmpty()) {
            showInitRepoDialog.value = false
            Msg.requireShow(stringResource(R.string.no_dir_selected))
        } else {
            ConfirmDialog(
                title = stringResource(R.string.init_repo),
                text = stringResource(R.string.will_init_selected_folders_to_git_repos_are_you_sure),
                okBtnEnabled = selctedDirs.isNotEmpty(),
                onCancel = { showInitRepoDialog.value = false }
            ) {
                showInitRepoDialog.value = false
                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                    try {
                        var successCnt = 0
                        selctedDirs.forEachBetter { dirPath ->
                            try {
                                Libgit2Helper.initGitRepo(dirPath)
                                successCnt++
                            } catch (e: Exception) {
                                MyLog.e(TAG, "init repo in FilesPage err: path=${dirPath}, err=${e.localizedMessage}")
                            }
                        }
                        Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.n_inited), listOf("" + successCnt)))
                    } finally {
                        if (enableFilterState.value.not()) {
                            changeStateTriggerRefreshPage(needRefreshFilesPage)
                        } else {
                            val filterList = filterList.value
                            filterList.toList().forEachIndexedBetter { idx, item ->
                                if (selctedDirs.contains(item.fullPath)) {
                                    filterList[idx] = FileItemDto.genFileItemDtoByFile(File(item.fullPath), activityContext)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showImportAsRepoDialog.value) {
        val selctedDirs = importAsRepoList.value
        if (selctedDirs.isEmpty()) {
            showImportAsRepoDialog.value = false
            Msg.requireShow(stringResource(R.string.no_dir_selected))
        } else {
            ConfirmDialog(
                title = stringResource(R.string.import_repo),
                requireShowTextCompose = true,
                textCompose = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        GrantManageStoragePermissionClickableText(activityContext)
                        Spacer(Modifier.height(5.dp))
                        MyCheckBox(text = stringResource(R.string.paths_are_repo_parent_dir), value = isReposParentFolderForImport)
                        Spacer(Modifier.height(5.dp))
                        if (isReposParentFolderForImport.value) {
                            MySelectionContainer {
                                DefaultPaddingText(stringResource(R.string.will_scan_repos_under_folders))
                            }
                        }
                    }
                },
                okBtnText = stringResource(R.string.ok),
                cancelBtnText = stringResource(R.string.cancel),
                okBtnEnabled = selctedDirs.isNotEmpty(),
                onCancel = { showImportAsRepoDialog.value = false },
            ) {
                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.importing)) {
                    val importRepoResult = ImportRepoResult()
                    try {
                        selctedDirs.forEachBetter { dirPath ->
                            val result = AppModel.dbContainer.repoRepository.importRepos(dir = dirPath, isReposParent = isReposParentFolderForImport.value)
                            importRepoResult.all += result.all
                            importRepoResult.success += result.success
                            importRepoResult.failed += result.failed
                            importRepoResult.existed += result.existed

                        }
                        showImportAsRepoDialog.value = false
                        Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.n_imported), listOf("" + importRepoResult.success)))
                    } catch (e: Exception) {
                        MyLog.e(TAG, "import repo from FilesPage err: importRepoResult=$importRepoResult, err=" + e.stackTraceToString())
                        Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    } finally {
                    }
                }
            }
        }
    }
    if (showDetailsDialog.value) {
        val itemList = details_itemList.value
        ConfirmDialog2(
            title = stringResource(id = R.string.details),
            requireShowTextCompose = true,
            textCompose = {
                MySelectionContainer {
                    ScrollableColumn {
                        if (itemList.size > 1) {
                            Row {
                                Text(
                                    text = replaceStringResList(
                                        stringResource(R.string.items_n1_n2_folders_n3_files),
                                        listOf("" + details_AllCount.intValue, "" + details_FoldersCount.intValue, "" + details_FilesCount.intValue)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                        Row {
                            Text(
                                text = replaceStringResList(
                                    stringResource(R.string.size_n),
                                    listOf(getHumanReadableSizeStr(details_ItemsSize.longValue))
                                ) + (if (details_CountingItemsSize.value) Cons.oneChar3dots else "")
                            )
                        }
                        if (itemList.size == 1) {
                            val item = itemList[0]
                            Spacer(modifier = Modifier.height(15.dp))
                            Row {
                                Text(text = stringResource(R.string.name) + ": " + item.name)
                            }
                            if (item.isDir) {
                                Spacer(modifier = Modifier.height(15.dp))
                                Row {
                                    Text(
                                        text = replaceStringResList(
                                            stringResource(R.string.item_count_n),
                                            listOf(replaceStringResList(stringResource(R.string.folder_n_file_m), listOf("" + item.folderCount, "" + item.fileCount)))
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            Row {
                                Text(text = stringResource(R.string.path) + ": " + item.fullPath)
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            Row {
                                Text(text = stringResource(R.string.last_modified) + ": " + item.lastModifiedTime)
                            }
                        }
                    }
                }
            },
            showCancel = false,  
            onCancel = {},  
            onDismiss = { showDetailsDialog.value = false },  
            okBtnText = stringResource(R.string.close),
        ) {  
            showDetailsDialog.value = false
        }
    }
    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val goToParentAndScrollToItem = { item: FileItemDto ->
        showSelectedItemsShortDetailsDialog.value = false
        changeStateTriggerRefreshPage(needRefreshFilesPage, StateRequestType.goToParentAndScrollToItem, item.fullPath)
    }
    val showSelectedItemsShortDetailsDialogForImportMode = rememberSaveable { mutableStateOf(false) }
    if (showSelectedItemsShortDetailsDialog.value) {
        SelectedFileItemsDialog(
            list = selectedItems.value,
            itemName = { it.name },
            itemPath = { it.fullPath },
            itemIsDir = { it.isDir },
            removeItem = { switchItemSelected(it) },
            clearAll = { selectedItems.value.clear() },
            showFileIcon = true,
            fileIconOnClick = { goToParentAndScrollToItem(it) },
            closeDialog = { showSelectedItemsShortDetailsDialog.value = false }
        )
    }
    if (showSelectedItemsShortDetailsDialogForImportMode.value) {
        SelectedFileItemsDialog(
            title = stringResource(R.string.import_str),
            list = requireImportUriList.value,
            itemName = { FsUtils.getFileRealNameFromUri(activityContext, it) ?: it.toString() },
            itemPath = { it.toString() },
            itemIsDir = { false },
            showFileIcon = false,
            fileIconOnClick = {},
            removeItem = { requireImportUriList.value.remove(it) },
            clearAll = { requireImportUriList.value.clear() },
            closeDialog = { showSelectedItemsShortDetailsDialogForImportMode.value = false },
        )
    }
    val showIgnoreDialog = rememberSaveable { mutableStateOf(false) }
    if (showIgnoreDialog.value) {
        val selectedItems = selectedItems.value
        GitIgnoreDialog(
            showIgnoreDialog = showIgnoreDialog,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            activityContext = activityContext,
            getRepository = {
                try {
                    var repoWillUse = Libgit2Helper.findRepoByPath(selectedItems[0].fullPath)
                    if (repoWillUse == null) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.err_dir_is_not_a_git_repo))
                    }
                    repoWillUse
                } catch (e: Exception) {
                    MyLog.e(TAG, "#getRepository err: ${e.stackTraceToString()}")
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    null
                }
            },
            getIgnoreItems = { repoWorkDirFullPath: String ->
                val selectedItemList = selectedItems.toList()
                if (selectedItemList.isEmpty()) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_item_selected))
                }
                selectedItemList.map {
                    IgnoreItem(pathspec = getFilePathUnderParent(repoWorkDirFullPath, it.fullPath), isFile = it.isFile)
                }
            },
            onCatch = { e: Exception ->
                val errMsg = e.localizedMessage
                Msg.requireShowLongDuration("err: " + errMsg)
                MyLog.e(TAG, "ignore files err: ${e.stackTraceToString()}")
            },
            onFinally = { repoWorkDirFullPath ->
                if (enableFilterState.value.not() && currentPath() == repoWorkDirFullPath) {
                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                }
            }
        )
    }
    val countNumOnClickForSelectAndPasteModeBottomBar = {
        showSelectedItemsShortDetailsDialog.value = true
    }
    val isFileChooserAndSingleDirType = isFileChooser && fileChooserType == FileChooserType.SINGLE_DIR
    if (isFileChooserAndSingleDirType || isFileSelectionMode.value) {
        val selectionModeIconList = if (isFileChooserAndSingleDirType) {
            listOf(
                Icons.Filled.Check, 
            )
        } else {
            listOf(
                Icons.Filled.Delete,
                Icons.Filled.ContentCut,
                Icons.Filled.ContentCopy,
                Icons.Filled.SelectAll,  
            )
        }
        val selectAll = {
            val list = if (enableFilterState.value) filterList.value else currentPathFileList.value
            list.toList().forEachBetter {
                selectItem(it)
            }
            Unit
        }
        val selectionModeIconTextList = if (isFileChooserAndSingleDirType) {
            listOf(
                stringResource(R.string.confirm), 
            )
        } else {
            listOf(
                stringResource(R.string.delete),
                stringResource(R.string.move),
                stringResource(R.string.copy),
                stringResource(R.string.select_all),
            )
        }
        val confirmForChooser = { path: String ->
            updateSelectedPath(path)
            naviUp()
        }
        val confirmForMultiChooser = { path: List<FileItemDto> ->
        }
        val selectionModeIconOnClickList = if (isFileChooserAndSingleDirType) {
            listOf(
                confirm@{
                    confirmForChooser(currentPath())
                }
            )
        } else {
            listOf<() -> Unit>(
                delete@{
                    initDelFileDialog(selectedItems.value)
                },
                move@{
                    setPasteModeThenShowPasteBar(pasteMode_Move)
                },
                copy@{
                    setPasteModeThenShowPasteBar(pasteMode_Copy)
                },
                selectAll@{
                    selectAll()
                }
            )
        }
        val selectionModeIconEnableList = if (isFileChooserAndSingleDirType) {
            listOf(
                confirm@{ true },  
            )
        } else {
            listOf(
                delete@{ getSelectedFilesCount() > 0 },  
                move@{ getSelectedFilesCount() > 0 },  
                copy@{ getSelectedFilesCount() > 0 },  
                selectAll@{ true },  
            )
        }
        val selectionModeMoreItemTextList = (listOf(
            stringResource(id = R.string.ignore),
            stringResource(id = R.string.remove_from_git),
            stringResource(id = R.string.details),
            stringResource(R.string.share),
            if (proFeatureEnabled(importReposFromFilesTestPassed)) stringResource(id = R.string.import_as_repo) else "",  
            if (proFeatureEnabled(initRepoFromFilesPageTestPassed)) stringResource(id = R.string.init_repo) else "",
            stringResource(R.string.add_storage_path),
            if (showImportForBottomBar) stringResource(R.string.import_str) else "", 
            stringResource(R.string.export),
        ))
        val selectionModeMoreItemOnClickList = (listOf(
            ignore@{
                showIgnoreDialog.value = true
            },
            removeFromGit@{
                showRemoveFromGitDialog.value = true
            },
            details@{
                initDetailsDialog(selectedItems.value.toList())
            },
            share@{
                shareFiles(selectedItems.value)
            },
            importAsRepo@{

                initImportAsRepoDialog(
                    selectedItems.value.filterAndMap(
                        predicate = { it.isDir },
                        transform = { it.fullPath }
                    )
                )
            },
            initRepo@{
                initInitRepoDialog(
                    selectedItems.value.filterAndMap(
                        predicate = { it.isDir },
                        transform = { it.fullPath }
                    )
                )
            },
            addStoragePath@{
                val selectedItems = selectedItems.value
                if(selectedItems.isEmpty()) {
                    return@addStoragePath
                }
                val storagePaths = StoragePathsMan.get()
                var needSave = false
                selectedItems.forEachBetter {
                    if(addStoragePath(
                        newPath =  it.fullPath,
                        storagePaths = storagePaths,
                        callSave = false,
                        showMsg = false
                    )) {
                        needSave = true
                    }
                }
                if(needSave) {
                    StoragePathsMan.save(storagePaths)
                }
                Msg.requireShow(activityContext.getString(R.string.done))
            },
            import@{

                initSafImportDialog()
            },
            export@{
                initSafExportDialog(selectedItems.value.toList())
            },
        ))
        val hasItemSelected = { getSelectedFilesCount() > 0 }
        val hasDirSelected = { selectedItems.value.any { it.isDir } }
        val hasFileSelected = { selectedItems.value.any { it.isFile } }
        val selectionModeMoreItemEnableList = (listOf(
            hasItemSelected, 
            hasItemSelected, 
            hasItemSelected, 
            hasFileSelected, 
            hasDirSelected,  
            hasDirSelected, 
            hasDirSelected, 
            {
                try {
                    File(currentPath()).canRead()
                } catch (_: Exception) {
                    false
                }
            },
            hasItemSelected, 
        ))
        if (!isLoading.value) {
            if (isFileChooser) {
                BottomBar(
                    showClose = false,
                    showSelectedCount = false,
                    quitSelectionMode = {},
                    iconList = selectionModeIconList,
                    iconTextList = selectionModeIconTextList,
                    iconDescTextList = selectionModeIconTextList,
                    iconOnClickList = selectionModeIconOnClickList,
                    iconEnableList = selectionModeIconEnableList,
                    moreItemTextList = listOf(),
                    moreItemOnClickList = listOf(),
                    moreItemEnableList = listOf(),
                    getSelectedFilesCount = getSelectedFilesCount,
                    countNumOnClickEnabled = true,
                    countNumOnClick = countNumOnClickForSelectAndPasteModeBottomBar,
                    reverseMoreItemList = true
                )
            } else {
                BottomBar(
                    quitSelectionMode = filesPageQuitSelectionMode,
                    iconList = selectionModeIconList,
                    iconTextList = selectionModeIconTextList,
                    iconDescTextList = selectionModeIconTextList,
                    iconOnClickList = selectionModeIconOnClickList,
                    iconEnableList = selectionModeIconEnableList,
                    moreItemTextList = selectionModeMoreItemTextList,
                    moreItemOnClickList = selectionModeMoreItemOnClickList,
                    moreItemEnableList = selectionModeMoreItemEnableList,
                    getSelectedFilesCount = getSelectedFilesCount,
                    countNumOnClickEnabled = true,
                    countNumOnClick = countNumOnClickForSelectAndPasteModeBottomBar,
                    reverseMoreItemList = true
                )
            }
        }
    }
    val quitImportMode = {
        isImportMode.value = false
        requireImportUriList.value.clear()
    }
    val getRequireUriFilesCount = { requireImportUriList.value.size }
    if (isImportMode.value) {
        val selectionModeIconList = listOf(
            Icons.Filled.FileDownload,
        )
        val selectionModeIconTextList = listOf(
            stringResource(R.string.import_str),
        )
        val selectionModeIconOnClickList = listOf(
            importFiles@{

                doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = activityContext.getString(R.string.importing)) {
                    val sb = StringBuilder()
                    var succCnt = 0
                    var failedCnt = 0
                    val dest = currentPath()
                    var previousFilePath = ""
                    var curTarget: File? = null
                    requireImportUriList.value.toList().forEachBetter forEach@{ it: Uri? ->
                        try {
                            if (it != null && it.path != null && it.path!!.length > 0) {
                                val src = File(it.path!!)
                                if (src.isDirectory) {  
                                    failedCnt++
                                    sb.appendLine("'${it.path}'" + ": is a directory, only support import files!")
                                    return@forEach
                                }
                                var srcFileName = FsUtils.getFileRealNameFromUri(activityContext, it)
                                if (srcFileName == null) {  
                                    srcFileName = src.name
                                    MyLog.w(TAG, "#importFiles@: getFileRealNameFromUri() return null, will use src.name:${srcFileName}")
                                    if (srcFileName.isNullOrEmpty()) {  
                                        val randomName = getShortUUID()
                                        srcFileName = randomName
                                        MyLog.w(TAG, "#importFiles@:src.name is null or empty, will use a random name:${srcFileName}")
                                    }
                                }
                                val target = FsUtils.getANonExistsFile(File(dest, srcFileName))  
                                curTarget = File(target.canonicalPath)  
                                val inputStream = activityContext.contentResolver.openInputStream(it)
                                if (inputStream == null) {
                                    failedCnt++
                                    sb.appendLine("'${it.path}'" + ": can't read!")
                                    return@forEach
                                }
                                inputStream.use { input ->
                                    target.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                succCnt++
                                previousFilePath == curTarget?.canonicalPath ?: ""
                            } else {
                                failedCnt++
                                sb.appendLine("uri is null!")
                            }
                        } catch (e: Exception) {
                            failedCnt++
                            sb.appendLine((it?.path ?: "/fileNameIsNull/") + ": error: " + e.localizedMessage)
                            try {
                                if (curTarget != null) {
                                    val curFilePath = curTarget!!.canonicalPath
                                    if (curFilePath.isNotBlank() && curFilePath != previousFilePath && curTarget!!.exists() && curTarget!!.length() <= 0) {
                                        curTarget!!.delete()
                                    }
                                }
                            } catch (e2: Exception) {
                            }
                        }
                    }
                    successImportCount.intValue = succCnt
                    failedImportCount.intValue = failedCnt
                    failedImportListStr.value = sb.toString()
                    if (failedCnt < 1) {  
                        Msg.requireShow(activityContext.getString(R.string.import_success))
                    } else {  
                        showImportResultDialog.value = true
                    }
                    quitImportMode()
                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                }
                Unit
            },
        )
        val selectionModeIconEnableList = listOf(
            { requireImportUriList.value.isNotEmpty() },
        )
        val countNumOnClickForImportMode = {
            showSelectedItemsShortDetailsDialogForImportMode.value = true
        }
        if (!isLoading.value) {
            BottomBar(
                quitSelectionMode = quitImportMode,
                iconList = selectionModeIconList,
                iconTextList = selectionModeIconTextList,
                iconDescTextList = selectionModeIconTextList,
                iconOnClickList = selectionModeIconOnClickList,
                iconEnableList = selectionModeIconEnableList,
                moreItemTextList = listOf(),
                moreItemOnClickList = listOf(),
                moreItemEnableList = listOf(),
                getSelectedFilesCount = getRequireUriFilesCount,
                countNumOnClickEnabled = true,
                countNumOnClick = countNumOnClickForImportMode
            )
        }
    }
    if (showImportExportErrorDialog.value) {
        val closeDialog = { showImportExportErrorDialog.value = false; importExportErrorMsg.value = "" }
        CopyableDialog(
            title = stringResource(R.string.error),
            text = importExportErrorMsg.value,
            onCancel = closeDialog
        ) { 
            val errMsg = importExportErrorMsg.value
            closeDialog()
            clipboardManager.setText(AnnotatedString(errMsg))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    val showExportDialog = rememberSaveable { mutableStateOf(false) }
    if (showExportDialog.value) {
        ConfirmDialog(
            title = stringResource(id = R.string.export),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    Row {
                        Text(text = stringResource(id = R.string.will_export_files_to) + ":")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = FsUtils.appExportFolderNameUnderDocumentsDirShowToUser,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        Text(text = stringResource(id = R.string.are_you_sure))
                    }
                }
            },
            onCancel = { showExportDialog.value = false }
        ) onOk@{
            showExportDialog.value = false
            val ret = FsUtils.getExportDirUnderPublicDocument()
            if (ret.hasError() || ret.data == null || !ret.data!!.exists()) {
                Msg.requireShowLongDuration(activityContext.getString(R.string.get_default_export_folder_failed_plz_choose_one))
                chooseDirLauncherThenExport.launch(null)  
                return@onOk
            }
            copyOrMoveOrExportFile(selectedItems.value, ret.data!!.canonicalPath, false)
        }
    }
    if (isPasteMode.value) {
        val iconList = listOf(
            Icons.Filled.ContentPaste,
            if (pasteMode.intValue == pasteMode_Move) Icons.Filled.ContentCut else Icons.Filled.ContentCopy
        )
        val iconTextList = listOf(
            stringResource(R.string.paste),
            if (pasteMode.intValue == pasteMode_Move) stringResource(R.string.cut) else stringResource(R.string.copy)
        )
        val iconOnClickList = listOf(
            paste@{
                copyOrMoveOrExportFile(selectedItems.value, currentPath(), pasteMode.intValue == pasteMode_Move)  
                Unit
            },
            cutOrCopyIndicator@{},
            )
        val iconEnableList = listOf(
            { getSelectedFilesCount() > 0 },  
            cutOrCopyIndicator@{ false },  
        )
        val quitPasteAndBackToSelectionMode = {
            isPasteMode.value = false  
            isFileSelectionMode.value = true  
        }
        if (!isLoading.value) {
            BottomBar(
                quitSelectionMode = quitPasteAndBackToSelectionMode,
                iconList = iconList,
                iconTextList = iconTextList,
                iconDescTextList = iconTextList,
                iconOnClickList = iconOnClickList,
                iconEnableList = iconEnableList,
                moreItemTextList = listOf(),
                moreItemOnClickList = listOf(),
                moreItemEnableList = listOf(),
                getSelectedFilesCount = getSelectedFilesCount,
                countNumOnClickEnabled = true,
                countNumOnClick = countNumOnClickForSelectAndPasteModeBottomBar
            )
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.goToTop) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            UIHelper.scrollToItem(scope, curListState.value, 0)
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.safDiff) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            initSafDiffDialog()
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.safImport) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            initSafImportDialog()
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.safExport) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            val curFile = File(currentPath())
            val curPathReadable = try {
                curFile.canRead()
            } catch (e: Exception) {
                false
            }
            if (curPathReadable) {
                val subFiles = curFile.listFiles()
                if (subFiles == null || subFiles.isEmpty()) { 
                    Msg.requireShow(activityContext.getString(R.string.folder_is_empty))
                } else { 
                    initSafExportDialog(subFiles.map { FileItemDto.genFileItemDtoByFile(it, activityContext) })
                }
            } else {
                Msg.requireShow(activityContext.getString(R.string.err_read_path_failed))
            }
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.switchBetweenTopAndLastPosition) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            UIHelper.switchBetweenTopAndLastVisiblePosition(scope, curListState.value, lastPosition)
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.requireShowPathDetails) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            initDetailsDialog(listOf(curPathFileItemDto.value))
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.createFileOrFolder) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            fileNameForCreateDialog.apply {
                value = value.copy(selection = TextRange(0, value.text.length))
            }
            createFileOrFolderErrMsg.value = ""  
            showCreateFileOrFolderDialog.value = true  
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.showViewAndSortMenu) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            onlyForThisFolderStateBuf.value = onlyForThisFolderState.value
            viewAndSortStateBuf.value = viewAndSortState.value.copy()
            showViewAndSortDialog.value = true
        }
    }
    if (PageRequest.DataRequest.isDataRequest(filesPageRequestFromParent.value, PageRequest.goToIndex)) {
        PageRequest.getRequestThenClearStateThenDoAct(filesPageRequestFromParent) { request ->
            val index = try {
                PageRequest.DataRequest.getDataFromRequest(request).toInt()
            } catch (e: Exception) {
                0
            }
            UIHelper.scrollToItem(scope, curListState.value, index)
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.goToPath) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            pathToGo.value = pathToGo.value.let { it.copy(selection = TextRange(0, it.text.length)) }
            showGoToPathDialog.value = true
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.copyFullPath) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            copyThenShowCopied(currentPath())
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.copyRepoRelativePath) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            copyRepoRelativePath(currentPath())
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.goToInternalStorage) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            goToPath(FsUtils.getInternalStorageRootPathNoEndsWithSeparator())
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.goToExternalStorage) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            goToPath(FsUtils.getExternalStorageRootPathNoEndsWithSeparator())
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.goToInnerDataStorage) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            goToPath(FsUtils.getInnerStorageRootPathNoEndsWithSeparator())
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.goToExternalDataStorage) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            val targetPath = AppModel.externalDataDir?.canonicalPath ?: ""
            if (targetPath.isBlank()) {
                Msg.requireShow(activityContext.getString(R.string.invalid_path))
            } else {
                goToPath(targetPath)
            }
        }
    }
    val refreshWhenResume = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(currentPath()) {
        runCatching {
            if (currentPath() == FsUtils.getExternalStorageRootPathNoEndsWithSeparator()) {
                val permissionRequestSent = requestStoragePermissionIfNeed(activityContext, TAG)
                refreshWhenResume.value = permissionRequestSent
            }
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if(refreshWhenResume.value) {
            refreshWhenResume.value = false
            changeStateTriggerRefreshPage(needRefreshFilesPage)
        }
    }
    LaunchedEffect(needRefreshFilesPage.value) {
        try {
            val (requestType, requestData) = getRequestDataByState<String?>(needRefreshFilesPage.value)
            if(requestType == StateRequestType.goToParentAndScrollToItem && requestData != null) {
                File(requestData).canonicalFile.parent?.let {
                    updateCurrentPath(it)
                }
            }
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                try {
                    doInit(
                        isFileChooser = isFileChooser,
                        currentPath = currentPath,
                        updateCurrentPath = updateCurrentPath,
                        currentPathFileList = currentPathFileList,
                        currentPathBreadCrumbList = currentPathBreadCrumbList,
                        settingsSnapshot = settingsSnapshot,
                        filesPageGetFilterModeOn = filesPageGetFilterMode,
                        filesPageFilterKeyword = filesPageFilterKeyword,
                        curListState = curListState,
                        getListState = getListState,
                        loadingOn = loadingOn,
                        loadingOff = loadingOff,
                        activityContext = activityContext,
                        requireImportFile = requireImportFile,
                        requireImportUriList = requireImportUriList,
                        filesPageQuitSelectionMode = filesPageQuitSelectionMode,
                        isImportedMode = isImportMode,
                        selectItem=selectItem,
                        filesPageRequestFromParent = filesPageRequestFromParent,
                        setErr = setErr,
                        viewAndSortState = viewAndSortState,
                        viewAndSortOnlyForThisFolderState = onlyForThisFolderState,
                        curPathFileItemDto = curPathFileItemDto,
                        quitImportMode = quitImportMode,
                        selectedItems = selectedItems.value,
                    )
                    if(keepFilterResultOnce.value) {
                        keepFilterResultOnce.value = false
                    }else {
                        triggerReFilter(filterResultNeedRefresh)
                    }
                    if(requestType == StateRequestType.goToParentAndScrollToItem && requestData != null) {
                        filesPageSimpleFilterOn.value = false
                        resetFilesSearchVars()
                        val targetIndex = currentPathFileList.value.toList().indexOfFirst { it.fullPath == requestData}
                        if(targetIndex >= 0) {
                            UIHelper.scrollToItem(scope, curListState.value, targetIndex - 2)
                        }
                    }
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("init Files err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#init Files page err: ${e.stackTraceToString()}")
                }
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect err: ${e.stackTraceToString()}")
        }
    }
}
private suspend fun doInit(
    isFileChooser:Boolean,
    currentPath: ()->String,
    updateCurrentPath: (String)->Unit,
    currentPathFileList: CustomStateListSaveable<FileItemDto>,
    currentPathBreadCrumbList: CustomStateListSaveable<FileItemDto>,
    settingsSnapshot:CustomStateSaveable<AppSettings>,
    filesPageGetFilterModeOn:()->Int,
    filesPageFilterKeyword:CustomStateSaveable<TextFieldValue>,
    curListState: CustomStateSaveable<LazyListState>,
    getListState:(String)->LazyListState,
    loadingOn: (String) -> Unit,
    loadingOff: () -> Unit,
    activityContext: Context,
    requireImportFile:MutableState<Boolean>,
    requireImportUriList: CustomStateListSaveable<Uri>,
    filesPageQuitSelectionMode:()->Unit,
    isImportedMode:MutableState<Boolean>,
    selectItem:(FileItemDto) ->Unit,
    filesPageRequestFromParent:MutableState<String>,
    setErr:(String)->Unit,
    viewAndSortState:CustomStateSaveable<DirViewAndSort>,
    viewAndSortOnlyForThisFolderState:MutableState<Boolean>,
    curPathFileItemDto:CustomStateSaveable<FileItemDto>,
    quitImportMode:()->Unit,
    selectedItems:List<FileItemDto>
){
    ThumbCache.clear()
    if(isFileChooser.not() && selectedItems.isEmpty()) {
        filesPageQuitSelectionMode()
    }
    val lastOpenedPathFromSettings = settingsSnapshot.value.files.lastOpenedPath
    if(currentPath().isBlank()) {
        updateCurrentPath(lastOpenedPathFromSettings)
    }
    currentPathFileList.value.clear()
    val repoBaseDirPath = AppModel.allRepoParentDir.canonicalPath
    updateCurrentPath(if(currentPath().isBlank()) {
        repoBaseDirPath
    }else {
        File(currentPath()).canonicalPath
    })
    var currentDir = File(currentPath())
    var currentFile:File? = null
    if(currentDir.canRead() && currentDir.isFile) {  
        val parent = currentDir.parentFile
        if(parent!=null && parent.exists() && parent.isDirectory) {
            currentFile = currentDir
            currentDir = parent
            updateCurrentPath(currentDir.canonicalPath)
        }
    }
    if(currentDir.canRead() && !currentDir.isDirectory) {  
        currentFile=null  
        currentDir = File(repoBaseDirPath)
        updateCurrentPath(repoBaseDirPath)
    }
    if(lastOpenedPathFromSettings != currentPath()) {
        settingsSnapshot.value.files.lastOpenedPath = currentPath()
        SettingsUtil.update {  
            it.files.lastOpenedPath = currentPath()
        }
    }
    val (viewAndSortOnlyForThisFolder, viewAndSort) = getViewAndSortForPath(currentPath(), settingsSnapshot.value)
    viewAndSortState.value = viewAndSort
    viewAndSortOnlyForThisFolderState.value = viewAndSortOnlyForThisFolder
    val sortMethod = viewAndSort.sortMethod
    val ascend = viewAndSort.ascend
    val comparator = { o1:FileItemDto, o2:FileItemDto ->  
        val sortByName = {
            compareStringAsNumIfPossible(getFileNameOrEmpty(o1.name), getFileNameOrEmpty(o2.name))
        }
        val sortByType = {
            compareStringAsNumIfPossible(getFileExtOrEmpty(o1.name), getFileExtOrEmpty(o2.name))
        }
        var compareResult = if(sortMethod == SortMethod.NAME.code) {
            sortByName()
        }else if(sortMethod == SortMethod.TYPE.code) {
            sortByType()
        } else if(sortMethod == SortMethod.SIZE.code) {
            o1.sizeInBytes.compareTo(o2.sizeInBytes)
        } else { 
            o1.lastModifiedTimeInSec.compareTo(o2.lastModifiedTimeInSec)
        }
        if(compareResult == 0 && sortMethod != SortMethod.NAME.code) {
            compareResult = sortByName()
        }
        if(compareResult == 0 && sortMethod != SortMethod.TYPE.code) {
            compareResult = sortByType()
        }
        if(compareResult > 0){
            if(ascend) 1 else -1
        } else {
            if(ascend) -1 else 1
        }
    }
    val fileSortedSet = sortedSetOf<FileItemDto>(comparator)
    val dirSortedSet = sortedSetOf<FileItemDto>(comparator)
    val needSelectFile = currentFile!=null && currentFile.exists()
    val curFilePath = currentFile?.canonicalPath
    var curFileFromCurPathAlreadySelected = false
    var curFileFromCurPathFileDto:FileItemDto? = null  
    var folderCount = 0
    var fileCount = 0
    currentDir.listFiles()?.let {
        for(file in it) {
            val fdto = FileItemDto.genFileItemDtoByFile(file, activityContext)
            if(fdto.isFile) {
                fileCount++
                if(needSelectFile && !curFileFromCurPathAlreadySelected && curFilePath == fdto.fullPath) {
                    filesPageQuitSelectionMode()
                    selectItem(fdto)
                    curFileFromCurPathFileDto=fdto
                    curFileFromCurPathAlreadySelected = true
                }
                fileSortedSet.add(fdto)
            }else {
                folderCount++
                if(viewAndSort.folderFirst) {
                    dirSortedSet.add(fdto)
                }else {
                    fileSortedSet.add(fdto)
                }
            }
        }
    }
    val curPathDtoTmp = FileItemDto.genFileItemDtoByFile(currentDir, activityContext)
    curPathDtoTmp.folderCount = folderCount
    curPathDtoTmp.fileCount = fileCount
    curPathFileItemDto.value = curPathDtoTmp
    currentPathFileList.value.addAll(dirSortedSet)
    currentPathFileList.value.addAll(fileSortedSet)
    curListState.value = getListState(currentPath())
    if(curFileFromCurPathAlreadySelected && curFileFromCurPathFileDto!=null) {
        var indexForScrollTo = currentPathFileList.value.indexOf(curFileFromCurPathFileDto)
        if(indexForScrollTo>0) {
            indexForScrollTo-=1
        }
        filesPageRequestFromParent.value = PageRequest.DataRequest.build(PageRequest.goToIndex, ""+indexForScrollTo)
    }
    val curDirPath = currentDir.canonicalPath
    val curBreadCrumbList = currentPathBreadCrumbList.value
    val separator = Cons.slashChar
    if(breadCrumbPathNotCoverdCurPath(curBreadCrumbList, curDirPath, separator)) {
        curBreadCrumbList.clear()  
        curBreadCrumbList.add(FileItemDto.getRootDto())  
        val splitPath = curDirPath.trim(separator).split(separator)
        if(!(splitPath.size == 1 && splitPath[0].isEmpty())) {  
            var lastPathName=StringBuilder(40)  
            for(pathName in splitPath) {  
                lastPathName.append(separator).append(pathName)  
                val pathDto = FileItemDto()
                pathDto.isDir=true
                pathDto.fullPath = lastPathName.toString()  
                pathDto.name = pathName
                curBreadCrumbList.add(pathDto)
            }
        }
    }
    if(requireImportFile.value) {
        requireImportFile.value = false
        if(requireImportUriList.value.isNotEmpty()) { 
            filesPageQuitSelectionMode()
            isImportedMode.value = true
        }
    }
    if(requireImportUriList.value.isEmpty()) { 
        quitImportMode()
    }
    setErr(if(currentDir.canRead() && currentDir.isDirectory) "" else activityContext.getString(R.string.err_read_path_failed))
}
@Composable
private fun getBackHandler(
    naviUp:()->Unit,
    isFileChooser: Boolean,
    appContext: Context,
    isFileSelectionMode: MutableState<Boolean>,
    filesPageQuitSelectionMode: () -> Unit,
    currentPath: ()->String,
    allRepoParentDir: File,
    needRefreshFilesPage: MutableState<String>,
    exitApp: () -> Unit,
    getFilterMode:()->Int,
    filesPageFilterModeOff:()->Unit,
    filesPageSimpleFilterOn: MutableState<Boolean>,
    openDrawer:()->Unit,
    goToPath:(String)->Unit,
    resetSearchVars:()->Unit,
): () -> Unit {
    val backStartSec =  rememberSaveable { mutableLongStateOf(0) }
    val ceilingPaths = rememberSaveable { FsUtils.getAppCeilingPaths() }
    val pressBackAgainForExitText = stringResource(R.string.press_back_again_to_exit);
    val showTextAndUpdateTimeForPressBackBtn = {
        openDrawer()
        showToast(appContext, pressBackAgainForExitText, Toast.LENGTH_SHORT)
        backStartSec.longValue = getSecFromTime() + Cons.pressBackDoubleTimesInThisSecWillExit
    }
    val backHandlerOnBack:()->Unit = {
        if (isFileChooser.not() && isFileSelectionMode.value) {
            filesPageQuitSelectionMode()
        } else if(filesPageSimpleFilterOn.value) {
            filesPageSimpleFilterOn.value = false
            resetSearchVars()
        }else if(getFilterMode() != 0) {
            filesPageFilterModeOff()
            resetSearchVars()
        }else if (ceilingPaths.contains(currentPath()).not()) { 
            goToPath(currentPath().let { it.substring(0, it.lastIndexOf(Cons.slashChar).coerceAtLeast(0)) }.ifEmpty { FsUtils.rootPath })
        }else if(isFileChooser){
            naviUp()
        } else {
            if (backStartSec.longValue > 0 && getSecFromTime() <= backStartSec.longValue) {  
                exitApp()
            } else {
                showTextAndUpdateTimeForPressBackBtn()
            }
        }
    }
    return backHandlerOnBack
}
private fun breadCrumbPathNotCoverdCurPath(curBreadCrumbList:List<FileItemDto>, curDirPath:String, separator:Char):Boolean {
    return if(curBreadCrumbList.isEmpty()) {
        true
    }else {
        val breadCrumbLastItemPath = curBreadCrumbList[curBreadCrumbList.size-1].fullPath
        val breadCrumbPathForCompare = if(breadCrumbLastItemPath.endsWith(separator)) breadCrumbLastItemPath else "$breadCrumbLastItemPath$separator"
        val curDirPathForCompare = if(curDirPath.endsWith(separator)) curDirPath else "$curDirPath$separator"
        !breadCrumbPathForCompare.startsWith(curDirPathForCompare)
    }
}
