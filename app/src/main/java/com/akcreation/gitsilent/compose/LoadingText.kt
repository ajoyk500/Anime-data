package com.akcreation.gitsilent.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.R

@Composable
fun LoadingText(
    contentPadding: PaddingValues,
    horizontalAlignment: Alignment.Horizontal= Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    scrollState: ScrollState? = rememberScrollState(),
    text:(@Composable ()->Unit)? = null,
) {
    LoadingTextBase(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .then(if(scrollState != null) Modifier.verticalScroll(scrollState) else Modifier)
        ,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
    ) {
        text?.invoke()
    }
}
@Composable
fun LoadingTextBase(
    modifier: Modifier,
    horizontalAlignment: Alignment.Horizontal= Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    text:(@Composable ()->Unit)? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
    ) {
        text?.invoke()
    }
}
@Composable
fun LoadingTextSimple(
    text:String= stringResource(R.string.loading),
    contentPadding: PaddingValues,
) {
    LoadingText(
        contentPadding=contentPadding,
        text = {
            Text(text = text)
        }
    )
}
@Composable
fun LoadingTextUnScrollable(
    text:String= stringResource(R.string.loading),
    contentPadding: PaddingValues,
) {
    LoadingText(
        contentPadding=contentPadding,
        scrollState = null,
        text = {
            Text(text = text)
        }
    )
}
@Composable
fun LoadingTextCancellable(
    text: String = stringResource(R.string.loading),
    contentPadding: PaddingValues,
    showCancel: Boolean,
    cancelText:String = stringResource(R.string.cancel),
    onCancel:()->Unit,  
) {
    LoadingText(
        contentPadding = contentPadding,
        text = {
            Text(text)
            if(showCancel) {
                Spacer(Modifier.height(10.dp))
                SingleLineClickableText(cancelText) {
                    onCancel()
                }
            }
        }
    )
}
