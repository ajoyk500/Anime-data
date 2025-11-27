package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.AskGitUsernameAndEmailDialogWithSelection
import com.akcreation.gitsilent.compose.BottomSheet
import com.akcreation.gitsilent.compose.BottomSheetItem
import com.akcreation.gitsilent.compose.CenterPaddingRow
import com.akcreation.gitsilent.compose.CheckoutDialog
import com.akcreation.gitsilent.compose.CheckoutDialogFrom
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.CopyScrollableColumn
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CreateBranchDialog
import com.akcreation.gitsilent.compose.DefaultPaddingRow
import com.akcreation.gitsilent.compose.DefaultPaddingText
import com.akcreation.gitsilent.compose.FetchRemotesDialog
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.ForcePushWithLeaseCheckBox
import com.akcreation.gitsilent.compose.FullScreenScrollableColumn
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LoadingDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.ResetDialog
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SelectionRow
import com.akcreation.gitsilent.compose.SetUpstreamDialog
import com.akcreation.gitsilent.compose.checkoutOptionJustCheckoutForLocalBranch
import com.akcreation.gitsilent.compose.getDefaultCheckoutOption
import com.akcreation.gitsilent.compose.invalidCheckoutOption
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.branchListPagePublishBranchTestPassed
import com.akcreation.gitsilent.dev.branchRenameTestPassed
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dev.rebaseTestPassed
import com.akcreation.gitsilent.dev.resetByHashTestPassed
import com.akcreation.gitsilent.dto.RemoteDto
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.BranchNameAndTypeDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.BranchItem
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.goToCommitListScreen
import com.akcreation.gitsilent.screen.functions.goToTreeToTreeChangeList
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.CommitListFrom
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StateRequestType
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.showErrAndSaveLog
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.github.git24j.core.Branch
import com.github.git24j.core.Repository

