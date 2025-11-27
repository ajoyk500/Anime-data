
package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.dao.ErrorDao
import com.akcreation.gitsilent.data.entity.ErrorEntity
import com.akcreation.gitsilent.utils.daysToSec
import com.akcreation.gitsilent.utils.getSecFromTime
import kotlinx.coroutines.flow.Flow

class ErrorRepositoryImpl(private val dao: ErrorDao) : ErrorRepository {
    override fun getAllStream(): Flow<List<ErrorEntity?>> = dao.getAllStream()
    override fun getStream(id: String): Flow<ErrorEntity?> = dao.getStream(id)
    override suspend fun insert(item: ErrorEntity) = dao.insert(item)
    override suspend fun delete(item: ErrorEntity) = dao.delete(item)
    override suspend fun update(item: ErrorEntity) = dao.update(item)
    override fun getListByRepoId(repoId: String): List<ErrorEntity> {
        return dao.getListByRepoId(repoId)
    }
    override fun getById(id: String): ErrorEntity? {
        return dao.getById(id)
    }
    override fun updateIsCheckedByRepoId(repoId: String, isChecked: Int) {
        dao.updateIsCheckedByRepoId(repoId,isChecked)
    }
    override fun deleteErrOverTime(timeInSec: Long) {
        dao.deleteErrOverTime(timeInSec)
    }
    override fun deleteErrOverLimitTime() {
        val limitTimeInSec = daysToSec(Cons.dbDeleteErrOverThisDay)
        val nowInSec = getSecFromTime()
        val willDeleteBeforeThisDayInSec = nowInSec - limitTimeInSec
        if(nowInSec>0) {
            deleteErrOverTime(willDeleteBeforeThisDayInSec)
        }
    }
    override fun deleteByRepoId(repoId: String) {
        dao.deleteByRepoId(repoId)
    }
    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }
}
