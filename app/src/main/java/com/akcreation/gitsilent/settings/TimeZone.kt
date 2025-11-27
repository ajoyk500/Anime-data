package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class TimeZone (
    var followSystem:Boolean = true,
    var offsetInMinutes:String="",
)
