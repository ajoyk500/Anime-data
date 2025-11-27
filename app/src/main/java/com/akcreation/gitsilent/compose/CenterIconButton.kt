package com.akcreation.gitsilent.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CenterIconButton(
    icon:ImageVector,
    text:String,
    iconDesc:String? = text.ifBlank { null },
    mainColor: Color? = null,
    attachContent: @Composable () -> Unit = {},
    condition: Boolean = true,  
    elseContent: @Composable ()->Unit = {},  
    enabled:Boolean = true,
    onClick:(()->Unit)? = null
) {
    if(condition) {
        Column(
            modifier = if(onClick != null) {
                Modifier.clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClick()
                }
            }else {
                Modifier
            },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row{
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = icon,
                    contentDescription = iconDesc,
                    tint = mainColor ?: LocalContentColor.current,
                )
            }
            Row {
                Text(
                    text = text,
                    color = mainColor ?: Color.Unspecified,
                )
            }
            attachContent()
        }
    }else {
        elseContent()
    }
}
