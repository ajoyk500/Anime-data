
package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.ImportRepoResult
import kotlinx.coroutines.flow.Flow

interface RepoRepository {
    fun getAllStream(): Flow<List<RepoEntity?>>
    fun getStream(id: String): Flow<RepoEntity?>
    suspend fun insert(item: RepoEntity)
    suspend fun delete(item: RepoEntity, requireDelFilesOnDisk:Boolean=false, requireTransaction: Boolean=true)
    suspend fun getIdByRepoNameAndExcludeId(repoName:String, excludeId:String): String?
    suspend fun isRepoNameAlreadyUsedByOtherItem(repoName:String, excludeId:String): Boolean
    suspend fun update(item: RepoEntity, requeryAfterUpdate:Boolean=true)
    suspend fun isRepoNameExist(repoName:String): Boolean
    suspend fun getById(id:String): RepoEntity?
    suspend fun getByName(name:String): RepoEntity?
    suspend fun getByNameOrId(repoNameOrId:String, forceUseIdMatchRepo:Boolean): Ret<RepoEntity?>
    suspend fun getByFullSavePath(fullSavePath:String, onlyReturnReadyRepo:Boolean=false, requireSyncRepoInfoWithGit:Boolean=true): RepoEntity?
    suspend fun getByIdNoSyncWithGit(id:String): RepoEntity?
    suspend fun getAll(updateRepoInfo:Boolean = true): List<RepoEntity>
    suspend fun cloneDoneUpdateRepoAndCreateRemote(item: RepoEntity)
    suspend fun getAReadyRepo():RepoEntity?
    suspend fun getReadyRepoList(requireSyncRepoInfoWithGit:Boolean=true):List<RepoEntity>
    suspend fun updateCredentialIdByCredentialId(oldCredentialIdForClone:String, newCredentialIdForClone:String)
    suspend fun unlinkCredentialIdByCredentialId(credentialIdForClone:String)
    suspend fun updateErrFieldsById(repoId:String, hasUncheckedErr:Int, latestUncheckedErrMsg:String)
    suspend fun checkedAllErrById(repoId:String)
    suspend fun setNewErrMsg(repoId:String, errMsg:String)
    suspend fun updateBranchAndCommitHash(repoId:String, branch:String, lastCommitHash:String, isDetached:Int, upstreamBranch:String)
    suspend fun updateDetachedAndCommitHash(repoId:String, lastCommitHash:String, isDetached:Int)
    suspend fun updateCommitHash(repoId:String, lastCommitHash:String)
    suspend fun updateUpstream(repoId:String, upstreamBranch: String)
    suspend fun updateLastUpdateTime(repoId:String, lastUpdateTime:Long)
    suspend fun updateIsShallow(repoId:String, isShallow:Int)
    suspend fun getByStorageDirId(storageDirId:String): List<RepoEntity>
    suspend fun deleteByStorageDirId(storageDirId:String)
    suspend fun importRepos(dir: String, isReposParent: Boolean, repoNamePrefix:String="", repoNameSuffix:String="", parentRepoId:String?=null, credentialId:String?=null): ImportRepoResult
    suspend fun isGoodRepoName(name:String):Boolean
    suspend fun updateRepoName(repoId:String, name: String)
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
