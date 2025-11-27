package com.akcreation.gitsilent.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.showToast

@Composable
fun ShowToast(
    showToast:MutableState<Boolean>,
    msg:MutableState<String>,
) {
    val activityContext = LocalContext.current
    if(showToast.value) {
        showToast(activityContext, msg.value)
        showToast.value=false
        msg.value=""
    }
}
