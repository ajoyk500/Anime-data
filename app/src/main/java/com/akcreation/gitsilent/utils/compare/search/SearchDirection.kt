package com.akcreation.gitsilent.utils.compare.search


class SearchDirection private constructor(val mode:Int) {
    companion object {
        val FORWARD = SearchDirection(0)  
        val REVERSE = SearchDirection(1)  
        val FORWARD_FIRST = SearchDirection(2)  
        val REVERSE_FIRST = SearchDirection(3)  
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SearchDirection
        return mode == other.mode
    }
    override fun hashCode(): Int {
        return mode
    }
}
