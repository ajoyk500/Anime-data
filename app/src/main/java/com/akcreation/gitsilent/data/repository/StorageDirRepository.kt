package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.constants.StorageDirCons
import com.akcreation.gitsilent.data.entity.StorageDirEntity

@Deprecated("[CHINESE]，[CHINESE]saf，[CHINESE]saf[CHINESE]c[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]")
interface StorageDirRepository {
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
