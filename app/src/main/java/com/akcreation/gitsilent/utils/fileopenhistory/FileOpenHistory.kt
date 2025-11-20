package com.akcreation.gitsilent.utils.fileopenhistory

import com.akcreation.gitsilent.settings.FileEditedPos
import kotlinx.serialization.Serializable

/**
 * remember file opened history
 */
@Serializable
data class FileOpenHistory (
    var storage:MutableMap<String, FileEditedPos> = mutableMapOf(),

)
