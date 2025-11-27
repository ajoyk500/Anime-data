
package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.data.entity.RemoteEntity
import com.akcreation.gitsilent.dto.RemoteDto
import com.akcreation.gitsilent.dto.RemoteDtoForCredential
import kotlinx.coroutines.flow.Flow

interface RemoteRepository {
    suspend fun getById(id: String): RemoteEntity?
    suspend fun insert(item: RemoteEntity)
    suspend fun delete(item: RemoteEntity)
    suspend fun deleteByRepoId(repoId: String)
    suspend fun update(item: RemoteEntity)
    suspend fun getByRepoIdAndRemoteName(repoId: String, remoteName:String): RemoteEntity?
    suspend fun getLinkedRemoteDtoForCredentialList(credentialId:String):List<RemoteDtoForCredential>
    suspend fun getUnlinkedRemoteDtoForCredentialList(credentialId:String):List<RemoteDtoForCredential>
    suspend fun updateCredentialIdByRemoteId(remoteId:String,credentialId:String)
    suspend fun updateCredentialIdByCredentialId(oldCredentialId:String,newCredentialId:String)
    suspend fun linkCredentialIdByRemoteId(remoteId:String,credentialId:String)
    suspend fun unlinkCredentialIdByRemoteId(remoteId:String)
    suspend fun unlinkAllCredentialIdByCredentialId(credentialId:String)
    suspend fun getRemoteDtoListByRepoId(repoId: String):List<RemoteDto>
    suspend fun updateRemoteUrlById(id:String, remoteUrl:String, requireTransaction: Boolean = true)
    suspend fun updatePushUrlById(id:String, url:String)
    suspend fun updatePushCredentialIdByRemoteId(remoteId:String, credentialId:String)
    suspend fun updatePushCredentialIdByCredentialId(oldCredentialId:String, newCredentialId:String)
    suspend fun updateFetchAndPushCredentialIdByRemoteId(remoteId:String, fetchCredentialId:String, pushCredentialId:String)
    suspend fun updateFetchAndPushCredentialIdByCredentialId(oldFetchCredentialId: String, oldPushCredentialId: String, newFetchCredentialId:String, newPushCredentialId:String)
    suspend fun subtractTimeOffset(offsetInSec:Long)
    suspend fun updateAllFetchAndPushCredentialId(fetchCredentialId: String, pushCredentialId: String)
    suspend fun updateAllFetchCredentialId(fetchCredentialId: String)
    suspend fun updateAllPushCredentialId(pushCredentialId: String)
}
