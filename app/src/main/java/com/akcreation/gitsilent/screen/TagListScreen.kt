package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.compose.AskGitUsernameAndEmailDialogWithSelection
import com.akcreation.gitsilent.compose.BottomBar
import com.akcreation.gitsilent.compose.CenterPaddingRow
import com.akcreation.gitsilent.compose.CheckoutDialog
import com.akcreation.gitsilent.compose.CheckoutDialogFrom
import com.akcreation.gitsilent.compose.CommitMsgMarkDownDialog
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CreateTagDialog
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
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SelectedItemDialog
import com.akcreation.gitsilent.compose.TagFetchPushDialog
import com.akcreation.gitsilent.compose.getDefaultCheckoutOption
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.git.TagDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.listitem.TagItem
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.screen.functions.fromTagToCommitHistory
import com.akcreation.gitsilent.screen.functions.triggerReFilter
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doActIfIndexGood
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.formatMinutesToUtc
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.time.TimeZoneUtil
import com.akcreation.gitsilent.utils.updateSelectedList
import com.github.git24j.core.Repository

private const val TAG = "TagListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TagListScreen(
    repoId:String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val shouldShowTimeZoneInfo = rememberSaveable { TimeZoneUtil.shouldShowTimeZoneInfo(settings) }
    val clipboardManager = LocalClipboardManager.current
    val inDarkTheme = Theme.inDarkTheme
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
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<TagDto>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<TagDto>())
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
        loadingText.value = activityContext.getString(R.string.loading)
        loading.value=false
    }
    val nameOfNewTag = rememberSaveable { mutableStateOf("")}
    val overwriteIfNameExistOfNewTag = rememberSaveable { mutableStateOf(false)}
    val showDialogOfNewTag = rememberSaveable { mutableStateOf(false)}
    val hashOfNewTag = rememberSaveable { mutableStateOf("HEAD")}  
    val msgOfNewTag = rememberSaveable { mutableStateOf("")}
    val annotateOfNewTag = rememberSaveable { mutableStateOf(false)}
    val initNewTagDialog = { hash:String ->
        doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
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
            force = overwriteIfNameExistOfNewTag
        ) {
            changeStateTriggerRefreshPage(needRefresh)
        }
    }
    val multiSelectionMode = rememberSaveable { mutableStateOf(false) }
    val selectedItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "selectedItemList", listOf<TagDto>())
    val getSelectedFilesCount = {
        selectedItemList.value.size
    }
    val quitSelectionMode = {
        multiSelectionMode.value=false  
        selectedItemList.value.clear()  
    }
    val switchItemSelected = { item: TagDto ->
        UIHelper.selectIfNotInSelectedListElseRemove(item, selectedItemList.value)
        multiSelectionMode.value = true
    }
    val selectItem = { item:TagDto ->
        multiSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseNoop(item, selectedItemList.value)
    }
    val isItemInSelected= { item:TagDto ->
        selectedItemList.value.contains(item)
    }
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val resetOid = rememberSaveable { mutableStateOf("") }
    val showResetDialog = rememberSaveable { mutableStateOf(false) }
    val closeResetDialog = {
        showResetDialog.value = false
    }
    val initResetDialog = { resetOidParam:String ->
        resetOid.value = resetOidParam
        showResetDialog.value = true
    }
    if (showResetDialog.value) {
        val item = selectedItemList.value.first()
        ResetDialog(
            fullOidOrBranchOrTag = resetOid,
            closeDialog=closeResetDialog,
            repoFullPath = curRepo.value.fullSavePath,
            repoId=repoId,
            refreshPage = { _, isDetached, _, ->
                if(isDetached) {
                    curRepo.value = curRepo.value.let {
                        it.copyAllFields(
                            settings,
                            it.copy(
                                isDetached = Cons.dbCommonTrue,
                                lastCommitHash = item.targetFullOidStr
                            ),
                        )
                    }
                }
            }
        )
    }
    val showCheckoutDialog = rememberSaveable { mutableStateOf(false) }
    val invalidCurItemIndex = -1  
    val initCheckoutDialogComposableVersion = {
        showCheckoutDialog.value = true
    }
    val branchNameForCheckout = rememberSaveable { mutableStateOf("") }
    val checkoutSelectedOption = rememberSaveable{ mutableIntStateOf(getDefaultCheckoutOption(false)) }
    if(showCheckoutDialog.value) {
        val item = selectedItemList.value.first()
        CheckoutDialog(
            checkoutSelectedOption = checkoutSelectedOption,
            showCheckoutDialog=showCheckoutDialog,
            branchName = branchNameForCheckout,
            from = CheckoutDialogFrom.OTHER,
            expectCheckoutType = Cons.checkoutType_checkoutRefThenDetachHead,  
            shortName = item.shortName,
            fullName = item.name,
            curRepo = curRepo.value,
            curCommitOid = item.targetFullOidStr,
            curCommitShortOid = Libgit2Helper.getShortOidStrByFull(item.targetFullOidStr),
            requireUserInputCommitHash = false,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            refreshPage = { _, _, _, _, ->
                doJobThenOffLoading job@{
                    curRepo.value = AppModel.dbContainer.repoRepository.getById(repoId) ?: return@job
                }
            },
        )
    }
    val showDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val detailsString = rememberSaveable { mutableStateOf("") }
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
    val filterModeOn = rememberSaveable { mutableStateOf(false) }
    val lastKeyword = rememberSaveable { mutableStateOf("") }
    val token = rememberSaveable { mutableStateOf("") }
    val searching = rememberSaveable { mutableStateOf(false) }
    val resetSearchVars = {
        searching.value = false
        token.value = ""
        lastKeyword.value = ""
    }
    val showTagFetchPushDialog = rememberSaveable { mutableStateOf(false) }
    val showForce = rememberSaveable { mutableStateOf( false) }
    val remoteList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "remoteList",
        listOf<String>()
    )
    val selectedRemoteList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "selectedRemoteList",
        listOf<String>()
    )
    val remoteCheckedList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "remoteCheckedList",
        listOf<Boolean>()
    )
    val fetchPushDialogTitle = rememberSaveable { mutableStateOf("") }
    val trueFetchFalsePush = rememberSaveable { mutableStateOf(true) }
    val requireDel = rememberSaveable { mutableStateOf(false) }
    val requireDelRemoteChecked = rememberSaveable { mutableStateOf(false) }
    val loadingTextForFetchPushDialog = rememberSaveable { mutableStateOf("") }
    if(showTagFetchPushDialog.value) {
        TagFetchPushDialog(
            title = fetchPushDialogTitle.value,
            remoteList = remoteList.value,
            selectedRemoteList = selectedRemoteList.value,
            remoteCheckedList = remoteCheckedList.value,
            enableOk = if(requireDel.value) true else selectedRemoteList.value.isNotEmpty(),   
            showForce = showForce.value,
            requireDel = requireDel.value,
            requireDelRemoteChecked = requireDelRemoteChecked,
            trueFetchFalsePush = trueFetchFalsePush.value,
            showTagFetchPushDialog=showTagFetchPushDialog,
            loadingOn=loadingOn,
            loadingOff=loadingOff,
            loadingTextForFetchPushDialog=loadingTextForFetchPushDialog,
            curRepo=curRepo.value,
            selectedTagsList=selectedItemList.value,
            allTagsList= list.value,
            onCancel = { showTagFetchPushDialog.value=false },
            onSuccess = {
                Msg.requireShow(activityContext.getString(R.string.success))
            },
            onErr = { e->
                val errMsgPrefix = "${fetchPushDialogTitle.value} err: "
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(curRepo.value.id, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#TagFetchPushDialog onOK error when '${fetchPushDialogTitle.value}': ${e.stackTraceToString()}")
            },
            onFinally = {
                changeStateTriggerRefreshPage(needRefresh)
            },
            pushFailedListHandler = { pushFailedList ->
                val prefix = "${pushFailedList.size} remotes are push failed"
                val toastMsg = StringBuilder("$prefix: ")
                val repoLogMsg = StringBuilder("$prefix:\n")
                val logMsg = StringBuilder("$prefix:\n")
                val suffix = ", "
                val spliter = "\n----------\n"
                pushFailedList.forEachBetter {
                    toastMsg.append(it.remoteName).append(suffix)
                    repoLogMsg.append("remoteName='${it.remoteName}', err=${it.exception?.localizedMessage}").append(spliter)
                    logMsg.append("remoteName='${it.remoteName}', err=${it.exception?.stackTraceToString()}").append(spliter)
                }
                Msg.requireShowLongDuration(toastMsg.removeSuffix(suffix).toString())
                createAndInsertError(curRepo.value.id, repoLogMsg.removeSuffix(spliter).toString()+"\n\n")
                MyLog.e(TAG, "#TagFetchPushDialog: ${logMsg.removeSuffix(spliter)}\n\n")
            }
        )
    }
    val initDelTagDialog = {
        requireDel.value = true
        requireDelRemoteChecked.value = false  
        trueFetchFalsePush.value = false
        fetchPushDialogTitle.value = activityContext.getString(R.string.delete_tags)
        showForce.value = false
        loadingTextForFetchPushDialog.value = activityContext.getString(R.string.deleting)
        showTagFetchPushDialog.value = true
    }
    val initPushTagDialog= {
        requireDel.value = false
        trueFetchFalsePush.value = false
        fetchPushDialogTitle.value = activityContext.getString(R.string.push_tags)
        showForce.value = true
        loadingTextForFetchPushDialog.value = activityContext.getString(R.string.pushing)
        showTagFetchPushDialog.value = true
    }
    val initFetchTagDialog = {
        requireDel.value = false
        trueFetchFalsePush.value = true
        fetchPushDialogTitle.value = activityContext.getString(R.string.fetch_tags)
        showForce.value = true
        loadingTextForFetchPushDialog.value = activityContext.getString(R.string.fetching)
        showTagFetchPushDialog.value = true
    }
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val filterListState = rememberLazyListState()
    val enableFilterState = rememberSaveable { mutableStateOf(false) }
    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false) }
    if(showSelectedItemsShortDetailsDialog.value) {
        SelectedItemDialog(
            selectedItems = selectedItemList.value,
            formatter = {it.shortName},
            switchItemSelected = switchItemSelected,
            clearAll = {selectedItemList.value.clear()},
            closeDialog = {showSelectedItemsShortDetailsDialog.value = false}
        )
    }
    val countNumOnClickForBottomBar = {
        showSelectedItemsShortDetailsDialog.value = true
    }
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    val showDetails = { selectedItemList:List<TagDto> ->
        val sb = StringBuilder()
        val itemSuffix = "\n\n"
        val spliter = "--------------\n\n"
        selectedItemList.forEachBetter {
            sb.append(activityContext.getString(R.string.name)).append(": ").append(it.shortName).append(itemSuffix)
            sb.append(activityContext.getString(R.string.full_name)).append(": ").append(it.name).append(itemSuffix)
            sb.append(activityContext.getString(R.string.target)).append(": ").append(it.targetFullOidStr).append(itemSuffix)
            sb.append(activityContext.getString(R.string.type)).append(": ").append(it.getType(activityContext, false)).append(itemSuffix)
            sb.append(Cons.flagStr).append(": ").append(it.getType(activityContext, true)).append(itemSuffix)
            if(it.isAnnotated) {
                sb.append(activityContext.getString(R.string.tag_oid)).append(": ").append(it.fullOidStr).append(itemSuffix)
                sb.append(activityContext.getString(R.string.author)).append(": ").append(it.getFormattedTaggerNameAndEmail()).append(itemSuffix)
                sb.append(activityContext.getString(R.string.date)).append(": ").append(it.getFormattedDate()+" (${it.getActuallyUsingTimeOffsetInUtcFormat()})").append(itemSuffix)
                sb.append(activityContext.getString(R.string.timezone)).append(": ").append(formatMinutesToUtc(it.originTimeOffsetInMinutes)).append(itemSuffix)
                sb.append(activityContext.getString(R.string.msg)).append(": ").append(it.msg).append(itemSuffix)
            }
            sb.append(spliter)
        }
        detailsString.value = sb.removeSuffix(itemSuffix+spliter).toString()
        showDetailsDialog.value = true
    }
    BackHandler {
        if(multiSelectionMode.value) {
            quitSelectionMode()
        } else if(filterModeOn.value) {
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
    val showItemMsgDialog = rememberSaveable { mutableStateOf(false) }
    val textOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val previewModeOnOfItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgPreviewModeOn) }
    val useSystemFontsForItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgUseSystemFonts) }
    val showItemMsg = { msg: String ->
        textOfItemMsgDialog.value = msg
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
                    if(filterModeOn.value) {
                        FilterTextField(filterKeyWord = filterKeyword, loading = searching.value)
                    }else {
                        val repoAndBranch = Libgit2Helper.getRepoOnBranchOrOnDetachedHash(curRepo.value)
                        Column (modifier = Modifier.combinedClickable (
                            onDoubleClick = {
                                defaultTitleDoubleClick(scope, listState, lastPosition)
                            },
                        ){  
                            showTitleInfoDialog.value = true
                        }){
                            ScrollableRow  {
                                Text(
                                    text= stringResource(R.string.tags),
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
                            tooltipText = stringResource(R.string.fetch_tags),
                            icon =  Icons.Filled.Downloading,
                            iconContentDesc = stringResource(R.string.fetch_tags),
                        ) {
                            initFetchTagDialog()
                        }
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.create_tag),
                            icon =  Icons.Filled.Add,
                            iconContentDesc = stringResource(R.string.create_tag),
                        ) {
                            val hash = ""
                            initNewTagDialog(hash)
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
            if(list.value.isEmpty()) {  
                FullScreenScrollableColumn(contentPadding) {
                    if(isInitLoading.value) {
                        Text(text = stringResource(R.string.loading))
                    }else {
                        Row {
                            Text(text = stringResource(R.string.no_tags_found))
                        }
                        CenterPaddingRow {
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Downloading,
                                tooltipText = stringResource(R.string.fetch),
                            ) {
                                initFetchTagDialog()
                            }
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Add,
                                tooltipText = stringResource(R.string.create),
                            ) {
                                val hash = ""
                                initNewTagDialog(hash)
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
                    match = { idx:Int, it: TagDto ->
                        it.shortName.contains(keyword, ignoreCase = true)
                                || it.name.contains(keyword, ignoreCase = true)
                                || it.msg.contains(keyword, ignoreCase = true)
                                || it.targetFullOidStr.contains(keyword, ignoreCase = true)
                                || it.taggerName.contains(keyword, ignoreCase = true)
                                || it.taggerEmail.contains(keyword, ignoreCase = true)
                                || it.fullOidStr.contains(keyword, ignoreCase = true)
                                || it.pointedCommitDto.let { commit ->
                                    if(commit == null) {
                                        false
                                    }else {
                                        commit.msg.contains(keyword, ignoreCase = true)
                                                || commit.getFormattedAuthorInfo().contains(keyword, ignoreCase = true)
                                                || commit.dateTime.contains(keyword, ignoreCase = true)
                                    }
                                }
                                || it.getFormattedDate().contains(keyword, ignoreCase = true)
                                || it.getFormattedTaggerNameAndEmail().contains(keyword, ignoreCase = true)
                                || it.getType(activityContext, false).contains(keyword, ignoreCase = true)
                                || it.getType(activityContext, true).contains(keyword, ignoreCase = true)
                                || it.getOriginTimeOffsetFormatted().contains(keyword, ignoreCase = true)
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
                    TagItem(
                        thisObj = it,
                        lastClickedItemKey = lastClickedItemKey,
                        shouldShowTimeZoneInfo = shouldShowTimeZoneInfo,
                        showItemMsg = showItemMsg,
                        isItemInSelected = isItemInSelected,
                        onLongClick = {
                            if(multiSelectionMode.value) {  
                                UIHelper.doSelectSpan(idx, it,
                                    selectedItemList.value, list,
                                    switchItemSelected,
                                    selectItem
                                )
                            }else {  
                                switchItemSelected(it)
                            }
                        }
                    ) {  
                        if(multiSelectionMode.value) {  
                            UIHelper.selectIfNotInSelectedListElseRemove(it, selectedItemList.value)
                        }else {  
                            fromTagToCommitHistory(
                                fullOid = it.targetFullOidStr,
                                shortName = it.shortName,
                                repoId = repoId
                            )
                        }
                    }
                    MyHorizontalDivider()
                }
                if (multiSelectionMode.value) {
                    val iconList:List<ImageVector> = listOf(
                        Icons.Filled.Delete,  
                        Icons.Filled.Upload,  
                        Icons.Filled.Info,  
                        Icons.Filled.SelectAll,  
                    )
                    val iconTextList:List<String> = listOf(
                        stringResource(id = R.string.delete),
                        stringResource(id = R.string.push),
                        stringResource(id = R.string.details),
                        stringResource(id = R.string.select_all),
                    )
                    val iconEnableList:List<()->Boolean> = listOf(
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.isNotEmpty()},  
                        {selectedItemList.value.isNotEmpty()},  
                        {true} 
                    )
                    val moreItemTextList = (listOf(
                        stringResource(R.string.checkout),
                        stringResource(R.string.reset),  
                    ))
                    val moreItemEnableList:List<()->Boolean> = (listOf(
                        {selectedItemList.value.size==1},  
                        {selectedItemList.value.size==1},  
                        {selectedItemList.value.isNotEmpty()}  
                    ))
                    val iconOnClickList:List<()->Unit> = listOf(  
                        delete@{
                            initDelTagDialog()
                        },
                        push@{
                            initPushTagDialog()
                        },
                        details@{
                            showDetails(selectedItemList.value)
                        },
                        selectAll@{
                            list.forEachBetter {
                                selectItem(it)
                            }
                            Unit
                        },
                    )
                    val moreItemOnClickList:List<()->Unit> = (listOf(
                        checkout@{
                            initCheckoutDialogComposableVersion()
                        },
                        hardReset@{
                            doActIfIndexGood(0, selectedItemList.value) { item ->
                                initResetDialog(item.targetFullOidStr)
                            }
                            Unit
                        },
                        ))
                    BottomBar(
                        quitSelectionMode=quitSelectionMode,
                        iconList=iconList,
                        iconTextList=iconTextList,
                        iconDescTextList=iconTextList,
                        iconOnClickList=iconOnClickList,
                        iconEnableList=iconEnableList,
                        moreItemTextList=moreItemTextList,
                        moreItemOnClickList=moreItemOnClickList,
                        moreItemEnableList = moreItemEnableList,
                        getSelectedFilesCount = getSelectedFilesCount,
                        countNumOnClickEnabled = true,
                        countNumOnClick = countNumOnClickForBottomBar,
                        reverseMoreItemList = true
                    )
                }
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        try {
            val refreshId = needRefresh.value
            val pageChanged = {
                refreshId != needRefresh.value
            }
            doJobThenOffLoading(initLoadingOn, initLoadingOff) {
                list.value.clear()  
                if(!repoId.isNullOrBlank()) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb!=null) {
                        curRepo.value = repoFromDb
                        Repository.open(repoFromDb.fullSavePath).use {repo ->
                            val tags = Libgit2Helper.getAllTags(repoId, repo, settings)
                                .sortedByDescending {
                                    it.pointedCommitDto?.originTimeInSecs ?: 0L
                                }
                            list.value.clear()
                            list.value.addAll(tags)
                            val pageChangedNeedAbort = updateSelectedList(
                                selectedItemList = selectedItemList.value,
                                itemList = list.value,
                                quitSelectionMode = quitSelectionMode,
                                match = { oldSelected, item-> oldSelected.name == item.name },
                                pageChanged = pageChanged
                            )
                            if (pageChangedNeedAbort) return@doJobThenOffLoading
                            val remotes = Libgit2Helper.getRemoteList(repo)
                            selectedRemoteList.value.clear()
                            remoteCheckedList.value.clear()
                            remoteList.value.clear()
                            remotes.forEachBetter { remoteCheckedList.value.add(false) }  
                            remoteList.value.addAll(remotes)
                        }
                    }
                }
                triggerReFilter(filterResultNeedRefresh)
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect() err: "+e.stackTraceToString())
        }
    }
}
