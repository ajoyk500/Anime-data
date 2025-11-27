package com.akcreation.gitsilent.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val READ_PERMISSION = 2
const val WRITE_PERMISSION = 3
private fun trueRequestManageStorageFalseRequestRwStorage(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}
fun Activity.getStoragePermission() {
    if(trueRequestManageStorageFalseRequestRwStorage()){
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    } else {
        requireWriteStoragePermission()
        requireReadStoragePermission()
    }
}
private fun Activity.requireReadStoragePermission() {
    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED
    )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_PERMISSION
        )
}
private fun Activity.requireWriteStoragePermission() {
    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED
    )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_PERMISSION
        )
}
fun hasManageStoragePermission(context: Context): Boolean {
    return if(trueRequestManageStorageFalseRequestRwStorage()) {
        Environment.isExternalStorageManager()
    }else {
        (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        &&
        (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }
}
fun requestStoragePermissionIfNeed(activityContext: Context, TAG: String): Boolean {
    return try {
        if (hasManageStoragePermission(activityContext)) {
            MyLog.d(TAG, "already has manage storage permission")
            false
        } else {
            MyLog.d(TAG, "no manage storage permission, will request...")
            ActivityUtil.getManageStoragePermissionOrShowFailedMsg(activityContext)
            true
        }
    } catch (e: Exception) {
        MyLog.d(TAG, "check and request manage storage permission err: ${e.stackTraceToString()}")
        false
    }
}
