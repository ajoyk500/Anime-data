package com.akcreation.gitsilent.utils

import android.content.Context
import java.util.Locale

object ContextUtil {
    private const val TAG = "ContextUtil"
    fun getLocalizedContext(newBase: Context):Context {
        try {
            val languageCode = LanguageUtil.getLangCode(newBase)
            if(LanguageUtil.isAuto(languageCode)) {
                return createDefaultContextForUnsupportedLanguage(newBase)
            }
            return createContextByLanguageCode(languageCode, newBase)
        }catch (e:Exception) {
            MyLog.e(TAG, "#getLocalizedContext err: ${e.localizedMessage}")
            return newBase
        }
    }
    private fun createContextByLanguageCode(languageCode: String, baseContext: Context): Context {
        val (language, country) = LanguageUtil.splitLanguageCode(languageCode)
        val locale = if (country.isBlank()) Locale(language) else Locale(language, country)
        return setLocalForContext(locale, baseContext)
    }
    private fun createDefaultContextForUnsupportedLanguage(baseContext: Context): Context {
        val systemDefaultLocal = Locale.getDefault()
        return setLocalForContext(systemDefaultLocal, baseContext)
    }
    private fun setLocalForContext(locale: Locale, baseContext:Context):Context {
        Locale.setDefault(locale)
        val config = baseContext.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return baseContext.createConfigurationContext(config)
    }
}
