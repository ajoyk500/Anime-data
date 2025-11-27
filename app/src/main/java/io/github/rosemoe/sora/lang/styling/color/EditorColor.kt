
package io.github.rosemoe.sora.lang.styling.color

import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class EditorColor
(private val colorId: Int) : ResolvableColor {
    override fun resolve(colorScheme: EditorColorScheme): Int {
        return colorScheme.getColor(colorId)
    }
}