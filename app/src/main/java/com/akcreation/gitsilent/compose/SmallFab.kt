package com.akcreation.gitsilent.compose

import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SmallFab(modifier: Modifier = Modifier, icon:ImageVector, iconDesc:String, alpha:Float = 0.5f, onClick: () -> Unit) {
    SmallFloatingActionButton(
        modifier=modifier,
        elevation= FloatingActionButtonDefaults.elevation(0.dp,0.dp,0.dp,0.dp),
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha=alpha),
        contentColor = MaterialTheme.colorScheme.secondary.copy(alpha = alpha)
    ) {
        Icon(icon, iconDesc)
    }
}
