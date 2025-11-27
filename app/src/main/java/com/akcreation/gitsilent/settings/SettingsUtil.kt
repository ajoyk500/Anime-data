package com.akcreation.gitsilent.settings

import com.akcreation.gitsilent.settings.version.SettingsVersion
import com.akcreation.gitsilent.utils.JsonUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

object SettingsUtil {
    private const val TAG = "SettingsUtil"
    private const val settingsFileName= "PuppyGitSettings.json"
    private const val settingsBakFileName= "PuppyGitSettings-bak.json"
    private val createFileLock = Mutex()
    private val saveLock = Mutex()
    private const val channelBufferSize = 50
    private val saveChannel = Channel<AppSettings>(capacity = channelBufferSize, onBufferOverflow = BufferOverflow.SUSPEND)
    private val saveJobStarted = AtomicBoolean(false)
    private var appSettings:AppSettings = AppSettings()
    private lateinit var settingsFile:File
    private lateinit var settingsBakFile:File
    private lateinit var saveDir:File
    suspend fun init(settingsSaveDir:File, useBak:Boolean=false) {  
        val saveDirPath = settingsSaveDir.canonicalPath
        saveDir = File(saveDirPath)
        settingsFile = File(saveDirPath, settingsFileName)
        settingsBakFile = File(saveDirPath, settingsBakFileName)
        createFileIfNonExists()
        if(!saveJobStarted.get()){
            saveJobStarted.compareAndSet(false, true)
            startSaveJob()
        }
        readSettingsFromFile(useBak)
    }
    fun destroyer() {
        saveChannel.close()
        saveJobStarted.compareAndSet(true, false)
    }
    private fun startSaveJob() {
        val logTag="settingsSaveJob"
        doJobThenOffLoading {
            var errCountLimit = 3
            var closed = false
            while (errCountLimit > 0 && closed.not()) {
                try {
                    saveLock.withLock {
                        var newSettings = saveChannel.receive()
                        var count = 0
                        while (count++ < channelBufferSize) {
                            val result = saveChannel.tryReceive()
                            if (result.isSuccess) {
                                newSettings = result.getOrElse { newSettings }
                            } else {  
                                break
                            }
                        }
                        appSettings = newSettings
                        saveSettings(newSettings)
                    }
                } catch (e: Exception) {
                    if(saveChannel.tryReceive().isClosed) {
                        closed = true
                    }
                    errCountLimit--
                    MyLog.e(TAG, "$logTag: write config to settings file err: ${e.stackTraceToString()}")
                }
            }
            if(saveChannel.tryReceive().isClosed.not()) {
                saveChannel.close()
            }
        }
    }
    private suspend fun createFileIfNonExists() {
        createFileLock.withLock {
            if(saveDir.exists().not()) {
                saveDir.mkdirs()
            }
            if (!settingsFile.exists()) {
                settingsFile.createNewFile()
            }
            if (!settingsBakFile.exists()) {
                settingsBakFile.createNewFile()
            }
        }
    }
    private suspend fun readSettingsFromFile(useBak: Boolean) {
        createFileIfNonExists()
        val settingsStr = if(useBak) settingsBakFile.readText() else settingsFile.readText();
        if(settingsStr.isBlank()) {  
            val newSettings = getNewSettings()
            appSettings = newSettings
            doJobThenOffLoading {
                saveChannel.send(newSettings)
            }
        }else {  
            val newSettings = JsonUtil.j.decodeFromString<AppSettings>(settingsStr)
            appSettings = newSettings
            migrateIfNeed()
        }
    }
    private fun migrateIfNeed() {
        val oldVersion = appSettings.version
        val newVersion = SettingsVersion.appSettingsCurVersion
        if(oldVersion != newVersion) {
        }
    }
    private fun getNewSettings():AppSettings {
        val newSettings = AppSettings()
        newSettings.version = SettingsVersion.appSettingsCurVersion  
        return newSettings
    }
    fun getSettingsSnapshot():AppSettings {
        return getSettingsSnapshotFromSrc(appSettings)
    }
    private fun getSettingsSnapshotFromSrc(src:AppSettings):AppSettings {
        return src.copy()
    }
    fun update(requireReturnSnapshotOfUpdatedSettings:Boolean=false, modifySettings:(AppSettings)->Unit):AppSettings? {
        val settingsForUpdate = getSettingsSnapshot()  
        modifySettings(settingsForUpdate)  
        updateSettings(settingsForUpdate)  
        return if(requireReturnSnapshotOfUpdatedSettings) getSettingsSnapshotFromSrc(settingsForUpdate) else null  
    }
    fun updateSettingsIfNotEqualsWithOld(newSettings: AppSettings) {  
        doJobThenOffLoading {
            try {
                val tmpSettings = appSettings
                if (newSettings.version != tmpSettings.version || !newSettings.equals(tmpSettings)) {  
                    saveChannel.send(newSettings)
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#updateSettingsIfNotEqualsWithOld(): update config err!\n"+e.stackTraceToString())
            }
        }
    }
    fun updateSettings(newSettings: AppSettings) {  
        doJobThenOffLoading {
            try{
                saveChannel.send(newSettings)
            }catch (e:Exception) {
                MyLog.e(TAG, "#updateSettings(): update config err!\n"+e.stackTraceToString())
            }
        }
    }
    private suspend fun saveSettings(newSettings: AppSettings) {
        createFileIfNonExists()
        saveSettingsBak(newSettings)
        copyBakToOrigin()
    }
    @OptIn(ExperimentalSerializationApi::class)
    fun saveSettingsBak(newSettings: AppSettings) {
        JsonUtil.j.encodeToStream(newSettings, settingsBakFile.outputStream())
    }
    @OptIn(ExperimentalSerializationApi::class)
    fun saveSettingsOrigin(newSettings: AppSettings) {
        JsonUtil.j.encodeToStream(newSettings, settingsFile.outputStream())
    }
    fun copyBakToOrigin() {
        settingsBakFile.copyTo(settingsFile, overwrite = true)
    }
    fun delSettingsFile(settingsSaveDir:File, delOrigin:Boolean=true, delBak:Boolean=true) {
        if(delOrigin) {
            File(settingsSaveDir.canonicalPath, settingsFileName).delete()
        }
        if(delBak) {
            File(settingsSaveDir.canonicalPath, settingsBakFileName).delete()
        }
    }
    fun obtainLastQuitHomeScreen() = appSettings.lastQuitHomeScreen
    fun editorTabIndentCount() = appSettings.editor.tabIndentSpacesCount
    fun isEditorSyntaxHighlightEnabled() = appSettings.editor.syntaxHighlightEnabled
    fun isEditorUseSystemFonts() = appSettings.editor.useSystemFonts
    fun isDiffUseSystemFonts() = appSettings.diff.useSystemFonts
    fun isDiffSyntaxHighlightEnabled() = appSettings.diff.syntaxHighlightEnabled
    fun isCommitMsgPreviewModeOn() = appSettings.commitMsgPreviewModeOn
    fun isCommitMsgUseSystemFonts() = appSettings.commitMsgUseSystemFonts
    fun isEnabledDetailsCompareForDiff() = appSettings.diff.enableDetailsCompare
    fun isEditorAutoCloseSymbolPairEnabled() = appSettings.editor.autoCloseSymbolPair
    fun editorThresholdLinesCountOfIncrementAnalyze() = appSettings.editor.thresholdLinesCountOfIncrementAnalyze
    fun obtainEditorFileAssociationList() = appSettings.editor.fileAssociationList
    fun sshAllowUnknownHosts() = appSettings.sshSetting.allowUnknownHosts
    fun httpSslVerify() = appSettings.httpSetting.sslVerify
    fun pullWithRebase() = appSettings.globalGitConfig.pullWithRebase
}
