package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.InLineIcon
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.getFormatTimeFromSec
import com.akcreation.gitsilent.utils.listItemPadding
import com.akcreation.gitsilent.utils.state.CustomStateSaveable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CredentialItem(
    showBottomSheet: MutableState<Boolean>,
    curCredentialState: CustomStateSaveable<CredentialEntity>,
    idx:Int,
    thisItem:CredentialEntity,
    isLinkMode:Boolean,
    linkedFetchId:String,
    linkedPushId:String,
    lastClickedItemKey:MutableState<String>,
    onClick:(CredentialEntity)->Unit
) {
    val haptic = LocalHapticFeedback.current
    val isMatchByDomain = SpecialCredential.MatchByDomain.equals_to(thisItem)
    val isNone = SpecialCredential.NONE.equals_to(thisItem)
    val isNotMatchByDomainOrNone = !(isMatchByDomain || isNone)
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisItem.id
                    onClick(thisItem)
                },
                onLongClick = {
                    if(isNotMatchByDomainOrNone) {
                        lastClickedItemKey.value = thisItem.id
                        curCredentialState.value = CredentialEntity()
                        curCredentialState.value = thisItem
                        showBottomSheet.value = true
                    }
                },
            )
            .then(
                if(lastClickedItemKey.value == thisItem.id) {
                    Modifier.background(UIHelper.getLastClickedColor())
                }else Modifier
            )
            .listItemPadding()
        ,
    ) {
        val trailIconSize = remember { MyStyleKt.trailIconSize + 10.dp }
        val trailIconPadding = if(isNotMatchByDomainOrNone) PaddingValues(end = trailIconSize) else PaddingValues()
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(trailIconPadding)
                .fillMaxWidth()
            ,
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                val linked = isLinkMode && (linkedFetchId==thisItem.id || linkedPushId==thisItem.id);
                Text(text = stringResource(R.string.name) + ": ")
                val iconSize = if(linked) {
                    if(linkedFetchId == thisItem.id && linkedPushId == thisItem.id) {
                        trailIconSize * 2
                    }else {
                        trailIconSize
                    }
                } else {
                    0.dp
                }
                Box {
                    ScrollableRow(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = iconSize)
                    ) {
                        Text(
                            text = thisItem.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if(linked) FontWeight.ExtraBold else defaultFontWeight,
                            color = if(linked) MyStyleKt.DropDownMenu.selectedItemColor() else Color.Unspecified,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(iconSize)
                    ) {
                        if(isLinkMode && linkedFetchId==thisItem.id) {
                            InLineIcon(
                                icon = Icons.Filled.Download,
                                tooltipText = stringResource(R.string.fetch),
                                enabled = false,
                            ) { }
                        }
                        if(isLinkMode && linkedPushId==thisItem.id) {
                            InLineIcon(
                                icon = Icons.Filled.Upload,
                                tooltipText = stringResource(R.string.push),
                                enabled = false,
                            ) { }
                        }
                    }
                }
            }
            if(isMatchByDomain || isNone) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Text(text = stringResource(R.string.desc) +": ")
                    ScrollableRow {
                        Text(text = if(isMatchByDomain) stringResource(R.string.credential_match_by_domain_note_short) else stringResource(R.string.no_credential_will_be_used),
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = defaultFontWeight
                        )
                    }
                }
            }
            if(isNotMatchByDomainOrNone) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Text(text = stringResource(R.string.edited) +": ")
                    ScrollableRow {
                        Text(text = getFormatTimeFromSec(thisItem.baseFields.baseUpdateTime),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = defaultFontWeight
                        )
                    }
                }
            }
        }
        if(isNotMatchByDomainOrNone) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(trailIconSize)
                ,
            ) {
                LongPressAbleIconBtn(
                    tooltipText = stringResource(R.string.edit),
                    icon = Icons.Filled.Edit,
                    iconContentDesc = stringResource(R.string.edit),
                ) {
                    lastClickedItemKey.value = thisItem.id
                    AppModel.navController.navigate(Cons.nav_CredentialNewOrEditScreen+"/"+thisItem.id)
                }
            }
        }
    }
}
