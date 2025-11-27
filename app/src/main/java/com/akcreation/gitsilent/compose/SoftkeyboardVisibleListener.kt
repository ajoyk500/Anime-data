package com.akcreation.gitsilent.compose

import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Composable
fun SoftkeyboardVisibleListener(
    view: View,
    isKeyboardVisible: MutableState<Boolean>,
    isKeyboardCoveredComponent: MutableState<Boolean>,
    componentHeight: MutableIntState,
    keyboardPaddingDp: MutableIntState,
    density: Density,
    skipCondition:()->Boolean,
    paddingAdjustValue:Dp = 120.dp
) {
    DisposableEffect(view) {
        val callback = ViewTreeObserver.OnGlobalLayoutListener cb@{
            if (skipCondition()) {
                return@cb
            }
            val insets = ViewCompat.getRootWindowInsets(view)
            isKeyboardVisible.value = insets?.isVisible(WindowInsetsCompat.Type.ime()) == true
            val keyboardHeightPx = insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0
            isKeyboardCoveredComponent.value = (componentHeight.intValue > 0) && (view.height - keyboardHeightPx < componentHeight.intValue)
            keyboardPaddingDp.intValue = if (isKeyboardCoveredComponent.value) {
                val p = with(density) { keyboardHeightPx.toDp() - paddingAdjustValue }
                if (p > 0.dp) p.value.toInt() else 0
            } else {
                0
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(callback)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(callback)
        }
    }
}
