package com.akcreation.gitsilent.server.bean

import kotlinx.serialization.Serializable

@Serializable
data class ConfigBean(
    val version: String = "1",
    val repoName:String,
    val repoId:String,
    val api: ApiBean
)
