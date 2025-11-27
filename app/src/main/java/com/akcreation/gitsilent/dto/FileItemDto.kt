package com.akcreation.gitsilent.dto

import android.content.Context
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.getFileAttributes
import com.akcreation.gitsilent.utils.getFormatTimeFromSec
import com.akcreation.gitsilent.utils.getHumanReadableSizeStr
import com.akcreation.gitsilent.utils.getShortTimeIfPossible
import com.akcreation.gitsilent.utils.mime.MimeType
import com.akcreation.gitsilent.utils.mime.guessFromFile
import java.io.File
import java.util.concurrent.TimeUnit

private const val TAG = "FileItemDto"
data class FileItemDto (
    var name:String="",
    var createTime:String="",
    var createTimeInSec:Long=0L,
    var lastModifiedTime:String="",
    var lastModifiedTimeInSec:Long=0L,
    var sizeInBytes:Long =0L,
    var sizeInHumanReadable:String ="",
    var isFile:Boolean = false,
    var isDir:Boolean = false,
    var fullPath:String = "",
    var mime:MimeType=MimeType.TEXT_PLAIN,
    var isHidden:Boolean = false,
    var folderCount:Int=0,
    var fileCount:Int=0
) {
    var cachedShortLastModifiedTime:String? = null
        private set
        get() = field ?: getShortTimeIfPossible(lastModifiedTime).let { field = it; it }
    fun getShortDesc():String {
        return if(isDir) {
            cachedShortLastModifiedTime ?: ""
        }else {  
            "$cachedShortLastModifiedTime, $sizeInHumanReadable"
        }
    }
    fun toFile():File {
        return File(fullPath)
    }
    fun equalsForSelected(other:FileItemDto):Boolean {
        return fullPath == other.fullPath
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FileItemDto
        if (name != other.name) return false
        if (createTime != other.createTime) return false
        if (createTimeInSec != other.createTimeInSec) return false
        if (lastModifiedTime != other.lastModifiedTime) return false
        if (lastModifiedTimeInSec != other.lastModifiedTimeInSec) return false
        if (sizeInBytes != other.sizeInBytes) return false
        if (sizeInHumanReadable != other.sizeInHumanReadable) return false
        if (isFile != other.isFile) return false
        if (isDir != other.isDir) return false
        if (fullPath != other.fullPath) return false
        if (mime != other.mime) return false
        if (isHidden != other.isHidden) return false
        return true
    }
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + createTime.hashCode()
        result = 31 * result + createTimeInSec.hashCode()
        result = 31 * result + lastModifiedTime.hashCode()
        result = 31 * result + lastModifiedTimeInSec.hashCode()
        result = 31 * result + sizeInBytes.hashCode()
        result = 31 * result + sizeInHumanReadable.hashCode()
        result = 31 * result + isFile.hashCode()
        result = 31 * result + isDir.hashCode()
        result = 31 * result + fullPath.hashCode()
        result = 31 * result + mime.hashCode()
        result = 31 * result + isHidden.hashCode()
        return result
    }
    override fun toString(): String {
        return "FileItemDto(name='$name', createTime='$createTime', createTimeInSec=$createTimeInSec, lastModifiedTime='$lastModifiedTime', lastModifiedTimeInSec=$lastModifiedTimeInSec, sizeInBytes=$sizeInBytes, sizeInHumanReadable='$sizeInHumanReadable', isFile=$isFile, isDir=$isDir, fullPath='$fullPath', mime=$mime, isHidden=$isHidden)"
    }
    companion object {
        fun getRootDto():FileItemDto {
            return FileItemDto(isDir = true, fullPath = FsUtils.rootPath, name = FsUtils.rootName)
        }
        fun genFileItemDtoByFile(file: File, activityContext:Context):FileItemDto {
            val fdto = FileItemDto()
            updateFileItemDto(fdto, file, activityContext)
            return fdto
        }
        fun updateFileItemDto(fdto:FileItemDto, file: File, activityContext: Context) {
            try {
                fdto.name = file.name
                fdto.fullPath = file.canonicalPath
                fdto.isFile = file.isFile
                fdto.isDir = file.isDirectory
                fdto.sizeInBytes = file.length()  
                fdto.mime = MimeType.guessFromFile(file)
                fdto.isHidden = file.isHidden
                if(fdto.isDir) {  
                    fdto.sizeInHumanReadable = "["+ activityContext.getString(R.string.folder) +"]"  
                }else {  
                    fdto.sizeInHumanReadable = getHumanReadableSizeStr(fdto.sizeInBytes)
                }
                val fileAttributes = getFileAttributes(file.canonicalPath)
                fdto.lastModifiedTimeInSec = fileAttributes?.lastModifiedTime()?.to(TimeUnit.SECONDS) ?: 0
                fdto.createTimeInSec = fileAttributes?.creationTime()?.to(TimeUnit.SECONDS) ?: 0
                fdto.lastModifiedTime = getFormatTimeFromSec(fdto.lastModifiedTimeInSec)
                fdto.createTime = getFormatTimeFromSec(fdto.createTimeInSec)
            }catch (e:Exception) {
                MyLog.e(TAG, "#updateFileItemDto err: ${e.localizedMessage}")
            }
        }
    }
}
