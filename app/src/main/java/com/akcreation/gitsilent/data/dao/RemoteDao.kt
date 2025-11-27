
package com.akcreation.gitsilent.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.akcreation.gitsilent.data.entity.RemoteEntity
import com.akcreation.gitsilent.dto.RemoteDto
import com.akcreation.gitsilent.dto.RemoteDtoForCredential
import kotlinx.coroutines.flow.Flow

@Dao
interface RemoteDao {
    @Query("SELECT * from remote ORDER BY id ASC")
    fun getAllStream(): Flow<List<RemoteEntity?>>
    @Query("SELECT * from remote WHERE id = :id")
    fun getStream(id: String): Flow<RemoteEntity?>
    @Query("SELECT * from remote WHERE id = :id")
    fun getById(id: String): RemoteEntity?
    @Insert
    suspend fun insert(item: RemoteEntity)
    @Update
    suspend fun update(item: RemoteEntity)
    @Delete
    suspend fun delete(item: RemoteEntity)
    @Query("DELETE FROM remote WHERE repoId = :repoId")
    suspend fun deleteByRepoId(repoId: String)
    @Query("SELECT * from remote WHERE repoId = :repoId and remoteName = :remoteName")
    suspend fun getByRepoIdAndRemoteName(repoId: String, remoteName:String): RemoteEntity?
    @Query("select rem.remoteName as remoteName, rem.id as remoteId, rep.id as repoId, rep.repoName as repoName, cre.name as credentialName, rem.credentialId as credentialId, cre.type as credentialType, rem.pushCredentialId as pushCredentialId, pushCre.name as pushCredentialName, pushCre.type as pushCredentialType from remote as rem left join credential as cre on rem.credentialId=cre.id left join credential as pushCre on rem.pushCredentialId=pushCre.id left join repo as rep on rep.id = rem.repoId where rem.credentialId = :credentialId or rem.pushCredentialId = :credentialId order by rem.baseCreateTime DESC")
    suspend fun getLinkedRemoteDtoForCredentialList(credentialId:String):List<RemoteDtoForCredential>
    @Query("select rem.remoteName as remoteName, rem.id as remoteId, rep.id as repoId, rep.repoName as repoName, cre.name as credentialName, rem.credentialId as credentialId, cre.type as credentialType, rem.pushCredentialId as pushCredentialId, pushCre.name as pushCredentialName, pushCre.type as pushCredentialType from remote as rem left join credential as cre on rem.credentialId=cre.id left join credential as pushCre on rem.pushCredentialId=pushCre.id left join repo as rep on rep.id = rem.repoId where rem.credentialId != :credentialId or rem.pushCredentialId != :credentialId order by rem.baseCreateTime DESC")
    suspend fun getUnlinkedRemoteDtoForCredentialList(credentialId:String):List<RemoteDtoForCredential>
    @Query("update remote set credentialId = :credentialId where id = :remoteId")
    suspend fun updateCredentialIdByRemoteId(remoteId:String, credentialId:String)
    @Query("update remote set credentialId = :newCredentialId where credentialId = :oldCredentialId")
    suspend fun updateCredentialIdByCredentialId(oldCredentialId:String, newCredentialId:String)
    @Query("update remote set pushCredentialId = :credentialId where id = :remoteId")
    suspend fun updatePushCredentialIdByRemoteId(remoteId:String, credentialId:String)
    @Query("update remote set pushCredentialId = :newCredentialId where pushCredentialId = :oldCredentialId")
    suspend fun updatePushCredentialIdByCredentialId(oldCredentialId:String, newCredentialId:String)
    @Query("update remote set credentialId = :fetchCredentialId , pushCredentialId = :pushCredentialId")
    suspend fun updateAllFetchAndPushCredentialId(fetchCredentialId: String, pushCredentialId: String)
    @Query("update remote set credentialId = :fetchCredentialId")
    suspend fun updateAllFetchCredentialId(fetchCredentialId: String)
    @Query("update remote set pushCredentialId = :pushCredentialId")
    suspend fun updateAllPushCredentialId(pushCredentialId: String)
    @Query("select rem.remoteName as remoteName, rem.id as remoteId, rem.remoteUrl as remoteUrl, rem.credentialId as credentialId, rep.id as repoId, rep.repoName as repoName, cre.name as credentialName, cre.value as credentialVal, cre.pass as credentialPass, cre.type as credentialType, rem.pushUrl as pushUrl, rem.pushCredentialId as pushCredentialId, pushCre.name as pushCredentialName, pushCre.value as pushCredentialVal, pushCre.pass as pushCredentialPass, pushCre.type as pushCredentialType from remote as rem left join credential as cre on rem.credentialId=cre.id left join credential as pushCre on rem.pushCredentialId=pushCre.id left join repo as rep on rep.id = rem.repoId where rem.repoId = :repoId order by rem.baseCreateTime DESC")
    suspend fun getRemoteDtoListByRepoId(repoId: String):List<RemoteDto>
    @Query("update or abort remote set remoteUrl = :remoteUrl where id = :id")
    suspend fun updateRemoteUrlById(id:String, remoteUrl:String)
    @Query("update remote set pushUrl = :url where id = :id")
    suspend fun updatePushUrlById(id:String, url:String)
    @Query("UPDATE remote set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
