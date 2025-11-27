package com.akcreation.gitsilent.utils.app.upgrade.migrator

import androidx.room.withTransaction
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.fileopenhistory.FileOpenHistoryMan

private const val TAG = "AppMigrator"
object AppMigrator {
    suspend fun sinceVer48():Boolean {
        val funName = "sinceVer48"
        val systemDefaultTimeOffsetInSec = (AppModel.getSystemTimeZoneOffsetInMinutesCached() * 60).toLong()
        try {
            FileOpenHistoryMan.subtractTimeOffset(systemDefaultTimeOffsetInSec)
            MyLog.w(TAG, "#$funName migrate `FileOpenHistory` success")
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName migrate `FileOpenHistory` failed: ${e.stackTraceToString()}")
        }
        try {
            AppModel.dbContainer.db.withTransaction {
                AppModel.dbContainer.credentialRepository.subtractTimeOffset(systemDefaultTimeOffsetInSec)
                AppModel.dbContainer.domainCredentialRepository.subtractTimeOffset(systemDefaultTimeOffsetInSec)
                AppModel.dbContainer.errorRepository.subtractTimeOffset(systemDefaultTimeOffsetInSec)
                AppModel.dbContainer.remoteRepository.subtractTimeOffset(systemDefaultTimeOffsetInSec)
                AppModel.dbContainer.repoRepository.subtractTimeOffset(systemDefaultTimeOffsetInSec)
                AppModel.dbContainer.settingsRepository.subtractTimeOffset(systemDefaultTimeOffsetInSec)
                AppModel.dbContainer.storageDirRepository.subtractTimeOffset(systemDefaultTimeOffsetInSec)
            }
            MyLog.w(TAG, "#$funName migrate `DataBase` success")
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName migrate `DataBase` failed: ${e.stackTraceToString()}")
        }
        return true
    }
}
