package com.akcreation.gitsilent.syntaxhighlight.hunk

import com.akcreation.gitsilent.syntaxhighlight.base.MyStyleReceiver
import io.github.rosemoe.sora.lang.styling.Styles

private const val TAG = "MyHunkStyleReceiver"

class MyHunkStyleReceiver(
    val highlighter: HunkSyntaxHighlighter,
) : MyStyleReceiver(TAG, highlighter.myLang?.analyzeManager) {

    override fun handleStyles(styles: Styles) {
        highlighter.applyStyles(styles)
    }

}
