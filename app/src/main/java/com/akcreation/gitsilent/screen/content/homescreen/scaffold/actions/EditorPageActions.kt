package com.akcreation.gitsilent.screen.content.homescreen.scaffold.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.compose.FontSizeAdjuster
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.SimpleCheckBox
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.dev.editorEnableLineSelecteModeFromMenuTestPassed
import com.akcreation.gitsilent.dev.editorSearchTestPassed
import com.akcreation.gitsilent.dev.proFeatureEnabled
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.shared.EditorPreviewNavStack
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.settings.SettingsCons
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import kotlinx.coroutines.runBlocking

@Composable
fun EditorPageActions(
    disableSoftKb: MutableState<Boolean>,
    initPreviewMode:()->Unit,
    requireEditorScrollToPreviewCurPos:MutableState<Boolean>,
    isPreviewModeOn:Boolean,
    previewNavStack: EditorPreviewNavStack,
    previewPath: String,
    previewPathChanged: String,
    editorPageShowingFilePath: MutableState<FilePath>,
    editorPageShowingFileIsReady: MutableState<Boolean>,
    needRefreshEditorPage: MutableState<String>,
    editorPageTextEditorState: CustomStateSaveable<TextEditorState>,
    isSaving: MutableState<Boolean>,
    isEdited: MutableState<Boolean>,
    showReloadDialog: MutableState<Boolean>,
    showCloseDialog: MutableState<Boolean>,
    closeDialogCallback:CustomStateSaveable<(Boolean)->Unit>,
    doSave:suspend ()->Unit,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    editorPageRequest:MutableState<String>,
    editorPageSearchMode:MutableState<Boolean>,
    editorPageMergeMode:MutableState<Boolean>,
    editorPagePatchMode:MutableState<Boolean>,
    readOnlyMode:MutableState<Boolean>,
    editorSearchKeyword:String,
    isSubPageMode:Boolean,
    fontSize:MutableIntState,
    lineNumFontSize:MutableIntState,
    adjustFontSizeMode:MutableState<Boolean>,
    adjustLineNumFontSizeMode:MutableState<Boolean>,
    showLineNum:MutableState<Boolean>,
    showUndoRedo:MutableState<Boolean>,
    undoStack:UndoStack,
    editorNeedSave: () -> Boolean,
) {
    val hasGoodKeyword = editorSearchKeyword.isNotEmpty()
    if(isPreviewModeOn) {
        val currentIsNotAtHome = remember(previewPathChanged) { derivedStateOf { runBlocking { previewNavStack.currentIsRoot().not() } } }
        val canGoBack = remember(previewPathChanged) { derivedStateOf { runBlocking { previewNavStack.backStackIsNotEmpty() } } }
        val canGoForward = remember(previewPathChanged) { derivedStateOf { runBlocking { previewNavStack.aheadStackIsNotEmpty() } } }
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.edit),
            icon = Icons.Filled.Edit,
        ) {
            requireEditorScrollToPreviewCurPos.value = true
            editorPageRequest.value = PageRequest.requireEditPreviewingFile
        }
        LongPressAbleIconBtn(
            enabled = currentIsNotAtHome.value,
            tooltipText = stringResource(R.string.home),
            icon = Icons.Filled.Home,
        ) {
            editorPageRequest.value = PageRequest.requireBackToHome
        }
        LongPressAbleIconBtn(
            enabled = canGoBack.value,
            tooltipText = stringResource(R.string.go_back),
            icon = Icons.AutoMirrored.Filled.ArrowBackIos,
        ) {
            editorPageRequest.value = PageRequest.editorPreviewPageGoBack
        }
        LongPressAbleIconBtn(
            enabled = canGoForward.value,
            tooltipText = stringResource(R.string.go_forward),
            icon = Icons.AutoMirrored.Filled.ArrowForwardIos,
        ) {
            editorPageRequest.value = PageRequest.editorPreviewPageGoForward
        }
        LongPressAbleIconBtn(
            enabled = true,
            tooltipText = stringResource(R.string.refresh),
            icon = Icons.Filled.Refresh,
        ) {
            editorPageRequest.value = PageRequest.editor_RequireRefreshPreviewPage
        }
        return  
    }else if(editorPageSearchMode.value) {
        LongPressAbleIconBtn(
            enabled = hasGoodKeyword,
            tooltipText = stringResource(R.string.find_previous),
            icon = Icons.Filled.ArrowUpward,
            iconContentDesc = stringResource(R.string.find_previous),
        ) {
            editorPageRequest.value = PageRequest.findPrevious
        }
        LongPressAbleIconBtn(
            enabled = hasGoodKeyword,
            tooltipText = stringResource(R.string.find_next),
            icon = Icons.Filled.ArrowDownward,
            iconContentDesc = stringResource(R.string.find_next),
            onLongClick = {
                editorPageRequest.value = PageRequest.showFindNextAndAllCount
            }
        ) {
            editorPageRequest.value = PageRequest.findNext
        }
        return  
    }else if(adjustFontSizeMode.value) {
        FontSizeAdjuster(fontSize = fontSize, resetValue = SettingsCons.defaultFontSize)
        return
    }else if(adjustLineNumFontSizeMode.value) {
        FontSizeAdjuster(fontSize = lineNumFontSize, resetValue = SettingsCons.defaultLineNumFontSize)
        return
    }
    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val closeMenu = {dropDownMenuExpandState.value = false}
    val enableMenuItem = editorPageShowingFilePath.value.isNotBlank()
    val showMenuIcon = enableMenuItem
    if(enableMenuItem) {
        if(showUndoRedo.value) {
            val enableUndo = remember(undoStack.undoStackIsEmpty()) { undoStack.undoStackIsEmpty().not() }
            val undoStr = stringResource(R.string.undo)
            LongPressAbleIconBtn(
                enabled = enableUndo,
                tooltipText = undoStr,
                icon = Icons.AutoMirrored.Filled.Undo,
                iconContentDesc = undoStr,
                onLongClick = {
                    Msg.requireShow("$undoStr(${undoStack.undoStackSize()})")
                }
            ) {
                editorPageRequest.value = PageRequest.requestUndo
            }
            val enableRedo = remember(undoStack.redoStackIsEmpty()) { undoStack.redoStackIsEmpty().not() }
            val redoStr = stringResource(R.string.redo)
            LongPressAbleIconBtn(
                enabled = enableRedo,
                tooltipText = redoStr,
                icon = Icons.AutoMirrored.Filled.Redo,
                iconContentDesc = redoStr,
                onLongClick = {
                    Msg.requireShow("$redoStr(${undoStack.redoStackSize()})")
                }
            ) {
                editorPageRequest.value = PageRequest.requestRedo
            }
        }
        if(editorPageMergeMode.value) {
            LongPressAbleIconBtn(
                tooltipText = stringResource(R.string.previous_conflict),
                icon = Icons.Filled.ArrowUpward,
                iconContentDesc = stringResource(R.string.previous_conflict),
            ) {
                editorPageRequest.value = PageRequest.previousConflict
            }
            LongPressAbleIconBtn(
                tooltipText = stringResource(R.string.next_conflict),
                icon = Icons.Filled.ArrowDownward,
                iconContentDesc = stringResource(R.string.next_conflict),
                onLongClick = {
                    editorPageRequest.value = PageRequest.showNextConflictAndAllConflictsCount
                }
            ) {
                editorPageRequest.value = PageRequest.nextConflict
            }
        }
    }
    if(showMenuIcon) {
        val softKbController = LocalSoftwareKeyboardController.current
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.menu),
            icon = Icons.Filled.MoreVert,
            iconContentDesc = stringResource(R.string.menu),
            onClick = {
                softKbController?.hide()
                dropDownMenuExpandState.value = true
            }
        )
    }
    Row(modifier = Modifier.padding(top = MyStyleKt.TopBar.dropDownMenuTopPaddingSize)) {
        DropdownMenu(
            expanded = dropDownMenuExpandState.value,
            onDismissRequest = { closeMenu() }
        ) {
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.close)) },
                onClick = {
                    showCloseDialog.value=true
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.reload_file)) },
                onClick = {
                    showReloadDialog.value = true
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem && editorNeedSave(),
                text = { Text(stringResource(R.string.save)) },
                onClick = {
                    editorPageRequest.value = PageRequest.requireSave
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem && editorPageShowingFileIsReady.value,
                text = { Text(stringResource(R.string.preview)) },
                onClick = {
                    initPreviewMode()
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.open_as)) },
                onClick = {
                    editorPageRequest.value = PageRequest.requireOpenAs
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.go_to_line)) },
                onClick = {
                    editorPageRequest.value = PageRequest.goToLine  
                    closeMenu()
                }
            )
            if(UserUtil.isPro() && (dev_EnableUnTestedFeature || editorSearchTestPassed)){
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.find)) },
                    onClick = {
                        editorPageRequest.value = PageRequest.requireSearch  
                        closeMenu()
                    }
                )
            }
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.syntax_highlighting)) },
                onClick = {
                    editorPageRequest.value = PageRequest.selectSyntaxHighlighting
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.line_break)) },
                onClick = {
                    editorPageRequest.value = PageRequest.showLineBreakDialog
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.encoding)) },
                onClick = {
                    editorPageRequest.value = PageRequest.showSelectEncodingDialog
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.convert_encoding)) },
                onClick = {
                    editorPageRequest.value = PageRequest.convertEncoding
                    closeMenu()
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.file_history)) },
                onClick = {
                    editorPageRequest.value = PageRequest.requireGoToFileHistory
                    closeMenu()
                }
            )
            if(!isSubPageMode) {
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.show_in_files)) },
                    onClick = {
                        editorPageRequest.value = PageRequest.showInFiles
                        closeMenu()
                    }
                )
            }
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.show_undo_redo)) },
                trailingIcon = {
                    SimpleCheckBox(showUndoRedo.value)
                },
                onClick = {
                    val newValue = !showUndoRedo.value
                    showUndoRedo.value = newValue
                    SettingsUtil.update {
                        it.editor.showUndoRedo = newValue
                    }
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.show_line_num)) },
                trailingIcon = {
                    SimpleCheckBox(showLineNum.value)
                },
                onClick = {
                    val newValue = !showLineNum.value
                    showLineNum.value = newValue
                    SettingsUtil.update {
                        it.editor.showLineNum = newValue
                    }
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem && showLineNum.value,
                text = { Text(stringResource(R.string.line_num_size)) },
                onClick = {
                    closeMenu()
                    adjustLineNumFontSizeMode.value = true
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.font_size)) },
                onClick = {
                    closeMenu()
                    adjustFontSizeMode.value = true
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.tab_size)) },
                onClick = {
                    closeMenu()
                    editorPageRequest.value = PageRequest.showSetTabSizeDialog
                }
            )
            val autoCloseSymbolPair = remember { mutableStateOf(SettingsUtil.isEditorAutoCloseSymbolPairEnabled()) }
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.symbol_pair)) },
                trailingIcon = {
                    SimpleCheckBox(autoCloseSymbolPair.value)
                },
                onClick = {
                    val newValue = !autoCloseSymbolPair.value
                    autoCloseSymbolPair.value = newValue
                    SettingsUtil.update {
                        it.editor.autoCloseSymbolPair = newValue
                    }
                }
            )
            if(proFeatureEnabled(editorEnableLineSelecteModeFromMenuTestPassed)) {
                val selectModeOn = editorPageTextEditorState.value.isMultipleSelectionMode
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.select_mode)) },
                    trailingIcon = {
                        SimpleCheckBox(selectModeOn)
                    },
                    onClick = {
                        editorPageRequest.value = PageRequest.editorSwitchSelectMode
                    }
                )
            }
        }
    }
}
