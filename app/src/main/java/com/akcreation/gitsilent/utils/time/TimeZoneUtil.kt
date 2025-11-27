package com.akcreation.gitsilent.utils.time

import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.formatMinutesToUtc
import com.akcreation.gitsilent.utils.isValidOffsetInMinutes

private const val TAG = "TimeZoneUtil"
object TimeZoneUtil {
    fun getAppTimeZoneMode(settings: AppSettings) : TimeZoneMode {
        return try{
            if(settings.timeZone.followSystem) {
                TimeZoneMode.FOLLOW_SYSTEM
            }else {
                val offsetMinutes = settings.timeZone.offsetInMinutes.trim().toInt()
                if(isValidOffsetInMinutes(offsetMinutes)){
                    TimeZoneMode.SPECIFY
                }else {
                    TimeZoneMode.UNSET
                }
            }
        }catch (_:Exception) {
            TimeZoneMode.UNSET
        }
    }
    fun shouldShowTimeZoneInfo(settings: AppSettings, useCache:Boolean = true) : Boolean {
        return if(useCache) {
            AppModel.getAppTimeZoneModeCached(settings) == TimeZoneMode.UNSET
        }else {
            getAppTimeZoneMode(settings) == TimeZoneMode.UNSET
        }
    }
    fun appendUtcTimeZoneText(str:String, offsetInMinutes:Int) : String {
        return "$str ${formatMinutesToUtc(offsetInMinutes)}"
    }
}