private const val TAG = "BranchListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BranchListScreen(
    repoId:String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<BranchNameAndTypeDto>())
    val requireBlinkIdx = rememberSaveable{mutableIntStateOf(-1)}
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val pageRequest = rememberSaveable{mutableStateOf("")}
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false) }
    val showCreateBranchDialog = rememberSaveable { mutableStateOf(false) }
    val requireCheckout = rememberSaveable { mutableStateOf(true) }
    val showCheckoutBranchDialog = rememberSaveable { mutableStateOf(false) }
    val forceCheckoutForCreateBranch = rememberSaveable { mutableStateOf(false) }
    val initCreateBranchDialog = {
        forceCheckoutForCreateBranch.value = false
        showCreateBranchDialog.value = true
    }
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val branchName = rememberSaveable { mutableStateOf("")}
    val curObjInPage = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curObjInPage", initValue = BranchNameAndTypeDto())
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))
    val showRebaseOrMergeDialog = rememberSaveable { mutableStateOf(false)}
    val rebaseOrMergeSrc = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "rebaseOrMergeSrc", initValue = BranchNameAndTypeDto())
    val requireRebase = rememberSaveable { mutableStateOf(false)}
    fun initRebaseOrMergeDialog(
        isRebase: Boolean,
        src: BranchNameAndTypeDto?,
        caller:BranchNameAndTypeDto,
    ) {
        val calledByCurrentBranchAndSrcIsUpstreamOfIt = caller.isCurrent
        if(src == null) {
            if(calledByCurrentBranchAndSrcIsUpstreamOfIt) {
                Msg.requireShowLongDuration(activityContext.getString(R.string.upstream_not_set_or_not_published))
            }else {
                Msg.requireShowLongDuration(activityContext.getString(R.string.resolve_reference_failed))
            }
            return
        }
        if(calledByCurrentBranchAndSrcIsUpstreamOfIt && caller.behind == 0) {
            Msg.requireShow(activityContext.getString(R.string.already_up_to_date))
            return
        }
        requireRebase.value = isRebase
        rebaseOrMergeSrc.value = src
        showRebaseOrMergeDialog.value = true
    }
    val username = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val showUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val afterSetUsernameAndEmailSuccessCallback = mutableCustomStateOf<(()->Unit)?>(keyTag = stateKeyTag, keyName = "afterSetUsernameAndEmailSuccessCallback") { null }
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
    if(showUsernameAndEmailDialog.value) {
        val curRepo = curRepo.value
        val closeDialog = { showUsernameAndEmailDialog.value = false }
        AskGitUsernameAndEmailDialogWithSelection(
            curRepo = curRepo,
            username = username,
            email = email,
            closeDialog = closeDialog,
            onErrorCallback = { e->
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                MyLog.e(TAG, "set username and email err (from BranchList page): ${e.stackTraceToString()}")
            },
            onFinallyCallback = {},
            onSuccessCallback = {
                val successCallback = afterSetUsernameAndEmailSuccessCallback.value
                afterSetUsernameAndEmailSuccessCallback.value = null
                successCallback?.invoke()
            },
        )
    }
    val repoCurrentActiveBranchOrShortDetachedHashForShown = rememberSaveable { mutableStateOf("")}  
    val repoCurrentActiveBranchFullRefForDoAct = rememberSaveable { mutableStateOf("")}  
    val repoCurrentActiveBranchOrDetachedHeadFullHashForDoAct = rememberSaveable { mutableStateOf("")}  
    val curRepoIsDetached = rememberSaveable { mutableStateOf(false)}  
    val defaultLoadingText = stringResource(R.string.loading)
    val loading = rememberSaveable { mutableStateOf(false)}
    val loadingText = rememberSaveable { mutableStateOf(defaultLoadingText)}
    val loadingOn = { text:String ->
        loadingText.value=text
        loading.value=true
    }
    val loadingOff = {
        loadingText.value = activityContext.getString(R.string.loading)
        loading.value=false
    }
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }
    suspend fun doMerge(trueMergeFalseRebase:Boolean, src: BranchNameAndTypeDto):Ret<Unit?> {
        val curObjInPage = Unit
        if(src.oidStr == repoCurrentActiveBranchOrDetachedHeadFullHashForDoAct.value) {
            Msg.requireShow(activityContext.getString(R.string.already_up_to_date))
            return Ret.createSuccess(null)  
        }
        Repository.open(curRepo.value.fullSavePath).use { repo ->
            val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
            if(Libgit2Helper.isUsernameAndEmailInvalid(usernameFromConfig,emailFromConfig)) {
                return Ret.createError(null, activityContext.getString(R.string.plz_set_username_and_email_first))
            }
            val targetRefName = src.fullName  
            val username = usernameFromConfig
            val email = emailFromConfig
            val requireMergeByRevspec = curRepoIsDetached.value  
            val revspec = src.oidStr
            val mergeResult = if(trueMergeFalseRebase) {
                Libgit2Helper.mergeOneHead(
                    repo = repo,
                    targetRefName = targetRefName,
                    username = username,
                    email = email,
                    requireMergeByRevspec = requireMergeByRevspec,
                    revspec = revspec,
                    settings = settings
                )
            } else {
                Libgit2Helper.mergeOrRebase(
                    repo,
                    targetRefName = targetRefName,
                    username = username,
                    email = email,
                    requireMergeByRevspec = requireMergeByRevspec,
                    revspec = revspec,
                    trueMergeFalseRebase = false,
                    settings=settings
                )
            }
            if (mergeResult.hasError()) {
                val errMsg = if (mergeResult.code == Ret.ErrCode.mergeFailedByAfterMergeHasConfilts) {
                    activityContext.getString(R.string.has_conflicts)
                }else{
                    mergeResult.msg
                }
                return Ret.createError(null, errMsg)
            }
            Libgit2Helper.cleanRepoState(repo)
            Libgit2Helper.updateDbAfterMergeSuccess(mergeResult,activityContext,curRepo.value.id, Msg.requireShow, trueMergeFalseRebase)
        }
        return Ret.createSuccess(null)
    }
    if (showCreateBranchDialog.value) {
        CreateBranchDialog(
            branchName = branchName,
            curRepo = curRepo.value,
            curBranchName = repoCurrentActiveBranchOrShortDetachedHashForShown.value,
            requireCheckout = requireCheckout,
            forceCheckout=forceCheckoutForCreateBranch,
            loadingOn=loadingOn,
            loadingOff = loadingOff,
            loadingText = stringResource(R.string.creating_branch),
            onCancel = {showCreateBranchDialog.value=false},
            onErr = {e->
                val branchName = branchName.value
                val errSuffix = " -(at create branch dialog, branch name=$branchName)"
                Msg.requireShowLongDuration(e.localizedMessage ?:"create branch err")
                createAndInsertError(repoId, ""+e.localizedMessage+errSuffix)
                MyLog.e(TAG, "create branch err: name=$branchName, requireCheckout=${requireCheckout.value}, forceCheckout=${forceCheckoutForCreateBranch.value}, err="+e.stackTraceToString())
            },
            onFinally = {
                changeStateTriggerRefreshPage(needRefresh)
            }
        )
    }
    val branchNameForCheckout = rememberSaveable { mutableStateOf("") }
    val initUpstreamForCheckoutRemoteBranch = rememberSaveable { mutableStateOf("") }
    val remotePrefixMaybe = rememberSaveable { mutableStateOf("") }
    val isCheckoutRemoteBranch = rememberSaveable { mutableStateOf(false) }
    val checkoutLocalBranch = rememberSaveable { mutableStateOf(false) }
    val checkoutSelectedOption = rememberSaveable{ mutableIntStateOf(invalidCheckoutOption) }
    if(showCheckoutBranchDialog.value) {
        val item = curObjInPage.value
        CheckoutDialog(
            checkoutSelectedOption = checkoutSelectedOption,
            showCheckoutDialog = showCheckoutBranchDialog,
            branchName = branchNameForCheckout,
            remoteBranchShortNameMaybe = initUpstreamForCheckoutRemoteBranch.value,
            remotePrefixMaybe = remotePrefixMaybe.value,
            isCheckoutRemoteBranch = isCheckoutRemoteBranch.value,
            from = CheckoutDialogFrom.BRANCH_LIST,
            showJustCheckout = checkoutLocalBranch.value,
            expectCheckoutType = if(checkoutLocalBranch.value) Cons.checkoutType_checkoutRefThenUpdateHead else Cons.checkoutType_checkoutRefThenDetachHead,
            shortName = item.shortName,
            fullName = item.fullName,
            curRepo = curRepo.value,
            curCommitOid = item.oidStr,
            curCommitShortOid = item.shortOidStr,
            requireUserInputCommitHash = false,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            refreshPage = { _, _, _, _, ->
                changeStateTriggerRefreshPage(needRefresh)
            },
        )
    }
    val showSetUpstreamForLocalBranchDialog = rememberSaveable { mutableStateOf(false)}
    val upstreamRemoteOptionsList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "upstreamRemoteOptionsList",
        initValue = listOf<String>()
    )  
    val upstreamSelectedRemote = rememberSaveable{mutableIntStateOf(0)}  
    val upstreamBranchSameWithLocal =rememberSaveable { mutableStateOf(true)}
    val showClearForSetUpstreamDialog =rememberSaveable { mutableStateOf(false)}
    val upstreamBranchShortRefSpec = rememberSaveable { mutableStateOf("")}
    val afterSetUpstreamSuccessCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "afterSetUpstreamSuccessCallback") { null }
    val setUpstreamOnFinallyCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "setUpstreamOnFinallyCallback") { null }
    val initSetUpstreamDialog = { curObjInPage:BranchNameAndTypeDto, callback:(()->Unit)? ->
        if(curObjInPage.type == Branch.BranchType.REMOTE) {  
            Msg.requireShowLongDuration(activityContext.getString(R.string.cant_set_upstream_for_remote_branch))
        }else { 
            var remoteIdx = 0   
            var shortBranch = curObjInPage.shortName  
            var sameWithLocal = true  
            val upstream = curObjInPage.upstream
            showClearForSetUpstreamDialog.value = upstream!=null && (upstream.remote.isNotBlank() || upstream.branchRefsHeadsFullRefSpec.isNotBlank())
            if(upstream!=null) {
                MyLog.d(TAG,"set upstream menu item #onClick(): upstream is not null, old remote in config is: ${upstream.remote}, old branch in config is:${upstream.branchRefsHeadsFullRefSpec}")
                val oldRemote = upstream.remote
                if(oldRemote.isNotBlank()) {
                    for((idx, value) in upstreamRemoteOptionsList.value.toList().withIndex()) {
                        if(value == oldRemote) {
                            MyLog.d(TAG,"set upstream menu item #onClick(): found old remote: ${value}, idx in remote list is: $idx")
                            remoteIdx = idx
                            break
                        }
                    }
                }
                val oldUpstreamShortBranchNameNoPrefix = upstream.remoteBranchShortRefSpecNoPrefix
                if(!oldUpstreamShortBranchNameNoPrefix.isNullOrBlank()) {
                    MyLog.d(TAG,"set upstream menu item #onClick(): found old branch full refspec: ${upstream.branchRefsHeadsFullRefSpec}, short refspec: $oldUpstreamShortBranchNameNoPrefix")
                    shortBranch = oldUpstreamShortBranchNameNoPrefix
                    sameWithLocal = false  
                }
            }
            upstreamSelectedRemote.intValue = remoteIdx
            upstreamBranchShortRefSpec.value = shortBranch
            upstreamBranchSameWithLocal.value = sameWithLocal
            MyLog.d(TAG, "set upstream menu item #onClick(): after read old settings, finally, default select remote idx is:${upstreamSelectedRemote.intValue}, branch name is:${upstreamBranchShortRefSpec.value}, check 'same with local branch` is:${upstreamBranchSameWithLocal.value}")
            setUpstreamOnFinallyCallback.value = if(callback != null) null else { {changeStateTriggerRefreshPage(needRefresh)} }
            afterSetUpstreamSuccessCallback.value = callback
            showSetUpstreamForLocalBranchDialog.value = true
        }
    }
    val doTaskOrShowSetUpstream = { curObjInPage:BranchNameAndTypeDto, task:(()->Unit)? ->
        if(curObjInPage.isUpstreamAlreadySet()) {
            task?.invoke()
        }else {
            initSetUpstreamDialog(curObjInPage) {
                task?.invoke()
            }
        }
    }
    if(showSetUpstreamForLocalBranchDialog.value) {
        SetUpstreamDialog(
            callerTag = TAG,
            curRepo = curRepo.value,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            curBranchFullName = curObjInPage.value.fullName,
            isCurrentBranchOfRepo = curObjInPage.value.isCurrent,
            curBranchShortName = curObjInPage.value.shortName,
            remoteList = upstreamRemoteOptionsList.value,
            selectedOption = upstreamSelectedRemote,
            upstreamBranchShortName = upstreamBranchShortRefSpec,
            upstreamBranchShortNameSameWithLocal = upstreamBranchSameWithLocal,
            showClear = showClearForSetUpstreamDialog.value,
            closeDialog = {
                showSetUpstreamForLocalBranchDialog.value = false
            },
            onClearSuccessCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            },
            onClearErrorCallback = { e ->
                val repoName = curRepo.value.repoName
                val curBranchShortName = curObjInPage.value.shortName
                Msg.requireShowLongDuration("clear upstream err: " + e.localizedMessage)
                createAndInsertError(
                    repoId,
                    "clear upstream for '$curBranchShortName' err: " + e.localizedMessage
                )
                MyLog.e(
                    TAG,
                    "clear upstream for '$curBranchShortName' of '$repoName' err: " + e.stackTraceToString()
                )
            },
            onClearFinallyCallback = {
                changeStateTriggerRefreshPage(needRefresh)
            },
            onSuccessCallback = {
                Msg.requireShow(activityContext.getString(R.string.set_upstream_success))
                Repository.open(curRepo.value.fullSavePath).use { repo ->
                    curObjInPage.value.upstream = Libgit2Helper.getUpstreamOfBranch(repo, curObjInPage.value.shortName)
                }
                val callback = afterSetUpstreamSuccessCallback.value
                afterSetUpstreamSuccessCallback.value = null
                callback?.invoke()
            },
            onErrorCallback = onErr@{ e->
                val repoName = curRepo.value.repoName
                val curBranchShortName = curObjInPage.value.shortName
                val upstreamSameWithLocal = upstreamBranchSameWithLocal.value
                val remoteList = upstreamRemoteOptionsList.value
                val selectedRemoteIndex = upstreamSelectedRemote.intValue
                val upstreamShortName = upstreamBranchShortRefSpec.value
                val remote = try {
                    remoteList[selectedRemoteIndex]
                } catch (e: Exception) {
                    MyLog.e(TAG,"err when get remote by index from remote list of '$repoName': remoteIndex=$selectedRemoteIndex, remoteList=$remoteList\nerr info:${e.stackTraceToString()}")
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_selected_remote_is_invalid))
                    return@onErr
                }
                Msg.requireShowLongDuration("set upstream err: " + e.localizedMessage)
                createAndInsertError(
                    repoId,
                    "set upstream for '$curBranchShortName' err: " + e.localizedMessage
                )
                MyLog.e(
                    TAG,
                    "set upstream for '$curBranchShortName' of '$repoName' err! user input branch is '$upstreamShortName', selected remote is $remote, user checked use same name with local is '$upstreamSameWithLocal'\nerr: " + e.stackTraceToString()
                )
            },
            onFinallyCallback = setUpstreamOnFinallyCallback.value,
        )
    }
    val resolveMergeSrc = { target: BranchNameAndTypeDto, branchList: List<BranchNameAndTypeDto> ->
        val curObjInPage = Unit
        val list = Unit
        if(target.isCurrent) {
            val upstreamFullName = target.upstream?.remoteBranchRefsRemotesFullRefSpec
            if(upstreamFullName == null) {
                null
            }else {
                branchList.find { it.fullName == upstreamFullName}
            }
        } else {
            target
        }
    }
    if(showRebaseOrMergeDialog.value) {
        val curObjInPage = Unit
        ConfirmDialog2(
            title = stringResource(if(requireRebase.value) R.string.rebase else R.string.merge),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.Center,
                      verticalAlignment = Alignment.CenterVertically
                  ) {
                      val left = if(!requireRebase.value) rebaseOrMergeSrc.value.shortName else if(curRepoIsDetached.value) Cons.gitDetachedHead else repoCurrentActiveBranchOrShortDetachedHashForShown.value
                      val right = if(requireRebase.value) rebaseOrMergeSrc.value.shortName else if(curRepoIsDetached.value) Cons.gitDetachedHead else repoCurrentActiveBranchOrShortDetachedHashForShown.value
                      val text = if(requireRebase.value) {
                          replaceStringResList(stringResource(R.string.rebase_left_onto_right), listOf(left, right))
                      }else{
                          replaceStringResList(stringResource(R.string.merge_left_into_right), listOf(left, right))
                      }
                      MySelectionContainer {
                          Text(
                              text = text,
                              softWrap = true,
                              overflow = TextOverflow.Visible
                          )
                      }
                  }
                }
            },
            onCancel = { showRebaseOrMergeDialog.value = false }
        ) {  
            showRebaseOrMergeDialog.value=false
            doJobThenOffLoading(
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                loadingText = if(requireRebase.value) activityContext.getString(R.string.rebasing) else activityContext.getString(R.string.merging),
            )  job@{
                try {
                    val mergeRet = doMerge(trueMergeFalseRebase = !requireRebase.value, rebaseOrMergeSrc.value)
                    if(mergeRet.hasError()) {
                        throw RuntimeException(mergeRet.msg)
                    }
                }catch (e:Exception) {
                    MyLog.e(TAG, "MergeDialog#doMerge(trueMergeFalseRebase=${!requireRebase.value}) err: "+e.stackTraceToString())
                    val errMsg = "${if(requireRebase.value) "rebase" else "merge"} failed: "+e.localizedMessage
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(curRepo.value.id, errMsg)
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val showLocalBranchDelDialog = rememberSaveable { mutableStateOf(false)}
    val delUpstreamToo = rememberSaveable { mutableStateOf(false)}  
    val delUpstreamPush = rememberSaveable { mutableStateOf(false)}
    val showRemoteBranchDelDialog = rememberSaveable { mutableStateOf(false)}
    val userSpecifyRemoteName = rememberSaveable { mutableStateOf("")}  
    val curRequireDelRemoteNameIsAmbiguous = rememberSaveable { mutableStateOf(false)}
    if(showLocalBranchDelDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    Row {
                        Text(text = stringResource(id = R.string.del_branch) + ":")
                    }
                    Row(modifier = Modifier.padding(5.dp)) {
                    }
                    MySelectionContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = curObjInPage.value.shortName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                    if (curObjInPage.value.isUpstreamValid()) {
                        Row(modifier = Modifier.padding(5.dp)) {
                        }
                        MyCheckBox(text = stringResource(R.string.del_upstream_too), value = delUpstreamToo)
                        if (delUpstreamToo.value) {  
                            DefaultPaddingRow {
                                Text(text = stringResource(id = R.string.upstream) + ": ")
                                MySelectionContainer {
                                    Text(
                                        text = curObjInPage.value.upstream?.remoteBranchShortRefSpec ?: "",  
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            MyCheckBox(text = stringResource(R.string.push), value = delUpstreamPush)
                        }
                    }
                }
            },
            onCancel = {showLocalBranchDelDialog.value=false},
            okTextColor = MyStyleKt.TextColor.danger(),
            okBtnText = stringResource(R.string.delete),
            cancelBtnText = stringResource(R.string.cancel),
        ) {
            showLocalBranchDelDialog.value=false
            val curRepo = curRepo.value
            val curObjInPage = curObjInPage.value
            doJobThenOffLoading(
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                loadingText = activityContext.getString(R.string.deleting_branch),
            ) {
                try {
                    Repository.open(curRepo.fullSavePath).use { repo ->
                        val deleteBranchRet = Libgit2Helper.deleteBranch(repo, curObjInPage.fullName);
                        if(deleteBranchRet.hasError()) {
                            throw RuntimeException(deleteBranchRet.msg)
                        }
                        Msg.requireShow(activityContext.getString(R.string.del_local_branch_success))
                        if (delUpstreamToo.value) {  
                            if (curObjInPage.isUpstreamValid()) {
                                Msg.requireShow(activityContext.getString(R.string.deleting_upstream))
                                val upstream = curObjInPage.upstream!!
                                val delBranchRet = Libgit2Helper.deleteBranch(
                                    repo,
                                    upstream.remoteBranchRefsRemotesFullRefSpec
                                )
                                if (delBranchRet.hasError()) {
                                    throw RuntimeException("del upstream '${upstream.remoteBranchShortRefSpec}' for '${curObjInPage.shortName}' err: ${delBranchRet.msg}")
                                }
                                if(delUpstreamPush.value) {  
                                    val remoteDb = AppModel.dbContainer.remoteRepository
                                    val remoteFromDb = remoteDb.getByRepoIdAndRemoteName(
                                        curRepo.id,
                                        upstream.remote
                                    )
                                    if (remoteFromDb == null) {
                                        throw RuntimeException("delete upstream '${upstream.remoteBranchShortRefSpec}' push err: query remote from db failed")
                                    }
                                    var credential: CredentialEntity? = null
                                    if (!remoteFromDb.pushCredentialId.isNullOrBlank()) {
                                        val credentialDb = AppModel.dbContainer.credentialRepository
                                        credential = credentialDb.getByIdWithDecryptAndMatchByDomain(id = remoteFromDb.pushCredentialId, url = remoteFromDb.pushUrl)
                                    }
                                    val delRemotePushRet = Libgit2Helper.deleteRemoteBranchByRemoteAndRefsHeadsBranchRefSpec(
                                            repo,
                                            upstream.remote,
                                            upstream.branchRefsHeadsFullRefSpec,
                                            credential
                                    )
                                    if (delRemotePushRet.hasError()) {
                                        throw RuntimeException("del upstream '${upstream.remoteBranchShortRefSpec}' push err: "+delRemotePushRet.msg)
                                    }
                                }
                                Msg.requireShow(activityContext.getString(R.string.del_upstream_success))
                            }else {  
                                throw RuntimeException(activityContext.getString(R.string.del_upstream_failed_upstream_is_invalid))
                            }
                        }
                    }
                }catch (e:Exception) {
                    val errMsg = "del branch failed: "+e.localizedMessage
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(curRepo.id, errMsg)
                    MyLog.e(TAG, "#delLocalBranchDialog err: "+e.stackTraceToString())
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val pushCheckBoxForRemoteBranchDelDialog = rememberSaveable { mutableStateOf(false)}
    if(showRemoteBranchDelDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.delete),
            okBtnEnabled = !pushCheckBoxForRemoteBranchDelDialog.value || !curRequireDelRemoteNameIsAmbiguous.value || userSpecifyRemoteName.value.isNotBlank(),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    Row {
                        Text(text = stringResource(id = R.string.del_remote_branch)+":")
                    }
                    Row(modifier = Modifier.padding(5.dp)){
                    }
                    MySelectionContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = curObjInPage.value.shortName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                    Row(modifier = Modifier.padding(5.dp)) {
                    }
                    MyCheckBox(text = activityContext.getString(R.string.push), value = pushCheckBoxForRemoteBranchDelDialog)
                    if (pushCheckBoxForRemoteBranchDelDialog.value) {
                        if (curRequireDelRemoteNameIsAmbiguous.value) {
                            Row(modifier = Modifier.padding(5.dp)) {
                            }
                            Row {
                                Text(text = stringResource(R.string.remote_name_ambiguous_plz_specify_remote_name))
                            }
                            Row(modifier = Modifier.padding(5.dp)) {
                            }
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = userSpecifyRemoteName.value,
                                singleLine = true,
                                onValueChange = {
                                    userSpecifyRemoteName.value = it
                                },
                                label = {
                                    Text(stringResource(R.string.specify_remote_name))
                                },
                                placeholder = {
                                    Text(stringResource(R.string.remote_name))
                                }
                            )
                        }
                    }
                }
            },
            onCancel = { showRemoteBranchDelDialog.value=false},
            okTextColor = MyStyleKt.TextColor.danger(),
            okBtnText = stringResource(R.string.delete),
            cancelBtnText = stringResource(R.string.cancel),
        ) {
            showRemoteBranchDelDialog.value=false
            val curRepo = curRepo.value
            val curObjInPage = curObjInPage.value
            doJobThenOffLoading(
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                loadingText = activityContext.getString(R.string.deleting_branch),
            ) {
                try {
                    Repository.open(curRepo.fullSavePath).use { repo ->
                        val delBranchRet = Libgit2Helper.deleteBranch(
                            repo,
                            curObjInPage.fullName
                        )
                        if (delBranchRet.hasError()) {
                            throw RuntimeException(delBranchRet.msg)
                        }
                        if(pushCheckBoxForRemoteBranchDelDialog.value) {
                            val remote = if(curRequireDelRemoteNameIsAmbiguous.value) userSpecifyRemoteName.value else curObjInPage.remotePrefixFromShortName
                            if(remote.isNullOrBlank()) {
                                throw RuntimeException("del remote branch '${curObjInPage.fullName}' err: remote name invalid")
                            }
                            val remoteDb = AppModel.dbContainer.remoteRepository
                            val remoteFromDb = remoteDb.getByRepoIdAndRemoteName(
                                curRepo.id,
                                remote
                            )
                            if (remoteFromDb == null) {
                                throw RuntimeException("del remote branch '${curObjInPage.shortName}' err: query remote from db failed")
                            }
                            var credential: CredentialEntity? = null
                            if (!remoteFromDb.pushCredentialId.isNullOrBlank()) {
                                val credentialDb = AppModel.dbContainer.credentialRepository
                                credential = credentialDb.getByIdWithDecryptAndMatchByDomain(id = remoteFromDb.pushCredentialId, url = remoteFromDb.pushUrl)
                            }
                            val branchRefsHeadsFullRefSpec = "refs/heads/"+Libgit2Helper.removeGitRefSpecPrefix("$remote/", curObjInPage.shortName)  
                            val delRemotePushRet = Libgit2Helper.deleteRemoteBranchByRemoteAndRefsHeadsBranchRefSpec(repo, remote, branchRefsHeadsFullRefSpec, credential)
                            if (delRemotePushRet.hasError()) {
                                throw RuntimeException(delRemotePushRet.msg)
                            }
                        }
                        Msg.requireShow(activityContext.getString(R.string.del_remote_branch_success))
                    }
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    val errPrefix = "del remote branch '${curObjInPage.shortName}' err: "
                    createAndInsertError(curRepo.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, errPrefix+e.stackTraceToString())
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val showResetDialog = rememberSaveable { mutableStateOf(false)}
    val resetDialogOid = rememberSaveable { mutableStateOf("")}
    val closeResetDialog = {
        showResetDialog.value = false
    }
    if (showResetDialog.value) {
        ResetDialog(
            fullOidOrBranchOrTag = resetDialogOid,
            closeDialog=closeResetDialog,
            repoFullPath = curRepo.value.fullSavePath,
            repoId=curRepo.value.id,
            refreshPage = {_, _, _ ->
                changeStateTriggerRefreshPage(needRefresh, StateRequestType.forceReload)
            }
        )
    }
    val clipboardManager = LocalClipboardManager.current
    val showDetailsDialog = rememberSaveable { mutableStateOf(false)}
    val detailsString = rememberSaveable { mutableStateOf("")}
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = stringResource(id = R.string.details),
            text = detailsString.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsString.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    val showRenameDialog = rememberSaveable { mutableStateOf(false)}
    val nameForRenameDialog = rememberSaveable { mutableStateOf("")}
    val forceForRenameDialog = rememberSaveable { mutableStateOf(false)}
    val errMsgForRenameDialog = rememberSaveable { mutableStateOf("")}
    if(showRenameDialog.value) {
        val curItem = curObjInPage.value
        ConfirmDialog(
            title = stringResource(R.string.rename),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                        value = nameForRenameDialog.value,
                        singleLine = true,
                        isError = errMsgForRenameDialog.value.isNotBlank(),
                        supportingText = {
                            if (errMsgForRenameDialog.value.isNotBlank()) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = errMsgForRenameDialog.value,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (errMsgForRenameDialog.value.isNotBlank()) {
                                Icon(imageVector=Icons.Filled.Error,
                                    contentDescription=null,
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        onValueChange = {
                            nameForRenameDialog.value = it
                            errMsgForRenameDialog.value = ""
                        },
                        label = {
                            Text(stringResource(R.string.new_name))
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                    MyCheckBox(text = stringResource(R.string.overwrite_if_exist), value = forceForRenameDialog)
                }
            },
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            okBtnEnabled = nameForRenameDialog.value != curItem.shortName,
            onCancel = {showRenameDialog.value = false}
        ) {
            val newName = nameForRenameDialog.value
            val branchShortName = curItem.shortName
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.renaming)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val renameRet = Libgit2Helper.renameBranch(repo, branchShortName, newName, forceForRenameDialog.value)
                        if(renameRet.hasError()) {
                            errMsgForRenameDialog.value = renameRet.msg
                            return@doJobThenOffLoading
                        }
                        showRenameDialog.value=false
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    val errmsg = e.localizedMessage ?: "rename branch err"
                    Msg.requireShowLongDuration(errmsg)
                    createAndInsertError(curRepo.value.id, "err: rename branch '${curObjInPage.value.shortName}' to ${nameForRenameDialog.value} failed, err is $errmsg")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn = rememberSaveable { mutableStateOf(false)}
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val filterListState = rememberLazyListState()
    val enableFilterState = rememberSaveable { mutableStateOf(false)}
    val showPublishDialog = rememberSaveable { mutableStateOf(false)}
    val forcePublish = rememberSaveable { mutableStateOf(false)}
    val forcePush_pushWithLease = rememberSaveable { mutableStateOf(false) }
    val forcePush_expectedRefspecForLease = rememberSaveable { mutableStateOf("") }
    if(showPublishDialog.value) {
        val curBranch = curObjInPage.value
        val upstream = curBranch.upstream
        if(curBranch.type != Branch.BranchType.LOCAL) {  
            showPublishDialog.value = false
            Msg.requireShowLongDuration(stringResource(R.string.canceled))
        }else if(curBranch.isUpstreamAlreadySet().not()) {  
            showPublishDialog.value = false  
            Msg.requireShowLongDuration(stringResource(R.string.plz_set_upstream_first))
        }else {  
            ConfirmDialog(
                title = stringResource(R.string.publish),
                requireShowTextCompose = true,
                textCompose = {
                    ScrollableColumn {
                        SelectionRow {
                            Text(text = stringResource(R.string.local) +": ")
                            Text(
                                text = curBranch.shortName,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        SelectionRow {
                            Text(text = stringResource(R.string.remote) +": ")
                            Text(
                                text = upstream?.remoteBranchShortRefSpec ?: "",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        SelectionRow {
                            Text(text = stringResource(R.string.will_push_local_branch_to_remote_are_you_sure))
                        }
                        if(upstream != null && upstream.isPublished) {
                            Spacer(modifier = Modifier.height(10.dp))
                            MyCheckBox(text = stringResource(R.string.force), value = forcePublish)
                            if(forcePublish.value) {
                                SelectionRow {
                                    DefaultPaddingText(
                                        text = stringResource(R.string.will_force_overwrite_remote_branch_even_it_is_ahead_to_local),
                                        color = MyStyleKt.TextColor.danger(),
                                    )
                                }
                                Spacer(Modifier.height(15.dp))
                                ForcePushWithLeaseCheckBox(forcePush_pushWithLease, forcePush_expectedRefspecForLease)
                            }
                        }
                    }
                },
                okBtnEnabled = forcePublish.value.not() || forcePush_pushWithLease.value.not() || forcePush_expectedRefspecForLease.value.isNotEmpty(),
                onCancel = { showPublishDialog.value=false}
            ) {
                showPublishDialog.value=false
                val curRepo = curRepo.value
                val repoId = curRepo.id
                val force = forcePublish.value
                val forcePush_pushWithLease = forcePush_pushWithLease.value
                val forcePush_expectedRefspecForLease = forcePush_expectedRefspecForLease.value
                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                    try {
                        val dbContainer = AppModel.dbContainer
                        Repository.open(curRepo.fullSavePath).use { repo->
                            if(force && forcePush_pushWithLease) {
                                loadingText.value = activityContext.getString(R.string.checking)
                                Libgit2Helper.forcePushLeaseCheckPassedOrThrow(
                                    repoEntity = curRepo,
                                    repo = repo,
                                    forcePush_expectedRefspecForLease = forcePush_expectedRefspecForLease,
                                    upstream = upstream,
                                )
                            }
                            loadingText.value = activityContext.getString(if(force) R.string.force_pushing else R.string.pushing)
                            val credential = Libgit2Helper.getRemoteCredential(
                                dbContainer.remoteRepository,
                                dbContainer.credentialRepository,
                                repoId,
                                upstream!!.remote,
                                trueFetchFalsePush = false
                            )
                            Libgit2Helper.push(repo, upstream!!.remote, listOf(upstream!!.pushRefSpec), credential, force)
                            val repoDb = AppModel.dbContainer.repoRepository
                            repoDb.updateLastUpdateTime(repoId, getSecFromTime())
                            Msg.requireShow(activityContext.getString(R.string.success))
                        }
                    }catch (e:Exception) {
                        showErrAndSaveLog(TAG, "#PublishBranchDialog(force=$force) err: "+e.stackTraceToString(), "Publish branch error: "+e.localizedMessage, Msg.requireShowLongDuration, repoId)
                    }finally {
                        changeStateTriggerRefreshPage(needRefresh)
                    }
                }
            }
        }
    }
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<BranchNameAndTypeDto>())
    val lastKeyword = rememberSaveable { mutableStateOf("") }
    val token = rememberSaveable { mutableStateOf("") }
    val searching = rememberSaveable { mutableStateOf(false) }
    val resetSearchVars = {
        searching.value = false
        token.value = ""
        lastKeyword.value = ""
    }
    val getActuallyListState = {
        if(enableFilterState.value) filterListState else listState
    }
    val getActuallyList = {
        if(enableFilterState.value) filterList.value else list.value
    }
    val goToUpstream = { curObj:BranchNameAndTypeDto ->
        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
            if(!curObj.isUpstreamValid()) {
                Msg.requireShowLongDuration(activityContext.getString(R.string.upstream_not_set_or_not_published))
            }else {
                val upstreamFullName = curObj.getUpstreamFullName(activityContext)
                val actuallyList = getActuallyList()
                val actuallyListState = getActuallyListState()
                val targetIdx = actuallyList.toList().indexOfFirst { it.fullName ==  upstreamFullName }
                if(targetIdx == -1) {  
                    if(filterModeOn.value) {
                        val indexInOriginList = list.value.toList().indexOfFirst { it.fullName ==  upstreamFullName }
                        if(indexInOriginList != -1){  
                            filterModeOn.value = false  
                            showBottomSheet.value = false  
                            UIHelper.scrollToItem(scope, listState, indexInOriginList)
                            requireBlinkIdx.intValue = indexInOriginList  
                        }else {
                            Msg.requireShow(activityContext.getString(R.string.upstream_not_found))
                        }
                    }else {  
                        Msg.requireShow(activityContext.getString(R.string.upstream_not_found))
                    }
                }else {  
                    UIHelper.scrollToItem(scope, actuallyListState, targetIdx)
                    requireBlinkIdx.intValue = targetIdx  
                }
            }
        }
    }
    if(pageRequest.value==PageRequest.goToUpstream) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            goToUpstream(curObjInPage.value)
        }
    }
    BackHandler {
        if(filterModeOn.value) {
            filterModeOn.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingOn = { msg:String ->
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
    }
    val showFetchAllDialog = rememberSaveable { mutableStateOf(false) }
    val remoteList = mutableCustomStateListOf(stateKeyTag, "remoteList") { listOf<RemoteDto>() }
    val initFetchAllDialog = {
        doJobThenOffLoading {
            AppModel.dbContainer.remoteRepository.getRemoteDtoListByRepoId(repoId).let {
                remoteList.value.clear()
                remoteList.value.addAll(it)
            }
            showFetchAllDialog.value = true
        }
    }
    if(showFetchAllDialog.value) {
        FetchRemotesDialog(
            title = stringResource(R.string.fetch_all),
            text = stringResource(R.string.fetch_all_are_u_sure),
            remoteList = remoteList.value,
            closeDialog = { showFetchAllDialog.value = false },
            curRepo = curRepo.value,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            refreshPage = { changeStateTriggerRefreshPage(needRefresh) },
        )
    }
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(filterModeOn.value) {
                        FilterTextField(filterKeyWord = filterKeyword, loading = searching.value)
                    }else {
                        val repoAndBranch = Libgit2Helper.getRepoOnBranchOrOnDetachedHash(curRepo.value)
                        Column (modifier = Modifier.combinedClickable (
                                    onDoubleClick = {
                                        defaultTitleDoubleClick(scope, listState, lastPosition)
                                    },
                                    onLongClick = null
                                ){  
                                    showTitleInfoDialog.value=true
                                }
                        ){
                            ScrollableRow  {
                                Text(
                                    text= stringResource(R.string.branches),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            ScrollableRow  {
                                Text(
                                    text= repoAndBranch,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.secondLineFontSize
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if(filterModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),
                        ) {
                            resetSearchVars()
                            filterModeOn.value = false
                        }
                    }else {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.back),
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            iconContentDesc = stringResource(R.string.back),
                        ) {
                            naviUp()
                        }
                    }
                },
                actions = {
                    if(!filterModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.focus_current_branch),
                            icon =  Icons.Filled.CenterFocusWeak,
                            iconContentDesc = stringResource(R.string.focus_current_branch),
                            enabled = !dbIntToBool(curRepo.value.isDetached)
                        ) {
                            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                                val indexOfCurrent = list.value.toList().indexOfFirst {
                                    it.isCurrent
                                }
                                if(indexOfCurrent == -1) {
                                    Msg.requireShow(activityContext.getString(R.string.not_found))
                                }else {  
                                    UIHelper.scrollToItem(scope, listState, indexOfCurrent)
                                    requireBlinkIdx.intValue = indexOfCurrent
                                }
                            }
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.filter),
                            icon =  Icons.Filled.FilterAlt,
                            iconContentDesc = stringResource(R.string.filter),
                        ) {
                            filterKeyword.value = TextFieldValue("")
                            filterModeOn.value = true
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.refresh),
                            icon =  Icons.Filled.Refresh,
                            iconContentDesc = stringResource(R.string.refresh),
                        ) {
                            changeStateTriggerRefreshPage(needRefresh)
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.fetch),
                            icon =  Icons.Filled.Downloading,
                            iconContentDesc = stringResource(R.string.fetch),
                        ) {
                            initFetchAllDialog()
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.create_branch),
                            icon =  Icons.Filled.Add,
                            iconContentDesc = stringResource(R.string.create_branch),
                        ) {
                            initCreateBranchDialog()
                        }
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(pageScrolled.value) {
                GoToTopAndGoToBottomFab(
                    filterModeOn = enableFilterState.value,
                    scope = scope,
                    filterListState = filterListState,
                    listState = listState,
                    filterListLastPosition = filterLastPosition,
                    listLastPosition = lastPosition,
                    showFab = pageScrolled
                )
            }
        }
    ) { contentPadding ->
        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) },
        ) {
            if (loading.value) {
                LoadingDialog(text = loadingText.value)
            }
            if(showBottomSheet.value) {
                BottomSheet(showBottomSheet, sheetState, curObjInPage.value.shortName) {
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.checkout),textDesc= stringResource(R.string.switch_branch),
                        enabled = curObjInPage.value.isCurrent.not()
                    ){
                        val curObjInPage = curObjInPage.value
                        doJobThenOffLoading {
                            val isCheckLocalBranch = curObjInPage.type == Branch.BranchType.LOCAL
                            checkoutLocalBranch.value = isCheckLocalBranch
                            val showJustCheckout = isCheckLocalBranch
                            if(checkoutSelectedOption.intValue == invalidCheckoutOption
                                || (showJustCheckout.not() && checkoutSelectedOption.intValue == checkoutOptionJustCheckoutForLocalBranch)
                            ) {
                                checkoutSelectedOption.intValue = getDefaultCheckoutOption(showJustCheckout)
                            }
                            val isCheckoutRemote = curObjInPage.type == Branch.BranchType.REMOTE
                            isCheckoutRemoteBranch.value =  isCheckoutRemote
                            if(isCheckoutRemote) { 
                                val maybeIsRemoteIfNoNameAmbiguous = upstreamRemoteOptionsList.value.find { curObjInPage.shortName.startsWith(it) }
                                initUpstreamForCheckoutRemoteBranch.value = if(maybeIsRemoteIfNoNameAmbiguous != null) {
                                    remotePrefixMaybe.value = maybeIsRemoteIfNoNameAmbiguous
                                    val branchNameNoRemotePrefix = curObjInPage.shortName.removePrefix("$maybeIsRemoteIfNoNameAmbiguous/")
                                    if(branchNameNoRemotePrefix == Cons.gitHeadStr) {
                                        ""
                                    }else {
                                        branchNameNoRemotePrefix
                                    }
                                }else {
                                    remotePrefixMaybe.value = ""
                                    ""
                                }
                                if(initUpstreamForCheckoutRemoteBranch.value.isNotBlank()) {
                                    branchNameForCheckout.value = initUpstreamForCheckoutRemoteBranch.value
                                }
                            }else {  
                                remotePrefixMaybe.value = ""
                                initUpstreamForCheckoutRemoteBranch.value = ""
                            }
                            showCheckoutBranchDialog.value = true
                        }
                    }
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.merge),
                        textDesc = if(curObjInPage.value.isCurrent) stringResource(R.string.upstream) else stringResource(R.string.merge_into_current),
                    ) {
                        val curObjInPage = curObjInPage.value
                        val list = list.value
                        doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
                            initRebaseOrMergeDialog(
                                isRebase = false,
                                src = resolveMergeSrc(curObjInPage, list),
                                caller = curObjInPage,
                            )
                        }
                    }
                    if(UserUtil.isPro() && (dev_EnableUnTestedFeature || rebaseTestPassed)) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.rebase),
                            textDesc = if(curObjInPage.value.isCurrent) stringResource(R.string.upstream) else stringResource(R.string.rebase_current_onto),
                        ) {
                            val curObjInPage = curObjInPage.value
                            val list = list.value
                            doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
                                initRebaseOrMergeDialog(
                                    isRebase = true,
                                    src = resolveMergeSrc(curObjInPage, list),
                                    caller = curObjInPage
                                )
                            }
                        }
                    }
                    if(curObjInPage.value.type == Branch.BranchType.LOCAL) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.set_upstream),
                            enabled = curObjInPage.value.type == Branch.BranchType.LOCAL
                        ){
                            initSetUpstreamDialog(curObjInPage.value, null)
                        }
                        if(proFeatureEnabled(branchListPagePublishBranchTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.publish),
                                enabled = curObjInPage.value.type == Branch.BranchType.LOCAL
                            ){
                                doTaskOrShowSetUpstream(curObjInPage.value) {
                                    forcePush_expectedRefspecForLease.value = curObjInPage.value.upstream?.remoteBranchShortRefSpec ?: ""
                                    forcePush_pushWithLease.value = false
                                    forcePublish.value = false
                                    showPublishDialog.value = true
                                }
                            }
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_upstream),
                            enabled = curObjInPage.value.type == Branch.BranchType.LOCAL
                        ){
                            val curObj = curObjInPage.value
                            if(!curObj.isUpstreamValid()) {  
                                Msg.requireShowLongDuration(activityContext.getString(R.string.upstream_not_set_or_not_published))
                            }else {
                                val upOid = curObj.upstream?.remoteOid ?: ""
                                if(upOid.isBlank()) {  
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.upstream_oid_is_invalid))
                                }else {
                                    val commit1 = curObj.oidStr
                                    val commit2 = upOid
                                    if(commit1 == commit2) {  
                                        Msg.requireShow(activityContext.getString(R.string.both_are_the_same))
                                    }else {   
                                        goToTreeToTreeChangeList(
                                            title = activityContext.getString(R.string.compare_to_upstream),
                                            repoId = curRepo.value.id,
                                            commit1 = commit1,
                                            commit2 = commit2,
                                            commitForQueryParents = Cons.git_AllZeroOidStr,
                                        )
                                    }
                                }
                            }
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.go_upstream),
                            enabled = curObjInPage.value.type == Branch.BranchType.LOCAL
                        ){
                            goToUpstream(curObjInPage.value)
                        }
                    }
                    if(proFeatureEnabled(resetByHashTestPassed)) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.reset),
                        ){
                            resetDialogOid.value = curObjInPage.value.oidStr
                            showResetDialog.value = true
                        }
                    }
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.details)){
                        val suffix = "\n\n"
                        val sb = StringBuilder()
                        val it = curObjInPage.value
                        sb.append(activityContext.getString(R.string.name)).append(": ").append(it.shortName).append(suffix)
                        sb.append(activityContext.getString(R.string.full_name)).append(": ").append(it.fullName).append(suffix)
                        sb.append(activityContext.getString(R.string.last_commit)).append(": ").append(it.shortOidStr).append(suffix)
                        sb.append(activityContext.getString(R.string.last_commit_full_oid)).append(": ").append(it.oidStr).append(suffix)
                        sb.append(activityContext.getString(R.string.type)).append(": ").append(it.getTypeString(activityContext, false)).append(suffix)
                        if(it.type==Branch.BranchType.LOCAL) {
                            sb.append(activityContext.getString(R.string.upstream)).append(": ").append(it.getUpstreamShortName(activityContext)).append(suffix)
                            if(it.isUpstreamValid()) {
                                sb.append(activityContext.getString(R.string.upstream_full_name)).append(": ").append(it.getUpstreamFullName(activityContext)).append(suffix)
                                sb.append(activityContext.getString(R.string.status)).append(": ").append(it.getAheadBehind(activityContext, false)).append(suffix)
                            }
                        }
                        if(it.isSymbolic) {
                            sb.append(activityContext.getString(R.string.symbolic_target)).append(": ").append(it.symbolicTargetShortName).append(suffix)
                            sb.append(activityContext.getString(R.string.symbolic_target_full_name)).append(": ").append(it.symbolicTargetFullName).append(suffix)
                        }
                        sb.append(activityContext.getString(R.string.other)).append(": ").append(it.getOther(activityContext, false)).append(suffix)
                        sb.append(Cons.flagStr).append(": ").append(it.getTypeString(activityContext, true)).append("; ${it.getAheadBehind(activityContext, true)}").append("; ${it.getOther(activityContext, true)}").append(suffix)
                        detailsString.value = sb.removeSuffix(suffix).toString()
                        showDetailsDialog.value = true
                    }
                    if(curObjInPage.value.type == Branch.BranchType.LOCAL && proFeatureEnabled(branchRenameTestPassed)) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.rename)) {
                            nameForRenameDialog.value = curObjInPage.value.shortName
                            forceForRenameDialog.value= false
                            showRenameDialog.value = true
                        }
                    }
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger(),
                        enabled = curObjInPage.value.isCurrent.not()
                    ){
                        if(curObjInPage.value.type == Branch.BranchType.REMOTE) {
                            curRequireDelRemoteNameIsAmbiguous.value = curObjInPage.value.isRemoteNameAmbiguous()
                            userSpecifyRemoteName.value=""
                            pushCheckBoxForRemoteBranchDelDialog.value = false
                            showRemoteBranchDelDialog.value = true
                        }else {
                            delUpstreamToo.value = false
                            delUpstreamPush.value = false
                            showLocalBranchDelDialog.value = true
                        }
                    }
                }
            }
            if(list.value.isEmpty()) {
                FullScreenScrollableColumn(contentPadding) {
                    if(isInitLoading.value) {
                        Text(text = stringResource(R.string.loading))
                    }else {
                        Row {
                            Text(text = stringResource(R.string.item_list_is_empty))
                        }
                        CenterPaddingRow {
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Downloading,
                                tooltipText = stringResource(R.string.fetch),
                            ) {
                                initFetchAllDialog()
                            }
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Add,
                                tooltipText =  stringResource(R.string.create),
                            ) {
                                initCreateBranchDialog()
                            }
                        }
                    }
                }
            }else {
                val keyword = filterKeyword.value.text  
                val enableFilter = filterModeActuallyEnabled(filterOn = filterModeOn.value, keyword = keyword)
                val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
                val list = filterTheList(
                    needRefresh = filterResultNeedRefresh.value,
                    lastNeedRefresh = lastNeedRefresh,
                    enableFilter = enableFilter,
                    keyword = keyword,
                    lastKeyword = lastKeyword,
                    searching = searching,
                    token = token,
                    activityContext = activityContext,
                    filterList = filterList.value,
                    list = list.value,
                    resetSearchVars = resetSearchVars,
                    match = { idx: Int, it: BranchNameAndTypeDto ->
                        it.fullName.contains(keyword, ignoreCase = true)
                                || it.oidStr.contains(keyword, ignoreCase = true)
                                || it.symbolicTargetFullName.contains(keyword, ignoreCase = true)
                                || it.getUpstreamShortName(activityContext).contains(keyword, ignoreCase = true)
                                || it.getOther(activityContext, false).contains(keyword, ignoreCase = true)
                                || it.getOther(activityContext, true).contains(keyword, ignoreCase = true)
                                || it.getTypeString(activityContext, false).contains(keyword, ignoreCase = true)
                                || it.getTypeString(activityContext, true).contains(keyword, ignoreCase = true)
                                || it.getAheadBehind(activityContext, false).contains(keyword, ignoreCase = true)
                                || it.getAheadBehind(activityContext, true).contains(keyword, ignoreCase = true)
                    }
                )
                val listState = if(enableFilter) filterListState else listState
                enableFilterState.value = enableFilter
                MyLazyColumn (
                    contentPadding = contentPadding,
                    list = list,
                    listState = listState,
                    requireForEachWithIndex = true,
                    requirePaddingAtBottom = true,
                    forEachCb = {},
                ){idx, it->
                    BranchItem(
                        showBottomSheet = showBottomSheet,
                        curObjFromParent = curObjInPage,
                        idx = idx,
                        thisObj = it,
                        requireBlinkIdx = requireBlinkIdx,
                        lastClickedItemKey = lastClickedItemKey,
                        pageRequest = pageRequest
                    ) {  
                        goToCommitListScreen(
                            repoId = repoId,
                            fullOid = it.oidStr,
                            shortBranchName = it.shortName,
                            isHEAD = it.isCurrent,
                            from = CommitListFrom.BRANCH,
                        )
                    }
                    MyHorizontalDivider()
                }
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        try {
            doJobThenOffLoading(initLoadingOn, initLoadingOff) {
                list.value.clear()  
                if(!repoId.isNullOrBlank()) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb!=null) {
                        curRepo.value = repoFromDb
                        Repository.open(repoFromDb.fullSavePath).use {repo ->
                            curRepoIsDetached.value = repo.headDetached()
                            repoCurrentActiveBranchOrShortDetachedHashForShown.value = if(curRepoIsDetached.value) repoFromDb.lastCommitHashShort?:"" else repoFromDb.branch;
                            if(!curRepoIsDetached.value) { 
                                repoCurrentActiveBranchFullRefForDoAct.value = Libgit2Helper.resolveHEAD(repo)?.name()?:""
                            }
                            repoCurrentActiveBranchOrDetachedHeadFullHashForDoAct.value = repo.head()?.id().toString()
                            val listAllBranch = Libgit2Helper.getBranchList(repo)
                            list.value.addAll(listAllBranch)
                            val remoteList = Libgit2Helper.getRemoteList(repo)
                            upstreamRemoteOptionsList.value.clear()
                            upstreamRemoteOptionsList.value.addAll(remoteList)
                        }
                    }
                }
                triggerReFilter((filterResultNeedRefresh))
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect() err: "+e.stackTraceToString())
        }
    }
}
