
package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.data.dao.SettingsDao
import com.akcreation.gitsilent.data.entity.SettingsEntity
import com.akcreation.gitsilent.utils.getSecFromTime

class SettingsRepositoryImpl(private val dao: SettingsDao) : SettingsRepository {
    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }
}
