package com.akcreation.gitsilent.fileeditor.texteditor.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.fileeditor.texteditor.state.MyTextFieldState
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.syntaxhighlight.base.PLFont
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.MyLog
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "MyTextField"
@Composable
internal fun MyTextField(
    scrollIfInvisible:()->Unit,
    readOnly:Boolean,
    focusThisLine:Boolean,
    textFieldState: MyTextFieldState,
    enabled: Boolean,
    onUpdateText: (TextFieldValue) -> Unit,
    onContainNewLine: (TextFieldValue) -> Unit,
    onFocus: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    fontSize:Int,
    fontColor:Color,
) {
    val inDarkTheme = Theme.inDarkTheme
    val textStyle = LocalTextStyle.current
    val currentTextField = textFieldState.value.let { remember(it) { mutableStateOf(it) } }
    val focusRequester = remember(focusThisLine) { if(focusThisLine) FocusRequester() else null }
    val alreadyCalledContainsNewLine = remember(focusThisLine) { AtomicBoolean(false) }
    BasicTextField(
        value = currentTextField.value,
        readOnly = readOnly,
        enabled = enabled,
        onValueChange = ovc@{ newState ->
            val indexOfLineBreak = newState.text.indexOf('\n')
            if (indexOfLineBreak != -1) {
                if(alreadyCalledContainsNewLine.compareAndSet(false, true)) {
                    onContainNewLine(newState)
                }
            } else {
                alreadyCalledContainsNewLine.set(false)
                val lastState = currentTextField.value
                val newState = keepStylesIfPossible(
                    newState,
                    lastState,
                    textChangedCallback = scrollIfInvisible
                )
                currentTextField.value = newState
                onUpdateText(newState)
            }
        },
        textStyle = textStyle.copy(
            fontSize = fontSize.sp,
            color = fontColor,
            fontFamily = PLFont.editorCodeFont(),
        ),
        cursorBrush = SolidColor(if(inDarkTheme) Color.LightGray else Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 2.dp)
            .then(
                if(focusRequester != null) {
                    Modifier.focusRequester(focusRequester)
                }else {
                    Modifier
                }
            )
            .onFocusChanged {
                if (it.isFocused) {
                    onFocus(
                        keepStylesIfPossible(
                            newState = currentTextField.value,
                            lastState = textFieldState.value,
                            textChangedCallback = {}
                        )
                    )
                }
            }
    )
    if(focusThisLine) {
        LaunchedEffect(Unit) {
            runCatching {
                focusRequester?.requestFocus()
            }
        }
    }
}
private fun keepStylesIfPossible(
    newState: TextFieldValue,
    lastState: TextFieldValue,
    textChangedCallback: () -> Unit,
) : TextFieldValue {
    try {
        val textChanged = lastState.text.length != newState.text.length || lastState.text != newState.text
        if(textChanged) {
            textChangedCallback()
        }else {
            return newState.copy(annotatedString = lastState.annotatedString)
        }
        val newTextLen = newState.text.length
        val validSpans = mutableListOf<AnnotatedString.Range<SpanStyle>>()
        for(it in lastState.annotatedString.spanStyles) {
            if(it.start < 0) {
                break
            }
            if(it.end > newTextLen) {
                validSpans.add(it.copy(start = it.start, end = newTextLen))
                break
            }
            validSpans.add(it)
        }
        if(validSpans.isEmpty()) {
            return newState
        }
        val lastSpanIndex = validSpans.last().end
        if(lastSpanIndex < newTextLen) {
            validSpans.add(AnnotatedString.Range(MyStyleKt.emptySpanStyle, lastSpanIndex, newTextLen))
        }
        val newAnnotatedString = newState.annotatedString
        return newState.copy(
            annotatedString = AnnotatedString(
                text = newAnnotatedString.text,
                spanStyles = validSpans,
                paragraphStyles = newAnnotatedString.paragraphStyles
            )
        )
    }catch (e: Exception) {
        MyLog.e(TAG, "#keepStylesIfPossible err: ${e.localizedMessage}")
        e.printStackTrace()
        return newState
    }
}
