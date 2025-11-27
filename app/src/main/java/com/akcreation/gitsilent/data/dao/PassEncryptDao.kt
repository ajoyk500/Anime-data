
package com.akcreation.gitsilent.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akcreation.gitsilent.data.entity.PassEncryptEntity

@Dao
interface PassEncryptDao {
    @Insert
    suspend fun insert(item: PassEncryptEntity)
    @Update
    suspend fun update(item: PassEncryptEntity)
    @Delete
    suspend fun delete(item: PassEncryptEntity)
    @Query("SELECT * from passEncrypt WHERE id = :id")
    suspend fun getById(id: Int): PassEncryptEntity?
}
