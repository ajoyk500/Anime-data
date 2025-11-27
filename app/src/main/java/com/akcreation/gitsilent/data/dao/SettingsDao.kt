
package com.akcreation.gitsilent.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akcreation.gitsilent.data.entity.SettingsEntity

@Dao
interface SettingsDao {
    @Insert
    suspend fun insert(item: SettingsEntity)
    @Update
    suspend fun update(item: SettingsEntity)
    @Delete
    suspend fun delete(item: SettingsEntity)
    @Query("SELECT * from settings where usedFor=:usedFor")
    suspend fun getByUsedFor(usedFor:Int): SettingsEntity?
    @Query("UPDATE settings set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
