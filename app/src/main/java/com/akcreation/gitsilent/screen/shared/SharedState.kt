package com.akcreation.gitsilent.screen.shared

import androidx.compose.runtime.mutableStateOf
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.MyCodeEditor
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.dto.Box
import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.utils.MyLog

private const val TAG = "SharedState"
object SharedState {
    const val defaultLoadingValue = true
    val homeScreenNeedRefresh = mutableStateOf("")
    var homeChangeList_itemList = mutableListOf<StatusTypeEntrySaver>()
    var homeChangeList_indexHasItem = mutableStateOf(false)
    val homeChangeList_LastClickedItemKey = mutableStateOf(Cons.init_last_clicked_item_key)
    val index_LastClickedItemKey = mutableStateOf(Cons.init_last_clicked_item_key)
    val treeToTree_LastClickedItemKey = mutableStateOf(Cons.init_last_clicked_item_key)
    val fileHistory_LastClickedItemKey = mutableStateOf(Cons.init_last_clicked_item_key)
    val homeChangeList_Refresh = mutableStateOf("IndexToWorkTree_ChangeList_refresh_init_value_5hpn")
    val indexChangeList_Refresh = mutableStateOf("HeadToIndex_ChangeList_refresh_init_value_ts7n")
    val fileChooser_DirPath = mutableStateOf("")  
    val fileChooser_FilePath = mutableStateOf("")  
    var homeCodeEditor: MyCodeEditor? = null
        private set
    fun updateHomeCodeEditor(newCodeEditor: MyCodeEditor) {
        homeCodeEditor?.let {
            if(it.uid != newCodeEditor.uid) {
                it.release()
            }
        }
        homeCodeEditor = newCodeEditor
    }
}
