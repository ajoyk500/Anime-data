package com.akcreation.gitsilent.utils.pref

import android.content.Context
import com.akcreation.gitsilent.ui.theme.Theme

object PrefUtil {
    private const val trueStr = "1"
    private const val falseStr = "0"
    private fun getBoolean(context:Context, key:String, default: Boolean): Boolean {
        return PrefMan.get(context, key, if(default) trueStr else falseStr) != falseStr
    }
    private fun setBoolean(context: Context, key:String, newValue:Boolean) {
        PrefMan.set(context, key, if(newValue) trueStr else falseStr)
    }
    fun setDevMode(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.devModeOn, enable)
    }
    fun getDevMode(context: Context):Boolean {
        return getBoolean(context, PrefMan.Key.devModeOn, false)
    }
    fun setShowRandomLaunchingText(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.showRandomLaunchingText, enable)
    }
    fun getShowRandomLaunchingText(context: Context): Boolean {
        return getBoolean(context, PrefMan.Key.showRandomLaunchingText, false)
    }
    fun setDynamicColorsScheme(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.dynamicColorsScheme, enable)
    }
    fun getDynamicColorsScheme(context: Context): Boolean {
        return getBoolean(context, PrefMan.Key.dynamicColorsScheme, Theme.defaultDynamicColorsValue)
    }
}
