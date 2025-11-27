package com.akcreation.gitsilent.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.akcreation.gitsilent.syntaxhighlight.base.TextMateUtil
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.StorageDirCons
import com.akcreation.gitsilent.data.AppContainer
import com.akcreation.gitsilent.data.AppDataContainer
import com.akcreation.gitsilent.dev.DevFeature
import com.akcreation.gitsilent.dev.FlagFileName
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.jni.LibLoader
import com.akcreation.gitsilent.notification.util.NotifyUtil
import com.akcreation.gitsilent.BuildConfig
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.service.HttpService
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.app.upgrade.migrator.AppMigrator
import com.akcreation.gitsilent.utils.app.upgrade.migrator.AppVersionMan
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.cache.CommitCache
import com.akcreation.gitsilent.utils.cert.CertMan
import com.akcreation.gitsilent.utils.encrypt.MasterPassUtil
import com.akcreation.gitsilent.utils.fileopenhistory.FileOpenHistoryMan
import com.akcreation.gitsilent.utils.pref.PrefMan
import com.akcreation.gitsilent.utils.pref.PrefUtil
import com.akcreation.gitsilent.utils.saf.SafUtil
import com.akcreation.gitsilent.utils.snapshot.SnapshotUtil
import com.akcreation.gitsilent.utils.storagepaths.StoragePathsMan
import com.akcreation.gitsilent.utils.time.TimeZoneMode
import com.akcreation.gitsilent.utils.time.TimeZoneUtil
import com.github.git24j.core.Libgit2
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.ZoneOffset

