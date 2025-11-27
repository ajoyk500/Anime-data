package com.akcreation.gitsilent.data.entity

import android.content.Context
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.StorageDirCons
import com.akcreation.gitsilent.data.entity.common.BaseFields
import com.akcreation.gitsilent.etc.RepoPendingTask
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.getShortTimeIfPossible
import com.akcreation.gitsilent.utils.getShortUUID
import com.github.git24j.core.Repository

private const val TAG = "RepoEntity"
@Entity(tableName = "repo")
data class RepoEntity(
    @PrimaryKey
    var id: String = getShortUUID(),
    var repoName: String = "",  
    var fullSavePath:String="",  
    var pullRemoteName: String = "",
    var pullRemoteUrl: String = "",
    var pushRemoteName: String = "",
    var pushRemoteUrl: String = "",
    var lastUpdateTime: Long = getSecFromTime(),  
    var workStatus: Int = Cons.dbRepoWorkStatusNotReadyNeedClone,
    var branch: String = "",  
    var lastCommitHash: String = "",  
    var isDetached:Int=Cons.dbCommonFalse,
    var upstreamBranch:String="",  
    @Deprecated("[CHINESE]latestUncheckedErrMsg[CHINESE]Blank[CHINESE]")
    var hasUncheckedErr: Int = Cons.dbCommonFalse,  
    var latestUncheckedErrMsg: String = "",
    var credentialIdForClone: String="",
    var cloneUrl:String="",
    @Deprecated("[CHINESE]")
    var isActive: Int=Cons.dbCommonTrue,
    var createBy:Int = Cons.dbRepoCreateByClone,
    var isRecursiveCloneOn:Int=Cons.dbCommonFalse,
    var createErrMsg:String="",
    var depth:Int=0, 
    var isShallow:Int=Cons.dbCommonFalse,
    var isSingleBranch:Int=Cons.dbCommonFalse,
    var parentRepoId:String="",
    var ahead:Int=0,
    var behind:Int=0,
    @Deprecated("[CHINESE]")
    var storageDirId:String=StorageDirCons.DefaultStorageDir.puppyGitRepos.id,
    @Embedded
    var baseFields: BaseFields=BaseFields(),
    var tmpStatus: String=""
) {
    @Ignore
    var gitRepoState:Repository.StateT? = null
    @Ignore
    var parentRepoName:String=""
    @Ignore
    var parentRepoValid:Boolean=false
    @Ignore
    var otherText:String?=null
    @Ignore
    var pendingTask:RepoPendingTask=RepoPendingTask.NONE
    @Ignore
    var lastCommitHashShort:String? = null  
        private set
        get() {
            val tmp =  field
            if(tmp != null && lastCommitHash.startsWith(tmp)) {
                return tmp
            }
            return Libgit2Helper.getShortOidStrByFull(lastCommitHash).let { field = it; it }
        }
    @Ignore
    var lastCommitDateTime:String=""
        private set
    @Ignore
    private var lastUpdateTimeFormattedCached:String? = null
    @Ignore
    private var cached_OneLineLastestUnCheckedErrMsg:String? = null
    fun getCachedOneLineLatestUnCheckedErrMsg(): String = (cached_OneLineLastestUnCheckedErrMsg ?: Libgit2Helper.zipOneLineMsg(latestUncheckedErrMsg).let { cached_OneLineLastestUnCheckedErrMsg = it; it });
    fun copyAllFields(
        settings: AppSettings,
        newInstance: RepoEntity = copy(),
    ):RepoEntity {
        newInstance.gitRepoState = gitRepoState
        newInstance.parentRepoName = parentRepoName
        newInstance.parentRepoValid = parentRepoValid
        newInstance.otherText = otherText
        newInstance.pendingTask = pendingTask
        newInstance.lastCommitDateTime = lastCommitDateTime
        newInstance.latestCommitMsg = latestCommitMsg
        updateLastCommitHashShort()
        updateCommitDateTime(settings)
        return newInstance
    }
    fun hasOther():Boolean {
        return dbIntToBool(isShallow)
    }
    fun getOther(): String {
        if(otherText == null) {
            otherText = if(dbIntToBool(isShallow)) Cons.isShallowStr else Cons.notShallowStr
        }
        return otherText ?: ""
    }
    fun equalsForSelected(other:RepoEntity):Boolean {
        return id == other.id
    }
    fun getRepoStateStr(context: Context): String {
        return Libgit2Helper.getRepoStateStr(gitRepoState, context)
    }
    fun updateLastCommitHashShort() {
        lastCommitHashShort = Libgit2Helper.getShortOidStrByFull(lastCommitHash)
    }
    fun updateCommitDateTime(settings: AppSettings) {
        try {
            Repository.open(fullSavePath).use { repo ->
                updateCommitDateTimeWithRepo(repo, settings)
            }
        }catch (e: Exception) {
            MyLog.e(TAG, "#updateCommitDateTime: resolve commit hash failed! hash=$lastCommitHash, err=${e.localizedMessage}")
        }
    }
    fun updateCommitDateTimeWithRepo(repo: Repository, settings: AppSettings) {
        lastCommitDateTime = try {
            getShortTimeIfPossible(Libgit2Helper.getSingleCommitSimple(repo, repoId = id, commitOidStr = lastCommitHash, settings).dateTime)
        }catch (e: Exception) {
            MyLog.e(TAG, "#updateCommitDateTimeWithRepo: resolve commit hash failed! hash=$lastCommitHash, err=${e.localizedMessage}")
            ""
        }
    }
    fun cachedLastUpdateTime():String {
        return lastUpdateTimeFormattedCached ?: getShortTimeIfPossible(lastUpdateTime).let { lastUpdateTimeFormattedCached = it; it }
    }
    fun createErrMsgForView(context: Context):String {
        return if(createErrMsg.isEmpty()) {
            ""
        }else {
            context.getString(R.string.error) + ": " + createErrMsg
        }
    }
    @Ignore
    private var cached_appRelatedPath:String? = null
    fun cachedAppRelatedPath() = cached_appRelatedPath ?: FsUtils.getPathWithInternalOrExternalPrefix(fullPath = fullSavePath).let { cached_appRelatedPath = it; it }
    @Ignore
    var latestCommitMsg = ""
    @Ignore
    private var cached_oneLineLatestCommitMsg:String? = null
    fun getOrUpdateCachedOneLineLatestCommitMsg() = cached_oneLineLatestCommitMsg ?: Libgit2Helper.zipOneLineMsg(latestCommitMsg).let { cached_oneLineLatestCommitMsg = it; it }
}
