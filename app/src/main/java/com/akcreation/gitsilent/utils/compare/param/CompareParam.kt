package com.akcreation.gitsilent.utils.compare.param


abstract class CompareParam<T: CharSequence>(
    protected val chars:T,
    protected val length:Int,
) {
    fun getLen(): Int {
        return length
    }
    fun getChar(index:Int): Char {
        if(index < 0 || index >= length) {
            throw IndexOutOfBoundsException("index=$index, range is [0, $length)")
        }
        return chars[index]
    }
    fun isEmpty(): Boolean {
        return length < 1
    }
    fun isOnlyLineSeparator(): Boolean {
        return getLen()==1 && getChar(0)=='\n'
    }
    fun hasEndOfNewLine(): Boolean {
        return length > 0 && getChar(length - 1) == '\n'
    }
    abstract fun getTextNoEndOfNewLine():CompareParam<T>
    fun identical(other:CompareParam<T>):Boolean {
        return this.length == other.length && this.chars === other.chars
    }
}
