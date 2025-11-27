package com.akcreation.gitsilent.utils.saf

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.createDirIfNonexists
import java.io.File

private const val TAG = "SafUtil"
object SafUtil {
    const val safDirName = "saf"
    var safDir:File? = null
    const val safContentPrefix = "content:
    fun init(puppyGitDataDir: File) {
        safDir = createDirIfNonexists(puppyGitDataDir, safDirName)
    }
    fun uriToDbSupportedFormat(uri: Uri):String {
        return uri.toString()
    }
    fun isSafPath(path:String):Boolean {
        return path.startsWith(safContentPrefix)
    }
    @Deprecated("")
    fun uriUnderSafDir(uri:Uri):Pair<Boolean, String?> {
        return try {
            val realPath = appCreatedUriToPath(uri)
            Pair(realPath!!.startsWith(safDir!!.canonicalPath + "/"), realPath)
        }catch (e:Exception) {
            Pair(false, null)
        }
    }
    @Deprecated("")
    fun realPathToExternalAppsUri(realPath:String):Uri? {
        return try {
            var realPath = realPath.removePrefix(safDir!!.canonicalPath+"/")
            realPath = safContentPrefix+realPath
            Uri.parse(realPath)
        }catch (_:Exception) {
            null
        }
    }
    @Deprecated("")
    fun getAppInternalUriPrefix():String {
        return "content:
    }
    @Deprecated("")
    fun getAppExternalUriPrefix():String {
        return "content:
    }
    @Deprecated("")
    fun appCreatedUriToPath(uri: Uri):String? {
        val uriStr = uri.toString()
        return if(uriStr.startsWith(getAppInternalUriPrefix())) {
            AppModel.externalFilesDir.canonicalPath + "/" + uriStr.substring(getAppInternalUriPrefix().length)
        }else if(uriStr.startsWith(getAppExternalUriPrefix())) {
            FsUtils.getExternalStorageRootPathNoEndsWithSeparator() + "/" + uriStr.substring(getAppExternalUriPrefix().length)
        }else {
            null
        }
    }
    fun takePersistableRWPermission(contentResolver: ContentResolver, uri: Uri):Boolean {
        return try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            true
        }catch (e:Exception) {
            MyLog.d(TAG, "#takePersistableRWPermission() try take RW permissions err: ${e.stackTraceToString()}")
            false
        }
    }
    fun takePersistableReadOnlyPermission(contentResolver: ContentResolver, uri: Uri):Boolean {
        return try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            true
        }catch (e:Exception) {
            MyLog.d(TAG, "#takePersistableReadOnlyPermission() try take ReadOnly permissions err: ${e.stackTraceToString()}")
            false
        }
    }
    fun releasePersistableRWPermission(contentResolver: ContentResolver, uri: Uri):Boolean {
        return try {
            contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            true
        } catch (e: SecurityException) {
            MyLog.d(TAG, "#releasePersistableRWPermission() try release RW permissions err: ${e.stackTraceToString()}")
            false
        }
    }
    fun releasePersistableReadOnlyPermission(contentResolver: ContentResolver, uri: Uri):Boolean {
        return try {
            contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            true
        } catch (e: SecurityException) {
            MyLog.d(TAG, "#releasePersistableReadOnlyPermission() try release ReadOnly permissions err: ${e.stackTraceToString()}")
            false
        }
    }
}
