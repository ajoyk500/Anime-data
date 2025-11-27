package com.akcreation.gitsilent.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import kotlinx.coroutines.delay

private const val pinnedIconsCount = 1
@Composable
fun BottomBar(
    modifier: Modifier=Modifier,
    showClose: Boolean = true,
    showSelectedCount: Boolean = true,
    height: Dp = MyStyleKt.BottomBar.height,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    quitSelectionMode: () -> Unit,
    iconList:List<ImageVector>,
    iconTextList:List<String>,
    iconDescTextList:List<String>,
    iconOnClickList:List<()->Unit>,
    iconEnableList:List<()->Boolean>,
    iconVisibleList:List<()->Boolean> = listOf(),  
    getSelectedFilesCount:()->Int,
    countNumOnClickEnabled:Boolean=false,
    countNumOnClick:()->Unit={},
    moreItemTextList:List<String>,
    visibleMoreIcon:Boolean = moreItemTextList.isNotEmpty(),
    enableMoreIcon:Boolean = visibleMoreIcon && getSelectedFilesCount() > 0,
    moreItemOnClickList:List<()->Unit>,
    moreItemEnableList:List<()->Boolean>,
    moreItemVisibleList:List<()->Boolean> = moreItemEnableList,  
    reverseMoreItemList:Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val showDropDownMenu = {
        dropDownMenuExpandState.value=true
    }
    val closeDropDownMenu = {
        dropDownMenuExpandState.value=false
    }
    val switchDropDownMenu = {
        dropDownMenuExpandState.value = !dropDownMenuExpandState.value
    }
    var moreItemTextList = moreItemTextList
    var moreItemOnClickList = moreItemOnClickList
    var moreItemEnableList = moreItemEnableList
    var moreItemVisibleList = moreItemVisibleList
    if(enableMoreIcon && reverseMoreItemList) {
        moreItemTextList = moreItemTextList.asReversed()
        moreItemOnClickList = moreItemOnClickList.asReversed()
        moreItemEnableList = moreItemEnableList.asReversed()
        moreItemVisibleList = moreItemVisibleList.asReversed()
    }
    val isIconHidden = { idx:Int ->
        iconVisibleList.isNotEmpty() && !iconVisibleList[idx]()
    }
    val pinnedIconList = mutableListOf<Int>()
    if(pinnedIconsCount > 0 && iconTextList.isNotEmpty()) {
        for(idx in IntRange(0, iconTextList.size-1).reversed()) {
            if(isIconHidden(idx)) {
                continue
            }
            pinnedIconList.add(0, idx)
            if(pinnedIconList.size >= pinnedIconsCount) {
                break
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .then(modifier)
        ,
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable(enabled = false) {}
                .background(color)
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 5.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if(showClose) {
                    LongPressAbleIconBtn(
                        tooltipText = stringResource(R.string.close),
                        icon = Icons.Filled.Close,
                        iconContentDesc = stringResource(R.string.close),
                        onClick = {
                            quitSelectionMode()
                        },
                    )
                }
                if(showSelectedCount) {
                    if(showClose.not()) {
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(
                        text = ""+getSelectedFilesCount(),
                        modifier = MyStyleKt.ClickableText.modifier
                            .clickable(enabled = countNumOnClickEnabled) {
                                countNumOnClick()
                            }
                            .padding(horizontal = 10.dp)
                    )
                }
            }
            Box {
                val baseIconSize = MyStyleKt.defaultLongPressAbleIconBtnPressedCircleSize
                val offsetForPinnedAndMenuIcons = baseIconSize.value.let {
                    pinnedIconList.size * it + (if(visibleMoreIcon) it else 0F)
                }.coerceAtLeast(0f).dp
                val scrollableIconListState = rememberScrollState()
                Row (
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = -offsetForPinnedAndMenuIcons)
                        .padding(start = offsetForPinnedAndMenuIcons)
                        .horizontalScroll(scrollableIconListState)
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    for((idx, text) in iconTextList.withIndex()) {
                        if(pinnedIconList.contains(idx)) {
                            break
                        }
                        if(isIconHidden(idx)) {
                            continue
                        }
                        LongPressAbleIconBtn(
                            enabled = iconEnableList[idx](),
                            tooltipText = text,
                            icon = iconList[idx],
                            iconContentDesc = iconDescTextList[idx],
                            onClick = {
                               iconOnClickList[idx]()
                            }
                        )
                    }
                }
                LaunchedEffect(Unit) {
                    doJobThenOffLoading {
                        delay(200)
                        UIHelper.scrollTo(scope, scrollableIconListState, 200 * iconTextList.size)
                    }
                }
                val offsetForMenuIcon = if(visibleMoreIcon) baseIconSize else 0.dp
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = -offsetForMenuIcon)
                        .padding(start = offsetForMenuIcon)
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (idx in pinnedIconList) {
                        if(isIconHidden(idx)) {
                            continue
                        }
                        LongPressAbleIconBtn(
                            enabled = iconEnableList[idx](),
                            tooltipText = iconTextList[idx],
                            icon = iconList[idx],
                            iconContentDesc = iconDescTextList[idx],
                            onClick = {
                                iconOnClickList[idx]()
                            }
                        )
                    }
                }
                if (visibleMoreIcon) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LongPressAbleIconBtn(
                            enabled = enableMoreIcon,
                            tooltipText = stringResource(R.string.menu),
                            icon = Icons.Filled.MoreVert,
                            iconContentDesc = stringResource(R.string.menu),
                            onClick = {
                                switchDropDownMenu()
                            }
                        )
                        DropdownMenu(
                            expanded = dropDownMenuExpandState.value,
                            onDismissRequest = { closeDropDownMenu() }
                        ) {
                            var idxOffset = 0;
                            var showDivider = false
                            for ((idx, text) in moreItemTextList.withIndex()) {
                                if(text == UIHelper.bottomBarDividerPlaceHolder) {
                                    showDivider = true
                                    idxOffset--
                                    continue
                                }
                                val willShowDivider = showDivider
                                showDivider = false
                                val idx = idx + idxOffset
                                if(moreItemVisibleList.isNotEmpty() && !moreItemVisibleList[idx]()) {
                                    continue
                                }
                                if(text.isBlank()) {
                                    continue
                                }
                                if(willShowDivider) {
                                    MyHorizontalDivider()
                                }
                                DropdownMenuItem(
                                    enabled = moreItemEnableList[idx](),
                                    text = { Text(text) },
                                    onClick = {
                                        moreItemOnClickList[idx]()
                                        closeDropDownMenu()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
