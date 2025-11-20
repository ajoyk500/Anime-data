package com.akcreation.gitsilent.compose

import androidx.compose.runtime.Composable
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope


@Composable
fun SelectSyntaxHighlightingDialog(
    plScope: PLScope,
    closeDialog: () -> Unit,
    onClick: (selectedScope: PLScope) -> Unit,
) {
    SingleSelectDialog(
        currentItem = plScope,
        itemList = PLScope.SCOPES_NO_AUTO,
        text = { it.name },
        closeDialog = closeDialog,
        onClick = onClick
    )
}
