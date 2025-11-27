package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akcreation.gitsilent.style.MyStyleKt

@Composable
fun SpacerRow(paddingValues: PaddingValues = PaddingValues(MyStyleKt.BottomBar.outsideContentPadding)) {
    Spacer(Modifier.padding(paddingValues))
}
