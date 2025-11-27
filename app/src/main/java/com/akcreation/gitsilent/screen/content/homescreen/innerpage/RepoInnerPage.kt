package com.akcreation.gitsilent.screen.content.homescreen.innerpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.compose.AddRepoDropDownMenu
import com.akcreation.gitsilent.compose.AskGitUsernameAndEmailDialog
import com.akcreation.gitsilent.compose.AskGitUsernameAndEmailDialogWithSelection
import com.akcreation.gitsilent.compose.BottomBar
import com.akcreation.gitsilent.compose.CommitMsgMarkDownDialog
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CopyableDialog2
import com.akcreation.gitsilent.compose.DefaultPaddingRow
import com.akcreation.gitsilent.compose.DefaultPaddingText
import com.akcreation.gitsilent.compose.FullScreenScrollableColumn
import com.akcreation.gitsilent.compose.InternalFileChooser
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PageCenterIconButton
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RepoCard
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.SelectedItemDialog
import com.akcreation.gitsilent.compose.SelectionRow
import com.akcreation.gitsilent.compose.SetUpstreamDialog
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.data.AppContainer
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.etc.RepoPendingTask
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.Upstream
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.goToErrScreen
import com.akcreation.gitsilent.screen.functions.goToStashPage
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.server.bean.ConfigBean
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.PackageNameAndRepo
import com.akcreation.gitsilent.settings.PackageNameAndRepoSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.ComposeHelper
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.RepoStatusUtil
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.doActIfIndexGood
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import com.akcreation.gitsilent.utils.genHttpHostPortStr
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.isLocked
import com.akcreation.gitsilent.utils.isRepoReadyAndPathExist
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.showErrAndSaveLog
import com.akcreation.gitsilent.utils.state.CustomStateListSaveable
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.strHasIllegalChars
import com.akcreation.gitsilent.utils.updateSelectedList
import com.github.git24j.core.Repository
import kotlinx.coroutines.sync.withLock
import java.io.File

