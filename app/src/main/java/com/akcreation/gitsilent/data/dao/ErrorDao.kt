
package com.akcreation.gitsilent.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akcreation.gitsilent.data.entity.ErrorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErrorDao {
    @Query("SELECT * from error ORDER BY id ASC")
    fun getAllStream(): Flow<List<ErrorEntity?>>
    @Query("SELECT * from error WHERE id = :id")
    fun getStream(id: String): Flow<ErrorEntity?>
    @Insert
    suspend fun insert(item: ErrorEntity)
    @Update
    suspend fun update(item: ErrorEntity)
    @Delete
    suspend fun delete(item: ErrorEntity)
    @Query("SELECT * from error WHERE repoId = :repoId order by baseCreateTime DESC")
    fun getListByRepoId(repoId: String): List<ErrorEntity>
    @Query("SELECT * from error WHERE id = :id")
    fun getById(id: String): ErrorEntity?
    @Query("update error set isChecked= :isChecked WHERE repoId = :repoId")
    fun updateIsCheckedByRepoId(repoId: String, isChecked:Int)
    @Query("delete from error WHERE baseCreateTime < :timeInSec")
    fun deleteErrOverTime(timeInSec:Long)
    @Query("delete from error WHERE repoId = :repoId")
    fun deleteByRepoId(repoId: String)
    @Query("UPDATE error set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
