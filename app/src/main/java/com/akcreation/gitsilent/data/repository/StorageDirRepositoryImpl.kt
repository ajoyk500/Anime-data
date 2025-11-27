
package com.akcreation.gitsilent.data.repository

import androidx.room.withTransaction
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.StorageDirCons
import com.akcreation.gitsilent.data.dao.StorageDirDao
import com.akcreation.gitsilent.data.entity.StorageDirEntity
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StorageDirUtil
import com.akcreation.gitsilent.utils.getSecFromTime

private const val TAG = "StorageDirRepositoryImpl"
class StorageDirRepositoryImpl(private val dao: StorageDirDao) : StorageDirRepository {
    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }
}
