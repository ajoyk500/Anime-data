package com.akcreation.gitsilent.git

import android.content.Context
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.generateRandomString
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.replaceStringResList
import com.github.git24j.core.Branch.BranchType

class BranchNameAndTypeDto {
    var isSymbolic:Boolean=false  
    var symbolicTargetFullName:String=""  
    var symbolicTargetShortName:String=""  
    var ahead:Int=0
    var behind:Int=0
    var shortName:String=""  
    var fullName:String=""  
    var oidStr:String=""  
    var shortOidStr:String=""  
    var type:BranchType=BranchType.INVALID
    var upstream:Upstream?=null  
    var isCurrent:Boolean=false  
    var remotePrefixFromShortName:String=""  
    private var otherCached:String? = null
    private var otherSearchableTextCached:String? = null
    fun isRemoteNameAmbiguous():Boolean {  
        return remotePrefixFromShortName.isBlank()
    }
    fun getAheadBehind(activityContext: Context, searchable: Boolean):String {
        return if(alreadyUpToDate()) {
            if(searchable) {
                BranchSearchableText.upToDate
            }else {
                activityContext.getString(R.string.uptodate)
            }
        }else {
            if(searchable){
                replaceStringResList(BranchSearchableText.ahead_n_behind_m, listOf(""+ahead, ""+behind))
            }else{
                replaceStringResList(activityContext.getString(R.string.ahead_n_behind_m), listOf(""+ahead, ""+behind))
            }
        }
    }
    fun alreadyUpToDate() = ahead == 0 && behind == 0;
    fun getBranchNameNoRemotePrefixOrEmptyStrIfAmbiguous():String {
        if(isRemoteNameAmbiguous()) {
            return ""
        }
        return Libgit2Helper.removeRemoteBranchShortRefSpecPrefixByRemoteName(remotePrefixFromShortName+"/", shortName)?:""
    }
    fun isUpstreamValid():Boolean {
        return isUpstreamAlreadySet() && isPublished()
    }
    fun isUpstreamAlreadySet():Boolean {
        return !(upstream == null || upstream?.remote.isNullOrBlank() || upstream?.branchRefsHeadsFullRefSpec.isNullOrBlank())
    }
    fun isPublished():Boolean {
        return upstream?.isPublished == true
    }
    fun getOther(activityContext:Context, searchable:Boolean):String {
        val noCache = if(searchable) {
            otherSearchableTextCached == null
        }else {
            otherCached == null
        }
        if(noCache) {
            val suffix = ", "
            val sb= StringBuilder()
            if(type == BranchType.LOCAL) {
                sb.append(if(isCurrent) {
                    if(searchable) {
                        BranchSearchableText.isCurrent
                    }else {
                        activityContext.getString(R.string.is_current)
                    }
                } else {
                    if(searchable) {
                        BranchSearchableText.notCurrent
                    }else {
                        activityContext.getString(R.string.not_current)
                    }
                }).append(suffix)
                sb.append(if(isUpstreamAlreadySet()) {
                    if(searchable) {
                        BranchSearchableText.hasUpstream
                    }else {
                        activityContext.getString(R.string.has_upstream)
                    }
                } else {
                    if(searchable) {
                        BranchSearchableText.noUpstream
                    }else {
                        activityContext.getString(R.string.no_upstream)
                    }
                }).append(suffix)
                sb.append(if(isPublished()){
                    if(searchable) {
                        BranchSearchableText.isPublished
                    }else {
                        activityContext.getString(R.string.is_published)
                    }
                } else {
                    if(searchable) {
                        BranchSearchableText.notPublished
                    }else {
                        activityContext.getString(R.string.not_published)
                    }
                }).append(suffix)
            }
            if(type == BranchType.REMOTE) {
            }
            sb.append(if(isSymbolic){
                if(searchable) {
                    BranchSearchableText.isSymbolic
                }else {
                    activityContext.getString(R.string.is_symbolic)
                }
            } else {
                if(searchable) {
                    BranchSearchableText.notSymbolic
                }else {
                    activityContext.getString(R.string.not_symbolic)
                }
            }).append(suffix)
            val text = sb.removeSuffix(suffix).toString()
            if(searchable) {
                otherSearchableTextCached = text
            }else {
                otherCached = text
            }
        }
        return (if(searchable) otherSearchableTextCached else otherCached) ?: ""
    }
    fun getTypeString(activityContext: Context, searchable: Boolean):String {
        return if(type == BranchType.LOCAL) {
            if(searchable) {
                BranchSearchableText.local
            }else {
                activityContext.getString(R.string.local)
            }
        } else {
            if(searchable) {
                BranchSearchableText.remote
            }else {
                activityContext.getString(R.string.remote)
            }
        }
    }
    fun getUpstreamShortName(activityContext: Context):String {
        if(type != BranchType.LOCAL) {
            return ""
        }
        val shortUpstreamBranchName = upstream?.remoteBranchShortRefSpec ?:""
        return shortUpstreamBranchName.ifBlank { "[" + activityContext.getString(R.string.none) + "]" }
    }
    fun getUpstreamFullName(activityContext: Context): String {
        if(type != BranchType.LOCAL) {
            return ""
        }
        val upstreamBranchName = upstream?.remoteBranchRefsRemotesFullRefSpec ?:""
        return upstreamBranchName.ifBlank { "[" + activityContext.getString(R.string.none) + "]" }
    }
}
private object BranchSearchableText {
    const val isCurrent = "IsCurrent"
    const val notCurrent = "NotCurrent"
    const val hasUpstream = "HasUpstream"
    const val noUpstream = "NoUpstream"
    const val isPublished = "IsPublished"
    const val notPublished = "NotPublished"
    const val isSymbolic = "IsSymbolic"
    const val notSymbolic = "NotSymbolic"
    const val local = "Local"
    const val remote = "Remote"
    const val upToDate = "UpToDate"
    const val ahead_n_behind_m = "ahead ph_a3f241dc_1, behind ph_a3f241dc_2"
}
