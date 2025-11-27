package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.InLineCopyIcon
import com.akcreation.gitsilent.compose.InLineIcon
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SingleLineClickableText
import com.akcreation.gitsilent.git.FileHistoryDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.listItemPadding
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.time.TimeZoneUtil
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileHistoryItem(
    showBottomSheet: MutableState<Boolean>,
    curCommit: CustomStateSaveable<FileHistoryDto>,
    curCommitIdx:MutableIntState,
    idx:Int,
    dto:FileHistoryDto,
    requireBlinkIdx:MutableIntState,  
    lastClickedItemKey:MutableState<String>,
    shouldShowTimeZoneInfo:Boolean,
    showItemMsg:(FileHistoryDto)->Unit,
    onClick:(FileHistoryDto)->Unit={}
) {
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val updateCurObjState = {
        curCommit.value = FileHistoryDto()
        curCommitIdx.intValue = -1
        curCommit.value = dto
        curCommitIdx.intValue = idx
    }
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = dto.getItemKey()
                    onClick(dto)
                },
                onLongClick = {  
                    lastClickedItemKey.value = dto.getItemKey()
                    updateCurObjState()
                    showBottomSheet.value = true
                },
            )
            .then(
                if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue==idx) {
                    val highlightColor = Modifier.background(UIHelper.getHighlightingBackgroundColor())
                    doJobThenOffLoading {
                        delay(UIHelper.getHighlightingTimeInMills())  
                        requireBlinkIdx.intValue = -1  
                    }
                    highlightColor
                } else if(dto.getItemKey() == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                }else {
                    Modifier
                }
            )
            .listItemPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InLineIcon(
                icon = Icons.Filled.Commit,
                tooltipText = stringResource(R.string.commit_id)
            )
            Text(
                text = dto.getCachedCommitShortOidStr(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(dto.commitOidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InLineIcon(
                icon = Icons.AutoMirrored.Filled.InsertDriveFile,
                tooltipText = stringResource(R.string.entry_id)
            )
            Text(
                text = dto.getCachedTreeEntryShortOidStr(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(dto.treeEntryOidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InLineIcon(
                icon = Icons.Filled.Person,
                tooltipText = stringResource(R.string.author)
            )
            ScrollableRow {
                Text(
                    text = Libgit2Helper.getFormattedUsernameAndEmail(dto.authorUsername, dto.authorEmail),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        if (!dto.authorAndCommitterAreSame()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InLineIcon(
                    icon = Icons.Outlined.Person,
                    tooltipText = stringResource(R.string.committer)
                )
                ScrollableRow {
                    Text(
                        text = Libgit2Helper.getFormattedUsernameAndEmail(dto.committerUsername, dto.committerEmail),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InLineIcon(
                icon = Icons.Filled.CalendarMonth,
                tooltipText = stringResource(R.string.date)
            )
            ScrollableRow {
                Text(
                    text = if(shouldShowTimeZoneInfo) TimeZoneUtil.appendUtcTimeZoneText(dto.dateTime, dto.originTimeOffsetInMinutes) else dto.dateTime,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InLineIcon(
                icon = Icons.AutoMirrored.Filled.Message,
                tooltipText = stringResource(R.string.msg)
            )
            SingleLineClickableText(dto.getCachedOneLineMsg()) {
                lastClickedItemKey.value = dto.getItemKey()
                updateCurObjState()
                showItemMsg(dto)
            }
        }
    }
}
