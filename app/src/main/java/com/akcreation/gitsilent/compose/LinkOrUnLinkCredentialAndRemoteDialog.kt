package com.akcreation.gitsilent.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.dto.RemoteDtoForCredential
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.state.CustomStateSaveable

@Composable
fun LinkOrUnLinkCredentialAndRemoteDialog(
    curItemInPage:CustomStateSaveable<CredentialEntity>,
    requireDoLink:Boolean, 
    targetAll:Boolean,
    title:String,
    thisItem: RemoteDtoForCredential,
    onCancel: () -> Unit,
    onErrCallback:suspend (err:Exception)->Unit,
    onFinallyCallback:()->Unit,
    onOkCallback:()->Unit,
) {
    val fetchChecked = rememberSaveable { mutableStateOf(if(targetAll) true else if(requireDoLink) thisItem.credentialId!=curItemInPage.value.id else thisItem.credentialId==curItemInPage.value.id)}
    val pushChecked = rememberSaveable { mutableStateOf(if(targetAll) true else if(requireDoLink) thisItem.pushCredentialId!=curItemInPage.value.id else thisItem.pushCredentialId==curItemInPage.value.id)}
    AlertDialog(
        title = {
            DialogTitle(title.ifEmpty { if (requireDoLink) stringResource(R.string.link) else stringResource(R.string.unlink) })
        },
        text = {
            ScrollableColumn {
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
                    val remoteId = thisItem.remoteId
                    val curCredentialId = curItemInPage.value.id
                    val remoteDb = AppModel.dbContainer.remoteRepository
                    doJobThenOffLoading {
                        try {
                            val targetCredentialId = if(requireDoLink) curCredentialId else SpecialCredential.NONE.credentialId;
                            if(requireDoLink) {  
                                if(targetAll) {
                                    if(fetchChecked.value && pushChecked.value) {
                                        remoteDb.updateAllFetchAndPushCredentialId(targetCredentialId, targetCredentialId)
                                    }else if(fetchChecked.value) {
                                        remoteDb.updateAllFetchCredentialId(targetCredentialId)
                                    }else {  
                                        remoteDb.updateAllPushCredentialId(targetCredentialId)
                                    }
                                }else {
                                    if(fetchChecked.value && pushChecked.value) {
                                        remoteDb.updateFetchAndPushCredentialIdByRemoteId(remoteId, targetCredentialId, targetCredentialId)
                                    }else if(fetchChecked.value) {
                                        remoteDb.updateCredentialIdByRemoteId(remoteId, targetCredentialId)
                                    }else {  
                                        remoteDb.updatePushCredentialIdByRemoteId(remoteId, targetCredentialId)
                                    }
                                }
                            }else {  
                                if(targetAll) {  
                                    if(fetchChecked.value && pushChecked.value) {
                                        remoteDb.updateFetchAndPushCredentialIdByCredentialId(curCredentialId, curCredentialId, targetCredentialId, targetCredentialId)
                                    }else if(fetchChecked.value) {
                                        remoteDb.updateCredentialIdByCredentialId(curCredentialId, targetCredentialId)
                                    }else {  
                                        remoteDb.updatePushCredentialIdByCredentialId(curCredentialId, targetCredentialId)
                                    }
                                }else {  
                                    if(fetchChecked.value && pushChecked.value) {
                                        remoteDb.updateFetchAndPushCredentialIdByRemoteId(remoteId, targetCredentialId, targetCredentialId)
                                    }else if(fetchChecked.value) {
                                        remoteDb.updateCredentialIdByRemoteId(remoteId, targetCredentialId)
                                    }else {  
                                        remoteDb.updatePushCredentialIdByRemoteId(remoteId, targetCredentialId)
                                    }
                                }
                            }
                            onOkCallback()
                        }catch (e:Exception){
                            onErrCallback(e)
                        }finally {
                            onFinallyCallback()
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
