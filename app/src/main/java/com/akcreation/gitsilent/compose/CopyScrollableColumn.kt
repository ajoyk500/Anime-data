package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CopyScrollableColumn(content:@Composable ()->Unit) {
    MySelectionContainer {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            content()
        }
    }
}
