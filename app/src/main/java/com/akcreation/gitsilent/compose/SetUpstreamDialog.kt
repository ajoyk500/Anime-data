package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.github.git24j.core.Repository

@Composable
fun SetUpstreamDialog(
    callerTag:String, 
    remoteList:List<String>,  
    selectedOption: MutableIntState,  
    upstreamBranchShortName: MutableState<String>,
    upstreamBranchShortNameSameWithLocal: MutableState<Boolean>,
    onOkText:String = stringResource(R.string.save),
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    curRepo:RepoEntity,
    curBranchShortName:String, 
    curBranchFullName:String,
    isCurrentBranchOfRepo:Boolean,
    showClear:Boolean,
    closeDialog: () -> Unit,  
    onCancel:() -> Unit = closeDialog, 
    onClearErrorCallback:suspend (Exception) -> Unit,
    onClearFinallyCallback:(() -> Unit)?,
    onClearSuccessCallback: suspend () -> Unit,
    onErrorCallback:suspend (Exception) -> Unit,
    onFinallyCallback:(() -> Unit)?,
    onSuccessCallback: suspend () -> Unit,
) {
    val funName = remember {"SetUpstreamDialog"}
    val activityContext = LocalContext.current
    val onClear = {
        closeDialog()
        val repoFullPath = curRepo.fullSavePath
        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
            try {
                Repository.open(repoFullPath).use { repo ->
                    Libgit2Helper.clearUpstreamForBranch(repo, curBranchShortName)
                }
                onClearSuccessCallback()
            } catch (e: Exception) {
                onClearErrorCallback(e)
            } finally {
                onClearFinallyCallback?.invoke()
            }
        }
    }
    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.set_upstream_title))
        },
        text = {
            ScrollableColumn {
                SelectionRow {
                    Text(text = stringResource(R.string.set_upstream_for_branch)+":")
                }
                SelectionRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MyStyleKt.defaultHorizontalPadding)
                    ,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = curBranchShortName, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(15.dp))
                SelectionRow {
                    Text(text = stringResource(R.string.select_a_remote)+":")
                }
                Spacer(Modifier.height(5.dp))
                if(remoteList.isEmpty()) {  
                    SelectionRow {
                        Text(
                            text = stringResource(R.string.err_remote_list_is_empty),
                            color = MyStyleKt.TextColor.error()
                        )
                    }
                }else{
                    SingleSelectList(
                        basePadding = { PaddingValues(0.dp) },
                        optionsList = remoteList,
                        selectedOptionIndex = selectedOption
                    )
                }
                Spacer(Modifier.height(20.dp))
                SelectionRow {
                    Text(stringResource(R.string.upstream_branch_name)+":")
                }
                Spacer(Modifier.height(5.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    enabled = !upstreamBranchShortNameSameWithLocal.value,
                    value = upstreamBranchShortName.value,
                    singleLine = true,
                    onValueChange = {
                        upstreamBranchShortName.value = it
                    },
                    placeholder = {
                        Text(stringResource(R.string.branch_name))
                    }
                )
                Spacer(Modifier.height(10.dp))
                MyCheckBox(stringResource(R.string.same_with_local), upstreamBranchShortNameSameWithLocal)
            }
        },
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                onClick = onOk@{
                    closeDialog()
                    val repoId = curRepo.id
                    val repoName = curRepo.repoName
                    val repoFullPath = curRepo.fullSavePath
                    val upstreamSameWithLocal = upstreamBranchShortNameSameWithLocal.value
                    val selectedRemoteIndex = selectedOption.intValue
                    val upstreamShortName = upstreamBranchShortName.value
                    val remote = try {
                        remoteList[selectedRemoteIndex]
                    } catch (e: Exception) {
                        MyLog.e(callerTag,"#$funName: err when get remote by index from remote list of '$repoName': remoteIndex=$selectedRemoteIndex, remoteList=$remoteList\nerr info:${e.stackTraceToString()}")
                        Msg.requireShowLongDuration(activityContext.getString(R.string.err_selected_remote_is_invalid))
                        return@onOk
                    }
                    doJobThenOffLoading {
                        try {
                            var branch = ""
                            var setUpstreamSuccess = false
                            Repository.open(repoFullPath).use { repo ->
                                branch = if (upstreamSameWithLocal) {  
                                    curBranchFullName
                                } else {  
                                    Libgit2Helper.getRefsHeadsBranchFullRefSpecFromShortRefSpec(upstreamShortName)
                                }
                                MyLog.d(callerTag, "#$funName: set upstream dialog #onOk(): repo is '$repoName', will write to git config: remote=$remote, branch=$branch")
                                setUpstreamSuccess = Libgit2Helper.setUpstreamForBranchByRemoteAndRefspec(
                                    repo = repo,
                                    remote = remote,
                                    fullBranchRefSpec = branch,
                                    targetBranchShortName = curBranchShortName
                                )
                            }
                            if (isCurrentBranchOfRepo) {
                                val upstreamBranchShortName = Libgit2Helper.getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(remote, branch)
                                MyLog.d(callerTag, "#$funName: set upstream dialog #onOk(): upstreamBranchShortName=$upstreamBranchShortName")
                                AppModel.dbContainer.repoRepository.updateUpstream(repoId, upstreamBranchShortName)
                            }
                            if (setUpstreamSuccess) {
                                onSuccessCallback()
                            } else {
                                throw RuntimeException("unknown error, code '1c3f943a8e'")  
                            }
                        } catch (e: Exception) {
                            onErrorCallback(e)
                        } finally {
                            onFinallyCallback?.invoke()
                        }
                    }
                },
                enabled = (!(!upstreamBranchShortNameSameWithLocal.value && upstreamBranchShortName.value.isBlank())) && remoteList.isNotEmpty(),
            ) {
                Text(text = onOkText)
            }
        },
        dismissButton = {
            ScrollableRow {
                if(showClear) {
                    TextButton(
                        onClick = {
                            onClear()
                        }
                    ) {
                        Text(stringResource(id = R.string.clear), color = MyStyleKt.TextColor.danger())
                    }
                }
                TextButton(
                    onClick = {
                        onCancel()
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        }
    )
}
