package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier

@Composable
fun FullScreenScrollableColumn(
    contentPadding: PaddingValues,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content:@Composable ()->Unit,
) {
    Column(
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
            .padding(MyStyleKt.defaultItemPadding)
        ,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        content()
    }
}
