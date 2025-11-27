package com.akcreation.gitsilent.syntaxhighlight.base

import androidx.compose.ui.graphics.Color
import com.akcreation.gitsilent.ui.theme.Theme
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry

object PLTheme {
    const val THEME_DARK = "solarized_dark"
    const val THEME_LIGHT = "solarized_light"
    val THEMES = listOf(THEME_DARK, THEME_LIGHT)
    val BG_DARK = Color(0xFF131313)
    val BG_LIGHT = Color(0xFFF7F7F7)
    fun setTheme(inDarkTheme: Boolean) {
        val theme = if(inDarkTheme) THEME_DARK else THEME_LIGHT
        ThemeRegistry.getInstance().setTheme(theme)
    }
    fun getBackground(inDarkTheme: Boolean) = if(inDarkTheme) BG_DARK else BG_LIGHT
    fun updateThemeByAppTheme() {
        setTheme(Theme.inDarkTheme)
    }
}
