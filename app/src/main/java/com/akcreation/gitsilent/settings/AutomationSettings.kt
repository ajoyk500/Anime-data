package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class AutomationSettings (
    var packageNameAndRepoIdsMap:MutableMap<String, List<String>> = mutableMapOf(),
    var packageNameAndRepoAndSettingsMap: MutableMap<String, PackageNameAndRepoSettings> =  mutableMapOf(),
    @Deprecated("now, only using show notify when progress")
    var showNotifyWhenErr:Boolean = true,
    @Deprecated("now, only using show notify when progress")
    var showNotifyWhenSuccess:Boolean = true,
    var showNotifyWhenProgress:Boolean = true,
    var pullIntervalInSec:Long = 0L,
    var pushDelayInSec:Long = 0L,
)
