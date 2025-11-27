package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class SshSettings(
    var allowUnknownHosts:Boolean = false
)
