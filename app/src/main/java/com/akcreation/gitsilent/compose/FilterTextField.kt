package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.state.CustomStateSaveable

val filterTextFieldDefaultContainerModifier = Modifier.fillMaxWidth()
@Composable
fun FilterTextField(
    filterKeyWord: CustomStateSaveable<TextFieldValue>,
    loading:Boolean = false,
    placeholderText:String = stringResource(R.string.input_keyword),
    singleLine:Boolean = true,
    requireFocus: Boolean = true,
    containerModifier: Modifier = filterTextFieldDefaultContainerModifier,
    trailingIcon: (@Composable ()->Unit)? = null,
    showClear:Boolean = filterKeyWord.value.text.isNotEmpty(),
    onClear:(()->Unit)? = {filterKeyWord.value = TextFieldValue("")},
    onValueChange:(newValue:TextFieldValue)->Unit = { filterKeyWord.value = it },
) {
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val trailIcon:@Composable (() -> Unit)? = if(trailingIcon != null){
        trailingIcon
    }else if(showClear) {
        {
            LongPressAbleIconBtn(
                tooltipText = stringResource(R.string.clear),
                icon = Icons.Filled.Close,
            ) {
                onClear?.invoke()
            }
        }
    }else {
        null
    }
    Column(
        modifier = containerModifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
            ,
            textStyle = LocalTextStyle.current.copy(fontSize = MyStyleKt.TextSize.default),  
            value = filterKeyWord.value,
            onValueChange = { onValueChange(it) },
            placeholder = { Text(placeholderText) },
            singleLine = singleLine,
            trailingIcon = trailIcon,
        )
        Row(
            modifier = Modifier.fillMaxWidth().height(1.dp),
        ){
            if(loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
    if(requireFocus) {
        LaunchedEffect(Unit) {
            filterKeyWord.apply {
                value = value.copy(text = value.text, selection = TextRange(0, value.text.length))
            }
        }
        Focuser(focusRequester, scope)
    }
}
