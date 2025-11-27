
package io.github.rosemoe.sora.lang.styling.line

import io.github.rosemoe.sora.lang.styling.color.ResolvableColor

data class LineBackground(override var line: Int, var color: ResolvableColor) :
    LineAnchorStyle(line)