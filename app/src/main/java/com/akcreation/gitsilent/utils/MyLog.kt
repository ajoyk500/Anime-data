package com.akcreation.gitsilent.utils

import android.content.Context
import android.util.Log
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.base.DateNamedFileWriter
import java.io.File
import java.time.LocalDateTime

private const val TAG = "MyLog"
object MyLog: DateNamedFileWriter(
    logTagOfSubClass = TAG,
    fileNameTag = "Log",
) {
    private const val DISABLED_SIGN = "0"
    val logLevelList = listOf(
        DISABLED_SIGN,
        "e",
        "w",
        "i",
        "d",
        "v",
    )
    private const val MYLOG_SWITCH = true 
    private const val MYLOG_WRITE_TO_FILE = true 
    var myLogLevel = 'w' 
    private var isInited = false
    fun init(logDir:File, logKeepDays: Int= fileKeepDays, logLevel: Char=myLogLevel) {
        try {
            isInited = true
            myLogLevel = logLevel
            super.init(logDir, logKeepDays)
            startWriter()
        }catch (e:Exception) {
            isInited = false
            try {
                e.printStackTrace()
                Log.e(TAG, "#init MyLog err: "+e.stackTraceToString())
            }catch (e2:Exception) {
                e2.printStackTrace()
            }
        }
    }
    fun setLogLevel(level:Char) {
        myLogLevel = level
    }
    fun w(tag: String, msg: Any) { 
        log(tag, msg.toString(), 'w')
    }
    fun e(tag: String, msg: Any) { 
        log(tag, msg.toString(), 'e')
    }
    fun d(tag: String, msg: Any) { 
        log(tag, msg.toString(), 'd')
    }
    fun i(tag: String, msg: Any) { 
        log(tag, msg.toString(), 'i')
    }
    fun v(tag: String, msg: Any) {  
        log(tag, msg.toString(), 'v')
    }
    fun w(tag: String, text: String) {
        log(tag, text, 'w')
    }
    fun e(tag: String, text: String) {
        log(tag, text, 'e')
    }
    fun d(tag: String, text: String) {
        log(tag, text, 'd')
    }
    fun i(tag: String, text: String) {
        log(tag, text, 'i')
    }
    fun v(tag: String, text: String) {
        log(tag, text, 'v')
    }
    private fun log(tag: String, msg: String, level: Char) {
        try {
            if(level.toString() == DISABLED_SIGN) {
                return
            }
            if(isInited.not()) {
                if(level == 'e') {
                    Log.e(tag, msg)
                }else if(level == 'w') {
                    Log.w(tag, msg)
                }else if(level == 'i') {
                    Log.i(tag, msg)
                }else if(level == 'd') {
                    Log.d(tag, msg)
                }else if(level == 'v') {
                    Log.v(tag, msg)
                }else {  
                    Log.d(tag, msg)
                }
                return
            }
            if (MYLOG_SWITCH) { 
                var isGoodLevel = true
                if ('e' == myLogLevel && 'e' == level) { 
                    Log.e(tag, msg)
                } else if ('w' == myLogLevel && ('w' == level || 'e' == level)) {
                    if ('w' == level) {
                        Log.w(tag, msg)
                    } else {
                        Log.e(tag, msg)
                    }
                } else if ('i' == myLogLevel && ('w' == level || 'e' == level || 'i' == level)) {
                    if ('w' == level) {
                        Log.w(tag, msg)
                    } else if ('e' == level) {
                        Log.e(tag, msg)
                    }else {
                        Log.i(tag, msg)
                    }
                } else if ('d' == myLogLevel && ('w' == level || 'e' == level || 'd' == level || 'i' == level)) {
                    if ('w' == level) {
                        Log.w(tag, msg)
                    } else if ('e' == level) {
                        Log.e(tag, msg)
                    } else if ('d' == level) {
                        Log.d(tag, msg)
                    } else {
                        Log.i(tag, msg)
                    }
                } else if ('v' == myLogLevel && ('w' == level || 'e' == level || 'd' == level || 'i' == level || 'v' == level)) {
                    if ('w' == level) {
                        Log.w(tag, msg)
                    } else if ('e' == level) {
                        Log.e(tag, msg)
                    } else if ('d' == level) {
                        Log.d(tag, msg)
                    } else if ('i' == level) {
                        Log.i(tag, msg)
                    } else {
                        Log.v(tag, msg)
                    }
                } else {
                    isGoodLevel = false
                }
                if (isGoodLevel && MYLOG_WRITE_TO_FILE) { 
                    doJobThenOffLoading {
                        writeLogToFile(level.toString(), tag, msg)
                    }
                }
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }
    }
    private suspend fun writeLogToFile(mylogtype: String, tag: String, text: String) { 
        try {
            val nowTimestamp = contentTimestampFormatter.format(LocalDateTime.now())
            val needWriteMessage = "$nowTimestamp | $mylogtype | $tag | $text"
            sendMsgToWriter(nowTimestamp, needWriteMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "#writeLogToFile err: "+e.stackTraceToString())
        }
    }
    fun getTextByLogLevel(level:String, context: Context):String {
        if(level == DISABLED_SIGN) {
            return context.getString(R.string.disable)
        } else if(level == "e") {
            return context.getString(R.string.error)
        }else if(level=="w"){
            return context.getString(R.string.warn)
        }else if(level=="i"){
            return context.getString(R.string.info)
        }else if(level=="d"){
            return context.getString(R.string.debug)
        }else if(level=="v"){
            return context.getString(R.string.verbose)
        }else {
            return context.getString(R.string.unknown)
        }
    }
    fun getCurrentLogLevel():String {
        return ""+myLogLevel
    }
}
