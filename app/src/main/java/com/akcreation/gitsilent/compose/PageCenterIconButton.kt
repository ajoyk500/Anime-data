package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun PageCenterIconButton(
    contentPadding: PaddingValues,
    onClick: ()->Unit,
    icon:ImageVector,
    text:String,
    iconDesc:String? = text.ifBlank { null },
    elseContent: @Composable () -> Unit = {},  
    condition: Boolean = true,
    attachContent: @Composable () -> Unit = {},
) {
    FullScreenScrollableColumn(contentPadding) {
        CenterIconButton(
            icon = icon,
            text = text,
            iconDesc = iconDesc,
            attachContent = attachContent,
            condition = condition,
            elseContent = elseContent,
            onClick = onClick,
        )
    }
}
