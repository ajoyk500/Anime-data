package com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.ignoreWorktreeFilesTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dev.pushForceTestPassed
import com.akcreation.gitsilent.dev.rebaseTestPassed
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.github.git24j.core.Repository
import kotlinx.coroutines.CoroutineScope

@Composable
fun ChangeListPageActions(
    changeListCurRepo: CustomStateSaveable<RepoEntity>,
    changeListRequireRefreshFromParentPage: (RepoEntity) -> Unit,
    changeListHasIndexItems:MutableState<Boolean>,
    requireDoActFromParent:MutableState<Boolean>,
    requireDoActFromParentShowTextWhenDoingAct:MutableState<String>,
    enableAction:MutableState<Boolean>,
    repoState:MutableIntState,
    fromTo:String,
    listState: LazyListState,
    scope: CoroutineScope,
    changeListPageNoRepo:MutableState<Boolean>,
    hasNoConflictItems:Boolean,
    changeListPageFilterModeOn:MutableState<Boolean>,
    changeListPageFilterKeyWord:CustomStateSaveable<TextFieldValue>,
    rebaseCurOfAll:String,
    naviTarget:MutableState<String>,
) {
    val isWorktreePage = fromTo == Cons.gitDiffFromIndexToWorktree
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val repoIsDetached = dbIntToBool(changeListCurRepo.value.isDetached)
    val repoUnderMerge = repoState.intValue == Repository.StateT.MERGE.bit
    if(isWorktreePage) {
        LongPressAbleIconBtn(
            enabled = !changeListPageNoRepo.value,
            tooltipText = stringResource(R.string.index),
            icon = if(changeListHasIndexItems.value) Icons.Filled.AllInbox else Icons.Filled.Inbox,
            iconContentDesc = stringResource(R.string.index),
            iconColor = UIHelper.getIconEnableColorOrNull(changeListHasIndexItems.value)
        ) {
            navController.navigate(Cons.nav_IndexScreen)
        }
    }
    LongPressAbleIconBtn(
        enabled = enableAction.value && !changeListPageNoRepo.value,
        tooltipText = stringResource(R.string.filter),
        icon =  Icons.Filled.FilterAlt,
        iconContentDesc = stringResource(id = R.string.filter),
    ) {
        changeListPageFilterKeyWord.value= TextFieldValue("")
        changeListPageFilterModeOn.value = true
    }
    LongPressAbleIconBtn(
        enabled = enableAction.value,
        tooltipText = stringResource(R.string.refresh),
        icon = Icons.Filled.Refresh,
        iconContentDesc = stringResource(R.string.refresh),
    ) {
        changeListRequireRefreshFromParentPage(changeListCurRepo.value)
    }
    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.menu),
        icon = Icons.Filled.MoreVert,
        iconContentDesc = stringResource(R.string.menu),
        onClick = {
            dropDownMenuExpandState.value = !dropDownMenuExpandState.value
        }
    )
    Row(modifier = Modifier.padding(top = MyStyleKt.TopBar.dropDownMenuTopPaddingSize)) {
        val enableMenuItem = enableAction.value && !changeListPageNoRepo.value
        DropdownMenu(
            expanded = dropDownMenuExpandState.value,
            onDismissRequest = { dropDownMenuExpandState.value=false }
        ) {
            if(isWorktreePage) {  
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.stage_all)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.stageAll)
                        requireDoActFromParentShowTextWhenDoingAct.value = activityContext.getString(R.string.staging)
                        requireDoActFromParent.value = true
                        enableAction.value=false  
                        dropDownMenuExpandState.value=false
                    }
                )
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.commit_all)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.indexToWorkTree_CommitAll)
                        requireDoActFromParentShowTextWhenDoingAct.value = activityContext.getString(R.string.committing)
                        requireDoActFromParent.value = true
                        enableAction.value=false  
                        dropDownMenuExpandState.value=false
                    }
                )
            }else {  
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.commit)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.commit)
                        requireDoActFromParentShowTextWhenDoingAct.value = activityContext.getString(R.string.committing)
                        requireDoActFromParent.value = true
                        enableAction.value=false  
                        dropDownMenuExpandState.value=false
                    }
                )
            }
            val enableRepoAction = enableMenuItem && !repoIsDetached
            DropdownMenuItem(
                enabled = enableRepoAction,
                text = { Text(stringResource(R.string.fetch)) },
                onClick = {
                    Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.fetch)
                    requireDoActFromParentShowTextWhenDoingAct.value = activityContext.getString(R.string.fetching)
                    requireDoActFromParent.value = true
                    enableAction.value=false  
                    dropDownMenuExpandState.value=false
                }
            )
            DropdownMenuItem(
                enabled = enableRepoAction,
                text = { Text(stringResource(R.string.pull_merge)) },
                onClick = {
                    Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.pull)
                    requireDoActFromParentShowTextWhenDoingAct.value = activityContext.getString(R.string.pulling)
                    requireDoActFromParent.value = true
                    enableAction.value=false  
                    dropDownMenuExpandState.value=false
                }
            )
            if(proFeatureEnabled(rebaseTestPassed)) {
                DropdownMenuItem(
                    enabled = enableRepoAction,
                    text = { Text(stringResource(R.string.pull_rebase)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.pullRebase)
                        requireDoActFromParentShowTextWhenDoingAct.value = activityContext.getString(R.string.pulling)
                        requireDoActFromParent.value = true
                        enableAction.value=false  
                        dropDownMenuExpandState.value=false
                    }
                )
            }
            DropdownMenuItem(
                enabled = enableRepoAction,
                text = { Text(stringResource(R.string.push)) },
                onClick = {
                    Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.push)
                    requireDoActFromParentShowTextWhenDoingAct.value=activityContext.getString(R.string.pushing)
                    requireDoActFromParent.value = true
                    enableAction.value=false
                    dropDownMenuExpandState.value=false
                }
            )
            if(proFeatureEnabled(pushForceTestPassed)) {
                DropdownMenuItem(
                    enabled = enableRepoAction,
                    text = { Text(stringResource(R.string.push_force)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.pushForce)
                        requireDoActFromParentShowTextWhenDoingAct.value=activityContext.getString(R.string.force_pushing)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
            }
            DropdownMenuItem(
                enabled = enableRepoAction,
                text = { Text(stringResource(R.string.sync_merge)) },
                onClick = {
                    Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.sync)
                    requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.syncing)
                    requireDoActFromParent.value = true
                    enableAction.value=false
                    dropDownMenuExpandState.value=false
                }
            )
            if(proFeatureEnabled(rebaseTestPassed)) {
                DropdownMenuItem(
                    enabled = enableRepoAction,
                    text = { Text(stringResource(R.string.sync_rebase)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.syncRebase)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.syncing)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
            }
            DropdownMenuItem(
                enabled = true,
                text = { Text(stringResource(R.string.stash)) },
                onClick = {
                    Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.goToStashPage)
                    requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.loading)
                    requireDoActFromParent.value = true
                    enableAction.value=false
                    dropDownMenuExpandState.value=false
                }
            )
            if(isWorktreePage) {
                if(proFeatureEnabled(ignoreWorktreeFilesTestPassed)) {
                    DropdownMenuItem(
                        enabled = true,
                        text = { Text(stringResource(R.string.edit_ignore_file)) },
                        onClick = {
                            Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.editIgnoreFile)
                            requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.loading)
                            requireDoActFromParent.value = true
                            enableAction.value=false
                            dropDownMenuExpandState.value=false
                        }
                    )
                }
                DropdownMenuItem(
                    enabled = true,  
                    text = { Text(stringResource(R.string.show_in_repos)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.showInRepos)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.loading)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
                if(changeListCurRepo.value.parentRepoId.isNotBlank()) {
                    DropdownMenuItem(
                        enabled = true,
                        text = { Text(stringResource(R.string.go_parent)) },
                        onClick = {
                            Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.goParent)
                            requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.loading)
                            requireDoActFromParent.value = true
                            enableAction.value=false
                            dropDownMenuExpandState.value=false
                        }
                    )
                }
            }
            if(repoUnderMerge) {
                MyHorizontalDivider()
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.merge_continue)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.mergeContinue)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.continue_merging)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.merge_abort)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.mergeAbort)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.aborting)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
            }
            if(repoState.intValue == Repository.StateT.REBASE_MERGE.bit) {
                MyHorizontalDivider()
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.rebase_continue)+rebaseCurOfAll) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.rebaseContinue)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.loading)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.rebase_skip)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.rebaseSkip)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.loading)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.rebase_abort)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.rebaseAbort)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.aborting)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
            }
            if(repoState.intValue == Repository.StateT.CHERRYPICK.bit) {
                MyHorizontalDivider()
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.cherrypick_continue)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.cherrypickContinue)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.loading)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.cherrypick_abort)) },
                    onClick = {
                        Cache.set(Cache.Key.changeListInnerPage_requireDoActFromParent, PageRequest.cherrypickAbort)
                        requireDoActFromParentShowTextWhenDoingAct.value= activityContext.getString(R.string.aborting)
                        requireDoActFromParent.value = true
                        enableAction.value=false
                        dropDownMenuExpandState.value=false
                    }
                )
            }
        }
    }
}
