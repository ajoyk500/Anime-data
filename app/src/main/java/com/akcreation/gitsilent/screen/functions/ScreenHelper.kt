package com.akcreation.gitsilent.screen.functions

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ClipboardManager
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.LineNum
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.constants.StrCons
import com.akcreation.gitsilent.dev.DevFeature
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.fileeditor.texteditor.state.EditorStateOnChangeCallerFrom
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.git.DiffableItem
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.shared.CommitListFrom
import com.akcreation.gitsilent.screen.shared.DiffFromScreen
import com.akcreation.gitsilent.screen.shared.FileChooserType
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.NaviCache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.doJobWithMainContext
import com.akcreation.gitsilent.utils.generateRandomString
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.withMainContext
import kotlinx.coroutines.CoroutineScope

private const val TAG = "ScreenHelper"
fun goToFileHistory(filePath: FilePath, activityContext: Context){
    goToFileHistory(filePath.toFuckSafFile(activityContext).canonicalPath, activityContext)
}
fun goToFileHistory(fileFullPath:String, activityContext: Context){
    doJobThenOffLoading job@{
        try {
            val repo = Libgit2Helper.findRepoByPath(fileFullPath)
            if(repo == null) {
                Msg.requireShow(activityContext.getString(R.string.no_repo_found))
                return@job
            }
            repo.use {
                val repoGitDirEndsWithSlash = Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(it) + Cons.slash
                if(fileFullPath.startsWith(repoGitDirEndsWithSlash)) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_file_under_git_dir))
                    return@job
                }
                val repoWorkDirPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(it)
                val relativePath = Libgit2Helper.getRelativePathUnderRepo(repoWorkDirPath, fileFullPath)
                if(relativePath == null) {  
                    Msg.requireShow(activityContext.getString(R.string.path_not_under_repo))
                    return@job
                }
                val repoDb = AppModel.dbContainer.repoRepository
                val repoFromDb = repoDb.getByFullSavePath(
                    repoWorkDirPath,
                    onlyReturnReadyRepo = false,  
                    requireSyncRepoInfoWithGit = false,  
                )
                if(repoFromDb == null) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.plz_import_repo_then_try_again))
                    return@job
                }
                goToFileHistoryByRelativePathWithMainContext(repoFromDb.id, relativePath)
            }
        }catch (e:Exception) {
            Msg.requireShowLongDuration(e.localizedMessage?:"err")
            MyLog.e(TAG, "#goToFileHistory err: ${e.stackTraceToString()}")
        }
    }
}
suspend fun goToFileHistoryByRelativePathWithMainContext(repoId:String, relativePathUnderRepo:String) {
    withMainContext {
        naviToFileHistoryByRelativePath(repoId, relativePathUnderRepo)
    }
}
fun naviToFileHistoryByRelativePath(repoId:String, relativePathUnderRepo:String) {
    val fileRelativePathKey = NaviCache.setThenReturnKey(relativePathUnderRepo)
    doJobWithMainContext {
        AppModel.navController.navigate(Cons.nav_FileHistoryScreen + "/" + repoId+"/"+fileRelativePathKey)
    }
}
fun getLoadText(loadedCount:Int, actuallyEnabledFilterMode:Boolean, activityContext:Context):String?{
    return if(loadedCount < 1){
        null
    }else if(actuallyEnabledFilterMode) {
        replaceStringResList(activityContext.getString(R.string.item_count_n), listOf(""+loadedCount))
    }else {
        replaceStringResList(activityContext.getString(R.string.loaded_n), listOf(""+loadedCount))
    }
}
fun getClipboardText(clipboardManager:ClipboardManager):String? {
    return try {
        clipboardManager.getText()?.text
    }catch (e:Exception) {
        MyLog.e(TAG, "#getClipboardText err: ${e.localizedMessage}")
        null
    }
}
fun openFileWithInnerSubPageEditor(
    context: Context,
    filePath:String,
    mergeMode:Boolean,
    readOnly:Boolean,
    goToLine:Int = LineNum.lastPosition,
    onlyGoToWhenFileExists: Boolean = false,
    showMsgIfGoToCanceledByFileNonExist:Boolean = true,
) {
    doJobWithMainContext job@{
        if(onlyGoToWhenFileExists && FilePath(filePath).toFuckSafFile(context).isActuallyReadable().not()) {
            if(showMsgIfGoToCanceledByFileNonExist){
                Msg.requireShowLongDuration(context.getString(R.string.file_doesnt_exist))
            }
            return@job
        }
        val filePathKey = NaviCache.setThenReturnKey(filePath)
        val initMergeMode = if(mergeMode) "1" else "0"
        val initReadOnly = if(readOnly) "1" else "0"
        AppModel.navController.navigate(Cons.nav_SubPageEditor + "/$goToLine/$initMergeMode/$initReadOnly/$filePathKey")
    }
}
fun fromTagToCommitHistory(fullOid:String, shortName:String, repoId:String){
    goToCommitListScreen(
        repoId = repoId,
        fullOid = fullOid,
        shortBranchName = shortName,
        isHEAD = false,
        from = CommitListFrom.TAG,
    )
}
fun defaultTitleDoubleClick(coroutineScope: CoroutineScope, listState: LazyListState, lastPosition: MutableState<Int>)  {
    UIHelper.switchBetweenTopAndLastVisiblePosition(coroutineScope, listState, lastPosition)
}
fun defaultTitleDoubleClick(coroutineScope: CoroutineScope, listState: ScrollState, lastPosition: MutableState<Int>)  {
    UIHelper.switchBetweenTopAndLastVisiblePosition(coroutineScope, listState, lastPosition)
}
fun defaultTitleDoubleClick(coroutineScope: CoroutineScope, listState: LazyStaggeredGridState, lastPosition: MutableState<Int>)  {
    UIHelper.switchBetweenTopAndLastVisiblePosition(coroutineScope, listState, lastPosition)
}
fun defaultTitleDoubleClickRequest(pageRequest: MutableState<String>) {
    pageRequest.value = PageRequest.switchBetweenTopAndLastPosition
}
fun maybeIsGoodKeyword(keyword:String) : Boolean {
    return keyword.isNotEmpty()
}
fun filterModeActuallyEnabled(filterOn:Boolean, keyword: String):Boolean {
    return filterOn && maybeIsGoodKeyword(keyword)
}
fun <T> search(
    src:List<T>,
    match:(srcIdx:Int, srcItem:T)->Boolean,
    matchedCallback:(srcIdx:Int, srcItem:T)->Unit,
    canceled:()->Boolean
) {
    for(idx in src.indices){
        if(canceled()) {
            return
        }
        val it = src[idx]
        if(match(idx, it)) {
            matchedCallback(idx, it)
        }
    }
}
suspend fun initSearch(keyword: String, lastKeyword: MutableState<String>, token:MutableState<String>):()->Boolean {
    lastKeyword.value = keyword
    val tokenForThisSession = generateNewTokenForSearch()
    withMainContext {
        token.value = tokenForThisSession
    }
    return {
        if(AppModel.devModeOn) {
            MyLog.v(TAG, "token.value==tokenForThisSession is '${token.value==tokenForThisSession}', if is false, may something wrong: token.value=${token.value}, tokenForThisSession=$tokenForThisSession")
        }
        token.value.isEmpty() || tokenForThisSession != token.value
    }
}
fun generateNewTokenForSearch():String {
    return generateRandomString(18)
}
fun triggerReFilter(filterResultNeedRefresh:MutableState<String>) {
    filterResultNeedRefresh.value = getShortUUID()
}
@Composable
fun <T> filterTheList(
    activityContext: Context,
    needRefresh:String,
    lastNeedRefresh:MutableState<String>,
    enableFilter: Boolean,
    keyword: String,
    lastKeyword: MutableState<String>,
    searching: MutableState<Boolean>,
    token: MutableState<String>,
    resetSearchVars: () -> Unit,
    match:(idx:Int, item:T)->Boolean, 
    list: List<T>,
    filterList: MutableList<T>,
    lastListSize: MutableIntState? = null,
    filterIdxList:MutableList<Int>? = null,
    customTask:(suspend ()->Unit)? = null,  
    orCustomDoFilterCondition:()->Boolean = {false},
    beforeSearchCallback:(()->Unit)? = null,
) : List<T> {
    return if (enableFilter) {
        val pageRefreshed = needRefresh != lastNeedRefresh.value;
        lastNeedRefresh.value = needRefresh
        val curListSize = list.size
        if (pageRefreshed || keyword != lastKeyword.value || (lastListSize != null && curListSize != lastListSize.intValue) || orCustomDoFilterCondition()) {
            lastListSize?.intValue = curListSize
            filterIdxList?.clear()
            beforeSearchCallback?.invoke()
            doJobThenOffLoading(loadingOff = { searching.value = false }) {
                (customTask ?: {
                    val canceled = initSearch(keyword = keyword, lastKeyword = lastKeyword, token = token)
                    searching.value = true
                    filterList.clear()
                    search(src = list, match = match, matchedCallback = {idx, item -> filterList.add(item)}, canceled = canceled)
                }).invoke()
            }
        }
        filterList
    } else {
        resetSearchVars()
        list
    }
}
fun newScrollState(initial:Int = 0):ScrollState = ScrollState(initial = initial)
fun navToFileChooser(type: FileChooserType) {
    doJobWithMainContext {
        AppModel.navController.navigate(Cons.nav_FileChooserScreen + "/" + type.code)
    }
}
fun getFilesScreenTitle(currentPath:String, activityContext: Context):String {
    if(currentPath == FsUtils.rootPath) {
        return FsUtils.rootName
    }
    val trimedSlashCurPath = currentPath.trimEnd(Cons.slashChar)
    return if(trimedSlashCurPath == FsUtils.getAppDataRootPathNoEndsWithSeparator()) {
        StrCons.appData
    }else if(trimedSlashCurPath == FsUtils.getInternalStorageRootPathNoEndsWithSeparator()) {
        activityContext.getString(R.string.internal_storage)
    }else if(trimedSlashCurPath == FsUtils.getExternalStorageRootPathNoEndsWithSeparator()) {
        activityContext.getString(R.string.external_storage)
    }else if(trimedSlashCurPath == FsUtils.getInnerStorageRootPathNoEndsWithSeparator()) {
        activityContext.getString(R.string.internal_storage)+"(Inner)"
    }else if(trimedSlashCurPath == AppModel.externalDataDir?.canonicalPath) {
        DevFeature.external_data_storage
    }else {
        runCatching { FsUtils.splitParentAndName(currentPath).second }.getOrDefault("").ifEmpty { activityContext.getString(R.string.files) }
    }
}
fun getEditorStateOnChange(
    editorPageTextEditorState: CustomStateSaveable<TextEditorState>,
    lastSavedFieldsId: MutableState<String>,
    undoStack: UndoStack,
    resetLastCursorAtColumn: () -> Unit,
): suspend (newState: TextEditorState, trueSaveToUndoFalseRedoNullNoSave:Boolean?, clearRedoStack:Boolean, caller: TextEditorState, from: EditorStateOnChangeCallerFrom?) -> Unit {
    return { newState, trueSaveToUndoFalseRedoNullNoSave, clearRedoStack, caller, from ->
        val lastState = editorPageTextEditorState.value
        editorPageTextEditorState.value = newState
        if(lastState.maybeNotEquals(newState)) {
            resetLastCursorAtColumn()
            if(trueSaveToUndoFalseRedoNullNoSave == true) {
                if(clearRedoStack && !undoStack.redoStackIsEmpty()) {
                    undoStack.clearRedoStackThenPushToUndoStack(lastState, force = true)
                }else {
                    undoStack.undoStackPush(lastState, force = lastState.fieldsId == lastSavedFieldsId.value)
                }
            }else if(trueSaveToUndoFalseRedoNullNoSave == false) {
                undoStack.redoStackPush(lastState)
            }
        }
    }
}
fun getInitTextEditorState() = TextEditorState(
    codeEditor = null,
    fields = listOf(),
    fieldsId = "",
    isContentEdited = mutableStateOf(false),
    editorPageIsContentSnapshoted = mutableStateOf(false),
    isMultipleSelectionMode = false,
    focusingLineIdx = null,
    onChanged = { i1, i2, i3, _, _ ->},
)
suspend fun goToStashPage(repoId:String) {
    withMainContext {
        AppModel.navController.navigate(Cons.nav_StashListScreen+"/"+repoId)
    }
}
fun goToTreeToTreeChangeList(title:String, repoId: String, commit1:String, commit2:String, commitForQueryParents:String) {
    doJobWithMainContext {
        val commit1OidStrCacheKey = NaviCache.setThenReturnKey(commit1)
        val commit2OidStrCacheKey = NaviCache.setThenReturnKey(commit2)
        val commitForQueryParentsCacheKey = NaviCache.setThenReturnKey(commitForQueryParents)
        val titleCacheKey = NaviCache.setThenReturnKey(title)
        AppModel.navController.navigate(
            "${Cons.nav_TreeToTreeChangeListScreen}/$repoId/$commit1OidStrCacheKey/$commit2OidStrCacheKey/$commitForQueryParentsCacheKey/$titleCacheKey"
        )
    }
}
fun goToCommitListScreen(repoId: String, fullOid:String, from: CommitListFrom, shortBranchName:String, isHEAD:Boolean) {
    doJobWithMainContext {
        val fullOidCacheKey = NaviCache.setThenReturnKey(fullOid)
        val shortBranchNameCacheKey = NaviCache.setThenReturnKey(shortBranchName)
        AppModel.navController.navigate(Cons.nav_CommitListScreen + "/" + repoId + "/" + (if(isHEAD) "1" else "0")+"/"+ from.code +"/"+fullOidCacheKey+"/"+shortBranchNameCacheKey)
    }
}
fun goToDiffScreen(
    diffableList:List<DiffableItem>,
    repoId: String,
    fromTo: String,
    commit1OidStr:String,
    commit2OidStr:String,
    isDiffToLocal:Boolean,
    curItemIndexAtDiffableList:Int,
    localAtDiffRight:Boolean,
    fromScreen:String,
) {
    doJobWithMainContext {
        val diffableListCacheKey = NaviCache.setThenReturnKey(diffableList) ;
        val isMultiMode:Boolean = if(DiffFromScreen.isFromFileHistory(fromScreen)) false else !DevFeature.singleDiff.state.value;
        AppModel.navController.navigate(
            Cons.nav_DiffScreen +
                    "/" + repoId +
                    "/" + fromTo +
                    "/" + commit1OidStr +
                    "/" + commit2OidStr +
                    "/" + (if(isDiffToLocal) 1 else 0)
                    + "/" + curItemIndexAtDiffableList
                    +"/" + (if(localAtDiffRight) 1 else 0)
                    +"/" + fromScreen
                    +"/"+diffableListCacheKey
                    +"/"+(if(isMultiMode) 1 else 0)
        )
    }
}
fun goToCloneScreen(repoId: String="") {
    doJobWithMainContext {
        AppModel.navController.navigate(
            Cons.nav_CloneScreen+"/${repoId.ifBlank { Cons.dbInvalidNonEmptyId }}"
        )
    }
}
fun getFilesGoToPath(
    lastPressedPath: MutableState<String>,
    getCurrentPath:()->String,
    updateCurrentPath:(String)->Unit,
    needRefresh: MutableState<String>
): (String)->Unit {
    val funName = "getFilesGoToPath"
    return { newPath:String ->
        val oldPath = getCurrentPath()
        try {
            try {
                val startIndexForFindEndOfLastPath = newPath.length + 1;
                lastPressedPath.value = if(newPath.length < oldPath.length && startIndexForFindEndOfLastPath < oldPath.length && oldPath.startsWith(newPath)) {
                    val indexOfSlash = oldPath.indexOf(Cons.slashChar, startIndex = startIndexForFindEndOfLastPath)
                    oldPath.substring(0, if(indexOfSlash < 0) oldPath.length else indexOfSlash)
                } else {
                    oldPath
                }
            }catch (e: Exception) {
                MyLog.d(TAG, "#$funName: resolve `lastPressedPath` failed! oldPath='$oldPath', newPath='$newPath', err=${e.stackTraceToString()}")
            }
            if(AppModel.devModeOn) {
                MyLog.v(TAG, "#$funName: lastPressedPath: ${lastPressedPath.value}")
            }
            updateCurrentPath(newPath)
            changeStateTriggerRefreshPage(needRefresh)
        }catch (e: Exception) {
            MyLog.e(TAG, "#$funName: change path failed! oldPath='$oldPath', newPath='$newPath', err=${e.stackTraceToString()}")
            Msg.requireShowLongDuration("err: change dir failed")
        }
    }
}
fun goToErrScreen(repoId:String) {
    doJobWithMainContext {
        AppModel.navController.navigate(Cons.nav_ErrorListScreen + "/" + repoId)
    }
}
