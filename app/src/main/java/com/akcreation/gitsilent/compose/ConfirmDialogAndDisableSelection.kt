package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.R

@Composable
fun ConfirmDialogAndDisableSelection(
    modifier: Modifier=Modifier,
    title: String="",
    text: String="",
    requireShowTitleCompose:Boolean=false,
    titleCompose:@Composable ()->Unit={},
    requireShowTextCompose:Boolean=false,
    textCompose:@Composable ()->Unit={},
    cancelBtnText: String = stringResource(R.string.cancel),
    okBtnText: String = stringResource(R.string.ok),
    cancelTextColor: Color = Color.Unspecified,
    okTextColor: Color = Color.Unspecified,
    okBtnEnabled: Boolean=true,
    showOk:Boolean = true,
    showCancel:Boolean = true,
    customOk:(@Composable ()->Unit)? = null,
    customCancel:(@Composable ()->Unit)? = null,
    onCancel: () -> Unit,
    onDismiss: ()->Unit = onCancel,  
    onOk: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        title = {
            DisableSelection {
                if(requireShowTitleCompose) {
                    titleCompose()
                }else {
                    DialogTitle(title)
                }
            }
        },
        text = {
            DisableSelection {
                if(requireShowTextCompose) {
                    textCompose()
                }else {
                    ScrollableColumn {
                        Text(text)
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            DisableSelection {
                if(showCancel) {
                    if(customCancel != null) {
                        customCancel()
                    }else {
                        TextButton(
                            onClick = onCancel
                        ) {
                            Text(
                                text = cancelBtnText,
                                color = cancelTextColor,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            DisableSelection {
                if(showOk) {
                    if(customOk != null) {
                        customOk()
                    }else {
                        TextButton(
                            enabled = okBtnEnabled,
                            onClick = {
                                onOk()
                            },
                        ) {
                            Text(
                                text = okBtnText,
                                color = if(okBtnEnabled) okTextColor else Color.Unspecified,
                            )
                        }
                    }
                }
            }
        },
    )
}
