
package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.data.dao.DomainCredentialDao
import com.akcreation.gitsilent.data.entity.DomainCredentialEntity
import com.akcreation.gitsilent.dto.DomainCredentialDto
import com.akcreation.gitsilent.utils.getSecFromTime

class DomainCredentialRepositoryImpl(private val dao: DomainCredentialDao) : DomainCredentialRepository {
    override suspend fun getAll(): List<DomainCredentialEntity> = dao.getAll()
    override suspend fun getAllDto(): List<DomainCredentialDto> = dao.getAllDto()
    override suspend fun isDomainExist(domain: String): Boolean = getByDomain(domain) != null
    override suspend fun getByDomain(domain: String): DomainCredentialEntity? = dao.getByDomain(domain)
    override suspend fun insert(item: DomainCredentialEntity){
        if(isDomainExist(item.domain)) {
            throw RuntimeException("dc#insert: domain name already exists")
        }
        dao.insert(item)
    }
    override suspend fun delete(item: DomainCredentialEntity) = dao.delete(item)
    override suspend fun update(item: DomainCredentialEntity) {
        val checkExist = getByDomain(item.domain)
        if(checkExist!=null && checkExist.id!=item.id) {
            throw RuntimeException("dc#update: domain name already exists")
        }
        item.baseFields.baseUpdateTime = getSecFromTime()
        dao.update(item)
    }
    override fun getById(id: String): DomainCredentialEntity? = dao.getById(id)
    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }
}
