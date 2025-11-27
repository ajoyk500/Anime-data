package com.akcreation.gitsilent.git

import android.content.Context
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.formatMinutesToUtc
import com.akcreation.gitsilent.utils.readTimeZoneOffsetInMinutesFromSettingsOrDefault

data class CommitDto (
    var oidStr: String="",
    var shortOidStr: String="",
    var branchShortNameList: MutableList<String> = mutableListOf(),  
    var parentOidStrList: MutableList<String> = mutableListOf(),  
    var parentShortOidStrList: MutableList<String> = mutableListOf(),  
    var dateTime: String="",
    var originTimeOffsetInMinutes:Int=0,
    var author: String="",  
    var email: String="",
    var committerUsername:String="",
    var committerEmail:String="",
    var shortMsg:String="", 
    var msg: String="",  
    var repoId:String="",  
    var treeOidStr:String="",  
    var isGrafted:Boolean=false,  
    var tagShortNameList:MutableList<String> = mutableListOf(),
    var draw_inputs: List<DrawCommitNode> = listOf(),
    var draw_outputs: List<DrawCommitNode> = listOf(),
    var originTimeInSecs: Long = 0L,
) {
    private var otherMsg:String?=null
    private var otherMsgSearchableText:String?=null
    private var cached_OneLineMsg:String? = null
    private var cached_BranchShortNameList:String? = null
    private var cached_TagShortNameList:String? = null
    private var cached_ParentShortOidStrList:String? = null
    private var cached_LineSeparated_BranchShortNameList:String? = null
    private var cached_LineSeparated_TagShortNameList:String? = null
    private var cached_LineSeparated_ParentFullOidStrList:String? = null
    fun hasOther():Boolean {
        return isGrafted || isMerged()
    }
    fun getOther(activityContext: Context, searchable:Boolean):String {
        val noCache = if(searchable) {
            otherMsgSearchableText == null
        }else {
            otherMsg == null
        }
        if(noCache) {
            val sb = StringBuilder()
            val suffix = ", "
            sb.append(if(isMerged()) {
                if(searchable) {
                    CommitDtoSearchableText.isMerged
                }else {
                    activityContext.getString(R.string.is_merged)
                }
            } else {
                if(searchable) {
                    CommitDtoSearchableText.notMerged
                }else {
                    activityContext.getString(R.string.not_merged)
                }
            }).append(suffix)
            sb.append(if(isGrafted) {
                if(searchable) {
                    CommitDtoSearchableText.isGrafted
                }else {
                    activityContext.getString(R.string.is_grafted)
                }
            } else {
                if(searchable) {
                    CommitDtoSearchableText.notGrafted
                }else {
                    activityContext.getString(R.string.not_grafted)
                }
            }).append(suffix)
            val text = sb.toString().removeSuffix(suffix)
            if(searchable) {
                otherMsgSearchableText = text
            }else {
                otherMsg = text
            }
        }
        return (if(searchable) otherMsgSearchableText else otherMsg) ?: ""
    }
    fun isMerged():Boolean {
        return parentOidStrList.size>1
    }
    fun authorAndCommitterAreSame():Boolean {
        return author==committerUsername && email==committerEmail
    }
    fun getActuallyUsingTimeZoneUtcFormat(settings: AppSettings): String {
        val minuteOffset = readTimeZoneOffsetInMinutesFromSettingsOrDefault(settings, originTimeOffsetInMinutes)
        return formatMinutesToUtc(minuteOffset)
    }
    fun getCachedOneLineMsg(): String = (cached_OneLineMsg ?: Libgit2Helper.zipOneLineMsg(msg).let { cached_OneLineMsg = it; it });
    fun cachedBranchShortNameList():String = cached_BranchShortNameList ?: branchShortNameList.joinToString { it }.let { cached_BranchShortNameList=it; it };
    fun cachedTagShortNameList():String = cached_TagShortNameList ?: tagShortNameList.joinToString { it }.let { cached_TagShortNameList=it; it };
    fun cachedParentShortOidStrList():String = cached_ParentShortOidStrList ?: parentShortOidStrList.joinToString { it }.let { cached_ParentShortOidStrList=it; it };
    fun cachedLineSeparatedBranchList():String = cached_LineSeparated_BranchShortNameList ?: branchShortNameList.joinToString(separator = "\n", prefix = "\n") { it }.let { cached_LineSeparated_BranchShortNameList=it; it };
    fun cachedLineSeparatedTagList():String = cached_LineSeparated_TagShortNameList ?: tagShortNameList.joinToString(separator = "\n", prefix = "\n") { it }.let { cached_LineSeparated_TagShortNameList=it; it };
    fun cachedLineSeparatedParentFullOidList():String = cached_LineSeparated_ParentFullOidStrList ?: parentOidStrList.joinToString(separator = "\n", prefix = "\n") { it }.let { cached_LineSeparated_ParentFullOidStrList=it; it };
    fun getFormattedAuthorInfo() = Libgit2Helper.getFormattedUsernameAndEmail(author, email)
    fun getFormattedCommitterInfo() = Libgit2Helper.getFormattedUsernameAndEmail(committerUsername, committerEmail)
}
private object CommitDtoSearchableText {
    const val isMerged = "IsMerged"
    const val notMerged = "NotMerged"
    const val isGrafted = "IsGrafted"
    const val notGrafted = "NotGrafted"
}
