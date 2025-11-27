package com.akcreation.gitsilent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.akcreation.gitsilent.service.HttpService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            if(HttpService.launchOnSystemStartUpEnabled(context)) {
                HttpService.start(context)
            }
        }
    }
}
