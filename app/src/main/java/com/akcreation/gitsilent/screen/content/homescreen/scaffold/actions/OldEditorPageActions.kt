package com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.state.CustomStateSaveable

@Composable
fun OldEditorPageActions(
    editorPageShowingFilePath: MutableState<String>,
    editorPageShowingFileIsReady: MutableState<Boolean>,
    needRefreshEditorPage: MutableState<String>,
    editorPageTextEditorState: CustomStateSaveable<TextEditorState>,
    isSaving: MutableState<Boolean>,
    isEdited: MutableState<Boolean>,
    showReloadDialog: MutableState<Boolean>,
    showCloseDialog: MutableState<Boolean>,
    closeDialogCallback:CustomStateSaveable<(Boolean)->Unit>,
    doSave:suspend ()->Unit,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    editorPageRequest:MutableState<String>,
) {
    if (editorPageShowingFilePath.value.isNotBlank()) {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.close),
            icon = Icons.Filled.Close,
            iconContentDesc = stringResource(id = R.string.close),
        ) {
            showCloseDialog.value=true
        }
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.reload_file),
            icon = Icons.Filled.Refresh,
            iconContentDesc = stringResource(id = R.string.reload_file),
        ) {
            showReloadDialog.value = true
        }
        LongPressAbleIconBtn(
            enabled = editorPageShowingFileIsReady.value && isEdited.value && !isSaving.value,  
            tooltipText = stringResource(R.string.save),
            icon = Icons.Filled.Save,
            iconContentDesc = stringResource(id = R.string.save),
        ) {
            editorPageRequest.value = PageRequest.requireSave
        }
        LongPressAbleIconBtn(
            enabled = true,  
            tooltipText = stringResource(R.string.open_as),
            icon = Icons.AutoMirrored.Filled.OpenInNew,
            iconContentDesc = stringResource(id = R.string.open_as),
        ) {
            editorPageRequest.value = PageRequest.requireOpenAs
        }
    }
}
