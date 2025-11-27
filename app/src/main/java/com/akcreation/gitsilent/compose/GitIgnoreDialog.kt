package com.akcreation.gitsilent.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.git.IgnoreItem
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.github.git24j.core.Repository
import java.io.File

@Composable
fun GitIgnoreDialog(
    showIgnoreDialog: MutableState<Boolean>,
    loadingOn: (String) -> Unit,
    loadingOff: () -> Unit,
    activityContext: Context,
    getIgnoreItems: (repoWorkDirFullPath:String)->List<IgnoreItem>,
    getRepository: () -> Repository?,
    onSuccess: suspend () -> Unit = { Msg.requireShow(activityContext.getString(R.string.success)) },
    onCatch: suspend (Exception)->Unit,
    onFinally: suspend (repoWorkDirFullPath:String)->Unit,  
) {
    ConfirmDialog(
        title = stringResource(R.string.ignore),
        text = stringResource(R.string.will_ignore_selected_files_are_you_sure),
        okTextColor = MyStyleKt.TextColor.danger(),  
        onCancel = { showIgnoreDialog.value = false }
    ) {
        showIgnoreDialog.value = false
        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
            var repoWorkDirFullPath = ""
            try {
                val repository = getRepository()
                if(repository == null) {
                    return@doJobThenOffLoading
                }
                repoWorkDirFullPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repository)
                val items = getIgnoreItems(repoWorkDirFullPath)
                if (items.isEmpty()) {
                    return@doJobThenOffLoading
                }
                repository.use { repo ->
                    val repoIndex = repo.index()
                    val lb = Cons.lineBreak
                    val slash = Cons.slash
                    val linesWillIgnore = lb +
                            (items.joinToString(lb) {
                                Libgit2Helper.removeFromGit(repoIndex, it.pathspec, it.isFile)
                                if (it.pathspec.startsWith(slash)) it.pathspec else (slash + it.pathspec)
                            }) +
                            lb
                    ;
                    val ignoreFile = File(Libgit2Helper.getRepoIgnoreFilePathNoEndsWithSlash(repo, createIfNonExists = true))
                    FsUtils.appendTextToFile(ignoreFile, linesWillIgnore)
                    repoIndex.write()
                }
                onSuccess()
            } catch (e: Exception) {
                onCatch(e)
            } finally {
                onFinally(repoWorkDirFullPath)
            }
        }
    }
}
