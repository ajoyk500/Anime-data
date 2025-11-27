package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.github.git24j.core.Repository

private const val TAG = "CreateTagDialog"
@Composable
fun CreateTagDialog(
    showDialog:MutableState<Boolean>,
    curRepo:RepoEntity,
    tagName:MutableState<String>,
    commitHashShortOrLong:MutableState<String>,
    annotate:MutableState<Boolean>,
    tagMsg:MutableState<String>,
    force:MutableState<Boolean>,  
    onOkDoneCallback:(newTagFullOidStr:String)->Unit
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val isKeyboardVisible = rememberSaveable { mutableStateOf(false) }
    val isKeyboardCoveredComponent = rememberSaveable { mutableStateOf(false) }
    val componentHeight = rememberSaveable { mutableIntStateOf(0) }
    val keyboardPaddingDp = rememberSaveable { mutableIntStateOf(0) }
    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        skipCondition = { false }
    )
    val activityContext = LocalContext.current
    val tagNameErrMsg = rememberSaveable { mutableStateOf("") }
    val commitHashShortOrLongErrMsg = rememberSaveable { mutableStateOf("") }
    val tagMsgErrMsg = rememberSaveable { mutableStateOf("") }
    val gitConfigUsername = rememberSaveable { mutableStateOf("") }
    val gitConfigEmail = rememberSaveable { mutableStateOf("") }
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    ConfirmDialog2(
        title = activityContext.getString(R.string.new_tag),
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                Row(modifier = Modifier.padding(5.dp)) {
                }
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = tagName.value,
                    singleLine = true,
                    onValueChange = {
                        tagName.value = it
                        tagNameErrMsg.value=""
                    },
                    label = {
                        Text(stringResource(R.string.tag_name))
                    },
                    isError = tagNameErrMsg.value.isNotEmpty(),
                    supportingText = {
                        if(tagNameErrMsg.value.isNotEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = tagNameErrMsg.value,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if(tagNameErrMsg.value.isNotEmpty()) {
                            Icon(imageVector= Icons.Filled.Error,
                                contentDescription="err icon",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    },
                )
                Row(modifier = Modifier.padding(5.dp)) {
                }
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = commitHashShortOrLong.value,
                    singleLine = true,
                    onValueChange = {
                        commitHashShortOrLong.value = it
                        commitHashShortOrLongErrMsg.value=""
                    },
                    label = {
                        Text(stringResource(R.string.target))
                    },
                    placeholder = {
                        Text(stringResource(R.string.hash_branch_tag))
                    },
                    isError = commitHashShortOrLongErrMsg.value.isNotEmpty(),
                    supportingText = {
                        if(commitHashShortOrLongErrMsg.value.isNotEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = commitHashShortOrLongErrMsg.value,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if(commitHashShortOrLongErrMsg.value.isNotEmpty()) {
                            Icon(imageVector= Icons.Filled.Error,
                                contentDescription="err icon",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    },
                )
                MyCheckBox(text = stringResource(R.string.annotate), value = annotate)
                if(annotate.value) {
                    if(gitConfigUsername.value.isBlank() || gitConfigEmail.value.isBlank()) {  
                        MySelectionContainer {
                            DefaultPaddingText(
                                text = stringResource(R.string.err_must_set_username_and_email_before_create_annotate_tag),
                                color = MyStyleKt.TextColor.error()
                            )
                        }
                    }else {  
                        Row(modifier = Modifier.padding(5.dp)) {
                        }
                        TextField(
                            modifier = Modifier.fillMaxWidth()
                                .onGloballyPositioned { layoutCoordinates ->
                                    componentHeight.intValue = layoutCoordinates.size.height
                                }
                                .then(
                                    if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                                )
                            ,
                            value = tagMsg.value,
                            onValueChange = {
                                tagMsg.value = it
                                tagMsgErrMsg.value=""
                            },
                            label = {
                                Text(stringResource(R.string.tag_msg))
                            },
                            isError = tagMsgErrMsg.value.isNotEmpty(),
                            supportingText = {
                                if(tagMsgErrMsg.value.isNotEmpty()) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = tagMsgErrMsg.value,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            trailingIcon = {
                                if(tagMsgErrMsg.value.isNotEmpty()) {
                                    Icon(imageVector= Icons.Filled.Error,
                                        contentDescription="err icon",
                                        tint = MaterialTheme.colorScheme.error)
                                }
                            },
                        )
                    }
                }
                MyCheckBox(text = stringResource(R.string.force), value = force)
                if(force.value) {
                    MySelectionContainer {
                        Row {
                            DefaultPaddingText(
                                text = stringResource(R.string.warn_will_override_if_tag_name_already_exists),
                                color = MyStyleKt.TextColor.danger()
                            )
                        }
                    }
                }
            }
        },
        okBtnEnabled = tagNameErrMsg.value.isEmpty() && commitHashShortOrLongErrMsg.value.isEmpty() && tagMsgErrMsg.value.isEmpty() && (!annotate.value || (gitConfigUsername.value.isNotBlank() && gitConfigEmail.value.isNotBlank())),
        onCancel = {
            showDialog.value = false
        }
    ) onOk@{
        if(tagName.value.isBlank()) {
            tagNameErrMsg.value = activityContext.getString(R.string.tag_name_is_empty)
            return@onOk
        }
        if(commitHashShortOrLong.value.isBlank()) {
            commitHashShortOrLongErrMsg.value = activityContext.getString(R.string.commit_hash_is_empty)
            return@onOk
        }
        if(annotate.value) {
            if(gitConfigUsername.value.isBlank() || gitConfigEmail.value.isBlank()) {
                Msg.requireShowLongDuration(activityContext.getString(R.string.plz_set_git_username_and_email_first))
                return@onOk
            }
            if(tagMsg.value.isBlank()) {
                tagMsgErrMsg.value = activityContext.getString(R.string.tag_msg_is_empty)
                return@onOk
            }
        }
        doJobThenOffLoading job@{
            try {
                Repository.open(curRepo.fullSavePath).use { repo->
                    val commit = Libgit2Helper.resolveCommitByHashOrRef(repo, commitHashShortOrLong.value).data
                    if(commit==null) {
                        commitHashShortOrLongErrMsg.value = activityContext.getString(R.string.invalid_commit_hash)
                        return@job
                    }
                    showDialog.value=false
                    if(annotate.value) {
                        Libgit2Helper.createTagAnnotated(
                            repo,
                            tagName.value,
                            commit,
                            tagMsg.value,
                            gitConfigUsername.value,
                            gitConfigEmail.value,
                            force.value,
                            settings
                        )
                    }else {
                        Libgit2Helper.createTagLight(
                            repo,
                            tagName.value,
                            commit,
                            force.value
                        )
                    }
                    Msg.requireShowLongDuration(activityContext.getString(R.string.success))
                    onOkDoneCallback(commit?.id()?.toString() ?: "")  
                }
            }catch (e:Exception) {
                val errMsg = "create tag '${tagName.value}' err: ${e.localizedMessage}"
                Msg.requireShowLongDuration(errMsg)
                createAndInsertError(curRepo.id, errMsg)
                MyLog.e(TAG, "#onOk err: $errMsg\n${e.stackTraceToString()}")
            }
        }
    }
    LaunchedEffect(Unit) {
        doJobThenOffLoading {
            Repository.open(curRepo.fullSavePath).use { repo->
                val (username, email) = Libgit2Helper.getGitUsernameAndEmail(repo)
                gitConfigUsername.value = username
                gitConfigEmail.value = email
            }
        }
    }
}
