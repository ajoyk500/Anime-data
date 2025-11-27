package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.github.git24j.core.Repository

private const val TAG = "AskGitUsernameAndEmailDialogWithSelection"
@Composable
fun AskGitUsernameAndEmailDialogWithSelection(
    curRepo: RepoEntity,
    username: MutableState<String>,
    email: MutableState<String>,
    closeDialog: () -> Unit,  
    onCancel:() -> Unit = closeDialog, 
    onErrorCallback:suspend (Exception) -> Unit,
    onFinallyCallback:() -> Unit,
    onSuccessCallback: suspend () -> Unit,
) {
    val activityContext = LocalContext.current
    val setUserAndEmailForGlobal = stringResource(R.string.set_for_global)
    val setUserAndEmailForCurRepo = stringResource(R.string.set_for_current_repo) + " (${curRepo.repoName})"
    val errWhenQuerySettingsFromDbStrRes = stringResource(R.string.err_when_querying_settings_from_db)
    val invalidUsernameOrEmail = stringResource(R.string.invalid_username_or_email)
    val optNumSetUserAndEmailForGlobal = 0  
    val optNumSetUserAndEmailForCurRepo = 1  
    val optionsList = listOf(  
        setUserAndEmailForGlobal,  
        setUserAndEmailForCurRepo
    )
    val selectedOption = rememberSaveable{ mutableIntStateOf(optNumSetUserAndEmailForGlobal) }
    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.user_info))
        },
        text = {
            ScrollableColumn {
                SingleSelection(
                    itemList = optionsList,
                    selected = {idx, item -> selectedOption.intValue == idx},
                    text = {idx, item -> item},
                    onClick = {idx, item -> selectedOption.intValue = idx}
                )
                Row(modifier = Modifier.padding(5.dp)) {
                }
                TextField(
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    value = username.value,
                    onValueChange = {
                        username.value = it
                    },
                    label = {
                        Text(stringResource(R.string.username))
                    },
                    placeholder = {
                        Text(stringResource(R.string.username))
                    }
                )
                Row(modifier = Modifier.padding(5.dp)) {
                }
                TextField(
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    value = email.value,
                    onValueChange = {
                        email.value = it
                    },
                    label = {
                        Text(stringResource(R.string.email))
                    },
                    placeholder = {
                        Text(stringResource(R.string.email))
                    }
                )
            }
        },
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    closeDialog()
                    val usernameValue = username.value
                    val emailValue = email.value
                    val forGlobal = selectedOption.intValue == optNumSetUserAndEmailForGlobal
                    doJobThenOffLoading {
                        try {
                            if(usernameValue.isBlank() || emailValue.isBlank()) {
                                throw RuntimeException(invalidUsernameOrEmail)
                            }
                            if (forGlobal) {  
                                if (!Libgit2Helper.saveGitUsernameAndEmailForGlobal(
                                        requireShowErr = Msg.requireShowLongDuration,
                                        errText = errWhenQuerySettingsFromDbStrRes,
                                        errCode1 = "1",
                                        errCode2 = "2",   
                                        username = usernameValue,
                                        email = emailValue
                                    )
                                ) {
                                    throw RuntimeException("set username and email for global err")
                                }
                            } else {  
                                Repository.open(curRepo.fullSavePath).use { repo ->
                                    if (!Libgit2Helper.saveGitUsernameAndEmailForRepo(
                                            repo = repo,
                                            requireShowErr = Msg.requireShowLongDuration,
                                            username = usernameValue,
                                            email = emailValue
                                        )
                                    ) {
                                        throw RuntimeException("set username and email for repo err")
                                    }
                                }
                            }
                            Msg.requireShow(activityContext.getString(R.string.saved))
                            onSuccessCallback()
                        }catch (e:Exception) {
                            onErrorCallback(e)
                        }finally {
                            onFinallyCallback()
                        }
                    }
                },
                enabled = username.value.isNotBlank() && email.value.isNotBlank()
            ) {
                Text(stringResource(id = R.string.save))
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
