package com.akcreation.gitsilent.data.repository

import androidx.room.withTransaction
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.data.dao.CredentialDao
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.encrypt.PassEncryptHelper
import com.akcreation.gitsilent.utils.getDomainByUrl
import com.akcreation.gitsilent.utils.getSecFromTime
import kotlinx.coroutines.sync.withLock

private const val TAG = "CredentialRepositoryImpl"
class CredentialRepositoryImpl(private val dao: CredentialDao) : CredentialRepository {
    override suspend fun getAllWithDecrypt(includeNone:Boolean, includeMatchByDomain:Boolean, masterPassword: String): List<CredentialEntity> {
        val all = dao.getAll().toMutableList()
        for(item in all) {
            decryptPassIfNeed(item, masterPassword)
        }
        prependSpecialItemIfNeed(list = all, includeNone = includeNone, includeMatchByDomain = includeMatchByDomain)
        return all
    }
    override suspend fun getAll(includeNone:Boolean, includeMatchByDomain:Boolean): List<CredentialEntity> {
        val list =  dao.getAll().toMutableList()
        prependSpecialItemIfNeed(list = list, includeNone = includeNone, includeMatchByDomain = includeMatchByDomain)
        return list
    }
    private fun prependSpecialItemIfNeed(list: MutableList<CredentialEntity>, includeNone:Boolean, includeMatchByDomain:Boolean) {
        if (includeMatchByDomain) {
            list.add(0, SpecialCredential.MatchByDomain.getEntityCopy())
        }
        if (includeNone) {
            list.add(0, SpecialCredential.NONE.getEntityCopy())
        }
    }
    override suspend fun insertWithEncrypt(item: CredentialEntity, masterPassword: String) {
        val funName = "insertWithEncrypt"
        Cons.credentialInsertLock.withLock {
            if(isCredentialNameExist(item.name)) {
                MyLog.w(TAG, "#insertWithEncrypt(): Credential name exists, item will NOT insert! name is: '${item.name}'")
                throw RuntimeException("#$funName err: name already exists")
            }
            encryptPassIfNeed(item, masterPassword)
            dao.insert(item)
        }
    }
    override suspend fun insert(item: CredentialEntity) {
        val funName = "insert"
        Cons.credentialInsertLock.withLock {
            if (isCredentialNameExist(item.name)) {  
                MyLog.w(TAG, "#insert(): Credential name exists, item will NOT insert! name is:" + item.name)
                throw RuntimeException("#$funName err: name already exists")
            }
            dao.insert(item)
        }
    }
    override suspend fun delete(item: CredentialEntity) = dao.delete(item)
    override suspend fun updateWithEncrypt(item: CredentialEntity, touchTime:Boolean, masterPassword: String) {
        if(SpecialCredential.isAllowedCredentialName(item.name).not()) {
            throw RuntimeException("credential name disallowed (#updateWithEncrypt)")
        }
        encryptPassIfNeed(item, masterPassword)
        if(touchTime) {
            item.baseFields.baseUpdateTime = getSecFromTime()
        }
        dao.update(item)
    }
    override suspend fun update(item: CredentialEntity, touchTime:Boolean) {
        if(SpecialCredential.isAllowedCredentialName(item.name).not()) {
            throw RuntimeException("credential name disallowed (#update)")
        }
        if(touchTime) {
            item.baseFields.baseUpdateTime = getSecFromTime()
        }
        dao.update(item)
    }
    override suspend fun isCredentialNameExist(name: String): Boolean {
        if(SpecialCredential.isAllowedCredentialName(name).not()) {
            return true
        }
        val id = dao.getIdByCredentialName(name)
        return id != null
    }
    override suspend fun getByIdWithDecrypt(id: String, masterPassword: String): CredentialEntity? {
        if(id.isBlank() || id==SpecialCredential.NONE.credentialId) {
            return null
        }
        val item = dao.getById(id)
        if(item == null) {
            return null
        }
        decryptPassIfNeed(item, masterPassword)
        return item
    }
    override suspend fun getByIdAndMatchByDomain(id: String, url: String): CredentialEntity? {
        return getByIdAndMatchByDomainAndDecryptOrNoDecrypt(id, url, decryptPass = false, masterPassword = "")
    }
    override suspend fun getByIdWithDecryptAndMatchByDomain(id: String, url: String, masterPassword: String): CredentialEntity? {
        return getByIdAndMatchByDomainAndDecryptOrNoDecrypt(id, url, decryptPass = true, masterPassword)
    }
    private suspend fun getByIdAndMatchByDomainAndDecryptOrNoDecrypt(id: String, url: String, decryptPass:Boolean, masterPassword: String): CredentialEntity? {
        if(id==SpecialCredential.MatchByDomain.credentialId) {
            val dcDb = AppModel.dbContainer.domainCredentialRepository
            val domain = getDomainByUrl(url)
            if(domain.isNotBlank()) {
                val domainCred = dcDb.getByDomain(domain) ?: return null
                val credId = if(Libgit2Helper.isSshUrl(url)) domainCred.sshCredentialId else domainCred.credentialId
                return if(decryptPass) getByIdWithDecrypt(credId, masterPassword) else getById(credId)
            }else {
                return null
            }
        }else {
            return if(decryptPass) getByIdWithDecrypt(id, masterPassword) else getById(id)
        }
    }
    override suspend fun getById(id: String, includeNone:Boolean, includeMatchByDomain:Boolean): CredentialEntity? {
        if(includeNone && id==SpecialCredential.NONE.credentialId) {
            return SpecialCredential.NONE.getEntityCopy()
        }else if(includeMatchByDomain && id == SpecialCredential.MatchByDomain.credentialId) {
            return SpecialCredential.MatchByDomain.getEntityCopy()
        }else if(id.isBlank()) {
            return null
        }
        return dao.getById(id)
    }
    override suspend fun deleteAndUnlink(item:CredentialEntity) {
        val db = AppModel.dbContainer.db
        val repoDb = AppModel.dbContainer.repoRepository
        val remoteDb = AppModel.dbContainer.remoteRepository
        db.withTransaction {
            remoteDb.updateFetchAndPushCredentialIdByCredentialId(item.id, item.id, "", "")  
            repoDb.unlinkCredentialIdByCredentialId(item.id)  
            delete(item)  
        }
    }
    override fun encryptPassIfNeed(item:CredentialEntity?, masterPassword:String) {
        if(item != null) {
            val curEncryptorVersion = PassEncryptHelper.passEncryptCurrentVer
            if(item.pass.isNotEmpty()) {
                item.pass = PassEncryptHelper.encryptWithSpecifyEncryptorVersion(curEncryptorVersion, item.pass, masterPassword)
            }
            item.encryptVer = curEncryptorVersion
        }
    }
    override fun decryptPassIfNeed(item:CredentialEntity?, masterPassword: String) {
        if (item != null && item.pass.isNotEmpty()) {
            item.pass = PassEncryptHelper.decryptWithSpecifyEncryptorVersion(item.encryptVer, item.pass, masterPassword)
        }
    }
    override suspend fun updateMasterPassword(oldMasterPassword:String, newMasterPassword:String): List<String> {
        if(oldMasterPassword == newMasterPassword) {
            MyLog.w(TAG, "old and new master passwords are the same, cancel update")
            return emptyList()
        }
        val decryptFailedList = mutableListOf<String>()
        val allCredentialList = getAll()
        reEncryptCredentials(
            credentialList = allCredentialList,
            oldMasterPassword = oldMasterPassword,
            newMasterPassword = newMasterPassword,
            decryptFailedCallback = { cred, exception ->
                decryptFailedList.add(cred.name)
            }
        )
        return decryptFailedList
    }
    private suspend fun reEncryptCredentials(
        credentialList: List<CredentialEntity>,
        oldMasterPassword: String,
        newMasterPassword: String,
        decryptFailedCallback: (failedCredential:CredentialEntity, exception:Exception) -> Unit,
    ) {
        AppModel.dbContainer.db.withTransaction {
            for (c in credentialList) {
                if (c.pass.isEmpty()) {
                    c.encryptVer = PassEncryptHelper.passEncryptCurrentVer
                    update(c, touchTime = false)
                    continue
                }
                try {
                    decryptPassIfNeed(c, oldMasterPassword)  
                } catch (e: Exception) {
                    MyLog.w(TAG, "decrypt password failed, credentialName=${c.name}, err=${e.localizedMessage}")
                    decryptFailedCallback(c, e)
                    continue
                }
                updateWithEncrypt(c, touchTime = false, masterPassword = newMasterPassword)
            }
        }
    }
    override suspend fun migrateEncryptVerIfNeed(masterPassword: String) {
        val needUpdateEncryptVerCredList = getByEncryptVerNotEqualsTo(PassEncryptHelper.passEncryptCurrentVer)
        if(needUpdateEncryptVerCredList.isEmpty()) {
            return
        }
        val failedList = mutableListOf<CredentialEntity>()
        reEncryptCredentials(
            needUpdateEncryptVerCredList,
            masterPassword,
            masterPassword,
            decryptFailedCallback = { c, e ->
                failedList.add(c)
            }
        )
        if(failedList.isNotEmpty()) {
            val split = ", "
            val sb = StringBuilder()
            for(i in failedList) {
                sb.append("(").append("name=${i.name}, ").append("encryptVer=${i.encryptVer}").append(")").append(split)
            }
            MyLog.e(TAG, "#migrateEncryptVerIfNeed: these credentials migrate failed: ${sb.removeSuffix(split)}")
        }
    }
    override suspend fun getByEncryptVerNotEqualsTo(encryptVer:Int): List<CredentialEntity> {
        return dao.getByEncryptVerNotEqualsTo(encryptVer)
    }
    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }
}
