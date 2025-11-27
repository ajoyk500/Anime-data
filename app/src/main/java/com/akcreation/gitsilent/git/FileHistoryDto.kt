package com.akcreation.gitsilent.git

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.dto.ItemKey
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.formatMinutesToUtc
import com.akcreation.gitsilent.utils.readTimeZoneOffsetInMinutesFromSettingsOrDefault

class FileHistoryDto(
    var fileName:String="",
    var filePathUnderRepo:String="",
    var fileFullPath:String="",
    var fileParentPathOfRelativePath:String = "",  
    var treeEntryOidStr:String="",
    var commitOidStr: String="",  
    var dateTime: String="",
    var originTimeOffsetInMinutes:Int = 0,  
    var authorUsername: String="",  
    var authorEmail: String="",
    var committerUsername:String="",
    var committerEmail:String="",
    var shortMsg:String="", 
    var msg: String="",  
    var repoId:String="",  
    var repoWorkDirPath:String="",
    var commitList:List<String> = listOf(),
): ItemKey {
    private var commitShortOidStr:String?=null
    private var treeEntryShortOidStr:String?=null
    private var cached_OneLineMsg:String? = null
    private var cached_ShortCommitListStr:String? = null
    fun authorAndCommitterAreSame():Boolean {
        return authorUsername==committerUsername && authorEmail==committerEmail
    }
    fun getCachedCommitShortOidStr():String {
        if(commitShortOidStr==null) {
            commitShortOidStr = Libgit2Helper.getShortOidStrByFull(commitOidStr)
        }
        return commitShortOidStr ?:""
    }
    fun getCachedTreeEntryShortOidStr():String {
        if(treeEntryShortOidStr==null) {
            treeEntryShortOidStr = Libgit2Helper.getShortOidStrByFull(treeEntryOidStr)
        }
        return treeEntryShortOidStr ?:""
    }
    fun getActuallyUsingTimeZoneUtcFormat(settings: AppSettings): String {
        val minuteOffset = readTimeZoneOffsetInMinutesFromSettingsOrDefault(settings, originTimeOffsetInMinutes)
        return formatMinutesToUtc(minuteOffset)
    }
    override fun getItemKey():String {
        return generateItemKey(commitOidStr)
    }
    fun toDiffableItem():DiffableItem {
        return DiffableItem(
            repoIdFromDb = repoId,
            relativePath = filePathUnderRepo,
            itemType = Cons.gitItemTypeFile,
            changeType = Cons.gitStatusModified,
            isChangeListItem = false,
            isFileHistoryItem = true,
            entryId = treeEntryOidStr,
            commitId = commitOidStr,
            sizeInBytes = 0L,
            shortCommitId = getCachedCommitShortOidStr(),
            repoWorkDirPath = repoWorkDirPath,
            fileName = fileName,
            fullPath = fileFullPath,
            fileParentPathOfRelativePath = fileParentPathOfRelativePath,
        )
    }
    fun getCachedOneLineMsg(): String = (cached_OneLineMsg ?: Libgit2Helper.zipOneLineMsg(msg).let { cached_OneLineMsg = it; it });
    fun cachedShortCommitListStr(): String = cached_ShortCommitListStr ?: commitList.joinToString { Libgit2Helper.getShortOidStrByFull(it) }.let { cached_ShortCommitListStr=it; it };
    companion object {
        fun generateItemKey(commitOidStr:String ):String {
            return commitOidStr
        }
    }
}
