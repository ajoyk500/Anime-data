package com.akcreation.gitsilent.compose

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akcreation.gitsilent.utils.UIHelper

@Composable
fun SimpleCheckBox(
    enabled:Boolean,
    contentDescription:String? = null,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = UIHelper.getCheckBoxByState(enabled),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}
