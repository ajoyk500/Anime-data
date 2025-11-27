
package io.github.rosemoe.sora.lang.styling


fun textStyle(
    foreground: Int, background: Int = 0, bold: Boolean = false,
    italic: Boolean = false, strikethrough: Boolean = false, noCompletion: Boolean = false
): Long {
    return TextStyle.makeStyle(foreground, background, bold, italic, strikethrough, noCompletion)
}