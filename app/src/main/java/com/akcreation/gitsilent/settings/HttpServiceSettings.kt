package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class HttpServiceSettings (
    var launchOnAppStartup:Boolean = false,
    var listenHost:String = "127.0.0.1",
    var listenPort:Int = 52520,
    var tokenList:List<String> = listOf("default_puppygit_token"),
    var ipWhiteList:List<String> = listOf("127.0.0.1"),
    @Deprecated("ipWhiteList and token fair enough")
    var ipBlackList:List<String> = listOf(),
    @Deprecated("now, only using show notify when progress")
    var showNotifyWhenErr:Boolean = true,
    @Deprecated("now, only using show notify when progress")
    var showNotifyWhenSuccess:Boolean = true,
    var showNotifyWhenProgress:Boolean = true,
)
