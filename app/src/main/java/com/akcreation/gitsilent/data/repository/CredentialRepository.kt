
package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.utils.AppModel

interface CredentialRepository {
    suspend fun getAllWithDecrypt(includeNone:Boolean = false, includeMatchByDomain:Boolean = false, masterPassword: String = AppModel.masterPassword.value): List<CredentialEntity>
    suspend fun getAll(includeNone:Boolean = false, includeMatchByDomain:Boolean = false): List<CredentialEntity>
    suspend fun insertWithEncrypt(item: CredentialEntity, masterPassword: String = AppModel.masterPassword.value)
    suspend fun insert(item: CredentialEntity)
    suspend fun delete(item: CredentialEntity)
    suspend fun updateWithEncrypt(item: CredentialEntity, touchTime: Boolean = true, masterPassword: String = AppModel.masterPassword.value)
    suspend fun update(item: CredentialEntity, touchTime: Boolean = true)
    suspend fun isCredentialNameExist(name: String): Boolean
    suspend fun getByIdWithDecrypt(id: String, masterPassword: String = AppModel.masterPassword.value): CredentialEntity?
    suspend fun getByIdWithDecryptAndMatchByDomain(id: String, url:String, masterPassword: String = AppModel.masterPassword.value): CredentialEntity?
    suspend fun getByIdAndMatchByDomain(id: String, url:String): CredentialEntity?
    suspend fun getById(id: String, includeNone:Boolean = false, includeMatchByDomain:Boolean = false): CredentialEntity?
    suspend fun deleteAndUnlink(item:CredentialEntity)
    fun encryptPassIfNeed(item:CredentialEntity?, masterPassword:String)
    fun decryptPassIfNeed(item:CredentialEntity?, masterPassword:String)
    suspend fun updateMasterPassword(oldMasterPassword:String, newMasterPassword:String):List<String>
    suspend fun migrateEncryptVerIfNeed(masterPassword: String)
    suspend fun getByEncryptVerNotEqualsTo(encryptVer:Int):List<CredentialEntity>
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
