
package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.data.entity.DomainCredentialEntity
import com.akcreation.gitsilent.dto.DomainCredentialDto

interface DomainCredentialRepository {
    suspend fun getAll(): List<DomainCredentialEntity>
    suspend fun getAllDto(): List<DomainCredentialDto>
    suspend fun isDomainExist(domain:String):Boolean
    suspend fun getByDomain(domain:String):DomainCredentialEntity?
    suspend fun insert(item: DomainCredentialEntity)
    suspend fun delete(item: DomainCredentialEntity)
    suspend fun update(item: DomainCredentialEntity)
    fun getById(id: String): DomainCredentialEntity?
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
