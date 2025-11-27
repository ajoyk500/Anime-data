package com.akcreation.gitsilent.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FontDownloadOff
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.syntaxhighlight.base.PLFont
import com.akcreation.gitsilent.syntaxhighlight.markdown.MarkDownSyntaxHighlighter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MarkDownDialog(
    title: String = stringResource(R.string.msg),
    text: String,
    previewModeOn: MutableState<Boolean>,
    useSystemFonts: MutableState<Boolean>,
    basePathNoEndSlash:String,
    close: () -> Unit,
    copy: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val highlightedMarkdownText = remember { mutableStateOf<AnnotatedString?>(null) }
    val annotatedText = remember { AnnotatedString(text) }
    val switchBetweenRawAndRenderedContent = { previewModeOn.value = !previewModeOn.value }
    val loadingForLetMarkDownContainerRefreshAfterSwitchFont = remember { mutableStateOf(false) }
    val switchFonts = {
        useSystemFonts.value = !useSystemFonts.value
        loadingForLetMarkDownContainerRefreshAfterSwitchFont.value = true
        scope.launch {
            delay(50)
            loadingForLetMarkDownContainerRefreshAfterSwitchFont.value = false
        }
        Unit
    }
    val rawOrHighlightedText = { highlightedMarkdownText.value ?: annotatedText}
    val font = remember(useSystemFonts.value) { if(useSystemFonts.value) null else PLFont.codeFont }
    ConfirmDialog3(
        title = title,
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                if(previewModeOn.value) {
                    if(loadingForLetMarkDownContainerRefreshAfterSwitchFont.value) {
                        Text(
                            text = text,
                            fontFamily = font
                        )
                    }else {
                        MarkDownContainer(
                            content = text,
                            style = LocalTextStyle.current.copy(fontFamily = font),
                            basePathNoEndSlash = basePathNoEndSlash,
                            onLinkClicked = { false },
                        )
                    }
                }else {
                    MySelectionContainer {
                        Text(
                            text = rawOrHighlightedText(),
                            fontFamily = font
                        )
                    }
                }
            }
        },
        customCancel = {
            ScrollableRow {
                IconButton(
                    onClick = switchFonts
                ) {
                    Icon(
                        imageVector = if(useSystemFonts.value) Icons.Filled.FontDownloadOff else Icons.Filled.FontDownload,
                        contentDescription = "switch use system fonts or not"
                    )
                }
                IconButton(
                    onClick = switchBetweenRawAndRenderedContent
                ) {
                    Icon(
                        imageVector = if(previewModeOn.value) Icons.AutoMirrored.Filled.Notes else Icons.Filled.RemoveRedEye,
                        contentDescription = "switch preview mode and text mode"
                    )
                }
                TextButton(
                    onClick = close
                ) {
                    Text(
                        text = stringResource(R.string.close),
                    )
                }
            }
        },
        customOk = {
            TextButton(
                onClick = {
                    copy()
                    close()
                }
            ) {
                Text(
                    text = stringResource(R.string.copy),
                )
            }
        },
        onCancel = close,
    )
    LaunchedEffect(previewModeOn.value) {
        if(!previewModeOn.value && highlightedMarkdownText.value == null) {
            MarkDownSyntaxHighlighter(text) {
                highlightedMarkdownText.value = it
            }.analyze()
        }
    }
}
