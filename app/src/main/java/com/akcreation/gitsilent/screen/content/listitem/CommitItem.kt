package com.akcreation.gitsilent.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.InLineCopyIcon
import com.akcreation.gitsilent.compose.InLineIcon
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SingleLineClickableText
import com.akcreation.gitsilent.dto.Box
import com.akcreation.gitsilent.git.CommitDto
import com.akcreation.gitsilent.git.DrawCommitNode
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import com.akcreation.gitsilent.utils.listItemPadding
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import com.akcreation.gitsilent.utils.time.TimeZoneUtil
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommitItem(
    drawLocalAheadUpstreamCount: Int,
    commitHistoryGraph:Boolean,
    density: Density,
    nodeCircleRadiusInPx:Float,
    nodeCircleStartOffsetX:Float,
    nodeLineWidthInPx:Float,
    lineDistanceInPx:Float,
    showBottomSheet: MutableState<Boolean>,
    curCommit: CustomStateSaveable<CommitDto>,
    curCommitIdx:MutableIntState,
    idx:Int,
    commitDto:CommitDto,
    requireBlinkIdx:MutableIntState,  
    lastClickedItemKey:MutableState<String>,
    shouldShowTimeZoneInfo:Boolean,
    showItemMsg:(CommitDto)->Unit,
    onClick:(CommitDto)->Unit={}
) {
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val updateCurObjState = {
        curCommit.value = CommitDto()
        curCommitIdx.intValue = -1
        curCommit.value = commitDto
        curCommitIdx.intValue = idx
    }
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawNode(
                commitItemIdx = idx,
                drawLocalAheadUpstreamCount = drawLocalAheadUpstreamCount,
                commitHistoryGraph = commitHistoryGraph,
                c = commitDto,
                density = density,
                nodeCircleRadiusInPx = nodeCircleRadiusInPx,
                nodeCircleStartOffsetX = nodeCircleStartOffsetX,
                nodeLineWidthInPx = nodeLineWidthInPx,
                lineDistanceInPx = lineDistanceInPx,
            )
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = commitDto.oidStr
                    onClick(commitDto)
                },
                onLongClick = {  
                    lastClickedItemKey.value = commitDto.oidStr
                    updateCurObjState()
                    showBottomSheet.value = true
                },
            )
            .then(
                if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue == idx) {
                    val highlightColor = Modifier.background(UIHelper.getHighlightingBackgroundColor())
                    doJobThenOffLoading {
                        delay(UIHelper.getHighlightingTimeInMills())  
                        requireBlinkIdx.intValue = -1  
                    }
                    highlightColor
                } else if (commitDto.oidStr == lastClickedItemKey.value) {
                    Modifier.background(UIHelper.getLastClickedColor())
                } else {
                    Modifier
                }
            )
            .listItemPadding()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Filled.Commit,
                tooltipText = stringResource(R.string.commit)
            )
            Text(text = commitDto.shortOidStr,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(commitDto.oidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Filled.Person,
                tooltipText = stringResource(R.string.author)
            )
            ScrollableRow {
                Text(text = commitDto.getFormattedAuthorInfo(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        if(!commitDto.authorAndCommitterAreSame()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                InLineIcon(
                    icon = Icons.Outlined.Person,
                    tooltipText = stringResource(R.string.committer)
                )
                ScrollableRow {
                    Text(text = commitDto.getFormattedCommitterInfo(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.Filled.CalendarMonth,
                tooltipText = stringResource(R.string.date)
            )
            ScrollableRow {
                Text(text = if(shouldShowTimeZoneInfo) TimeZoneUtil.appendUtcTimeZoneText(commitDto.dateTime, commitDto.originTimeOffsetInMinutes) else commitDto.dateTime,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            InLineIcon(
                icon = Icons.AutoMirrored.Filled.Message,
                tooltipText = stringResource(R.string.msg)
            )
            SingleLineClickableText(commitDto.getCachedOneLineMsg()) {
                lastClickedItemKey.value = commitDto.oidStr
                updateCurObjState()
                showItemMsg(commitDto)
            }
        }
        if(commitDto.branchShortNameList.isNotEmpty()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                InLineIcon(
                    icon = ImageVector.vectorResource(R.drawable.branch),
                    tooltipText = (if(commitDto.branchShortNameList.size > 1) stringResource(R.string.branches) else stringResource(R.string.branch))
                )
                ScrollableRow {
                    Text(text = commitDto.cachedBranchShortNameList(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }
        }
        if(commitDto.tagShortNameList.isNotEmpty()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                InLineIcon(
                    icon = Icons.AutoMirrored.Filled.Label,
                    tooltipText = (if(commitDto.tagShortNameList.size > 1) stringResource(R.string.tags) else stringResource(R.string.tag))
                )
                ScrollableRow {
                    Text(text = commitDto.cachedTagShortNameList(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }
        }
        if(commitDto.parentShortOidStrList.isNotEmpty()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                InLineIcon(
                    icon = Icons.Filled.AccountTree,
                    tooltipText = (if(commitDto.parentShortOidStrList.size > 1) stringResource(R.string.parents) else stringResource(R.string.parent))
                )
                ScrollableRow {
                    Text(text = commitDto.cachedParentShortOidStrList(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }
        }
        if(commitDto.hasOther()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                InLineIcon(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    tooltipText = stringResource(R.string.other)
                )
                ScrollableRow {
                    Text(text = commitDto.getOther(activityContext, false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }
        }
    }
}
@Composable
private fun Modifier.drawNode(
    commitItemIdx:Int,
    drawLocalAheadUpstreamCount: Int,
    commitHistoryGraph:Boolean,
    c: CommitDto,
    density: Density,
    nodeCircleRadiusInPx:Float,
    nodeCircleStartOffsetX:Float,
    nodeLineWidthInPx:Float,
    lineDistanceInPx:Float,
):Modifier {
    if(commitHistoryGraph.not()) return this;
    val isRtl = UIHelper.isRtlLayout()
    return drawBehind {
        val horizontalWidth = size.width
        val verticalHeight = size.height
        val startOffSetX = if(isRtl) 0f else horizontalWidth
        val verticalCenter = verticalHeight/2
        var initInputLineStartX = getInitStartX(isRtl, startOffSetX, nodeCircleStartOffsetX)
        var inputLineStartX = initInputLineStartX
        var circleEndX = Box(inputLineStartX)
        var lastStartX = 0F;
        c.draw_inputs.forEachIndexedBetter { idx, node->
            lastStartX = inputLineStartX
            if(isRtl) {
                inputLineStartX = initInputLineStartX + (idx * lineDistanceInPx)
            }else {
                inputLineStartX = initInputLineStartX - (idx * lineDistanceInPx)
            }
            if(node.inputIsEmpty.not()) {
                val color = getColor(idx, commitItemIdx, drawLocalAheadUpstreamCount)
                drawInputLinesAndCircle(
                    node,
                    nodeCircleRadiusInPx,
                    nodeLineWidthInPx,
                    inputLineStartX,
                    verticalCenter,
                    color,
                    circleEndX
                )
                node.mergedList.forEachBetter { node ->
                    drawInputLinesAndCircle(
                        node,
                        nodeCircleRadiusInPx,
                        nodeLineWidthInPx,
                        inputLineStartX,
                        verticalCenter,
                        color,
                        circleEndX
                    )
                }
            }
        }
        var initOutputLineStartX = getInitStartX(isRtl, startOffSetX, nodeCircleStartOffsetX)
        var outputLineStartX = initOutputLineStartX
        c.draw_outputs.forEachIndexedBetter { idx, node->
            lastStartX = outputLineStartX
            if(isRtl) {
                outputLineStartX = initOutputLineStartX + (idx * lineDistanceInPx)
            }else {
                outputLineStartX = initOutputLineStartX - (idx * lineDistanceInPx)
            }
            if(node.outputIsEmpty.not()) {
                val color = getColor(idx, commitItemIdx, drawLocalAheadUpstreamCount)
                drawOutputLinesAndCircle(
                    node,
                    nodeCircleRadiusInPx,
                    nodeLineWidthInPx,
                    outputLineStartX,
                    verticalHeight,
                    verticalCenter,
                    color,
                    circleEndX
                )
                node.mergedList.forEachBetter { node ->
                    drawOutputLinesAndCircle(
                        node,
                        nodeCircleRadiusInPx,
                        nodeLineWidthInPx,
                        outputLineStartX,
                        verticalHeight,
                        verticalCenter,
                        color,
                        circleEndX
                    )
                }
            }
        }
    }
}
private fun getInitStartX(isRtl: Boolean, startOffSetX: Float, nodeCircleStartOffsetX: Float): Float {
    return if(isRtl) startOffSetX + nodeCircleStartOffsetX else (startOffSetX - nodeCircleStartOffsetX)
}
private fun DrawScope.drawInputLinesAndCircle(
    node: DrawCommitNode,
    nodeCircleRadiusInPx:Float,
    nodeLineWidthInPx:Float,
    inputLineStartX:Float,
    verticalCenter:Float,
    color:Color,
    circleEndX:Box<Float>
) {
    val endX = if(node.endAtHere) circleEndX.value else inputLineStartX
    drawLine(
        color = color,
        blendMode = DrawCommitNode.colorBlendMode,
        strokeWidth = nodeLineWidthInPx,  
        start = Offset(inputLineStartX, 0f),
        end = Offset(endX, verticalCenter),
    )
    if(node.circleAtHere) {
        circleEndX.value = endX
        drawCircle(
            color = color, 
            blendMode = DrawCommitNode.colorBlendMode,
            radius = nodeCircleRadiusInPx, 
            center = Offset(endX, verticalCenter) 
        )
    }
}
private fun DrawScope.drawOutputLinesAndCircle(
    node: DrawCommitNode,
    nodeCircleRadiusInPx:Float,
    nodeLineWidthInPx:Float,
    outputLineStartX:Float,
    verticalHeight:Float,
    verticalCenter:Float,
    color:Color,
    circleEndX:Box<Float>
) {
    if(node.circleAtHere) {
        drawCircle(
            color = color, 
            blendMode = DrawCommitNode.colorBlendMode,
            radius = nodeCircleRadiusInPx, 
            center = Offset(circleEndX.value, verticalCenter) 
        )
    }
    val startX = if(node.startAtHere) circleEndX.value else outputLineStartX
    drawLine(
        color = color,
        blendMode = DrawCommitNode.colorBlendMode,
        strokeWidth = nodeLineWidthInPx,  
        start = Offset(startX, verticalCenter),
        end = Offset(outputLineStartX, verticalHeight),
    )
}
private fun getColor(lineIdx: Int, commitItemIdx:Int, drawLocalAheadUpstreamCount:Int) :Color {
    return if(commitItemIdx < drawLocalAheadUpstreamCount) {
        DrawCommitNode.localAheadUpstreamColor()
    }else {
        DrawCommitNode.getNodeColorByIndex(lineIdx)
    }
}
