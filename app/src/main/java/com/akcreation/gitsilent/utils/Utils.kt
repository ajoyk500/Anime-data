package com.akcreation.gitsilent.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Build
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.IndentChar
import com.akcreation.gitsilent.data.entity.ErrorEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dto.AppInfo
import com.akcreation.gitsilent.dto.LineNumParseResult
import com.akcreation.gitsilent.dto.rawAppInfoToAppInfo
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.DirViewAndSort
import com.akcreation.gitsilent.settings.SettingsUtil
import com.github.git24j.core.Repository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.exists
import kotlin.math.absoluteValue

private const val TAG = "Utils"
fun showToast(context: Context, text:String, duration:Int=Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}
fun getRepoNameFromGitUrl(gitUrl: String):String{
    val gitIdx = gitUrl.lastIndexOf(".git")
    val urlSeparatorIdx = gitUrl.lastIndexOf("/")+1
    if(urlSeparatorIdx < gitUrl.length && urlSeparatorIdx<=gitIdx){
        val folderName = gitUrl.substring(urlSeparatorIdx, gitIdx)
        return folderName
    }else {
        return ""
    }
}
fun isPathExists(baseDir: String?, subDir:String):Boolean {
    val file = if(baseDir!=null) File(baseDir, subDir) else File(subDir)  
    return file.exists()
}
fun strHasSpaceChar(str:String):Boolean {
    for(c in str) {
        if(c.isWhitespace()) {
            return true
        }
    }
    return false
}
fun strHasIllegalChars(str:String):Boolean {
    if(str.contains("/") || str.contains("\\") ||str.contains('?')|| str.contains(File.separator) || str.contains(File.pathSeparatorChar)
        || str.contains("*")|| str.contains("<") || str.contains(">") ||  str.contains("|") ||  str.contains("\"")
    ) {
        return true
    }
    return false
}
fun checkFileOrFolderNameAndTryCreateFile(nameWillCheck:String, appContext: Context):Ret<String?> {
    val funName="checkFileOrFolderNameAndTryCreateFile"
    try{
        if(nameWillCheck.isEmpty()) {
            throw RuntimeException(appContext.getString(R.string.err_name_is_empty))
        }
        if(strHasIllegalChars(nameWillCheck)) {
            throw RuntimeException(appContext.getString(R.string.error_has_illegal_chars))
        }
        val cacheDir = AppModel.getOrCreateExternalCacheDir()
        val fileNameNeedTest = Cons.createDirTestNamePrefix + nameWillCheck;  
        val path = cacheDir.canonicalPath + File.separator + fileNameNeedTest
        val file = File(path)
        val createSuccess = file.createNewFile()  
        if(file.exists()) {
            if(createSuccess) {  
                file.delete()
            }else {  
                MyLog.w(TAG, "#$funName: warn: may has invalid file '${file.name}' in cache dir, try clear app cache if you don't know that file")
            }
            return Ret.createSuccess(null)  
        }
        throw RuntimeException(appContext.getString(R.string.error_cant_create_file_with_this_name))
    }catch (e:Exception) {
        MyLog.e(TAG, "#$funName err: ${e.localizedMessage}")
        return Ret.createError(null, e.localizedMessage ?: appContext.getString(R.string.unknown_err_plz_try_another_name))
    }
}
fun getRandomUUID(len: Int = 32):String {
    return generateRandomString(len)
}
fun getShortUUID(len:Int=20):String {
    return getRandomUUID(len)
}
val randomStringCharList = listOf('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z');
fun generateRandomString(length: Int=16): String {
    val sb = StringBuilder(length)
    for (i in 1..length) {
        sb.append(randomStringCharList.random())
    }
    return sb.toString()
}
fun dbIntToBool(v:Int):Boolean {
    return v != Cons.dbCommonFalse
}
fun boolToDbInt(b:Boolean):Int {
    return if(b) Cons.dbCommonTrue else Cons.dbCommonFalse
}
fun getShortTimeIfPossible(originDateTimeFormatted:String) : String {
    return try {
        val nowFormatted = getNowInSecFormatted()
        val nowDateTime = nowFormatted.split(' ')
        val nowYmd = nowDateTime[0]
        val originDateTime = originDateTimeFormatted.split(' ')
        val originYmd = originDateTime[0]
        val originHms = originDateTime[1]
        if(nowYmd == originYmd) { 
            originHms
        }else {
            val nowYmdArr = nowYmd.split('-')
            val originYmdArr = originYmd.split('-')
            if(nowYmdArr[0] == originYmdArr[0]) {  
                "${originYmdArr[1]}-${originYmdArr[2]} $originHms"
            }else { 
                originDateTimeFormatted
            }
        }
    }catch (e: Exception) {
        MyLog.e(TAG, "#getShortTimeIfPossible(String) err: originDateTimeFormatted=$originDateTimeFormatted, err=${e.localizedMessage}")
        ""
    }
}
fun getShortTimeIfPossible(sec: Long) : String {
    return getShortTimeIfPossible(getFormatTimeFromSec(sec))
}
fun getSecFromTime():Long {
    return getUtcTimeInSec()
}
fun getTimeFromSec(sec:Long):ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochSecond(sec), AppModel.getAppTimeZoneOffsetCached())
}
fun getFormatTimeFromSec(sec:Long, formatter:DateTimeFormatter = Cons.defaultDateTimeFormatter):String {
    try {
        val timeFromSec = getTimeFromSec(sec)
        return formatter.format(timeFromSec)
    }catch (e:Exception) {
        MyLog.e(TAG, "#getFormatTimeFromSec: format datetime failed: ${e.stackTraceToString()}")
        return ""
    }
}
fun getNowInSecFormatted(formatter:DateTimeFormatter = Cons.defaultDateTimeFormatter):String {
    return getFormatTimeFromSec(getSecFromTime(), formatter)
}
fun getSystemDefaultTimeZoneOffset() :ZoneOffset{
    return ZoneOffset.systemDefault().rules.getOffset(Instant.now())
}
fun formatMinutesToUtc(minutes:Int):String {
    try {
        val cachedValue = AppModel.timezoneCacheMap.get(minutes)
        if(cachedValue != null) {
            return cachedValue
        }
        val hours = minutes / 60
        val resetOfMinutes = (minutes % 60).absoluteValue  
        val hoursStr = if(hours >= 0) {
            "+$hours"
        }else {
            "$hours"
        }
        val resetOfMinutesStr = if(resetOfMinutes > 0) {
            val tmp = if(resetOfMinutes > 9) {
                "$resetOfMinutes"
            }else {
                "0$resetOfMinutes"
            }
            ":$tmp"
        }else {
            ""
        }
        val result = "UTC$hoursStr$resetOfMinutesStr"
        AppModel.timezoneCacheMap.put(minutes, result)
        return result
    }catch (e:Exception) {
        MyLog.e(TAG, "#formatMinutesToUtc() err: ${e.stackTraceToString()}")
        return ""
    }
}
fun daysToSec(days:Int) :Long{
    return (days * 24 * 60 * 60).toLong()
}
private fun getDirIfNullThenShowToastAndThrowException(context:Context, dir:File?, errMsg:String):File {
    if(dir==null) {
        showToast(context, errMsg, Toast.LENGTH_LONG)
        throw RuntimeException(errMsg)
    }else {
        if(!dir.exists()) {
            dir.mkdirs()
        }
        return dir;
    }
}
fun getExternalFilesIfErrGetInnerIfStillErrThrowException(context:Context):File {
    return try {
        getDirIfNullThenShowToastAndThrowException(context, context.getExternalFilesDir(null), Cons.errorCantGetExternalFilesDir)
    }catch (e:Exception) {
        getDirIfNullThenShowToastAndThrowException(context, context.filesDir, Cons.errorCantGetInnerFilesDir)
    }
}
fun getExternalCacheDirIfErrGetInnerIfStillErrThrowException(context:Context):File {
    return try{
        getDirIfNullThenShowToastAndThrowException(context, context.externalCacheDir, Cons.errorCantGetExternalCacheDir)
    }catch (e:Exception) {
        getDirIfNullThenShowToastAndThrowException(context, context.cacheDir, Cons.errorCantGetInnerCacheDir)
    }
}
fun getInnerDataDirOrThrowException(context:Context):File {
    return getDirIfNullThenShowToastAndThrowException(context, context.dataDir, Cons.errorCantGetInnerDataDir)
}
fun getExternalDataDirOrNull(context:Context):File? {
    return try {
        val dir = context.getExternalFilesDir(null) ?: throw RuntimeException("`context.getExternalFilesDir(null)` returned `null`")
        if(!dir.exists()) {
            dir.mkdirs()
        }
        dir.canonicalFile.parentFile
    }catch (e:Exception) {
        MyLog.e(TAG, "get app external data failed, usually this folder at '/storage/emulated/Android/data/app_package_name, err is: ${e.stackTraceToString()}")
        null
    }
}
fun getInnerCacheDirOrNull(context:Context):File? {
    return try {
        val dir = context.cacheDir ?: throw RuntimeException("`context.cacheDir` returned `null`")
        if(!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }catch (e:Exception) {
        MyLog.e(TAG, "get app inner cache dir failed, err is: ${e.stackTraceToString()}")
        null
    }
}
fun createDirIfNonexists(baseDir:File, subDirName:String):File {
    val dir = File(baseDir.canonicalPath, subDirName)
    if(!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}
fun deleteIfFileOrDirExist(f: File):Boolean {
    if(f.exists()) {
        return f.deleteRecursively()
    }
    return true;
}
fun isFileSizeOverLimit(size:Long, limit:Long=SettingsUtil.getSettingsSnapshot().editor.maxFileSizeLimit) :Boolean {
    return isSizeOverLimit(size = size, limitMax = limit)
}
fun isDiffContentSizeOverLimit(size:Long, limit:Long=SettingsUtil.getSettingsSnapshot().diff.diffContentSizeMaxLimit) :Boolean {
    return isSizeOverLimit(size = size, limitMax = limit)
}
fun isSizeOverLimit(size:Long, limitMax:Long):Boolean {
    if(limitMax == 0L) {
        return false
    }
    return size > limitMax
}
fun getFileNameFromCanonicalPath(path:String) : String {
    return runCatching { FsUtils.splitParentAndName(path).second }.getOrDefault("")
}
@Deprecated("over complex")
fun deprecated_getFileNameFromCanonicalPath(path:String, separator:Char=Cons.slashChar) : String {
    try {
        val pathRemovedSuffix = path.trim(separator)  
        val lastSeparatorIndex = pathRemovedSuffix.lastIndexOf(separator)  
        if(lastSeparatorIndex == -1) {
            return pathRemovedSuffix
        }
        return pathRemovedSuffix.substring(lastSeparatorIndex+1)
    }catch (e:Exception) {
        MyLog.e(TAG, "#getFileNameFromCanonicalPath err: path=$path, separator=$separator, err=${e.localizedMessage}")
        return path
    }
}
fun getFilePathStrBasedAllRepoDir(path:String):String {
    var ret = ""
    val allRepoBaseDirParentFullPath = AppModel.allRepoParentDir.parent?:""  
    val allRepoBaseIndexOf = path.indexOf(allRepoBaseDirParentFullPath)
    if(allRepoBaseIndexOf!=-1) {
        val underAllRepoBaseDirPathStartAt = allRepoBaseIndexOf + allRepoBaseDirParentFullPath.length
        if(underAllRepoBaseDirPathStartAt < path.length) {
            var pathBaseAllRepoDir = path.substring(underAllRepoBaseDirPathStartAt)  
            ret = pathBaseAllRepoDir  
        }
    }
    return ret.removePrefix(File.separator).removeSuffix(File.separator)  
}
fun getFilePathStrUnderRepoByFullPath(fullPath:String):Pair<String,String> {
    var repoFullPath = ""
    var relativePathUnderRepo = ""
    val filePathStrBasedAllRepoDir = getFilePathStrBasedAllRepoDir(fullPath)  
    if(filePathStrBasedAllRepoDir.isNotBlank()) {
        val firstSeparatorIndex = filePathStrBasedAllRepoDir.indexOf(File.separator)
        val cutAllRepoDirIndex = firstSeparatorIndex + 1  
        if(cutAllRepoDirIndex!=0 && cutAllRepoDirIndex < filePathStrBasedAllRepoDir.length) {  
            val repoPathUnderAllRepoBase = filePathStrBasedAllRepoDir.substring(cutAllRepoDirIndex)  
            val finallyStrIndex = repoPathUnderAllRepoBase.indexOf(File.separator)+1  
            if(finallyStrIndex!=0 && finallyStrIndex < repoPathUnderAllRepoBase.length) {
                val repoNameEndsWithSeparator = repoPathUnderAllRepoBase.substring(0, finallyStrIndex)  
                repoFullPath = File(AppModel.allRepoParentDir.canonicalPath, repoNameEndsWithSeparator).canonicalPath  
                relativePathUnderRepo = repoPathUnderAllRepoBase.substring(finallyStrIndex).removePrefix(File.separator).removeSuffix(File.separator)  
            }
        }
    }
    return Pair(repoFullPath, relativePathUnderRepo)
}
fun getFilePathUnderParent(parentFullPath:String, subFullPath:String) :String {
    if(parentFullPath.isBlank() || subFullPath.isBlank()
        ||subFullPath.length <= parentFullPath.length
        ) {
        return ""
    }
    val indexOf = subFullPath.indexOf(parentFullPath)
    if(indexOf==-1 || indexOf!=0) {  
        return ""
    }
    val startIndex = parentFullPath.length
    if(startIndex >= subFullPath.length){
        return ""
    }
    return subFullPath.substring(startIndex).removePrefix(File.separator).removeSuffix(File.separator)
}
fun getFilePathStrBasedRepoDir(path:String, returnResultStartsWithSeparator:Boolean=false):String {
    val path2 = getFilePathStrBasedAllRepoDir(path)
    val firstIdxOfSeparator = path2.indexOf(File.separator)  
    val isAllRepoDir = firstIdxOfSeparator==-1
    var result= if(isAllRepoDir) "" else path2.substring(firstIdxOfSeparator+1)
    if(returnResultStartsWithSeparator) {
        if(!result.startsWith(File.separator)) {
            result = File.separator+result
        }
    }else {
        if(result.startsWith(File.separator)) {
            result = result.removePrefix(File.separator)
        }
    }
    return result
}
fun getParentPathEndsWithSeparator(path:String, trueWhenNoParentReturnSeparatorFalseReturnPath:Boolean=true, trueWhenNoParentReturnEmpty:Boolean=false):String {
    try {
        val separator = File.separator
        val path = path.removeSuffix(separator)  
        val lastIndexOfSeparator = path.lastIndexOf(separator)
        if(lastIndexOfSeparator != -1) {  
            return path.substring(0, lastIndexOfSeparator+1)  
        }else {  
            if(trueWhenNoParentReturnEmpty) {
                return ""
            }
            return if(trueWhenNoParentReturnSeparatorFalseReturnPath) separator else path
        }
    }catch (e:Exception) {
        MyLog.e(TAG, "#getParentPathEndsWithSeparator err: path=$path, trueWhenNoParentReturnSeparatorFalseReturnPath=$trueWhenNoParentReturnSeparatorFalseReturnPath, trueWhenNoParentReturnEmpty=$trueWhenNoParentReturnEmpty, err=${e.localizedMessage}")
        return path
    }
}
fun isRepoReadyAndPathExist(r: RepoEntity?): Boolean {
    if(r==null) {
        return false
    }
    runCatching {
        Repository.open(r.fullSavePath)?.use { repo->
            r.gitRepoState = repo.state()
        }
    }
    if (Libgit2Helper.isRepoStatusReady(r)
        && r.isActive == Cons.dbCommonTrue
        && r.fullSavePath.isNotBlank()
        && r.gitRepoState != null
    ) {
        if (File(r.fullSavePath).exists()) {
            return true;
        }
    }
    return false
}
fun setErrMsgForTriggerNotify(hasErrState:MutableState<Boolean>,errMsgState:MutableState<String>,errMsg:String) {
    hasErrState.value=true;
    errMsgState.value=errMsg;
}
fun doJobThenOffLoading(
    loadingOn: (String)->Unit={},
    loadingOff: ()->Unit={},
    loadingText: String="Loading…",  
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    job: suspend ()->Unit
): Job? {
    return try {
        CoroutineScope(coroutineDispatcher).launch {
            try {
                loadingOn(loadingText)
            }catch (e:Exception) {
                Msg.requireShowLongDuration("loadOn err: "+e.localizedMessage)
                MyLog.e(TAG, "#doJobThenOffLoading(): #loadingOn error!\n" + e.stackTraceToString())
            }finally {
                try {
                    job()
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("job err: "+e.localizedMessage)
                    MyLog.e(TAG, "#doJobThenOffLoading(): #job error!\n" + e.stackTraceToString())
                }finally {
                    try {
                        loadingOff()  
                    }catch (e:Exception) {
                        Msg.requireShowLongDuration("loadOff err: "+e.localizedMessage)
                        MyLog.e(TAG, "#doJobThenOffLoading(): #loadingOff error!\n" + e.stackTraceToString())
                    }
                }
            }
        }
    }catch (e:Exception) {
        Msg.requireShowLongDuration("coroutine err: "+e.localizedMessage)
        MyLog.e(TAG, "#doJobThenOffLoading(): #launch error!\n" + e.stackTraceToString())
        null
    }
}
fun doJob(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    job: suspend () -> Unit
) :Job {
    return CoroutineScope(coroutineDispatcher).launch {
        job()
    }
}
private fun replaceStringRes(strRes:String, placeHolderCount:Int, strWillReplaced:String):String {
    return strRes.replace(Cons.placeholderPrefixForStrRes+placeHolderCount, strWillReplaced)
}
fun replaceStringResList(strRes:String, strWillReplacedList:List<String>):String {
    var ret=strRes
    for((idx, str) in strWillReplacedList.withIndex()) {
        val idxPlus1 = idx+1
        ret = replaceStringRes(ret, idxPlus1, str)
    }
    return ret;
}
fun getStrShorterThanLimitLength(src:String, limit:Int=12):String {
    return if(src.length > limit) src.substring(0, limit)+"…" else src
}
suspend fun createAndInsertError(repoId:String, errMsg: String) {
    if(repoId.isBlank() || errMsg.isBlank()) {
        return
    }
    val repoDb = AppModel.dbContainer.repoRepository
    if(repoDb.getById(repoId) == null) {
        MyLog.e(TAG, "$TAG#createAndInsertError: not found repo which matched the repoId '$repoId', and the errMsg is: $errMsg")
        return
    }
    repoDb.setNewErrMsg(repoId, errMsg)
    val errDb = AppModel.dbContainer.errorRepository
    errDb.insert(
        ErrorEntity(
            msg = errMsg,
            repoId = repoId,
            date = getNowInSecFormatted()
        )
    )
}
suspend fun showErrAndSaveLog(logTag:String, logMsg:String, showMsg:String, showMsgMethod:(String)->Unit, repoId:String, errMsgForErrDb:String = showMsg) {
    showMsgMethod(showMsg)
    createAndInsertError(repoId, errMsgForErrDb)
    MyLog.e(logTag, logMsg)
}
fun getHumanReadableSizeStr(size:Long):String {
    var s:Double=0.0;
    var unit = ""
    if(size >= Cons.sizeTB) {  
        s=size.toDouble()/Cons.sizeTB
        unit = Cons.sizeTBHumanRead
    }else if(size >= Cons.sizeGB) {
        s=size.toDouble()/Cons.sizeGB
        unit = Cons.sizeGBHumanRead
    }else if(size >= Cons.sizeMB) {
        s=size.toDouble()/Cons.sizeMB
        unit = Cons.sizeMBHumanRead
    }else if(size >= Cons.sizeKB) {
        s=size.toDouble()/Cons.sizeKB
        unit = Cons.sizeKBHumanRead
    }else {
        return size.toString()+Cons.sizeBHumanRead
    }
    return "%.2f".format(s) + unit
}
fun getFileAttributes(pathToFile:String): BasicFileAttributes? {
    try {
        val filePath = Paths.get(pathToFile)
        if(!filePath.exists()) return null  
        val attributes: BasicFileAttributes = Files.readAttributes(filePath, BasicFileAttributes::class.java)
        return attributes
    }catch (e:Exception) {
        MyLog.e(TAG, "#getFileAttributes err: pathToFile=$pathToFile, err=${e.localizedMessage}")
        return null
    }
}
fun doJobWithMainContext(job:()->Unit) {
    doJobThenOffLoading(coroutineDispatcher = Dispatchers.Main) {
        job()
    }
}
suspend fun withMainContext(job:()->Unit) {
    withContext(Dispatchers.Main) {
        job()
    }
}
fun addPrefix(str: String, prefix:String="*"):String {
    return prefix+str
}
fun<T> doActIfIndexGood(idx:Int, list:List<T>, act:(T)-> Unit):Ret<T?> {
    try {
        if(idx>=0 && idx<list.size) {
            val item = list[idx]
            act(item)
            return Ret.createSuccess(item)
        }
        return Ret.createError(null, "err:invalid index for list", Ret.ErrCode.invalidIdxForList)
    }catch (e:Exception) {
        MyLog.e(TAG, "#doActIfIndexGood() err: "+e.stackTraceToString())
        return Ret.createError(null, "err: "+e.localizedMessage, Ret.ErrCode.doActForItemErr)
    }
}
fun getSafeIndexOfListOrNegativeOne(indexWillCheck:Int, listSize:Int):Int {
    if(listSize<=0) {
        return -1
    }
    return indexWillCheck.coerceAtLeast(0).coerceAtMost(listSize - 1)
}
fun <T> isGoodIndexForList(index:Int, list:List<T>) = isGoodIndex(index, list.size);
fun isGoodIndexForStr(index:Int, str:String) = isGoodIndex(index, str.length);
fun isGoodIndex(index:Int, size:Int) = index >= 0 && index < size;
fun getHostFromSshUrl(sshUrl: String): String? {
    val regex = Regex("^(?:([^@]+)@)?([^:]+)(?::.*)?$")
    val matchResult = regex.matchEntire(sshUrl)
    return matchResult?.groups?.get(2)?.value 
}
fun getDomainByUrl(url:String):String {
    try {
        if(Libgit2Helper.isHttpUrl(url)) { 
            return URI.create(url).host ?: ""
        }else { 
            return getHostFromSshUrl(url) ?: ""
        }
    }catch (e:Exception) {
        MyLog.e(TAG, "#getDomainByUrl err: url=$url, err=${e.localizedMessage}")
        return ""
    }
}
fun getFormattedLastModifiedTimeOfFile(file:File):String{
    return getFormatTimeFromSec(sec = file.lastModified() / 1000)
}
fun getFormattedLastModifiedTimeOfFile(file:FuckSafFile):String{
    return getFormatTimeFromSec(sec = file.lastModified() / 1000)
}
fun<T> getFirstOrNullThenRemove(list:MutableList<T>):T? {
    try {
        return list.removeAt(0)
    }catch (e:Exception) {
        return null
    }
}
fun getViewAndSortForPath(path:String, settings:AppSettings) :Pair<Boolean, DirViewAndSort> {
    val folderViewSort = settings.files.dirAndViewSort_Map[path]
    return if(folderViewSort == null) {
        Pair(false,  settings.files.defaultViewAndSort)
    }else {
        Pair(true, folderViewSort)
    }
}
fun getFileExtOrEmpty_treatStartWithDotAsNoExt(filename:String):String {
    val extIndex = filename.lastIndexOf('.')
    return if(extIndex <= 0 || extIndex == filename.lastIndex){
        ""
    }else{
        filename.substring(extIndex)
    }
}
fun splitFileNameAndExt(filename:String) : Pair<String, String> {
    val extIndex = filename.lastIndexOf('.')
    return if(extIndex < 0) {
        Pair(filename, "")
    }else if(extIndex == 0) {
        Pair("", filename.substring(1))
    }else {  
        Pair(filename.substring(0, extIndex), filename.substring(extIndex + 1))
    }
}
fun getFileNameOrEmpty(filename:String) = splitFileNameAndExt(filename).first
fun getFileExtOrEmpty(filename:String) = splitFileNameAndExt(filename).second
fun readTimeZoneOffsetInMinutesFromSettingsOrDefault(settings: AppSettings, defaultTimeOffsetInMinutes:Int):Int {
    return readTimeZoneOffsetInMinutesFromSettingsOrDefaultNullable(settings, defaultTimeOffsetInMinutes)!!
}
fun readTimeZoneOffsetInMinutesFromSettingsOrDefaultNullable(settings: AppSettings, defaultTimeOffsetInMinutes:Int?):Int? {
    return try{
        if(settings.timeZone.followSystem) {
            AppModel.getSystemTimeZoneOffsetInMinutesCached()
        }else {
            val offsetMinutes = settings.timeZone.offsetInMinutes.trim().toInt()
            if(isValidOffsetInMinutes(offsetMinutes)){
                offsetMinutes
            }else {
                val errMsg = getInvalidTimeZoneOffsetErrMsg(offsetMinutes)
                MyLog.e(TAG, "#readTimeZoneOffsetInMinutesFromSettingsOrDefaultNullable err: $errMsg")
                throw RuntimeException(errMsg)
            }
        }
    }catch (_:Exception) {
        defaultTimeOffsetInMinutes
    }
}
fun getInvalidTimeZoneOffsetErrMsg(offsetInMinutes:Int):String {
    return "invalid timezone offset: $offsetInMinutes minutes, expect in ${getValidTimeZoneOffsetRangeInMinutes()}"
}
fun getValidTimeZoneOffsetRangeInMinutes():String {
    return "[-1080, 1080] minutes"
}
fun isValidOffsetInMinutes(offsetInMinutes:Int):Boolean {
    return offsetInMinutes >= -1080 && offsetInMinutes <= 1080
}
fun getUtcTimeInSec():Long {
    return Instant.now().epochSecond
}
fun <T> updateSelectedList(
    selectedItemList: MutableList<T>,
    itemList: List<T>,
    match:(oldSelected:T, item:T)->Boolean,  
    quitSelectionMode: () -> Unit,
    pageChanged: () -> Boolean = {false}, 
): Boolean {
    if (selectedItemList.isEmpty() || itemList.isEmpty()) {
        quitSelectionMode()
    } else {
        val stillSelectedList = mutableListOf<T>()
        selectedItemList.forEachBetter { oldSelected ->
            val found = itemList.find { match(oldSelected, it) }
            if (found != null) {
                stillSelectedList.add(found)
            }
        }
        if (pageChanged()) {
            return true
        }
        selectedItemList.clear()
        selectedItemList.addAll(stillSelectedList)
        if (selectedItemList.isEmpty()) {
            quitSelectionMode()
        }
    }
    return false
}
fun parseIntOrDefault(str:String, default:Int?):Int? {
    return try {
        str.trim().toInt()
    }catch (_:Exception){
        default
    }
}
fun parseLongOrDefault(str:String, default:Long?):Long? {
    return try {
        str.trim().toLong()
    }catch (_:Exception){
        default
    }
}
fun parseDoubleOrDefault(str:String, default:Double?):Double? {
    return try {
        str.trim().toDouble()
    }catch (_:Exception){
        default
    }
}
fun compareStringAsNumIfPossible(str1: String, str2: String, ignoreCase: Boolean = true):Int {
    val str1Num = parseDoubleOrDefault(str1, null)
    if(str1Num != null) {
        val str2Num = parseDoubleOrDefault(str2, null)
        if(str2Num != null) {
            return str1Num.compareTo(str2Num)
        }
    }
    return str1.compareTo(str2, ignoreCase = ignoreCase)
}
fun receiverFlags(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ContextCompat.RECEIVER_EXPORTED
} else {
    ContextCompat.RECEIVER_NOT_EXPORTED
}
fun copyTextToClipboard(context: Context, text: String, label:String="label") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text) 
    clipboard.setPrimaryClip(clip) 
}
fun copyAndShowCopied(
    context:Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    text:String
) {
    clipboardManager.setText(AnnotatedString(text))
    Msg.requireShow(context.getString(R.string.copied))
}
fun genHttpHostPortStr(host:String, port:String, https:Boolean = false) : String {
    val host = if(host == Cons.zero000Ip) Cons.localHostIp else host
    val prefix = if(https) "https:
    return "$prefix$host:$port"
}
fun getInstalledAppList(context:Context, selected:(AppInfo)->Boolean = {false}):List<AppInfo> {
    val packageManager = context.packageManager
    val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
    val apps = mutableListOf<AppInfo>()
    for (pkg in packages) {
        val applicationInfo = pkg.applicationInfo ?: continue
        val appInfo = rawAppInfoToAppInfo(applicationInfo, packageManager, selected) ?: continue
        apps.add(appInfo)
    }
    return apps
}
fun trimLineBreak(str:String) :String {
    return str.trim { it == '\n' || it == '\r' }
}
fun isStartInclusiveEndExclusiveRangeValid(start:Int, endExclusive:Int, size:Int):Boolean {
  return  start < endExclusive && start >= 0 && start < size && endExclusive > 0 && endExclusive <= size
}
suspend fun isLocked(mutex: Mutex):Boolean {
    delay(1)
    return mutex.isLocked
}
suspend fun doActWithLockIfFree(mutex: Mutex, whoCalled:String, act: suspend ()->Unit) {
    val logPrefix = "#doActWithLockIfFree, called by '$whoCalled'";
    if(isLocked(mutex)) {
        if(AppModel.devModeOn) {
            MyLog.d(TAG, "$logPrefix: lock is busy, task will not run")
        }
        return
    }
    if(AppModel.devModeOn) {
        MyLog.d(TAG, "$logPrefix: lock is free, will run task")
    }
    mutex.withLock { act() }
    if(AppModel.devModeOn) {
        MyLog.d(TAG, "$logPrefix: task completed")
    }
}
suspend fun apkIconOrNull(context:Context, apkPath:String, iconSizeInPx:Int): ImageBitmap? {
    return try {
        val pm = context.packageManager
        val appInfo = pm.getPackageArchiveInfo(apkPath, 0)!!
        delay(1)
        appInfo.applicationInfo!!.let {
            it.sourceDir = apkPath
            it.publicSourceDir = apkPath
            val icon = it.loadIcon(pm).toBitmapOrNull(width = iconSizeInPx, height = iconSizeInPx)!!.asImageBitmap()
            delay(1)
            icon
        }
    }catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
suspend fun getVideoThumbnail(videoPath: String): ImageBitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        delay(1)
        try {
            retriever.setDataSource(videoPath)
            val frame = retriever.getFrameAtTime(50000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)!!.asImageBitmap()
            delay(1)
            frame
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun paddingLineNumber(lineNum:String, expectLength:Int): String {
    return lineNum.padStart(expectLength, ' ')
}
fun getRangeForRenameFile(fileName:String):TextRange {
    val lastIndexOfDot = fileName.lastIndexOf('.')
    return TextRange(0, if(lastIndexOfDot > 0) lastIndexOfDot else fileName.length)
}
fun appendSecondsUnit(str:String) = str+"s";
fun parseLineAndColumn(str:String) = try {
    val lineAndColumn = str.split(":")
    val lineNum = lineAndColumn[0].trim().toInt()
    val columnNum = lineAndColumn.getOrNull(1)?.trim()?.toInt() ?: 1
    val isRelative = str.startsWith("+") || str.startsWith("-")
    LineNumParseResult(lineNum, columnNum, isRelative)
}catch (e:Exception) {
    LineNumParseResult()
}
fun onOffText(enabled:Boolean) = if(enabled) "ON" else "OFF";
fun tabToSpaces(spacesCount:Int) = if(spacesCount > 0) " ".repeat(spacesCount) else "\t"
fun getNextIndentByCurrentStr(current:String?, aTabToNSpaces:Int):String {
    if(current == null) {
        return ""
    }
    val sb = StringBuilder()
    for(i in current) {
        if(IndentChar.isIndent(i)) {
            sb.append(i)
        }else {
            break
        }
    }
    appendIndentForUnClosedSignPair(current, sb, aTabToNSpaces)
    return sb.toString()
}
private fun appendIndentForUnClosedSignPair(current: String, sb: StringBuilder, aTabToNSpaces: Int) {
    if (current.trim().let {
            it.endsWith("{")
    }) {
        sb.append(tabToSpaces(aTabToNSpaces))
    }
}
fun appAvailHeapSizeInMb():Long {
    val funName = "appAvailHeapSize"
    val runtime = Runtime.getRuntime()
    val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
    val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
    val availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB
    if(AppModel.devModeOn) {
        MyLog.i(TAG, "#$funName: ${availHeapSizeInMB}MB")
    }
    return availHeapSizeInMB
}
@Deprecated("due to it use a delay for more accurately result, so if run this in concurrency way, maybe got a backlog of many mem check tasks")
@WorkerThread
fun noMoreHeapMemThenDoAct_Deprecated(
    lowestMemInMb: Int = 30,
    lowestMemLimitCount: Int = 3,
    act: () -> Unit,
) : Boolean  {
    var lowMemCount = 0
    while (true) {
        if(appAvailHeapSizeInMb() < lowestMemInMb) {
            if(++lowMemCount >= lowestMemLimitCount) {
                act()
                return true
            }
            runBlocking { delay(100) }
        }else {
            return false
        }
    }
}
@WorkerThread
fun noMoreHeapMemThenDoAct(
    lowestMemInMb: Int = 16,
    act: () -> Unit,
) : Boolean  {
    if(appAvailHeapSizeInMb() < lowestMemInMb) {
        act()
        return true
    }else {
        return false
    }
}
