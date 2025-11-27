package com.akcreation.gitsilent.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.InterceptPlatformTextInput
import kotlinx.coroutines.awaitCancellation

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DisableSoftKeyboard(
    disable: Boolean,
    content: @Composable () -> Unit
) {
    InterceptPlatformTextInput(
        interceptor = { request, nextHandler ->
            if (disable) {
                awaitCancellation()
            } else {
                nextHandler.startInputMethod(request)
            }
        },
        content = content
    )
}
