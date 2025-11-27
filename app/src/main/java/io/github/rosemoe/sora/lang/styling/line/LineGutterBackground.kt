
package io.github.rosemoe.sora.lang.styling.line

import io.github.rosemoe.sora.lang.styling.color.ResolvableColor

class LineGutterBackground(override var line: Int, var color: ResolvableColor) :
    LineAnchorStyle(line)