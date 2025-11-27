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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.data.entity.ErrorEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.listItemPadding
import com.akcreation.gitsilent.utils.state.CustomStateSaveable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ErrorItem(
    showBottomSheet: MutableState<Boolean>,
    curObjInState: CustomStateSaveable<ErrorEntity>,
    idx:Int,
    lastClickedItemKey:MutableState<String>,
    curObj: ErrorEntity,
    onClick:()->Unit
) {
    val haptic = LocalHapticFeedback.current
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable (
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = curObj.id
                    onClick()
                },
                onLongClick = {
                    lastClickedItemKey.value = curObj.id
                    curObjInState.value = ErrorEntity()
                    curObjInState.value = curObj
                    showBottomSheet.value = true
                },
            )
            .then(
                if(lastClickedItemKey.value == curObj.id) {
                    Modifier.background(UIHelper.getLastClickedColor())
                }else Modifier
            )
            .listItemPadding()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(text = stringResource(R.string.id) +": ")
            Text(text = curObj.id,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            ){
            Text(text = stringResource(R.string.date) +": ")
            Text(text = curObj.date,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            ){
            Text(text = stringResource(R.string.msg) +": ")
            Text(text = curObj.getCachedOneLineMsg(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
        }
    }
}
