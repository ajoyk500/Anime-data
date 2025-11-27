package com.akcreation.gitsilent.git

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.dto.ItemKey
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.github.git24j.core.Repository
import kotlinx.coroutines.channels.Channel
import java.io.File

data class DiffableItem(
    val repoIdFromDb:String="",
    val relativePath:String = "",
    val repoWorkDirPath:String="",
    val fileName:String="",
    val fullPath:String="",
    val fileParentPathOfRelativePath:String="",
    val itemType:Int = Cons.gitItemTypeFile,
    val changeType:String = "",
    val isChangeListItem:Boolean = false,
    val isFileHistoryItem:Boolean = false,
    val entryId:String = "",
    val commitId:String = "",
    val sizeInBytes:Long = 0L,
    val shortCommitId:String = "",
    val loading:Boolean = true,
    val loadChannel:Channel<Int> = Channel(),
    val diffItemSaver: DiffItemSaver = DiffItemSaver(),
    val stringPairMap: MutableMap<String, CompareLinePairResult> = mutableStateMapOf(),
    val compareLinePair:CompareLinePair = CompareLinePair(),
    val submoduleIsDirty:Boolean = false,
    val errMsg: String = "",
    val visible:Boolean = false,
    var noDiffItemAvailable:Boolean = false,
    var whyNoDiffItem:(@Composable ()->Unit)? = null,
    var whyNoDiffItem_msg:String = "",
):ItemKey {
    companion object {
        fun anInvalidInstance(): DiffableItem {
            return DiffableItem(repoIdFromDb = "an_invalid_DiffableItem_30bc0f41-63e8-461a-b48b-415d5584740d")
        }
    }
    override fun getItemKey(): String {
        return if(isChangeListItem) StatusTypeEntrySaver.generateItemKey(repoIdFromDb, relativePath, changeType, itemType) else FileHistoryDto.generateItemKey(commitId)
    }
    fun getFileNameEllipsis(fileNameLimit:Int):String {
        return relativePath.let {
            if(it.length > fileNameLimit) {
                "…${it.reversed().substring(0, fileNameLimit).reversed()}"
            }else {
                it
            }
        }
    }
    fun copyForLoading():DiffableItem {
        return copy(loading = true, visible = true, stringPairMap = mutableStateMapOf(), compareLinePair = CompareLinePair(), submoduleIsDirty = false, errMsg = "", loadChannel = Channel())
    }
    fun closeLoadChannel() {
        runCatching { loadChannel.close() }
    }
    protected fun finalize() {
        closeLoadChannel()
    }
    fun toChangeListItem(): StatusTypeEntrySaver {
        val stes = StatusTypeEntrySaver()
        stes.repoIdFromDb = repoIdFromDb
        stes.fileName = fileName
        stes.relativePathUnderRepo = relativePath
        stes.changeType = changeType
        stes.canonicalPath = fullPath
        stes.fileParentPathOfRelativePath = fileParentPathOfRelativePath
        stes.fileSizeInBytes = sizeInBytes
        stes.itemType = itemType
        stes.dirty = submoduleIsDirty
        stes.repoWorkDirPath = repoWorkDirPath
        return stes
    }
    fun toFileHistoryItem():FileHistoryDto {
        val fhi = FileHistoryDto()
        fhi.fileName = fileName
        fhi.filePathUnderRepo = relativePath
        fhi.fileFullPath = fullPath
        fhi.fileParentPathOfRelativePath = fileParentPathOfRelativePath
        fhi.commitOidStr = commitId
        fhi.repoId = repoIdFromDb
        fhi.repoWorkDirPath = repoWorkDirPath
        return fhi
    }
    fun toFile():File = File(fullPath)
    fun neverLoadedDifferences() : Boolean = diffItemSaver.relativePathUnderRepo.isEmpty()
    fun maybeLoadedAtLeastOnce() = !neverLoadedDifferences()
    fun atRootOfWorkDir() = fileParentPathOfRelativePath == "/";
    fun getAnnotatedAddDeletedAndParentPathString(colorOfChangeType: Color): AnnotatedString {
        return buildAnnotatedString {
            if(maybeLoadedAtLeastOnce()) {
                if(diffItemSaver.addedLines > 0) {
                    withStyle(style = SpanStyle(color = Theme.mdGreen)) { append("+"+diffItemSaver.addedLines) }
                    withStyle(style = SpanStyle(color = Theme.Gray1)) { append(", ") }
                }
                if(diffItemSaver.deletedLines > 0) {
                    withStyle(style = SpanStyle(color = Theme.mdRed)) { append("-"+diffItemSaver.deletedLines) }
                    withStyle(style = SpanStyle(color = Theme.Gray1)) { append(", ") }
                }
            }
            withStyle(style = SpanStyle(color = colorOfChangeType)) { append(fileParentPathOfRelativePath) }
        }
    }
    private var cachedOneLineCommitMsg:String? = null
    fun oneLineCommitMsgOfCommitOid():String {
        return (cachedOneLineCommitMsg ?: (try {
            Repository.open(repoWorkDirPath).use { repo ->
                Libgit2Helper.getCommitMsgOneLine(repo, commitId)
            }
        }catch (e: Exception) {
            e.printStackTrace()
            ""
        }).let { cachedOneLineCommitMsg = it; it })
    }
}
