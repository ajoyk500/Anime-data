package me.saket.swipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

class SwipeAction(
  val onSwipe: () -> Unit,
  val icon: @Composable () -> Unit,
  val background: Color,
  val weight: Double = 1.0,
  val isUndo: Boolean = false,
  val enableAnimation: Boolean = true,
  val enableAct: Boolean = true,
  val enableVibration: Boolean = enableAct,
) {
  init {
    require(weight > 0.0) { "invalid weight $weight; must be greater than zero" }
  }
  fun copy(
    onSwipe: () -> Unit = this.onSwipe,
    icon: @Composable () -> Unit = this.icon,
    background: Color = this.background,
    weight: Double = this.weight,
    isUndo: Boolean = this.isUndo,
    enableAnimation: Boolean = this.enableAnimation,
    enableAct: Boolean = this.enableAct,
    enableVibration: Boolean = this.enableVibration,
  ) = SwipeAction(
    onSwipe = onSwipe,
    icon = icon,
    background = background,
    weight = weight,
    isUndo = isUndo,
    enableAnimation = enableAnimation,
    enableAct = enableAct,
    enableVibration = enableVibration,
  )
}
fun SwipeAction(
  onSwipe: () -> Unit,
  icon: Painter,
  background: Color,
  weight: Double = 1.0,
  isUndo: Boolean = false,
  enableAnimation: Boolean = true,
  enableAct: Boolean = true,
  enableVibration: Boolean = enableAct,
): SwipeAction {
  return SwipeAction(
    icon = {
      Image(
        modifier = Modifier.padding(16.dp),
        painter = icon,
        contentDescription = null
      )
    },
    background = background,
    weight = weight,
    onSwipe = onSwipe,
    isUndo = isUndo,
    enableAnimation = enableAnimation,
    enableAct = enableAct,
    enableVibration = enableVibration,
  )
}
