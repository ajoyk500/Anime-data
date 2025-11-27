package com.akcreation.gitsilent.utils

import android.content.Context
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.data.repository.CredentialRepository
import com.akcreation.gitsilent.data.repository.RemoteRepository
import com.akcreation.gitsilent.data.repository.RepoRepository
import com.akcreation.gitsilent.dev.DevFeature
import com.akcreation.gitsilent.dto.Box
import com.akcreation.gitsilent.dto.RemoteDto
import com.akcreation.gitsilent.dto.createCommitDto
import com.akcreation.gitsilent.dto.createFileHistoryDto
import com.akcreation.gitsilent.dto.createSimpleCommitDto
import com.akcreation.gitsilent.dto.createSubmoduleDto
import com.akcreation.gitsilent.etc.RepoPendingTask
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.BranchNameAndTypeDto
import com.akcreation.gitsilent.git.CommitDto
import com.akcreation.gitsilent.git.DiffItemSaver
import com.akcreation.gitsilent.git.DrawCommitNode
import com.akcreation.gitsilent.git.FileHistoryDto
import com.akcreation.gitsilent.git.IgnoreItem
import com.akcreation.gitsilent.git.PatchFile
import com.akcreation.gitsilent.git.PuppyHunkAndLines
import com.akcreation.gitsilent.git.PuppyLine
import com.akcreation.gitsilent.git.PushFailedItem
import com.akcreation.gitsilent.git.ReflogEntryDto
import com.akcreation.gitsilent.git.RemoteAndCredentials
import com.akcreation.gitsilent.git.SquashData
import com.akcreation.gitsilent.git.StashDto
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.git.SubmoduleDto
import com.akcreation.gitsilent.git.TagDto
import com.akcreation.gitsilent.git.Upstream
import com.akcreation.gitsilent.jni.LibgitTwo
import com.akcreation.gitsilent.jni.SaveBlobRet
import com.akcreation.gitsilent.jni.SaveBlobRetCode
import com.akcreation.gitsilent.jni.SshAskUserUnknownHostRequest
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.KnownHostRequestStateMan
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.template.CommitMsgTemplateUtil
import com.akcreation.gitsilent.utils.cache.CommitCache
import com.akcreation.gitsilent.utils.state.CustomBoxSaveable
import com.github.git24j.core.AnnotatedCommit
import com.github.git24j.core.Apply
import com.github.git24j.core.Blob
import com.github.git24j.core.Branch
import com.github.git24j.core.Checkout
import com.github.git24j.core.Cherrypick
import com.github.git24j.core.Clone
import com.github.git24j.core.Commit
import com.github.git24j.core.Config
import com.github.git24j.core.Credential
import com.github.git24j.core.Diff
import com.github.git24j.core.Diff.Line
import com.github.git24j.core.FetchOptions
import com.github.git24j.core.GitObject
import com.github.git24j.core.Graph
import com.github.git24j.core.Index
import com.github.git24j.core.Merge
import com.github.git24j.core.Oid
import com.github.git24j.core.Patch
import com.github.git24j.core.PushOptions
import com.github.git24j.core.Rebase
import com.github.git24j.core.Reference
import com.github.git24j.core.Reflog
import com.github.git24j.core.Remote
import com.github.git24j.core.Repository
import com.github.git24j.core.Repository.MergeheadForeachCb
import com.github.git24j.core.Reset
import com.github.git24j.core.Revparse
import com.github.git24j.core.Revwalk
import com.github.git24j.core.Signature
import com.github.git24j.core.SortT
import com.github.git24j.core.Stash
import com.github.git24j.core.Status
import com.github.git24j.core.Status.StatusList
import com.github.git24j.core.Submodule
import com.github.git24j.core.Tag
import com.github.git24j.core.Tree
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.nio.charset.Charset
import java.time.ZoneOffset
import java.util.EnumSet

