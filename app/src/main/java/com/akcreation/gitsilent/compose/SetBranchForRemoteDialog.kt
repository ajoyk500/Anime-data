package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.git.BranchMode
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StrListUtil
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository

private const val TAG = "SetBranchForRemoteDialog"
@Composable
fun SetBranchForRemoteDialog(
    stateKeyTag:String,
    curRepo: RepoEntity,
    remoteName: String,
    isAllInitValue: Boolean,
    onCancel: () -> Unit,
    onOk: (remoteName:String, isAll: Boolean, branchCsvStr: String) -> Unit
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val activityContext = LocalContext.current
    val selectedOption = mutableCustomStateOf(stateKeyTag, "selectedOption") { if(isAllInitValue) BranchMode.ALL else BranchMode.CUSTOM }
    val branchCsvStr = rememberSaveable { mutableStateOf("")}
    val strListSeparator = Cons.comma
    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.set_branch_mode))
        },
        text = {
            ScrollableColumn {
                SelectionRow {
                    Text(text = stringResource(R.string.remote) + ": ")
                    Text(text = remoteName,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                SelectionRow {
                    Text(text = stringResource(R.string.branch_mode_note))
                }
                Spacer(modifier = Modifier.height(20.dp))
                SingleSelection(
                    itemList = BranchMode.entries,
                    selected = {idx, item -> selectedOption.value == item},
                    text = {idx, item -> activityContext.getString(if(item == BranchMode.ALL) R.string.all else R.string.custom) },
                    onClick = {idx, item -> selectedOption.value = item}
                )
                if(selectedOption.value == BranchMode.CUSTOM) {
                    Spacer(Modifier.height(5.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = branchCsvStr.value,
                        onValueChange = {
                            branchCsvStr.value = it
                        },
                        label = {
                            Text(replaceStringResList(stringResource(R.string.branches_split_by_sign), listOf(strListSeparator)))
                        },
                        placeholder = {
                            Text(stringResource(R.string.branches_placeholder))
                        }
                    )
                }
            }
        },
        onDismissRequest = { onCancel() },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                enabled = (selectedOption.value == BranchMode.ALL) || branchCsvStr.value.isNotBlank(),
                onClick = { onOk(remoteName, selectedOption.value == BranchMode.ALL, branchCsvStr.value) }
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    )
    LaunchedEffect(Unit) {
        doJobThenOffLoading {
            try {
                Repository.open(curRepo.fullSavePath).use { repo ->
                    val remote = Libgit2Helper.resolveRemote(repo, remoteName)
                    if (remote == null) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.err_resolve_remote_failed))
                        return@doJobThenOffLoading
                    }
                    val (isAllRealValue, branchNameList) = Libgit2Helper.getRemoteFetchBranchList(remote)
                    selectedOption.value = if(isAllRealValue) BranchMode.ALL else BranchMode.CUSTOM
                    if (isAllRealValue) {
                        branchCsvStr.value = ""
                    } else {
                        branchCsvStr.value = StrListUtil.listToCsvStr(branchNameList)
                    }
                }
            } catch (e: Exception) {
                Msg.requireShowLongDuration("err: " + e.localizedMessage)
                createAndInsertError(curRepo.id, "err: ${e.localizedMessage}")
                MyLog.e(TAG, "#LaunchedEffect: err: ${e.stackTraceToString()}")
            }
        }
    }
}
