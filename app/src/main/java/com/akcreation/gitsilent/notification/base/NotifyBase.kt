package com.akcreation.gitsilent.notification.base

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.akcreation.gitsilent.notification.bean.Action
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.forEachBetter

abstract class NotifyBase(
    private val TAG:String, 
    private val channelId:String,
    private val channelName:String,
    private val channelDesc:String,
    private val actionList:((context:Context) -> List<Action>)? = null
) {
    companion object {
        lateinit var appContext:Context
    }
    abstract val notifyId:Int
    private val inited:MutableState<Boolean> = mutableStateOf(false)
    fun init(context: Context) {
        if(inited.value) {
            return
        }
        inited.value = true
        appContext = context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = channelDesc
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            MyLog.w(TAG, "notify channel '$channelId' registered")
        }
        MyLog.w(TAG, "notification '$notifyId' inited")
    }
    fun sendNotification(context: Context?, title: String?, message: String?, pendingIntent: PendingIntent?) {
        val context = context ?: appContext
        val builder = getNotificationBuilder(context, title, message, pendingIntent) 
        NotificationManagerCompat.from(context).apply {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                MyLog.e(TAG, "#sendNotification: send notification failed, permission 'POST_NOTIFICATIONS' not granted")
                return
            }
            notify(notifyId, builder.build())
        }
    }
    fun getNotificationBuilder(context: Context?, title: String?, message: String?, pendingIntent: PendingIntent?): NotificationCompat.Builder {
        val context = context ?: appContext
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.dog_head) 
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent) 
            .setAutoCancel(true) 
        actionList?.invoke(context)?.forEachBetter { action ->
            builder.addAction(action.iconId, action.text, action.pendingIntent)
        }
        return builder
    }
}
