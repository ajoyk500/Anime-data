package com.akcreation.gitsilent.fileeditor.ui.composable.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.ui.theme.Theme

@Composable
fun FieldIcon(
    isMultipleSelection: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when {
            isMultipleSelection && isSelected -> {
                CheckCircleIcon(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
            }
            isSelected -> {
                MenuIcon(modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            }
        }
    }
}
@Preview
@Composable
private fun FieldIcon_Preview() {
    MaterialTheme {
        Column {
            FieldIcon(isMultipleSelection = false, isSelected = false, modifier = Modifier.size(32.dp))
            FieldIcon(isMultipleSelection = false, isSelected = true, modifier = Modifier.size(32.dp))
            FieldIcon(isMultipleSelection = true, isSelected = true, modifier = Modifier.size(32.dp))
        }
    }
}
@Composable
fun FieldIcon(
    focused: Boolean,
    modifier: Modifier = Modifier
) {
    val inDarkTheme = Theme.inDarkTheme
    Box(modifier = modifier) {
        if (focused){
            MenuIcon(modifier = Modifier.align(Alignment.Center), color = Color.Gray)
        }
    }
}
