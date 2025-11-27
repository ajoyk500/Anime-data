
package io.github.rosemoe.sora.lang.styling.span

import android.graphics.Canvas
import android.graphics.Paint
import io.github.rosemoe.sora.annotations.Experimental
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

@Experimental
interface SpanExternalRenderer : SpanExt {
    fun requirePreDraw(): Boolean
    fun requirePostDraw(): Boolean
    fun draw(canvas: Canvas?, paint: Paint?, colorScheme: EditorColorScheme?, preOrPost: Boolean)
}