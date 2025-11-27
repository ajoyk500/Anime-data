package com.akcreation.gitsilent.syntaxhighlight.codeeditor

import com.akcreation.gitsilent.fileeditor.texteditor.state.FieldsId
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.syntaxhighlight.base.MyStyleReceiver
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import io.github.rosemoe.sora.lang.styling.Styles
import kotlinx.coroutines.runBlocking

private const val TAG = "MyEditorStyleReceiver"
class MyEditorStyleReceiver(
    val codeEditor: MyCodeEditor,
    val inDarkTheme: Boolean,
    val stylesMap: MutableMap<String, StylesResult>,
    val editorState: TextEditorState?,
    val languageScope: PLScope,
): MyStyleReceiver(TAG,  codeEditor.myLang?.analyzeManager) {
    override fun handleStyles(styles: Styles) {
        if(editorState == null || editorState.fieldsId.isBlank()) {
            return
        }
        val latestFieldsId = codeEditor.editorState.value.fieldsId
        val carriedFieldsId = editorState.fieldsId
        if(AppModel.devModeOn) {
            MyLog.i(TAG, "latestFieldsId: $latestFieldsId")
            MyLog.i(TAG, "carriedFieldsId: $carriedFieldsId")
        }
        val isUnusedFieldsId = (latestFieldsId != carriedFieldsId
                && FieldsId.parse(latestFieldsId).timestamp > FieldsId.parse(carriedFieldsId).timestamp
                && runBlocking { codeEditor.undoStack.value.contains(carriedFieldsId).not()}
        )
        if(isUnusedFieldsId) {
            if(AppModel.devModeOn) {
                MyLog.i(TAG, "will drop unused styles for fieldsId: $carriedFieldsId")
            }
            return
        }
        val stylesResult = StylesResult(inDarkTheme, styles, StylesResultFrom.CODE_EDITOR, fieldsId = editorState.fieldsId, languageScope = languageScope)
        if(codeEditor.isGoodStyles(stylesResult, editorState).not()) {
            MyLog.i(TAG, "`stylesResult` doesn't match with editor state: stylesResult=$stylesResult, styles.spans.lineCount=${styles.spans.lineCount}, editorState.fields.size=${editorState.fields.size}")
            return
        }
        codeEditor.latestStyles = stylesResult
        val copiedStyles = stylesResult.copyWithDeepCopyStyles()
        stylesMap.put(editorState.fieldsId, copiedStyles)
        if(AppModel.devModeOn) {
            MyLog.i(TAG, "will apply styles for fieldsId: ${editorState.fieldsId}")
        }
        doJobThenOffLoading {
            editorState.applySyntaxHighlighting(copiedStyles)
        }
    }
}
