
package com.akcreation.gitsilent.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.akcreation.gitsilent.data.entity.DomainCredentialEntity
import com.akcreation.gitsilent.dto.DomainCredentialDto

@Dao
interface DomainCredentialDao {
    @Query("SELECT * from domain_credential order by baseCreateTime DESC")
    suspend fun getAll(): List<DomainCredentialEntity>
    @Query("SELECT d.id as domainCredId, d.domain as domain, c.name as credName, c.id as credId, sshc.name as sshCredName, sshc.id as sshCredId from domain_credential d left join credential c on c.id= d.credentialId left join credential sshc on sshc.id=d.sshCredentialId order by d.baseCreateTime DESC")
    suspend fun getAllDto(): List<DomainCredentialDto>
    @Query("select * from domain_credential where domain=:domain LIMIT 1")
    suspend fun getByDomain(domain:String):DomainCredentialEntity?
    @Insert
    suspend fun insert(item: DomainCredentialEntity)
    @Update
    suspend fun update(item: DomainCredentialEntity)
    @Delete
    suspend fun delete(item: DomainCredentialEntity)
    @Query("SELECT * from domain_credential WHERE id = :id")
    fun getById(id: String): DomainCredentialEntity?
    @Query("UPDATE domain_credential set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
