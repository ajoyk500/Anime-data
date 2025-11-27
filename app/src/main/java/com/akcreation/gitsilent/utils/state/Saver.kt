package com.akcreation.gitsilent.utils.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

object Saver {
    val STRING = Saver<String, String>(
        save = { it },
        restore = { it }
    )
    @Composable
    fun rememberSaveableString(init: ()->String):String {
        return rememberSaveable(saver = STRING, init = init)
    }
}
