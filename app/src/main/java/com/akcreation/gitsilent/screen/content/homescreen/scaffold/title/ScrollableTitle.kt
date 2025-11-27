package com.akcreation.gitsilent.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableTitle(
    text:String,
    listState: ScrollState,
    lastPosition:MutableState<Int>,
) {
    val scope = rememberCoroutineScope()
    ScrollableRow {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.combinedClickable(onDoubleClick = {
                defaultTitleDoubleClick(scope, listState, lastPosition)
            }) {  }
        )
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableTitle(
    text:String,
    listState: LazyListState,
    lastPosition:MutableState<Int>,
) {
    val scope = rememberCoroutineScope()
    ScrollableRow {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.combinedClickable(onDoubleClick = {
                defaultTitleDoubleClick(scope, listState, lastPosition)
            }) {  }
        )
    }
}
