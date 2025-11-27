
package com.akcreation.gitsilent.data.repository

import androidx.room.Query
import com.akcreation.gitsilent.data.entity.ErrorEntity
import kotlinx.coroutines.flow.Flow

interface ErrorRepository {
    fun getAllStream(): Flow<List<ErrorEntity?>>
    fun getStream(id: String): Flow<ErrorEntity?>
    suspend fun insert(item: ErrorEntity)
    suspend fun delete(item: ErrorEntity)
    suspend fun update(item: ErrorEntity)
    fun getListByRepoId(repoId: String): List<ErrorEntity>
    fun getById(id: String): ErrorEntity?
    fun updateIsCheckedByRepoId(repoId: String, isChecked:Int)
    fun deleteErrOverTime(timeInSec:Long)
    fun deleteErrOverLimitTime()
    fun deleteByRepoId(repoId: String)
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
