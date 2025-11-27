package com.akcreation.gitsilent.utils.cache

import com.akcreation.gitsilent.git.CommitDto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Deprecated("[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]")
object CommitCache {
    private val cache = mutableMapOf<String, MutableMap<String, CommitDto>>()
    private val lock = Mutex()
    private const val EACH_REPO_CACHE_SIZE = 100
    suspend fun cacheIt(repoId:String, commitFullHash:String, commitDto: CommitDto) {
        return;
        lock.withLock {
            val cacheOfRepo = getCacheMapOfRepo(repoId)
            if(cacheOfRepo.size < EACH_REPO_CACHE_SIZE) {
                cacheOfRepo.put(commitFullHash, commitDto)
            }
        }
    }
    suspend fun getCachedDataOrNull(repoId: String, commitFullHash: String): CommitDto? {
        return null;
        lock.withLock {
            val cacheOfRepo = getCacheMapOfRepo(repoId)
            return cacheOfRepo.get(commitFullHash)
        }
    }
    suspend fun clear() {
        return;
        lock.withLock {
            cache.clear()
        }
    }
    private fun getCacheMapOfRepo(repoId:String) = cache.getOrPut(repoId) { mutableMapOf() }
}
