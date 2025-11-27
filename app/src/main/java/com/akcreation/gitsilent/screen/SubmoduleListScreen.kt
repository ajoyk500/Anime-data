package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReplayCircleFilled
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.BottomBar
import com.akcreation.gitsilent.compose.CenterPaddingRow
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.CopyScrollableColumn
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CredentialSelector
import com.akcreation.gitsilent.compose.DefaultPaddingText
import com.akcreation.gitsilent.compose.DepthTextField
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.FullScreenScrollableColumn
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LoadingDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.ResetDialog
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SelectedItemDialog
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.git.ImportRepoResult
import com.akcreation.gitsilent.git.SubmoduleDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.SubmoduleItem
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.openFileWithInnerSubPageEditor
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.parseIntOrDefault
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.updateSelectedList
import com.github.git24j.core.Repository
import java.io.File

private const val TAG = "SubmoduleListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SubmoduleListScreen(
    repoId:String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val inDarkTheme = Theme.inDarkTheme
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<SubmoduleDto>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<SubmoduleDto>())
    val listState = rememberLazyListState()
    val needRefresh = rememberSaveable { mutableStateOf("") }
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))
    val defaultLoadingText = stringResource(R.string.loading)
    val loading = rememberSaveable { mutableStateOf(false)}
    val loadingText = rememberSaveable { mutableStateOf( defaultLoadingText)}
    val loadingOn = { text:String ->
        loadingText.value=text
        loading.value=true
    }
    val loadingOff = {
        loadingText.value = activityContext.getString(R.string.loading)
        loading.value=false
    }
    val credentialList = mutableCustomStateListOf(stateKeyTag, "credentialList", listOf<CredentialEntity>())
    val selectedCredentialIdx = rememberSaveable{mutableIntStateOf(0)}
    val showCreateDialog = rememberSaveable { mutableStateOf(false)}
    val remoteUrlForCreate = rememberSaveable { mutableStateOf("")}
    val pathForCreate = rememberSaveable { mutableStateOf("")}
    val initCreateDialog = {
        showCreateDialog.value = true
    }
    if(showCreateDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.create),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn{
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = pathForCreate.value,
                        singleLine = true,
                        onValueChange = {
                            pathForCreate.value = it
                        },
                        label = {
                            Text(stringResource(R.string.path))
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = remoteUrlForCreate.value,
                        singleLine = true,
                        onValueChange = {
                            remoteUrlForCreate.value = it
                        },
                        label = {
                            Text(stringResource(R.string.url))
                        },
                    )
                }
            },
            okBtnEnabled = pathForCreate.value.isNotBlank() &&remoteUrlForCreate.value.isNotBlank(),
            onCancel = {showCreateDialog.value = false}
        ) {
            showCreateDialog.value = false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.creating)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        Libgit2Helper.addSubmodule(
                            repo = repo,
                            remoteUrl = remoteUrlForCreate.value,
                            relativePathUnderParentRepo = pathForCreate.value
                        )
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    val errPrefix = "create submodule '${pathForCreate.value}' err: "
                    val errMsg = e.localizedMessage
                    Msg.requireShowLongDuration(errMsg ?: errPrefix)
                    createAndInsertError(curRepo.value.id, "$errPrefix$errMsg")
                    MyLog.e(TAG, "#CreateDialog err: path=${pathForCreate.value}, url=${remoteUrlForCreate.value}, err=${e.stackTraceToString()}")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val multiSelectionMode = rememberSaveable { mutableStateOf(false)}
    val selectedItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "selectedItemList",listOf<SubmoduleDto>() )
    val quitSelectionMode = {
        multiSelectionMode.value=false  
        selectedItemList.value.clear()  
    }
    val getSelectedFilesCount = {
        selectedItemList.value.size
    }
    val containsForSelectedItems = { srcList:List<SubmoduleDto>, curItem:SubmoduleDto ->
        srcList.indexOfFirst { it.name == curItem.name } != -1
    }
    val switchItemSelected = { item: SubmoduleDto ->
        UIHelper.selectIfNotInSelectedListElseRemove(item, selectedItemList.value, contains = containsForSelectedItems)
        multiSelectionMode.value = true
    }
    val selectItem = { item:SubmoduleDto ->
        multiSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseNoop(item, selectedItemList.value, contains = containsForSelectedItems)
    }
    val isItemInSelected= { item:SubmoduleDto ->
        containsForSelectedItems(selectedItemList.value, item)
    }
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val getDetail = { item:SubmoduleDto ->
        val sb = StringBuilder()
        sb.appendLine(activityContext.getString(R.string.name)+": "+item.name).appendLine()
            .appendLine(activityContext.getString(R.string.url)+": "+item.remoteUrl).appendLine()
            .appendLine(activityContext.getString(R.string.target)+": "+item.targetHash).appendLine()
            .appendLine(activityContext.getString(R.string.location)+": "+item.location.toString()).appendLine()
            .appendLine(activityContext.getString(R.string.path)+": "+item.relativePathUnderParent).appendLine()
            .appendLine(activityContext.getString(R.string.full_path)+": "+item.fullPath).appendLine()
            .appendLine(activityContext.getString(R.string.status)+": "+item.getStatus(activityContext)).appendLine()
            .append(activityContext.getString(R.string.other)+": "+item.getOther())
        sb.toString()
    }
    val showDetailsDialog = rememberSaveable { mutableStateOf(false)}
    val detailsString = rememberSaveable { mutableStateOf("")}
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = stringResource(id = R.string.details),
            text = detailsString.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsString.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    val showSetUrlDialog = rememberSaveable { mutableStateOf(false)}
    val urlForSetUrlDialog = rememberSaveable { mutableStateOf( "")}
    val nameForSetUrlDialog = rememberSaveable { mutableStateOf( "")}
    if(showSetUrlDialog.value) {
        ConfirmDialog2(title = activityContext.getString(R.string.set_url),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = urlForSetUrlDialog.value,
                        singleLine = true,
                        onValueChange = {
                            urlForSetUrlDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.url))
                        },
                    )
                }
            },
            onCancel = {showSetUrlDialog.value = false}
        ) {
            showSetUrlDialog.value = false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.updating)) act@{
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val sm = Libgit2Helper.resolveSubmodule(repo, nameForSetUrlDialog.value)
                        if(sm==null) {
                            Msg.requireShowLongDuration(activityContext.getString(R.string.resolve_submodule_failed))
                            return@act
                        }
                        Libgit2Helper.updateSubmoduleUrl(repo, sm, urlForSetUrlDialog.value)
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?: " err")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val showSyncConfigDialog = rememberSaveable { mutableStateOf(false)}
    val syncParentConfig = rememberSaveable { mutableStateOf(false)}
    val syncSubmoduleConfig = rememberSaveable { mutableStateOf(false)}
    if(showSyncConfigDialog.value) {
        ConfirmDialog2(title = activityContext.getString(R.string.sync_configs),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(stringResource(R.string.will_sync_info_from_gitmodules_to_selected_configs))
                    }
                    Spacer(Modifier.height(15.dp))
                    MyCheckBox(text = stringResource(R.string.parent_config), value = syncParentConfig)
                    MyCheckBox(text = stringResource(R.string.submodule_config), value = syncSubmoduleConfig)
                }
            },
            onCancel = {showSyncConfigDialog.value = false},
            okBtnEnabled = syncParentConfig.value || syncSubmoduleConfig.value
        ) {
            showSyncConfigDialog.value=false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.updating)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        selectedItemList.value.toList().forEachBetter {
                            val sm = Libgit2Helper.openSubmodule(repo, it.name)
                            if(sm!=null) {
                                if(syncParentConfig.value) {
                                    try {
                                        sm.init(true)
                                    }catch (_:Exception) {
                                    }
                                }
                                if(syncSubmoduleConfig.value) {
                                    try {
                                        sm.sync()
                                    }catch (_:Exception) {
                                    }
                                }
                            }
                        }
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?: "err")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val showInitRepoDialog = rememberSaveable { mutableStateOf(false)}
    if(showInitRepoDialog.value) {
        ConfirmDialog2(title = activityContext.getString(R.string.init_repo),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    Text(stringResource(R.string.will_do_init_repo_for_selected_submodules), fontWeight = FontWeight.Light)
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.most_time_need_not_do_init_repo_by_yourself))
                }
            },
            onCancel = {showInitRepoDialog.value = false},
        ) {
            showInitRepoDialog.value=false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo ->
                        val repoWorkDirFullPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                        selectedItemList.value.toList().forEachBetter {
                            val sm = Libgit2Helper.openSubmodule(repo, it.name)
                            if(sm!=null) {
                                try {
                                    Libgit2Helper.submoduleRepoInit(repoWorkDirFullPath, sm)
                                }catch (_:Exception){
                                }
                            }
                        }
                    }
                    Msg.requireShow(activityContext.getString(R.string.done))
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?: "err")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val showRestoreDotGitFileDialog = rememberSaveable { mutableStateOf(false)}
    if(showRestoreDotGitFileDialog.value) {
        ConfirmDialog2(
            title = activityContext.getString(R.string.restore_dot_git_file),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    Text(stringResource(R.string.will_try_restore_git_file_for_selected_submodules), fontWeight = FontWeight.Light)
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.most_time_need_not_restore_dot_git_file_by_yourself))
                }
            },
            onCancel = { showRestoreDotGitFileDialog.value = false },
        ) {
            showRestoreDotGitFileDialog.value = false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.restoring)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo ->
                        val repoWorkDirFullPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                        selectedItemList.value.toList().forEachBetter {
                            try {
                                Libgit2Helper.SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoWorkDirFullPath, it.relativePathUnderParent)
                            }catch (_:Exception){
                            }
                        }
                    }
                    Msg.requireShow(activityContext.getString(R.string.done))
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?:"err")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val showReloadDialog = rememberSaveable { mutableStateOf(false)}
    val forceReload = rememberSaveable { mutableStateOf(false)}
    if(showReloadDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.reload),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(stringResource(R.string.reload_submodule_note))
                    }
                    Spacer(Modifier.height(15.dp))
                    MyCheckBox(text = stringResource(R.string.force), forceReload)
                }
            },
            onCancel = {showReloadDialog.value=false},
        ) {
            showReloadDialog.value=false
            val force = forceReload.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.reloading)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { parentRepo ->
                        selectedItemList.value.toList().forEachBetter {
                            try {
                                val sm = Libgit2Helper.resolveSubmodule(parentRepo, it.name)
                                if(sm!=null) {
                                    Libgit2Helper.reloadSubmodule(sm, force)
                                }
                            }catch (e:Exception) {
                                MyLog.e(TAG, "reload submodule '${it.name}' err: ${e.localizedMessage}")
                            }
                        }
                    }
                    Msg.requireShow(activityContext.getString(R.string.done))
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val showResetToTargetDialog = rememberSaveable { mutableStateOf(false)}
    val closeResetDialog = {showResetToTargetDialog.value=false}
    if(showResetToTargetDialog.value) {
        ResetDialog(
            fullOidOrBranchOrTag = null,  
            closeDialog=closeResetDialog,
            repoFullPath = curRepo.value.fullSavePath,  
            repoId=repoId,  
            refreshPage = {_, _, _-> },  
            onOk = { resetType->
                closeResetDialog()
                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.resetting)) {
                    try {
                        selectedItemList.value.toList().forEachBetter {
                            if (it.targetHash.isNotBlank()) {
                                try {
                                    Repository.open(it.fullPath).use { subRepo ->
                                        if (subRepo.headUnborn().not()) {  
                                            Libgit2Helper.resetToRevspec(subRepo, it.targetHash, resetType)
                                        }
                                    }
                                } catch (_: Exception) {
                                }
                            }
                        }
                        Msg.requireShow(activityContext.getString(R.string.done))
                    }finally {
                        changeStateTriggerRefreshPage(needRefresh)
                    }
                }
            }
        )
    }
    val showImportToReposDialog = rememberSaveable { mutableStateOf(false)}
    if(showImportToReposDialog.value){
        ConfirmDialog2(
            title = activityContext.getString(R.string.import_to_repos),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    CredentialSelector(credentialList.value, selectedCredentialIdx)
                    Spacer(Modifier.height(10.dp))
                    MySelectionContainer {
                        DefaultPaddingText(stringResource(R.string.import_repos_link_credential_note))
                    }
                }
            },
            onCancel = { showImportToReposDialog.value = false },
        ) {
            showImportToReposDialog.value = false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.importing)) {
                val repoNameSuffix = Libgit2Helper.genRepoNameSuffixForSubmodule(curRepo.value.repoName)
                val parentRepoId = curRepo.value.id
                val importList = selectedItemList.value.toList()  
                val selectedCredentialId = credentialList.value[selectedCredentialIdx.intValue].id
                val repoDb = AppModel.dbContainer.repoRepository
                val importRepoResult = ImportRepoResult()
                try {
                    importList.forEachBetter {

                        val result = repoDb.importRepos(dir=it.fullPath, isReposParent=false, repoNameSuffix = repoNameSuffix, parentRepoId = parentRepoId, credentialId = selectedCredentialId)
                        importRepoResult.all += result.all
                        importRepoResult.success += result.success
                        importRepoResult.failed += result.failed
                        importRepoResult.existed += result.existed

                    }
                    Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.n_imported), listOf(""+importRepoResult.success)))
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage
                    Msg.requireShowLongDuration(errMsg ?: "import err")
                    createAndInsertError(curRepo.value.id, "import repos err: $errMsg")
                    MyLog.e(TAG, "import repos from SubmoduleListPage err: importRepoResult=$importRepoResult, err="+e.stackTraceToString())
                }finally {
                }
            }
        }
    }
    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val filterKeyword =mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn = rememberSaveable { mutableStateOf(false)}
    val lastKeyword = rememberSaveable { mutableStateOf("") }
    val token = rememberSaveable { mutableStateOf("") }
    val searching = rememberSaveable { mutableStateOf(false) }
    val resetSearchVars = {
        searching.value = false
        token.value = ""
        lastKeyword.value = ""
    }
    val recursiveClone = rememberSaveable { mutableStateOf( false)}
    val showCloneDialog = rememberSaveable { mutableStateOf( false)}
    val deleteConfigForDeleteDialog =rememberSaveable { mutableStateOf(false)}
    val deleteFilesForDeleteDialog =rememberSaveable { mutableStateOf(false)}
    val showDeleteDialog = rememberSaveable { mutableStateOf(false)}
    val initDelDialog = {
        deleteConfigForDeleteDialog.value = false
        deleteFilesForDeleteDialog.value = false
        showDeleteDialog.value = true
    }
    if(showDeleteDialog.value) {
        ConfirmDialog2(title = activityContext.getString(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MyCheckBox(text = stringResource(R.string.del_config), value = deleteConfigForDeleteDialog)
                    if(deleteConfigForDeleteDialog.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.submodule_del_config_info_note))
                        }
                    }
                    Spacer(Modifier.height(15.dp))
                    MyCheckBox(text = stringResource(R.string.del_files), value = deleteFilesForDeleteDialog)
                    if(deleteFilesForDeleteDialog.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.submodule_del_files_on_disk_note))
                        }
                    }
                }
            },
            okBtnEnabled = deleteConfigForDeleteDialog.value || deleteFilesForDeleteDialog.value,
            onCancel = {showDeleteDialog.value = false}
        ) {
            showDeleteDialog.value=false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.deleting)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val repoWorkDirPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                        selectedItemList.value.toList().forEachBetter { smdto ->
                            try {
                                Libgit2Helper.removeSubmodule(
                                    deleteFiles = deleteFilesForDeleteDialog.value,
                                    deleteConfigs = deleteConfigForDeleteDialog.value,
                                    repo = repo,
                                    repoWorkDirPath = repoWorkDirPath,
                                    submoduleName = smdto.name,
                                    submoduleFullPath = smdto.fullPath,
                                )
                            }catch (e:Exception) {
                                val errPrefix = "del submodule err: delConfig=${deleteConfigForDeleteDialog.value}, delFiles=${deleteFilesForDeleteDialog.value}, err="
                                val errMsg = e.localizedMessage
                                Msg.requireShowLongDuration(errMsg ?: "err")
                                createAndInsertError(curRepo.value.id, errPrefix+errMsg)
                                MyLog.e(TAG, "#DeleteDialog err: delConfig=${deleteConfigForDeleteDialog.value}, delFiles=${deleteFilesForDeleteDialog.value}, err=${e.stackTraceToString()}")
                            }
                        }
                    }
                    Msg.requireShow(activityContext.getString(R.string.done))
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val initCloneDialog= {
        recursiveClone.value = false
        showCloneDialog.value = true
    }
    val depth = rememberSaveable { mutableStateOf("") }
    val showUpdateDialog = rememberSaveable { mutableStateOf(false)}
    val recursiveUpdate = rememberSaveable { mutableStateOf(false)}
    val initUpdateDialog = {
        recursiveUpdate.value = false
        showUpdateDialog.value=true
    }
    if(showCloneDialog.value) {
        ConfirmDialog2(title = activityContext.getString(R.string.clone),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    CredentialSelector(credentialList.value, selectedCredentialIdx)
                    Spacer(Modifier.height(5.dp))
                    DepthTextField(depth)
                    Spacer(Modifier.height(5.dp))
                    MyCheckBox(text = stringResource(R.string.recursive), value = recursiveClone)
                    if(recursiveClone.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.recursive_clone_submodule_nested_loop_warn), color = MyStyleKt.TextColor.danger())
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            },
            onCancel = {showCloneDialog.value = false}
        ) {
            showCloneDialog.value=false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.cloning)) {
                try {
                    val recursive = recursiveClone.value
                    val depth = parseIntOrDefault(depth.value, null) ?: 0;
                    val willCloneList = selectedItemList.value.toList()
                    val credentialDb = AppModel.dbContainer.credentialRepository
                    val selectedCredential = credentialList.value[selectedCredentialIdx.intValue]
                    val credential = if(SpecialCredential.MatchByDomain.credentialId == selectedCredential.id) selectedCredential.copy() else credentialDb.getByIdWithDecrypt(selectedCredential.id)
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        willCloneList.forEachBetter { selectedItem ->
                            try {
                                Libgit2Helper.cloneSubmodules(repo, recursive, depth, specifiedCredential=credential, submoduleNameList= listOf(selectedItem.name), credentialDb=credentialDb)
                            }catch (e:Exception) {
                                val errPrefix = "clone '${selectedItem.name}' err: "
                                val errMsg = e.localizedMessage ?: "clone submodule err"
                                Msg.requireShow(errMsg)
                                createAndInsertError(curRepo.value.id, errPrefix+errMsg)
                            }
                        }
                    }
                    Msg.requireShow(activityContext.getString(R.string.done))
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    if(showUpdateDialog.value) {
        ConfirmDialog2(title = activityContext.getString(R.string.update),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    CredentialSelector(credentialList.value, selectedCredentialIdx)
                    Spacer(Modifier.height(5.dp))
                    MyCheckBox(text = stringResource(R.string.recursive), value = recursiveUpdate)
                    if(recursiveUpdate.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.recursive_update_submodule_nested_loop_warn), color = MyStyleKt.TextColor.danger())
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            },
            onCancel = { showUpdateDialog.value = false }
        ) {
            showUpdateDialog.value = false
            val curRepo = curRepo.value
            val recursiveUpdate = recursiveUpdate.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.updating)) {
                val selectedItemList = selectedItemList.value.toList()
                try {
                    val selectedCredential = credentialList.value[selectedCredentialIdx.intValue]
                    val credentialDb = AppModel.dbContainer.credentialRepository
                    val credential = if(SpecialCredential.MatchByDomain.credentialId == selectedCredential.id) selectedCredential.copy() else credentialDb.getByIdWithDecrypt(selectedCredential.id)
                    Repository.open(curRepo.fullSavePath).use { repo ->
                        selectedItemList.forEachBetter {
                            try {
                                Libgit2Helper.updateSubmodule(repo, credential, listOf(it.name), recursiveUpdate, credentialDb, superParentRepo = repo)
                            }catch (e:Exception) {
                                createAndInsertError(curRepo.id, "update submodule '${it.name}' err: ${e.localizedMessage}")
                                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                            }
                        }
                    }
                    Msg.requireShow(activityContext.getString(R.string.done))
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val filterListState = rememberLazyListState()
    val enableFilterState = rememberSaveable { mutableStateOf(false) }
    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false)}
    if(showSelectedItemsShortDetailsDialog.value) {
        SelectedItemDialog(
            selectedItems = selectedItemList.value,
            formatter = {it.name},
            switchItemSelected = switchItemSelected,
            clearAll = {selectedItemList.value.clear()},
            closeDialog = {showSelectedItemsShortDetailsDialog.value = false}
        )
    }
    val countNumOnClickForBottomBar = {
        showSelectedItemsShortDetailsDialog.value = true
    }
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    BackHandler {
        if(multiSelectionMode.value) {
            quitSelectionMode()
        } else if(filterModeOn.value) {
            filterModeOn.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }
    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingOn = { msg:String ->
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
    }
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(filterModeOn.value) {
                        FilterTextField(filterKeyWord = filterKeyword, loading = searching.value)
                    }else {
                        val repoAndBranch = Libgit2Helper.getRepoOnBranchOrOnDetachedHash(curRepo.value)
                        Column (modifier = Modifier.combinedClickable (
                            onDoubleClick = {
                                defaultTitleDoubleClick(scope, listState, lastPosition)
                            },
                        ){  
                            showTitleInfoDialog.value = true
                        }){
                            ScrollableRow  {
                                Text(
                                    text= stringResource(R.string.submodules),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            ScrollableRow  {
                                Text(
                                    text= repoAndBranch,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.secondLineFontSize
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if(filterModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),
                        ) {
                            resetSearchVars()
                            filterModeOn.value = false
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
                    if(!filterModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.filter),
                            icon =  Icons.Filled.FilterAlt,
                            iconContentDesc = stringResource(R.string.filter),
                        ) {
                            filterKeyword.value = TextFieldValue("")
                            filterModeOn.value = true
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.refresh),
                            icon =  Icons.Filled.Refresh,
                            iconContentDesc = stringResource(R.string.refresh),
                        ) {
                            changeStateTriggerRefreshPage(needRefresh)
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.create),
                            icon =  Icons.Filled.Add,
                            iconContentDesc = stringResource(R.string.create),
                        ) {
                            initCreateDialog()
                        }
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(pageScrolled.value) {
                GoToTopAndGoToBottomFab(
                    filterModeOn = enableFilterState.value,
                    scope = scope,
                    filterListState = filterListState,
                    listState = listState,
                    filterListLastPosition = filterLastPosition,
                    listLastPosition = lastPosition,
                    showFab = pageScrolled
                )
            }
        }
    ) { contentPadding ->
        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {
            if (loading.value) {
                LoadingDialog(text = loadingText.value)
            }
            if(list.value.isEmpty()) {  
                FullScreenScrollableColumn(contentPadding) {
                    if(isInitLoading.value) {
                        Text(stringResource(R.string.loading))
                    }else {
                        Row {
                            Text(text = stringResource(R.string.no_submodules_found))
                        }
                        CenterPaddingRow {
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Add,
                                tooltipText = stringResource(R.string.create),
                            ) {
                                initCreateDialog()
                            }
                        }
                    }
                }
            }else {  
                val keyword = filterKeyword.value.text  
                val enableFilter = filterModeActuallyEnabled(filterModeOn.value, keyword)
                val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
                val list = filterTheList(
                    needRefresh = filterResultNeedRefresh.value,
                    lastNeedRefresh = lastNeedRefresh,
                    enableFilter = enableFilter,
                    keyword = keyword,
                    lastKeyword = lastKeyword,
                    searching = searching,
                    token = token,
                    activityContext = activityContext,
                    filterList = filterList.value,
                    list = list.value,
                    resetSearchVars = resetSearchVars,
                    match = { idx:Int, it: SubmoduleDto ->
                        it.name.contains(keyword, ignoreCase = true)
                                || it.remoteUrl.contains(keyword, ignoreCase = true)
                                || it.getStatus(activityContext).contains(keyword, ignoreCase = true)
                                || it.targetHash.contains(keyword, ignoreCase = true)
                                || it.location.toString().contains(keyword, ignoreCase = true)
                                || it.getOther().contains(keyword, ignoreCase = true)
                    }
                )
                val listState = if(enableFilter) filterListState else listState
                enableFilterState.value = enableFilter
                MyLazyColumn(
                    contentPadding = contentPadding,
                    list = list,
                    listState = listState,
                    requireForEachWithIndex = true,
                    requirePaddingAtBottom = true,
                    forEachCb = {},
                ){idx, it->
                    SubmoduleItem(it, lastClickedItemKey, isItemInSelected, onLongClick = {
                        if(multiSelectionMode.value) {  
                            UIHelper.doSelectSpan(
                                itemIdxOfItemList = idx,
                                item = it,
                                selectedItems = selectedItemList.value,
                                itemList = list,
                                switchItemSelected = switchItemSelected,
                                selectIfNotInSelectedListElseNoop = selectItem
                            )
                        }else {  
                            switchItemSelected(it)
                        }
                    }
                    ) {  
                        if(multiSelectionMode.value) {  
                            UIHelper.selectIfNotInSelectedListElseRemove(it, selectedItemList.value, contains = containsForSelectedItems)
                        }else {  
                            detailsString.value = getDetail(it)
                            showDetailsDialog.value = true
                        }
                    }
                    MyHorizontalDivider()
                }
                if (multiSelectionMode.value) {
                    val iconList:List<ImageVector> = listOf(
                        Icons.Filled.Delete,  
                        Icons.Filled.ReplayCircleFilled,  
                        Icons.Filled.DownloadForOffline,  
                        Icons.Filled.SelectAll,  
                    )
                    val iconTextList:List<String> = listOf(
                        stringResource(id = R.string.delete),
                        stringResource(R.string.update),
                        stringResource(id = R.string.clone),
                        stringResource(id = R.string.select_all),
                    )
                    val iconEnableList:List<()->Boolean> = listOf(
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.isNotEmpty()},  
                        {true} 
                    )
                    val iconOnClickList:List<()->Unit> = listOf(  
                        delete@{
                            initDelDialog()
                        },
                        update@{
                            initUpdateDialog()
                        },
                        clone@{
                            initCloneDialog()
                        },
                        selectAll@{
                            list.forEachBetter {
                                selectItem(it)
                            }
                            Unit
                        },
                    )
                    val moreItemEnableList:List<()->Boolean> = (listOf(
                        {selectedItemList.value.size == 1},  
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.size == 1},  
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.size == 1},  
                        {selectedItemList.value.isNotEmpty()},  
                    ))
                    val moreItemTextList = (listOf(
                        stringResource(R.string.copy_full_path),
                        stringResource(R.string.import_to_repos),
                        stringResource(R.string.reset_to_target),
                        stringResource(R.string.set_url),
                        stringResource(R.string.reload),
                        stringResource(R.string.sync_configs),
                        stringResource(R.string.init_repo),
                        stringResource(R.string.restore_dot_git_file),
                        stringResource(R.string.edit_config),
                        stringResource(R.string.details),  
                    ))
                    val moreItemOnClickList:List<()->Unit> = (listOf(
                        copyFullPath@{  
                            try {
                                if(selectedItemList.value.isNotEmpty()) {
                                    clipboardManager.setText(AnnotatedString(selectedItemList.value[0].fullPath))
                                    Msg.requireShow(activityContext.getString(R.string.copied))
                                }else {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_item_selected))
                                }
                            }catch (e:Exception){
                                MyLog.e(TAG, "#copyFullPath for Submodule err: ${e.localizedMessage}")
                                e.printStackTrace()
                                Msg.requireShowLongDuration("err: " + e.localizedMessage)
                            }
                        },
                        importToRepos@{

                            showImportToReposDialog.value = true
                        },
                        resetToTarget@{
                            showResetToTargetDialog.value = true
                        },
                        setUrl@{  
                            try {
                                if(selectedItemList.value.isNotEmpty()) {
                                    val curItem = selectedItemList.value[0]
                                    urlForSetUrlDialog.value = curItem.remoteUrl
                                    nameForSetUrlDialog.value = curItem.name
                                    showSetUrlDialog.value = true
                                }else {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_item_selected))
                                }
                            }catch (e:Exception){
                                MyLog.e(TAG, "#setUrl for Submodule err: ${e.localizedMessage}")
                                e.printStackTrace()
                                Msg.requireShowLongDuration("err: "+e.localizedMessage)
                            }
                        },
                        reload@{
                            forceReload.value=false
                            showReloadDialog.value = true
                        },
                        syncConfigs@{  
                            syncParentConfig.value = true
                            syncSubmoduleConfig.value = true
                            showSyncConfigDialog.value = true
                        },
                        initRepo@{ 
                            showInitRepoDialog.value = true
                        },
                        restoreDotGitFile@{ 
                            showRestoreDotGitFileDialog.value = true
                        },
                        editConfig@{
                            val item = selectedItemList.value.firstOrNull()
                            if(item == null) {
                                Msg.requireShowLongDuration(activityContext.getString(R.string.no_item_selected))
                                return@editConfig
                            }
                            try {
                                Repository.open(item.fullPath).use { subRepo ->
                                    val configPath = Libgit2Helper.getRepoConfigFilePath(subRepo)
                                    if(File(configPath).exists().not()) {
                                        Msg.requireShowLongDuration(activityContext.getString(R.string.file_doesnt_exist))
                                        return@editConfig
                                    }
                                    openFileWithInnerSubPageEditor(
                                        context = activityContext,
                                        filePath = configPath,
                                        mergeMode = false,
                                        readOnly = false,
                                    )
                                }
                            }catch (e: Exception) {
                                MyLog.e(TAG, "#editConfig for Submodule err: ${e.localizedMessage}")
                                e.printStackTrace()
                                Msg.requireShowLongDuration("err: "+e.localizedMessage)
                            }
                        },
                        details@{
                            val sb = StringBuilder()
                            val spliter = Cons.itemDetailSpliter
                            selectedItemList.value.forEachBetter {
                                sb.append(getDetail(it))
                                sb.append(spliter)
                            }
                            detailsString.value = sb.removeSuffix(spliter).toString()
                            showDetailsDialog.value = true
                        },
                    ))
                    BottomBar(
                        quitSelectionMode=quitSelectionMode,
                        iconList=iconList,
                        iconTextList=iconTextList,
                        iconDescTextList=iconTextList,
                        iconOnClickList=iconOnClickList,
                        iconEnableList=iconEnableList,
                        moreItemTextList=moreItemTextList,
                        moreItemOnClickList=moreItemOnClickList,
                        moreItemEnableList = moreItemEnableList,
                        moreItemVisibleList = moreItemEnableList,
                        getSelectedFilesCount = getSelectedFilesCount,
                        countNumOnClickEnabled = true,
                        countNumOnClick = countNumOnClickForBottomBar,
                        reverseMoreItemList = true
                    )
                }
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        try {
            val refreshId = needRefresh.value
            val pageChanged = {
                refreshId != needRefresh.value
            }
            doJobThenOffLoading(initLoadingOn, initLoadingOff) {
                list.value.clear()  
                credentialList.value.clear()
                if(!repoId.isNullOrBlank()) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb!=null) {
                        curRepo.value = repoFromDb
                        Repository.open(repoFromDb.fullSavePath).use {repo ->
                            val items = Libgit2Helper.getSubmoduleDtoList(repo, invalidUrlAlertText = activityContext.getString(R.string.submodule_invalid_url_err));
                            list.value.addAll(items)
                        }
                    }
                    val credentialDb = AppModel.dbContainer.credentialRepository
                    val credentialListFromDb = credentialDb.getAll(includeNone = true, includeMatchByDomain = true)
                    if(credentialListFromDb.isNotEmpty()) {
                        credentialList.value.addAll(credentialListFromDb)
                    }
                }
                val pageChangedNeedAbort = updateSelectedList(
                    selectedItemList = selectedItemList.value,
                    itemList = list.value,
                    quitSelectionMode = quitSelectionMode,
                    match = { oldSelected, item-> oldSelected.name == item.name },
                    pageChanged = pageChanged
                )
                if (pageChangedNeedAbort) return@doJobThenOffLoading
                triggerReFilter(filterResultNeedRefresh)
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "$TAG#LaunchedEffect() err: "+e.stackTraceToString())
        }
    }
}
