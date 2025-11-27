package com.akcreation.gitsilent.notification.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.akcreation.gitsilent.constants.IntentCons
import com.akcreation.gitsilent.notification.AutomationNotify
import com.akcreation.gitsilent.notification.HttpServiceExecuteNotify
import com.akcreation.gitsilent.notification.HttpServiceHoldNotify
import com.akcreation.gitsilent.notification.NormalNotify
import com.akcreation.gitsilent.notification.base.NotifyBase
import com.akcreation.gitsilent.activity.MainActivity
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.forEachBetter
import kotlin.random.Random

object NotifyUtil {
    private val notifyList:List<NotifyBase> = listOf(
        NormalNotify.create(1),
        HttpServiceHoldNotify.create(2),
        HttpServiceExecuteNotify.create(3),
        AutomationNotify.create(4),
    )
    fun initAllNotify(appContext: Context) {
        notifyList.forEachBetter {
            it.init(appContext)
        }
    }
    fun sendNotificationClickGoToSpecifiedPage(notify: NotifyBase, title:String, msg:String, startPage:Int, startRepoId:String) {
        notify.sendNotification(
            null,
            title,
            msg,
            createPendingIntentGoToSpecifiedPage(null, startPage, startRepoId)
        )
    }
    fun createPendingIntent(context: Context?, extras:Map<String, String>): PendingIntent {
        val context = context ?: AppModel.realAppContext
        val intent = Intent(context, MainActivity::class.java) 
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        extras.forEachBetter { k, v ->
            intent.putExtra(k, v)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
    fun createPendingIntentGoToSpecifiedPage(context: Context?, startPage:Int, startRepoId:String):PendingIntent {
        return createPendingIntent(
            context,
            mapOf(
                IntentCons.ExtrasKey.startPage to startPage.toString(),
                IntentCons.ExtrasKey.startRepoId to startRepoId
            )
        )
    }
    fun genId():Int {
        return Random.nextInt(51, Int.MAX_VALUE)
    }
}
