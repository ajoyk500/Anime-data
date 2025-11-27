package com.akcreation.gitsilent.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.activity.findActivity
import com.akcreation.gitsilent.syntaxhighlight.base.TextMateUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.pref.PrefMan
import com.akcreation.gitsilent.utils.pref.PrefUtil

private const val TAG = "Theme"
object Theme {
    val Orange = Color(0xFFFF5722)
    val darkLightBlue = Color(0xDF456464)
    val mdGreen = Color(0xFF13831C)
    val mdRed = Color(0xFFAD2A2A)
    val Gray1 = Color(0xFF8C8C8C)
    val Gray2 = Color(0xFF7C7C7C)
    const val auto = 0
    const val light = 1
    const val dark = 2
    const val defaultThemeValue: Int = auto 
    var inDarkTheme = false
    val inDarkThemeState = mutableStateOf(false)
    val themeList = listOf(
        auto,  
        light,  
        dark,  
    )
    var theme:MutableState<Int> = mutableStateOf(defaultThemeValue)
    const val defaultDynamicColorsValue = true
    val dynamicColor = mutableStateOf(defaultDynamicColorsValue)
    fun init(themeValue: Int, dynamicColorEnabled: Boolean) {
        theme.value = getALegalThemeValue(themeValue)
        dynamicColor.value = dynamicColorEnabled
    }
    fun updateThemeValue(context: Context, newValue:Int) {
        val themeValue = getALegalThemeValue(newValue)
        theme.value = themeValue
        PrefMan.set(context, PrefMan.Key.theme, ""+themeValue)
    }
    fun getALegalThemeValue(themeValue:Int) = if(themeList.contains(themeValue)) themeValue else defaultThemeValue;
    fun updateDynamicColor(context:Context, newValue:Boolean) {
        dynamicColor.value = newValue
        PrefUtil.setDynamicColorsScheme(context, newValue)
    }
    fun getThemeTextByCode(themeCode:Int?, appContext: Context):String {
        if(themeCode == auto) {
            return appContext.getString(R.string.auto)
        }else if(themeCode == light) {
            return appContext.getString(R.string.light)
        }else if(themeCode == dark) {
            return appContext.getString(R.string.dark)
        }else {
            return appContext.getString(R.string.unknown)
        }
    }
}
private val DarkColorScheme = darkColorScheme(
)
private val LightColorScheme = lightColorScheme(
)
@Composable
fun InitContent(context: Context, content: @Composable ()->Unit) {
    Theme.init(
        themeValue = PrefMan.getInt(context, PrefMan.Key.theme, Theme.defaultThemeValue),
        dynamicColorEnabled = PrefUtil.getDynamicColorsScheme(context),
    )
    PuppyGitAndroidTheme {
        content()
    }
}
@Composable
fun PuppyGitAndroidTheme(
    theme:Int = Theme.theme.value,
    dynamicColor: Boolean = Theme.dynamicColor.value,
    content: @Composable () -> Unit
) {
    val darkTheme = if(theme == Theme.auto) isSystemInDarkTheme() else (theme == Theme.dark)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                Theme.inDarkTheme = true;
                Theme.inDarkThemeState.value = true
                getDynamicColor(true, context)
            }else {
                Theme.inDarkTheme = false
                Theme.inDarkThemeState.value = false
                getDynamicColor(false, context)
            }
        }
        darkTheme -> {
            Theme.inDarkTheme = true;
            Theme.inDarkThemeState.value = true
            DarkColorScheme
        }
        else -> {
            Theme.inDarkTheme = false;
            Theme.inDarkThemeState.value = false
            LightColorScheme
        }
    }
    TextMateUtil.updateTheme(Theme.inDarkTheme)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            if(activity != null) {
                val window = activity.window
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun getDynamicColor(inDarkTheme: Boolean, context: Context): ColorScheme {
    return (if(inDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)).let {
        if(isNeutralColorScheme(it)) {
            MyLog.d(TAG, "Neutral (gray) color scheme detected, will use secondary replaced primary colors")
            it.copy(
                primary = it.secondary,
                primaryContainer = it.secondaryContainer,
                onPrimary = it.onSecondary,
                onPrimaryContainer = it.onSecondaryContainer
            )
        }else {
            MyLog.d(TAG, "Not Neutral (gray) color scheme, will use default primary colors")
            it
        }
    }
}
fun isNeutralColorScheme(colorScheme: ColorScheme) = colorScheme.primary.let { it.red == it.green && it.red == it.blue }
