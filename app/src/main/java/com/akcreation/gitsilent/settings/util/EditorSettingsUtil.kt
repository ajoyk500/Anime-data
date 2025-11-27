package com.akcreation.gitsilent.settings.util

import androidx.compose.runtime.MutableState
import com.akcreation.gitsilent.settings.SettingsUtil

object EditorSettingsUtil {
    fun updateDisableSoftKb(newValue:Boolean, state: MutableState<Boolean>?) {
        state?.value = newValue
        SettingsUtil.update {
            it.editor.disableSoftwareKeyboard = newValue
        }
    }
}
