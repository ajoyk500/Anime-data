package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.state.mutableCustomBoxOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository

private const val TAG = "RequireCommitMsgDialog"
@Composable
fun RequireCommitMsgDialog(
    stateKeyTag:String,
    curRepo:RepoEntity,
    repoPath:String,
    repoState:Int,
    overwriteAuthor:MutableState<Boolean>,
    amend:MutableState<Boolean>,
    commitMsg: CustomStateSaveable<TextFieldValue>,
    indexIsEmptyForCommitDialog:MutableState<Boolean>,
    showPush:Boolean,
    showSync:Boolean,
    commitBtnText:String = stringResource(R.string.commit),
    onOk: (curRepo: RepoEntity, msg:String, requirePush:Boolean, requireSync:Boolean) -> Unit,
    onCancel: (curRepo: RepoEntity) -> Unit,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val activityContext = LocalContext.current
    val repoStateIsRebase= repoState == Repository.StateT.REBASE_MERGE.bit
    val repoStateIsCherrypick = repoState == Repository.StateT.CHERRYPICK.bit
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val amendMsg = mutableCustomStateOf(stateKeyTag, "amendMsg") { TextFieldValue("") }
    val getCommitMsg = {
        if(amend.value) amendMsg.value.text else commitMsg.value.text
    }
    val amendMsgAlreadySetOnce = mutableCustomBoxOf(stateKeyTag, "amendMsgAlreadySetOnce") { false }
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
        paddingAdjustValue = 180.dp,
        skipCondition = { false }
    )
    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.commit_message))
        },
        text = {
            ScrollableColumn {
                if(repoState==Repository.StateT.NONE.bit && amend.value.not() && indexIsEmptyForCommitDialog.value) {
                    MySelectionContainer {
                        Row(modifier = Modifier.padding(5.dp)) {
                            Text(text = stringResource(R.string.warn_index_is_empty_will_create_a_empty_commit), color = MyStyleKt.TextColor.danger())
                        }
                    }
                }
                TextField(
                    maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                    modifier = Modifier.fillMaxWidth()
                        .onGloballyPositioned { layoutCoordinates ->
                            componentHeight.intValue = layoutCoordinates.size.height
                        }
                        .then(
                            if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                        )
                    ,
                    value = if(amend.value) amendMsg.value else commitMsg.value,
                    onValueChange = {
                        if(amend.value) {
                            amendMsg.value = it
                        }else {
                            commitMsg.value = it
                        }
                    },
                    label = {
                        Text(stringResource(R.string.commit_message))
                    },
                    placeholder = {
                        Text(stringResource(R.string.input_your_commit_message))
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                MySelectionContainer {
                    Column {
                        if(repoStateIsRebase || repoStateIsCherrypick || amend.value) {
                            MultiLineClickableText(stringResource(R.string.leave_msg_empty_will_use_origin_commit_s_msg)) {
                                Repository.open(repoPath).use { repo ->
                                    val oldMsg = if (repoStateIsRebase) Libgit2Helper.rebaseGetCurCommitMsg(repo) else if(repoStateIsCherrypick) Libgit2Helper.getCherryPickHeadCommitMsg(repo) else Libgit2Helper.getHeadCommitMsg(repo)
                                    if(amend.value) {
                                        amendMsg.value = TextFieldValue(oldMsg)
                                    }else {
                                        commitMsg.value = TextFieldValue(oldMsg)
                                    }
                                }
                            }
                        }else {
                            MultiLineClickableText(stringResource(R.string.you_can_leave_msg_empty_will_auto_gen_one)) {
                                Repository.open(repoPath).use { repo ->
                                    commitMsg.value = TextFieldValue(Libgit2Helper.genCommitMsgNoFault(repo, itemList = null, settings.commitMsgTemplate))
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
                if(repoState == Repository.StateT.NONE.bit) {
                    MyCheckBox(text = stringResource(R.string.amend), value = amend, onValueChange = { amendOn ->
                        if(amendOn && amendMsgAlreadySetOnce.value.not() && amendMsg.value.text.isEmpty()) {
                            amendMsgAlreadySetOnce.value = true
                            runCatching {
                                Repository.open(repoPath).use { repo ->
                                    amendMsg.value = TextFieldValue(Libgit2Helper.getHeadCommitMsg(repo))
                                }
                            }
                        }
                        amend.value = amendOn
                    })
                }
                if(repoStateIsRebase || repoStateIsCherrypick || amend.value) {
                    MyCheckBox(text = stringResource(R.string.overwrite_author), value = overwriteAuthor)
                }
                if(overwriteAuthor.value) {
                    MySelectionContainer {
                        DefaultPaddingText(text = stringResource(R.string.will_use_your_username_and_email_overwrite_original_commits_author_info))
                    }
                }
            }
        },
        onDismissRequest = {
            onCancel(curRepo)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val requirePush = false
                    val requireSync = false
                    onOk(curRepo, getCommitMsg(), requirePush, requireSync)
                },
                enabled = true
            ) {
                Text(commitBtnText)
            }
        },
        dismissButton = {
            ScrollableRow {
                if(showSync) {
                    TextButton(
                        onClick = {
                            val requirePush = false
                            val requireSync = true
                            onOk(curRepo, getCommitMsg(), requirePush, requireSync)
                        }
                    ) {
                        Text(stringResource(id = R.string.sync))
                    }
                }
                if(showPush) {
                    TextButton(
                        onClick = {
                            val requirePush = true
                            val requireSync = false
                            onOk(curRepo, getCommitMsg(), requirePush, requireSync)
                        }
                    ) {
                        Text(stringResource(R.string.push))
                    }
                }
                TextButton(
                    onClick = {
                        onCancel(curRepo)
                    }
                ) {
                    Text(stringResource(R.string.cancel), color = MyStyleKt.TextColor.danger())
                }
            }
        }
    )
}
