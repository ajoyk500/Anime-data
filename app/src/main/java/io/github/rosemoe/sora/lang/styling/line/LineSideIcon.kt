
package io.github.rosemoe.sora.lang.styling.line

import android.graphics.drawable.Drawable

data class LineSideIcon(override var line: Int, val drawable: Drawable) : LineAnchorStyle(line)