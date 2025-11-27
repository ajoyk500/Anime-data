package com.akcreation.gitsilent.dto

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.git.BranchNameAndTypeDto
import com.akcreation.gitsilent.git.CommitDto
import com.akcreation.gitsilent.git.FileHistoryDto
import com.akcreation.gitsilent.git.SubmoduleDto
import com.akcreation.gitsilent.git.TagDto
import com.akcreation.gitsilent.server.bean.ApiBean
import com.akcreation.gitsilent.server.bean.ConfigBean
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.genHttpHostPortStr
import com.akcreation.gitsilent.utils.getFileNameFromCanonicalPath
import com.akcreation.gitsilent.utils.getParentPathEndsWithSeparator
import com.github.git24j.core.Commit
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository
import com.github.git24j.core.Submodule
import java.io.File

fun createSimpleCommitDto(
    commit: Commit,
    repoId: String,
    settings:AppSettings
):CommitDto = createCommitDto(
        commitOid = commit.id(),
        allBranchList = null,
        allTagList = null,
        commit = commit,
        repoId = repoId,
        repoIsShallow = false,
        shallowOidList = null,
        settings = settings
    )
fun createCommitDto(
    commitOid: Oid,
    allBranchList: List<BranchNameAndTypeDto>?,
    allTagList:List<TagDto>?,
    commit: Commit,
    repoId: String,
    repoIsShallow:Boolean,
    shallowOidList:List<String>?,
    settings:AppSettings,
    queryParents:Boolean = true,
): CommitDto {
    val c = CommitDto()
    c.oidStr = commitOid.toString()  
    c.shortOidStr = Libgit2Helper.getShortOidStrByFull(c.oidStr)
    val commitOidStr = commit.id().toString()
    if(allBranchList != null) {
        for (b in allBranchList) {
            if (b.oidStr == commitOidStr) {
                c.branchShortNameList.add(b.shortName)
            }
        }
    }
    if(allTagList != null) {
        for(t in allTagList) {
            if(t.targetFullOidStr == commitOidStr) {
                c.tagShortNameList.add(t.shortName)
            }
        }
    }
    if(queryParents) {
        val parentCount = commit.parentCount()
        if (parentCount > 0) {
            var pc = 0
            while (pc < parentCount) {
                val parentOidStr = commit.parentId(pc).toString()
                c.parentOidStrList.add(parentOidStr)
                c.parentShortOidStrList.add(Libgit2Helper.getShortOidStrByFull(parentOidStr))
                pc++
            }
        }
    }
    c.dateTime = Libgit2Helper.getDateTimeStrOfCommit(commit, settings)
    c.originTimeOffsetInMinutes = commit.timeOffset()
    c.originTimeInSecs = commit.time().epochSecond
    val commitSignature = commit.author()  
    c.author = commitSignature.name
    c.email = commitSignature.email
    val committer = commit.committer()  
    c.committerUsername = committer.name
    c.committerEmail = committer.email
    c.shortMsg = commit.summary()
    c.msg = commit.message()
    c.repoId = repoId
    c.treeOidStr = commit.treeId().toString()
    if(repoIsShallow && shallowOidList != null && shallowOidList.contains(c.oidStr)) {
        c.isGrafted=true  
    }
    return c
}
suspend fun updateRemoteDtoList(repo: Repository, remoteDtoList: List<RemoteDto>, onErr:(errRemote: RemoteDto, e:Exception)->Unit={r,e->}) {
    remoteDtoList.forEachBetter {
        try {
            updateRemoteDto(repo, it)
            val credDb = AppModel.dbContainer.credentialRepository
            val matchByDomainId = SpecialCredential.MatchByDomain.credentialId
            if(it.credentialId == matchByDomainId) {
                val actuallyCred = credDb.getByIdAndMatchByDomain(matchByDomainId, it.remoteUrl)
                if(actuallyCred!=null) {
                    it.actuallyCredentialIdWhenCredentialIdIsMatchByDomain = actuallyCred.id
                    it.credentialName = actuallyCred.name
                    it.credentialVal = actuallyCred.value
                    it.credentialPass = actuallyCred.pass
                    it.credentialType = actuallyCred.type
                }
            }
            if(it.pushCredentialId == matchByDomainId) {
                val actuallyCred = credDb.getByIdAndMatchByDomain(matchByDomainId, it.pushUrl)
                if(actuallyCred!=null) {
                    it.actuallyPushCredentialIdWhenCredentialIdIsMatchByDomain = actuallyCred.id
                    it.pushCredentialName = actuallyCred.name
                    it.pushCredentialVal = actuallyCred.value
                    it.pushCredentialPass = actuallyCred.pass
                    it.pushCredentialType = actuallyCred.type
                }
            }
        }catch (e:Exception) {
            onErr(it, e)
        }
    }
}
fun updateRemoteDto(repo: Repository, remoteDto: RemoteDto) {
    val remoteName = remoteDto.remoteName
    val remote = Libgit2Helper.resolveRemote(repo, remoteName) ?: return
    remoteDto.remoteUrl = remote.url()
    remoteDto.pushUrl = remote.pushurl()?:""
    if(remoteDto.pushUrl.isBlank()) {
        remoteDto.pushUrl = remoteDto.remoteUrl
        remoteDto.pushUrlTrackFetchUrl = true
    }
    val (isAll, branchNameList) = Libgit2Helper.getRemoteFetchBranchList(remote)
    if(isAll) {
        remoteDto.branchMode = Cons.dbRemote_Fetch_BranchMode_All
        remoteDto.branchListForFetch = emptyList()  
    }else {
        remoteDto.branchMode = Cons.dbRemote_Fetch_BranchMode_CustomBranches
        remoteDto.branchListForFetch = branchNameList  
    }
}
fun createSubmoduleDto(
    sm: Submodule,
    smName: String,
    parentWorkdirPathNoSlashSuffix: String,
    invalidUrlAlertText: String
): SubmoduleDto {
    val smRelativePath = sm.path()
    val smFullPath = parentWorkdirPathNoSlashSuffix + Cons.slash + smRelativePath.removePrefix(Cons.slash)
    val smUrl = sm.url() ?: ""
    val smDto = SubmoduleDto(
        name = smName,
        relativePathUnderParent = smRelativePath,
        fullPath = smFullPath,
        cloned = Libgit2Helper.isValidGitRepo(smFullPath),
        remoteUrl = smUrl,
        targetHash = Libgit2Helper.getParentRecordedTargetHashForSubmodule(sm),
        tempStatus = if (smUrl.isBlank()) invalidUrlAlertText else "",
        location = Libgit2Helper.getSubmoduleLocation(sm)
    )
    return smDto
}
fun createFileHistoryDto(
    repoWorkDirPath:String,
    commitOidStr: String,
    treeEntryOidStr:String,
    commit: Commit,
    repoId: String,
    fileRelativePathUnderRepo:String,
    settings: AppSettings,
    commitList: List<String>,
): FileHistoryDto {
    val obj = FileHistoryDto()
    obj.commitList = commitList.toList()
    obj.repoWorkDirPath = repoWorkDirPath
    obj.fileParentPathOfRelativePath = getParentPathEndsWithSeparator(fileRelativePathUnderRepo)
    obj.fileName = getFileNameFromCanonicalPath(fileRelativePathUnderRepo)
    obj.fileFullPath = File(repoWorkDirPath, fileRelativePathUnderRepo).canonicalPath
    obj.filePathUnderRepo = fileRelativePathUnderRepo
    obj.treeEntryOidStr = treeEntryOidStr
    obj.commitOidStr = commitOidStr
    obj.dateTime = Libgit2Helper.getDateTimeStrOfCommit(commit, settings)
    obj.originTimeOffsetInMinutes = commit.timeOffset()
    val commitSignature = commit.author()  
    obj.authorUsername = commitSignature.name
    obj.authorEmail = commitSignature.email
    val committer = commit.committer()  
    obj.committerUsername = committer.name
    obj.committerEmail = committer.email
    obj.shortMsg = commit.summary()
    obj.msg = commit.message()
    obj.repoId = repoId
    return obj
}
fun genConfigDto(
    repoEntity: RepoEntity,
    settings: AppSettings
): ConfigBean {
    val host = settings.httpService.listenHost
    val port = settings.httpService.listenPort
    val token = settings.httpService.tokenList.let { if(it.isEmpty()) "" else it.first() }
    return ConfigBean(
        repoName = repoEntity.repoName,
        repoId = repoEntity.id,
        api = ApiBean(
            protocol = "http",
            host = host,
            port = port,
            token = token,
            pull = "/pull",
            push = "/push",
            sync = "/sync",
            pull_example = "${genHttpHostPortStr(host, port.toString())}/pull?token=$token&repoNameOrId=${repoEntity.repoName}",
            push_example = "${genHttpHostPortStr(host, port.toString())}/push?token=$token&repoNameOrId=${repoEntity.repoName}",
            sync_example = "${genHttpHostPortStr(host, port.toString())}/sync?token=$token&repoNameOrId=${repoEntity.repoName}",
        )
    )
}
fun rawAppInfoToAppInfo(rawAppInfo: ApplicationInfo, packageManager: PackageManager, selected:(AppInfo)->Boolean) :AppInfo? {
    val appName = rawAppInfo.loadLabel(packageManager).toString()
    val appIcon = rawAppInfo.loadIcon(packageManager) ?: return null
    val isSystemApp = (rawAppInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0
    val tmp = AppInfo(
        appName = appName,
        packageName = rawAppInfo.packageName,

        appIcon = appIcon,
        isSystemApp = isSystemApp,
        isSelected = false
    )
    tmp.isSelected = selected(tmp)
    return tmp
}
fun getCredentialName(
    isFetch:Boolean,
    fetchCredentialId:String?,
    fetchCredentialName:String?,
    pushCredentialId:String?,
    pushCredentialName:String?
):String {
    val id = if(isFetch) fetchCredentialId else pushCredentialId
    val name = if(isFetch) fetchCredentialName else pushCredentialName
    val scmbd = SpecialCredential.MatchByDomain
    val scnone = SpecialCredential.NONE
    return if(id == scmbd.credentialId) scmbd.name else (name ?: scnone.name);
}
