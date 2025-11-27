package com.akcreation.gitsilent.utils.compare.search

import com.akcreation.gitsilent.utils.compare.param.CompareParam
import com.akcreation.gitsilent.utils.compare.result.IndexModifyResult
import com.akcreation.gitsilent.utils.compare.result.IndexStringPart
import java.util.*

abstract class Search {
    companion object {
        val INSTANCE = SearchOn()  
        val INSTANCE_BETTER_MATCH_BUT_SLOW = SearchOnm()  
    }
    abstract fun<T:CharSequence> doSearch(add: CompareParam<T>, del: CompareParam<T>, reverse: Boolean): IndexModifyResult
    protected fun getAddListMethod(reverse: Boolean) : (Int, Int, Boolean, LinkedList<IndexStringPart>, IndexStringPart?)-> IndexStringPart? {
        return if(reverse) { start:Int, end:Int, modified:Boolean, list: LinkedList<IndexStringPart>, lastItem: IndexStringPart? ->
            var last = lastItem
            val len = start - end
            if(len>0) {
                if(last?.modified == modified) {
                    last.start = end+1
                }else {
                    val new = IndexStringPart(end+1, start+1, modified)
                    list.addFirst(new)
                    last=new
                }
            }
            last  
        }else { start:Int, end:Int, modified:Boolean, list: LinkedList<IndexStringPart>, lastItem: IndexStringPart? ->
            var last = lastItem
            val len = end-start
            if(len>0) {
                if(last?.modified == modified) {
                    last!!.end = end
                }else {
                    val new = IndexStringPart(start, end, modified)
                    list.add(new)
                    last=new
                }
            }
            last
        }
    }
}
