package com.akcreation.gitsilent.service

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.notification.AutomationNotify
import com.akcreation.gitsilent.notification.base.ServiceNotify
import com.akcreation.gitsilent.notification.util.NotifyUtil
import com.akcreation.gitsilent.base.BaseAccessibilityService
import com.akcreation.gitsilent.receiver.ScreenOnOffReceiver
import com.akcreation.gitsilent.server.bean.NotificationSender
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.settings.util.AutomationUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.RepoActUtil
import com.akcreation.gitsilent.utils.cache.AutoSrvCache
import com.akcreation.gitsilent.utils.cache.NotifySenderMap
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.generateRandomString
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AutomationService: BaseAccessibilityService() {
    companion object {
        private const val TAG = "AutomationService"
        private val lock = Mutex()
        val targetPackageTrueOpenedFalseCloseNullNeverOpenedList = ConcurrentMap<String, Boolean>()
        val appLeaveTime = ConcurrentMap<String, Long>()
        private var lastTargetPackageName = ""  
        private val ignorePackageNames = listOf<String>(
            "com.android.systemui",  
            "android",  
            "com.oplus.notificationmanager",  
            "com.google.android.permissioncontroller",  
        )
        private fun createNotify(notifyId:Int):ServiceNotify {
            return ServiceNotify(AutomationNotify.create(notifyId))
        }
        private fun sendSuccessNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings) = { title:String?, msg:String?, startPage:Int?, startRepoId:String? ->
            if(settings.automation.showNotifyWhenProgress) {
                serviceNotify.sendSuccessNotification(title, msg, startPage, startRepoId)
            }
        }
        private fun sendErrNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings)={ title:String, msg:String, startPage:Int, startRepoId:String ->
            if(settings.automation.showNotifyWhenProgress) {
                serviceNotify.sendErrNotification(title, msg, startPage, startRepoId)
            }
        }
        private fun sendProgressNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings) = { repoNameOrId:String, progress:String ->
            if(settings.automation.showNotifyWhenProgress) {
                serviceNotify.sendProgressNotification(repoNameOrId, progress)
            }
        }
        private suspend fun pullRepoList(
            sessionId:String,
            settings: AppSettings,
            repoList:List<RepoEntity>,
        ) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#pullRepoList: generate notifyers for ${repoList.size} repos")
            }
            repoList.forEachBetter {
                val serviceNotify = createNotify(NotifyUtil.genId())
                NotifySenderMap.set(
                    NotifySenderMap.genKey(it.id, sessionId),
                    NotificationSender(
                        sendErrNotificationIfEnable(serviceNotify, settings),
                        sendSuccessNotificationIfEnable(serviceNotify, settings),
                        sendProgressNotificationIfEnable(serviceNotify, settings),
                    )
                )
            }
            RepoActUtil.pullRepoList(
                sessionId,
                repoList,
                routeName = "'auto pull service'",
                gitUsernameFromUrl="",
                gitEmailFromUrl="",
                pullWithRebase = SettingsUtil.pullWithRebase(),
            )
        }
        suspend fun pushRepoList(
            sessionId:String,
            settings: AppSettings,
            repoList:List<RepoEntity>,
        ) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#pushRepoList: generate notifyers for ${repoList.size} repos")
            }
            repoList.forEachBetter {
                val serviceNotify = createNotify(NotifyUtil.genId())
                NotifySenderMap.set(
                    NotifySenderMap.genKey(it.id, sessionId),
                    NotificationSender(
                        sendErrNotificationIfEnable(serviceNotify, settings),
                        sendSuccessNotificationIfEnable(serviceNotify, settings),
                        sendProgressNotificationIfEnable(serviceNotify, settings),
                    )
                )
            }
            RepoActUtil.pushRepoList(
                sessionId,
                repoList,
                routeName = "'auto push service'",
                gitUsernameFromUrl="",
                gitEmailFromUrl="",
                autoCommit=true,
                force=false,
            )
        }
    }
    private var screenOnOffReceiver:ScreenOnOffReceiver? = null
    override fun onCreate() {
        super.onCreate()
        AppModel.init_1(realAppContext = applicationContext, exitApp = {}, initActivity = false)
        runBlocking {
            AppModel.init_2()
        }
        registerScreenOnOffReceiver()
        MyLog.d(TAG, "#onCreate() finished")
    }
    private fun registerScreenOnOffReceiver() {
        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
            screenOnOffReceiver = ScreenOnOffReceiver().let {
                registerReceiver(it, filter)
                it
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#registerScreenOnOffReceiver() err: ${e.stackTraceToString()}")
        }
    }
    private fun unregisterScreenOnOffReceiver() {
        try {
            screenOnOffReceiver?.let { unregisterReceiver(it) }
            screenOnOffReceiver = null
        }catch (e:Exception) {
            MyLog.e(TAG, "#unregisterScreenOnOffReceiver() err: ${e.stackTraceToString()}")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterScreenOnOffReceiver()
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(event == null) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#onAccessibilityEvent: event is null")
            }
            return
        }
        if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName.toString()
            if(AppModel.devModeOn) {
                MyLog.v(TAG, "TYPE_WINDOW_STATE_CHANGED: $packageName")
            }
            if(ignorePackageNames.contains(packageName)) {
                if(AppModel.devModeOn) {
                    MyLog.d(TAG, "ignore package name: '$packageName'")
                }
                return
            }
            try {
                if((getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.enabledInputMethodList?.find { it.packageName == packageName } != null) {
                    if(AppModel.devModeOn) {
                        MyLog.d(TAG, "ignore input method (soft keyboard): '$packageName'")
                    }
                    return
                }
            }catch (e:Exception) {
                MyLog.d(TAG, "get enabledInputMethodList err: ${e.stackTraceToString()}")
            }
            val settings = SettingsUtil.getSettingsSnapshot()
            val targetPackageList = AutomationUtil.getPackageNames(settings.automation)
            if(targetPackageList.isEmpty()) {
                return
            }
            doJobThenOffLoading {
                val event = Unit  
                lock.withLock {
                    val sessionId = generateRandomString()
                    if(targetPackageList.contains(packageName)) {  
                        AutoSrvCache.setCurPackageName(packageName)
                        lastTargetPackageName = packageName
                        val lastTargetPackageName = Unit  
                        MyLog.d(TAG, "target packageName '$packageName' opened, checking need pull or no....")
                        val targetOpened = targetPackageTrueOpenedFalseCloseNullNeverOpenedList[packageName] == true
                        if(!targetOpened) { 
                            targetPackageTrueOpenedFalseCloseNullNeverOpenedList[packageName] = true
                            MyLog.d(TAG, "target packageName '$packageName' opened, need do pull")
                            val repoList = AutomationUtil.getRepos(settings.automation, packageName)
                            if(repoList.isNullOrEmpty()) {
                                return@withLock
                            }
                            val repoListWillDoAct = mutableListOf<RepoEntity>()
                            val nowInMillSec = System.currentTimeMillis()
                            val lastLeaveAt = appLeaveTime[packageName] ?: 0L ;
                            repoList.forEachBetter {
                                val pullIntervalInSec = AutomationUtil.getAppAndRepoSpecifiedSettings(packageName, it.id, settings).getPullIntervalActuallyValue(settings)
                                if(pullIntervalInSec >= 0L) {
                                    val pullIntervalInMillSec = pullIntervalInSec * 1000L
                                    if(pullIntervalInMillSec == 0L || lastLeaveAt == 0L || (nowInMillSec - lastLeaveAt) > pullIntervalInMillSec) {
                                        repoListWillDoAct.add(it)
                                    }
                                }else {
                                    MyLog.d(TAG, "Repo '${it.repoName}': pull interval less than 0, pull canceled")
                                }
                            }
                            repoListWillDoAct.let {
                                if(it.isNotEmpty()) {
                                    pullRepoList(sessionId, settings, it)
                                }
                            }
                        }else {
                            MyLog.d(TAG, "target packageName '$packageName' opened but no need do pull")
                        }
                    }else { 
                        AutoSrvCache.setCurPackageName("")
                        if(lastTargetPackageName.isNotBlank()) { 
                            val packageName = Unit  
                            val lastOpenedTarget = lastTargetPackageName
                            lastTargetPackageName = ""
                            val lastTargetPackageName = Unit  
                            if(targetPackageList.contains(lastOpenedTarget)) {
                                MyLog.d(TAG, "target packageName '$lastOpenedTarget' leaved, checking need push or no...")
                                val targetOpened = targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastOpenedTarget] == true
                                if(targetOpened) { 
                                    MyLog.d(TAG, "target packageName '$lastOpenedTarget' leaved, need do push")
                                    targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastOpenedTarget] = false
                                    appLeaveTime[lastOpenedTarget] = System.currentTimeMillis()
                                    val repoList = AutomationUtil.getRepos(settings.automation, lastOpenedTarget)
                                    if(repoList.isNullOrEmpty()) {
                                        return@withLock
                                    }
                                    AutomationUtil.groupReposByPushDelayTime(
                                        lastOpenedTarget,
                                        repoList,
                                        settings
                                    ).forEachBetter mark@{ pushDelayInSec, repoList ->
                                        if(repoList.isEmpty()) {
                                            return@mark
                                        }
                                        if(pushDelayInSec >= 0L) {
                                            doJobThenOffLoading pushTask@{
                                                val pushDelayInMillSec =  pushDelayInSec * 1000L
                                                var taskCanceled = false
                                                if(pushDelayInMillSec > 0L) {
                                                    val startAt = System.currentTimeMillis()
                                                    while (true) {
                                                        delay(Cons.pushDelayCheckFrquencyInMillSec)
                                                        if(
                                                            targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastOpenedTarget] == true
                                                            || startAt < (appLeaveTime[lastOpenedTarget] ?: 0L)
                                                        ) {
                                                            taskCanceled = true
                                                            break
                                                        }
                                                        if((System.currentTimeMillis() - startAt) > pushDelayInMillSec) {
                                                            break
                                                        }
                                                    }
                                                }
                                                if(!taskCanceled) {
                                                    var repoList = repoList
                                                    val curShowingPackageName = AutoSrvCache.getCurPackageName()
                                                    if(curShowingPackageName.isNotBlank()) {
                                                        val repoListOfCurShowingPackage = AutomationUtil.getRepos(settings.automation, curShowingPackageName)
                                                        if(!repoListOfCurShowingPackage.isNullOrEmpty()) {
                                                            val newList = mutableListOf<RepoEntity>()
                                                            repoList.forEachBetter { r1 ->
                                                                var contains = false
                                                                for(r2 in repoListOfCurShowingPackage) {
                                                                    if(r1.id == r2.id) {
                                                                        contains = true
                                                                        break
                                                                    }
                                                                }
                                                                if(contains.not()) {
                                                                    newList.add(r1)
                                                                }
                                                            }
                                                            repoList = newList
                                                        }
                                                    }
                                                    if(repoList.isEmpty()) {
                                                        MyLog.d(TAG, "push cancelled, current app full-covered target repoList, will do push after current app leave")
                                                    }else {
                                                        pushRepoList(sessionId, settings, repoList)
                                                    }
                                                }
                                            }
                                        }else {
                                            MyLog.d(TAG, "push delay less than 0, push canceled")
                                        }
                                    }
                                }else {
                                    MyLog.d(TAG, "target packageName '$packageName' opened but no need do push")
                                }
                            }else {  
                                MyLog.d(TAG, "target packageName '$lastOpenedTarget' was in targetList but removed, will not do push for it")
                                targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastOpenedTarget] = false
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onInterrupt() {
        if(AppModel.devModeOn) {
            MyLog.w(TAG, "#onInterrupt: interrupted?")
        }
    }
}
