package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.BottomSheet
import com.akcreation.gitsilent.compose.BottomSheetItem
import com.akcreation.gitsilent.compose.CenterPaddingRow
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CreateRemoteDialog
import com.akcreation.gitsilent.compose.FetchRemotesDialog
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.FullScreenScrollableColumn
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MyLazyColumn
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.SetBranchForRemoteDialog
import com.akcreation.gitsilent.compose.UnLinkCredentialAndRemoteDialogForRemoteListPage
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RemoteEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.createRemoteTestPassed
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dev.shallowAndSingleBranchTestPassed
import com.akcreation.gitsilent.dto.RemoteDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.RemoteItem
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.getSecFromTime
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.showErrAndSaveLog
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.github.git24j.core.Remote
import com.github.git24j.core.Repository

private const val TAG = "RemoteListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RemoteListScreen(
    repoId:String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current
    val navController = AppModel.navController
    val dbContainer = AppModel.dbContainer
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<RemoteDto>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<RemoteDto>())
    val lazyListState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id="") )
    val curObjInState = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curObjInState", initValue = RemoteDto())
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val showFetchAllDialog = rememberSaveable { mutableStateOf(false)}
    val showSetUrlDialog = rememberSaveable { mutableStateOf(false)}
    val isPushUrl = rememberSaveable { mutableStateOf(false)}
    val urlTextForSetUrlDialog = rememberSaveable { mutableStateOf("")}
    val oldUrlTextForSetUrlDialog = rememberSaveable { mutableStateOf("")}
    val urlErrMsg = rememberSaveable { mutableStateOf( "")}
    val showUnlinkCredentialDialog = rememberSaveable { mutableStateOf(false)}
    val showSetBranchDialog = rememberSaveable { mutableStateOf( false)}
    val defaultLoadingText = stringResource(R.string.loading)
    val isLoading = rememberSaveable { mutableStateOf( false)}
    val loadingText = rememberSaveable { mutableStateOf(defaultLoadingText)}
    val loadingOn = {msg:String ->
        loadingText.value=msg
        isLoading.value=true
    }
    val loadingOff = {
        isLoading.value=false
        loadingText.value = defaultLoadingText
    }
    val showCreateRemoteDialog = rememberSaveable { mutableStateOf(false)}
    val remoteNameForCreate = rememberSaveable { mutableStateOf( "")}
    val remoteUrlForCreate = rememberSaveable { mutableStateOf("")}
    if(showCreateRemoteDialog.value) {
        CreateRemoteDialog(
            show = showCreateRemoteDialog,
            curRepo = curRepo.value,
            remoteName = remoteNameForCreate,
            remoteUrl = remoteUrlForCreate,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            onErr = { e->
                val actionDesc = "create remote"
                val errMsgPrefix = "$actionDesc err: remoteName='${remoteNameForCreate.value}', remoteUrl=${remoteUrlForCreate.value}, err="
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(curRepo.value.id, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#CreateRemoteDialog: $errMsgPrefix${e.stackTraceToString()}")
            },
            onFinally = {
                changeStateTriggerRefreshPage(needRefresh)
            }
        )
    }
    val showDelRemoteDialog = rememberSaveable { mutableStateOf(false)}
    if(showDelRemoteDialog.value) {
        val remoteWillDel = curObjInState.value
        val remoteNameWillDel = remoteWillDel.remoteName
        ConfirmDialog(
            title = stringResource(id = R.string.delete) +" '$remoteNameWillDel'",
            text = stringResource(id = R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDelRemoteDialog.value = false }
        ) {
            showDelRemoteDialog.value = false
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.deleting)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val ret = Libgit2Helper.delRemote(repo, remoteNameWillDel)
                        val noExist = ret.msg.startsWith("remote") && ret.msg.endsWith("does not exist")
                        if(ret.success() || noExist) {
                            val remoteDb = AppModel.dbContainer.remoteRepository
                            remoteDb.delete(RemoteEntity(id=remoteWillDel.remoteId))  
                        }else{
                            throw ret.exception ?: Exception(ret.msg)
                        }
                        if(noExist){  
                            Msg.requireShowLongDuration(ret.msg)
                        }else{ 
                            Msg.requireShow(activityContext.getString(R.string.success))
                        }
                    }
                }catch (e:Exception) {
                    val actionDesc = "delete remote"
                    val errMsgPrefix = "$actionDesc err: remoteName='$remoteNameWillDel', err="
                    Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                    createAndInsertError(curRepo.value.id, errMsgPrefix + e.localizedMessage)
                    MyLog.e(TAG, "$errMsgPrefix${e.stackTraceToString()}")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    val showViewDialog = rememberSaveable { mutableStateOf(false)}
    val viewDialogText = rememberSaveable { mutableStateOf("")}
    val clipboardManager = LocalClipboardManager.current
    if(showViewDialog.value) {
        CopyableDialog(
            title = stringResource(id = R.string.remote_info),
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
    if(showSetBranchDialog.value) {
        SetBranchForRemoteDialog(
            stateKeyTag = stateKeyTag,
            curRepo = curRepo.value,
            remoteName = curObjInState.value.remoteName,
            isAllInitValue = curObjInState.value.branchMode == Cons.dbRemote_Fetch_BranchMode_All,
            onCancel = {showSetBranchDialog.value=false},
        ) { remoteName:String, isAll: Boolean, branchCsvStr: String ->
            showSetBranchDialog.value=false
            doJobThenOffLoading onOk@{
                Repository.open(curRepo.value.fullSavePath).use { repo->
                    val config = Libgit2Helper.getRepoConfigForWrite(repo)
                    val ret = if(isAll) {
                        Libgit2Helper.setRemoteFetchRefSpecToGitConfig(
                            config = config,
                            fetch_BranchMode = Cons.dbRemote_Fetch_BranchMode_All,
                            remote = remoteName,
                            branchOrBranches = Cons.gitFetchAllBranchSign,
                            appContext = activityContext
                            )
                    }else {
                        Libgit2Helper.setRemoteFetchRefSpecToGitConfig(
                            config=config,
                            fetch_BranchMode = Cons.dbRemote_Fetch_BranchMode_CustomBranches,
                            remote = remoteName,
                            branchOrBranches = branchCsvStr,
                            appContext = activityContext
                        )
                    }
                    if(ret.hasError()) {
                        Msg.requireShowLongDuration(ret.msg)
                    }else{
                        Msg.requireShow(activityContext.getString(R.string.saved))
                    }
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    if(showFetchAllDialog.value) {
        FetchRemotesDialog(
            title = stringResource(R.string.fetch_all),
            text = stringResource(R.string.fetch_all_are_u_sure),
            remoteList = list.value,
            closeDialog = { showFetchAllDialog.value = false },
            curRepo = curRepo.value,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            refreshPage = { changeStateTriggerRefreshPage(needRefresh) },
        )
    }
    if(showSetUrlDialog.value) {
        ConfirmDialog(
            okBtnEnabled = isPushUrl.value || urlTextForSetUrlDialog.value.isNotEmpty(),
            title = if(isPushUrl.value) stringResource(id = R.string.set_push_url) else stringResource(id = R.string.set_url),
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Row {
                            Text(text = stringResource(id = R.string.remote)+": ")
                            Text(text = curObjInState.value.remoteName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = urlTextForSetUrlDialog.value,
                        singleLine = true,
                        isError = urlErrMsg.value.isNotEmpty(),
                        supportingText = {
                            if (urlErrMsg.value.isNotEmpty()) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = urlErrMsg.value,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (urlErrMsg.value.isNotEmpty()) {
                                Icon(imageVector=Icons.Filled.Error,
                                    contentDescription=urlErrMsg.value,
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        onValueChange = {
                            urlTextForSetUrlDialog.value = it
                            urlErrMsg.value="" 
                        },
                        label = {
                            Text(if(isPushUrl.value) stringResource(id = R.string.push_url) else stringResource(R.string.url))
                        }
                    )
                    if(isPushUrl.value) {
                        MySelectionContainer {
                            Text(text = stringResource(R.string.leave_it_empty_will_use_url), color=MyStyleKt.TextColor.getHighlighting())
                        }
                    }
                }
            },
            onCancel = { showSetUrlDialog.value = false }
        ) onOk@{
            val remoteName = curObjInState.value.remoteName
            val remoteId = curObjInState.value.remoteId
            val newUrl = urlTextForSetUrlDialog.value
            val oldUrl = oldUrlTextForSetUrlDialog.value
            val repoFullPath = curRepo.value.fullSavePath
            val setUrlPrefix = if(isPushUrl.value) "set pushUrl" else "set url"
            try {
                if(!isPushUrl.value && newUrl.isBlank()) {
                    urlErrMsg.value = activityContext.getString(R.string.err_url_is_empty)
                    return@onOk
                }
                if(newUrl == oldUrl) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.url_not_changed))
                    urlErrMsg.value=""
                    showSetUrlDialog.value=false
                    return@onOk
                }
                if(isPushUrl.value && newUrl.isBlank()) {
                    showSetUrlDialog.value=false
                    doJobThenOffLoading {
                        try {
                            Repository.open(repoFullPath).use { repo->
                                Libgit2Helper.deletePushUrl(Libgit2Helper.getRepoConfigForWrite(repo), remoteName)
                            }
                            val remoteDb = AppModel.dbContainer.remoteRepository
                            remoteDb.updatePushUrlById(remoteId, "")  
                            Msg.requireShow(activityContext.getString(R.string.success))
                        }catch (e:Exception) {
                            val err1 = e.localizedMessage ?: activityContext.getString(R.string.unknown_err)
                            Msg.requireShowLongDuration(err1)
                            val errWillSave = "$setUrlPrefix for remote '$remoteName' err (delete pushUrl): $err1"
                            createAndInsertError(repoId, errWillSave)
                        }finally {
                            changeStateTriggerRefreshPage(needRefresh)
                        }
                    }
                    return@onOk
                }
                showSetUrlDialog.value=false
                doJobThenOffLoading {
                    try {
                        Repository.open(repoFullPath).use { repo ->
                            if(isPushUrl.value){
                                Remote.setPushurl(repo, remoteName, newUrl)
                            }else{
                                Remote.setUrl(repo, remoteName, newUrl)
                            }
                        }
                        val remoteDb = AppModel.dbContainer.remoteRepository
                        if(isPushUrl.value){
                            remoteDb.updatePushUrlById(remoteId, newUrl)
                        }else {
                            remoteDb.updateRemoteUrlById(remoteId, newUrl, requireTransaction=false)
                        }
                        Msg.requireShow(activityContext.getString(R.string.success))
                    }catch (e:Exception) {
                        val err1 = (e.localizedMessage ?: activityContext.getString(R.string.unknown_err))
                        Msg.requireShowLongDuration(err1)
                        val errWillSave = "$setUrlPrefix for remote '$remoteName' err: $err1"
                        createAndInsertError(repoId, errWillSave)
                    }finally {
                        changeStateTriggerRefreshPage(needRefresh)
                    }
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "$setUrlPrefix in RemoteList err: remoteName=${remoteName}, newUrl=$newUrl, oldUrl=$oldUrl, err is:\n${e.stackTraceToString()}")
                val err1 = e.localizedMessage ?: activityContext.getString(R.string.unknown_err)
                if(showSetUrlDialog.value) {  
                    urlErrMsg.value = err1  
                }else { 
                    urlErrMsg.value=""  
                    Msg.requireShowLongDuration(err1)
                    val errWillSave = "$setUrlPrefix for remote '$remoteName' err: $err1"
                    doJobThenOffLoading {
                        createAndInsertError(repoId, errWillSave)
                    }
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }
    if(showUnlinkCredentialDialog.value) {
        UnLinkCredentialAndRemoteDialogForRemoteListPage(
            remoteId = curObjInState.value.remoteId,
            remoteName = curObjInState.value.remoteName,
            onCancel = { showUnlinkCredentialDialog.value = false }
        ) {
            showUnlinkCredentialDialog.value=false
            changeStateTriggerRefreshPage(needRefresh)
        }
    }
    val doFetch:suspend (String?,RepoEntity)->Boolean = doFetch@{remoteNameParam:String?, curRepo:RepoEntity ->  
        var fetchSuccessRetVal = false
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                val remoteName = remoteNameParam
                if (remoteName.isNullOrBlank()) {
                    throw RuntimeException(activityContext.getString(R.string.remote_name_is_invalid))
                }
                val credential = Libgit2Helper.getRemoteCredential(
                    dbContainer.remoteRepository,
                    dbContainer.credentialRepository,
                    curRepo.id,
                    remoteName,
                    trueFetchFalsePush = true
                )
                Libgit2Helper.fetchRemoteForRepo(repo, remoteName, credential, curRepo)
            }
            val repoDb = AppModel.dbContainer.repoRepository
            repoDb.updateLastUpdateTime(curRepo.id, getSecFromTime())
            Msg.requireShow(activityContext.getString(R.string.success))
            fetchSuccessRetVal = true
        } catch (e: Exception) {
            showErrAndSaveLog(
                TAG,
                "#doFetch() from RemoteList Page err: " + e.stackTraceToString(),
                "fetch err: " + e.localizedMessage,
                Msg.requireShowLongDuration,
                curRepo.id
            )
            fetchSuccessRetVal = false
        }
        return@doFetch fetchSuccessRetVal
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
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
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
                    }else {
                        val repoName = curRepo.value.repoName
                        Column (modifier = Modifier.combinedClickable(onDoubleClick = {
                            defaultTitleDoubleClick(scope, lazyListState, lastPosition)
                        }) {
                            showTitleInfoDialog.value = true
                        }){
                            Text(
                                text= stringResource(R.string.remotes),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = "[$repoName]",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = MyStyleKt.Title.secondLineFontSize
                            )
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
                            icon =  Icons.Filled.Refresh,
                            iconContentDesc = stringResource(R.string.refresh),
                        ) {
                            changeStateTriggerRefreshPage(needRefresh)
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.fetch_all),
                            icon =  Icons.Filled.Downloading,
                            iconContentDesc = stringResource(R.string.fetch_all),
                        ) {
                            showFetchAllDialog.value = true
                        }
                        if(proFeatureEnabled(createRemoteTestPassed)) {
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.create),
                                icon =  Icons.Filled.Add,
                                iconContentDesc = stringResource(R.string.create),
                            ) {
                                showCreateRemoteDialog.value = true
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
                    listState = lazyListState,
                    filterListLastPosition = filterLastPosition,
                    listLastPosition = lastPosition,
                    showFab = pageScrolled
                )
            }
        }
    ) { contentPadding ->
        if(showBottomSheet.value) {
            BottomSheet(showBottomSheet, sheetState, curObjInState.value.remoteName) {
                BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text= stringResource(R.string.fetch)){
                    val fetchingRemoteLoadingText = replaceStringResList(activityContext.getString(R.string.fetching_remotename), listOf(curObjInState.value.remoteName))
                    doJobThenOffLoading(loadingOn, loadingOff, fetchingRemoteLoadingText) {
                        doFetch(curObjInState.value.remoteName, curRepo.value)
                        changeStateTriggerRefreshPage(needRefresh)
                    }
                }
                if(dev_EnableUnTestedFeature || shallowAndSingleBranchTestPassed) {
                    val isPro = UserUtil.isPro()
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, enabled = isPro,
                        text=if(isPro) stringResource(R.string.set_branch_mode) else stringResource(R.string.set_branch_mode_pro_only)
                    ){
                        showSetBranchDialog.value=true
                    }
                }
                BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.set_url)){
                    val oldUrl = curObjInState.value.remoteUrl
                    urlTextForSetUrlDialog.value = oldUrl  
                    oldUrlTextForSetUrlDialog.value = oldUrl 
                    isPushUrl.value = false
                    urlErrMsg.value=""
                    showSetUrlDialog.value=true
                }
                if(UserUtil.isPro()) {
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.set_push_url)){
                        val oldUrl = if(curObjInState.value.pushUrlTrackFetchUrl) "" else curObjInState.value.pushUrl
                        urlTextForSetUrlDialog.value = oldUrl  
                        oldUrlTextForSetUrlDialog.value = oldUrl 
                        isPushUrl.value = true
                        urlErrMsg.value=""
                        showSetUrlDialog.value=true
                    }
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.link_credential)){
                        navController.navigate(Cons.nav_CredentialManagerScreen+"/${curObjInState.value.remoteId}")
                    }
                }
                if(proFeatureEnabled(createRemoteTestPassed)) {
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger()){
                        showDelRemoteDialog.value=true
                    }
                }
            }
        }
        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {
            if(isLoading.value || list.value.isEmpty()) {
                FullScreenScrollableColumn(contentPadding) {
                    if(isLoading.value) {
                        Text(text = loadingText.value)
                    }else {
                        Row {
                            Text(text = stringResource(R.string.item_list_is_empty))
                        }
                        CenterPaddingRow {
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Add,
                                tooltipText =  stringResource(R.string.create),
                            ) {
                                showCreateRemoteDialog.value = true
                            }
                        }
                    }
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
                    match = { idx:Int, it: RemoteDto ->
                        it.remoteName.contains(keyword, ignoreCase = true)
                                || it.remoteUrl.contains(keyword, ignoreCase = true)
                                || it.pushUrl.contains(keyword, ignoreCase = true)
                                || it.credentialName?.contains(keyword, ignoreCase = true) == true
                                || it.pushCredentialName?.contains(keyword, ignoreCase = true) == true
                                || it.branchListForFetch.toString().contains(keyword, ignoreCase = true)
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
                ) {idx,it->
                    RemoteItem(showBottomSheet,curObjInState,idx,it, lastClickedItemKey){ 
                        val sb = StringBuilder()
                        sb.append(activityContext.getString(R.string.name)+": "+it.remoteName)
                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.url)+": "+it.remoteUrl)
                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.push_url)+": "+it.pushUrl)
                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.fetch_credential)+": "+it.getLinkedFetchCredentialName())
                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.push_credential)+": "+it.getLinkedPushCredentialName())
                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.branch_mode)+": "+(if(it.branchMode == Cons.dbRemote_Fetch_BranchMode_All) activityContext.getString(R.string.all) else activityContext.getString(R.string.custom)))
                        if(it.branchMode != Cons.dbRemote_Fetch_BranchMode_All) {
                            sb.appendLine()
                            sb.appendLine()
                            sb.append((if(it.branchListForFetch.size > 1) activityContext.getString(R.string.branches) else activityContext.getString(R.string.branch)) +": ${it.branchListForFetch}")
                        }
                        viewDialogText.value = sb.toString()
                        showViewDialog.value = true
                    }
                    MyHorizontalDivider()
                }
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        try {
            if(repoId.isNotBlank()) {
                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb == null) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.repo_id_invalid))
                        return@doJobThenOffLoading
                    }
                    curRepo.value = repoFromDb
                    val remoteDb = AppModel.dbContainer.remoteRepository
                    val listFromDb = remoteDb.getRemoteDtoListByRepoId(repoId)
                    list.value.clear()
                    list.value.addAll(listFromDb)
                    triggerReFilter(filterResultNeedRefresh)
                }
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "$TAG#LaunchedEffect() err: "+e.stackTraceToString())
        }
    }
}
