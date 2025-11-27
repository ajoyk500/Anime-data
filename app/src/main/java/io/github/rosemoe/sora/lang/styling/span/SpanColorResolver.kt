
package io.github.rosemoe.sora.lang.styling.span

import io.github.rosemoe.sora.lang.styling.Span
import io.github.rosemoe.sora.lang.styling.color.ResolvableColor

interface SpanColorResolver : SpanExt {
    fun getForegroundColor(span: Span): ResolvableColor?
    fun getBackgroundColor(span: Span): ResolvableColor?
}