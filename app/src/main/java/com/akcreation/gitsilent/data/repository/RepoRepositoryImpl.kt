
package com.akcreation.gitsilent.data.repository

import androidx.room.withTransaction
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.dao.RepoDao
import com.akcreation.gitsilent.data.entity.RemoteEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.ImportRepoResult
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.getNowInSecFormatted
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.isRepoReadyAndPathExist
import com.akcreation.gitsilent.utils.strHasIllegalChars
import com.github.git24j.core.Repository
import kotlinx.coroutines.flow.Flow
import java.io.File

private const val TAG = "RepoRepositoryImpl"
class RepoRepositoryImpl(private val dao: RepoDao) : RepoRepository {
    @Deprecated("[CHINESE]")
    override fun getAllStream(): Flow<List<RepoEntity?>> = dao.getAllStream()
    @Deprecated("[CHINESE]")
    override fun getStream(id: String): Flow<RepoEntity?> = dao.getStream(id)
    override suspend fun insert(item: RepoEntity) {
        val funName = "insert"
        if(isRepoNameExist(item.repoName)) {
            MyLog.w(TAG, "#$funName: warn: item's repoName '${item.repoName}' already exists! operation abort...")
            throw RuntimeException("#$funName err: repoName already exists")
        }
        item.fullSavePath = File(item.fullSavePath).canonicalPath
        item.createErrMsg = addTimeStampIfErrMsgIsNotBlank(item.createErrMsg)
        item.latestUncheckedErrMsg = addTimeStampIfErrMsgIsNotBlank(item.latestUncheckedErrMsg)
        val timeNowInSec = getSecFromTime()
        item.baseFields.baseCreateTime = timeNowInSec
        item.baseFields.baseUpdateTime = timeNowInSec
        val tmpStatus = item.tmpStatus
        item.tmpStatus=""
        dao.insert(item)
        item.tmpStatus = tmpStatus
    }
    private fun addTimeStampIfErrMsgIsNotBlank(errMsg:String):String {
        val timestampSuffix = ",timestamp)"
        return if(errMsg.isBlank() || errMsg.endsWith(timestampSuffix)) errMsg else "$errMsg (${getNowInSecFormatted()}$timestampSuffix"
    }
    override suspend fun delete(item: RepoEntity, requireDelFilesOnDisk:Boolean, requireTransaction: Boolean) {
        MyLog.d(TAG, "will delete repo, repoId=${item.id}, repoFullPath=${item.fullSavePath}")
        val repoFullPath = item.fullSavePath  
        val errDb = AppModel.dbContainer.errorRepository
        val remoteDb = AppModel.dbContainer.remoteRepository
        val act = suspend {
            errDb.deleteByRepoId(item.id)  
            remoteDb.deleteByRepoId(item.id)  
            dao.delete(item)  
        }
        if(requireTransaction) {
            AppModel.dbContainer.db.withTransaction {
                act()
            }
        }else {
            act()
        }
        if(requireDelFilesOnDisk) {
            Libgit2Helper.removeRepoFiles(repoFullPath, createEmptyWorkDirAfterRemove = item.parentRepoId.isNotBlank())
        }
        MyLog.d(TAG, "success delete repo, repoId=${item.id}, repoFullPath=${repoFullPath}")
    }
    override suspend fun update(item: RepoEntity, requeryAfterUpdate:Boolean) {
        val funName ="update"
        if(isRepoNameAlreadyUsedByOtherItem(item.repoName, item.id)) {
            MyLog.w(TAG, "#$funName: warn: item's repoName '${item.repoName}' already used by other item! operation abort...")
            throw RuntimeException("#$funName err: repoName already exists")
        }
        item.fullSavePath = File(item.fullSavePath).canonicalPath
        item.createErrMsg = addTimeStampIfErrMsgIsNotBlank(item.createErrMsg)
        item.latestUncheckedErrMsg = addTimeStampIfErrMsgIsNotBlank(item.latestUncheckedErrMsg)
        item.baseFields.baseUpdateTime = getSecFromTime()
        val tmpStatus = item.tmpStatus
        item.tmpStatus=""
        dao.update(item)
        item.tmpStatus = tmpStatus
        if(requeryAfterUpdate){
            Libgit2Helper.updateRepoInfo(item)
        }
    }
    override suspend fun isRepoNameExist(repoName: String): Boolean {
        return dao.getIdByRepoName(repoName) != null
    }
    override suspend fun getById(id: String): RepoEntity? {
        if(id.isEmpty()) {
            return null
        }
        val repoFromDb = dao.getById(id)?:return null
        Libgit2Helper.updateRepoInfo(repoFromDb)
        return repoFromDb
    }
    override suspend fun getByName(name: String): RepoEntity? {
        if(name.isEmpty()) {
            return null
        }
        val items = dao.getByName(name)
        return if(items.isEmpty()) {
            null
        }else {
            val repoFromDb = items.get(0)
            Libgit2Helper.updateRepoInfo(repoFromDb)
            repoFromDb
        }
    }
    override suspend fun getByNameOrId(repoNameOrId: String, forceUseIdMatchRepo: Boolean): Ret<RepoEntity?> {
        if(repoNameOrId.isEmpty()) {
            return Ret.createError(null, "repoNameOrId is empty")
        }
        val repo = if (forceUseIdMatchRepo) {
            val repo = getById(repoNameOrId)
            if (repo == null) {
                return Ret.createError(null, "no repo matched the id '$repoNameOrId'")
            }
            repo
        } else {
            var repo = getByName(repoNameOrId)
            if (repo == null) {
                repo = getById(repoNameOrId)
                if (repo == null) {
                    return Ret.createError(null, "no repo matched the name or id '$repoNameOrId'")
                }
            }
            repo
        }
        return Ret.createSuccess(repo)
    }
    override suspend fun getByFullSavePath(fullSavePath: String, onlyReturnReadyRepo:Boolean, requireSyncRepoInfoWithGit:Boolean): RepoEntity? {
        val repo = dao.getByFullSavePath(fullSavePath)
        if(repo==null) {
            return null
        }
        if(onlyReturnReadyRepo && repoIsNotReady(repo)) {
            return null
        }
        if(requireSyncRepoInfoWithGit) {
            Libgit2Helper.updateRepoInfo(repo)
        }
        return repo
    }
    override suspend fun getByIdNoSyncWithGit(id: String): RepoEntity? {
        return dao.getById(id)
    }
    override suspend fun getAll(updateRepoInfo:Boolean): List<RepoEntity> {
        val list = dao.getAll()
        if(updateRepoInfo) {
            val settings = SettingsUtil.getSettingsSnapshot()
            list.forEachBetter {
                Libgit2Helper.updateRepoInfo(it, settings = settings)
            }
        }
        return list
    }
    override suspend fun cloneDoneUpdateRepoAndCreateRemote(item: RepoEntity) {
        val remoteRepository = AppModel.dbContainer.remoteRepository
        val db = AppModel.dbContainer.db
        val remoteForSave = RemoteEntity(
            remoteName = item.pullRemoteName,
            remoteUrl = item.pullRemoteUrl,
            isForPull = Cons.dbCommonTrue,
            isForPush = Cons.dbCommonTrue,
            credentialId = item.credentialIdForClone,
            pushCredentialId = item.credentialIdForClone,
            repoId = item.id,
        )
        if(dbIntToBool(item.isSingleBranch)) {
            remoteForSave.fetchMode=Cons.dbRemote_Fetch_BranchMode_SingleBranch
            remoteForSave.singleBranch=item.branch
        }
        db.withTransaction {
            remoteRepository.deleteByRepoId(item.id)  
            remoteRepository.insert(remoteForSave)
            update(item)
        }
    }
    override suspend fun getAReadyRepo(): RepoEntity? {
        val repos = getAll()
        if(repos.isEmpty()) {
            return null
        }
        for(r in repos) {
            if (repoIsReady(r)){
                Libgit2Helper.updateRepoInfo(r)
                return r
            }
        }
        return null;
    }
    override suspend fun getReadyRepoList(requireSyncRepoInfoWithGit:Boolean): List<RepoEntity> {
        val repoList = mutableListOf<RepoEntity>()
        val repos = getAll()
        val settings = SettingsUtil.getSettingsSnapshot()
        for(r in repos) {
            if (repoIsReady(r)) {
                if(requireSyncRepoInfoWithGit) {
                    Libgit2Helper.updateRepoInfo(r, settings = settings)
                }
                repoList.add(r)
            }
        }
        return repoList;
    }
    override suspend fun updateCredentialIdByCredentialId(
        oldCredentialIdForClone: String,
        newCredentialIdForClone: String
    ) {
        dao.updateCredentialIdByCredentialId(oldCredentialIdForClone,newCredentialIdForClone)
    }
    override suspend fun unlinkCredentialIdByCredentialId(credentialIdForClone: String) {
        updateCredentialIdByCredentialId(credentialIdForClone, "")
    }
    override suspend fun updateErrFieldsById(
        repoId: String,
        hasUncheckedErr: Int,
        latestUncheckedErrMsg: String
    ) {
        dao.updateErrFieldsById(
            repoId,
            hasUncheckedErr,  
            addTimeStampIfErrMsgIsNotBlank(latestUncheckedErrMsg)
        )
    }
    override suspend fun checkedAllErrById(repoId: String) {
        updateErrFieldsById(repoId, Cons.dbCommonFalse, "")
        val errDb = AppModel.dbContainer.errorRepository
        errDb.updateIsCheckedByRepoId(repoId, Cons.dbCommonTrue)
    }
    override suspend fun setNewErrMsg(repoId: String, errMsg: String) {
        updateErrFieldsById(repoId, Cons.dbCommonTrue, errMsg)
    }
    override suspend fun updateBranchAndCommitHash(
        repoId: String,
        branch: String,
        lastCommitHash: String,
        isDetached:Int,
        upstreamBranch:String
    ) {
        dao.updateBranchAndCommitHash(repoId, branch, lastCommitHash, isDetached, upstreamBranch)
    }
    override suspend fun updateDetachedAndCommitHash(
        repoId: String,
        lastCommitHash: String,
        isDetached: Int
    ) {
        dao.updateDetachedAndCommitHash(repoId, lastCommitHash, isDetached)
    }
    override suspend fun updateCommitHash(repoId: String, lastCommitHash: String) {
        dao.updateCommitHash(repoId,lastCommitHash)
    }
    override suspend fun updateUpstream(repoId:String, upstreamBranch: String) {
        dao.updateUpstream(repoId, upstreamBranch)
    }
    override suspend fun updateLastUpdateTime(repoId: String, lastUpdateTime: Long) {
        dao.updateLastUpdateTime(repoId,lastUpdateTime)
    }
    override suspend fun updateIsShallow(repoId: String, isShallow: Int) {
        dao.updateIsShallow(repoId, isShallow)
    }
    override suspend fun getByStorageDirId(storageDirId: String): List<RepoEntity> {
        return dao.getByStorageDirId(storageDirId)
    }
    override suspend fun deleteByStorageDirId(storageDirId: String) {
        dao.deleteByStorageDirId(storageDirId)
    }
    override suspend fun importRepos(
        dir: String,
        isReposParent: Boolean,
        repoNamePrefix:String,
        repoNameSuffix:String,
        parentRepoId:String?,
        credentialId:String?,
    ): ImportRepoResult {
        val repos = getAll(updateRepoInfo = false).toMutableList()
        var all = 0  
        var success=0  
        var existed = 0  
        var failed = 0  
        if(isReposParent) {  
            val subdirs = File(dir).listFiles { it -> it.isDirectory }
            if(subdirs!=null && subdirs.isNotEmpty()) {
                subdirs.forEachBetter { sub ->
                    try {
                        Repository.open(sub.canonicalPath).use { repo->
                            all++
                            val repoWorkDirPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                            if(repos.indexOfFirst {it.fullSavePath == repoWorkDirPath} != -1) {
                                existed++
                            }else {
                                val importSuccess = importSingleRepo(
                                    repo = repo,
                                    repoWorkDirPath = repoWorkDirPath,
                                    initRepoName = repoNamePrefix + sub.name + repoNameSuffix,
                                    addRepoToThisListIfSuccess = repos,
                                    parentRepoId=parentRepoId,
                                    credentialId=credentialId
                                )
                                if(importSuccess) {
                                    success++
                                }else {
                                    failed++
                                }
                            }
                        }
                    }catch (_:Exception) {
                    }
                }
            }
        }else {  
            val dirFile = File(dir)
            try {
                Repository.open(dirFile.canonicalPath).use {repo ->
                    all=1
                    val repoWorkdirPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                    if(repos.indexOfFirst {it.fullSavePath == repoWorkdirPath} != -1) {  
                        existed = 1
                    }else { 
                        val importSuccess = importSingleRepo(
                            repo = repo,
                            repoWorkDirPath = repoWorkdirPath,
                            initRepoName = repoNamePrefix + dirFile.name + repoNameSuffix,
                            parentRepoId=parentRepoId,
                            credentialId = credentialId
                        )
                        if(importSuccess) {
                            success = 1
                        }else {
                            failed = 1
                        }
                    }
                }
            }catch (_:Exception) {
            }
        }
        return ImportRepoResult(all=all, success=success, existed=existed, failed=failed)
    }
    private suspend fun importSingleRepo(
        repo:Repository,
        repoWorkDirPath:String,
        initRepoName:String,
        addRepoToThisListIfSuccess:MutableList<RepoEntity>?=null,
        parentRepoId: String?,
        credentialId:String?,
    ):Boolean {
        val funName = "importSingleRepo"
        try {
            var repoName = initRepoName
            if(isRepoNameExist(repoName)) {
                repoName = initRepoName+ "_"+getShortUUID(6)
                if(isRepoNameExist(repoName)) {
                    repoName = initRepoName+ "_"+getShortUUID(8)
                    if(isRepoNameExist(repoName)) {
                        repoName = initRepoName+ "_"+getShortUUID(10)
                        if(isRepoNameExist(repoName)) {
                            repoName = initRepoName+ "_"+getShortUUID(12)
                            if(isRepoNameExist(repoName)) {
                                repoName = initRepoName+ "_"+getShortUUID(16)
                            }
                        }
                    }
                }
            }
            val repoEntity = RepoEntity()
            val remoteEntityList = mutableListOf<RemoteEntity>()
            repoEntity.repoName = repoName
            repoEntity.fullSavePath = repoWorkDirPath
            repoEntity.workStatus = Cons.dbRepoWorkStatusUpToDate
            repoEntity.createBy = Cons.dbRepoCreateByImport
            if(parentRepoId != null) {
                repoEntity.parentRepoId = parentRepoId
            }
            val remotes = Libgit2Helper.getRemoteList(repo)
            remotes.forEachBetter { remoteName ->
                val remoteEntity = RemoteEntity()
                remoteEntity.remoteName = remoteName
                remoteEntity.repoId = repoEntity.id
                if(credentialId!=null) {
                    remoteEntity.credentialId = credentialId
                    remoteEntity.pushCredentialId = credentialId
                }
                remoteEntityList.add(remoteEntity)
            }
            val remoteDb = AppModel.dbContainer.remoteRepository
            AppModel.dbContainer.db.withTransaction {
                insert(repoEntity)
                remoteEntityList.forEachBetter {remote ->
                    remoteDb.insert(remote)
                }
            }
            try {
                addRepoToThisListIfSuccess?.add(repoEntity)
            }catch (addToListException:Exception) {
                MyLog.e(TAG, "#$funName: err when add repo to list, err=${addToListException.localizedMessage}")
            }
            return true
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: import repo err, err=${e.stackTraceToString()}")
            return false
        }
    }
    override suspend fun isGoodRepoName(name: String): Boolean {
        return !strHasIllegalChars(name) && !isRepoNameExist(name)
    }
    override suspend fun updateRepoName(repoId:String, name: String) {
        dao.updateRepoName(repoId, name)
    }
    private fun repoIsReady(repoEntity: RepoEntity?): Boolean {
        return isRepoReadyAndPathExist(repoEntity)
    }
    private fun repoIsNotReady(repoEntity: RepoEntity?) = repoIsReady(repoEntity).not();
    override suspend fun getIdByRepoNameAndExcludeId(repoName: String, excludeId: String): String? {
        return dao.getIdByRepoNameAndExcludeId(repoName, excludeId)
    }
    override suspend fun isRepoNameAlreadyUsedByOtherItem(repoName: String, excludeId: String): Boolean {
        return getIdByRepoNameAndExcludeId(repoName, excludeId) != null
    }
    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }
}
