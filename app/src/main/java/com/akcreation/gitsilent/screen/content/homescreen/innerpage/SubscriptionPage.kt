package com.akcreation.gitsilent.screen.content.homescreen.innerpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.ComposeHelper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier

private const val TAG = "SubscriptionPage"
@Composable
fun SubscriptionPage(contentPadding: PaddingValues, needRefresh: MutableState<String>, openDrawer: ()->Unit){
    val activityContext = LocalContext.current
    val exitApp = AppModel.exitApp;
    val clipboardManager = LocalClipboardManager.current
    val copy={text:String ->
        clipboardManager.setText(AnnotatedString(text))
        Msg.requireShow(activityContext.getString(R.string.copied))
    }
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer=openDrawer, exitApp= exitApp)
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    Column (
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
            .padding(MyStyleKt.defaultItemPadding)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
    }
    LaunchedEffect(needRefresh.value) {
        val funName = "LaunchedEffect"
        try {
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
        }
    }
}
