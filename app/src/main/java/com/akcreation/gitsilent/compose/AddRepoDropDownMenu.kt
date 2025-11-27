package com.akcreation.gitsilent.compose

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.goToCloneScreen

@Composable
fun AddRepoDropDownMenu(
    showMenu:Boolean,
    closeMenu:()->Unit,
    cloneOnClick:()->Unit = { goToCloneScreen() },
    importOnClick:()->Unit,

) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { closeMenu() }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.clone)) },
            onClick = {
                closeMenu()
                cloneOnClick()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.import_str)) },
            onClick = {
                closeMenu()
                importOnClick()

            }
        )
    }
}
