
package io.github.rosemoe.sora.lang.styling.color

import io.github.rosemoe.sora.lang.styling.span.SpanExt
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

fun interface ResolvableColor : SpanExt {
    fun resolve(colorScheme: EditorColorScheme): Int
}