package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.AskGitUsernameAndEmailDialogWithSelection
import com.akcreation.gitsilent.compose.BottomSheet
import com.akcreation.gitsilent.compose.BottomSheetItem
import com.akcreation.gitsilent.compose.CheckoutDialog
import com.akcreation.gitsilent.compose.CheckoutDialogFrom
import com.akcreation.gitsilent.compose.ClickableText
import com.akcreation.gitsilent.compose.CommitMsgMarkDownDialog
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.CopyScrollableColumn
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CopyableDialog2
import com.akcreation.gitsilent.compose.CreatePatchSuccessDialog
import com.akcreation.gitsilent.compose.CreateTagDialog
import com.akcreation.gitsilent.compose.DefaultPaddingText
import com.akcreation.gitsilent.compose.DiffCommitsDialog
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LoadMore
import com.akcreation.gitsilent.compose.LoadingDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MultiLineClickableText
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PrintNodesInfo
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.ResetDialog
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.SetPageSizeDialog
import com.akcreation.gitsilent.compose.SimpleCheckBox
import com.akcreation.gitsilent.compose.SingleSelectList
import com.akcreation.gitsilent.compose.SoftkeyboardVisibleListener
import com.akcreation.gitsilent.compose.getDefaultCheckoutOption
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.cherrypickTestPassed
import com.akcreation.gitsilent.dev.commitsDiffCommitsTestPassed
import com.akcreation.gitsilent.dev.commitsDiffToLocalTestPassed
import com.akcreation.gitsilent.dev.createPatchTestPassed
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.dev.diffToHeadTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dev.resetByHashTestPassed
import com.akcreation.gitsilent.dev.tagsTestPassed
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.git.CommitDto
import com.akcreation.gitsilent.git.DrawCommitNode
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.CommitItem
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.getLoadText
import com.akcreation.gitsilent.screen.functions.goToTreeToTreeChangeList
import com.akcreation.gitsilent.screen.functions.initSearch
import com.akcreation.gitsilent.screen.functions.maybeIsGoodKeyword
import com.akcreation.gitsilent.screen.functions.search
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.CommitListFrom
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StateRequestType
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.boolToDbInt
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.cache.NaviCache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doActIfIndexGood
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.formatMinutesToUtc
import com.akcreation.gitsilent.utils.getRequestDataByState
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.mutableCustomBoxOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.time.TimeZoneUtil
import com.github.git24j.core.Branch
import com.github.git24j.core.GitObject
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository
import com.github.git24j.core.Revwalk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "CommitListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommitListScreen(
    repoId: String,
    from: CommitListFrom,
    isHEAD:Boolean,
    fullOidCacheKey:String,
    shortBranchNameCacheKey:String,  
    naviUp: () -> Unit,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val view = LocalView.current
    val density = LocalDensity.current
    val isKeyboardVisible = rememberSaveable { mutableStateOf(false) }
    val isKeyboardCoveredComponent = rememberSaveable { mutableStateOf(false) }
    val componentHeight = rememberSaveable { mutableIntStateOf(0) }
    val keyboardPaddingDp = rememberSaveable { mutableIntStateOf(0) }
    val isHEAD = rememberSaveable { mutableStateOf(isHEAD) }
    val onlyUpdateRepoInfoOnce = rememberSaveable { mutableStateOf(false) }
    val lineDistanceInPx = remember(density) { with(density){ 30.dp.toPx() } }
    val nodeCircleRadiusInPx = remember(density) { with(density){ 8.dp.toPx() } }
    val nodeCircleStartOffsetX = remember(nodeCircleRadiusInPx) { nodeCircleRadiusInPx*1.8f }
    val nodeLineWidthInPx = remember(density) { with(density){ 5.dp.toPx() } }
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current
    val navController = AppModel.navController
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val fullOidValue =  rememberSaveable { NaviCache.getByType<String>(fullOidCacheKey) ?: "" }
    val shortBranchName = rememberSaveable { NaviCache.getByType<String>(shortBranchNameCacheKey) ?: "" }
    val fullOid = rememberSaveable { mutableStateOf(fullOidValue)}  
    val branchShortNameOrShortHashByFullOid =rememberSaveable { mutableStateOf(shortBranchName)}  
    val branchShortNameOrShortHashByFullOidForShowOnTitle = rememberSaveable { mutableStateOf(shortBranchName)}  
    val loadChannel = remember { Channel<Int>() }
    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot = rememberSaveable { mutableStateOf(false) }
    val enableFilterState = rememberSaveable { mutableStateOf(false)}
    val filterIdxList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "filterIdxList",
        listOf<Int>()
    )
    val filterListState = rememberLazyListState()
    val listState = rememberLazyListState()
    val getActuallyListState = {
        if(enableFilterState.value) filterListState else listState
    }
    val goToTop = {
        UIHelper.scrollToItem(scope, getActuallyListState(), 0)
    }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    val needRefresh = rememberSaveable { mutableStateOf("CommitList_refresh_init_value_et5c")}
    val fullyRefresh = {
        goToTop()
        changeStateTriggerRefreshPage(needRefresh, StateRequestType.forceReload)
    }
    val getActuallyLastPosition = {
        if(enableFilterState.value) filterLastPosition else lastPosition
    }
    val repoOfSetUsernameAndEmailDialog = mutableCustomStateOf(stateKeyTag, "repoOfSetUsernameAndEmailDialog") { RepoEntity(id = "") }
    val username = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val showUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val afterSetUsernameAndEmailSuccessCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "afterSetUsernameAndEmailSuccessCallback") { null }
    val initSetUsernameAndEmailDialog = { targetRepo:RepoEntity, callback:(()->Unit)? ->
        try {
            Repository.open(targetRepo.fullSavePath).use { repo ->
                val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
                username.value = usernameFromConfig
                email.value = emailFromConfig
            }
            repoOfSetUsernameAndEmailDialog.value = targetRepo
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
        val curRepo = repoOfSetUsernameAndEmailDialog.value
        val closeDialog = { showUsernameAndEmailDialog.value = false }
        AskGitUsernameAndEmailDialogWithSelection(
            curRepo = curRepo,
            username = username,
            email = email,
            closeDialog = closeDialog,
            onErrorCallback = { e->
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                MyLog.e(TAG, "set username and email err: ${e.stackTraceToString()}")
            },
            onFinallyCallback = {},
            onSuccessCallback = {
                val successCallback = afterSetUsernameAndEmailSuccessCallback.value
                afterSetUsernameAndEmailSuccessCallback.value = null
                successCallback?.invoke()
            },
            )
    }
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<CommitDto>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<CommitDto>())
    val lastListSize = rememberSaveable { mutableIntStateOf(0) }
    val lastKeyword = rememberSaveable { mutableStateOf("") }
    val token = rememberSaveable { mutableStateOf("") }
    val searching = rememberSaveable { mutableStateOf(false) }
    val resetSearchVars = {
        searching.value = false
        token.value = ""
        lastKeyword.value = ""
    }
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val shouldShowTimeZoneInfo = rememberSaveable { TimeZoneUtil.shouldShowTimeZoneInfo(settings) }
    val commitHistoryRTL = rememberSaveable { mutableStateOf(settings.commitHistoryRTL) }
    val commitHistoryGraph = rememberSaveable { mutableStateOf(settings.commitHistoryGraph) }
    val pageSize = rememberSaveable{ mutableStateOf(settings.commitHistoryPageSize) }
    val rememberPageSize = rememberSaveable { mutableStateOf(true) }
    val nextCommitOid = mutableCustomStateOf<Oid>(
        keyTag = stateKeyTag,
        keyName = "nextCommitOid",
        initValue = Cons.git_AllZeroOid
    )
    val headOidOfThisScreen = mutableCustomStateOf<Oid>(
        keyTag = stateKeyTag,
        keyName = "headOidOfThisScreen",
        initValue = Cons.git_AllZeroOid
    )
    val showTopBarMenu = rememberSaveable { mutableStateOf(false)}
    val isSearchingMode = rememberSaveable { mutableStateOf(false)}
    val isShowSearchResultMode = rememberSaveable { mutableStateOf(false)}
    val repoOnBranchOrDetachedHash = rememberSaveable { mutableStateOf("")}
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val curCommit = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "curCommit",
        initValue = CommitDto()
    )
    val curRepo = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "curRepo",
        initValue = RepoEntity(id = "")
    )
    val showFilterByPathsDialog = rememberSaveable { mutableStateOf(false) }
    val pathsForFilterBuffer = rememberSaveable { mutableStateOf("") }  
    val pathsForFilter = rememberSaveable { mutableStateOf("") }
    val pathsListForFilter = mutableCustomStateListOf(stateKeyTag, "pathsListForFilter") { listOf<String>() }
    val lastPathsListForFilter = mutableCustomStateListOf(stateKeyTag, "lastPathsListForFilter") { listOf<String>() }
    val filterByEntryName = rememberSaveable { mutableStateOf(false) }
    val filterByEntryNameBuffer = rememberSaveable { mutableStateOf(false) }
    if(showFilterByPathsDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.filter_by_paths),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Text(stringResource(R.string.filter_commits_which_included_the_paths_leave_empty_for_show_all))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    MySelectionContainer {
                        Text(stringResource(R.string.per_line_one_path), fontWeight = FontWeight.Light)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = pathsForFilterBuffer.value,
                        onValueChange = {
                            pathsForFilterBuffer.value = it
                        },
                        label = {
                            Text(stringResource(R.string.paths))
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                    Column(
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                        ,
                        horizontalAlignment = Alignment.End
                    ) {
                        ClickableText (
                            text = stringResource(R.string.clear),
                            modifier = MyStyleKt.ClickableText.modifier.clickable {
                                pathsForFilterBuffer.value = ""
                            },
                            fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            },
            onCancel = {showFilterByPathsDialog.value = false}
        ) {
            showFilterByPathsDialog.value = false
            doJobThenOffLoading {
                filterByEntryName.value = filterByEntryNameBuffer.value
                pathsForFilter.value = pathsForFilterBuffer.value
                pathsListForFilter.value.clear()
                pathsForFilter.value.lines().forEachBetter {
                    if(it.isNotEmpty()) {
                        pathsListForFilter.value.add(it)
                    }
                }
            }
        }
    }
    val loadMoreLoading = rememberSaveable { mutableStateOf(false)}
    val loadMoreText = rememberSaveable { mutableStateOf("")}
    val hasMore = rememberSaveable { mutableStateOf(false)}
    val loadingStrRes = stringResource(R.string.loading)
    val loadingText = rememberSaveable { mutableStateOf(loadingStrRes)}
    val showLoadingDialog = rememberSaveable { mutableStateOf(false)}
    val loadingOn = { msg:String->
        loadingText.value = msg
        showLoadingDialog.value=true
    }
    val loadingOff = {
        loadingText.value=loadingStrRes
        showLoadingDialog.value=false
    }
    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val revwalk = mutableCustomStateOf<Revwalk?>(stateKeyTag, "revwalk", null)
    val repositoryForRevWalk = mutableCustomStateOf<Repository?>(stateKeyTag, "repositoryForRevWalk", null)
    val loadLock = mutableCustomStateOf<Mutex>(stateKeyTag, "loadLock", Mutex())
    val draw_lastOutputNodes = mutableCustomBoxOf(stateKeyTag, "draw_lastOutputNodes") { listOf<DrawCommitNode>() }
    val resetDrawNodesInfo = {
        draw_lastOutputNodes.value = listOf()
    }
    val drawLocalAheadUpstreamCount = rememberSaveable { mutableStateOf(0) }
    val doLoadMore = doLoadMore@{ repoFullPath: String, oid: Oid, firstLoad: Boolean, forceReload: Boolean, loadToEnd:Boolean ->
        if (oid.isNullOrEmptyOrZero) {  
            nextCommitOid.value = oid
            return@doLoadMore
        }
        if (repoFullPath.isBlank()) {
            return@doLoadMore
        }
        if (firstLoad && list.value.isNotEmpty() && !forceReload) {
            return@doLoadMore
        }
        doJobThenOffLoading job@{
            loadLock.value.withLock {
                loadMoreLoading.value = true
                loadMoreText.value = activityContext.getString(R.string.loading)
                try {
                    if (firstLoad || forceReload || repositoryForRevWalk.value==null || revwalk.value==null) {
                        list.value.clear()
                        resetDrawNodesInfo()
                        drawLocalAheadUpstreamCount.value = 0
                        runCatching {
                            Repository.open(repoFullPath).use { repo ->
                                val shortBranchName = shortBranchName.ifEmpty { curRepo.value.branch }
                                val localBranchOrNull = Libgit2Helper.resolveBranch(repo, shortBranchName, Branch.BranchType.LOCAL)
                                if(localBranchOrNull != null) {
                                    val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)
                                    if(upstream.isPublished) {
                                        val upstreamCommitRet = Libgit2Helper.resolveCommitByHashOrRef(repo, upstream.remoteBranchRefsRemotesFullRefSpec)
                                        if(upstreamCommitRet.success()) {
                                            val (ahead, behind) = Libgit2Helper.getAheadBehind(repo, oid, upstreamCommitRet.data!!.id())
                                            if(ahead > 0 && behind == 0) {
                                                drawLocalAheadUpstreamCount.value = ahead
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        repositoryForRevWalk.value?.close()
                        repositoryForRevWalk.value = null  
                        val repo = Repository.open(repoFullPath)
                        val newRevwalk = Libgit2Helper.createRevwalk(repo, oid)
                        if(newRevwalk == null) {
                            val oidStr = oid.toString()
                            Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.create_revwalk_failed_oid), listOf(Libgit2Helper.getShortOidStrByFull(oidStr))))
                            createAndInsertError(repoId, "create Revwalk failed, oid=$oidStr")
                            return@job
                        }
                        repositoryForRevWalk.value = repo
                        revwalk.value = newRevwalk
                        nextCommitOid.value = newRevwalk.next() ?: Cons.git_AllZeroOid
                    }
                    val repo = repositoryForRevWalk.value ?: throw RuntimeException("repo for revwalk is null")
                    if(nextCommitOid.value.isNullOrEmptyOrZero) {
                        hasMore.value = false
                        loadMoreText.value = activityContext.getString(R.string.end_of_the_list)
                    }else {
                        Libgit2Helper.getCommitList(
                            repo,
                            revwalk.value!!,
                            nextCommitOid.value,
                            repoId,
                            if(loadToEnd) Int.MAX_VALUE else pageSize.value,
                            retList = list.value,  
                            loadChannel = loadChannel,
                            checkChannelFrequency = settings.commitHistoryLoadMoreCheckAbortSignalFrequency,
                            settings,
                            draw_lastOutputNodes,
                        )
                        nextCommitOid.value = revwalk.value!!.next() ?: Cons.git_AllZeroOid
                        hasMore.value = !nextCommitOid.value.isNullOrEmptyOrZero
                        loadMoreText.value = if (hasMore.value) activityContext.getString(R.string.load_more) else activityContext.getString(R.string.end_of_the_list)
                    }
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?: "unknown err"
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(repoId, "err: $errMsg")
                    MyLog.e(TAG, "#doLoadMore: err: ${e.stackTraceToString()}")
                }finally {
                    loadMoreLoading.value = false
                    triggerReFilter(filterResultNeedRefresh)
                }
            }
        }
    }
    val clipboardManager = LocalClipboardManager.current
    val showSquashDialog = rememberSaveable { mutableStateOf(false) }
    val forceSquash = rememberSaveable { mutableStateOf(false) }
    val headFullNameForSquashDialog = rememberSaveable { mutableStateOf("") } 
    val headCommitFullOidForSquashDialog = rememberSaveable { mutableStateOf("") }
    val headCommitShortOidForSquashDialog = rememberSaveable { mutableStateOf("") }
    val targetCommitFullOidForSquashDialog = rememberSaveable { mutableStateOf("") }
    val targetCommitShortOidForSquashDialog = rememberSaveable { mutableStateOf("") }
    val commitMsgForSquashDialog = mutableCustomStateOf(stateKeyTag, "commitMsgForSquashDialog") { TextFieldValue("") }
    val usernameForSquashDialog = rememberSaveable { mutableStateOf("") }
    val emailForSquashDialog = rememberSaveable { mutableStateOf("") }
    val closeSquashDialog = {
        showSquashDialog.value = false
    }
    val initShowSquashDialog = {targetFullOid:String, targetShortOid:String, headFullOid:String, headFullName:String, username:String, email:String->
        headFullNameForSquashDialog.value = headFullName
        headCommitFullOidForSquashDialog.value= headFullOid
        headCommitShortOidForSquashDialog.value = Libgit2Helper.getShortOidStrByFull(headFullOid)
        targetCommitFullOidForSquashDialog.value = targetFullOid
        targetCommitShortOidForSquashDialog.value = targetShortOid
        usernameForSquashDialog.value = username
        emailForSquashDialog.value = email
        forceSquash.value = false
        showSquashDialog.value = true
    }
    val clearCommitMsg = {
        commitMsgForSquashDialog.value = TextFieldValue("")
    }
    val genSquashCommitMsg = {
        Libgit2Helper.squashCommitsGenCommitMsg(targetCommitShortOidForSquashDialog.value, headCommitShortOidForSquashDialog.value)
    }
    if(showSquashDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.squash),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            text = replaceStringResList(
                                stringResource(R.string.squash_commits_not_include_the_left_commit),
                                listOf(targetCommitShortOidForSquashDialog.value, headCommitShortOidForSquashDialog.value)
                            ),
                            fontWeight = FontWeight.Light
                        )
                    }
                    Spacer(Modifier.height(15.dp))
                    TextField(
                        maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                        modifier = Modifier.fillMaxWidth(),
                        value = commitMsgForSquashDialog.value,
                        onValueChange = {
                            commitMsgForSquashDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.commit_message))
                        },
                        placeholder = {
                            Text(stringResource(R.string.input_your_commit_message))
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                    MultiLineClickableText(stringResource(R.string.you_can_leave_msg_empty_will_auto_gen_one)) {
                        Repository.open(curRepo.value.fullSavePath).use { repo ->
                            commitMsgForSquashDialog.value = TextFieldValue(genSquashCommitMsg())
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    MyCheckBox(stringResource(R.string.force), forceSquash)
                    if(forceSquash.value) {
                        MySelectionContainer {
                            DefaultPaddingText(
                                text = stringResource(R.string.if_index_contains_uncommitted_changes_will_commit_as_well),
                                color = MyStyleKt.TextColor.danger(),
                            )
                        }
                    }
                }
            },
            onCancel = closeSquashDialog
        ) {
            closeSquashDialog()
            val commitMsg = commitMsgForSquashDialog.value.text.ifBlank { genSquashCommitMsg() }
            val targetFullOid = targetCommitFullOidForSquashDialog.value
            val headFullName = headFullNameForSquashDialog.value
            val username = usernameForSquashDialog.value
            val email = emailForSquashDialog.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) job@{
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val checkRet = Libgit2Helper.squashCommitsCheckBeforeExecute(repo, forceSquash.value)
                        if(checkRet.hasError()) {
                            throw checkRet.exception ?: RuntimeException(checkRet.msg)
                        }
                        val ret = Libgit2Helper.squashCommits(
                            repo = repo,
                            targetFullOidStr = targetFullOid,
                            commitMsg = commitMsg,
                            username = username,
                            email = email,
                            currentBranchFullNameOrHEAD = headFullName,
                            settings = settings
                        )
                        if(ret.hasError()) {
                            throw ret.exception ?: RuntimeException(ret.msg)
                        }
                        fullOid.value = ret.data!!.toString()
                    }
                    clearCommitMsg()
                    Msg.requireShow(activityContext.getString(R.string.success))
                    fullyRefresh()
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    createAndInsertError(curRepo.value.id, "squash err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#SquashDialog err: " + e.stackTraceToString())
                }
            }
        }
    }
    val requireUserInputCommitHash = rememberSaveable { mutableStateOf(false)}
    val showCheckoutDialog = rememberSaveable { mutableStateOf(false)}
    val curCommitIndex = rememberSaveable{mutableIntStateOf(-1)}
    val refreshCommitByPredicate = { curRepo:RepoEntity, predicate:(CommitDto)->Boolean ->
        Repository.open(curRepo.fullSavePath).use { repo ->
            var commitQueryCache:CommitDto? = null
            if(enableFilterState.value) {
                val filterListIndex = filterList.value.indexOfFirst { predicate(it) }
                if(filterListIndex >= 0) {
                    filterList.value[filterListIndex] = filterList.value[filterListIndex].let {
                        Libgit2Helper.getSingleCommit(repo, repoId = curRepo.id, commitOidStr = it.oidStr, settings)
                            .copy(draw_inputs = it.draw_inputs, draw_outputs = it.draw_outputs).let { commitQueryCache = it; it }
                    }
                }
            }
            val srcListIndex = list.value.indexOfFirst { predicate(it) }
            if(srcListIndex >= 0) {
                list.value[srcListIndex] = list.value[srcListIndex].let {
                    if(commitQueryCache != null && commitQueryCache.oidStr == it.oidStr) {
                        commitQueryCache
                    }else {  
                        Libgit2Helper.getSingleCommit(repo, repoId = curRepo.id, commitOidStr = it.oidStr, settings)
                            .copy(draw_inputs = it.draw_inputs, draw_outputs = it.draw_outputs)
                    }
                }
            }
        }
    }
    val nameOfNewTag = rememberSaveable { mutableStateOf("")}
    val overwriteIfNameExistOfNewTag = rememberSaveable { mutableStateOf(false)}  
    val showDialogOfNewTag = rememberSaveable { mutableStateOf(false)}
    val hashOfNewTag = rememberSaveable { mutableStateOf( "")}
    val msgOfNewTag = rememberSaveable { mutableStateOf( "")}
    val annotateOfNewTag = rememberSaveable { mutableStateOf(false)}
    val initNewTagDialog = { hash:String ->
        doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
            hashOfNewTag.value = hash  
            overwriteIfNameExistOfNewTag.value = false
            showDialogOfNewTag.value = true
        }
    }
    if(showDialogOfNewTag.value) {
        CreateTagDialog(
            showDialog = showDialogOfNewTag,
            curRepo = curRepo.value,
            tagName = nameOfNewTag,
            commitHashShortOrLong = hashOfNewTag,
            annotate = annotateOfNewTag,
            tagMsg = msgOfNewTag,
            force = overwriteIfNameExistOfNewTag,
        ) success@{ newTagOidStr ->
            if(newTagOidStr.isBlank()) {  
                Msg.requireShowLongDuration(activityContext.getString(R.string.tag_oid_invalid))
                return@success
            }
            fullOid.value = newTagOidStr
            val tagNameIfFromTagList = shortBranchName
            if(from == CommitListFrom.TAG && nameOfNewTag.value == tagNameIfFromTagList) {
                fullyRefresh()
            }else {
                if(overwriteIfNameExistOfNewTag.value) {
                    try {
                        refreshCommitByPredicate(curRepo.value) {
                            it.tagShortNameList.contains(nameOfNewTag.value)
                        }
                    }catch (e: Exception) {
                    }
                }
                runCatching {
                    refreshCommitByPredicate(curRepo.value) {
                        it.oidStr == newTagOidStr
                    }
                }
            }
        }
    }
    val initCheckoutDialogComposableVersion = { requireUserInputHash:Boolean ->
        requireUserInputCommitHash.value = requireUserInputHash
        showCheckoutDialog.value = true
    }
    val branchNameForCheckout = rememberSaveable { mutableStateOf("") }
    val checkoutSelectedOption = rememberSaveable{ mutableIntStateOf(getDefaultCheckoutOption(false)) }
    if(showCheckoutDialog.value) {
        CheckoutDialog(
            checkoutSelectedOption = checkoutSelectedOption,
            showCheckoutDialog=showCheckoutDialog,
            branchName = branchNameForCheckout,
            from = CheckoutDialogFrom.OTHER,
            expectCheckoutType = Cons.checkoutType_checkoutCommitThenDetachHead,
            curRepo = curRepo.value,
            shortName = curCommit.value.shortOidStr,
            fullName = curCommit.value.oidStr,
            curCommitOid = curCommit.value.oidStr,
            curCommitShortOid = curCommit.value.shortOidStr,
            requireUserInputCommitHash = requireUserInputCommitHash.value,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            headChangedCallback = j@{
                if(from == CommitListFrom.BRANCH) {
                    isHEAD.value = Repository.open(curRepo.value.fullSavePath).use { repo ->
                        Libgit2Helper.resolveHEAD(repo)?.shorthand() == shortBranchName
                    }
                    if(isHEAD.value) {
                        fullyRefresh()
                        return@j
                    }
                }
                onlyUpdateRepoInfoOnce.value = true
                changeStateTriggerRefreshPage(needRefresh)
            },
            refreshPage = { checkout:Boolean, targetOid:String, forceCreateBranch:Boolean, branchName:String ->
                MyLog.d(TAG, "CommitListScreen#CheckoutDialog#refreshPage(): checkout=$checkout, targetOid=$targetOid, forceCreateBranch=$forceCreateBranch, branchName=$branchName")
                val targetMaybeIsHash = Libgit2Helper.maybeIsHash(targetOid)
                if(targetMaybeIsHash) {
                    fullOid.value = targetOid
                }
                if((from != CommitListFrom.FOLLOW_HEAD || !checkout) && (forceCreateBranch.not() || branchName != shortBranchName)) {
                    if(forceCreateBranch) {
                        runCatching {
                            refreshCommitByPredicate(curRepo.value) {
                                it.branchShortNameList.contains(branchName)
                            }
                        }
                    }
                    if(targetMaybeIsHash) {
                        runCatching {
                            refreshCommitByPredicate(curRepo.value) {
                                it.oidStr == targetOid
                            }
                        }
                    }
                }else {
                    fullyRefresh()
                }
            },
        )
    }
    val resetOid = rememberSaveable { mutableStateOf("")}
    val showResetDialog = rememberSaveable { mutableStateOf(false)}
    val closeResetDialog = {
        showResetDialog.value = false
    }
    if (showResetDialog.value) {
        ResetDialog(
            fullOidOrBranchOrTag = resetOid,
            closeDialog=closeResetDialog,
            repoFullPath = curRepo.value.fullSavePath,
            repoId=curRepo.value.id,
            refreshPage = { oldHeadCommitOid, isDetached, resetTargetCommitOid ->
                val curRepo = curRepo.value
                curRepo.isDetached = boolToDbInt(isDetached)
                fullOid.value = resetTargetCommitOid
                if(isHEAD.value) {
                    fullyRefresh()
                }else if(!isDetached) {
                    runCatching {
                        val curBranch = curRepo.branch
                        refreshCommitByPredicate(curRepo) {
                            it.branchShortNameList.contains(curBranch)
                        }
                    }
                    runCatching {
                        refreshCommitByPredicate(curRepo) {
                            it.oidStr == resetTargetCommitOid
                        }
                    }
                }
            }
        )
    }
    val showNodesInfoDialog = rememberSaveable { mutableStateOf(false) }
    val commitOfNodesInfo = mutableCustomStateOf(stateKeyTag, "commitOfNodesInfo") { CommitDto() }
    val showNodesInfo = { curCommit:CommitDto ->
        commitOfNodesInfo.value = curCommit
        showNodesInfoDialog.value = true
    }
    if(showNodesInfoDialog.value) {
        val commitOfNodesInfo = commitOfNodesInfo.value
        CopyableDialog2(
            title = stringResource(R.string.nodes),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    val hasOutputs = commitOfNodesInfo.draw_outputs.isNotEmpty()
                    if(commitOfNodesInfo.draw_inputs.isNotEmpty()) {
                        PrintNodesInfo(
                            title = "Inputs",
                            nodes = commitOfNodesInfo.draw_inputs,
                            appendEndNewLine = hasOutputs,
                        )
                    }
                    if(hasOutputs) {
                        PrintNodesInfo(
                            title = "Outputs",
                            nodes = commitOfNodesInfo.draw_outputs,
                            appendEndNewLine = false,
                        )
                    }
                }
            },
            onCancel = { showNodesInfoDialog.value = false },
            cancelBtnText = stringResource(R.string.close),
            okCompose = {},
            onOk = {}
        )
    }
    val showDetailsDialog = rememberSaveable { mutableStateOf( false)}
    val detailsString = rememberSaveable { mutableStateOf( "")}
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = stringResource(R.string.details),
            text = detailsString.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsString.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    val showItemMsgDialog = rememberSaveable { mutableStateOf(false) }
    val textOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val previewModeOnOfItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgPreviewModeOn) }
    val useSystemFontsForItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgUseSystemFonts) }
    val showItemMsg = { curCommit:CommitDto ->
        textOfItemMsgDialog.value = curCommit.msg
        showItemMsgDialog.value = true
    }
    if(showItemMsgDialog.value) {
        CommitMsgMarkDownDialog(
            dialogVisibleState = showItemMsgDialog,
            text = textOfItemMsgDialog.value,
            previewModeOn = previewModeOnOfItemMsgDialog,
            useSystemFonts = useSystemFontsForItemMsgDialog,
            basePathNoEndSlash = curRepo.value.fullSavePath,
        )
    }
    val showItemDetails = { curCommit:CommitDto ->
        val suffix = "\n\n"
        val sb = StringBuilder()
        sb.append("${activityContext.getString(R.string.hash)}: "+curCommit.oidStr).append(suffix)
        sb.append("${activityContext.getString(R.string.author)}: "+ Libgit2Helper.getFormattedUsernameAndEmail(curCommit.author, curCommit.email)).append(suffix)
        sb.append("${activityContext.getString(R.string.committer)}: "+ Libgit2Helper.getFormattedUsernameAndEmail(curCommit.committerUsername, curCommit.committerEmail)).append(suffix)
        sb.append("${activityContext.getString(R.string.date)}: "+curCommit.dateTime +" (${curCommit.getActuallyUsingTimeZoneUtcFormat(settings)})").append(suffix)
        sb.append("${activityContext.getString(R.string.timezone)}: "+(formatMinutesToUtc(curCommit.originTimeOffsetInMinutes))).append(suffix)
        if(curCommit.branchShortNameList.isNotEmpty()){
            sb.append((if(curCommit.branchShortNameList.size > 1) activityContext.getString(R.string.branches) else activityContext.getString(R.string.branch)) +": "+curCommit.cachedLineSeparatedBranchList()).append(suffix)
        }
        if(curCommit.tagShortNameList.isNotEmpty()) {
            sb.append((if(curCommit.tagShortNameList.size > 1) activityContext.getString(R.string.tags) else activityContext.getString(R.string.tag)) +": "+curCommit.cachedLineSeparatedTagList()).append(suffix)
        }
        if(curCommit.parentOidStrList.isNotEmpty()) {
            sb.append((if(curCommit.parentOidStrList.size > 1) activityContext.getString(R.string.parents) else activityContext.getString(R.string.parent)) +": "+curCommit.cachedLineSeparatedParentFullOidList()).append(suffix)
        }
        if(curCommit.hasOther()) {
            sb.append("${activityContext.getString(R.string.other)}: ${curCommit.getOther(activityContext, false)}").append(suffix)
        }
        sb.append("${Cons.flagStr}: ${curCommit.getOther(activityContext, true)}").append(suffix)
        sb.append("${activityContext.getString(R.string.msg)}: "+curCommit.msg).append(suffix)
        detailsString.value = sb.removeSuffix(suffix).toString()
        showDetailsDialog.value = true
    }
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val requireBlinkIdx = rememberSaveable{mutableIntStateOf(-1)}
    val showDiffCommitDialog = rememberSaveable { mutableStateOf(false) }
    val diffCommitsDialogCommit1 = mutableCustomStateOf(stateKeyTag, "diffCommitsDialogCommit1") { TextFieldValue("") }
    val diffCommitsDialogCommit2 = mutableCustomStateOf(stateKeyTag, "diffCommitsDialogCommit2") { TextFieldValue("") }
    val diffCommitsDialogTrueFocusCommit1FalseFocus2 = rememberSaveable { mutableStateOf(false) }
    if(showDiffCommitDialog.value) {
        DiffCommitsDialog(
            showDialog = showDiffCommitDialog,
            commit1 = diffCommitsDialogCommit1,
            commit2 = diffCommitsDialogCommit2,
            trueFocusCommit1FalseFocus2 = diffCommitsDialogTrueFocusCommit1FalseFocus2.value,
            curRepo = curRepo.value,
        )
    }
    val initDiffCommitsDialog = { commit1:String?, commit2:String?, focus1:Boolean ->
        if(commit1 != null) {
            diffCommitsDialogCommit1.value = TextFieldValue(commit1)
        }
        if(commit2 != null) {
            diffCommitsDialogCommit2.value = TextFieldValue(commit2)
        }
        if(focus1) {
            diffCommitsDialogCommit1.apply {
                value = value.copy(selection = TextRange(0, value.text.length))
            }
        }else {
            diffCommitsDialogCommit2.apply {
                value = value.copy(selection = TextRange(0, value.text.length))
            }
        }
        diffCommitsDialogTrueFocusCommit1FalseFocus2.value = focus1
        showDiffCommitDialog.value = true
    }
    val savePatchPath= rememberSaveable { mutableStateOf("")}
    val showSavePatchSuccessDialog = rememberSaveable { mutableStateOf(false)}
    if(showSavePatchSuccessDialog.value) {
        CreatePatchSuccessDialog(
            path = savePatchPath.value,
            closeDialog = {showSavePatchSuccessDialog.value = false}
        )
    }
    val showCreatePatchDialog = rememberSaveable { mutableStateOf(false)}
    val createPatchTargetHash = rememberSaveable { mutableStateOf("")}
    val createPatchParentHash = rememberSaveable { mutableStateOf("")}
    val createPatchParentList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "createPatchParentList", listOf<String>())
    val initCreatePatchDialog = { targetFullHash:String, defaultParentFullHash:String, parentList:List<String> ->
        createPatchParentList.value.clear()
        createPatchParentList.value.addAll(parentList)
        createPatchTargetHash.value = targetFullHash
        createPatchParentHash.value = defaultParentFullHash
        showCreatePatchDialog.value = true
    }
    if(showCreatePatchDialog.value) {
        val shortTarget = Libgit2Helper.getShortOidStrByFull(createPatchTargetHash.value)
        val shortParent = Libgit2Helper.getShortOidStrByFull(createPatchParentHash.value)
        val padding=10.dp
        ConfirmDialog(
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            title = stringResource(R.string.create_patch),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            text =  buildAnnotatedString {
                                append(stringResource(R.string.target)+": ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append(shortTarget)
                                }
                            },
                            modifier = Modifier.padding(horizontal = padding)
                        )
                    }
                    Row(modifier = Modifier.padding(padding)) {
                        Text(text = stringResource(R.string.select_a_parent_for_find_changes)+":")
                    }
                    SingleSelectList(
                        optionsList = createPatchParentList.value,
                        selectedOptionIndex = null,
                        selectedOptionValue = createPatchParentHash.value,
                        menuItemSelected = {_, value-> value==createPatchParentHash.value},
                        menuItemOnClick = {idx, value ->
                            createPatchParentHash.value = value
                        },
                        menuItemFormatter = {_, value ->
                            Libgit2Helper.getShortOidStrByFull(value?:"")
                        }
                    )
                }
            },
            onCancel = { showCreatePatchDialog.value = false }
        ) {
            showCreatePatchDialog.value = false
            doJobThenOffLoading(
                loadingOn,
                loadingOff,
                activityContext.getString(R.string.creating_patch)
            ) {
                try {
                    val left = createPatchParentHash.value
                    val right = createPatchTargetHash.value
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val tree1 = Libgit2Helper.resolveTree(repo, left) ?: throw RuntimeException("resolve left tree failed, 10137466")
                        val tree2 = Libgit2Helper.resolveTree(repo, right) ?: throw RuntimeException("resolve right tree failed, 11015534")
                        val outFile = FsUtils.Patch.newPatchFile(curRepo.value.repoName, left, right)
                        val ret = Libgit2Helper.savePatchToFileAndGetContent(
                            outFile=outFile,
                            repo = repo,
                            tree1 = tree1,
                            tree2 = tree2,
                            fromTo = Cons.gitDiffFromTreeToTree,
                            reverse = false,
                            treeToWorkTree = false,
                            returnDiffContent = false  
                        )
                        if(ret.hasError()) {
                            Msg.requireShowLongDuration(ret.msg)
                            if(ret.code != Ret.ErrCode.alreadyUpToDate) {  
                                createAndInsertError(repoId, "create patch of '$shortParent..$shortTarget' err: "+ret.msg)
                            }
                        }else {
                            savePatchPath.value = outFile.canonicalPath
                            showSavePatchSuccessDialog.value = true
                        }
                    }
                }catch (e:Exception) {
                    val errPrefix = "create patch err: "
                    Msg.requireShowLongDuration(e.localizedMessage ?: errPrefix)
                    createAndInsertError(curRepo.value.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, "$errPrefix${e.stackTraceToString()}")
                }
            }
        }
    }
    val showCherrypickDialog = rememberSaveable { mutableStateOf(false)}
    val cherrypickTargetHash = rememberSaveable { mutableStateOf("")}
    val cherrypickParentHash = rememberSaveable { mutableStateOf("")}
    val cherrypickParentList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "cherrypickParentList", listOf<String>())
    val cherrypickAutoCommit = rememberSaveable { mutableStateOf(false)}
    val initCherrypickDialog = { targetFullHash:String, defaultParentFullHash:String, parentList:List<String> ->
        cherrypickParentList.value.clear()
        cherrypickParentList.value.addAll(parentList)
        cherrypickTargetHash.value = targetFullHash
        cherrypickParentHash.value = defaultParentFullHash
        cherrypickAutoCommit.value = false
        showCherrypickDialog.value = true
    }
    if(showCherrypickDialog.value) {
        val shortTarget = Libgit2Helper.getShortOidStrByFull(cherrypickTargetHash.value)
        val shortParent = Libgit2Helper.getShortOidStrByFull(cherrypickParentHash.value)
        val padding=10.dp
        ConfirmDialog(
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
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
                        modifier = Modifier.padding(horizontal = padding)
                    )
                    Row(modifier = Modifier.padding(padding)) {
                        Text(text = stringResource(R.string.select_a_parent_for_find_changes)+":")
                    }
                    DisableSelection {
                        SingleSelectList(
                            optionsList = cherrypickParentList.value,
                            selectedOptionIndex = null,
                            selectedOptionValue = cherrypickParentHash.value,
                            menuItemSelected = {_, value-> value==cherrypickParentHash.value},
                            menuItemOnClick = {idx, value ->
                                cherrypickParentHash.value = value
                            },
                            menuItemFormatter = {_, value ->
                                Libgit2Helper.getShortOidStrByFull(value?:"")
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(padding))
                    MyCheckBox(text = stringResource(R.string.auto_commit), value = cherrypickAutoCommit)
                }
            },
            onCancel = { showCherrypickDialog.value = false }
        ) {
            showCherrypickDialog.value = false
            doJobThenOffLoading(
                loadingOn,
                loadingOff,
                activityContext.getString(R.string.cherrypicking)
            ) {
                Repository.open(curRepo.value.fullSavePath).use { repo->
                    val ret = Libgit2Helper.cherrypick(
                        repo,
                        targetCommitFullHash = cherrypickTargetHash.value,
                        parentCommitFullHash = cherrypickParentHash.value,
                        autoCommit = cherrypickAutoCommit.value,
                        settings = settings
                    )
                    if(ret.hasError()) {
                        Msg.requireShowLongDuration(ret.msg)
                        if(ret.code != Ret.ErrCode.alreadyUpToDate) {  
                            createAndInsertError(repoId, "cherrypick commit changes of '$shortParent..$shortTarget' err: "+ret.msg)
                        }
                    }else {
                        Msg.requireShow(activityContext.getString(R.string.success))
                    }
                }
            }
        }
    }
    val showSetPageSizeDialog = rememberSaveable { mutableStateOf(false) }
    val pageSizeForDialog =mutableCustomStateOf(stateKeyTag, "pageSizeForDialog") { TextFieldValue("") }
    val initSetPageSizeDialog = {
        pageSizeForDialog.value = pageSize.value.toString().let { TextFieldValue(it, selection = TextRange(0, it.length)) }
        showSetPageSizeDialog.value = true
    }
    if(showSetPageSizeDialog.value) {
        SetPageSizeDialog(
            pageSizeBuf = pageSizeForDialog,
            pageSize = pageSize,
            rememberPageSize = rememberPageSize,
            trueCommitHistoryFalseFileHistory = true,
            closeDialog = {showSetPageSizeDialog.value=false}
        )
    }
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    BackHandler {
        if(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
            filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
                        FilterTextField(
                            filterKeyWord = filterKeyword,
                            loading = searching.value,
                            trailingIcon = {
                                LongPressAbleIconBtn(
                                    tooltipText = stringResource(R.string.filter_by_paths),
                                    icon = Icons.AutoMirrored.Filled.List,
                                    iconContentDesc = stringResource(R.string.a_list_icon_lor_filter_commits_by_paths),
                                    iconColor = UIHelper.getIconEnableColorOrNull(pathsForFilter.value.isNotEmpty()),
                                ) {
                                    pathsForFilterBuffer.value = pathsForFilter.value  
                                    filterByEntryNameBuffer.value = filterByEntryName.value
                                    showFilterByPathsDialog.value = true
                                }
                            },
                        )
                    }else{
                        val repoAndBranchText = if(isHEAD.value) repoOnBranchOrDetachedHash.value else branchShortNameOrShortHashByFullOidForShowOnTitle.value
                        Column(
                            modifier = Modifier.combinedClickable(
                                onDoubleClick = {
                                    defaultTitleDoubleClick(scope, getActuallyListState(), getActuallyLastPosition())
                                },
                                onLongClick = {
                                    val count = if(enableFilterState.value) filterIdxList.value.size else list.value.size
                                    Msg.requireShow(replaceStringResList(activityContext.getString(R.string.item_count_n), listOf(""+count)))
                                }
                            ) { 
                                showTitleInfoDialog.value = true
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                            ) {
                                Text(
                                    text = stringResource(R.string.commit_history),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                            ) {
                                Text(
                                    text = repoAndBranchText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.secondLineFontSize
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),
                        ) {
                            resetSearchVars()
                            filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
                        }
                    } else {
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
                    if(!filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
                        Row {
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.filter),
                                icon = Icons.Filled.FilterAlt,
                                iconContentDesc = stringResource(R.string.filter),
                            ) {
                                filterKeyword.value = TextFieldValue("")
                                pathsForFilter.value = ""
                                filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = true
                            }
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.refresh),
                                icon = Icons.Filled.Refresh,
                                iconContentDesc = stringResource(id = R.string.refresh),
                                enabled = true,
                            ) {
                                fullyRefresh()
                            }
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.checkout),
                                icon = Icons.Filled.MoveToInbox,
                                iconContentDesc = stringResource(R.string.checkout),
                                enabled = true,
                            ) {
                                val requireUserInputHash = true
                                initCheckoutDialogComposableVersion(
                                    requireUserInputHash
                                )
                            }
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.reverse),
                                icon = Icons.Filled.Compare,
                                iconContentDesc = stringResource(R.string.reverse),
                                enabled = true,
                                iconColor = UIHelper.getIconEnableColorOrNull(commitHistoryRTL.value),
                            ) {
                                val newValue = !commitHistoryRTL.value
                                commitHistoryRTL.value = newValue
                                SettingsUtil.update { it.commitHistoryRTL = newValue }
                            }
                            if((proFeatureEnabled(commitsDiffCommitsTestPassed) || proFeatureEnabled(resetByHashTestPassed))) {
                                LongPressAbleIconBtn(
                                    tooltipText = stringResource(R.string.menu),
                                    icon = Icons.Filled.MoreVert,
                                    iconContentDesc = stringResource(id = R.string.menu),
                                    enabled = true,
                                ) {
                                    showTopBarMenu.value = true
                                }
                            }
                        }
                        if(showTopBarMenu.value) {
                            Row (modifier = Modifier.padding(top = MyStyleKt.TopBar.dropDownMenuTopPaddingSize)) {
                                DropdownMenu(
                                    expanded = showTopBarMenu.value,
                                    onDismissRequest = { showTopBarMenu.value=false }
                                ) {
                                    if(proFeatureEnabled(commitsDiffCommitsTestPassed)) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.diff_commits)) },
                                            onClick = {
                                                initDiffCommitsDialog(null, null, true)
                                                showTopBarMenu.value = false
                                            }
                                        )
                                    }
                                    if(proFeatureEnabled(resetByHashTestPassed)){
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.reset)) },
                                            onClick = {
                                                showResetDialog.value = true
                                                showTopBarMenu.value = false
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.graph)) },
                                        trailingIcon = {
                                            SimpleCheckBox(commitHistoryGraph.value)
                                        },
                                        onClick = {
                                            val newValue = !commitHistoryGraph.value
                                            commitHistoryGraph.value = newValue
                                            SettingsUtil.update { it.commitHistoryGraph = newValue }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.page_size)) },
                                        onClick = {
                                            initSetPageSizeDialog()
                                            showTopBarMenu.value = false
                                        }
                                    )
                                }
                            }
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
            onRefresh = { fullyRefresh() }
        ) {
            if (showLoadingDialog.value) {
                LoadingDialog(loadingText.value)
            }
            if(list.value.isEmpty()) {
                Column(
                    modifier = Modifier
                        .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
                        .padding(MyStyleKt.defaultItemPadding)
                    ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(if(loadMoreLoading.value) R.string.loading else R.string.commit_history_is_empty))
                }
            }else {
                if (showBottomSheet.value) {
                    BottomSheet(showBottomSheet, sheetState, curCommit.value.shortOidStr) {
                        if(enableFilterState.value) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.show_in_list)) {
                                filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
                                showBottomSheet.value = false
                                doJobThenOffLoading {
                                    val curItemIndex = curCommitIndex.intValue  
                                    val idxList = filterIdxList.value  
                                    doActIfIndexGood(curItemIndex, idxList) {  
                                        UIHelper.scrollToItem(scope, listState, it)  
                                        requireBlinkIdx.intValue = it  
                                    }
                                }
                            }
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.checkout)) {
                            val requireUserInputHash = false
                            initCheckoutDialogComposableVersion(requireUserInputHash)
                        }
                        if(dev_EnableUnTestedFeature || tagsTestPassed) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.new_tag)) {
                                initNewTagDialog(curCommit.value.oidStr)
                            }
                        }
                        if(proFeatureEnabled(resetByHashTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.reset)) {
                                resetOid.value = curCommit.value.oidStr
                                showResetDialog.value = true
                            }
                        }
                        if(isHEAD.value) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.squash)) {
                                val targetCommitFullOid = curCommit.value.oidStr
                                val targetCommitShortOid = curCommit.value.shortOidStr
                                doJobThenOffLoading {
                                    Repository.open(curRepo.value.fullSavePath).use { repo ->
                                        val ret = Libgit2Helper.squashCommitsCheckBeforeShowDialog(
                                            repo = repo,
                                            targetFullOidStr = targetCommitFullOid,
                                            isShowingCommitListForHEAD = isHEAD.value
                                        )
                                        if(ret.hasError()) {
                                            Msg.requireShowLongDuration(ret.msg)
                                            createAndInsertError(curRepo.value.id, "squash commits pre-check err: "+ret.msg)
                                        }else {
                                            val squashData = ret.data!!
                                            initShowSquashDialog(targetCommitFullOid, targetCommitShortOid, squashData.headFullOid, squashData.headFullName, squashData.username, squashData.email)
                                        }
                                    }
                                }
                            }
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff)) {
                            initDiffCommitsDialog(curCommit.value.oidStr, null, false)
                        }
                        if(UserUtil.isPro() && (dev_EnableUnTestedFeature || commitsDiffToLocalTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_local)) {
                                goToTreeToTreeChangeList(
                                    title = activityContext.getString(R.string.compare_to_local),
                                    repoId = curRepo.value.id,
                                    commit1 = curCommit.value.oidStr,
                                    commit2 = Cons.git_LocalWorktreeCommitHash,
                                    commitForQueryParents = Cons.git_AllZeroOidStr,
                                )
                            }
                        }
                        if(proFeatureEnabled(diffToHeadTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_head)) {
                                goToTreeToTreeChangeList(
                                    title = activityContext.getString(R.string.compare_to_head),
                                    repoId = curRepo.value.id,
                                    commit1 = curCommit.value.oidStr,
                                    commit2 = Cons.git_HeadCommitHash,
                                    commitForQueryParents = Cons.git_AllZeroOidStr,
                                )
                            }
                        }
                        if(proFeatureEnabled(cherrypickTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.cherrypick)) {
                                if(curCommit.value.parentOidStrList.isEmpty()) {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_parent_for_find_changes_for_cherrypick))
                                }else {
                                    initCherrypickDialog(curCommit.value.oidStr, curCommit.value.parentOidStrList[0], curCommit.value.parentOidStrList)
                                }
                            }
                        }
                        if(proFeatureEnabled(createPatchTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.create_patch)) {
                                if(curCommit.value.parentOidStrList.isEmpty()) {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_parent_for_find_changes_for_create_patch))
                                }else {
                                    initCreatePatchDialog(curCommit.value.oidStr, curCommit.value.parentOidStrList[0], curCommit.value.parentOidStrList)
                                }
                            }
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.details)){
                            showItemDetails(curCommit.value)
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.refresh)) {
                            val curCommit = curCommit.value
                            try {
                                refreshCommitByPredicate(curRepo.value) {
                                    it.oidStr == curCommit.oidStr
                                }
                                Msg.requireShow(activityContext.getString(R.string.success))
                            }catch (e: Exception) {
                                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                                MyLog.e(TAG, "refresh commit err: commitOid=${curCommit.oidStr}, err=${e.stackTraceToString()}")
                            }
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.nodes)){
                            showNodesInfo(curCommit.value)
                        }
                    }
                }
                val keyword = filterKeyword.value.text  
                val pathList = pathsListForFilter.value
                val needFilterByPath = pathList.isNotEmpty()
                val enableFilter = filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value && (maybeIsGoodKeyword(keyword) || needFilterByPath)
                val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
                val list = filterTheList(
                    needRefresh = filterResultNeedRefresh.value,
                    lastNeedRefresh = lastNeedRefresh,
                    orCustomDoFilterCondition = if(needFilterByPath.not()) {
                        { false }
                    }else {
                        {
                            val lastCopy = lastPathsListForFilter.value.toList()
                            val curCopy = pathList.toList()
                            lastCopy.size != curCopy.size || run {
                                var changed = false
                                for (idx in lastCopy.indices) {
                                    if (lastCopy[idx] != curCopy[idx]) {
                                        changed = true
                                        break
                                    }
                                }
                                changed
                            }
                        }
                    },
                    beforeSearchCallback = {
                        lastPathsListForFilter.value.clear()
                        lastPathsListForFilter.value.addAll(pathList)
                    },
                    enableFilter = enableFilter,
                    keyword = keyword,
                    lastKeyword = lastKeyword,
                    searching = searching,
                    token = token,
                    activityContext = activityContext,
                    filterList = filterList.value,
                    list = list.value,
                    resetSearchVars = resetSearchVars,
                    match = {idx,item -> true},
                    lastListSize = lastListSize,
                    filterIdxList = filterIdxList.value,
                    customTask = {
                        val repo = if(needFilterByPath) {
                            try{
                                Repository.open(curRepo.value.fullSavePath)
                            }catch (_:Exception) {
                                null
                            }
                        }else {
                            null
                        }
                        val canceled = initSearch(keyword = keyword, lastKeyword = lastKeyword, token = token)
                        val match = { idx:Int, it: CommitDto ->
                            var found = it.oidStr.contains(keyword, ignoreCase = true)
                                    || it.getFormattedCommitterInfo().contains(keyword, ignoreCase = true)
                                    || it.getFormattedAuthorInfo().contains(keyword, ignoreCase = true)
                                    || it.email.contains(keyword, ignoreCase = true)
                                    || it.author.contains(keyword, ignoreCase = true)
                                    || it.committerEmail.contains(keyword, ignoreCase = true)
                                    || it.committerUsername.contains(keyword, ignoreCase = true)
                                    || it.dateTime.contains(keyword, ignoreCase = true)
                                    || it.branchShortNameList.toString().contains(keyword, ignoreCase = true)
                                    || it.tagShortNameList.toString().contains(keyword, ignoreCase = true)
                                    || it.parentOidStrList.toString().contains(keyword, ignoreCase = true)
                                    || it.treeOidStr.contains(keyword, ignoreCase = true)
                                    || it.msg.contains(keyword, ignoreCase = true)
                                    || it.getOther(activityContext, false).contains(keyword, ignoreCase = true)
                                    || it.getOther(activityContext, true).contains(keyword, ignoreCase = true)
                                    || formatMinutesToUtc(it.originTimeOffsetInMinutes).contains(keyword, ignoreCase = true)
                            if(found) {
                                if(needFilterByPath && repo!=null) {
                                    val tree = Libgit2Helper.resolveTreeByTreeId(repo, Oid.of(it.treeOidStr))
                                    if(tree != null) {
                                        found = Libgit2Helper.isTreeIncludedPaths(tree, pathList, filterByEntryName.value)
                                    }
                                }
                            }
                            found
                        }
                        searching.value = true
                        filterList.value.clear()
                        repo.use { repo ->
                            search(
                                src = list.value,
                                match = match,
                                matchedCallback = { idx, item ->
                                    filterList.value.add(item)
                                    filterIdxList.value.add(idx)
                                },
                                canceled = canceled
                            )
                        }
                    },
                )
                val listState = if(enableFilter) filterListState else listState
                enableFilterState.value = enableFilter
                CompositionLocalProvider(
                    LocalLayoutDirection.provides(if(commitHistoryRTL.value) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    MyLazyColumn(
                        contentPadding = contentPadding,
                        list = list,
                        listState = listState,
                        requireForEachWithIndex = true,
                        requirePaddingAtBottom = false,
                        requireCustomBottom = true,
                        customBottom = {
                            LoadMore(
                                initSetPageSizeDialog = initSetPageSizeDialog,
                                text = loadMoreText.value,
                                enableLoadMore = !loadMoreLoading.value && hasMore.value, enableAndShowLoadToEnd = !loadMoreLoading.value && hasMore.value,
                                btnUpsideText = getLoadText(list.size, enableFilter, activityContext),
                                loadToEndOnClick = {
                                    val firstLoad = false
                                    val forceReload = false
                                    val loadToEnd = true
                                    doLoadMore(
                                        curRepo.value.fullSavePath,
                                        nextCommitOid.value,
                                        firstLoad,
                                        forceReload,
                                        loadToEnd
                                    )
                                }
                            ) {
                                val firstLoad = false
                                val forceReload = false
                                val loadToEnd = false
                                doLoadMore(
                                    curRepo.value.fullSavePath,
                                    nextCommitOid.value,
                                    firstLoad,
                                    forceReload,
                                    loadToEnd
                                )
                            }
                        }
                    ) { idx, it ->
                        CommitItem(
                            drawLocalAheadUpstreamCount = drawLocalAheadUpstreamCount.value,
                            commitHistoryGraph = commitHistoryGraph.value,
                            density = density,
                            nodeCircleRadiusInPx = nodeCircleRadiusInPx,
                            nodeCircleStartOffsetX = nodeCircleStartOffsetX,
                            nodeLineWidthInPx = nodeLineWidthInPx,
                            lineDistanceInPx = lineDistanceInPx,
                            showBottomSheet = showBottomSheet,
                            curCommit = curCommit,
                            curCommitIdx = curCommitIndex,
                            idx = idx,
                            commitDto = it,
                            requireBlinkIdx = requireBlinkIdx,
                            lastClickedItemKey = lastClickedItemKey,
                            shouldShowTimeZoneInfo = shouldShowTimeZoneInfo,
                            showItemMsg = showItemMsg
                        ) { thisObj ->
                            val parents = thisObj.parentOidStrList
                            if (parents.isEmpty()) {  
                                Msg.requireShowLongDuration(activityContext.getString(R.string.no_parent_to_compare))
                            } else {  
                                val commit2 = thisObj.oidStr
                                goToTreeToTreeChangeList(
                                    title = activityContext.getString(R.string.compare_to_parent),
                                    repoId = curRepo.value.id,
                                    commit1 = parents[0],
                                    commit2 = commit2,
                                    commitForQueryParents = commit2,
                                )
                            }
                        }
                        MyHorizontalDivider()
                    }
                    if(enableFilter && list.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Spacer(Modifier.height(50.dp))
                            Text(stringResource(if(searching.value) R.string.loading else R.string.no_matched_item), fontWeight = FontWeight.Light)
                            LoadMore(
                                modifier = Modifier.padding(top = 30.dp),
                                initSetPageSizeDialog = initSetPageSizeDialog,
                                text = loadMoreText.value,
                                btnUpsideText = getLoadText(list.size, enableFilter, activityContext),
                                enableLoadMore = !loadMoreLoading.value && hasMore.value, enableAndShowLoadToEnd = !loadMoreLoading.value && hasMore.value,
                                loadToEndOnClick = {
                                    val firstLoad = false
                                    val forceReload = false
                                    val loadToEnd = true
                                    doLoadMore(
                                        curRepo.value.fullSavePath,
                                        nextCommitOid.value,
                                        firstLoad,
                                        forceReload,
                                        loadToEnd
                                    )
                                }
                            ) {
                                val firstLoad = false
                                val forceReload = false
                                val loadToEnd = false
                                doLoadMore(
                                    curRepo.value.fullSavePath,
                                    nextCommitOid.value,
                                    firstLoad,
                                    forceReload,
                                    loadToEnd
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        doJobThenOffLoading job@{
            val (requestType, data) = getRequestDataByState<Any?>(needRefresh.value)
            val forceReload = (requestType == StateRequestType.forceReload)
            if(forceReload || curRepo.value.id.isBlank() || headOidOfThisScreen.value.isNullOrEmptyOrZero || onlyUpdateRepoInfoOnce.value) {
                val repoDb = AppModel.dbContainer.repoRepository
                val repoFromDb = repoDb.getById(repoId)
                if (repoFromDb == null) {
                    MyLog.w(TAG, "#LaunchedEffect: query repo info from db error! repoId=$repoId}")
                    return@job
                }
                curRepo.value = repoFromDb
                val repoFullPath = repoFromDb.fullSavePath
                val repoName = repoFromDb.repoName
                repoOnBranchOrDetachedHash.value = repoName
                branchShortNameOrShortHashByFullOidForShowOnTitle.value = repoName
                Repository.open(repoFullPath).use { repo ->
                    headOidOfThisScreen.value = if(isHEAD.value) {  
                        val head = Libgit2Helper.resolveHEAD(repo)
                        if (head == null) {
                            MyLog.w(TAG, "#LaunchedEffect: head is null! repoId=$repoId}")
                            return@job
                        }
                        val headOid = head.peel(GitObject.Type.COMMIT)?.id()
                        if (headOid == null || headOid.isNullOrEmptyOrZero) {
                            MyLog.w(TAG, "#LaunchedEffect: head oid is null or invalid! repoId=${repoId}, headOid=${headOid.toString()}")
                            return@job
                        }
                        repoOnBranchOrDetachedHash.value = Libgit2Helper.getRepoOnBranchOrOnDetachedHash(repoFromDb)
                        fullOid.value = headOid.toString()
                        headOid
                    }else {  
                        val commit = Libgit2Helper.resolveCommitByHash(repo, fullOid.value)
                        val commitOid = commit?.id() ?: throw RuntimeException("CommitListScreen#LaunchedEffect: resolve commit err!, fullOid='${fullOid.value}'")
                        branchShortNameOrShortHashByFullOidForShowOnTitle.value = Libgit2Helper.getBranchNameOfRepoName(repoName, branchShortNameOrShortHashByFullOid.value)
                        commitOid
                    }
                }
            }
            if(onlyUpdateRepoInfoOnce.value) {
                onlyUpdateRepoInfoOnce.value = false
            }else {
                val firstLoad = true
                val loadToEnd = false
                doLoadMore(curRepo.value.fullSavePath, headOidOfThisScreen.value, firstLoad, forceReload, loadToEnd)
            }
        }
    }
    DisposableEffect(Unit) {  
        onDispose {
            doJobThenOffLoading {
                loadChannel.close()
            }
        }
    }
    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        skipCondition = {
            showFilterByPathsDialog.value.not()
        }
    )
}
