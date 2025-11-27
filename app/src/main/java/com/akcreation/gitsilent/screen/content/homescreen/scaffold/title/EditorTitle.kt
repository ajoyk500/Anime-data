package com.akcreation.gitsilent.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.Difference
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.compose.FilterTextField
import com.akcreation.gitsilent.compose.ReadOnlyIcon
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SimpleCheckBox
import com.akcreation.gitsilent.compose.SmallIcon
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.dev.editorMergeModeTestPassed
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClickRequest
import com.akcreation.gitsilent.screen.shared.EditorPreviewNavStack
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.settings.util.EditorSettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.onOffText
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorTitle(
    disableSoftKb: MutableState<Boolean>,
    recentFileListIsEmpty: Boolean,
    recentFileListFilterModeOn: Boolean,
    recentListFilterKeyword: CustomStateSaveable<TextFieldValue>,
    getActuallyRecentFilesListState: () -> LazyStaggeredGridState,
    getActuallyRecentFilesListLastPosition: () -> MutableState<Int>,
    patchModeOn: MutableState<Boolean>,
    previewNavStack: EditorPreviewNavStack,
    previewingPath: String,
    isPreviewModeOn: Boolean,
    previewLastScrollPosition: MutableState<Int>,
    scope: CoroutineScope,
    editorPageShowingFilePath: MutableState<FilePath>,
    editorPageRequestFromParent: MutableState<String>,
    editorSearchMode: Boolean,
    editorSearchKeyword: CustomStateSaveable<TextFieldValue>,
    editorPageMergeMode: MutableState<Boolean>,
    readOnly: MutableState<Boolean>,
    editorPageShowingFileIsReady: MutableState<Boolean>,
    isSaving: MutableState<Boolean>,
    isEdited: MutableState<Boolean>,
    showReloadDialog: MutableState<Boolean>,
    showCloseDialog: MutableState<Boolean>,
    editorNeedSave: () -> Boolean,
) {
    val activityContext = LocalContext.current
    val softKbController = LocalSoftwareKeyboardController.current
    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val closeMenu = { dropDownMenuExpandState.value = false }
    val switchDropDownMenu = {
        softKbController?.hide()
        dropDownMenuExpandState.value = true
    }
    val enableMenuItem = editorPageShowingFilePath.value.isNotBlank()
    if(editorPageShowingFilePath.value.isNotBlank()) {
        val fileName =  remember(isPreviewModeOn, previewingPath, editorPageShowingFilePath.value) {
            FuckSafFile(activityContext, if(isPreviewModeOn) FilePath(previewingPath) else editorPageShowingFilePath.value).name
        }
        val filePathNoFileNameNoEndSlash = FsUtils.getPathWithInternalOrExternalPrefixAndRemoveFileNameAndEndSlash(
            path = if(isPreviewModeOn) previewingPath else editorPageShowingFilePath.value.ioPath,
            fileName
        )
        Column(
            modifier = Modifier
                .combinedClickable(
                    onDoubleClick = {
                        if (isPreviewModeOn) {
                            runBlocking {
                                defaultTitleDoubleClick(scope, previewNavStack.getCurrentScrollState(), previewLastScrollPosition)
                            }
                        } else {
                            defaultTitleDoubleClickRequest(editorPageRequestFromParent)
                        }
                    },
                    onLongClick = if (isPreviewModeOn.not()) ({
                        val newValue = disableSoftKb.value.not()
                        EditorSettingsUtil.updateDisableSoftKb(newValue, disableSoftKb)
                        Msg.requireShow(activityContext.getString(R.string.software_keyboard) + ": ${onOffText(newValue.not())}")
                    }) else null
                ) {  
                    if(isPreviewModeOn) {
                        editorPageRequestFromParent.value = PageRequest.showDetails
                    }else {
                        switchDropDownMenu()
                    }
                }
                .widthIn(min = MyStyleKt.Title.clickableTitleMinWidth)
        ) {
            if(editorSearchMode) {
                    FilterTextField(
                        filterKeyWord = editorSearchKeyword,
                        showClear = false,
                        containerModifier = Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent opke@{ keyEvent ->
                                if (keyEvent.type != KeyEventType.KeyDown) {
                                    return@opke false
                                }
                                if(keyEvent.key == Key.F3 && !keyEvent.isShiftPressed) {
                                    editorPageRequestFromParent.value = PageRequest.findNext
                                    return@opke true
                                }
                                if(keyEvent.key == Key.F3 && keyEvent.isShiftPressed) {
                                    editorPageRequestFromParent.value = PageRequest.findPrevious
                                    return@opke true
                                }
                                return@opke false
                            }
                    )
            }else {
                ScrollableRow {
                    if(isPreviewModeOn) {
                        SmallIcon(
                            imageVector = Icons.Filled.RemoveRedEye,
                            contentDescription = stringResource(R.string.preview),
                        )
                    }else {
                        if(editorPageMergeMode.value) {
                            SmallIcon(
                                imageVector = Icons.Filled.Merge,
                                contentDescription = stringResource(R.string.merge_mode),
                            )
                        }
                        if(patchModeOn.value) {
                            SmallIcon(
                                imageVector = Icons.Outlined.Difference,
                                contentDescription = stringResource(R.string.patch_mode),
                            )
                        }
                        if(disableSoftKb.value) {
                            SmallIcon(
                                imageVector = ImageVector.vectorResource(R.drawable.outline_keyboard_off_24),
                                contentDescription = stringResource(R.string.software_keyboard)+": ${onOffText(disableSoftKb.value.not())}",
                            )
                        }
                        if(readOnly.value) {
                            ReadOnlyIcon()
                        }
                    }
                    Text(
                        text =fileName,
                        fontSize = MyStyleKt.Title.firstLineFontSizeSmall,
                        maxLines=1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                ScrollableRow  {
                    Text(
                        text = filePathNoFileNameNoEndSlash,
                        fontSize = MyStyleKt.Title.secondLineFontSize,
                        maxLines=1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
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
                    editorPageRequestFromParent.value = PageRequest.requireSave
                    closeMenu()
                }
            )
            if(UserUtil.isPro() && (dev_EnableUnTestedFeature || editorMergeModeTestPassed)){
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.merge_mode)) },
                    trailingIcon = {
                        SimpleCheckBox(editorPageMergeMode.value)
                    },
                    onClick = {
                        editorPageMergeMode.value = !editorPageMergeMode.value
                    }
                )
            }
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.patch_mode)) },
                trailingIcon = {
                    SimpleCheckBox(patchModeOn.value)
                },
                onClick = {
                    val newValue = !patchModeOn.value
                    patchModeOn.value = newValue
                    SettingsUtil.update {
                        it.editor.patchModeOn = newValue
                    }
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.read_only)) },
                trailingIcon = {
                    SimpleCheckBox(readOnly.value)
                },
                onClick = {
                    editorPageRequestFromParent.value = PageRequest.doSaveIfNeedThenSwitchReadOnly
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.software_keyboard)) },
                trailingIcon = {
                    SimpleCheckBox(disableSoftKb.value.not())
                },
                onClick = {
                    EditorSettingsUtil.updateDisableSoftKb(disableSoftKb.value.not(), disableSoftKb)
                }
            )
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.details)) },
                onClick = {
                    closeMenu()
                    editorPageRequestFromParent.value = PageRequest.showDetails
                }
            )
        }
    }else {
        if(recentFileListFilterModeOn) {
            FilterTextField(filterKeyWord = recentListFilterKeyword)
        }else {
            ScrollableRow(
                modifier = Modifier
                    .combinedClickable(
                        onDoubleClick = {
                            defaultTitleDoubleClick(scope, getActuallyRecentFilesListState(), getActuallyRecentFilesListLastPosition())
                        },
                    ) { }
                    .widthIn(min = MyStyleKt.Title.clickableTitleMinWidth)
            ) {
                Text(
                    text = stringResource(if(recentFileListIsEmpty) R.string.editor else R.string.recent_files),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
