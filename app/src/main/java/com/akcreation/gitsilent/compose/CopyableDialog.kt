package com.akcreation.gitsilent.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.doJobThenOffLoading

@Composable
fun CopyableDialog(
    title: String="",
    text: String="",
    requireShowTitleCompose:Boolean=false,
    titleCompose:@Composable ()->Unit={},
    requireShowTextCompose:Boolean=false,
    textCompose:@Composable ()->Unit={},
    cancelBtnText: String = stringResource(R.string.close),
    okBtnText: String = stringResource(R.string.copy),
    cancelTextColor: Color = Color.Unspecified,
    okTextColor: Color = Color.Unspecified,
    okBtnEnabled: Boolean=true,
    loadingOn:(loadingText:String)->Unit={},
    loadingOff:()->Unit={},
    loadingText: String= stringResource(R.string.loading),
    onCancel: () -> Unit,
    onOk: suspend () -> Unit,  
) {
    CopyableDialog2(
        title = title,
        text = text,
        requireShowTitleCompose = requireShowTitleCompose,
        titleCompose = titleCompose,
        requireShowTextCompose = requireShowTextCompose,
        textCompose = textCompose,
        cancelBtnText = cancelBtnText,
        okBtnText = okBtnText,
        cancelTextColor = cancelTextColor,
        okTextColor= okTextColor,
        okBtnEnabled= okBtnEnabled,
        loadingOn= loadingOn,
        loadingOff= loadingOff,
        loadingText= loadingText,
        onCancel= onCancel,
        onOk= onOk,
        cancelCompose = {
            TextButton(
                onClick = onCancel
            ) {
                Text(
                    text = cancelBtnText,
                    color = cancelTextColor,
                )
            }
        },
        okCompose = {
            TextButton(
                enabled = okBtnEnabled,
                onClick = {
                    doJobThenOffLoading(loadingOn, loadingOff, loadingText) {
                        onOk()
                    }
                },
            ) {
                Text(
                    text = okBtnText,
                    color = if(okBtnEnabled) okTextColor else Color.Unspecified,
                )
            }
        },
    )
}
@Composable
fun CopyableDialog2(
    title: String="",
    text: String="",
    requireShowTitleCompose:Boolean=false,
    titleCompose:@Composable ()->Unit={},
    requireShowTextCompose:Boolean=false,
    textCompose:@Composable ()->Unit={},
    cancelBtnText: String = stringResource(R.string.close),
    okBtnText: String = stringResource(R.string.copy),
    cancelTextColor: Color = Color.Unspecified,
    okTextColor: Color = Color.Unspecified,
    okBtnEnabled: Boolean=true,
    loadingOn:(loadingText:String)->Unit={},
    loadingOff:()->Unit={},
    loadingText: String= stringResource(R.string.loading),
    cancelCompose: (@Composable ()->Unit)? = null,
    okCompose: (@Composable ()->Unit)? = null,
    onCancel: () -> Unit,
    onDismiss: ()->Unit = onCancel,
    onOk: suspend () -> Unit,  
) {
    AlertDialog(
        title = {
            MySelectionContainer {
                if(requireShowTitleCompose) {
                    titleCompose()
                }else {
                    DialogTitle(title)
                }
            }
        },
        text = {
            MySelectionContainer {
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
            if(cancelCompose == null) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text(
                        text = cancelBtnText,
                        color = cancelTextColor,
                    )
                }
            }else {
                cancelCompose()
            }
        },
        confirmButton = {
            if(okCompose == null) {
                TextButton(
                    enabled = okBtnEnabled,
                    onClick = {
                        doJobThenOffLoading(loadingOn, loadingOff, loadingText) {
                            onOk()
                        }
                    },
                ) {
                    Text(
                        text = okBtnText,
                        color = if(okBtnEnabled) okTextColor else Color.Unspecified,
                    )
                }
            }else {
                okCompose()
            }
        },
    )
}
