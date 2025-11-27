package com.akcreation.gitsilent.dto


data class LineNumParseResult(
    val lineNum:Int = 1,
    val columnNum:Int = 1,
    val isRelative: Boolean = false,
) {
    fun lineNumToIndex(curLineIndex:Int, maxLineIndex:Int):Int {
        val lineNum = if(isRelative) curLineIndex + lineNum else lineNum.dec()
        return lineNum.coerceAtMost(maxLineIndex).coerceAtLeast(0)
    }
    fun columnNumToIndex(): Int {
        return columnNum - 1
    }
}
