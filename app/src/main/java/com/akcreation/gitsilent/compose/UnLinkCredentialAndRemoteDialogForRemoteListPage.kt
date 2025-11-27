package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.doJobThenOffLoading

@Composable
fun UnLinkCredentialAndRemoteDialogForRemoteListPage(
    remoteId:String,
    remoteName:String,
    onCancel: () -> Unit,
    onOkCallback:()->Unit,
) {
    val fetchChecked = rememberSaveable { mutableStateOf(true) }
    val pushChecked = rememberSaveable { mutableStateOf(true) }
    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.unlink))
        },
        text = {
            ScrollableColumn {
                SelectionRow {
                    Text(text = stringResource(id = R.string.remote)+": ")
                    Text(text = remoteName,
                        fontWeight = FontWeight.ExtraBold,
                        overflow = TextOverflow.Visible
                    )
                }
                Spacer(Modifier.height(10.dp))
                MyCheckBox(text = stringResource(R.string.fetch), value = fetchChecked)
                MyCheckBox(text = stringResource(R.string.push), value = pushChecked)
            }
        },
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                enabled = fetchChecked.value || pushChecked.value,  
                onClick = onOk@{
                    if(!fetchChecked.value && !pushChecked.value) {
                        return@onOk
                    }
                    val remoteDb = AppModel.dbContainer.remoteRepository
                    val emptyId = ""
                    doJobThenOffLoading {
                        try {
                            if(fetchChecked.value && pushChecked.value) {
                                remoteDb.updateFetchAndPushCredentialIdByRemoteId(remoteId, emptyId, emptyId)
                            }else if(fetchChecked.value) {
                                remoteDb.updateCredentialIdByRemoteId(remoteId, emptyId)
                            }else {  
                                remoteDb.updatePushCredentialIdByRemoteId(remoteId, emptyId)
                            }
                        }finally {
                            onOkCallback()
                        }
                    }
                }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
