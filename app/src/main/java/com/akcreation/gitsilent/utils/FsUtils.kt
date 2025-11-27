package com.akcreation.gitsilent.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.akcreation.gitsilent.activity.findActivity
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.IntentCons
import com.akcreation.gitsilent.constants.LineNum
import com.akcreation.gitsilent.dto.Box
import com.akcreation.gitsilent.dto.EditorPayload
import com.akcreation.gitsilent.dto.FileItemDto
import com.akcreation.gitsilent.dto.FileSimpleDto
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.BuildConfig
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.utils.mime.MimeType
import com.akcreation.gitsilent.utils.mime.guessFromFile
import com.akcreation.gitsilent.utils.snapshot.SnapshotFileFlag
import com.akcreation.gitsilent.utils.snapshot.SnapshotUtil
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.temp.TempFileFlag
import org.mozilla.universalchardet.Constants
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "FsUtils"
object FsUtils {
    const val binaryMimeType = "application/octet-stream"
    const val rootName = "root"
    const val rootPath = "/"
    const val internalPathPrefix = "App:
    const val externalPathPrefix = "Ext:
    const val innerPathPrefix = "Inner:
    const val appDataPathPrefix = "AppData:
    const val contentUriPathPrefix = "content:
    const val fileUriPathPrefix = "file:
    const val absolutePathPrefix = "/"
    const val textMIME = "text/plain"
    const val appExportFolderName = "PuppyGitExport"
    const val appExportFolderNameUnderDocumentsDirShowToUser = "Documents/${appExportFolderName}"  
    enum class CopyFileConflictStrategy(val code:Int) {
        SKIP(1),
        RENAME(2),
        OVERWRITE_FOLDER_AND_FILE(3),
        ;
        companion object {
            fun fromCode(code: Int): CopyFileConflictStrategy? {
                return CopyFileConflictStrategy.entries.find { it.code == code }
            }
        }
    }
    object Patch {
        const val suffix = ".patch"
        fun getPatchDir():File{
            return AppModel.getOrCreatePatchDir()
        }
        fun newPatchFile(repoName:String, commitLeft:String, commitRight:String):File {
            val patchDir = getPatchDir()
            val parentDir = File(patchDir, repoName)
            if(!parentDir.exists()) {
                parentDir.mkdirs()
            }
            val commitLeft = Libgit2Helper.getShortOidStrByFull(commitLeft)
            val commitRight = Libgit2Helper.getShortOidStrByFull(commitRight)
            var file = File(parentDir.canonicalPath, genFileName(commitLeft, commitRight))
            if(file.exists()) {  
                file = File(parentDir.canonicalPath, genFileName(commitLeft, commitRight))
                if(file.exists()) {
                    file = File(parentDir.canonicalPath, genFileName(commitLeft, commitRight))
                    if(file.exists()) {
                        file = File(parentDir.canonicalPath, genFileName(commitLeft, commitRight))
                    }
                }
            }
            return file
        }
        private fun genFileName(commitLeft: String, commitRight: String):String {
            return "$commitLeft..$commitRight-${getShortUUID(6)}-${getNowInSecFormatted(Cons.dateTimeFormatter_yyyyMMddHHmm)}$suffix"
        }
    }
    object FileMimeTypes {
        data class MimeTypeAndDescText(
            val type:String,
            val descText:(context:Context) -> String,
        )
        val typeList = listOf(
            MimeTypeAndDescText("text/plain") {it.getString(R.string.file_open_as_type_text)},
            MimeTypeAndDescText("image* [CHINESE]app[CHINESE]；
            MimeTypeAndDescText(binaryMimeType) {it.getString(R.string.file_open_as_type_other)},
            MimeTypeAndDescText("*
    fun getAuthorityOfUri():String {
        return BuildConfig.FILE_PROVIDIER_AUTHORITY
    }
    fun getUriForFile(context: Context, file: File):Uri {
        val uri = FileProvider.getUriForFile(
            context,
            getAuthorityOfUri(),
            file
        )
        MyLog.d(TAG, "#getUriForFile: uri='$uri'")
        return uri
    }
    fun openFile(
        context: Context,
        file: File,
        mimeType: String,
        readOnly:Boolean,
        editorPayload: EditorPayload? = null
    ):Boolean {
        try {
            val uri = getUriForFile(context, file)
            val intent = Intent(Intent.ACTION_VIEW)  
            MyLog.d(TAG, "#openFile(): require open: mimeType=$mimeType, uri=$uri")
            intent.setDataAndType(uri, mimeType)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            if(!readOnly) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            if(editorPayload != null) {
                intent.putExtra(IntentCons.ExtrasKey.editorPayload, editorPayload)
            }
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            MyLog.e(TAG, "#openFile(): try open file(path=${file.canonicalPath}) err! params is: mimeType=$mimeType, readOnly=$readOnly\n" + e.stackTraceToString())
            return false
        }
    }
    fun getExportDirUnderPublicDocument():Ret<File?> {
        return createDirUnderPublicExternalDir(dirNameWillCreate=appExportFolderName, publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
    }
    private fun createDirUnderPublicExternalDir(dirNameWillCreate: String, publicDir:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)): Ret<File?> {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return Ret.createError(null, "Doesn't support export file to public dir on android version lower than 9", Ret.ErrCode.doesntSupportAndroidVersion)
        }
        val dir = File(publicDir.canonicalPath, dirNameWillCreate)
        return if(dir.exists() || dir.mkdirs()) { 
            Ret.createSuccess(dir, "open folder success!", Ret.SuccessCode.default)
        }else { 
            Ret.createError(null, "open folder failed!", Ret.ErrCode.openFolderFailed)
        }
    }
    fun getANonExistsFile(file:File):File {
        if(file.exists().not()) {
            return file
        }
        val (parent, fileName) = splitParentAndName(file.canonicalPath)
        return File(parent + getANonExistsName(fileName, exists = { newName -> File(parent+newName).exists() }))
    }
    fun splitNameAndExt(name:String):Pair<String, String> {
        val extIndex = name.lastIndexOf('.')
        return if(extIndex > 0) {  
            Pair(name.substring(0, extIndex), name.substring(extIndex))
        }else {  
            Pair(name, "")
        }
    }
    fun splitParentAndName(canonicalPath:String):Pair<String, String> {
        if(canonicalPath.isEmpty() || canonicalPath == rootPath) {
            return Pair(canonicalPath, "")
        }
        val lastSeparatorAt = canonicalPath.lastIndexOf('/')
        val fileNameStartAt = lastSeparatorAt+1
        return if(fileNameStartAt >= canonicalPath.length) { 
            Pair(canonicalPath, "")
        } else {
            Pair(canonicalPath.substring(0, fileNameStartAt), canonicalPath.substring(fileNameStartAt))
        }
    }
    fun getParentPath(canonicalPath: String):String {
        return File(canonicalPath).canonicalFile.parent ?: ""
    }
    fun getANonExistsName(name:String, exists:(String)->Boolean):String {
        var target = name
        if(exists(name)) {
            val (fileName, fileExt) = splitNameAndExt(name)
            val max = 1000
            for(i in 1..max) {
                target = "$fileName($i)$fileExt"
                if(!exists(target)) {
                    break
                }
            }
            if(exists(target)){
                while (true) {
                    target = "$fileName(${getShortUUID(len=8)})$fileExt"
                    if(!exists(target)) {
                        break
                    }
                }
            }
        }
        return target
    }
    data class PasteResult(val srcPath: String, val targetPath:String, val exception:Exception?)
    fun copyOrMoveOrExportFile(srcList:List<File>, destDir:File, requireDeleteSrc:Boolean):Ret<List<PasteResult>?> {
        if(srcList.isEmpty()) {  
            return Ret.createError(null, "srcList is empty!", Ret.ErrCode.srcListIsEmpty)  
        }
        if(!destDir.isDirectory || destDir.isFile) {  
            return Ret.createError(null, "target is a file but expect dir!", Ret.ErrCode.targetIsFileButExpectDir)
        }
        val errList = mutableListOf<PasteResult>()
        srcList.forEachBetter forEach@{
            val src = it
            var target:File? = null
            try {
                if((!src.exists()) || (src.isDirectory && destDir.canonicalPath.startsWith(src.canonicalPath))) {
                    return@forEach  
                }
                target = getANonExistsFile(File(destDir, src.name))
                src.copyRecursively(target, false)  
                if(requireDeleteSrc) {  
                    src.deleteRecursively()
                }
            }catch (e:Exception) {
                errList.add(PasteResult(src.canonicalPath, target?.canonicalPath?:"", e))
            }
        }
        return if(errList.isEmpty()) {
            Ret.createSuccess(null)
        }else {
            Ret.createError(errList, "plz check the err list")
        }
    }
    fun saveFile(fileFullPath:String, text:String, charsetName: String?) {
        FileOutputStream(fileFullPath).use { fos ->
            EncodingUtil.addBomIfNeed(fos, charsetName)
            fos.bufferedWriter(EncodingUtil.resolveCharset(charsetName)).use {
                it.write(text)
            }
        }
    }
    fun saveFileAndGetResult(fileFullPath:String, text:String, charsetName: String?):Ret<Unit?> {
        try {
            saveFile(fileFullPath, text, charsetName)
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#saveFileAndGetResult() err: "+e.stackTraceToString())
            return Ret.createError(null, "save file failed: ${e.localizedMessage}", Ret.ErrCode.saveFileErr)
        }
    }
    fun readFile(fileFullPath: String, charset:Charset? = null):String {
        val br = FileInputStream(fileFullPath).bufferedReader(charset ?: EncodingUtil.resolveCharset(
            EncodingUtil.detectEncoding(
                newInputStream = { FileInputStream(fileFullPath) }
            )
        ))
        br.use {
            return it.readText()
        }
    }
    fun getDocumentFileFromUri(context: Context, fileUri:Uri):DocumentFile? {
        return DocumentFile.fromSingleUri(context, fileUri)
    }
    fun getFileRealNameFromUri(context: Context?, fileUri: Uri?): String? {
        if (context == null || fileUri == null) return null
        val documentFile: DocumentFile = getDocumentFileFromUri(context, fileUri) ?: return null
        val name = documentFile.name
        return if(name.isNullOrEmpty()) null else name
    }
    fun recursiveExportFiles_Saf(
        contentResolver: ContentResolver,
        targetDir: DocumentFile,
        srcFiles: Array<File>,
        ignorePaths:List<String> = listOf(),
        canceled:()->Boolean = {false},
        conflictStrategy:CopyFileConflictStrategy = CopyFileConflictStrategy.RENAME,
    ) {
        if(canceled()) {
            throw CancellationException()
        }
        val filesUnderExportDir = targetDir.listFiles() ?: arrayOf<DocumentFile>()
        for(f in srcFiles) {
            if(canceled()) {
                throw CancellationException()
            }
            if(ignorePaths.contains(f.canonicalPath)) {
                continue
            }
            var targetName = f.name
            val targetFileBeforeCreate = filesUnderExportDir.find { it.name == targetName }
            if(targetFileBeforeCreate != null) {  
                if(conflictStrategy == CopyFileConflictStrategy.SKIP) {
                    continue
                }else if(conflictStrategy == CopyFileConflictStrategy.OVERWRITE_FOLDER_AND_FILE) {
                    if(targetFileBeforeCreate.isDirectory) {   
                        recursiveDeleteFiles_Saf(contentResolver, targetFileBeforeCreate, targetFileBeforeCreate.listFiles() ?: arrayOf<DocumentFile>(), canceled)
                    }else {  
                        targetFileBeforeCreate.delete()
                    }
                }else if(conflictStrategy == CopyFileConflictStrategy.RENAME) {
                    targetName = getANonExistsName(targetName, exists = {newName -> filesUnderExportDir.find { it.name == newName } != null})
                }
            }
            if(f.isDirectory) {
                val nextTargetDir = targetDir.createDirectory(targetName)?:continue
                val nextSrcFiles = f.listFiles()?:continue
                if(nextSrcFiles.isNotEmpty()) {
                    recursiveExportFiles_Saf(
                        contentResolver = contentResolver,
                        targetDir = nextTargetDir,
                        srcFiles = nextSrcFiles,
                        ignorePaths = ignorePaths,
                        canceled = canceled,
                        conflictStrategy = conflictStrategy
                    )
                }
            }else {
                val targetFile = targetDir.createFile(binaryMimeType, targetName)?:continue
                val output = contentResolver.openOutputStream(targetFile.uri)?:continue
                f.inputStream().use { ins->
                    output.use { outs ->
                        ins.copyTo(outs)
                    }
                }
            }
        }
    }
    fun recursiveImportFiles_Saf(
        contentResolver: ContentResolver,
        targetDir: File,
        srcFiles: Array<DocumentFile>,
        canceled:()->Boolean = {false},
        conflictStrategy:CopyFileConflictStrategy = CopyFileConflictStrategy.RENAME,
    ) {
        if(canceled()) {
            throw CancellationException()
        }
        val filesUnderImportDir = targetDir.listFiles() ?: arrayOf<File>()
        for(f in srcFiles) {
            if(canceled()) {
                throw CancellationException()
            }
            var targetName = f.name ?: continue
            val targetFileBeforeCreate = filesUnderImportDir.find { it.name == targetName }
            if(targetFileBeforeCreate != null) {  
                if(conflictStrategy == CopyFileConflictStrategy.SKIP) {
                    continue
                }else if(conflictStrategy == CopyFileConflictStrategy.OVERWRITE_FOLDER_AND_FILE) {
                    targetFileBeforeCreate.deleteRecursively()
                }else if(conflictStrategy == CopyFileConflictStrategy.RENAME) {
                    targetName = getANonExistsName(targetName, exists = {newName -> filesUnderImportDir.find { it.name == newName } != null})
                }
            }
            val nextTarget = File(targetDir.canonicalPath, targetName)
            if(f.isDirectory) {
                nextTarget.mkdirs()
                val nextSrcFiles = f.listFiles() ?: continue
                if(nextSrcFiles.isNotEmpty()) {
                    recursiveImportFiles_Saf(
                        contentResolver = contentResolver,
                        targetDir = nextTarget,
                        srcFiles = nextSrcFiles,
                        canceled = canceled,
                        conflictStrategy = conflictStrategy
                    )
                }
            }else {
                nextTarget.createNewFile()
                val inputStream = contentResolver.openInputStream(f.uri)?:continue
                val outputStream = nextTarget.outputStream()
                inputStream.use { ins->
                    outputStream.use { outs ->
                        ins.copyTo(outs)
                    }
                }
            }
        }
    }
    private fun recursiveDeleteFiles_Saf(
        contentResolver: ContentResolver,
        targetDir: DocumentFile,
        filesUnderTargetDir: Array<DocumentFile>,
        canceled:()->Boolean,
    ) {
        if(canceled()) {
            throw CancellationException()
        }
        for(f in filesUnderTargetDir) {
            if(canceled()) {
                throw CancellationException()
            }
            if(f.isDirectory) {
                val nextTargetFiles = f.listFiles() ?: arrayOf<DocumentFile>()
                if(nextTargetFiles.isEmpty()) {  
                    f.delete()
                }else {  
                    recursiveDeleteFiles_Saf(contentResolver, f, nextTargetFiles, canceled)
                }
            }else {
                f.delete()
            }
        }
        targetDir.delete()
    }
    fun simpleSafeFastSave(
        context: Context,
        content: String?,
        editorState: TextEditorState,
        trueUseContentFalseUseEditorState: Boolean,
        targetFilePath: FilePath,
        requireBackupContent: Boolean,
        requireBackupFile: Boolean,
        contentSnapshotFlag: SnapshotFileFlag,
        fileSnapshotFlag: SnapshotFileFlag
    ): Ret<Triple<Boolean, String, String>> {
        var contentAndFileSnapshotPathPair = Pair("","")
        try {
            val targetFile = FuckSafFile(context, targetFilePath)
            val contentRet = if(requireBackupContent) {
                SnapshotUtil.createSnapshotByContentAndGetResult(
                    srcFileName = targetFile.name,
                    fileContent = content,
                    editorState = editorState,
                    trueUseContentFalseUseEditorState = trueUseContentFalseUseEditorState,
                    flag = contentSnapshotFlag
                )
            }else {
                Ret.createSuccess(null, "no require backup content yet")
            }
            val fileRet = if(requireBackupFile) {
                SnapshotUtil.createSnapshotByFileAndGetResult(targetFile, fileSnapshotFlag)
            } else {
                Ret.createSuccess(null, "no require backup file yet")
            }
            if(contentRet.hasError() && fileRet.hasError()) {
                throw RuntimeException("save content and file snapshots err")
            }
            if(contentRet.hasError()) {
                contentAndFileSnapshotPathPair = Pair("", fileRet.data?.second?:"")
                throw RuntimeException("save content snapshot err")
            }
            if(fileRet.hasError()) {
                contentAndFileSnapshotPathPair = Pair(contentRet.data?.second?:"", "")
                throw RuntimeException("save file snapshot err")
            }
            contentAndFileSnapshotPathPair = Pair(contentRet.data?.second?:"", fileRet.data?.second?:"")
            if(trueUseContentFalseUseEditorState) {
                val charsetName = editorState.codeEditor?.editorCharset?.value
                targetFile.outputStream().use { output ->
                    EncodingUtil.addBomIfNeed(output, charsetName)
                    output.bufferedWriter(EncodingUtil.resolveCharset(charsetName)).use { writer ->
                        writer.write(content!!)
                    }
                }
            }else {
                editorState!!.dumpLines(targetFile.outputStream())
            }
            val writeContentToTargetFileSuccess = true
            return Ret.createSuccess(Triple(writeContentToTargetFileSuccess, contentAndFileSnapshotPathPair.first, contentAndFileSnapshotPathPair.second))
        }catch (e:Exception) {
            MyLog.e(TAG, "#simpleSafeFastSave: err: "+e.stackTraceToString())
            val writeContentToTargetFileSuccess = false
            return Ret.createError(Triple(writeContentToTargetFileSuccess, contentAndFileSnapshotPathPair.first, contentAndFileSnapshotPathPair.second), "SSFS: save err: "+e.localizedMessage)
        }
    }
    fun getDoSaveForEditor(
        editorPageShowingFilePath: MutableState<FilePath>,
        editorPageLoadingOn: (String) -> Unit,
        editorPageLoadingOff: () -> Unit,
        activityContext: Context,
        editorPageIsSaving: MutableState<Boolean>,
        needRefreshEditorPage: MutableState<String>,
        editorPageTextEditorState: CustomStateSaveable<TextEditorState>,
        pageTag: String,
        editorPageIsEdited: MutableState<Boolean>,
        requestFromParent: MutableState<String>,
        editorPageFileDto: CustomStateSaveable<FileSimpleDto>,
        isSubPageMode:Boolean,
        isContentSnapshoted: MutableState<Boolean>,
        snapshotedFileInfo: CustomStateSaveable<FileSimpleDto>,  
        lastSavedFieldsId: MutableState<String>,
    ): suspend () -> Unit {
        val doSave: suspend () -> Unit = doSave@{
            val funName ="doSave"  
            editorPageIsSaving.value = true
            editorPageLoadingOn(activityContext.getString(R.string.saving))
            val editorPageTextEditorState = Box(editorPageTextEditorState.value)
            try {
                val filePath = editorPageShowingFilePath.value
                if (filePath.isEmpty()) {
                    if(editorPageTextEditorState.value.contentIsEmpty().not() && !isContentSnapshoted.value ) {
                        MyLog.w(pageTag, "#$funName: filePath is empty, but content is not empty, will create content snapshot with a random filename...")
                        val flag = SnapshotFileFlag.editor_content_FilePathEmptyWhenSave_Backup
                        val contentSnapRet = SnapshotUtil.createSnapshotByContentWithRandomFileName(
                            fileContent = null,
                            editorState = editorPageTextEditorState.value,
                            trueUseContentFalseUseEditorState = false,
                            flag = flag
                        )
                        if (contentSnapRet.hasError()) {
                            MyLog.e(pageTag, "#$funName: create content snapshot for empty path failed:" + contentSnapRet.msg)
                            throw RuntimeException("path is empty, and save content snapshot err")
                        }else {
                            isContentSnapshoted.value=true
                            throw RuntimeException("path is empty, but save content snapshot success")
                        }
                    }
                    throw RuntimeException("path is empty!")
                }
                val targetFile = filePath.toFuckSafFile(activityContext)
                if(targetFile.exists()) {
                    val newDto = FileSimpleDto.genByFile(targetFile)
                    if(newDto != editorPageFileDto.value) { 
                        if(newDto != snapshotedFileInfo.value) {  
                            MyLog.w(pageTag, "#$funName: warn! file maybe modified by external! will create a snapshot before save...")
                            val snapRet = SnapshotUtil.createSnapshotByFileAndGetResult(targetFile, SnapshotFileFlag.editor_file_BeforeSave)
                            if (snapRet.hasError()) {
                                MyLog.e(pageTag, "#$funName: create file snapshot for '$filePath' failed:" + snapRet.msg)
                                if(editorPageTextEditorState.value.contentIsEmpty().not() && !isContentSnapshoted.value) {
                                    val contentSnapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                                        srcFileName = targetFile.name,
                                        fileContent = null,
                                        editorState = editorPageTextEditorState.value,
                                        trueUseContentFalseUseEditorState = false,
                                        flag = SnapshotFileFlag.editor_content_CreateSnapshotForExternalModifiedFileErrFallback
                                    )
                                    if (contentSnapRet.hasError()) {
                                        MyLog.e(pageTag, "#$funName: create content snapshot for '$filePath' failed:" + contentSnapRet.msg)
                                        throw RuntimeException("save origin file and content snapshots err")
                                    }else {
                                        isContentSnapshoted.value=true
                                        throw RuntimeException("save origin file snapshot err, but save content snapshot success")
                                    }
                                }else {
                                    throw RuntimeException("save origin file snapshot err, and content is empty or snapshot already exists")
                                }
                            }else { 
                                snapshotedFileInfo.value = newDto
                            }
                        }else {
                            MyLog.d(pageTag, "#$funName: file snapshot of '$filePath' already exists")
                        }
                    }
                }
                val ret = FsUtils.simpleSafeFastSave(
                    context = activityContext,
                    content = null,
                    editorState = editorPageTextEditorState.value,
                    trueUseContentFalseUseEditorState = false,
                    targetFilePath = filePath,
                    requireBackupContent = true,
                    requireBackupFile = true,
                    contentSnapshotFlag = SnapshotFileFlag.editor_content_NormalDoSave,
                    fileSnapshotFlag = SnapshotFileFlag.editor_file_NormalDoSave
                )
                val (_, contentSnapshotPath, _) = ret.data
                if(contentSnapshotPath.isNotEmpty()) {  
                    isContentSnapshoted.value=true
                }
                if (ret.hasError()) {
                    MyLog.e(pageTag, "#$funName: save file '$filePath' failed:" + ret.msg)
                    if (editorPageTextEditorState.value.contentIsEmpty().not() && !isContentSnapshoted.value) {
                        val snapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                            srcFileName = targetFile.name,
                            fileContent = null,
                            editorState = editorPageTextEditorState.value,
                            trueUseContentFalseUseEditorState = false,
                            flag = SnapshotFileFlag.editor_content_SaveErrFallback
                        )
                        if (snapRet.hasError()) {
                            MyLog.e(pageTag, "#$funName: save content snapshot for '$filePath' failed:" + snapRet.msg)
                            throw RuntimeException("save file and content snapshots err")
                        }else {
                            isContentSnapshoted.value=true
                            throw RuntimeException("save file err, but save content snapshot success")
                        }
                    }else {
                        MyLog.w(pageTag, "#$funName: save file failed, but content is empty or already snapshoted, so will not create snapshot for it")
                        throw RuntimeException(ret.msg)
                    }
                } else {  
                    editorPageFileDto.value = FileSimpleDto.genByFile(FuckSafFile(activityContext, filePath))
                    editorPageIsEdited.value = false
                    lastSavedFieldsId.value = editorPageTextEditorState.value.fieldsId
                    Msg.requireShow(activityContext.getString(R.string.file_saved))
                }
            }catch (e:Exception){
                editorPageIsEdited.value=true  
                throw e
            }finally {
                editorPageIsSaving.value = false
                editorPageLoadingOff()
            }
        }
        return doSave
    }
    fun delFilesOverKeepInDays(keepInDays: Int, folder: File, folderDesc:String) {
        val funName = "delFilesOverKeepInDays"
        try {
            MyLog.w(TAG, "#$funName: start: del expired files for '$folderDesc'")
            val keepInDaysInMillSec = keepInDays*24*60*60*1000L
            val currentTimeInMillSec = System.currentTimeMillis()
            val predicate = predicate@{f:File ->
                if(!f.isFile) {
                    return@predicate false
                }
                val lastModTimeInMillSec = f.lastModified()
                val diffInMillSec = currentTimeInMillSec - lastModTimeInMillSec
                return@predicate diffInMillSec > keepInDaysInMillSec
            }
            val successDeletedCount = delFilesByPredicate(predicate, folder, folderDesc)
            MyLog.w(TAG, "#$funName: end: del expired files for '$folderDesc' done, success deleted: $successDeletedCount")
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: del expired files for '$folderDesc' err: ${e.stackTraceToString()}")
        }
    }
    fun delFilesByPredicate(predicate:(File)->Boolean, folder: File, folderDesc:String):Int {
        val funName = "delFilesByPredicate"
        var successDeleteFilesCount = 0  
        try {
            MyLog.w(TAG, "#$funName: checking '$folderDesc' is ready for delete files or not")
            if(!folder.exists()) {
                MyLog.w(TAG, "#$funName: '$folderDesc' doesn't exist yet, operation abort")
                return successDeleteFilesCount
            }
            val files = folder.listFiles()
            if(files==null) {
                MyLog.w(TAG, "#$funName: list files for '$folderDesc' returned null, operation abort")
                return successDeleteFilesCount
            }
            if(files.isEmpty()) {
                MyLog.w(TAG, "#$funName: '$folderDesc' is empty, operation abort")
                return successDeleteFilesCount
            }
            MyLog.w(TAG, "#$funName: '$folderDesc' passed check, will start del files for it")
            for(f in files){
                try {
                    if(predicate(f)){
                        f.delete()
                        successDeleteFilesCount++
                    }
                }catch (e:Exception) {
                    MyLog.e(TAG, "#$funName: del file '${f.name}' for $folderDesc err: "+e.stackTraceToString())
                }
            }
            if(successDeleteFilesCount==0) {
                MyLog.w(TAG, "#$funName: no file need del in '$folderDesc'")
            }else {
                MyLog.w(TAG, "#$funName: deleted $successDeleteFilesCount file(s) for '$folderDesc'")
            }
            return successDeleteFilesCount
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: del files for '$folderDesc' err: "+e.stackTraceToString())
            return successDeleteFilesCount
        }
    }
    @Deprecated("[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]jetpackcompose[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]")
    fun deprecated_isReadOnlyDir(path: String): Boolean {
        return try {
            path.startsWith(AppModel.getOrCreateFileSnapshotDir().canonicalPath)
                    || path.startsWith(AppModel.getOrCreateEditCacheDir().canonicalPath)
                    || path.startsWith(AppModel.getOrCreateLogDir().canonicalPath)
                    || path.startsWith(AppModel.certBundleDir.canonicalPath)
                    || path.startsWith(AppModel.getOrCreateSettingsDir().canonicalPath)
                    || path.startsWith(AppModel.getOrCreateSubmoduleDotGitBackupDir().canonicalPath)
                    || path.startsWith(Lg2HomeUtils.getLg2Home().canonicalPath)
        }catch (e:Exception) {
            MyLog.e(TAG, "#isReadOnlyDir err: ${e.stackTraceToString()}")
            false
        }
    }
    fun calculateFolderSize(fileOrFolder: File, itemsSize: MutableLongState) {
        if(fileOrFolder.isDirectory) {
            val list = fileOrFolder.listFiles()
            if(!list.isNullOrEmpty()) {
                list.forEachBetter {
                    calculateFolderSize(it, itemsSize)
                }
            }
        }else {  
            itemsSize.longValue += fileOrFolder.length()
        }
    }
    fun getAppCeilingPaths():List<String> {
        return listOf(
            getExternalStorageRootPathNoEndsWithSeparator(),
            AppModel.innerDataDir.canonicalPath,
            rootPath
        )
    }
    fun getInternalStorageRootPathNoEndsWithSeparator():String {
        return AppModel.allRepoParentDir.canonicalPath
    }
    fun getAppDataRootPathNoEndsWithSeparator():String {
        return AppModel.appDataUnderAllReposDir.canonicalPath
    }
    fun getExternalStorageRootPathNoEndsWithSeparator():String{
        return try {
            Environment.getExternalStorageDirectory().canonicalPath
        }catch (_:Exception) {
            ""
        }
    }
    fun getInnerStorageRootPathNoEndsWithSeparator():String{
        return try {
            AppModel.innerDataDir.canonicalPath
        }catch (_:Exception) {
            ""
        }
    }
    @Deprecated("[CHINESE]，[CHINESE]")
    fun getRealPathFromUri(uri:Uri):String {
        return try {
            val uriPathString = uri.path ?: throw NullPointerException("`uri.path` is null")
            (getExternalStorageRootPathNoEndsWithSeparator() + Cons.slash + uriPathString.let{ it.substring(it.indexOf(":") + 1).trim(Cons.slashChar) }).trimEnd(Cons.slashChar)
        }catch (_:Exception) {
            ""
        }
    }
    fun internalExternalPrefixPathToRealPath(path:String):String {
        return if(path.startsWith(appDataPathPrefix)) {
            (getAppDataRootPathNoEndsWithSeparator() + Cons.slash + removeAppDataPrefix(path)).trimEnd(Cons.slashChar)
        }else if(path.startsWith(internalPathPrefix)) {
            (getInternalStorageRootPathNoEndsWithSeparator() + Cons.slash + removeInternalStoragePrefix(path)).trimEnd(Cons.slashChar)
        }else if(path.startsWith(externalPathPrefix)) {
            (getExternalStorageRootPathNoEndsWithSeparator() + Cons.slash + removeExternalStoragePrefix(path)).trimEnd(Cons.slashChar)
        }else if(path.startsWith(innerPathPrefix)) {
            (getInnerStorageRootPathNoEndsWithSeparator() + Cons.slash + removeInnerStoragePrefix(path)).trimEnd(Cons.slashChar)
        }else {
            path
        }
    }
    fun userInputPathToCanonical(path: String):Ret<String?> {
        try{
            val path = internalExternalPrefixPathToRealPath(trimLineBreak(path))
            if(path.isBlank()) {
                throw RuntimeException("invalid path")
            }
            return Ret.createSuccess(data = File(path).canonicalPath)
        }catch (e:Exception) {
            return Ret.createError(data = null, errMsg = "err: ${e.localizedMessage}", exception = e)
        }
    }
    @Deprecated("this too complex, only trim line break then use `File(path).cononicalPath` is enough")
    fun trimPath(path:String, appendEndSlash:Boolean = false):String {
        val path = trimLineBreak(path)
        return if(path.length == 1) { 
            path
        }else {  
            val slash = '/'
            val pathStartsWithSlash = path.startsWith(slash)
            val path = path.trimEnd(slash)
            if(pathStartsWithSlash && path.isEmpty()) {
                slash.toString()
            }else {
                val pathWithoutEndSlash = if(pathStartsWithSlash) {
                    "$slash${path.trimStart(slash)}"
                }else {
                    path
                }
                if(appendEndSlash) {
                    "$pathWithoutEndSlash$slash"
                }else {
                    pathWithoutEndSlash
                }
            }
        }
    }
    fun getPathAfterParent(parent: String, fullPath: String): String {
        return fullPath.removePrefix(parent)
    }
    fun getPathWithInternalOrExternalPrefix(fullPath:String) :String {
        val appDataRoot = getAppDataRootPathNoEndsWithSeparator()
        val internalStorageRoot = getInternalStorageRootPathNoEndsWithSeparator()
        val externalStorageRoot = getExternalStorageRootPathNoEndsWithSeparator()
        val innerStorageRoot = getInnerStorageRootPathNoEndsWithSeparator()
        return if(fullPath.startsWith(appDataRoot)) {  
            appDataPathPrefix+((getPathAfterParent(parent= appDataRoot, fullPath=fullPath)).removePrefix("/"))
        }else if(fullPath.startsWith(internalStorageRoot)) {  
            internalPathPrefix+((getPathAfterParent(parent= internalStorageRoot, fullPath=fullPath)).removePrefix("/"))
        }else if(fullPath.startsWith(externalStorageRoot)) {
            externalPathPrefix+((getPathAfterParent(parent= externalStorageRoot, fullPath=fullPath)).removePrefix("/"))
        }else if(fullPath.startsWith(innerStorageRoot)) {
            innerPathPrefix+((getPathAfterParent(parent= innerStorageRoot, fullPath=fullPath)).removePrefix("/"))
        }else { 
            fullPath
        }
    }
    fun removeInternalStoragePrefix(path: String): String {
        return path.removePrefix(internalPathPrefix)
    }
    fun removeAppDataPrefix(path: String): String {
        return path.removePrefix(appDataPathPrefix)
    }
    fun removeExternalStoragePrefix(path: String): String {
        return path.removePrefix(externalPathPrefix)
    }
    fun removeInnerStoragePrefix(path: String): String {
        return path.removePrefix(innerPathPrefix)
    }
    fun stringToLines(string: String):List<String> {
        return string.lines()
    }
    private suspend fun replaceOrInsertOrDeleteLinesToFile(
        file: FuckSafFile,
        startLineNum: Int,
        newLines: List<String>,
        trueInsertFalseReplaceNullDelete:Boolean?,
        settings: AppSettings,
        charsetName: String = Constants.CHARSET_UTF_8
    ) {
        if(trueInsertFalseReplaceNullDelete != null && newLines.isEmpty()) {
            return
        }
        if(startLineNum<1 && startLineNum!=LineNum.EOF.LINE_NUM) {
            throw RuntimeException("invalid line num")
        }
        if(file.exists().not()) {
            throw RuntimeException("target file doesn't exist")
        }
        if(settings.diff.createSnapShotForOriginFileBeforeSave) {
            val snapRet = SnapshotUtil.createSnapshotByFileAndGetResult(file, SnapshotFileFlag.diff_file_BeforeSave)
            if(snapRet.hasError()) {
                throw (snapRet.exception ?: RuntimeException(snapRet.msg.ifBlank { "err: create snapshot failed" }))
            }
        }
        val tempFile = FuckSafFile.fromFile(createTempFile("${TempFileFlag.FROM_DIFF_SCREEN_REPLACE_LINES_TO_FILE.flag}-${file.name}"))
        var found = false
        val lineBreak = file.detectLineBreak(charsetName).value
        file.bufferedReader(charsetName).use { reader ->
            tempFile.bufferedWriter(charsetName).use { writer ->
                var currentLine = 1
                while(true) {
                    val line = reader.readLine() ?:break
                    if (currentLine++ == startLineNum) {
                        found = true
                        if(trueInsertFalseReplaceNullDelete == null) {
                            continue
                        }
                        for(i in newLines.indices) {
                            writer.write(newLines[i])
                            writer.write(lineBreak)
                        }
                        if(trueInsertFalseReplaceNullDelete == true) {
                            writer.write(line)
                            writer.write(lineBreak)
                        }
                    }else {  
                        writer.write(line)
                        writer.write(lineBreak)
                    }
                }
                if (found.not() && trueInsertFalseReplaceNullDelete!=null) {
                    for(i in newLines.indices) {
                        writer.write(newLines[i])
                        writer.write(lineBreak)
                    }
                }
            }
        }
        if(found.not() && trueInsertFalseReplaceNullDelete==null){  
            tempFile.delete()
        }else {  
            tempFile.renameTo(file)
        }
    }
    suspend fun replaceLinesToFile(file: FuckSafFile, startLineNum: Int, newLines: List<String>, settings: AppSettings) {
        replaceOrInsertOrDeleteLinesToFile(file, startLineNum, newLines, trueInsertFalseReplaceNullDelete = false, settings)
    }
    suspend fun insertLinesToFile(file: FuckSafFile, startLineNum: Int, newLines: List<String>, settings: AppSettings) {
        prependLinesToFile(file, startLineNum, newLines, settings)
    }
    suspend fun prependLinesToFile(file: FuckSafFile, startLineNum: Int, newLines: List<String>, settings: AppSettings) {
        replaceOrInsertOrDeleteLinesToFile(file, startLineNum, newLines, trueInsertFalseReplaceNullDelete = true, settings)
    }
    suspend fun appendLinesToFile(file: FuckSafFile, startLineNum: Int, newLines: List<String>, settings: AppSettings) {
        replaceOrInsertOrDeleteLinesToFile(file, startLineNum+1, newLines, trueInsertFalseReplaceNullDelete = true, settings)
    }
    suspend fun deleteLineToFile(file: FuckSafFile, lineNum: Int, settings: AppSettings) {
        replaceOrInsertOrDeleteLinesToFile(file, lineNum, newLines=listOf(), trueInsertFalseReplaceNullDelete = null, settings)
    }
    fun createTempFile(prefix:String, suffix:String=".tmp"):File{
        return File(AppModel.getOrCreateExternalCacheDir().canonicalPath, "$prefix-${generateRandomString(8)}$suffix")
    }
    fun readLinesFromFile(
        file: FuckSafFile,
        charsetName: String?,
        addNewLineIfFileEmpty:Boolean = true,
    ): List<String> {
        val estimateLineCount = (file.length() shr 7).toInt().coerceAtLeast(10).coerceAtMost(10000) 
        val lines = ArrayList<String>(estimateLineCount)  
        val arrBuf = CharArray(4096)
        val aline = StringBuilder(100)
        var lastChar:Char? = null
        file.bufferedReader(charsetName).use { reader ->
            while (true) {
                val readSize = reader.read(arrBuf)
                if(readSize == -1) {
                    break
                }
                for (i in 0 until readSize) {
                    val char = arrBuf[i]
                    if (char == '\n'){
                        if(lastChar != '\r') {
                            lines.add(aline.toString())
                            aline.clear()
                        }
                    } else if(char == '\r') {
                        lines.add(aline.toString())
                        aline.clear()
                    } else {
                        aline.append(char)
                    }
                    lastChar = char
                }
            }
        }
        if(aline.isNotEmpty()) {
            lines.add(aline.toString())
        }else if(lastChar != null) {
            lines.add("")
        }
        if (addNewLineIfFileEmpty && lines.isEmpty()) {
            lines.add("")
        }
        return lines
    }
    fun isNotRelativePath(path: String):Boolean {
        return path.startsWith("https:
                || path.startsWith("content:
                || path.startsWith("/") || path.startsWith("ftp:
    }
    fun maybeIsRelativePath(path: String) :Boolean {
        return isNotRelativePath(path).not()
    }
    fun getAbsolutePathIfIsRelative(path:String, basePathNoEndSlash: String):String {
        if(maybeIsRelativePath(path)) {
            return File(basePathNoEndSlash, path).canonicalPath
        }
        return path
    }
    fun makeThePathCanonical(path: String): String {
        if(path.isBlank()) {
            return ""
        }
        return File(path).canonicalPath
    }
    fun copy(inputStream:InputStream, outputStream:OutputStream) {
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
    fun appendTextToFile(file: File, text:String, charset: Charset = StandardCharsets.UTF_8) {
        if(text.isEmpty()) {
            return
        }
        val append = true
        val filerWriter = OutputStreamWriter(FileOutputStream(file, append), charset)
        filerWriter.buffered().use { writer ->
            writer.write(text)
        }
    }
    fun readShortContent(
        file: FuckSafFile,
        charsetName: String? = null,
        contentCharsLimit:Int = 80
    ):String {
        return try {
            val sb = StringBuilder()
            file.bufferedReader(charsetName ?: file.detectEncoding()).use { br ->
                while (true) {
                    if(sb.length >= contentCharsLimit) {
                        break
                    }
                    val line = br.readLine() ?: break
                    line.trim().let {
                        if(it.isNotBlank()) {
                            sb.appendLine(it)
                        }
                    }
                }
            }
            if(sb.length <= contentCharsLimit) {
                sb.toString()
            }else {
                sb.substring(0, contentCharsLimit)
            }
        }catch (e: Exception) {
            MyLog.d(TAG, "readShortContent of file err: fileIoPath=${file.path.ioPath}, err=${e.localizedMessage}")
            ""
        }
    }
    fun getPathWithInternalOrExternalPrefixAndRemoveFileNameAndEndSlash(path:String, fileName:String):String {
        return if(path.startsWith(contentUriPathPrefix)) {
            path
        }else {
            getPathWithInternalOrExternalPrefix(path.removeSuffix(fileName).trimEnd(Cons.slashChar)).ifBlank { Cons.slash }
        }
    }
    fun translateContentUriToRealPath(uri: Uri, appContext:Context = AppModel.realAppContext, mode: String = "rw"): String? {
        val funName = "translateContentUriToRealPath"
        try {
            val resolver = appContext.contentResolver
            MyLog.d(TAG, "#$funName: Resolving content URI: $uri")
            resolver.openFileDescriptor(uri, mode)?.use { pfd ->
                val path = findRealPath(pfd.fd)
                if (path != null) {
                    MyLog.d(TAG, "#$funName: Found real file path: $path")
                    return path
                }
            }
        } catch(e: Exception) {
            MyLog.w(TAG, "#$funName: Failed to open content fd: ${e.localizedMessage}")
            e.printStackTrace()
        }
        return null
    }
    fun findRealPath(fd: Int): String? {
        var ins: InputStream? = null
        try {
            val path = File("/proc/self/fd/${fd}").canonicalPath
            if (!path.startsWith("/proc") && File(path).canRead()) {
                ins = FileInputStream(path)
                ins.read()
                return path
            }
        } catch(e: Exception) { } finally { ins?.close() }
        return null
    }
    fun shareFiles(activityContext: Context, files: List<FileItemDto>) {
        if(files.isEmpty()) {
            return
        }
        val uris = mutableListOf<Uri>()
        val mimeTypes = mutableListOf<MimeType>()
        for (f in files) {
            if(f.isFile) {
                val file = f.toFile()
                uris.add(getUriForFile(activityContext, file))
                mimeTypes.add(MimeType.guessFromFile(file))
            }
        }
        if(uris.isEmpty()) {
            return
        }
        val intent = uris.createSendStreamIntent(activityContext, mimeTypes)
            .withChooser()
        ActivityUtil.startActivitySafe(activityContext.findActivity(), intent)
    }
}
