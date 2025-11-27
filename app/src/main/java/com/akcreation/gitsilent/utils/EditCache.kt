package com.akcreation.gitsilent.utils

import androidx.compose.runtime.mutableStateOf
import com.akcreation.gitsilent.utils.base.DateNamedFileWriter
import java.io.File
import java.time.LocalDateTime

private const val TAG = "EditCache"
object EditCache: DateNamedFileWriter(
    logTagOfSubClass = TAG,
    fileNameTag = "EditCache",
) {
    private var enable = true  
    private var isInited = false
    private var lastSavedText = mutableStateOf("")
    fun init(enableCache:Boolean, cacheDir:File, keepInDays: Int=fileKeepDays) {
        try {
            super.init(cacheDir, keepInDays)
            enable = enableCache
            if(enableCache) {
                isInited=true
                startWriter()
            }else {
                isInited = false
            }
        }catch (e:Exception) {
            isInited=false
            MyLog.e(TAG, "#init err: "+e.stackTraceToString())
        }
    }
    fun writeToFile(text: String) {
        if(!enable || !isInited) {
            return
        }
        doJob job@{
            try {
                val text = text.trimEnd()
                if (text.isBlank() || text == lastSavedText.value) {
                    return@job
                }
                lastSavedText.value = text
                val nowTimestamp = contentTimestampFormatter.format(LocalDateTime.now())
                val needWriteMessage = "-------- $nowTimestamp :\n$text"
                sendMsgToWriter(nowTimestamp, needWriteMessage)
            } catch (e: Exception) {
                MyLog.e(TAG, "#writeToFile err: "+e.stackTraceToString())
            }
        }
    }
}
