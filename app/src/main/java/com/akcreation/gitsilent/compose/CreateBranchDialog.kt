package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.forceCheckoutTestPassed
import com.akcreation.gitsilent.dev.overwriteExistWhenCreateBranchTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository

private const val TAG = "CreateBranchDialog"
@Composable
fun CreateBranchDialog(
    title: String = stringResource(R.string.create_branch),
    curBranchName:String,
    branchName: MutableState<String>,
    requireCheckout: MutableState<Boolean>,
    forceCheckout:MutableState<Boolean>,
    curRepo: RepoEntity,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    loadingText:String,
    onCancel:()->Unit,
    onErr:suspend (e:Exception)->Unit, 
    onFinally:()->Unit, 
) {
    val activityContext = LocalContext.current
    val repoId = curRepo.id
    val optHEAD = 0;
    val optCommit = 1;
    val selectedOption = rememberSaveable{ mutableIntStateOf(optHEAD) }
    val createMethodList = listOf(activityContext.getString(R.string.head), activityContext.getString(R.string.commit))
    val userInputHash = rememberSaveable { mutableStateOf("") }
    val overwriteIfExist = rememberSaveable { mutableStateOf(false) }
    val doCreateBranch:suspend (String,Boolean,String, Boolean, Boolean)-> Ret<Triple<String, String, String>?> = doCreateBranch@{ branchNameParam:String, basedHead:Boolean, baseRefSpec:String, createByRef:Boolean, overwriteIfExist:Boolean ->
        Repository.open(curRepo.fullSavePath).use { repo ->
            val ret = Libgit2Helper.doCreateBranch(activityContext, repo, repoId, branchNameParam, basedHead, baseRefSpec, createByRef, overwriteIfExist)
            return@doCreateBranch ret
        }
    }
    val doCheckoutBranch: suspend (String, String, String, force:Boolean) -> Ret<Oid?> = doCheckoutLocalBranch@{ shortBranchName:String, fullBranchName:String, upstreamBranchShortNameParam:String, force:Boolean ->
        Repository.open(curRepo.fullSavePath).use { repo ->
            val ret = Libgit2Helper.doCheckoutBranchThenUpdateDb(
                repo,
                repoId,
                shortBranchName,
                fullBranchName,
                upstreamBranchShortNameParam,
                Cons.checkoutType_checkoutRefThenUpdateHead,  
                force=force,
                updateHead = true 
            )
            return@doCheckoutLocalBranch ret
        }
    }
    AlertDialog(
        title = {
            DialogTitle(title)
        },
        text = {
            ScrollableColumn {
                SelectionRow {
                    Text(text = stringResource(R.string.create_branch_based_on) + ":")
                }
                Spacer(Modifier.height(5.dp))
                SingleSelection(
                    itemList = createMethodList,
                    selected = {idx, item -> selectedOption.intValue == idx},
                    text = {idx, item -> item + (if(idx == optHEAD) " ($curBranchName)" else "") },
                    onClick = {idx, item -> selectedOption.intValue = idx}
                )
                if(selectedOption.intValue == optCommit) {
                    Spacer(Modifier.height(5.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = userInputHash.value,
                        singleLine = true,
                        onValueChange = {
                            userInputHash.value = it
                        },
                        label = {
                            Text(stringResource(R.string.target))
                        },
                        placeholder = {
                            Text(stringResource(R.string.hash_branch_tag))
                        },
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = branchName.value,
                    singleLine = true,
                    onValueChange = {
                        branchName.value = it
                    },
                    label = {
                        Text(stringResource(R.string.branch_name))
                    },
                    placeholder = {
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                if(proFeatureEnabled(overwriteExistWhenCreateBranchTestPassed)) {
                    MyCheckBox(text = stringResource(R.string.overwrite_if_exist), value = overwriteIfExist)
                    if(overwriteIfExist.value) {
                        SelectionRow {
                            DefaultPaddingText(
                                text = stringResource(R.string.will_overwrite_if_branch_already_exists),
                                color = MyStyleKt.TextColor.danger(),
                            )
                        }
                    }
                }
                MyCheckBox(text = stringResource(R.string.checkout), value = requireCheckout)
                if(proFeatureEnabled(forceCheckoutTestPassed)) {
                    if(requireCheckout.value) {
                        MyCheckBox(text = stringResource(R.string.force), value = forceCheckout)
                        if(forceCheckout.value) {
                            SelectionRow {
                                DefaultPaddingText(
                                    text = stringResource(R.string.warn_force_checkout_will_overwrite_uncommitted_changes),
                                    color = MyStyleKt.TextColor.danger()
                                )
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCancel()  
                    val branchName = branchName.value
                    val basedHead = selectedOption.intValue == optHEAD
                    val baseRefSpec = if(basedHead) "" else userInputHash.value
                    val createByRef = false  
                    val needCheckout = requireCheckout.value
                    doJobThenOffLoading(
                        loadingOn = loadingOn,
                        loadingOff = loadingOff,
                        loadingText = loadingText,
                    )  job@{
                        try {
                            val actuallyBaseRefSpecCommitHash =  if(!basedHead) {  
                                var r = baseRefSpec
                                Repository.open(curRepo.fullSavePath).use { repo->
                                    val ret = Libgit2Helper.resolveCommitByHashOrRef(repo, baseRefSpec)
                                    if(ret.success() && ret.data != null) {  
                                        r = ret.data!!.id().toString()
                                    }
                                }
                                r
                            }else {
                                baseRefSpec
                            }
                            val createBranchRet = doCreateBranch(branchName, basedHead, actuallyBaseRefSpecCommitHash, createByRef, overwriteIfExist.value)
                            if(createBranchRet.hasError()) {  
                                throw RuntimeException(activityContext.getString(R.string.create_branch_err)+": "+createBranchRet.msg)
                            }
                            Msg.requireShow(activityContext.getString(R.string.create_branch_success))
                            if(needCheckout) {
                                Msg.requireShow(activityContext.getString(R.string.checking_out))
                                val (branchFullRefspec, _) = createBranchRet.data!!
                                val upstreamBranchShortNameParam = ""  
                                val checkoutRet = doCheckoutBranch(branchName, branchFullRefspec, upstreamBranchShortNameParam, forceCheckout.value)
                                if(checkoutRet.hasError()) {
                                    throw RuntimeException(activityContext.getString(R.string.checkout_error)+": "+checkoutRet.msg)
                                }
                                Msg.requireShow(activityContext.getString(R.string.checkout_success))
                            }
                        }catch (e:Exception) {
                            onErr(e)
                        }finally {
                            onFinally()
                        }
                    }
                },
                enabled = branchName.value.isNotBlank() && (if(selectedOption.intValue == optCommit) userInputHash.value.isNotBlank() else true)
            ) {
                Text(stringResource(id = R.string.create))
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
