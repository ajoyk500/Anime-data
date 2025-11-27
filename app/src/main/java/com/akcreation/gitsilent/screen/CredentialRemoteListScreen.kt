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
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.FullScreenScrollableColumn
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.InfoDialog
import com.akcreation.gitsilent.compose.LinkOrUnLinkCredentialAndRemoteDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.PageCenterIconButton
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.dto.RemoteDtoForCredential
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.RemoteItemForCredential
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

private const val TAG = "CredentialRemoteListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CredentialRemoteListScreen(
    credentialId:String,
    isShowLink:Boolean,
    naviUp: () -> Unit,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val (isShowLink, setIsShowLink) = rememberSaveable { mutableStateOf(isShowLink) }
    val clipboardManager = LocalClipboardManager.current
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val isNonePage = credentialId == SpecialCredential.NONE.credentialId;
    val listState = rememberLazyListState()
    val showUnLinkAllDialog = rememberSaveable { mutableStateOf(false)}
    val curItemInPage = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curItemInPage", initValue = CredentialEntity())
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<RemoteDtoForCredential>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<RemoteDtoForCredential>())
    val needOverrideLinkItem = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "needOverrideLinkItem", initValue = RemoteDtoForCredential())
    val showOverrideLinkDialog = rememberSaveable { mutableStateOf(false)}
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val showLinkOrUnLinkDialog = rememberSaveable { mutableStateOf( false)}
    val requireDoLink = rememberSaveable { mutableStateOf(false)}
    val targetAll = rememberSaveable { mutableStateOf(false)}
    val curItem = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curItem", initValue = RemoteDtoForCredential())
    val linkOrUnlinkDialogTitle = rememberSaveable { mutableStateOf("") }
    val doLink= { remoteId: String ->
        doJobThenOffLoading {
            val remoteDb = AppModel.dbContainer.remoteRepository
            remoteDb.linkCredentialIdByRemoteId(remoteId, curItemInPage.value.id)
            changeStateTriggerRefreshPage(needRefresh)
        }
    }
    val doUnLinkAll = {
        doJobThenOffLoading {
            val remoteDb = AppModel.dbContainer.remoteRepository
            remoteDb.unlinkAllCredentialIdByCredentialId(curItemInPage.value.id)
            changeStateTriggerRefreshPage(needRefresh)
        }
    }
    if(showUnLinkAllDialog.value) {
        ConfirmDialog(title = stringResource(id = R.string.unlink_all),
            text = stringResource(id = R.string.unlink_all_ask_text),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showUnLinkAllDialog.value=false }
        ) {
            showUnLinkAllDialog.value=false
            doUnLinkAll()
        }
    }
    if(showOverrideLinkDialog.value) {
        ConfirmDialog(title = stringResource(id = R.string.override_link),
            text = stringResource(id = R.string.override_link_ask_text),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showOverrideLinkDialog.value=false }
        ) {
            showOverrideLinkDialog.value=false
            doLink(needOverrideLinkItem.value.remoteId)
        }
    }
    if(showLinkOrUnLinkDialog.value) {
        LinkOrUnLinkCredentialAndRemoteDialog(
            curItemInPage = curItemInPage,
            requireDoLink = requireDoLink.value,
            targetAll = targetAll.value,
            title = linkOrUnlinkDialogTitle.value,
            thisItem = curItem.value,
            onCancel = {showLinkOrUnLinkDialog.value=false},
            onFinallyCallback = {
                showLinkOrUnLinkDialog.value=false
                changeStateTriggerRefreshPage(needRefresh)
            },
            onErrCallback = { e->
                val errMsgPrefix = "${linkOrUnlinkDialogTitle.value} err: remote='${curItem.value.remoteName}', credential=${curItemInPage.value.name}, err="
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(curItem.value.repoId, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#LinkOrUnLinkCredentialAndRemoteDialog err: $errMsgPrefix${e.stackTraceToString()}")
            },
            onOkCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        )
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
        }
    }
    val urlForShow = rememberSaveable { mutableStateOf("")}
    val titleForShow = rememberSaveable { mutableStateOf("")}
    val showUrlDialogState = rememberSaveable { mutableStateOf(false)}
    val showUrlDialog = { title:String, url:String ->
        titleForShow.value = title
        urlForShow.value = url
        showUrlDialogState.value = true
    }
    if(showUrlDialogState.value) {
        CopyableDialog(
            title = titleForShow.value,
            text = urlForShow.value,
            onCancel = {showUrlDialogState.value=false},
        ) {
            showUrlDialogState.value=false
            clipboardManager.setText(AnnotatedString(urlForShow.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    val goToCreateLinkPage = {
        navController.navigate(Cons.nav_CredentialRemoteListScreen + "/" + credentialId + "/0")
    }
    val initLoadingText = rememberSaveable { mutableStateOf("") }
    val initLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingOn = { msg:String ->
        initLoadingText.value = msg
        initLoading.value = true
    }
    val initLoadingOff = {
        initLoading.value = false
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
                    }else{
                        Column (modifier = Modifier.combinedClickable(onDoubleClick = {
                            defaultTitleDoubleClick(scope, listState, lastPosition)
                        }) {
                            showTitleInfoDialog.value = true
                        }){
                            ScrollableRow {
                                titleString.value = if(isShowLink) stringResource(R.string.linked_remotes) else stringResource(R.string.unlinked_remotes)
                                Text(
                                    text= titleString.value,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            ScrollableRow {
                                titleSecondaryString.value = "["+curItemInPage.value.name+"]"
                                Text(
                                    text= titleSecondaryString.value,
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
                            val linkOrUnLinkAll = if(isShowLink) stringResource(R.string.unlink_all) else stringResource(R.string.link_all)
                            LongPressAbleIconBtn(
                                tooltipText = linkOrUnLinkAll,
                                icon = if(isShowLink) Icons.Filled.LinkOff else Icons.Filled.Link,
                                iconContentDesc = linkOrUnLinkAll,
                                enabled = (isShowLink.not() || isNonePage.not()) && list.value.isNotEmpty(),
                            ) {
                                requireDoLink.value = !isShowLink
                                targetAll.value = true
                                linkOrUnlinkDialogTitle.value = linkOrUnLinkAll
                                showLinkOrUnLinkDialog.value = true
                            }
                            if(isShowLink) {
                                LongPressAbleIconBtn(
                                    tooltipText = stringResource(R.string.create_link),
                                    icon = Icons.Filled.Add,
                                    iconContentDesc = stringResource(R.string.create_link),
                                ) {
                                    goToCreateLinkPage()
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
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {
            if(list.value.isEmpty()) {
                if(initLoading.value) {
                    FullScreenScrollableColumn(contentPadding) {
                        Text(initLoadingText.value)
                    }
                } else {
                    PageCenterIconButton(
                        contentPadding = contentPadding,
                        onClick = goToCreateLinkPage,
                        icon = Icons.Filled.Add,
                        text = stringResource(R.string.create_link),
                        condition = isShowLink,
                        elseContent = {
                            Text(stringResource(R.string.item_list_is_empty))
                        }
                    )
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
                    match = { idx:Int, it: RemoteDtoForCredential ->
                        it.repoName.contains(keyword, ignoreCase = true)
                                || it.remoteName.contains(keyword, ignoreCase = true)
                                || it.remoteFetchUrl.contains(keyword, ignoreCase = true)
                                || it.remotePushUrl.contains(keyword, ignoreCase = true)
                                || it.getCredentialNameOrNone().contains(keyword, ignoreCase = true)
                                || it.getPushCredentialNameOrNone().contains(keyword, ignoreCase = true)
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
                ) { idx,it->
                    RemoteItemForCredential(
                        isShowLink=isShowLink,
                        idx = idx, thisItem = it,
                        showUrlDialog = showUrlDialog,
                        actIcon = if(isShowLink) Icons.Filled.LinkOff else Icons.Filled.Link,
                        actText = if(isShowLink) stringResource(R.string.unlink) else stringResource(R.string.link),
                        actAction = if(isNonePage && isShowLink) null else ({
                            curItem.value = it
                            requireDoLink.value = !isShowLink
                            targetAll.value = false
                            linkOrUnlinkDialogTitle.value=if(requireDoLink.value) activityContext.getString(R.string.link) else activityContext.getString(R.string.unlink)  
                            showLinkOrUnLinkDialog.value=true
                        })
                    )
                    MyHorizontalDivider()
                }
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        try {
            doJobThenOffLoading(initLoadingOn, initLoadingOff, activityContext.getString(R.string.loading)) {
                val remoteDb = AppModel.dbContainer.remoteRepository
                val credentialDb = AppModel.dbContainer.credentialRepository
                curItemInPage.value = credentialDb.getById(credentialId, includeNone = true, includeMatchByDomain = true) ?: CredentialEntity(id="")
                val listFromDb = if (isShowLink) {
                    remoteDb.getLinkedRemoteDtoForCredentialList(credentialId)
                }else {
                    remoteDb.getUnlinkedRemoteDtoForCredentialList(credentialId)
                }
                list.value.clear()
                list.value.addAll(listFromDb)
                triggerReFilter(filterResultNeedRefresh)
            }
        } catch (cancel: Exception) {
        }
    }
}
