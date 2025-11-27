package com.akcreation.gitsilent.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.dropDownItemContainerColor
import com.akcreation.gitsilent.utils.isGoodIndexForList

@Composable
fun<T> SingleSelectList(
    outterModifier: Modifier = Modifier,
    dropDownMenuModifier:Modifier = Modifier,
    basePadding: (defaultHorizontalPadding:Dp) -> PaddingValues = { defaultHorizontalPadding -> PaddingValues(horizontal = defaultHorizontalPadding) },
    optionsList:List<T>,   
    selectedOptionIndex:MutableIntState?,
    selectedOptionValue:T? = if(selectedOptionIndex!=null && isGoodIndexForList(selectedOptionIndex.intValue, optionsList)) optionsList[selectedOptionIndex.intValue] else null,
    menuItemFormatter:(index:Int?, value:T?)->String = {index, value-> value?.toString() ?: ""},
    menuItemOnClick:(index:Int, value:T)->Unit = {index, value-> selectedOptionIndex?.intValue = index},
    menuItemSelected:(index:Int, value:T) -> Boolean = {index, value -> selectedOptionIndex?.intValue == index},
    menuItemFormatterLine2:(index:Int?, value:T?)->String = {index, value-> ""},
    menuItemTrailIcon:ImageVector?=null,
    menuItemTrailIconDescription:String?=null,
    menuItemTrailIconEnable:(index:Int, value:T)->Boolean = {index, value-> true},
    menuItemTrailIconOnClick:(index:Int, value:T) ->Unit = {index, value->},
) {
    val expandDropdownMenu = rememberSaveable { mutableStateOf(false) }
    val containerSize = remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    Surface (
        modifier = Modifier
            .padding(basePadding(MyStyleKt.defaultHorizontalPadding))
            .clickable {
                expandDropdownMenu.value = !expandDropdownMenu.value
            }
            .onSizeChanged {
                containerSize.value = it
            }
            .then(outterModifier)
        ,
    ) {
        val trailIconWidth = 20.dp
        Box(
            modifier = Modifier
                .background(UIHelper.defaultCardColor())
                .padding(horizontal = 10.dp)
                .defaultMinSize(minHeight = 50.dp)
                .fillMaxWidth()
            ,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = trailIconWidth)
                    .align(Alignment.CenterStart)
                ,
                verticalArrangement = Arrangement.Center,
            ) {
                val index = selectedOptionIndex?.intValue
                val value = selectedOptionValue
                SelectionRow(Modifier.horizontalScroll(rememberScrollState())) {
                    Text(text = menuItemFormatter(index, value))
                }
                menuItemFormatterLine2(index, value).let {
                    if(it.isNotBlank()) {
                        SelectionRow(Modifier.horizontalScroll(rememberScrollState())) {
                            Text(text = it, fontSize = MyStyleKt.Title.secondLineFontSize, fontWeight = FontWeight.Light)
                        }
                    }
                }
            }
            Row (
                modifier = Modifier
                    .width(trailIconWidth)
                    .align(Alignment.CenterEnd)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(imageVector = if(expandDropdownMenu.value) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft
                    , contentDescription = null
                )
            }
        }
        DropdownMenu(
            modifier = dropDownMenuModifier.width(UIHelper.pxToDpAtLeast0(containerSize.value.width, density)),
            expanded = expandDropdownMenu.value,
            onDismissRequest = { expandDropdownMenu.value=false }
        ) {
            val lastIndex = optionsList.size - 1
            for ((index, value) in optionsList.withIndex()) {
                val selected = menuItemSelected(index, value)
                Column(
                    modifier = Modifier
                        .dropDownItemContainerColor(selected)
                        .fillMaxSize()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DropdownMenuItem(
                            text = {
                                DropDownMenuItemText(
                                    text1 = menuItemFormatter(index, value),
                                    text2 = menuItemFormatterLine2(index, value),
                                )
                            },
                            onClick = {
                                expandDropdownMenu.value=false
                                menuItemOnClick(index, value)
                            },
                            trailingIcon = (
                                    if(menuItemTrailIcon != null) ({
                                        IconButton(
                                            enabled = menuItemTrailIconEnable(index, value),
                                            onClick = {
                                                menuItemTrailIconOnClick(index, value)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = menuItemTrailIcon,
                                                contentDescription = menuItemTrailIconDescription
                                            )
                                        }
                                    })else {
                                        null
                                    }
                            )
                        )
                    }
                }
            }
        }
    }
}
