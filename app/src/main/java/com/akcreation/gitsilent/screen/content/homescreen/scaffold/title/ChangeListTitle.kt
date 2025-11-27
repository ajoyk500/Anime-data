package com.akcreation.gitsilent.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.compose.DropDownMenuItemText
import com.akcreation.gitsilent.compose.IconOfRepoState
import com.akcreation.gitsilent.compose.RepoInfoDialog
import com.akcreation.gitsilent.compose.TitleDropDownMenu
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.StateRequestType
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.getRequestDataByState
import com.akcreation.gitsilent.utils.state.CustomStateListSaveable
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.github.git24j.core.Repository
import kotlinx.coroutines.CoroutineScope

@Composable
fun ChangeListTitle(
    changeListCurRepo: CustomStateSaveable<RepoEntity>,
    dropDownMenuItemOnClick: (RepoEntity) -> Unit,
    repoState: MutableIntState,
    isSelectionMode: MutableState<Boolean>,
    listState: LazyListState,
    scope: CoroutineScope,
    enableAction:Boolean,
    repoList:CustomStateListSaveable<RepoEntity>,
    needReQueryRepoList:MutableState<String>,
    goToChangeListPage:(RepoEntity)->Unit,
) {
    val enableAction = true
    val activityContext = LocalContext.current
    val inDarkTheme = Theme.inDarkTheme
    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(
            curRepo = changeListCurRepo.value,
            showTitleInfoDialog = showTitleInfoDialog,
            prependContent = {
                Text(stringResource(R.string.comparing_label)+": "+ Libgit2Helper.getLeftToRightFullHash(Cons.git_IndexCommitHash, Cons.git_LocalWorktreeCommitHash))
            }
        )
    }
    val repoStateText = rememberSaveable(repoState.intValue) { mutableStateOf(Libgit2Helper.getRepoStateText(repoState.intValue, activityContext)) }
    val switchDropDownMenu = {
        if(dropDownMenuExpandState.value) {  
            dropDownMenuExpandState.value = false
        } else {  
            changeStateTriggerRefreshPage(needReQueryRepoList)
            dropDownMenuExpandState.value = true
        }
    }
    val getTitleColor = {
        UIHelper.getTitleColor(enabled = enableAction)
    }
    if(repoList.value.isEmpty()) {
        Text(
            text = stringResource(id = R.string.changelist),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }else {
        TitleDropDownMenu(
            dropDownMenuExpandState = dropDownMenuExpandState,
            curSelectedItem = changeListCurRepo.value,
            itemList = repoList.value.toList(),
            titleClickEnabled = enableAction,
            switchDropDownMenuShowHide = switchDropDownMenu,
            titleFirstLine = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconOfRepoState(repoState.intValue)
                    Text(
                        text = changeListCurRepo.value.repoName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = MyStyleKt.Title.firstLineFontSize,
                        color = getTitleColor()
                    )
                }
            },
            titleSecondLine = {
                Text(
                    text = (if(dbIntToBool(changeListCurRepo.value.isDetached)) Libgit2Helper.genDetachedText(changeListCurRepo.value.lastCommitHashShort) else Libgit2Helper.genLocalBranchAndUpstreamText(changeListCurRepo.value.branch, changeListCurRepo.value.upstreamBranch)) + (if(repoStateText.value.isNotBlank()) " | ${repoStateText.value}" else ""),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = MyStyleKt.Title.secondLineFontSize,
                    color = getTitleColor()
                )
            },
            titleRightIcon = {
                Icon(
                    imageVector = if (dropDownMenuExpandState.value) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft,
                    contentDescription = stringResource(R.string.switch_repo),
                    tint = if (enableAction) LocalContentColor.current else UIHelper.getDisableBtnColor(inDarkTheme)
                )
            },
            isItemSelected = { it.id == changeListCurRepo.value.id },
            menuItem = { r, selected ->
                DropDownMenuItemText(
                    text1 = r.repoName,
                    text2 = r.gitRepoState.let { if(it == null) stringResource(R.string.invalid) else if(it != Repository.StateT.NONE) it.toString() else "" },
                )
            },
            titleOnLongClick = { showTitleInfoDialog.value = true },
            itemOnClick = { r ->
                if(changeListCurRepo.value.id != r.id) {
                    isSelectionMode.value=false
                }
                dropDownMenuItemOnClick(r)
            },
        )
    }
    LaunchedEffect(needReQueryRepoList.value) {
        try {
            doJobThenOffLoading {
                val repoDb = AppModel.dbContainer.repoRepository
                val readyRepoListFromDb = repoDb.getReadyRepoList(requireSyncRepoInfoWithGit = false)
                repoList.value.clear()
                repoList.value.addAll(readyRepoListFromDb)
                val (requestType, targetRepoFullPath) = getRequestDataByState<String?>(needReQueryRepoList.value)
                if(requestType == StateRequestType.jumpAfterImport && targetRepoFullPath.let { it != null && it.isNotBlank() }) {
                    readyRepoListFromDb.find { it.fullSavePath == targetRepoFullPath }?.let {
                        goToChangeListPage(it)
                    }
                }
            }
        } catch (cancel: Exception) {
        }
    }
}
