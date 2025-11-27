package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.akcreation.gitsilent.compose.InLineHistoryIcon
import com.akcreation.gitsilent.git.StashDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.fromTagToCommitHistory
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.listItemPadding
import com.akcreation.gitsilent.utils.state.CustomStateSaveable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StashItem(
    repoId:String,
    showBottomSheet: MutableState<Boolean>,
    curObjFromParent: CustomStateSaveable<StashDto>,
    idx:Int,
    lastClickedItemKey:MutableState<String>,
    thisObj:StashDto,
    onClick:()->Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisObj.getItemKey()
                    onClick()
                },
                onLongClick = {
                    lastClickedItemKey.value = thisObj.getItemKey()
                    curObjFromParent.value = StashDto()
                    curObjFromParent.value = thisObj
                    showBottomSheet.value = true
                },
            )
            .then(
                if(thisObj.getItemKey() == lastClickedItemKey.value){
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
            Text(text = stringResource(R.string.index) + ": ")
            Text(
                text = thisObj.index.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.stash_id) + ": ")
            Text(
                text = thisObj.getCachedShortStashId(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(thisObj.stashId.toString()))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
            InLineHistoryIcon {
                lastClickedItemKey.value = thisObj.getItemKey()
                fromTagToCommitHistory(
                    fullOid = thisObj.stashId.toString(),
                    shortName = thisObj.getCachedShortStashId(),
                    repoId = repoId
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.msg) + ": ")
            Text(
                text = thisObj.getCachedOneLineMsg(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
        }
    }
}
