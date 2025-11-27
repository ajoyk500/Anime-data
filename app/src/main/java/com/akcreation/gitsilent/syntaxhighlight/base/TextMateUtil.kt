package com.akcreation.gitsilent.syntaxhighlight.base

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.StyleReceiver
import io.github.rosemoe.sora.lang.styling.Span
import io.github.rosemoe.sora.lang.styling.TextStyle
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.util.RendererUtils
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import org.eclipse.tm4e.core.registry.IThemeSource

private const val TAG = "TextMateInit"
object TextMateUtil {
    private var inited = false
    var colorScheme: EditorColorScheme = EditorColorScheme()
        private set
    fun doInit(appContext: Context) {
        if(inited) {
            return
        }
        inited = true
        try {
            setupTextmate(appContext)
            colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
                .apply {
                    applyDefault()
                }
        }catch (e: Exception) {
            inited = false
            MyLog.e(TAG, "$TAG#doInit err: ${e.stackTraceToString()}")
        }
    }
    fun updateTheme(inDarkTheme:Boolean) {
        PLTheme.setTheme(inDarkTheme)
    }
    private fun setupTextmate(appContext: Context) {
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(
                appContext.assets 
            )
        )
        loadDefaultTextMateThemes()
        loadDefaultTextMateLanguages()
    }
     fun loadDefaultTextMateThemes()  {
        val themes = PLTheme.THEMES
        val themeRegistry = ThemeRegistry.getInstance()
        themes.forEach { name ->
            val path = "textmate/$name.json"
            themeRegistry.loadTheme(
                ThemeModel(
                    IThemeSource.fromInputStream(
                        FileProviderRegistry.getInstance().tryGetInputStream(path), path, null
                    ), name
                ).apply {
                    isDark = name != PLTheme.THEME_LIGHT
                }
            )
        }
    }
    private  fun loadDefaultTextMateLanguages()  {
        GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")
    }
    fun setReceiverThenDoAct(
        language: Language?,
        receiver: StyleReceiver,
        act: (StyleReceiver)->Unit,
    ) {
        language?.analyzeManager?.setReceiver(receiver)
        act(receiver)
    }
    fun cleanLanguage(language: Language?) {
        if (language != null) {
            val formatter = language.getFormatter()
            formatter.setReceiver(null)
            formatter.destroy()
            language.getAnalyzeManager().setReceiver(null)
            language.getAnalyzeManager().destroy()
            language.destroy()
        }
    }
    fun forEachSpanResult(rawText: String, spans:List<Span>, foreach:(start: Int, end: Int, spanStyle: SpanStyle) -> Unit) {
        var start = 0
        var spanIdx = 1
        while (spanIdx <= spans.size && start < rawText.length) {
            val curSpan = spans.get(spanIdx - 1)
            val nextSpan = spans.getOrNull(spanIdx++)
            var endExclusive = nextSpan?.column ?: rawText.length
            if(start < 0) { 
                MyLog.i(TAG, "should never happened, plz check the code: invalid `start` index when apply syntax highlight styles: start=$start, endExclusive=$endExclusive, rawText.length=${rawText.length}")
                start = 0
            }
            if(endExclusive > rawText.length) { 
                if(AppModel.devModeOn) {
                    MyLog.d(TAG, "invalid `end` index when apply syntax highlight styles: start=$start, endExclusive=$endExclusive, rawText.length=${rawText.length}")
                }
                endExclusive = rawText.length
            }
            if(start >= endExclusive) {
                MyLog.i(TAG, "should never happened, plz check the code: empty range when apply syntax highlight styles: start=$start, endExclusive=$endExclusive, rawText.length=${rawText.length}")
                start = endExclusive
                continue
            }
            val style = curSpan.style
            val foregroundColor = Color(RendererUtils.getForegroundColor(curSpan, colorScheme))
            val fontWeight = if(TextStyle.isBold(style)) FontWeight.Bold else null
            val fontStyle = if(TextStyle.isItalics(style)) FontStyle.Italic else null
            foreach(
                start,
                endExclusive,
                SpanStyle(color = foregroundColor, fontStyle = fontStyle, fontWeight = fontWeight)
            )
            start = endExclusive
        }
        if(start < rawText.length) {
            foreach(
                start,
                rawText.length,
                MyStyleKt.emptySpanStyle
            )
        }
    }
}
