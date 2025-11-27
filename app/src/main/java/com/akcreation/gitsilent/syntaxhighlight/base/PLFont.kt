package com.akcreation.gitsilent.syntaxhighlight.base

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.SettingsUtil

object PLFont {
    val codeFontResId = R.font.jb_mono_nl_regular
    val codeFont = FontFamily(Font(codeFontResId))
    fun editorCodeFont() = if(SettingsUtil.isEditorUseSystemFonts()) null else codeFont
    fun diffCodeFont() = if(SettingsUtil.isDiffUseSystemFonts()) null else codeFont
}
