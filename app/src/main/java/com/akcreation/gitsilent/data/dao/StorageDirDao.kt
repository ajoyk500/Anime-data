
package com.akcreation.gitsilent.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akcreation.gitsilent.data.entity.StorageDirEntity

@Dao
interface StorageDirDao {
    @Insert
    suspend fun insert(item: StorageDirEntity)
    @Update
    suspend fun update(item: StorageDirEntity)
    @Delete
    suspend fun delete(item: StorageDirEntity)
    @Query("SELECT * from storageDir where id = :id")
    suspend fun getById(id:String): StorageDirEntity?
    @Query("SELECT * from storageDir where fullPath = :fullPath LIMIT 1")
    suspend fun getByFullPath(fullPath:String): StorageDirEntity?
    @Query("SELECT * from storageDir where name = :name LIMIT 1")
    suspend fun getByName(name:String): StorageDirEntity?
    @Query("SELECT * from storageDir where fullPath = :fullPath or name=:name LIMIT 1")
    suspend fun getByNameOrFullPath(name:String, fullPath:String): StorageDirEntity?
    @Query("SELECT * from storageDir where id!=:excludeId and (fullPath = :fullPath or name=:name) LIMIT 1")
    suspend fun getByNameOrFullPathExcludeId(name: String, fullPath: String, excludeId:String): StorageDirEntity?
    @Query("SELECT * from storageDir")
    suspend fun getAll(): List<StorageDirEntity>
    @Query("SELECT * from storageDir where baseStatus=:status")
    suspend fun getListByStatus(status:Int): List<StorageDirEntity>
    @Query("UPDATE storageDir set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
