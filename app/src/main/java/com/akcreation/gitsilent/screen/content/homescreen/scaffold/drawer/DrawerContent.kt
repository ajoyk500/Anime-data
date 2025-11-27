package com.akcreation.gitsilent.screen.content.homescreen.scaffold.drawer

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.AppModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun drawerContent(
    currentHomeScreen: MutableIntState,
    scope: CoroutineScope,
    drawerState: DrawerState,
    drawerItemShape: Shape,
    drawTextList: List<String>,
    drawIdList: List<Int>,
    drawIconList: List<ImageVector>,
    drawerItemOnClick:List<()->Unit>,
    showExit:Boolean, 
    filesPageKeepFilterResultOnce:MutableState<Boolean>,
): @Composable() (ColumnScope.() -> Unit) =
    {
        var drawTextList = drawTextList
        var drawIdList = drawIdList
        var drawIconList = drawIconList
        var drawerItemOnClick = drawerItemOnClick
        if(showExit) {
            drawTextList = drawTextList.toMutableList()
            drawIdList = drawIdList.toMutableList()
            drawIconList = drawIconList.toMutableList()
            drawerItemOnClick = drawerItemOnClick.toMutableList()
            (drawTextList as MutableList).add(stringResource(R.string.exit))
            (drawIdList as MutableList).add(Cons.selectedItem_Exit)
            (drawIconList as MutableList).add(Icons.AutoMirrored.Filled.ExitToApp)
            (drawerItemOnClick as MutableList).add(AppModel.exitApp)
        }
        val m = Modifier.padding(5.dp)
        for((index, text) in drawTextList.withIndex()) {
            val id = drawIdList[index]
            NavigationDrawerItem(
                modifier = m,
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
                icon = {
                    Icon(imageVector = drawIconList[index], contentDescription = text)
                },
                shape = drawerItemShape,
                label = { Text(text) },
                selected = id == currentHomeScreen.intValue,
                onClick = {
                    if(Cons.selectedItem_Files.let { id == it && currentHomeScreen.intValue != it }) {
                        filesPageKeepFilterResultOnce.value = true
                    }
                    drawerItemOnClick[index]()
                    currentHomeScreen.intValue = id
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }
            )
        }
    }
