package com.akcreation.gitsilent.syntaxhighlight.hunk

import android.content.Context
import android.os.Bundle
import androidx.annotation.WorkerThread
import androidx.compose.ui.text.SpanStyle
import com.akcreation.gitsilent.git.PuppyHunkAndLines
import com.akcreation.gitsilent.msg.OneTimeToast
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.syntaxhighlight.base.TextMateUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentReference
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "HunkSyntaxHighlighter"
class HunkSyntaxHighlighter(
    val hunk: PuppyHunkAndLines,
) {
    val appContext: Context = AppModel.realAppContext
    val analyzeLock = Mutex()
    var myLang: TextMateLanguage? = null
    @WorkerThread
    fun noMoreMemory(noMoreMemToaster: OneTimeToast) : Boolean {
        return hunk.diffItemSaver.syntaxDisabledOrNoMoreMem(noMoreMemToaster)
    }
    fun analyze(scope: PLScope, noMoreMemToaster: OneTimeToast) {
        if(noMoreMemory(noMoreMemToaster)) {
            return
        }
        doJobThenOffLoading job@{
            analyzeLock.withLock {
                if(noMoreMemory(noMoreMemToaster)) {
                    return@job
                }
                doAnalyzeNoLock(scope)
            }
        }
    }
    fun release() {
        cleanOldLanguage()
    }
    fun cleanOldLanguage() {
        TextMateUtil.cleanLanguage(myLang)
    }
    private fun doAnalyzeNoLock(scope: PLScope) {
        val text = hunk.linesToString()
        MyLog.w(TAG, "will run full syntax highlighting analyze(at $TAG, filename: ${hunk.diffItemSaver.fileName()})")
        cleanOldLanguage()
        val autoComplete = false
        val lang = TextMateLanguage.create(scope.scope, autoComplete)
        myLang = lang
        try {
            TextMateUtil.setReceiverThenDoAct(lang, MyHunkStyleReceiver(this)) { receiver ->
                lang.analyzeManager.reset(ContentReference(Content(text)), Bundle(), receiver)
            }
        }catch (e: Exception) {
            MyLog.e(TAG, "#doAnalyzeNoLock() err: call `TextMateUtil.setReceiverThenDoAct()` err: fileRelativePath=${hunk.diffItemSaver.relativePathUnderRepo} err=${e.stackTraceToString()}")
        }
    }
    fun applyStyles(styles: Styles) {
        val spansReader = styles.spans.read()
        hunk.diffItemSaver.operateStylesMapWithWriteLock { styleMap ->
            hunk.lines.forEachIndexedBetter { idx, line ->
                val spans = spansReader.getSpansOnLine(idx)
                val lineStyles = mutableListOf<LineStylePart>()
                TextMateUtil.forEachSpanResult(line.getContentNoLineBreak(), spans) { start, end, style ->
                    lineStyles.add(LineStylePart(start, end, style))
                }
                styleMap.put(line.key, lineStyles)
            }
        }
        release()
    }
}
data class LineStylePart(
    val start: Int,
    val end: Int,  
    val style: SpanStyle,
)
