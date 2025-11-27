package com.akcreation.gitsilent.service

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.quicksettings.Tile
import androidx.core.content.ContextCompat
import com.akcreation.gitsilent.constants.IntentCons
import com.akcreation.gitsilent.base.BaseTileService
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.receiverFlags

private const val TAG = "TileHttpService"
@TargetApi(Build.VERSION_CODES.N)  
class TileHttpService: BaseTileService() {
    companion object {
        val ACTION_UPDATE = IntentCons.Action.UPDATE_TILE
        const val INTENT_EXTRA_KEY_NEW_STATE = IntentCons.ExtrasKey.newState
        fun sendUpdateTileRequest(appContext: Context, newState:Boolean) {
            val intent = Intent(ACTION_UPDATE)
            intent.putExtra(INTENT_EXTRA_KEY_NEW_STATE, newState)
            appContext.sendBroadcast(intent)
        }
    }
    private val updateTileReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val newState = intent?.extras?.getBoolean(INTENT_EXTRA_KEY_NEW_STATE)
            MyLog.d(TAG, "#updateTileReceiver.updateTileReceiver(): received action: ${intent?.action}, newState=$newState")
            if(newState != null) {
                updateState(newState)
            }
        }
    }
    override fun onTileAdded() {
        super.onTileAdded()
        updateState(HttpService.isRunning())
    }
    override fun onStartListening() {
        super.onStartListening()
        try {
            ContextCompat.registerReceiver(applicationContext, updateTileReceiver, IntentFilter(ACTION_UPDATE), receiverFlags())
        }catch (e:Exception) {
            MyLog.e(TAG, "#onStartListening: ${e.stackTraceToString()}")
        }
    }
    override fun onStopListening() {
        super.onStopListening()
        try {
            unregisterReceiver(updateTileReceiver)
        }catch (e:Exception) {
            MyLog.e(TAG, "#onStopListening: ${e.stackTraceToString()}")
        }
    }
    override fun onClick() {
        super.onClick()
        if(qsTile.state == Tile.STATE_ACTIVE) {
            HttpService.stop(applicationContext)
        }else {
            HttpService.start(applicationContext)
        }
    }
    private fun updateState(newState:Boolean) {
        qsTile?.state = if (newState) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        qsTile?.updateTile()
    }
}
