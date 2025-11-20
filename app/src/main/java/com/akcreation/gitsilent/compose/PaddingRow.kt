package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akcreation.gitsilent.style.MyStyleKt

@Composable
fun PaddingRow(
    paddingValues: PaddingValues = PaddingValues(horizontal = MyStyleKt.defaultHorizontalPadding),
    content: @Composable RowScope.()->Unit
) {
    Row(modifier = Modifier.padding(paddingValues)) {
        content()
    }
}

@Composable
fun DefaultPaddingRow(content: @Composable RowScope.()->Unit) {
    PaddingRow(content = content)
}
