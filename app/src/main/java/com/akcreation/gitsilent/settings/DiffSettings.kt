package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class DiffSettings (
    var groupDiffContentByLineNum:Boolean = false,
    var diffContentSizeMaxLimit:Long = 0L,  
    var loadDiffContentCheckAbortSignalLines:Int=1000,
    var loadDiffContentCheckAbortSignalSize:Long=1000000L,  
    var showLineNum:Boolean=true,
    var showOriginType:Boolean=true,
    var fontSize:Int = SettingsCons.defaultFontSize,  
    var lineNumFontSize:Int = SettingsCons.defaultLineNumFontSize,  
    var enableBetterButSlowCompare:Boolean=false,
    var matchByWords:Boolean=false,
    var enableSelectCompare:Boolean = true,
    var createSnapShotForOriginFileBeforeSave:Boolean = false,
    var readOnly:Boolean = false,
    var useSystemFonts: Boolean = false,
    var syntaxHighlightEnabled: Boolean = true,
    val enableDetailsCompare: Boolean = true,
)
