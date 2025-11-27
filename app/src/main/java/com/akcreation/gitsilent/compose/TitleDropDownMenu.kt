package com.akcreation.gitsilent.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.dropDownItemContainerColor

@Composable
fun <T> SimpleTitleDropDownMenu(
    dropDownMenuExpandState: MutableState<Boolean>,
    dropDownMenuItemContentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    curSelectedItem:T,
    itemList: List<T>,
    isItemSelected:(T)->Boolean,
    titleClickEnabled:Boolean,
    showHideMenuIconContentDescription:String,  
    menuItemFormatter:(T)->String,
    titleFirstLineFormatter:(T)->String,
    titleSecondLineFormatter:(T)->String,
    titleOnLongClick:(T)->Unit,
    itemOnClick: (T)->Unit
) {
    TitleDropDownMenu(
        dropDownMenuExpandState = dropDownMenuExpandState,
        dropDownMenuItemContentPadding = dropDownMenuItemContentPadding,
        curSelectedItem = curSelectedItem,
        itemList = itemList,
        titleClickEnabled = titleClickEnabled,
        titleFirstLine={
            Text(
                text = titleFirstLineFormatter(curSelectedItem),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = MyStyleKt.Title.firstLineFontSize,
            )
        },
        titleSecondLine={
            Text(
                text = titleSecondLineFormatter(curSelectedItem),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = MyStyleKt.Title.secondLineFontSize,
            )
        },
        titleRightIcon = {
            Icon(
                imageVector = if (dropDownMenuExpandState.value) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft,
                contentDescription = showHideMenuIconContentDescription,
            )
        },
        isItemSelected = isItemSelected,
        menuItem = {it, selected ->
            DropDownMenuItemText(
                text1 = menuItemFormatter(it),
            )
        },
        titleOnLongClick = titleOnLongClick,
        itemOnClick = itemOnClick,
    )
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> TitleDropDownMenu(
    dropDownMenuExpandState: MutableState<Boolean>,
    dropDownMenuItemContentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    curSelectedItem:T,
    itemList: List<T>,
    titleClickEnabled:Boolean,
    switchDropDownMenuShowHide:()->Unit = {
        dropDownMenuExpandState.value = !dropDownMenuExpandState.value
    },
    closeDropDownMenu:()->Unit  = {
        dropDownMenuExpandState.value = false
    },
    titleFirstLine:@Composable (T)->Unit,
    titleSecondLine:@Composable (T)->Unit,
    titleRightIcon:@Composable (T)->Unit,  
    menuItem:@Composable (T, selected:Boolean)->Unit,
    titleOnLongClick:(T)->Unit,
    isItemSelected:(T)->Boolean,
    itemOnClick: (T)->Unit,
    titleOnClick: ()->Unit = { switchDropDownMenuShowHide() },  
    showExpandIcon: Boolean = true,
) {
    val haptic = LocalHapticFeedback.current
    val configuration = AppModel.getCurActivityConfig()
    val itemWidth = remember(configuration.screenWidthDp) { (configuration.screenWidthDp / 2).dp }
    val iconWidth = remember { 30.dp }
    val textWidth = remember (showExpandIcon, itemWidth, iconWidth) { if(showExpandIcon) itemWidth - iconWidth else itemWidth }
    Box(
        modifier = Modifier
            .width(itemWidth)
            .combinedClickable(
                enabled = titleClickEnabled,
                onLongClick = {  
                    titleOnLongClick(curSelectedItem)
                }
            ) { 
                titleOnClick()
            },
    ) {
        Column(
            modifier = Modifier
                .width(textWidth)
                .align(Alignment.CenterStart)
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
            ) {
                titleFirstLine(curSelectedItem)
            }
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
            ) {
                titleSecondLine(curSelectedItem)
            }
        }
        if(showExpandIcon) {
            Column(
                modifier = Modifier
                    .width(iconWidth)
                    .align(Alignment.CenterEnd)
            ) {
                titleRightIcon(curSelectedItem)
            }
        }
    }
    DropdownMenu(
        expanded = dropDownMenuExpandState.value,
        onDismissRequest = { closeDropDownMenu() }
    ) {
        for (i in itemList.toList()) {
            val selected = isItemSelected(i)
            DropdownMenuItem(
                contentPadding = dropDownMenuItemContentPadding,
                modifier = Modifier
                    .dropDownItemContainerColor(selected)
                    .width(itemWidth)
                ,
                text = { menuItem(i, selected) },
                onClick = {
                    closeDropDownMenu()
                    itemOnClick(i)
                }
            )
        }
    }
}
