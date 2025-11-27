package com.akcreation.gitsilent.syntaxhighlight.codeeditor

import android.content.Context
import android.os.Bundle
import androidx.annotation.WorkerThread
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.akcreation.gitsilent.constants.LineBreak
import com.akcreation.gitsilent.constants.StrCons
import com.akcreation.gitsilent.dto.UndoStack
import com.akcreation.gitsilent.fileeditor.texteditor.state.MyTextFieldState
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.screen.shared.FilePath
import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.syntaxhighlight.base.PLTheme
import com.akcreation.gitsilent.syntaxhighlight.base.TextMateUtil
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.getRandomUUID
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.isLocked
import com.akcreation.gitsilent.utils.noMoreHeapMemThenDoAct
import com.akcreation.gitsilent.utils.state.CustomStateSaveable
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.StyleReceiver
import io.github.rosemoe.sora.lang.styling.Span
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentReference
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.absoluteValue

private const val TAG = "MyCodeEditor"
class MyCodeEditor(
    val editorState: CustomStateSaveable<TextEditorState>,
    val undoStack: CustomStateSaveable<UndoStack>,
    val plScope: MutableState<PLScope>,
    val editorCharset: MutableState<String?>,
) {
    val appContext: Context = AppModel.realAppContext
    val uid = getShortUUID()
    private var file: FuckSafFile = FuckSafFile(appContext, FilePath(""))
    var lineBreak = LineBreak.LF
    internal var latestStyles: StylesResult? = null
    var languageScope: PLScope = PLScope.NONE
        private set
    var myLang: TextMateLanguage? = null
    val highlightMap: MutableMap<String, SyntaxHighlightResult> = ConcurrentMap()
    val stylesMap: MutableMap<String, StylesResult> = ConcurrentMap()
    val annotatedStringCachedMap: MutableMap<String, AnnotatedString> = ConcurrentMap()
    private val stylesRequestLock = Mutex()
    private val analyzeLock = Mutex()
    private val delayAnalyzingTaskLock = Mutex()
    val textEditorStateOnChangeLock = Mutex()
    private fun genNewStyleDelegate(editorState: TextEditorState?) = MyEditorStyleReceiver(this, Theme.inDarkTheme, stylesMap, editorState, languageScope)
    init {
        undoStack.value.codeEditor = this
    }
    @WorkerThread
    fun noMoreMemory() : Boolean {
        return noMoreHeapMemThenDoAct {
            resetAllPlScopes()
            release()
            Msg.requireShowLongDuration(StrCons.syntaxHightDisabledDueToNoMoreMem)
        }
    }
    private fun resetAllPlScopes() {
        resetPlScope()
        languageScope = PLScope.NONE
    }
    fun resetPlScope() {
        plScope.value = PLScope.AUTO
    }
    private fun updatePlScopeIfNeeded(plScope: MutableState<PLScope>, fileName: String) {
        if(plScope.value == PLScope.AUTO) {
            plScope.value = if(SettingsUtil.isEditorSyntaxHighlightEnabled()) {
                PLScope.guessScopeType(fileName)
            }else {
                PLScope.NONE
            }
        }
    }
    fun updatePlScopeThenAnalyze() {
        updatePlScopeIfNeeded(plScope, file.name)
        analyze()
    }
    fun release() {
        cleanOldLanguage()
        highlightMap.clear()
        stylesMap.clear()
        annotatedStringCachedMap.clear()
    }
    fun reset(newFile: FuckSafFile, force: Boolean) {
        if(force.not() && newFile.path.ioPath == file.path.ioPath) {
            return
        }
        editorCharset.value = null
        resetPlScope()
        resetFile(newFile)
        release()
    }
    private fun resetFile(newFile: FuckSafFile) {
        file = newFile
    }
    fun obtainCachedStyles(editorState: TextEditorState): StylesResult? {
        val targetFieldsId = editorState.fieldsId
        val cachedStyles = stylesMap.get(targetFieldsId)
        return if(isGoodStyles(cachedStyles, editorState)) {
            cachedStyles
        }else {
            null
        }
    }
    private fun plScopeStateInvalid() = PLScope.scopeInvalid(plScope.value.scope)
    @OptIn(ExperimentalCoroutinesApi::class)
    fun sendUpdateStylesRequest(stylesUpdateRequest: StylesUpdateRequest, language: Language? = myLang) {
        if(plScopeStateInvalid()) {
            return
        }
        doJobThenOffLoading job@{
            stylesRequestLock.withLock {
                if(plScopeStateInvalid()) {
                    return@job
                }
                if(noMoreMemory()) {
                    return@job
                }
                val targetEditorState = if(stylesUpdateRequest.ignoreThis) {
                    if(AppModel.devModeOn) {
                        MyLog.i(TAG, "ignore styles for fieldsId: ${stylesUpdateRequest.targetEditorState.fieldsId}, the styles maybe update by later calls")
                    }
                    null
                }else {
                    stylesUpdateRequest.targetEditorState
                }
                val receiver = genNewStyleDelegate(targetEditorState)
                try {
                    TextMateUtil.setReceiverThenDoAct(language, receiver, stylesUpdateRequest.act)
                    if(AppModel.devModeOn) {
                        MyLog.i(TAG, "send syntax highlight analyze request for fieldsId: ${targetEditorState?.fieldsId}")
                    }
                }catch (e: Exception) {
                    MyLog.e(TAG, "#sendUpdateStylesRequest() err: call `TextMateUtil.setReceiverThenDoAct()` err: targetFieldsId=${receiver.editorState?.fieldsId}, err=${e.stackTraceToString()}")
                }
            }
        }
    }
    fun getLatestStylesResultIfMatch(editorState: TextEditorState) = if(isGoodStyles(latestStyles, editorState)) latestStyles else null
    fun analyze(
        editorState: TextEditorState? = this.editorState?.value,
        plScope: PLScope = this.plScope.value,
    ) {
        if(editorState == null) {
            return
        }
        if(plScopeStateInvalid()) {
            return
        }
        if(noMoreMemory()) {
            return
        }
        doJobThenOffLoading {
            analyzeLock.withLock {
                doAnalyzeNoLock(editorState, plScope)
            }
        }
    }
    private fun doAnalyzeNoLock(
        editorState: TextEditorState,
        plScope: PLScope,
    ) {
        val scopeChanged = plScope != languageScope
        languageScope = plScope
        if(PLScope.scopeInvalid(plScope.scope)) {
            release()
            return
        }
        if(editorState.fieldsId.isBlank()) {
            return
        }
        if(scopeChanged) {
            release()
        } else {
            val cachedStyles = obtainCachedStyles(editorState)
            if(cachedStyles != null) {
                doJobThenOffLoading {
                    editorState.applySyntaxHighlighting(cachedStyles)
                }
                return
            }
        }
        val text = editorState.getAllText()
        MyLog.i(TAG, "will run full syntax highlighting analyze (at $TAG)")
        PLTheme.updateThemeByAppTheme()
        cleanOldLanguage()
        val autoComplete = false
        val lang = TextMateLanguage.create(plScope.scope, autoComplete)
        myLang = lang
        sendUpdateStylesRequest(
            stylesUpdateRequest = StylesUpdateRequest(
                ignoreThis = false,
                targetEditorState = editorState,
                act = { styleReceiver -> lang.analyzeManager.reset(ContentReference(Content(text)), Bundle(), styleReceiver) }
            ),
            language = lang
        )
    }
    private fun cleanOldLanguage() {
        latestStyles = null
        val old: Language? = myLang
        myLang = null
        TextMateUtil.cleanLanguage(old)
    }
    fun ifSameReturnCachedAnnotatedString(syntaxHighlightId: String, annotatedStringToCheck: AnnotatedString) : AnnotatedString {
        return annotatedStringCachedMap.get(syntaxHighlightId).let {
            if(it == annotatedStringToCheck) {
                it
            }else {
                annotatedStringToCheck.also {
                    annotatedStringCachedMap.put(syntaxHighlightId, it)
                }
            }
        }
    }
    fun putSyntaxHighlight(fieldsId: String, highlights: SyntaxHighlightResult) {
        highlightMap.put(fieldsId, highlights)
    }
    fun obtainSyntaxHighlight(fieldsId: String, textFieldState: MyTextFieldState) : MyTextFieldState {
        val fieldsStyles = highlightMap.get(fieldsId)
        val annotatedString = fieldsStyles?.obtainAnnotatedString(textFieldState.syntaxHighlightId)
        return if(annotatedString == null || annotatedString == textFieldState.value.annotatedString) {
            textFieldState
        }else {
            textFieldState.copy(value = textFieldState.value.copy(annotatedString = annotatedString))
        }
    }
    fun startAnalyzeWhenUserStopInputForAWhile(initState: TextEditorState, delayInSec: Int = 1, checkTimes: Int = 5) {
        if(plScopeStateInvalid()) {
            return
        }
        doJobThenOffLoading task@{
            if(isLocked(delayAnalyzingTaskLock)) {
                return@task
            }
            delayAnalyzingTaskLock.withLock {
                var initState = initState
                var count = 0
                val delayInMillSec = delayInSec * 1000L
                while (count++ < checkTimes) {
                    delay(delayInMillSec)
                    if(plScopeStateInvalid()) {
                        break
                    }
                    if(editorState != null && editorState.value.fieldsId.let { it.isNotBlank() && it == initState.fieldsId }) {
                        analyze(editorState.value)
                        break
                    }else {
                        initState = editorState?.value ?: break
                    }
                }
            }
        }
    }
    fun cleanStylesByFieldsIdList(fieldsIdList: List<String>) {
        for (fieldsId in fieldsIdList) {
            cleanStylesByFieldsId(fieldsId)
        }
    }
    fun cleanStylesByFieldsId(fieldsId: String) {
        stylesMap.remove(fieldsId)
        highlightMap.remove(fieldsId)
    }
    fun releaseAndClearUndoStack() {
        release()
        doJobThenOffLoading {
            undoStack.value.reset("", force = true, cleanUnusedStyles = false)
        }
    }
    suspend fun doActWithLatestEditorState(owner: String, act: suspend (TextEditorState) -> Unit) {
        if(AppModel.devModeOn) {
            MyLog.v(TAG, "owner locked: $owner")
        }
        textEditorStateOnChangeLock.withLock {
            act(editorState.value)
        }
        if(AppModel.devModeOn) {
            MyLog.v(TAG, "owner freed: $owner")
        }
    }
    fun doActWithLatestEditorStateInCoroutine(owner: String, act: suspend (TextEditorState) -> Unit) {
        doJobThenOffLoading {
            doActWithLatestEditorState(owner, act)
        }
    }
    fun generateAnnotatedStringForLine(textFieldState: MyTextFieldState, spans:List<Span>): AnnotatedString {
        val rawText = textFieldState.value.text
        return buildAnnotatedString {
            TextMateUtil.forEachSpanResult(rawText, spans) { start, end, style ->
                withStyle(style) {
                    append(rawText.substring(start, end))
                }
            }
        }
    }
}
data class StylesResult(
    val inDarkTheme: Boolean,
    val styles: Styles,
    val from: StylesResultFrom,
    val uniqueId: String = getRandomUUID(),
    val fieldsId:String,
    val languageScope: PLScope,
    val applied: AtomicBoolean = AtomicBoolean(false)
) {
    fun copyForEditorState(newFieldsId: String) = copy(
        styles = styles.copy(),
        from = StylesResultFrom.TEXT_EDITOR_STATE,
        uniqueId = getRandomUUID(),
        fieldsId = newFieldsId,
        applied = AtomicBoolean(false)
    )
    fun copyWithDeepCopyStyles() = copy(styles = this.styles.copy())
}
enum class StylesResultFrom {
    CODE_EDITOR,
    TEXT_EDITOR_STATE,
}
class SyntaxHighlightResult(
    val inDarkTheme: Boolean,
    val storage: Map<String, AnnotatedString>
) {
    fun obtainAnnotatedString(syntaxHighlightId: String) = if(inDarkTheme == Theme.inDarkTheme) storage.get(syntaxHighlightId) else null;
}
class StylesUpdateRequest(
    val ignoreThis: Boolean,
    val targetEditorState: TextEditorState,
    val act:(StyleReceiver)->Unit,
)
fun MyCodeEditor?.scopeInvalid() = this == null || PLScope.scopeInvalid(languageScope.scope)
fun MyCodeEditor?.scopeMatched(scope: String?) = this != null && scope != null && languageScope.scope == scope && !this.scopeInvalid()
fun MyCodeEditor?.isGoodStyles(stylesResult: StylesResult?, editorState: TextEditorState):Boolean {
    return !(this == null || stylesResult == null || stylesResult.fieldsId != editorState.fieldsId
            || stylesResult.inDarkTheme != Theme.inDarkTheme
            || (stylesResult.styles.spans.lineCount - editorState.fields.size).absoluteValue > 5
            || !this.scopeMatched(stylesResult.languageScope.scope)
    )
}
