package com.akcreation.gitsilent.utils

import android.content.Context
import com.akcreation.gitsilent.constants.LangCode
import com.akcreation.gitsilent.constants.StrCons
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.pref.PrefMan

object LanguageUtil {
    private const val TAG="LanguageUtil"
    private const val key = PrefMan.Key.lang
    val languageCodeList = listOf(
        LangCode.auto,
        LangCode.ar,
        LangCode.bn,
        LangCode.en,
        LangCode.ru,
        LangCode.tr,
        LangCode.zh_cn,
    )
    fun getLangCode(context: Context):String {
        return PrefMan.get(context, key, "").let {
            if(isAuto(it)) {
                LangCode.auto
            }else {
                it
            }
        }
    }
    fun setLangCode(context: Context, langCode:String) {
        PrefMan.set(context, key, langCode)
    }
    fun isAuto(langCode: String):Boolean {
        return langCode == LangCode.auto || langCode.isBlank() || !languageCodeList.contains(langCode)
    }
    fun getLanguageTextByCode(languageCode:String, context: Context):String {
        if(isAuto(languageCode)) {
            return context.getString(R.string.auto)
        }
        if(languageCode == LangCode.ar) {
            return StrCons.langName_Arabic
        }
        if(languageCode == LangCode.bn) {
            return StrCons.langName_Bangla
        }
        if(languageCode == LangCode.en) {
            return StrCons.langName_English
        }
        if(languageCode == LangCode.ru) {
            return StrCons.langName_Russian
        }
        if(languageCode == LangCode.zh_cn) {
            return StrCons.langName_ChineseSimplified
        }
        if(languageCode == LangCode.tr) {
            return StrCons.langName_Turkish
        }
        MyLog.d(TAG, "#getLanguageTextByCode: unknown language code '$languageCode', will use `auto`")
        return context.getString(R.string.auto)
    }
    fun splitLanguageCode(languageCode:String):Pair<String,String> {
        val codes = languageCode.split("-r")
        if(codes.size>1) {
            return Pair(codes[0], codes[1])
        }else {
            return Pair(codes[0], "")
        }
    }
}
