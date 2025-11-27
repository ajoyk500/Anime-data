package com.akcreation.gitsilent.utils.pref

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import android.content.SharedPreferences

object PrefMan {
    private const val fileName = "settings"
    object Key {
        const val lang = "lang"
        const val logLevel = "log_level"
        const val logKeepDays = "log_keep_days"
        const val theme = "theme"
        const val launchServiceOnSystemStartup = "launch_service_on_system_startup"
        const val masterPass = "mpwd"
        const val firstUse = "firstUse"
        const val devModeOn = "devModeOn"
        const val showRandomLaunchingText = "showRandomLaunchingText"
        const val dynamicColorsScheme = "dynamicColorsScheme"
    }
    private fun getPrefs(appContext: Context) = appContext.getSharedPreferences(fileName, MODE_PRIVATE)
    fun get(appContext: Context, key:String, defaultValue:String):String {
        try {
            val prefs = getPrefs(appContext)
            val value = prefs.getString(key, defaultValue)
            return value ?: defaultValue
        }catch (_:Exception) {
            return defaultValue
        }
    }
    fun getInt(appContext: Context, key:String, defaultValue:Int):Int {
        try {
            val value = get(appContext, key, ""+defaultValue)
            return value.toInt()
        }catch (_:Exception) {
            return defaultValue
        }
    }
    fun getChar(appContext: Context, key:String, defaultValue:Char):Char {
        try {
            val value = get(appContext, key, ""+defaultValue)
            return value.get(0)
        }catch (_:Exception) {
            return defaultValue
        }
    }
    fun set(appContext: Context, key:String, value:String) {
        val prefs: SharedPreferences = getPrefs(appContext)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply() 
    }
    fun isFirstUse(context: Context):Boolean {
        return PrefMan.get(context, PrefMan.Key.firstUse, "1") == "1"
    }
    fun updateFirstUse(context: Context, newValue:Boolean) {
        PrefMan.set(context, PrefMan.Key.firstUse, if(newValue) "1" else "0")
    }
}
