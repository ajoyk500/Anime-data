package com.akcreation.gitsilent.syntaxhighlight.markdown

import android.content.Context
import android.os.Bundle
import androidx.compose.ui.text.AnnotatedString
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.syntaxhighlight.base.PLTheme
import com.akcreation.gitsilent.syntaxhighlight.base.TextMateUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentReference
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "MarkDownSyntaxHighlighter"
class MarkDownSyntaxHighlighter(
    val text: String,
    val onReceive: (AnnotatedString) -> Unit,
) {
    val appContext: Context = AppModel.realAppContext
    val analyzeLock = Mutex()
    var myLang: TextMateLanguage? = null
    val scope = PLScope.MARKDOWN
    fun getTextLines() = text.lines()
    fun analyze() {
        doJobThenOffLoading {
            analyzeLock.withLock {
                doAnalyzeNoLock()
            }
        }
    }
    fun release() {
        cleanOldLanguage()
    }
    fun cleanOldLanguage() {
        TextMateUtil.cleanLanguage(myLang)
    }
    private fun doAnalyzeNoLock() {
        MyLog.w(TAG, "will run full syntax highlighting analyze(at $TAG)")
        cleanOldLanguage()
        PLTheme.updateThemeByAppTheme()
        val autoComplete = false
        val lang = TextMateLanguage.create(scope.scope, autoComplete)
        myLang = lang
        try {
            TextMateUtil.setReceiverThenDoAct(lang, MarkDownStyleReceiver(this)) { receiver ->
                lang.analyzeManager.reset(ContentReference(Content(text)), Bundle(), receiver)
            }
        }catch (e: Exception) {
            MyLog.e(TAG, "#doAnalyzeNoLock() err: call `TextMateUtil.setReceiverThenDoAct()` err: ${e.stackTraceToString()}")
        }
    }
}
