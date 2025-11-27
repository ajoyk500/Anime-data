package com.akcreation.gitsilent.utils.fileopenhistory

import com.akcreation.gitsilent.settings.FileEditedPos
import kotlinx.serialization.Serializable

@Serializable
data class FileOpenHistory (
    var storage:MutableMap<String, FileEditedPos> = mutableMapOf(),
)