private const val TAG ="AppModel"
object AppModel {
    val showChangelogDialog = mutableStateOf(false)
    const val appPackageName = BuildConfig.APPLICATION_ID
    var masterPassword:MutableState<String> = mutableStateOf("")
    var devModeOn = false
    lateinit var realAppContext:Context
    lateinit var dbContainer: AppContainer
    lateinit var navController:NavHostController
    private var systemTimeZoneOffsetInMinutes:Int? = null
    private var timeZoneOffset:ZoneOffset?=null
    private var timeZoneOffsetInMinutes:Int?=null
    private var timeZoneMode:TimeZoneMode?=null
    val timezoneCacheMap:MutableMap<Int, String> = ConcurrentMap()
    @OptIn(ExperimentalMaterial3Api::class)
    lateinit var homeTopBarScrollBehavior: TopAppBarScrollBehavior
    lateinit var allRepoParentDir: File  
    var exitApp: ()->Unit = {}
    lateinit var externalFilesDir: File
    lateinit var externalCacheDir: File
    var innerCacheDir: File? = null
    lateinit var innerDataDir: File
    var externalDataDir: File? = null
    lateinit var certBundleDir: File
    lateinit var certUserDir: File
    lateinit var appDataUnderAllReposDir: File
    private lateinit var fileSnapshotDir: File  
    private lateinit var editCacheDir: File
    private lateinit var patchDir: File
    private lateinit var settingsDir: File
    private lateinit var logDir: File
    private lateinit var submoduleDotGitBackupDir: File
    object PuppyGitUnderGitDirManager {
        const val dirName = "PuppyGit"
        fun getDir(gitRepoDotGitDir:String):File {
            val puppyGitUnderGit = File(gitRepoDotGitDir, dirName)
            if(!puppyGitUnderGit.exists()) {
                puppyGitUnderGit.mkdirs()
            }
            return puppyGitUnderGit
        }
    }
    fun init_1(realAppContext:Context, exitApp:()->Unit, initActivity:Boolean) {
        LibLoader.load()
        Libgit2.init();
        Libgit2.optsGitOptSetOwnerValidation(false)
        AppModel.dbContainer = AppDataContainer(realAppContext)
        AppModel.devModeOn = PrefUtil.getDevMode(realAppContext)
        DevFeature.showRandomLaunchingText.state.value = PrefUtil.getShowRandomLaunchingText(realAppContext)
        AppModel.realAppContext = realAppContext
        AppModel.masterPassword.value = MasterPassUtil.get(realAppContext)
        val externalFilesDir = getExternalFilesIfErrGetInnerIfStillErrThrowException(realAppContext)
        val externalCacheDir = getExternalCacheDirIfErrGetInnerIfStillErrThrowException(realAppContext)
        val innerDataDir = getInnerDataDirOrThrowException(realAppContext)
        AppModel.externalFilesDir = externalFilesDir
        AppModel.externalCacheDir = externalCacheDir
        AppModel.innerDataDir = innerDataDir
        AppModel.innerCacheDir = getInnerCacheDirOrNull(realAppContext)
        AppModel.externalDataDir = getExternalDataDirOrNull(realAppContext)
        AppModel.allRepoParentDir = createDirIfNonexists(externalFilesDir, Cons.defaultAllRepoParentDirName)
        StorageDirCons.DefaultStorageDir.puppyGitRepos.fullPath = AppModel.allRepoParentDir.canonicalPath
        AppModel.appDataUnderAllReposDir = createDirIfNonexists(AppModel.allRepoParentDir, Cons.defalutPuppyGitDataUnderAllReposDirName)
        AppModel.logDir = createDirIfNonexists(AppModel.appDataUnderAllReposDir, Cons.defaultLogDirName)
        MyLog.init(
            logDir=AppModel.getOrCreateLogDir(),
            logKeepDays=PrefMan.getInt(realAppContext, PrefMan.Key.logKeepDays, MyLog.fileKeepDays),
            logLevel=PrefMan.getChar(realAppContext, PrefMan.Key.logLevel, MyLog.myLogLevel),
        )
        AppModel.certBundleDir = createDirIfNonexists(AppModel.appDataUnderAllReposDir, CertMan.defaultCertBundleDirName)
        AppModel.certUserDir = createDirIfNonexists(AppModel.appDataUnderAllReposDir, CertMan.defaultCertUserDirName)
        AppModel.fileSnapshotDir = createDirIfNonexists(AppModel.appDataUnderAllReposDir, Cons.defaultFileSnapshotDirName)
        AppModel.editCacheDir = createDirIfNonexists(AppModel.appDataUnderAllReposDir, Cons.defaultEditCacheDirName)
        AppModel.patchDir = createDirIfNonexists(AppModel.appDataUnderAllReposDir, Cons.defaultPatchDirName)
        AppModel.settingsDir = createDirIfNonexists(AppModel.appDataUnderAllReposDir, Cons.defaultSettingsDirName)
        AppModel.submoduleDotGitBackupDir = createDirIfNonexists(AppModel.appDataUnderAllReposDir, Cons.defaultSubmoduleDotGitFileBakDirName)
        SafUtil.init(AppModel.appDataUnderAllReposDir)
        NotifyUtil.initAllNotify(realAppContext)
        if(initActivity) {
            updateExitApp(exitApp)
        }
        dev_EnableUnTestedFeature = try {
            File(AppModel.appDataUnderAllReposDir, FlagFileName.enableUnTestedFeature).exists()
        }catch (_:Exception) {
            false
        }
    }
    fun updateExitApp(
        exit:()->Unit
    ) {
        AppModel.exitApp = exit
    }
    suspend fun init_2() {
        val funName = "init_2"
        val applicationContext = AppModel.realAppContext
        val settingsSaveDir = AppModel.getOrCreateSettingsDir()
        try {
            SettingsUtil.init(settingsSaveDir, useBak = false)
        }catch (e:Exception) {
            try {
                MyLog.e(TAG, "#$funName init settings err: "+e.stackTraceToString())
                MyLog.w(TAG, "#$funName init origin settings err, will try use backup")
                SettingsUtil.init(settingsSaveDir, useBak = true)
                MyLog.w(TAG, "#$funName init bak settings success, will restore it to origin")
                SettingsUtil.copyBakToOrigin()  
                MyLog.w(TAG, "#$funName restore bak settings to origin success")
            }catch (e2:Exception) {
                MyLog.e(TAG, "#$funName init settings with bak err: "+e2.stackTraceToString())
                MyLog.w(TAG, "#$funName init bak settings err, will clear origin settings, user settings will lost!")
                SettingsUtil.delSettingsFile(settingsSaveDir)
                MyLog.w(TAG, "#$funName del settings success, will reInit settings, if failed, app will not work...")
                SettingsUtil.init(settingsSaveDir, useBak = false)
                MyLog.w(TAG, "#$funName reInit settings success")
            }
        }
        val settings = SettingsUtil.getSettingsSnapshot()
        reloadTimeZone(settings)
        CertMan.init(applicationContext, AppModel.certBundleDir, AppModel.certUserDir)  
        Lg2HomeUtils.init(AppModel.appDataUnderAllReposDir, applicationContext)
        DevFeature.singleDiff.state.value = settings.devSettings.singleDiffOn
        DevFeature.treatNoWordMatchAsNoMatchedForDiff.state.value = settings.devSettings.treatNoWordMatchAsNoMatchedForDiff
        DevFeature.degradeMatchByWordsToMatchByCharsIfNonMatched.state.value = settings.devSettings.degradeMatchByWordsToMatchByCharsIfNonMatched
        DevFeature.showMatchedAllAtDiff.state.value = settings.devSettings.showMatchedAllAtDiff
        DevFeature.legacyChangeListLoadMethod.state.value = settings.devSettings.legacyChangeListLoadMethod
        try {
            EditCache.init(
                enableCache = settings.editor.editCacheEnable,
                cacheDir = AppModel.getOrCreateEditCacheDir(),
                keepInDays = settings.editor.editCacheKeepInDays
            )
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName init EditCache err: "+e.stackTraceToString())
        }
        try {
            SnapshotUtil.init(
                enableContentSnapshotForEditorInitValue = settings.editor.enableContentSnapshot || File(AppModel.appDataUnderAllReposDir, FlagFileName.enableContentSnapshot).exists(),
                enableFileSnapshotForEditorInitValue = settings.editor.enableFileSnapshot || File(AppModel.appDataUnderAllReposDir, FlagFileName.enableFileSnapshot).exists(),
                enableFileSnapshotForDiffInitValue = settings.diff.createSnapShotForOriginFileBeforeSave
            )
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName init SnapshotUtil err: "+e.stackTraceToString())
        }
        try {
            FileOpenHistoryMan.init(
                saveDir = settingsSaveDir,
                limit = settings.editor.fileOpenHistoryLimit,
                requireClearOldSettingsEditedHistory = false
            )
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName init FileOpenHistoryMan err: "+e.stackTraceToString())
        }
        try {
            StoragePathsMan.init(
                saveDir = settingsSaveDir,
                oldSettingsStoragePaths = null,
                oldSettingsLastSelectedPath = null
            )
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName init StoragePathsMan err: "+e.stackTraceToString())
        }
        doJobThenOffLoading {
            try {
                MyLog.delExpiredFiles()
            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName del expired log files err: "+e.stackTraceToString())
            }
            try {
                EditCache.delExpiredFiles()
            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName del expired edit cache files err: "+e.stackTraceToString())
            }
            try {
                val snapshotKeepInDays = settings.snapshotKeepInDays
                val snapshotSaveFolder = AppModel.getOrCreateFileSnapshotDir()
                FsUtils.delFilesOverKeepInDays(snapshotKeepInDays, snapshotSaveFolder, "snapshot folder")
            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName del expired snapshot files err: "+e.stackTraceToString())
            }
        }
        AppVersionMan.init migrate@{ oldVer ->
            if(oldVer == AppVersionMan.err_fileNonExists || oldVer == AppVersionMan.err_parseVersionFailed
                || oldVer != AppVersionMan.currentVersion
            ) {
                showChangelogDialog.value = true
                return@migrate true
            }
            if(oldVer == AppVersionMan.currentVersion) {
                return@migrate true
            }
            if(oldVer < 48 && AppVersionMan.currentVersion >= 48) {
                val success = AppMigrator.sinceVer48()
                if(!success) {
                    return@migrate false
                }
            }
            return@migrate true
        }
        if(settings.httpService.launchOnAppStartup) {
            HttpService.start(applicationContext)
        }
        TextMateUtil.doInit(applicationContext)
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun init_3(){
        AppModel.navController = rememberNavController()
        AppModel.homeTopBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    }
    @Composable
    fun getAppIcon(context: Context, inDarkTheme:Boolean = Theme.inDarkTheme) : ImageVector{
        return ImageVector.vectorResource(if(inDarkTheme)  R.drawable.icon_dark_foreground else R.drawable.icon_light_foreground)
    }
    @Composable
    fun getAppIconMonoChrome(context: Context): ImageVector {
        return ImageVector.vectorResource(R.drawable.icon_monochrome)
    }
    fun getAppVersionCode():Int {
        return BuildConfig.VERSION_CODE
    }
    fun getAppVersionName():String {
        return BuildConfig.VERSION_NAME
    }
    fun getAppVersionNameAndCode():String {
        return getAppVersionName()+"v"+getAppVersionCode()
    }
    fun getStringByResKey(context: Context, resKey: String): String {
        val funName = "getStringByResKey"
        try {
            val res = context.resources
            val resType = "string"
            return res.getString(res.getIdentifier(resKey, resType, appPackageName))
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
            return ""
        }
    }
    fun destroyer() {
        runCatching {
            Cache.clearFilesListStates()
        }
        runCatching {
            runBlocking { CommitCache.clear() }
        }
        SharedState.homeCodeEditor?.releaseAndClearUndoStack()
    }
    fun getOrCreatePuppyGitDataUnderAllReposDir():File{
        if(!appDataUnderAllReposDir.exists()) {
            appDataUnderAllReposDir.mkdirs()
        }
        return appDataUnderAllReposDir
    }
    fun getOrCreateFileSnapshotDir():File{
        if(!fileSnapshotDir.exists()) {
            fileSnapshotDir.mkdirs()
        }
        return fileSnapshotDir
    }
    fun getOrCreateEditCacheDir():File{
        if(!editCacheDir.exists()) {
            editCacheDir.mkdirs()
        }
        return editCacheDir
    }
    fun getOrCreatePatchDir():File{
        if(!patchDir.exists()) {
            patchDir.mkdirs()
        }
        return patchDir
    }
    fun getOrCreateSettingsDir():File{
        if(!settingsDir.exists()) {
            settingsDir.mkdirs()
        }
        return settingsDir
    }
    fun getOrCreateLogDir():File {
        if(!logDir.exists()) {
            logDir.mkdirs()
        }
        return logDir
    }
    fun getOrCreateSubmoduleDotGitBackupDir():File {
        if(!submoduleDotGitBackupDir.exists()) {
            submoduleDotGitBackupDir.mkdirs()
        }
        return submoduleDotGitBackupDir
    }
    fun getOrCreateExternalCacheDir():File{
        if(externalCacheDir.exists().not()) {
            externalCacheDir.mkdirs()
        }
        return externalCacheDir
    }
    fun requireMasterPassword(settings: AppSettings = SettingsUtil.getSettingsSnapshot()):Boolean {
        return (
                (settings.masterPasswordHash.isNotEmpty() && masterPassword.value.isEmpty())
            || (settings.masterPasswordHash.isNotEmpty() && masterPassword.value.isNotEmpty() && !HashUtil.verify(masterPassword.value, settings.masterPasswordHash))
        )
    }
    fun masterPasswordEnabled(): Boolean {
        return masterPassword.value.isNotEmpty()
    }
    fun getAppTimeZoneOffsetCached(settings: AppSettings? = null) : ZoneOffset {
        if(timeZoneOffset == null) {
            reloadTimeZone(settings ?: SettingsUtil.getSettingsSnapshot())
        }
        return timeZoneOffset!!
    }
    fun getAppTimeZoneOffsetInMinutesCached(settings: AppSettings? = null) : Int {
        if(timeZoneOffsetInMinutes == null) {
            reloadTimeZone(settings ?: SettingsUtil.getSettingsSnapshot())
        }
        return timeZoneOffsetInMinutes!!
    }
    fun getAppTimeZoneModeCached(settings: AppSettings? = null) : TimeZoneMode {
        if(timeZoneMode == null) {
            reloadTimeZone(settings ?: SettingsUtil.getSettingsSnapshot())
        }
        return timeZoneMode!!
    }
    fun reloadTimeZone(settings: AppSettings){
        systemTimeZoneOffsetInMinutes = try {
            getSystemDefaultTimeZoneOffset().totalSeconds / 60
        }catch (e:Exception) {
            MyLog.e(TAG, "#reloadTimeZone() get system timezone offset in minutes err, will use UTC+0, err is: ${e.stackTraceToString()}")
            0
        }
        MyLog.d(TAG, "#reloadTimeZone(): new value of systemTimeZoneOffsetInMinutes=$systemTimeZoneOffsetInMinutes")
        timeZoneOffsetInMinutes = readTimeZoneOffsetInMinutesFromSettingsOrDefault(settings, systemTimeZoneOffsetInMinutes!!)
        timeZoneOffset = ZoneOffset.ofTotalSeconds(timeZoneOffsetInMinutes!! * 60)
        timeZoneMode = TimeZoneUtil.getAppTimeZoneMode(settings)
        MyLog.d(TAG, "#reloadTimeZone(): new value of App TimeZone: timeZoneMode=$timeZoneMode, timeZoneOffsetInMinutes=$timeZoneOffsetInMinutes, timeZoneOffset=$timeZoneOffset")
    }
    fun getSystemTimeZoneOffsetInMinutesCached(settings: AppSettings? = null):Int {
      if(systemTimeZoneOffsetInMinutes == null) {
          reloadTimeZone(settings ?: SettingsUtil.getSettingsSnapshot())
      }
      return systemTimeZoneOffsetInMinutes!!
    }
    @Composable
    fun init_forPreview() {
        val realAppContext = LocalContext.current
        AppModel.dbContainer = AppDataContainer(realAppContext)
        AppModel.devModeOn = PrefUtil.getDevMode(realAppContext)
        AppModel.realAppContext = realAppContext
        AppModel.masterPassword.value = MasterPassUtil.get(realAppContext)
        val externalFilesDir = File("/test_android_preview")
        val externalCacheDir = externalFilesDir
        val innerDataDir = externalFilesDir
        AppModel.externalFilesDir = externalFilesDir
        AppModel.externalCacheDir = externalCacheDir
        AppModel.innerDataDir = innerDataDir
        AppModel.innerCacheDir = externalFilesDir
        AppModel.externalDataDir = externalFilesDir
        AppModel.allRepoParentDir = externalFilesDir
        StorageDirCons.DefaultStorageDir.puppyGitRepos.fullPath = AppModel.allRepoParentDir.canonicalPath
        AppModel.appDataUnderAllReposDir = externalFilesDir
        AppModel.certBundleDir = externalFilesDir
        AppModel.certUserDir = externalFilesDir
        AppModel.fileSnapshotDir = externalFilesDir
        AppModel.editCacheDir = externalFilesDir
        AppModel.patchDir = externalFilesDir
        AppModel.settingsDir = externalFilesDir
        AppModel.logDir = externalFilesDir
        AppModel.submoduleDotGitBackupDir = externalFilesDir
        AppModel.exitApp = {}
        dev_EnableUnTestedFeature = try {
            File(AppModel.appDataUnderAllReposDir, FlagFileName.enableUnTestedFeature).exists()
        }catch (_:Exception) {
            false
        }
        reloadTimeZone(AppSettings())
        AppModel.init_3()
    }
    private val currentConfiguration = mutableStateOf<Configuration?>(null)
    @Composable
    fun getCurActivityConfig(): Configuration {
        return currentConfiguration.value ?: LocalConfiguration.current
    }
    fun handleActivityConfigurationChanged(newConfig: Configuration) {
        currentConfiguration.value = newConfig
    }
}
