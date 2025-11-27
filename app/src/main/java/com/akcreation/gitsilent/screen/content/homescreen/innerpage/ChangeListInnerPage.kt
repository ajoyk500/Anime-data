package com.akcreation.gitsilent.screen.content.homescreen.innerpage

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.compose.AskGitUsernameAndEmailDialogWithSelection
import com.akcreation.gitsilent.compose.BottomBar
import com.akcreation.gitsilent.compose.ClickableText
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.CopyScrollableColumn
import com.akcreation.gitsilent.compose.CreatePatchSuccessDialog
import com.akcreation.gitsilent.compose.CredentialSelector
import com.akcreation.gitsilent.compose.DefaultPaddingText
import com.akcreation.gitsilent.compose.ForcePushWithLeaseCheckBox
import com.akcreation.gitsilent.compose.FullScreenScrollableColumn
import com.akcreation.gitsilent.compose.GitIgnoreDialog
import com.akcreation.gitsilent.compose.LoadingTextSimple
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.OpenAsDialog
import com.akcreation.gitsilent.compose.PaddingText
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RequireCommitMsgDialog
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.SetUpstreamDialog
import com.akcreation.gitsilent.compose.SingleSelection
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.data.AppContainer
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.checkoutFilesTestPassed
import com.akcreation.gitsilent.dev.cherrypickTestPassed
import com.akcreation.gitsilent.dev.createPatchTestPassed
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.dev.ignoreWorktreeFilesTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dev.tagsTestPassed
import com.akcreation.gitsilent.dev.treeToTreeBottomBarActAtLeastOneTestPassed
import com.akcreation.gitsilent.dto.Box
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.IgnoreItem
import com.akcreation.gitsilent.git.ImportRepoResult
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.git.Upstream
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.ChangeListItem
import com.akcreation.gitsilent.screen.content.listitem.SelectedFileItemsDialog
import com.akcreation.gitsilent.screen.functions.ChangeListFunctions
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.goToCommitListScreen
import com.akcreation.gitsilent.screen.functions.goToDiffScreen
import com.akcreation.gitsilent.screen.functions.goToStashPage
import com.akcreation.gitsilent.screen.functions.naviToFileHistoryByRelativePath
import com.akcreation.gitsilent.screen.functions.openFileWithInnerSubPageEditor
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.CommitListFrom
import com.akcreation.gitsilent.screen.shared.DiffFromScreen
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.RegexUtil
import com.akcreation.gitsilent.utils.StateRequestType
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.cache.ThumbCache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.doJobWithMainContext
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.getRequestDataByState
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.isRepoReadyAndPathExist
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.showErrAndSaveLog
import com.akcreation.gitsilent.utils.showToast
import com.akcreation.gitsilent.utils.state.CustomStateListSaveable
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.updateSelectedList
import com.akcreation.gitsilent.utils.withMainContext
import com.github.git24j.core.Repository
import com.github.git24j.core.Repository.StateT
import kotlinx.coroutines.delay

