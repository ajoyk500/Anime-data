package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.compose.IconOfItem
import com.akcreation.gitsilent.compose.ListItemRow
import com.akcreation.gitsilent.compose.ListItemSpacer
import com.akcreation.gitsilent.compose.ListItemToggleButton
import com.akcreation.gitsilent.compose.ListItemTrailingIconRow
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.UIHelper

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChangeListItem(
    item: StatusTypeEntrySaver,
    isFileSelectionMode: MutableState<Boolean>,
    menuKeyTextList: List<String>,
    menuKeyActList: List<(StatusTypeEntrySaver)->Unit>,
    menuKeyEnableList: List<(StatusTypeEntrySaver)->Boolean>,
    menuKeyVisibleList: List<(StatusTypeEntrySaver)->Boolean> = listOf(),
    fromTo:String,
    isDiffToLocal:Boolean,  
    lastClickedItemKey:MutableState<String>,
    switchItemSelected:(StatusTypeEntrySaver)->Unit,
    isItemInSelected:(StatusTypeEntrySaver)->Boolean,
    onLongClick:(StatusTypeEntrySaver)->Unit,
    onClick:(StatusTypeEntrySaver) -> Unit
){
    val activityContext = LocalContext.current
    val itemIsDir = item.itemType == Cons.gitItemTypeDir || item.itemType == Cons.gitItemTypeSubmodule
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = {
                    lastClickedItemKey.value = item.getItemKey()
                    onLongClick(item)
                }
            ){  
                lastClickedItemKey.value = item.getItemKey()
                onClick(item)
            }
            .then(
                if (isItemInSelected(item)) Modifier.background(
                    MaterialTheme.colorScheme.primaryContainer
                ) else if(item.getItemKey() == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                } else Modifier
            )
            ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ListItemRow{
            ListItemToggleButton(
                checked = isItemInSelected(item),
                onCheckedChange = cc@{
                    switchItemSelected(item)
                }
            ) {
                IconOfItem(
                    fileName = item.fileName,
                    filePath = item.canonicalPath,
                    context = activityContext,
                    defaultIconWhenLoadFailed = if(item.changeType == Cons.gitStatusDeleted) ImageVector.vectorResource(R.drawable.outline_unknown_document_24) else if(itemIsDir) Icons.Filled.Folder else null,
                    contentDescription = if(item.changeType == Cons.gitStatusDeleted) null else if(itemIsDir) stringResource(R.string.folder_icon) else stringResource(R.string.file_icon),
                )
            }
            ListItemSpacer()
            Column {
                val changeTypeColor = UIHelper.getChangeTypeColor(item.changeType ?: "")
                Text(text = item.fileName, fontSize = 20.sp, color = changeTypeColor)
                val secondLineFontSize = 12.sp
                Text(text = item.getChangeListItemSecondLineText(isDiffToLocal), fontSize = secondLineFontSize, color = changeTypeColor)
                val parentPath = item.getParentDirStr()
                if(parentPath.isNotEmpty()) {
                    Text(text = parentPath, fontSize = secondLineFontSize, color = changeTypeColor)
                }
            }
        }
        val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }  
        ListItemTrailingIconRow {
            IconButton(onClick = { dropDownMenuExpandState.value = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.file_or_folder_menu)
                )
            }
            DropdownMenu(
                expanded = dropDownMenuExpandState.value,
                onDismissRequest = { dropDownMenuExpandState.value = false }
            ) {
                for((idx,v) in menuKeyTextList.withIndex()) {
                    if(menuKeyVisibleList.isNotEmpty() && !menuKeyVisibleList[idx](item)) {
                        continue
                    }
                    DropdownMenuItem(
                        enabled = menuKeyEnableList[idx](item),
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
