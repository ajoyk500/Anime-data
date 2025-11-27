package com.akcreation.gitsilent.utils

import com.akcreation.gitsilent.utils.cache.Cache

object RepoStatusUtil {
    private fun getRepoStatusKey(repoId: String):String {
        return Cache.Key.repoTmpStatusPrefix + Cache.keySeparator + repoId
    }
    fun setRepoStatus(repoId:String, status:String) {
        val statusKey = getRepoStatusKey(repoId)
        Cache.set(statusKey, status)
    }
    fun getRepoStatus(repoId: String):String? {
        return Cache.getByType<String>(getRepoStatusKey(repoId))
    }
    fun clearRepoStatus(repoId: String) {
        setRepoStatus(repoId, "")
    }
}
