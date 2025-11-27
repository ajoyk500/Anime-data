
package io.github.rosemoe.sora.lang.styling.color

import android.graphics.Color
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class ConstColor : ResolvableColor {
    private val color: Int
    constructor(color: Int) {
        this.color = color
    }
    constructor(color: String) {
        this.color = Color.parseColor(color)
    }
    override fun resolve(colorScheme: EditorColorScheme): Int {
        return color
    }
}