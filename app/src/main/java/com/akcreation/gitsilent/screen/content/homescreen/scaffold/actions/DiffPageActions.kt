package com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DensityLarge
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.SimpleCheckBox
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.dev.detailsDiffTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.SettingsUtil

private const val TAG = "DiffPageActions"
@Composable
fun DiffPageActions(
    isMultiMode: Boolean,
    fromTo:String,
    refreshPage: () -> Unit,
    request:MutableState<String>,
    requireBetterMatchingForCompare:MutableState<Boolean>,
    readOnlyModeOn:MutableState<Boolean>,  
    readOnlyModeSwitchable:Boolean,
    showLineNum:MutableState<Boolean>,
    showOriginType:MutableState<Boolean>,
    adjustFontSizeModeOn:MutableState<Boolean>,
    adjustLineNumSizeModeOn:MutableState<Boolean>,
    groupDiffContentByLineNum:MutableState<Boolean>,
    enableSelectCompare:MutableState<Boolean>,
    matchByWords:MutableState<Boolean>,
) {
    val fileChangeTypeIsModified = remember { true }
    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    if(fromTo == Cons.gitDiffFileHistoryFromTreeToPrev || fromTo == Cons.gitDiffFileHistoryFromTreeToLocal) {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.restore),
            icon =  Icons.Filled.Restore,
            iconContentDesc = stringResource(R.string.restore),
        ) {
            request.value = PageRequest.showRestoreDialog
        }
    }
    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.refresh),
        icon = Icons.Filled.Refresh,
        iconContentDesc = stringResource(id = R.string.refresh),
    ) {
        refreshPage()
    }
    if(isMultiMode){
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.expand_all),
            icon = Icons.Filled.DensityLarge,
            iconContentDesc = stringResource(R.string.expand_all),
        ) label@{
            request.value = PageRequest.expandAll
        }
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.collapse_all),
            icon = Icons.Filled.DensitySmall,
            iconContentDesc = stringResource(R.string.collapse_all),
        ) label@{
            request.value = PageRequest.collapseAll
        }
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.go_to_bottom),
            icon = Icons.Filled.KeyboardDoubleArrowDown,
            iconContentDesc = stringResource(R.string.go_to_bottom),
        ) label@{
            request.value = PageRequest.goToBottomOfCurrentFile
        }
    } else {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.open),
            icon = Icons.Filled.FileOpen,
            iconContentDesc = stringResource(id = R.string.open),
        ) label@{
            request.value = PageRequest.requireOpenInInnerEditor
        }
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.open_as),
            icon = Icons.AutoMirrored.Filled.OpenInNew,
            iconContentDesc = stringResource(id = R.string.open_as),
        ) label@{
            request.value = PageRequest.showOpenAsDialog
        }
    }
    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.menu),
        icon = Icons.Filled.MoreVert,
        iconContentDesc = stringResource(R.string.menu),
        onClick = {
            dropDownMenuExpandState.value = !dropDownMenuExpandState.value
        }
    )
    DropdownMenu(
        offset = DpOffset(x=100.dp, y=8.dp),
        expanded = dropDownMenuExpandState.value,
        onDismissRequest = { dropDownMenuExpandState.value=false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.create_patch) + (if(isMultiMode) " (${stringResource(R.string.all)})" else "")) },
            onClick = {
                request.value = PageRequest.createPatchForAllItems
                dropDownMenuExpandState.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.syntax_highlighting)) },
            onClick = {
                request.value = PageRequest.showSyntaxHighlightingSelectLanguageDialogForCurItem
                dropDownMenuExpandState.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.font_size)) },
            onClick = {
                adjustFontSizeModeOn.value = true
                dropDownMenuExpandState.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.line_num_size)) },
            onClick = {
                adjustLineNumSizeModeOn.value = true
                dropDownMenuExpandState.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.show_line_num)) },
            trailingIcon = {
                SimpleCheckBox(showLineNum.value)
            },
            onClick = {
                showLineNum.value = !showLineNum.value
                SettingsUtil.update {
                    it.diff.showLineNum = showLineNum.value
                }
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.show_change_type)) },
            trailingIcon = {
                SimpleCheckBox(showOriginType.value)
            },
            onClick = {
                showOriginType.value = !showOriginType.value
                SettingsUtil.update {
                    it.diff.showOriginType = showOriginType.value
                }
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.group_by_line)) },
            trailingIcon = {
                SimpleCheckBox(groupDiffContentByLineNum.value)
            },
            onClick = {
                groupDiffContentByLineNum.value = !groupDiffContentByLineNum.value
                SettingsUtil.update {
                    it.diff.groupDiffContentByLineNum = groupDiffContentByLineNum.value
                }
            }
        )
        if (fileChangeTypeIsModified && proFeatureEnabled(detailsDiffTestPassed)){
            DropdownMenuItem(
                text = { Text(stringResource(R.string.better_compare)) },
                trailingIcon = {
                    SimpleCheckBox(requireBetterMatchingForCompare.value)
                },
                onClick = {
                    requireBetterMatchingForCompare.value = !requireBetterMatchingForCompare.value
                    SettingsUtil.update {
                        it.diff.enableBetterButSlowCompare = requireBetterMatchingForCompare.value
                    }
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.match_by_words)) },
                trailingIcon = {
                    SimpleCheckBox(matchByWords.value)
                },
                onClick = {
                    matchByWords.value = !matchByWords.value
                    SettingsUtil.update {
                        it.diff.matchByWords = matchByWords.value
                    }
                }
            )
        }
        DropdownMenuItem(
            enabled = readOnlyModeSwitchable,
            text = { Text(stringResource(R.string.read_only)) },
            trailingIcon = {
                SimpleCheckBox(readOnlyModeOn.value)
            },
            onClick = {
                readOnlyModeOn.value = !readOnlyModeOn.value
                SettingsUtil.update {
                    it.diff.readOnly = readOnlyModeOn.value
                }
            }
        )
        DropdownMenuItem(
            enabled = fileChangeTypeIsModified,
            text = { Text(stringResource(R.string.select_compare)) },
            trailingIcon = {
                SimpleCheckBox(enableSelectCompare.value)
            },
            onClick = {
                enableSelectCompare.value = !enableSelectCompare.value
                SettingsUtil.update {
                    it.diff.enableSelectCompare = enableSelectCompare.value
                }
            }
        )
    }
}
