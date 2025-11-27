package com.akcreation.gitsilent.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.notification.HttpServiceHoldNotify
import com.akcreation.gitsilent.notification.base.NotifyBase
import com.akcreation.gitsilent.notification.util.NotifyUtil
import com.akcreation.gitsilent.base.BaseService
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.server.HttpServer
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.copyTextToClipboard
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.genHttpHostPortStr
import com.akcreation.gitsilent.utils.pref.PrefMan
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class HttpService : BaseService() {
    companion object {
        private const val TAG = "HttpService"
        private var httpServer: HttpServer? = null
        const val command_stop = "STOP"
        const val command_copy_addr = "COPY_ADDR"
        private val lock = Mutex()
        private suspend fun doActWithLock(act:suspend () -> Unit) {
            lock.withLock {
                act()
            }
        }
        private suspend fun runNewHttpServer(settings:AppSettings) {
            stopCurrentServer()
            val newServer = HttpServer(
                settings.httpService.listenHost,
                settings.httpService.listenPort
            )
            httpServer = newServer
            newServer.startServer()
        }
        private suspend fun stopCurrentServer() {
            httpServer?.stopServer()
        }
        fun launchOnSystemStartUpEnabled(context: Context):Boolean {
            return PrefMan.get(context, PrefMan.Key.launchServiceOnSystemStartup, "0") == "1"
        }
        fun setLaunchOnSystemStartUp(context: Context, enable:Boolean) {
            PrefMan.set(context, PrefMan.Key.launchServiceOnSystemStartup, if (enable) "1" else "0")
        }
        fun start(appContext: Context) {
            if(isRunning()) {
                MyLog.w(TAG, "HttpService already running, start canceled")
            }else {
                val serviceIntent = Intent(appContext, HttpService::class.java)
                appContext.startForegroundService(serviceIntent)
                MyLog.d(TAG, "HttpService started")
            }
            TileHttpService.sendUpdateTileRequest(appContext, true)
        }
        fun stop(appContext: Context) {
            val serviceIntent = Intent(appContext, HttpService::class.java)
            appContext.stopService(serviceIntent)
            MyLog.d(TAG, "HttpService stopped")
            TileHttpService.sendUpdateTileRequest(appContext, false)
        }
        fun isRunning() :Boolean {
            return httpServer?.isServerRunning() == true
        }
    }
    override fun onCreate() {
        super.onCreate()
        AppModel.init_1(realAppContext = applicationContext, exitApp = {}, initActivity = false)
        runBlocking {
            AppModel.init_2()
        }
        MyLog.w(TAG, "#onCreate() finished")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if(action == command_stop){ 
            stop(AppModel.realAppContext)
            MyLog.w(TAG, "#onStartCommand() stop finished")
        }else if(action == command_copy_addr){ 
            val settings = SettingsUtil.getSettingsSnapshot()
            copyTextToClipboard(
                context = applicationContext,
                label = "PuppyGit Http Addr",
                text = genHttpHostPortStr(settings.httpService.listenHost, settings.httpService.listenPort.toString())
            )
            Msg.requireShow(applicationContext.getString(R.string.copied))
        }else { 
            val settings = SettingsUtil.getSettingsSnapshot()
            val serviceNotify = HttpServiceHoldNotify.create(1)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { 
                startForeground(serviceNotify.notifyId, getNotification(serviceNotify, settings), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            }else {
                startForeground(serviceNotify.notifyId, getNotification(serviceNotify, settings))
            }
            doJobThenOffLoading {
                doActWithLock {
                    runNewHttpServer(settings)
                }
            }
            MyLog.w(TAG, "#onStartCommand() start finished")
        }
        return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        doJobThenOffLoading {
            doActWithLock {
                stopCurrentServer()
            }
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        MyLog.w(TAG, "#onDestroy() finished")
    }
    private fun getNotification(notifyBase: NotifyBase, settings: AppSettings): Notification {
        val builder = notifyBase.getNotificationBuilder(
            this,
            "PuppyGit Service",
            "Listen on: ${genHttpHostPortStr(settings.httpService.listenHost, settings.httpService.listenPort.toString())}",
            NotifyUtil.createPendingIntentGoToSpecifiedPage(applicationContext, Cons.selectedItem_Service, startRepoId = ""),
        )
        return builder.build()
    }
}
