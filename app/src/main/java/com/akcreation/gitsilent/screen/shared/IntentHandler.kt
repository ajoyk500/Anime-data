package com.akcreation.gitsilent.screen.shared

import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage

object IntentHandler {
    private const val TAG = "IntentHandler"
    val gotNewIntent = mutableStateOf("")
    val intentConsumed = mutableStateOf(false)
    val intent = mutableStateOf<Intent?>(null)
    fun setNewIntent(newIntent: Intent?) {
        intent.value = newIntent
        changeStateTriggerRefreshPage(gotNewIntent)
    }
    fun requireHandleNewIntent() {
        val intent = intent.value ?: return;
        if(needConsume(intent)) {
            MyLog.d(TAG, "will navigate to HomeScreen to handle new Intent")
            intentConsumed.value = false
            changeStateTriggerRefreshPage(SharedState.homeScreenNeedRefresh)
            AppModel.navController.let {
                it.popBackStack(it.graph.startDestinationId, inclusive = false)
            }
        }
    }
    fun needConsume(intent:Intent?) = intent?.extras != null || intent?.data != null;
}
