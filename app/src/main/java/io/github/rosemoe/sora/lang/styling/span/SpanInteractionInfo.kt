
package io.github.rosemoe.sora.lang.styling.span


interface SpanInteractionInfo : SpanExt {
    fun isClickable(): Boolean
    fun isLongClickable(): Boolean
    fun isDoubleClickable(): Boolean
    fun getData(): Any?
}