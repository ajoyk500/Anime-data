package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.BottomSheet
import com.akcreation.gitsilent.compose.BottomSheetItem
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.InfoDialog
import com.akcreation.gitsilent.compose.LinkOrUnLinkCredentialAndRemoteDialog
import com.akcreation.gitsilent.compose.LoadingTextSimple
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.data.entity.RemoteEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dto.RemoteDtoForCredential
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.CredentialItem
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf

private const val TAG = "CredentialManagerScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CredentialManagerScreen(
    remoteId:String,
    naviUp: () -> Boolean
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLinkMode = remoteId.isNotBlank()
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<CredentialEntity>() )
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<CredentialEntity>() )
    val listState = rememberLazyListState()
    val curCredential = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curCredential", initValue = CredentialEntity(id=""))
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val showLoadingDialog = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue)}
    val loadingStrRes = stringResource(R.string.loading)
    val loadingText = rememberSaveable { mutableStateOf(loadingStrRes)}
    val loadingOn = {text:String ->
        loadingText.value=text
        showLoadingDialog.value=true
    }
    val loadingOff = {
        showLoadingDialog.value=false
        loadingText.value=""
    }
    val remote =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "remote", initValue = RemoteEntity(id=""))
    val curRepo =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))
    val showLinkOrUnLinkDialog = rememberSaveable { mutableStateOf(false)}
    val onClickCurItem =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "onClickCurItem", initValue = CredentialEntity(id=""))  
    val requireDoLink = rememberSaveable { mutableStateOf(false)}
    val targetAll = rememberSaveable { mutableStateOf( false)}
    val remoteDtoForCredential = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "remoteDtoForCredential", initValue = RemoteDtoForCredential(remoteId = remoteId))
    val linkOrUnLinkDialogTitle = rememberSaveable { mutableStateOf("")}
    if(showLinkOrUnLinkDialog.value) {
        LinkOrUnLinkCredentialAndRemoteDialog(
            curItemInPage = onClickCurItem,
            requireDoLink = requireDoLink.value,
            targetAll = targetAll.value,
            title = linkOrUnLinkDialogTitle.value,  
            thisItem = remoteDtoForCredential.value,
            onCancel = { showLinkOrUnLinkDialog.value=false},
            onFinallyCallback = {
                showLinkOrUnLinkDialog.value=false
                changeStateTriggerRefreshPage(needRefresh)
            },
            onErrCallback = { e->
                val errMsgPrefix = "${linkOrUnLinkDialogTitle.value} err: remote='${remote.value.remoteName}', credential=${onClickCurItem.value.name}, err="
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(remote.value.repoId, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#LinkOrUnLinkCredentialAndRemoteDialog err: $errMsgPrefix${e.stackTraceToString()}")
            },
            onOkCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        )
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val doDelete = {
        doJobThenOffLoading {
            try{
                val credentialDb = AppModel.dbContainer.credentialRepository
                credentialDb.deleteAndUnlink(curCredential.value)
                Msg.requireShow(activityContext.getString(R.string.success))
            }finally{
                changeStateTriggerRefreshPage(needRefresh)
            }
        }
    }
    val showDeleteDialog = rememberSaveable { mutableStateOf( false)}
    if(showDeleteDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.delete_credential),
            text = stringResource(R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDeleteDialog.value = false }
        ) {   
            showDeleteDialog.value=false
            doDelete()
        }
    }
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val filterListState = rememberLazyListState()
    val enableFilterState = rememberSaveable { mutableStateOf(false)}
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
    val titleString = rememberSaveable { mutableStateOf("")}
    val titleSecondaryString = rememberSaveable { mutableStateOf("")}  
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        InfoDialog(showTitleInfoDialog) {
            Text(titleString.value)
            if(titleSecondaryString.value.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(titleSecondaryString.value)
            }
            if(isLinkMode) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(stringResource(R.string.repo)+": "+curRepo.value.repoName)
                Text(stringResource(R.string.remote)+": "+remote.value.remoteName)
                val fetchCredential = list.value.find { remote.value.credentialId == it.id }
                val pushCredential = list.value.find { remote.value.pushCredentialId == it.id }
                Text(stringResource(R.string.fetch_credential)+": "+fetchCredential?.name)
                Text(stringResource(R.string.push_credential)+": "+pushCredential?.name)
            }
        }
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
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(filterModeOn.value) {
                        FilterTextField(filterKeyWord = filterKeyword, loading = searching.value)
                    }else{
                        Column(modifier = Modifier.combinedClickable(onDoubleClick = {
                            defaultTitleDoubleClick(scope, listState, lastPosition)
                        }) {
                            showTitleInfoDialog.value = true
                        }) {
                            titleString.value = stringResource(id = R.string.credential_manager)
                            ScrollableRow {
                                Text(
                                    text = titleString.value,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if(remote.value.id.isNotEmpty()) {  
                                titleSecondaryString.value = "${stringResource(R.string.link_mode)}: [${remote.value.remoteName} of ${curRepo.value.repoName}]";
                                ScrollableRow  {
                                    Text(
                                        text= titleSecondaryString.value,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = MyStyleKt.Title.secondLineFontSize
                                    )
                                }
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
                        Row {
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
                                iconContentDesc = stringResource(id = R.string.refresh),
                            ) {
                                changeStateTriggerRefreshPage(needRefresh)
                            }
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.domains),
                                icon =  Icons.Filled.Domain,
                                iconContentDesc = stringResource(id = R.string.domains),
                            ) {
                                navController.navigate(Cons.nav_DomainCredentialListScreen)
                            }
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.create),
                                icon =  Icons.Filled.Add,
                                iconContentDesc = stringResource(id = R.string.create_new_credential),
                            ) {
                                navController.navigate(Cons.nav_CredentialNewOrEditScreen+"/"+null)
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
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {
            if(showBottomSheet.value) {
                BottomSheet(showBottomSheet, sheetState, curCredential.value.name) {
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.edit)){
                        navController.navigate(Cons.nav_CredentialNewOrEditScreen+"/"+curCredential.value.id)
                    }
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger()){
                        showDeleteDialog.value=true
                    }
                }
            }
            if (showLoadingDialog.value) {
                LoadingTextSimple(text = loadingText.value, contentPadding = contentPadding)
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
                    match = { idx:Int, it: CredentialEntity ->
                        it.name.contains(keyword, ignoreCase = true) || it.value.contains(keyword, ignoreCase = true)
                    }
                )
                val listState = if(enableFilter) filterListState else listState
                enableFilterState.value = enableFilter
                MyLazyColumn(
                    contentPadding = contentPadding,
                    list = list,
                    listState = listState,
                    requireForEachWithIndex = true,
                    requirePaddingAtBottom = true
                ) {idx, value->
                    CredentialItem(
                        showBottomSheet = showBottomSheet,
                        curCredentialState = curCredential,
                        idx = idx,
                        thisItem = value,
                        isLinkMode = isLinkMode,
                        linkedFetchId = remote.value.credentialId,
                        linkedPushId = remote.value.pushCredentialId,
                        lastClickedItemKey = lastClickedItemKey
                    ) {
                        if(remoteId.isEmpty()) {  
                            navController.navigate(Cons.nav_CredentialRemoteListScreen+"/"+it.id+"/1")
                        }else {  
                            onClickCurItem.value = it  
                            requireDoLink.value = true
                            targetAll.value=false
                            remoteDtoForCredential.value = RemoteDtoForCredential(
                                remoteId = remoteId,
                                credentialId = remote.value.credentialId,
                                pushCredentialId = remote.value.pushCredentialId
                            )
                            linkOrUnLinkDialogTitle.value = activityContext.getString(R.string.link)+" '${it.name}'"
                            showLinkOrUnLinkDialog.value=true
                        }
                    }
                    MyHorizontalDivider()
                }
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        try {
            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = activityContext.getString(R.string.loading)) job@{
                val credentialDb = AppModel.dbContainer.credentialRepository
                list.value.clear()
                list.value.addAll(credentialDb.getAll(includeMatchByDomain = true, includeNone = true))
                if(isLinkMode) {
                    val remoteFromDb = AppModel.dbContainer.remoteRepository.getById(remoteId)
                    if(remoteFromDb!=null){
                        remote.value=remoteFromDb
                        val repoFromDb = AppModel.dbContainer.repoRepository.getById(remoteFromDb.repoId)
                        if(repoFromDb!=null) {
                            curRepo.value = repoFromDb
                        }
                    }
                }
                triggerReFilter((filterResultNeedRefresh))
            }
        } catch (cancel: Exception) {
        }
    }
}