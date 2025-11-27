package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class GlobalGitConfig(
    var username:String="",
    var email:String="",
    var pullWithRebase: Boolean = false,
)
