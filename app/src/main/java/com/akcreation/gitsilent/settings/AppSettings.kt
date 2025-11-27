package com.akcreation.gitsilent.settings

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.settings.SettingsCons.startPageMode_rememberLastQuit
import com.akcreation.gitsilent.settings.version.SettingsVersion
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    var version:Int = SettingsVersion.commonStartVer,  
    var startPageMode:Int=startPageMode_rememberLastQuit,
    var lastQuitHomeScreen:Int= Cons.selectedItem_Repos,
    @Deprecated("[CHINESE]prefman[CHINESE]")
    var firstUse:Boolean=true,
    var snapshotKeepInDays:Int = 3,
    var globalGitConfig:GlobalGitConfig = GlobalGitConfig(),
    val files:Files = Files(),
    val editor:Editor = Editor(),
    var changeList:ChangeList = ChangeList(),
    @Deprecated("[CHINESE]")
    val storageDir:StorageDir = StorageDir(),  
    @Deprecated("instead by StoragePaths")
    val storagePaths:MutableList<String> = mutableListOf(),
    @Deprecated("instead by StoragePaths")
    var storagePathLastSelected:String="",
    @Deprecated("instead by `DiffSettings` same name field")
    var groupDiffContentByLineNum:Boolean = true,
    val diff:DiffSettings = DiffSettings(),
    var commitHistoryPageSize:Int = 50,
    var commitHistoryLoadMoreCheckAbortSignalFrequency:Int= 1000,
    @Deprecated("instead by `PrefMan.Key` same name field")
    var theme:Int = 0,
    @Deprecated("instead by `PrefMan.Key` same name field")
    var logLevel:Char = 'w',
    @Deprecated("instead by `PrefMan.Key` same name field")
    var logKeepDays:Int = 3,
    var showNaviButtons:Boolean = false,
    var fileHistoryPageSize:Int = 10,
    val sshSetting:SshSettings=SshSettings(),
    var masterPasswordHash:String = "",
    val timeZone: TimeZone = TimeZone(),
    var httpService:HttpServiceSettings = HttpServiceSettings(),
    val automation: AutomationSettings = AutomationSettings(),
    val devSettings: DevSettings = DevSettings(),
    var commitHistoryRTL: Boolean = false,
    var commitHistoryGraph: Boolean = true,
    var commitMsgTemplate:String = "",
    var commitMsgPreviewModeOn:Boolean = true,
    var commitMsgUseSystemFonts: Boolean = false,
    val httpSetting: HttpSettings = HttpSettings(),
) {
    override fun equals(other: Any?): Boolean {
        return this === other
    }
}
