package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.BottomSheet
import com.akcreation.gitsilent.compose.BottomSheetItem
import com.akcreation.gitsilent.compose.CommitListDialog
import com.akcreation.gitsilent.compose.CommitMsgMarkDownDialog
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CreatePatchSuccessDialog
import com.akcreation.gitsilent.compose.FileHistoryRestoreDialog
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LoadMore
import com.akcreation.gitsilent.compose.LoadingDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.RepoInfoDialogItemSpacer
import com.akcreation.gitsilent.compose.SetPageSizeDialog
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.commitsDiffCommitsTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dev.resetByHashTestPassed
import com.akcreation.gitsilent.git.FileHistoryDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.FileHistoryItem
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.getLoadText
import com.akcreation.gitsilent.screen.functions.goToDiffScreen
import com.akcreation.gitsilent.screen.functions.initSearch
import com.akcreation.gitsilent.screen.functions.search
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.DiffFromScreen
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StateRequestType
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.cache.NaviCache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doActIfIndexGood
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.formatMinutesToUtc
import com.akcreation.gitsilent.utils.getFileNameFromCanonicalPath
import com.akcreation.gitsilent.utils.getRequestDataByState
import com.akcreation.gitsilent.utils.isGoodIndexForList
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.time.TimeZoneUtil
import com.github.git24j.core.GitObject
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository
import com.github.git24j.core.Revwalk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "FileHistoryScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FileHistoryScreen(
    repoId: String,
    fileRelativePathKey:String,
    naviUp: () -> Unit,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current
    val navController = AppModel.navController
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    val loadChannel = remember { Channel<Int>() }
    val fileRelativePath = rememberSaveable { NaviCache.getByType<String>(fileRelativePathKey) ?: "" }
    val lastVersionEntryOid = rememberSaveable { mutableStateOf<String?>(null) }
    val list = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "list",
        initValue = listOf<FileHistoryDto>()
    )
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val shouldShowTimeZoneInfo = rememberSaveable { TimeZoneUtil.shouldShowTimeZoneInfo(settings) }
    val pageSize = rememberSaveable{ mutableStateOf(settings.fileHistoryPageSize) }
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
    val listState = rememberLazyListState()
    val showTopBarMenu = rememberSaveable { mutableStateOf(false)}
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val curObj = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "curObj",
        initValue = FileHistoryDto()
    )
    val curRepo = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "curRepo",
        initValue = RepoEntity(id = "")
    )
    val curObjShortOid = remember(curObj.value.commitOidStr) { derivedStateOf{
        Libgit2Helper.getShortOidStrByFull(curObj.value.commitOidStr)
    }}
    val loadMoreLoading = rememberSaveable { mutableStateOf(false)}
    val loadMoreText = rememberSaveable { mutableStateOf("")}
    val hasMore = rememberSaveable { mutableStateOf(false)}
    val needRefresh = rememberSaveable { mutableStateOf("FileHistory_refresh_init_value_c2k8")}
    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot = rememberSaveable { mutableStateOf(false) }
    val filterIdxList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "filterIdxList",
        listOf<Int>()
    )
    val filterList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "filterList",
        listOf<FileHistoryDto>()
    )
    val filterListState = rememberLazyListState()
    val enableFilterState = rememberSaveable { mutableStateOf(false)}
    val getActuallyList = {
        if(enableFilterState.value) {
            filterList.value
        }else{
            list.value
        }
    }
    val getActuallyListState = {
        if(enableFilterState.value) filterListState else listState
    }
    val goToTop = {
        UIHelper.scrollToItem(scope, getActuallyListState(), 0)
    }
    val fullyRefresh = {
        goToTop()
        changeStateTriggerRefreshPage(needRefresh, StateRequestType.forceReload)
    }
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
                        lastVersionEntryOid.value = null
                        list.value.clear()
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
                        val (retLastVersionEntryOid, retNextCommitOid) = Libgit2Helper.getFileHistoryList(
                            repo = repo,
                            revwalk = revwalk.value!!,
                            initNext = nextCommitOid.value,
                            repoId = repoId,
                            pageSize = if(loadToEnd) Int.MAX_VALUE else pageSize.value,
                            retList = list.value,  
                            loadChannel = loadChannel,
                            checkChannelFrequency = settings.commitHistoryLoadMoreCheckAbortSignalFrequency,
                            lastVersionEntryOid = lastVersionEntryOid.value,
                            fileRelativePathUnderRepo = fileRelativePath,
                            settings = settings
                        )
                        lastVersionEntryOid.value = retLastVersionEntryOid
                        nextCommitOid.value = retNextCommitOid ?: Cons.git_AllZeroOid
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
    val showRestoreDialog = rememberSaveable { mutableStateOf(false) }
    if(showRestoreDialog.value) {
        FileHistoryRestoreDialog(
            targetCommitOidStr = curObj.value.commitOidStr,
            commitMsg = curObj.value.getCachedOneLineMsg(),
            showRestoreDialog = showRestoreDialog,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            activityContext = activityContext,
            curRepo = curRepo,
            fileRelativePath = fileRelativePath,
            repoId = repoId
        )
    }
    val requireUserInputCommitHash = rememberSaveable { mutableStateOf(false)}
    val showCheckoutDialog = rememberSaveable { mutableStateOf(false)}
    val curObjIndex = rememberSaveable{mutableIntStateOf(-1)}
    val lastListSize = rememberSaveable { mutableIntStateOf(0) }
    val lastKeyword = rememberSaveable { mutableStateOf("") }
    val token = rememberSaveable { mutableStateOf("") }
    val searching = rememberSaveable { mutableStateOf(false) }
    val resetSearchVars = {
        searching.value = false
        token.value = ""
        lastKeyword.value = ""
    }
    val nameOfNewTag = rememberSaveable { mutableStateOf("")}
    val overwriteIfNameExistOfNewTag = rememberSaveable { mutableStateOf(false)}  
    val showDialogOfNewTag = rememberSaveable { mutableStateOf(false)}
    val hashOfNewTag = rememberSaveable { mutableStateOf( "")}
    val msgOfNewTag = rememberSaveable { mutableStateOf( "")}
    val annotateOfNewTag = rememberSaveable { mutableStateOf(false)}
    val initNewTagDialog = { hash:String ->
        hashOfNewTag.value = hash  
        overwriteIfNameExistOfNewTag.value = false
        showDialogOfNewTag.value = true
    }
    val initCheckoutDialogComposableVersion = { requireUserInputHash:Boolean ->
        requireUserInputCommitHash.value = requireUserInputHash
        showCheckoutDialog.value = true
    }
    val resetOid = rememberSaveable { mutableStateOf("")}
    val showResetDialog = rememberSaveable { mutableStateOf(false)}
    val closeResetDialog = {
        showResetDialog.value = false
    }
    val showDetailsDialog = rememberSaveable { mutableStateOf( false)}
    val detailsString = rememberSaveable { mutableStateOf( "")}
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
    val showItemDetails = { curObj:FileHistoryDto ->
        val suffix = "\n\n"
        val sb = StringBuilder()
        sb.append("${activityContext.getString(R.string.path)}: "+curObj.filePathUnderRepo).append(suffix)
        sb.append("${activityContext.getString(R.string.commit_id)}: "+curObj.commitOidStr).append(suffix)
        sb.append("${activityContext.getString(R.string.entry_id)}: "+curObj.treeEntryOidStr).append(suffix)
        sb.append("${activityContext.getString(R.string.author)}: "+ Libgit2Helper.getFormattedUsernameAndEmail(curObj.authorUsername, curObj.authorEmail)).append(suffix)
        sb.append("${activityContext.getString(R.string.committer)}: "+ Libgit2Helper.getFormattedUsernameAndEmail(curObj.committerUsername, curObj.committerEmail)).append(suffix)
        sb.append("${activityContext.getString(R.string.date)}: "+curObj.dateTime +" (${curObj.getActuallyUsingTimeZoneUtcFormat(settings)})").append(suffix)
        sb.append("${activityContext.getString(R.string.timezone)}: "+(formatMinutesToUtc(curObj.originTimeOffsetInMinutes))).append(suffix)
        sb.append("${activityContext.getString(R.string.msg)}: "+curObj.msg).append(suffix)
        detailsString.value = sb.removeSuffix(suffix).toString()
        showDetailsDialog.value = true
    }
    val fileHistoryDtoOfCommitListDialog = mutableCustomStateOf(stateKeyTag, "fileHistoryDtoOfCommitListDialog") { FileHistoryDto() }
    val showCommitListDialog = rememberSaveable { mutableStateOf(false) }
    val showCommits = { curObj:FileHistoryDto ->
        fileHistoryDtoOfCommitListDialog.value = curObj
        showCommitListDialog.value = true
    }
    if(showCommitListDialog.value) {
        val item = fileHistoryDtoOfCommitListDialog.value
        CommitListDialog(
            title = stringResource(R.string.commits),
            firstLineLabel = stringResource(R.string.entry_id),
            firstLineText = item.treeEntryOidStr,
            commitListLabel = stringResource(R.string.commits),
            commits = item.commitList,
            closeDialog = {showCommitListDialog.value = false}
        )
    }
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val requireBlinkIdx = rememberSaveable{mutableIntStateOf(-1)}
    val lastClickedItemKey = rememberSaveable{ SharedState.fileHistory_LastClickedItemKey }
    val savePatchPath= rememberSaveable { mutableStateOf("")}
    val showSavePatchSuccessDialog = rememberSaveable { mutableStateOf(false)}
    if(showSavePatchSuccessDialog.value) {
        CreatePatchSuccessDialog(
            path = savePatchPath.value,
            closeDialog = {showSavePatchSuccessDialog.value = false}
        )
    }
    val showSetPageSizeDialog = rememberSaveable { mutableStateOf(false) }
    val pageSizeForDialog = mutableCustomStateOf(stateKeyTag, "pageSizeForDialog") { TextFieldValue("") }
    val initSetPageSizeDialog = {
        pageSizeForDialog.value = pageSize.value.toString().let { TextFieldValue(it, selection = TextRange(0, it.length)) }
        showSetPageSizeDialog.value = true
    }
    if(showSetPageSizeDialog.value) {
        SetPageSizeDialog(
            pageSizeBuf = pageSizeForDialog,
            pageSize = pageSize,
            rememberPageSize = rememberPageSize,
            trueCommitHistoryFalseFileHistory = false,
            closeDialog = {showSetPageSizeDialog.value=false}
        )
    }
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    val titleInfo = rememberSaveable { mutableStateOf(fileRelativePath) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(
            curRepo = curRepo.value,
            showTitleInfoDialog = showTitleInfoDialog,
            prependContent = {
                Text(stringResource(R.string.file_name)+": "+ getFileNameFromCanonicalPath(fileRelativePath))
                RepoInfoDialogItemSpacer()
                Text(stringResource(R.string.file_path)+": "+fileRelativePath)
            }
        )
    }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    BackHandler {
        if(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
            filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }
    val showItemMsgDialog = rememberSaveable { mutableStateOf(false) }
    val textOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val previewModeOnOfItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgPreviewModeOn) }
    val useSystemFontsForItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgUseSystemFonts) }
    val showItemMsg = { curItem: FileHistoryDto ->
        textOfItemMsgDialog.value = curItem.msg
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
                        )
                    }else{
                        Column(
                            modifier = Modifier.combinedClickable(
                                onDoubleClick = {
                                    defaultTitleDoubleClick(scope, listState, lastPosition)
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
                                    text = stringResource(R.string.file_history),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                            ) {
                                Text(
                                    text = titleInfo.value,
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
                    Text(stringResource(if(loadMoreLoading.value) R.string.loading else R.string.file_hasnt_history_yet))
                }
            }else {
                if (showBottomSheet.value) {
                    BottomSheet(showBottomSheet, sheetState, curObjShortOid.value) {
                        if(enableFilterState.value) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.show_in_list)) {
                                filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
                                showBottomSheet.value = false
                                doJobThenOffLoading {
                                    val curItemIndex = curObjIndex.intValue  
                                    val idxList = filterIdxList.value  
                                    doActIfIndexGood(curItemIndex, idxList) {  
                                        UIHelper.scrollToItem(scope, listState, it)  
                                        requireBlinkIdx.intValue = it  
                                    }
                                }
                            }
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.restore)) {
                            showRestoreDialog.value = true
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_prev)) label@{
                            val list = list.value
                            val indexAtDiffableList = curObjIndex.intValue
                            val previousIndex = indexAtDiffableList +1
                            if(!isGoodIndexForList(previousIndex, list)) {
                                if(hasMore.value) {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.plz_lode_more_then_try_again))
                                }else {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_prev_to_compare))
                                }
                                return@label
                            }
                            val previous = list[previousIndex]
                            val commit1 = previous.commitOidStr
                            val commit2 = curObj.value.commitOidStr
                            goToDiffScreen(
                                diffableList = list.map { it.toDiffableItem() },
                                repoId = repoId,
                                fromTo = Cons.gitDiffFileHistoryFromTreeToPrev,
                                commit1OidStr = commit1,
                                commit2OidStr = commit2,
                                isDiffToLocal = false,
                                curItemIndexAtDiffableList = indexAtDiffableList,
                                localAtDiffRight = false,
                                fromScreen = DiffFromScreen.FILE_HISTORY_TREE_TO_PREV.code
                            )
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.details)) {
                            showItemDetails(curObj.value)
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.commits)) {
                            showCommits(curObj.value)
                        }
                    }
                }
                val keyword = filterKeyword.value.text  
                val enableFilter = filterModeActuallyEnabled(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value, keyword)
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
                    match = { idx, it -> true },
                    lastListSize = lastListSize,
                    filterIdxList = filterIdxList.value,
                    customTask = {
                        val canceled = initSearch(keyword = keyword, lastKeyword = lastKeyword, token = token)
                        val match = { idx:Int, it: FileHistoryDto ->
                            val found = it.treeEntryOidStr.contains(keyword, ignoreCase = true)
                                    || it.commitOidStr.contains(keyword, ignoreCase = true)
                                    || (it.commitList.find { commitOidStr -> commitOidStr.contains(keyword, ignoreCase = true) } != null)
                                    || it.authorEmail.contains(keyword, ignoreCase = true)
                                    || it.authorUsername.contains(keyword, ignoreCase = true)
                                    || it.committerEmail.contains(keyword, ignoreCase = true)
                                    || it.committerUsername.contains(keyword, ignoreCase = true)
                                    || it.dateTime.contains(keyword, ignoreCase = true)
                                    || it.msg.contains(keyword, ignoreCase = true)
                                    || formatMinutesToUtc(it.originTimeOffsetInMinutes).contains(keyword, ignoreCase = true)
                            found
                        }
                        searching.value = true
                        filterList.value.clear()
                        search(
                            src = list.value,
                            match = match,
                            matchedCallback = { idx, item ->
                                filterList.value.add(item)
                                filterIdxList.value.add(idx)
                            },
                            canceled = canceled)
                    }
                )
                val listState = if(enableFilter) filterListState else listState
                enableFilterState.value = enableFilter
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
                ) { idx, it ->
                    FileHistoryItem(
                        showBottomSheet = showBottomSheet,
                        curCommit = curObj,
                        curCommitIdx = curObjIndex,
                        idx = idx,
                        dto = it,
                        requireBlinkIdx = requireBlinkIdx,
                        lastClickedItemKey = lastClickedItemKey,
                        shouldShowTimeZoneInfo = shouldShowTimeZoneInfo,
                        showItemMsg = showItemMsg,
                    ) { thisObj ->
                        Msg.requireShow(activityContext.getString(R.string.diff_to_local))
                        goToDiffScreen(
                            diffableList = list.map { it.toDiffableItem() },
                            repoId = repoId,
                            fromTo = Cons.gitDiffFileHistoryFromTreeToLocal,
                            commit1OidStr = it.commitOidStr,
                            commit2OidStr = Cons.git_LocalWorktreeCommitHash,
                            isDiffToLocal = true,
                            curItemIndexAtDiffableList = idx,
                            localAtDiffRight = true,
                            fromScreen = DiffFromScreen.FILE_HISTORY_TREE_TO_LOCAL.code
                        )
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
    LaunchedEffect(needRefresh.value) {
        doJobThenOffLoading job@{
            val (requestType, data) = getRequestDataByState<Any?>(needRefresh.value)
            val actuallyList = if(enableFilterState.value) filterList.value else list.value
            val actuallyListState = if(enableFilterState.value) filterListState else listState
            val lastClickedItemKey = SharedState.fileHistory_LastClickedItemKey.value
            UIHelper.scrollByPredicate(scope, actuallyList, actuallyListState) { idx, item ->
                item.getItemKey() == lastClickedItemKey
            }
            val forceReload = (requestType == StateRequestType.forceReload)
            if(forceReload || curRepo.value.id.isBlank() || headOidOfThisScreen.value.isNullOrEmptyOrZero) {
                val repoDb = AppModel.dbContainer.repoRepository
                val repoFromDb = repoDb.getById(repoId)
                if (repoFromDb == null) {
                    MyLog.w(TAG, "#LaunchedEffect: query repo info from db error! repoId=$repoId}")
                    return@job
                }
                curRepo.value = repoFromDb
                val repoFullPath = repoFromDb.fullSavePath
                titleInfo.value = "[${getFileNameFromCanonicalPath(fileRelativePath)} of ${repoFromDb.repoName}]"
                Repository.open(repoFullPath).use { repo ->
                    val head = Libgit2Helper.resolveHEAD(repo)
                    if (head == null) {
                        MyLog.w(TAG, "#LaunchedEffect: head is null! repoId=$repoId}")
                        return@job
                    }
                    val headOid = head.peel(GitObject.Type.COMMIT)?.id()
                    if (headOid == null || headOid.isNullOrEmptyOrZero) {
                        MyLog.w(TAG, "#LaunchedEffect: headOid is null or invalid! repoId=$repoId, headOid=$headOid")
                        return@job
                    }
                    headOidOfThisScreen.value = headOid
                }
            }
            val firstLoad = true
            val loadToEnd = false
            doLoadMore(curRepo.value.fullSavePath, headOidOfThisScreen.value, firstLoad, forceReload, loadToEnd)
        }
    }
    DisposableEffect(Unit) {  
        onDispose {
            doJobThenOffLoading {
                loadChannel.close()
            }
        }
    }
}
