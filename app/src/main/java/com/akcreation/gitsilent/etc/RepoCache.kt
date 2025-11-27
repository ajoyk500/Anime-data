package com.akcreation.gitsilent.etc

import com.akcreation.gitsilent.data.entity.RepoEntity
import kotlinx.coroutines.sync.Mutex

@Deprecated("need not this object, only Mutex enough")
class RepoCache(
    val lock:Mutex,
    val data:RepoEntity  
) {
}