package com.akcreation.gitsilent.utils.storagepaths

import kotlinx.serialization.Serializable

@Serializable
data class StoragePaths (
    val storagePaths:MutableList<String> = mutableListOf(),
    var storagePathLastSelected:String="",
)
