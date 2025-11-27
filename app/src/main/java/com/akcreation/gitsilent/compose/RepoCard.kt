package com.akcreation.gitsilent.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.goToCloneScreen
import com.akcreation.gitsilent.screen.functions.goToCommitListScreen
import com.akcreation.gitsilent.screen.functions.goToErrScreen
import com.akcreation.gitsilent.screen.shared.CommitListFrom
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.copyAndShowCopied
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.github.git24j.core.Repository
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RepoCard(
    itemWidth:Float,
    requireFillMaxWidth:Boolean,
    showBottomSheet: MutableState<Boolean>,
    curRepo: CustomStateSaveable<RepoEntity>,
    curRepoIndex: MutableIntState,
    repoDto: RepoEntity,
    repoDtoIndex:Int,
    itemSelected:Boolean,
    titleOnClick:(RepoEntity)->Unit,
    goToFilesPage:(path:String) -> Unit,
    requireBlinkIdx:MutableIntState,
    pageRequest:MutableState<String>,
    isSelectionMode:Boolean,
    onClick: (RepoEntity) -> Unit,
    onLongClick:(RepoEntity)->Unit,
    copyErrMsg: (String) -> Unit,
    requireDelRepo:(RepoEntity)->Unit,
    doCloneSingle:(RepoEntity)->Unit,
    initErrMsgDialog:(RepoEntity, errMsg: String)->Unit,
    initCommitMsgDialog:(RepoEntity)->Unit,
    workStatusOnclick:(clickedRepo:RepoEntity, status:Int)->Unit
) {
    val navController = AppModel.navController
    val haptic = LocalHapticFeedback.current
    val activityContext = LocalContext.current
    val inDarkTheme = Theme.inDarkTheme
    val repoNotReady = Libgit2Helper.isRepoStatusNotReady(repoDto)
    val repoErr = Libgit2Helper.isRepoStatusErr(repoDto)
    val repoStatusGood = !repoNotReady && (repoDto.tmpStatus.isNotBlank() || (repoDto.gitRepoState!=null && !repoErr))
    val cardColor = UIHelper.defaultCardColor()
    val highlightColor = remember(inDarkTheme) {if(inDarkTheme) Color(0xFF9D9C9C) else Color(0xFFFFFFFF)}
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    val clipboardManager = LocalClipboardManager.current
    val setCurRepo = {
        curRepo.value = RepoEntity()  
        curRepo.value = repoDto  
        curRepoIndex.intValue = repoDtoIndex
    }
    Column (
        modifier = if(requireFillMaxWidth) Modifier.fillMaxWidth() else Modifier.width(itemWidth.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        MyCard(
            modifier = Modifier
                .padding(MyStyleKt.defaultItemPadding)
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onClick(repoDto)
                    },
                    onLongClick = {
                        setCurRepo()
                        onLongClick(repoDto)
                    },
                )
            ,
            containerColor = if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue == repoDtoIndex) {
                doJobThenOffLoading {
                    delay(UIHelper.getHighlightingTimeInMills())  
                    requireBlinkIdx.intValue = -1  
                }
                highlightColor
            } else {
                cardColor
            },
        ) {
            RepoTitle(
                haptic = haptic,
                repoDto = repoDto,
                isSelectionMode = isSelectionMode,
                itemSelected = itemSelected,
                titleOnClick = titleOnClick,
                titleOnLongClick = onLongClick
            )
            MyHorizontalDivider()
            if (Libgit2Helper.isRepoStatusNoErr(repoDto)) {
                Column(
                    modifier = Modifier.padding(start = 10.dp, top = 4.dp, end = 10.dp, bottom = 10.dp)
                ) {
                    if(!repoNotReady && repoDto.gitRepoState != null && repoDto.gitRepoState != Repository.StateT.NONE && repoDto.tmpStatus.isBlank()) {  
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InLineIcon(
                                icon = Icons.Outlined.Info,
                                tooltipText = stringResource(R.string.state)
                            )
                            ScrollableRow {
                                Text(
                                    text = repoDto.getRepoStateStr(activityContext),  
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = defaultFontWeight,
                                    modifier = MyStyleKt.ClickableText.modifier,
                                )
                            }
                        }
                    }
                    if(repoStatusGood) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InLineIcon(
                                icon = ImageVector.vectorResource(R.drawable.branch),
                                tooltipText = stringResource(R.string.repo_label_branch)
                            )
                            ScrollableRow {
                                ClickableText (
                                    text = if(repoStatusGood) {if(dbIntToBool(repoDto.isDetached)) Libgit2Helper.genDetachedText(repoDto.lastCommitHashShort) else Libgit2Helper.genLocalBranchAndUpstreamText(repoDto.branch, repoDto.upstreamBranch)} else "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = defaultFontWeight,
                                    modifier = MyStyleKt.ClickableText.modifier.clickable(enabled = repoStatusGood) {
                                        navController.navigate(Cons.nav_BranchListScreen + "/" + repoDto.id)
                                    },
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InLineIcon(
                            icon = Icons.Filled.AccessTime,
                            tooltipText = stringResource(R.string.repo_label_last_update_time)
                        )
                        ScrollableRow {
                            Text(
                                text = repoDto.cachedLastUpdateTime(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = MyStyleKt.ClickableText.modifier,
                                fontWeight = defaultFontWeight
                            )
                        }
                    }
                    if(repoStatusGood) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InLineIcon(
                                icon = Icons.Filled.Commit,
                                tooltipText = stringResource(R.string.repo_label_last_commit)
                            )
                            ScrollableRow {
                                ClickableText (
                                    text = (if(repoStatusGood) repoDto.lastCommitHashShort ?: "" else "") +
                                            (repoDto.lastCommitDateTime.let { if(it.isBlank()) "" else " ($it)"})
                                    ,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = MyStyleKt.ClickableText.modifier.combinedClickable(
                                        enabled = repoStatusGood,
                                        onLongClick = {
                                            copyAndShowCopied(activityContext, clipboardManager, repoDto.lastCommitHash)
                                        }
                                    ) {
                                        goToCommitListScreen(
                                            repoId = repoDto.id,
                                            fullOid = "",  
                                            shortBranchName = "",
                                            isHEAD = true,
                                            from = CommitListFrom.FOLLOW_HEAD,
                                            )
                                    },
                                    fontWeight = defaultFontWeight
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InLineIcon(
                                icon = Icons.AutoMirrored.Outlined.Message,
                                tooltipText = stringResource(R.string.msg)
                            )
                            ClickableText (
                                text = repoDto.getOrUpdateCachedOneLineLatestCommitMsg(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = defaultFontWeight,
                                modifier = MyStyleKt.ClickableText.modifier.combinedClickable(
                                    enabled = repoStatusGood,
                                ) {
                                    initCommitMsgDialog(repoDto)
                                },
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InLineIcon(
                            icon = Icons.Filled.Info,
                            tooltipText = stringResource(R.string.repo_label_status)
                        )
                        ScrollableRow {
                            val tmpStatus = repoDto.tmpStatus
                            if(repoErr || repoNotReady || tmpStatus.isNotBlank() || repoDto.workStatus == Cons.dbRepoWorkStatusUpToDate) {  
                                var nullNormalTrueUpToDateFalseError:Boolean? = null
                                val text = if(repoErr || (repoDto.gitRepoState==null && tmpStatus.isBlank())) { nullNormalTrueUpToDateFalseError = false; stringResource(R.string.error) }else tmpStatus.ifBlank { nullNormalTrueUpToDateFalseError = true; stringResource(R.string.repo_status_uptodate) }
                                Text(
                                    text = text,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = MyStyleKt.ClickableText.modifier,
                                    fontWeight = defaultFontWeight,
                                    color = if(nullNormalTrueUpToDateFalseError == null) Color.Unspecified else if(nullNormalTrueUpToDateFalseError == true) MyStyleKt.TextColor.getHighlighting() else MyStyleKt.TextColor.error(),
                                    )
                            } else {  
                                ClickableText (
                                    text = (
                                            if (repoDto.workStatus == Cons.dbRepoWorkStatusMerging
                                                || repoDto.workStatus==Cons.dbRepoWorkStatusRebasing
                                                || repoDto.workStatus==Cons.dbRepoWorkStatusCherrypicking
                                            ) {
                                                stringResource(R.string.require_actions)
                                            } else if (repoDto.workStatus == Cons.dbRepoWorkStatusHasConflicts) {
                                                stringResource(R.string.repo_status_has_conflict)
                                            } else if(repoDto.workStatus == Cons.dbRepoWorkStatusNeedCommit) {
                                                stringResource(R.string.repo_status_need_commit)
                                            } else if (repoDto.workStatus == Cons.dbRepoWorkStatusNeedSync) {
                                                stringResource(R.string.repo_status_need_sync)
                                            } else if (repoDto.workStatus == Cons.dbRepoWorkStatusNeedPull) {
                                                stringResource(R.string.repo_status_need_pull)
                                            } else if (repoDto.workStatus == Cons.dbRepoWorkStatusNeedPush) {
                                                stringResource(R.string.repo_status_need_push)
                                            }else if (repoDto.workStatus == Cons.dbRepoWorkStatusNoHEAD) {
                                                stringResource(R.string.no_commit)
                                            } else {
                                                ""  
                                            }
                                            ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = MyStyleKt.ClickableText.modifier.clickable(enabled = repoStatusGood) {
                                        workStatusOnclick(repoDto, repoDto.workStatus)  
                                    },
                                    fontWeight = defaultFontWeight
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InLineIcon(
                            icon = Icons.Outlined.Folder,
                            tooltipText = stringResource(R.string.storage)
                        )
                        ScrollableRow {
                            ClickableText (
                                text = repoDto.cachedAppRelatedPath(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = MyStyleKt.ClickableText.modifier.combinedClickable(
                                    onLongClick = { 
                                        clipboardManager.setText(AnnotatedString(repoDto.fullSavePath))
                                        Msg.requireShow(activityContext.getString(R.string.copied))
                                    }
                                ) {  
                                    goToFilesPage(repoDto.fullSavePath)
                                },
                                fontWeight = defaultFontWeight
                            )
                        }
                    }
                    if(!repoNotReady) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val hasUncheckedErr = repoDto.latestUncheckedErrMsg.isNotBlank()
                            InLineIcon(
                                icon = Icons.Outlined.Dangerous,
                                tooltipText = stringResource(R.string.repo_label_error)
                            )
                            ClickableText (
                                text = if (hasUncheckedErr) repoDto.getCachedOneLineLatestUnCheckedErrMsg() else stringResource(R.string.repo_err_no_err_or_all_checked),
                                maxLines = 1,
                                color = if (hasUncheckedErr) MyStyleKt.ClickableText.getErrColor() else MyStyleKt.ClickableText.getColor(),
                                fontWeight = defaultFontWeight,
                                modifier = MyStyleKt.ClickableText.modifier.clickable {
                                    if (hasUncheckedErr) {
                                        val errMsg = StringBuilder("${activityContext.getString(R.string.repo)}: ")
                                            .append(repoDto.repoName)
                                            .append("\n\n")
                                            .append("${activityContext.getString(R.string.error)}: ")
                                            .append(repoDto.latestUncheckedErrMsg)
                                            .toString()
                                        initErrMsgDialog(repoDto, errMsg)
                                    }else {
                                        goToErrScreen(repoDto.id)
                                    }
                                } ,
                            )
                        }
                    }
                    if(repoStatusGood && repoDto.hasOther()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InLineIcon(
                                icon = Icons.AutoMirrored.Filled.Notes,
                                tooltipText = stringResource(R.string.other)
                            )
                            ScrollableRow {
                                Text(
                                    text = repoDto.getOther(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = defaultFontWeight,
                                    modifier = MyStyleKt.ClickableText.modifier,
                                )
                            }
                        }
                    }
                    if(repoDto.parentRepoValid) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InLineIcon(
                                icon = Icons.Outlined.Home,
                                tooltipText = stringResource(R.string.parent_repo)
                            )
                            ScrollableRow {
                                ClickableText(
                                    text = repoDto.parentRepoName,
                                    maxLines = 1,
                                    modifier = MyStyleKt.ClickableText.modifier.clickable {  
                                        setCurRepo()
                                        pageRequest.value = PageRequest.goParent
                                    },
                                    fontWeight = defaultFontWeight
                                )
                            }
                        }
                    }
                }
            }else {  
                val iconSize = remember {28.dp}
                val iconPressedSize = remember {36.dp}
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MySelectionContainer {
                            Text(
                                text = repoDto.createErrMsgForView(activityContext),
                                color= MyStyleKt.TextColor.error(),
                                modifier = Modifier.combinedClickable {}
                            )
                        }
                    }
                    ScrollableRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InLineIcon(
                            icon = Icons.Filled.Delete,
                            tooltipText = stringResource(R.string.del_repo),
                            iconModifier = Modifier.size(iconSize),
                            pressedCircleSize = iconPressedSize,
                        ) { requireDelRepo(repoDto) }
                        InLineIcon(
                            icon = Icons.Filled.Replay,
                            tooltipText = stringResource(R.string.retry),
                            iconModifier = Modifier.size(iconSize),
                            pressedCircleSize = iconPressedSize,
                        ) { doCloneSingle(repoDto) }
                        InLineIcon(
                            icon = Icons.Filled.EditNote,
                            tooltipText = stringResource(R.string.edit_repo),
                            iconModifier = Modifier.size(iconSize),
                            pressedCircleSize = iconPressedSize,
                        ) {
                            goToCloneScreen(repoDto.id)
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RepoTitle(
    haptic: HapticFeedback,
    repoDto: RepoEntity,
    isSelectionMode:Boolean,
    itemSelected: Boolean,
    titleOnClick: (RepoEntity) -> Unit,
    titleOnLongClick:(RepoEntity) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .combinedClickable(onLongClick = {
                titleOnLongClick(repoDto)
            }) {
                titleOnClick(repoDto)
            }
            .then(
                if (itemSelected) {
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                } else {
                    Modifier
                }
            ),
    ) {
        ScrollableRow(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 5.dp, end = MyStyleKt.defaultIconSize),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RepoCardTitleText(repoDto.repoName)
        }
        if(isSelectionMode) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(MyStyleKt.defaultIconSize)
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SimpleCheckBox(
                    enabled = itemSelected,
                    modifier = MyStyleKt.Icon.modifier,
                )
            }
        }
    }
}
