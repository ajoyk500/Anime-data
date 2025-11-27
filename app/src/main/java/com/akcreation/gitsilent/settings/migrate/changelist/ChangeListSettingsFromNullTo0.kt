package com.akcreation.gitsilent.settings.migrate.changelist

import com.akcreation.gitsilent.settings.migrate.FromVToV
import com.akcreation.gitsilent.settings.version.SettingsVersion

class ChangeListSettingsFromNullTo0: FromVToV(from = "null", to="0") {
    override fun doMigration(s: MutableMap<String,String>) {
        val key_version = SettingsVersion.commonKey_version
        val key_lastUsedRepoId = "lastUsedRepoId";  
    }
}
