
package io.github.rosemoe.sora.lang.analysis


interface StyleUpdateRange {
    fun isInRange(line: Int): Boolean
    fun lineIndexIterator(maxLineIndex: Int): IntIterator
}