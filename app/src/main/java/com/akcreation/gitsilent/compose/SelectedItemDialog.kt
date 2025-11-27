package com.akcreation.gitsilent.compose

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter

private val trailingIconSize = MyStyleKt.defaultIconSize
@Composable
fun <T> SelectedItemDialog(
    title: String = stringResource(R.string.selected_str),
    selectedItems:List<T>,
    formatter:(T)->String,
    switchItemSelected:(T)->Unit,  
    clearAll:()->Unit,
    closeDialog:()->Unit,
    onCancel:()->Unit = closeDialog
) {
    SelectedItemDialog3(
        title = title,
        selectedItems = selectedItems,
        text = { Text(text = formatter(it)) },
        textFormatterForCopy = formatter,
        switchItemSelected = switchItemSelected,
        clearAll = clearAll,
        closeDialog = closeDialog,
        onCancel = onCancel,
    )
}
@Composable
private fun <T> SelectedItemDialog2(
    selectedItems:List<T>,
    title:String,
    text:@Composable BoxScope.(T) -> Unit,
    trailIcon:@Composable BoxScope.(T) -> Unit,
    clearAll:()->Unit,
    closeDialog:()->Unit,
    onCancel:()->Unit,
    onCopy:()->Unit
) {
    ConfirmDialog3(
        title = title,
        requireShowTextCompose = true,
        textCompose = {
            LazyColumn {
                selectedItems.forEachBetter {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            text(it)
                            trailIcon(it)
                        }
                        MyHorizontalDivider()
                    }
                }
            }
        },
        customCancel = {
            ScrollableRow {
                if(selectedItems.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            closeDialog()
                            clearAll()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.clear),
                            color = MyStyleKt.TextColor.danger(),
                        )
                    }
                }
                TextButton(
                    onClick = onCancel
                ) {
                    Text(
                        text = stringResource(R.string.close),
                        color = Color.Unspecified,
                    )
                }
            }
        },
        onCancel = onCancel,
        okBtnEnabled = selectedItems.isNotEmpty(),
        okBtnText = stringResource(R.string.copy),
    ) {  
        onCopy()
    }
}
@Composable
fun <T> SelectedItemDialog3(
    title: String = stringResource(R.string.selected_str),
    selectedItems:List<T>,
    switchItemSelected:(T)->Unit = {},  
    text:@Composable RowScope.(T) -> Unit,
    textFormatterForCopy:(T)->String,
    customText:@Composable (BoxScope.(T) -> Unit)? = null,
    customTrailIcon:@Composable (BoxScope.(T) -> Unit)? = null,
    textPadding: PaddingValues = PaddingValues(start = 5.dp, end = trailingIconSize),
    clearAll:()->Unit,
    closeDialog:()->Unit,
    onCancel:()->Unit = closeDialog
) {
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current
    SelectedItemDialog2(
        selectedItems = selectedItems,
        title = title,
        text = {
            if(customText != null) {
                customText(it)
            }else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(textPadding).align(Alignment.CenterStart).horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MySelectionContainer {
                        text(it)
                    }
                }
            }
        },
        trailIcon = {
            if(customTrailIcon != null) {
                customTrailIcon(it)
            }else {
                Row(
                    modifier = Modifier.size(trailingIconSize).align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { switchItemSelected(it) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = stringResource(R.string.trash_bin_icon_for_delete_item)
                        )
                    }
                }
            }
        },
        clearAll = clearAll,
        closeDialog = closeDialog,
        onCancel = onCancel,
        onCopy = {
            closeDialog()
            doJobThenOffLoading {
                val lb = Cons.lineBreak
                val sb = StringBuilder()
                selectedItems.forEachBetter {
                    sb.append(textFormatterForCopy(it)).append(lb)
                }
                clipboardManager.setText(AnnotatedString(sb.removeSuffix(lb).toString()))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
    )
}
