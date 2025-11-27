package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.forEachIndexedBetter

@Composable
fun RemoteCheckBoxList(
    itemList:List<String>,
    selectedList:MutableList<String>,
    checkedList:MutableList<Boolean>,
    enabled:Boolean = true,  
) {
    val getSelectedAllState = {
        if(selectedList.isEmpty()) {
            ToggleableState.Off
        }else if(selectedList.size == itemList.size) {
            ToggleableState.On
        }else {
            ToggleableState.Indeterminate
        }
    }
    val selectAll = rememberSaveable { mutableStateOf(getSelectedAllState())}
    val checkedListState = checkedList.map { rememberSaveable { mutableStateOf(it) }}
    val selectItem = { name:String->
        UIHelper.selectIfNotInSelectedListElseNoop(name, selectedList)
    }
    val removeItem = {name:String->
        selectedList.remove(name)
    }
    val showChildren = rememberSaveable { mutableStateOf(false)}
    Row (
        verticalAlignment = Alignment.CenterVertically,
    ){
        Row (modifier = Modifier.fillMaxWidth(0.8f)){
            MyTriCheckBox(text = stringResource(R.string.all)+" (${selectedList.size}/${itemList.size})", state = selectAll.value, enabled=enabled) {
                if(selectAll.value!=ToggleableState.On) {
                    itemList.forEachBetter { selectItem(it) }
                    checkedListState.forEachIndexedBetter {idx, it -> it.value = true; checkedList[idx]=true }
                    selectAll.value = ToggleableState.On
                }else {
                    selectedList.clear()
                    checkedListState.forEachIndexedBetter {idx, it-> it.value = false; checkedList[idx]=false }
                    selectAll.value = ToggleableState.Off
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            LongPressAbleIconBtn(
                tooltipText = stringResource(R.string.show_hidden_items),
                icon = if (showChildren.value) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                iconContentDesc =stringResource(R.string.show_hidden_items)
            ) {
                showChildren.value = !showChildren.value
            }
        }
    }
    if(showChildren.value) {
        itemList.forEachIndexedBetter {idx, name->
            val v = checkedListState[idx]
            Row(modifier = Modifier.padding(start = 10.dp)) {
                MyCheckBox(text = name, value = v, enabled = enabled) {
                    v.value = it
                    checkedList[idx] = it  
                    if(it) {
                        selectItem(name)
                    }else {
                        removeItem(name)
                    }
                    selectAll.value = getSelectedAllState()
                }
            }
        }
    }
}
