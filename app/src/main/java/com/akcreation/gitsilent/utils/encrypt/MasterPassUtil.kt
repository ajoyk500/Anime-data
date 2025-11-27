package com.akcreation.gitsilent.utils.encrypt

import android.content.Context
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.HashUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.pref.PrefMan
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64

object MasterPassUtil {
    private const val TAG = "MasterPassUtil"
    fun goodToSave(masterPass: String):Boolean {
        return decode(encode(masterPass)) == masterPass
    }
    private fun encode(masterPass:String):String {
        if(masterPass.isEmpty()) {
            return masterPass
        }
        return masterPass.encodeBase64()
    }
    private fun decode(masterPassEncoded:String):String {
        if(masterPassEncoded.isEmpty()) {
            return masterPassEncoded
        }
        return masterPassEncoded.decodeBase64String()
    }
    fun save(appContext: Context = AppModel.realAppContext, newMasterPass: String): AppSettings {
        PrefMan.set(appContext, PrefMan.Key.masterPass, encode(newMasterPass))
        val newSettingsWithNewPass = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
            it.masterPasswordHash = if(newMasterPass.isEmpty()) { 
                newMasterPass
            }else {  
                HashUtil.hash(newMasterPass)
            }
        }!!
        AppModel.masterPassword.value = newMasterPass
        return newSettingsWithNewPass
    }
    fun get(appContext: Context):String {
        try {
            val masterPassEncoded = PrefMan.get(appContext, PrefMan.Key.masterPass, "")
            return decode(masterPassEncoded)
        }catch (e:Exception) {
            MyLog.e(TAG, "get() failed: ${e.stackTraceToString()}")
            return ""
        }
    }
    fun clear(appContext: Context = AppModel.realAppContext) {
        save(appContext, "")
    }
}
