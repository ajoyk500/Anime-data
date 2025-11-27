package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.compose.InLineIcon
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.git.SubmoduleDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.listItemPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubmoduleItem(
    thisObj:SubmoduleDto,
    lastClickedItemKey: MutableState<String>,
    isItemInSelected:(SubmoduleDto) -> Boolean,
    onLongClick:(SubmoduleDto)->Unit,
    onClick:(SubmoduleDto)->Unit
) {
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisObj.name
                    onClick(thisObj)
                },
                onLongClick = {
                    lastClickedItemKey.value = thisObj.name
                    onLongClick(thisObj)
                },
            )
            .then(
                if (isItemInSelected(thisObj)) Modifier.background(
                    MaterialTheme.colorScheme.primaryContainer
                ) else if(thisObj.name == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                } else Modifier
            )
            .listItemPadding()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Outlined.GridView,
                tooltipText = stringResource(R.string.submodule)
            )
            ScrollableRow {
                Text(text = thisObj.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Outlined.Cloud,
                tooltipText = stringResource(R.string.url)
            )
            ScrollableRow {
                Text(text = thisObj.remoteUrl,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Outlined.Folder,
                tooltipText = stringResource(R.string.path)
            )
            ScrollableRow {
                Text(text = thisObj.relativePathUnderParent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Filled.Commit,
                tooltipText = stringResource(R.string.target)
            )
            Text(text = thisObj.getShortTargetHashCached(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Outlined.LocationOn,
                tooltipText = stringResource(R.string.location)
            )
            ScrollableRow {
                Text(text = thisObj.location.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Filled.Info,
                tooltipText = stringResource(R.string.status)
            )
            ScrollableRow {
                Text(text = thisObj.getStatus(activityContext),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight,
                    color = thisObj.getStatusColor()
                )
            }
        }
        if(thisObj.hasOther()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                InLineIcon(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    tooltipText = stringResource(R.string.other)
                )
                ScrollableRow {
                    Text(text = thisObj.getOther(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight,
                    )
                }
            }
        }
     }
}
