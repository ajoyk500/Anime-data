package com.akcreation.gitsilent.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.showToast

@Composable
fun ShowErrorIfNeed(
    hasErr:MutableState<Boolean>,
    errMsg:MutableState<String>,
    useErrorPrefix:Boolean=true
) {
    val activityContext = LocalContext.current
    if(hasErr.value) {
        if(useErrorPrefix){
            showToast(activityContext, stringResource(R.string.error)+": "+ errMsg.value)
        }else {
            showToast(activityContext, errMsg.value)
        }
        hasErr.value=false
        errMsg.value=""
    }
}
