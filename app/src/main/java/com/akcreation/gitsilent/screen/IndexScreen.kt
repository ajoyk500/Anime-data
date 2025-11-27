package com.akcreation.gitsilent.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.GoToTopAndGoToBottomFab
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.ChangeListInnerPage
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions.ChangeListPageActions
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.IndexScreenTitle
import com.akcreation.gitsilent.screen.functions.ChangeListFunctions
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository

private const val TAG = "IndexScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexScreen(
    naviUp: () -> Unit
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val navController = AppModel.navController
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val allRepoParentDir = AppModel.allRepoParentDir
    val scope = rememberCoroutineScope()
    val settings = remember {SettingsUtil.getSettingsSnapshot()}
    val changeListRefreshRequiredByParentPage = rememberSaveable { SharedState.indexChangeList_Refresh }
    val changeListRequireRefreshFromParentPage = { whichRepoRequestRefresh:RepoEntity ->
        ChangeListFunctions.changeListDoRefresh(changeListRefreshRequiredByParentPage, whichRepoRequestRefresh)
    }
    val changeListCurRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "changeListCurRepo", initValue = RepoEntity(id=""))
    val changeListIsShowRepoList = rememberSaveable { mutableStateOf(false)}
    val changeListPageHasIndexItem = rememberSaveable { mutableStateOf(false)}
    val changeListShowRepoList = {
        changeListIsShowRepoList.value = true
    }
    val changeListIsFileSelectionMode = rememberSaveable { mutableStateOf( false)}
    val changeListPageNoRepo = rememberSaveable { mutableStateOf( false)}
    val changeListPageHasNoConflictItems = rememberSaveable { mutableStateOf(false)}
    val changeListPageRebaseCurOfAll = rememberSaveable { mutableStateOf( "")}
    val changeListNaviTarget = rememberSaveable { mutableStateOf(Cons.ChangeListNaviTarget_InitValue)}
    val changeListPageFilterKeyWord = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "changeListPageFilterKeyWord",
        initValue = TextFieldValue("")
    )
    val changeListPageFilterModeOn = rememberSaveable { mutableStateOf(false)}
    val changeListLastSearchKeyword = rememberSaveable { mutableStateOf("") }
    val changeListSearchToken = rememberSaveable { mutableStateOf("") }
    val changeListSearching = rememberSaveable { mutableStateOf(false) }
    val resetChangeListSearchVars = {
        changeListSearching.value = false
        changeListSearchToken.value = ""
        changeListLastSearchKeyword.value = ""
    }
    val changelistFilterListState = rememberLazyListState()
    val swap =rememberSaveable { mutableStateOf(false)}
    val changeListErrScrollState = rememberScrollState()
    val changeListHasErr = rememberSaveable { mutableStateOf(false) }
    val changeListErrLastPosition = rememberSaveable { mutableStateOf(0) }
    val requireDoActFromParent = rememberSaveable { mutableStateOf(false)}
    val requireDoActFromParentShowTextWhenDoingAct = rememberSaveable { mutableStateOf("")}
    val enableAction = rememberSaveable { mutableStateOf(true)}
    val repoState = rememberSaveable{mutableIntStateOf(Repository.StateT.NONE.bit)}  
    val fromTo = Cons.gitDiffFromHeadToIndex
    val changeListPageItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListPageItemListState = rememberLazyListState()
    val changeListPageSelectedItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageSelectedItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changelistPageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val changelistNewestPageId = rememberSaveable { mutableStateOf("") }
    val changeListPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val changeListFilterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListFilterList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListLastClickedItemKey = rememberSaveable{ SharedState.index_LastClickedItemKey }
    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(changeListPageFilterModeOn.value) {
                        FilterTextField(filterKeyWord = changeListPageFilterKeyWord, loading = changeListSearching.value)
                    }else {
                        IndexScreenTitle(changeListCurRepo, repoState, scope, changeListPageItemListState, lastPosition)
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
                    if(!changeListPageFilterModeOn.value) {
                        ChangeListPageActions(
                            changeListCurRepo,
                            changeListRequireRefreshFromParentPage,
                            changeListPageHasIndexItem,
                            requireDoActFromParent,
                            requireDoActFromParentShowTextWhenDoingAct,
                            enableAction,
                            repoState,
                            fromTo,
                            changeListPageItemListState,
                            scope,
                            changeListPageNoRepo=changeListPageNoRepo,
                            hasNoConflictItems = changeListPageHasNoConflictItems.value,
                            changeListPageFilterModeOn=changeListPageFilterModeOn,
                            changeListPageFilterKeyWord=changeListPageFilterKeyWord,
                            rebaseCurOfAll = changeListPageRebaseCurOfAll.value,
                            naviTarget = changeListNaviTarget
                        )
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
            errScrollState = changeListErrScrollState,
            hasError = changeListHasErr,
            commit1OidStr = Cons.git_HeadCommitHash,
            commit2OidStr = Cons.git_IndexCommitHash,
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
            changeListPageNoRepo=changeListPageNoRepo,
            hasNoConflictItems = changeListPageHasNoConflictItems,
            changelistPageScrolled=changelistPageScrolled,
            changeListPageFilterModeOn= changeListPageFilterModeOn,
            changeListPageFilterKeyWord=changeListPageFilterKeyWord,
            filterListState = changelistFilterListState,
            swap=swap.value,
            commitForQueryParents = "",
            rebaseCurOfAll = changeListPageRebaseCurOfAll,
            openDrawer = {}, 
            newestPageId = changelistNewestPageId,
            naviTarget = changeListNaviTarget,
            enableFilterState = changeListPageEnableFilterState,
            filterList = changeListFilterList,
            lastClickedItemKey = changeListLastClickedItemKey
        )
    }
}
