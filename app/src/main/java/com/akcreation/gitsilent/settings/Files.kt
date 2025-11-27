package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class Files (
    var lastOpenedPath:String="",
    var defaultViewAndSort:DirViewAndSort=DirViewAndSort(),
    val dirAndViewSort_Map:MutableMap<String, DirViewAndSort> = mutableMapOf()
)
