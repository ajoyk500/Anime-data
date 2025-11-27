package com.akcreation.gitsilent.constants

import androidx.compose.runtime.MutableState
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog

object PageRequest {
    private const val TAG = "PageRequest"
    const val goToIndex ="goToIndex"  
    const val showLineBreakDialog = "showLineBreakDialog"
    const val convertEncoding = "convertEncoding"
    const val showSelectEncodingDialog = "showSelectEncodingDialog"
    const val showSyntaxHighlightingSelectLanguageDialogForCurItem = "showSyntaxHighlightingSelectLanguageDialogForCurItem"
    const val showSetTabSizeDialog = "showSetTabSizeDialog"
    const val selectSyntaxHighlighting = "selectSyntaxHighlighting"
    const val hideKeyboardForAWhile = "hideKeyboardForAWhile"
    const val reloadRecentFileList = "reloadRecentFileList"
    const val reloadIfChanged = "reloadIfChanged"
    const val editor_RequireRefreshPreviewPage = "editor_RequireRefreshPreviewPage"
    const val goToBottomOfCurrentFile = "goToBottomOfCurrentFile"
    const val goToCurItem = "goToCurItem"
    const val requireOpenInInnerEditor = "requireOpenInInnerEditor"
    const val expandAll = "expandAll"
    const val collapseAll = "collapseAll"
    const val goToStashPage = "goToStashPage"
    const val goToInnerDataStorage = "goToInnerDataStorage"
    const val goToExternalDataStorage = "goToExternalDataStorage"
    const val editorPreviewPageGoBack = "editorPreviewPageGoBack"
    const val editorPreviewPageGoForward = "editorPreviewPageGoForward"
    const val editorPreviewPageGoToTop = "editorPreviewPageGoToTop"
    const val editorPreviewPageGoToBottom = "editorPreviewPageGoToBottom"
    const val requireEditPreviewingFile = "requireEditPreviewingFile"
    const val requireBackToHome = "requireBackToHome"
    const val requireInitPreviewFromSubEditor = "requireInitPreviewFromSubEditor"
    const val requireInitPreview = "requireInitPreview"
    const val safDiff = "safDiff"
    const val safExport = "safExport"
    const val safImport = "safImport"
    const val createPatchForAllItems = "createPatchForAllItems"
    const val indexToWorkTree_CommitAll = "indexToWorkTree_CommitAll"
    const val goToUpstream = "goToUpstream"
    const val editorQuitSelectionMode = "editorQuitSelectionMode"
    const val requestUndo = "requestUndo"
    const val requestRedo = "requestRedo"
    const val requireShowPathDetails = "requireShowPathDetails"
    const val showViewAndSortMenu = "showViewAndSortMenu"
    const val requireGoToFileHistory = "requireGoToFileHistory"
    const val showRestoreDialog = "showRestoreDialog"
    const val showOther = "showOther"
    const val goParent = "goParent"
    const val showInRepos = "showInRepos"
    const val editIgnoreFile = "editIgnoreFile"
    const val goToInternalStorage = "goToInternalStorage"
    const val goToExternalStorage = "goToExternalStorage"
    const val showDetails = "showDetails"
    const val editorSwitchSelectMode = "editorSwitchSelectMode"
    const val requireSaveFontSizeAndQuitAdjust = "requireSaveFontSizeAndQuitAdjust"
    const val requireSaveLineNumFontSizeAndQuitAdjust = "requireSaveLineNumFontSizeAndQuitAdjust"
    const val showInFiles ="showInFiles"
    const val goToPath ="goToPath"
    const val copyFullPath="copyFullPath"
    const val copyRepoRelativePath="copyRepoRelativePath"
    const val cherrypickContinue ="cherrypickContinue"
    const val cherrypickAbort ="cherrypickAbort"
    const val rebaseContinue ="rebaseContinue"
    const val rebaseAbort ="rebaseAbort"
    const val rebaseSkip ="rebaseSkip"
    const val fetch ="fetch"
    const val pull ="pull"
    const val pullRebase ="pullRebase"
    const val push ="push"
    const val pushForce ="pushForce"
    const val sync ="sync"
    const val syncRebase ="syncRebase"
    const val commit ="commit"
    const val mergeAbort ="mergeAbort"
    const val mergeContinue ="mergeContinue"
    const val stageAll ="stageAll"
    const val goToTop ="goToTop"
    const val createFileOrFolder ="createFileOrFolder"
    const val goToLine ="goToLine"
    const val backFromExternalAppAskReloadFile = "backFromExternalAppAskReloadFile"  
    const val needNotReloadFile = "needNotReloadFile"  
    const val requireSave = "requireSave"
    const val requireClose = "requireClose"
    const val requireOpenAs = "requireOpenAs"  
    const val requireSearch = "requireSearch"
    const val findPrevious = "findPrevious"
    const val findNext = "findNext"
    const val showFindNextAndAllCount = "showFindNextAndAllCount"
    const val previousConflict = "previousConflict"
    const val nextConflict = "nextConflict"
    const val showNextConflictAndAllConflictsCount = "showNextConflictAndAllConflictsCount"
    const val doSaveIfNeedThenSwitchReadOnly = "doSaveIfNeedThenSwitchReadOnly"
    const val backLastEditedLine = "backLastEditedLine"  
    const val showOpenAsDialog = "showOpenAsDialog"
    const val switchBetweenFirstLineAndLastEditLine = "switchBetweenFirstLineAndLastEditLine"   
    const val switchBetweenTopAndLastPosition = "switchBetweenTopAndLastPosition"   
    fun clearStateThenDoAct(state:MutableState<String>, act:()->Unit) {
        state.value=""
        try {
            act()
        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "#clearStateThenDoAct err: ${e.stackTraceToString()}")
        }
    }
    fun getRequestThenClearStateThenDoAct(state:MutableState<String>, act:(request:String)->Unit) {
        val request = state.value
        state.value=""
        act(request)
    }
    object DataRequest{
        const val dataSplitBy = "#"
        fun isDataRequest(actually:String, expect:String):Boolean {
            if(actually.isBlank() || expect.isBlank()) {
                return false
            }
            return actually.startsWith(expect)
        }
        fun build(request:String, data:String):String {
            return request+dataSplitBy+data
        }
        fun getDataFromRequest(request:String):String {
            val splitIndex = request.indexOf(dataSplitBy)
            if(splitIndex == -1 || splitIndex == request.lastIndex) {
                return ""
            }else {
                return request.substring(splitIndex+1)
            }
        }
    }
}
