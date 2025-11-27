package com.akcreation.gitsilent.screen.shared

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.akcreation.gitsilent.compose.FileChangeListenerState
import com.akcreation.gitsilent.constants.LineBreak
import com.akcreation.gitsilent.etc.PathType
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.EncodingUtil
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "FuckSafFile"
class FuckSafFile(val context: Context?, val path: FilePath) {
    companion object {
        fun fromFile(f:File):FuckSafFile {
            return FuckSafFile(null, FilePath(f.canonicalPath))
        }
    }
    var isSaf:Boolean = false
    var file:File? = null
    var safFile:DocumentFile? = null
    var safUri:Uri? = null
    val isFile:Boolean
        get() = (if(isSaf) safFile?.isFile else file?.isFile) == true
    val isDirectory:Boolean
        get() = (if(isSaf) safFile?.isDirectory else file?.isDirectory) == true
    val name:String
        get() = (if(isSaf) safFile?.name else file?.name) ?: ""
    val canonicalPath:String
        get() = (if(isSaf) safUri?.toString() else file?.canonicalPath) ?: ""
    init {
        val ioPath = path.ioPath
        val pathType = path.ioPathType
        if(pathType == PathType.ABSOLUTE) {
            initFile(ioPath)
        }else if(pathType == PathType.CONTENT_URI) {
            initDocFile(ioPath)
        }else if(pathType == PathType.FILE_URI) {
            val realPath = ioPath.removePrefix(FsUtils.fileUriPathPrefix)
            initFile(realPath)
        }
    }
    fun lastModified():Long {
        return (if (isSaf) safFile?.lastModified() else file?.lastModified()) ?: 0L
    }
    fun creationTime():Long {
        return lastModified()
    }
    fun length():Long {
        return (if (isSaf) safFile?.length() else file?.length()) ?: 0L
    }
    private fun initFile(path:String) {
        isSaf = false
        file = File(path)
    }
    private fun initDocFile(path:String) {
        isSaf = true
        val currentUri = Uri.parse(path)
        safUri = currentUri
        safFile = FsUtils.getDocumentFileFromUri(context!!, currentUri)
    }
    fun inputStream():InputStream {
        return if(isSaf) context!!.contentResolver.openInputStream(safUri!!)!! else file!!.inputStream()
    }
    fun outputStream():OutputStream {
        return if(isSaf) context!!.contentResolver.openOutputStream(safUri!!)!! else file!!.outputStream()
    }
    fun bufferedReader(charsetName: String?):BufferedReader {
        return EncodingUtil.ignoreBomIfNeed(
            newInputStream = { inputStream() },
            charsetName = charsetName
        ).inputStream.bufferedReader(EncodingUtil.resolveCharset(charsetName))
    }
    fun bufferedWriter(charsetName: String?): BufferedWriter {
        val output = outputStream()
        EncodingUtil.addBomIfNeed(output, charsetName)
        return output.bufferedWriter(EncodingUtil.resolveCharset(charsetName))
    }
    fun exists():Boolean {
        return (if(isSaf) safFile?.exists() else file?.exists()) == true
    }
    fun renameFileTo(newPath:String):Boolean {
        return renameFileTo(File(newPath))
    }
    fun renameFileTo(newFile:File):Boolean {
        return file?.renameTo(newFile) == true
    }
    fun renameSafFile(newName:String):Boolean {
        return safFile?.renameTo(newName) == true
    }
    fun delete():Boolean {
        return (if(isSaf) safFile?.delete() else file?.delete()) == true
    }
    fun renameTo(newFuckSafFile:FuckSafFile):Boolean {
        inputStream().copyTo(newFuckSafFile.outputStream())
        return delete()
    }
    fun copyTo(target:OutputStream) {
        FsUtils.copy(inputStream(), target)
    }
    fun canRead():Boolean {
        return (if(isSaf) safFile?.canRead() else file?.canRead()) == true
    }
    override fun toString(): String {
        return canonicalPath
    }
    fun createChangeListener(
        fileChangeListenerState: FileChangeListenerState,
        taskName: String? = null,
        onChange:()->Unit
    ):Job? {
        if(path.ioPathType == PathType.INVALID) {
            return null
        }
        val taskName = taskName ?: "FileChangeListener(fileName: $name)"
        val intervalInMillSec = fileChangeListenerState.intervalInMillSec
        return doJobThenOffLoading {
            try {
                var oldFileLen = fileChangeListenerState.lastLength ?: length()
                var oldFileModified = fileChangeListenerState.lastModified ?: lastModified()
                while (true) {
                    delay(intervalInMillSec)
                    val newFileLen = length()
                    val newFileModified = lastModified()
                    fileChangeListenerState.lastLength = newFileLen
                    fileChangeListenerState.lastModified = newFileModified
                    MyLog.v(TAG, "$taskName: oldFileLen=$oldFileLen, newFileLen=$newFileLen, oldFileModified=$oldFileModified, newFileModified=$newFileModified")
                    if (oldFileLen != newFileLen || oldFileModified != newFileModified) {
                        oldFileLen = newFileLen
                        oldFileModified = newFileModified
                        MyLog.d(TAG, "$taskName: file changed, will call `onChange()`")
                        onChange()
                    }
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                MyLog.d(TAG, "$taskName: listen change of file err: fileName='$name', filePath='${path.ioPath}', err=${e.stackTraceToString()}")
            }
        }
    }
    fun isActuallyReadable(): Boolean {
        return try {
            inputStream().use { it.read() }
            true
        }catch(_: Exception) {
            false
        }
    }
    fun detectEncoding(): String {
        return try {
            EncodingUtil.detectEncoding(newInputStream = { inputStream() })
        }catch (e: Exception) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "$TAG#detectEncoding err: ${e.localizedMessage}")
            }
            EncodingUtil.defaultCharsetName
        }
    }
    fun detectLineBreak(charsetName: String?): LineBreak {
        var lineBreak = ""
        val arrBuf = CharArray(2048)
        bufferedReader(charsetName).use { reader ->
            while (lineBreak.isEmpty()) {
                val readSize = reader.read(arrBuf)
                if(readSize == -1) {
                    break
                }
                for (i in 0 until readSize) {
                    val char = arrBuf[i]
                    if(char == '\r') {
                        val nextChar = arrBuf.getOrNull(i+1)
                        lineBreak = if(nextChar == null) {
                            if(reader.read() == '\n'.code) {
                                "\r\n"
                            }else {  
                                "\r"
                            }
                        }else if(nextChar == '\n') {
                            "\r\n"
                        }else {
                            "\r"
                        }
                        break
                    }else if(char == '\n') {
                        lineBreak = "\n"
                        break
                    }
                }
            }
        }
        return LineBreak.getType(lineBreak, default = LineBreak.LF)!!
    }
}
