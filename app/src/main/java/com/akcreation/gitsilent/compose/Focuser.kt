package com.akcreation.gitsilent.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Focuser(
    focusRequester: FocusRequester,
    scope: CoroutineScope,
){
    LaunchedEffect(Unit) {
        scope.launch {
            runCatching {
                delay(500)
                focusRequester.requestFocus()
            }
        }
    }
}
@Composable
fun OneTimeFocusRightNow(focusRequester: FocusRequester) {
    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }
}