private const val TAG = "RepoInnerPage"
private const val invalidIdx = -1
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoInnerPage(
    stateKeyTag:String,
    requireInnerEditorOpenFile:(filePath:String, expectReadOnly:Boolean)->Unit,
    lastSearchKeyword:MutableState<String>,
    searchToken:MutableState<String>,
    searching:MutableState<Boolean>,
    resetSearchVars:()->Unit,
    showBottomSheet: MutableState<Boolean>,
    sheetState: SheetState,
    curRepo: CustomStateSaveable<RepoEntity>,
    curRepoIndex: MutableIntState,
    contentPadding: PaddingValues,
    repoPageListState: LazyListState,
    showSetGlobalGitUsernameAndEmailDialog:MutableState<Boolean>,
    needRefreshRepoPage:MutableState<String>,
    repoList:CustomStateListSaveable<RepoEntity>,
    goToFilesPage:(path:String) -> Unit,
    goToChangeListPage:(repoWillShowInChangeListPage: RepoEntity) -> Unit,
    repoPageScrolled:MutableState<Boolean>,
    repoPageFilterModeOn:MutableState<Boolean>,
    repoPageFilterKeyWord:CustomStateSaveable<TextFieldValue>,
    filterListState:LazyListState,
    openDrawer:()->Unit,
    showImportRepoDialog:MutableState<Boolean>,
    goToThisRepoId:MutableState<String>,
    enableFilterState:MutableState<Boolean>,
    filterList:CustomStateListSaveable<RepoEntity>,
    isSelectionMode:MutableState<Boolean>,
    selectedItems:CustomStateListSaveable<RepoEntity>,
    unshallowList:CustomStateListSaveable<RepoEntity>,
    deleteList:CustomStateListSaveable<RepoEntity>,
    userInfoRepoList:CustomStateListSaveable<RepoEntity>,
    upstreamRemoteOptionsList:CustomStateListSaveable<String>,
    specifiedRefreshRepoList:CustomStateListSaveable<RepoEntity>,
    showWelcomeToNewUser:MutableState<Boolean>,
    closeWelcome:()->Unit,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val activityContext = LocalContext.current
    val exitApp = AppModel.exitApp;
    val navController = AppModel.navController;
    val scope = rememberCoroutineScope()
    val settings = remember {
        val s = SettingsUtil.getSettingsSnapshot()
        repoPageScrolled.value = s.showNaviButtons
        s
    }
    val itemWidth = remember { UIHelper.getRepoItemWidth() }
    val configuration = AppModel.getCurActivityConfig()
    val repoCountEachRow = remember(configuration.screenWidthDp) { UIHelper.getRepoItemsCountEachRow(configuration.screenWidthDp.toFloat()) }
    val clipboardManager = LocalClipboardManager.current
    val cloningText = stringResource(R.string.cloning)
    val unknownErrWhenCloning = stringResource(R.string.unknown_err_when_cloning)
    val dbContainer = AppModel.dbContainer;
    val inDarkTheme = Theme.inDarkTheme
    val requireBlinkIdx = rememberSaveable { mutableIntStateOf(-1) }
    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingText = rememberSaveable { mutableStateOf(activityContext.getString(R.string.loading)) }
    val initLoadingOn = {text:String->
        initLoadingText.value = text
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
        initLoadingText.value = ""
    }
    val errWhenQuerySettingsFromDbStrRes = stringResource(R.string.err_when_querying_settings_from_db)
    val saved = stringResource(R.string.saved)
    val pageRequest = rememberSaveable { mutableStateOf("")}
    val repoOfSetUsernameAndEmailDialog = mutableCustomStateOf(stateKeyTag, "repoOfSetUsernameAndEmailDialog") { RepoEntity(id = "") }
    val username = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val showUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val afterSetUsernameAndEmailSuccessCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "afterSetUsernameAndEmailSuccessCallback") { null }
    val initSetUsernameAndEmailDialog = { targetRepo:RepoEntity, callback:(()->Unit)? ->
        try {
            Repository.open(targetRepo.fullSavePath).use { repo ->
                val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
                username.value = usernameFromConfig
                email.value = emailFromConfig
            }
            repoOfSetUsernameAndEmailDialog.value = targetRepo
            afterSetUsernameAndEmailSuccessCallback.value = callback
            showUsernameAndEmailDialog.value = true
        }catch (e:Exception) {
            Msg.requireShowLongDuration("init username and email dialog err: ${e.localizedMessage}")
            MyLog.e(TAG, "#initSetUsernameAndEmailDialog err: ${e.stackTraceToString()}")
        }
    }
    val doTaskOrShowSetUsernameAndEmailDialog = { curRepo:RepoEntity, task:(()->Unit)? ->
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                if(Libgit2Helper.repoUsernameAndEmailInvaild(repo)) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.plz_set_username_and_email_first))
                    initSetUsernameAndEmailDialog(curRepo, task)
                }else {
                    task?.invoke()
                }
            }
        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "#doTaskOrShowSetUsernameAndEmailDialog err: ${e.stackTraceToString()}")
        }
    }
    if(showUsernameAndEmailDialog.value) {
        val curRepo = repoOfSetUsernameAndEmailDialog.value
        val closeDialog = { showUsernameAndEmailDialog.value = false }
        AskGitUsernameAndEmailDialogWithSelection(
            curRepo = curRepo,
            username = username,
            email = email,
            closeDialog = closeDialog,
            onErrorCallback = { e->
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                MyLog.e(TAG, "set username and email err (from Repos page): ${e.stackTraceToString()}")
            },
            onFinallyCallback = {},
            onSuccessCallback = {
                val successCallback = afterSetUsernameAndEmailSuccessCallback.value
                afterSetUsernameAndEmailSuccessCallback.value = null
                successCallback?.invoke()
            },
        )
    }
    val setGlobalGitUsernameAndEmailStrRes = stringResource(R.string.set_global_username_and_email)
    val globalUsername = rememberSaveable { mutableStateOf("")}
    val globalEmail = rememberSaveable { mutableStateOf("")}
    if(showSetGlobalGitUsernameAndEmailDialog.value) {
        val curRepo = curRepo.value
        AskGitUsernameAndEmailDialog(
            title = if(showWelcomeToNewUser.value) stringResource(R.string.welcome) else stringResource(R.string.user_info),
            text = if(showWelcomeToNewUser.value) stringResource(R.string.welcome_please_set_git_username_and_email) else setGlobalGitUsernameAndEmailStrRes,
            username=globalUsername,
            email=globalEmail,
            isForGlobal=true,
            repos = listOf(), 
            onOk={
                if(showWelcomeToNewUser.value) {
                    closeWelcome()
                }
                doJobThenOffLoading(
                ){
                    Libgit2Helper.saveGitUsernameAndEmailForGlobal(
                        requireShowErr=Msg.requireShowLongDuration,
                        errText=errWhenQuerySettingsFromDbStrRes,
                        errCode1="15569470",  
                        errCode2="10405847",
                        username=globalUsername.value,
                        email=globalEmail.value
                    )
                    showSetGlobalGitUsernameAndEmailDialog.value=false
                    Msg.requireShow(saved)
                }
            },
            onCancel={
                if(showWelcomeToNewUser.value) {
                    closeWelcome()
                }
                showSetGlobalGitUsernameAndEmailDialog.value=false
                globalUsername.value=""
                globalEmail.value=""
            },
            enableOk={true},
        )
    }
    val showSetCurRepoGitUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val curRepoUsername = rememberSaveable { mutableStateOf("") }
    val curRepoEmail = rememberSaveable { mutableStateOf("") }
    val showSetUserInfoDialog = showSetUserInfoDialog@{repos:List<RepoEntity> ->
        if(repos.isEmpty()) {
            return@showSetUserInfoDialog
        }
        userInfoRepoList.value.clear()
        userInfoRepoList.value.addAll(repos)
        showSetCurRepoGitUsernameAndEmailDialog.value = true
    }
    if(showSetCurRepoGitUsernameAndEmailDialog.value) {
        AskGitUsernameAndEmailDialog(
            title= stringResource(R.string.user_info),
            text=stringResource(R.string.set_username_and_email_for_repo),
            username=curRepoUsername,
            email=curRepoEmail,
            isForGlobal=false,
            repos=userInfoRepoList.value,
            onOk={
                showSetCurRepoGitUsernameAndEmailDialog.value=false
                doJobThenOffLoading {
                    userInfoRepoList.value.toList().forEachBetter { curRepo ->
                        Repository.open(curRepo.fullSavePath).use { repo ->
                            Libgit2Helper.saveGitUsernameAndEmailForRepo(
                                repo = repo,
                                requireShowErr=Msg.requireShowLongDuration,
                                username=curRepoUsername.value,
                                email=curRepoEmail.value
                            )
                        }
                    }
                    Msg.requireShow(saved)
                }
            },
            onCancel={
                showSetCurRepoGitUsernameAndEmailDialog.value=false
                curRepoUsername.value=""
                curRepoEmail.value=""
            },
            enableOk={true},
        )
    }
    val importRepoPath = rememberSaveable { SharedState.fileChooser_DirPath }
    val isReposParentFolderForImport = rememberSaveable { mutableStateOf(false) }
    if(showImportRepoDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.import_repo),
            requireShowTextCompose = true,
            textCompose = {
                MySelectionContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        InternalFileChooser(activityContext, path = importRepoPath)
                        Spacer(Modifier.height(15.dp))
                        MyCheckBox(text = stringResource(R.string.the_path_is_a_repos_parent_dir), value = isReposParentFolderForImport)
                        Spacer(Modifier.height(5.dp))
                        if(isReposParentFolderForImport.value) {
                            DefaultPaddingText(stringResource(R.string.will_scan_repos_under_this_folder))
                        }
                    }
                }
            },
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            okBtnEnabled = importRepoPath.value.isNotBlank(),
            onCancel = { showImportRepoDialog.value = false },
        ) {
            val importRepoPath = importRepoPath.value
            doJobThenOffLoading {
                try {
                    val newPathRet = FsUtils.userInputPathToCanonical(importRepoPath)
                    if(newPathRet.hasError()) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_path))
                        return@doJobThenOffLoading
                    }
                    val newPath = newPathRet.data!!
                    val f = File(newPath)
                    if(!f.canRead()) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.cant_read_path))
                        return@doJobThenOffLoading
                    }
                    if(!f.isDirectory) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.path_is_not_a_dir))
                        return@doJobThenOffLoading
                    }
                    showImportRepoDialog.value = false
                    Msg.requireShowLongDuration(activityContext.getString(R.string.importing))
                    val importRepoResult = AppModel.dbContainer.repoRepository.importRepos(dir=newPath, isReposParent=isReposParentFolderForImport.value)
                    Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.n_imported), listOf(""+importRepoResult.success)))
                }catch (e:Exception) {
                    MyLog.e(TAG, "import repo from ReposPage err: "+e.stackTraceToString())
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                }finally {
                    changeStateTriggerRefreshPage(needRefreshRepoPage)
                }
            }
        }
    }
    val doActAndLogErr:suspend (curRepo:RepoEntity, actName:String, act:suspend ()->Unit)->Unit = {curRepo, actName, act ->
        try {
            act()
        }catch (e:Exception) {
            showErrAndSaveLog(
                logTag = TAG,
                logMsg = "do `$actName` from Repo Page err: "+e.stackTraceToString(),
                showMsg = "$actName err: "+e.localizedMessage,
                showMsgMethod = {},  
                repoId = curRepo.id
            )
        }
    }
    val doFetch:suspend (String?,RepoEntity)->Unit = doFetch@{remoteNameParam:String?,curRepo:RepoEntity ->  
        Repository.open(curRepo.fullSavePath).use { repo ->
            var remoteName = remoteNameParam
            if(remoteName == null || remoteName.isBlank()) {
                val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)
                val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)
                remoteName = upstream.remote
                if(remoteName == null || remoteName.isBlank()) {  
                    throw RuntimeException(activityContext.getString(R.string.err_upstream_invalid_plz_go_branches_page_set_it_then_try_again))
                }
            }
            val credential = Libgit2Helper.getRemoteCredential(
                dbContainer.remoteRepository,
                dbContainer.credentialRepository,
                curRepo.id,
                remoteName,
                trueFetchFalsePush = true
            )
            Libgit2Helper.fetchRemoteForRepo(repo, remoteName, credential, curRepo)
        }
        val repoDb = AppModel.dbContainer.repoRepository
        repoDb.updateLastUpdateTime(curRepo.id, getSecFromTime())
    }
    suspend fun doMerge(upstreamParam: Upstream?, curRepo:RepoEntity):Unit {
        val trueMergeFalseRebase = !SettingsUtil.pullWithRebase()
        Repository.open(curRepo.fullSavePath).use { repo ->
            var upstream = upstreamParam
            if(Libgit2Helper.isUpstreamInvalid(upstream)) {  
                val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)  
                upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)  
                if(Libgit2Helper.isUpstreamInvalid(upstream)) {
                    throw RuntimeException(activityContext.getString(R.string.err_upstream_invalid_plz_go_branches_page_set_it_then_try_again))
                }
            }
            val remoteRefSpec = Libgit2Helper.getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(
                upstream!!.remote,
                upstream.branchRefsHeadsFullRefSpec
            )
            MyLog.d(TAG, "doMerge: remote="+upstream.remote+", branchFullRefSpec=" + upstream.branchRefsHeadsFullRefSpec +", trueMergeFalseRebase=$trueMergeFalseRebase")
            val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
            if(Libgit2Helper.isUsernameAndEmailInvalid(usernameFromConfig,emailFromConfig)) {
                throw RuntimeException(activityContext.getString(R.string.plz_set_username_and_email_first))
            }
            val mergeResult = Libgit2Helper.mergeOrRebase(
                repo,
                targetRefName = remoteRefSpec,
                username = usernameFromConfig,
                email = emailFromConfig,
                requireMergeByRevspec = false,
                revspec = "",
                trueMergeFalseRebase = trueMergeFalseRebase,
                settings = settings
            )
            if (mergeResult.hasError()) {
                if (mergeResult.code == Ret.ErrCode.mergeFailedByAfterMergeHasConfilts) {
                    throw RuntimeException(activityContext.getString(R.string.has_conflicts))
                }
                throw RuntimeException(mergeResult.msg)
            }
            Libgit2Helper.cleanRepoState(repo)
            Libgit2Helper.updateDbAfterMergeSuccess(mergeResult, activityContext, curRepo.id, {}, trueMergeFalseRebase)  
        }
    }
    val doPush:suspend (Upstream?,RepoEntity) -> Unit  = doPush@{upstreamParam:Upstream?,curRepo:RepoEntity ->
        Repository.open(curRepo.fullSavePath).use { repo ->
            if(repo.headDetached()) {
                throw RuntimeException(activityContext.getString(R.string.push_failed_by_detached_head))
            }
            var upstream:Upstream? = upstreamParam
            if(Libgit2Helper.isUpstreamInvalid(upstream)) {  
                val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)  
                upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)  
                if(Libgit2Helper.isUpstreamInvalid(upstream)) {
                    throw RuntimeException(activityContext.getString(R.string.err_upstream_invalid_plz_go_branches_page_set_it_then_try_again))
                }
            }
            MyLog.d(TAG, "#doPush: upstream.remote="+upstream!!.remote+", upstream.branchFullRefSpec="+upstream!!.branchRefsHeadsFullRefSpec)
            val credential = Libgit2Helper.getRemoteCredential(
                dbContainer.remoteRepository,
                dbContainer.credentialRepository,
                curRepo.id,
                upstream!!.remote,
                trueFetchFalsePush = false
            )
            Libgit2Helper.push(repo, upstream!!.remote, listOf(upstream!!.pushRefSpec), credential, force = false)
            val repoDb = AppModel.dbContainer.repoRepository
            repoDb.updateLastUpdateTime(curRepo.id, getSecFromTime())
        }
    }
    val doClone = doClone@{repoList:List<RepoEntity> ->
        if(repoList.isEmpty()) {
            return@doClone
        }
        doJobThenOffLoading {
            repoList.forEachBetter { curRepo ->
                val repoLock = Libgit2Helper.getRepoLock(curRepo.id)
                if(isLocked(repoLock)) {
                    return@doJobThenOffLoading
                }
                repoLock.withLock {
                    val repoRepository = dbContainer.repoRepository
                    val repoFromDb = repoRepository.getById(curRepo.id)?:return@withLock
                    if(repoFromDb.workStatus == Cons.dbRepoWorkStatusCloneErr) {
                        repoFromDb.workStatus = Cons.dbRepoWorkStatusNotReadyNeedClone
                        repoFromDb.createErrMsg = ""
                        repoRepository.update(repoFromDb)
                    }
                }
            }
            changeStateTriggerRefreshPage(needRefreshRepoPage)
        }
        Unit
    }
    val doCloneSingle = { targetRepo:RepoEntity ->
        doClone(listOf(targetRepo))
    }
    val doSync:suspend (RepoEntity)->Unit = doSync@{curRepo:RepoEntity ->
        Repository.open(curRepo.fullSavePath).use { repo ->
            if(repo.headDetached()) {
                throw RuntimeException(activityContext.getString(R.string.sync_failed_by_detached_head))
            }
            val hasUpstream = Libgit2Helper.isBranchHasUpstream(repo)
            val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)
            if (!hasUpstream) {  
                throw RuntimeException(activityContext.getString(R.string.err_upstream_invalid_plz_go_branches_page_set_it_then_try_again))
            }
            val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)
            doFetch(upstream.remote, curRepo)
            val isUpstreamExistOnLocal = Libgit2Helper.isUpstreamActuallyExistOnLocal(
                repo,
                upstream.remote,
                upstream.branchRefsHeadsFullRefSpec
            )
            MyLog.d(TAG, "@doSync: isUpstreamExistOnLocal="+isUpstreamExistOnLocal)
            if(isUpstreamExistOnLocal) {  
                doMerge(upstream, curRepo)
            }
            doPush(upstream, curRepo)
        }
    }
    val doPull:suspend (RepoEntity)->Unit = {curRepo ->
        doFetch(null, curRepo)
        doMerge(null, curRepo)
    }
    val getCurActiveList = {
        if(enableFilterState.value) filterList.value else repoList.value
    }
    val getCurActiveListState = {
        if(enableFilterState.value) filterListState else repoPageListState
    }
    val doActAndSetRepoStatus:suspend (Int, String, String, suspend ()->Unit) -> Unit = {_idx:Int, repoId:String, status:String, act: suspend ()->Unit ->
        val repoList = repoList.value
        val idx = repoList.indexOfFirst { it.id == repoId }
        doActIfIndexGood(idx,repoList) {
            repoList[idx] = it.copyAllFields(settings, it.copy(tmpStatus = status))
        }
        RepoStatusUtil.setRepoStatus(repoId, status)
        act()
        RepoStatusUtil.clearRepoStatus(repoId)
        doActIfIndexGood(idx,repoList) {
            doJobThenOffLoading {
                val repoDb = dbContainer.repoRepository
                val reQueriedRepoInfo = repoDb.getById(it.id)?:return@doJobThenOffLoading
                if(reQueriedRepoInfo.pendingTask == RepoPendingTask.NEED_CHECK_UNCOMMITED_CHANGES) {
                    val curRefreshValue = needRefreshRepoPage.value
                    checkGitStatusAndUpdateItemInList(
                        settings = settings,
                        item = reQueriedRepoInfo,
                        idx = idx,
                        repoList = repoList,
                        loadingText = activityContext.getString(R.string.loading),
                        pageChanged = {
                            needRefreshRepoPage.value != curRefreshValue
                        }
                    )
                }else { 
                    repoList[idx] = reQueriedRepoInfo
                }
                val curRepoInMenu = curRepo.value  
                if(curRepoInMenu.id == reQueriedRepoInfo.id) {  
                    curRepoInMenu.tmpStatus = reQueriedRepoInfo.tmpStatus  
                    curRepoInMenu.isDetached = reQueriedRepoInfo.isDetached
                    curRepoInMenu.isShallow = reQueriedRepoInfo.isShallow
                }
            }
        }
    }
    val showDelRepoDialog = rememberSaveable { mutableStateOf(false)}
    val willDeleteRepoNames = rememberSaveable { mutableStateOf("") }
    val requireDelFilesOnDisk = rememberSaveable { mutableStateOf(false)}
    val requireDelRepo = {expectDelRepos:List<RepoEntity> ->
        val suffix = ", "
        val sb = StringBuilder()
        expectDelRepos.forEachBetter { sb.append(it.repoName).append(suffix) }
        willDeleteRepoNames.value = sb.removeSuffix(suffix).toString()
        deleteList.value.clear()
        deleteList.value.addAll(expectDelRepos)
        requireDelFilesOnDisk.value = false
        showDelRepoDialog.value = true
    }
    if(showDelRepoDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    SelectionRow {
                        Text(text = stringResource(id = R.string.delete_repos)+":")
                    }
                    Spacer(Modifier.height(10.dp))
                    MySelectionContainer {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = willDeleteRepoNames.value,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(5.dp))
                    MyCheckBox(stringResource(R.string.del_files_on_disk), requireDelFilesOnDisk)
                    if(requireDelFilesOnDisk.value) {
                        MySelectionContainer {
                            DefaultPaddingRow {
                                Text(
                                    text = stringResource(R.string.will_delete_repo_and_all_its_files_on_disk),
                                    color = MyStyleKt.TextColor.danger()
                                )
                            }
                        }
                    }
                }
            },
            okBtnText = stringResource(R.string.delete),
            okTextColor = if(requireDelFilesOnDisk.value) MyStyleKt.TextColor.danger() else Color.Unspecified,
            onCancel = { showDelRepoDialog.value = false }
        ) {
            showDelRepoDialog.value = false
            val requireDelFilesOnDisk = requireDelFilesOnDisk.value
            val requireTransaction = true
            doJobThenOffLoading {
                var curRepo:RepoEntity? = null
                try {
                    val settings = SettingsUtil.getSettingsSnapshot()
                    var updatedPackageNameAndRepoIdMap = settings.automation.packageNameAndRepoIdsMap
                    val tmpPackageNameAndRepoIdMap = mutableMapOf<String, List<String>>()
                    var updatedPackageNameAndRepoSettingsMap = settings.automation.packageNameAndRepoAndSettingsMap
                    val tmpPackageNameAndRepoSettingsMap = mutableMapOf<String, PackageNameAndRepoSettings>()
                    deleteList.value.toList().forEachBetter { willDeleteRepo ->
                        curRepo = willDeleteRepo
                        val repoDb = AppModel.dbContainer.repoRepository
                        repoDb.delete(
                            item = willDeleteRepo,
                            requireDelFilesOnDisk = requireDelFilesOnDisk,
                            requireTransaction = requireTransaction
                        )
                        for(i in updatedPackageNameAndRepoIdMap) {
                            tmpPackageNameAndRepoIdMap.put(i.key, i.value.filter { it != willDeleteRepo.id })
                        }
                        updatedPackageNameAndRepoIdMap = tmpPackageNameAndRepoIdMap.toMutableMap()
                        tmpPackageNameAndRepoIdMap.clear()
                        for(i in updatedPackageNameAndRepoSettingsMap) {
                            val keySuffix = PackageNameAndRepo(repoId = willDeleteRepo.id).toKeySuffix()
                            if(!i.key.endsWith(keySuffix)) {
                                tmpPackageNameAndRepoSettingsMap.put(i.key, i.value)
                            }
                        }
                        updatedPackageNameAndRepoSettingsMap = tmpPackageNameAndRepoSettingsMap.toMutableMap()
                        tmpPackageNameAndRepoSettingsMap.clear()
                    }
                    settings.automation.packageNameAndRepoIdsMap = updatedPackageNameAndRepoIdMap
                    settings.automation.packageNameAndRepoAndSettingsMap = updatedPackageNameAndRepoSettingsMap
                    SettingsUtil.updateSettings(settings)
                    Msg.requireShow(activityContext.getString(R.string.success))
                } catch (e: Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?: "err")
                    MyLog.e(TAG, "del repo '${curRepo?.repoName}' in $TAG err: ${e.stackTraceToString()}")
                } finally {
                    changeStateTriggerRefreshPage(needRefreshRepoPage)
                }
            }
        }
    }
    val showRenameDialog = rememberSaveable { mutableStateOf(false)}
    val repoNameForRenameDialog = mutableCustomStateOf(stateKeyTag, "repoNameForRenameDialog") { TextFieldValue("") }
    val errMsgForRenameDialog = rememberSaveable { mutableStateOf("")}
    if(showRenameDialog.value) {
        val focusRequester = remember { FocusRequester() }
        val curRepo = curRepo.value
        ConfirmDialog(
            title = stringResource(R.string.rename_repo),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                        ,
                        value = repoNameForRenameDialog.value,
                        singleLine = true,
                        isError = errMsgForRenameDialog.value.isNotBlank(),
                        supportingText = {
                            if (errMsgForRenameDialog.value.isNotBlank()) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = errMsgForRenameDialog.value,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (errMsgForRenameDialog.value.isNotBlank()) {
                                Icon(imageVector=Icons.Filled.Error,
                                    contentDescription=null,
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        onValueChange = {
                            repoNameForRenameDialog.value = it
                            errMsgForRenameDialog.value = ""
                        },
                        label = {
                            Text(stringResource(R.string.new_name))
                        }
                    )
                }
            },
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            okBtnEnabled = repoNameForRenameDialog.value.text.isNotBlank() && errMsgForRenameDialog.value.isEmpty() && repoNameForRenameDialog.value.text != curRepo.repoName,
            onCancel = {showRenameDialog.value = false}
        ) {
            val newName = repoNameForRenameDialog.value.text
            val repoId = curRepo.id
            doJobThenOffLoading {
                try {
                    val repoDb = AppModel.dbContainer.repoRepository
                    if(strHasIllegalChars(newName)) {
                        errMsgForRenameDialog.value = activityContext.getString(R.string.name_has_illegal_chars)
                        return@doJobThenOffLoading
                    }
                    if(repoDb.isRepoNameExist(newName)) {
                        errMsgForRenameDialog.value = activityContext.getString(R.string.name_already_exists)
                        return@doJobThenOffLoading
                    }
                    showRenameDialog.value = false
                    repoDb.updateRepoName(repoId, newName)
                    Msg.requireShow(activityContext.getString(R.string.success))
                    changeStateTriggerRefreshPage(needRefreshRepoPage)
                }catch (e:Exception) {
                    val errmsg = e.localizedMessage ?: "rename repo failed"
                    Msg.requireShowLongDuration("err: "+errmsg)
                    createAndInsertError(curRepo.id, "err: rename repo '${curRepo.repoName}' to ${repoNameForRenameDialog.value} failed, err message is '$errmsg'")
                    changeStateTriggerRefreshPage(needRefreshRepoPage)
                }
            }
        }
        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }
    val showUnshallowDialog = rememberSaveable { mutableStateOf(false) }
    val unshallowRepoNames = rememberSaveable { mutableStateOf("") }
    if(showUnshallowDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.unshallow),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Row {
                            Text(text = stringResource(R.string.will_do_unshallow_for_repos) + ":")
                        }
                    }
                    MySelectionContainer {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = unshallowRepoNames.value,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    MySelectionContainer {
                        Text(
                            text = stringResource(R.string.unshallow_success_cant_back),
                            color = MyStyleKt.TextColor.danger()
                        )
                    }
                }
            },
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showUnshallowDialog.value = false }
        ) {
            showUnshallowDialog.value = false
            val unshallowingText = activityContext.getString(R.string.unshallowing)
            unshallowList.value.toList().forEachBetter { curRepo ->
                doJobThenOffLoading {
                    val curRepoId = curRepo.id
                    val curRepoIdx = -1  
                    val curRepoFullPath = curRepo.fullSavePath
                    val curRepoVal =  curRepo
                    val lock = Libgit2Helper.getRepoLock(curRepoId)
                    if(isLocked(lock)) {
                        return@doJobThenOffLoading
                    }
                    lock.withLock {
                        doActAndSetRepoStatus(curRepoIdx, curRepoId, unshallowingText) {
                            Repository.open(curRepoFullPath).use { repo->
                                val ret = Libgit2Helper.unshallowRepo(
                                    repo,
                                    curRepoVal,
                                    AppModel.dbContainer.repoRepository,
                                    AppModel.dbContainer.remoteRepository,
                                    AppModel.dbContainer.credentialRepository
                                )
                                if(ret.hasError()) {
                                    Msg.requireShow(ret.msg)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    val statusClickedRepo =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "statusClickedRepo", RepoEntity(id=""))
    val showRequireActionsDialog = rememberSaveable { mutableStateOf(false)}
    if(showRequireActionsDialog.value) {
        val targetRepo = statusClickedRepo.value
        if(targetRepo.id.isBlank()) {
            showRequireActionsDialog.value = false
            Msg.requireShow(stringResource(R.string.repo_id_invalid))
        }else {  
            ConfirmDialog(
                title = stringResource(R.string.require_actions),
                text = stringResource(R.string.will_go_to_changelist_then_you_can_continue_or_abort_your_merge_rebase_cherrpick),
                okBtnText = stringResource(id = R.string.ok),
                cancelBtnText = stringResource(id = R.string.cancel),
                onCancel = { showRequireActionsDialog.value = false }
            ) {
                showRequireActionsDialog.value = false
                goToChangeListPage(targetRepo)
            }
        }
    }
    val goToThisRepoAndHighlightingIt = goTo@{ targetId:String ->
        if(targetId.isBlank()) {
            Msg.requireShow(activityContext.getString(R.string.not_found))
            return@goTo
        }
        try {
            val list = getCurActiveList()
            val listState = getCurActiveListState()
            val targetIndex = list.toList().indexOfFirst { it.id == targetId }
            if(targetIndex != -1) {  
                UIHelper.scrollToItem(scope, listState, targetIndex / repoCountEachRow)
                requireBlinkIdx.intValue = targetIndex
            }else{
                if(repoPageFilterModeOn.value) {
                    val indexInOriginList = repoList.value.toList().indexOfFirst { it.id == targetId }
                    if(indexInOriginList != -1){  
                        repoPageFilterModeOn.value = false  
                        showBottomSheet.value = false  
                        UIHelper.scrollToItem(scope, repoPageListState, indexInOriginList / repoCountEachRow)
                        requireBlinkIdx.intValue = indexInOriginList  
                    }else {
                        Msg.requireShow(activityContext.getString(R.string.not_found))
                    }
                }else {
                    Msg.requireShow(activityContext.getString(R.string.not_found))
                }
            }
        }catch (_:Exception) {
        }
    }
    val refreshSpecifedRepos = { repos:List<RepoEntity> ->
        specifiedRefreshRepoList.value.clear()
        specifiedRefreshRepoList.value.addAll(repos)
        changeStateTriggerRefreshPage(needRefreshRepoPage)
    }
    val showSetUpstreamForLocalBranchDialog = rememberSaveable { mutableStateOf(false)}
    val upstreamSelectedRemote = rememberSaveable{mutableIntStateOf(0)}  
    val upstreamBranchSameWithLocal =rememberSaveable { mutableStateOf(true)}
    val upstreamBranchShortRefSpec = rememberSaveable { mutableStateOf("")}
    val upstreamDialogOnOkText  =rememberSaveable { mutableStateOf("")}
    val curBranchShortNameForSetUpstreamDialog  =rememberSaveable { mutableStateOf("")}
    val curBranchFullNameForSetUpstreamDialog  =rememberSaveable { mutableStateOf("")}
    val doActAfterSetUpstreamSuccess = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "doActAfterSetUpstreamSuccess") { null }
    val setUpstreamOnFinally = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "setUpstreamOnFinally") { null }
    val showClearForSetUpstreamDialog = rememberSaveable { mutableStateOf(false) }
    val initSetUpstreamDialog: suspend (RepoEntity, String, (()->Unit)?) -> Unit = {targetRepo, onOkText, actAfterSuccess ->
        try {
            curRepo.value = targetRepo
            var remoteIdx = 0   
            var shortBranch = targetRepo.branch  
            var sameWithLocal = true  
            Repository.open(targetRepo.fullSavePath).use { repo->
                val headRef = Libgit2Helper.resolveHEAD(repo) ?: throw RuntimeException("resolve HEAD failed")
                curBranchShortNameForSetUpstreamDialog.value = headRef.shorthand()
                curBranchFullNameForSetUpstreamDialog.value = headRef.name()
                val remoteList = Libgit2Helper.getRemoteList(repo)
                upstreamRemoteOptionsList.value.clear()
                upstreamRemoteOptionsList.value.addAll(remoteList)
                val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranch)
                showClearForSetUpstreamDialog.value = upstream.remote.isNotBlank() || upstream.branchRefsHeadsFullRefSpec.isNotBlank()
                MyLog.d(TAG,"set upstream menu item #onClick(): upstream is not null, old remote in config is: ${upstream.remote}, old branch in config is:${upstream.branchRefsHeadsFullRefSpec}")
                val oldRemote = upstream.remote
                if(oldRemote.isNotBlank()) {
                    for((idx, value) in upstreamRemoteOptionsList.value.toList().withIndex()) {
                        if(value == oldRemote) {
                            MyLog.d(TAG,"set upstream menu item #onClick(): found old remote: ${value}, idx in remote list is: $idx")
                            remoteIdx = idx
                            break
                        }
                    }
                }
                val oldUpstreamShortBranchNameNoPrefix = upstream.remoteBranchShortRefSpecNoPrefix
                if(oldUpstreamShortBranchNameNoPrefix.isNotBlank()) {
                    MyLog.d(TAG,"set upstream menu item #onClick(): found old branch full refspec: ${upstream.branchRefsHeadsFullRefSpec}, short refspec: $oldUpstreamShortBranchNameNoPrefix")
                    shortBranch = oldUpstreamShortBranchNameNoPrefix
                    sameWithLocal = false  
                }
            }
            upstreamSelectedRemote.intValue = remoteIdx
            upstreamBranchShortRefSpec.value = shortBranch
            upstreamBranchSameWithLocal.value = sameWithLocal
            MyLog.d(TAG, "set upstream menu item #onClick(): after read old settings, finally, default select remote idx is:${upstreamSelectedRemote.intValue}, branch name is:${upstreamBranchShortRefSpec.value}, check 'same with local branch` is:${upstreamBranchSameWithLocal.value}")
            upstreamDialogOnOkText.value = onOkText
            doActAfterSetUpstreamSuccess.value = actAfterSuccess
            setUpstreamOnFinally.value = if(actAfterSuccess != null) null else { { refreshSpecifedRepos(listOf(targetRepo)) } }
            showSetUpstreamForLocalBranchDialog.value = true
        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            createAndInsertError(targetRepo.id, "init set upstream dialog err: ${e.localizedMessage}")
            MyLog.e(TAG, "init set upstream dialog err: targetRepo='${targetRepo.repoName}', err=${e.stackTraceToString()}")
        }
    }
    if(showSetUpstreamForLocalBranchDialog.value) {
        val curRepo = curRepo.value
        SetUpstreamDialog(
            callerTag = TAG,
            curRepo = curRepo,
            loadingOn = {},
            loadingOff = {},
            onOkText = upstreamDialogOnOkText.value,
            remoteList = upstreamRemoteOptionsList.value,
            isCurrentBranchOfRepo = true,
            curBranchShortName = curBranchShortNameForSetUpstreamDialog.value, 
            curBranchFullName = curBranchFullNameForSetUpstreamDialog.value,
            selectedOption = upstreamSelectedRemote,
            upstreamBranchShortName = upstreamBranchShortRefSpec,
            upstreamBranchShortNameSameWithLocal = upstreamBranchSameWithLocal,
            showClear = showClearForSetUpstreamDialog.value,
            closeDialog = {
                showSetUpstreamForLocalBranchDialog.value = false
            },
            onClearSuccessCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            },
            onClearErrorCallback = { e ->
                val repoId = curRepo.id
                val repoName = curRepo.repoName
                val curBranchShortName = curBranchShortNameForSetUpstreamDialog.value
                Msg.requireShowLongDuration("clear upstream err: " + e.localizedMessage)
                createAndInsertError(
                    repoId,
                    "clear upstream for '$curBranchShortName' err: " + e.localizedMessage
                )
                MyLog.e(
                    TAG,
                    "clear upstream for '$curBranchShortName' of '$repoName' err: " + e.stackTraceToString()
                )
            },
            onSuccessCallback = {
                Msg.requireShow(activityContext.getString(R.string.set_upstream_success))
                val cb = doActAfterSetUpstreamSuccess.value
                doActAfterSetUpstreamSuccess.value = null
                cb?.invoke()
            },
            onErrorCallback = onErr@{ e ->
                val repoId = curRepo.id
                val repoName = curRepo.repoName
                val upstreamSameWithLocal = upstreamBranchSameWithLocal.value
                val remoteList = upstreamRemoteOptionsList.value
                val selectedRemoteIndex = upstreamSelectedRemote.intValue
                val upstreamShortName = upstreamBranchShortRefSpec.value
                val curBranchShortName = curBranchShortNameForSetUpstreamDialog.value
                val remote = try {
                    remoteList[selectedRemoteIndex]
                } catch (e: Exception) {
                    MyLog.e(TAG,"err when get remote by index from remote list of '$repoName': remoteIndex=$selectedRemoteIndex, remoteList=$remoteList\nerr info: ${e.stackTraceToString()}")
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_selected_remote_is_invalid))
                    return@onErr
                }
                Msg.requireShowLongDuration("set upstream err: " + e.localizedMessage)
                createAndInsertError(
                    repoId,
                    "set upstream for '$curBranchShortName' err: " + e.localizedMessage
                )
                MyLog.e(
                    TAG,
                    "set upstream for '$curBranchShortName' of '$repoName' err! user input branch is '$upstreamShortName', selected remote is $remote, user checked use same name with local is '$upstreamSameWithLocal'\nerr: " + e.stackTraceToString()
                )
            },
            onClearFinallyCallback = { refreshSpecifedRepos(listOf(curRepo)) },
            onFinallyCallback = setUpstreamOnFinally.value,
        )
    }
    val apiPullUrl = rememberSaveable { mutableStateOf("") }
    val apiPushUrl = rememberSaveable { mutableStateOf("") }
    val apiSyncUrl = rememberSaveable { mutableStateOf("") }
    val showApiDialog2 = rememberSaveable { mutableStateOf(false) }
    if(showApiDialog2.value) {
        ConfirmDialog2(
            title = stringResource(R.string.api),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    val pullurl = apiPullUrl.value
                    val pushurl = apiPushUrl.value
                    val syncurl = apiSyncUrl.value
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                                .align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.pull)+": ", fontWeight = FontWeight.Bold)
                            MySelectionContainer {
                                Text(text = pullurl)
                            }
                        }
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth(.2f)
                                .align(Alignment.CenterEnd),
                            onClick = {
                                clipboardManager.setText(AnnotatedString(pullurl))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }
                    MyHorizontalDivider(Modifier.padding(top = 10.dp, bottom = 10.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                                .align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        )  {
                            Text(stringResource(R.string.push)+": ", fontWeight = FontWeight.Bold)
                            MySelectionContainer {
                                Text(text = pushurl)
                            }
                        }
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth(.2f)
                                .align(Alignment.CenterEnd),
                            onClick = {
                                clipboardManager.setText(AnnotatedString(pushurl))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }
                    MyHorizontalDivider(Modifier.padding(top = 10.dp, bottom = 10.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                                .align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.sync)+": ", fontWeight = FontWeight.Bold)
                            MySelectionContainer {
                                Text(text = syncurl)
                            }
                        }
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth(.2f)
                                .align(Alignment.CenterEnd),
                            onClick = {
                                clipboardManager.setText(AnnotatedString(syncurl))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }
                }
            },
            onCancel = { showApiDialog2.value = false },
            cancelBtnText = stringResource(R.string.close),
            showOk = false
        ) {}
    }
    val showDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val detailsTitle = rememberSaveable { mutableStateOf("") }
    val detailsString = rememberSaveable { mutableStateOf("") }
    val initDetailsDialog = { title:String, text:String ->
        detailsTitle.value = title
        detailsString.value = text
        showDetailsDialog.value = true
    }
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = detailsTitle.value,
            text = detailsString.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsString.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    val quitSelectionMode = {
        isSelectionMode.value = false
        selectedItems.value.clear()
    }
    val getSelectedFilesCount = {
        selectedItems.value.size
    }
    val selectedSingle = {
        getSelectedFilesCount() == 1
    }
    val hasSelectedItems = {
        getSelectedFilesCount() > 0
    }
    val containsForSelected = { srcList:List<RepoEntity>, item:RepoEntity ->
        srcList.indexOfFirst { it.equalsForSelected(item) } != -1
    }
    val switchItemSelected = { item: RepoEntity ->
        isSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseRemove(item, selectedItems.value, contains = containsForSelected, remove = { srcList, curItem ->
            val foundIdx = srcList.indexOfFirst { it.id == curItem.id }
            if(foundIdx != -1) {
                srcList.removeAt(foundIdx)
                true
            }else {
                false
            }
        })
    }
    val selectItem = { item:RepoEntity ->
        isSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseNoop(item, selectedItems.value, contains = containsForSelected)
    }
    val repoCardTitleOnClick = { item:RepoEntity ->
        switchItemSelected(item)
    }
    val isRepoGoodAndActEnabled = {curRepo:RepoEntity ->
        val repoStatusGood = curRepo.gitRepoState!=null && !Libgit2Helper.isRepoStatusNotReadyOrErr(curRepo)
        val isDetached = dbIntToBool(curRepo.isDetached)
        val hasTmpStatus = curRepo.pendingTask != RepoPendingTask.NEED_CHECK_UNCOMMITED_CHANGES && curRepo.tmpStatus.isNotBlank()
        val actionEnabled = !isDetached && !hasTmpStatus
        repoStatusGood && actionEnabled
    }
    val doActWithLockIfRepoGoodAndActEnabled = { curRepo:RepoEntity, act: suspend ()->Unit ->
        doJobThenOffLoading {
            Libgit2Helper.doActWithRepoLockIfPredicatePassed(curRepo, isRepoGoodAndActEnabled, act)
        }
    }
    val isRepoGood = {curRepo:RepoEntity ->
        curRepo.gitRepoState!=null && !Libgit2Helper.isRepoStatusNotReadyOrErr(curRepo)
    }
    val doActIfRepoGoodOrElse = { curRepo:RepoEntity, act:()->Unit, elseAct:()->Unit ->
        if(isRepoGood(curRepo)) {
            act()
        }else {
            elseAct()
        }
    }
    val doActIfRepoGood = { curRepo:RepoEntity, act:()->Unit ->
        doActIfRepoGoodOrElse(curRepo, act, {})
    }
    val showNoCommitDialog = rememberSaveable { mutableStateOf(false) }
    val repoNameOfNoCommitDialog = rememberSaveable { mutableStateOf("") }
    val initNoCommitDialog = {curRepo:RepoEntity ->
        repoNameOfNoCommitDialog.value = curRepo.repoName
        showNoCommitDialog.value = true
    }
    if(showNoCommitDialog.value) {
        CopyableDialog2(
            title = repoNameOfNoCommitDialog.value,
            text = stringResource(R.string.repo_no_commit_note),
            onCancel = { showNoCommitDialog.value = false },
            cancelBtnText = stringResource(R.string.ok),
            okCompose = {}
        ) { }  
    }
    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false)}
    if(showSelectedItemsShortDetailsDialog.value) {
        SelectedItemDialog(
            selectedItems = selectedItems.value,
            formatter = {it.repoName},
            switchItemSelected = switchItemSelected,
            clearAll = {selectedItems.value.clear()},
            closeDialog = {showSelectedItemsShortDetailsDialog.value = false}
        )
    }
    val showSelectedItems = {
        showSelectedItemsShortDetailsDialog.value = true
    }
    val repoIdForErrMsgDialog = rememberSaveable { mutableStateOf("") }
    val errMsgDialogText = rememberSaveable { mutableStateOf("") }
    val showErrMsgDialog = rememberSaveable { mutableStateOf(false) }
    if(showErrMsgDialog.value) {
        val closeDialog = { showErrMsgDialog.value = false }
        CopyableDialog2(
            title = stringResource(R.string.error_msg),
            text = errMsgDialogText.value,
            onCancel = closeDialog,
            cancelCompose = {
                Row {
                    TextButton(
                        onClick = {
                            closeDialog()
                            goToErrScreen(repoIdForErrMsgDialog.value)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.all),
                        )
                    }
                    TextButton(
                        onClick = closeDialog
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                        )
                    }
                }
            }
        ) { 
            showErrMsgDialog.value=false
            clipboardManager.setText(AnnotatedString(errMsgDialogText.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    val showItemMsgDialog = rememberSaveable { mutableStateOf(false) }
    val textOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val basePathNoEndSlashOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val previewModeOnOfItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgPreviewModeOn) }
    val useSystemFontsForItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgUseSystemFonts) }
    val showItemMsg = { repoDto: RepoEntity ->
        textOfItemMsgDialog.value = repoDto.latestCommitMsg
        basePathNoEndSlashOfItemMsgDialog.value = repoDto.fullSavePath
        showItemMsgDialog.value = true
    }
    if(showItemMsgDialog.value) {
        CommitMsgMarkDownDialog(
            dialogVisibleState = showItemMsgDialog,
            text = textOfItemMsgDialog.value,
            previewModeOn = previewModeOnOfItemMsgDialog,
            useSystemFonts = useSystemFontsForItemMsgDialog,
            basePathNoEndSlash = basePathNoEndSlashOfItemMsgDialog.value
        )
    }
    val initErrMsgDialog = { repoEntity: RepoEntity, errMsg:String ->
        val repoId = repoEntity.id
        repoIdForErrMsgDialog.value = repoId
        errMsgDialogText.value = errMsg
        showErrMsgDialog.value = true
        doJobThenOffLoading {
            AppModel.dbContainer.repoRepository.updateErrFieldsById(repoId, Cons.dbCommonFalse, "")
        }
        Unit
    }
    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp = exitApp)
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {
        if(isSelectionMode.value){
            quitSelectionMode()
        } else if(repoPageFilterModeOn.value) {
            repoPageFilterModeOn.value = false
            resetSearchVars()
        }else {
            backHandlerOnBack()
        }
    })
    PullToRefreshBox(
        contentPadding = contentPadding,
        onRefresh = { changeStateTriggerRefreshPage(needRefreshRepoPage) }
    ) {
        if (repoList.value.isEmpty()) {  
            if(isInitLoading.value) {
                FullScreenScrollableColumn(contentPadding) {
                    Text(initLoadingText.value)
                }
            }else {  
                val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
                PageCenterIconButton(
                    contentPadding = contentPadding,
                    onClick = {
                        dropDownMenuExpandState.value = !dropDownMenuExpandState.value
                    },
                    icon = Icons.Filled.Add,
                    text = stringResource(R.string.add_a_repo),
                    attachContent = {
                        AddRepoDropDownMenu(
                            showMenu = dropDownMenuExpandState.value,
                            closeMenu = { dropDownMenuExpandState.value = false },
                            importOnClick = {

                                showImportRepoDialog.value = true
                            }
                        )
                    }
                )
            }
        }else {  
            val keyword = repoPageFilterKeyWord.value.text  
            val enableFilter = filterModeActuallyEnabled(repoPageFilterModeOn.value, keyword)
            val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
            val filteredListTmp = filterTheList(
                needRefresh = filterResultNeedRefresh.value,
                lastNeedRefresh = lastNeedRefresh,
                enableFilter = enableFilter,
                keyword = keyword,
                lastKeyword = lastSearchKeyword,
                searching = searching,
                token = searchToken,
                activityContext = activityContext,
                filterList = filterList.value,
                list = repoList.value,
                resetSearchVars = resetSearchVars,
                match = { idx:Int, it: RepoEntity ->
                    it.repoName.contains(keyword, ignoreCase = true)
                            || it.branch.contains(keyword, ignoreCase = true)
                            || it.lastCommitHash.contains(keyword, ignoreCase = true)
                            || it.upstreamBranch.contains(keyword, ignoreCase = true)
                            || it.parentRepoName.contains(keyword, ignoreCase = true)
                            || it.fullSavePath.contains(keyword, ignoreCase = true)
                            || it.cachedAppRelatedPath().contains(keyword, ignoreCase = true)
                            || it.cachedLastUpdateTime().contains(keyword, ignoreCase = true)
                            || it.latestUncheckedErrMsg.contains(keyword, ignoreCase = true)
                            || it.tmpStatus.contains(keyword, ignoreCase = true)
                            || it.getOrUpdateCachedOneLineLatestCommitMsg().contains(keyword, ignoreCase = true)
                            || it.createErrMsgForView(activityContext).contains(keyword, ignoreCase = true)
                            || it.getOther().contains(keyword, ignoreCase = true)
                            || it.getRepoStateStr(activityContext).contains(keyword, ignoreCase = true)
                }
            )
            val requireFillMaxWidth = repoCountEachRow == 1
            val paddingItemCount = filteredListTmp.size % repoCountEachRow
            val needPaddingItems = paddingItemCount != 0
            val filteredList =  filteredListTmp.chunked(repoCountEachRow)
            val lastChunkListIndex = filteredList.lastIndex
            val listState = if(enableFilter) filterListState else repoPageListState
            enableFilterState.value = enableFilter
            MyLazyColumn(
                contentPadding = contentPadding,
                list = filteredList,
                listState = listState,
                requireForEachWithIndex = true,
                requirePaddingAtBottom = true
            ) {chunkedListIdx, chunkedList ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    chunkedList.forEachIndexedBetter { subListIdx, element ->
                        val idx = chunkedListIdx * repoCountEachRow + subListIdx
                        RepoCard(
                            itemWidth = itemWidth,
                            requireFillMaxWidth = requireFillMaxWidth,
                            showBottomSheet = showBottomSheet,
                            curRepo = curRepo,
                            curRepoIndex = curRepoIndex,
                            repoDto = element,
                            repoDtoIndex = idx,
                            isSelectionMode = isSelectionMode.value,
                            itemSelected = containsForSelected(selectedItems.value, element),
                            titleOnClick = repoCardTitleOnClick,
                            goToFilesPage = goToFilesPage,
                            requireBlinkIdx = requireBlinkIdx,
                            pageRequest = pageRequest,
                            onClick = {
                                if (isSelectionMode.value) {  
                                    switchItemSelected(it)
                                }
                            },
                            onLongClick = {
                                if (!isSelectionMode.value) {
                                    switchItemSelected(it)
                                }else if(isSelectionMode.value) {
                                    UIHelper.doSelectSpan(idx, it,
                                        selectedItems.value.toList(), filteredListTmp.toList(),
                                        switchItemSelected,
                                        selectItem
                                    )
                                }
                            },
                            requireDelRepo = {curRepo -> requireDelRepo(listOf(curRepo))},
                            copyErrMsg = {msg->
                                clipboardManager.setText(AnnotatedString(msg))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                            },
                            doCloneSingle = doCloneSingle,
                            initErrMsgDialog = initErrMsgDialog,
                            initCommitMsgDialog = showItemMsg,
                        ) workStatusOnclick@{ clickedRepo, status ->  
                            statusClickedRepo.value = clickedRepo  
                            if(status == Cons.dbRepoWorkStatusMerging
                                || status == Cons.dbRepoWorkStatusRebasing
                                || status == Cons.dbRepoWorkStatusCherrypicking
                            ){ 
                                showRequireActionsDialog.value = true
                            } else if (
                                status == Cons.dbRepoWorkStatusHasConflicts
                                || status == Cons.dbRepoWorkStatusNeedCommit
                            ) {
                                goToChangeListPage(clickedRepo)
                            } else if (status == Cons.dbRepoWorkStatusNeedSync) {
                                val curRepo = clickedRepo
                                if(dbIntToBool(curRepo.isDetached)){  
                                    Msg.requireShow(activityContext.getString(R.string.sync_failed_by_detached_head))
                                }else {  
                                    if(curRepo.upstreamBranch.isBlank()) {  
                                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                            doJobThenOffLoading {
                                                initSetUpstreamDialog(curRepo, activityContext.getString(R.string.save_and_sync)) {
                                                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                                                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.syncing)) {
                                                            doActAndLogErr(curRepo, "sync") {
                                                                doSync(curRepo)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }else {  
                                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                            doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                                                doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.syncing)) {
                                                    doActAndLogErr(curRepo, "sync") {
                                                        doSync(curRepo)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }else if (status == Cons.dbRepoWorkStatusNeedPull) {
                                val curRepo = clickedRepo
                                doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.pulling)) {
                                            doActAndLogErr(curRepo, "pull") {
                                                doPull(curRepo)
                                            }
                                        }
                                    }
                                }
                            }else if (status == Cons.dbRepoWorkStatusNeedPush) {
                                val curRepo = clickedRepo
                                doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                                    doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.pushing)) {
                                        doActAndLogErr(curRepo, "push") {
                                            doPush(null, curRepo)
                                        }
                                    }
                                }
                            }else if (status == Cons.dbRepoWorkStatusNoHEAD) {
                                val curRepo = clickedRepo
                                initNoCommitDialog(curRepo)
                            }
                        }
                    }
                    if(needPaddingItems && chunkedListIdx == lastChunkListIndex) {
                        for(i in 0 until (repoCountEachRow - chunkedList.size)) {
                            Column(modifier = Modifier.width(itemWidth.dp)) {}
                        }
                    }
                }
            }
        }
    }
    if(pageRequest.value == PageRequest.goParent) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            goToThisRepoAndHighlightingIt(curRepo.value.parentRepoId)
        }
    }
    if(isSelectionMode.value) {
        val selectionModeIconList = listOf(
            ImageVector.vectorResource(R.drawable.two_way_sync),  
            Icons.Filled.Publish,  
            Icons.Filled.Download,  
            Icons.Filled.Downloading,  
            Icons.Filled.SelectAll,  
        )
        val selectionModeIconTextList = listOf(
            stringResource(R.string.sync),
            stringResource(R.string.push),
            stringResource(R.string.pull),
            stringResource(R.string.fetch),
            stringResource(R.string.select_all),
        )
        val selectionModeIconOnClickList = listOf<()->Unit>(
            sync@{
                val needSetUpstreamBeforeSync = {curRepo:RepoEntity -> curRepo.upstreamBranch.isBlank() && !dbIntToBool(curRepo.isDetached)}
                val expectRepos = {it:RepoEntity -> it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached) }
                val task = { curRepo: RepoEntity ->
                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.syncing)) {
                            doActAndLogErr(curRepo, "sync") {
                                doSync(curRepo)
                            }
                        }
                    }
                }
                val list = selectedItems.value.toList()
                if(list.size == 1) {
                    val curRepo = list.first()
                    if(expectRepos(curRepo)) {
                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                            task(curRepo)
                        }
                    }else if(needSetUpstreamBeforeSync(curRepo)){ 
                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                            doJobThenOffLoading {
                                initSetUpstreamDialog(curRepo, activityContext.getString(R.string.save_and_sync)) {
                                    task(curRepo)
                                }
                            }
                        }
                    }
                }else { 
                    list.filter { expectRepos(it) }.forEachBetter { curRepo ->
                        task(curRepo)
                    }
                }
                Unit
            },
            push@{
                selectedItems.value.toList().filter { it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached) }.forEachBetter { curRepo ->
                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.pushing)) {
                            doActAndLogErr(curRepo, "push") {
                                doPush(null, curRepo)
                            }
                        }
                    }
                }
            },
            pull@{
                val expectRepos = {it:RepoEntity -> it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached)}
                val task = { curRepo:RepoEntity ->
                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.pulling)) {
                            doActAndLogErr(curRepo, "pull") {
                                doPull(curRepo)
                            }
                        }
                    }
                }
                val list = selectedItems.value.toList()
                if(list.size == 1) {
                    val curRepo = list.first()
                    if(expectRepos(curRepo)){
                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                            task(curRepo)
                        }
                    }
                }else {
                    list.filter { expectRepos(it) }.forEachBetter {
                        task(it)
                    }
                }
            },
            fetch@{
                selectedItems.value.toList().filter { it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached) }.forEachBetter { curRepo ->
                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.fetching)) {
                            doActAndLogErr(curRepo, "fetch") {
                                doFetch(null, curRepo)
                            }
                        }
                    }
                }
            },
            selectAll@{
                val list = if(enableFilterState.value) filterList.value else repoList.value
                list.toList().forEachBetter {
                    selectItem(it)
                }
                Unit
            }
        )
        val bottomBarIconDefaultEnable =  { hasSelectedItems() && selectedItems.value.any { it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached) } }
        val selectionModeIconEnableList = listOf(
            syncEnable@{
                val list = selectedItems.value
                if(list.size == 1) {  
                    val item = list.first()
                    !dbIntToBool(item.isDetached) && isRepoReadyAndPathExist(item)
                }else { 
                    bottomBarIconDefaultEnable()
                }
            },
            pushEnable@{ bottomBarIconDefaultEnable() },
            pullEnable@{ bottomBarIconDefaultEnable() },
            fetchEnable@{ bottomBarIconDefaultEnable() },
            selectAllEnable@{ true },
        )
        val selectionModeMoreItemTextList = (listOf(
            stringResource(R.string.refresh), 
            stringResource(R.string.changelist), 
            stringResource(R.string.user_info), 
            stringResource(R.string.rename), 
            stringResource(R.string.set_upstream), 
            stringResource(R.string.clone), 
            stringResource(R.string.unshallow), 
            stringResource(R.string.remotes), 
            stringResource(R.string.tags),  
            stringResource(R.string.stash), 
            stringResource(R.string.reflog), 
            stringResource(R.string.submodules), 
            stringResource(R.string.edit_config), 
            stringResource(R.string.api), 
            stringResource(R.string.details), 
            stringResource(R.string.delete), 
        ))
        val selectionModeMoreItemOnClickList = (listOf(
            refresh@{
                refreshSpecifedRepos(selectedItems.value)
            },
            changelist@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    goToChangeListPage(curRepo)
                }
            },
            userInfo@{
                showSetUserInfoDialog(selectedItems.value.filter { isRepoGood(it) })
            },
            rename@{
                val selectedRepo = selectedItems.value.first()
                curRepo.value = RepoEntity()
                curRepo.value = selectedRepo
                repoNameForRenameDialog.value = TextFieldValue(text = selectedRepo.repoName, selection = TextRange(0, selectedRepo.repoName.length))
                errMsgForRenameDialog.value = ""
                showRenameDialog.value = true
            },
            setUpstream@{
                doJobThenOffLoading {
                    initSetUpstreamDialog(selectedItems.value.first(), activityContext.getString(R.string.save), null)
                }
                Unit
            },
            clone@{
                doClone(selectedItems.value.filter { it.workStatus == Cons.dbRepoWorkStatusCloneErr })
            },
            unshallow@{
                val unshallowableList = selectedItems.value.filter { curRepo ->
                    val repoStatusGood = curRepo.gitRepoState!=null && !Libgit2Helper.isRepoStatusNotReadyOrErr(curRepo)
                    repoStatusGood && dbIntToBool(curRepo.isShallow)
                }
                if(unshallowableList.isNotEmpty()) {
                    unshallowList.value.clear()
                    unshallowList.value.addAll(unshallowableList)
                    val sb = StringBuilder()
                    val suffix = ", "
                    unshallowableList.forEachBetter { sb.append(it.repoName).append(suffix) }
                    unshallowRepoNames.value = sb.removeSuffix(suffix).toString()
                    showUnshallowDialog.value = true
                }
            },
            remotes@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    navController.navigate(Cons.nav_RemoteListScreen+"/"+curRepo.id)
                }
            },
            tags@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    navController.navigate(Cons.nav_TagListScreen + "/" + curRepo.id)
                }
            },
            stash@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    doJobThenOffLoading {
                        goToStashPage(curRepo.id)
                    }
                }
            },
            reflog@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    navController.navigate(Cons.nav_ReflogListScreen+"/"+curRepo.id)
                }
            },
            submodules@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    navController.navigate(Cons.nav_SubmoduleListScreen + "/" + curRepo.id)
                }
            },
            editConfig@{
                try {
                    val curRepo = selectedItems.value.first()
                    Repository.open(curRepo.fullSavePath).use { repo ->
                        val dotGitDirPath = Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(repo)
                        val configFile = File(dotGitDirPath, "config")
                        if(configFile.canRead()) {
                            val expectReadOnly = false
                            requireInnerEditorOpenFile(configFile.canonicalPath, expectReadOnly)
                        }else {
                            Msg.requireShowLongDuration(activityContext.getString(R.string.file_not_found))
                        }
                    }
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#editConfig err: ${e.stackTraceToString()}")
                }
            },
            api@{
                val settings = SettingsUtil.getSettingsSnapshot()
                val host = settings.httpService.listenHost
                val port = settings.httpService.listenPort
                val token = settings.httpService.tokenList.let {
                    if(it.isEmpty()) "" else it.first()
                }
                val sbpull = StringBuilder("${genHttpHostPortStr(host, port.toString())}/pull?token=$token")
                val sbpush = StringBuilder("${genHttpHostPortStr(host, port.toString())}/push?token=$token")
                val sbsync = StringBuilder("${genHttpHostPortStr(host, port.toString())}/sync?token=$token")
                selectedItems.value.forEachBetter {
                    val repoNameOrId = "&repoNameOrId=${it.repoName}"
                    sbpull.append(repoNameOrId)
                    sbpush.append(repoNameOrId)
                    sbsync.append(repoNameOrId)
                }
                apiPullUrl.value = sbpull.toString()
                apiPushUrl.value = sbpush.toString()
                apiSyncUrl.value = sbsync.toString()
                showApiDialog2.value = true
            },
            details@{
                val sb = StringBuilder()
                val lb = "\n"
                val spliter = Cons.itemDetailSpliter
                selectedItems.value.forEachBetter {
                    sb.append(activityContext.getString(R.string.name)).append(": ").append(it.repoName).append(lb).append(lb)
                    sb.append(activityContext.getString(R.string.id)).append(": ").append(it.id).append(lb).append(lb)
                    sb.append(activityContext.getString(R.string.state)).append(": ").append(it.getRepoStateStr(activityContext)).append(lb).append(lb)
                    sb.append(activityContext.getString(R.string.other)).append(": ").append(it.getOther())
                    sb.append(spliter)
                }
                initDetailsDialog(activityContext.getString(R.string.details), sb.removeSuffix(spliter).toString())
            },
            delete@{
                requireDelRepo(selectedItems.value.toList())
            }
        ))
        val selectionModeMoreItemEnableList = (listOf(
            refresh@{ hasSelectedItems() },
            changelist@{
                selectedSingle() && isRepoGood(selectedItems.value.first())
            },
            userInfo@{
                hasSelectedItems() && selectedItems.value.any { isRepoGood(it) }
            },
            rename@{
                selectedSingle()
            },
            setUpstream@{
                if(selectedSingle()) {
                    val item = selectedItems.value.first()
                    !dbIntToBool(item.isDetached) && isRepoReadyAndPathExist(item)
                }else {
                    false
                }
            },
            clone@{
                hasSelectedItems() && selectedItems.value.any { it.workStatus == Cons.dbRepoWorkStatusCloneErr }
            },
            unshallow@{
                hasSelectedItems() && selectedItems.value.any { dbIntToBool(it.isShallow) }
            },
            remotes@{
                selectedSingle() && isRepoGood(selectedItems.value.first())
            },
            tags@{
                selectedSingle() && isRepoGood(selectedItems.value.first())
            },
            stash@{
                selectedSingle()&& isRepoGood(selectedItems.value.first())
            },
            reflog@{
                selectedSingle()&& isRepoGood(selectedItems.value.first())
            },
            submodules@{
                selectedSingle() && isRepoGood(selectedItems.value.first())
            },
            editConfig@{
                selectedSingle()
            },
            api@{
                hasSelectedItems()
            },
            details@{
                hasSelectedItems()
            },
            delete@{
                hasSelectedItems()
            }
        ))
        BottomBar(
            quitSelectionMode=quitSelectionMode,
            iconList=selectionModeIconList,
            iconTextList=selectionModeIconTextList,
            iconDescTextList=selectionModeIconTextList,
            iconOnClickList=selectionModeIconOnClickList,
            iconEnableList=selectionModeIconEnableList,
            moreItemTextList=selectionModeMoreItemTextList,
            moreItemOnClickList=selectionModeMoreItemOnClickList,
            moreItemEnableList = selectionModeMoreItemEnableList,
            moreItemVisibleList = selectionModeMoreItemEnableList,  
            getSelectedFilesCount = getSelectedFilesCount,
            countNumOnClickEnabled = true,
            countNumOnClick = showSelectedItems,
            reverseMoreItemList = true
        )
    }
    LaunchedEffect(needRefreshRepoPage.value) {
        try {
            val loadingText = activityContext.getString(R.string.loading)
            doJobThenOffLoading(initLoadingOn, initLoadingOff, loadingText) {
                try {
                    doInit(
                        dbContainer = dbContainer,
                        repoDtoList = repoList,
                        selectedItems = selectedItems.value,
                        quitSelectionMode = quitSelectionMode,
                        cloningText = cloningText,
                        unknownErrWhenCloning = unknownErrWhenCloning,
                        goToThisRepoId = goToThisRepoId,
                        goToThisRepoAndHighlightingIt = goToThisRepoAndHighlightingIt,
                        settings=settings,
                        refreshId=needRefreshRepoPage.value,
                        latestRefreshId = needRefreshRepoPage,
                        specifiedRefreshRepoList = specifiedRefreshRepoList.value,
                        loadingText = loadingText
                    )
                    triggerReFilter(filterResultNeedRefresh)
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("init Repos err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#init Repos page err: ${e.stackTraceToString()}")
                }
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect err: ${e.stackTraceToString()}")
        }
    }
}
private suspend fun doInit(
    dbContainer: AppContainer,
    repoDtoList: CustomStateListSaveable<RepoEntity>,
    selectedItems: MutableList<RepoEntity>,
    quitSelectionMode:()->Unit,
    cloningText: String,
    unknownErrWhenCloning: String,
    goToThisRepoId: MutableState<String>,
    goToThisRepoAndHighlightingIt:(id:String) ->Unit,
    settings:AppSettings,
    refreshId:String,
    latestRefreshId:MutableState<String>,
    specifiedRefreshRepoList:MutableList<RepoEntity>,
    loadingText: String,
){
    val pageChanged = {
        refreshId != latestRefreshId.value
    }
    val specifiedRefreshRepoList:MutableList<RepoEntity> = if(specifiedRefreshRepoList.isNotEmpty()) {
        val copy = specifiedRefreshRepoList.toMutableList()
        specifiedRefreshRepoList.clear()  
        copy
    }else {
        mutableListOf()
    }
    val repoRepository = dbContainer.repoRepository
    val willReloadTheseRepos = specifiedRefreshRepoList.ifEmpty { repoRepository.getAll() }
    if(specifiedRefreshRepoList.isEmpty()) {
        repoDtoList.value.clear()
        repoDtoList.value.addAll(willReloadTheseRepos)
    }else {
        val spCopy = specifiedRefreshRepoList.toList()
        specifiedRefreshRepoList.clear()
        spCopy.forEachBetter forEach@{
            val reQueriedRepo = repoRepository.getById(it.id) ?: return@forEach
            specifiedRefreshRepoList.add(reQueriedRepo)
        }
        val newList = mutableListOf<RepoEntity>()
        repoDtoList.value.toList().forEachBetter { i1->
            val found = specifiedRefreshRepoList.find { i2-> i2.id == i1.id }
            if(found != null) {
                newList.add(found)
            }else {
                newList.add(i1)
            }
        }
        repoDtoList.value.clear()
        repoDtoList.value.addAll(newList)
    }
    val pageChangedNeedAbort = updateSelectedList(
        selectedItemList = selectedItems,
        itemList = repoDtoList.value,
        quitSelectionMode = quitSelectionMode,
        match = { oldSelected, item-> oldSelected.id == item.id },
        pageChanged = pageChanged
    )
    if (pageChangedNeedAbort) return
    if(goToThisRepoId.value.isNotBlank()) {
        val target = goToThisRepoId.value
        goToThisRepoId.value = ""
        goToThisRepoAndHighlightingIt(target)
    }
    for ((idx,item) in repoDtoList.value.toList().withIndex()) {
        if(specifiedRefreshRepoList.isNotEmpty() && specifiedRefreshRepoList.find { it.id == item.id } == null) {
            continue
        }
        if (item.workStatus == Cons.dbRepoWorkStatusNotReadyNeedClone) {
            repoDtoList.value[idx].tmpStatus = cloningText
            Libgit2Helper.cloneSingleRepo(
                targetRepo = item,
                repoDb = repoRepository,
                settings = settings,
                unknownErrWhenCloning = unknownErrWhenCloning,
                repoDtoList = repoDtoList.value,
                repoCurrentIndexInRepoDtoList = idx,
                selectedItems = selectedItems
            )
        }else if(item.pendingTask == RepoPendingTask.NEED_CHECK_UNCOMMITED_CHANGES) {
            checkGitStatusAndUpdateItemInList(
                settings = settings,
                item = item,
                idx = idx,
                repoList = repoDtoList.value,
                loadingText = loadingText,
                pageChanged = pageChanged
            )
        } else {
        }
    }
}
private fun updateRepoListByIndexOrId(newItem:RepoEntity, idx: Int, list:MutableList<RepoEntity>, expectListSize:Int) {
    if(list.size == expectListSize) { 
        list[idx] = newItem
    }else {  
        val targetIdx = list.indexOfFirst { it.id == newItem.id }
        if(targetIdx != -1) {  
            list[targetIdx] = newItem
        }
    }
}
private fun checkGitStatusAndUpdateItemInList(
    settings: AppSettings,
    item: RepoEntity,
    idx: Int,
    repoList: MutableList<RepoEntity>,
    loadingText: String,
    pageChanged: () -> Boolean
) {
    val funName = "checkGitStatusAndUpdateItemInList"
    val repoLock = Libgit2Helper.getRepoLock(item.id)
    if(RepoStatusUtil.getRepoStatus(item.id)?.isNotBlank() == true) {
        MyLog.d(TAG, "#$funName: canceled check `git status`, because repo busy now")
        return
    }
    val repoListSizeSnapshot = repoList.size
    val needUpdateTmpStatus = item.tmpStatus.isBlank()
    if(needUpdateTmpStatus) {
        val newRepo = item.copyAllFields(settings)
        newRepo.tmpStatus = loadingText
        updateRepoListByIndexOrId(newRepo, idx, repoList, repoListSizeSnapshot)
    }
    doJobThenOffLoading {
        try {
            Repository.open(item.fullSavePath).use { repo ->
                MyLog.d(TAG, "#checkRepoGitStatus: checking git status for repo '${item.repoName}'")
                val needCommit = Libgit2Helper.hasUncommittedChanges(repo)
                MyLog.d(TAG, "#checkRepoGitStatus: repoName=${item.repoName}, repoId=${item.id}, needCommit=$needCommit, pageChanged=${pageChanged()}")
                if(pageChanged().not() && RepoStatusUtil.getRepoStatus(item.id).let{ it == null || it.isBlank() }) {
                    val newRepo = item.copyAllFields(settings)
                    newRepo.pendingTask = RepoPendingTask.NONE
                    if(needUpdateTmpStatus) {
                        newRepo.tmpStatus = ""
                    }
                    if(needCommit) {
                        newRepo.workStatus = Cons.dbRepoWorkStatusNeedCommit
                    }
                    updateRepoListByIndexOrId(newRepo, idx, repoList, repoListSizeSnapshot)
                }
            }
        }catch (e: Exception) {
            createAndInsertError(item.id, "check repo changes err: ${e.localizedMessage}")
            MyLog.e(TAG, "$TAG#$funName err: ${e.stackTraceToString()}")
        }
    }
}
