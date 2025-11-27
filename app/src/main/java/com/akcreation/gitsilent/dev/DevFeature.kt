package com.akcreation.gitsilent.dev

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.pref.PrefUtil

object DevFeature {
    private const val prefix = "Dev: "
    fun appendDevPrefix(text:String):String {
        return prefix+text
    }
    val safDiff_text = appendDevPrefix("SAF Diff")
    val inner_data_storage = appendDevPrefix("Inner Data")  
    val external_data_storage = appendDevPrefix("External Data") 
    val singleDiff = object : DevItem<Boolean>(text = "Single Diff", state = mutableStateOf(false), desc="Enable for better performance") {
        override fun update(newValue: Boolean, context: Context?) {
            state.value = newValue
            SettingsUtil.update {
                it.devSettings.singleDiffOn = newValue
            }
        }
    }
    val treatNoWordMatchAsNoMatchedForDiff = object : DevItem<Boolean>(text = "Treat No Words Matched as Non-Matched", state = mutableStateOf(false), desc="Treat none words matched as non-matched when Diff contents and enabled match by words") {
        override fun update(newValue: Boolean, context: Context?) {
            state.value = newValue
            SettingsUtil.update {
                it.devSettings.treatNoWordMatchAsNoMatchedForDiff = newValue
            }
        }
    }
    val degradeMatchByWordsToMatchByCharsIfNonMatched = object : DevItem<Boolean>(text = "Degrade Match by words", state = mutableStateOf(false), desc="Degrade to Match by chars if Match by words was non-matched, not good for space-split language matching (like English), but good for non-space-split language (like Chinese)") {
        override fun update(newValue: Boolean, context: Context?) {
            state.value = newValue
            SettingsUtil.update {
                it.devSettings.degradeMatchByWordsToMatchByCharsIfNonMatched = newValue
            }
        }
    }
    val setDiffRowToNoMatched = appendDevPrefix("No Matched")
    val setDiffRowToAllMatched = appendDevPrefix("All Matched")
    val showMatchedAllAtDiff = object : DevItem<Boolean>(text = "Show 'No/All Matched' at Diff Screen", state = mutableStateOf(true)) {
        override fun update(newValue: Boolean, context: Context?) {
            state.value = newValue
            SettingsUtil.update {
                it.devSettings.showMatchedAllAtDiff = newValue
            }
        }
    }
    val showRandomLaunchingText = object : DevItem<Boolean>(text = "Show Random Launching Text", state = mutableStateOf(false)) {
        override fun update(newValue: Boolean, context: Context?) {
            state.value = newValue
            PrefUtil.setShowRandomLaunchingText(context!!, newValue)
        }
    }
    val legacyChangeListLoadMethod = object : DevItem<Boolean>(text = "Legacy ChangeList Load Method", state = mutableStateOf(true), desc = "Better enable this, new method are unstable and maybe slower") {
        override fun update(newValue: Boolean, context: Context?) {
            state.value = newValue
            SettingsUtil.update { it.devSettings.legacyChangeListLoadMethod = newValue }
        }
    }
    val settingsItemList = listOf(
        singleDiff,
        treatNoWordMatchAsNoMatchedForDiff,
        degradeMatchByWordsToMatchByCharsIfNonMatched,
        showMatchedAllAtDiff,
        legacyChangeListLoadMethod,
    )
}
abstract class DevItem<T>(
    val text:String,
    val state: MutableState<T>,
    val desc:String = "",
) {
    abstract fun update(newValue:T, context: Context? = null)
}
