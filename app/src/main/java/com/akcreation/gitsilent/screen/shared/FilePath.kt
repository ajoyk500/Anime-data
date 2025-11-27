package com.akcreation.gitsilent.screen.shared

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.etc.PathType
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.MyLog
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File

private const val TAG = "FilePath"
private const val emptyPath = ""
private val knownSystemFilesManagerUris = listOf(
    "content:
    "content:
)
private val knownUris = listOf(
    "content:
    "content:
    "content:
    "content:
)
@Parcelize
class FilePath(
    private val rawPath:String,
):Parcelable {
    @IgnoredOnParcel
    private val rawPathType = PathType.getType(rawPath)
    @IgnoredOnParcel
    var ioPath:String = rawPath
    @IgnoredOnParcel
    var ioPathType = rawPathType
    init {
        initIoPath()
    }
    private fun pathOrEmpty(tryThisPath :String):String {
        val file = File(tryThisPath)
        return if (file.canRead()) {file.canRead()
            file.canonicalPath
        } else {
            emptyPath
        }
    }
    private fun readableCanonicalPathOrDefault(file:File, default:String):String {
        return file.let {
            if(it.canRead()) {
                it.canonicalPath
            }else {
                default
            }
        }
    }
    private fun initIoPath() {
        ioPath = if(rawPathType == PathType.CONTENT_URI) {
            try {
                MyLog.d(TAG, "#initIoPath: type=CONTENT_URI, rawPath=$rawPath")
                FsUtils.translateContentUriToRealPath(Uri.parse(rawPath))
                    ?: resolveFileSlashSlashUri()
                    .ifBlank { tryResolveKnownUriToRealPath() }
                    .ifBlank { rawPath }
            }catch (_: Exception) {
                rawPath
            }
        }else if(rawPathType == PathType.FILE_URI) {
            rawPath.removePrefix(FsUtils.fileUriPathPrefix)
        }else if(rawPathType == PathType.ABSOLUTE) {
            rawPath
        }else {
            rawPath
        }
        ioPathType = PathType.getType(ioPath)
    }
    private fun tryResolveKnownUriToRealPath(): String {
        val funName = "tryResolveKnownUriToRealPath"
        try {
            val uriStr = decodeTheFuckingUriPath(rawPath)
            val maybeCanGetPathFromUri = knownSystemFilesManagerUris.any { uriStr.startsWith(it) }
            return if(maybeCanGetPathFromUri) {
                readableCanonicalPathOrDefault(File(FsUtils.getRealPathFromUri(Uri.parse(rawPath))), emptyPath)
            } else {
                var resolvedPath = emptyPath
                for (uriPrefix in knownUris) {
                    val indexOfPrefix = uriStr.indexOf(uriPrefix)
                    if(indexOfPrefix == 0) {
                        val realPathNoRootPrefix = uriStr.substring(uriPrefix.length)  
                        val file = File("/", realPathNoRootPrefix)
                        MyLog.d(TAG, "resolved a known uri: realPathNoRootPrefix=$realPathNoRootPrefix, the path which will using is '${file.canonicalPath}'")
                        resolvedPath = readableCanonicalPathOrDefault(file, emptyPath)
                        if(resolvedPath.isNotBlank()) {
                            break
                        }
                    }
                }
                resolvedPath
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: resolved uri err: ${e.localizedMessage}")
            return emptyPath
        }
    }
    private fun resolveFileSlashSlashUri():String {
        val funName = "resolveFileSlashSlashUri"
        return try {
            val safUri = Uri.parse(rawPath)
            MyLog.d(TAG, "#$funName: safUri: $safUri, safUri.path=${safUri.path}")
            val uriPath = safUri?.path ?: emptyPath
            if(uriPath.isBlank()) {
                emptyPath
            }else {
                val decodedPath = decodeTheFuckingUriPath(uriPath)
                MyLog.d(TAG, "#$funName: decodePath=$decodedPath")
                val file = File(decodedPath)
                if (file.canRead()) {
                    file.canonicalPath
                } else {
                    val uriPath = decodedPath
                    if (uriPath.startsWith(FsUtils.fileUriPathPrefix)) {
                        pathOrEmpty(uriPath.removePrefix(FsUtils.fileUriPathPrefix))
                    } else {
                        val slashPrefixFileUriPath = Cons.slash + FsUtils.fileUriPathPrefix  
                        if (uriPath.startsWith(slashPrefixFileUriPath)) {
                            pathOrEmpty(uriPath.removePrefix(slashPrefixFileUriPath))
                        }else {
                            emptyPath
                        }
                    }
                }
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: resolve uri err: ${e.localizedMessage}")
            emptyPath
        }
    }
    fun isEmpty():Boolean = ioPath.isEmpty()
    fun isBlank():Boolean = ioPath.isBlank()
    fun isNotEmpty():Boolean = ioPath.isNotEmpty()
    fun isNotBlank():Boolean = ioPath.isNotBlank()
    fun toFuckSafFile(context: Context):FuckSafFile {
        return FuckSafFile(context = context, path = this)
    }
    override fun toString(): String {
        return ioPath
    }
    companion object {
        fun decodeTheFuckingUriPath(uriPath:String):String {
            var decodedPath = Uri.decode(uriPath)
            for (i in 1..3000) { 
                val newPath = Uri.decode(decodedPath)
                if (newPath == decodedPath) { 
                    break
                }
                decodedPath = newPath
            }
            return decodedPath
        }
    }
}
