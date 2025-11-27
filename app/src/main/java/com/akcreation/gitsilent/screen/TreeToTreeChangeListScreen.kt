package com.akcreation.gitsilent.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.compose.CompareInfo
import com.akcreation.gitsilent.compose.DropDownMenuItemText
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.InDialogTitle
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.RepoInfoDialogItemSpacer
import com.akcreation.gitsilent.compose.TitleDropDownMenu
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.commitsTreeToTreeDiffReverseTestPassed
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.git.CommitDto
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.ChangeListInnerPage
import com.akcreation.gitsilent.screen.functions.ChangeListFunctions
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.cache.NaviCache
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository

private const val TAG = "TreeToTreeChangeListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TreeToTreeChangeListScreen(
    repoId:String,
    commit1OidStrCacheKey:String,  
    commit2OidStrCacheKey:String,  
    commitForQueryParentsCacheKey:String,  
    titleCacheKey:String,
    naviUp: () -> Unit
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val commit1OidStrState = rememberSaveable(commit1OidStrCacheKey) { mutableStateOf((NaviCache.getByType<String>(commit1OidStrCacheKey) ?:"").ifBlank { Cons.git_AllZeroOidStr }) }
    val commit2OidStr = rememberSaveable(commit2OidStrCacheKey) { (NaviCache.getByType<String>(commit2OidStrCacheKey) ?:"").ifBlank { Cons.git_AllZeroOidStr } }
    val commitForQueryParents = rememberSaveable(commitForQueryParentsCacheKey){ NaviCache.getByType<String>(commitForQueryParentsCacheKey) ?:"" }
    val commitParentList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "commitParentList",
        initValue = listOf<String>()
    )
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val navController = AppModel.navController
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val allRepoParentDir = AppModel.allRepoParentDir
    val activityContext = LocalContext.current
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val titleDesc = rememberSaveable { mutableStateOf(NaviCache.getByType<String>(titleCacheKey) ?: "") }
    val changeListRefreshRequiredByParentPage = rememberSaveable { mutableStateOf("TreeToTree_ChangeList_refresh_init_value_63wk") }
    val changeListRequireRefreshFromParentPage = { whichRepoRequestRefresh:RepoEntity ->
        ChangeListFunctions.changeListDoRefresh(changeListRefreshRequiredByParentPage, whichRepoRequestRefresh)
    }
    val changeListCurRepo = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "changeListCurRepo",
        initValue = RepoEntity(id="")
    )
    val changeListIsShowRepoList = rememberSaveable { mutableStateOf(false) }
    val changeListPageHasIndexItem = rememberSaveable { mutableStateOf(false) }
    val changeListShowRepoList = {
        changeListIsShowRepoList.value = true
    }
    val changeListIsFileSelectionMode = rememberSaveable { mutableStateOf(false) }
    val changeListPageNoRepo = rememberSaveable { mutableStateOf(false) }
    val changeListPageHasNoConflictItems = rememberSaveable { mutableStateOf(false) }
    val swap = rememberSaveable { mutableStateOf(false) }
    val requireDoActFromParent = rememberSaveable { mutableStateOf(false) }
    val requireDoActFromParentShowTextWhenDoingAct = rememberSaveable { mutableStateOf("") }
    val enableAction = rememberSaveable { mutableStateOf(true) }
    val repoState = rememberSaveable{mutableIntStateOf(Repository.StateT.NONE.bit)}  
    val fromTo = Cons.gitDiffFromTreeToTree
    val changeListPageItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListPageItemListState = rememberLazyListState()
    val changeListPageSelectedItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageSelectedItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changelistPageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val changelistNewestPageId = rememberSaveable { mutableStateOf("") }
    val changeListNaviTarget = rememberSaveable { mutableStateOf(Cons.ChangeListNaviTarget_InitValue)}
    val changeListPageFilterKeyWord = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "changeListPageFilterKeyWord",
        initValue = TextFieldValue("")
    )
    val changeListPageFilterModeOn = rememberSaveable { mutableStateOf(false) }
    val changelistFilterListState = rememberLazyListState()
    val showParentListDropDownMenu = rememberSaveable { mutableStateOf(false) }
    val showInfoDialog = rememberSaveable { mutableStateOf(false) }
    val actuallyLeftName = rememberSaveable { mutableStateOf("") }
    val actuallyRightName = rememberSaveable { mutableStateOf("") }
    val actuallyLeftCommitDto = mutableCustomStateOf(stateKeyTag, "actuallyLeftCommitDto") { CommitDto() }
    val actuallyRightCommitDto = mutableCustomStateOf(stateKeyTag, "actuallyRightCommitDto") { CommitDto() }
    val initInfoDialog = {
        runCatching {
            val curRepo = changeListCurRepo.value
            val repoId = curRepo.id
            val actuallyLeftCommit = if(swap.value) commit2OidStr else commit1OidStrState.value
            val actuallyRightCommit = if(swap.value) commit1OidStrState.value else commit2OidStr
            actuallyLeftName.value = actuallyLeftCommit
            actuallyRightName.value = actuallyRightCommit
            Repository.open(curRepo.fullSavePath).use { repo->
                val (left, right) = Libgit2Helper.getLeftRightCommitDto(repo, actuallyLeftCommit, actuallyRightCommit, repoId, settings)
                actuallyLeftCommitDto.value = left
                actuallyRightCommitDto.value = right
            }
        }
        showInfoDialog.value = true
    }
    if(showInfoDialog.value) {
        RepoInfoDialog(changeListCurRepo.value, showInfoDialog, prependContent = {
            Row {
                InDialogTitle(titleDesc.value)
            }
            RepoInfoDialogItemSpacer()
            CompareInfo(
                leftName = actuallyLeftName.value,
                leftCommitDto = actuallyLeftCommitDto.value,
                rightName = actuallyRightName.value,
                rightCommitDto = actuallyRightCommitDto.value,
            )
            RepoInfoDialogItemSpacer()
            MyHorizontalDivider()
            RepoInfoDialogItemSpacer()
            Row {
                InDialogTitle(stringResource(R.string.repo))
            }
        })
    }
    val changeListPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val changeListFilterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListFilterList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListLastClickedItemKey = rememberSaveable{ SharedState.treeToTree_LastClickedItemKey }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    val changeListLastSearchKeyword = rememberSaveable { mutableStateOf("") }
    val changeListSearchToken = rememberSaveable { mutableStateOf("") }
    val changeListSearching = rememberSaveable { mutableStateOf(false) }
    val resetChangeListSearchVars = {
        changeListSearching.value = false
        changeListSearchToken.value = ""
        changeListLastSearchKeyword.value = ""
    }
    val changeListErrScrollState = rememberScrollState()
    val changeListHasErr = rememberSaveable { mutableStateOf(false) }
    val changeListErrLastPosition = rememberSaveable { mutableStateOf(0) }
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(changeListPageFilterModeOn.value) {
                        FilterTextField(filterKeyWord = changeListPageFilterKeyWord, loading = changeListSearching.value)
                    }else {
                        val titleText = Libgit2Helper.getLeftToRightDiffCommitsText(commit1OidStrState.value, commit2OidStr, swap.value)
                        val titleSecondLineText = "[${changeListCurRepo.value.repoName}]"
                        val expandable = Libgit2Helper.CommitUtil.mayGoodCommitHash(commitForQueryParents)
                        TitleDropDownMenu(
                            dropDownMenuExpandState = showParentListDropDownMenu,
                            curSelectedItem = commit1OidStrState.value,
                            itemList = commitParentList.value.toList(),
                            titleClickEnabled = true,
                            switchDropDownMenuShowHide = { showParentListDropDownMenu.apply { value = !value } },
                            titleFirstLine = {
                                Text(
                                    text = titleText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.firstLineFontSize,
                                )
                            },
                            titleSecondLine = {
                                Text(
                                    text = titleSecondLineText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.secondLineFontSize,
                                )
                            },
                            titleRightIcon = {
                                Icon(
                                    imageVector = if (showParentListDropDownMenu.value) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft,
                                    contentDescription = stringResource(R.string.switch_item),
                                )
                            },
                            isItemSelected = { it == commit1OidStrState.value },
                            menuItem = { it, selected ->
                                DropDownMenuItemText(
                                    text1 = Libgit2Helper.getShortOidStrByFull(it),
                                )
                            },
                            titleOnLongClick = { initInfoDialog() },
                            itemOnClick = {
                                showParentListDropDownMenu.value=false
                                val curRepo = changeListCurRepo.value
                                if(commit1OidStrState.value != it) {
                                    changeListIsFileSelectionMode.value=false  
                                    changeListPageSelectedItemList.value.clear() 
                                }
                                commit1OidStrState.value=it
                                changeListRequireRefreshFromParentPage(curRepo)
                            },
                            titleOnClick = {
                                if (expandable) {
                                    showParentListDropDownMenu.value = true
                                }
                            },
                            showExpandIcon = expandable
                        )
                    }
                },
                navigationIcon = {
                    if(changeListPageFilterModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),
                        ) {
                            resetChangeListSearchVars()
                            changeListPageFilterModeOn.value = false
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
                    if(!changeListPageFilterModeOn.value) {
                        Row {
                            LongPressAbleIconBtn(
                                enabled = enableAction.value && !changeListPageNoRepo.value,
                                tooltipText = stringResource(R.string.filter),
                                icon =  Icons.Filled.FilterAlt,
                                iconContentDesc = stringResource(id = R.string.filter),
                            ) {
                                changeListPageFilterKeyWord.value=TextFieldValue("")
                                changeListPageFilterModeOn.value = true
                            }
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.refresh),
                                icon = Icons.Filled.Refresh,
                                iconContentDesc = stringResource(R.string.refresh),
                            ) {
                                changeListRequireRefreshFromParentPage(changeListCurRepo.value)
                            }
                            if(UserUtil.isPro() && (dev_EnableUnTestedFeature || commitsTreeToTreeDiffReverseTestPassed)) {
                                LongPressAbleIconBtn(
                                    tooltipText = stringResource(R.string.swap_commits),
                                    icon = Icons.Filled.SwapHoriz,
                                    iconContentDesc = stringResource(R.string.swap_commits),
                                    iconColor = UIHelper.getIconEnableColorOrNull(swap.value)
                                ) {
                                    swap.value = !swap.value
                                    Msg.requireShow(activityContext.getString(if (swap.value) R.string.swap_commits_on else R.string.swap_commits_off))
                                    val curRepo = changeListCurRepo.value
                                    changeListRequireRefreshFromParentPage(curRepo)
                                }
                            }
                        }
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(changelistPageScrolled.value) {
                if(changeListHasErr.value) {
                    GoToTopAndGoToBottomFab(
                        scope = scope,
                        listState = changeListErrScrollState,
                        listLastPosition = changeListErrLastPosition,
                        showFab = changelistPageScrolled
                    )
                }else {
                    GoToTopAndGoToBottomFab(
                        filterModeOn = changeListPageEnableFilterState.value,
                        scope = scope,
                        filterListState = changelistFilterListState,
                        listState = changeListPageItemListState,
                        filterListLastPosition = filterLastPosition,
                        listLastPosition = lastPosition,
                        showFab = changelistPageScrolled
                    )
                }
            }
        }
    ) { contentPadding ->
        ChangeListInnerPage(
            stateKeyTag = stateKeyTag,
            errScrollState= changeListErrScrollState,
            hasError = changeListHasErr,
            lastSearchKeyword=changeListLastSearchKeyword,
            searchToken=changeListSearchToken,
            searching=changeListSearching,
            resetSearchVars=resetChangeListSearchVars,
            contentPadding = contentPadding,
            fromTo = fromTo,
            curRepoFromParentPage = changeListCurRepo,
            isFileSelectionMode = changeListIsFileSelectionMode,
            refreshRequiredByParentPage = changeListRefreshRequiredByParentPage.value,
            changeListRequireRefreshFromParentPage = changeListRequireRefreshFromParentPage,
            changeListPageHasIndexItem = changeListPageHasIndexItem,
            requireDoActFromParent = requireDoActFromParent,
            requireDoActFromParentShowTextWhenDoingAct = requireDoActFromParentShowTextWhenDoingAct,
            enableActionFromParent = enableAction,
            repoState = repoState,
            naviUp=naviUp,
            itemList = changeListPageItemList,
            itemListState = changeListPageItemListState,
            selectedItemList = changeListPageSelectedItemList,
            commit1OidStr=commit1OidStrState.value,
            commit2OidStr=commit2OidStr,
            commitParentList = commitParentList.value,
            repoId=repoId,
            changeListPageNoRepo=changeListPageNoRepo,
            hasNoConflictItems = changeListPageHasNoConflictItems,  
            changelistPageScrolled=changelistPageScrolled,
            changeListPageFilterModeOn= changeListPageFilterModeOn,
            changeListPageFilterKeyWord=changeListPageFilterKeyWord,
            filterListState = changelistFilterListState,
            swap=swap.value,
            commitForQueryParents = commitForQueryParents,
            openDrawer = {}, 
            newestPageId = changelistNewestPageId,
            naviTarget = changeListNaviTarget,
            enableFilterState = changeListPageEnableFilterState,
            filterList = changeListFilterList,
            lastClickedItemKey = changeListLastClickedItemKey
        )
    }
}
