package com.akcreation.gitsilent.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.compose.BarContainer
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialogAndDisableSelection
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.CreatePatchSuccessDialog
import com.akcreation.gitsilent.compose.DiffRow
import com.akcreation.gitsilent.compose.FileHistoryRestoreDialog
import com.akcreation.gitsilent.compose.FontSizeAdjuster
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.InLineIcon
import com.akcreation.gitsilent.compose.LoadingDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.MySelectionContainerPlaceHolder
import com.akcreation.gitsilent.compose.OneTimeFocusRightNow
import com.akcreation.gitsilent.compose.OpenAsAskReloadDialog
import com.akcreation.gitsilent.compose.OpenAsDialog
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.ReadOnlyIcon
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SelectSyntaxHighlightingDialog
import com.akcreation.gitsilent.compose.SelectionRow
import com.akcreation.gitsilent.compose.SingleLineCardButton
import com.akcreation.gitsilent.compose.SoftkeyboardVisibleListener
import com.akcreation.gitsilent.compose.TwoLineTextCardButton
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.LineNum
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.FlagFileName
import com.akcreation.gitsilent.dev.detailsDiffTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dto.MenuIconBtnItem
import com.akcreation.gitsilent.dto.MenuTextItem
import com.akcreation.gitsilent.git.CompareLinePair
import com.akcreation.gitsilent.git.DiffItemSaver
import com.akcreation.gitsilent.git.DiffableItem
import com.akcreation.gitsilent.git.PuppyHunkAndLines
import com.akcreation.gitsilent.git.PuppyLine
import com.akcreation.gitsilent.git.PuppyLineOriginType
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.msg.OneTimeToast
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.DiffPageActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.DiffScreenTitle
import com.akcreation.gitsilent.screen.functions.ChangeListFunctions
import com.akcreation.gitsilent.screen.functions.openFileWithInnerSubPageEditor
import com.akcreation.gitsilent.screen.shared.DiffFromScreen
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsCons
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.syntaxhighlight.base.PLFont
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StateRequestType
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.cache.NaviCache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.compare.result.IndexStringPart
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.doJobWithMainContext
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import com.akcreation.gitsilent.utils.getFormattedLastModifiedTimeOfFile
import com.akcreation.gitsilent.utils.getHumanReadableSizeStr
import com.akcreation.gitsilent.utils.getRequestDataByState
import com.akcreation.gitsilent.utils.isGoodIndex
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.mutableCustomBoxOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.github.git24j.core.Diff
import com.github.git24j.core.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

