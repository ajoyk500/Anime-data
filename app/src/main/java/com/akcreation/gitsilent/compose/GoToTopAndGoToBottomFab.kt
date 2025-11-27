package com.akcreation.gitsilent.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.UIHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GoToTopAndGoToBottomFab(
    filterModeOn: Boolean,
    scope: CoroutineScope,
    filterListState: LazyStaggeredGridState,
    listState: LazyStaggeredGridState,
    filterListLastPosition: MutableState<Int>,
    listLastPosition: MutableState<Int>,
    showFab: MutableState<Boolean>,
    listSize:Int,
) {
    val goToTop = {UIHelper.switchBetweenTopAndLastVisiblePosition(scope, listState, listLastPosition)}
    val goToTopFiltered = {UIHelper.switchBetweenTopAndLastVisiblePosition(scope, filterListState, filterListLastPosition)}
    val goToBottom = {UIHelper.scrollToItem(scope, listState, listSize)}
    val goToBottomFiltered = {UIHelper.scrollToItem(scope, filterListState, listSize)}
    val hideButton = getHideButton(showFab, scope)
    GoToTopAndGoToBottomFab_Internal(
        filterModeOn = filterModeOn,
        scrollToTop = goToTop,
        scrollToTopForFilterState = goToTopFiltered,
        scrollToBottom = goToBottom,
        scrollToBottomForFilterState = goToBottomFiltered,
        hideButton = hideButton
    )
}
@Composable
fun GoToTopAndGoToBottomFab(
    filterModeOn: Boolean,
    scope: CoroutineScope,
    filterListState: LazyListState,
    listState: LazyListState,
    filterListLastPosition: MutableState<Int>,
    listLastPosition: MutableState<Int>,
    showFab: MutableState<Boolean>
) {
    val goToTop = {UIHelper.switchBetweenTopAndLastVisiblePosition(scope, listState, listLastPosition)}
    val goToTopFiltered = {UIHelper.switchBetweenTopAndLastVisiblePosition(scope, filterListState, filterListLastPosition)}
    val goToBottom = {UIHelper.scrollToItem(scope, listState, Int.MAX_VALUE)}
    val goToBottomFiltered = {UIHelper.scrollToItem(scope, filterListState, Int.MAX_VALUE)}
    val hideButton = getHideButton(showFab, scope)
    GoToTopAndGoToBottomFab_Internal(
        filterModeOn = filterModeOn,
        scrollToTop = goToTop,
        scrollToTopForFilterState = goToTopFiltered,
        scrollToBottom = goToBottom,
        scrollToBottomForFilterState = goToBottomFiltered,
        hideButton = hideButton
    )
}
@Composable
fun GoToTopAndGoToBottomFab(
    scope: CoroutineScope,
    listState: LazyListState,
    listLastPosition: MutableState<Int>,
    showFab: MutableState<Boolean>
) {
    val goToTop = {UIHelper.switchBetweenTopAndLastVisiblePosition(scope, listState, listLastPosition)}
    val goToBottom = {UIHelper.scrollToItem(scope, listState, Int.MAX_VALUE)}
    val hideButton = getHideButton(showFab, scope)
    GoToTopAndGoToBottomFab_Internal(
        filterModeOn = false,
        scrollToTop = goToTop,
        scrollToTopForFilterState = {},
        scrollToBottom = goToBottom,
        scrollToBottomForFilterState = {},
        hideButton = hideButton
    )
}
@Composable
fun GoToTopAndGoToBottomFab(
    scope: CoroutineScope,
    listState: ScrollState,
    listLastPosition: MutableState<Int>,
    showFab: MutableState<Boolean>
) {
    val goToTop = {UIHelper.switchBetweenTopAndLastVisiblePosition(scope, listState, listLastPosition)}
    val goToBottom = {UIHelper.scrollTo(scope, listState, Int.MAX_VALUE)}
    val hideButton = getHideButton(showFab, scope)
    GoToTopAndGoToBottomFab_Internal(
        filterModeOn = false,
        scrollToTop = goToTop,
        scrollToTopForFilterState = {},
        scrollToBottom = goToBottom,
        scrollToBottomForFilterState = {},
        hideButton = hideButton
    )
}
@Composable
private fun GoToTopAndGoToBottomFab_Internal(
    filterModeOn: Boolean,
    scrollToTop:()->Unit,
    scrollToTopForFilterState:()->Unit,
    scrollToBottom:()->Unit,
    scrollToBottomForFilterState:()->Unit,
    hideButton:()->Unit,
) {
    val configuration = AppModel.getCurActivityConfig()
    Column(modifier = MyStyleKt.Fab.getFabModifier(UIHelper.isPortrait(configuration), UIHelper.getDeviceWidthHeightInDp(configuration))) {
        SmallFab(
            icon = Icons.Filled.VerticalAlignTop, iconDesc = stringResource(id = R.string.go_to_top)
        ) {
            if (filterModeOn) {
                scrollToTopForFilterState()
            } else {
                scrollToTop()
            }
        }
        SmallFab(
            icon = Icons.Filled.HideSource, iconDesc = stringResource(id = R.string.hide)
        ) {
            hideButton()
        }
        SmallFab(
            icon = Icons.Filled.VerticalAlignBottom, iconDesc = stringResource(id = R.string.go_to_bottom)
        ) {
            if (filterModeOn) {
                scrollToBottomForFilterState()
            } else {
                scrollToBottom()
            }
        }
    }
}
private fun getHideButton(showButton:MutableState<Boolean>, scope: CoroutineScope) :()->Unit {
    return {
        showButton.value = false
        scope.launch {
            runCatching {
                delay(10_000) 
                showButton.value = true
            }
        }
    }
}
