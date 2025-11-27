package com.akcreation.gitsilent.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.ui.theme.Theme

@Composable
fun LoadingDialog(text:String = stringResource(R.string.loading)) {
    val inDarkTheme = Theme.inDarkTheme
    Dialog(
        onDismissRequest = { },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight(.2f)
                .fillMaxWidth(.5f)
                .background(
                    color = if(inDarkTheme) Color.DarkGray else Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = text, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, softWrap = true)
            }
        }
    }
}
