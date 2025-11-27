
package com.akcreation.gitsilent.data.repository

import com.akcreation.gitsilent.data.entity.SettingsEntity

@Deprecated("[CHINESE]，[CHINESE]json[CHINESE]AppSettings[CHINESE]")
interface SettingsRepository {
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
