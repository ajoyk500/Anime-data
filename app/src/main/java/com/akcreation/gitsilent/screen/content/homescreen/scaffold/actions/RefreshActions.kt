package com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.R

@Composable
fun RefreshActions(
    refreshPage: () -> Unit,
) {
    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.refresh),
        icon = Icons.Filled.Refresh,
        iconContentDesc = stringResource(id = R.string.refresh),
    ) {
        refreshPage()
    }
}
