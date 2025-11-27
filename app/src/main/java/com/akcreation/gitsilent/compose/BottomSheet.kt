package com.akcreation.gitsilent.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun BottomSheet(
    showBottomSheet: MutableState<Boolean>,
    sheetState: SheetState,
    title: String,
    showCancel:Boolean=true,  
    onCancel:()->Unit={showBottomSheet.value = false},  
    footerPaddingSize:Int=10,
    content:@Composable ()->Unit
) {
        ModalBottomSheet(
            modifier = Modifier.systemBarsPadding()  
            ,
            onDismissRequest = {
                onCancel()
            },
            sheetState = sheetState,
        ) {
            BottomSheetTitle(title)
            FlowRow(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                maxItemsInEachRow = 2,
            ) {
                content()  
                if(showCancel) {
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.cancel),
                    ){
                        onCancel()
                    }
                }
                BottomSheetPaddingFooter(footerPaddingSize)
            }
        }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetItem(
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    text: String,
    textColor:Color = Color.Unspecified,
    textDesc:String="",  
    textDescColor:Color = Color.Gray,  
    enabled:Boolean=true,
    onClick:()->Unit,
) {
    val scope = rememberCoroutineScope()
    val closeBottomSheet = { 
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet.value = false
            }
        }
    }
    val inDarkTheme = Theme.inDarkTheme
    Row(
        modifier = Modifier
            .fillMaxWidth(.5f)  
            .clickable(
                enabled = enabled
            ) {
                closeBottomSheet()
                onClick()
            }
            .height(50.dp)
            .padding(top = 2.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color=if(enabled) textColor else {if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable}
            )
            if(textDesc.isNotBlank()) {
                ScrollableRow {
                    Text(
                        text = "($textDesc)",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color=if(enabled) textDescColor else {if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable}
                    )
                }
            }
        }
    }
}
@Composable
private fun BottomSheetTitle(title:String) {
    MySelectionContainer {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 10.dp)
                .horizontalScroll(rememberScrollState())
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontSize = MyStyleKt.Title.firstLineFontSizeSmall,
                color = Color.Gray,
                )
        }
    }
    MyHorizontalDivider()
}
@Composable
private fun BottomSheetPaddingFooter(paddingSize:Int=30) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSize.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
    }
}
