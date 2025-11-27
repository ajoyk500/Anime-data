package com.akcreation.gitsilent.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.akcreation.gitsilent.constants.LineNum
import com.akcreation.gitsilent.dev.DevFeature
import com.akcreation.gitsilent.git.CompareLinePair
import com.akcreation.gitsilent.git.CompareLinePairHelper
import com.akcreation.gitsilent.git.CompareLinePairResult
import com.akcreation.gitsilent.git.DiffItemSaver
import com.akcreation.gitsilent.git.PuppyLine
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.getClipboardText
import com.akcreation.gitsilent.screen.functions.openFileWithInnerSubPageEditor
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.syntaxhighlight.base.PLFont
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.addTopPaddingIfIsFirstLine
import com.akcreation.gitsilent.utils.compare.result.IndexStringPart
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import com.akcreation.gitsilent.utils.paddingLineNumber
import com.akcreation.gitsilent.utils.replaceStringResList
import com.github.git24j.core.Diff

private const val TAG = "DiffRow"
@Composable
fun DiffRow(
    stateKeyTag:String,
    index:Int,
    line:PuppyLine,
    lineNumExpectLength:Int,
    stringPartList:List<IndexStringPart>? = null,
    fileFullPath:String,
    enableLineEditActions:Boolean,
    clipboardManager: ClipboardManager,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    repoId:String,
    showLineNum:Boolean,
    showOriginType:Boolean,
    fontSize: TextUnit,
    lineNumSize: TextUnit,
    getComparePairBuffer:() -> CompareLinePair,
    setComparePairBuffer: (CompareLinePair) -> Unit,
    betterCompare:Boolean,
    indexStringPartListMap:MutableMap<String, CompareLinePairResult>,
    enableSelectCompare: Boolean,
    matchByWords:Boolean,
    settings:AppSettings,
    navController:NavController,
    activityContext:Context,
    lineClickedMenuOffset: DpOffset,
    diffItemSaver: DiffItemSaver,
    initEditLineDialog: (content:String, lineNum:Int, prependOrAppendOrReplace:Boolean?, filePath:String) -> Unit,
    initDelLineDialog: (lineNum:Int, filePath:String) -> Unit,
    initRestoreLineDialog: (content:String, lineNum:Int, trueRestoreFalseReplace_param:Boolean, filePath:String) -> Unit,
) {
    val isNotEof = line.lineNum != LineNum.EOF.LINE_NUM
    val enableLineEditActions = enableLineEditActions && isNotEof
    val enableSelectCompare = enableSelectCompare && isNotEof
    val enableLineCopy = true
    val lineClickable = enableLineCopy || enableLineEditActions || enableSelectCompare
    val useStringPartList = !(stringPartList.isNullOrEmpty() || (stringPartList.indexOfFirst { it.modified.not() } == -1))
    val inDarkTheme = Theme.inDarkTheme
    val bgColor = UIHelper.getDiffLineBgColor(line, inDarkTheme)
    val textColor = UIHelper.getDiffLineTextColor(line, inDarkTheme)
    val lineNumColor = MyStyleKt.Diff.lineNumColorForDiff(inDarkTheme)
    val bgColorSpanStyle = remember(bgColor) { SpanStyle(background = bgColor) }
    val emptySpanStyle = remember { MyStyleKt.emptySpanStyle }
    val lineNum = paddingLineNumber(if(line.lineNum == LineNum.EOF.LINE_NUM) LineNum.EOF.TEXT else line.lineNum.toString(), lineNumExpectLength)
    val content = line.getContentNoLineBreak()
    val prefix = if(showOriginType && showLineNum.not()) {
        "${line.originType}:"
    } else if(showOriginType.not() && showLineNum) {
        "$lineNum:"
    } else if(showOriginType && showLineNum) {
        "${line.originType}$lineNum:"
    } else {
        ""
    }
    val expandedMenu = remember { mutableStateOf(false) }
    val compareLineToText = { contentOfLine:String, line:PuppyLine, text:String ->
        doJobThenOffLoading {
            val newcp = CompareLinePair(
                line1 = contentOfLine,
                line1OriginType = line.originType,
                line1Num = line.lineNum,
                line1Key = line.key,
                line2 = text,
                line2OriginType = CompareLinePairHelper.clipboardLineOriginType,
                line2Num = CompareLinePairHelper.clipboardLineNum,
                line2Key = CompareLinePairHelper.clipboardLineKey,
            )
            newcp.compare(
                betterCompare = betterCompare,
                matchByWords = matchByWords,
                map = indexStringPartListMap
            )
            setComparePairBuffer(CompareLinePair())
        }
    }
    val compareToClipboard = label@{ content:String, line:PuppyLine, trueContentToClipboardFalseClipboardToContent:Boolean ->
        if(line.originType == Diff.Line.OriginType.CONTEXT.toString()) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.can_t_compare_clipboard_to_context_line))
            return@label
        }
        val clipboardText = getClipboardText(clipboardManager)
        if(clipboardText == null) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.clipboard_is_empty))
            return@label
        }
        Msg.requireShow(activityContext.getString(R.string.comparing))
        doJobThenOffLoading {
            val newcp = if(trueContentToClipboardFalseClipboardToContent){  
                CompareLinePair(
                    line1 = content,
                    line1OriginType = line.originType,
                    line1Num = line.lineNum,
                    line1Key = line.key,
                    line2 = clipboardText,
                    line2OriginType = CompareLinePairHelper.clipboardLineOriginType,
                    line2Num = CompareLinePairHelper.clipboardLineNum,
                    line2Key = CompareLinePairHelper.clipboardLineKey,
                )
            } else {  
                CompareLinePair(
                    line1 = clipboardText,
                    line1OriginType = CompareLinePairHelper.clipboardLineOriginType,
                    line1Num = CompareLinePairHelper.clipboardLineNum,
                    line1Key = CompareLinePairHelper.clipboardLineKey,
                    line2 = content,
                    line2OriginType = line.originType,
                    line2Num = line.lineNum,
                    line2Key = line.key,
                )
            }
            newcp.compare(
                betterCompare = betterCompare,
                matchByWords = matchByWords,
                map = indexStringPartListMap
            )
        }
    }
    Row(
        modifier = (
                if (lineClickable) {
                    Modifier.clickable { expandedMenu.value = true }
                } else {
                    Modifier
                }
            )
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .background(UIHelper.getMatchedTextBgColorForDiff(inDarkTheme, line))
    ) {
        Row(
            modifier = (if(useStringPartList) Modifier else Modifier.background(bgColor))
                .fillMaxHeight()
                .addTopPaddingIfIsFirstLine(index)
        ) {
            Text(
                text = prefix,
                color = lineNumColor,
                fontSize = lineNumSize,
                fontFamily = PLFont.codeFont,  
                modifier = Modifier
                    .clickable {
                        openFileWithInnerSubPageEditor(
                            context = activityContext,
                            filePath = fileFullPath,
                            mergeMode = false,
                            readOnly = false,
                            goToLine = if(lineNum == LineNum.EOF.TEXT) {
                                LineNum.EOF.LINE_NUM
                            } else {
                                line.lineNum
                            }
                        )
                    }
                    .padding(start = (if(prefix.isNotEmpty()) 2.dp else 5.dp))
            )
        }
        val obtainStylePartList = { diffItemSaver.operateStylesMapWithReadLock { it.get(line.key) } }
        val contentModifier = Modifier
            .fillMaxWidth()
            .padding(end = 5.dp)
            .addTopPaddingIfIsFirstLine(index)
        if(useStringPartList) {
            SelectionRow(
                modifier = contentModifier
            ) {
                Text(
                    text = try {
                        buildAnnotatedString {
                            obtainStylePartList()?.let { stylePartList ->
                                PuppyLine.mergeStringAndStylePartList(stringPartList, stylePartList, bgColorSpanStyle).forEachBetter {
                                    withStyle(it.style) {
                                        append(content.substring(it.start, it.end))
                                    }
                                }
                            } ?: stringPartList.forEachIndexedBetter { idx, it ->
                                withStyle(style = if(it.modified) bgColorSpanStyle else emptySpanStyle) {
                                    append(content.substring(it.start, it.end))
                                }
                            }
                        }
                    }catch (e: Exception) {
                        MyLog.e(TAG, "DiffRow create substring err: lineNum=$lineNum, positionCode=1914714811075084, err=${e.localizedMessage}")
                        e.printStackTrace()
                        buildAnnotatedString {
                            withStyle(bgColorSpanStyle) {
                                append(content)
                            }
                        }
                    },
                    fontFamily = PLFont.diffCodeFont(),
                    color = textColor,
                    overflow = TextOverflow.Visible,
                    softWrap = true,
                    fontSize = fontSize,
                )
            }
        }else {
            SelectionRow(
                modifier = Modifier
                    .background(bgColor)
                    .then(contentModifier)
            ) {
                Text(
                    text = try {
                        buildAnnotatedString {
                            obtainStylePartList()?.forEachBetter {
                                withStyle(it.style) {
                                    append(content.substring(it.start, it.end))
                                }
                            } ?: append(content)
                        }
                    }catch (e: Exception) {
                        MyLog.e(TAG, "DiffRow create substring err: lineNum=$lineNum, positionCode=1855426513892273, err=${e.localizedMessage}")
                        e.printStackTrace()
                        buildAnnotatedString {
                            append(content)
                        }
                    },
                    fontFamily = PLFont.diffCodeFont(),
                    color = textColor,
                    overflow = TextOverflow.Visible,
                    softWrap = true,
                    fontSize = fontSize,
                )
            }
        }
        if(lineClickable) {
            DisableSelection {
                DropdownMenu(
                    expanded = expandedMenu.value,
                    onDismissRequest = { expandedMenu.value = false },
                    offset = lineClickedMenuOffset
                ) {
                    DropdownMenuItem(
                        text = { Text(line.originType+lineNum)},
                        enabled = false,
                        onClick ={}
                    )
                    if(enableLineEditActions) {
                        if(line.originType == Diff.Line.OriginType.ADDITION.toString() || line.originType == Diff.Line.OriginType.CONTEXT.toString()){
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit))},
                                onClick = {
                                    initEditLineDialog(line.getContentNoLineBreak(), line.lineNum, null, fileFullPath)
                                    expandedMenu.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.insert))},
                                onClick = {
                                    initEditLineDialog("", line.lineNum, true, fileFullPath)
                                    expandedMenu.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.append))},
                                onClick = {
                                    initEditLineDialog("", line.lineNum, false, fileFullPath)
                                    expandedMenu.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete))},
                                onClick = {
                                    initDelLineDialog(line.lineNum, fileFullPath)
                                    expandedMenu.value = false
                                }
                            )
                        }else if(line.originType == Diff.Line.OriginType.DELETION.toString()) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.restore))},
                                onClick = {
                                    initRestoreLineDialog(line.getContentNoLineBreak(), line.lineNum, true, fileFullPath)
                                    expandedMenu.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.replace))},
                                onClick = {
                                    initRestoreLineDialog(line.getContentNoLineBreak(), line.lineNum, false, fileFullPath)
                                    expandedMenu.value = false
                                }
                            )
                        }
                    }
                    if(enableSelectCompare) {
                        val cp = getComparePairBuffer()
                        val line1ready = cp.line1ReadyForCompare()
                        DropdownMenuItem(
                            enabled = line.key != cp.line1Key,
                            text = { Text(
                                if(line1ready) replaceStringResList(stringResource(R.string.compare_to_origintype_linenum), listOf(cp.line1OriginType + cp.line1Num))
                                else { stringResource(R.string.select_compare) }
                            )},
                            onClick = label@{
                                expandedMenu.value = false
                                if(line1ready) {
                                    if(line.originType == Diff.Line.OriginType.CONTEXT.toString() && cp.line1OriginType == line.originType) {
                                        Msg.requireShow(activityContext.getString(R.string.can_t_compare_both_context_type_lines))
                                        return@label
                                    }
                                    cp.line2 = content
                                    cp.line2Num = line.lineNum
                                    cp.line2OriginType = line.originType
                                    cp.line2Key = line.key
                                    Msg.requireShow(activityContext.getString(R.string.comparing))
                                    doJobThenOffLoading {
                                        cp.compare(
                                            betterCompare = betterCompare,
                                            matchByWords = matchByWords,
                                            map = indexStringPartListMap
                                        )
                                        setComparePairBuffer(CompareLinePair())
                                    }
                                }else {
                                    cp.line1 = content
                                    cp.line1Num = line.lineNum
                                    cp.line1OriginType = line.originType
                                    cp.line1Key = line.key
                                    Msg.requireShow(replaceStringResList(activityContext.getString(R.string.added_line_for_compare), listOf(line.originType+lineNum)) )
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.compare_to_clipboard)+" ->")},
                            onClick = {
                                expandedMenu.value = false
                                compareToClipboard(content, line, true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.compare_to_clipboard)+" <-")},
                            onClick = {
                                expandedMenu.value = false
                                compareToClipboard(content, line, false)
                            }
                        )
                        if(getComparePairBuffer().isEmpty().not()) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.clear_compare))},
                                onClick = {
                                    expandedMenu.value = false
                                    setComparePairBuffer(CompareLinePair())
                                }
                            )
                        }
                    }
                    if(enableLineCopy) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.copy))},
                            onClick = {
                                clipboardManager.setText(AnnotatedString(content))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                                expandedMenu.value = false
                            }
                        )
                    }
                    if(DevFeature.showMatchedAllAtDiff.state.value) {
                        DropdownMenuItem(
                            text = { Text(DevFeature.setDiffRowToNoMatched)},
                            onClick = {
                                expandedMenu.value = false
                                compareLineToText(content, line, "\n")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(DevFeature.setDiffRowToAllMatched)},
                            onClick = {
                                expandedMenu.value = false
                                compareLineToText(content, line, content)
                            }
                        )
                    }
                }
            }
        }
    }
}
