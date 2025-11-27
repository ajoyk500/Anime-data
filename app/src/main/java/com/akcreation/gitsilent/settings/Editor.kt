package com.akcreation.gitsilent.settings

import com.akcreation.gitsilent.utils.fileopenhistory.FileOpenHistoryMan
import kotlinx.serialization.Serializable

@Serializable
data class Editor (
    var lastEditedFilePath:String="",  
    @Deprecated("after app 1.0.5v26, use `FileOpenHistory.history` instead")
    val filesLastEditPosition:MutableMap<String,FileEditedPos> = mutableMapOf(),
    val fileOpenHistoryLimit:Int = FileOpenHistoryMan.defaultHistoryMaxCount,
    var restoreLastEditColumn:Boolean=true,  
    var editCacheKeepInDays:Int = 3,  
    var editCacheEnable:Boolean = false,  
    var conflictStartStr:String = SettingsCons.defaultConflictStartStr,
    var conflictSplitStr:String = SettingsCons.defaultConfilctSplitStr,
    var conflictEndStr:String = SettingsCons.defaultConflictEndStr,
    var fontSize:Int = SettingsCons.defaultFontSize,  
    var lineNumFontSize:Int = SettingsCons.defaultLineNumFontSize,  
    var showLineNum:Boolean = true,  
    var enableFileSnapshot:Boolean = false,  
    var enableContentSnapshot:Boolean = false,  
    var maxFileSizeLimit:Long = 0L,  
    var showUndoRedo:Boolean = true,
    var fileAssociationList:List<String> = SettingsCons.editor_defaultFileAssociationList,
    var recentFilesLimit:Int = FileOpenHistoryMan.defaultHistoryMaxCount,
    var patchModeOn:Boolean = false,
    var disableSoftwareKeyboard: Boolean = false,
    var tabIndentSpacesCount:Int = 4,
    var syntaxHighlightEnabled:Boolean = true,
    var useSystemFonts: Boolean = false,
    var autoCloseSymbolPair: Boolean = true,
    var thresholdLinesCountOfIncrementAnalyze: Int = 260,
)
