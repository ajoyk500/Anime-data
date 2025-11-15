package com.akcreation.gitsilent.data.dao

import androidx.room.*
import com.akcreation.gitsilent.data.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    
    @Query("SELECT * FROM repositories ORDER BY lastSyncTime DESC")
    fun getAllRepos(): Flow<List<RepoEntity>>
    
    @Query("SELECT * FROM repositories WHERE id = :id")
    suspend fun getRepoById(id: Int): RepoEntity?
    
    @Query("SELECT * FROM repositories WHERE path = :path LIMIT 1")
    suspend fun getRepoByPath(path: String): RepoEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo: RepoEntity): Long
    
    @Update
    suspend fun updateRepo(repo: RepoEntity)
    
    @Delete
    suspend fun deleteRepo(repo: RepoEntity)
    
    @Query("DELETE FROM repositories WHERE id = :id")
    suspend fun deleteRepoById(id: Int)
    
    @Query("UPDATE repositories SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
}