private const val TAG = "ChangeListInnerPage"
@Composable
fun ChangeListInnerPage(
    stateKeyTag:String,
    errScrollState: ScrollState,
    hasError: MutableState<Boolean>,  
    lastSearchKeyword:MutableState<String>,
    searchToken:MutableState<String>,
    searching:MutableState<Boolean>,
    resetSearchVars:()->Unit,
    contentPadding: PaddingValues,
    fromTo: String,
    curRepoFromParentPage: CustomStateSaveable<RepoEntity>,
    isFileSelectionMode:MutableState<Boolean>,
    refreshRequiredByParentPage: String,
    changeListRequireRefreshFromParentPage:(whichRepoRequestRefresh:RepoEntity) -> Unit,
    changeListPageHasIndexItem: MutableState<Boolean>,
    requireDoActFromParent:MutableState<Boolean>,
    requireDoActFromParentShowTextWhenDoingAct:MutableState<String>,
    enableActionFromParent:MutableState<Boolean>,
    repoState: MutableIntState,
    naviUp: () -> Unit,
    itemList: CustomStateListSaveable<StatusTypeEntrySaver>,
    itemListState: LazyListState,
    selectedItemList:CustomStateListSaveable<StatusTypeEntrySaver>,
    commit1OidStr:String,
    commit2OidStr:String,
    commitParentList:MutableList<String> = mutableListOf<String>(),
    repoId:String="",
    changeListPageNoRepo:MutableState<Boolean>,
    hasNoConflictItems:MutableState<Boolean>,
    goToFilesPage:(path:String) -> Unit = {},  
    changelistPageScrolled:MutableState<Boolean>,
    changeListPageFilterModeOn:MutableState<Boolean>,
    changeListPageFilterKeyWord:CustomStateSaveable<TextFieldValue>,
    filterListState:LazyListState,
    swap:Boolean,
    commitForQueryParents:String,
    rebaseCurOfAll:MutableState<String>? = null,  
    openDrawer:()->Unit,
    goToRepoPage:(targetRepoId:String)->Unit = {},  
    changeListRepoList:CustomStateListSaveable<RepoEntity>? =null,
    goToChangeListPage:(goToThisRepo:RepoEntity)->Unit ={},
    needReQueryRepoList:MutableState<String>? =null,
    newestPageId:MutableState<String>,  
    naviTarget:MutableState<String>,
    enableFilterState:MutableState<Boolean>,
    filterList:CustomStateListSaveable<StatusTypeEntrySaver>,
    lastClickedItemKey:MutableState<String>
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val commit1OidStr = commit1OidStr.ifBlank { Cons.git_AllZeroOidStr }
    val commit2OidStr = commit2OidStr.ifBlank { Cons.git_AllZeroOidStr }
    val repoId = remember(repoId, curRepoFromParentPage.value.id) { derivedStateOf { if(repoId.isBlank()) curRepoFromParentPage.value.id else repoId } }.value  
    val isDiffToLocal = commit1OidStr==Cons.git_LocalWorktreeCommitHash || commit2OidStr==Cons.git_LocalWorktreeCommitHash
    val isWorktreePage = fromTo == Cons.gitDiffFromIndexToWorktree
    val localAtDiffRight = remember(fromTo, commit1OidStr, commit2OidStr, swap) { derivedStateOf {
        (fromTo == Cons.gitDiffFromIndexToWorktree
                || (if(swap) commit1OidStr==Cons.git_LocalWorktreeCommitHash else (commit2OidStr==Cons.git_LocalWorktreeCommitHash))
        )
    } }
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current
    val exitApp = AppModel.exitApp
    val dbContainer = AppModel.dbContainer
    val navController = AppModel.navController
    val scope = rememberCoroutineScope()
    val settings = remember {
        val s = SettingsUtil.getSettingsSnapshot()
        changelistPageScrolled.value = s.showNaviButtons
        s
    }
    val actFromDiffScreen = rememberSaveable { mutableStateOf(false) }
    val doActWithLock:suspend (curRepo:RepoEntity, act: suspend ()->Unit) -> Unit =  { curRepo, act ->
        Libgit2Helper.doActWithRepoLock(
            curRepo = curRepo,
            onLockFailed = { Msg.requireShowLongDuration(Cons.repoBusyStr) }
        ) {
            act()
        }
        Unit
    }
    val username = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val showUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val afterSetUsernameAndEmailSuccessCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "afterSetUsernameAndEmailSuccessCallback") { null }
    val initSetUsernameAndEmailDialog = { curRepo:RepoEntity, callback:(()->Unit)? ->
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
                username.value = usernameFromConfig
                email.value = emailFromConfig
            }
            afterSetUsernameAndEmailSuccessCallback.value = callback
            showUsernameAndEmailDialog.value = true
        }catch (e:Exception) {
            Msg.requireShowLongDuration("init username and email dialog err: ${e.localizedMessage}")
            MyLog.e(TAG, "#initSetUsernameAndEmailDialog err: ${e.stackTraceToString()}")
        }
    }
    val doTaskOrShowSetUsernameAndEmailDialog = { curRepo:RepoEntity, task:(()->Unit)? ->
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                if(Libgit2Helper.repoUsernameAndEmailInvaild(repo)) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.plz_set_username_and_email_first))
                    initSetUsernameAndEmailDialog(curRepo, task)
                }else {
                    task?.invoke()
                }
            }
        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "#doTaskOrShowSetUsernameAndEmailDialog err: ${e.stackTraceToString()}")
        }
    }
    val pleaseSetUsernameAndEmailBeforeCommit = stringResource(R.string.please_set_username_email_before_commit)
    val changeListPageHasWorktreeItem = rememberSaveable { mutableStateOf(false) }
    val showRebaseSkipDialog = rememberSaveable { mutableStateOf(false) }
    val showRebaseAbortDialog = rememberSaveable { mutableStateOf(false) }
    val showCherrypickAbortDialog = rememberSaveable { mutableStateOf(false) }
    val showMergeAbortDialog = rememberSaveable { mutableStateOf(false) }
    val initRebaseSkipDialog = { curRepo:RepoEntity ->
        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
            showRebaseSkipDialog.value = true
        }
    }
    val initMergeAbortDialog = {
        showCherrypickAbortDialog.value = false
        showRebaseAbortDialog.value = false
        showMergeAbortDialog.value = true
    }
    val initRebaseAbortDialog = {
        showMergeAbortDialog.value = false
        showCherrypickAbortDialog.value = false
        showRebaseAbortDialog.value = true
    }
    val initCherrypickAbortDialog = {
        showMergeAbortDialog.value = false
        showRebaseAbortDialog.value = false
        showCherrypickAbortDialog.value = true
    }
    val showMergeAcceptTheirsOrOursDialog = rememberSaveable { mutableStateOf(false)}
    val mergeAcceptTheirs = rememberSaveable { mutableStateOf(false)}
    val curRepoUpstream = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepoUpstream", initValue = Upstream())
    val requireShowToast = Msg.requireShowLongDuration
    val noItemSelectedStrRes = stringResource(R.string.no_item_selected)
    val quitSelectionMode = {
        isFileSelectionMode.value=false  
        selectedItemList.value.clear()  
    }
    val filesPageAddFileToSelectedListIfAbsentElseRemove:(StatusTypeEntrySaver)->Unit = { item:StatusTypeEntrySaver ->
        val fileList = selectedItemList.value
        if (fileList.contains(item)) {
            fileList.remove(item)
        } else {
            fileList.add(item)
        }
    }
    val selectedListIsEmpty:()->Boolean = {
        selectedItemList.value.isEmpty()
    }
    val selectedListIsNotEmpty:()->Boolean = {
        selectedItemList.value.isNotEmpty()
    }
    val hasConflictItemsSelected:()->Boolean = {
        selectedItemList.value.toList().any { it.changeType == Cons.gitStatusConflict }
    }
    val isLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue)}
    val loadingText = rememberSaveable { mutableStateOf(activityContext.getString(R.string.loading))}
    val loadingOn = {text:String->
        enableActionFromParent.value=false
        loadingText.value = text
        isLoading.value=true
    }
    val loadingOff = {
        enableActionFromParent.value=true
        loadingText.value = ""
        isLoading.value=false
    }
    val loadingToken = rememberSaveable { mutableStateOf("") }
    val loadingOnByToken = { token:String, loadingText:String ->
        loadingToken.value = token
        loadingOn(loadingText)
    }
    val loadingOffByToken = { token:String ->
        if(token == loadingToken.value) {
            loadingOff()
        }
    }
    val mustSelectAllConflictBeforeCommitStrRes = stringResource(R.string.must_resolved_conflict_and_select_them_before_commit)
    val canceledStrRes = stringResource(R.string.canceled)
    val nFilesStagedStrRes = stringResource(R.string.n_files_staged)
    val bottomBarActDoneCallback= {msg:String, curRepo:RepoEntity ->
        if(msg.isNotBlank()) {
            requireShowToast(msg)
        }
        quitSelectionMode()
        changeListRequireRefreshFromParentPage(curRepo)
    }
    val showCommitMsgDialog = rememberSaveable { mutableStateOf(false)}
    val amendCommit = rememberSaveable { mutableStateOf(false)}
    val overwriteAuthor = rememberSaveable { mutableStateOf(false)}
    val commitMsg = mutableCustomStateOf(stateKeyTag, "commitMsg") { TextFieldValue("") }
    val indexIsEmptyForCommitDialog = rememberSaveable { mutableStateOf(false)}
    val commitBtnTextForCommitDialog = rememberSaveable { mutableStateOf("") }
    val upstreamRemoteOptionsList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "upstreamRemoteOptionsList", initValue = listOf<String>() )
    val upstreamSelectedRemote = rememberSaveable{mutableIntStateOf(0)} 
    val upstreamBranchSameWithLocal =rememberSaveable { mutableStateOf(true)}
    val upstreamBranchShortRefSpec = rememberSaveable { mutableStateOf("")}
    val upstreamCurBranchShortName = rememberSaveable { mutableStateOf("")}
    val upstreamCurBranchFullName = rememberSaveable { mutableStateOf("")}
    val showSetUpstreamDialog  =rememberSaveable { mutableStateOf(false)}
    val upstreamDialogOnOkText  =rememberSaveable { mutableStateOf("")}
    val afterSetUpstreamSuccessCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "afterSetUpstreamSuccessCallback") { null }
    val setUpstreamOnFinallyCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "setUpstreamOnFinallyCallback") { null }
    val initSetUpstreamDialog:(List<String>, String, String, String, (()->Unit)?) -> Unit = { remoteList, curBranchShortName, curBranchFullName, onOkText, successCallback ->
        upstreamRemoteOptionsList.value.clear()
        upstreamRemoteOptionsList.value.addAll(remoteList)
        upstreamSelectedRemote.intValue = 0  
        upstreamBranchSameWithLocal.value = true
        upstreamBranchShortRefSpec.value = curBranchShortName
        upstreamCurBranchShortName.value = curBranchShortName
        upstreamCurBranchFullName.value = curBranchFullName
        upstreamDialogOnOkText.value = onOkText
        afterSetUpstreamSuccessCallback.value = successCallback
        setUpstreamOnFinallyCallback.value = if(successCallback != null) null else { { changeListRequireRefreshFromParentPage(curRepoFromParentPage.value) } }
        showSetUpstreamDialog.value = true
    }
    val successCommitStrRes = stringResource(R.string.commit_success)
    val plzSetUpStreamForCurBranch = stringResource(R.string.please_set_upstream_for_current_branch)
    val doStageAll = { curRepo:RepoEntity ->
        ChangeListFunctions.doStage(
            curRepo = curRepo,
            requireCloseBottomBar = true,
            userParamList = true,
            paramList = itemList.value.toList(),
            fromTo = fromTo,
            selectedListIsEmpty = selectedListIsEmpty,
            requireShowToast = requireShowToast,
            noItemSelectedStrRes = noItemSelectedStrRes,
            activityContext = activityContext,
            selectedItemList = selectedItemList.value.toList(),
            loadingText = loadingText,
            nFilesStagedStrRes = nFilesStagedStrRes,
            bottomBarActDoneCallback = bottomBarActDoneCallback
        )
    }
    val doStageAll_2 = { paramList:List<StatusTypeEntrySaver>, curRepo:RepoEntity, requireCloseBottomBar:Boolean->
        ChangeListFunctions.doStage(
            curRepo = curRepo,
            requireCloseBottomBar = requireCloseBottomBar,
            userParamList = true,
            paramList = paramList,
            fromTo = fromTo,
            selectedListIsEmpty = selectedListIsEmpty,
            requireShowToast = requireShowToast,
            noItemSelectedStrRes = noItemSelectedStrRes,
            activityContext = activityContext,
            selectedItemList = selectedItemList.value.toList(),
            loadingText = loadingText,
            nFilesStagedStrRes = nFilesStagedStrRes,
            bottomBarActDoneCallback = bottomBarActDoneCallback
        )
    }
    val doAbortMerge:suspend (RepoEntity)->Unit = { curRepo:RepoEntity ->
        loadingText.value = activityContext.getString(R.string.aborting_merge)
        Repository.open(curRepo.fullSavePath).use { repo ->
            val ret = Libgit2Helper.resetHardToHead(repo)
            if(ret.hasError()) {
                requireShowToast(ret.msg)
                createAndInsertError(curRepo.id, ret.msg)
            }else {
                requireShowToast(activityContext.getString(R.string.success))
            }
        }
    }
    val forcePush_ShowDialog = rememberSaveable { mutableStateOf(false) }
    val forcePush_pushWithLease = rememberSaveable { mutableStateOf(false) }
    val forcePush_expectedRefspecForLease = rememberSaveable { mutableStateOf("") }
    val forcePush_curRepo = mutableCustomStateOf(stateKeyTag, "forcePush_curRepo") { RepoEntity(id="") }
    val initForcePushDialog = {
        val curRepo = curRepoFromParentPage.value
        forcePush_expectedRefspecForLease.value = curRepo.upstreamBranch
        forcePush_curRepo.value = curRepo
        forcePush_pushWithLease.value = false
        forcePush_ShowDialog.value = true
    }
    if(forcePush_ShowDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.push_force),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        PaddingText(
                            stringResource(R.string.will_force_overwrite_remote_branch_even_it_is_ahead_to_local),
                            color = MyStyleKt.TextColor.danger(),
                            fontWeight = null, 
                        )
                    }
                    Spacer(Modifier.height(15.dp))
                    ForcePushWithLeaseCheckBox(forcePush_pushWithLease, forcePush_expectedRefspecForLease)
                }
            },
            okTextColor = MyStyleKt.TextColor.danger(),
            okBtnText = stringResource(R.string.push),
            okBtnEnabled = forcePush_pushWithLease.value.not() || forcePush_expectedRefspecForLease.value.isNotEmpty(),
            onCancel = { forcePush_ShowDialog.value = false }
        ) {
            forcePush_ShowDialog.value = false
            val curRepo = forcePush_curRepo.value
            val forcePush_pushWithLease = forcePush_pushWithLease.value
            val forcePush_expectedRefspecForLease = forcePush_expectedRefspecForLease.value
            doJobThenOffLoading(
                loadingOn,  
                loadingOff,
                activityContext.getString(R.string.force_pushing)
            ) {
                doActWithLock(curRepo) {
                    try {
                        val success = ChangeListFunctions.doPush(
                            requireCloseBottomBar = true,
                            upstreamParam = null,
                            force = true,
                            curRepoFromParentPage = curRepo,
                            requireShowToast = requireShowToast,
                            activityContext = activityContext,
                            loadingText = loadingText,
                            bottomBarActDoneCallback = bottomBarActDoneCallback,
                            dbContainer = dbContainer,
                            forcePush_pushWithLease = forcePush_pushWithLease,
                            forcePush_expectedRefspecForLease = forcePush_expectedRefspecForLease,
                        )
                        if(success) {
                            requireShowToast(activityContext.getString(R.string.push_force_success))
                        }else {
                            requireShowToast(activityContext.getString(R.string.push_force_failed))
                        }
                    }catch (e:Exception){
                        showErrAndSaveLog(
                            logTag = TAG,
                            logMsg = "Push(Force) error: "+e.stackTraceToString(),
                            showMsg = activityContext.getString(R.string.push_force_failed)+": "+e.localizedMessage,
                            showMsgMethod = requireShowToast,
                            repoId = curRepo.id
                        )
                    }finally {
                        changeListRequireRefreshFromParentPage(curRepo)
                    }
                }
            }
        }
    }
    val openFileWithInnerEditor = { filePath:String, initMergeMode:Boolean ->
        openFileWithInnerSubPageEditor(
            context = activityContext,
            filePath = filePath,
            mergeMode = initMergeMode,
            readOnly = false,
        )
    }
    val goParentChangeList = { curRepo:RepoEntity ->
        val parentId = curRepo.parentRepoId
        if(parentId.isBlank()) {
            Msg.requireShow(activityContext.getString(R.string.not_found))
        }else {
            val target = changeListRepoList?.value?.find { it.id == parentId }
            if(target == null) {
                Msg.requireShow(activityContext.getString(R.string.not_found))
            }else {
                goToChangeListPage(target)
            }
        }
        Unit
    }
    if(requireDoActFromParent.value) {  
        requireDoActFromParent.value = false  
        val actFromDiffScreen_tmp = actFromDiffScreen.value
        actFromDiffScreen.value = false
        doJobThenOffLoading(loadingOn,
            loadingOff={
                      loadingOff() 
                      enableActionFromParent.value=true  
        }, requireDoActFromParentShowTextWhenDoingAct.value) {
            val requireAct = Cache.syncGetThenDel(Cache.Key.changeListInnerPage_requireDoActFromParent) ?: return@doJobThenOffLoading;
            MyLog.d(TAG, "requireDoActFromParent, act is: "+requireAct)
            val curRepo = curRepoFromParentPage.value
            if(requireAct == PageRequest.editIgnoreFile) {
                try {
                    Repository.open(curRepo.fullSavePath).use { repo->
                        val ignoreFilePath = Libgit2Helper.getRepoIgnoreFilePathNoEndsWithSlash(repo, createIfNonExists = true)
                        withMainContext {
                            val initMergeMode = false
                            openFileWithInnerEditor(ignoreFilePath, initMergeMode)
                        }
                    }
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?: "err")
                    MyLog.e(TAG, "get ignore file for repo '${curRepo.repoName}' err: ${e.stackTraceToString()}")
                }
            }else if(requireAct==PageRequest.goToStashPage) {
                goToStashPage(curRepo.id)
            }else if(requireAct==PageRequest.showInRepos) {
                goToRepoPage(repoId)
            }else if(requireAct==PageRequest.goParent) {
                goParentChangeList(curRepo)
            }else if(requireAct==PageRequest.pull) { 
                doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.pulling)) {
                        doActWithLock(curRepo) {
                            ChangeListFunctions.doPull(
                                curRepo = curRepo,
                                activityContext = activityContext,
                                dbContainer = dbContainer,
                                requireShowToast = requireShowToast,
                                loadingText = loadingText,
                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                changeListRequireRefreshFromParentPage = changeListRequireRefreshFromParentPage,
                                trueMergeFalseRebase = true,
                                requireCloseBottomBar = true
                            )
                        }
                    }
                }
            }else if(requireAct==PageRequest.pullRebase) {
                doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.pulling)) {
                        doActWithLock(curRepo) {
                            ChangeListFunctions.doPull(
                                curRepo = curRepo,
                                trueMergeFalseRebase = false,
                                activityContext = activityContext,
                                requireCloseBottomBar = true,
                                dbContainer = dbContainer,
                                requireShowToast = requireShowToast,
                                loadingText = loadingText,
                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                changeListRequireRefreshFromParentPage = changeListRequireRefreshFromParentPage
                            )
                        }
                    }
                }
            }else if(requireAct == PageRequest.sync) {
                doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.syncing)) {
                        doActWithLock(curRepo) {
                            try {
                                ChangeListFunctions.doSync(
                                    loadingOn = loadingOn,
                                    loadingOff = loadingOff,
                                    requireCloseBottomBar = true,
                                    trueMergeFalseRebase = true,
                                    curRepoFromParentPage = curRepo,
                                    requireShowToast = requireShowToast,
                                    activityContext = activityContext,
                                    bottomBarActDoneCallback = bottomBarActDoneCallback,
                                    plzSetUpStreamForCurBranch = plzSetUpStreamForCurBranch,
                                    initSetUpstreamDialog = initSetUpstreamDialog,
                                    loadingText = loadingText,
                                    dbContainer = dbContainer
                                )
                            }catch (e:Exception){
                                showErrAndSaveLog(TAG, "require Sync(Merge) error: "+e.stackTraceToString(), activityContext.getString(R.string.sync_merge_failed)+": "+e.localizedMessage, requireShowToast, curRepo.id)
                            }finally {
                                changeListRequireRefreshFromParentPage(curRepo)
                            }
                        }
                    }
                }
            }else if(requireAct == PageRequest.syncRebase) {
                doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.syncing)) {
                        doActWithLock(curRepo) {
                            try {
                                ChangeListFunctions.doSync(
                                    loadingOn = loadingOn,
                                    loadingOff = loadingOff,
                                    requireCloseBottomBar = true,
                                    trueMergeFalseRebase = false,
                                    curRepoFromParentPage = curRepo,
                                    requireShowToast = requireShowToast,
                                    activityContext = activityContext,
                                    bottomBarActDoneCallback = bottomBarActDoneCallback,
                                    plzSetUpStreamForCurBranch = plzSetUpStreamForCurBranch,
                                    initSetUpstreamDialog = initSetUpstreamDialog,
                                    loadingText = loadingText,
                                    dbContainer = dbContainer
                                )
                            }catch (e:Exception){
                                showErrAndSaveLog(TAG, "require Sync(Rebase) error: "+e.stackTraceToString(), activityContext.getString(R.string.sync_rebase_failed)+": "+e.localizedMessage, requireShowToast, curRepo.id)
                            }finally {
                                changeListRequireRefreshFromParentPage(curRepo)
                            }
                        }
                    }
                }
            } else {  
                doActWithLock(curRepo) {
                    if(requireAct==PageRequest.fetch) {
                        try {
                            val fetchSuccess = ChangeListFunctions.doFetch(
                                remoteNameParam = null,
                                curRepoFromParentPage = curRepo,
                                requireShowToast = requireShowToast,
                                activityContext = activityContext,
                                loadingText = loadingText,
                                dbContainer = dbContainer
                            )
                            if(fetchSuccess) {
                                requireShowToast(activityContext.getString(R.string.fetch_success))
                            }else {
                                requireShowToast(activityContext.getString(R.string.fetch_failed))
                            }
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require fetch error: "+e.stackTraceToString(), activityContext.getString(R.string.fetch_failed)+": "+e.localizedMessage, requireShowToast, curRepo.id)
                        }finally {
                            changeListRequireRefreshFromParentPage(curRepo)
                        }
                    }else if(requireAct == PageRequest.push) {
                        try {
                            val success = ChangeListFunctions.doPush(
                                requireCloseBottomBar = true,
                                upstreamParam = null,
                                force = false,
                                curRepoFromParentPage = curRepo,
                                requireShowToast = requireShowToast,
                                activityContext = activityContext,
                                loadingText = loadingText,
                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                dbContainer = dbContainer
                            )
                            if(success) {
                                requireShowToast(activityContext.getString(R.string.push_success))
                            }else {
                                requireShowToast(activityContext.getString(R.string.push_failed))
                            }
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require push error: "+e.stackTraceToString(), activityContext.getString(R.string.push_failed)+": "+e.localizedMessage, requireShowToast,curRepo.id)
                        }finally {
                            changeListRequireRefreshFromParentPage(curRepo)
                        }
                    }else if(requireAct == PageRequest.pushForce) {
                        initForcePushDialog()
                    }else if(requireAct == PageRequest.mergeAbort) {
                        initMergeAbortDialog()
                    }else if(requireAct == PageRequest.stageAll) {
                        try {
                            doStageAll(curRepo)
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require stage_all error: "+e.stackTraceToString(), activityContext.getString(R.string.stage_all_failed)+": "+e.localizedMessage, requireShowToast, curRepo.id)
                        }finally {
                            changeListRequireRefreshFromParentPage(curRepo)
                        }
                    }else if(requireAct == PageRequest.indexToWorkTree_CommitAll) {
                        try {
                            val listForStage = if(actFromDiffScreen_tmp) (Cache.getByType<List<StatusTypeEntrySaver>>(Cache.Key.diffableList_of_fromDiffScreenBackToWorkTreeChangeList)?:listOf()) else itemList.value.toList()
                            if(AppModel.devModeOn) {
                                MyLog.d(TAG, "actFromDiffScreen=$actFromDiffScreen_tmp, listForStage.isEmpty() = ${listForStage.isEmpty()}")
                            }
                            if(listForStage.isNotEmpty()) {
                                val requireCloseBottombar = false
                                doStageAll_2(listForStage, curRepo, requireCloseBottombar)
                            }
                            ChangeListFunctions.doCommit(
                                requireShowCommitMsgDialog = true,
                                cmtMsg = "",
                                requireCloseBottomBar = true,
                                curRepoFromParentPage = curRepo,
                                refreshChangeList = changeListRequireRefreshFromParentPage,
                                username = username,
                                email = email,
                                requireShowToast = requireShowToast,
                                pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                                initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                                amendCommit = amendCommit,
                                overwriteAuthor = overwriteAuthor,
                                showCommitMsgDialog = showCommitMsgDialog,
                                repoState = repoState,
                                activityContext = activityContext,
                                loadingText = loadingText,
                                repoId = repoId,
                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                fromTo = fromTo,
                                itemList = null,  
                                successCommitStrRes = successCommitStrRes,
                                indexIsEmptyForCommitDialog=indexIsEmptyForCommitDialog,
                                commitBtnTextForCommitDialog=commitBtnTextForCommitDialog,
                            )
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require commit all error: "+e.stackTraceToString(), activityContext.getString(R.string.commit_err)+": "+e.localizedMessage, requireShowToast, curRepo.id)
                        }
                    }else if(requireAct == PageRequest.commit) {
                        try {
                            ChangeListFunctions.doCommit(
                                requireShowCommitMsgDialog = true,
                                cmtMsg = "",
                                requireCloseBottomBar = true,
                                curRepoFromParentPage = curRepo,
                                refreshChangeList = changeListRequireRefreshFromParentPage,
                                username = username,
                                email = email,
                                requireShowToast = requireShowToast,
                                pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                                initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                                amendCommit = amendCommit,
                                overwriteAuthor = overwriteAuthor,
                                showCommitMsgDialog = showCommitMsgDialog,
                                repoState = repoState,
                                activityContext = activityContext,
                                loadingText = loadingText,
                                repoId = repoId,
                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                fromTo = fromTo,
                                itemList = itemList.value,
                                successCommitStrRes = successCommitStrRes,
                                indexIsEmptyForCommitDialog=indexIsEmptyForCommitDialog,
                                commitBtnTextForCommitDialog=commitBtnTextForCommitDialog,
                            )
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require commit error: "+e.stackTraceToString(), activityContext.getString(R.string.commit_failed)+": "+e.localizedMessage, requireShowToast, curRepo.id)
                        }
                    }else if(requireAct == PageRequest.mergeContinue) {
                        try {
                            val repoFullPath = curRepo.fullSavePath
                            Repository.open(repoFullPath).use {repo ->
                                val readyRet = Libgit2Helper.readyForContinueMerge(repo, activityContext)
                                if(readyRet.hasError()) {
                                    Msg.requireShowLongDuration(readyRet.msg)
                                    val errPrefix= activityContext.getString(R.string.merge_continue_err)
                                    createAndInsertError(repoId, "$errPrefix:${readyRet.msg}")
                                }else {
                                    ChangeListFunctions.doCommit(
                                        requireShowCommitMsgDialog = true,
                                        cmtMsg = "",
                                        requireCloseBottomBar = true,
                                        curRepoFromParentPage = curRepo,
                                        refreshChangeList = changeListRequireRefreshFromParentPage,
                                        username = username,
                                        email = email,
                                        requireShowToast = requireShowToast,
                                        pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                                        initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                                        amendCommit = amendCommit,
                                        overwriteAuthor = overwriteAuthor,
                                        showCommitMsgDialog = showCommitMsgDialog,
                                        repoState = repoState,
                                        activityContext = activityContext,
                                        loadingText = loadingText,
                                        repoId = repoId,
                                        bottomBarActDoneCallback = bottomBarActDoneCallback,
                                        fromTo = fromTo,
                                        itemList = itemList.value,
                                        successCommitStrRes = successCommitStrRes,
                                        indexIsEmptyForCommitDialog=indexIsEmptyForCommitDialog,
                                        commitBtnTextForCommitDialog=commitBtnTextForCommitDialog,
                                    )
                                }
                            }
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require Continue Merge error: "+e.stackTraceToString(), activityContext.getString(R.string.continue_merge_err)+": "+e.localizedMessage, requireShowToast,repoId)
                        }
                    }else if(requireAct == PageRequest.rebaseContinue) {
                        try {
                            ChangeListFunctions.doCommit(
                                requireShowCommitMsgDialog = true,
                                cmtMsg = "",
                                requireCloseBottomBar = true,
                                curRepoFromParentPage = curRepo,
                                refreshChangeList = changeListRequireRefreshFromParentPage,
                                username = username,
                                email = email,
                                requireShowToast = requireShowToast,
                                pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                                initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                                amendCommit = amendCommit,
                                overwriteAuthor = overwriteAuthor,
                                showCommitMsgDialog = showCommitMsgDialog,
                                repoState = repoState,
                                activityContext = activityContext,
                                loadingText = loadingText,
                                repoId = repoId,
                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                fromTo = fromTo,
                                itemList = itemList.value,
                                successCommitStrRes = successCommitStrRes,
                                indexIsEmptyForCommitDialog=indexIsEmptyForCommitDialog,
                                commitBtnTextForCommitDialog=commitBtnTextForCommitDialog,
                            )
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require Rebase Continue error: "+e.stackTraceToString(), activityContext.getString(R.string.rebase_continue_err)+": "+e.localizedMessage, requireShowToast,repoId)
                        }
                    }else if(requireAct == PageRequest.rebaseSkip) {
                        initRebaseSkipDialog(curRepo)
                    }else if(requireAct == PageRequest.rebaseAbort) {
                        initRebaseAbortDialog()
                    }else if(requireAct == PageRequest.cherrypickContinue) {
                        try {
                            ChangeListFunctions.doCommit(
                                requireShowCommitMsgDialog = true,
                                cmtMsg = "",
                                requireCloseBottomBar = true,
                                curRepoFromParentPage = curRepo,
                                refreshChangeList = changeListRequireRefreshFromParentPage,
                                username = username,
                                email = email,
                                requireShowToast = requireShowToast,
                                pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                                initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                                amendCommit = amendCommit,
                                overwriteAuthor = overwriteAuthor,
                                showCommitMsgDialog = showCommitMsgDialog,
                                repoState = repoState,
                                activityContext = activityContext,
                                loadingText = loadingText,
                                repoId = repoId,
                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                fromTo = fromTo,
                                itemList = itemList.value,
                                successCommitStrRes = successCommitStrRes,
                                indexIsEmptyForCommitDialog=indexIsEmptyForCommitDialog,
                                commitBtnTextForCommitDialog=commitBtnTextForCommitDialog,
                            )
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require Cherrypick Continue error: "+e.stackTraceToString(), activityContext.getString(R.string.cherrypick_continue_err)+": "+e.localizedMessage, requireShowToast,repoId)
                        }
                    }else if(requireAct == PageRequest.cherrypickAbort) {
                        initCherrypickAbortDialog()
                    }
                }
            }
        }
    }
    val getCommitRight = {
        if(swap) {
            commit1OidStr
        }else {
            commit2OidStr
        }
    }
    val getCommitLeft = {
        if(swap) {
            commit2OidStr
        }else {
            commit1OidStr
        }
    }
    val getActuallyList = {
        if(enableFilterState.value) filterList.value else itemList.value
    }
    val getActuallyListState = {
        if(enableFilterState.value) filterListState else itemListState
    }
    val quitFilterMode = {
        changeListPageFilterModeOn.value = false
        resetSearchVars()
    }
    if(showRebaseSkipDialog.value) {
        val closeDialog = {
            showRebaseSkipDialog.value = false
        }
        ConfirmDialog(
            title=stringResource(R.string.rebase_skip),
            text=stringResource(R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {
                closeDialog()
            }
        ) {  
            closeDialog()
            val curRepo = curRepoFromParentPage.value
            doJobThenOffLoading(
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                loadingText = activityContext.getString(R.string.loading)
            ) {
                doActWithLock(curRepo) {
                    try {
                        val repoFullPath = curRepo.fullSavePath
                        Repository.open(repoFullPath).use { repo ->
                            val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
                            if (usernameFromConfig.isBlank() || emailFromConfig.isBlank()) {
                                Msg.requireShowLongDuration(activityContext.getString(R.string.plz_set_username_and_email_first))
                                initSetUsernameAndEmailDialog(curRepo) {
                                    showRebaseSkipDialog.value = true
                                }
                            } else {
                                val readyRet = Libgit2Helper.rebaseSkip(repo, activityContext, usernameFromConfig, emailFromConfig, settings = settings)
                                if (readyRet.hasError()) {
                                    Msg.requireShowLongDuration(readyRet.msg)
                                    val errPrefix = activityContext.getString(R.string.rebase_skip_err)
                                    createAndInsertError(repoId, "$errPrefix:${readyRet.msg}")
                                } else {
                                    Msg.requireShow(activityContext.getString(R.string.rebase_success))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        showErrAndSaveLog(
                            TAG,
                            "require Rebase Skip error: " + e.stackTraceToString(),
                            activityContext.getString(R.string.rebase_skip_err) + ": " + e.localizedMessage,
                            requireShowToast,
                            repoId
                        )
                    } finally {
                        changeListRequireRefreshFromParentPage(curRepo)
                    }
                }
            }
        }
    }
    if(showMergeAbortDialog.value || showRebaseAbortDialog.value || showCherrypickAbortDialog.value) {
        val closeDialog = {
            showMergeAbortDialog.value = false
            showRebaseAbortDialog.value = false
            showCherrypickAbortDialog.value = false
        }
        ConfirmDialog(
            title=stringResource(R.string.abort),
            text=stringResource(R.string.abort_merge_notice_text),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {
                closeDialog()
            }
        ) {  
            val nullMergeTrueRebaseFalseCherrypick = if(showMergeAbortDialog.value) null else showRebaseAbortDialog.value
            closeDialog()
            val curRepo = curRepoFromParentPage.value
            doJobThenOffLoading(
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                loadingText = activityContext.getString(R.string.aborting)
            ) {
                doActWithLock(curRepo) {
                    if(nullMergeTrueRebaseFalseCherrypick == null) {  
                        try {
                            doAbortMerge(curRepo)
                        }catch (e:Exception){
                            showErrAndSaveLog(TAG,"require abort_merge error: "+e.stackTraceToString(), activityContext.getString(R.string.abort_merge_failed)+": "+e.localizedMessage, requireShowToast, curRepo.id)
                        }finally {
                            changeListRequireRefreshFromParentPage(curRepo)
                        }
                    }else if(nullMergeTrueRebaseFalseCherrypick) {  
                        try {
                            val repoFullPath = curRepo.fullSavePath
                            Repository.open(repoFullPath).use { repo ->
                                val readyRet = Libgit2Helper.rebaseAbort(repo)
                                if (readyRet.hasError()) {
                                    Msg.requireShowLongDuration(readyRet.msg)
                                    val errPrefix = activityContext.getString(R.string.rebase_abort_err)
                                    createAndInsertError(repoId, "$errPrefix:${readyRet.msg}")
                                } else {
                                    Msg.requireShow(activityContext.getString(R.string.rebase_aborted))
                                }
                            }
                        } catch (e: Exception) {
                            showErrAndSaveLog(
                                TAG,
                                "require Rebase Abort error: " + e.stackTraceToString(),
                                activityContext.getString(R.string.rebase_abort_err) + ": " + e.localizedMessage,
                                requireShowToast,
                                repoId
                            )
                        } finally {
                            changeListRequireRefreshFromParentPage(curRepo)
                        }
                    }else {  
                        try {
                            val repoFullPath = curRepo.fullSavePath
                            Repository.open(repoFullPath).use { repo ->
                                val readyRet = Libgit2Helper.cherrypickAbort(repo)
                                if (readyRet.hasError()) {
                                    Msg.requireShowLongDuration(readyRet.msg)
                                    val errPrefix = activityContext.getString(R.string.cherrypick_abort_err)
                                    createAndInsertError(repoId, "$errPrefix:${readyRet.msg}")
                                } else {
                                    Msg.requireShow(activityContext.getString(R.string.cherrypick_aborted))
                                }
                            }
                        } catch (e: Exception) {
                            showErrAndSaveLog(
                                TAG,
                                "require Cherrypick Abort error: " + e.stackTraceToString(),
                                activityContext.getString(R.string.cherrypick_abort_err) + ": " + e.localizedMessage,
                                requireShowToast,
                                repoId
                            )
                        } finally {
                            changeListRequireRefreshFromParentPage(curRepo)
                        }
                    }
                }
            }
        }
    }
    if(showMergeAcceptTheirsOrOursDialog.value) {
        val acceptTheirs = mergeAcceptTheirs.value
        ConfirmDialog(
            title=if(acceptTheirs) stringResource(R.string.accept_theirs) else stringResource(R.string.accept_ours),
            text=stringResource(R.string.ask_do_operation_for_selected_conflict_items),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {
                showMergeAcceptTheirsOrOursDialog.value=false
            }
        ) {  
            showMergeAcceptTheirsOrOursDialog.value=false
            val curRepo = curRepoFromParentPage.value
            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = activityContext.getString(R.string.loading)) {
                doActWithLock(curRepo) {
                    ChangeListFunctions.doAccept(
                        curRepo = curRepo,
                        acceptTheirs = acceptTheirs,
                        loadingText = loadingText,
                        activityContext = activityContext,
                        hasConflictItemsSelected = hasConflictItemsSelected,
                        requireShowToast = requireShowToast,
                        selectedItemList = selectedItemList.value.toList(),
                        repoState = repoState,
                        repoId = repoId,
                        fromTo = fromTo,
                        selectedListIsEmpty = selectedListIsEmpty,
                        noItemSelectedStrRes = noItemSelectedStrRes,
                        nFilesStagedStrRes = nFilesStagedStrRes,
                        bottomBarActDoneCallback = bottomBarActDoneCallback,
                        changeListRequireRefreshFromParentPage = changeListRequireRefreshFromParentPage,
                    )
                }
            }
        }
    }
    if(showSetUpstreamDialog.value) {
        val curRepo = curRepoFromParentPage.value
        SetUpstreamDialog(
            callerTag = TAG,
            curRepo = curRepo,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            showClear = false,  
            remoteList = upstreamRemoteOptionsList.value,
            curBranchShortName = upstreamCurBranchShortName.value,  
            curBranchFullName = upstreamCurBranchFullName.value,
            selectedOption = upstreamSelectedRemote,
            upstreamBranchShortName = upstreamBranchShortRefSpec,
            upstreamBranchShortNameSameWithLocal = upstreamBranchSameWithLocal,
            onOkText = upstreamDialogOnOkText.value,
            isCurrentBranchOfRepo = true,
            onSuccessCallback = {
                requireShowToast(activityContext.getString(R.string.upstream_saved))
                val cb = afterSetUpstreamSuccessCallback.value
                afterSetUpstreamSuccessCallback.value = null
                cb?.invoke()
            },
            onErrorCallback = {
                requireShowToast(activityContext.getString(R.string.set_upstream_error))
            },
            closeDialog = {
                showSetUpstreamDialog.value = false
            },
            onCancel = {
                showSetUpstreamDialog.value = false
                changeListRequireRefreshFromParentPage(curRepo)
            },
            onClearErrorCallback = {},
            onClearSuccessCallback = {},
            onClearFinallyCallback = null, 
            onFinallyCallback = setUpstreamOnFinallyCallback.value,
        )
    }
    val commitSuccessCallback = {
        commitMsg.value = TextFieldValue("")
    }
    if(showCommitMsgDialog.value) {
        val curRepo = curRepoFromParentPage.value
        val repoIsNotDetached = !dbIntToBool(curRepo.isDetached)
        RequireCommitMsgDialog(
            stateKeyTag = stateKeyTag,
            curRepo = curRepo,
            repoPath = curRepo.fullSavePath,
            repoState=repoState.intValue,
            overwriteAuthor=overwriteAuthor,
            amend=amendCommit,
            commitMsg=commitMsg,
            commitBtnText = commitBtnTextForCommitDialog.value,
            showPush = repoIsNotDetached,
            showSync = repoIsNotDetached,
            onOk={curRepo, msgOrAmendMsg, requirePush, requireSync->
                showCommitMsgDialog.value = false  
                val cmtMsg = msgOrAmendMsg  
                doJobThenOffLoading(loadingOn, loadingOff,activityContext.getString(R.string.committing)) {
                    doActWithLock(curRepo) {
                        try {
                            val commitSuccess = ChangeListFunctions.doCommit(
                                requireShowCommitMsgDialog = false,
                                cmtMsg = cmtMsg,
                                requireCloseBottomBar = !(requireSync || requirePush),
                                curRepoFromParentPage = curRepo,
                                refreshChangeList = changeListRequireRefreshFromParentPage,
                                username = username,
                                email = email,
                                requireShowToast = requireShowToast,
                                pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                                initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                                amendCommit = amendCommit,
                                overwriteAuthor = overwriteAuthor,
                                showCommitMsgDialog = showCommitMsgDialog,
                                repoState = repoState,
                                activityContext = activityContext,
                                loadingText = loadingText,
                                repoId = repoId,
                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                fromTo = fromTo,
                                itemList = itemList.value,
                                successCommitStrRes = successCommitStrRes,
                                indexIsEmptyForCommitDialog=indexIsEmptyForCommitDialog,
                                commitBtnTextForCommitDialog=commitBtnTextForCommitDialog,
                            )
                            if(commitSuccess) {  
                                commitSuccessCallback()
                                if(requireSync) {
                                    loadingText.value = activityContext.getString(R.string.syncing)
                                    ChangeListFunctions.doSync(
                                        loadingOn = loadingOn,
                                        loadingOff = loadingOff,
                                        requireCloseBottomBar = true,
                                        trueMergeFalseRebase = !SettingsUtil.pullWithRebase(),
                                        curRepoFromParentPage = curRepo,
                                        requireShowToast = requireShowToast,
                                        activityContext = activityContext,
                                        bottomBarActDoneCallback = bottomBarActDoneCallback,
                                        plzSetUpStreamForCurBranch = plzSetUpStreamForCurBranch,
                                        initSetUpstreamDialog = initSetUpstreamDialog,
                                        loadingText = loadingText,
                                        dbContainer = dbContainer
                                    )
                                }else if(requirePush) {
                                    loadingText.value = activityContext.getString(R.string.pushing)
                                    val success = ChangeListFunctions.doPush(
                                        requireCloseBottomBar = true,
                                        upstreamParam = null,
                                        force = false,
                                        curRepoFromParentPage = curRepo,
                                        requireShowToast = requireShowToast,
                                        activityContext = activityContext,
                                        loadingText = loadingText,
                                        bottomBarActDoneCallback = bottomBarActDoneCallback,
                                        dbContainer = dbContainer
                                    )
                                    if(success) {
                                        requireShowToast(activityContext.getString(R.string.push_success))
                                    }else {
                                        requireShowToast(activityContext.getString(R.string.push_failed))
                                    }
                                }
                            }else {  
                                if(requireSync) {
                                    requireShowToast(activityContext.getString(R.string.sync_canceled_by_commit_failed))
                                }else if(requirePush) {
                                    requireShowToast(activityContext.getString(R.string.push_canceled_by_commit_failed))
                                }
                            }
                        }catch (e:Exception){
                            MyLog.e(TAG, "#doCommit at showCommitMsgDialog #onOk: " + e.stackTraceToString())
                        }
                    }
                }
            },
            indexIsEmptyForCommitDialog = indexIsEmptyForCommitDialog,
            onCancel={curRepo ->
                showCommitMsgDialog.value = false
                changeListRequireRefreshFromParentPage(curRepo)
            },
        )
    }
    if(showUsernameAndEmailDialog.value) {
        val curRepo = curRepoFromParentPage.value
        val closeDialog = { showUsernameAndEmailDialog.value = false }
        AskGitUsernameAndEmailDialogWithSelection(
            curRepo = curRepo,
            username = username,
            email = email,
            closeDialog = closeDialog,
            onCancel = {
                closeDialog()
                changeListRequireRefreshFromParentPage(curRepo)
            },
            onErrorCallback = { e->
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                MyLog.e(TAG, "set username and email err (from ChangeList page): ${e.stackTraceToString()}")
            },
            onFinallyCallback = {},
            onSuccessCallback = {
                val successCallback = afterSetUsernameAndEmailSuccessCallback.value
                afterSetUsernameAndEmailSuccessCallback.value = null
                successCallback?.invoke()
            },
        )
    }
    val showCherrypickDialog = rememberSaveable { mutableStateOf(false)}
    val cherrypickTargetHash = rememberSaveable { mutableStateOf("")}
    val cherrypickParentHash = rememberSaveable { mutableStateOf("")}
    val cherrypickAutoCommit = rememberSaveable { mutableStateOf(false)}
    val initCherrypickDialog = { curRepo: RepoEntity ->
        doJobThenOffLoading job@{
            Repository.open(curRepo.fullSavePath).use { repo->
                val ret=Libgit2Helper.resolveCommitByHashOrRef(repo, commit1OidStr)
                if(ret.hasError() || ret.data==null) {
                    Msg.requireShowLongDuration(ret.msg)
                    return@job
                }
                cherrypickParentHash.value =ret.data!!.id().toString()
                val ret2=Libgit2Helper.resolveCommitByHashOrRef(repo, commit2OidStr)
                if(ret2.hasError() || ret2.data==null) {
                    Msg.requireShowLongDuration(ret2.msg)
                    return@job
                }
                cherrypickTargetHash.value = ret2.data!!.id().toString()
                cherrypickAutoCommit.value = false
                showCherrypickDialog.value = true
            }
        }
    }
    if(showCherrypickDialog.value) {
        val shortTarget = Libgit2Helper.getShortOidStrByFull(cherrypickTargetHash.value)
        val shortParent = Libgit2Helper.getShortOidStrByFull(cherrypickParentHash.value)
        ConfirmDialog(
            title = stringResource(R.string.cherrypick),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    Text(
                        text =  buildAnnotatedString {
                            append(stringResource(R.string.target)+": ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                append(shortTarget)
                            }
                        },
                        modifier = Modifier.padding(horizontal = MyStyleKt.defaultHorizontalPadding)
                    )
                    Text(
                        text =  buildAnnotatedString {
                            append(stringResource(R.string.parent)+": ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                append(shortParent)
                            }
                        },
                        modifier = Modifier.padding(horizontal = MyStyleKt.defaultHorizontalPadding)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.will_cherrypick_changes_of_selected_files_are_you_sure),
                        modifier = Modifier.padding(horizontal = MyStyleKt.defaultHorizontalPadding)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    MyCheckBox(text = stringResource(R.string.auto_commit), value = cherrypickAutoCommit)
                }
            },
            onCancel = { showCherrypickDialog.value = false }
        ) {
            showCherrypickDialog.value = false
            val curRepo = curRepoFromParentPage.value
            val selectedItemList = selectedItemList.value.toList()
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.cherrypicking)) {
                doActWithLock(curRepo) {
                    val pathSpecs = selectedItemList.map { it.relativePathUnderRepo }
                    Repository.open(curRepo.fullSavePath).use { repo->
                        val ret = Libgit2Helper.cherrypick(
                            repo,
                            targetCommitFullHash = cherrypickTargetHash.value,
                            parentCommitFullHash = cherrypickParentHash.value,
                            pathSpecList = pathSpecs,
                            autoCommit = cherrypickAutoCommit.value,
                            settings = settings
                        )
                        if(ret.hasError()) {
                            Msg.requireShowLongDuration(ret.msg)
                            if(ret.code != Ret.ErrCode.alreadyUpToDate) {  
                                createAndInsertError(repoId, "cherrypick files changes of '$shortParent..$shortTarget' err: "+ret.msg)
                            }
                        }else {
                            Msg.requireShow(activityContext.getString(R.string.success))
                        }
                    }
                }
            }
        }
    }
    val showIgnoreDialog = rememberSaveable { mutableStateOf(false)}
    if(showIgnoreDialog.value) {
        val curRepo = curRepoFromParentPage.value
        GitIgnoreDialog(
            showIgnoreDialog = showIgnoreDialog,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            activityContext = activityContext,
            getRepository = {
                try {
                    Repository.open(curRepo.fullSavePath).let {
                        if(it == null) {
                            throw RuntimeException("resolve repo failed")
                        }
                        it
                    }
                }catch (e:Exception) {
                    MyLog.e(TAG, "#getRepository err: ${e.stackTraceToString()}")
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    null
                }
            },
            getIgnoreItems = { _:String ->  
                val selectedItemList = selectedItemList.value.toList()
                if(selectedItemList.isEmpty()) {
                    Msg.requireShowLongDuration(noItemSelectedStrRes)
                }
                selectedItemList.map { IgnoreItem(pathspec = it.relativePathUnderRepo, isFile = it.toFile().isFile) }
            },
            onCatch = { e:Exception ->
                val errMsg = e.localizedMessage
                Msg.requireShowLongDuration("err: " + errMsg)
                createAndInsertError(curRepo.id, "ignore files err: $errMsg")
            },
            onFinally = {
                changeListRequireRefreshFromParentPage(curRepo)
            }
        )
    }
    val showCreatePatchDialog = rememberSaveable { mutableStateOf(false)}
    val savePatchPath= rememberSaveable { mutableStateOf("") } 
    val showSavePatchSuccessDialog = rememberSaveable { mutableStateOf(false)}
    if(showSavePatchSuccessDialog.value) {
        CreatePatchSuccessDialog(
            path = savePatchPath.value,
            closeDialog = {showSavePatchSuccessDialog.value = false}
        )
    }
    if(showCreatePatchDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.create_patch),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(text = stringResource(R.string.will_create_patch_for_selected_files_are_you_sure))
                    }
                }
            },
            onCancel = { showCreatePatchDialog.value = false }
        ){
            showCreatePatchDialog.value = false
            val curRepo = curRepoFromParentPage.value
            doJobThenOffLoading(loadingOn,loadingOff, activityContext.getString(R.string.creating_patch)) job@{
                try {
                    val left = getCommitLeft()
                    val right = getCommitRight()
                    val savePatchRet = ChangeListFunctions.createPath(
                        curRepo = curRepo,
                        leftCommit = left,
                        rightCommit = right,
                        fromTo = if(left == Cons.git_IndexCommitHash && right == Cons.git_LocalWorktreeCommitHash) Cons.gitDiffFromIndexToWorktree else fromTo,
                        relativePaths = selectedItemList.value.map { it.relativePathUnderRepo }
                    );
                    if(savePatchRet.success()) {
                        savePatchPath.value = savePatchRet.data?.outFileFullPath ?: ""
                        showSavePatchSuccessDialog.value = true
                    }else {
                        throw (savePatchRet.exception ?: RuntimeException(savePatchRet.msg))
                    }
                }catch (e:Exception) {
                    val errPrefix = "create patch err: "
                    Msg.requireShowLongDuration(e.localizedMessage ?: errPrefix)
                    createAndInsertError(curRepo.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, "$errPrefix${e.stackTraceToString()}")
                }
            }
        }
    }
    val checkoutForce = rememberSaveable { mutableStateOf(false) }
    val showCheckoutFilesDialog = rememberSaveable { mutableStateOf(false) }
    val checkoutTarget = rememberSaveable { mutableStateOf("") }
    val checkoutList = mutableCustomStateListOf(stateKeyTag, "checkoutList") { listOf<String>() }
    val leftFullHash = rememberSaveable { mutableStateOf("") }
    val rightFullHash = rememberSaveable { mutableStateOf("") }
    val initCheckoutDialog = { curRepo:RepoEntity, targetHash:String ->
        val left = getCommitLeft()
        val right = getCommitRight()
        try {
            leftFullHash.value = ""
            rightFullHash.value = ""
            Repository.open(curRepo.fullSavePath).use { repo->
                val (left, right) = Libgit2Helper.getLeftRightCommitDto(repo, left, right, repoId, settings)
                leftFullHash.value = left.oidStr
                rightFullHash.value = right.oidStr
            }
        }catch (e: Exception) {
            MyLog.d(TAG, "resolve left and right to commit hash err: ${e.stackTraceToString()}")
        }
        checkoutList.value.apply {
            clear()
            add(left)
            add(right)
        }
        if(checkoutTarget.value != left && checkoutTarget.value != right) {
            checkoutTarget.value = left
        }
        checkoutForce.value = false
        showCheckoutFilesDialog.value = true
    }
    if(showCheckoutFilesDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.checkout),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    SingleSelection(
                        itemList = checkoutList.value,
                        selected = {idx, item -> item == checkoutTarget.value},
                        text = {idx, item ->
                            val isLeft = idx == 0;
                            val shortHash = Libgit2Helper.getShortOidStrByFullIfIsHash(item)
                            val shortLeft = Libgit2Helper.getShortOidStrByFullIfIsHash(leftFullHash.value)
                            val shortRight = Libgit2Helper.getShortOidStrByFullIfIsHash(rightFullHash.value)
                            activityContext.getString(if(isLeft) R.string.left else R.string.right) + ": " + shortHash + (
                                    if(isLeft && shortHash != shortLeft) {
                                        " ($shortLeft)"
                                    }else if(isLeft.not() && shortHash != shortRight) {
                                        " ($shortRight)"
                                    }else {
                                        ""
                                    }
                            )
                        },
                        onClick = {idx, item -> checkoutTarget.value = item}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    MyCheckBox(text = stringResource(R.string.force), value = checkoutForce)
                    if(checkoutForce.value) {
                        MySelectionContainer {
                            DefaultPaddingText(
                                text = stringResource(R.string.if_local_has_uncommitted_changes_will_overwrite),
                                color = MyStyleKt.TextColor.danger(),
                            )
                        }
                    }
                }
            },
            okTextColor = if(checkoutForce.value) MyStyleKt.TextColor.danger() else Color.Unspecified,
            onCancel = { showCheckoutFilesDialog.value = false }
        ) onOk@{
            showCheckoutFilesDialog.value = false
            if(checkoutTarget.value == Cons.git_LocalWorktreeCommitHash) {
                Msg.requireShowLongDuration(activityContext.getString(R.string.already_up_to_date))
                return@onOk
            }
            val curRepo = curRepoFromParentPage.value
            val selectedItemList = selectedItemList.value.toList()
            val targetHash = checkoutTarget.value
            val checkoutForce = checkoutForce.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.checking_out)) {
                doActWithLock(curRepo) job@{
                    val pathSpecs = selectedItemList.map{it.relativePathUnderRepo}
                    Repository.open(curRepo.fullSavePath).use { repo->
                        val checkoutTargetHash = if(targetHash == Cons.git_IndexCommitHash) {  
                            targetHash
                        }else {
                            val commitRet = Libgit2Helper.resolveCommitByHashOrRef(repo, targetHash)
                            if (commitRet.hasError() || commitRet.data == null) {
                                Msg.requireShowLongDuration(commitRet.msg)
                                return@job
                            }
                            commitRet.data!!.id().toString()
                        }
                        val ret = Libgit2Helper.checkoutFiles(repo, checkoutTargetHash, pathSpecs, force=checkoutForce)
                        if(ret.hasError()) {
                            Msg.requireShowLongDuration(ret.msg)
                            createAndInsertError(repoId, "checkout files err: "+ret.msg)
                        }else {
                            Msg.requireShow(activityContext.getString(R.string.success))
                        }
                        if(checkoutList.value.contains(Cons.git_LocalWorktreeCommitHash)) {
                            changeListRequireRefreshFromParentPage(curRepo)
                        }
                    }
                }
            }
        }
    }
    val selectItem = { item: StatusTypeEntrySaver ->
        isFileSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseNoop(item, selectedItemList.value)
    }
    val selectAll = {
        val list = if(enableFilterState.value) filterList.value else itemList.value
        list.toList().forEachBetter {
            selectItem(it)
        }
        Unit
    }
    val revertStrRes = stringResource(R.string.revert_n_and_del_m_files)
    val showRevertAlert = rememberSaveable { mutableStateOf(false)}
    val doRevert = { curRepo:RepoEntity, selectedItemList:List<StatusTypeEntrySaver> ->
                if(selectedListIsEmpty()) {  
                    requireShowToast(noItemSelectedStrRes)
                }else{
                    loadingText.value = activityContext.getString(R.string.reverting)
                    Repository.open(curRepo.fullSavePath).use { repo ->
                        val untrakcedFileList = mutableListOf<String>()  
                        val pathspecList = mutableListOf<String>()  
                        selectedItemList.forEachBetter {
                            if(it.changeType == Cons.gitStatusNew) {
                                untrakcedFileList.add(it.canonicalPath)  
                            }else if(it.changeType != Cons.gitStatusConflict){  
                                pathspecList.add(it.relativePathUnderRepo)
                            }
                        }
                        if(pathspecList.isNotEmpty()) {
                            Libgit2Helper.revertFilesToIndexVersion(repo, pathspecList)
                        }
                        if(untrakcedFileList.isNotEmpty()) {
                            Libgit2Helper.rmUntrackedFiles(untrakcedFileList)
                        }
                        val revertedCount = pathspecList.size.toString()
                        val deletedCount = untrakcedFileList.size.toString()
                        val msg = replaceStringResList(revertStrRes, listOf(revertedCount, deletedCount))
                        bottomBarActDoneCallback(msg, curRepo)
                    }
                }
        }
    val doUnstage = doUnstage@{ curRepo:RepoEntity, selectedItemList:List<StatusTypeEntrySaver> ->
        loadingText.value = activityContext.getString(R.string.unstaging)
        Repository.open(curRepo.fullSavePath).use {repo ->
            val refspecList = mutableListOf<String>()
            selectedItemList.forEachBetter {
                refspecList.add(it.relativePathUnderRepo)
            }
            Libgit2Helper.unStageItems(repo, refspecList)
        }
        bottomBarActDoneCallback(activityContext.getString(R.string.unstage_success), curRepo)
    }
    val showUnstageConfirmDialog = rememberSaveable { mutableStateOf(false)}
    if(showUnstageConfirmDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.unstage),
            text = stringResource(R.string.will_unstage_are_u_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showUnstageConfirmDialog.value = false }
        ) {
            showUnstageConfirmDialog.value = false
            val curRepo = curRepoFromParentPage.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.unstaging)) {
                doActWithLock(curRepo) {
                    doUnstage(curRepo, selectedItemList.value.toList())
                }
            }
        }
    }
    val credentialList = mutableCustomStateListOf(stateKeyTag, "credentialList", listOf<CredentialEntity>())
    val selectedCredentialIdx = rememberSaveable{mutableIntStateOf(0)}
    val importList = mutableCustomStateListOf(stateKeyTag, "importList", listOf<StatusTypeEntrySaver>())
    val jumpAfterImportRepo = rememberSaveable { mutableStateOf(false) }
    val showImportToReposDialog = rememberSaveable { mutableStateOf(false) }
    if(showImportToReposDialog.value){
        ConfirmDialog2(
            title = activityContext.getString(R.string.import_as_repo),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        DefaultPaddingText(stringResource(R.string.will_try_import_selected_dirs_as_repos))
                    }
                    Spacer(Modifier.height(15.dp))
                    CredentialSelector(credentialList.value.toList(), selectedCredentialIdx)
                    Spacer(Modifier.height(10.dp))
                    MySelectionContainer {
                        DefaultPaddingText(stringResource(R.string.import_repos_link_credential_note))
                    }
                }
            },
            onCancel = { showImportToReposDialog.value = false },
        ) {
            showImportToReposDialog.value = false
            val curRepo = curRepoFromParentPage.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.importing)) {
                val repoNameSuffix = Libgit2Helper.genRepoNameSuffixForSubmodule(curRepo.repoName)
                val parentRepoId = curRepo.id
                val importList = importList.value.toList()
                val credentialList = credentialList.value.toList()
                val selectedCredentialId = credentialList[selectedCredentialIdx.intValue].id
                val repoDb = AppModel.dbContainer.repoRepository
                val importRepoResult = ImportRepoResult()
                val importSuccess = Box(false)
                try {
                    importList.forEachBetter {

                        val result = repoDb.importRepos(dir=it.canonicalPath, isReposParent=false, repoNameSuffix = repoNameSuffix, parentRepoId = parentRepoId, credentialId = selectedCredentialId)
                        importRepoResult.all += result.all
                        importRepoResult.success += result.success
                        importRepoResult.failed += result.failed
                        importRepoResult.existed += result.existed

                    }
                    importSuccess.value = true

                    Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.n_imported), listOf(""+importRepoResult.success)))
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage
                    Msg.requireShowLongDuration(errMsg ?: "import err")
                    createAndInsertError(curRepo.id, "import repo(s) err: $errMsg")
                    MyLog.e(TAG, "import repo(s) from ChangeList err: importRepoResult=$importRepoResult, err="+e.stackTraceToString())
                }finally {
                    if(needReQueryRepoList != null) {
                        if(importSuccess.value && jumpAfterImportRepo.value && importList.isNotEmpty()) {
                            val firstItem = importList.getOrNull(0)
                            if(firstItem != null) {
                                changeStateTriggerRefreshPage(needReQueryRepoList, requestType = StateRequestType.jumpAfterImport, data=firstItem.canonicalPath)
                            }
                        }else {
                            changeStateTriggerRefreshPage(needReQueryRepoList)
                        }
                    }
                }
            }
        }
    }
    fun initImportAsRepo(
        selectedItemList:List<StatusTypeEntrySaver>,
        jumpAfterImport:Boolean = false,
    ) {
        val tmplist = selectedItemList.filter { it.toFile().isDirectory }
        if(tmplist.isEmpty()) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.no_dir_selected))
        }else {
            jumpAfterImportRepo.value = jumpAfterImport
            importList.value.clear()
            importList.value.addAll(tmplist)

            showImportToReposDialog.value=true
        }
    }
    val switchItemSelected = { item:StatusTypeEntrySaver ->
        if(isFileSelectionMode.value.not()) {
            selectedItemList.value.clear()
            isFileSelectionMode.value = true
        }
        filesPageAddFileToSelectedListIfAbsentElseRemove(item)
    }
    val getSelectedFilesCount = {
        selectedItemList.value.size
    }
    val isItemInSelected:(StatusTypeEntrySaver)->Boolean = { item:StatusTypeEntrySaver ->
        selectedItemList.value.contains(item)
    }
    val showOpenAsDialog = rememberSaveable { mutableStateOf(false)}
    val readOnlyForOpenAsDialog = rememberSaveable { mutableStateOf(false)}
    val openAsDialogFilePath = rememberSaveable { mutableStateOf("")}
    val openAsDialogFileName = rememberSaveable { mutableStateOf("")}
    if(showOpenAsDialog.value) {
        OpenAsDialog(readOnly = readOnlyForOpenAsDialog,fileName = openAsDialogFileName.value, filePath = openAsDialogFilePath.value) {
            showOpenAsDialog.value=false
        }
    }
    fun diffFiles(curItem:StatusTypeEntrySaver, itemListOrFilterList:List<StatusTypeEntrySaver>, commit1OidStr:String, commit2OidStr:String, fromTo:String) {
        SharedState.homeChangeList_itemList = itemList.value
        SharedState.homeChangeList_indexHasItem = changeListPageHasIndexItem
        var indexAtDiffableList = -1
        val diffableList = mutableListOf<StatusTypeEntrySaver>()
        val itemCopy = itemListOrFilterList.toList()
        for(idx in itemCopy.indices) {
            val item = itemCopy[idx]
            if(item.changeType != Cons.gitStatusConflict) {
                diffableList.add(item)
                if(item == curItem) {
                    indexAtDiffableList = diffableList.lastIndex
                }
            }
        }
        naviTarget.value = Cons.ChangeListNaviTarget_NoNeedReload
        goToDiffScreen(
            diffableList = diffableList.map { it.toDiffableItem() },
            repoId = curItem.repoIdFromDb,
            fromTo = fromTo,
            commit1OidStr = commit1OidStr,
            commit2OidStr = commit2OidStr,
            isDiffToLocal = commit1OidStr==Cons.git_LocalWorktreeCommitHash || commit2OidStr==Cons.git_LocalWorktreeCommitHash,
            curItemIndexAtDiffableList = indexAtDiffableList,
            localAtDiffRight = commit2OidStr==Cons.git_LocalWorktreeCommitHash,
            fromScreen = (if(fromTo == Cons.gitDiffFromIndexToWorktree) DiffFromScreen.HOME_CHANGELIST.code
            else if(fromTo == Cons.gitDiffFromHeadToIndex) DiffFromScreen.INDEX.code
            else DiffFromScreen.TREE_TO_TREE.code),
        )
    }
    val goToSub = { item:StatusTypeEntrySaver ->
        val target = changeListRepoList?.value?.find { item.toFile().canonicalPath == it.fullSavePath }
        if(target == null) {
            initImportAsRepo(listOf(item), jumpAfterImport = true)
        }else {
            goToChangeListPage(target)
        }
        Unit
    }
    val menuKeyTextList = listOf(
        stringResource(R.string.open),
        stringResource(R.string.open_as),
        stringResource(R.string.show_in_files),
        Libgit2Helper.getLeftToRightFullHash(if(fromTo == Cons.gitDiffFromHeadToIndex) Cons.git_HeadCommitHash else stringResource(R.string.left), stringResource(R.string.local)) ,
        Libgit2Helper.getLeftToRightFullHash(if(fromTo == Cons.gitDiffFromHeadToIndex) stringResource(R.string.index) else stringResource(R.string.right), stringResource(R.string.local)) ,
        stringResource(R.string.file_history),
        stringResource(R.string.copy_full_path),
        stringResource(R.string.copy_repo_relative_path),
        stringResource(R.string.import_as_repo),
        stringResource(R.string.go_sub),
    )
    val menuKeyActList = listOf(
        open@{ item:StatusTypeEntrySaver ->
            if(!item.toFile().exists()) {
                requireShowToast(activityContext.getString(R.string.file_doesnt_exist))
                return@open
            }
            naviTarget.value = Cons.ChangeListNaviTarget_NoNeedReload
            openFileWithInnerEditor(item.canonicalPath, item.changeType == Cons.gitStatusConflict)
        },
        openAs@{item:StatusTypeEntrySaver ->
            if(!item.toFile().exists()) {
                requireShowToast(activityContext.getString(R.string.file_doesnt_exist))
                return@openAs
            }
            openAsDialogFilePath.value = item.canonicalPath
            openAsDialogFileName.value=item.fileName
            showOpenAsDialog.value=true
        },
        showInFiles@{item:StatusTypeEntrySaver ->
            if(!item.toFile().exists()) {
                requireShowToast(activityContext.getString(R.string.file_doesnt_exist))
                return@showInFiles
            }
            goToFilesPage(item.canonicalPath)
        },
        leftToLocal@{
            if(fromTo == Cons.gitDiffFromHeadToIndex) {
                diffFiles(
                    curItem = it,
                    itemListOrFilterList = getActuallyList(),
                    commit1OidStr = Cons.git_HeadCommitHash,
                    commit2OidStr = Cons.git_LocalWorktreeCommitHash,
                    fromTo = Cons.gitDiffFromTreeToTree
                )
            }else {
                diffFiles(
                    curItem = it,
                    itemListOrFilterList = getActuallyList(),
                    commit1OidStr = getCommitLeft(),
                    commit2OidStr = Cons.git_LocalWorktreeCommitHash,
                    fromTo = Cons.gitDiffFromTreeToTree
                )
            }
        },
        rightToLocal@{
            if(fromTo == Cons.gitDiffFromHeadToIndex) {
                diffFiles(
                    curItem = it,
                    itemListOrFilterList = getActuallyList(),
                    commit1OidStr = Cons.git_IndexCommitHash,
                    commit2OidStr = Cons.git_LocalWorktreeCommitHash,
                    fromTo = Cons.gitDiffFromTreeToTree
                )
            }else {
                diffFiles(
                    curItem = it,
                    itemListOrFilterList = getActuallyList(),
                    commit1OidStr = getCommitRight(),
                    commit2OidStr = Cons.git_LocalWorktreeCommitHash,
                    fromTo = Cons.gitDiffFromTreeToTree
                )
            }
        },
        fileHistory@{item:StatusTypeEntrySaver ->
            naviToFileHistoryByRelativePath(repoId, item.relativePathUnderRepo)
        },
        copyFullPath@{item:StatusTypeEntrySaver ->
            clipboardManager.setText(AnnotatedString(item.canonicalPath))
            Msg.requireShow(activityContext.getString(R.string.copied))
        },
        copyRepoRelativePath@{item:StatusTypeEntrySaver ->
            clipboardManager.setText(AnnotatedString(item.relativePathUnderRepo))
            Msg.requireShow(activityContext.getString(R.string.copied))
        },
        importAsRepo@{

            initImportAsRepo(listOf(it))
        },
        goToSub@{
            goToSub(it)
        }
    )
    val menuKeyEnableList:List<(StatusTypeEntrySaver)->Boolean> = listOf(
        openEnabled@{true },  
        openAsEnabled@{true },
        showInFilesEnabled@{fromTo == Cons.gitDiffFromIndexToWorktree},  
        leftToLocal@{ fromTo == Cons.gitDiffFromTreeToTree || fromTo == Cons.gitDiffFromHeadToIndex },
        rightToLocal@{ fromTo == Cons.gitDiffFromTreeToTree || fromTo == Cons.gitDiffFromHeadToIndex },
        fileHistoryEnabled@{it.maybeIsFileAndExist()},
        copyFullPath@{true},
        copyRepoRelativePath@{true},
        importAsRepo@{ it.toFile().isDirectory }, 

        goToSub@{fromTo == Cons.gitDiffFromIndexToWorktree && it.toFile().isDirectory},  
    )
    val menuKeyVisibleList:List<(StatusTypeEntrySaver)->Boolean> = menuKeyEnableList
    val errMsg = rememberSaveable { mutableStateOf("")}
    val setErrMsg = {msg:String ->
        errMsg.value = msg
        hasError.value = true
    }
    val clearErrMsg = {
        hasError.value=false
        errMsg.value="";
    }
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = getBackHandler(
        appContext = activityContext,
        exitApp = exitApp,
        isFileSelectionMode = isFileSelectionMode,
        quitSelectionMode = quitSelectionMode,
        fromTo = fromTo,
        naviUp = naviUp,
        changeListPageFilterModeOn = changeListPageFilterModeOn,
        quitFilterMode = quitFilterMode,
        openDrawer = openDrawer
    )
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false)}
    if(showSelectedItemsShortDetailsDialog.value) {
        val closeDialog = { showSelectedItemsShortDetailsDialog.value = false }
        SelectedFileItemsDialog(
            list = selectedItemList.value,
            itemName = { it.fileName },
            itemPath = { it.relativePathUnderRepo },
            itemIsDir = { it.maybeIsDirAndExist() },
            removeItem = { switchItemSelected(it) },
            clearAll = { selectedItemList.value.clear() },
            showFileIcon = true,
            fileIconOnClick = {
                val predicate = { item: StatusTypeEntrySaver ->
                    item.relativePathUnderRepo == it.relativePathUnderRepo
                }
                var found = Box(false)
                UIHelper.scrollByPredicate(scope, getActuallyList(), getActuallyListState()) { idx, item ->
                    if(predicate(item)) {
                        found.value = true
                        true
                    }else {
                        false
                    }
                }
                if(!found.value && enableFilterState.value) {
                    val index = itemList.value.indexOfFirst { item ->
                        predicate(item)
                    }
                    if(index >= 0) {
                        found.value = true
                        quitFilterMode()
                        UIHelper.scrollToItem(scope, itemListState, index + Cons.scrollToItemOffset)
                    }
                }
                if(found.value) {
                    closeDialog()
                }
            },
            closeDialog = closeDialog
        )
    }
    val countNumOnClickForBottomBar = {
        showSelectedItemsShortDetailsDialog.value = true
    }
    if(showRevertAlert.value) {
        ConfirmDialog(
            title=stringResource(R.string.revert),
            text=stringResource(R.string.will_revert_modified_or_deleted_and_rm_new_files_are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showRevertAlert.value=false}
        ) {  
            showRevertAlert.value=false
            val curRepo = curRepoFromParentPage.value
            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = activityContext.getString(R.string.reverting)) {
                doActWithLock(curRepo) {
                    doRevert(curRepo, selectedItemList.value.toList())
                }
            }
        }
    }
    val showRemoveFromGitDialog = rememberSaveable { mutableStateOf(false) }
    if(showRemoveFromGitDialog.value) {
        ConfirmDialog(
            title = stringResource(id = R.string.remove_from_git),
            text = stringResource(R.string.will_remove_selected_items_from_git_are_u_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showRemoveFromGitDialog.value=false }
        ) {
            showRemoveFromGitDialog.value = false
            val curRepoFromParentPage = curRepoFromParentPage.value
            doJobThenOffLoading (loadingOn = loadingOn, loadingOff=loadingOff) {
                val selectedItemList = selectedItemList.value.toList()
                if(selectedItemList.isEmpty()) {
                    Msg.requireShowLongDuration(noItemSelectedStrRes)
                    return@doJobThenOffLoading  
                }
                try {
                    Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
                        val repoWorkDirFullPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                        MyLog.d(TAG, "#RemoveFromGitDialog: will remove files from repo workdir: '${repoWorkDirFullPath}'")
                        val repoIndex = repo.index()
                        selectedItemList.forEachBetter {
                            Libgit2Helper.removeFromGit(repoIndex, it.relativePathUnderRepo, it.toFile().isFile)
                        }
                        repoIndex.write()
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                    changeListRequireRefreshFromParentPage(curRepoFromParentPage)
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage
                    Msg.requireShowLongDuration("err: $errMsg")
                    createAndInsertError(curRepoFromParentPage.id, "remove from git err: $errMsg")
                    MyLog.e(TAG, "#RemoveFromGitDialog: remove from git err: ${e.stackTraceToString()}")
                    changeListRequireRefreshFromParentPage(curRepoFromParentPage)
                }
            }
        }
    }
    val iconModifier = MyStyleKt.Icon.modifier
    PullToRefreshBox(
        contentPadding = contentPadding,
        onRefresh = { changeListRequireRefreshFromParentPage(curRepoFromParentPage.value) },
    ) {
        if(isLoading.value) {
            LoadingTextSimple(text = loadingText.value, contentPadding = contentPadding)
        }else {
            if(hasError.value) {  
                FullScreenScrollableColumn(contentPadding) {
                    MySelectionContainer {
                        Text(errMsg.value, color = MyStyleKt.TextColor.error())
                    }
                }
            }else {  
                val curRepoOnUi = curRepoFromParentPage.value
                val curRepoUpstreamOnUi = curRepoUpstream.value
                if(itemList.value.isEmpty()) {  
                    FullScreenScrollableColumn(contentPadding) {
                        if(fromTo == Cons.gitDiffFromIndexToWorktree) {  
                            MySelectionContainer {
                                Text(text = stringResource(R.string.work_tree_clean))
                            }
                            MySelectionContainer {
                                Row(modifier = Modifier.padding(top = 10.dp)) {
                                    if (changeListPageHasIndexItem.value){  
                                        ClickableText(
                                            text =  stringResource(R.string.index_dirty),
                                            modifier = MyStyleKt.ClickableText.modifierNoPadding
                                                .clickable {  
                                                    navController.navigate(Cons.nav_IndexScreen)
                                                }
                                            ,
                                        )
                                    } else{  
                                        Text(text =  stringResource(R.string.index_clean))
                                    }
                                }
                            }
                            if (!dbIntToBool(curRepoOnUi.isDetached)) {
                                var upstreamNotSet = false
                                val fontSizeOfPullPushSync = 16.sp
                                val splitHorizonPadding = 10.dp
                                Column(
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                    ,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (curRepoOnUi.ahead != 0 || curRepoOnUi.behind != 0
                                        || curRepoUpstreamOnUi.branchRefsHeadsFullRefSpec.isBlank()
                                        || (curRepoUpstreamOnUi.branchRefsHeadsFullRefSpec.isNotBlank() && !curRepoUpstreamOnUi.isPublished)
                                    ) {
                                        if (curRepoOnUi.ahead != 0) {
                                            MySelectionContainer {
                                                Row {
                                                    Text(text = stringResource(R.string.local_ahead) + ": " + curRepoOnUi.ahead)
                                                }
                                            }
                                        }
                                        if (curRepoOnUi.behind != 0) {
                                            MySelectionContainer {
                                                Row {  
                                                    Text(text = stringResource(R.string.local_behind) + ": " + curRepoOnUi.behind)
                                                }
                                            }
                                        }
                                        if(curRepoUpstreamOnUi.branchRefsHeadsFullRefSpec.isBlank()) {
                                            upstreamNotSet = true
                                            MySelectionContainer {
                                                Row {  
                                                    Text(text = stringResource(R.string.no_upstream))
                                                }
                                            }
                                        }
                                        if(curRepoUpstreamOnUi.branchRefsHeadsFullRefSpec.isNotBlank() && !curRepoUpstreamOnUi.isPublished) {
                                            MySelectionContainer {
                                                Row {  
                                                    Text(text = stringResource(R.string.upstream_not_published))
                                                }
                                            }
                                        }
                                        if(curRepoOnUi.behind != 0) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                LongPressAbleIconBtn(
                                                    iconModifier = iconModifier,
                                                    tooltipText = stringResource(R.string.rebase),
                                                    icon = ImageVector.vectorResource(R.drawable.git_rebase),
                                                    iconContentDesc = stringResource(R.string.rebase),
                                                ) {
                                                    val curRepo = curRepoFromParentPage.value
                                                    doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                                        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.rebasing)) {
                                                            doActWithLock(curRepo) {
                                                                ChangeListFunctions.doMerge(
                                                                    requireCloseBottomBar = true,
                                                                    upstreamParam = null,
                                                                    showMsgIfHasConflicts = true,
                                                                    trueMergeFalseRebase = false,
                                                                    curRepoFromParentPage = curRepo,
                                                                    requireShowToast = requireShowToast,
                                                                    activityContext = activityContext,
                                                                    loadingText = loadingText,
                                                                    bottomBarActDoneCallback = bottomBarActDoneCallback
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                LongPressAbleIconBtn(
                                                    iconModifier = iconModifier,
                                                    tooltipText = stringResource(R.string.merge),
                                                    icon = Icons.Filled.Merge ,
                                                    iconContentDesc = stringResource(R.string.merge),
                                                ) {
                                                    val curRepo = curRepoFromParentPage.value
                                                    doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                                        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.merging)) {
                                                            doActWithLock(curRepo) {
                                                                ChangeListFunctions.doMerge(
                                                                    requireCloseBottomBar = true,
                                                                    upstreamParam = null,
                                                                    showMsgIfHasConflicts = true,
                                                                    trueMergeFalseRebase = true,
                                                                    curRepoFromParentPage = curRepo,
                                                                    requireShowToast = requireShowToast,
                                                                    activityContext = activityContext,
                                                                    loadingText = loadingText,
                                                                    bottomBarActDoneCallback = bottomBarActDoneCallback
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        MySelectionContainer {
                                            Text(text = stringResource(id = R.string.already_up_to_date))
                                        }
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        val syncText = stringResource(if(upstreamNotSet) R.string.set_upstream_and_sync else R.string.sync)
                                        val syncIcon = ImageVector.vectorResource(R.drawable.two_way_sync)
                                        LongPressAbleIconBtn(
                                            iconModifier = iconModifier,
                                            tooltipText = syncText,
                                            icon = syncIcon ,
                                            iconContentDesc = syncText,
                                        ) {
                                            val curRepo = curRepoFromParentPage.value
                                            doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.syncing)) {
                                                    doActWithLock(curRepo) {
                                                        try {
                                                            ChangeListFunctions.doSync(
                                                                loadingOn = loadingOn,
                                                                loadingOff = loadingOff,
                                                                requireCloseBottomBar = true,
                                                                trueMergeFalseRebase = !SettingsUtil.pullWithRebase(),
                                                                curRepoFromParentPage = curRepo,
                                                                requireShowToast = requireShowToast,
                                                                activityContext = activityContext,
                                                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                                                plzSetUpStreamForCurBranch = plzSetUpStreamForCurBranch,
                                                                initSetUpstreamDialog = initSetUpstreamDialog,
                                                                loadingText = loadingText,
                                                                dbContainer = dbContainer
                                                            )
                                                        } catch (e: Exception) {
                                                            showErrAndSaveLog(
                                                                logTag = TAG,
                                                                logMsg = "sync error: " + e.stackTraceToString(),
                                                                showMsg = activityContext.getString(R.string.sync_failed) + ": " + e.localizedMessage,
                                                                showMsgMethod = requireShowToast,
                                                                repoId = curRepo.id
                                                            )
                                                        } finally {
                                                            changeListRequireRefreshFromParentPage(curRepo)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if(!upstreamNotSet) {
                                            LongPressAbleIconBtn(
                                                iconModifier = iconModifier,
                                                tooltipText = stringResource(R.string.push),
                                                icon =  Icons.Filled.Upload,
                                                iconContentDesc = stringResource(id = R.string.push),
                                            ) {
                                                val curRepo = curRepoFromParentPage.value
                                                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.pushing)) {
                                                    doActWithLock(curRepo) {
                                                        try {
                                                            val success = ChangeListFunctions.doPush(
                                                                requireCloseBottomBar = true,
                                                                upstreamParam = null,
                                                                force = false,
                                                                curRepoFromParentPage = curRepo,
                                                                requireShowToast = requireShowToast,
                                                                activityContext = activityContext,
                                                                loadingText = loadingText,
                                                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                                                dbContainer = dbContainer
                                                            )
                                                            if (!success) {
                                                                requireShowToast(activityContext.getString(R.string.push_failed))
                                                            } else {
                                                                requireShowToast(activityContext.getString(R.string.push_success))
                                                            }
                                                        } catch (e: Exception) {
                                                            showErrAndSaveLog(
                                                                logTag = TAG,
                                                                logMsg = "push err: " + e.stackTraceToString(),
                                                                showMsg = activityContext.getString(R.string.push_failed) + ": " + e.localizedMessage,
                                                                showMsgMethod = requireShowToast,
                                                                repoId = curRepo.id
                                                            )
                                                        } finally {
                                                            changeListRequireRefreshFromParentPage(curRepo)
                                                        }
                                                    }
                                                }
                                            }
                                            LongPressAbleIconBtn(
                                                iconModifier = iconModifier,
                                                tooltipText = stringResource(R.string.pull),
                                                icon =  Icons.Filled.Download,
                                                iconContentDesc = stringResource(id = R.string.pull),
                                            ) {
                                                val curRepo = curRepoFromParentPage.value
                                                doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.pulling)) {
                                                        doActWithLock(curRepo) {
                                                            ChangeListFunctions.doPull(
                                                                curRepo = curRepo,
                                                                activityContext = activityContext,
                                                                dbContainer = dbContainer,
                                                                requireShowToast = requireShowToast,
                                                                loadingText = loadingText,
                                                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                                                changeListRequireRefreshFromParentPage = changeListRequireRefreshFromParentPage,
                                                                trueMergeFalseRebase = !SettingsUtil.pullWithRebase(),
                                                                requireCloseBottomBar = true
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(!dbIntToBool(curRepoOnUi.isDetached) && curRepoUpstreamOnUi.branchRefsHeadsFullRefSpec.isNotBlank()) {
                                MySelectionContainer {
                                    Row(Modifier.padding(top = 10.dp)) {
                                        ClickableText(
                                            text = stringResource(id = R.string.check_update),
                                            modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
                                                val curRepo = curRepoFromParentPage.value
                                                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.fetching)) {
                                                    doActWithLock(curRepo) {
                                                        try {
                                                            val fetchSuccess = ChangeListFunctions.doFetch(
                                                                remoteNameParam = null,
                                                                curRepoFromParentPage = curRepo,
                                                                requireShowToast = requireShowToast,
                                                                activityContext = activityContext,
                                                                loadingText = loadingText,
                                                                dbContainer = dbContainer
                                                            )
                                                            if (fetchSuccess) {
                                                                requireShowToast(
                                                                    activityContext.getString(
                                                                        R.string.fetch_success
                                                                    )
                                                                )
                                                            } else {
                                                                requireShowToast(
                                                                    activityContext.getString(
                                                                        R.string.fetch_failed
                                                                    )
                                                                )
                                                            }
                                                        } catch (e: Exception) {
                                                            showErrAndSaveLog(
                                                                logTag = TAG,
                                                                logMsg = "fetch err: " + e.stackTraceToString(),
                                                                showMsg = activityContext.getString(R.string.fetch_failed) + ": " + e.localizedMessage,
                                                                showMsgMethod = requireShowToast,
                                                                repoId = curRepo.id
                                                            )
                                                        } finally {
                                                            changeListRequireRefreshFromParentPage(curRepo)
                                                        }
                                                    }
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                            if(UserUtil.isPro()) {
                                Row(modifier=Modifier.padding(top=18.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if(dev_EnableUnTestedFeature || tagsTestPassed) {
                                        LongPressAbleIconBtn(
                                            enabled = true,
                                            iconModifier = iconModifier,
                                            tooltipText = stringResource(R.string.tags),
                                            icon = Icons.AutoMirrored.Filled.Label,
                                            iconContentDesc = stringResource(R.string.tags),
                                        ) {  
                                            navController.navigate(Cons.nav_TagListScreen + "/" + repoId)
                                        }
                                    }
                                    LongPressAbleIconBtn(
                                        enabled = true,
                                        iconModifier = iconModifier,
                                        tooltipText = stringResource(R.string.commit_history),
                                        icon =  Icons.Filled.History,
                                        iconContentDesc = stringResource(id = R.string.commit_history),
                                        ) {  
                                        val curRepo = curRepoFromParentPage.value
                                        goToCommitListScreen(
                                            repoId = curRepo.id,
                                            fullOid = "",  
                                            shortBranchName = "",
                                            isHEAD = true,
                                            from = CommitListFrom.FOLLOW_HEAD,
                                        )
                                    }
                                    LongPressAbleIconBtn(
                                        enabled = true,
                                        iconModifier = iconModifier,
                                        tooltipText = stringResource(R.string.branches),
                                        icon = ImageVector.vectorResource(id = R.drawable.branch),
                                        iconContentDesc = stringResource(id = R.string.branches),
                                        ) { 
                                        navController.navigate(Cons.nav_BranchListScreen + "/" + curRepoFromParentPage.value.id)
                                    }
                                    LongPressAbleIconBtn(
                                        enabled = true,
                                        iconModifier = iconModifier,
                                        tooltipText = stringResource(R.string.files),
                                        icon =  Icons.Filled.Folder,
                                        iconContentDesc = stringResource(id = R.string.files),
                                        ) { 
                                        goToFilesPage(curRepoFromParentPage.value.fullSavePath)
                                    }
                                }
                            }
                        }else if(fromTo == Cons.gitDiffFromHeadToIndex) {  
                            MySelectionContainer {
                                Text(text = stringResource(id = R.string.index_clean))
                            }
                        }else {  
                            MySelectionContainer {
                                Text(text = stringResource(id = R.string.no_difference_found))
                            }
                        }
                    }
                }else {  
                    val keyword = changeListPageFilterKeyWord.value.text  
                    val enableFilter = filterModeActuallyEnabled(filterOn = changeListPageFilterModeOn.value, keyword = keyword)
                    val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
                    val itemListOrFilterList = filterTheList(
                        needRefresh = filterResultNeedRefresh.value,
                        lastNeedRefresh = lastNeedRefresh,
                        enableFilter = enableFilter,
                        keyword = keyword,
                        lastKeyword = lastSearchKeyword,
                        searching = searching,
                        token = searchToken,
                        activityContext = activityContext,
                        filterList = filterList.value,
                        list = itemList.value,
                        resetSearchVars = resetSearchVars,
                        match = { idx:Int, it: StatusTypeEntrySaver ->
                            it.fileName.let { it.contains(keyword, ignoreCase = true) || RegexUtil.matchWildcard(it, keyword) }
                                    || it.relativePathUnderRepo.contains(keyword, ignoreCase = true)
                                    || it.getSizeStr().contains(keyword, ignoreCase = true)
                                    || it.getChangeTypeAndSuffix(isDiffToLocal).contains(keyword, ignoreCase = true)
                                    || it.getItemTypeString().contains(keyword, ignoreCase = true)
                        }
                    )
                    val listState = if(enableFilter) filterListState else itemListState
                    enableFilterState.value = enableFilter
                    MyLazyColumn(
                        contentPadding = contentPadding,
                        list = itemListOrFilterList,
                        listState = listState,
                        requireForEachWithIndex = true,
                        requirePaddingAtBottom = true,
                        forEachCb = {}
                    ) { index, it:StatusTypeEntrySaver ->
                        ChangeListItem(
                            item = it,
                            isFileSelectionMode = isFileSelectionMode,
                            menuKeyTextList=menuKeyTextList,
                            menuKeyActList=menuKeyActList,
                            menuKeyEnableList=menuKeyEnableList,
                            menuKeyVisibleList=menuKeyVisibleList,
                            fromTo=fromTo,
                            isDiffToLocal = isDiffToLocal,
                            lastClickedItemKey = lastClickedItemKey,
                            switchItemSelected=switchItemSelected,
                            isItemInSelected=isItemInSelected,
                            onLongClick= lc@{
                                if(fromTo == Cons.gitDiffFromTreeToTree && !proFeatureEnabled(treeToTreeBottomBarActAtLeastOneTestPassed())) {
                                    return@lc
                                }
                                if (!isFileSelectionMode.value) {
                                    switchItemSelected(it)
                                }else {
                                    UIHelper.doSelectSpan(index, it,
                                        selectedItemList.value, itemListOrFilterList,
                                        switchItemSelected,
                                        selectItem
                                    )
                                }
                            }
                        ){  
                            if (isFileSelectionMode.value) {
                                switchItemSelected(it)
                            } else {
                                if(it.changeType == Cons.gitStatusConflict) {  
                                    naviTarget.value = Cons.ChangeListNaviTarget_NoNeedReload
                                    val initMergeMode = true  
                                    openFileWithInnerEditor(it.canonicalPath, initMergeMode)
                                }
                                else {  
                                    diffFiles(
                                        curItem = it,
                                        itemListOrFilterList = itemListOrFilterList,
                                        commit1OidStr = getCommitLeft(),
                                        commit2OidStr = getCommitRight(),
                                        fromTo = fromTo
                                    )
                                }
                            }
                        }
                        MyHorizontalDivider()
                    }
                    if (isFileSelectionMode.value) {
                        val iconList:List<ImageVector> = if(fromTo == Cons.gitDiffFromIndexToWorktree) listOf(  
                            Icons.Filled.DoneAll,  
                            Icons.Filled.SelectAll,  
                        )else if(fromTo==Cons.gitDiffFromHeadToIndex) listOf(  
                            Icons.Outlined.Check,  
                            Icons.Outlined.SelectAll  
                        )else if(fromTo == Cons.gitDiffFromTreeToTree) listOf(
                            Icons.Filled.Inventory,  
                            Icons.Filled.Download,  
                            ImageVector.vectorResource(R.drawable.outline_nutrition_24),  
                            Icons.Filled.LibraryAdd,  
                            Icons.Outlined.SelectAll, 
                        ) else listOf()
                        val iconTextList:List<String> = if(fromTo == Cons.gitDiffFromIndexToWorktree) listOf(  
                            stringResource(id = R.string.commit_selected_and_index_items),
                            stringResource(id = R.string.select_all),
                        )else if(fromTo == Cons.gitDiffFromHeadToIndex) listOf(  
                            stringResource(id = R.string.commit_all_index_items),
                            stringResource(id = R.string.select_all),
                        ) else listOf(  
                            stringResource(R.string.import_as_repo),
                            stringResource(R.string.checkout),
                            stringResource(R.string.cherrypick),
                            stringResource(R.string.create_patch),
                            stringResource(id = R.string.select_all),
                        )
                        val iconVisibleList = if(fromTo == Cons.gitDiffFromTreeToTree) listOf(
                            imortAsRepo@{ true },
                            checkoutFile@{ proFeatureEnabled(checkoutFilesTestPassed) },
                            cherrypickFiles@{ proFeatureEnabled(cherrypickTestPassed) },
                            createPatch@{ proFeatureEnabled(createPatchTestPassed) },
                            selectedAll@{ true }
                        ) else listOf()
                        val enableImportAsRepo = {
                            selectedItemList.value.toList().any { it.toFile().isDirectory }
                        }
                        val enableAcceptOursTheirs = {
                            val repoState = repoState.intValue
                            (repoState == Repository.StateT.MERGE.bit || repoState == Repository.StateT.REBASE_MERGE.bit || repoState == Repository.StateT.CHERRYPICK.bit) && hasConflictItemsSelected()
                        }
                        val iconEnableList:List<()->Boolean> = if(fromTo == Cons.gitDiffFromIndexToWorktree) listOf( 
                            selectedListIsNotEmpty,  
                            {true},  
                        ) else if(fromTo==Cons.gitDiffFromHeadToIndex) listOf( 
                            { true }, 
                            {true} 
                        )else listOf( 
                            enableImportAsRepo, 
                            selectedListIsNotEmpty,  
                            {commitParentList.isNotEmpty() && selectedListIsNotEmpty()},  
                            selectedListIsNotEmpty,  
                            {true}  
                        )
                        val moreItemEnableList:List<()->Boolean> = (if(fromTo == Cons.gitDiffFromIndexToWorktree) listOf( 
                            enableAcceptOursTheirs,  
                            enableAcceptOursTheirs,  
                            selectedListIsNotEmpty,  
                            selectedListIsNotEmpty,  
                            selectedListIsNotEmpty, 
                            selectedListIsNotEmpty, 
                            selectedListIsNotEmpty, 
                            enableImportAsRepo, 
                        ) else if(fromTo == Cons.gitDiffFromHeadToIndex) listOf( 
                            selectedListIsNotEmpty,  
                            selectedListIsNotEmpty, 
                            enableImportAsRepo, 
                        ) else listOf(  
                        ))
                        val iconOnClickList:List<()->Unit> = if(fromTo == Cons.gitDiffFromIndexToWorktree) listOf(  
                            commit@{
                                val curRepo = curRepoFromParentPage.value
                                doJobThenOffLoading(loadingOn=loadingOn,loadingOff=loadingOff, loadingText=activityContext.getString(R.string.committing)) {
                                    doActWithLock(curRepo) {
                                        val stageSuccess = ChangeListFunctions.doStage(
                                            curRepo = curRepo,
                                            requireCloseBottomBar = false,
                                            userParamList = false,
                                            paramList = null,
                                            fromTo = fromTo,
                                            selectedListIsEmpty = selectedListIsEmpty,
                                            requireShowToast = requireShowToast,
                                            noItemSelectedStrRes = noItemSelectedStrRes,
                                            activityContext = activityContext,
                                            selectedItemList = selectedItemList.value.toList(),
                                            loadingText = loadingText,
                                            nFilesStagedStrRes = nFilesStagedStrRes,
                                            bottomBarActDoneCallback = bottomBarActDoneCallback
                                        )
                                        if(!stageSuccess){
                                            bottomBarActDoneCallback(activityContext.getString(R.string.stage_failed), curRepo)
                                        }else {
                                            ChangeListFunctions.doCommit(
                                                requireShowCommitMsgDialog = true,
                                                cmtMsg = "",
                                                requireCloseBottomBar = true,
                                                curRepoFromParentPage = curRepo,
                                                refreshChangeList = changeListRequireRefreshFromParentPage,
                                                username = username,
                                                email = email,
                                                requireShowToast = requireShowToast,
                                                pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                                                initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                                                amendCommit = amendCommit,
                                                overwriteAuthor = overwriteAuthor,
                                                showCommitMsgDialog = showCommitMsgDialog,
                                                repoState = repoState,
                                                activityContext = activityContext,
                                                loadingText = loadingText,
                                                repoId = repoId,
                                                bottomBarActDoneCallback = bottomBarActDoneCallback,
                                                fromTo = fromTo,
                                                itemList = itemList.value,
                                                successCommitStrRes = successCommitStrRes,
                                                indexIsEmptyForCommitDialog=indexIsEmptyForCommitDialog,
                                                commitBtnTextForCommitDialog=commitBtnTextForCommitDialog,
                                            )
                                        }
                                    }
                                }
                            },
                            selectAll@{
                                selectAll()
                            },
                        ) else if(fromTo == Cons.gitDiffFromHeadToIndex) listOf(  
                            commit@{
                                val curRepo = curRepoFromParentPage.value
                                doJobThenOffLoading(loadingOn=loadingOn,loadingOff=loadingOff, loadingText=activityContext.getString(R.string.committing)) {
                                    doActWithLock(curRepo) {
                                        ChangeListFunctions.doCommit(
                                            requireShowCommitMsgDialog = true,
                                            cmtMsg = "",
                                            requireCloseBottomBar = true,
                                            curRepoFromParentPage = curRepo,
                                            refreshChangeList = changeListRequireRefreshFromParentPage,
                                            username = username,
                                            email = email,
                                            requireShowToast = requireShowToast,
                                            pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                                            initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                                            amendCommit = amendCommit,
                                            overwriteAuthor = overwriteAuthor,
                                            showCommitMsgDialog = showCommitMsgDialog,
                                            repoState = repoState,
                                            activityContext = activityContext,
                                            loadingText = loadingText,
                                            repoId = repoId,
                                            bottomBarActDoneCallback = bottomBarActDoneCallback,
                                            fromTo = fromTo,
                                            itemList = itemList.value,
                                            successCommitStrRes = successCommitStrRes,
                                            indexIsEmptyForCommitDialog=indexIsEmptyForCommitDialog,
                                            commitBtnTextForCommitDialog=commitBtnTextForCommitDialog,
                                        )
                                    }
                                }
                            },
                            selectAll@{
                                selectAll()
                            }
                        )else {  
                            listOf(  
                                importAsRepo@{

                                    initImportAsRepo(selectedItemList.value.toList())
                                },
                                checkout@{
                                    val curRepo = curRepoFromParentPage.value
                                    val target = if(localAtDiffRight.value) getCommitLeft() else getCommitRight()
                                    initCheckoutDialog(curRepo, target)
                                },
                                cherrypick@{
                                    val curRepo = curRepoFromParentPage.value
                                    if(commitParentList.isNotEmpty()) {
                                        initCherrypickDialog(curRepo)
                                    }else {
                                        Msg.requireShowLongDuration(activityContext.getString(R.string.cherrypick_only_work_for_diff_to_parents))
                                    }
                                },
                                createPatch@{
                                    showCreatePatchDialog.value = true
                                },
                                selectAll@{
                                    selectAll()
                                }
                            )
                        }
                        val moreItemTextList = (if(fromTo == Cons.gitDiffFromIndexToWorktree) listOf(
                            stringResource(R.string.accept_ours),
                            stringResource(R.string.accept_theirs),
                            UIHelper.bottomBarDividerPlaceHolder,
                            stringResource(R.string.stage),
                            stringResource(R.string.revert),
                            stringResource(R.string.create_patch),
                            if(proFeatureEnabled(ignoreWorktreeFilesTestPassed)) stringResource(R.string.ignore) else "",  
                            stringResource(R.string.remove_from_git),
                            stringResource(R.string.import_as_repo),
                            )  
                        else if(fromTo == Cons.gitDiffFromHeadToIndex) listOf(stringResource(R.string.unstage), stringResource(R.string.create_patch), stringResource(R.string.import_as_repo),)
                        else listOf(  
                        ))
                        val moreItemOnClickList:List<()->Unit> = (if(fromTo == Cons.gitDiffFromIndexToWorktree) listOf(
                            acceptOurs@{
                                if(!UserUtil.isPro()) {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.this_feature_is_pro_only))
                                    return@acceptOurs
                                }
                                mergeAcceptTheirs.value=false
                                showMergeAcceptTheirsOrOursDialog.value=true
                            },
                            acceptTheirs@{
                                if(!UserUtil.isPro()) {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.this_feature_is_pro_only))
                                    return@acceptTheirs
                                }
                                mergeAcceptTheirs.value=true
                                showMergeAcceptTheirsOrOursDialog.value=true
                            },
                            stage@{
                                doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText=activityContext.getString(R.string.staging)) {
                                    val curRepo = curRepoFromParentPage.value
                                    doActWithLock(curRepo) {
                                        ChangeListFunctions.doStage(
                                            curRepo = curRepo,
                                            requireCloseBottomBar = true,
                                            userParamList = false,
                                            paramList = null,
                                            fromTo = fromTo,
                                            selectedListIsEmpty = selectedListIsEmpty,
                                            requireShowToast = requireShowToast,
                                            noItemSelectedStrRes = noItemSelectedStrRes,
                                            activityContext = activityContext,
                                            selectedItemList = selectedItemList.value.toList(),
                                            loadingText = loadingText,
                                            nFilesStagedStrRes = nFilesStagedStrRes,
                                            bottomBarActDoneCallback = bottomBarActDoneCallback
                                        )
                                    }
                                }
                                Unit
                            },
                            revert@{
                                showRevertAlert.value = true
                            },
                            createPatch@{
                                showCreatePatchDialog.value = true
                            },
                            ignore@{
                                showIgnoreDialog.value = true
                            },
                            removeFromGit@{
                                showRemoveFromGitDialog.value = true
                            },
                            importAsRepo@{

                                initImportAsRepo(selectedItemList.value.toList())
                            }
                        ) else if(fromTo == Cons.gitDiffFromHeadToIndex) listOf(
                            unstage@{
                                showUnstageConfirmDialog.value = true
                            },
                            createPatch@{
                                showCreatePatchDialog.value = true
                            },
                            importAsRepo@{

                                initImportAsRepo(selectedItemList.value.toList())
                            }
                        ) else listOf( 
                        ))
                        BottomBar(
                            quitSelectionMode=quitSelectionMode,
                            iconList=iconList,
                            iconTextList=iconTextList,
                            iconDescTextList=iconTextList,
                            iconOnClickList=iconOnClickList,
                            iconEnableList=iconEnableList,
                            iconVisibleList = iconVisibleList,
                            moreItemTextList=moreItemTextList,
                            moreItemOnClickList=moreItemOnClickList,
                            moreItemEnableList = moreItemEnableList,
                            moreItemVisibleList = moreItemEnableList,
                            countNumOnClickEnabled = true,
                            getSelectedFilesCount = getSelectedFilesCount,
                            countNumOnClick = countNumOnClickForBottomBar,
                            reverseMoreItemList = true
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(refreshRequiredByParentPage) {
        try {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#LaunchedEffect: naviTarget.value=${naviTarget.value}")
            }
            val (requestType, payloadRepoId) = getRequestDataByState<String?>(refreshRequiredByParentPage)
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#LaunchedEffect: requestType=$requestType, payloadRepoId=$payloadRepoId")
            }
            if(requestType == StateRequestType.withRepoId && payloadRepoId != null) {
                val curRepo = curRepoFromParentPage.value
                val repoMaybeValid = curRepo.fullSavePath.isNotBlank() && curRepo.id.isNotBlank()
                if(repoMaybeValid && payloadRepoId != curRepo.id) {
                    return@LaunchedEffect
                }
            }
            if(naviTarget.value == Cons.ChangeListNaviTarget_NoNeedReload) {
                naviTarget.value = Cons.ChangeListNaviTarget_InitValue
                val actuallyList = if(enableFilterState.value) filterList.value else itemList.value
                val actuallyListState = if(enableFilterState.value) filterListState else itemListState
                val lastClickedItemKey = if(fromTo == Cons.gitDiffFromIndexToWorktree) SharedState.homeChangeList_LastClickedItemKey.value else if(fromTo == Cons.gitDiffFromHeadToIndex) SharedState.index_LastClickedItemKey.value else SharedState.treeToTree_LastClickedItemKey.value
                doJobThenOffLoading {
                    delay(500)
                    UIHelper.scrollByPredicate(scope, actuallyList, actuallyListState) { idx, item ->
                        item.getItemKey() == lastClickedItemKey
                    }
                }
                if(requestType == StateRequestType.indexToWorkTree_CommitAll) {
                    Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.indexToWorkTree_CommitAll)
                    actFromDiffScreen.value = true
                    requireDoActFromParentShowTextWhenDoingAct.value = activityContext.getString(R.string.committing)
                    requireDoActFromParent.value = true
                    enableActionFromParent.value=false  
                }else if(requestType == StateRequestType.headToIndex_CommitAll) {
                    Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.commit)
                    requireDoActFromParentShowTextWhenDoingAct.value = activityContext.getString(R.string.committing)
                    requireDoActFromParent.value = true
                    enableActionFromParent.value=false
                }
                triggerReFilter(filterResultNeedRefresh)
                return@LaunchedEffect
            }
            val currentPageId = refreshRequiredByParentPage
            newestPageId.value = currentPageId
            val loadingToken = getShortUUID()
            doJobThenOffLoading(
                loadingOn={ loadingText ->
                    loadingOnByToken(loadingToken, loadingText)
                },
                loadingOff={
                    loadingOffByToken(loadingToken)
                },
                loadingText = activityContext.getString(R.string.loading)
            ) {
                try {
                    changeListInit(
                        dbContainer = dbContainer,
                        activityContext = activityContext,
                        curRepoUpstream=curRepoUpstream,
                        isFileSelectionMode = isFileSelectionMode,
                        changeListPageNoRepo = changeListPageNoRepo,
                        changeListPageHasIndexItem = changeListPageHasIndexItem,
                        changeListPageHasWorktreeItem = changeListPageHasWorktreeItem,
                        itemList = itemList,
                        requireShowToast=requireShowToast,
                        curRepoFromParentPage = curRepoFromParentPage,
                        selectedItemList = selectedItemList,
                        fromTo = fromTo,
                        repoState=repoState,
                        commit1OidStr = commit1OidStr,
                        commit2OidStr=commit2OidStr,
                        commitParentList=commitParentList,
                        repoId = repoId,
                        setErrMsg=setErrMsg,
                        clearErrMsg=clearErrMsg,
                        loadingOn=loadingOn,
                        loadingOff=loadingOff,
                        hasNoConflictItems=hasNoConflictItems,
                        swap=swap,
                        commitForQueryParents=commitForQueryParents,
                        rebaseCurOfAll=rebaseCurOfAll,
                        credentialList=credentialList,
                        quitSelectionMode = quitSelectionMode,
                        repoChanged = {
                            val repoChanged = currentPageId != newestPageId.value
                            if(repoChanged) {
                                MyLog.d(TAG, "Repo Changed!")
                            }
                            repoChanged
                        }
                    )
                    triggerReFilter(filterResultNeedRefresh)
                }catch (e:Exception) {
                    val curRepo = curRepoFromParentPage.value
                    val prefix = "init ChangeList err"
                    val errMsgForUsers = "$prefix: ${e.localizedMessage}"
                    setErrMsg(errMsgForUsers)
                    showErrAndSaveLog(
                        logTag = TAG,
                        logMsg = "#LaunchedEffect: $prefix, params are: fromTo=${fromTo}, commit1OidStr=${commit1OidStr}, commit2OidStr=${commit2OidStr}, curRepo=${curRepo}\nerr is: "+e.stackTraceToString(),
                        showMsg = errMsgForUsers,
                        showMsgMethod = Msg.requireShowLongDuration,
                        repoId = curRepo.id
                    )
                }
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect err: ${e.stackTraceToString()}")
        }
    }
}
private suspend fun changeListInit(
    dbContainer: AppContainer,
    activityContext: Context,
    curRepoUpstream: CustomStateSaveable<Upstream>,
    isFileSelectionMode: MutableState<Boolean>,
    changeListPageNoRepo: MutableState<Boolean>,
    changeListPageHasIndexItem: MutableState<Boolean>,
    changeListPageHasWorktreeItem: MutableState<Boolean>,
    itemList: CustomStateListSaveable<StatusTypeEntrySaver>,
    requireShowToast:(String)->Unit,
    curRepoFromParentPage: CustomStateSaveable<RepoEntity>,
    selectedItemList: CustomStateListSaveable<StatusTypeEntrySaver>,
    fromTo: String,
    repoState: MutableIntState,
    commit1OidStr:String,  
    commit2OidStr:String,
    commitParentList: MutableList<String>,
    repoId:String,
    setErrMsg:(String)->Unit,
    clearErrMsg:()->Unit,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    hasNoConflictItems: MutableState<Boolean>,
    swap:Boolean,
    commitForQueryParents:String,
    rebaseCurOfAll: MutableState<String>?,
    credentialList: CustomStateListSaveable<CredentialEntity>,
    quitSelectionMode: () -> Unit,
    repoChanged:()->Boolean,
){
    val funName = "changeListInit"
    ThumbCache.clear()
    val tmpCommit1 = commit1OidStr
    val commit1OidStr = if(swap) commit2OidStr else commit1OidStr
    val commit2OidStr = if(swap) tmpCommit1 else commit2OidStr
    clearErrMsg()
    changeListPageNoRepo.value=false
    changeListPageHasIndexItem.value=false
    repoState.intValue = StateT.NONE.bit
    itemList.value.clear()  
    credentialList.value.clear()  
    val credentialListFromDb = AppModel.dbContainer.credentialRepository.getAll(includeNone = true, includeMatchByDomain = true)
    if(repoChanged()) {
        return
    }
    credentialList.value.clear()
    credentialList.value.addAll(credentialListFromDb)
    if(fromTo == Cons.gitDiffFromTreeToTree) {
        val repoDb = dbContainer.repoRepository
        val repoFromDb = repoDb.getById(repoId)
        if(repoFromDb==null) {
            MyLog.w(TAG, "#$funName, tree to tree diff, query repo err!")
            changeListPageNoRepo.value=true
            setErrMsg(activityContext.getString(R.string.err_when_querying_repo_info))
            return
        }
        curRepoFromParentPage.value = repoFromDb
        val curRepoFromParentPage = curRepoFromParentPage.value
        commitParentList.clear()
        Repository.open(repoFromDb.fullSavePath).use { repo ->
            if(commit1OidStr == commit2OidStr) {
                itemList.value.clear()
            } else if(Libgit2Helper.CommitUtil.isLocalCommitHash(commit1OidStr)
                || Libgit2Helper.CommitUtil.isLocalCommitHash(commit2OidStr)
            ) {  
                val reverse = Libgit2Helper.CommitUtil.isLocalCommitHash(commit1OidStr)
                val leftCommit = if(reverse) commit2OidStr else commit1OidStr
                val isActuallyIndexToLocal = leftCommit == Cons.git_IndexCommitHash;
                val tree = if(isActuallyIndexToLocal) {
                    null
                } else {
                    Libgit2Helper.resolveTree(repo, leftCommit)
                }
                if(isActuallyIndexToLocal.not() && tree == null) {
                    MyLog.w(TAG, "#$funName, tree to tree diff, query tree err! leftCommit=$leftCommit")
                    setErrMsg(activityContext.getString(R.string.error_invalid_commit_hash)+" '$leftCommit', (err at: 3277)")
                    return
                }
                val cl = if(isActuallyIndexToLocal) {
                    val wtStatusList = Libgit2Helper.getWorkdirStatusList(repo)
                    Libgit2Helper.getWorktreeChangeList(repo, wtStatusList, repoId)
                } else {
                    Libgit2Helper.getTreeToTreeChangeList(repo, repoId, tree!!, null, reverse=reverse, treeToWorkTree = true)
                }
                if(repoChanged()) {
                    return
                }
                itemList.value.clear()
                itemList.value.addAll(cl)
            }else {  
                val tree1 = Libgit2Helper.resolveTree(repo, commit1OidStr)
                if(tree1==null) {
                    MyLog.w(TAG, "#$funName, tree to tree diff, query tree1 err! commit1OidStr=$commit1OidStr")
                    setErrMsg(activityContext.getString(R.string.error_invalid_commit_hash)+" '$commit1OidStr', (err at: 5145)")
                    return
                }
                val tree2 = Libgit2Helper.resolveTree(repo, commit2OidStr)
                if(tree2==null) {
                    MyLog.w(TAG, "#$funName, tree to tree diff, query tree2 err! commit2OidStr=$commit2OidStr")
                    setErrMsg(activityContext.getString(R.string.error_invalid_commit_hash)+" '$commit2OidStr', (err at: 7136)")
                    return
                }
                val treeToTreeChangeList = Libgit2Helper.getTreeToTreeChangeList(repo, repoId, tree1, tree2);
                if(repoChanged()) {
                    return
                }
                itemList.value.clear()
                itemList.value.addAll(treeToTreeChangeList)
                if(Libgit2Helper.CommitUtil.mayGoodCommitHash(commitForQueryParents)) {
                    val parentList = Libgit2Helper.getCommitParentsOidStrList(repo, commitForQueryParents)
                    if(repoChanged()) {
                        return
                    }
                    commitParentList.addAll(parentList)
                }
            }
        }
    }else {  
        val changeListSettings = SettingsUtil.getSettingsSnapshot().changeList
        val lastUsedRepoId = changeListSettings.lastUsedRepoId
        var needQueryRepoFromDb = false
        if(!isRepoReadyAndPathExist(curRepoFromParentPage.value)) {
            if (lastUsedRepoId.isBlank()) {  
                needQueryRepoFromDb = true  
            } else {  
                val repoFromDb = dbContainer.repoRepository.getById(lastUsedRepoId)
                if (repoFromDb == null || !isRepoReadyAndPathExist(repoFromDb)) {  
                    needQueryRepoFromDb = true
                } else {  
                    curRepoFromParentPage.value = repoFromDb  
                }
            }
        }else { 
            val repoFromDb = dbContainer.repoRepository.getById(curRepoFromParentPage.value.id)
            if(repoFromDb == null || !isRepoReadyAndPathExist(repoFromDb)) {  
                needQueryRepoFromDb = true
            }else {  
                curRepoFromParentPage.value = repoFromDb  
            }
        }
        if(repoChanged()) {
            return
        }
        if (needQueryRepoFromDb) {  
            val repoFromDb = dbContainer.repoRepository.getAReadyRepo()
            if(repoChanged()) {
                return
            }
            if (repoFromDb == null) {  
                changeListPageNoRepo.value = true
                setErrMsg(activityContext.getString(R.string.no_repo_for_shown))
                return
            } else {
                curRepoFromParentPage.value = repoFromDb
            }
        }
        if(repoChanged()) {
            return
        }
        val curRepoFromParentPage = curRepoFromParentPage.value
        if (!isRepoReadyAndPathExist(curRepoFromParentPage)) {
            changeListPageNoRepo.value=true
            setErrMsg(activityContext.getString(R.string.no_repo_for_shown))
            return
        }
        if(repoChanged()) {
            return
        }
        if(changeListSettings.lastUsedRepoId != curRepoFromParentPage.id) {
            changeListSettings.lastUsedRepoId = curRepoFromParentPage.id
            val settingsWillSave = SettingsUtil.getSettingsSnapshot()
            settingsWillSave.changeList = changeListSettings
            SettingsUtil.updateSettings(settingsWillSave)
        }
        Repository.open(curRepoFromParentPage.fullSavePath).use { gitRepository ->
            repoState.intValue = gitRepository.state()?.bit?: Cons.gitRepoStateInvalid
            if(repoState.intValue == Repository.StateT.REBASE_MERGE.bit
                && (fromTo== Cons.gitDiffFromIndexToWorktree || fromTo== Cons.gitDiffFromHeadToIndex)
            ) {
                rebaseCurOfAll?.value = Libgit2Helper.rebaseCurOfAllFormatted(gitRepository)
            }
            hasNoConflictItems.value = !gitRepository.index().hasConflicts()
            MyLog.d(TAG, "hasNoConflictItems="+hasNoConflictItems.value)
            val (indexIsEmpty, indexList) = Libgit2Helper.checkIndexIsEmptyAndGetIndexList(gitRepository, curRepoFromParentPage.id, onlyCheckEmpty = false)
            if(repoChanged()) {
                return
            }
            changeListPageHasIndexItem.value = !indexIsEmpty
            MyLog.d(TAG,"#$funName(): changeListPageHasIndexItem = "+changeListPageHasIndexItem.value)
            if(fromTo == Cons.gitDiffFromHeadToIndex) {
                itemList.value.clear()
                indexList?.let {
                    itemList.value.addAll(it)
                }
            }
            if(fromTo == Cons.gitDiffFromIndexToWorktree) {  
                val wtStatusList = Libgit2Helper.getWorkdirStatusList(gitRepository)
                if(repoChanged()) {
                    return
                }
                val hasWorktreeItem = wtStatusList.entryCount() > 0
                changeListPageHasWorktreeItem.value = hasWorktreeItem
                if (hasWorktreeItem) {
                    val worktreeItems = Libgit2Helper.getWorktreeChangeList(gitRepository, wtStatusList, curRepoFromParentPage.id)
                    if(repoChanged()) {
                        return
                    }
                    itemList.value.clear()
                    itemList.value.addAll(worktreeItems)
                }
                curRepoUpstream.value = Libgit2Helper.getUpstreamOfBranch(gitRepository, curRepoFromParentPage.branch)
            }
        }
    }
    if(repoChanged()) {
        return
    }
    val pageChangedNeedAbort = updateSelectedList(
        selectedItemList = selectedItemList.value,
        itemList = itemList.value,
        quitSelectionMode = quitSelectionMode,
        match = { oldSelected, item-> oldSelected.relativePathUnderRepo == item.relativePathUnderRepo },
        pageChanged = repoChanged
    )
    if (pageChangedNeedAbort) return
}
@Composable
private fun getBackHandler(
    appContext: Context,
    exitApp: () -> Unit,
    isFileSelectionMode:MutableState<Boolean>,
    quitSelectionMode:()->Unit,
    fromTo: String,
    naviUp:()->Unit,
    changeListPageFilterModeOn:MutableState<Boolean>,
    quitFilterMode: () -> Unit,
    openDrawer:()->Unit
): () -> Unit {
    val backStartSec = rememberSaveable { mutableLongStateOf(0) }
    val pressBackAgainForExitText = stringResource(R.string.press_back_again_to_exit);
    val showTextAndUpdateTimeForPressBackBtn = {
        openDrawer()
        showToast(appContext, pressBackAgainForExitText, Toast.LENGTH_SHORT)
        backStartSec.longValue = getSecFromTime() + Cons.pressBackDoubleTimesInThisSecWillExit
    }
    val backHandlerOnBack:()->Unit = {
        if(isFileSelectionMode.value) {
            quitSelectionMode()
        }else if(changeListPageFilterModeOn.value){
            quitFilterMode()
        } else {
            if(fromTo != Cons.gitDiffFromIndexToWorktree) { 
                doJobWithMainContext{
                    naviUp()
                }
            }else {  
                if (backStartSec.longValue > 0 && getSecFromTime() <= backStartSec.longValue) {  
                    exitApp()
                } else {
                    showTextAndUpdateTimeForPressBackBtn()
                }
            }
        }
    }
    return backHandlerOnBack
}
