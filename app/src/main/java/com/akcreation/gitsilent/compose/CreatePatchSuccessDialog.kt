package com.akcreation.gitsilent.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.akcreation.gitsilent.play.pro.R
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.replaceStringResList


@Composable
fun CreatePatchSuccessDialog(
    path:String,
    closeDialog:()->Unit,
) {
    val activityContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    CopyableDialog(
        title = stringResource(R.string.success),
        text = replaceStringResList(stringResource(R.string.export_path_ph1_you_can_go_to_files_page_found_this_file), listOf(path)),
        okBtnText = stringResource(R.string.copy_path),
        onCancel = closeDialog
    ) {
        closeDialog()

        clipboardManager.setText(AnnotatedString(path))
        Msg.requireShow(activityContext.getString(R.string.copied))
    }
}
