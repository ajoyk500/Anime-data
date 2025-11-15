package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.data.dao.RepoDao
import com.akcreation.gitsilent.data.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

class RepoRepository(private val repoDao: RepoDao) {
    
    val allRepos: Flow<List<RepoEntity>> = repoDao.getAllRepos()
    
    suspend fun getRepoById(id: Int): RepoEntity? {
        return repoDao.getRepoById(id)
    }
    
    suspend fun getRepoByPath(path: String): RepoEntity? {
        return repoDao.getRepoByPath(path)
    }
    
    suspend fun insertRepo(repo: RepoEntity): Long {
        return repoDao.insertRepo(repo)
    }
    
    suspend fun updateRepo(repo: RepoEntity) {
        repoDao.updateRepo(repo)
    }
    
    suspend fun deleteRepo(repo: RepoEntity) {
        repoDao.deleteRepo(repo)
    }
    
    suspend fun deleteRepoById(id: Int) {
        repoDao.deleteRepoById(id)
    }
    
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        repoDao.updateFavoriteStatus(id, isFavorite)
    }
}