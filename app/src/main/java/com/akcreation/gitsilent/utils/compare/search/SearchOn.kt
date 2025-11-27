package com.akcreation.gitsilent.utils.compare.search

import com.akcreation.gitsilent.utils.compare.param.CompareParam
import com.akcreation.gitsilent.utils.compare.result.IndexModifyResult
import com.akcreation.gitsilent.utils.compare.result.IndexStringPart
import java.util.*

class SearchOn: Search() {
    override fun<T:CharSequence> doSearch(
        add: CompareParam<T>,
        del: CompareParam<T>,
        reverse: Boolean
    ): IndexModifyResult {
        val addList = LinkedList<IndexStringPart>()
        val delList = LinkedList<IndexStringPart>()
        var hasSameChars = false
        var addCur = if(reverse) add.getLen()-1 else 0
        val addEnd = if(reverse) -1 else add.getLen()
        var delCur = if(reverse) del.getLen()-1 else 0
        val delEnd = if(reverse) -1 else del.getLen()
        var matching = false
        var addStart=addCur
        var delStart=delCur
        var lastAdd: IndexStringPart? = null
        var lastDel: IndexStringPart? = null
        val updateAddIndex = if(reverse){{addCur--}} else {{addCur++}};
        val updateDelIndex = if(reverse){{delCur--}} else {{delCur++}};
        val addToList = getAddListMethod(reverse)
        while (true) {
            if(addCur==addEnd && delCur==delEnd) {  
                addToList(addStart, addEnd, false, addList, lastAdd)
                addToList(delStart, delEnd, false, delList, lastDel)
                break
            }else if(addCur == addEnd) {
                addToList(addStart, addEnd, !matching, addList, lastAdd)
                lastDel = addToList(delStart, delCur, !matching, delList, lastDel)
                addToList(delCur, delEnd, true, delList, lastDel)
                break
            }else if (delCur == delEnd) {  
                addToList(delStart, delEnd, false, delList, lastDel)
                lastAdd = addToList(addStart, addCur, false, addList, lastAdd)
                addToList(addCur, addEnd, true, addList, lastAdd)
                break
            }
            val addCurItem = add.getChar(addCur)
            val delCurItem = del.getChar(delCur)
            if(addCurItem == delCurItem) {
                hasSameChars=true
                if(!matching) {
                    lastAdd = addToList(addStart, addCur, true, addList, lastAdd)
                    addStart = addCur
                    delStart = delCur
                    matching=true
                }
                updateAddIndex()
                updateDelIndex()
            }else {
                if(matching) {
                    lastAdd = addToList(addStart, addCur, false, addList, lastAdd)
                    lastDel = addToList(delStart, delCur, false, delList, lastDel)
                    addStart = addCur
                    delStart = delCur
                    matching = false
                }
                updateAddIndex()  
            }
        }
        return IndexModifyResult(matched = hasSameChars, matchedByReverseSearch = reverse, addList, delList)
    }
}
