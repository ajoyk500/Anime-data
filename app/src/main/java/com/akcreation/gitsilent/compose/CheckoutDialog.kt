package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.dontCheckoutWhenCreateBranchAtCheckoutDialogTestPassed
import com.akcreation.gitsilent.dev.dontUpdateHeadWhenCheckoutTestPassed
import com.akcreation.gitsilent.dev.forceCheckoutTestPassed
import com.akcreation.gitsilent.dev.overwriteExistWhenCreateBranchTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository

private const val TAG = "CheckoutDialog"
private const val checkoutOptionDontUpdateHead = 0
private const val checkoutOptionDetachHead = 1
private const val checkoutOptionCreateBranch = 2
const val checkoutOptionJustCheckoutForLocalBranch = 3
private const val maxCheckoutSelectedOptionIndex = checkoutOptionJustCheckoutForLocalBranch
const val invalidCheckoutOption = -1
fun getDefaultCheckoutOption(showJustCheckout: Boolean) =  if(showJustCheckout) checkoutOptionJustCheckoutForLocalBranch else checkoutOptionCreateBranch
@Composable
fun CheckoutDialog(
    checkoutSelectedOption: MutableIntState,
    branchName: MutableState<String>,
    remoteBranchShortNameMaybe:String = "",
    isCheckoutRemoteBranch:Boolean=false,  
    remotePrefixMaybe:String="",
    showCheckoutDialog:MutableState<Boolean>,
    from: CheckoutDialogFrom,
    curRepo:RepoEntity,
    curCommitOid:String,  
    curCommitShortOid:String,  
    shortName:String,  
    fullName:String, 
    requireUserInputCommitHash:Boolean, 
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    headChangedCallback:() -> Unit = {},
    refreshPage:(checkout:Boolean, targetOid:String, forceCreateBranch:Boolean, branchName:String) -> Unit,  
    expectCheckoutType:Int,  
    showJustCheckout:Boolean= false,  
) {
    val repoId = curRepo.id
    val activityContext = LocalContext.current
    val checkoutRemoteOptions = listOf(
        stringResource(R.string.dont_update_head),
        Cons.gitDetachHeadStr,  
        stringResource(R.string.new_branch),
        stringResource(R.string.just_checkout)
    )
    val checkoutUserInputCommitHash = rememberSaveable { mutableStateOf("") }
    val forceCheckout = rememberSaveable { mutableStateOf(false) }
    val dontCheckout = rememberSaveable { mutableStateOf(false) }
    val overwriteIfBranchExist = rememberSaveable { mutableStateOf(false) }
    val setUpstream = rememberSaveable { mutableStateOf(isCheckoutRemoteBranch) }
    val getCheckoutOkBtnEnabled:()->Boolean = getCheckoutOkBtnEnabled@{
        if(checkoutSelectedOption.intValue == checkoutOptionCreateBranch && branchName.value.isBlank()) {
            return@getCheckoutOkBtnEnabled false
        }
        if(requireUserInputCommitHash && checkoutUserInputCommitHash.value.isBlank()) {
            return@getCheckoutOkBtnEnabled false
        }
        if(checkoutSelectedOption.intValue.let { it < 0 || it > maxCheckoutSelectedOptionIndex }) {
            return@getCheckoutOkBtnEnabled false
        }
        return@getCheckoutOkBtnEnabled true
    }
    val doCheckoutBranch: suspend (String, String, String, Boolean,Boolean, Int) -> Ret<Oid?> =
        doCheckoutLocalBranch@{ shortBranchNameOrHash: String, fullBranchNameOrHash: String, upstreamBranchShortNameParam: String , force:Boolean, updateHead:Boolean, checkoutType:Int->
            Repository.open(curRepo.fullSavePath).use { repo ->
                val ret = Libgit2Helper.doCheckoutBranchThenUpdateDb(
                    repo,
                    repoId,
                    shortBranchNameOrHash,
                    fullBranchNameOrHash,
                    upstreamBranchShortNameParam,
                    checkoutType,
                    force,
                    updateHead
                )
                return@doCheckoutLocalBranch ret
            }
        }
    val doCreateBranch: (String, String, Boolean) -> Ret<Triple<String, String, String>?> = doCreateBranch@{ branchNamePram: String, baseRefSpec: String, overwriteIfExisted:Boolean ->
            Repository.open(curRepo.fullSavePath).use { repo ->
                val ret = Libgit2Helper.doCreateBranch(
                    activityContext,
                    repo,
                    repoId,
                    branchNamePram,
                    false,
                    baseRefSpec,
                    false,
                    overwriteIfExisted
                )
                return@doCreateBranch ret
            }
        }
    ConfirmDialog(
        title = activityContext.getString(R.string.checkout),
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                Row(modifier = Modifier.padding(5.dp)) {
                }
                Row {
                    Text(
                        text = activityContext.getString(R.string.checkout_to) + ": ",
                        overflow = TextOverflow.Visible
                    )
                }
                Row(modifier = Modifier.padding(5.dp)) {
                }
                if(requireUserInputCommitHash) {  
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = checkoutUserInputCommitHash.value,
                        singleLine = true,
                        onValueChange = {
                            checkoutUserInputCommitHash.value = it
                        },
                        label = {
                            Text(stringResource(R.string.target))
                        },
                        placeholder = {
                            Text(stringResource(R.string.hash_branch_tag))
                        },
                    )
                }else {  
                    MySelectionContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = shortName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                }
                Row(modifier = Modifier.padding(5.dp)) {
                }
                Row {
                    Text(text = activityContext.getString(R.string.plz_choose_a_checkout_type) + ":")
                }
                Spacer(Modifier.height(5.dp))
                SingleSelection(
                    itemList = checkoutRemoteOptions,
                    selected = {idx, item -> checkoutSelectedOption.intValue == idx},
                    text = {idx, item -> item},
                    onClick = {idx, item -> checkoutSelectedOption.intValue = idx},
                    skip = {idx, item -> (idx == checkoutOptionDontUpdateHead && !proFeatureEnabled(dontUpdateHeadWhenCheckoutTestPassed)) || (idx == checkoutOptionJustCheckoutForLocalBranch && !showJustCheckout)}
                )
                if (checkoutSelectedOption.intValue == checkoutOptionCreateBranch) {
                    Row(modifier = Modifier.padding(5.dp)) {
                    }
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
                    )
                    Spacer(Modifier.height(10.dp))
                    if(isCheckoutRemoteBranch && remotePrefixMaybe.isNotBlank() && remoteBranchShortNameMaybe.isNotBlank()) {
                        MyCheckBox(text = stringResource(R.string.set_upstream), value = setUpstream)
                    }
                    if(proFeatureEnabled(overwriteExistWhenCreateBranchTestPassed)) {
                        MyCheckBox(text = stringResource(R.string.overwrite_if_exist), value = overwriteIfBranchExist)
                        if(overwriteIfBranchExist.value) {
                            MySelectionContainer {
                                Row {
                                    DefaultPaddingText(
                                        text = stringResource(R.string.will_overwrite_if_branch_already_exists),
                                        color = MyStyleKt.TextColor.danger(),
                                    )
                                }
                            }
                        }
                    }
                    if(proFeatureEnabled(dontCheckoutWhenCreateBranchAtCheckoutDialogTestPassed)) {
                        MyCheckBox(text = stringResource(R.string.dont_checkout), value = dontCheckout)
                        if(dontCheckout.value) {
                            MySelectionContainer {
                                Row {
                                    DefaultPaddingText(
                                        text = stringResource(R.string.wont_checkout_only_create_branch),
                                    )
                                }
                            }
                        }
                    }
                } else if (checkoutSelectedOption.intValue == checkoutOptionDetachHead) {
                }
                val showForceCheckout = (!(checkoutSelectedOption.intValue == checkoutOptionCreateBranch && dontCheckout.value)) && proFeatureEnabled(forceCheckoutTestPassed)
                if(showForceCheckout) {
                    Spacer(Modifier.height(10.dp))
                    MyCheckBox(text = stringResource(R.string.force), value = forceCheckout)
                    if(forceCheckout.value) {
                        MySelectionContainer {
                            Row {
                                DefaultPaddingText(
                                    text = stringResource(R.string.warn_force_checkout_will_overwrite_uncommitted_changes),
                                    color = MyStyleKt.TextColor.danger(),
                                )
                            }
                        }
                    }
                }
            }
        },
        okBtnText = stringResource(id = R.string.ok),
        cancelBtnText = stringResource(id = R.string.cancel),
        okBtnEnabled = getCheckoutOkBtnEnabled(),
        onCancel = {
            showCheckoutDialog.value = false
        }
    ) {  
        showCheckoutDialog.value = false
        val dontCheckout = dontCheckout.value
        val useUserInputHash = requireUserInputCommitHash
        val checkoutUserInputCommitHash = checkoutUserInputCommitHash.value
        val curCommitOidOrRefName = if(useUserInputHash) checkoutUserInputCommitHash else if(expectCheckoutType==Cons.checkoutType_checkoutCommitThenDetachHead) curCommitOid else fullName
        val curCommitShortOidOrShortRefName = if(useUserInputHash) checkoutUserInputCommitHash else if(expectCheckoutType==Cons.checkoutType_checkoutCommitThenDetachHead) curCommitShortOid else shortName
        val localBranchWillCreate = branchName.value
        val updateHead = !(checkoutSelectedOption.intValue == checkoutOptionDontUpdateHead || (checkoutSelectedOption.intValue == checkoutOptionCreateBranch && dontCheckout))
        val upstreamBranchShortNameParam = ""
        val checkoutType = if (checkoutSelectedOption.intValue == checkoutOptionCreateBranch || checkoutSelectedOption.intValue == checkoutOptionJustCheckoutForLocalBranch) Cons.checkoutType_checkoutRefThenUpdateHead else if(checkoutSelectedOption.intValue==checkoutOptionDetachHead && expectCheckoutType!=Cons.checkoutType_checkoutCommitThenDetachHead) Cons.checkoutType_checkoutRefThenDetachHead else expectCheckoutType
        val headWillChange = checkoutSelectedOption.intValue.let { it == checkoutOptionDetachHead || (it == checkoutOptionCreateBranch && !dontCheckout) }
        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.checking_out)) {
            try {
                val curCommitOidOrRefName =  if(useUserInputHash) {  
                    var r = checkoutUserInputCommitHash
                    Repository.open(curRepo.fullSavePath).use { repo->
                        val ret = Libgit2Helper.resolveCommitByHashOrRef(repo, checkoutUserInputCommitHash)
                        if(ret.success() && ret.data != null) {  
                            r = ret.data!!.id().toString()
                        }else {  
                            throw RuntimeException(activityContext.getString(R.string.failed_resolve_commit_by_user_input))
                        }
                    }
                    r
                }else {
                    curCommitOidOrRefName
                }
                if (checkoutSelectedOption.intValue == checkoutOptionDetachHead
                    || checkoutSelectedOption.intValue == checkoutOptionDontUpdateHead
                ) {
                    MyLog.d(TAG, "checkout commit as detached head: commitOidOrRefName=$curCommitOidOrRefName, fullOid=$curCommitOid")
                    val checkoutRet = doCheckoutBranch(curCommitShortOidOrShortRefName, curCommitOidOrRefName, upstreamBranchShortNameParam, forceCheckout.value, updateHead, checkoutType)
                    if(checkoutRet.success()) {
                        curRepo.isDetached = Cons.dbCommonTrue
                        Msg.requireShow(activityContext.getString(R.string.checkout_success))
                    }else {  
                        val checkoutErrMsg = activityContext.getString(R.string.checkout_error)+": "+checkoutRet.msg
                        throw RuntimeException(checkoutErrMsg)
                    }
                } else if(checkoutSelectedOption.intValue == checkoutOptionCreateBranch){
                    val baseCommitOid = if(useUserInputHash) curCommitOidOrRefName else curCommitOid
                    MyLog.d(TAG, "checkout commit to new local branch: localBranchWillCreate=$localBranchWillCreate, baseCommitOid=$baseCommitOid")
                    val createBranchRet = doCreateBranch(localBranchWillCreate, baseCommitOid, overwriteIfBranchExist.value)  
                    if (createBranchRet.success()) {
                        val (fullBranchRefspec, branchShortName, branchHeadFullHash) = createBranchRet.data!!  
                        if(setUpstream.value) {
                            if(remotePrefixMaybe.isNotBlank()) {
                                if(remoteBranchShortNameMaybe.isNotBlank()) {
                                    Repository.open(curRepo.fullSavePath).use { repo->
                                        val success = Libgit2Helper.setUpstreamForBranchByRemoteAndRefspec(
                                            repo,
                                            remotePrefixMaybe,
                                            Libgit2Helper.getRefsHeadsBranchFullRefSpecFromShortRefSpec(remoteBranchShortNameMaybe),
                                            branchShortName
                                        )
                                        if(!success) {
                                            Msg.requireShowLongDuration(activityContext.getString(R.string.set_upstream_error))
                                        }  
                                    }
                                }
                            }else { 
                                Msg.requireShowLongDuration(activityContext.getString(R.string.set_upstream_err_remote_is_invalid))
                            }
                        }
                        Msg.requireShow(activityContext.getString(R.string.create_branch_success))
                        if(dontCheckout) {
                            refreshPage(false, branchHeadFullHash, overwriteIfBranchExist.value, localBranchWillCreate)
                            return@doJobThenOffLoading
                        }
                        Msg.requireShow(activityContext.getString(R.string.checking_out))
                        val checkoutRet = doCheckoutBranch(branchShortName, fullBranchRefspec, upstreamBranchShortNameParam, forceCheckout.value, updateHead, checkoutType)
                        if(checkoutRet.success()) {
                            curRepo.isDetached = Cons.dbCommonFalse
                            Msg.requireShow(activityContext.getString(R.string.checkout_success))
                        }else {  
                            val checkoutErrMsg = activityContext.getString(R.string.checkout_error)+": "+checkoutRet.msg
                            throw RuntimeException(checkoutErrMsg)
                        }
                    }else {  
                        val createBranchErrMsg = activityContext.getString(R.string.create_branch_err)+": "+createBranchRet.msg
                        throw RuntimeException(createBranchErrMsg)
                    }
                }else if(checkoutSelectedOption.intValue == checkoutOptionJustCheckoutForLocalBranch){
                    val upstreamRefspec = ""  
                    val isLocal=true
                    val checkoutRet = doCheckoutBranch(shortName, fullName, upstreamRefspec, forceCheckout.value, updateHead, checkoutType)
                    if(checkoutRet.success()) {
                        Msg.requireShow(activityContext.getString(R.string.checkout_success))
                    }else {  
                        val checkoutErrMsg = activityContext.getString(R.string.checkout_error)+": "+checkoutRet.msg
                        throw RuntimeException(checkoutErrMsg)
                    }
                }
                if(headWillChange) {
                    headChangedCallback()
                }
                if(checkoutSelectedOption.intValue != checkoutOptionDontUpdateHead) {  
                    val headOidStr = Repository.open(curRepo.fullSavePath).use { repo -> Libgit2Helper.resolveHEAD(repo)?.id()?.toString()?:"" }
                    refreshPage(true, headOidStr, overwriteIfBranchExist.value, localBranchWillCreate)
                }
            } catch (e: Exception) {
                refreshPage(false, curCommitOidOrRefName, overwriteIfBranchExist.value, localBranchWillCreate)
                Msg.requireShowLongDuration("err: " + e.localizedMessage)
                val refName = if(shortName.isNotBlank() && shortName==curCommitOidOrRefName) shortName else "$shortName($curCommitOidOrRefName)"
                createAndInsertError(repoId, "checkout '" + refName + "' err: " + e.localizedMessage)
                MyLog.e(TAG, "checkout '" + refName + "' err: " + e.stackTraceToString())
            }
        }
    }
}
enum class CheckoutDialogFrom {
    OTHER,
    BRANCH_LIST,  
}
