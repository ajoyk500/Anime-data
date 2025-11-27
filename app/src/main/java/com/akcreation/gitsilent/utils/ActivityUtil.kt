package com.akcreation.gitsilent.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.akcreation.gitsilent.activity.findActivity
import com.akcreation.gitsilent.R

private const val TAG = "ActivityUtil"
object ActivityUtil {
    fun restartActivityByIntent(activity:Activity, intent: Intent?) {
        activity.apply {
            val intent = intent ?: getIntent()
            finish()
            startActivity(intent)
        }
    }
    fun openUrl(context: Context, linkUrl:String) {
        val uri = Uri.parse(linkUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }
    fun openSpecifedAppInfoPage(context: Context, packageName:String) {
        if(packageName.isBlank()) {
            return
        }
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }
    fun openThisAppInfoPage(context: Context) {
        openSpecifedAppInfoPage(context, AppModel.appPackageName)
    }
    fun getManageStoragePermissionOrShowFailedMsg(context: Context) {
        val activity = context.findActivity()
        if(activity == null) {
            Msg.requireShowLongDuration(context.getString(R.string.please_go_to_system_settings_allow_manage_storage))
        }else {
            activity.getStoragePermission()
        }
    }
    fun startActivitySafe(activity: Activity?, intent: Intent, options: Bundle? = null) {
        if(activity == null) {
            Msg.requireShowLongDuration("Can't found Activity for action.")
            return
        }
        try {
            activity.startActivity(intent, options)
        } catch (e: ActivityNotFoundException) {
            Msg.requireShowLongDuration(activity.getString(R.string.activity_not_found))
        } catch (e: Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "#startActivitySafe() err: ${e.localizedMessage}")
        }
    }
}
