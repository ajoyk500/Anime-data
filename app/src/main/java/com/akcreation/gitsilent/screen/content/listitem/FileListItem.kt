package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.compose.IconOfItem
import com.akcreation.gitsilent.compose.ListItemRow
import com.akcreation.gitsilent.compose.ListItemSpacer
import com.akcreation.gitsilent.compose.ListItemToggleButton
import com.akcreation.gitsilent.compose.ListItemTrailingIconRow
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.dto.FileItemDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.getParentPathEndsWithSeparator

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItem(
    fullPathOfTopNoEndSlash:String,
    item: FileItemDto,
    lastPathByPressBack:MutableState<String>,
    menuKeyTextList: List<String>,
    menuKeyActList: List<(FileItemDto)->Unit>,
    iconOnClick:()->Unit,
    isItemInSelected:(FileItemDto)->Boolean,
    itemOnLongClick:(FileItemDto)->Unit,
    itemOnClick:(FileItemDto)->Unit,
){
    val activityContext = LocalContext.current
    val inDarkTheme = Theme.inDarkTheme
    val alpha = 0.6f
    val iconColor = if(item.isHidden) LocalContentColor.current.copy(alpha = alpha) else LocalContentColor.current
    val fontColor = if(item.isHidden) {if(inDarkTheme) Color.White.copy(alpha = alpha) else Color.Black.copy(alpha = alpha)} else Color.Unspecified
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    lastPathByPressBack.value = item.fullPath
                    itemOnClick(item)
                },
                onLongClick = {
                    lastPathByPressBack.value = item.fullPath
                    itemOnLongClick(item)
                }
            )
            .then(
                if (isItemInSelected(item)) {
                    Modifier.background(
                        MaterialTheme.colorScheme.primaryContainer
                    )
                }else if(lastPathByPressBack.value == item.fullPath){  
                    Modifier.background(
                        UIHelper.getLastClickedColor()
                    )
                } else Modifier
            )
            ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ListItemRow{
            ListItemToggleButton(
                checked = isItemInSelected(item),
                onCheckedChange = {
                    lastPathByPressBack.value = item.fullPath
                    iconOnClick()
                },
            ) {
                IconOfItem(
                    fileName = item.name,
                    filePath = item.fullPath,
                    context = activityContext,
                    contentDescription = if(item.isDir) stringResource(R.string.folder_icon) else stringResource(R.string.file_icon),
                    iconColor = iconColor,
                )
            }
            ListItemSpacer()
            Column {
                Row {
                    Text(
                            text = item.name,
                            fontSize = 20.sp,
                            color = fontColor
                    )
                }
                Row{
                    Text(item.getShortDesc(), fontSize = 12.sp, color = fontColor)
                }
                if(fullPathOfTopNoEndSlash.isNotBlank()) {
                    val relativePath = item.fullPath.removePrefix(fullPathOfTopNoEndSlash).removePrefix(Cons.slash)
                        .let { getParentPathEndsWithSeparator(it, trueWhenNoParentReturnEmpty = true) }
                    if(relativePath.isNotEmpty()) {
                        Row {
                            Text(text = relativePath, fontSize = 12.sp, color = fontColor)
                        }
                    }
                }
            }
        }
        val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
        ListItemTrailingIconRow{
            IconButton(onClick = {
                lastPathByPressBack.value = item.fullPath
                dropDownMenuExpandState.value = true
            }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.file_or_folder_menu)
                )
            }
            DropdownMenu(
                expanded = dropDownMenuExpandState.value,
                onDismissRequest = { dropDownMenuExpandState.value = false }
            ) {
                for ((idx,v) in menuKeyTextList.withIndex()) {
                    if(v.isBlank()) {
                        continue
                    }
                    DropdownMenuItem(
                        text = { Text(v) },
                        onClick = {
                            menuKeyActList[idx](item)
                            dropDownMenuExpandState.value = false
                        }
                    )
                }
            }
        }
    }
}
