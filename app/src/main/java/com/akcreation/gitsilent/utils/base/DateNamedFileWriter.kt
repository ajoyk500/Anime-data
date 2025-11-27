package com.akcreation.gitsilent.utils.base

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class DateNamedFileWriter(
    private val logTagOfSubClass:String,  
    private val fileNameTag:String,  
    private val fileExt:String=".txt",
    private val fileNameSeparator:String="#",
    var fileKeepDays:Int = 3,
    channelBufferSize:Int = 50,  
    private val maxErrCount:Int = 5,  
) {
    private val writeLock = Mutex()
    private val writeChannel = Channel<String> (capacity = channelBufferSize, onBufferOverflow = BufferOverflow.SUSPEND) {  }
    private val fileNameDateFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    protected val contentTimestampFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") 
    private var fileWriter: BufferedWriter? = null
    protected var file:File? = null
    private val writerJob: MutableState<Job?> = mutableStateOf(null)
    private var saveDir:File?=null
        get() {
            field?.let {
                if(!it.exists()) {
                    it.mkdirs()
                }
            }
            return field
        }
    protected fun init(saveDir:File, fileKeepDays: Int = this.fileKeepDays) {
        this.saveDir = saveDir
        this.fileKeepDays = fileKeepDays
    }
    protected fun startWriter() {
        if(writerJob.value != null) {
            return
        }
        writerJob.value = doJobThenOffLoading {
            var (file, writer) = initWriter()
            var errCountLimit = maxErrCount
            while (errCountLimit > 0) {
                writeLock.withLock {
                    val textWillWrite = writeChannel.receive()
                    while (errCountLimit > 0) {
                        try {
                            if (file.exists().not()) {
                                val pair = initWriter()
                                file = pair.first
                                writer = pair.second
                            }
                            writer.write(textWillWrite + "\n\n")
                            writer.flush()
                            errCountLimit = maxErrCount
                            break
                        } catch (e: Exception) {
                            errCountLimit--
                            val pair = initWriter()
                            file = pair.first
                            writer = pair.second
                            Log.e(logTagOfSubClass, "write to file err: ${e.stackTraceToString()}")
                        }
                    }
                }
            }
            writerJob.value = null
        }
    }
    private fun getDateFromFileName(fileName:String):String {
        return try {
            fileName.split(this.fileNameSeparator)[0]
        }catch (e:Exception) {
            Log.e(logTagOfSubClass, "#getDateFromFileName err: ${e.stackTraceToString()}")
            ""
        }
    }
    private fun getFileName(): String {
        val datePrefix = fileNameDateFormatter.format(LocalDateTime.now())
        return datePrefix + this.fileNameSeparator + fileNameTag + fileExt
    }
    private fun dateChanged(nowTimestamp:String, fileNameTimestamp:String):Boolean {
        return nowTimestamp.startsWith(fileNameTimestamp).not()
    }
    private fun closeWriterIfTimeChanged(nowTimestamp:String){
        file?.let {
            if(dateChanged(nowTimestamp, getDateFromFileName(it.name))) {
                fileWriter?.close()
            }
        }
    }
    protected suspend fun sendMsgToWriter(nowTimestamp:String, msg:String){
        closeWriterIfTimeChanged(nowTimestamp)
        writeChannel.send(msg)
    }
    private fun initWriter(charset: Charset = StandardCharsets.UTF_8):Pair<File, BufferedWriter>{
        val funName = "initWriter"
        val dirsFile = saveDir!!
        if (!dirsFile.exists()) {
            dirsFile.mkdirs()
        }
        val file = File(dirsFile.getCanonicalPath(), getFileName())
        this.file = file
        if (!file.exists()) {
            file.createNewFile()
        }
        try {
            fileWriter?.close()
        }catch (e:Exception) {
            Log.e(logTagOfSubClass, "#$funName err: ${e.stackTraceToString()}")
        }
        val append = true
        val filerWriter = OutputStreamWriter(FileOutputStream(file, append), charset)
        val newBufferedWriter = filerWriter.buffered()
        fileWriter = newBufferedWriter
        return Pair(file, newBufferedWriter)
    }
    fun delExpiredFiles() { 
        val funName = "delExpiredFiles"
        try {
            MyLog.d(logTagOfSubClass, "#$funName, start: del expired '$fileNameTag' files")
            val dirPath = saveDir!! 
            val todayInDay = LocalDate.now().toEpochDay()
            val fileList = dirPath.listFiles()?:return
            for (f in fileList) {  
                if (f == null) {
                    continue
                }
                try {
                    val dateStrFromFileName = getDateFromFileName(f.name) 
                    val fileCreatedDateInDay = LocalDate.from(fileNameDateFormatter.parse(dateStrFromFileName)).toEpochDay()
                    val diffInDay = todayInDay - fileCreatedDateInDay 
                    if (diffInDay > fileKeepDays) {
                        f.delete()
                    }
                } catch (e: Exception) {
                    MyLog.e(logTagOfSubClass, "#$funName, looping err: "+e.stackTraceToString())
                    continue
                }
            }
            MyLog.d(logTagOfSubClass, "#$funName, end: del expired '$fileNameTag' files")
        } catch (e: Exception) {
            MyLog.e(logTagOfSubClass, "#$funName, err: "+e.stackTraceToString())
        }
    }
}
