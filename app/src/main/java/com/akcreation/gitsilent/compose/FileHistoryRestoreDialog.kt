package com.akcreation.gitsilent.compose

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.github.git24j.core.Repository

@Composable
fun FileHistoryRestoreDialog(
    targetCommitOidStr: String,
    commitMsg: String,
    showRestoreDialog: MutableState<Boolean>,
    loadingOn: (String) -> Unit,
    loadingOff: () -> Unit,
    activityContext: Context,
    curRepo: CustomStateSaveable<RepoEntity>,
    fileRelativePath: String,
    repoId: String,
    onSuccess:() -> Unit = {}
) {
    ConfirmDialog2(
        title = stringResource(R.string.restore),
        requireShowTextCompose = true,
        textCompose = {
            MySelectionContainer {
                ScrollableColumn {
                    Text(
                        replaceStringResList(stringResource(R.string.target_ph), listOf(targetCommitOidStr))
                    )

                    if(commitMsg.isNotBlank()) {
                        Spacer(Modifier.height(15.dp))
                        Text(commitMsg)
                    }
                }
            }
        },
        onCancel = { showRestoreDialog.value = false },
        okBtnText = stringResource(R.string.restore)
    ) {
        showRestoreDialog.value = false
        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.restoring)) {
            try {
                Repository.open(curRepo.value.fullSavePath).use { repo ->
                    //fun checkoutFiles(repo: Repository, targetCommitHash:String, pathSpecs: List<String>, force: Boolean, checkoutOptions: Checkout.Options?=null): Ret<Unit?> {
                    Libgit2Helper.checkoutFiles(repo, targetCommitOidStr, pathSpecs = listOf(fileRelativePath), force = true)

                }

                Msg.requireShow(activityContext.getString(R.string.success))

                onSuccess()
            } catch (e: Exception) {
                val errMsg = e.localizedMessage ?: "unknown err"
                Msg.requireShowLongDuration(errMsg)
                createAndInsertError(repoId, errMsg)
            }
        }
    }
}