private const val TAG = "DiffScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffScreen(
    repoId: String,
    fromTo:String,
    treeOid1Str:String,
    treeOid2Str:String,
    isDiffToLocal:Boolean,  
    curItemIndexAtDiffableItemList:Int,
    localAtDiffRight:Boolean,
    fromScreen: DiffFromScreen, 
    diffableListCacheKey:String,
    isMultiMode:Boolean,
    naviUp: () -> Unit,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val isSingleMode = isMultiMode.not();
    val inDarkTheme = remember(Theme.inDarkTheme) { Theme.inDarkTheme }
    val dbContainer = AppModel.dbContainer
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val configuration = AppModel.getCurActivityConfig()
    val activityContext = LocalContext.current
    val navController = AppModel.navController
    val clipboardManager = LocalClipboardManager.current
    val showEditLineDialog = rememberSaveable { mutableStateOf(false) }
    val showRestoreLineDialog = rememberSaveable { mutableStateOf(false) }
    val view = LocalView.current
    val density = LocalDensity.current
    val isKeyboardVisible = remember { mutableStateOf(false) }
    val isKeyboardCoveredComponent = remember { mutableStateOf(false) }
    val componentHeight = remember { mutableIntStateOf(0) }
    val keyboardPaddingDp = remember { mutableIntStateOf(0) }
    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        skipCondition = {
            !(showEditLineDialog.value || showRestoreLineDialog.value)
        }
    )
    val isFileHistoryTreeToLocal = fromTo == Cons.gitDiffFileHistoryFromTreeToLocal
    val isFileHistoryTreeToPrev = fromTo == Cons.gitDiffFileHistoryFromTreeToPrev
    val isFileHistory = isFileHistoryTreeToLocal || isFileHistoryTreeToPrev;
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val lastClickedItemKey = rememberSaveable {
        if(fromScreen == DiffFromScreen.HOME_CHANGELIST) {
            SharedState.homeChangeList_LastClickedItemKey
        }else if(fromScreen == DiffFromScreen.INDEX) {
            SharedState.index_LastClickedItemKey
        }else if(fromScreen == DiffFromScreen.TREE_TO_TREE) {
            SharedState.treeToTree_LastClickedItemKey
        }else { 
            SharedState.fileHistory_LastClickedItemKey
        }
    }
    val treeOid1Str = rememberSaveable { mutableStateOf(
        if(fromTo == Cons.gitDiffFromIndexToWorktree) {
            Cons.git_IndexCommitHash
        }else if(fromTo == Cons.gitDiffFromHeadToIndex) {
            Cons.git_HeadCommitHash
        }else{
            treeOid1Str
        }
    ) }
    val treeOid2Str = rememberSaveable { mutableStateOf(
        if(fromTo == Cons.gitDiffFromIndexToWorktree) {
            Cons.git_LocalWorktreeCommitHash
        }else if(fromTo == Cons.gitDiffFromHeadToIndex) {
            Cons.git_IndexCommitHash
        }else {
            treeOid2Str
        }
    ) }
    val tree1FullHash = rememberSaveable { mutableStateOf("") }
    val tree2FullHash = rememberSaveable { mutableStateOf("") }
    val needRefresh = rememberSaveable { mutableStateOf("DiffScreen_refresh_init_value_4kc9") }
    val listState = rememberLazyListState()
    val goToTop = {
        UIHelper.scrollToItem(scope, listState, 0)
    }
    val diffableListLock = mutableCustomBoxOf<Mutex>(stateKeyTag, "diffableListLock", Mutex()).value
    val diffableItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "diffableItemList") {
        NaviCache.getByType<List<DiffableItem>>(diffableListCacheKey) ?: listOf()
    }
    val subDiffableItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "subDiffableItemList") {
        listOf<Int>(curItemIndexAtDiffableItemList)
    }
    val firstTimeLoad = rememberSaveable { mutableStateOf(true) }
    val requireRefreshSubList = { itemIndices:List<Int> ->
        if(itemIndices.isNotEmpty()) {
            subDiffableItemList.value.clear()
            subDiffableItemList.value.addAll(itemIndices)
            changeStateTriggerRefreshPage(needRefresh)
        }
    }
    val titleFileNameFontSize = remember { MyStyleKt.Title.firstLineFontSizeSmall }
    val titleRelativePathFontSize = remember { MyStyleKt.Title.secondLineFontSize }
    val fileTitleFileNameWidthLimit = remember(configuration.screenWidthDp) {(configuration.screenWidthDp / 2).dp}
    val curItemIndex = rememberSaveable { mutableIntStateOf(curItemIndexAtDiffableItemList) }
    val curItemIndexAtDiffableItemList = Unit  
    val getTargetIdxOfLazyColumnByRelativePath = { relativePath:String ->
        diffableItemList.value.toList().let {
            var count = 0
            for((idx, item) in it.withIndex()) {
                if(relativePath == item.relativePath) {
                    curItemIndex.intValue = idx
                    break
                }
                count += if(item.visible) {
                    val fileHeaderAndFooterAndSpacer = 3
                    val hunksHeadersAndSpliters = item.diffItemSaver.hunks.size * 2 - 1;
                    (hunksHeadersAndSpliters + item.diffItemSaver.allLines + fileHeaderAndFooterAndSpacer) + (if(item.noDiffItemAvailable || item.submoduleIsDirty) 1 else 0)
                }else {
                    1
                }
            }
            count
        }
    }
    val scrollToCurrentItemHeader = { relativePath:String ->
        val targetIdx = getTargetIdxOfLazyColumnByRelativePath(relativePath)
        UIHelper.scrollToItem(scope, listState, targetIdx)
    }
    val naviUp = {
        diffableItemList.value.getOrNull(curItemIndex.intValue)?.let {
            lastClickedItemKey.value = it.getItemKey()
        }
        if(fromScreen == DiffFromScreen.HOME_CHANGELIST) {
            Cache.set(Cache.Key.diffableList_of_fromDiffScreenBackToWorkTreeChangeList, diffableItemList.value.map {it.toChangeListItem()})
        }
        doJobWithMainContext {
            naviUp()
        }
    }
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity())
    val showMyFileHeader = isMultiMode
    val openFileWithInnerSubPageEditor = { filePath:String ->
        openFileWithInnerSubPageEditor(
            context = activityContext,
            filePath = filePath,
            mergeMode = false,
            readOnly = false,
            onlyGoToWhenFileExists = true,
        )
    }
    val enableSelectCompare = rememberSaveable(settings.diff.enableSelectCompare) { mutableStateOf(settings.diff.enableSelectCompare) }
    val readOnlySwitchable = remember(localAtDiffRight) { derivedStateOf {
        localAtDiffRight
    }}
    val readOnlyModeOn = rememberSaveable(settings.diff.readOnly, readOnlySwitchable.value) { mutableStateOf(settings.diff.readOnly || readOnlySwitchable.value.not()) }
    val removeTargetFromChangeList = {targetItems: List<StatusTypeEntrySaver> ->
        SharedState.homeChangeList_itemList.removeIf {
            targetItems.any {it2 -> it.relativePathUnderRepo == it2.relativePathUnderRepo}
        }
    }
    val handleChangeListPageStuffs:suspend (targetItems:List<StatusTypeEntrySaver> , hasIndex:Boolean?) ->Unit = { targetItems, hasIndex ->
        diffableListLock.withLock {
            val noItems = if(targetItems.size == diffableItemList.value.size) {
                diffableItemList.value.clear()
                true
            }else {
                val listIsEmpty = diffableItemList.value.let { list ->
                    list.removeIf { targetItems.any { it2 -> it.relativePath == it2.relativePathUnderRepo } }
                    list.isEmpty()
                }
                if(listIsEmpty.not()) {
                    diffableItemList.value.toList().forEachIndexedBetter { idx, item ->
                        diffableItemList.value[idx] = item.copy()
                    }
                }
                listIsEmpty
            }
            removeTargetFromChangeList(targetItems)
            hasIndex?.let {
                SharedState.homeChangeList_indexHasItem.value = hasIndex
            }
            if(noItems) {
                naviUp()
            }
        }
    }
    val stageItem:suspend (List<StatusTypeEntrySaver>)->Unit= { targetItems ->
        val curRepo = curRepo.value
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                Libgit2Helper.stageStatusEntryAndWriteToDisk(repo, targetItems)
            }
            Msg.requireShow(activityContext.getString(R.string.success))
            handleChangeListPageStuffs(targetItems, true)
        }catch (e:Exception) {
            val errMsg = "err: ${e.localizedMessage}"
            Msg.requireShowLongDuration(errMsg)
            createAndInsertError(curRepo.id, errMsg)
            MyLog.e(TAG, "stage items for repo '${curRepo.repoName}' err: ${e.stackTraceToString()}")
        }
    }
    val revertItem:suspend (List<StatusTypeEntrySaver>)->Unit = { targetItems ->
        val curRepo = curRepo.value
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                val untrakcedFileList = mutableListOf<String>()  
                val pathspecList = mutableListOf<String>()  
                targetItems.forEachBetter { targetItem->
                    if(targetItem.changeType == Cons.gitStatusNew) {
                        untrakcedFileList.add(targetItem.canonicalPath)  
                    }else if(targetItem.changeType != Cons.gitStatusConflict){  
                        pathspecList.add(targetItem.relativePathUnderRepo)
                    }
                }
                if(pathspecList.isNotEmpty()) {
                    Libgit2Helper.revertFilesToIndexVersion(repo, pathspecList)
                }
                if(untrakcedFileList.isNotEmpty()) {
                    Libgit2Helper.rmUntrackedFiles(untrakcedFileList)
                }
            }
            Msg.requireShow(activityContext.getString(R.string.success))
            handleChangeListPageStuffs(targetItems, null)
        }catch (e:Exception) {
            val errMsg = "err: ${e.localizedMessage}"
            Msg.requireShowLongDuration(errMsg)
            createAndInsertError(curRepo.id, errMsg)
            MyLog.e(TAG, "revert items for repo '${curRepo.repoName}' err: ${e.stackTraceToString()}")
        }
    }
    val unstageItem:suspend (List<StatusTypeEntrySaver>)->Unit = { targetItems ->
        val curRepo = curRepo.value
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                Libgit2Helper.unStageItems(repo, targetItems.map { it.relativePathUnderRepo })
            }
            Msg.requireShow(activityContext.getString(R.string.success))
            handleChangeListPageStuffs(targetItems, null)
        }catch (e:Exception) {
            val errMsg = "err: ${e.localizedMessage}"
            Msg.requireShowLongDuration(errMsg)
            createAndInsertError(curRepo.id, errMsg)
            MyLog.e(TAG, "unstage items for repo '${curRepo.repoName}' err: ${e.stackTraceToString()}")
        }
    }
    val showRevertDialog = rememberSaveable { mutableStateOf(false) }
    val showUnstageDialog = rememberSaveable { mutableStateOf(false) }
    val revertDialogList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName="revertDialogList") { listOf<StatusTypeEntrySaver>() }
    val unstageDialogList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName="unstageDialogList") { listOf<StatusTypeEntrySaver>() }
    val revertCallback = remember { mutableStateOf<suspend ()->Unit>({}) }
    val unstageCallback = remember { mutableStateOf<suspend ()->Unit>({}) }
    val initRevertDialog = { items:List<StatusTypeEntrySaver>, callback:suspend ()->Unit ->
        revertDialogList.value.apply {
            clear()
            addAll(items)
        }
        revertCallback.value = callback
        showRevertDialog.value = true
    }
    val initUnstageDialog = { items:List<StatusTypeEntrySaver>, callback:suspend ()->Unit ->
        unstageDialogList.value.apply {
            clear()
            addAll(items)
        }
        unstageCallback.value = callback
        showUnstageDialog.value = true
    }
    if(showRevertDialog.value) {
        ConfirmDialog(
            title=stringResource(R.string.revert),
            text=stringResource(R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showRevertDialog.value=false}
        ) {  
            showRevertDialog.value=false
            doJobThenOffLoading {
                revertItem(revertDialogList.value.toList())
                revertCallback.value()
            }
        }
    }
    if(showUnstageDialog.value) {
        ConfirmDialog(
            title=stringResource(R.string.unstage),
            text=stringResource(R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showUnstageDialog.value=false}
        ) {  
            showUnstageDialog.value=false
            doJobThenOffLoading {
                unstageItem(unstageDialogList.value.toList())
                unstageCallback.value()
            }
        }
    }
    val requireBetterMatchingForCompare = rememberSaveable { mutableStateOf(settings.diff.enableBetterButSlowCompare) }
    val matchByWords = rememberSaveable { mutableStateOf(settings.diff.matchByWords) }
    val adjustFontSizeModeOn = rememberSaveable { mutableStateOf(false) }
    val adjustLineNumSizeModeOn = rememberSaveable { mutableStateOf(false) }
    val showLineNum = rememberSaveable { mutableStateOf(settings.diff.showLineNum) }
    val showOriginType = rememberSaveable { mutableStateOf(settings.diff.showOriginType) }
    val fontSize = rememberSaveable { mutableIntStateOf(settings.diff.fontSize) }
    val lineNumFontSize = rememberSaveable { mutableIntStateOf(settings.diff.lineNumFontSize) }
    val groupDiffContentByLineNum = rememberSaveable { mutableStateOf(settings.diff.groupDiffContentByLineNum) }
    val loadingForAction= rememberSaveable { mutableStateOf(false)}
    val loadingText = rememberSaveable { mutableStateOf("")}
    val loadingOn = { text:String ->
        loadingText.value = text
        loadingForAction.value = true
    }
    val loadingOff = {
        loadingForAction.value = false
        loadingText.value = ""
    }
    val pageRequest = rememberSaveable { mutableStateOf("") }
    val refreshPage = { changeStateTriggerRefreshPage(needRefresh) }
    val getCurItem = {
        diffableItemList.value.getOrNull(curItemIndex.intValue) ?: DiffableItem.anInvalidInstance()
    }
    val showBackFromExternalAppAskReloadDialog = rememberSaveable { mutableStateOf(false)}
    val backFromExternalApp_ItemIdx = rememberSaveable { mutableIntStateOf(0) }
    val initBackFromExtAppDialog = { itemIdx:Int ->
        backFromExternalApp_ItemIdx.intValue = itemIdx
        showBackFromExternalAppAskReloadDialog.value = true
    }
    if(showBackFromExternalAppAskReloadDialog.value) {
        OpenAsAskReloadDialog(
            onCancel = { showBackFromExternalAppAskReloadDialog.value=false }
        ) { 
            showBackFromExternalAppAskReloadDialog.value=false
            val targetIdx = backFromExternalApp_ItemIdx.intValue
            val target = diffableItemList.value.getOrNull(targetIdx)
            if(target != null) {
                requireRefreshSubList(listOf(targetIdx))
            }else {
                Msg.requireShowLongDuration("reload err: index invalid")
            }
        }
    }
    val savePatchPath= rememberSaveable { mutableStateOf("") }
    val showSavePatchSuccessDialog = rememberSaveable { mutableStateOf(false)}
    if(showSavePatchSuccessDialog.value) {
        CreatePatchSuccessDialog(
            path = savePatchPath.value,
            closeDialog = {showSavePatchSuccessDialog.value = false}
        )
    }
    val showCreatePatchDialog = rememberSaveable { mutableStateOf(false)}
    val createPatchList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "createPatchList") { listOf<String>() }
    val initCreatePatchDialog = { relativePaths:List<String> ->
        createPatchList.value.clear()
        createPatchList.value.addAll(relativePaths)
        showCreatePatchDialog.value=true
    }
    if(showCreatePatchDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.create_patch),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(text = stringResource(R.string.are_you_sure))
                    }
                }
            },
            onCancel = { showCreatePatchDialog.value = false }
        ){
            showCreatePatchDialog.value = false
            val curRepo = curRepo.value
            val leftCommit = treeOid1Str.value
            val rightCommit = treeOid2Str.value
            val createPatchList = createPatchList.value.toList()
            val fromTo = if(leftCommit == Cons.git_IndexCommitHash && rightCommit == Cons.git_LocalWorktreeCommitHash) Cons.gitDiffFromIndexToWorktree else fromTo;
            doJobThenOffLoading(loadingOn,loadingOff, activityContext.getString(R.string.creating_patch)) job@{
                try {
                    val savePatchRet = ChangeListFunctions.createPath(
                        curRepo = curRepo,
                        leftCommit = leftCommit,
                        rightCommit = rightCommit,
                        fromTo = fromTo,
                        relativePaths = createPatchList
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
    val showOpenAsDialog = rememberSaveable { mutableStateOf(false)}
    val readOnlyForOpenAsDialog = rememberSaveable { mutableStateOf(false)}
    val openAsDialogItem = mutableCustomStateOf(stateKeyTag, "openAsDialogItem") { DiffableItem() }
    val openAsDialogItemIdx = rememberSaveable { mutableIntStateOf(0) }
    val initOpenAsDialog = { idx:Int ->
        try {
            val target = diffableItemList.value.getOrNull(idx)
            if(target == null) {
                throw RuntimeException("index invalid")
            }else {
               val fileFullPath = target.fullPath
                if(!File(fileFullPath).exists()) {
                    throw RuntimeException(activityContext.getString(R.string.file_doesnt_exist))
                }else { 
                    openAsDialogItem.value = target
                    openAsDialogItemIdx.intValue = idx
                    showOpenAsDialog.value=true
                }
            }
        }catch (e:Exception) {
            val errPrefix = "'Open As' err: "
            Msg.requireShowLongDuration(errPrefix+e.localizedMessage)
            MyLog.e(TAG, errPrefix+e.stackTraceToString())
        }
    }
    if(showOpenAsDialog.value) {
        val openAsDialogItem = openAsDialogItem.value
        OpenAsDialog(readOnly=readOnlyForOpenAsDialog,fileName=openAsDialogItem.fileName, filePath = openAsDialogItem.fullPath,
            openSuccessCallback = {
                if(isDiffToLocal) {
                    initBackFromExtAppDialog(openAsDialogItemIdx.intValue)
                }
            }
        ) {
            showOpenAsDialog.value=false
        }
    }
    val detailsString = rememberSaveable { mutableStateOf("")}
    val showDetailsDialog = rememberSaveable { mutableStateOf(false)}
    if(showDetailsDialog.value){
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
    val initDetailsDialog = { itemIdx:Int ->
        val curItem = diffableItemList.value.getOrNull(itemIdx)
        val suffix = "\n\n"
        val sb = StringBuilder()
        sb.append(activityContext.getString(R.string.comparing_label)+": ").append(Libgit2Helper.getLeftToRightDiffCommitsText(treeOid1Str.value, treeOid2Str.value, false)).append(suffix)
        sb.append(activityContext.getString(R.string.left)+": ").append(tree1FullHash.value).append(suffix)
        sb.append(activityContext.getString(R.string.right)+": ").append(tree2FullHash.value).append(suffix)
        sb.append(replaceStringResList(activityContext.getString(R.string.current_n_all_m), listOf((itemIdx+1).toString(), diffableItemList.value.size.toString()))).append(suffix)
        if(curItem != null) {
            sb.append(activityContext.getString(R.string.name)+": ").append(curItem.fileName).append(suffix)
            if(isFileHistoryTreeToLocal || isFileHistoryTreeToPrev) {
                val commitId = if(isFileHistoryTreeToLocal) treeOid1Str.value else treeOid2Str.value
                sb.append(activityContext.getString(R.string.commit_id)+": ").append(commitId).append(suffix)
                sb.append(activityContext.getString(R.string.entry_id)+": ").append(curItem.entryId).append(suffix)
            }else {  
                sb.append(activityContext.getString(R.string.change_type)+": ").append(curItem.diffItemSaver.changeType).append(suffix)
            }
            sb.append(activityContext.getString(R.string.path)+": ").append(curItem.relativePath).append(suffix)
            val fileFullPath = curItem.fullPath
            sb.append(activityContext.getString(R.string.full_path)+": ").append(fileFullPath).append(suffix)
            val file = File(fileFullPath)
            if(file.exists()) {
                if(file.isFile) {
                    sb.append(activityContext.getString(R.string.size)+": ").append(getHumanReadableSizeStr(file.length())).append(suffix)
                }
                sb.append(activityContext.getString(R.string.last_modified)+": ").append(getFormattedLastModifiedTimeOfFile(file)).append(suffix)
            }
        }
        detailsString.value = sb.removeSuffix(suffix).toString()
        showDetailsDialog.value = true
    }
    val showRestoreDialog = rememberSaveable { mutableStateOf(false) }
    val oidForRestoreDialog = rememberSaveable { mutableStateOf("") }
    val msgForRestoreDialog = rememberSaveable { mutableStateOf("") }
    val initRestoreDialog = {
        val targetCommitOid = if(isFileHistoryTreeToLocal){
            treeOid1Str.value
        }else { 
            treeOid2Str.value
        }
        oidForRestoreDialog.value = targetCommitOid
        msgForRestoreDialog.value = try {
            Repository.open(curRepo.value.fullSavePath).use { repo ->
                Libgit2Helper.getCommitMsgOneLine(repo, targetCommitOid)
            }
        }catch (e: Exception) {
            MyLog.d(TAG, "initRestoreDialog err: ${e.stackTraceToString()}")
            ""
        }
        showRestoreDialog.value = true
    }
    if(showRestoreDialog.value) {
        FileHistoryRestoreDialog(
            targetCommitOidStr = oidForRestoreDialog.value,
            commitMsg = msgForRestoreDialog.value,
            showRestoreDialog = showRestoreDialog,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            activityContext = activityContext,
            curRepo = curRepo,
            fileRelativePath = getCurItem().relativePath,
            repoId = repoId,
            onSuccess = {
                if(isFileHistoryTreeToLocal) {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        )
    }
    if(pageRequest.value == PageRequest.createPatchForAllItems) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initCreatePatchDialog(diffableItemList.value.map { it.relativePath })
        }
    }
    if(pageRequest.value == PageRequest.goToBottomOfCurrentFile) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            val curItemRelativePath = getCurItem().relativePath
            val diffableItemList = diffableItemList.value
            val indexOfCurItem = diffableItemList.indexOfFirst { it.relativePath ==  curItemRelativePath }
            if(indexOfCurItem >= 0) {
                val nextItem = diffableItemList.getOrNull(indexOfCurItem + 1)
                val targetIdx = if(nextItem == null) {  
                    Int.MAX_VALUE
                }else {
                    getTargetIdxOfLazyColumnByRelativePath(nextItem.relativePath) - 1
                }
                UIHelper.scrollToItem(scope, listState, targetIdx)
            }
        }
    }
    if(pageRequest.value == PageRequest.showOpenAsDialog) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initOpenAsDialog(curItemIndex.intValue)
        }
    }
    if(pageRequest.value == PageRequest.requireOpenInInnerEditor) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            openFileWithInnerSubPageEditor(getCurItem().fullPath)
        }
    }
    val showOrHideAll = { show:Boolean ->
        if(show) {  
            val hideAndNeverLoadedList = mutableListOf<Int>()
            diffableItemList.value.toList().forEachIndexedBetter { idx, it ->
                if(it.visible.not()) {
                    if(it.neverLoadedDifferences()) {
                        hideAndNeverLoadedList.add(idx)
                    }else {  
                        diffableItemList.value[idx] = it.copy(visible = true)
                    }
                }
            }
            requireRefreshSubList(hideAndNeverLoadedList)
        }else {  
            val allHideList = diffableItemList.value.map {it.copy(visible = false) }
            diffableItemList.value.clear()
            diffableItemList.value.addAll(allHideList)
        }
    }
    if(pageRequest.value == PageRequest.expandAll) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            showOrHideAll(true)
        }
    }
    if(pageRequest.value == PageRequest.collapseAll) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            showOrHideAll(false)
        }
    }
    if(pageRequest.value == PageRequest.showRestoreDialog) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initRestoreDialog()
        }
    }
    if(pageRequest.value == PageRequest.showDetails) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initDetailsDialog(curItemIndex.intValue)
        }
    }
    if(pageRequest.value == PageRequest.goToCurItem) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            scrollToCurrentItemHeader(getCurItem().relativePath)
        }
    }
    val lineClickedMenuOffset = remember(configuration.screenWidthDp) {
        DpOffset(x = (configuration.screenWidthDp/1.5f).coerceAtLeast(50f).dp, y=0.dp)
    }
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val switchItem = { oldItem:DiffableItem?, newItem:DiffableItem, newItemIndex:Int, isToNext:Boolean ->
        doJobThenOffLoading j@{
            oldItem?.closeLoadChannel()
            if(DiffFromScreen.isFromFileHistory(fromScreen)) {
                if(fromScreen == DiffFromScreen.FILE_HISTORY_TREE_TO_LOCAL) {
                    treeOid1Str.value = newItem.commitId
                }else {  
                    val leftOidStr = if(isToNext) {
                        diffableItemList.value.getOrNull(newItemIndex + 1)?.commitId
                    } else {
                        oldItem?.commitId
                    }
                    if(leftOidStr.isNullOrBlank()) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.plz_lode_more_then_try_again))
                        return@j
                    }
                    treeOid1Str.value = leftOidStr
                    treeOid2Str.value = newItem.commitId
                }
            }
            curItemIndex.intValue = newItemIndex
            changeStateTriggerRefreshPage(needRefresh, requestType = StateRequestType.requireGoToTop)
        }
        Unit
    }
    val saveFontSizeAndQuitAdjust = {
        adjustFontSizeModeOn.value = false
        SettingsUtil.update {
            it.diff.fontSize = fontSize.intValue
        }
        Unit
    }
    val saveLineNumFontSizeAndQuitAdjust = {
        adjustLineNumSizeModeOn.value = false
        SettingsUtil.update {
            it.diff.lineNumFontSize = lineNumFontSize.intValue
        }
        Unit
    }
    val errorStrRes = stringResource(R.string.error)
    val updateCurrentViewingIdx = { viewingIndex:Int ->
        curItemIndex.intValue = viewingIndex
    }
    BackHandler(
        onBack = getBackHandler(
            naviUp = naviUp,
            adjustFontSizeMode=adjustFontSizeModeOn,
            adjustLineNumFontSizeMode=adjustLineNumSizeModeOn,
            saveFontSizeAndQuitAdjust = saveFontSizeAndQuitAdjust,
            saveLineNumFontSizeAndQuitAdjust = saveLineNumFontSizeAndQuitAdjust,
        )
    )
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    val fileActuallyReadOnly = { diffableItem: DiffableItem -> readOnlyModeOn.value || !diffableItem.toFile().exists()}
    val showSelectSyntaxLangDialog = rememberSaveable { mutableStateOf(false) }
    val diffableItemIndexForSelctSyntaxLangDialog = rememberSaveable { mutableStateOf(0) }
    val plScopeForSelctSyntaxLangDialog = rememberSaveable { mutableStateOf(PLScope.AUTO) }
    val diffableItemForSelectSyntaxLangDialog = mutableCustomStateOf<DiffableItem?>(stateKeyTag, "diffableItemForSelectSyntaxLangDialog") { null }
    val initSelectSyntaxHighlightLanguagDialog = isshld@{ diffableItem: DiffableItem, diffableItemIndex: Int ->
        curItemIndex.value = diffableItemIndex
        diffableItemIndexForSelctSyntaxLangDialog.value = diffableItemIndex
        diffableItemForSelectSyntaxLangDialog.value = diffableItem
        plScopeForSelctSyntaxLangDialog.value = diffableItem.diffItemSaver.getAndUpdateScopeIfIsAuto(diffableItem.fileName)
        showSelectSyntaxLangDialog.value = true
    }
    if(showSelectSyntaxLangDialog.value) {
        SelectSyntaxHighlightingDialog(
            plScope = plScopeForSelctSyntaxLangDialog.value,
            closeDialog = { showSelectSyntaxLangDialog.value = false }
        ) { newScope ->
            val item = diffableItemForSelectSyntaxLangDialog.value ?: getCurItem()
            diffableItemForSelectSyntaxLangDialog.value = null
            val scopeChanged = item.diffItemSaver.changeScope(newScope) != false
            val itemIndex = diffableItemIndexForSelctSyntaxLangDialog.value
            try {
                if(scopeChanged || !item.visible) {
                    diffableItemList.value[itemIndex] = item.copy(visible = true)
                    requireRefreshSubList(listOf(itemIndex))
                }
            }catch (e: Exception) {
                val errPrefix = "switch language scope for '${item.fileName}' err: selected scope is `$newScope`, err="
                Msg.requireShowLongDuration("switch language scope err: ${e.localizedMessage}")
                MyLog.e(TAG, errPrefix + e.stackTraceToString())
                doJobThenOffLoading {
                    createAndInsertError(repoId, errPrefix + e.localizedMessage)
                }
            }
        }
    }
    if(pageRequest.value == PageRequest.showSyntaxHighlightingSelectLanguageDialogForCurItem) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initSelectSyntaxHighlightLanguagDialog(getCurItem(), curItemIndex.intValue)
        }
    }
    val filePathOfEditLineDialog = rememberSaveable { mutableStateOf("") }
    val lineContentOfEditLineDialog = rememberSaveable { mutableStateOf("") }
    val lineNumOfEditLineDialog = rememberSaveable { mutableStateOf(LineNum.invalidButNotEof) }  
    val lineNumStrOfEditLineDialog = rememberSaveable { mutableStateOf("") }  
    val truePrependFalseAppendNullReplace = rememberSaveable { mutableStateOf<Boolean?>(null) }
    val showDelLineDialog = rememberSaveable { mutableStateOf(false) }
    val trueRestoreFalseReplace = rememberSaveable { mutableStateOf(false) }
    val initEditLineDialog = { content:String, lineNum:Int, prependOrAppendOrReplace:Boolean?, filePath:String ->
        if(lineNum == LineNum.invalidButNotEof){
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
        }else {
            filePathOfEditLineDialog.value = filePath
            truePrependFalseAppendNullReplace.value = prependOrAppendOrReplace
            lineContentOfEditLineDialog.value = content
            lineNumOfEditLineDialog.value = lineNum
            showEditLineDialog.value = true
        }
    }
    val initDelLineDialog = { lineNum:Int, filePath:String ->
        if(lineNum == LineNum.invalidButNotEof){
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
        }else {
            filePathOfEditLineDialog.value = filePath
            lineNumOfEditLineDialog.value = lineNum
            showDelLineDialog.value = true
        }
    }
    val initRestoreLineDialog = { content:String, lineNum:Int, trueRestoreFalseReplace_param:Boolean, filePath:String ->
        if(lineNum == LineNum.invalidButNotEof){
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
        }else {
            filePathOfEditLineDialog.value = filePath
            lineContentOfEditLineDialog.value = content
            lineNumOfEditLineDialog.value = lineNum
            lineNumStrOfEditLineDialog.value = ""+lineNum
            trueRestoreFalseReplace.value = trueRestoreFalseReplace_param
            showRestoreLineDialog.value = true
        }
    }
    if(showEditLineDialog.value) {
        val focusRequester = remember { FocusRequester() }
        ConfirmDialogAndDisableSelection(
            onDismiss = {},
            title = if(truePrependFalseAppendNullReplace.value == true) stringResource(R.string.insert) else if(truePrependFalseAppendNullReplace.value == false) stringResource(R.string.append) else stringResource(R.string.edit),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Text(
                            replaceStringResList(
                                stringResource(if (truePrependFalseAppendNullReplace.value == null) R.string.line_at_n else R.string.new_line_at_n),
                                listOf(""+(if(lineNumOfEditLineDialog.value == LineNum.EOF.LINE_NUM) LineNum.EOF.TEXT else if (truePrependFalseAppendNullReplace.value != false) lineNumOfEditLineDialog.value else { lineNumOfEditLineDialog.value + 1 }))
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = lineContentOfEditLineDialog.value,
                        onValueChange = {
                            lineContentOfEditLineDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.content))
                        },
                    )
                }
            },
            okBtnText = stringResource(R.string.save),
            cancelTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showEditLineDialog.value = false}
        ) {
            showEditLineDialog.value = false
            val fileFullPath = filePathOfEditLineDialog.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.saving)) job@{
                try {
                    val lineNum = lineNumOfEditLineDialog.value
                    if(lineNum<1 && lineNum!=LineNum.EOF.LINE_NUM) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
                        return@job
                    }
                    val lines = FsUtils.stringToLines(lineContentOfEditLineDialog.value)
                    val file = FilePath(fileFullPath).toFuckSafFile(activityContext)
                    if(truePrependFalseAppendNullReplace.value == true) {
                        FsUtils.prependLinesToFile(file, lineNum, lines, settings)
                    }else if (truePrependFalseAppendNullReplace.value == false) {
                        FsUtils.appendLinesToFile(file, lineNum, lines, settings)
                    }else {
                        FsUtils.replaceLinesToFile(file, lineNum, lines, settings)
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                    refreshPage()
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?:"err"
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(repoId, errMsg)
                }
            }
        }
        OneTimeFocusRightNow(focusRequester)
    }
    if(showDelLineDialog.value) {
        ConfirmDialogAndDisableSelection(
            title = stringResource(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            replaceStringResList(
                                stringResource(R.string.line_at_n),
                                listOf(
                                    if (lineNumOfEditLineDialog.value != LineNum.EOF.LINE_NUM) {
                                        "" + lineNumOfEditLineDialog.value
                                    } else {
                                        LineNum.EOF.TEXT
                                    }
                                )
                            )
                        )
                    }
                }
            },
            okBtnText = stringResource(R.string.delete),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showDelLineDialog.value = false}
        ) {
            showDelLineDialog.value = false
            val fileFullPath = filePathOfEditLineDialog.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.deleting)) job@{
                try {
                    val lineNum = lineNumOfEditLineDialog.value
                    if(lineNum<1 && lineNum!=LineNum.EOF.LINE_NUM) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
                        return@job
                    }
                    val file = FilePath(fileFullPath).toFuckSafFile(activityContext)
                    FsUtils.deleteLineToFile(file, lineNum, settings)
                    Msg.requireShow(activityContext.getString(R.string.success))
                    refreshPage()
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?:"err"
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(repoId, errMsg)
                }
            }
        }
    }
    if(showRestoreLineDialog.value) {
        val focusRequester = remember { FocusRequester() }
        ConfirmDialogAndDisableSelection(
            onDismiss = {},
            title = if(trueRestoreFalseReplace.value) stringResource(R.string.restore) else stringResource(R.string.replace),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Text(stringResource(R.string.note_if_line_number_doesnt_exist_will_append_content_to_the_end_of_the_file), color = MyStyleKt.TextColor.getHighlighting())
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = lineNumStrOfEditLineDialog.value,
                        onValueChange = {
                            lineNumStrOfEditLineDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.line_number))
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = lineContentOfEditLineDialog.value,
                        onValueChange = {
                            lineContentOfEditLineDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.content))
                        },
                    )
                }
            },
            okBtnText = if(trueRestoreFalseReplace.value) stringResource(R.string.restore) else stringResource(R.string.replace),
            cancelTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showRestoreLineDialog.value = false}
        ) {
            showRestoreLineDialog.value = false
            val fileFullPath = filePathOfEditLineDialog.value
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.restoring)) job@{
                try {
                    var lineNum = try {
                        lineNumStrOfEditLineDialog.value.toInt()
                    }catch (_:Exception) {
                        LineNum.invalidButNotEof
                    }
                    if(lineNum<1) {
                        lineNum=LineNum.EOF.LINE_NUM
                    }
                    val lines = FsUtils.stringToLines(lineContentOfEditLineDialog.value)
                    val file = FilePath(fileFullPath).toFuckSafFile(activityContext)
                    if(trueRestoreFalseReplace.value) {
                        FsUtils.prependLinesToFile(file, lineNum, lines, settings)
                    }else {
                        FsUtils.replaceLinesToFile(file, lineNum, lines, settings)
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                    refreshPage()
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?:"err"
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(repoId, errMsg)
                }
            }
        }
        OneTimeFocusRightNow(focusRequester)
    }
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColorsSimple(),
                title = {
                    val curItem = getCurItem()
                    DiffScreenTitle(
                        isMultiMode = isMultiMode,
                        listState = listState,
                        scope = scope,
                        request = pageRequest,
                        readOnly = fileActuallyReadOnly(curItem),
                        lastPosition = lastPosition,
                        curItem = curItem
                    )
                },
                navigationIcon = {
                    if(adjustFontSizeModeOn.value || adjustLineNumSizeModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),
                        ) {
                            if(adjustFontSizeModeOn.value) {
                                saveFontSizeAndQuitAdjust()
                            }else {
                                saveLineNumFontSizeAndQuitAdjust()
                            }
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
                    if(adjustFontSizeModeOn.value) {
                        FontSizeAdjuster(fontSize = fontSize, resetValue = SettingsCons.defaultFontSize)
                    }else if(adjustLineNumSizeModeOn.value){
                        FontSizeAdjuster(fontSize = lineNumFontSize, resetValue = SettingsCons.defaultLineNumFontSize)
                    }else {
                        DiffPageActions(
                            isMultiMode = isMultiMode,
                            fromTo=fromTo,
                            refreshPage = refreshPage,
                            request = pageRequest,
                            requireBetterMatchingForCompare = requireBetterMatchingForCompare,
                            readOnlyModeOn = readOnlyModeOn,
                            readOnlyModeSwitchable = readOnlySwitchable.value,
                            showLineNum=showLineNum,
                            showOriginType=showOriginType,
                            adjustFontSizeModeOn = adjustFontSizeModeOn,
                            adjustLineNumSizeModeOn = adjustLineNumSizeModeOn,
                            groupDiffContentByLineNum=groupDiffContentByLineNum,
                            enableSelectCompare=enableSelectCompare,
                            matchByWords=matchByWords,
                        )
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(pageScrolled.value) {
                GoToTopAndGoToBottomFab(
                    scope = scope,
                    listState = listState,
                    listLastPosition = lastPosition,
                    showFab = pageScrolled
                )
            }
        }
    ) { contentPadding ->
        if(loadingForAction.value) {
            LoadingDialog(loadingText.value)
        }
        val loadingOnParent=loadingOn
        val loadingOffParent=loadingOff
        val enableSelectCompare = enableSelectCompare.value;
        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {
            MySelectionContainerPlaceHolder {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                    state = listState
                ) {
                    val diffableItemList = diffableItemList.value
                    diffableItemList.toList().forEachIndexedBetter diffableItemListLoop@{ diffableItemIdx, diffableItem ->
                        if(isSingleMode && diffableItemIdx != curItemIndex.intValue) return@diffableItemListLoop;
                        val diffItem = diffableItem.diffItemSaver
                        val changeType = diffableItem.diffItemSaver.changeType
                        val visible = diffableItem.visible
                        val diffableItemFile = diffableItem.toFile()
                        val relativePath = diffableItem.relativePath
                        val readOnlyModeOn = fileActuallyReadOnly(diffableItem)
                        val switchVisible = {
                            val newVisible = visible.not()
                            diffableItemList[diffableItemIdx] = diffableItem.copy(visible = newVisible)
                            curItemIndex.intValue = diffableItemIdx
                            if(newVisible && diffableItem.neverLoadedDifferences()) {
                                requireRefreshSubList(listOf(diffableItemIdx))
                            }
                        }
                        val colorOfChangeType = UIHelper.getChangeTypeColor(changeType)
                        val iconSize = MenuIconBtnItem.defaultIconSize
                        val pressedCircleSize = MenuIconBtnItem.defaultPressedCircleSize
                        if (showMyFileHeader) {
                            item {
                                val moreMenuExpandState = remember { mutableStateOf(false) }
                                BarContainer(
                                    modifier = Modifier
                                        .onGloballyPositioned { layoutCoordinates ->
                                            if(visible) {
                                                val position = layoutCoordinates.positionInRoot()
                                                if(position.y < 0) {
                                                    updateCurrentViewingIdx(diffableItemIdx)
                                                }
                                            }
                                        }
                                    ,
                                    onClick = switchVisible,
                                    showMoreIcon = true,
                                    moreMenuExpandState = moreMenuExpandState,
                                    moreMenuItems = (if(fromScreen == DiffFromScreen.HOME_CHANGELIST) {
                                        mutableListOf(
                                            MenuTextItem(
                                                text = stringResource(R.string.stage),
                                                onClick = {
                                                    doJobThenOffLoading {
                                                        stageItem(listOf(diffableItem.toChangeListItem()))
                                                    }
                                                }
                                            ),
                                            MenuTextItem(
                                                text = stringResource(R.string.revert),
                                                onClick = {
                                                    initRevertDialog(listOf(diffableItem.toChangeListItem())) {}
                                                }
                                            )
                                        )
                                    }else if(fromScreen == DiffFromScreen.INDEX) {
                                        mutableListOf(
                                            MenuTextItem(
                                                text = stringResource(R.string.unstage),
                                                onClick = {
                                                    initUnstageDialog(listOf(diffableItem.toChangeListItem())) {}
                                                }
                                            )
                                        )
                                    }else {
                                        mutableListOf<MenuTextItem>()
                                    }).apply {
                                        add(
                                            MenuTextItem(
                                                text = stringResource(R.string.create_patch),
                                                onClick = {
                                                    initCreatePatchDialog(listOf(relativePath))
                                                }
                                            )
                                        )
                                        add(
                                            MenuTextItem(
                                                text = stringResource(R.string.syntax_highlighting),
                                                onClick = {
                                                    initSelectSyntaxHighlightLanguagDialog(diffableItem, diffableItemIdx)
                                                }
                                            )
                                        )
                                    },
                                    actions = listOf(
                                        MenuIconBtnItem(
                                            icon = Icons.Filled.Refresh,
                                            text = stringResource(R.string.refresh),
                                            onClick = {
                                                requireRefreshSubList(listOf(diffableItemIdx))
                                            }
                                        ),
                                        MenuIconBtnItem(
                                            icon = Icons.Filled.FileOpen,
                                            text = stringResource(R.string.open),
                                            onClick = {
                                                openFileWithInnerSubPageEditor(diffableItem.fullPath)
                                            }
                                        ),
                                        MenuIconBtnItem(
                                            icon = Icons.AutoMirrored.Filled.OpenInNew,
                                            text = stringResource(R.string.open_as),
                                            onClick = {
                                                initOpenAsDialog(diffableItemIdx)
                                            }
                                        ),
                                        ),
                                ) {
                                    val loadedAtLeastOnce = diffableItem.maybeLoadedAtLeastOnce()
                                    Row(
                                        modifier = Modifier.widthIn(max = fileTitleFileNameWidthLimit),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        InLineIcon(
                                            iconModifier = Modifier.size(iconSize),
                                            pressedCircleSize = pressedCircleSize,
                                            icon = if(visible) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowRight ,
                                            tooltipText = "",
                                        )
                                        Column {
                                            ScrollableRow(
                                                modifier = Modifier
                                                    .clickable { initDetailsDialog(diffableItemIdx) }
                                                    .widthIn(min = MyStyleKt.Title.clickableTitleMinWidth)
                                                ,
                                            ) {
                                                if(readOnlyModeOn) {
                                                    ReadOnlyIcon()
                                                }
                                                Text(
                                                    text = diffableItem.fileName,
                                                    fontSize = titleFileNameFontSize,
                                                    color = colorOfChangeType,
                                                )
                                            }
                                            ScrollableRow {
                                                Text(
                                                    text = diffableItem.getAnnotatedAddDeletedAndParentPathString(colorOfChangeType),
                                                    fontSize = titleRelativePathFontSize,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(!visible) {
                            return@diffableItemListLoop
                        }
                        val isSubmodule = diffableItem.itemType == Cons.gitItemTypeSubmodule
                        val errMsg = diffableItem.errMsg
                        val submoduleIsDirty = diffableItem.submoduleIsDirty
                        val loading = diffableItem.loading
                        val loadChannel = diffableItem.loadChannel
                        val fileChangeTypeIsModified = true
                        val enableLineEditActions = if(localAtDiffRight.not() || readOnlyModeOn || isSubmodule) {
                            false
                        }else {
                            diffableItemFile.let { f ->
                                f.exists() && f.isFile
                            }
                        }
                        if (diffableItem.noDiffItemAvailable) {
                            item {
                                DisableSelection {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 100.dp, horizontal = 20.dp)
                                        ,
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        MySelectionContainer {
                                            diffableItem.whyNoDiffItem?.invoke()
                                        }
                                    }
                                    if(isSingleMode) {
                                        HunkDivider()
                                    }
                                }
                            }
                        } else {  
                            val indexStringPartListMapForComparePair = diffableItem.stringPairMap
                            val comparePairBuffer = diffableItem.compareLinePair
                            if (submoduleIsDirty) {
                                item {
                                    ScrollableRow (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                        ,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Text(stringResource(R.string.submodule_is_dirty_note_short), fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)
                                    }
                                }
                            }
                            val groupDiffContentByLineNum = groupDiffContentByLineNum.value
                            val itemFile = File(curRepo.value.fullSavePath, relativePath)
                            val fileFullPath = itemFile.canonicalPath
                            val showOriginType = showOriginType.value
                            val showLineNum = showLineNum.value
                            val fontSize = fontSize.intValue.sp
                            val lastHunkIndex = diffItem.hunks.size - 1;
                            val lineNumSize = lineNumFontSize.intValue.sp
                            val getComparePairBuffer = { diffableItem.compareLinePair }
                            val setComparePairBuffer = { newCompareLinePair:CompareLinePair ->
                                diffableItemList[diffableItemIdx] = diffableItemList[diffableItemIdx].copy(compareLinePair = newCompareLinePair)
                            }
                            val enableSelectCompare = fileChangeTypeIsModified && enableSelectCompare;
                            diffItem.hunks.forEachIndexedBetter { hunkIndex, hunkAndLines: PuppyHunkAndLines ->
                                item {
                                    SelectionRow(
                                        modifier = Modifier
                                            .background(UIHelper.getHunkColor())
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = hunkAndLines.hunk.cachedNoLineBreakHeader(),
                                            fontFamily = PLFont.diffCodeFont(),
                                            fontSize = fontSize,
                                            fontStyle = FontStyle.Italic,
                                            color = UIHelper.getFontColor(),
                                        )
                                    }
                                }
                                val lineNumExpectLength = diffItem.maxLineNum.toString().length.let { if(diffItem.hasEofLine) it.coerceAtLeast(LineNum.EOF.TEXT.length) else it }
                                if (fileChangeTypeIsModified && proFeatureEnabled(detailsDiffTestPassed)) {  
                                    if (!groupDiffContentByLineNum || FlagFileName.flagFileExist(FlagFileName.disableGroupDiffContentByLineNum)) {
                                        hunkAndLines.clearCachesForShown()
                                        hunkAndLines.lines.forEachIndexedBetter printLine@{ lineIndex, line: PuppyLine ->
                                            if (line.originType != PuppyLineOriginType.ADDITION
                                                && line.originType != PuppyLineOriginType.DELETION
                                                && line.originType != PuppyLineOriginType.CONTEXT
                                            ) {
                                                return@printLine
                                            }
                                            if (line.originType == PuppyLineOriginType.CONTEXT) {
                                                item {
                                                    DiffRow(
                                                        index = lineIndex,
                                                        lineNumExpectLength = lineNumExpectLength,
                                                        line = line,
                                                        fileFullPath = fileFullPath,
                                                        enableLineEditActions = enableLineEditActions,
                                                        clipboardManager = clipboardManager,
                                                        loadingOn = loadingOnParent,
                                                        loadingOff = loadingOffParent,
                                                        repoId = repoId,
                                                        showOriginType = showOriginType,
                                                        showLineNum = showLineNum,
                                                        fontSize = fontSize,
                                                        lineNumSize = lineNumSize,
                                                        getComparePairBuffer = getComparePairBuffer,
                                                        setComparePairBuffer = setComparePairBuffer,
                                                        betterCompare = requireBetterMatchingForCompare.value,
                                                        indexStringPartListMap = indexStringPartListMapForComparePair,
                                                        enableSelectCompare = enableSelectCompare,
                                                        matchByWords = matchByWords.value,
                                                        settings = settings,
                                                        navController = navController,
                                                        activityContext = activityContext,
                                                        stateKeyTag = stateKeyTag,
                                                        lineClickedMenuOffset = lineClickedMenuOffset,
                                                        diffItemSaver = diffItem,
                                                        initEditLineDialog = initEditLineDialog,
                                                        initDelLineDialog = initDelLineDialog,
                                                        initRestoreLineDialog = initRestoreLineDialog,
                                                    )
                                                }
                                            } else {  
                                                val mergeAddDelLineResult = hunkAndLines.needShowAddOrDelLineAsContext(line.lineNum)
                                                if (mergeAddDelLineResult.needShowAsContext) {
                                                    if (mergeAddDelLineResult.line != null) {
                                                        item {
                                                            DiffRow(
                                                                index = lineIndex,
                                                                lineNumExpectLength = lineNumExpectLength,
                                                                line = mergeAddDelLineResult.line,
                                                                fileFullPath = fileFullPath,
                                                                enableLineEditActions = enableLineEditActions,
                                                                clipboardManager = clipboardManager,
                                                                loadingOn = loadingOnParent,
                                                                loadingOff = loadingOffParent,
                                                                repoId = repoId,
                                                                showOriginType = showOriginType,
                                                                showLineNum = showLineNum,
                                                                fontSize = fontSize,
                                                                lineNumSize = lineNumSize,
                                                                getComparePairBuffer = getComparePairBuffer,
                                                                setComparePairBuffer = setComparePairBuffer,
                                                                betterCompare = requireBetterMatchingForCompare.value,
                                                                indexStringPartListMap = indexStringPartListMapForComparePair,
                                                                enableSelectCompare = enableSelectCompare,
                                                                matchByWords = matchByWords.value,
                                                                settings = settings,
                                                                navController = navController,
                                                                activityContext = activityContext,
                                                                stateKeyTag = stateKeyTag,
                                                                lineClickedMenuOffset = lineClickedMenuOffset,
                                                                diffItemSaver = diffItem,
                                                                initEditLineDialog = initEditLineDialog,
                                                                initDelLineDialog = initDelLineDialog,
                                                                initRestoreLineDialog = initRestoreLineDialog,
                                                            )
                                                        }
                                                    }
                                                    return@printLine
                                                }
                                                val compareResult = indexStringPartListMapForComparePair[line.key]
                                                val stringPartListWillUse = if (compareResult == null) {
                                                    val modifyResult = hunkAndLines.getModifyResult(
                                                        line = line,
                                                        requireBetterMatchingForCompare = requireBetterMatchingForCompare.value,
                                                        matchByWords = matchByWords.value
                                                    )
                                                    if (modifyResult?.matched == true) {
                                                        if (line.originType == PuppyLineOriginType.ADDITION) modifyResult.add else modifyResult.del
                                                    } else {
                                                        null
                                                    }
                                                } else {
                                                    compareResult.stringPartList
                                                }
                                                item {
                                                    DiffRow(
                                                        index = lineIndex,
                                                        lineNumExpectLength = lineNumExpectLength,
                                                        line = line,
                                                        fileFullPath = fileFullPath,
                                                        stringPartList = stringPartListWillUse,
                                                        enableLineEditActions = enableLineEditActions,
                                                        clipboardManager = clipboardManager,
                                                        loadingOn = loadingOnParent,
                                                        loadingOff = loadingOffParent,
                                                        repoId = repoId,
                                                        showOriginType = showOriginType,
                                                        showLineNum = showLineNum,
                                                        fontSize = fontSize,
                                                        lineNumSize = lineNumSize,
                                                        getComparePairBuffer = getComparePairBuffer,
                                                        setComparePairBuffer = setComparePairBuffer,
                                                        betterCompare = requireBetterMatchingForCompare.value,
                                                        indexStringPartListMap = indexStringPartListMapForComparePair,
                                                        enableSelectCompare = enableSelectCompare,
                                                        matchByWords = matchByWords.value,
                                                        settings = settings,
                                                        navController = navController,
                                                        activityContext = activityContext,
                                                        stateKeyTag = stateKeyTag,
                                                        lineClickedMenuOffset = lineClickedMenuOffset,
                                                        diffItemSaver = diffItem,
                                                        initEditLineDialog = initEditLineDialog,
                                                        initDelLineDialog = initDelLineDialog,
                                                        initRestoreLineDialog = initRestoreLineDialog,
                                                    )
                                                }
                                            }
                                        }
                                    } else {  
                                        hunkAndLines.groupedLines.forEachBetter printLine@{ _lineNum: Int, lines: Map<String, PuppyLine> ->
                                            if (!(lines.contains(PuppyLineOriginType.ADDITION)
                                                        || lines.contains(PuppyLineOriginType.DELETION)
                                                        || lines.contains(PuppyLineOriginType.CONTEXT)
                                                        )
                                            ) {
                                                return@printLine
                                            }
                                            val add = lines.get(PuppyLineOriginType.ADDITION)
                                            val del = lines.get(PuppyLineOriginType.DELETION)
                                            val context = lines.get(PuppyLineOriginType.CONTEXT)
                                            val mergeDelAndAddToFakeContext = add != null && del != null && add.getContentNoLineBreak().equals(del.getContentNoLineBreak());
                                            if (mergeDelAndAddToFakeContext.not()) {  
                                                val addCompareLinePairResult = indexStringPartListMapForComparePair.get(add?.key ?: "nonexist keyadd")
                                                val delCompareLinePairResult = indexStringPartListMapForComparePair.get(del?.key ?: "nonexist keydel")
                                                var addUsedPair = false
                                                var delUsedPair = false
                                                var delStringPartListWillUse: List<IndexStringPart>? = null
                                                var addStringPartListWillUse: List<IndexStringPart>? = null
                                                if (delCompareLinePairResult != null && del != null) {
                                                    delUsedPair = true
                                                    delStringPartListWillUse = delCompareLinePairResult.stringPartList
                                                }
                                                if (addCompareLinePairResult != null && add != null) {
                                                    addUsedPair = true
                                                    addStringPartListWillUse = addCompareLinePairResult.stringPartList
                                                }
                                                if(del != null && !delUsedPair) {
                                                    val modifyResult = hunkAndLines.getModifyResult(
                                                        line = del,
                                                        requireBetterMatchingForCompare = requireBetterMatchingForCompare.value,
                                                        matchByWords = matchByWords.value
                                                    )
                                                    if (modifyResult?.matched == true) {
                                                        delStringPartListWillUse = modifyResult.del
                                                    }
                                                }
                                                if(add != null && !addUsedPair) {
                                                    val modifyResult = hunkAndLines.getModifyResult(
                                                        line = add,
                                                        requireBetterMatchingForCompare = requireBetterMatchingForCompare.value,
                                                        matchByWords = matchByWords.value
                                                    )
                                                    if (modifyResult?.matched == true) {
                                                        addStringPartListWillUse = modifyResult.add
                                                    }
                                                }
                                                if (del != null) {
                                                    item {
                                                        DiffRow(
                                                            index = del.fakeIndexOfGroupedLine,
                                                            lineNumExpectLength = lineNumExpectLength,
                                                            line = del,
                                                            stringPartList = delStringPartListWillUse,
                                                            fileFullPath = fileFullPath,
                                                            enableLineEditActions = enableLineEditActions,
                                                            clipboardManager = clipboardManager,
                                                            loadingOn = loadingOnParent,
                                                            loadingOff = loadingOffParent,
                                                            repoId = repoId,
                                                            showOriginType = showOriginType,
                                                            showLineNum = showLineNum,
                                                            fontSize = fontSize,
                                                            lineNumSize = lineNumSize,
                                                            getComparePairBuffer = getComparePairBuffer,
                                                            setComparePairBuffer = setComparePairBuffer,
                                                            betterCompare = requireBetterMatchingForCompare.value,
                                                            indexStringPartListMap = indexStringPartListMapForComparePair,
                                                            enableSelectCompare = enableSelectCompare,
                                                            matchByWords = matchByWords.value,
                                                            settings = settings,
                                                            navController = navController,
                                                            activityContext = activityContext,
                                                            stateKeyTag = stateKeyTag,
                                                            lineClickedMenuOffset = lineClickedMenuOffset,
                                                            diffItemSaver = diffItem,
                                                            initEditLineDialog = initEditLineDialog,
                                                            initDelLineDialog = initDelLineDialog,
                                                            initRestoreLineDialog = initRestoreLineDialog,
                                                        )
                                                    }
                                                }
                                                if (add != null) {
                                                    item {
                                                        DiffRow(
                                                            index = add.fakeIndexOfGroupedLine,
                                                            lineNumExpectLength = lineNumExpectLength,
                                                            line = add,
                                                            stringPartList = addStringPartListWillUse,
                                                            fileFullPath = fileFullPath,
                                                            enableLineEditActions = enableLineEditActions,
                                                            clipboardManager = clipboardManager,
                                                            loadingOn = loadingOnParent,
                                                            loadingOff = loadingOffParent,
                                                            repoId = repoId,
                                                            showOriginType = showOriginType,
                                                            showLineNum = showLineNum,
                                                            fontSize = fontSize,
                                                            lineNumSize = lineNumSize,
                                                            getComparePairBuffer = getComparePairBuffer,
                                                            setComparePairBuffer = setComparePairBuffer,
                                                            betterCompare = requireBetterMatchingForCompare.value,
                                                            indexStringPartListMap = indexStringPartListMapForComparePair,
                                                            enableSelectCompare = enableSelectCompare,
                                                            matchByWords = matchByWords.value,
                                                            settings = settings,
                                                            navController = navController,
                                                            activityContext = activityContext,
                                                            stateKeyTag = stateKeyTag,
                                                            lineClickedMenuOffset = lineClickedMenuOffset,
                                                            diffItemSaver = diffItem,
                                                            initEditLineDialog = initEditLineDialog,
                                                            initDelLineDialog = initDelLineDialog,
                                                            initRestoreLineDialog = initRestoreLineDialog,
                                                        )
                                                    }
                                                }
                                            } else if (context == null) { 
                                                item {
                                                    DiffRow(
                                                        index = del.fakeIndexOfGroupedLine,
                                                        lineNumExpectLength = lineNumExpectLength,
                                                        line = del!!.copy(originType = PuppyLineOriginType.CONTEXT),
                                                        fileFullPath = fileFullPath,
                                                        enableLineEditActions = enableLineEditActions,
                                                        clipboardManager = clipboardManager,
                                                        loadingOn = loadingOnParent,
                                                        loadingOff = loadingOffParent,
                                                        repoId = repoId,
                                                        showOriginType = showOriginType,
                                                        showLineNum = showLineNum,
                                                        fontSize = fontSize,
                                                        lineNumSize = lineNumSize,
                                                        getComparePairBuffer = getComparePairBuffer,
                                                        setComparePairBuffer = setComparePairBuffer,
                                                        betterCompare = requireBetterMatchingForCompare.value,
                                                        indexStringPartListMap = indexStringPartListMapForComparePair,
                                                        enableSelectCompare = enableSelectCompare,
                                                        matchByWords = matchByWords.value,
                                                        settings = settings,
                                                        navController = navController,
                                                        activityContext = activityContext,
                                                        stateKeyTag = stateKeyTag,
                                                        lineClickedMenuOffset = lineClickedMenuOffset,
                                                        diffItemSaver = diffItem,
                                                        initEditLineDialog = initEditLineDialog,
                                                        initDelLineDialog = initDelLineDialog,
                                                        initRestoreLineDialog = initRestoreLineDialog,
                                                    )
                                                }
                                                return@printLine
                                            }
                                            if (context != null) {
                                                item {
                                                    DiffRow(
                                                        index = context.fakeIndexOfGroupedLine,
                                                        lineNumExpectLength = lineNumExpectLength,
                                                        line = context,
                                                        fileFullPath = fileFullPath,
                                                        enableLineEditActions = enableLineEditActions,
                                                        clipboardManager = clipboardManager,
                                                        loadingOn = loadingOnParent,
                                                        loadingOff = loadingOffParent,
                                                        repoId = repoId,
                                                        showOriginType = showOriginType,
                                                        showLineNum = showLineNum,
                                                        fontSize = fontSize,
                                                        lineNumSize = lineNumSize,
                                                        getComparePairBuffer = getComparePairBuffer,
                                                        setComparePairBuffer = setComparePairBuffer,
                                                        betterCompare = requireBetterMatchingForCompare.value,
                                                        indexStringPartListMap = indexStringPartListMapForComparePair,
                                                        enableSelectCompare = enableSelectCompare,
                                                        matchByWords = matchByWords.value,
                                                        settings = settings,
                                                        navController = navController,
                                                        activityContext = activityContext,
                                                        stateKeyTag = stateKeyTag,
                                                        lineClickedMenuOffset = lineClickedMenuOffset,
                                                        diffItemSaver = diffItem,
                                                        initEditLineDialog = initEditLineDialog,
                                                        initDelLineDialog = initDelLineDialog,
                                                        initRestoreLineDialog = initRestoreLineDialog,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else { 
                                    hunkAndLines.lines.forEachIndexedBetter printLine@{ lineIndex, line: PuppyLine ->
                                        if (line.originType == PuppyLineOriginType.ADDITION
                                            || line.originType == PuppyLineOriginType.DELETION
                                            || line.originType == PuppyLineOriginType.CONTEXT
                                        ) {
                                            item {
                                                DiffRow(
                                                    index = lineIndex,
                                                    lineNumExpectLength = lineNumExpectLength,
                                                    line = line,
                                                    fileFullPath = fileFullPath,
                                                    enableLineEditActions = enableLineEditActions,
                                                    clipboardManager = clipboardManager,
                                                    loadingOn = loadingOnParent,
                                                    loadingOff = loadingOffParent,
                                                    repoId = repoId,
                                                    showOriginType = showOriginType,
                                                    showLineNum = showLineNum,
                                                    fontSize = fontSize,
                                                    lineNumSize = lineNumSize,
                                                    getComparePairBuffer = getComparePairBuffer,
                                                    setComparePairBuffer = setComparePairBuffer,
                                                    betterCompare = requireBetterMatchingForCompare.value,
                                                    indexStringPartListMap = indexStringPartListMapForComparePair,
                                                    enableSelectCompare = enableSelectCompare,
                                                    matchByWords = matchByWords.value,
                                                    settings = settings,
                                                    navController = navController,
                                                    activityContext = activityContext,
                                                    stateKeyTag = stateKeyTag,
                                                    lineClickedMenuOffset = lineClickedMenuOffset,
                                                    diffItemSaver = diffItem,
                                                    initEditLineDialog = initEditLineDialog,
                                                    initDelLineDialog = initDelLineDialog,
                                                    initRestoreLineDialog = initRestoreLineDialog,
                                                )
                                            }
                                        }
                                    }
                                }
                                if (hunkIndex == lastHunkIndex) {
                                    val indexOfEOFNL = hunkAndLines.lines.indexOfFirst { it.originType == PuppyLineOriginType.ADD_EOFNL || it.originType == PuppyLineOriginType.DEL_EOFNL }
                                    if (indexOfEOFNL != -1) {  
                                        val eofLine = hunkAndLines.lines.get(indexOfEOFNL)
                                        val fakeIndex = -1
                                        item {
                                            DiffRow(
                                                index = fakeIndex,
                                                lineNumExpectLength = lineNumExpectLength,
                                                line = LineNum.EOF.transLineToEofLine(eofLine, add = eofLine.originType == PuppyLineOriginType.ADD_EOFNL),
                                                fileFullPath = fileFullPath,
                                                enableLineEditActions = enableLineEditActions,
                                                clipboardManager = clipboardManager,
                                                loadingOn = loadingOnParent,
                                                loadingOff = loadingOffParent,
                                                repoId = repoId,
                                                showOriginType = showOriginType,
                                                showLineNum = showLineNum,
                                                fontSize = fontSize,
                                                lineNumSize = lineNumSize,
                                                getComparePairBuffer = getComparePairBuffer,
                                                setComparePairBuffer = setComparePairBuffer,
                                                betterCompare = requireBetterMatchingForCompare.value,
                                                indexStringPartListMap = indexStringPartListMapForComparePair,
                                                enableSelectCompare = enableSelectCompare,
                                                matchByWords = matchByWords.value,
                                                settings = settings,
                                                navController = navController,
                                                activityContext = activityContext,
                                                stateKeyTag = stateKeyTag,
                                                lineClickedMenuOffset = lineClickedMenuOffset,
                                                diffItemSaver = diffItem,
                                                initEditLineDialog = initEditLineDialog,
                                                initDelLineDialog = initDelLineDialog,
                                                initRestoreLineDialog = initRestoreLineDialog,
                                            )
                                        }
                                    }
                                }
                                if(isSingleMode || hunkIndex != lastHunkIndex) {
                                    item {
                                        HunkDivider()
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(80.dp))
                        }
                        if(isMultiMode) {
                            item {
                                BarContainer(
                                    modifier = Modifier
                                        .onGloballyPositioned { layoutCoordinates ->
                                            if(visible) {
                                                val position = layoutCoordinates.positionInRoot()
                                                if(position.y < 0) {
                                                    updateCurrentViewingIdx(diffableItemIdx)
                                                }
                                            }
                                        }
                                    ,
                                    horizontalArrangement = Arrangement.Center,
                                    onClick = {scrollToCurrentItemHeader(relativePath)}
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        ScrollableRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                        ) {
                                            Text(
                                                text = diffableItem.fileName,
                                                fontSize = titleFileNameFontSize,
                                                color = colorOfChangeType
                                            )
                                        }
                                        InLineIcon(
                                            iconModifier = Modifier.size(iconSize),
                                            pressedCircleSize = pressedCircleSize,
                                            icon = Icons.Filled.KeyboardDoubleArrowUp,
                                            tooltipText = "",  
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.height(if(isMultiMode) 150.dp else 50.dp))
                    }
                    item {
                        DisableSelection {
                            NaviButton(
                                stateKeyTag = stateKeyTag,
                                isMultiMode = isMultiMode,
                                fromScreen = fromScreen,
                                diffableItemList = diffableItemList,
                                curItemIndex = curItemIndex,
                                switchItem = switchItem,
                                fromTo = fromTo,
                                naviUp = naviUp,
                                lastClickedItemKey = lastClickedItemKey,
                                pageRequest = pageRequest,
                                stageItem = stageItem,
                                initRevertDialog = initRevertDialog,
                                initUnstageDialog = initUnstageDialog,
                                goToTop = goToTop,
                            )
                        }
                    }
                    item {
                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
        }
    }
    LaunchedEffect(needRefresh.value) {
        val (requestType, requestData) = getRequestDataByState<Any?>(needRefresh.value)
        if(requestType == StateRequestType.requireGoToTop) {
            goToTop()
        }
        val subList = subDiffableItemList.value.toList()
        subDiffableItemList.value.clear()  
        val subDiffableItemList = Unit  
        val repoDb = dbContainer.repoRepository
        val repoFromDb = repoDb.getById(repoId)
        if(repoFromDb == null) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.repo_id_invalid))
            return@LaunchedEffect
        }
        curRepo.value = repoFromDb
        val treeOid1Str = treeOid1Str.value
        val treeOid2Str = treeOid2Str.value
        try {
            Repository.open(repoFromDb.fullSavePath).use { repo ->
                Libgit2Helper.resolveCommitByHashOrRef(repo, treeOid1Str).let {
                    tree1FullHash.value = it.data?.id()?.toString() ?: treeOid1Str
                }
                Libgit2Helper.resolveCommitByHashOrRef(repo, treeOid2Str).let {
                    tree2FullHash.value = it.data?.id()?.toString() ?: treeOid2Str
                }
            }
        }catch (e: Exception) {
            MyLog.d(TAG, "resolve tree1 and tree2 full hash failed: treeOid1Str=$treeOid1Str, treeOid2Str=$treeOid2Str, err=${e.stackTraceToString()}")
        }
        val firstLoad = firstTimeLoad.value
        if(isMultiMode && firstTimeLoad.value) {
            firstTimeLoad.value = false
            scope.launch {
                runCatching {
                    delay(500)
                    UIHelper.scrollToItem(scope, listState, curItemIndex.intValue)
                }
            }
        }
        val noMoreMemToaster = OneTimeToast()
        diffableItemList.value.toList().forEachIndexedBetter label@{ idx, item ->
            if(isSingleMode && idx != curItemIndex.intValue) return@label;
            if(!isSingleMode && ((subList.isNotEmpty() && subList.contains(idx).not()) || (subList.isEmpty() && !item.visible))) return@label;
            item.closeLoadChannel()
            val item  = item.copyForLoading()
            diffableItemList.value[idx] = item  
            val relativePath = item.relativePath
            val isSubmodule = item.itemType == Cons.gitItemTypeSubmodule;
            val channelForThisJob = item.loadChannel
            doJobThenOffLoading launch@{
                val loadedDiffableItem = try {
                    if(channelForThisJob.tryReceive().isClosed) {
                        return@launch
                    }
                    val languageScope = item.diffItemSaver.languageScope.value
                    Repository.open(repoFromDb.fullSavePath).use { repo->
                        val diffItemSaver = if(treeOid1Str == treeOid2Str) {
                            DiffItemSaver(relativePathUnderRepo = relativePath, fromTo = fromTo)
                        } else if(fromTo == Cons.gitDiffFromTreeToTree || fromTo == Cons.gitDiffFileHistoryFromTreeToLocal || fromTo == Cons.gitDiffFileHistoryFromTreeToPrev){  
                            val diffItemSaver = if(Libgit2Helper.CommitUtil.isLocalCommitHash(treeOid1Str) || Libgit2Helper.CommitUtil.isLocalCommitHash(treeOid2Str)) {  
                                val reverse = Libgit2Helper.CommitUtil.isLocalCommitHash(treeOid1Str)
                                val isActuallyIndexToLocal = if(reverse) treeOid2Str == Cons.git_IndexCommitHash else (treeOid1Str == Cons.git_IndexCommitHash);
                                val tree1 = if(isActuallyIndexToLocal) {
                                    null
                                }else {
                                    Libgit2Helper.resolveTree(repo, if(reverse) treeOid2Str else treeOid1Str)
                                }
                                MyLog.d(TAG, "treeOid1Str:$treeOid1Str, treeOid2Str:$treeOid2Str, reverse=$reverse")
                                Libgit2Helper.getSingleDiffItem(
                                    repo,
                                    relativePath,
                                    if(isActuallyIndexToLocal) Cons.gitDiffFromIndexToWorktree else fromTo,
                                    tree1,
                                    null,
                                    reverse=reverse,
                                    treeToWorkTree = true,
                                    maxSizeLimit = settings.diff.diffContentSizeMaxLimit,
                                    loadChannel = channelForThisJob,
                                    checkChannelLinesLimit = settings.diff.loadDiffContentCheckAbortSignalLines,
                                    checkChannelSizeLimit = settings.diff.loadDiffContentCheckAbortSignalSize,
                                    languageScope = languageScope,
                                )
                            }else { 
                                val tree1 = Libgit2Helper.resolveTree(repo, treeOid1Str)
                                val tree2 = Libgit2Helper.resolveTree(repo, treeOid2Str)
                                Libgit2Helper.getSingleDiffItem(
                                    repo,
                                    relativePath,
                                    fromTo,
                                    tree1,
                                    tree2,
                                    maxSizeLimit = settings.diff.diffContentSizeMaxLimit,
                                    loadChannel = channelForThisJob,
                                    checkChannelLinesLimit = settings.diff.loadDiffContentCheckAbortSignalLines,
                                    checkChannelSizeLimit = settings.diff.loadDiffContentCheckAbortSignalSize,
                                    languageScope = languageScope,
                                )
                            }
                            if(channelForThisJob.tryReceive().isClosed) {
                                return@launch
                            }
                            diffItemSaver
                        }else {  
                            val diffItemSaver = Libgit2Helper.getSingleDiffItem(
                                repo,
                                relativePath,
                                fromTo,
                                maxSizeLimit = settings.diff.diffContentSizeMaxLimit,
                                loadChannel = channelForThisJob,
                                checkChannelLinesLimit = settings.diff.loadDiffContentCheckAbortSignalLines,
                                checkChannelSizeLimit = settings.diff.loadDiffContentCheckAbortSignalSize,
                                languageScope = languageScope,
                            )
                            if(channelForThisJob.tryReceive().isClosed) {
                                return@launch
                            }
                            diffItemSaver
                        }
                        val submdirty = if(isDiffToLocal && isSubmodule) {
                            val submdirty = Libgit2Helper.submoduleIsDirty(parentRepo = repo, submoduleName = relativePath)
                            if(channelForThisJob.tryReceive().isClosed) {
                                return@launch
                            }
                            submdirty
                        }else {
                            false
                        }
                        diffItemSaver.startAnalyzeSyntaxHighlight(noMoreMemToaster)
                        item.copy(loading = false, submoduleIsDirty = submdirty, diffItemSaver = diffItemSaver)
                    }
                }catch (e:Exception) {
                    if(channelForThisJob.tryReceive().isClosed) {
                        return@launch
                    }
                    val errMsg = errorStrRes + ": " + e.localizedMessage
                    createAndInsertError(repoId, errMsg)
                    MyLog.e(TAG, "#LaunchedEffect err: "+e.stackTraceToString())
                    item.copy(loading = false, errMsg = errMsg)
                }
                val loading = loadedDiffableItem.loading
                val errMsg = loadedDiffableItem.errMsg
                val changeType = item.diffItemSaver.changeType
                val diffItem = loadedDiffableItem.diffItemSaver
                val submoduleIsDirty = loadedDiffableItem.submoduleIsDirty
                val isSupportedChangeType = (
                        changeType == Cons.gitStatusModified
                                || changeType == Cons.gitStatusUnmodified
                                || changeType == Cons.gitStatusNew
                                || changeType == Cons.gitStatusDeleted
                                || changeType == Cons.gitStatusTypechanged  
                        )
                val loadingFinishedButHasErr = (loading.not() && errMsg.isNotBlank())
                val unsupportedChangeType = !isSupportedChangeType
                val isBinary = diffItem?.flags?.contains(Diff.FlagT.BINARY) ?: false
                val fileNoChange = !(diffItem?.isFileModified ?: false)
                val isContentSizeOverLimit = diffItem?.isContentSizeOverLimit == true
                val noHunks = loadedDiffableItem.diffItemSaver.hunks.isEmpty();
                loadedDiffableItem.noDiffItemAvailable = loading || loadingFinishedButHasErr || unsupportedChangeType || isBinary || fileNoChange || isContentSizeOverLimit || noHunks;
                if (loadedDiffableItem.noDiffItemAvailable) {
                    loadedDiffableItem.whyNoDiffItem_msg = if (loading) {
                        activityContext.getString(R.string.loading)
                    } else if (loadingFinishedButHasErr) {
                        errMsg
                    } else if (unsupportedChangeType) {
                        activityContext.getString(R.string.unknown_change_type)
                    } else if (isBinary) {
                        activityContext.getString(R.string.doesnt_support_view_binary_file)
                    } else if (fileNoChange) {
                        if (isSubmodule && submoduleIsDirty) {  
                            activityContext.getString(R.string.submodule_is_dirty_note)
                        } else {
                            activityContext.getString(R.string.the_file_has_not_changed)
                        }
                    } else if (isContentSizeOverLimit) {
                        activityContext.getString(R.string.content_size_over_limit) + "(" + getHumanReadableSizeStr(settings.diff.diffContentSizeMaxLimit) + ")"
                    } else if(noHunks) {
                        activityContext.getString(R.string.file_is_empty)
                    } else "";
                    loadedDiffableItem.whyNoDiffItem = {
                        Row {
                            Text(
                                text = loadedDiffableItem.whyNoDiffItem_msg,
                                color = if(loadingFinishedButHasErr) MyStyleKt.TextColor.error() else Color.Unspecified,
                            )
                        }
                    }
                }
                diffableListLock.withLock {
                    diffableItemList.value[idx] = loadedDiffableItem
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            doJobThenOffLoading {
                diffableItemList.value.forEachBetter { it.closeLoadChannel() }
            }
        }
    }
}
@Composable
private fun getBackHandler(
    naviUp:()->Unit,
    adjustFontSizeMode: MutableState<Boolean>,
    adjustLineNumFontSizeMode: MutableState<Boolean>,
    saveFontSizeAndQuitAdjust:()->Unit,
    saveLineNumFontSizeAndQuitAdjust:()->Unit,
): () -> Unit {
    val backHandlerOnBack = {
        if(adjustFontSizeMode.value) {
            saveFontSizeAndQuitAdjust()
        }else if(adjustLineNumFontSizeMode.value) {
            saveLineNumFontSizeAndQuitAdjust()
        }else {
            naviUp()
        }
        Unit
    }
    return backHandlerOnBack
}
private val headIconWidth = MyStyleKt.trailIconSize
@Composable
private fun NaviButton(
    isMultiMode: Boolean,
    fromScreen: DiffFromScreen,
    stateKeyTag:String,
    fromTo: String,
    diffableItemList: MutableList<DiffableItem>,
    curItemIndex: MutableIntState,
    lastClickedItemKey: MutableState<String>,
    pageRequest:MutableState<String>,
    stageItem:suspend (List<StatusTypeEntrySaver>)->Unit,
    initRevertDialog:(items:List<StatusTypeEntrySaver>, callback:suspend ()->Unit)->Unit,
    initUnstageDialog:(items:List<StatusTypeEntrySaver>, callback:suspend ()->Unit)->Unit,
    naviUp:()->Unit,
    switchItem: (oldItem: DiffableItem?, newItem:DiffableItem, newItemIndex: Int, isToNext:Boolean) -> Unit,
    goToTop:()->Unit,
) {
    val isFileHistoryTreeToLocalOrTree = fromTo == Cons.gitDiffFileHistoryFromTreeToLocal || fromTo == Cons.gitDiffFileHistoryFromTreeToPrev
    val size = diffableItemList.size
    val previousIndex = curItemIndex.intValue - 1
    val nextIndex = curItemIndex.intValue + 1
    val hasPrevious = previousIndex >= 0 && previousIndex < size
    val hasNext = if(fromTo != Cons.gitDiffFileHistoryFromTreeToPrev) isGoodIndex(nextIndex, size) else isGoodIndex(nextIndex + 1, size);
    val noneText = Pair(stringResource(R.string.none), "")
    fun getItemTextByIdx(idx:Int):Pair<String, String> {
        return diffableItemList.getOrNull(idx)?.let {
            if(isFileHistoryTreeToLocalOrTree) {
                Pair(it.shortCommitId, it.oneLineCommitMsgOfCommitOid())
            } else {
                Pair(it.fileName, it.fileParentPathOfRelativePath)
            }
        } ?: noneText
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(isFileHistoryTreeToLocalOrTree) {
            SingleLineCardButton(
                text = stringResource(R.string.restore),
                enabled = true
            ) {
                pageRequest.value = PageRequest.showRestoreDialog
            }
            Spacer(Modifier.height(20.dp))
        }
        if(size > 0) {
            val getCurItem = {
                val targetItem = diffableItemList.getOrNull(curItemIndex.intValue)
                if(targetItem == null) {
                    Msg.requireShowLongDuration("err: bad index ${curItemIndex.intValue}")
                }
                targetItem
            }
            val doActThenSwitchItem:suspend (targetIndex:Int, targetItem: DiffableItem?, act:suspend ()->Unit)->Unit = { targetIndex, targetItem, act ->
                act()
                val nextOrPreviousIndex = if(hasNext) (nextIndex - 1) else previousIndex
                if(nextOrPreviousIndex >= 0 && nextOrPreviousIndex < diffableItemList.size) {  
                    val nextOrPrevious = diffableItemList[nextOrPreviousIndex]
                    lastClickedItemKey.value = nextOrPrevious.getItemKey()
                    switchItem(targetItem, nextOrPrevious, nextOrPreviousIndex, hasNext)
                }
            }
            val targetChangeListItemState = mutableCustomStateOf(stateKeyTag, "targetChangeListItemState") { StatusTypeEntrySaver() }
            val targetIndexState = rememberSaveable { mutableIntStateOf(-1) }
            if(fromTo == Cons.gitDiffFromIndexToWorktree) {
                SingleLineCardButton(
                    text = stringResource(R.string.stage),
                    enabled = true
                ) onClick@{
                    val targetIndex = curItemIndex.intValue
                    val targetItem = getCurItem() ?: return@onClick
                    doJobThenOffLoading {
                        if(isMultiMode) {
                            stageItem(diffableItemList.map { it.toChangeListItem() })
                        }else {
                            doActThenSwitchItem(targetIndex, targetItem) {
                                stageItem(listOf(targetItem.toChangeListItem()))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                SingleLineCardButton(
                    text = stringResource(R.string.revert),
                    enabled = true
                ) onClick@{
                    val targetIndex = curItemIndex.intValue
                    val targetItem = getCurItem() ?: return@onClick
                    targetChangeListItemState.value = targetItem.toChangeListItem()
                    targetIndexState.intValue = targetIndex
                    if(isMultiMode) {
                        initRevertDialog(diffableItemList.map { it.toChangeListItem() }) {}
                    }else {
                        initRevertDialog(listOf(targetChangeListItemState.value)) {
                            doActThenSwitchItem(targetIndexState.intValue, targetItem) {}
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }else if(fromTo == Cons.gitDiffFromHeadToIndex) {  
                SingleLineCardButton(
                    text = stringResource(R.string.unstage),
                    enabled = true
                ) onClick@{
                    val targetIndex = curItemIndex.intValue
                    val targetItem = getCurItem() ?: return@onClick
                    targetChangeListItemState.value = targetItem.toChangeListItem()
                    targetIndexState.intValue = targetIndex
                    if(isMultiMode) {
                        initUnstageDialog(diffableItemList.map { it.toChangeListItem() }) {}
                    }else {
                        initUnstageDialog(listOf(targetChangeListItemState.value)) {
                            doActThenSwitchItem(targetIndexState.intValue, targetItem) {}
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
            if(fromTo == Cons.gitDiffFromIndexToWorktree || fromTo == Cons.gitDiffFromHeadToIndex) {
                val (state, requestType) = if(fromTo == Cons.gitDiffFromIndexToWorktree) {
                    Pair(SharedState.homeChangeList_Refresh, StateRequestType.indexToWorkTree_CommitAll)
                } else {
                    Pair(SharedState.indexChangeList_Refresh, StateRequestType.headToIndex_CommitAll)
                }
                SingleLineCardButton(
                    text = stringResource(R.string.commit),
                    enabled = true
                ) {
                    changeStateTriggerRefreshPage(state, requestType)
                    naviUp()
                }
                Spacer(Modifier.height(20.dp))
            }
            Spacer(Modifier.height(20.dp))
            if(isMultiMode.not()) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        replaceStringResList(stringResource(R.string.current_n_all_m), listOf("" + (curItemIndex.intValue+1), "" + size)),
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        color = UIHelper.getSecondaryFontColor()
                    )
                }
                TwoLineTextCardButton(
                    enabled = hasPrevious,
                    textPair = if(hasPrevious) getItemTextByIdx(previousIndex) else noneText,
                    headIcon = Icons.Filled.KeyboardArrowUp,
                    headIconWidth = headIconWidth,
                    headIconDesc = "Previous",
                ) {
                    val item = diffableItemList[previousIndex]
                    lastClickedItemKey.value = item.getItemKey()
                    switchItem(getCurItem(), item, previousIndex, false)
                }
                Spacer(Modifier.height(10.dp))
                TwoLineTextCardButton(
                    enabled = true,
                    textPair = getItemTextByIdx(curItemIndex.intValue),
                    headIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    headIconWidth = headIconWidth,
                    headIconDesc = "Current",
                ) {
                    goToTop()
                }
                Spacer(Modifier.height(10.dp))
                TwoLineTextCardButton(
                    enabled = hasNext,
                    textPair = if(hasNext) getItemTextByIdx(nextIndex) else noneText,
                    headIcon = Icons.Filled.KeyboardArrowDown,
                    headIconWidth = headIconWidth,
                    headIconDesc = "Next",
                ) {
                    val item = diffableItemList[nextIndex]
                    lastClickedItemKey.value = item.getItemKey()
                    switchItem(getCurItem(), item, nextIndex, true)
                }
            }
        }
    }
}
@Composable
private fun HunkDivider() {
    MyHorizontalDivider(
        modifier = Modifier.padding(vertical = 30.dp),
        thickness = 3.dp
    )
}
