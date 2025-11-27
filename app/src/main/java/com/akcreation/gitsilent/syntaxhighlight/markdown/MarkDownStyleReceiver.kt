package com.akcreation.gitsilent.syntaxhighlight.markdown

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.akcreation.gitsilent.syntaxhighlight.base.MyStyleReceiver
import com.akcreation.gitsilent.syntaxhighlight.base.TextMateUtil
import com.akcreation.gitsilent.utils.forEachIndexedBetter
import io.github.rosemoe.sora.lang.styling.Styles

private const val TAG = "MarkDownStyleReceiver"
class MarkDownStyleReceiver(
    val highlighter: MarkDownSyntaxHighlighter,
) : MyStyleReceiver(TAG, highlighter.myLang?.analyzeManager) {
    override fun handleStyles(styles: Styles) {
        val spansReader = styles.spans.read()
        val lines = highlighter.getTextLines()
        val lastIndex = lines.lastIndex
        val result = buildAnnotatedString {
            lines.forEachIndexedBetter { idx, line ->
                val spans = spansReader.getSpansOnLine(idx)
                TextMateUtil.forEachSpanResult(line, spans) { start, end, style ->
                    withStyle(style) {
                        append(line.substring(start, end))
                    }
                }
                if(idx != lastIndex) {
                    append('\n')
                }
            }
        }
        highlighter.release()
        highlighter.onReceive(result)
    }
}
