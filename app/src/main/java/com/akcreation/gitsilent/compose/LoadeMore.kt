package com.akcreation.gitsilent.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.UIHelper

@Composable
fun LoadMore(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(30.dp),
    text:String= stringResource(R.string.load_more),
    loadToEndText:String= stringResource(R.string.load_all),
    enableLoadMore:Boolean=true,
    enableAndShowLoadToEnd:Boolean=true,
    loadToEndOnClick:()->Unit={},
    initSetPageSizeDialog:()->Unit,
    btnUpsideText:String?=null, 
    onClick:()->Unit
) {
    val inDarkTheme = Theme.inDarkTheme
    val buttonHeight = 50
    val textLineModifier = Modifier.padding(horizontal = 10.dp)
    Column(modifier= Modifier
        .fillMaxWidth()
        .padding(paddingValues)
        .padding(start = 10.dp, end = 10.dp)
        .then(modifier)
    ) {
        if(btnUpsideText!=null) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text(btnUpsideText, fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic, color = UIHelper.getSecondaryFontColor())
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            MyCard(
                modifier = Modifier
                    .clickable(enabled = enableLoadMore) {  
                        onClick()
                    }
                ,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight.dp)
                    ,
                ) {
                    Row (
                        modifier = textLineModifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = text,
                            color = if(enableLoadMore) MyStyleKt.TextColor.enable else if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable
                        )
                    }
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp),
                    ) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.set_page_size),
                            icon =  Icons.Filled.Settings,
                            iconContentDesc = stringResource(R.string.set_page_size),
                        ) {
                            initSetPageSizeDialog()
                        }
                    }
                }
            }
        }
        if(enableAndShowLoadToEnd) {
            Spacer(modifier = Modifier.height(20.dp))
            MyCard(
                modifier = Modifier
                    .clickable{  
                        loadToEndOnClick()
                    }
                ,
            ) {
                Row(
                    modifier = textLineModifier
                        .fillMaxWidth()
                        .height(buttonHeight.dp)
                    ,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = loadToEndText,
                        color = MyStyleKt.TextColor.enable
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(95.dp))
    }
}
