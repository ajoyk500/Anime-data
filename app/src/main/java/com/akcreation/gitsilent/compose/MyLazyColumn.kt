package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.forEachIndexedBetter

@Composable
fun <T> MyLazyColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    list: List<T>,
    listState: LazyListState,
    requireForEachWithIndex: Boolean,
    requirePaddingAtBottom: Boolean,
    requireUseParamModifier:Boolean=false,  
    requireCustomBottom:Boolean=false,
    requireUseCustomLazyListScope:Boolean=false,
    customLazyListScope: LazyListScope.(T) -> Unit={},
    customLazyListScopeWithIndex: LazyListScope.(Int, T) -> Unit={ idx, v->},
    customBottom: @Composable ()->Unit={},
    forEachCb: @Composable (T) -> Unit={},
    forEachIndexedCb: @Composable (Int, T) -> Unit
) {
    if(list.isEmpty()) {  
        Column(modifier =if(requireUseParamModifier) {
                    modifier
                }else {
                    Modifier.baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
                }
            ,
        ) {
        }
    }else {
        val listCopy = list.toList()
        LazyColumn(modifier = if(requireUseParamModifier) {
                        modifier
                    }else {
                        Modifier
                            .fillMaxSize()
                    }
            ,
            contentPadding = contentPadding,
            state = listState
        ){
            if(requireForEachWithIndex) {
                listCopy.forEachIndexedBetter { idx,it->
                    if(requireUseCustomLazyListScope) {
                        customLazyListScopeWithIndex(idx, it)
                    }else {
                        item {
                            forEachIndexedCb(idx, it)
                        }
                    }
                }
            }else {
                listCopy.forEachBetter {
                    if(requireUseCustomLazyListScope) {
                        customLazyListScope(it)
                    }else {
                        item {
                            forEachCb(it)
                        }
                    }
                }
            }
            if(requireCustomBottom) {
                item {
                    customBottom()
                }
            }
            if(requirePaddingAtBottom) {
                item { SpacerRow() }
            }
        }
    }
}
