package com.akcreation.gitsilent.utils.snapshot

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.EncodingUtil
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.getNowInSecFormatted
import com.akcreation.gitsilent.utils.getShortUUID
import java.io.File

object SnapshotUtil:SnapshotCreator {
    private const val TAG = "SnapshotUtil"
    private val fileSnapshotDisable_FileNamePlaceHolder = "FileSnapshotDisable-name"  
    private val fileSnapshotDisable_FilePathPlaceHolder = "FileSnapshotDisable-path"  
    private val contentSnapshotDisable_FileNamePlaceHolder = "ContentSnapshotDisable-name"  
    private val contentSnapshotDisable_FilePathPlaceHolder = "ContentSnapshotDisable-path"  
    private var enableFileSnapshotForEditor = true
    private var enableContentSnapshotForEditor = true
    private var enableFileSnapshotForDiff = true
    fun init(enableFileSnapshotForEditorInitValue:Boolean, enableContentSnapshotForEditorInitValue:Boolean, enableFileSnapshotForDiffInitValue:Boolean) {
        enableFileSnapshotForEditor = enableFileSnapshotForEditorInitValue
        enableContentSnapshotForEditor = enableContentSnapshotForEditorInitValue
        enableFileSnapshotForDiff = enableFileSnapshotForDiffInitValue
    }
    fun update_enableFileSnapshotForEditor(newValue:Boolean) {
        enableFileSnapshotForEditor = newValue
    }
    fun update_enableContentSnapshotForEditor(newValue: Boolean) {
        enableContentSnapshotForEditor = newValue
    }
    fun update_enableFileSnapshotForDiff(newValue: Boolean) {
        enableFileSnapshotForDiff = newValue
    }
    override fun createSnapshotByContentAndGetResult(
        srcFileName:String,
        fileContent:String?,
        editorState: TextEditorState,
        trueUseContentFalseUseEditorState: Boolean,
        flag:SnapshotFileFlag
    ): Ret<Pair<String, String>?> {
        if(!enableContentSnapshotForEditor) {
            return Ret.createSuccess(Pair(contentSnapshotDisable_FileNamePlaceHolder, contentSnapshotDisable_FilePathPlaceHolder))
        }
        val funName = "createSnapshotByContentAndGetResult"
        try {
            if((trueUseContentFalseUseEditorState && fileContent!!.isNotEmpty()) || (trueUseContentFalseUseEditorState.not() && editorState!!.contentIsEmpty().not())) {
                val (snapshotFileName, snapFileFullPath, snapFile) = getSnapshotFileNameAndFullPathAndFile(srcFileName, flag)
                MyLog.d(TAG, "#$funName: will save snapFile to: '$snapFileFullPath'")
                val snapRet = if(trueUseContentFalseUseEditorState) {
                    FsUtils.saveFileAndGetResult(
                        fileFullPath = snapFileFullPath,
                        text = fileContent!!,
                        charsetName = editorState.codeEditor?.editorCharset?.value
                    )
                } else {
                    editorState.dumpLinesAndGetRet(File(snapFileFullPath).outputStream())
                }
                if(snapRet.hasError()) {
                    MyLog.e(TAG, "#$funName: save snapFile '$snapshotFileName' failed: ${snapRet.msg}")
                    return Ret.createError(null, snapRet.msg)
                }else {
                    return Ret.createSuccess(Pair(snapshotFileName, snapFileFullPath))
                }
            }else {  
                val msg = "file content is empty, will not create snapshot for it($srcFileName)"
                MyLog.d(TAG, "#$funName: $msg")
                return Ret.createSuccess(null, msg, Ret.SuccessCode.fileContentIsEmptyNeedNotCreateSnapshot)
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName() err: srcFileName=$srcFileName, err=${e.stackTraceToString()}")
            return Ret.createError(null, "save file snapshot failed: ${e.localizedMessage}", Ret.ErrCode.saveFileErr)
        }
    }
    override fun createSnapshotByFileAndGetResult(srcFile:FuckSafFile, flag:SnapshotFileFlag):Ret<Pair<String,String>?>{
        if((!enableFileSnapshotForEditor && !enableFileSnapshotForDiff)
            || (!enableFileSnapshotForEditor && enableFileSnapshotForDiff && !flag.isDiffFileSnapShot())
            || (enableFileSnapshotForEditor && !enableFileSnapshotForDiff && !flag.isEditorFileSnapShot())
        ) {
            return Ret.createSuccess(Pair(fileSnapshotDisable_FileNamePlaceHolder, fileSnapshotDisable_FilePathPlaceHolder))  
        }
        val funName = "createSnapshotByFileAndGetResult"
        var srcFileNameForLog = ""
        var snapshotFileNameForLog = ""
        try {
            if(!srcFile.exists()) {
                throw RuntimeException("`srcFile` doesn't exist!, path=${srcFile.canonicalPath}")
            }
            val srcFileName = srcFile.name
            srcFileNameForLog = srcFileName
            val (snapshotFileName, snapFileFullPath, snapFile) = getSnapshotFileNameAndFullPathAndFile(srcFileName, flag)
            snapshotFileNameForLog = snapshotFileName
            MyLog.d(TAG, "#$funName: will save snapFile to: '$snapFileFullPath'")
            srcFile.copyTo(snapFile.outputStream())
            if(!snapFile.exists()) {  
                MyLog.e(TAG, "#$funName: save snapFile '$snapshotFileName' failed!")
                throw RuntimeException("copy src to snapshot failed!")
            }else{  
                return Ret.createSuccess(Pair(snapshotFileName, snapFileFullPath))
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName() err: srcFileName=$srcFileNameForLog, snapshotFileName=$snapshotFileNameForLog, err=${e.stackTraceToString()}")
            return Ret.createError(null, "save file snapshot failed: ${e.localizedMessage}", Ret.ErrCode.saveFileErr)
        }
    }
    fun createSnapshotByContentWithRandomFileName(
        fileContent: String?,
        editorState: TextEditorState,
        trueUseContentFalseUseEditorState: Boolean,
        flag:SnapshotFileFlag
    ): Ret<Pair<String, String>?> {
        return createSnapshotByContentAndGetResult(
            srcFileName = getShortUUID(10),
            fileContent = fileContent,
            editorState = editorState,
            trueUseContentFalseUseEditorState = trueUseContentFalseUseEditorState,
            flag = flag
        )
    }
    private fun getSnapshotFileNameAndFullPathAndFile(
        srcFileName: String,
        flag: SnapshotFileFlag
    ): Triple<String, String, File> {
        val snapshotFileName = getANonexistsSnapshotFileName(srcFileName, flag.flag)
        val snapDir = AppModel.getOrCreateFileSnapshotDir()
        val snapFile = File(snapDir.canonicalPath, snapshotFileName)
        return Triple(snapshotFileName, snapFile.canonicalPath, snapFile)
    }
    private fun getANonexistsSnapshotFileName(srcFileName:String, flag: String):String {
        var count = 0
        val limit = 100
        val timestamp = getNowInSecFormatted(Cons.dateTimeFormatterCompact)
        while(true) {
            if(++count > limit) {  
                throw RuntimeException("err: generate snapshot filename failed")
            }
            val fileName = genSnapshotFileName(srcFileName, flag, timestamp, getShortUUID(6))
            if(!File(fileName).exists()) {
                return fileName
            }
        }
    }
    private fun genSnapshotFileName(srcFileName:String, flag: String, timestamp:String, uid:String):String {
        return "$srcFileName-$flag-$timestamp-$uid.bak"
    }
}
