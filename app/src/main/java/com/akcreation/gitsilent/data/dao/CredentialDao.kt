
package com.akcreation.gitsilent.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akcreation.gitsilent.data.entity.CredentialEntity

@Dao
interface CredentialDao {
    @Query("SELECT * from credential order by baseCreateTime DESC")
    suspend fun getAll(): List<CredentialEntity>
    @Insert
    suspend fun insert(item: CredentialEntity)
    @Update
    suspend fun update(item: CredentialEntity)
    @Delete
    suspend fun delete(item: CredentialEntity)
    @Query("SELECT id from credential WHERE name = :name LIMIT 1")
    suspend fun getIdByCredentialName(name: String): String?
    @Query("SELECT * from credential WHERE id = :id")
    suspend fun getById(id: String): CredentialEntity?
    @Query("SELECT * from credential WHERE type = :type order by baseCreateTime DESC")
    suspend fun getListByType(type:Int): List<CredentialEntity>
    @Query("SELECT * from credential WHERE encryptVer != :encryptVer")
    suspend fun getByEncryptVerNotEqualsTo(encryptVer:Int): List<CredentialEntity>
    @Query("UPDATE credential set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
