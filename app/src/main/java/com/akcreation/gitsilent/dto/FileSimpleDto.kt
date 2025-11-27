package com.akcreation.gitsilent.dto

import android.content.Context
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.utils.MyLog
import java.util.concurrent.TimeUnit

private const val TAG = "FileSimpleDto"
data class FileSimpleDto(
    var name:String="",
    var createTime:Long=0L,
    var lastModifiedTime:Long=0L,
    var sizeInBytes:Long =0L,
    var isFile: Boolean = true,
    var fullPath:String = "",
) {
    companion object {
        fun genByFile(file: FuckSafFile, timeUnit: TimeUnit=TimeUnit.MILLISECONDS):FileSimpleDto {
            val fdto = FileSimpleDto()
            updateDto(fdto, file, timeUnit)
            return fdto
        }
        fun updateDto(fdto:FileSimpleDto, file: FuckSafFile, timeUnit: TimeUnit=TimeUnit.MILLISECONDS) {
            try {
                fdto.name = file.name
                fdto.fullPath = file.canonicalPath
                fdto.isFile = file.isFile
                fdto.sizeInBytes = file.length()  
                fdto.lastModifiedTime = timeUnit.convert(file.lastModified(), TimeUnit.MILLISECONDS)
                fdto.createTime = timeUnit.convert(file.creationTime(), TimeUnit.MILLISECONDS)  
            }catch (e:Exception) {
                MyLog.e(TAG, "#updateDto err: ${e.localizedMessage}")
            }
        }
    }
    fun toFuckSafFile(context:Context?): FuckSafFile {
        return FuckSafFile(context, FilePath(fullPath))
    }
}