private const val TAG = "Libgit2Helper"
object Libgit2Helper {
    object CommitUtil{
        fun isSameCommitHash(h1:String, h2:String):Boolean {
            return h1 == h2
        }
        fun isLocalCommitHash(c:String):Boolean {
            return c == Cons.git_LocalWorktreeCommitHash
        }
        fun mayGoodCommitHash(c:String):Boolean {
            return c.isNotBlank() && c != Cons.git_AllZeroOidStr && Cons.gitSha1HashMinLen1Regex.matches(c)
        }
    }
    object ShallowManage {
        const val originShallow = "shallow"  
        const val bak1 = "shallow.1.bak"  
        const val bak2 = "shallow.2.bak"  
        fun createShallowBak(repo:Repository) {
            val repoDotGitDir = Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(repo)
            val originShallowFile = File(repoDotGitDir, originShallow)  
            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(repoDotGitDir).canonicalPath
            val bak1File = File(puppyGitDirUnderGitCanonicalPath, bak1)  
            val bak2File = File(puppyGitDirUnderGitCanonicalPath, bak2)  
            originShallowFile.copyTo(bak1File, overwrite = true)
            originShallowFile.copyTo(bak2File, overwrite = true)
        }
        fun restoreShallowFile(repo:Repository) {
            val repoDotGitDir = Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(repo)
            val originShallowFile = File(repoDotGitDir, originShallow)
            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(repoDotGitDir).canonicalPath
            val bak1File = File(puppyGitDirUnderGitCanonicalPath, bak1)
            if(bak1File.exists()) {
                bak1File.copyTo(originShallowFile, overwrite = true)
            }else {  
                val bak2File = File(puppyGitDirUnderGitCanonicalPath, bak2)
                bak2File.copyTo(bak1File, overwrite = true)
                bak2File.copyTo(originShallowFile, overwrite = true)
            }
        }
        fun deleteBak1(repo:Repository) {
            val repoDotGitDir = Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(repo)
            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(repoDotGitDir).canonicalPath
            val bak1File = File(puppyGitDirUnderGitCanonicalPath, bak1)
            if(bak1File.exists()) {
                bak1File.delete()
            }
        }
        fun getShallowOidList(repo: Repository):List<String> {
            val repoDotGitDir = Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(repo)
            val originShallowFile = File(repoDotGitDir, originShallow)  
            if(!originShallowFile.exists()) {
                return emptyList()
            }
            return originShallowFile.readLines().filter { it.isNotBlank() }
        }
        fun getShallowFile(repo:Repository):File {
            val repoDotGitDir = Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(repo)
            return File(repoDotGitDir, originShallow)
        }
    }
    object RebaseHelper {
        private const val rebaseInitedButNeverNextYetValue = -1
        private const val detachedHead = "detached HEAD"
        private val fileName_beforeBranch = "rebase_before_branch"
        fun saveRepoCurBranchNameOrDetached(repo:Repository) {
            val name = if(repo.headDetached()) detachedHead else repo.head()?.name()
            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(getRepoGitDirPathNoEndsWithSlash(repo)).canonicalPath
            val file = File(puppyGitDirUnderGitCanonicalPath, fileName_beforeBranch)
            if(!file.exists()) {
                file.createNewFile()
            }
            file.bufferedWriter().use {
                it.write(name)
            }
        }
        fun getRebaseBeforeName(repo:Repository):String? {
            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(getRepoGitDirPathNoEndsWithSlash(repo)).canonicalPath
            val file = File(puppyGitDirUnderGitCanonicalPath, fileName_beforeBranch)
            if(!file.exists()) {
                return null
            }
            file.bufferedReader().use {
                val ret = try {
                    it.readLine()  
                }catch (_:Exception){
                    null
                }
                return ret
            }
        }
    }
    object SubmoduleDotGitFileMan {
        private fun genSubmoduleDotGitFilePath(parentFullPath:String, submodulePathUnderParent:String):String {
            return File(parentFullPath, File(submodulePathUnderParent, ".git").canonicalPath).canonicalPath
        }
        fun backupDotGitFileForSubmodule(parentFullPath:String, submodulePathUnderParent:String) {
            val submoduleDotGitFullPath = genSubmoduleDotGitFilePath(parentFullPath, submodulePathUnderParent)
            try {
                if(!File(submoduleDotGitFullPath).exists()) {
                    return
                }
                val backup = File(AppModel.getOrCreateSubmoduleDotGitBackupDir(), submoduleDotGitFullPath)
                backup.parentFile?.mkdirs()  
                File(submoduleDotGitFullPath).copyTo(backup, overwrite = true)
            }catch (e:Exception) {
                MyLog.e(TAG, "#backupDotGitFileForSubmodule: backup git file failed, path=$submoduleDotGitFullPath")
            }
        }
        fun restoreDotGitFileForSubmodule(parentFullPath:String, submodulePathUnderParent:String) {
            val submoduleDotGitFullPath = genSubmoduleDotGitFilePath(parentFullPath, submodulePathUnderParent)
            try {
                if(File(submoduleDotGitFullPath).exists()) {
                    return
                }
                val backup = File(AppModel.getOrCreateSubmoduleDotGitBackupDir(), submoduleDotGitFullPath)
                if(backup.exists()) {
                    backup.copyTo(File(submoduleDotGitFullPath), overwrite = true)
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#restoreDotGitFileForSubmodule: restore git file failed, path=$submoduleDotGitFullPath")
            }
        }
    }
    private val repoLockMap:MutableMap<String, Mutex> = ConcurrentMap()
    private fun getCredentialCb(credentialType:Int, credentialEntity:CredentialEntity): (url: String?, usernameFromUrl: String?, allowedTypes: Int?) -> Credential {
        val usernameOrPrivateKey = credentialEntity.value
        val passOrPassphrase = credentialEntity.pass
        return { url: String?, usernameFromUrl: String?, allowedTypes: Int? ->
            if (credentialType == Cons.dbCredentialTypeHttp) {  
                Credential.userpassPlaintextNew(
                    usernameOrPrivateKey,
                    passOrPassphrase
                )
            } else {  
                var usernameForSsh  = Cons.gitWellKnownSshUserName
                if(usernameFromUrl != null && usernameFromUrl.isNotBlank()) {
                    usernameForSsh = usernameFromUrl
                }
                val passphraseOrNull = passOrPassphrase.ifEmpty { null }
                Credential.sshKeyMemoryNew(
                    usernameForSsh, null,
                    usernameOrPrivateKey, passphraseOrNull
                )
            }
        }
    }
    private fun getIgnoreSubmoduleApplyDeltaCallback(submodulePathList:List<String>):(delta:Diff.Delta)->Int {
        val skip = 1;  
        val ok=0  
        val abort = -1  
        val cb:(delta:Diff.Delta)->Int  = cb@{ delta ->
            var path = delta.oldFile.path
            if(path==null || path.isBlank()) {
                path = delta.newFile.path
                if(path==null || path.isBlank()) {
                    return@cb ok
                }
            }
            if(submodulePathList.contains(path)) {
                MyLog.d(TAG, "#getIgnoreSubmoduleApplyDeltaCallback: SKIP: target is a submodule '$path', apply it will abort the procedure")
                return@cb skip
            }
            return@cb ok
        }
        return cb
    }
    private fun getDefaultStatusOptTypeSet():EnumSet<Status.OptT> {
        return EnumSet.of(
            Status.OptT.OPT_INCLUDE_UNTRACKED,
            Status.OptT.OPT_RECURSE_UNTRACKED_DIRS,  
            Status.OptT.OPT_SORT_CASE_INSENSITIVELY,
        )
    }
    private fun getDefaultDiffOptionsFlags():EnumSet<Diff.Options.FlagT> {
        return EnumSet.of(
            Diff.Options.FlagT.INCLUDE_UNTRACKED,
            Diff.Options.FlagT.SHOW_UNTRACKED_CONTENT,
            Diff.Options.FlagT.RECURSE_UNTRACKED_DIRS,  
        )
    }
    private fun getDefaultRevwalkSortMode():EnumSet<SortT>{
        return EnumSet.of(
            SortT.TOPOLOGICAL, 
            SortT.TIME,  
        )
    }
    private val defaultBranchTypeForList = Branch.BranchType.ALL
    private fun getRepoStatusList(
        repo:Repository,
        showType:Status.ShowT = Status.ShowT.INDEX_AND_WORKDIR,
        flags:EnumSet<Status.OptT> = getDefaultStatusOptTypeSet()
    ) :StatusList {
        val statusOpts: Status.Options = Status.Options.newDefault()
        statusOpts.show = showType.bit  
        statusOpts.flags = flags;  
        if (repo.isBare) {
            throw RuntimeException("Cannot report status on bare repository: " + getRepoWorkdirNoEndsWithSlash(repo))
        }
        return StatusList.listNew(repo, statusOpts)
    }
    fun getIndexStatusList(
        repo:Repository,
        flags:EnumSet<Status.OptT> = getDefaultStatusOptTypeSet()
    ) :StatusList {
        val repoStatusList = getRepoStatusList(repo, Status.ShowT.INDEX_ONLY, flags)
        return repoStatusList
    }
    fun checkIndexIsEmptyAndGetIndexList(repo:Repository, repoId:String, onlyCheckEmpty:Boolean):Pair<Boolean, List<StatusTypeEntrySaver>?> {
        val repoStatusList = getIndexStatusList(repo)
        val index = repo.index()
        val indexMustEmpty = repoStatusList.entryCount() < 1
        if(indexMustEmpty) {
            return Pair(true, null)
        }
        if(!index.hasConflicts() && onlyCheckEmpty) {  
            return Pair(false, null)
        }
        val (_, statusMap) = runBlocking {statusListToStatusMap(
            repo,
            repoStatusList,
            repoId,
            Cons.gitDiffFromHeadToIndex
        )}
        val indexListFromStatusMap = statusMap[Cons.gitStatusKeyIndex]
        return Pair(indexListFromStatusMap.isNullOrEmpty(), indexListFromStatusMap)
    }
    fun indexIsEmpty(repo:Repository):Boolean {
        val (isEmpty,_) = checkIndexIsEmptyAndGetIndexList(repo = repo, repoId = "", onlyCheckEmpty = true)
        return isEmpty
    }
    fun getWorkdirStatusList(
        repo:Repository,
        flags:EnumSet<Status.OptT> = getDefaultStatusOptTypeSet()
    ) :StatusList {
        return getRepoStatusList(repo,Status.ShowT.WORKDIR_ONLY,flags)
    }
    @Deprecated("[CHINESE]index[CHINESE]workdir[CHINESE]list，[CHINESE]")
    fun getIndexAndWorkdirStatusList(
        repo:Repository,
        flags:EnumSet<Status.OptT> = getDefaultStatusOptTypeSet()
    ) :StatusList {
        return getRepoStatusList(repo,Status.ShowT.INDEX_AND_WORKDIR,flags)
    }
    fun hasConflictItemInRepo(repo:Repository):Boolean {
        return repo.index().hasConflicts()
    }
    fun getRepoCanonicalPath(repo: Repository, itemUnderRepoRelativePath: String): String {
        val slashChar = Cons.slashChar
        return getRepoWorkdirNoEndsWithSlash(repo) + slashChar + (itemUnderRepoRelativePath.trim(slashChar))
    }
    fun getRepoPathSpecType(path: String): Int {
        val endsWithSeparator = path.endsWith("/")
        if(endsWithSeparator){
            return Cons.gitItemTypeDir
        }else {
            return Cons.gitItemTypeFile
        }
    }
    fun getRelativePathUnderRepo(repoPathNoFileSeparatorAtEnd: String, fileFullPath: String): String? {
        try {
            return fileFullPath.substring(fileFullPath.indexOf(repoPathNoFileSeparatorAtEnd)+repoPathNoFileSeparatorAtEnd.length+1)
        }catch (e:Exception) {
            MyLog.e(TAG, "#getRelativePathUnderRepo err: ${e.stackTraceToString()}")
            return null
        }
    }
    @Deprecated("[CHINESE]，[CHINESE]，[CHINESE]jni[CHINESE]，" +
            "[CHINESE]java[CHINESE]，[CHINESE]jni[CHINESE]c[CHINESE]，[CHINESE]，" +
            "[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，" +
            "[CHINESE]，[CHINESE]jni[CHINESE]，[CHINESE]，[CHINESE]legacy[CHINESE]")
    suspend fun statusListToStatusMap_LoadListInJni(
        repo: Repository,
        statusList:StatusList,
        repoIdFromDb:String,
        fromTo: String,
        removeNonExistsConflictItems:Boolean=true
    ):Pair<Boolean, Map<String,List<StatusTypeEntrySaver>>> {
        val funName = "statusListToStatusMap_LoadListInJni"
        val debugExeTime_Start = System.currentTimeMillis()
        MyLog.d(TAG, "#$funName(): change list load method: start at $debugExeTime_Start")
        val index:MutableList<StatusTypeEntrySaver> = ArrayList()
        val workdir:MutableList<StatusTypeEntrySaver> =ArrayList()
        val conflict:MutableList<StatusTypeEntrySaver> = ArrayList()
        val entryCnt: Int = statusList.entryCount()
        val repoIndex = repo.index()
        var isIndexChanged = false
        val submodulePathList = getSubmodulePathList(repo)  
        val repoWorkDirPath = getRepoWorkdirNoEndsWithSlash(repo)
        val allStatusEntryDtos = LibgitTwo.getStatusEntries(statusList.rawPointer)
        val trueIndex2WorktreeFalseHead2Index = fromTo == Cons.gitDiffFromIndexToWorktree;
        for (i in allStatusEntryDtos)  {
            val oldFilePath = (if(trueIndex2WorktreeFalseHead2Index) i.indexToWorkDirOldFilePath else i.headToIndexOldFilePath) ?: ""
            val newFilePath = (if(trueIndex2WorktreeFalseHead2Index) i.indexToWorkDirNewFilePath else i.headToIndexNewFilePath) ?: ""
            var path= newFilePath
            var fileSize = (if(trueIndex2WorktreeFalseHead2Index) i.indexToWorkDirNewFileSize else i.headToIndexNewFileSize) ?: 0L
            val status = i.statusFlagToSet();  
            val statusTypeSaver = StatusTypeEntrySaver()
            statusTypeSaver.repoWorkDirPath = repoWorkDirPath
            statusTypeSaver.repoIdFromDb = repoIdFromDb
            if(status.contains(Status.StatusT.CONFLICTED)) {  
                val mustPath = newFilePath.ifEmpty { oldFilePath }
                if(mustPath.isNotEmpty()) {
                    val f = File(getRepoWorkdirNoEndsWithSlash(repo), mustPath)
                    if(!f.exists() && removeNonExistsConflictItems){
                        MyLog.w(TAG, "#$funName(): removed a Non-exists conflict item from git, file '$mustPath' may delete after it become conflict item")
                        repoIndex.conflictRemove(mustPath)
                        isIndexChanged = true
                    }else {
                        statusTypeSaver.changeType=Cons.gitStatusConflict
                        conflict.add(statusTypeSaver)
                    }
                }else{
                    MyLog.w(TAG, "#$funName(): conflict item with empty path!, repoWorkDir at '$repoWorkDirPath'")
                }
            }else{
                if(fromTo == Cons.gitDiffFromHeadToIndex) {  
                    if(status.contains(Status.StatusT.INDEX_NEW)){
                        statusTypeSaver.changeType=Cons.gitStatusNew
                        index.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.INDEX_DELETED)){
                        fileSize = i.headToIndexOldFileSize ?: 0L
                        path=oldFilePath
                        statusTypeSaver.changeType=Cons.gitStatusDeleted
                        index.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.INDEX_MODIFIED)){
                        statusTypeSaver.changeType=Cons.gitStatusModified
                        index.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.INDEX_RENAMED)){
                        statusTypeSaver.changeType=Cons.gitStatusRenamed
                        index.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.INDEX_TYPECHANGE)){
                        statusTypeSaver.changeType=Cons.gitStatusTypechanged
                        index.add(statusTypeSaver)
                    }
                }else {  
                    if(status.contains(Status.StatusT.WT_NEW)){ 
                        statusTypeSaver.changeType=Cons.gitStatusNew
                        workdir.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.WT_DELETED)){
                        fileSize = i.indexToWorkDirOldFileSize ?: 0L
                        path=oldFilePath
                        statusTypeSaver.changeType=Cons.gitStatusDeleted
                        workdir.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.WT_MODIFIED)){
                        statusTypeSaver.changeType=Cons.gitStatusModified
                        workdir.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.WT_RENAMED)){
                        statusTypeSaver.changeType=Cons.gitStatusRenamed
                        workdir.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.WT_TYPECHANGE)){
                        statusTypeSaver.changeType=Cons.gitStatusTypechanged
                        workdir.add(statusTypeSaver)
                    }
                }
            }
            val canonicalPath = getRepoCanonicalPath(repo,path)
            val fileType = getRepoPathSpecType(path)
            statusTypeSaver.itemType= if(submodulePathList.contains(path)) Cons.gitItemTypeSubmodule else fileType
            if(statusTypeSaver.itemType == Cons.gitItemTypeSubmodule) {
                statusTypeSaver.dirty = submoduleIsDirty(repo, path)
            }
            statusTypeSaver.canonicalPath = canonicalPath
            statusTypeSaver.fileName = getFileNameFromCanonicalPath(canonicalPath)  
            statusTypeSaver.relativePathUnderRepo = path
            statusTypeSaver.fileParentPathOfRelativePath = getParentPathEndsWithSeparator(path)
            statusTypeSaver.fileSizeInBytes = fileSize
            if(statusTypeSaver.fileSizeInBytes==0L && statusTypeSaver.changeType == Cons.gitStatusDeleted) {
                val diffItem = getSingleDiffItem(
                    repo,
                    statusTypeSaver.relativePathUnderRepo,
                    fromTo,
                    onlyCheckFileSize = true,
                    loadChannel = null,
                    checkChannelLinesLimit = -1,
                    checkChannelSizeLimit = -1L,
                )
                statusTypeSaver.fileSizeInBytes = diffItem.getEfficientFileSize()
            }
        }
        if(isIndexChanged) {
            repoIndex.write()
        }
        val resultMap:MutableMap<String,MutableList<StatusTypeEntrySaver>> = HashMap()
        resultMap[Cons.gitStatusKeyIndex] = index
        resultMap[Cons.gitStatusKeyWorkdir] = workdir
        resultMap[Cons.gitStatusKeyConflict] = conflict
        val debugExeTime_End = System.currentTimeMillis()
        MyLog.d(TAG, "#$funName(): change list load method: end at $debugExeTime_End, spent: ${debugExeTime_End - debugExeTime_Start}")
        return Pair(isIndexChanged, resultMap);
    }
    suspend fun statusListToStatusMap_legacy(
        repo: Repository,
        statusList:StatusList,
        repoIdFromDb:String,
        fromTo: String,
        removeNonExistsConflictItems:Boolean=true
    ):Pair<Boolean, Map<String,List<StatusTypeEntrySaver>>> {
        val funName = "statusListToStatusMap_legacy"
        val debugExeTime_Start = System.currentTimeMillis()
        MyLog.d(TAG, "#$funName(): change list load method: start at: $debugExeTime_Start")
        val index:MutableList<StatusTypeEntrySaver> = ArrayList()
        val workdir:MutableList<StatusTypeEntrySaver> =ArrayList()
        val conflict:MutableList<StatusTypeEntrySaver> = ArrayList()
        val entryCnt: Int = statusList.entryCount()
        val repoIndex = repo.index()
        var isIndexChanged = false
        val submodulePathList = getSubmodulePathList(repo)  
        val repoWorkDirPath = getRepoWorkdirNoEndsWithSlash(repo)
        for (i in 0 until entryCnt)  {
            val entry = statusList.byIndex(i)
            var delta = entry.indexToWorkdir  
            if(fromTo == Cons.gitDiffFromHeadToIndex){  
                delta = entry.headToIndex
            }
            val oldFile = delta?.oldFile
            val newFile = delta?.newFile
            var file=newFile
            var path=newFile?.path?:""
            val status = entry.status  
            val statusTypeSaver = StatusTypeEntrySaver()
            statusTypeSaver.repoWorkDirPath = repoWorkDirPath
            statusTypeSaver.repoIdFromDb = repoIdFromDb
            if(status.contains(Status.StatusT.CONFLICTED)) {  
                val mustPath = newFile?.path?:oldFile?.path?:""
                if(mustPath.isNotEmpty()) {
                    val f = File(getRepoWorkdirNoEndsWithSlash(repo), mustPath)
                    if(!f.exists() && removeNonExistsConflictItems){
                        MyLog.w(TAG, "#$funName(): removed a Non-exists conflict item from git, file '$mustPath' may delete after it become conflict item")
                        repoIndex.conflictRemove(mustPath)
                        isIndexChanged = true
                    }else {
                        statusTypeSaver.changeType=Cons.gitStatusConflict
                        conflict.add(statusTypeSaver)
                    }
                }else{
                    MyLog.w(TAG, "#$funName(): conflict item with empty path!, repoWorkDir at '$repoWorkDirPath'")
                }
            }else{
                if(fromTo == Cons.gitDiffFromHeadToIndex) {  
                    if(status.contains(Status.StatusT.INDEX_NEW)){
                        statusTypeSaver.changeType=Cons.gitStatusNew
                        index.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.INDEX_DELETED)){
                        file=oldFile
                        path=oldFile?.path?:""
                        statusTypeSaver.changeType=Cons.gitStatusDeleted
                        index.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.INDEX_MODIFIED)){
                        statusTypeSaver.changeType=Cons.gitStatusModified
                        index.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.INDEX_RENAMED)){
                        statusTypeSaver.changeType=Cons.gitStatusRenamed
                        index.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.INDEX_TYPECHANGE)){
                        statusTypeSaver.changeType=Cons.gitStatusTypechanged
                        index.add(statusTypeSaver)
                    }
                }else {  
                    if(status.contains(Status.StatusT.WT_NEW)){ 
                        statusTypeSaver.changeType=Cons.gitStatusNew
                        workdir.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.WT_DELETED)){
                        file=oldFile
                        path=oldFile?.path?:""
                        statusTypeSaver.changeType=Cons.gitStatusDeleted
                        workdir.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.WT_MODIFIED)){
                        statusTypeSaver.changeType=Cons.gitStatusModified
                        workdir.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.WT_RENAMED)){
                        statusTypeSaver.changeType=Cons.gitStatusRenamed
                        workdir.add(statusTypeSaver)
                    }else if(status.contains(Status.StatusT.WT_TYPECHANGE)){
                        statusTypeSaver.changeType=Cons.gitStatusTypechanged
                        workdir.add(statusTypeSaver)
                    }
                }
            }
            val canonicalPath = getRepoCanonicalPath(repo,path)
            val fileType = getRepoPathSpecType(path)
            statusTypeSaver.itemType= if(submodulePathList.contains(path)) Cons.gitItemTypeSubmodule else fileType
            if(statusTypeSaver.itemType == Cons.gitItemTypeSubmodule) {
                statusTypeSaver.dirty = submoduleIsDirty(repo, path)
            }
            statusTypeSaver.canonicalPath = canonicalPath
            statusTypeSaver.fileName = getFileNameFromCanonicalPath(canonicalPath)  
            statusTypeSaver.relativePathUnderRepo = path
            statusTypeSaver.fileParentPathOfRelativePath = getParentPathEndsWithSeparator(path)
            statusTypeSaver.fileSizeInBytes = file?.size?.toLong()?:0L
            if(statusTypeSaver.fileSizeInBytes==0L && statusTypeSaver.changeType == Cons.gitStatusDeleted) {
                val diffItem = getSingleDiffItem(
                    repo,
                    statusTypeSaver.relativePathUnderRepo,
                    fromTo,
                    onlyCheckFileSize = true,
                    loadChannel = null,
                    checkChannelLinesLimit = -1,
                    checkChannelSizeLimit = -1L,
                )
                statusTypeSaver.fileSizeInBytes = diffItem.getEfficientFileSize()
            }
        }
        if(isIndexChanged) {
            repoIndex.write()
        }
        val resultMap:MutableMap<String,MutableList<StatusTypeEntrySaver>> = HashMap()
        resultMap[Cons.gitStatusKeyIndex] = index
        resultMap[Cons.gitStatusKeyWorkdir] = workdir
        resultMap[Cons.gitStatusKeyConflict] = conflict
        val debugExeTime_End = System.currentTimeMillis()
        MyLog.d(TAG, "#$funName(): change list load method: end at $debugExeTime_End, spent: ${debugExeTime_End - debugExeTime_Start}")
        return Pair(isIndexChanged, resultMap);
    }
    suspend fun statusListToStatusMap(
        repo: Repository,
        statusList:StatusList,
        repoIdFromDb:String,
        fromTo: String,
        removeNonExistsConflictItems:Boolean=true
    ):Pair<Boolean, Map<String,List<StatusTypeEntrySaver>>> {
        return if(DevFeature.legacyChangeListLoadMethod.state.value) {
            MyLog.d(TAG, "will use change list load method: `statusListToStatusMap_legacy`")
            statusListToStatusMap_legacy(
                repo,
                statusList,
                repoIdFromDb,
                fromTo,
                removeNonExistsConflictItems,
            )
        }else {
            MyLog.d(TAG, "will use change list load method: `statusListToStatusMap_LoadListInJni`")
            statusListToStatusMap_LoadListInJni(
                repo,
                statusList,
                repoIdFromDb,
                fromTo,
                removeNonExistsConflictItems,
            )
        }
    }
    fun getGitUrlType(gitUrl: String): Int {
        return if(
            gitUrl.let { it.startsWith(Cons.gitUrlHttpsStartStr, ignoreCase = true)
                    || it.startsWith(Cons.gitUrlHttpStartStr, ignoreCase = true)
            }
        ) {
            Cons.gitUrlTypeHttp
        }else {
            Cons.gitUrlTypeSsh
        }
    }
    fun getCredentialTypeByGitUrlType(gitUrlType: Int): Int {
        return if(gitUrlType == Cons.gitUrlTypeHttp){
            Cons.dbCredentialTypeHttp
        }else {  
            Cons.dbCredentialTypeSsh
        }
    }
    fun getTreeToTreeChangeList(
        repo: Repository,
        repoId: String,
        tree1: Tree,
        tree2: Tree?,  
        diffOptionsFlags: EnumSet<Diff.Options.FlagT> = getDefaultDiffOptionsFlags(),
        reverse: Boolean = false, 
        treeToWorkTree: Boolean = false, 
    ):List<StatusTypeEntrySaver> {
        val ret = mutableListOf<StatusTypeEntrySaver>()  
        val options = Diff.Options.create()
        val opFlags = diffOptionsFlags.toMutableSet()
        if(reverse) {
            opFlags.add(Diff.Options.FlagT.REVERSE)
        }
        options.flags = EnumSet.copyOf(opFlags);
        MyLog.d(TAG, "#getTreeToTreeChangeList: options.flags = $opFlags")
        val diff = if(treeToWorkTree) Diff.treeToWorkdir(repo, tree1, options) else Diff.treeToTree(repo, tree1, tree2, options)
        val submodulePathList = getSubmodulePathList(repo)  
        val repoWorkDirPath = getRepoWorkdirNoEndsWithSlash(repo)
        diff.foreach(
            { delta: Diff.Delta, progress: Float ->
                val oldFile = delta.oldFile
                val newFile = delta.newFile
                val oldFileOid = oldFile.id
                val newFileOid = newFile.id
                val stes = StatusTypeEntrySaver()
                stes.repoWorkDirPath = repoWorkDirPath
                stes.repoIdFromDb = repoId
                stes.relativePathUnderRepo = newFile.path  
                stes.canonicalPath = getRepoCanonicalPath(repo, stes.relativePathUnderRepo)
                stes.fileName = getFileNameFromCanonicalPath(stes.relativePathUnderRepo)  
                stes.fileParentPathOfRelativePath = getParentPathEndsWithSeparator(stes.relativePathUnderRepo)
                val fileType = getRepoPathSpecType(stes.relativePathUnderRepo)
                stes.itemType= if(submodulePathList.contains(stes.relativePathUnderRepo)) Cons.gitItemTypeSubmodule else fileType
                if(stes.itemType == Cons.gitItemTypeSubmodule) {
                    stes.dirty = submoduleIsDirty(repo, stes.relativePathUnderRepo)
                }
                if(oldFileOid.isNullOrEmptyOrZero && !newFileOid.isNullOrEmptyOrZero) {  
                    stes.changeType = Cons.gitStatusNew
                    stes.fileSizeInBytes = newFile.size.toLong()  
                }else if(!oldFileOid.isNullOrEmptyOrZero && newFileOid.isNullOrEmptyOrZero) {  
                    stes.changeType = Cons.gitStatusDeleted
                    stes.fileSizeInBytes = oldFile.size.toLong()  
                }else if(!oldFileOid.isNullOrEmptyOrZero && !newFileOid.isNullOrEmptyOrZero){ 
                    stes.changeType = Cons.gitStatusModified
                    stes.fileSizeInBytes = newFile.size.toLong()  
                }else{ 
                    stes.changeType = Cons.gitStatusDeleted
                    stes.fileSizeInBytes = oldFile.size.toLong()
                }
                ret.add(stes)
                0
            },
            { delta, binary ->
                0
            },
            { delta: Diff.Delta?, hunk: Diff.Hunk ->
                0
            },
            { delta: Diff.Delta?, hunk: Diff.Hunk?, line: Diff.Line ->
                0
            })
        return ret
    }
    fun revparseSingle(repo: Repository, revspec: String):GitObject? {
        try {
            val gitObject = Revparse.single(repo, revspec)
            return gitObject
        }catch (e:Exception) {
            MyLog.e(TAG, "#revparseSingle() error, params are (revspec=$revspec),\nerr is: "+e.stackTraceToString())
            return null
        }
    }
    fun resetHardToHead(repo: Repository, checkoutOptions: Checkout.Options?=null):Ret<String?> {
        return resetHardToRevspec(repo, "HEAD",checkoutOptions)
    }
    fun resetHardToRevspec(repo: Repository, revspec: String,checkoutOptions: Checkout.Options?=null):Ret<String?> {
        return resetToRevspec(repo, revspec, Reset.ResetT.HARD,checkoutOptions)
    }
    fun resetToRevspec(repo: Repository, revspec: String, resetType:Reset.ResetT,checkoutOptions: Checkout.Options?=null):Ret<String?> {
        if(revspec.isBlank()) {
            return Ret.createError(null, "invalid revspec (empty)")
        }
        try {
            val resetTarget = revparseSingle(repo, revspec)
            if(resetTarget==null){
                return Ret.createError(null, "resolve revspec failed!", Ret.ErrCode.resolveRevspecFailed)
            }
            Reset.reset(repo, resetTarget, resetType, checkoutOptions);
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#resetToRevspec() error: "+e.stackTraceToString())
            return Ret.createError(null, "reset err: ${e.localizedMessage}", Ret.ErrCode.resetErr)
        }
    }
    fun forEachMergeHeads(repo: Repository, act:(Oid)->Unit) {
        repo.mergeHeadForeach(  
            object : MergeheadForeachCb() {
                override fun call(oid: Oid): Int {
                    act(oid)
                    return 0
                }
            })
    }
    fun getMergeHeads(repo: Repository):MutableList<Oid> {
        val mergeHeadList = mutableListOf<Oid>()
        forEachMergeHeads(repo) {
            mergeHeadList.add(it)
        }
        return mergeHeadList
    }
    fun readyForContinueMerge(repo: Repository, activityContext:Context):Ret<String?> {
        try {
            if(repo.state() != Repository.StateT.MERGE) {
                return Ret.createError(null, activityContext.getString(R.string.repo_not_in_merging))
            }
            if(repo.index().hasConflicts()) {
                return Ret.createError(null, activityContext.getString(R.string.plz_resolve_conflicts_first))
            }
            if(getMergeHeads(repo).isEmpty()) {
                return Ret.createError(null, activityContext.getString(R.string.no_merge_head_found))
            }
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#readyForContinueMerge err: "+e.stackTraceToString())
            return Ret.createError(null, "err: "+e.localizedMessage)
        }
    }
    fun readyForContinueRebase(repo: Repository, activityContext:Context):Ret<Oid?> {
        val funName = "readyForContinueRebase"
        try {
            if(repo.state() != Repository.StateT.REBASE_MERGE) {
                return Ret.createError(null, activityContext.getString(R.string.repo_not_in_rebasing))
            }
            if(repo.index().hasConflicts()) {
                return Ret.createError(null, activityContext.getString(R.string.plz_resolve_conflicts_first))
            }
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: "+e.stackTraceToString())
            return Ret.createError(null, "err: "+e.localizedMessage)
        }
    }
    fun rebaseGetCurCommit(repo:Repository):Commit? {
        val rebaseOptions = Rebase.Options.createDefault()
        initRebaseOptions(rebaseOptions)
        val rebase = Rebase.open(repo, rebaseOptions)
        val curidx = rebase.operationCurrent()
        val cur = rebase.operationByIndex(curidx)
        val curOid = cur?.id ?: return null
        return resolveCommitByHash(repo, curOid.toString())
    }
    fun rebaseGetCurCommitRet(repo:Repository):Ret<Commit?> {
        try {
            val c = rebaseGetCurCommit(repo)
            if(c==null) {
                return Ret.createError(null, "resolve cur commit of rebase err")
            }
            return Ret.createSuccess(c)
        }catch (e:Exception) {
            MyLog.e(TAG, "#rebaseGetCurCommitRet err: ${e.stackTraceToString()}")
            return Ret.createError(null, e.localizedMessage ?: "err when get cur commit of rebase", exception = e)
        }
    }
    fun rebaseGetCurCommitMsg(repo:Repository):String {
        val curCommit = rebaseGetCurCommit(repo)
        if(curCommit!=null) {
            return curCommit.message()
        }else{
            return ""
        }
    }
    fun rebaseContinue(repo:Repository, activityContext:Context, username: String, email: String, overwriteAuthorForFirstCommit:Boolean=false, commitMsgForFirstCommit: String="", skipFirst:Boolean= false, settings: AppSettings):Ret<Oid?>{
        val readyCheck = readyForContinueRebase(repo, activityContext)
        if (readyCheck.hasError()) {
            return readyCheck
        }
        val rebaseOptions = Rebase.Options.createDefault()
        initRebaseOptions(rebaseOptions)
        val rebase = Rebase.open(repo, rebaseOptions)
        val rebaseCommitter = Libgit2Helper.createSignature(username, email, settings)
        if(!skipFirst && !indexIsEmpty(repo)) {  
            rebase.commit(if(overwriteAuthorForFirstCommit) rebaseCommitter else null, rebaseCommitter, null, commitMsgForFirstCommit.ifBlank { null })
        }
        val curRebaseOpIndex = rebase.operationCurrent()
        val allOpCount = rebase.operationEntrycount()
        var nextIdx = curRebaseOpIndex+1
        while(nextIdx < allOpCount) {  
            rebase.next()
            if(hasConflictItemInRepo(repo)) {  
                return Ret.createError(null, "rebase: conflicts abort continue, plz resolve then try again")
            }else {  
                rebase.commit(null, rebaseCommitter, null, null)
            }
            nextIdx++
        }
        rebase.finish(rebaseCommitter)
        val headId = repo.head()?.id() ?: return Ret.createError(null, "rebase: get new oid failed after finished rebase")
        return Ret.createSuccess(headId)
    }
    fun rebaseSkip(repo: Repository, activityContext:Context, username: String, email: String, settings: AppSettings):Ret<Oid?> {
        val checkoutOptions = Checkout.Options.defaultOptions()
        checkoutOptions.strategy = EnumSet.of(Checkout.StrategyT.FORCE)  
        Checkout.head(repo, checkoutOptions)
        return rebaseContinue(repo, activityContext, username, email, skipFirst = true, settings = settings)
    }
    fun rebaseAbort(repo:Repository):Ret<Unit?> {
        val funName = "rebaseAbort"
        try{
            val rebaseOptions = Rebase.Options.createDefault()
            initRebaseOptions(rebaseOptions)
            val rebase = Rebase.open(repo, rebaseOptions)
            rebase.abort()
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
            return Ret.createError(null, e.localizedMessage?:"rebase abort err", exception = e)
        }
    }
    fun rebaseAccept(repo: Repository, pathSpecList: List<String>, acceptTheirs:Boolean):Ret<String?> {
        try {
            if(pathSpecList.isEmpty()) {
                return Ret.createError(null, "pathspec list is Empty!")
            }
            if(repo.state() != Repository.StateT.REBASE_MERGE) {
                return Ret.createError(null, "repo not in REBASE")
            }
            if(!repo.index().hasConflicts()) {
                return Ret.createError(null, "repo has no conflicts")
            }
            val checkoutOpts = Checkout.Options.defaultOptions()
            checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.DISABLE_PATHSPEC_MATCH, Checkout.StrategyT.FORCE)  
            checkoutOpts.setPaths(pathSpecList.toTypedArray())
            return if(acceptTheirs) {
                rebaseAcceptTheirs(repo, checkoutOpts)
            }else {  
                mergeAcceptOurs(repo, checkoutOpts)
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#rebaseAccept err: params are: pathSpecList=$pathSpecList, acceptTheirs=$acceptTheirs\n"+e.stackTraceToString())
            return Ret.createError(null, "rebase accept ${if(acceptTheirs) "theirs" else "ours"} err: "+e.localizedMessage)
        }
    }
    fun cherrypickAccept(repo: Repository, pathSpecList: List<String>, acceptTheirs:Boolean):Ret<String?> {
        try {
            if(pathSpecList.isEmpty()) {
                return Ret.createError(null, "pathspec list is Empty!")
            }
            if(repo.state() != Repository.StateT.CHERRYPICK) {
                return Ret.createError(null, "repo not in CHERRYPICK")
            }
            if(!repo.index().hasConflicts()) {
                return Ret.createError(null, "repo has no conflicts")
            }
            val checkoutOpts = Checkout.Options.defaultOptions()
            checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.DISABLE_PATHSPEC_MATCH, Checkout.StrategyT.FORCE)  
            checkoutOpts.setPaths(pathSpecList.toTypedArray())
            return if(acceptTheirs) {
                cherrypickAcceptTheirs(repo, checkoutOpts)
            }else {  
                mergeAcceptOurs(repo, checkoutOpts)
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#cherrypickAccept err: params are: pathSpecList=$pathSpecList, acceptTheirs=$acceptTheirs\n"+e.stackTraceToString())
            return Ret.createError(null, "cherrypick accept ${if(acceptTheirs) "theirs" else "ours"} err: "+e.localizedMessage)
        }
    }
    private fun cherrypickAcceptTheirs(repo:Repository, checkoutOptions: Checkout.Options):Ret<String?> {
        val cpHeadCommitId = getCherryPickHeadCommit(repo).data?.id() ?: return Ret.createError(null, "resolve cur cherrypick commit err!")
        val targetTree = resolveTree(repo, cpHeadCommitId.toString()) ?: return Ret.createError(null, "resolve cur cherrypick commit to tree err!")
        Checkout.tree(repo, targetTree, checkoutOptions)
        return Ret.createSuccess(null)
    }
    private fun rebaseAcceptTheirs(repo:Repository, checkoutOptions: Checkout.Options):Ret<String?> {
        val rebaseOptions = Rebase.Options.createDefault()
        initRebaseOptions(rebaseOptions)
        val rebase = Rebase.open(repo, rebaseOptions)
        val curRebase = rebase.operationByIndex(rebase.operationCurrent())
        val curRebaseCommitId = curRebase?.id ?: return Ret.createError(null, "resolve cur rebase commit err!")
        val targetTree = resolveTree(repo, curRebaseCommitId.toString()) ?: return Ret.createError(null, "resolve cur rebase commit to tree err!")
        Checkout.tree(repo, targetTree, checkoutOptions)
        return Ret.createSuccess(null)
    }
    fun mergeAccept(repo:Repository, pathSpecList: List<String>, acceptTheirs:Boolean):Ret<String?> {
        try {
            if(pathSpecList.isEmpty()) {
                return Ret.createError(null, "pathspec list is Empty!")
            }
            if(repo.state() != Repository.StateT.MERGE) {
                return Ret.createError(null, "repo not in MERGE")
            }
            if(!repo.index().hasConflicts()) {
                return Ret.createError(null, "repo has no conflicts")
            }
            val checkoutOpts = Checkout.Options.defaultOptions()
            checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.DISABLE_PATHSPEC_MATCH, Checkout.StrategyT.FORCE)  
            checkoutOpts.setPaths(pathSpecList.toTypedArray())
            return if(acceptTheirs) {
                mergeAcceptTheirs(repo, checkoutOpts)
            }else {
                mergeAcceptOurs(repo, checkoutOpts)
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#mergeAccept err: params are: pathSpecList=$pathSpecList, acceptTheirs=$acceptTheirs\n"+e.stackTraceToString())
            return Ret.createError(null, "merge accept ${if(acceptTheirs) "theirs" else "ours"} err: "+e.localizedMessage)
        }
    }
    private fun mergeAcceptTheirs(repo:Repository, checkoutOptions: Checkout.Options):Ret<String?> {
        val mergeHeadList = getMergeHeads(repo)
        if(mergeHeadList.size!=1) {
            return Ret.createError(null, "too less or many merge head, expect 1, but found ${mergeHeadList.size}")
        }
        val mergeHead = mergeHeadList[0]
        val mergeHeadTree = resolveTree(repo, mergeHead.toString()) ?: return Ret.createError(null, "resolve merge head to tree err!")
        Checkout.tree(repo, mergeHeadTree, checkoutOptions)
        return Ret.createSuccess(null)
    }
    private fun mergeAcceptOurs(repo:Repository, checkoutOptions: Checkout.Options):Ret<String?> {
        Checkout.head(repo, checkoutOptions)
        return Ret.createSuccess(null)
    }
    suspend fun getSingleDiffItem(
        repo:Repository,
        relativePathUnderRepo:String,
        fromTo:String,
        tree1:Tree?=null,
        tree2:Tree?=null,
        diffOptionsFlags:EnumSet<Diff.Options.FlagT> = getDefaultDiffOptionsFlags(),
        onlyCheckFileSize:Boolean = false,
        reverse: Boolean=false,
        treeToWorkTree: Boolean = false,
        maxSizeLimit:Long = SettingsUtil.getSettingsSnapshot().diff.diffContentSizeMaxLimit,
        loadChannel: Channel<Int>?,
        checkChannelLinesLimit:Int,  
        checkChannelSizeLimit:Long,  
        languageScope: PLScope = PLScope.AUTO
    ):DiffItemSaver{
        val funName = "getSingleDiffItem"
        MyLog.d(TAG, "#$funName(): relativePathUnderRepo=${relativePathUnderRepo}, fromTo=${fromTo}")
        val diffItem = DiffItemSaver(relativePathUnderRepo = relativePathUnderRepo, fromTo = fromTo, languageScope = Box(languageScope))
        val options = Diff.Options.create()
        val opFlags = diffOptionsFlags.toMutableSet()
        if(reverse) {
            opFlags.add(Diff.Options.FlagT.REVERSE)
        }
        options.flags = EnumSet.copyOf(opFlags);
        MyLog.d(TAG, "#$funName: options.flags = $opFlags")
        options.pathSpec = arrayOf(relativePathUnderRepo) 
        val diff = if(fromTo == Cons.gitDiffFromIndexToWorktree) {
            Diff.indexToWorkdir(repo, null, options)
        }else if(fromTo == Cons.gitDiffFromHeadToIndex) {
            val headTree:Tree? = resolveHeadTree(repo)
            if(headTree == null) {
                MyLog.w(TAG, "#$funName(): require diff from head to index, but resolve HEAD tree failed!")
                throw RuntimeException("resolve 'HEAD Tree' failed")
            }
            Diff.treeToIndex(repo, headTree, repo.index(), options)
        }
        else {  
            MyLog.d(TAG, "#$funName(): require diff from tree to tree, tree1Oid=${tree1?.id().toString()}, tree2Oid=${tree2?.id().toString()}, reverse=$reverse")
            if(treeToWorkTree) {
                if(tree1 == null) {
                    throw RuntimeException("tree1 is null")
                }
                Diff.treeToWorkdir(repo, tree1, options)
            } else {
                if(tree1 == null) {
                    throw RuntimeException("tree1 is null")
                }
                if(tree2 == null) {
                    throw RuntimeException("tree2 is null")
                }
                Diff.treeToTree(repo, tree1, tree2, options)
            }
        }
        val deltaNum = diff.numDeltas()
        if(deltaNum<1) {
            diffItem.isFileModified = false
            return diffItem
        }
        diffItem.isFileModified = true
        val patch = Patch.fromDiff(diff, 0)?:return diffItem
        val delta=patch.delta
        diffItem.flags = delta.flags
        val oldFile = delta.oldFile
        val newFile = delta.newFile
        diffItem.oldFileOid = oldFile.id.toString()
        diffItem.newFileOid = newFile.id.toString()
        if(diffItem.oldFileOid == Cons.git_AllZeroOidStr && diffItem.newFileOid != Cons.git_AllZeroOidStr) {
            diffItem.changeType = Cons.gitStatusNew
        } else if(diffItem.oldFileOid != Cons.git_AllZeroOidStr && diffItem.newFileOid == Cons.git_AllZeroOidStr) {
            diffItem.changeType = Cons.gitStatusDeleted
        } else {
            diffItem.changeType = Cons.gitStatusModified
        }
        if(diffItem.oldFileOid == diffItem.newFileOid) {
            diffItem.changeType = Cons.gitStatusUnmodified
            diffItem.isFileModified = false
            return diffItem
        }
        diffItem.oldFileSize = oldFile.size.toLong()
        diffItem.newFileSize = newFile.size.toLong()
        if(onlyCheckFileSize) {
            return diffItem
        }
        val numHunks = patch.numHunks()
        if(numHunks==0) {
            return diffItem
        }
        var contentLenSum =0L
        var checkChannelLinesCount = 0
        var checkChannelContentSizeCount = 0L
        for(i in 0 until numHunks) {
            val hunkInfo = patch.getHunk(i) ?:continue
            val hunk = hunkInfo.hunk
            val lineCnt = hunkInfo.lines
            val hunkAndLines = PuppyHunkAndLines(diffItem)
            hunkAndLines.hunk.header = hunk.header
            MyLog.d(TAG, "#$funName(): hunk header: "+hunkAndLines.hunk.header)
            diffItem.hunks.add(hunkAndLines)
            for(j in 0 until lineCnt) {
                if(loadChannel!=null) {
                    if(++checkChannelLinesCount > checkChannelLinesLimit || checkChannelContentSizeCount>checkChannelSizeLimit) {
                        delay(1)
                        val recv = loadChannel.tryReceive()
                        if(recv.isClosed){  
                            MyLog.d(TAG, "#$funName: abort by terminate signal")
                            break
                        }else {
                            checkChannelLinesCount = 0
                            checkChannelContentSizeCount = 0
                        }
                    }
                }
                val line = patch.getLineInHunk(i, j)?:continue
                val pLine = PuppyLine()
                pLine.contentLen = line.contentLen
                contentLenSum+=pLine.contentLen
                checkChannelContentSizeCount+=pLine.contentLen
                if(isDiffContentSizeOverLimit(contentLenSum, limit = maxSizeLimit)) {
                    diffItem.isContentSizeOverLimit = true
                    return diffItem
                }
                line.origin.let {
                    pLine.originType = it.toString()
                    if(it == Line.OriginType.ADDITION) {
                        diffItem.addedLines++
                        hunkAndLines.addedLinesCount++
                    }else if(it == Line.OriginType.DELETION) {
                        diffItem.deletedLines++
                        hunkAndLines.deletedLinesCount++
                    }else if(it == Line.OriginType.ADD_EOFNL || it == Line.OriginType.DEL_EOFNL || it == Line.OriginType.CONTEXT_EOFNL) {
                        diffItem.hasEofLine = true
                    }
                    diffItem.allLines++
                    Unit
                }
                pLine.oldLineNum= line.oldLineno 
                pLine.newLineNum=line.newLineno  
                    pLine.content = line.content
                pLine.lineNum = pLine.getAValidLineNum().let { if(it > diffItem.maxLineNum) diffItem.maxLineNum = it; it }
                pLine.howManyLines = line.numLines
                hunkAndLines.addLine(pLine, diffItem.changeType)
            }
        }
        diffItem.generateFakeIndexForGroupedLines()
        diffItem.isContentSizeOverLimit = false
        return diffItem
    }
    fun getRepoGitDirPathNoEndsWithSlash(repo:Repository):String {
        return repo.itemPath(Repository.Item.GITDIR)?.trimEnd(Cons.slashChar) ?: ""
    }
    fun getRepoWorkdirNoEndsWithSlash(repo: Repository): String {
        return repo.workdir().let {
            if(it == null) {
                ""
            }else {
                File(it).canonicalPath
            }
        }
    }
    fun getRepoIgnoreFilePathNoEndsWithSlash(repo: Repository, createIfNonExists:Boolean = false): String {
        var path = getRepoWorkdirNoEndsWithSlash(repo) + Cons.slash + ".gitignore"
        if(createIfNonExists) {
            File(path).let {
                it.createNewFile()  
                path = it.canonicalPath
            }
        }
        return path
    }
    fun removeFromGit(
        repoIndex: Index,
        relativePathUnderRepo: String,
        isFile:Boolean
    ) {
        if (relativePathUnderRepo.isNotEmpty() && relativePathUnderRepo != ".git" && !relativePathUnderRepo.startsWith(".git/")) {
            Libgit2Helper.removeFromIndexThenWriteToDisk(
                repoIndex,
                IgnoreItem(relativePathUnderRepo, isFile),
                requireWriteToDisk = false  
            )  
        }
    }
    fun isRepoShallow(repo:Repository):Boolean {
        return repo.isShallow
    }
    fun getGitUsernameAndEmail(repo: Repository):Pair<String, String> {
        var (username, email) = getGitUserNameAndEmailFromRepo(repo)
        if(username.isBlank() || email.isBlank()) {
            val (usernameFromGlobal, emailFromGlobal) = getGitUsernameAndEmailFromGlobalConfig()
            if(username.isBlank()) {
                username = usernameFromGlobal
            }
            if(email.isBlank()) {
                email = emailFromGlobal
            }
        }
        return Pair(username, email)
    }
    fun genCommitMsgNoFault(
        repo: Repository,
        itemList: List<StatusTypeEntrySaver>?,
        msgTemplate: String,
    ):String {
        return try {
            genCommitMsg(repo, itemList, msgTemplate).let {
                if(it.hasError() || it.data.isNullOrBlank()) {
                    MyLog.w(TAG, "#genCommitMsgNoFault: generate commit msg err! will use fallback commit msg! errCode=${it.code}, errMsg=${it.msg}, commitMsgRet.data='${it.data}'")
                }
                it.data ?: Cons.fallbackCommitMsg
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#genCommitMsgNoFault: generate commit msg err: ${e.stackTraceToString()}")
            Cons.fallbackCommitMsg
        }
    }
    private fun genCommitMsg(
        repo:Repository,
        itemList: List<StatusTypeEntrySaver>?,
        msgTemplate:String,
    ): Ret<String?> {
        val repoState = repo.state()
        var actuallyItemList = itemList
        if(actuallyItemList.isNullOrEmpty()) { 
            val (isIndexEmpty, indexItemList) = checkIndexIsEmptyAndGetIndexList(repo, "", onlyCheckEmpty = false)  
            if(repoState != Repository.StateT.MERGE && (isIndexEmpty || indexItemList.isNullOrEmpty())) {  
                MyLog.w(TAG, "#genCommitMsg(): WARN: repo state may incorrect, state is not MERGE but index is empty, params are: repoState=$repoState, isIndexEmpty=$isIndexEmpty, indexItemList.isNullOrEmpty()=${indexItemList.isNullOrEmpty()}")
            }
            actuallyItemList = indexItemList?: emptyList()
        }
        return if(msgTemplate.isBlank()) {
            genCommitMsgLegacy(repo, repoState, actuallyItemList)
        }else {
            genCommitMsgByTemplate(repo, actuallyItemList, msgTemplate).let { ret ->
                if(ret.data.let { it == null || it.isBlank() }) {
                    genCommitMsgLegacy(repo, repoState, actuallyItemList)
                }else {
                    ret
                }
            }
        }
    }
    private fun genCommitMsgByTemplate(
        repo:Repository,
        itemList: List<StatusTypeEntrySaver>?,
        msgTemplate:String,
    ): Ret<String?> {
        return try {
            Ret.createSuccess(CommitMsgTemplateUtil.replace(repo, itemList, msgTemplate))
        }catch (e: Exception) {
            MyLog.e(TAG, "#genCommitMsgByTemplate err: ${e.stackTraceToString()}")
            Ret.createError(null, e.localizedMessage ?: "err", exception = e)
        }
    }
    private fun genCommitMsgLegacy(
        repo:Repository,
        repoState: Repository.StateT?,
        actuallyItemList: List<StatusTypeEntrySaver>,
    ): Ret<String?> {
        val allFilesCount = actuallyItemList.size
        val summary = (if(repoState==Repository.StateT.MERGE) "Conclude Merge" else if(repoState==Repository.StateT.REBASE_MERGE) "Rebase" else if(repoState==Repository.StateT.CHERRYPICK) "Cherrypick" else "Update $allFilesCount ${if(allFilesCount>1) "files" else "file"}") + (":\n")
        val descriptions=StringBuilder(summary)
        descriptions.append(CommitMsgTemplateUtil.genFileNames(actuallyItemList))
        return Ret.createSuccess(descriptions.toString())
    }
    private fun doCreateCommit(
        repo: Repository,
        msg: String?,
        username: String,
        email: String,
        curBranchFullRefSpec:String,
        parentList:List<Commit>,
        amend: Boolean,
        overwriteAuthorWhenAmend:Boolean,
        cleanRepoStateIfSuccess:Boolean,
        settings: AppSettings
    ):Ret<Oid?> {
        if(username.isBlank()) {
            return Ret.createError(null, "username is blank", Ret.ErrCode.usernameIsBlank)
        }
        if(email.isBlank()) {
            return Ret.createError(null, "email is blank",Ret.ErrCode.emailIsBlank)
        }
        val sign: Signature = Libgit2Helper.createSignature(username, email, settings)
        val tree = Tree.lookup(repo, repo.index().writeTree())
        val messageEncoding = null 
        val resultCommitOid =if(amend){
            val headCommitRet = getHeadCommit(repo)
            if(headCommitRet.hasError()) {
                return Ret.createError(null, "resolve HEAD failed!")
            }
            val author = if(overwriteAuthorWhenAmend) sign else null  
            val committer = sign  
            Commit.amend(headCommitRet.data!!, curBranchFullRefSpec, author, committer, messageEncoding, msg, tree)
        }else{
            val author = if(!overwriteAuthorWhenAmend && (repo.state()==Repository.StateT.CHERRYPICK || repo.state()==Repository.StateT.REBASE_MERGE)) {  
                val commitRet = if(repo.state() == Repository.StateT.CHERRYPICK) getCherryPickHeadCommit(repo) else  rebaseGetCurCommitRet(repo)
                if(commitRet.hasError() || commitRet.data==null) {
                    return Ret.createError(null, "query origin commit author info err!")
                }
                commitRet.data!!.author()
            }else {  
                sign
            }
            Commit.create(
                repo, curBranchFullRefSpec, author, sign, messageEncoding,
                msg ?: "Update File(s)",  
                tree, parentList
            )
        }
        if(cleanRepoStateIfSuccess) {
            cleanRepoState(repo)
        }
        return Ret.createSuccess(resultCommitOid)
    }
    fun cleanRepoState(repo: Repository, cancelIfHasConflicts:Boolean = true) {
        if(cancelIfHasConflicts && hasConflictItemInRepo(repo)) {
            return
        }
        repo.stateCleanup()
    }
    fun isReadyCreateCommit(repo: Repository, activityContext:Context):Ret<Oid?> {
        if(hasConflictItemInRepo(repo)) {
            return Ret.createError(null, activityContext.getString(R.string.plz_resolve_conflicts_first), Ret.ErrCode.hasConflictsNotStaged)
        }
        val repoState = repo.state()
        val indexIsEmpty1 = indexIsEmpty(repo)
        if(repoState==Repository.StateT.MERGE && getMergeHeads(repo).isEmpty()) {
            return Ret.createError(null, activityContext.getString(R.string.repo_state_is_merge_but_no_merge_head_found_plz_use_abort_merge_to_clean_state))
        }else if(repoState==Repository.StateT.REBASE_MERGE) {
            if(rebaseGetCurCommit(repo) == null) {
                return Ret.createError(null, activityContext.getString(R.string.repo_state_is_rebase_but_no_rebase_head_found_plz_use_abort_rebase_to_clean_state))
            }
        }else if(repoState==Repository.StateT.CHERRYPICK) {
            if(getCherryPickHeadCommit(repo).data == null) {
                return Ret.createError(null, activityContext.getString(R.string.repo_state_is_cherrypick_but_no_cherrypick_head_found_plz_use_abort_cherrypick_to_clean_state))
            }
        }
        if(repoState!=Repository.StateT.MERGE && repoState!=Repository.StateT.REBASE_MERGE && repoState!=Repository.StateT.CHERRYPICK
            && indexIsEmpty1
        ) {
            return Ret.createError(null, activityContext.getString(R.string.index_is_empty), Ret.ErrCode.indexIsEmpty)
        }
        return Ret.createSuccess(null)
    }
    fun createCommit(
        repo: Repository,
        msg: String,
        username: String,
        email: String,
        branchFullRefName:String="",
        indexItemList:List<StatusTypeEntrySaver> ?= null,
        parents:List<Commit>? = null,
        amend:Boolean = false,
        overwriteAuthorWhenAmend:Boolean = false,
        cleanRepoStateIfSuccess:Boolean,
        settings: AppSettings
    ):Ret<Oid?> {
        val funName = "createCommit"
        if(repo.state() == Repository.StateT.REBASE_MERGE) {
            return Ret.createError(null, "plz use Rebase Continue instead")
        }
        val firstCommit = repo.headUnborn()
        val needQueryRefNameByFunc = branchFullRefName.isEmpty() && !firstCommit
        val needAddParentsByFunc = (parents == null && !firstCommit)  
        val parents = if(needAddParentsByFunc || firstCommit) mutableListOf<Commit>() else parents!!
        var branchFullRefName = branchFullRefName  
        if(firstCommit.not() && (needAddParentsByFunc || needQueryRefNameByFunc)) {  
            val headRef = resolveRefByName(repo, "HEAD")
            if(headRef==null) {
                return Ret.createError(null, "get HEAD failed!", Ret.ErrCode.headIsNull)
            }
            if(needQueryRefNameByFunc) {
                branchFullRefName = headRef.name()
            }
            MyLog.d(TAG, "#$funName(), repo.state() = "+repo.state())
            if(needAddParentsByFunc) {
                val curBranchLatestCommit = headRef.id()?.let { Commit.lookup(repo, it) }
                if (curBranchLatestCommit != null) {
                    (parents as MutableList<Commit>).add(curBranchLatestCommit)
                }else {
                    return Ret.createError(null,"get current HEAD latest commit failed",Ret.ErrCode.createCommitFailedByGetRepoHeadCommitFaild)
                }
                if(repo.state() == Repository.StateT.MERGE) {
                    forEachMergeHeads(repo) { oid ->
                        (parents as MutableList<Commit>).add(Commit.lookup(repo, oid))  
                    }
                }
            }
        }
        val msg = if(msg.isBlank()) {  
            if(amend) {
                null
            }else {  
                genCommitMsgNoFault(repo, indexItemList, settings.commitMsgTemplate)
            }
        }else {
            msg
        }
        if(firstCommit) {
            branchFullRefName = "HEAD"
        }
        return doCreateCommit(repo,msg,username,email,branchFullRefName,parents, amend, overwriteAuthorWhenAmend, cleanRepoStateIfSuccess, settings)
    }
    fun getRefsHeadsBranchFullRefSpecFromShortRefSpec(shortRefSpec:String):String {
        return "refs/heads/"+shortRefSpec
    }
    fun getRepoCurBranchFullRefSpec(repo: Repository):String {
        val headRef = repo.head()
        return headRef?.name() ?: ""
    }
    fun getRepoCurBranchShortRefSpec(repo: Repository):String {
        if(repo.headDetached()) {
            return ""
        }
        val headRef = repo.head()
        return headRef?.shorthand() ?: ""
    }
    fun getUpstreamOfBranch(repo: Repository, shortBranchName: String): Upstream {
        try {
            val u = Upstream()
            if(shortBranchName.isBlank() || shortBranchName == "HEAD") {
                return u
            }
            val c = getRepoConfigForRead(repo)
            val refsHeadsBranchName = "refs/heads/$shortBranchName"
            val localBranchRef = resolveRefByName(repo, refsHeadsBranchName, trueUseDwimFalseUseLookup = false)
            if(localBranchRef==null) {
                throw RuntimeException("resolve shortBranchName to reference failed! shortBranchName=${shortBranchName}, refsHeadsBranchName=$refsHeadsBranchName")
            }
            val remoteFromConfig = c.getString("branch."+shortBranchName+".remote").orElse("")
            val fullRefSpecFromConfig = c.getString("branch."+shortBranchName+".merge").orElse("")
            if(remoteFromConfig.isBlank() || fullRefSpecFromConfig.isBlank()) {  
                return u  
            }
            u.remote = remoteFromConfig
            u.branchRefsHeadsFullRefSpec = fullRefSpecFromConfig
            u.downstreamLocalBranchShortRefSpec = localBranchRef?.shorthand()?:""
            u.downstreamLocalBranchRefsHeadsFullRefSpec = localBranchRef?.name()?:""
            u.localOid = localBranchRef?.peel(GitObject.Type.COMMIT)?.id()?.toString() ?:""
            u.pushRefSpec = u.downstreamLocalBranchRefsHeadsFullRefSpec+":"+u.branchRefsHeadsFullRefSpec
            u.remoteBranchShortRefSpec = getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(u.remote, u.branchRefsHeadsFullRefSpec)
            u.remoteBranchRefsRemotesFullRefSpec = "refs/remotes/"+u.remoteBranchShortRefSpec
            u.remoteBranchShortRefSpecNoPrefix = getShortRefSpecByRefsHeadsRefSpec(u.branchRefsHeadsFullRefSpec)?:""  
            u.isPublished = false  
            val remoteOid = resolveCommitOidByRef(repo, u.remoteBranchRefsRemotesFullRefSpec)
            if(remoteOid!=null && !remoteOid.isNullOrEmptyOrZero) {
                u.remoteOid = remoteOid.toString()
            }
            if(u.remote.isNotBlank() && u.branchRefsHeadsFullRefSpec.isNotBlank()) {  
                u.isPublished = isUpstreamActuallyExistOnLocal(repo, u.remote, u.branchRefsHeadsFullRefSpec)
            }
            return u
        }catch (e:Exception) {
            MyLog.e(TAG, "#getUpstreamOfBranch() error: "+e.stackTraceToString())
            return Upstream()  
        }
    }
    fun clearUpstreamForBranch(repo: Repository, targetBranchShortName:String) {
        val c = getRepoConfigForWrite(repo)
        try {
            c.deleteMultivar("branch."+targetBranchShortName+".remote", Cons.regexMatchAll)
        }catch (e:Exception) {
            MyLog.e(TAG, "#clearUpstreamForBranch err when delete 'remote': ${e.stackTraceToString()}")
        }
        try {
            c.deleteMultivar("branch."+targetBranchShortName+".merge", Cons.regexMatchAll)
        }catch (e:Exception) {
            MyLog.e(TAG, "#clearUpstreamForBranch err when delete 'merge': ${e.stackTraceToString()}")
        }
    }
    fun setUpstreamForBranch(repo: Repository, upstream: Upstream, setForBranchName:String="") {
        setUpstreamForBranchByRemoteAndRefspec(repo, upstream.remote, upstream.branchRefsHeadsFullRefSpec, setForBranchName)
    }
    fun setUpstreamForBranchByRemoteAndRefspec(repo: Repository, remote: String?, fullBranchRefSpec:String?, targetBranchShortName:String=""):Boolean {
        if(remote.isNullOrBlank() || fullBranchRefSpec.isNullOrBlank()) {
            MyLog.d(TAG,"#setUpstreamForBranchByRemoteAndRefspec, bad upstream, remote="+remote+", fullBranchRefSpec="+fullBranchRefSpec)
            return false
        }
        val c = getRepoConfigForWrite(repo)
        val branchWhichWillUpdate = if(targetBranchShortName.isBlank()) {
            getRepoCurBranchShortRefSpec(repo)
        }else {
            targetBranchShortName
        }
        c.setString("branch."+branchWhichWillUpdate+".remote", remote)
        c.setString("branch."+branchWhichWillUpdate+".merge",fullBranchRefSpec)
        return true
    }
    fun getUpstreamRemoteBranchShortRefSpecByLocalBranchShortName(repo:Repository, localBranchShortBranchName:String):String? {
        val localBranchRef = resolveBranch(repo, localBranchShortBranchName, Branch.BranchType.LOCAL)  
        if(localBranchRef == null) {
            return null
        }
        val upstreamRef = Branch.upstream(localBranchRef)  
        if(upstreamRef==null) {
            return null
        }
        return upstreamRef.shorthand()
    }
    fun isUpstreamInvalid(upstream: Upstream?):Boolean {
        if(upstream == null) {
            return true
        }
        return isUpstreamFieldsInvalid(upstream.remote, upstream.branchRefsHeadsFullRefSpec)
    }
    fun isUpstreamFieldsInvalid(remote: String?, branchFullRefSpec: String?):Boolean {
        return isStringListHasInvalidItem(listOf(remote,branchFullRefSpec))
    }
    fun isUsernameAndEmailInvalid(username: String?, email: String?):Boolean {
        return isStringListHasInvalidItem(listOf(username,email))
    }
    fun isStringListHasInvalidItem(strList:List<String?>): Boolean{
        for(s in strList) {
            if(s==null || s.isBlank()) {
                return true
            }
        }
        return false
    }
    fun isUpstreamActuallyExistOnLocal(repo: Repository, remote:String, branchFullRefSpec:String):Boolean {
        val needCheckRemoteBranchName = getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(remote, branchFullRefSpec)
        try {
            val ref = resolveRefByName(repo, needCheckRemoteBranchName)  
            val exist = ref!=null
            return exist
        }catch (e:Exception) {
            MyLog.e(TAG, "#isUpstreamActuallyExistOnLocal() error: "+e.stackTraceToString())
            return false
        }
    }
    fun getRemoteList(repo: Repository): List<String> {
        try {
            return Remote.list(repo)
        }catch (e:Exception) {
            MyLog.e(TAG, "#getRemoteList() err, will return an empty list!\nException is:${e.stackTraceToString()}")
            return listOf()
        }
    }
    fun isBranchHasUpstream(repo: Repository):Boolean {
        val shortBranchName = getRepoCurBranchShortRefSpec(repo)
        val upstream: Upstream = getUpstreamOfBranch(repo, shortBranchName)
        if (upstream.remote.isBlank() || upstream.branchRefsHeadsFullRefSpec.isBlank()) {
            return false
        }
        return true
    }
    fun addToIndexThenWriteToDisk(repo: Repository, pathSpecList: List<String>) {
        val index = repo.index()
        pathSpecList.forEachBetter {
            index.add(it)
        }
        index.write()
    }
    fun removePathSpecListFromIndexThenWriteToDisk(repo: Repository, pathSpecList: List<IgnoreItem>) {
        val repoIndex = repo.index()
        pathSpecList.forEachBetter {
            removeFromIndexThenWriteToDisk(repoIndex, it, requireWriteToDisk=false)  
        }
        repoIndex.write()
    }
    fun removeFromIndexThenWriteToDisk(index:Index, ignoreItem: IgnoreItem, requireWriteToDisk:Boolean=false) {
        val isFile = ignoreItem.isFile
        val pathspec = ignoreItem.pathspec  
        if(isFile) {  
            index.removeByPath(pathspec)
        }else {  
            index.removeDirectory(pathspec, 0)  
        }
        if(requireWriteToDisk) {
            index.write()
        }
    }
    fun stageStatusEntryAndWriteToDisk(repo: Repository, list: List<StatusTypeEntrySaver>) {
        val index = repo.index()
        var neverShowErr = true
        list.forEachBetter {
            try {
                if(it.changeType == Cons.gitStatusDeleted) {  
                    index.removeByPath(it.relativePathUnderRepo)
                }else{  
                    index.add(it.relativePathUnderRepo)
                }
            }catch (e:Exception) {
                val fileName = getFileNameFromCanonicalPath(it.relativePathUnderRepo)
                MyLog.e(TAG, "#stageStatusEntryAndWriteToDisk err: fileName=$fileName, pathUnderRepo=${it.relativePathUnderRepo}, err=${e.localizedMessage}")
                if(neverShowErr) {
                    neverShowErr = false
                    Msg.requireShowLongDuration("stage '$fileName' err: ${e.localizedMessage}")
                }
            }
        }
        index.write()
    }
    fun fetchRemoteListForRepo(
        repo: Repository,
        remoteList:List<RemoteAndCredentials>,
        repoFromDb:RepoEntity,
        requireUnshallow:Boolean=false,
        refspecs:Array<String>? = null,
        pruneType:FetchOptions.PruneT =FetchOptions.PruneT.PRUNE,  
        downloadTags: Remote.AutotagOptionT = Remote.AutotagOptionT.UNSPECIFIED,  
    ) {
        var repoIsShallow = isRepoShallow(repo)
        if (repoIsShallow) {
            Libgit2Helper.ShallowManage.createShallowBak(repo)
        }
        val shallowFile = Libgit2Helper.ShallowManage.getShallowFile(repo)
        for(remoteAndCredentials in remoteList) {
            try {
                val fetchOpts = FetchOptions.createDefault()
                if(requireUnshallow) {
                    fetchOpts.depth = FetchOptions.DepthT.UNSHALLOW
                }else {
                    fetchOpts.depth = FetchOptions.DepthT.FULL  
                }
                fetchOpts.downloadTags = downloadTags
                fetchOpts.prune = pruneType  
                val remoteName = remoteAndCredentials.remoteName
                val credential = remoteAndCredentials.fetchCredential
                val remote = Remote.lookup(repo, remoteName)!!
                val remoteFetchUrl = getRemoteFetchUrl(remote)
                val callbacks = fetchOpts.callbacks
                if(credential!=null) {  
                    setCredentialCbForRemoteCallbacks(callbacks, getCredentialTypeByUrl(remoteFetchUrl), credential)
                }
                setCertCheckCallback(remoteFetchUrl, callbacks, repo)
                remote.fetch(refspecs, fetchOpts, "fetch: $remoteName")
                if(repoIsShallow && !shallowFile.exists()) {
                    if (requireUnshallow) {  
                        ShallowManage.deleteBak1(repo)  
                        repoIsShallow=false  
                        MyLog.d(TAG, "deleted '${ShallowManage.bak1}' for repo '${repoFromDb.repoName}'")
                    } else { 
                        ShallowManage.restoreShallowFile(repo)
                    }
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "fetchRemoteListForRepo err: remoteName=${remoteAndCredentials.remoteName}, err=${e.stackTraceToString()}")
                throw e
            }
        }
    }
    fun setCertCheckCallback(
        url: String,
        callbacks: Remote.Callbacks,
        repo: Repository?,
    ) {
        if(isSshUrl(url)) {
            if(SettingsUtil.sshAllowUnknownHosts()) {  
                setAllowUnknownHostsForCertificatesCheck(callbacks)
            }else { 
                callbacks.setCertificateCheckCb cb@{ cert, valid, hostname ->
                    if(valid) {
                        MyLog.d(TAG, "libgit2 think cert of '$hostname' is valid, the cert maybe in the 'known_hosts' file, certCb will return 1 to honor libgit2's decide, then the connection should allow")
                        return@cb 1
                    }
                    val sshCert = LibgitTwo.jniGetDataOfSshCert(cert.rawPointer, hostname)
                    MyLog.d(TAG, "sshCertFromJni==null: ${sshCert==null}")
                    if(sshCert == null) {
                        return@cb -1
                    }
                    if(sshCert.isEmpty()) {
                        Msg.requireShowLongDuration("err: empty host fingerprint, hostname=$hostname")
                        MyLog.w(TAG, "empty ssh cert: hostname=$hostname")
                        return@cb -1
                    }
                    if(Lg2HomeUtils.itemInUserKnownHostsFile(sshCert)) {
                        return@cb 0
                    }
                    MyLog.d(TAG, "unknown `SshCert` request review: $sshCert")
                    doJobThenOffLoading {
                        KnownHostRequestStateMan.addToList(
                            SshAskUserUnknownHostRequest(
                                sshCert
                            )
                        )
                    }
                    Msg.requireShow(AppModel.realAppContext.getString(R.string.aborted_unknown_host))
                    return@cb -1
                }
            }
        }else {  
            if(!isHttpSslVerifyEnabled(repo)) {
                setAllowUnknownHostsForCertificatesCheck(callbacks)
            }
        }
    }
    fun isHttpSslVerifyEnabled(repo: Repository?): Boolean {
        val globalSettings = SettingsUtil.httpSslVerify()
        if(repo == null) {
            return globalSettings
        }
        return try {
            getRepoConfigForRead(repo)
                .getBool(Cons.gitConfigKeyHttpSslVerify)
                .orElse(globalSettings)
        }catch (_: Exception) {
            globalSettings
        }
    }
    fun getRemoteFetchUrlByName(repo:Repository, remoteName: String):String {
        try {
            return getRemoteFetchUrl(Remote.lookup(repo, remoteName)!!)
        }catch (e:Exception) {
            return ""
        }
    }
    fun getRemoteActuallyUsedPushUrlByName(repo:Repository, remoteName: String):String {
        try {
            return getRemoteActuallyUsedPushUrl(Remote.lookup(repo, remoteName)!!)
        }catch (e:Exception) {
            return ""
        }
    }
    fun getRemoteFetchUrl(remote:Remote):String {
        return try {
            remote.url()
        }catch (e:Exception) {
            ""
        }
    }
    fun getRemoteActuallyUsedPushUrl(remote:Remote):String {
        try {
            val pushUrl = remote.pushurl() ?:""
            if(pushUrl.isBlank()) {
                return getRemoteFetchUrl(remote)
            }
            return pushUrl
        }catch (e:Exception) {
            return ""
        }
    }
    fun unshallowRepoByRemoteList(repo: Repository, remoteList:List<RemoteAndCredentials>,repoFromDb: RepoEntity){
        fetchRemoteListForRepo(repo, remoteList, repoFromDb, requireUnshallow = true)
    }
    suspend fun unshallowRepo(repo: Repository, repoFromDb: RepoEntity, repoDb:RepoRepository, remoteDb:RemoteRepository, credentialDb: CredentialRepository):Ret<String?> {
        try {
            val remoteDtoListFromDb = remoteDb.getRemoteDtoListByRepoId(repoFromDb.id)
            val remoteAndCredentialPairList = genRemoteCredentialPairList(remoteDtoListFromDb, credentialDb, requireFetchCredential = true, requirePushCredential = false)
            val singleOriginList = listOf(remoteAndCredentialPairList.first { it.remoteName == Cons.gitDefaultRemoteOrigin })
            unshallowRepoByRemoteList(repo, singleOriginList, repoFromDb)
            repoDb.updateIsShallow(repoFromDb.id, Cons.dbCommonFalse)
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#unshallowRepo(): err: "+e.stackTraceToString())
            val errMsg = "unshallow err: "+e.localizedMessage
            createAndInsertError(repoFromDb.id, errMsg)
            return Ret.createError(null, errMsg, Ret.ErrCode.unshallowRepoErr)
        }
    }
    fun genRemoteCredentialPairList(list:List<RemoteDto>, credentialDb: CredentialRepository, requireFetchCredential:Boolean, requirePushCredential:Boolean):List<RemoteAndCredentials> {
        val remoteCredentialList = mutableListOf<RemoteAndCredentials>()
        val masterPassword = AppModel.masterPassword.value
        list.forEachBetter {  
            val rac = RemoteAndCredentials()
            rac.remoteName = it.remoteName
            if(requireFetchCredential) {
                var credential: CredentialEntity? = null
                if (it.credentialVal?.isNotBlank() == true || it.credentialPass?.isNotBlank() == true) {
                    credential = CredentialEntity()
                    credential.value = it.credentialVal ?: ""
                    credential.pass = it.credentialPass ?: ""
                    credential.type = it.credentialType
                    credentialDb.decryptPassIfNeed(credential, masterPassword)
                    rac.fetchCredential = credential
                }
            }
            if(requirePushCredential) {
                var credential: CredentialEntity? = null
                if (it.pushCredentialVal?.isNotBlank() == true || it.pushCredentialPass?.isNotBlank() == true) {
                    credential = CredentialEntity()
                    credential.value = it.pushCredentialVal ?: ""
                    credential.pass = it.pushCredentialPass ?: ""
                    credential.type = it.pushCredentialType
                    credentialDb.decryptPassIfNeed(credential, masterPassword)
                    rac.pushCredential = credential
                }
            }
            remoteCredentialList.add(rac)
        }
        return remoteCredentialList
    }
    fun fetchRemoteForRepo(repo:Repository, remoteName: String, credential:CredentialEntity?, repoFromDb: RepoEntity, refspecs:Array<String>? = null) {
        val remote = listOf(RemoteAndCredentials(remoteName, fetchCredential = credential))
        fetchRemoteListForRepo(repo, remote, repoFromDb, refspecs=refspecs)
    }
    fun setCredentialCbForRemoteCallbacks(remoteCallbacks: Remote.Callbacks, credentialType: Int, credentialEntity: CredentialEntity) {
        if(credentialEntity.maybeIsValid()) {  
            remoteCallbacks.setCredAcquireCb(getCredentialCb(credentialType, credentialEntity))
        }else {  
            MyLog.w(TAG, "#setCredentialCbForRemoteCallbacks(): call method with empty username/privatekey and password/passphrase")
        }
    }
    fun setAllowUnknownHostsForCertificatesCheck(remoteCallbacks: Remote.Callbacks) {
        remoteCallbacks.setCertificateCheckCb { cert, valid, hostname ->
            0
        }
    }
    suspend fun getRemoteCredential(remoteDb:RemoteRepository, credentialDb:CredentialRepository, repoId:String, remoteName:String, trueFetchFalsePush:Boolean, masterPassword: String = AppModel.masterPassword.value):CredentialEntity? {
        val remote = remoteDb.getByRepoIdAndRemoteName(repoId, remoteName)
        val credentialId = if(trueFetchFalsePush) {remote?.credentialId ?:""} else {remote?.pushCredentialId ?:""}
        val remoteUrl = if(trueFetchFalsePush) {remote?.remoteUrl ?: ""} else {remote?.pushUrl ?: ""}
        if(remote == null || credentialId.isBlank()) {
            return null
        }
        val credential = credentialDb.getByIdWithDecryptAndMatchByDomain(id = credentialId, url = remoteUrl, masterPassword = masterPassword)
        return credential
    }
    fun mergeOneHead(repo: Repository, targetRefName: String, username: String, email: String, requireMergeByRevspec:Boolean=false, revspec: String="", settings: AppSettings):Ret<Oid?> {
        return mergeOrRebase(repo, targetRefName, username, email, requireMergeByRevspec, revspec, trueMergeFalseRebase = true, settings = settings)
    }
    private fun startRebase(
        repo: Repository,
        theirHeads: List<AnnotatedCommit>,
        username: String,
        email: String,
        settings: AppSettings
    ):Ret<Oid?> {
        val funName = "startRebase"
        val state = repo.state()
        if(state == null || (state != Repository.StateT.NONE)) {
            return Ret.createError(null, "repo state is not 'NONE'", Ret.ErrCode.rebaseFailedByRepoStateIsNotNone)
        }
        val analysisResult = Merge.analysis(repo, theirHeads)  
        val analySet = analysisResult.analysisSet
        val preferenceSet = analysisResult.preferenceSet
        if (analySet.contains(Merge.AnalysisT.UP_TO_DATE)) {
            return Ret.createSuccess(null, "Already up-to-date", Ret.SuccessCode.upToDate)
        }
        if (analySet.contains(Merge.AnalysisT.UNBORN)  
            || (analySet.contains(Merge.AnalysisT.FASTFORWARD)
                    && !preferenceSet.contains(Merge.PreferenceT.NO_FASTFORWARD)
                    )
        ) {  
            if (theirHeads.size != 1) {
                return Ret.createError(null, "theirHeads.size!=1 when do fast-forward", Ret.ErrCode.fastforwardTooManyHeads)
            }
            val targetId = theirHeads[0].id()
            return doFastForward(repo, targetId, analySet.contains(Merge.AnalysisT.UNBORN), caller = "rebase")
        }
        if(analySet.contains(Merge.AnalysisT.NORMAL)){
            if(preferenceSet.contains(Merge.PreferenceT.FASTFORWARD_ONLY)) {
                return Ret.createError(null, "config is fast-forward only, but need merge", Ret.ErrCode.mergeFailedByConfigIsFfOnlyButCantFfMustMerge)
            }
            val head = resolveHEAD(repo)
            if(head == null) {
                return Ret.createError(null, "HEAD is null")
            }
            val headDetachedBeforeRebase = repo.headDetached()
            if(headDetachedBeforeRebase && head.id()==null) {
                return Ret.createError(null, "HEAD's oid is null")
            }
            val srcAnnotatedCommit = if(headDetachedBeforeRebase) AnnotatedCommit.lookup(repo, head.id()!!) else AnnotatedCommit.fromRef(repo, head)
            val rebaseOptions: Rebase.Options = Rebase.Options.createDefault()
            initRebaseOptions(rebaseOptions)
            val onto = null 
            val rebase = Rebase.init(repo, srcAnnotatedCommit, theirHeads[0], onto, rebaseOptions)
            val allOpCount = rebase.operationEntrycount()
            val rebaseCommiter = Libgit2Helper.createSignature(username, email, settings)
            val originCommitAuthor:Signature? = null 
            val originCommitMsgEncoding:Charset? = null 
            val originCommitMsg:String? = null 
            for(i in 0 ..< allOpCount) {
                rebase.next()
                if(!repo.index().hasConflicts()) {  
                    rebase.commit(originCommitAuthor, rebaseCommiter, originCommitMsgEncoding, originCommitMsg)
                }else {  
                    return Ret.createError(null, "rebase:has conflicts", Ret.ErrCode.mergeFailedByAfterMergeHasConfilts)  
                }
            }
            rebase.finish(rebaseCommiter)
            val headId = repo.head()?.id() ?: return Ret.createError(null, "rebase:get new oid err after finish rebase")
            return Ret.createSuccess(headId)
        }
        MyLog.e(TAG, "#$funName:rebase failed, unknown analysis set of repo: repo state before rebase=`$state` analysis set=`$analySet`, preference set=`$preferenceSet`, codepos=12614088")
        return Ret.createError(null, "rebase err: unknown analysis set: $analySet")
    }
    private fun initRebaseOptions(rebaseOptions: Rebase.Options) {
        val mergeOpts = rebaseOptions.mergeOptions
        mergeOpts.flags = 0  
        mergeOpts.fileFlags = Merge.FileFlagT.STYLE_DIFF3.bit 
        val checkoutOpts = rebaseOptions.checkoutOptions
        checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.SAFE, Checkout.StrategyT.ALLOW_CONFLICTS)
    }
    fun mergeOrRebase(repo: Repository, targetRefName: String, username: String, email: String, requireMergeByRevspec:Boolean=false, revspec: String="", trueMergeFalseRebase:Boolean=true, settings: AppSettings):Ret<Oid?> {
        val funName = "mergeOrRebase"
        MyLog.d(TAG, "#mergeOneHead(): targetRefName=$targetRefName, revspec=$revspec, requireMergeByRevspec=$requireMergeByRevspec")
        if(username.isBlank() || email.isBlank()) {
            MyLog.w(TAG, "#$funName(): can't start merge, because `username` or `email` is blank!, username.isBlank()=${username.isBlank()}, email.isBlank()=${email.isBlank()}")
            return Ret.createError(null, "username or email is blank!",  Ret.ErrCode.usernameOrEmailIsBlank)
        }
        val targetAnnotatedCommit = if(requireMergeByRevspec) {
            AnnotatedCommit.fromRevspec(repo, revspec) 
        }else{
            val targetRef = resolveRefByName(repo, targetRefName)  
            MyLog.d(TAG, "#$funName(): targetRef==null: "+(targetRef==null)+", targetRefName: "+targetRefName)
            if(targetRef==null) {
                return Ret.createError(null, "resolve targetRefName '$targetRefName' to Reference failed!", Ret.ErrCode.resolveReferenceError)
            }
            AnnotatedCommit.fromRef(repo, targetRef)  
        }
        val parents = mutableListOf<AnnotatedCommit>()
        parents.add(targetAnnotatedCommit)
        return if(trueMergeFalseRebase) {
            mergeManyHeads(repo, parents, username, email, settings=settings)
        }else{
            startRebase(repo, parents, username, email, settings)
        }
    }
    private fun mergeManyHeads(
        repo: Repository,
        theirHeads: List<AnnotatedCommit>,
        username: String,
        email: String,
        settings: AppSettings
    ):Ret<Oid?> {
        val state = repo.state()
        if(state != Repository.StateT.NONE) {
            if(state == Repository.StateT.MERGE && getMergeHeads(repo).isEmpty() && !hasConflictItemInRepo(repo) && getIndexStatusList(repo).entryCount()==0) {
                MyLog.w(TAG, "#mergeManyHeads(): repo state is 'MERGE' , but no conflict items and Index is empty! maybe is wrong state, will clean repo state, old state is: '$state', new state will be 'NONE'")
                cleanRepoState(repo)
            }else {
                MyLog.w(TAG, "#mergeManyHeads(): merge failed, repo state is: '$state', expect 'NONE'")
                return Ret.createError(null, "merge failed! repo state is: '$state', expect 'NONE'", Ret.ErrCode.mergeFailedByRepoStateIsNotNone)
            }
        }
        val analysisResult = Merge.analysis(repo, theirHeads)  
        val analySet = analysisResult.analysisSet
        val preferenceSet = analysisResult.preferenceSet
        if (analySet.contains(Merge.AnalysisT.UP_TO_DATE)) {
            return Ret.createSuccess(null, "Already up-to-date", Ret.SuccessCode.upToDate)
        }
        if (analySet.contains(Merge.AnalysisT.UNBORN)  
            || (analySet.contains(Merge.AnalysisT.FASTFORWARD)
                    && !preferenceSet.contains(Merge.PreferenceT.NO_FASTFORWARD)
                    )
        ) {  
            if (theirHeads.size != 1) {
                return Ret.createError(null, "theirHeads.size!=1 when do fast-forward", Ret.ErrCode.fastforwardTooManyHeads)
            }
            val targetId = theirHeads[0].id()
            return doFastForward(repo, targetId, analySet.contains(Merge.AnalysisT.UNBORN), caller = "merge")
        }
        if(analySet.contains(Merge.AnalysisT.NORMAL)) {  
            if(preferenceSet.contains(Merge.PreferenceT.FASTFORWARD_ONLY)) {
                return Ret.createError(null, "config is fast-forward only, but need merge", Ret.ErrCode.mergeFailedByConfigIsFfOnlyButCantFfMustMerge)
            }
            val mergeOpts = Merge.Options.create()
            mergeOpts.flags = 0  
            mergeOpts.fileFlags = Merge.FileFlagT.STYLE_DIFF3.bit 
            val checkoutOpts = Checkout.Options.defaultOptions()
            checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.SAFE, Checkout.StrategyT.ALLOW_CONFLICTS)
            Merge.merge(repo, theirHeads, mergeOpts, checkoutOpts)
        }
        if(hasConflictItemInRepo(repo)) {
            return Ret.createError(null, "merge failed: has conflicts", Ret.ErrCode.mergeFailedByAfterMergeHasConfilts)  
        }else {  
            val headName = Cons.gitHeadStr
            val parents = mutableListOf<Commit>()
            val headRef = resolveRefByName(repo, headName)
            if(headRef==null) {
                return Ret.createError(null, "resolve HEAD error!", Ret.ErrCode.headIsNull)
            }
            val headCommit = resolveCommitByRef(repo, headName)
            if (headCommit == null) {
                return Ret.createError(null, "get current HEAD latest commit failed", Ret.ErrCode.mergeFailedByGetRepoHeadCommitFaild)
            }
            parents.add(headCommit)
            val branchNames = StringBuilder()
            val suffix = ", "
            for(ac in theirHeads) {
                val c = Commit.lookup(repo, ac.id())
                val ref = ac.ref()?:""  
                var branchNameOrRefShortHash = getShortRefSpecByRefsHeadsRefSpec(ref)  
                if(branchNameOrRefShortHash == null || branchNameOrRefShortHash.isBlank()) {  
                    branchNameOrRefShortHash = getShortRefSpecByRefsRemotesRefSpec(ref)  
                    if(branchNameOrRefShortHash == null || branchNameOrRefShortHash.isBlank()) {  
                        branchNameOrRefShortHash = c.shortId().toString()  
                    }
                }
                branchNames.append(branchNameOrRefShortHash).append(suffix)  
                parents.add(c)
            }
            val msg = "Merge '${branchNames.removeSuffix(suffix)}' into '${headRef.shorthand()}'"
            val branchFullRefName: String = headRef.name()
            val commitResult = createCommit(
                repo = repo,
                msg = msg,
                username = username,
                email = email,
                branchFullRefName = branchFullRefName,
                indexItemList = null,
                parents = parents,
                settings = settings,
                cleanRepoStateIfSuccess = true
            )
            if(commitResult.hasError()) {
                return Ret.createError(null, "merge failed: "+commitResult.msg, Ret.ErrCode.mergeFailedByCreateCommitFaild)
            }
            return Ret.createSuccess(commitResult.data, "merge success, new commit oid: "+commitResult.data.toString())
        }
    }
    private fun doFastForward(repo:Repository, targetOid: Oid, isUnborn:Boolean, caller:String):Ret<Oid?> {
        val targetRef = if(isUnborn) { 
            val headRef = Reference.lookup(repo, "HEAD")
            val symbolicRef = headRef?.symbolicTarget()?:throw RuntimeException("doFastForward() failed by headRef.symbolicTarget() return null")
            Reference.create(repo, symbolicRef, targetOid, false, "born HEAD when fast-forward")
        } else {
            repo.head()
        }
        if(targetRef == null) {
            return Ret.createError(null,  "targetRef is null", Ret.ErrCode.targetRefNotFound)
        }
        val targetCommit = resolveGitObject(repo, targetOid, GitObject.Type.COMMIT)
        if(targetCommit == null) {
            return Ret.createError(null, "targetCommit is null", Ret.ErrCode.targetCommitNotFound)
        }
        val fastForwardCheckoutOpts = Checkout.Options.defaultOptions()
        fastForwardCheckoutOpts.strategy = EnumSet.of(Checkout.StrategyT.SAFE)
        Checkout.tree(repo, targetCommit, fastForwardCheckoutOpts)
        val newTargetRef = targetRef.setTarget(targetOid, "$caller: ${targetRef.name()} fast-forward to $targetOid")
        return if(newTargetRef != null) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "doFastForward: fast-forward success")
            }
            Ret.createSuccess(newTargetRef.id(), "fast-forward success", successCode = Ret.SuccessCode.fastForwardSuccess)
        }else {
            Ret.createError(null, "newTargetRef is null", Ret.ErrCode.newTargetRefIsNull)
        }
    }
    suspend fun updateDbAfterMergeSuccess(mergeResult:Ret<Oid?>, appContext:Context, repoId:String, msgNotifier:(String)->Unit, trueMergeFalseRebase:Boolean) {
        if(mergeResult.code == Ret.SuccessCode.upToDate) {  
            msgNotifier(appContext.getString(R.string.already_up_to_date))
        }else {  
            val repoDB = AppModel.dbContainer.repoRepository
            val shortNewCommitHash = mergeResult.data.toString().substring(Cons.gitShortCommitHashRange)
            repoDB.updateCommitHash(
                repoId=repoId,
                lastCommitHash = shortNewCommitHash,
            )
            val fastforwardPrefix = if(mergeResult.code == Ret.SuccessCode.fastForwardSuccess) "(FF) " else ""
            msgNotifier(fastforwardPrefix + appContext.getString(if(trueMergeFalseRebase) R.string.merge_success else R.string.rebase_success))
        }
    }
    fun getShortRefSpecByRefsHeadsRefSpec(refspec:String):String? {
        return removeGitRefSpecPrefix("refs/heads/", refspec)
    }
    fun removeRemoteBranchShortRefSpecPrefixByRemoteName(remoteName: String, refspec:String):String? {
        return removeGitRefSpecPrefix(remoteName, refspec)
    }
    @Deprecated("[CHINESE]，[CHINESE] `removeRemoteBranchShortRefSpecPrefixByRemoteName()`")
    fun removeRemoteBranchShortRefSpecPrefixBySeparator(refspec:String):String? {
        return removeGitRefSpecPrefix("/",refspec)  
    }
    fun getShortRefSpecByRefsRemotesRefSpec(refspec:String):String? {
        return removeGitRefSpecPrefix("refs/remotes/", refspec)
    }
    fun removeGitRefSpecPrefix(prefix: String, refspec:String):String? {
        if(refspec.isBlank()) {
            return null
        }
        val indexOf = refspec.indexOf(prefix)
        if(indexOf == -1) {  
            return null
        }
        return refspec.substring(indexOf+prefix.length)
    }
    fun push(repo: Repository, remoteName: String, refspecs: List<String>, credential: CredentialEntity?, force: Boolean) {
        if(refspecs.isEmpty()) {
            throw RuntimeException("refspecs are empty")
        }
        val refspecs:List<String> = if(force) {
            refspecs.map { if(it.startsWith("+")) it else "+$it" }
        }else {
            refspecs
        }
        val pushOptions = PushOptions.createDefault()
        val callbacks = pushOptions.callbacks!!
        val remote = resolveRemote(repo, remoteName)
        if(remote == null) {
            throw RuntimeException("resolve remote failed!")
        }
        val pushUrl = getRemoteActuallyUsedPushUrl(remote)
        if(credential != null) {
            setCredentialCbForRemoteCallbacks(callbacks, getCredentialTypeByUrl(pushUrl), credential)
        }
        setCertCheckCallback(pushUrl, callbacks, repo)
        MyLog.d(TAG, "#push(): remoteName=$remoteName, refspecs=$refspecs")
        remote.push(refspecs, pushOptions)
    }
    fun pushMulti(repo: Repository, remotes: List<RemoteAndCredentials>, refspecs: List<String>):List<PushFailedItem> {
        val funName = "pushMulti"
        val pushFailedList = mutableListOf<PushFailedItem>()
        for (rc in remotes) {
            try {
                push(repo, rc.remoteName, refspecs, rc.pushCredential, rc.forcePush)
            }catch (e:Exception) {
                pushFailedList.add(PushFailedItem(rc.remoteName, e))
            }
        }
        return pushFailedList
    }
    fun isSshUrl(url:String):Boolean {
        return getGitUrlType(url) == Cons.gitUrlTypeSsh
    }
    fun isHttpUrl(url:String):Boolean {
        return getGitUrlType(url) == Cons.gitUrlTypeHttp
    }
    fun getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(remote:String, branchFullRefSpec:String) :String {
        if(remote.isBlank() || branchFullRefSpec.isBlank()) {
            return ""
        }
        return remote + "/" + getShortRefSpecByRefsHeadsRefSpec(branchFullRefSpec)
    }
    fun unStageItems(repo: Repository, pathSpecList: List<String>) {
        val head = repo.head()
        val headCommit = head?.peel(GitObject.Type.COMMIT)
        headCommit?.let { Reset.resetDefault(repo, it, pathSpecList.toTypedArray()) }
    }
    fun getBranchList(repo:Repository, branchType:Branch.BranchType=defaultBranchTypeForList, excludeRemoteHead:Boolean=false):List<BranchNameAndTypeDto> {
        val isDetached = repo.headDetached()
        var head:Reference?=null
        if(!isDetached) {
            head = repo.head()
        }
        val it = Branch.Iterator.create(repo, branchType)
        val list = mutableListOf<BranchNameAndTypeDto>()
        var b = it.next()
        while(b!=null) {
            if(excludeRemoteHead) {  
                if(b.value == Branch.BranchType.REMOTE
                    && b.key.name().endsWith("/HEAD")  
                    && b.key.id()==null  
                    && b.key.symbolicTarget()!=null
                ) {
                    b = it.next()  
                    continue
                }
            }
            val bnat = BranchNameAndTypeDto()
            bnat.fullName = b.key.name()
            bnat.shortName = b.key.shorthand()
            bnat.type = b.value
            bnat.isSymbolic = b.key.symbolicTarget()!=null
            if(bnat.isSymbolic) {  
                bnat.symbolicTargetFullName = b.key.symbolicTarget() ?: ""
                bnat.symbolicTargetShortName = getBranchShortNameByFull(bnat.symbolicTargetFullName)
            }
            try {  
                bnat.oidStr  = b.key.peel(GitObject.Type.COMMIT)?.id()?.toString()?:throw RuntimeException("resolve branch to direct ref(commit) failed, branch is: ${bnat.fullName}") 
            }catch (e:Exception) {
                MyLog.e(TAG, "#getBranchList() err: ${e.stackTraceToString()}")
                b=it.next()  
                continue;  
            }
            bnat.shortOidStr = getShortOidStrByFull(bnat.oidStr)
            if(b.value == Branch.BranchType.LOCAL) {
                val upstream = getUpstreamOfBranch(repo, bnat.shortName)
                bnat.upstream = upstream
                if(upstream.remoteOid.isNotBlank() && upstream.localOid.isNotBlank()) {
                    val (ahead, behind) = getAheadBehind(repo, Oid.of(upstream.localOid), Oid.of(upstream.remoteOid))
                    bnat.ahead = ahead
                    bnat.behind = behind
                }
            }else if(b.value == Branch.BranchType.REMOTE) {  
                bnat.remotePrefixFromShortName = resolveBranchRemotePrefix(repo, bnat.fullName)
            }
            bnat.isCurrent = !isDetached && head!=null && head.name() == bnat.fullName
            list.add(bnat)
            b = it.next()
        }
        return list
    }
    fun getBranchShortNameByFull(fullName: String): String {
        if(fullName.isBlank()) {
            return ""
        }
        val refsHeadsPrefix = "refs/heads/"
        val refHeadsIdx = fullName.indexOf(refsHeadsPrefix)
        if(refHeadsIdx != -1) {  
            return fullName.substring(refHeadsIdx+refsHeadsPrefix.length)
        }else {  
            val refsRemotesPrefix = "refs/remotes/"
            val refRemoteIdx = fullName.indexOf(refsRemotesPrefix)
            if(refRemoteIdx==-1) {  
                return ""
            }
            return fullName.substring(refRemoteIdx+refsRemotesPrefix.length)
        }
    }
    fun getRepoNameOnBranch(repoName:String, branchName: String):String {
        return "$repoName on $branchName"
    }
    fun getBranchNameOfRepoName(repoName:String, branchName: String):String {
        return "$branchName of $repoName"
    }
    fun getShortOidStrByFull(oidStr:String):String{
        if(oidStr.length > Cons.gitShortCommitHashRange.endInclusive) {
            return oidStr.substring(Cons.gitShortCommitHashRange)
        }
        return oidStr
    }
    fun getShortOidStrByFullIfIsHash(oidStr:String):String {
        return if(maybeIsHash(oidStr)) {
            getShortOidStrByFull(oidStr)
        }else {
            oidStr
        }
    }
    fun splitRemoteAndBranchFromRemoteShortRefName(shortRefName:String):Pair<String,String> {
        val indexOf = shortRefName.indexOf("/")
        val remote = shortRefName.substring(0, indexOf)
        val branch = shortRefName.substring(indexOf+1, shortRefName.length)
        return Pair(remote, branch)
    }
    fun isBranchNameAlreadyExists(repo:Repository ,branchName:String):Boolean {  
        return resolveRefByName(repo, branchName) != null
    }
    fun createLocalBranchBasedHead(repo: Repository, branchName:String, overwriteIfExisted: Boolean):Ret<Triple<String, String, String>?> {
        val head = repo.head()
        if(head==null) {
            return Ret.createError(null, "HEAD is null",Ret.ErrCode.headIsNull)
        }
        val currentHeadLatestCommitHash = head.id().toString()
        return createLocalBranchBasedOidStr(repo, branchName, currentHeadLatestCommitHash, overwriteIfExisted)
    }
    fun createLocalBranchBasedOidStr(repo: Repository, branchName:String, oidStr:String, overwriteIfExisted: Boolean):Ret<Triple<String, String, String>?> {
        val commit = resolveCommitByHash(repo, oidStr)
        if(commit == null) {
            return Ret.createError(null, "resolve commit error!",Ret.ErrCode.resolveCommitErr)
        }
        val newBranch = Branch.create(
            repo,
            branchName,
            commit,
            overwriteIfExisted
        )
        return Ret.createSuccess(Triple(newBranch.name(), newBranch.shorthand(), commit.id().toString()))
    }
    fun checkoutLocalBranchThenUpdateHead(repo: Repository, branchName: String, force:Boolean=false, updateHead:Boolean=true):Ret<Oid?> {
        return checkoutBranchThenUpdateHead(repo, branchName,force,detachHead = false, updateHead=updateHead)
    }
    fun checkoutRemoteBranchThenDetachHead(repo: Repository, branchName: String, force:Boolean=false, updateHead:Boolean=true):Ret<Oid?> {
        return checkoutBranchThenUpdateHead(repo, branchName,force,detachHead = true, updateHead=updateHead)
    }
    fun checkoutCommitThenDetachHead(repo: Repository, commitHash:String, force:Boolean=false, updateHead:Boolean=true):Ret<Oid?> {
        val checkRet = checkoutByHash(repo, commitHash, force)
        if(checkRet.hasError()) {
            return checkRet
        }
        if(updateHead) {
            val targetCommitOid = checkRet.data ?: return Ret.createError(null,"checkout success but detach head failed, because new commit id is invalid",  Ret.ErrCode.checkoutSuccessButDetacheHeadFailedByNewCommitInvalid)
            repo.setHeadDetached(targetCommitOid)
        }
        return checkRet
    }
    private fun checkoutBranchThenUpdateHead(repo: Repository, branchName: String, force:Boolean=false, detachHead:Boolean, updateHead:Boolean=true):Ret<Oid?> {
        val ref = resolveRefByName(repo, branchName)
        if(ref==null) {
            return Ret.createError(null,"ref is null",Ret.ErrCode.refIsNull)
        }
        val checkoutRet = checkoutByHash(repo, ref.peel(GitObject.Type.COMMIT).id().toString(), force)
        if(checkoutRet.hasError()) {
            return checkoutRet
        }
        if(updateHead) {
            if(detachHead) { 
                val ac = AnnotatedCommit.fromRef(repo, ref)  
                repo.setHeadDetachedFromAnnotated(ac)  
            }else {  
                repo.setHead(ref.name())
            }
        }
        return checkoutRet
    }
    private fun checkoutByHash(repo: Repository, commitHash:String, force:Boolean=false):Ret<Oid?> {
        val state = repo.state()
        if(state != Repository.StateT.NONE) {
            MyLog.d(TAG, "#checkoutByHash: repo state is not NONE, it is: '$state'")
            return Ret.createError(null, "repo state is not NONE", Ret.ErrCode.repoStateIsNotNone)
        }
        val ckOpts = Checkout.Options.defaultOptions()
        val strategy = if (force) EnumSet.of(Checkout.StrategyT.FORCE) else EnumSet.of(Checkout.StrategyT.SAFE)
        ckOpts.strategy = strategy
        MyLog.w(TAG, "#checkoutByHash: will checkout commit '$commitHash' with strategy '$strategy'")
        val targetCommit = resolveCommitByHash(repo, commitHash)
        if(targetCommit==null) {
            MyLog.d(TAG, "#checkoutByHash: target commit not found, hash is: "+commitHash)
            return Ret.createError(null,"target commit not found!",Ret.ErrCode.targetCommitNotFound)
        }
        val errno = Checkout.tree(repo, targetCommit, ckOpts)
        if(errno < 0) {
            MyLog.d(TAG, "#checkoutByHash: Checkout.tree() err, errno="+errno)
            return Ret.createError(null, "Checkout Tree err(errno=$errno)!", Ret.ErrCode.checkoutTreeError)
        }
        return Ret.createSuccess(targetCommit.id())
    }
    fun resolveHEAD(repo:Repository):Reference? {
        return resolveRefByName(repo, "HEAD")
    }
    fun resolveRefByName(repo:Repository, refNameShortOrFull:String, trueUseDwimFalseUseLookup:Boolean=true):Reference? {
        try {
            MyLog.v(TAG, "#resolveRefByName(refNameShortOrFull=$refNameShortOrFull, trueUseDwimFalseUseLookup=$trueUseDwimFalseUseLookup)")
            val ref = if(trueUseDwimFalseUseLookup) Reference.dwim(repo, refNameShortOrFull) else Reference.lookup(repo, refNameShortOrFull)
            return ref?.resolve()  
        }catch (e:Exception) {
            MyLog.d(TAG, "#resolveRefByName(): resolve refname err! refname="+refNameShortOrFull+", trueUseDwimFalseUseLookup=$trueUseDwimFalseUseLookup, err is: "+e.stackTraceToString())
            return null
        }
    }
    fun resolveRefByName2(
        repo:Repository,
        refNameShortOrFull:String,
        trueUseDwimFalseUseLookup:Boolean=true,
        tryResolveRefToDirect:Boolean=true
    ):Ret<Reference?> {
        try {
            val ref = if(trueUseDwimFalseUseLookup) Reference.dwim(repo, refNameShortOrFull) else Reference.lookup(repo, refNameShortOrFull)
            return Ret.createSuccess(if(tryResolveRefToDirect) ref?.resolve() else ref)  
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage ?:"err", exception = e)
        }
    }
    fun resolveGitObject(repo:Repository, targetOid:Oid, type:GitObject.Type):GitObject? {
        try {
            val gitObj = GitObject.lookup(repo, targetOid, type)
            return gitObj
        }catch (e:Exception) {
            MyLog.e(TAG, "#resolveGitObject(): resolve GitObject err! targetOid=$targetOid, type=${type.name} \n Exception is: "+e.stackTraceToString())
            return null
        }
    }
    fun resolveCommitByHash(repo: Repository, shortOrLongHash:String):Commit? {
        try {
            val resolved =  Commit.lookupPrefix(repo, shortOrLongHash)
            return resolved
        }catch (e:Exception) {
            MyLog.d(TAG, "#resolveCommitByHash() error, param is (shortOrLongHash=$shortOrLongHash):\nerr is: "+e.stackTraceToString())
            return null
        }
    }
    fun resolveBranchRemotePrefix(repo: Repository, fullRemoteBranchRefSpec:String):String {
        try {
            val remoteName = Branch.remoteName(repo, fullRemoteBranchRefSpec) ?: ""  
            MyLog.d(TAG, "#resolveBranchRemotePrefix: in: fullRemoteBranchRefSpec=$fullRemoteBranchRefSpec; out: remoteName=$remoteName")
            return remoteName
        }catch (e:Exception) {
            MyLog.e(TAG, "#resolveBranchRemotePrefix() error, param is (fullRemoteBranchRefSpec=$fullRemoteBranchRefSpec) :\nerr is: "+e.stackTraceToString())
            return ""
        }
    }
    fun resolveRemote(repo: Repository, remoteName:String):Remote? {
        try {
            val remoteObj = Remote.lookup(repo, remoteName)
            return remoteObj
        }catch (e:Exception) {
            MyLog.e(TAG, "#resolveRemote() error, param is (remoteName=$remoteName):\nerr is: "+e.stackTraceToString())
            return null
        }
    }
    fun getRemoteFetchBranchList(remote:Remote): Pair<Boolean, List<String>> {
        val isNotAll = false
        try {
            val list = remote.fetchRefspecs
            if(list==null || list.isEmpty()) {
                return Pair(isNotAll, emptyList())
            }
            val branchNameList = mutableListOf<String>()
            list.forEachBetter forEach@{
                val prefixStr = "refs/heads/"  
    suspend fun getFileHistoryList(
        repo: Repository,
        revwalk: Revwalk,
        initNext:Oid?,
        repoId: String,
        pageSize:Int,
        retList: MutableList<FileHistoryDto>,
        loadChannel:Channel<Int>,
        checkChannelFrequency:Int,
        lastVersionEntryOid:String?,
        fileRelativePathUnderRepo:String, 
        settings: AppSettings
    ): Pair<String?, Oid?> {
        if(initNext == null || initNext.isNullOrEmptyOrZero) {
            return Pair(lastVersionEntryOid, initNext)
        }
        var lastCommit:Commit? = null
        var lastLastCommit:Commit? = null
        var lastLastEntryOidStr:String? = null
        var lastVersionEntryOid = lastVersionEntryOid
        var next = initNext
        var count = 0
        var checkChannelCount = 0
        val repoWorkDirPath = getRepoWorkdirNoEndsWithSlash(repo)
        val commitList = mutableListOf<String>()
        while (next!=null) {
            try {
                if(++checkChannelCount > checkChannelFrequency) {
                    delay(1)
                    val recv = loadChannel.tryReceive()
                    if(recv.isClosed){  
                        MyLog.d(TAG, "#getFileHistoryList: abort by terminate signal")
                        break
                    }else {
                        checkChannelCount = 0
                    }
                }
                val nextStr = next.toString()
                val commit = resolveCommitByHash(repo, nextStr)
                if(commit!=null) {
                    val tree = commit.tree()
                    if(tree != null) {
                        val entry =getEntryOrNullByPathOrName(tree, fileRelativePathUnderRepo, byName = false)
                        if(entry!=null) {
                            val entryOid = entry.id()
                            if(!entryOid.isNullOrEmptyOrZero) {
                                val entryOidStr = entryOid.toString()
                                if(entryOidStr != lastVersionEntryOid) {
                                    if(AppModel.devModeOn) {
                                        if((lastCommit==null && lastVersionEntryOid!=null)
                                            || (lastCommit!=null && lastVersionEntryOid==null)
                                        ) {
                                            throw RuntimeException("#getFileHistoryList() err: Wrong State: lastCommit=${lastCommit?.id().toString()}, lastVersionEntryOid=$lastVersionEntryOid")
                                        }
                                    }
                                    if(lastCommit != null) {
                                        lastLastCommit = lastCommit
                                    }
                                    if(lastVersionEntryOid != null) {
                                        lastLastEntryOidStr = lastVersionEntryOid
                                    }
                                    lastVersionEntryOid = entryOidStr
                                    lastCommit = commit
                                    if(lastLastCommit != null && lastLastEntryOidStr != null) {
                                        retList.add(createFileHistoryDto(
                                            repoWorkDirPath = repoWorkDirPath,
                                            commitOidStr= lastLastCommit.id().toString(),
                                            treeEntryOidStr= lastLastEntryOidStr.toString(),
                                            commit=lastLastCommit,
                                            repoId=repoId,
                                            fileRelativePathUnderRepo=fileRelativePathUnderRepo,
                                            settings = settings,
                                            commitList = commitList,
                                        ))
                                        if(++count >= pageSize) {
                                            break
                                        }
                                        commitList.clear()
                                    }
                                }else {
                                    lastCommit = commit
                                }
                                commitList.add(commit.id().toString())
                            }
                        }
                    }
                }
                next = revwalk.next()
            }catch (e:Exception) {
                throw e
            }
        }
        if(next == null) {
            if(lastCommit != null && lastVersionEntryOid != null) {
                retList.add(createFileHistoryDto(
                    repoWorkDirPath = repoWorkDirPath,
                    commitOidStr= lastCommit.id().toString(),
                    treeEntryOidStr= lastVersionEntryOid.toString(),
                    commit=lastCommit,
                    repoId=repoId,
                    fileRelativePathUnderRepo=fileRelativePathUnderRepo,
                    settings = settings,
                    commitList = commitList
                ))
            }
        }
        return Pair(lastVersionEntryOid, next)
    }
    fun getEntryOrNullByPathOrName(tree:Tree, path:String, byName:Boolean):Tree.Entry? {
        if(path.isEmpty()) {
            return null
        }
        try {
            val entry =  if(byName) tree.entryByName(path) else tree.entryByPath(path)
            return entry
        }catch (e:Exception) {
            MyLog.e(TAG, "#getEntryOrNullByPathOrName err: path=$path, byName=$byName, err=${e.stackTraceToString()}")
            return null
        }
    }
    fun getCommitParentsOidStrList(repo: Repository, commitOidStr:String):List<String> {
        val parentList = mutableListOf<String>()
        val commit = resolveCommitByHash(repo, commitOidStr)
        if(commit == null) {
            return parentList
        }
        val parentCount = commit.parentCount()
        if(parentCount>0) {
            var pc = 0
            while (pc < parentCount) {
                val parent = commit.parentId(pc).toString()
                parentList.add(parent)
                pc++
            }
        }
        return parentList
    }
    fun getDateTimeStrOfCommit(commit: Commit, settings: AppSettings):String {
        val time = commit.time()
        val minuteOffset = readTimeZoneOffsetInMinutesFromSettingsOrDefault(settings, commit.timeOffset())
        val secOffset = minuteOffset * 60  
        val formattedTimeStr = time.atOffset(ZoneOffset.ofTotalSeconds(secOffset)).format(Cons.defaultDateTimeFormatter)
        return formattedTimeStr
    }
    fun getRepoOnBranchOrOnDetachedHash(repo:RepoEntity):String {
        return if(dbIntToBool(repo.isDetached)) "[${repo.repoName} on ${repo.lastCommitHashShort}(Detached)]" else "[${repo.repoName} on ${repo.branch}]"
    }
    suspend fun doCheckoutBranchThenUpdateDb(
        repo:Repository,
        repoId:String,
        shortBranchNameOrShortHash:String,
        fullBranchNameOrFullHash:String,
        upstreamBranchShortNameParam:String,
        checkoutType:Int,
        force:Boolean=false,
        updateHead:Boolean=true
    ):Ret<Oid?> {
        val checkoutRet = if(checkoutType==Cons.checkoutType_checkoutRefThenUpdateHead) { 
            checkoutLocalBranchThenUpdateHead(repo, fullBranchNameOrFullHash, force, updateHead)
        }else if(checkoutType==Cons.checkoutType_checkoutRefThenDetachHead){  
            checkoutRemoteBranchThenDetachHead(repo, fullBranchNameOrFullHash, force, updateHead)
        }else {  
            checkoutCommitThenDetachHead(repo, fullBranchNameOrFullHash, force, updateHead)
        }
        if(checkoutRet.hasError()) {
            return checkoutRet
        }
        if(updateHead) {
            val repoDb = AppModel.dbContainer.repoRepository
            val lastCommitHash = getShortOidStrByFull(checkoutRet.data.toString())  
            if(checkoutType == Cons.checkoutType_checkoutRefThenUpdateHead) { 
                repoDb.updateBranchAndCommitHash(
                    repoId = repoId,
                    branch = shortBranchNameOrShortHash,
                    lastCommitHash=lastCommitHash,
                    isDetached = Cons.dbCommonFalse,  
                    upstreamBranch = upstreamBranchShortNameParam  
                )
            }else {  
                repoDb.updateBranchAndCommitHash(
                    repoId = repoId,
                    branch = lastCommitHash,  
                    lastCommitHash=lastCommitHash,
                    isDetached = Cons.dbCommonTrue,
                    upstreamBranch = upstreamBranchShortNameParam
                )
            }
        }
        return checkoutRet
    }
    fun doCreateBranch(
        activityContext:Context,
        repo: Repository,
        repoId: String,
        branchNameParam: String,  
        basedHead: Boolean,  
        baseRefSpec: String,  
        createByRef: Boolean,  
        overwriteIfExisted:Boolean,
        ):Ret<Triple<String, String, String>?> {
        val result = if(basedHead) {
            createLocalBranchBasedHead(repo, branchNameParam, overwriteIfExisted)  
        }else {  
            var refOidStr = baseRefSpec  
            if(createByRef) {  
                val baseRef = resolveRefByName(repo, baseRefSpec)
                if(baseRef == null) {
                    return Ret.createError(null,activityContext.getString(R.string.resolve_reference_failed), Ret.ErrCode.refIsNull)
                }
                refOidStr = baseRef.peel(GitObject.Type.COMMIT).id().toString()  
            }
            createLocalBranchBasedOidStr(repo, branchNameParam, refOidStr, overwriteIfExisted)
        }
        if(result.hasError()) {  
            if(result.code == Ret.ErrCode.headIsNull) {
                result.msg = activityContext.getString(R.string.resolve_repo_head_failed)
            }
            return result
        }
        return result
    }
    fun isRepoStatusErr(repoFromDb: RepoEntity):Boolean {
        return repoFromDb.workStatus >= Cons.dbCommonErrValStart
    }
    fun isRepoStatusNotReady(repoFromDb: RepoEntity):Boolean {
        return repoFromDb.workStatus==Cons.dbRepoWorkStatusNotReadyNeedClone || repoFromDb.workStatus==Cons.dbRepoWorkStatusNotReadyNeedInit
    }
    fun isRepoStatusNotReadyOrErr(repoFromDb: RepoEntity):Boolean {
        return isRepoStatusErr(repoFromDb) || isRepoStatusNotReady(repoFromDb)
    }
    fun isRepoStatusReady(repoFromDb: RepoEntity):Boolean {
        return !isRepoStatusNotReady(repoFromDb)
    }
    fun isRepoStatusNoErr(repoFromDb: RepoEntity):Boolean {
        return !isRepoStatusErr(repoFromDb)
    }
    suspend fun updateRepoInfo(
        repoFromDb: RepoEntity,
        requireQueryParentInfo:Boolean=true,
        settings: AppSettings = SettingsUtil.getSettingsSnapshot(),
    ) {
        val funName = "updateRepoInfo"
        try {
            if(isRepoStatusNotReadyOrErr(repoFromDb)) {
                return;
            }
            if(requireQueryParentInfo && repoFromDb.parentRepoId.isNotBlank()) {
                val repoDb = AppModel.dbContainer.repoRepository
                val parentRepo = repoDb.getByIdNoSyncWithGit(repoFromDb.parentRepoId)
                if(parentRepo!=null) {
                    repoFromDb.parentRepoValid = true
                    repoFromDb.parentRepoName = parentRepo.repoName
                }
            }
            Repository.open(repoFromDb.fullSavePath).use { repo ->
                repoFromDb.gitRepoState = repo.state()
                val head = resolveHEAD(repo)
                repoFromDb.branch = head?.shorthand()?:""
                val lastCommitHashFull = head?.id()?.toString() ?: ""  
                repoFromDb.lastCommitHash = lastCommitHashFull
                repoFromDb.updateLastCommitHashShort()
                repoFromDb.updateCommitDateTimeWithRepo(repo, settings)
                val headCommit = resolveCommitByHash(repo, lastCommitHashFull);
                if(headCommit != null) {
                    repoFromDb.latestCommitMsg = headCommit.message()
                    repoFromDb.getOrUpdateCachedOneLineLatestCommitMsg()
                }
                repoFromDb.isDetached = boolToDbInt(repo.headDetached())
                if(!dbIntToBool(repoFromDb.isDetached)) {  
                    repoFromDb.workStatus = Cons.dbRepoWorkStatusNeedSync
                    val upstream = getUpstreamOfBranch(repo, repoFromDb.branch)
                    repoFromDb.upstreamBranch = upstream.remoteBranchShortRefSpec
                    if(upstream.isPublished && upstream.remote.isNotBlank() && upstream.branchRefsHeadsFullRefSpec.isNotBlank()) {
                        val localOid = head?.id()
                        val upstreamOid = resolveRefByName(repo, upstream.remoteBranchRefsRemotesFullRefSpec)?.id()
                        if(localOid!=null && upstreamOid!=null) {
                            val (ahead, behind) = getAheadBehind(repo, localOid, upstreamOid)
                            repoFromDb.ahead = ahead
                            repoFromDb.behind = behind
                            if(repoFromDb.ahead == 0 && repoFromDb.behind == 0) {  
                                repoFromDb.workStatus = Cons.dbRepoWorkStatusUpToDate
                            }else if(repoFromDb.ahead == 0 && repoFromDb.behind > 0) {  
                                repoFromDb.workStatus = Cons.dbRepoWorkStatusNeedPull
                            }else if(repoFromDb.ahead > 0 && repoFromDb.behind == 0) {
                                repoFromDb.workStatus = Cons.dbRepoWorkStatusNeedPush
                            } 
                        }
                    }
                }
                repoFromDb.isShallow = boolToDbInt(isRepoShallow(repo))
                val repoState = repo.state()
                if(hasConflictItemInRepo(repo)) {  
                    repoFromDb.workStatus = Cons.dbRepoWorkStatusHasConflicts
                }else if(repoState == Repository.StateT.NONE) { 
                    repoFromDb.pendingTask = RepoPendingTask.NEED_CHECK_UNCOMMITED_CHANGES
                }else { 
                    if(repoState == Repository.StateT.MERGE) {  
                        repoFromDb.workStatus = Cons.dbRepoWorkStatusMerging
                    }else if(repoState==Repository.StateT.REBASE_MERGE) {
                        repoFromDb.workStatus = Cons.dbRepoWorkStatusRebasing
                    }else if(repoState==Repository.StateT.CHERRYPICK) {
                        repoFromDb.workStatus = Cons.dbRepoWorkStatusCherrypicking
                    }
                }
                if(head == null) {
                    repoFromDb.workStatus = Cons.dbRepoWorkStatusNoHEAD
                }
            }
            repoFromDb.tmpStatus = RepoStatusUtil.getRepoStatus(repoFromDb.id) ?: ""
        }catch (e:Exception) {
            MyLog.d(TAG, "#$funName() failed: repoId=${repoFromDb.id}, repo.toString()=${repoFromDb.toString()}\n Exception is: ${e.stackTraceToString()}")
        }
    }
    fun getAheadBehind(repo:Repository, localOid:Oid, upstreamOid:Oid) : Pair<Int,Int>{
        try{
            val count = Graph.aheadBehind(repo, localOid, upstreamOid)
            return Pair(count.ahead, count.behind)
        }catch (e:Exception) {
            MyLog.d(TAG, "#getAheadBehind() failed!, localOid=${localOid}, upstreamOid=${upstreamOid}\n Exception is:${e.stackTraceToString()}")
            return Pair(0,0)
        }
    }
    fun setRemoteUrlForRepo(repo: Repository, remote:String, url:String) {
        Remote.setUrl(repo,remote, url);
    }
    fun findRepoByPath(underGitRepoPath:String, cellingDir:String = FsUtils.getExternalStorageRootPathNoEndsWithSeparator()) :Repository? {
        try {  
            return Repository.openExt(underGitRepoPath, null, cellingDir)
        }catch (e:Exception) {
            MyLog.d(TAG, "#findRepoByPath(): err: ${e.stackTraceToString()}")
            return null
        }
    }
    fun getRepoStateText(repoStateIntValue:Int, appContext: Context):String {
        return if(repoStateIntValue == Cons.gitRepoStateInvalid) {
            appContext.getString(R.string.invalid)
        }else if(repoStateIntValue == Repository.StateT.MERGE.bit) {
            appContext.getString(R.string.merge_state)
        }else if(repoStateIntValue == Repository.StateT.REBASE_MERGE.bit) {
            appContext.getString(R.string.rebase_state)
        }else if(repoStateIntValue == Repository.StateT.CHERRYPICK.bit) {
            appContext.getString(R.string.cherrypick_state)
        }else { 
            ""
        }
    }
    fun createTagLight(repo: Repository, tagName:String, commit: Commit, force: Boolean):Oid {
        return Tag.createLightWeight(repo, tagName, commit, force)
    }
    fun createTagAnnotated(repo: Repository, tagName:String, commit: Commit, tagMsg: String, gitUserName:String, gitEmail:String, force: Boolean, settings: AppSettings):Oid {
        return Tag.create(
            repo,
            tagName,
            commit,
            Libgit2Helper.createSignature(gitUserName, gitEmail, settings),
            tagMsg,
            force
        )
    }
    fun getAllTags(repoId: String, repo: Repository, settings: AppSettings): List<TagDto> {
        val timeOffsetInMins = readTimeZoneOffsetInMinutesFromSettingsOrDefaultNullable(settings, null)
        val tags = mutableListOf<TagDto>()
        Tag.foreach(repo) { name, oidStr ->
            val oid = try {
                Oid.of(oidStr)
            }catch (e: Exception) {
                MyLog.e(TAG, "tag pointed an invalid oid: $oidStr")
                Cons.git_AllZeroOid
            }
            val tagDto = try {
                val tag = Tag.lookup(repo, oid)
                val tagger = tag.tagger()
                TagDto(
                    name = name,
                    shortName = getShortTagNameByFull(name),
                    fullOidStr = oidStr,
                    targetFullOidStr = tag.targetId().toString(),
                    isAnnotated = true,
                    taggerName = tagger?.name ?:"",
                    taggerEmail = tagger?.email ?:"",
                    date = tagger?.getWhenWithOffset(timeOffsetInMins),
                    originTimeOffsetInMinutes = tagger?.timeOffsetInMinutes() ?: 0,
                    msg = tag.message(),
                )
            }catch (e:Exception) {
                TagDto(
                    name = name,
                    shortName = getShortTagNameByFull(name),
                    fullOidStr = oidStr,
                    targetFullOidStr = oidStr,
                    isAnnotated = false
                )
            }
            val commit = Libgit2Helper.resolveCommitByHash(repo, tagDto.targetFullOidStr)
            if(commit != null) {
                tagDto.pointedCommitDto = createSimpleCommitDto(
                    commit = commit,
                    repoId = repoId,
                    settings = settings
                )
            }
            tags.add(tagDto)
            0
        }
        return tags
    }
    fun getShortTagNameByFull(fullTagName:String) :String {
        return try {
            val keyword = "refs/tags/"
            fullTagName.substring(fullTagName.indexOf(keyword)+keyword.length)
        }catch (e:Exception) {
            ""
        }
    }
    fun getFormattedUsernameAndEmail(username: String, email: String):String {
        return username+" <${email}>"
    }
    fun fetchAllTags(repo:Repository, repoFromDb: RepoEntity, remoteAndCredentials:List<RemoteAndCredentials>, force:Boolean = false) {
        val refspecs = Array(1) {if(force) "+${Cons.gitAllTagsRefspecForFetchAndPush}" else Cons.gitAllTagsRefspecForFetchAndPush}  
        val downloadTags = if(force) Remote.AutotagOptionT.NONE else Remote.AutotagOptionT.UNSPECIFIED
        fetchRemoteListForRepo(
            repo, remoteAndCredentials, repoFromDb,
            requireUnshallow = false, refspecs,
            pruneType = FetchOptions.PruneT.UNSPECIFIED,  
            downloadTags = downloadTags,
        )
    }
    fun pushTags(repo:Repository, remoteAndCredentials:List<RemoteAndCredentials>, refspecs: List<String>):List<PushFailedItem> {
        return pushMulti(repo, remoteAndCredentials, refspecs)
    }
    fun delTags(repoId: String, repo:Repository, tagShortNames:List<String>) {
        val funName = "delTags"
        tagShortNames.forEachBetter {
            try {
                Tag.delete(repo, it)
            }catch (e:Exception) {
                Msg.requireShow("del $it err: ${e.localizedMessage}")
                doJobThenOffLoading {
                    createAndInsertError(repoId, "del tag '$it' err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#$funName err: del tag '$it' err: ${e.stackTraceToString()}")
                }
            }
        }
    }
    fun createRemote(repo:Repository, remoteName:String, url:String):Ret<Remote?> {
        return try{
            val remote = Remote.create(
                repo,
                remoteName,
                url
            )
            Ret.createSuccess(remote)
        }catch (e:Exception) {
            MyLog.e(TAG, "#createRemote err: remoteName=$remoteName, url=$url, err="+e.stackTraceToString())
            Ret.createError(null, e.localizedMessage ?: "unknown err", exception = e)
        }
    }
    fun delRemote(repo:Repository, remoteName:String):Ret<String?> {
        return try {
            val key = "remote.$remoteName.fetch"
            val writableConfig = getRepoConfigForWrite(repo)
            writableConfig.deleteMultivar(key, Cons.regexMatchAll)
            Remote.delete(repo, remoteName)
            Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#delRemote err: remoteName=$remoteName, err="+e.stackTraceToString())
            Ret.createError(null, e.localizedMessage?:"unknown err", exception = e)
        }
    }
    fun stashList(repo:Repository, out:MutableList<StashDto>):List<StashDto> {
        Stash.foreach(
            repo
        ) { index: Int, message: String?, stashId: Oid ->
            out.add(StashDto(index, message?:"", stashId))
            0
        }
        return out
    }
    fun stashSave(
        repo:Repository,
        stasher: Signature,
        msg:String,
        flags:EnumSet<Stash.Flags> = EnumSet.of(Stash.Flags.DEFAULT)
    ):Oid? {
        return Stash.save(repo, stasher, msg, flags)
    }
    fun stashApply(repo:Repository, indexOfStash:Int, applyOptions: Stash.ApplyOptions? = null) {
        Stash.apply(repo, indexOfStash, applyOptions);
    }
    fun stashPop(repo:Repository, indexOfStash:Int, applyOptions: Stash.ApplyOptions? = null) {
        Stash.pop(repo, indexOfStash, applyOptions);
    }
    fun stashDrop(repo:Repository, indexOfStash:Int) {
        Stash.drop(repo, indexOfStash);
    }
    fun stashGenMsg():String {
        return "Stash@"+ getNowInSecFormatted(Cons.dateTimeFormatterCompact)+"#"+getShortUUID()
    }
    fun getReflogList(repo:Repository, name:String, out: MutableList<ReflogEntryDto>, settings: AppSettings):List<ReflogEntryDto> {
        val timeZoneOffsetMins = readTimeZoneOffsetInMinutesFromSettingsOrDefaultNullable(settings, null)
        val reflog = Reflog.read(repo, name)
        val count = reflog.entryCount()
        if(count>0) {
            for(i in 0 ..< count) {
                val e = reflog.entryByIndex(i)
                val commiter = e.committer()
                val whentime = commiter.getWhenWithOffset(timeZoneOffsetMins)
                out.add(ReflogEntryDto(
                    username = commiter.name,
                    email = commiter.email,
                    date = whentime.format(Cons.defaultDateTimeFormatter),
                    actuallyUsingTimeZoneOffsetInMinutes = whentime.offset.totalSeconds / 60,
                    originTimeZoneOffsetInMinutes = commiter.timeOffsetInMinutes(),
                    idNew = e.idNew(),
                    idOld = e.idOld(),
                    msg = e.message()?:""
                ))
            }
        }
        return out
    }
    private fun initCherrypickOptions(opts:Cherrypick.Options) {
        val mergeOpts = opts.mergeOpts
        mergeOpts.flags = 0  
        mergeOpts.fileFlags = Merge.FileFlagT.STYLE_DIFF3.bit 
        val checkoutOpts = opts.checkoutOpts
        checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.SAFE, Checkout.StrategyT.ALLOW_CONFLICTS)
    }
    fun cherrypick(repo:Repository, targetCommitFullHash:String, parentCommitFullHash:String="", pathSpecList: List<String>?=null, cherrypickOpts:Cherrypick.Options? = null, autoCommit:Boolean = true, settings: AppSettings):Ret<Oid?> {
        val state = repo.state()
        if(state != Repository.StateT.NONE) {
            return Ret.createError(null, "repo state is not 'NONE'")
        }
        val cherrypickOpts = if(cherrypickOpts == null) {
            val tmp = Cherrypick.Options.createDefault()
            initCherrypickOptions(tmp)
            tmp
        }else {
            cherrypickOpts
        }
        val target = resolveCommitByHash(repo, targetCommitFullHash)
        if(target == null){
            return Ret.createError(null, "resolve target commit failed!")
        }
        val pc = target.parentCount()
        if(pc>1) {
            if(parentCommitFullHash.isBlank()) {
                return Ret.createError(null, "parent commit not specified")
            }
            for(i in 0..<pc) {
                val pid = target.parentId(i)
                if(pid==null) {  
                    continue
                }
                if(parentCommitFullHash.equals(pid.toString())) {
                    cherrypickOpts.mainline = i+1  
                    break
                }
            }
        }
        if(pathSpecList!=null && pathSpecList.isNotEmpty()) {
            cherrypickOpts.checkoutOpts.setPaths(pathSpecList.toTypedArray())
        }
        Cherrypick.cherrypick(repo, target, cherrypickOpts)
        if(hasConflictItemInRepo(repo)) {
            return Ret.createError(null, "cherrypick: has conflicts!", Ret.ErrCode.mergeFailedByAfterMergeHasConfilts)
        }
        if(indexIsEmpty(repo)) {
            cleanRepoState(repo)
            return Ret.createError(null, "Already up-to-date", errCode = Ret.ErrCode.alreadyUpToDate)
        }
        if(autoCommit) {
            val author = target.author()
            return createCommit(repo, target.message(), author.name, author.email, cleanRepoStateIfSuccess = true, settings = settings)
        }
        return Ret.createSuccess(null)
    }
    fun getCherryPickHeadHash(repo: Repository):String {
        val file = File(getRepoGitDirPathNoEndsWithSlash(repo), "CHERRY_PICK_HEAD")
        if(!file.exists()) {
            return ""
        }
        file.bufferedReader().use {
            return it.readLine().trim()
        }
    }
    fun readyForContinueCherrypick(activityContext:Context, repo: Repository):Ret<Oid?> {
        val funName = "readyForContinueCherrypick"
        try {
            if(repo.state() != Repository.StateT.CHERRYPICK) {
                return Ret.createError(null, activityContext.getString(R.string.repo_not_in_cherrypick))
            }
            if(hasConflictItemInRepo(repo)) {
                return Ret.createError(null, activityContext.getString(R.string.plz_resolve_conflicts_first))
            }
            if(getCherryPickHeadHash(repo).isBlank()) {
                return Ret.createError(null, activityContext.getString(R.string.no_cherrypick_head_found))
            }
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: "+e.stackTraceToString())
            return Ret.createError(null, "err: "+e.localizedMessage)
        }
    }
    fun getCherryPickHeadCommit(repo:Repository):Ret<Commit?> {
        val cherrypickTargetCommit = resolveCommitByHash(repo, getCherryPickHeadHash(repo))
        if(cherrypickTargetCommit==null) {
            return Ret.createError(null, "resolve cherry pick HEAD failed!")
        }
        return Ret.createSuccess(cherrypickTargetCommit)
    }
    fun getCherryPickHeadCommitMsg(repo: Repository):String {
        val ret = getCherryPickHeadCommit(repo)
        if(ret.hasError()) {
            return ""
        }else {
            return ret.data!!.message()
        }
    }
    fun cherrypickContinue(activityContext:Context, repo: Repository, msg: String="", username: String, email: String, autoClearState:Boolean=true, overwriteAuthor: Boolean, settings: AppSettings):Ret<Oid?> {
        val readyCheckRet = readyForContinueCherrypick(activityContext, repo)
        if(readyCheckRet.hasError()) {
            return readyCheckRet
        }
        val msg = if(msg.isBlank()) {
            val r = getCherryPickHeadCommit(repo)
            if(r.hasError()) {
                return Ret.createError(null, r.msg, exception = r.exception)
            }
            r.data!!.message()
        }else {  
            msg
        }
        if(indexIsEmpty(repo)) {  
            cleanRepoState(repo)  
            return Ret.createError(null, "Index is empty, cherrypick canceled")
        }
        val ret = createCommit(repo, msg, username, email, overwriteAuthorWhenAmend = overwriteAuthor, cleanRepoStateIfSuccess = autoClearState, settings = settings)
        return ret
    }
    fun cherrypickAbort(repo:Repository):Ret<String?> {
        return resetHardToHead(repo)
    }
    fun savePatchToFileAndGetContent(
        outFile:File?=null,  
        pathSpecList: List<String>?=null,   
        repo: Repository,
        tree1: Tree?,  
        tree2: Tree?,  
        diffOptionsFlags: EnumSet<Diff.Options.FlagT> = getDefaultDiffOptionsFlags(),
        fromTo: String,
        reverse: Boolean = false, 
        treeToWorkTree: Boolean = false,  
        returnDiffContent:Boolean = false  
    ):Ret<PatchFile?>{
        val funName = "savePatchToFileAndGetContent"
        try{
            val options = Diff.Options.create()
            val opFlags = diffOptionsFlags.toMutableSet()
            if(reverse) {
                opFlags.add(Diff.Options.FlagT.REVERSE)
            }
            options.flags = EnumSet.copyOf(opFlags);
            MyLog.d(TAG, "#$funName: options.flags = $opFlags")
            if(pathSpecList!=null && pathSpecList.isNotEmpty()) {
                options.pathSpec = pathSpecList.toTypedArray()
            }
            val diff = if(fromTo == Cons.gitDiffFromIndexToWorktree) {
                Diff.indexToWorkdir(repo, null, options)
            }else if(fromTo == Cons.gitDiffFromHeadToIndex) {
                val headTree:Tree? = resolveHeadTree(repo)
                if(headTree==null) {
                    MyLog.w(TAG, "#$funName(): require diff from head to index, but resolve HEAD tree failed!")
                    return Ret.createError(null, "require diff from head to index, but resolve HEAD tree failed!")
                }
                Diff.treeToIndex(repo, headTree, repo.index(), options)
            }
            else {  
                MyLog.d(TAG, "#$funName(): require diff from tree to tree, tree1Oid=${tree1?.id().toString()}, tree2Oid=${tree2?.id().toString()}, reverse=$reverse")
                if(treeToWorkTree) Diff.treeToWorkdir(repo, tree1, options) else Diff.treeToTree(repo, tree1, tree2, options)
            }
            val sb = StringBuilder()
            val allDeltas = diff.numDeltas()
            for(i in 0..<allDeltas) {
                val d = diff.getDelta(i)
                if(d?.flags?.contains(Diff.FlagT.BINARY) == true) {
                    continue
                }
                val patch = Patch.fromDiff(diff, i)
                val patchOutStr = patch?.toBuf() ?:""
                if(patchOutStr.isEmpty()) {
                    continue
                }
                sb.append(patchOutStr).appendLine()
            }
            var diffContent:String? = null
            val writeToFile = (outFile != null)
            if(writeToFile) {
                outFile!!.bufferedWriter().use {
                    diffContent = sb.toString()
                    it.write(diffContent)
                }
            }
            val retContent = if(returnDiffContent) {
                diffContent ?: sb.toString()
            }else {  
                null
            }
            return Ret.createSuccess(
                PatchFile(
                    outFileFullPath = outFile?.canonicalPath,
                    content = retContent
                )
            )
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
            return Ret.createError(null, e.localizedMessage ?: "save patch err", exception = e)
        }
    }
    fun isWorktreeClean(repo: Repository):Boolean {
        return getWorkdirStatusList(repo).entryCount() == 0
    }
    fun applyPatchFromFile(
        inputFile:File,
        repo:Repository,
        checkOnlyDontRealApply:Boolean,
        location:Apply.LocationT = Apply.LocationT.WORKDIR,  
        checkWorkdirCleanBeforeApply: Boolean = true,
        checkIndexCleanBeforeApply: Boolean = false
    ):Ret<Unit?> {
        try {
            if(checkWorkdirCleanBeforeApply && !isWorktreeClean(repo)) {
                return Ret.createError(null, "err: workdir has uncommitted changes!")
            }
            if(checkIndexCleanBeforeApply && !indexIsEmpty(repo)) {
                return Ret.createError(null, "err: index has uncommitted changes!")
            }
            if(repo.index().hasConflicts()) {
                return Ret.createError(null, "err: plz resolve conflicts before apply patch!")
            }
            var content = ""
            inputFile.bufferedReader().use {
                content = it.readText()
            }
            if(content.isBlank()) {
                return Ret.createError(null, "err: patch is empty!")
            }
            val diff = Diff.fromBuffer(content)
            val deltaCallback = getIgnoreSubmoduleApplyDeltaCallback(getSubmodulePathList(repo))
            val applyOptions:Apply.Options = Apply.Options.createDefault(deltaCallback, null)
            if(checkOnlyDontRealApply) {
                applyOptions.flags = EnumSet.of(Apply.FlagsT.CHECK)
            }
            Apply.apply(repo, diff, location, applyOptions)
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage ?:"apply patch err", exception = e)
        }
    }
    fun getHeadCommit(repo: Repository):Ret<Commit?> {
        try {
            val head = resolveHEAD(repo) ?: return Ret.createError(null, "HEAD is null")
            val commit = resolveCommitByHash(repo, head.id().toString()) ?: return Ret.createError(null, "HEAD commit is null")
            return Ret.createSuccess(commit)
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage ?:"get commit of head err", exception = e)
        }
    }
    fun getHeadCommitMsg(repo:Repository):String {
        val commitRet = getHeadCommit(repo)
        return if(commitRet.hasError()) {
            ""
        }else {
            commitRet.data?.message() ?: ""
        }
    }
    fun resolveTag(repo:Repository, oidFullOrShortStr: String):Tag? {
        try {
            val tag = Tag.lookupPrefix(repo, oidFullOrShortStr)
            return tag
        }catch (e:Exception) {
            MyLog.e(TAG, "#resolveTag() error, params are (oidFullOrShortStr=$oidFullOrShortStr}),\nerr is: "+e.stackTraceToString())
            return null
        }
    }
    fun resolveCommitOidByRef(repo:Repository, shortOrFullRefSpec:String):Oid? {
        try {
            val ref = resolveRefByName(repo, shortOrFullRefSpec)
            val cid = ref?.peel(GitObject.Type.COMMIT)?.id()
            return cid
        }catch (e:Exception) {
            MyLog.d(TAG, "#resolveCommitOidByRef() error, params are (shortOrFullRefSpec=$shortOrFullRefSpec}),\nerr is: "+e.stackTraceToString())
            return null
        }
    }
    fun resolveCommitOidByRef2(repo:Repository, shortOrFullRefSpec:String):Ret<Oid?> {
        try {
            val ref = resolveRefByName(repo, shortOrFullRefSpec)
            val cid = ref?.peel(GitObject.Type.COMMIT)?.id()
            if(cid==null) {
                return Ret.createError(null, "resolved commit oid is null")
            }
            return Ret.createSuccess(cid)
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage ?:"err", exception = e)
        }
    }
    fun resolveCommitByRef(repo:Repository, shortOrFullRefSpec:String):Commit? {
        try {
            val cid = resolveCommitOidByRef(repo, shortOrFullRefSpec)
            if(cid==null || cid.isNullOrEmptyOrZero) {
                return null
            }
            return resolveCommitByHash(repo, cid.toString())
        }catch (e:Exception) {
            MyLog.d(TAG, "#resolveCommitByRef() error, params are (shortOrFullRefSpec=$shortOrFullRefSpec}),\nerr is: "+e.stackTraceToString())
            return null
        }
    }
    fun resolveCommitByHashOrRef(repo: Repository, hashOrBranchOrTag: String): Ret<Commit?> {
        if(hashOrBranchOrTag.isBlank()) {
            return Ret.createError(null, "invalid hash")
        }
        val funName ="resolveCommitByHashOrRef"
        try {
            var c =  resolveCommitByHash(repo, hashOrBranchOrTag)
            if(c==null) {  
                c = resolveCommitByRef(repo, hashOrBranchOrTag)
                if(c==null) {
                    return Ret.createError(null, "resolve commit failed!")
                }
            }
            return Ret.createSuccess(c)
        }catch (e:Exception) {
            MyLog.d(TAG, "#$funName() error, params are (hashOrBranchOrTag=$hashOrBranchOrTag}),\nerr is: "+e.stackTraceToString())
            return Ret.createError(null, e.localizedMessage ?:"resolve commit err: param=$hashOrBranchOrTag", exception = e)
        }
    }
    fun checkoutFiles(
        repo: Repository,
        targetCommitHash:String,
        pathSpecs: List<String>,
        force: Boolean,
        checkoutOptions: Checkout.Options?=null
    ):Ret<Unit?> {
        val funName = "checkoutFiles"
        try {
            val checkoutOptions = if(checkoutOptions == null) {
                val opts = Checkout.Options.defaultOptions()
                opts.strategy = getCheckoutStrategies(force)
                if(pathSpecs.isNotEmpty()) {
                    opts.setPaths(pathSpecs.toTypedArray())
                }
                opts
            }else {
                checkoutOptions
            }
            if(targetCommitHash == Cons.git_IndexCommitHash) {  
                checkoutIndex(repo, checkoutOptions)
            }else { 
                val targetTree = resolveTree(repo, targetCommitHash) ?: return Ret.createError(null, "resolve target tree failed")
                Checkout.tree(repo, targetTree, checkoutOptions)
            }
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: targetCommitHash=$targetCommitHash, err=${e.stackTraceToString()}")
            return Ret.createError(null, e.localizedMessage ?: "checkout files err", exception = e)
        }
    }
    fun rebaseCurOfAllFormatted(repo:Repository, prefix: String="(", split:String="/", suffix:String=")"):String {
        try {
            if(repo.state() != Repository.StateT.REBASE_MERGE) {
                throw RuntimeException("repo state is not REBASE")
            }
            val rebase = Rebase.open(repo, null)  
            return prefix+(rebase.operationCurrent()+1)+split+rebase.operationEntrycount()+suffix
        }catch (e:Exception) {
            return ""
        }
    }
    fun isValidGitRepo(repoFullPath:String):Boolean {
        if(repoFullPath.isBlank()) {
            return false
        }
        try {
            val dir = File(repoFullPath)
            if(dir.canRead().not()) {  
                return false
            }
            Repository.open(repoFullPath).use { repo ->
                return !repo.headUnborn()
            }
        }catch (e:Exception) {
            return false
        }
    }
    fun renameBranch(repo: Repository, branchShortName:String, newName:String, force: Boolean):Ret<Unit?> {
        try {
            val branch = resolveBranch(repo, branchShortName, Branch.BranchType.LOCAL)
            if(branch!=null) {
                Branch.move(branch, newName, force)
                return Ret.createSuccess(null)
            }else {
                return Ret.createError(null, "resolve branch failed!")
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#renameBranch err: ${e.stackTraceToString()}")
            return Ret.createError(null, e.localizedMessage ?: "rename branch err")
        }
    }
    fun getRepoConfigFilePath(repo: Repository): String {
        return repo.itemPath(Repository.Item.CONFIG) ?: ""
    }
    fun createDotGitFileIfNeed(parentRepoFullPathNoSlashSuffix:String, subRepoPathUnderParentNoSlashPrefixAndSuffix:String, force:Boolean) {
        val subFullPath = parentRepoFullPathNoSlashSuffix + Cons.slash + subRepoPathUnderParentNoSlashPrefixAndSuffix
        val subModulesGitFile = File(subFullPath, ".git")
        if(force) {
            subModulesGitFile.delete()
        }
        if (!subModulesGitFile.exists()) {
            val relativeGoToSubModuleDotGitFolderAtParentFromSubModuleWorkDir =
                getGoToParentRelativePathFromSub(parentRepoFullPathNoSlashSuffix, subFullPath) +
                        ".git/modules/" +
                        subRepoPathUnderParentNoSlashPrefixAndSuffix;
            subModulesGitFile.createNewFile()
            subModulesGitFile.bufferedWriter().use {
                it.write("gitdir: $relativeGoToSubModuleDotGitFolderAtParentFromSubModuleWorkDir")
            }
        }
    }
    private fun getGoToParentRelativePathFromSub(parent:String, subFullPath:String):String? {
        if(parent == subFullPath) {
            return ""
        }
        if(!subFullPath.startsWith(parent)) {
            return null
        }
        val pIdx = subFullPath.indexOf(parent)
        val subRelativePath = subFullPath.substring(pIdx+parent.length)
        val levelCount = subRelativePath.count {
            it == Cons.slashChar
        }
        var result = ""
        for(i in 0..<levelCount) {
            result+="../"
        }
        return result
    }
    fun addSubmodule(repo: Repository, remoteUrl: String, relativePathUnderParentRepo:String) {
        val useGitlink=true
        Submodule.addSetup(repo, remoteUrl, relativePathUnderParentRepo, useGitlink)
        SubmoduleDotGitFileMan.backupDotGitFileForSubmodule(getRepoWorkdirNoEndsWithSlash(repo), relativePathUnderParentRepo)
    }
    fun getSubmoduleDtoList(repo:Repository, invalidUrlAlertText:String, predicate: (submoduleName: String) -> Boolean={true}):List<SubmoduleDto> {
        val parentWorkdirPathNoSlashSuffix = getRepoWorkdirNoEndsWithSlash(repo)
        val list = mutableListOf<SubmoduleDto>()
        Submodule.foreach(repo) { sm, name ->
            try {
                if(!predicate(name)) {
                    return@foreach 0
                }
                val smDto = createSubmoduleDto(
                    sm = sm,
                    smName = name,
                    parentWorkdirPathNoSlashSuffix = parentWorkdirPathNoSlashSuffix,
                    invalidUrlAlertText = invalidUrlAlertText
                )
                list.add(smDto)
            }catch (e:Exception) {
                MyLog.e(TAG, "#getSubmoduleDtoList: get submodule '$name' err: ${e.localizedMessage}")
            }
            0
        }
        return list
    }
    fun getSubmodulePathList(repo:Repository, predicate: (submoduleName: String) -> Boolean={true}):List<String> {
        return getSubmoduleNameList(repo, predicate)
    }
    fun getSubmoduleNameList(repo:Repository, predicate: (submoduleName: String) -> Boolean={true}):List<String> {
        val list = mutableListOf<String>()
        Submodule.foreach(repo) { sm, name ->
            if(!predicate(name)) {
                return@foreach 0
            }
            list.add(name)
            0
        }
        return list
    }
    fun removeRepoFilesForGoodRepo(workDirPath: String, createEmptyWorkDirAfterRemove:Boolean = false) {
        kotlin.runCatching {
            val repoGitDirPath = Repository.open(workDirPath).use { repo ->
                Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(repo)
            }
            File(repoGitDirPath).deleteRecursively()
        }
        File(workDirPath).let {
            it.deleteRecursively()
            if(createEmptyWorkDirAfterRemove) {
                it.mkdirs()
            }
        }
    }
    fun removeRepoFiles(workDirPath: String, createEmptyWorkDirAfterRemove:Boolean = false) {
        val dotGitDir = Libgit2Helper.getDotGitDirByWorkDirPath(workDirPath)
        if (dotGitDir != null) {
            MyLog.d(TAG, "dotGitDir.canonicalPath=${dotGitDir.canonicalPath}")
            if (dotGitDir.exists()) {
                MyLog.d(TAG, "will delete .git folder at: ${dotGitDir.canonicalPath}")
                dotGitDir.deleteRecursively()
            }
        } else {
            MyLog.d(TAG, "remove '.git' file err: '.git' file doesn't exist or doesn't include a valid path")
        }
        val workdir = File(workDirPath)
        if (workdir.exists()) {
            MyLog.d(TAG, "will delete workdir files at: ${workdir.canonicalPath}")
            workdir.deleteRecursively()
            if(createEmptyWorkDirAfterRemove) {
                workdir.mkdirs()
            }
        }
    }
    fun removeSubmodule(
        deleteFiles: Boolean,
        deleteConfigs: Boolean,
        repo: Repository,
        repoWorkDirPath: String,
        submoduleName:String,
        submoduleFullPath:String,
    ){
        if (deleteFiles) {
            removeRepoFiles(submoduleFullPath, createEmptyWorkDirAfterRemove = deleteConfigs.not())
        }
        if (deleteConfigs) {
            val parentConfig = Libgit2Helper.getRepoConfigFilePath(repo)
            if (parentConfig.isNotBlank()) {
                val parentConfigFile = File(parentConfig)
                if (parentConfigFile.exists()) {
                    MyLog.d(TAG, "will delete submodule key from parent repo config at: ${parentConfigFile.canonicalPath}")
                    Libgit2Helper.deleteSubmoduleInfoFromGitConfigFile(parentConfigFile, submoduleName)
                }
            }
            val gitmoduleFile = File(repoWorkDirPath, Cons.git_DotGitModules)
            if (gitmoduleFile.exists()) {
                MyLog.d(TAG, "will delete submodule key from submodule config at: ${gitmoduleFile.canonicalPath}")
                Libgit2Helper.deleteSubmoduleInfoFromGitConfigFile(gitmoduleFile, submoduleName)
            }
        }
    }
    fun updateSubmoduleUrl(parentRepo:Repository, sm:Submodule, remoteUrl:String) {
        Submodule.setUrl(parentRepo, sm.name(), remoteUrl)
        val overwrite = true
        sm.init(overwrite)  
        sm.sync()  
    }
    fun resolveSubmodule(repo:Repository, name: String):Submodule? {
        return try {
            Submodule.lookup(repo, name)
        }catch (e:Exception) {
            MyLog.e(TAG, "#resolveSubmodule err: name=$name, err=${e.localizedMessage}")
            null
        }
    }
    fun getCredentialTypeByUrl(url:String):Int {
        if(isSshUrl(url)) {
            return Cons.dbCredentialTypeSsh
        }else {
            return Cons.dbCredentialTypeHttp
        }
    }
    fun needSetDepth(depth:Int):Boolean {
        return depth > 0
    }
    suspend fun cloneSubmodules(repo:Repository, recursive:Boolean, depth:Int, specifiedCredential: CredentialEntity?, submoduleNameList:List<String>, credentialDb:CredentialRepository) {
        val repoFullPathNoSlashSuffix = getRepoWorkdirNoEndsWithSlash(repo)
        submoduleNameList.forEachBetter forEach@{ name ->
            val sm = resolveSubmodule(repo, name)
            if(sm==null){
                return@forEach
            }
            val submodulePath = sm.path()
            val overwriteForInit = true
            val submoduleFullPath = File(repoFullPathNoSlashSuffix, submodulePath).canonicalPath
            val isCloned = isValidGitRepo(submoduleFullPath)
            try {
                sm.init(overwriteForInit)
                if(!isCloned) {
                    submoduleRepoInit(repoFullPathNoSlashSuffix, sm)
                }
                sm.sync()
            }catch (_:Exception) {
            }
            val updateOpts = Submodule.UpdateOptions.createDefault()
            val fetchOpts = updateOpts.fetchOpts
            if(needSetDepth(depth)) {
                fetchOpts.depth = depth
            }
            val callbacks = fetchOpts.callbacks
            val smUrl = sm.url() ?: ""
            try {
                if(specifiedCredential!=null) {
                    if(SpecialCredential.MatchByDomain.credentialId == specifiedCredential.id) {  
                        val credentialByDomain = credentialDb.getByIdWithDecryptAndMatchByDomain(specifiedCredential.id, smUrl)
                        if(credentialByDomain!=null) {
                            callbacks.setCredAcquireCb(getCredentialCb(getCredentialTypeByUrl(smUrl), credentialByDomain))
                        }
                    }else {  
                        callbacks.setCredAcquireCb(getCredentialCb(getCredentialTypeByUrl(smUrl), specifiedCredential))
                    }
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#cloneSubmodules: set credential for submodule '$name' err: ${e.localizedMessage}")
            }
            setCertCheckCallback(smUrl, callbacks, repo)
            SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
            val subRepo:Repository? = if(isCloned) {
                try {
                    Repository.open(submoduleFullPath)
                }catch (e2:Exception) {
                    MyLog.e(TAG,"#cloneSubmodules: open submodule '$name' err: ${e2.localizedMessage}")
                    null
                }
            }else{  
                SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
                MyLog.d(TAG,"#cloneSubmodules: will clone submodule '$name'")
                try {
                    sm.clone(updateOpts)
                }catch (e:Exception) {
                    MyLog.e(TAG,"#cloneSubmodules: clone submodule '$name' err: ${e.localizedMessage}")
                    null
                }
            }
            SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
            if(subRepo==null) {
                MyLog.e(TAG, "#cloneSubmodules: clone submodule '$name' for '${File(repoFullPathNoSlashSuffix).name}' err")
                return@forEach
            }
            try {
                MyLog.d(TAG,"#cloneSubmodules: will update submodule '$name'")
                val init = true
                sm.update(init, updateOpts)
            }catch (e:Exception) {
                MyLog.e(TAG,"#cloneSubmodules: update submodule '$name' err: ${e.localizedMessage}")
            }
            SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
            try {
                MyLog.d(TAG, "#cloneSubmodules: will do addFinalize() for submodule '$name'")
                sm.addFinalize()
            }catch (e:Exception) {
                MyLog.e(TAG, "#cloneSubmodules: do addFinalize() for submodule '$name' err: ${e.localizedMessage}")
            }
            SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
            if(recursive) {
                cloneSubmodules(subRepo, recursive, depth, specifiedCredential, getSubmoduleNameList(subRepo), credentialDb)
            }
        }
    }
    fun submoduleRepoInit(parentRepoFullPath:String, sm: Submodule) {
        try {
            val useGitlink = true
            sm.repoInit(useGitlink)
            SubmoduleDotGitFileMan.backupDotGitFileForSubmodule(parentRepoFullPath, sm.path())
        }catch (e:Exception) {
            MyLog.e(TAG, "#submoduleRepoInit: repoInit err: ${e.localizedMessage}")
        }
    }
    suspend fun updateSubmodule(
        parentRepo:Repository,
        specifiedCredential: CredentialEntity?,
        submoduleNameList: List<String>,
        recursive: Boolean,
        credentialDb: CredentialRepository,
        superParentRepo: Repository
    ) {
        val repoFullPathNoSlashSuffix = getRepoWorkdirNoEndsWithSlash(parentRepo)
        submoduleNameList.forEachBetter { submoduleName ->
            val sm = resolveSubmodule(parentRepo, submoduleName)
            if(sm==null){
                return
            }
            val submodulePath = sm.path()
            try {
                SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
                val updateOpts = Submodule.UpdateOptions.createDefault()
                val callbacks = updateOpts.fetchOpts.callbacks
                val smUrl = sm.url() ?: ""
                try {
                    if(specifiedCredential!=null) {
                        if(SpecialCredential.MatchByDomain.credentialId == specifiedCredential.id) {  
                            val credentialByDomain = credentialDb.getByIdWithDecryptAndMatchByDomain(specifiedCredential.id, smUrl)
                            if(credentialByDomain!=null) {
                                callbacks.setCredAcquireCb(getCredentialCb(getCredentialTypeByUrl(smUrl), credentialByDomain))
                            }
                        }else {  
                            callbacks.setCredAcquireCb(getCredentialCb(getCredentialTypeByUrl(smUrl), specifiedCredential))
                        }
                    }
                }catch (e:Exception) {
                    MyLog.e(TAG, "#updateSubmodule: set credential for submodule '$submoduleName' err: ${e.localizedMessage}")
                }
                setCertCheckCallback(smUrl, callbacks, superParentRepo)
                MyLog.d(TAG,"#updateSubmodule: will update submodule '$submoduleName'")
                val overwriteForInit = true
                val initForUpdate = true
                sm.init(overwriteForInit)
                sm.sync()
                SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
                sm.update(initForUpdate, updateOpts)
                SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
                if(recursive) {
                    val smFullPath = File(repoFullPathNoSlashSuffix, submodulePath).canonicalPath
                    Repository.open(smFullPath).use { smRepo->
                        updateSubmodule(smRepo, specifiedCredential, getSubmoduleNameList(smRepo), recursive, credentialDb, superParentRepo)
                    }
                }
            }catch (e:Exception) {
                SubmoduleDotGitFileMan.restoreDotGitFileForSubmodule(repoFullPathNoSlashSuffix, submodulePath)
            }
        }
    }
    fun openSubmodule(repo: Repository, subName: String):Submodule? {
        try {
            return Submodule.lookup(repo, subName)
        }catch (e:Exception) {
            MyLog.e(TAG, "#openSubmodule err: ${e.stackTraceToString()}")
            return null
        }
    }
    fun getDotGitDirByWorkDirPath(workDirPath: String):File? {
        try {
            val dotGitFile = File(workDirPath, ".git")
            MyLog.d(TAG, "#getDotGitDirByWorkDirPath: dotGitFile.canonicalPath=${dotGitFile.canonicalPath}")
            if(dotGitFile.exists().not()) {
                return null
            }
            if(dotGitFile.isDirectory) {
                return dotGitFile
            }
            val prefix = "gitdir:"
            var gitDirPath:String? = null
            dotGitFile.bufferedReader().use {
                while (true) {
                    var line = it.readLine() ?: break
                    if(line.startsWith(prefix)) {
                        gitDirPath = line.substring(prefix.length).trim()
                        MyLog.d(TAG, "#getDotGitDirByWorkDirPath: got .git dir path from .git file: '$gitDirPath'")
                        break
                    }
                }
            }
            return gitDirPath.let {
                if(it.isNullOrEmpty()) null else if(it.startsWith('/')) File(it) else File(dotGitFile.canonicalFile.parent!!, it)
            }
        }catch (e:Exception) {
            return null
        }
    }
    fun getDotGitDirPathByWorkDirPath(workDirPath:String):String? {
        return getDotGitDirByWorkDirPath(workDirPath)?.canonicalPath
    }
    fun deleteSubmoduleInfoFromGitConfigFile(gitConfigOrGitModuleFile:File, smname:String) {
        val keyword = "[submodule \"$smname\"]"
        deleteTopLevelItemFromGitConfig(gitConfigOrGitModuleFile, keyword)
    }
    fun deleteTopLevelItemFromGitConfig(gitConfig:File, keyName:String) {
        try {
            val begin = keyName
            val newLines = mutableListOf<String>()
            var matched = false
            val br = gitConfig.bufferedReader()
            var rLine = br.readLine()
            while(rLine!=null) {
                if(rLine != begin) {
                    if(!matched) {
                        newLines.add(rLine)
                    }else if(rLine.startsWith("[")) {  
                        newLines.add(rLine)
                        matched = false
                    }
                }else {
                    matched=true
                }
                rLine = br.readLine()
            }
            gitConfig.bufferedWriter().use { writer ->
                if(newLines.isEmpty()) {
                    writer.write("\n")
                }else {
                    newLines.forEachBetter { line->
                        writer.write("$line\n")
                    }
                }
            }
        }catch (_:Exception){
        }
    }
    fun getValueFromGitConfig(configFile:File, key:String):String {
        try {
            val split = key.split(".")
            return getValueFromGitConfigByKeyArr(configFile, split)
        }catch (e:Exception) {
            MyLog.e(TAG, "#getValueFromGitConfig err: ${e.stackTraceToString()}")
            return ""
        }
    }
    private fun getValueFromGitConfigByKeyArr(configFile: File, keyArr:List<String>):String {
        if(keyArr.size >3 || keyArr.size<1) {
            return ""
        }
        return if(keyArr.size == 3) {
            getValueFromGitConfigByKeyArr3Level(configFile, keyArr)
        }else if(keyArr.size==2) {
            getValueFromGitConfigByKeyArr2Level(configFile, keyArr)
        }else {
            getValueFromGitConfigByKeyArr1Level(configFile, keyArr)
        }
    }
    private fun getValueFromGitConfigByKeyArr3Level(configFile: File, keyArr:List<String>):String{
        val l1 = keyArr[0]
        val l2 = keyArr[1]
        val l3 = keyArr[2]
        val begin = "[$l1 \"$l2\"]"
        return getValueFromGitConfigByKeyArr2or3Level(configFile, begin, l3)
    }
    private fun getValueFromGitConfigByKeyArr2Level(configFile: File, keyArr:List<String>):String{
        val l1 = keyArr[0]
        val l2 = keyArr[1]
        val begin = "[$l1]"
        return getValueFromGitConfigByKeyArr2or3Level(configFile, begin, l2)
    }
    private fun getValueFromGitConfigByKeyArr2or3Level(configFile: File, key1:String, key2:String):String{
        val begin = key1
        var matched = false
        configFile.bufferedReader().use { br ->
            var line = br.readLine()
            while (line!=null) {
                line = line.trim()
                if(matched) {
                    val idx = line.indexOf("=")
                    val rightStartIdx = idx+1
                    if(idx > 0 && rightStartIdx<line.length) {  
                        val left = line.substring(0, idx).trim()
                        val right = line.substring(rightStartIdx).trim()
                        if(left == key2) {
                            return right
                        }
                    }
                }
                if(begin == line) {
                    matched = true
                }else if(line.startsWith("[")) {  
                    matched = false
                }
                line = br.readLine()
            }
        }
        return ""
    }
    private fun getValueFromGitConfigByKeyArr1Level(configFile: File, keyArr:List<String>):String{
        val l1 = keyArr[0]
        configFile.bufferedReader().use { br ->
            var line = br.readLine()
            while (line!=null) {
                line = line.trim()
                val idx = line.indexOf("=")
                val rightStartIdx = idx+1
                if(idx > 0 && rightStartIdx<line.length) {  
                    val left = line.substring(0, idx).trim()
                    val right = line.substring(rightStartIdx).trim()
                    if(left == l1) {
                        return right
                    }
                }
                line = br.readLine()
            }
        }
        return ""
    }
    fun initGitRepo(path: String) {
        val isBare = false
        Repository.init(path, isBare)
    }
    fun getParentRecordedTargetHashForSubmodule(submodule:Submodule):String {
        return submodule.headId()?.toString() ?: ""
    }
    fun reloadSubmodule(sm:Submodule, force: Boolean) {
        sm.reload(force)
    }
    fun getSubmoduleLocation(sm:Submodule):Set<Submodule.StatusT> {
        return try {
            sm.location()
        }catch (_:Exception) {
            setOf()
        }
    }
    fun getStatusOfSubmodule(parent: Repository, smName:String):Set<Submodule.StatusT>{
        return try {
            Submodule.status(parent, smName, null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#getStatusOfSubmodule: get status of submodule '$smName' err: ${e.localizedMessage}")
            setOf()
        }
    }
    fun submoduleIsDirty(parentRepo:Repository, submoduleName:String):Boolean {
        val statusSet = getStatusOfSubmodule(parentRepo, submoduleName)
        return statusSet.indexOfFirst {
            it == Submodule.StatusT.WD_INDEX_MODIFIED ||  
                    it == Submodule.StatusT.WD_WD_MODIFIED ||  
                    it == Submodule.StatusT.WD_UNTRACKED  
        } != -1
    }
    fun squashCommitsGenCommitMsg(targetShortOidStr:String, headShortOidStr:String):String {
        return "Squash: ${getLeftToRightShortHash(targetShortOidStr, headShortOidStr)}"
    }
    fun squashCommitsCheckBeforeShowDialog(repo: Repository, targetFullOidStr: String, isShowingCommitListForHEAD:Boolean):Ret<SquashData?>  {
        try {
            if(!isShowingCommitListForHEAD) {
                return Ret.createError(null, "squash only available for Current Branch or Detached HEAD")
            }
            if(repo.state() != Repository.StateT.NONE) {
                return Ret.createError(null, "repo state is not 'NONE'")
            }
            val (username, email) = getGitUsernameAndEmail(repo)
            if(username.isBlank() || email.isBlank()) {
                return Ret.createErrorDefaultDataNull("plz set git username and email then try again")
            }
            if(hasConflictItemInRepo(repo)) {
                return Ret.createError(null, "plz resolve conflicts then try again")
            }
            val headRefRet = resolveRefByName2(repo, "HEAD")
            if(headRefRet.hasError()) {
                return headRefRet.copyWithNewData()
            }
            val headRef = headRefRet.data!!
            val headFullOid = headRef.peel(GitObject.Type.COMMIT)?.id()?.toString()
            if(headFullOid == null) {
                return Ret.createError(null, "resolve head oid failed")
            }
            if(targetFullOidStr == headFullOid) {
                return Ret.createError(null, "can't squash HEAD to HEAD")
            }
            return Ret.createSuccess(
                SquashData(
                    username = username,
                    email = email,
                    headFullOid = headFullOid,
                    headFullName = headRef.name()
                )
            )
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage?:"err", exception = e)
        }
    }
    fun squashCommitsCheckBeforeExecute(repo: Repository, force:Boolean):Ret<String?>  {
        try {
            if(!force && !indexIsEmpty(repo)) {
                return Ret.createError(null, "index dirty")
            }
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage?:"err", exception = e)
        }
    }
    fun squashCommits(
        repo: Repository,
        targetFullOidStr:String,
        commitMsg: String,
        username: String,
        email: String,
        currentBranchFullNameOrHEAD: String, 
        settings: AppSettings,
    ):Ret<Oid?> {
        try {
            if(commitMsg.isBlank()) {
                return Ret.createErrorDefaultDataNull("commit msg is empty")
            }
            if(targetFullOidStr.isBlank()) {
                return Ret.createErrorDefaultDataNull("target oid is empty")
            }
            val checkoutOpt = Checkout.Options.defaultOptions()
            checkoutOpt.strategy = EnumSet.of(Checkout.StrategyT.FORCE, Checkout.StrategyT.SAFE)
            val resetRet = resetToRevspec(repo, targetFullOidStr, Reset.ResetT.SOFT, checkoutOpt)
            if(resetRet.hasError()) {
                return resetRet.copyWithNewData()
            }
            return createCommit(
                repo=repo,
                msg=commitMsg,
                username=username,
                email=email,
                branchFullRefName=currentBranchFullNameOrHEAD,
                settings = settings,
                cleanRepoStateIfSuccess = false,
            )
        }catch (e:Exception) {
            return Ret.createErrorDefaultDataNull(e.localizedMessage?:"unknown err", exception = e)
        }
    }
    fun isTreeIncludedPaths(tree:Tree, paths: List<String>, byName:Boolean): Boolean {
        for(i in paths.indices) {
            if(getEntryOrNullByPathOrName(tree, paths[i], byName) != null) {
                return true
            }
        }
        return false
    }
    fun maybeIsHash(str:String):Boolean {
        return Cons.gitSha1HashRegex.matches(str)
    }
    fun getLeftToRightDiffCommitsText(left:String, right:String, swap:Boolean):String {
        val right = if(maybeIsHash(right)) Libgit2Helper.getShortOidStrByFull(right) else right
        val left = if(maybeIsHash(left)) Libgit2Helper.getShortOidStrByFull(left) else left
        return if (swap) {
            getLeftToRightFullHash(right, left)
        } else {
            getLeftToRightFullHash(left, right)
        }
    }
    fun getLeftToRightShortHash(left:String, right:String):String {
        return getLeftToRightFullHash(getShortOidStrByFull(left), getShortOidStrByFull(right))
    }
    fun getLeftToRightFullHash(left:String, right:String):String {
        return "$left..$right"
    }
    fun getRepoLock(repoId:String):Mutex {
        return repoLockMap.getOrPut(repoId) {
            Mutex()
        }
    }
    fun cloneSingleRepo(
        targetRepo: RepoEntity,
        repoDb: RepoRepository,
        settings: AppSettings,
        unknownErrWhenCloning: String,
        repoDtoList: MutableList<RepoEntity>?,
        repoCurrentIndexInRepoDtoList: Int,  
        selectedItems: MutableList<RepoEntity>?,
    ) {
        doJobThenOffLoading {
            val repoLock = getRepoLock(targetRepo.id)
            if(isLocked(repoLock)) {
                return@doJobThenOffLoading
            }
            repoLock.withLock {
                val repo2ndQuery = repoDb.getById(targetRepo.id) ?: return@withLock
                val repoDir = File(repo2ndQuery.fullSavePath)
                if (repo2ndQuery.workStatus == Cons.dbRepoWorkStatusNotReadyNeedClone) {
                    deleteIfFileOrDirExist(repoDir)
                    val cloneUrl = repo2ndQuery.cloneUrl
                    val savePath = repo2ndQuery.fullSavePath
                    val branch = repo2ndQuery.branch
                    val depth = repo2ndQuery.depth
                    val hasDepth = needSetDepth(depth)
                    val options = Clone.Options.defaultOpts()
                    if (branch.isNotBlank()) {
                        options.checkoutBranch = branch
                        if (dbIntToBool(repo2ndQuery.isSingleBranch)) {
                            options.setRemoteCreateCb { repo, name, url ->
                                MyLog.d(TAG, "in cloneOptions.setRemoteCreateCb: name=$name, url=$url")
                                val singleBranchRefSpec = Libgit2Helper.getGitRemoteFetchRefSpec(remote = name, branch = branch)
                                MyLog.d(TAG, "in cloneOptions.setRemoteCreateCb: singleBranchRefSpec=$singleBranchRefSpec")
                                Remote.createWithFetchspec(
                                    repo,
                                    name,
                                    url,
                                    singleBranchRefSpec
                                )
                            }
                        }
                    }
                    if (hasDepth) {
                        options.fetchOpts.depth = depth
                    }
                    val callbacks = options.fetchOpts.callbacks
                    val credentialId = repo2ndQuery.credentialIdForClone
                    if (credentialId.isNotBlank()) {
                        val credentialDb = AppModel.dbContainer.credentialRepository
                        val credentialFromDb = credentialDb.getByIdWithDecryptAndMatchByDomain(id = credentialId, url = repo2ndQuery.cloneUrl)
                        if (credentialFromDb != null) {
                            val credentialType = Libgit2Helper.getCredentialTypeByUrl(cloneUrl)
                            Libgit2Helper.setCredentialCbForRemoteCallbacks(callbacks, credentialType, credentialFromDb)
                        }
                    }
                    Libgit2Helper.setCertCheckCallback(cloneUrl, callbacks, repo = null)
                    try {
                        Clone.cloneRepo(cloneUrl, savePath, options).use { clonedRepo ->
                            if (dbIntToBool(repo2ndQuery.isRecursiveCloneOn)) {
                                val submoduleUpdateOptions =
                                    Submodule.UpdateOptions.createDefault()
                                Submodule.foreach(clonedRepo) { submodule, submoduleName ->
                                    Submodule.setFetchRecurseSubmodules(
                                        clonedRepo,
                                        submoduleName,
                                        Submodule.RecurseT.YES
                                    ) 
                                    submodule.clone(submoduleUpdateOptions)  
                                    0
                                }
                            }
                            val headRef = Libgit2Helper.resolveHEAD(clonedRepo)
                            if (headRef != null) {
                                repo2ndQuery.branch = headRef.shorthand()
                                repo2ndQuery.lastCommitHash = headRef.id()?.toString() ?: ""
                                repo2ndQuery.updateLastCommitHashShort()
                            }
                            val defaultRemoteName = Remote.list(clonedRepo).getOrNull(0) ?: Cons.gitDefaultRemoteOrigin  
                            repo2ndQuery.pullRemoteName = defaultRemoteName
                            repo2ndQuery.pullRemoteUrl = repo2ndQuery.cloneUrl
                            repo2ndQuery.pushRemoteName = defaultRemoteName
                            repo2ndQuery.pushRemoteUrl = repo2ndQuery.cloneUrl
                            val isRepoShallow = Libgit2Helper.isRepoShallow(clonedRepo)
                            repo2ndQuery.isShallow = boolToDbInt(isRepoShallow)
                            if (isRepoShallow) {
                                Libgit2Helper.ShallowManage.createShallowBak(clonedRepo)
                            }
                            repo2ndQuery.upstreamBranch = Libgit2Helper.getUpstreamOfBranch(clonedRepo, repo2ndQuery.branch).remoteBranchShortRefSpec
                            repo2ndQuery.workStatus = Cons.dbRepoWorkStatusUpToDate
                            repo2ndQuery.createErrMsg = ""
                        }
                    } catch (e: Exception) {
                        repo2ndQuery.workStatus = Cons.dbRepoWorkStatusCloneErr
                        repo2ndQuery.createErrMsg =
                            e.localizedMessage ?: unknownErrWhenCloning
                        deleteIfFileOrDirExist(repoDir)
                        MyLog.e(TAG, "cloneErr: " + e.stackTraceToString())
                    }
                    repo2ndQuery.baseFields.baseUpdateTime = getSecFromTime()
                    repo2ndQuery.lastUpdateTime = getSecFromTime()
                    try {
                        if (repo2ndQuery.workStatus == Cons.dbRepoWorkStatusUpToDate) {
                            repoDb.cloneDoneUpdateRepoAndCreateRemote(repo2ndQuery)
                        } else {  
                            repoDb.update(repo2ndQuery)
                        }
                    } catch (e: Exception) {
                        MyLog.e(TAG, "clone success but update db err: " + e.stackTraceToString())
                    }
                    repo2ndQuery.tmpStatus = ""
                    if(repoDtoList!=null && repoDtoList.isNotEmpty()) {
                        val indexOfRepo = repoDtoList.indexOfFirst { it.id == repo2ndQuery.id }
                        if(indexOfRepo != -1) { 
                            repoDtoList[indexOfRepo] = repo2ndQuery
                        }
                    }
                    if(selectedItems!=null && selectedItems.isNotEmpty()) {
                        val indexOfRepo = selectedItems.indexOfFirst { it.id == repo2ndQuery.id }
                        if(indexOfRepo != -1) { 
                            selectedItems[indexOfRepo] = repo2ndQuery
                        }
                    }
                }
            }
        }
    }
    fun getAllRefs(repo: Repository, includeHEAD: Boolean = true): List<String> {
        val refs = Reference.list(repo)
        return if (refs.contains(Cons.gitHeadStr)) {
            if(includeHEAD) {
                refs
            } else {
                refs.remove(Cons.gitHeadStr)
                refs
            }
        } else {
            if(includeHEAD) {
                val refs2 = mutableListOf(Cons.gitHeadStr)
                refs2.addAll(refs)
                refs2
            }else {
                refs
            }
        }
    }
    fun createSignature(name: String, email: String, settings: AppSettings):Signature {
        val offsetMinutes = AppModel.getAppTimeZoneOffsetInMinutesCached(settings)
        return Signature(name, email, getUtcTimeInSec(), offsetMinutes)
    }
    fun hasUncommittedChanges(repo:Repository):Boolean {
        return hasConflictItemInRepo(repo) || indexIsEmpty(repo).not() || getWorktreeChangeList(repo, getWorkdirStatusList(repo), repoId = "").isNotEmpty()
    }
    suspend fun doActWithRepoLockIfPredicatePassed(curRepo:RepoEntity, predicate:(RepoEntity)->Boolean, act:suspend ()->Unit) {
        if(predicate(curRepo)) {
            doActWithRepoLock(curRepo, act = act)
        }
    }
    suspend fun doActWithRepoLock(curRepo:RepoEntity, waitInMillSec:Long=0, onLockFailed:(lock:Mutex)->Unit={}, act: suspend ()->Unit) {
        val lock = Libgit2Helper.getRepoLock(curRepo.id)
        if(isLocked(lock)) {
            if(waitInMillSec > 0) {
                delay(waitInMillSec)
                if(isLocked(lock)) {
                    onLockFailed(lock)
                }else {
                    lock.withLock {
                        act()
                    }
                }
            }else {
                onLockFailed(lock)
            }
        }else {
            lock.withLock {
                act()
            }
        }
    }
    fun stageAll(repo: Repository, repoId:String):Ret<Unit?> {
        try {
            val wtStatusList = getWorkdirStatusList(repo)
            val size = wtStatusList.entryCount()
            if(size > 0) {
                val statusEntryList = getWorktreeChangeList(repo, wtStatusList, repoId)
                stageStatusEntryAndWriteToDisk(repo, statusEntryList)
                return Ret.createSuccess(null)
            }else {
                return Ret.createError(null, "no changes found")
            }
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage ?: "stage err", exception = e)
        }
    }
    fun getWorktreeChangeList(repo: Repository, rawStatusList: StatusList, repoId: String):List<StatusTypeEntrySaver> {
        if(rawStatusList.entryCount() < 1) {
            return listOf()
        }
        val (_, statusMap) = runBlocking {statusListToStatusMap(repo, rawStatusList, repoIdFromDb = repoId, Cons.gitDiffFromIndexToWorktree)}
        val retList = mutableListOf<StatusTypeEntrySaver>()
        statusMap[Cons.gitStatusKeyConflict]?.let {  
            retList.addAll(it)
        }
        statusMap[Cons.gitStatusKeyWorkdir]?.let {  
            retList.addAll(it)
        }
        return retList
    }
    fun getRepoStateStr(gitRepoState: Repository.StateT?, context: Context): String {
        return gitRepoState?.toString() ?: context.getString(R.string.invalid)
    }
    fun getSimpleCommitDto(repo:Repository, commitHashOrRef:String, repoId: String, settings: AppSettings): Ret<CommitDto?> {
        return try {
            val commitDto = if(commitHashOrRef.let { it == Cons.git_AllZeroOidStr || it == Cons.git_LocalWorktreeCommitHash || it == Cons.git_IndexCommitHash }) {
                CommitDto(oidStr = commitHashOrRef, shortOidStr = commitHashOrRef)
            }else {
                val ret = Libgit2Helper.resolveCommitByHashOrRef(repo, commitHashOrRef)
                if(ret.hasError()) {
                    throw (ret.exception ?: RuntimeException(ret.msg))
                }
                val commit = ret.data!!
                createSimpleCommitDto(
                    commit = commit,
                    repoId = repoId,
                    settings = settings
                )
            }
            Ret.createSuccess(commitDto)
        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "#getSimpleCommitDto: query commit info err: ${e.stackTraceToString()}")
            Ret.createError(null, "err: "+e.localizedMessage, exception = e)
        }
    }
    fun getLeftRightCommitDto(repo: Repository, leftHashOrRef:String, rightHashOrRef:String, repoId:String, settings: AppSettings):Pair<CommitDto, CommitDto> {
        val leftRet = Libgit2Helper.getSimpleCommitDto(repo, commitHashOrRef = leftHashOrRef, repoId, settings)
        val left = if(leftRet.hasError()) CommitDto(oidStr = leftHashOrRef, shortOidStr = Libgit2Helper.getShortOidStrByFull(leftHashOrRef)) else leftRet.data!!
        val rightRet = Libgit2Helper.getSimpleCommitDto(repo, commitHashOrRef = rightHashOrRef, repoId, settings)
        val right = if(rightRet.hasError()) CommitDto(oidStr = rightHashOrRef, shortOidStr = Libgit2Helper.getShortOidStrByFull(rightHashOrRef)) else rightRet.data!!
        return Pair(left, right)
    }
    fun zipOneLineMsg(msg:String, limitedCharsCount: Int = 500) :String {
        val sb = StringBuilder()
        var started = false
        var lastAppendChar = ' '
        var count = 0
        for(c in msg) {
            if(count >= limitedCharsCount) {
                break
            }
            if(!started && c.isWhitespace()) {
                continue
            }
            started = true
            if((c == '\n' || c == '\r' || c.isWhitespace())
                && lastAppendChar == ' '
            ) {
                continue
            }
            val willAppendChar = if(c.isWhitespace()) ' ' else c
            sb.append(willAppendChar)
            lastAppendChar = willAppendChar
            count++
        }
        return sb.toString()
    }
    fun saveFileOfCommitToPath(repo:Repository, refOrHash:String, relativePath:String, genFilePath:(entry:Tree.Entry)->String): SaveBlobRet {
        val tree = resolveTree(repo, refOrHash) ?: return SaveBlobRet(code = SaveBlobRetCode.ERR_RESOLVE_TREE_FAILED)
        val entry = getEntryOrNullByPathOrName(tree, relativePath, byName = false) ?: return SaveBlobRet(code = SaveBlobRetCode.ERR_RESOLVE_ENTRY_FAILED)
        return saveEntryToPath(repo, entry, genFilePath(entry))
    }
    fun saveEntryToPath(repo:Repository, entry:Tree.Entry, savePath:String): SaveBlobRet {
        val blob = Blob.lookup(repo, entry.id()) ?: return SaveBlobRet(code = SaveBlobRetCode.ERR_RESOLVE_BLOB_FAILED)
        return SaveBlobRet(code = LibgitTwo.saveBlobToPath(blob, savePath), savePath = savePath)
    }
    suspend fun forcePushLeaseCheckPassedOrThrow(
        repoEntity: RepoEntity,
        repo: Repository,
        forcePush_expectedRefspecForLease:String,
        upstream:Upstream?,
    ) {
        if(upstream == null) {
            throw RuntimeException("force push with lease canceled: upstream is null")
        }
        val funName = "forcePushLeaseCheckPassedOrThrow"
        val dbContainer = AppModel.dbContainer
        val repoId = repoEntity.id
        val remoteName = upstream.remote
        val remoteBranchRefsRemotesFullRefSpec = upstream.remoteBranchRefsRemotesFullRefSpec
        val expectedCommitOidRet = Libgit2Helper.resolveCommitByHashOrRef(repo, forcePush_expectedRefspecForLease)
        if(expectedCommitOidRet.hasError()) {
            throw RuntimeException("force push with lease canceled: resolve expected refspec failed, expected refspec is `$forcePush_expectedRefspecForLease`")
        }
        val expectedCommitOidStr = expectedCommitOidRet.data!!.id()!!.toString()
        MyLog.d(TAG, "#$funName: force push with lease: expectedCommitOid=$expectedCommitOidStr")
        val credential = Libgit2Helper.getRemoteCredential(
            dbContainer.remoteRepository,
            dbContainer.credentialRepository,
            repoId,
            remoteName,
            trueFetchFalsePush = true
        )
        Libgit2Helper.fetchRemoteForRepo(repo, remoteName, credential, repoEntity)
        val latestUpstreamOidStr = Libgit2Helper.resolveCommitOidByRef(repo, remoteBranchRefsRemotesFullRefSpec).toString()
        val expectedEqualsToLatest = expectedCommitOidStr == latestUpstreamOidStr
        MyLog.d(TAG, "#$funName: force push with lease: upstream.remoteBranchRefsRemotesFullRefSpec=${remoteBranchRefsRemotesFullRefSpec}, latestUpstreamOid=$latestUpstreamOidStr, expectedCommitOid=$expectedCommitOidStr, expectedCommitOid==latestUpstreamOid is `$expectedEqualsToLatest`")
        if(!expectedEqualsToLatest) {
            throw RuntimeException("force push canceled: upstream didn't match the expected refspec, upstream is `$latestUpstreamOidStr`, expected is `$expectedCommitOidStr`")
        }
    }
    fun genDetachedText(shortHash:String?):String {
        return "$shortHash (Detached)"
    }
    fun genLocalBranchAndUpstreamText(localBranch:String, upstreamBranch:String):String {
        return "$localBranch:$upstreamBranch"
    }
    fun genRepoNameSuffixForSubmodule(parentRepoName:String) = "_of_$parentRepoName";
    fun getCommitMsgOneLine(repo: Repository, targetCommitOid:String):String {
        return Libgit2Helper.resolveCommitByHash(repo, targetCommitOid)?.message()?.let {
            Libgit2Helper.zipOneLineMsg(it)
        } ?: ""
    }
}
