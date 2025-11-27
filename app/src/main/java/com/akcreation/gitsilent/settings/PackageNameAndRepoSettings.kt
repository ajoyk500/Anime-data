package com.akcreation.gitsilent.settings

import com.akcreation.gitsilent.utils.parseLongOrDefault
import kotlinx.serialization.Serializable

@Serializable
data class PackageNameAndRepoSettings(
    private val pullInterval:String="",
    private val pushDelay:String="",
) {
    fun getPullIntervalFormatted() = parseLongOrDefault(pullInterval, null)?.toString() ?: ""
    fun getPushDelayFormatted() = parseLongOrDefault(pushDelay, null)?.toString() ?: ""
    fun getPullIntervalActuallyValue(settings: AppSettings) = parseLongOrDefault(pullInterval, settings.automation.pullIntervalInSec) ?: 0L
    fun getPushDelayActuallyValue(settings: AppSettings) = parseLongOrDefault(pushDelay, settings.automation.pushDelayInSec) ?: 0L
    companion object {
        fun formatPullIntervalBeforeSaving(value:String) = parseLongOrDefault(value, null)?.toString() ?: ""
        fun formatPushDelayBeforeSaving(value:String) = parseLongOrDefault(value, null)?.toString() ?: ""
    }
}
