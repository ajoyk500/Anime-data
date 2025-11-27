package com.akcreation.gitsilent.screen.content.editor

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.MyLazyVerticalStaggeredGrid
import com.akcreation.gitsilent.dto.FileDetail
import com.akcreation.gitsilent.screen.content.listitem.FileDetailItem
import com.akcreation.gitsilent.screen.functions.filterModeActuallyEnabled
import com.akcreation.gitsilent.screen.functions.filterTheList
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.RegexUtil
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import com.akcreation.gitsilent.utils.state.CustomStateSaveable

private const val itemWidth = 150
private const val itemMargin = 10
private val itemMarginDp = itemMargin.dp
private const val oneItemRequiredMargin = itemMargin*2
private const val oneItemRequiredWidth = (itemWidth + oneItemRequiredMargin)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FileDetailList(
    contentPadding: PaddingValues,
    state: LazyStaggeredGridState,
    list:List<FileDetail>,
    filterListState: LazyStaggeredGridState,
    filterList: MutableList<FileDetail>,
    filterOn: MutableState<Boolean>,  
    enableFilterState: MutableState<Boolean>,  
    filterKeyword: CustomStateSaveable<TextFieldValue>,
    lastSearchKeyword: MutableState<String>,
    filterResultNeedRefresh: MutableState<String>,
    searching: MutableState<Boolean>,
    searchToken: MutableState<String>,
    resetSearchVars: ()->Unit,
    onClick:(FileDetail)->Unit,
    itemOnLongClick:(idx:Int, FileDetail)->Unit,
    isItemSelected: (FileDetail) -> Boolean,
) {
    val activityContext = LocalContext.current
    val configuration = AppModel.getCurActivityConfig()
    val screenWidthDp = configuration.screenWidthDp
    val width = remember(configuration.screenWidthDp) {
        val width = if(screenWidthDp < oneItemRequiredWidth) {
            (screenWidthDp - oneItemRequiredMargin).coerceAtLeast(screenWidthDp)
        }else {  
            itemWidth
        }
        width.dp
    }
    val keyword = filterKeyword.value.text  
    val enableFilter = filterModeActuallyEnabled(filterOn.value, keyword)
    val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
    val filteredList = filterTheList(
        needRefresh = filterResultNeedRefresh.value,
        lastNeedRefresh = lastNeedRefresh,
        enableFilter = enableFilter,
        keyword = keyword,
        lastKeyword = lastSearchKeyword,
        searching = searching,
        token = searchToken,
        activityContext = activityContext,
        filterList = filterList,
        list = list,
        resetSearchVars = resetSearchVars,
        match = { idx:Int, it: FileDetail ->
            it.file.name.let {
                it.contains(keyword, ignoreCase = true) || RegexUtil.matchWildcard(it, keyword)
            } || it.file.path.ioPath.contains(keyword, ignoreCase = true)
                    || it.cachedAppRelatedPath().contains(keyword, ignoreCase = true)
                    || it.shortContent.contains(keyword, ignoreCase = true)
        }
    )
    val listState = if(enableFilter) filterListState else state
    enableFilterState.value = enableFilter
    MyLazyVerticalStaggeredGrid(
        contentPadding = contentPadding,
        itemMinWidth = width,
        state = listState,
    ) {
        filteredList.forEachIndexedBetter { idx, it ->
            item {
                FileDetailItem(
                    width = width,
                    margin = itemMarginDp,
                    idx = idx,
                    item = it,
                    onLongClick = itemOnLongClick,
                    onClick = onClick,
                    selected = isItemSelected(it)
                )
            }
        }
    }
}
