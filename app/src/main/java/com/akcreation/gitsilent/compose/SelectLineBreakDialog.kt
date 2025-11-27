package com.akcreation.gitsilent.compose

import androidx.compose.runtime.Composable
import com.akcreation.gitsilent.constants.LineBreak

@Composable
fun SelectLineBreakDialog(
    current: LineBreak,
    closeDialog: () -> Unit,
    onClick: (selected: LineBreak) -> Unit,
) {
    SingleSelectDialog(
        currentItem = current,
        itemList = LineBreak.list,
        text = { it.visibleValue },
        closeDialog = closeDialog,
        onClick = onClick
    )
}
