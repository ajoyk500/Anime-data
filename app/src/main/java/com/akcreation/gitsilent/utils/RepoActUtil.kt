package com.akcreation.gitsilent.utils

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.AppContainer
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.server.bean.NotificationSender
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.cache.NotifySenderMap
import com.akcreation.gitsilent.utils.encrypt.MasterPassUtil
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository

private const val TAG = "RepoActUtil"
object RepoActUtil {
    private const val waitInMillSecIfApiBusy = 5000L  
    private fun throwRepoBusy(prefix:String, repoName:String) {
        throwWithPrefix(prefix, RuntimeException(Cons.repoBusyStr))
    }
    private fun getNotifySender(repoId:String, sessionId: String):NotificationSender? {
        return NotifySenderMap.getByType<NotificationSender>(NotifySenderMap.genKey(repoId, sessionId))
    }
    private fun removeNotifySender(repoId:String, sessionId: String) {
        NotifySenderMap.del(NotifySenderMap.genKey(repoId, sessionId))
    }
    suspend fun syncRepoList(
        sessionId:String,
        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,    
        gitEmailFromUrl:String,    
        autoCommit:Boolean,
        force:Boolean,  
        pullWithRebase: Boolean, 
    ){
        val funName = "syncRepoList"
        if(repoList.isEmpty()) {
            MyLog.d(TAG, "#$funName: target list is empty")
            return
        }
        val prefix = "sync"
        val db = AppModel.dbContainer
        val settings = SettingsUtil.getSettingsSnapshot()
        val masterPassword = MasterPassUtil.get(AppModel.realAppContext)
        repoList.forEachBetter { repoFromDb ->
            doJobThenOffLoading {
                val notiSender = getNotifySender(repoFromDb.id, sessionId)
                try {
                    notiSender?.sendProgressNotification?.invoke(repoFromDb.repoName, "syncing...")
                    Libgit2Helper.doActWithRepoLock(repoFromDb, waitInMillSec = waitInMillSecIfApiBusy, onLockFailed = { throwRepoBusy(prefix, repoFromDb.repoName) }) {
                        syncSingle(
                            sendProgressNotification = notiSender?.sendProgressNotification,
                            repoFromDb = repoFromDb,
                            db = db,
                            masterPassword = masterPassword,
                            gitUsernameFromUrl = gitUsernameFromUrl,
                            gitEmailFromUrl = gitEmailFromUrl,
                            settings = settings,
                            sendSuccessNotification = notiSender?.sendSuccessNotification,
                            autoCommit = autoCommit,
                            routeName = routeName,
                            pullWithRebase = pullWithRebase,
                            sendErrNotification = notiSender?.sendErrNotification,
                            force = force
                        )
                    }
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?: "unknown err"
                    createAndInsertError(repoFromDb.id, "sync by api $routeName err: $errMsg")
                    notiSender?.sendErrNotification?.invoke(repoFromDb.repoName, errMsg, Cons.selectedItem_ChangeList, repoFromDb.id)
                    MyLog.e(TAG, "#$funName: route:$routeName, repoName=${repoFromDb.repoName}, err=${e.stackTraceToString()}")
                }finally {
                    removeNotifySender(repoFromDb.id, sessionId)
                }
            }
        }
    }
    private suspend fun syncSingle(
        sendProgressNotification: ((repoNameOrId: String, progress: String) -> Unit)?,
        repoFromDb: RepoEntity,
        db: AppContainer,
        masterPassword: String,
        gitUsernameFromUrl: String,
        gitEmailFromUrl: String,
        settings: AppSettings,
        sendSuccessNotification: ((title: String?, msg: String?, startPage: Int?, startRepoId: String?) -> Unit)?,
        autoCommit: Boolean,
        routeName: String,
        pullWithRebase: Boolean,
        sendErrNotification: ((title: String, msg: String, startPage: Int, startRepoId: String) -> Unit)?,
        force: Boolean
    ) {
        val prefix = "sync"
        try {
            sendProgressNotification?.invoke(repoFromDb.repoName, "pulling...")
            pullSingle(
                repoFromDb = repoFromDb,
                db = db,
                masterPassword = masterPassword,
                gitUsernameFromUrl = gitUsernameFromUrl,
                gitEmailFromUrl = gitEmailFromUrl,
                settings = settings,
                pullWithRebase = pullWithRebase,
                sendSuccessNotification = sendSuccessNotification
            )
            sendProgressNotification?.invoke(repoFromDb.repoName, "pushing...")
            pushSingle(
                repoFromDb = repoFromDb,
                autoCommit = autoCommit,
                gitUsernameFromUrl = gitUsernameFromUrl,
                gitEmailFromUrl = gitEmailFromUrl,
                routeName = routeName,
                sendErrNotification = sendErrNotification,
                settings = settings,
                force = force,
                sendSuccessNotification = sendSuccessNotification,
                db = db,
                masterPassword = masterPassword
            )
            sendProgressNotification?.invoke(repoFromDb.repoName, "sync successfully")
        }catch (e:Exception) {
            throwWithPrefix(prefix, e)
        }
    }
    suspend fun pullRepoList(
        sessionId:String,
        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,  
        gitEmailFromUrl:String,  
        pullWithRebase: Boolean,
    ) {
        val funName = "pullRepoList"
        if(repoList.isEmpty()) {
            MyLog.d(TAG, "#$funName: target list is empty")
            return
        }
        val prefix = "pull"
        val db = AppModel.dbContainer
        val settings = SettingsUtil.getSettingsSnapshot()
        val masterPassword = MasterPassUtil.get(AppModel.realAppContext)
        repoList.forEachBetter { repoFromDb ->
            doJobThenOffLoading {
                val notiSender = getNotifySender(repoFromDb.id, sessionId)
                try {
                    notiSender?.sendProgressNotification?.invoke(repoFromDb.repoName, "pulling...")
                    Libgit2Helper.doActWithRepoLock(repoFromDb, waitInMillSec = waitInMillSecIfApiBusy, onLockFailed = { throwRepoBusy(prefix, repoFromDb.repoName) }) {
                        pullSingle(
                            repoFromDb = repoFromDb,
                            db = db,
                            masterPassword = masterPassword,
                            gitUsernameFromUrl = gitUsernameFromUrl,
                            gitEmailFromUrl = gitEmailFromUrl,
                            settings = settings,
                            pullWithRebase = pullWithRebase,
                            sendSuccessNotification = notiSender?.sendSuccessNotification
                        )
                    }
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?: "unknown err"
                    createAndInsertError(repoFromDb.id, "pull by api $routeName err: $errMsg")
                    notiSender?.sendErrNotification?.invoke(repoFromDb.repoName, errMsg, Cons.selectedItem_ChangeList, repoFromDb.id)
                    MyLog.e(TAG, "#$funName: route:$routeName, repoName=${repoFromDb.repoName}, err=${e.stackTraceToString()}")
                }finally {
                    removeNotifySender(repoFromDb.id, sessionId)
                }
            }
        }
    }
    private suspend fun pullSingle(
        repoFromDb: RepoEntity,
        db: AppContainer,
        masterPassword: String,
        gitUsernameFromUrl: String,
        gitEmailFromUrl: String,
        pullWithRebase: Boolean,
        settings: AppSettings,
        sendSuccessNotification: ((title: String?, msg: String?, startPage: Int?, startRepoId: String?) -> Unit)?
    ) {
        val prefix = "pull"
        try {
            if (dbIntToBool(repoFromDb.isDetached)) {
                throw RuntimeException("repo is detached")
            }
            if (!Libgit2Helper.isValidGitRepo(repoFromDb.fullSavePath)) {
                throw RuntimeException("invalid git repo")
            }
            Repository.open(repoFromDb.fullSavePath).use { gitRepo ->
                val upstream = Libgit2Helper.getUpstreamOfBranch(gitRepo, repoFromDb.branch)
                if (upstream.remote.isBlank() || upstream.branchRefsHeadsFullRefSpec.isBlank()) {
                    throw RuntimeException("invalid upstream")
                }
                val credential = Libgit2Helper.getRemoteCredential(
                    db.remoteRepository,
                    db.credentialRepository,
                    repoFromDb.id,
                    upstream.remote,
                    trueFetchFalsePush = true,
                    masterPassword = masterPassword
                )
                Libgit2Helper.fetchRemoteForRepo(gitRepo, upstream.remote, credential, repoFromDb)
                val repoDb = AppModel.dbContainer.repoRepository
                repoDb.updateLastUpdateTime(repoFromDb.id, getSecFromTime())
                val (username, email) = if (gitUsernameFromUrl.isNotBlank() && gitEmailFromUrl.isNotBlank()) {
                    Pair(gitUsernameFromUrl, gitEmailFromUrl)
                } else {
                    val (gitUsernameFromConfig, gitEmailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(gitRepo)
                    val finallyUsername = gitUsernameFromUrl.ifBlank { gitUsernameFromConfig }
                    val finallyEmail = gitEmailFromUrl.ifBlank { gitEmailFromConfig }
                    Pair(finallyUsername, finallyEmail)
                }
                if (username == null || username.isBlank()) {
                    throw RuntimeException("git username invalid")
                }
                if (email == null || email.isBlank()) {
                    throw RuntimeException("git email invalid")
                }
                val remoteRefSpec = Libgit2Helper.getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(
                    upstream!!.remote,
                    upstream.branchRefsHeadsFullRefSpec
                )
                val mergeRet = Libgit2Helper.mergeOrRebase(
                    repo = gitRepo,
                    targetRefName = remoteRefSpec,
                    username = username,
                    email = email,
                    requireMergeByRevspec = false,
                    revspec = "",
                    trueMergeFalseRebase = !pullWithRebase,
                    settings = settings
                )
                if (mergeRet.hasError()) {
                    throw RuntimeException(mergeRet.msg)
                }
                val successMsg = if(mergeRet.code == Ret.SuccessCode.upToDate) { 
                    "$prefix: Already up-to-date"
                }else { 
                    "pull successfully"
                }
                sendSuccessNotification?.invoke(repoFromDb.repoName, successMsg, Cons.selectedItem_ChangeList, repoFromDb.id)
            }
        }catch (e:Exception) {
            throwWithPrefix(prefix, e)
        }
    }
    suspend fun pushRepoList(
        sessionId: String,
        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,    
        gitEmailFromUrl:String,    
        autoCommit:Boolean,
        force:Boolean,
    ) {
        val funName = "pushRepoList"
        if(repoList.isEmpty()) {
            MyLog.d(TAG, "#$funName: target list is empty")
            return
        }
        val prefix = "push"
        val db = AppModel.dbContainer
        val settings = SettingsUtil.getSettingsSnapshot()
        val masterPassword = MasterPassUtil.get(AppModel.realAppContext)
        repoList.forEachBetter { repoFromDb ->
            doJobThenOffLoading {
                val notiSender = getNotifySender(repoFromDb.id, sessionId)
                try {
                    notiSender?.sendProgressNotification?.invoke(repoFromDb.repoName, "pushing...")
                    Libgit2Helper.doActWithRepoLock(repoFromDb, waitInMillSec = waitInMillSecIfApiBusy, onLockFailed = { throwRepoBusy(prefix, repoFromDb.repoName) }) {
                        pushSingle(
                            repoFromDb = repoFromDb,
                            autoCommit = autoCommit,
                            gitUsernameFromUrl = gitUsernameFromUrl,
                            gitEmailFromUrl = gitEmailFromUrl,
                            routeName = routeName,
                            sendErrNotification = notiSender?.sendErrNotification,
                            settings = settings,
                            force = force,
                            sendSuccessNotification = notiSender?.sendSuccessNotification,
                            db = db,
                            masterPassword = masterPassword
                        )
                    }
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?: "unknown err"
                    createAndInsertError(repoFromDb.id, "push by api $routeName err: $errMsg")
                    notiSender?.sendErrNotification?.invoke(repoFromDb.repoName, errMsg, Cons.selectedItem_ChangeList, repoFromDb.id)
                    MyLog.e(TAG, "#$funName: route:$routeName, repoName=${repoFromDb.repoName}, err=${e.stackTraceToString()}")
                }finally {
                    removeNotifySender(repoFromDb.id, sessionId)
                }
            }
        }
    }
    private suspend fun pushSingle(
        repoFromDb: RepoEntity,
        autoCommit: Boolean,
        gitUsernameFromUrl: String,
        gitEmailFromUrl: String,
        routeName: String,
        sendErrNotification: ((title: String, msg: String, startPage: Int, startRepoId: String) -> Unit)?,
        settings: AppSettings,
        force: Boolean,
        sendSuccessNotification: ((title: String?, msg: String?, startPage: Int?, startRepoId: String?) -> Unit)?,
        db: AppContainer,
        masterPassword: String
    ) {
        val funName = "pushSingle"
        val prefix = "push"
        try {
            if (dbIntToBool(repoFromDb.isDetached)) {
                throw RuntimeException("repo is detached")
            }
            if (!Libgit2Helper.isValidGitRepo(repoFromDb.fullSavePath)) {
                throw RuntimeException("invalid git repo")
            }
            Repository.open(repoFromDb.fullSavePath).use { gitRepo ->
                val repoState = gitRepo.state()
                if (repoState != Repository.StateT.NONE) {
                    throw RuntimeException("repository state is '$repoState', expect 'NONE'")
                }
                val upstream = Libgit2Helper.getUpstreamOfBranch(gitRepo, repoFromDb.branch)
                if (upstream.remote.isBlank() || upstream.branchRefsHeadsFullRefSpec.isBlank()) {
                    throw RuntimeException("invalid upstream")
                }
                if (autoCommit) {
                    val (username, email) = if (gitUsernameFromUrl.isNotBlank() && gitEmailFromUrl.isNotBlank()) {
                        Pair(gitUsernameFromUrl, gitEmailFromUrl)
                    } else {
                        val (gitUsernameFromConfig, gitEmailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(gitRepo)
                        val finallyUsername = gitUsernameFromUrl.ifBlank { gitUsernameFromConfig }
                        val finallyEmail = gitEmailFromUrl.ifBlank { gitEmailFromConfig }
                        Pair(finallyUsername, finallyEmail)
                    }
                    if (username == null || username.isBlank() || email == null || email.isBlank()) {
                        val errMsg = "auto commit aborted by username or email invalid"
                        MyLog.d(TAG, "#$funName: api $routeName: $errMsg")
                        val errMsgAndPrefix = "$prefix: $errMsg"
                        sendErrNotification?.invoke(repoFromDb.repoName, errMsgAndPrefix, Cons.selectedItem_ChangeList, repoFromDb.id)
                        createAndInsertError(repoFromDb.id, errMsgAndPrefix)
                    } else {
                        if (Libgit2Helper.hasConflictItemInRepo(gitRepo)) {
                            val errMsg = "auto commit aborted by conflicts"
                            MyLog.d(TAG, "#$funName: api=$routeName, repoName=${repoFromDb.repoName}, err=$errMsg")
                            val errMsgAndPrefix = "$prefix: $errMsg"
                            sendErrNotification?.invoke(repoFromDb.repoName, errMsgAndPrefix, Cons.selectedItem_ChangeList, repoFromDb.id)
                            createAndInsertError(repoFromDb.id, errMsgAndPrefix)
                        } else {
                            Libgit2Helper.stageAll(gitRepo, repoFromDb.id)
                            if (!Libgit2Helper.indexIsEmpty(gitRepo)) {
                                val ret = Libgit2Helper.createCommit(
                                    repo = gitRepo,
                                    msg = "",
                                    username = username,
                                    email = email,
                                    indexItemList = null,
                                    amend = false,
                                    overwriteAuthorWhenAmend = false,
                                    settings = settings,
                                    cleanRepoStateIfSuccess = true,
                                )
                                if (ret.hasError()) {
                                    MyLog.d(TAG, "#$funName: api=$routeName, repoName=${repoFromDb.repoName}, create commit err: ${ret.msg}, exception=${ret.exception?.stackTraceToString()}")
                                    val errMsgAndPrefix = "$prefix: auto commit err: ${ret.msg}"
                                    sendErrNotification?.invoke(repoFromDb.repoName, errMsgAndPrefix, Cons.selectedItem_ChangeList, repoFromDb.id)
                                    createAndInsertError(repoFromDb.id, errMsgAndPrefix)
                                } else if(ret.data != null){
                                    upstream.localOid = ret.data!!.toString()
                                }
                            }
                        }
                    }
                }
                if (!force && upstream.isPublished) {
                    val (ahead, behind) = Libgit2Helper.getAheadBehind(gitRepo, Oid.of(upstream.localOid), Oid.of(upstream.remoteOid))
                    if (behind > 0) {  
                        throw RuntimeException("upstream ahead of local")
                    }
                    if (ahead < 1) {
                        sendSuccessNotification?.invoke(repoFromDb.repoName, "$prefix: Already up-to-date", Cons.selectedItem_ChangeList, repoFromDb.id)
                        return
                    }
                }
                val credential = Libgit2Helper.getRemoteCredential(
                    db.remoteRepository,
                    db.credentialRepository,
                    repoFromDb.id,
                    upstream.remote,
                    trueFetchFalsePush = false,
                    masterPassword = masterPassword
                )
                Libgit2Helper.push(gitRepo, upstream.remote, listOf(upstream.pushRefSpec), credential, force)
                val repoDb = AppModel.dbContainer.repoRepository
                repoDb.updateLastUpdateTime(repoFromDb.id, getSecFromTime())
            }
            sendSuccessNotification?.invoke(repoFromDb.repoName, "push successfully", Cons.selectedItem_ChangeList, repoFromDb.id)
        }catch (e:Exception) {
            throwWithPrefix(prefix, e)
        }
    }
    private fun throwWithPrefix(prefix:String, exception:Exception) {
        val prefixedException = RuntimeException("$prefix: ${exception.localizedMessage ?: "err"}")
        prefixedException.stackTrace = exception.stackTrace
        throw prefixedException
    }
}
