package com.akcreation.gitsilent.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.akcreation.gitsilent.compose.ReadOnlyIcon
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.constants.PageRequest
import com.akcreation.gitsilent.git.DiffableItem
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.functions.defaultTitleDoubleClick
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.UIHelper
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiffScreenTitle(
    isMultiMode:Boolean,
    listState: LazyListState,
    scope: CoroutineScope,
    request:MutableState<String>,
    readOnly:Boolean,
    lastPosition:MutableState<Int>,
    curItem: DiffableItem,
) {
    val fileName = curItem.fileName
    val changeType = curItem.diffItemSaver.changeType
    val relativePath = curItem.relativePath
    if(relativePath.isNotEmpty()) {
        Column(
            modifier = Modifier.combinedClickable(
                            onDoubleClick = {
                                defaultTitleDoubleClick(scope, listState, lastPosition)
                            },
                        ) {
                            request.value = if(isMultiMode) {
                                PageRequest.goToCurItem
                            }else {
                                PageRequest.showDetails
                            }
                        }
                        .widthIn(min=MyStyleKt.Title.clickableTitleMinWidth)
        ) {
                val changeTypeColor = UIHelper.getChangeTypeColor(changeType)
                ScrollableRow {
                    if(readOnly) {
                        ReadOnlyIcon()
                    }
                    Text(
                        text = fileName,
                        fontSize = MyStyleKt.Title.firstLineFontSizeSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,  
                        color = changeTypeColor
                    )
                }
                ScrollableRow  {
                    Text(
                        text = curItem.getAnnotatedAddDeletedAndParentPathString(changeTypeColor),
                        fontSize = MyStyleKt.Title.secondLineFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
    }else {
        Text(
            text = stringResource(id = R.string.diff_screen_default_title),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
