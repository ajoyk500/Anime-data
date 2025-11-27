package com.akcreation.gitsilent.screen.functions

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.AppContainer
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.PatchFile
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.git.Upstream
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StateRequestType
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.showErrAndSaveLog
import com.github.git24j.core.Repository
import com.github.git24j.core.Tree

private const val TAG = "ChangeListFunctions"
object ChangeListFunctions {
    suspend fun doCommit(
        requireShowCommitMsgDialog:Boolean,
        cmtMsg:String,
        requireCloseBottomBar:Boolean,
        curRepoFromParentPage:RepoEntity,
        refreshChangeList:(RepoEntity) -> Unit,
        username:MutableState<String>,
        email:MutableState<String>,
        requireShowToast:(String)->Unit,
        pleaseSetUsernameAndEmailBeforeCommit:String,
        initSetUsernameAndEmailDialog:(curRepo:RepoEntity, callback:(()->Unit)?)->Unit,
        amendCommit: MutableState<Boolean>,
        overwriteAuthor: MutableState<Boolean>,
        showCommitMsgDialog: MutableState<Boolean>,
        repoState:MutableIntState,
        activityContext:Context,
        loadingText:MutableState<String>,
        repoId:String,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        fromTo:String,
        itemList:List<StatusTypeEntrySaver>?,
        successCommitStrRes:String,
        indexIsEmptyForCommitDialog:MutableState<Boolean>,
        commitBtnTextForCommitDialog:MutableState<String>,
    ):Boolean{
        val settings = SettingsUtil.getSettingsSnapshot()
        commitBtnTextForCommitDialog.value = activityContext.getString(R.string.commit)
        indexIsEmptyForCommitDialog.value = false 
        Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
            val readyCreateCommit = Libgit2Helper.isReadyCreateCommit(repo, activityContext)
            if(readyCreateCommit.hasError()) {
                if(readyCreateCommit.code == Ret.ErrCode.indexIsEmpty) {
                    indexIsEmptyForCommitDialog.value = true
                }else {  
                    Msg.requireShowLongDuration(readyCreateCommit.msg)
                    refreshChangeList(curRepoFromParentPage)
                    return@doCommit false
                }
            }
            val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
            if(usernameFromConfig.isBlank() || emailFromConfig.isBlank()){
                MyLog.d(TAG, "#doCommit, username and email not set, will show dialog for set them")
                requireShowToast(pleaseSetUsernameAndEmailBeforeCommit)
                initSetUsernameAndEmailDialog(curRepoFromParentPage) {
                    doJobThenOffLoading {
                        doCommit(
                            requireShowCommitMsgDialog = requireShowCommitMsgDialog,
                            cmtMsg = cmtMsg,
                            requireCloseBottomBar = requireCloseBottomBar,
                            curRepoFromParentPage = curRepoFromParentPage,
                            refreshChangeList = refreshChangeList,
                            username = username,
                            email = email,
                            requireShowToast = requireShowToast,
                            pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                            initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                            amendCommit = amendCommit,
                            overwriteAuthor = overwriteAuthor,
                            showCommitMsgDialog = showCommitMsgDialog,
                            repoState = repoState,
                            activityContext = activityContext,
                            loadingText = loadingText,
                            repoId = repoId,
                            bottomBarActDoneCallback = bottomBarActDoneCallback,
                            fromTo = fromTo,
                            itemList = itemList,
                            successCommitStrRes = successCommitStrRes,
                            indexIsEmptyForCommitDialog = indexIsEmptyForCommitDialog,
                            commitBtnTextForCommitDialog = commitBtnTextForCommitDialog
                        )
                    }
                }
                return@doCommit false
            }
            var commitMsgWillUse = ""
            if(requireShowCommitMsgDialog) {  
                amendCommit.value = false
                overwriteAuthor.value = false
                showCommitMsgDialog.value = true
                return@doCommit false
            }else { 
                commitMsgWillUse = cmtMsg
            }
            val amendCommit = amendCommit.value
            val overwriteAuthor = overwriteAuthor.value
            val username = usernameFromConfig
            val email = emailFromConfig
            MyLog.d(TAG, "#doCommit, before createCommit")
            val ret = if(repoState.intValue== Repository.StateT.REBASE_MERGE.bit) {    
                loadingText.value = activityContext.getString(R.string.rebase_continue) + Cons.oneChar3dots
                Libgit2Helper.rebaseContinue(
                    repo,
                    activityContext,
                    username,
                    email,
                    commitMsgForFirstCommit = commitMsgWillUse,
                    overwriteAuthorForFirstCommit = overwriteAuthor,
                    settings = settings
                )
            }else if(repoState.intValue == Repository.StateT.CHERRYPICK.bit) {
                loadingText.value = activityContext.getString(R.string.cherrypick_continue)+Cons.oneChar3dots
                Libgit2Helper.cherrypickContinue(
                    activityContext,
                    repo,
                    msg=commitMsgWillUse,
                    username = usernameFromConfig,
                    email=emailFromConfig,
                    autoClearState = false,  
                    overwriteAuthor = overwriteAuthor,
                    settings=settings
                )
            }else {
                loadingText.value = activityContext.getString(R.string.committing)
                Libgit2Helper.createCommit(
                    repo = repo,
                    msg = commitMsgWillUse,
                    username = username,
                    email = email,
                    indexItemList = null,
                    amend = amendCommit,
                    overwriteAuthorWhenAmend = overwriteAuthor,
                    settings = settings,
                    cleanRepoStateIfSuccess = false,
                )
            }
            if(ret.hasError()) {  
                MyLog.d(TAG, "#doCommit, createCommit failed, has error: "+ret.msg)
                Msg.requireShowLongDuration(ret.msg)
                val errPrefix= activityContext.getString(R.string.commit_err)
                createAndInsertError(repoId, "$errPrefix: ${ret.msg}")
                if(requireCloseBottomBar) {
                    bottomBarActDoneCallback("", curRepoFromParentPage)
                }else {
                    refreshChangeList(curRepoFromParentPage)
                }
                return@doCommit false
            }else {  
                MyLog.d(TAG, "#doCommit, createCommit success")
                Libgit2Helper.cleanRepoState(repo)  
                repoState.intValue = repo.state()?.bit?: Cons.gitRepoStateInvalid  
                val repoDb = AppModel.dbContainer.repoRepository
                val shortNewCommitHash = ret.data.toString().substring(Cons.gitShortCommitHashRange)
                repoDb.updateCommitHash(
                    repoId=curRepoFromParentPage.id,
                    lastCommitHash = shortNewCommitHash,
                )
                repoDb.updateLastUpdateTime(curRepoFromParentPage.id, getSecFromTime())
                requireShowToast(successCommitStrRes)
                if(requireCloseBottomBar) {
                    bottomBarActDoneCallback("", curRepoFromParentPage)
                }
                return@doCommit true
            }
        }
    }
    suspend fun doFetch(
        remoteNameParam:String?,
        curRepoFromParentPage:RepoEntity,
        requireShowToast:(String)->Unit,
        activityContext:Context,
        loadingText:MutableState<String>,
        dbContainer:AppContainer,
    ):Boolean{   
        Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
            try {
                var remoteName = remoteNameParam
                if(remoteName == null || remoteName.isBlank()) {
                    val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)
                    val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)
                    remoteName = upstream.remote
                    if(remoteName == null || remoteName.isBlank()) {  
                        requireShowToast(activityContext.getString(R.string.err_upstream_invalid_plz_try_sync_first))
                        return@doFetch false
                    }
                }
                loadingText.value = activityContext.getString(R.string.fetching)
                val credential = Libgit2Helper.getRemoteCredential(
                    dbContainer.remoteRepository,
                    dbContainer.credentialRepository,
                    curRepoFromParentPage.id,
                    remoteName,
                    trueFetchFalsePush = true
                )
                Libgit2Helper.fetchRemoteForRepo(repo, remoteName, credential, curRepoFromParentPage)
                val repoDb = AppModel.dbContainer.repoRepository
                repoDb.updateLastUpdateTime(curRepoFromParentPage.id, getSecFromTime())
                return@doFetch true
            }catch (e:Exception) {
                showErrAndSaveLog(TAG, "#doFetch() err: "+e.stackTraceToString(), "fetch err: "+e.localizedMessage, requireShowToast, curRepoFromParentPage.id)
                return@doFetch false
            }
        }
    }
    suspend fun doMerge(
        requireCloseBottomBar:Boolean,
        upstreamParam: Upstream?,
        showMsgIfHasConflicts:Boolean,
        trueMergeFalseRebase:Boolean,
        curRepoFromParentPage:RepoEntity,
        requireShowToast:(String)->Unit,
        activityContext:Context,
        loadingText:MutableState<String>,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
    ):Boolean {
        try {
            val settings = SettingsUtil.getSettingsSnapshot()
            Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
                var upstream = upstreamParam
                if(Libgit2Helper.isUpstreamInvalid(upstream)) {  
                    val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)  
                    upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)  
                    if(Libgit2Helper.isUpstreamInvalid(upstream)) {
                        requireShowToast(activityContext.getString(R.string.err_upstream_invalid_plz_try_sync_first))
                        return false
                    }
                }
                val remoteRefSpec = Libgit2Helper.getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(
                    upstream!!.remote,
                    upstream.branchRefsHeadsFullRefSpec
                )
                MyLog.d(TAG, "doMerge: remote="+upstream.remote+", branchFullRefSpec=" + upstream.branchRefsHeadsFullRefSpec +", trueMergeFalseRebase=$trueMergeFalseRebase")
                val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
                if(Libgit2Helper.isUsernameAndEmailInvalid(usernameFromConfig,emailFromConfig)) {
                    requireShowToast(activityContext.getString(R.string.plz_set_username_and_email_first))
                    return false
                }
                val mergeResult = if(trueMergeFalseRebase) {
                    loadingText.value = activityContext.getString(R.string.merging)
                    Libgit2Helper.mergeOneHead(
                        repo,
                        remoteRefSpec,
                        usernameFromConfig,
                        emailFromConfig,
                        settings = settings
                    )
                }else {
                    loadingText.value = activityContext.getString(R.string.rebasing)
                    Libgit2Helper.mergeOrRebase(
                        repo,
                        targetRefName = remoteRefSpec,
                        username = usernameFromConfig,
                        email = emailFromConfig,
                        requireMergeByRevspec = false,
                        revspec = "",
                        trueMergeFalseRebase = false,
                        settings = settings
                    )
                }
                if (mergeResult.hasError()) {
                    if (mergeResult.code == Ret.ErrCode.mergeFailedByAfterMergeHasConfilts) {
                        if(showMsgIfHasConflicts){
                            requireShowToast(activityContext.getString(R.string.has_conflicts))
                        }
                    }else {
                        requireShowToast(mergeResult.msg)
                    }
                    createAndInsertError(curRepoFromParentPage.id, mergeResult.msg)
                    if (requireCloseBottomBar) {
                        bottomBarActDoneCallback("", curRepoFromParentPage)
                    }
                    return false
                }
                Libgit2Helper.cleanRepoState(repo)
                Libgit2Helper.updateDbAfterMergeSuccess(mergeResult, activityContext, curRepoFromParentPage.id, requireShowToast, trueMergeFalseRebase)
                if (requireCloseBottomBar) {
                    bottomBarActDoneCallback("", curRepoFromParentPage)
                }
                return true
            }
        }catch (e:Exception) {
            if (requireCloseBottomBar) {
                bottomBarActDoneCallback("", curRepoFromParentPage)
            }
            showErrAndSaveLog(
                logTag = TAG,
                logMsg = "#doMerge(trueMergeFalseRebase=$trueMergeFalseRebase) err: "+e.stackTraceToString(),
                showMsg = "${if(trueMergeFalseRebase) "merge" else "rebase"} err: "+e.localizedMessage,
                showMsgMethod = requireShowToast,
                repoId = curRepoFromParentPage.id,
            )
            return false
        }
    }
    suspend fun doPush(
        requireCloseBottomBar:Boolean,
        upstreamParam:Upstream?,
        force:Boolean=false,
        curRepoFromParentPage:RepoEntity,
        requireShowToast:(String)->Unit,
        activityContext:Context,
        loadingText:MutableState<String>,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        dbContainer: AppContainer,
        forcePush_pushWithLease: Boolean = false,
        forcePush_expectedRefspecForLease:String = "",
    ) : Boolean {
        try {
            Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
                if(repo.headDetached()) {
                    requireShowToast(activityContext.getString(R.string.push_failed_by_detached_head))
                    return@doPush false
                }
                var upstream:Upstream? = upstreamParam
                if(Libgit2Helper.isUpstreamInvalid(upstream)) {  
                    val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)  
                    upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)  
                    if(Libgit2Helper.isUpstreamInvalid(upstream)) {
                        requireShowToast(activityContext.getString(R.string.err_upstream_invalid_plz_try_sync_first))
                        return@doPush false
                    }
                }
                MyLog.d(TAG, "#doPush: upstream.remote="+upstream!!.remote+", upstream.branchFullRefSpec="+upstream!!.branchRefsHeadsFullRefSpec)
                if(force && forcePush_pushWithLease) {
                    loadingText.value = activityContext.getString(R.string.checking)
                    Libgit2Helper.forcePushLeaseCheckPassedOrThrow(
                        repoEntity = curRepoFromParentPage,
                        repo = repo,
                        forcePush_expectedRefspecForLease = forcePush_expectedRefspecForLease,
                        upstream = upstream,
                    )
                }
                loadingText.value = activityContext.getString(if(force) R.string.force_pushing else R.string.pushing)
                val credential = Libgit2Helper.getRemoteCredential(
                    dbContainer.remoteRepository,
                    dbContainer.credentialRepository,
                    curRepoFromParentPage.id,
                    upstream!!.remote,
                    trueFetchFalsePush = false
                )
                Libgit2Helper.push(repo, upstream!!.remote, listOf(upstream!!.pushRefSpec), credential, force)
                val repoDb = AppModel.dbContainer.repoRepository
                repoDb.updateLastUpdateTime(curRepoFromParentPage.id, getSecFromTime())
                if (requireCloseBottomBar) {
                    bottomBarActDoneCallback("", curRepoFromParentPage)
                }
                return@doPush true
            }
        }catch (e:Exception) {
            if (requireCloseBottomBar) {
                bottomBarActDoneCallback("", curRepoFromParentPage)
            }
            showErrAndSaveLog(
                logTag = TAG,
                logMsg = "#doPush(force=$force) err: "+e.stackTraceToString(),
                showMsg = "${if(force) "Push(Force)" else "Push"} error: "+e.localizedMessage,
                showMsgMethod = requireShowToast,
                repoId = curRepoFromParentPage.id
            )
            return@doPush false
        }
    }
    suspend fun doSync(
        loadingOn:(String)->Unit,
        loadingOff:()->Unit,
        requireCloseBottomBar:Boolean,
        trueMergeFalseRebase:Boolean,
        curRepoFromParentPage:RepoEntity,
        requireShowToast:(String)->Unit,
        activityContext:Context,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        plzSetUpStreamForCurBranch:String,
        initSetUpstreamDialog:(remoteList: List<String>, curBranchShortName: String, curBranchFullName: String, onOkText: String, successCallback: (()->Unit)?) -> Unit,
        loadingText:MutableState<String>,
        dbContainer:AppContainer,
    ) {
        Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
            if(repo.headDetached()) {
                requireShowToast(activityContext.getString(R.string.sync_failed_by_detached_head))
                return@doSync
            }
            val hasUpstream = Libgit2Helper.isBranchHasUpstream(repo)
            val headRef = Libgit2Helper.resolveHEAD(repo) ?: throw RuntimeException("resolve HEAD failed")
            val curBranchShortName = headRef.shorthand()
            val curBranchFullName = headRef.name()
            if (!hasUpstream) {  
                requireShowToast(plzSetUpStreamForCurBranch)  
                initSetUpstreamDialog(Libgit2Helper.getRemoteList(repo), curBranchShortName, curBranchFullName, activityContext.getString(R.string.save_and_sync)) {
                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.syncing)) {
                        doSync(
                            loadingOn = loadingOn,
                            loadingOff = loadingOff,
                            requireCloseBottomBar = requireCloseBottomBar,
                            trueMergeFalseRebase = trueMergeFalseRebase,
                            curRepoFromParentPage = curRepoFromParentPage,
                            requireShowToast = requireShowToast,
                            activityContext = activityContext,
                            bottomBarActDoneCallback = bottomBarActDoneCallback,
                            plzSetUpStreamForCurBranch = plzSetUpStreamForCurBranch,
                            initSetUpstreamDialog = initSetUpstreamDialog,
                            loadingText = loadingText,
                            dbContainer = dbContainer
                        )
                    }
                    Unit
                }
            }else {  
                try {
                    loadingText.value = activityContext.getString(R.string.syncing)
                    val upstream = Libgit2Helper.getUpstreamOfBranch(repo, curBranchShortName)
                    val fetchSuccess = doFetch(
                        upstream.remote,
                        curRepoFromParentPage = curRepoFromParentPage,
                        requireShowToast = requireShowToast,
                        activityContext = activityContext,
                        loadingText = loadingText,
                        dbContainer = dbContainer
                    )
                    if(!fetchSuccess) {
                        requireShowToast(activityContext.getString(R.string.fetch_failed))
                        if(requireCloseBottomBar) {
                            bottomBarActDoneCallback("", curRepoFromParentPage)
                        }
                        return@doSync
                    }
                    val isUpstreamExistOnLocal = Libgit2Helper.isUpstreamActuallyExistOnLocal(
                        repo,
                        upstream.remote,
                        upstream.branchRefsHeadsFullRefSpec
                    )
                    MyLog.d(TAG, "@doSync: isUpstreamExistOnLocal="+isUpstreamExistOnLocal)
                    if(isUpstreamExistOnLocal) {  
                        val mergeSuccess = doMerge(
                            requireCloseBottomBar = false,
                            upstreamParam = upstream,
                            showMsgIfHasConflicts = false,
                            trueMergeFalseRebase = trueMergeFalseRebase,
                            curRepoFromParentPage = curRepoFromParentPage,
                            requireShowToast = requireShowToast,
                            activityContext = activityContext,
                            loadingText = loadingText,
                            bottomBarActDoneCallback = bottomBarActDoneCallback,
                        )
                        if(!mergeSuccess) {  
                            if(Libgit2Helper.hasConflictItemInRepo(repo)) {  
                                requireShowToast(activityContext.getString(R.string.has_conflicts))
                            }
                            if(requireCloseBottomBar) {
                                bottomBarActDoneCallback("", curRepoFromParentPage)
                            }
                            return@doSync
                        }
                    }
                    val pushSuccess = doPush(
                        requireCloseBottomBar = false,
                        upstreamParam = upstream,
                        force = false,
                        curRepoFromParentPage = curRepoFromParentPage,
                        requireShowToast = requireShowToast,
                        activityContext = activityContext,
                        loadingText = loadingText,
                        bottomBarActDoneCallback = bottomBarActDoneCallback,
                        dbContainer = dbContainer
                    )
                    if(pushSuccess) {
                        requireShowToast(activityContext.getString(if(trueMergeFalseRebase) R.string.sync_merge_success else R.string.sync_rebase_success))
                    }else {
                        requireShowToast(activityContext.getString(if(trueMergeFalseRebase) R.string.sync_merge_failed else R.string.sync_rebase_failed))
                    }
                    if(requireCloseBottomBar) {
                        bottomBarActDoneCallback("", curRepoFromParentPage)
                    }
                }catch (e:Exception) {
                    if(requireCloseBottomBar) {
                        bottomBarActDoneCallback("", curRepoFromParentPage)
                    }
                    showErrAndSaveLog(TAG, "#doSync() err: "+e.stackTraceToString(), "sync err: "+e.localizedMessage, requireShowToast, curRepoFromParentPage.id)
                }
            }
        }
    }
    fun doStage(
        curRepo:RepoEntity,
        requireCloseBottomBar:Boolean,
        userParamList:Boolean,
        paramList:List<StatusTypeEntrySaver>?,
        fromTo: String,
        selectedListIsEmpty:()->Boolean,
        requireShowToast:(String)->Unit,
        noItemSelectedStrRes:String,
        activityContext:Context,
        selectedItemList:List<StatusTypeEntrySaver>,
        loadingText:MutableState<String>,
        nFilesStagedStrRes:String,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit
    ):Boolean{
        if(fromTo != Cons.gitDiffFromIndexToWorktree) {
            return true
        }
        if (!userParamList && selectedListIsEmpty()) {  
            requireShowToast(noItemSelectedStrRes)
            return false
        }
        if(userParamList && paramList.isNullOrEmpty()) {
            requireShowToast(activityContext.getString(R.string.item_list_is_empty))
            return false
        }
        val actuallyStageList = if(userParamList) paramList!! else selectedItemList
        loadingText.value = activityContext.getString(R.string.staging)
        Repository.open(curRepo.fullSavePath).use { repo ->
            Libgit2Helper.stageStatusEntryAndWriteToDisk(repo, actuallyStageList)
        }
        val msg = replaceStringResList(
            nFilesStagedStrRes,
            listOf(actuallyStageList.size.toString())
        )
        if(requireCloseBottomBar) {
            bottomBarActDoneCallback(msg, curRepo)
        }
        return true
    }
    fun changeListDoRefresh(stateForRefresh:MutableState<String>, whichRepoRequestRefresh:RepoEntity) {
        changeStateTriggerRefreshPage(stateForRefresh, requestType= StateRequestType.withRepoId, data = whichRepoRequestRefresh.id)
    }
    suspend fun doPull(
        curRepo:RepoEntity,
        trueMergeFalseRebase: Boolean,
        activityContext:Context,
        requireCloseBottomBar:Boolean,
        dbContainer: AppContainer,
        requireShowToast:(String)->Unit,
        loadingText:MutableState<String>,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        changeListRequireRefreshFromParentPage:(RepoEntity) -> Unit,
    ) {
        try {
            val fetchSuccess = doFetch(
                remoteNameParam = null,
                curRepoFromParentPage = curRepo,
                requireShowToast = requireShowToast,
                activityContext = activityContext,
                loadingText = loadingText,
                dbContainer = dbContainer
            )
            if(!fetchSuccess) {
                requireShowToast(activityContext.getString(R.string.fetch_failed))
            }else {
                val mergeSuccess = doMerge(
                    requireCloseBottomBar = false,
                    upstreamParam = null,
                    showMsgIfHasConflicts = true,
                    trueMergeFalseRebase = trueMergeFalseRebase,
                    curRepoFromParentPage = curRepo,
                    requireShowToast = requireShowToast,
                    activityContext = activityContext,
                    loadingText = loadingText,
                    bottomBarActDoneCallback = bottomBarActDoneCallback
                )
                if(!mergeSuccess){
                    requireShowToast(activityContext.getString(if(trueMergeFalseRebase) R.string.merge_failed else R.string.rebase_failed))
                }else {
                    requireShowToast(activityContext.getString(if(trueMergeFalseRebase) R.string.pull_merge_success else R.string.pull_rebase_success))
                }
            }
            if(requireCloseBottomBar) {
                bottomBarActDoneCallback("", curRepo)
            }
        }catch (e:Exception){
            if(requireCloseBottomBar) {
                bottomBarActDoneCallback("", curRepo)
            }
            showErrAndSaveLog(
                logTag = TAG,
                logMsg = "doPull(trueMergeFalseRebase=$trueMergeFalseRebase) err: "+e.stackTraceToString(),
                showMsg = activityContext.getString(if(trueMergeFalseRebase) R.string.pull_merge_failed else R.string.pull_rebase_failed)+": "+e.localizedMessage,
                showMsgMethod = requireShowToast,
                repoId = curRepo.id
            )
        }
    }
    suspend fun doAccept(
        curRepo:RepoEntity,
        acceptTheirs:Boolean,
        loadingText:MutableState<String>,
        activityContext:Context,
        hasConflictItemsSelected:()->Boolean,
        requireShowToast:(String)->Unit,
        selectedItemList:List<StatusTypeEntrySaver>,
        repoState:MutableIntState,
        repoId:String,
        fromTo:String,
        selectedListIsEmpty:()->Boolean,
        noItemSelectedStrRes:String,
        nFilesStagedStrRes:String,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        changeListRequireRefreshFromParentPage:(RepoEntity)->Unit,
    ) {
        loadingText.value = (if(acceptTheirs) activityContext.getString(R.string.accept_theirs) else activityContext.getString(R.string.accept_ours)) + Cons.oneChar3dots
        val repoFullPath = curRepo.fullSavePath
        if(!hasConflictItemsSelected()) {
            requireShowToast(activityContext.getString(R.string.err_no_conflict_item_selected))
        }
        val conflictList = selectedItemList.toList().filter { it.changeType == Cons.gitStatusConflict }
        val pathspecList = conflictList.map { it.relativePathUnderRepo }
        Repository.open(repoFullPath).use { repo->
            val acceptRet = if(repoState.intValue == Repository.StateT.MERGE.bit) {
                Libgit2Helper.mergeAccept(repo, pathspecList, acceptTheirs)
            }else if(repoState.intValue == Repository.StateT.REBASE_MERGE.bit) {
                Libgit2Helper.rebaseAccept(repo, pathspecList, acceptTheirs)
            }else if(repoState.intValue == Repository.StateT.CHERRYPICK.bit) {
                Libgit2Helper.cherrypickAccept(repo, pathspecList, acceptTheirs)
            }else {
                Ret.createError(null, "bad repo state")
            }
            if(acceptRet.hasError()) {
                requireShowToast(acceptRet.msg)
                createAndInsertError(repoId, acceptRet.msg)
            }else {  
                val existConflictItems = conflictList.filter { it.toFile().exists() }
                val stageSuccess = if(existConflictItems.isEmpty()) { 
                    true
                }else {  
                    ChangeListFunctions.doStage(
                        curRepo=curRepo,
                        requireCloseBottomBar = false,
                        userParamList = true,
                        paramList = existConflictItems,
                        fromTo = fromTo,
                        selectedListIsEmpty = selectedListIsEmpty,
                        requireShowToast = requireShowToast,
                        noItemSelectedStrRes = noItemSelectedStrRes,
                        activityContext = activityContext,
                        selectedItemList = selectedItemList,
                        loadingText = loadingText,
                        nFilesStagedStrRes = nFilesStagedStrRes,
                        bottomBarActDoneCallback = bottomBarActDoneCallback
                    )
                }
                if(stageSuccess) {  
                    requireShowToast(activityContext.getString(R.string.success))
                }else{  
                    requireShowToast(activityContext.getString(R.string.stage_failed))
                }
            }
            changeListRequireRefreshFromParentPage(curRepo)
        }
    }
    fun createPath(
        curRepo: RepoEntity,
        leftCommit:String,  
        rightCommit:String,  
        fromTo: String,
        relativePaths: List<String>
    ):Ret<PatchFile?> {
        Repository.open(curRepo.fullSavePath).use { repo ->
            var treeToWorkTree = false  
            val (reverse: Boolean, tree1: Tree?, tree2: Tree?) = if (fromTo == Cons.gitDiffFromIndexToWorktree || fromTo == Cons.gitDiffFromHeadToIndex) {
                Triple(false, null, null)
            } else if (Libgit2Helper.CommitUtil.isLocalCommitHash(leftCommit) || Libgit2Helper.CommitUtil.isLocalCommitHash(rightCommit)) {
                treeToWorkTree = true
                val reverse = Libgit2Helper.CommitUtil.isLocalCommitHash(leftCommit)  
                val tree1 = if (reverse) {
                    Libgit2Helper.resolveTree(repo, rightCommit)
                } else {
                    Libgit2Helper.resolveTree(repo, leftCommit)
                }
                if (tree1 == null) {
                    throw RuntimeException("resolve tree1 failed, 11982433")
                }
                val tree2 = null
                Triple(reverse, tree1, tree2)
            } else {
                val reverse = false
                val tree1 = Libgit2Helper.resolveTree(repo, leftCommit) ?: throw RuntimeException("resolve tree1 failed, 12978960")
                val tree2 = Libgit2Helper.resolveTree(repo, rightCommit) ?: throw RuntimeException("resolve tree2 failed, 17819020")
                Triple(reverse, tree1, tree2)
            }
            val (left: String, right: String) = if (fromTo == Cons.gitDiffFromIndexToWorktree) {
                Pair(Cons.git_IndexCommitHash, Cons.git_LocalWorktreeCommitHash)
            } else if (fromTo == Cons.gitDiffFromHeadToIndex) {
                Pair(Cons.git_HeadCommitHash, Cons.git_IndexCommitHash)
            } else {
                Pair(leftCommit, rightCommit)
            }
            val outFile = FsUtils.Patch.newPatchFile(curRepo.repoName, left, right)
            return Libgit2Helper.savePatchToFileAndGetContent(
                outFile = outFile,
                pathSpecList = relativePaths,
                repo = repo,
                tree1 = tree1,
                tree2 = tree2,
                fromTo = fromTo,
                reverse = reverse,
                treeToWorkTree = treeToWorkTree,
                returnDiffContent = false  
            )
        }
    }
}
