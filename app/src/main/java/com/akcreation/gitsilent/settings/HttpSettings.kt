package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class HttpSettings(
    var sslVerify: Boolean = true,
)
