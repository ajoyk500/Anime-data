package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.InLineCopyIcon
import com.akcreation.gitsilent.compose.InLineIcon
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SingleLineClickableText
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.git.BranchNameAndTypeDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.listItemPadding
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.github.git24j.core.Branch
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BranchItem(
    showBottomSheet: MutableState<Boolean>,
    curObjFromParent: CustomStateSaveable<BranchNameAndTypeDto>,
    idx:Int,
    thisObj:BranchNameAndTypeDto,
    requireBlinkIdx: MutableIntState,  
    lastClickedItemKey:MutableState<String>,
    pageRequest:MutableState<String>,
    onClick:()->Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val setCurObj = {
        curObjFromParent.value = BranchNameAndTypeDto()
        curObjFromParent.value = thisObj
    }
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisObj.fullName
                    onClick()
                },
                onLongClick = {
                    lastClickedItemKey.value = thisObj.fullName
                    setCurObj()
                    showBottomSheet.value = true
                },
            )
            .then(
                if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue == idx) {
                    val highlightColor = Modifier.background(UIHelper.getHighlightingBackgroundColor())
                    doJobThenOffLoading {
                        delay(UIHelper.getHighlightingTimeInMills())  
                        requireBlinkIdx.intValue = -1  
                    }
                    highlightColor
                } else if(thisObj.fullName == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                }else {
                    Modifier
                }
            )
            .listItemPadding()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = ImageVector.vectorResource(R.drawable.branch),
                tooltipText = stringResource(R.string.branch)
            )
            ScrollableRow {
                Text(text = thisObj.shortName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if(thisObj.isCurrent) FontWeight.ExtraBold else defaultFontWeight,
                    color = if(thisObj.isCurrent) MyStyleKt.DropDownMenu.selectedItemColor() else Color.Unspecified
                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Filled.Commit,
                tooltipText = stringResource(R.string.last_commit)
            )
            Text(text = thisObj.shortOidStr,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(thisObj.oidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Filled.Category,
                tooltipText = stringResource(R.string.type)
            )
            ScrollableRow {
                Text(text = thisObj.getTypeString(activityContext, false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        if(thisObj.type == Branch.BranchType.LOCAL) {
            if(thisObj.isUpstreamAlreadySet()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    InLineIcon(
                        icon = Icons.Filled.Cloud,
                        tooltipText = stringResource(R.string.upstream)
                    )
                    ScrollableRow {
                        SingleLineClickableText(thisObj.getUpstreamShortName(activityContext)) {
                            lastClickedItemKey.value = thisObj.fullName
                            setCurObj()
                            pageRequest.value = PageRequest.goToUpstream
                        }
                    }
                }
            }
            if(thisObj.isUpstreamValid()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    InLineIcon(
                        icon = Icons.Filled.Info,
                        tooltipText = stringResource(R.string.status)
                    )
                    ScrollableRow {
                        Text(
                            text = thisObj.getAheadBehind(activityContext, false),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = defaultFontWeight,
                            color = if(thisObj.alreadyUpToDate()) MyStyleKt.TextColor.getHighlighting() else Color.Unspecified
                        )
                    }
                }
            }
        }
        if (thisObj.isSymbolic) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                InLineIcon(
                    icon = Icons.Filled.Link,
                    tooltipText = stringResource(R.string.symbolic_target)
                )
                ScrollableRow {
                    Text(text = thisObj.symbolicTargetShortName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.AutoMirrored.Filled.Notes,
                tooltipText = stringResource(R.string.other)
            )
            ScrollableRow {
                Text(text = thisObj.getOther(activityContext, false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
     }
}
