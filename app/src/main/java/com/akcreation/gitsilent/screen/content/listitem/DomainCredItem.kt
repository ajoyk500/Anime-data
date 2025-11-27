package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.dto.DomainCredentialDto
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.listItemPadding
import com.akcreation.gitsilent.utils.state.CustomStateSaveable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DomainCredItem(
    showBottomSheet: MutableState<Boolean>,
    curCredentialState: CustomStateSaveable<DomainCredentialDto>,
    idx:Int,
    lastClickedItemKey:MutableState<String>,
    thisItem:DomainCredentialDto,
    onClick:(DomainCredentialDto)->Unit
) {
    val none = "[${stringResource(R.string.none)}]"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisItem.domainCredId
                    onClick(thisItem)
                },
                onLongClick = {
                    lastClickedItemKey.value = thisItem.domainCredId
                    curCredentialState.value = DomainCredentialDto()
                    curCredentialState.value = thisItem
                    showBottomSheet.value = true
                },
            )
            .then(
                if(lastClickedItemKey.value == thisItem.domainCredId) {
                    Modifier.background(UIHelper.getLastClickedColor())
                }else Modifier
            )
            .listItemPadding()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(text = stringResource(R.string.domain) +": ")
            Text(text = thisItem.domain,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(text = stringResource(R.string.http_s) +": ")
            Text(text = thisItem.credName ?: none,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(text = stringResource(R.string.ssh) +": ")
            Text(text = thisItem.sshCredName ?: none,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light
            )
        }
    }
}
