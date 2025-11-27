package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.compose.BottomSheet
import com.akcreation.gitsilent.compose.BottomSheetItem
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.ErrorEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.ErrorItem
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf

private const val TAG = "ErrorListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ErrorListScreen(
    repoId:String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val activityContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<ErrorEntity>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<ErrorEntity>())
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))
    val lazyListState = rememberLazyListState()
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val curObjInState = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curObjInState",initValue = ErrorEntity())
    val showClearAllConfirmDialog = rememberSaveable { mutableStateOf(false)}
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
    val removeItemByPredicate = { predicate:(ErrorEntity)->Boolean ->
        if(enableFilterState.value) {
            filterList.value.removeIf { predicate(it) }
        }
        list.value.removeIf { predicate(it) }
    }
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val getActuallyListState = {
        if(enableFilterState.value) filterListState else lazyListState
    }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    if(showClearAllConfirmDialog.value) {
        ConfirmDialog(
            title=stringResource(R.string.clear_all),
            text=stringResource(R.string.clear_all_ask_text),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showClearAllConfirmDialog.value=false },
            onOk = {
                showClearAllConfirmDialog.value = false
                doJobThenOffLoading {
                    AppModel.dbContainer.errorRepository.deleteByRepoId(repoId)
                    Msg.requireShow(activityContext.getString(R.string.success))
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        )
    }
    val viewDialogText = rememberSaveable { mutableStateOf("") }
    val showViewDialog = rememberSaveable { mutableStateOf(false) }
    if(showViewDialog.value) {
        CopyableDialog(
            title = stringResource(R.string.error_msg),
            text = viewDialogText.value,
            onCancel = {
                showViewDialog.value=false
            }
        ) { 
            showViewDialog.value=false
            clipboardManager.setText(AnnotatedString(viewDialogText.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
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
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(filterModeOn.value) {
                        FilterTextField(filterKeyWord = filterKeyword, loading = searching.value)
                    }else {
                        Column(modifier = Modifier.combinedClickable(onDoubleClick = {
                            defaultTitleDoubleClick(scope, lazyListState, lastPosition)
                        }) {
                            showTitleInfoDialog.value=true
                        }) {
                            ScrollableRow {
                                Text(
                                    text= stringResource(R.string.error),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            ScrollableRow {
                                Text(
                                    text= "[${curRepo.value.repoName}]",
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
                    }else{
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
                            icon = Icons.Filled.Refresh,
                            iconContentDesc = stringResource(R.string.refresh),
                        ) {
                            changeStateTriggerRefreshPage(needRefresh)
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.clear_all),
                            icon =  Icons.Filled.DeleteSweep,
                            iconContentDesc = stringResource(R.string.clear_all),
                        ) {
                            showClearAllConfirmDialog.value = true
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
                    listState = lazyListState,
                    filterListLastPosition = filterLastPosition,
                    listLastPosition = lastPosition,
                    showFab = pageScrolled
                )
            }
        }
    ) { contentPadding ->
        if(showBottomSheet.value) {
            BottomSheet(showBottomSheet, sheetState, stringResource(R.string.id)+": "+curObjInState.value.id) {
                BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger()) {
                    val target = curObjInState.value
                    doJobThenOffLoading {
                        AppModel.dbContainer.errorRepository.delete(target)
                        removeItemByPredicate {
                            it.id == target.id
                        }
                        Msg.requireShow(activityContext.getString(R.string.success))
                    }
                }
            }
        }
        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {
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
                match = { idx: Int, it: ErrorEntity ->
                    it.msg.contains(keyword, ignoreCase = true)
                            || it.date.contains(keyword, ignoreCase = true)
                            || it.id.contains(keyword, ignoreCase = true)
                }
            )
            val listState = if(enableFilter) filterListState else lazyListState
            enableFilterState.value = enableFilter
            MyLazyColumn(
                contentPadding = contentPadding,
                list = list,
                listState = listState,
                requireForEachWithIndex = true,
                requirePaddingAtBottom = true
            ) { idx, it ->
                ErrorItem(showBottomSheet,curObjInState,idx, lastClickedItemKey, it) {
                    val suffix = "\n\n"
                    val sb = StringBuilder()
                    sb.append(activityContext.getString(R.string.id)).append(": ").append(it.id).append(suffix)
                    sb.append(activityContext.getString(R.string.date)).append(": ").append(it.date).append(suffix)
                    sb.append(activityContext.getString(R.string.msg)).append(": ").append(it.msg).append(suffix)
                    viewDialogText.value = sb.removeSuffix(suffix).toString()
                    showViewDialog.value = true
                }
                MyHorizontalDivider()
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        try {
            if (repoId.isNotBlank()) {
                doJobThenOffLoading {
                    list.value.clear()
                    curRepo.value = AppModel.dbContainer.repoRepository.getByIdNoSyncWithGit(repoId) ?: RepoEntity(id="")
                    val errDb = AppModel.dbContainer.errorRepository
                    val errList = errDb.getListByRepoId(repoId)
                    list.value.addAll(errList)
                    val repoDb = AppModel.dbContainer.repoRepository
                    if (errList.isNotEmpty()) { 
                        repoDb.checkedAllErrById(repoId)
                    }else {  
                        repoDb.updateErrFieldsById(repoId, Cons.dbCommonFalse, "")
                    }
                    triggerReFilter(filterResultNeedRefresh)
                }
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect() err: repoId=$repoId , err is:${e.stackTraceToString()}")
        }
    }
    DisposableEffect(Unit) {
        onDispose {
        }
    }
}