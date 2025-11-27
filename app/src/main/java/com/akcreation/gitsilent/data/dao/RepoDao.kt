
package com.akcreation.gitsilent.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akcreation.gitsilent.data.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * from repo ORDER BY baseCreateTime DESC")
    fun getAllStream(): Flow<List<RepoEntity?>>
    @Query("SELECT * from repo WHERE id = :id")
    fun getStream(id: String): Flow<RepoEntity?>
    @Insert
    suspend fun insert(item: RepoEntity)
    @Update
    suspend fun update(item: RepoEntity)
    @Delete
    suspend fun delete(item: RepoEntity)
    @Query("SELECT id from repo where repoName=:repoName LIMIT 1")
    suspend fun getIdByRepoName(repoName:String): String?
    @Query("SELECT id from repo where id!=:excludeId and repoName=:repoName LIMIT 1")
    suspend fun getIdByRepoNameAndExcludeId(repoName:String, excludeId:String): String?
    @Query("SELECT * from repo where id=:id")
    suspend fun getById(id:String): RepoEntity?
    @Query("SELECT * from repo where repoName=:name")
    suspend fun getByName(name:String): List<RepoEntity>
    @Query("SELECT * from repo where fullSavePath=:fullSavePath LIMIT 1")
    suspend fun getByFullSavePath(fullSavePath:String): RepoEntity?
    @Query("SELECT * from repo where storageDirId=:storageDirId ORDER BY baseCreateTime DESC")
    suspend fun getByStorageDirId(storageDirId:String): List<RepoEntity>
    @Query("delete from repo WHERE storageDirId = :storageDirId")
    suspend fun deleteByStorageDirId(storageDirId:String)
    @Query("SELECT * from repo ORDER BY baseCreateTime DESC")
    suspend fun getAll(): List<RepoEntity>
    @Query("update repo set credentialIdForClone = :newCredentialIdForClone where credentialIdForClone = :oldCredentialIdForClone")
    suspend fun updateCredentialIdByCredentialId(oldCredentialIdForClone:String, newCredentialIdForClone:String)
    @Query("update repo set hasUncheckedErr = :hasUncheckedErr , latestUncheckedErrMsg = :latestUncheckedErrMsg where id = :repoId")
    suspend fun updateErrFieldsById(repoId:String, hasUncheckedErr:Int, latestUncheckedErrMsg:String)
    @Query("update repo set branch = :branch , lastCommitHash = :lastCommitHash , isDetached=:isDetached, upstreamBranch=:upstreamBranch where id = :repoId")
    suspend fun updateBranchAndCommitHash(repoId:String, branch:String, lastCommitHash:String, isDetached:Int, upstreamBranch:String)
    @Query("update repo set lastCommitHash = :lastCommitHash , isDetached=:isDetached where id = :repoId")
    suspend fun updateDetachedAndCommitHash(repoId:String, lastCommitHash:String, isDetached:Int)
    @Query("update repo set lastCommitHash = :lastCommitHash where id = :repoId")
    suspend fun updateCommitHash(repoId:String, lastCommitHash:String)
    @Query("update repo set upstreamBranch = :upstreamBranch where id = :repoId")
    suspend fun updateUpstream(repoId:String, upstreamBranch: String)
    @Query("update repo set lastUpdateTime = :lastUpdateTime where id = :repoId")
    suspend fun updateLastUpdateTime(repoId:String, lastUpdateTime:Long)
    @Query("update repo set isShallow = :isShallow where id = :repoId")
    suspend fun updateIsShallow(repoId:String, isShallow:Int)
    @Query("update repo set repoName = :name where id = :repoId")
    suspend fun updateRepoName(repoId:String, name: String)
    @Query("UPDATE repo set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec), lastUpdateTime = lastUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
