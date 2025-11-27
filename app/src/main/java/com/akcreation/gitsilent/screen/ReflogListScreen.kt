package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.akcreation.gitsilent.compose.BottomSheet
import com.akcreation.gitsilent.compose.BottomSheetItem
import com.akcreation.gitsilent.compose.CheckoutDialog
import com.akcreation.gitsilent.compose.CheckoutDialogFrom
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.FullScreenScrollableColumn
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LoadingDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.ResetDialog
import com.akcreation.gitsilent.compose.SimpleTitleDropDownMenu
import com.akcreation.gitsilent.compose.getDefaultCheckoutOption
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dev.resetByHashTestPassed
import com.akcreation.gitsilent.git.ReflogEntryDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.ReflogItem
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.formatMinutesToUtc
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.time.TimeZoneUtil
import com.github.git24j.core.Repository

private const val TAG = "ReflogListScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflogListScreen(
    repoId:String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val inDarkTheme = Theme.inDarkTheme
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val shouldShowTimeZoneInfo = rememberSaveable { TimeZoneUtil.shouldShowTimeZoneInfo(settings) }
    val refName = rememberSaveable { mutableStateOf(Cons.gitHeadStr) }
    val curClickItem = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curClickItem", initValue = ReflogEntryDto())
    val curLongClickItem = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curLongClickItem", initValue = ReflogEntryDto())
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<ReflogEntryDto>())
    val allRefList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "allRefList", initValue = listOf<String>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<ReflogEntryDto>())
    val listState = rememberLazyListState()
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))
    val defaultLoadingText = stringResource(R.string.loading)
    val loading = rememberSaveable { mutableStateOf(false)}
    val loadingText = rememberSaveable { mutableStateOf(defaultLoadingText)}
    val loadingOn = { text:String ->
        loadingText.value=text
        loading.value=true
    }
    val loadingOff = {
        loading.value=false
        loadingText.value = defaultLoadingText
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
    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn = rememberSaveable { mutableStateOf(false)}
    val lastKeyword = rememberSaveable { mutableStateOf("") }
    val token = rememberSaveable { mutableStateOf("") }
    val searching = rememberSaveable { mutableStateOf(false) }
    val resetSearchVars = {
        searching.value = false
        token.value = ""
        lastKeyword.value = ""
    }
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val filterListState = rememberLazyListState()
    val enableFilterState = rememberSaveable { mutableStateOf(false)}
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val checkoutNew = rememberSaveable { mutableStateOf( false)}
    val requireUserInputCommitHash = rememberSaveable { mutableStateOf(false)}
    val showCheckoutDialog = rememberSaveable { mutableStateOf(false)}
    val initCheckoutDialog = { requireUserInputHash:Boolean ->
        requireUserInputCommitHash.value = requireUserInputHash
        showCheckoutDialog.value = true
    }
    val branchNameForCheckout = rememberSaveable { mutableStateOf("") }
    val checkoutSelectedOption = rememberSaveable{ mutableIntStateOf(getDefaultCheckoutOption(false)) }
    if(showCheckoutDialog.value) {
        val id = (if(checkoutNew.value) curLongClickItem.value.idNew else curLongClickItem.value.idOld) ?: Cons.git_AllZeroOid
        if(id.isNullOrEmptyOrZero) {  
            showCheckoutDialog.value = false
            Msg.requireShow(stringResource(R.string.invalid_oid))
        }else {
            val fullOidStr = id.toString()
            val shortOidStr = Libgit2Helper.getShortOidStrByFull(fullOidStr)
            CheckoutDialog(
                checkoutSelectedOption = checkoutSelectedOption,
                showCheckoutDialog=showCheckoutDialog,
                branchName = branchNameForCheckout,
                from = CheckoutDialogFrom.OTHER,
                expectCheckoutType = Cons.checkoutType_checkoutCommitThenDetachHead,
                curRepo = curRepo.value,
                shortName = shortOidStr,
                fullName = fullOidStr,
                curCommitOid = fullOidStr,
                curCommitShortOid = shortOidStr,
                requireUserInputCommitHash = requireUserInputCommitHash.value,
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                refreshPage = { _, _, _, _, ->
                    changeStateTriggerRefreshPage(needRefresh)
                },
            )
        }
    }
    val resetOid = rememberSaveable { mutableStateOf("")}
    val resetNew = rememberSaveable { mutableStateOf( false)}
    val showResetDialog = rememberSaveable { mutableStateOf(false)}
    val closeResetDialog = {
        showResetDialog.value = false
    }
    if (showResetDialog.value) {
        val id = (if(resetNew.value) curLongClickItem.value.idNew else curLongClickItem.value.idOld) ?: Cons.git_AllZeroOid
        if(id.isNullOrEmptyOrZero) {  
            showResetDialog.value = false
            Msg.requireShow(stringResource(R.string.invalid_oid))
        }else{
            resetOid.value = id.toString()
            ResetDialog(
                fullOidOrBranchOrTag = resetOid,
                closeDialog=closeResetDialog,
                repoFullPath = curRepo.value.fullSavePath,
                repoId=curRepo.value.id,
                refreshPage = { _, _, _ ->
                    changeStateTriggerRefreshPage(needRefresh)
                }
            )
        }
    }
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(
            prependContent = {
                Row {
                    Text(stringResource(R.string.reference) + ": " + refName.value)
                }
            },
            curRepo = curRepo.value,
            showTitleInfoDialog = showTitleInfoDialog
        )
    }
    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val switchRef = { newRef: String ->
        refName.value = newRef
        changeStateTriggerRefreshPage(needRefresh)
    }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    BackHandler {
        if(filterModeOn.value) {
            filterModeOn.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }
    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingOn = { msg:String ->
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
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
                        SimpleTitleDropDownMenu(
                            dropDownMenuExpandState = dropDownMenuExpandState,
                            curSelectedItem = refName.value,
                            itemList = allRefList.value,
                            titleClickEnabled = true,
                            showHideMenuIconContentDescription = stringResource(R.string.switch_reference),
                            titleFirstLineFormatter = { it },
                            titleSecondLineFormatter = { Libgit2Helper.getRepoOnBranchOrOnDetachedHash(curRepo.value) },
                            menuItemFormatter= { it },
                            titleOnLongClick = { showTitleInfoDialog.value = true },
                            itemOnClick = { switchRef(it) },
                            isItemSelected = { it == refName.value }
                        )
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
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {
            if (loading.value) {
                LoadingDialog(text = loadingText.value)
            }
            if (showBottomSheet.value) {
                val title = Libgit2Helper.getShortOidStrByFull((curLongClickItem.value.idOld ?: Cons.git_AllZeroOid).toString())+".."+Libgit2Helper.getShortOidStrByFull((curLongClickItem.value.idNew ?: Cons.git_AllZeroOid).toString())
                BottomSheet(showBottomSheet, sheetState, title) {
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.checkout_new)) {
                        checkoutNew.value = true
                        val requireUserInputHash = false
                        initCheckoutDialog(requireUserInputHash)
                    }
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.checkout_old)) {
                        checkoutNew.value = false
                        val requireUserInputHash = false
                        initCheckoutDialog(requireUserInputHash)
                    }
                    if(proFeatureEnabled(resetByHashTestPassed)) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.reset_new)) {
                            resetNew.value=true
                            showResetDialog.value = true
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.reset_old)) {
                            resetNew.value=false
                            showResetDialog.value = true
                        }
                    }
                }
            }
            if(list.value.isEmpty()) {
                FullScreenScrollableColumn(contentPadding) {
                    Text(stringResource(if(isInitLoading.value) R.string.loading else R.string.item_list_is_empty))
                }
            }else {
                val keyword = filterKeyword.value.text  
                val enableFilter = filterModeActuallyEnabled(filterModeOn.value, keyword)
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
                    match = { idx:Int, it: ReflogEntryDto ->
                        it.username.contains(keyword, ignoreCase = true)
                                || it.email.contains(keyword, ignoreCase = true)
                                || it.date.contains(keyword, ignoreCase = true)
                                || it.msg.contains(keyword, ignoreCase = true)
                                || it.idNew.toString().contains(keyword, ignoreCase = true)
                                || it.idOld.toString().contains(keyword, ignoreCase = true)
                                || formatMinutesToUtc(it.originTimeZoneOffsetInMinutes).contains(keyword, ignoreCase = true)
                    }
                )
                val listState = if(enableFilter) filterListState else listState
                enableFilterState.value = enableFilter
                MyLazyColumn(
                    contentPadding = contentPadding,
                    list = list,
                    listState = listState,
                    requireForEachWithIndex = true,
                    requirePaddingAtBottom = true,
                    forEachCb = {},
                ){idx, it->
                    ReflogItem(repoId, showBottomSheet, curLongClickItem, lastClickedItemKey, shouldShowTimeZoneInfo, it) {  
                        val suffix = "\n\n"
                        val sb = StringBuilder()
                        sb.append(activityContext.getString(R.string.new_oid)).append(": ").append(it.idNew).append(suffix)
                        sb.append(activityContext.getString(R.string.old_oid)).append(": ").append(it.idOld).append(suffix)
                        sb.append(activityContext.getString(R.string.date)).append(": ").append(it.date+" (${formatMinutesToUtc(it.actuallyUsingTimeZoneOffsetInMinutes)})").append(suffix)
                        sb.append(activityContext.getString(R.string.timezone)).append(": ").append(formatMinutesToUtc(it.originTimeZoneOffsetInMinutes)).append(suffix)
                        sb.append(activityContext.getString(R.string.author)).append(": ").append(Libgit2Helper.getFormattedUsernameAndEmail(it.username, it.email)).append(suffix)
                        sb.append(activityContext.getString(R.string.msg)).append(": ").append(it.msg).append(suffix)
                        detailsString.value = sb.removeSuffix(suffix).toString()
                        curClickItem.value = it
                        showDetailsDialog.value=true
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
                allRefList.value.clear()
                if(repoId.isNotBlank()) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb != null) {
                        curRepo.value = repoFromDb
                        Repository.open(repoFromDb.fullSavePath).use {repo ->
                            Libgit2Helper.getReflogList(repo, refName.value, out = list.value, settings)
                            allRefList.value.addAll(Libgit2Helper.getAllRefs(repo, includeHEAD = true))
                        }
                    }else {
                        Msg.requireShowLongDuration("err: query repo failed")
                    }
                }else {
                    Msg.requireShowLongDuration("err: invalid repo id")
                }
                triggerReFilter(filterResultNeedRefresh)
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect() err: "+e.stackTraceToString())
        }
    }
}
