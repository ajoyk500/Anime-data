package com.akcreation.gitsilent.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.goToTreeToTreeChangeList
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "DiffCommitsDialog"
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiffCommitsDialog(
    showDialog:MutableState<Boolean>,
    commit1: CustomStateSaveable<TextFieldValue>,
    commit2: CustomStateSaveable<TextFieldValue>,
    trueFocusCommit1FalseFocus2:Boolean,
    curRepo:RepoEntity,
) {
    val repoId = curRepo.id
    val activityContext = LocalContext.current
    val navController = AppModel.navController
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val commit1FocusRequest = remember { FocusRequester() }
    val commit2FocusRequest = remember { FocusRequester() }
    ConfirmDialog(title = activityContext.getString(R.string.diff_commits),
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                MySelectionContainer {
                    Text(text = stringResource(R.string.compare_left_to_right))
                }
                Spacer(modifier = Modifier.height(10.dp))
                MySelectionContainer {
                    Text(text = stringResource(R.string.note_leave_commit_hash_empty_to_compare_with_local_worktree), fontWeight = FontWeight.Light)
                }
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(commit1FocusRequest)
                    ,
                    value = commit1.value,
                    singleLine = true,
                    onValueChange = {
                        commit1.value = it
                    },
                    label = {
                        Text(stringResource(R.string.left))
                    },
                    placeholder = {
                        Text(stringResource(R.string.hash_branch_tag))
                    },
                )
                Row(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .fillMaxWidth()
                        .combinedClickable(
                        onLongClick = {
                            Msg.requireShow(activityContext.getString(R.string.swap))
                        }
                    ) { 
                        val tmp = commit1.value
                        commit1.value = commit2.value
                        commit2.value = tmp
                    }
                        .padding(vertical = 5.dp)
                    ,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.SwapVert,
                        contentDescription = stringResource(R.string.swap),
                    )
                }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(commit2FocusRequest)
                    ,
                    value = commit2.value,
                    singleLine = true,
                    onValueChange = {
                        commit2.value = it
                    },
                    label = {
                        Text(stringResource(R.string.right))
                    },
                    placeholder = {
                        Text(stringResource(R.string.hash_branch_tag))
                    },
                )
            }
        },
        okBtnText = stringResource(id = R.string.ok),
        cancelBtnText = stringResource(id = R.string.cancel),
        onCancel = {
            showDialog.value = false
        }
    ) ok@{  
        val commit1 = commit1.value.text.ifBlank { Cons.git_LocalWorktreeCommitHash }
        val commit2 = commit2.value.text.ifBlank { Cons.git_LocalWorktreeCommitHash }
        if(Libgit2Helper.CommitUtil.isSameCommitHash(commit1, commit2)) {
            Msg.requireShow(activityContext.getString(R.string.num2_commits_same))
            return@ok
        }
        showDialog.value = false
        goToTreeToTreeChangeList(
            title = activityContext.getString(R.string.diff_commits),
            repoId = repoId,
            commit1 = commit1,
            commit2 = commit2,
            commitForQueryParents = Cons.git_AllZeroOidStr,
        )
    }
    Focuser(if(trueFocusCommit1FalseFocus2) commit1FocusRequest else commit2FocusRequest, scope)
}
