package com.akcreation.gitsilent.settings.migrate.changelist

import com.akcreation.gitsilent.settings.migrate.FromVToV
import com.akcreation.gitsilent.settings.version.SettingsVersion

class ChangeListSettingsFrom0To1: FromVToV(from = "0", to="1") {
    override fun doMigration(s: MutableMap<String,String>) {
        val key_version = SettingsVersion.commonKey_version  
        val key_lastUsedRepoId = "lastUsedRepoId";  
    }
}
