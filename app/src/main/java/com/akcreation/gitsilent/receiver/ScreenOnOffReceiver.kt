package com.akcreation.gitsilent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.service.AutomationService
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.settings.util.AutomationUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.cache.AutoSrvCache
import com.akcreation.gitsilent.utils.doJob
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.generateRandomString
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

private const val TAG = "ScreenOnOffReceiver"
class ScreenOnOffReceiver : BroadcastReceiver() {
    private var job = mutableStateOf<Job?>(null)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            MyLog.d(TAG, "Screen is OFF")
            val lastPackage = AutoSrvCache.getCurPackageName()
            if(lastPackage.isBlank()) { 
                return
            }
            AutomationService.targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastPackage] = false
            AutomationService.appLeaveTime[lastPackage] = System.currentTimeMillis()
            job.value = doJob job@{
                try {
                    val settings = SettingsUtil.getSettingsSnapshot()
                    val repoList = AutomationUtil.getRepos(settings.automation, lastPackage)
                    if(repoList.isNullOrEmpty()) {
                        return@job
                    }
                    AutomationUtil.groupReposByPushDelayTime(
                        lastPackage,
                        repoList,
                        settings
                    ).forEachBetter forEach@{ pushDelayInSec, repoList ->
                        if(repoList.isEmpty()) {
                            return@forEach
                        }
                        if (pushDelayInSec >= 0L) {
                            val pushDelayInMillSec = pushDelayInSec * 1000L
                            if (pushDelayInMillSec > 0L) {
                                val startAt = System.currentTimeMillis()
                                while (true) {
                                    delay(Cons.pushDelayCheckFrquencyInMillSec)
                                    if ((System.currentTimeMillis() - startAt) > pushDelayInMillSec) {
                                        break
                                    }
                                }
                            }
                            AutomationService.pushRepoList(sessionId = "ScrOffAutoPush_"+generateRandomString(), settings, repoList)
                        }else {
                            MyLog.d(TAG, "push delay less than 0, push canceled")
                        }
                    }
                }catch (e:Exception) {
                    MyLog.e(TAG, "push canceled by err: ${e.stackTraceToString()}")
                }finally {
                    job.value = null
                }
            }
        } else if (intent?.action == Intent.ACTION_SCREEN_ON) {
            MyLog.d(TAG, "Screen is ON")
            runCatching {
                job.value?.cancel()
                job.value = null
            }
        }
    }
}
