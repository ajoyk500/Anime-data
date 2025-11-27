package com.akcreation.gitsilent.utils.compare

import com.akcreation.gitsilent.utils.compare.param.CompareParam
import com.akcreation.gitsilent.utils.compare.result.IndexModifyResult
import com.akcreation.gitsilent.utils.compare.result.IndexStringPart
import com.akcreation.gitsilent.utils.compare.search.Search
import com.akcreation.gitsilent.utils.compare.search.SearchDirection
import com.akcreation.gitsilent.utils.iterator.NoCopyIterator

class SimilarCompareImpl: SimilarCompare {
    override fun<T:CharSequence> doCompare(
        add: CompareParam<T>,
        del: CompareParam<T>,
        emptyAsMatched:Boolean,
        emptyAsModified:Boolean,
        searchDirection: SearchDirection,
        requireBetterMatching: Boolean,
        search: Search,
        betterSearch: Search,
        matchByWords:Boolean,
        ignoreEndOfNewLine:Boolean,
        degradeToCharMatchingIfMatchByWordFailed:Boolean,
        treatNoWordMatchAsNoMatchedWhenMatchByWord:Boolean,
    ): IndexModifyResult {
        if(add.identical(del)) {
            return IndexModifyResult(
                matched = true,
                matchedByReverseSearch = false,
                listOf(IndexStringPart(0, add.getLen(), false)),
                listOf(IndexStringPart(0, del.getLen(), false))
            )
        }
        val addWillUse = if(ignoreEndOfNewLine) {
            add.getTextNoEndOfNewLine()
        }else {
            add
        }
        val delWillUse = if(ignoreEndOfNewLine) {
            del.getTextNoEndOfNewLine()
        }else {
            del
        }
        if((addWillUse.isEmpty() && delWillUse.isEmpty())) {  
            return IndexModifyResult(matched = emptyAsMatched, matchedByReverseSearch = false,
                listOf(IndexStringPart(0, add.getLen(), emptyAsModified)),
                listOf(IndexStringPart(0, del.getLen(), emptyAsModified)))
        }else if(addWillUse.isEmpty() && delWillUse.isEmpty().not()) {
            return IndexModifyResult(matched = emptyAsMatched, matchedByReverseSearch = false,
                listOf(IndexStringPart(0, add.getLen(), emptyAsModified)),
                listOf(IndexStringPart(0, del.getLen(), true)))
        }else if(addWillUse.isEmpty().not() && delWillUse.isEmpty()) {
            return IndexModifyResult(matched = emptyAsMatched, matchedByReverseSearch = false,
                listOf(IndexStringPart(0, add.getLen(), true)),
                listOf(IndexStringPart(0, del.getLen(), emptyAsModified)))
        }
        var result:IndexModifyResult? = null
        if(matchByWords) {
            result = doMatchByWords(addWillUse, delWillUse, requireBetterMatching, treatNoWordMatchAsNoMatchedWhenMatchByWord)
        }
        if(result == null || (result.matched.not() && degradeToCharMatchingIfMatchByWordFailed)) {
            val reverse = searchDirection == SearchDirection.REVERSE || searchDirection == SearchDirection.REVERSE_FIRST
            val reverseMatchIfNeed = searchDirection == SearchDirection.REVERSE_FIRST || searchDirection == SearchDirection.FORWARD_FIRST
            result = if(requireBetterMatching) {
                betterSearch.doSearch(addWillUse, delWillUse, reverse)
            }else {
                search.doSearch(addWillUse, delWillUse, reverse)
            }
            if(reverseMatchIfNeed && !result.matched) {
                result = if(requireBetterMatching) betterSearch.doSearch(addWillUse, delWillUse, !reverse) else search.doSearch(addWillUse, delWillUse, !reverse)
            }
        }
        if(ignoreEndOfNewLine) {
            if (add.hasEndOfNewLine()) {
                val addList = result.add as MutableList
                addList.add(
                    IndexStringPart(
                        start = add.getLen()-1,
                        end = add.getLen(),
                        modified = del.hasEndOfNewLine().not()
                    )
                )
            }
            if (del.hasEndOfNewLine()) {
                val delList = result.del as MutableList
                delList.add(
                    IndexStringPart(
                        start = del.getLen()-1,
                        end = del.getLen(),
                        modified = add.hasEndOfNewLine().not()
                    )
                )
            }
        }
        return result
    }
    private fun <T:CharSequence> doMatchByWords(
        add: CompareParam<T>,
        del: CompareParam<T>,
        requireBetterMatching: Boolean,
        treatNoWordMatchAsNoMatched:Boolean,
    ):IndexModifyResult {
        val isAppendContentMaybe = maybeIsAppendContent(add, del)
        val addWordSpacePair = getWordAndIndexList(add, requireBetterMatching)
        val delWordSpacePair = getWordAndIndexList(del, requireBetterMatching)
        val addIter = NoCopyIterator(srcList = addWordSpacePair.words as MutableList)
        val delIter = NoCopyIterator(srcList = delWordSpacePair.words as MutableList)
        val addIndexResultList = mutableListOf<IndexStringPart>()
        val delIndexResultList = mutableListOf<IndexStringPart>()
        var matched = doEqualsMatchWordsOrSpaces(addIter, delIter, addIndexResultList, delIndexResultList)
        if(treatNoWordMatchAsNoMatched && !matched) {
            return IndexModifyResult(
                matched = false,
                matchedByReverseSearch = false,
                add = addIndexResultList,
                del = delIndexResultList
            )
        }
        val addSpaceIter = NoCopyIterator(srcList = addWordSpacePair.spaces as MutableList, isReversed = !isAppendContentMaybe)
        val delSpaceIter = NoCopyIterator(srcList = delWordSpacePair.spaces as MutableList, isReversed = !isAppendContentMaybe)
        matched = doEqualsMatchWordsOrSpaces(addSpaceIter, delSpaceIter, addIndexResultList, delIndexResultList) || matched
        val wordsNotAllMatched = !(addIter.srcIsEmpty() || delIter.srcIsEmpty())
        val spacesNotAllMatched = !(addSpaceIter.srcIsEmpty() || delSpaceIter.srcIsEmpty())
        if(requireBetterMatching && (wordsNotAllMatched || spacesNotAllMatched)) {
            if(wordsNotAllMatched) {
                matched = doIndexOfMatchWordsOrSpaces(addIter, delIter, addIndexResultList, delIndexResultList) || matched
            }
            if(spacesNotAllMatched) {
                matched = doIndexOfMatchWordsOrSpaces(addSpaceIter, delSpaceIter, addIndexResultList, delIndexResultList) || matched
            }
        }
        addAllToIndexResultList(addIter, delIter, addIndexResultList, delIndexResultList)
        addAllToIndexResultList(addSpaceIter, delSpaceIter, addIndexResultList, delIndexResultList)
        addIndexResultList.sortWith(comparator)
        delIndexResultList.sortWith(comparator)
        return IndexModifyResult(
            matched = matched,
            matchedByReverseSearch = false,
            add = addIndexResultList,
            del = delIndexResultList
        )
    }
    private fun <T:CharSequence> maybeIsAppendContent(
        add: CompareParam<T>,
        del: CompareParam<T>,
        expectedAppendCount: Int = 4,
    ): Boolean {
        if(expectedAppendCount < 1) {
            return true
        }
        if(add.getLen() < expectedAppendCount
            || del.getLen() < expectedAppendCount
        ) {
            return false
        }
        val luckyOffset = -2
        var addIndex = add.getLen() - 1 + luckyOffset
        val delIndex = del.getLen() - 1 + luckyOffset
        if(addIndex < 0 || addIndex >= add.getLen() || delIndex < 0 || delIndex >= del.getLen()) {
            return false
        }
        val lastCharOfDel = del.getChar(delIndex)
        var appendCount = 0
        while(appendCount < expectedAppendCount) {
            if(add.getChar(addIndex--) == lastCharOfDel) {
                return false
            }
            appendCount++
            if(addIndex < 0 || addIndex >= add.getLen()) {
                return appendCount >= expectedAppendCount
            }
        }
        return true
    }
    private fun addAllToIndexResultList(
        addIter: NoCopyIterator<WordAndIndex>,
        delIter: NoCopyIterator<WordAndIndex>,
        addIndexResultList: MutableList<IndexStringPart>,
        delIndexResultList: MutableList<IndexStringPart>
    ) {
        addIter.reset()
        while (addIter.hasNext()) {
            val item = addIter.next()
            addIndexResultList.add(
                IndexStringPart(
                    start = item.index,
                    end = item.index + item.getWordStr().length,
                    modified = true
                )
            )
        }
        delIter.reset()
        while (delIter.hasNext()) {
            val item = delIter.next()
            delIndexResultList.add(
                IndexStringPart(
                    start = item.index,
                    end = item.index + item.getWordStr().length,
                    modified = true
                )
            )
        }
    }
    private fun doIndexOfMatchWordsOrSpaces(
        addIter: NoCopyIterator<WordAndIndex>,
        delIter: NoCopyIterator<WordAndIndex>,
        addIndexResultList: MutableList<IndexStringPart>,
        delIndexResultList: MutableList<IndexStringPart>
    ): Boolean {
        var matched = false
        addIter.reset()
        while (addIter.hasNext()) {
            val a = addIter.next()
            val addStr = a.getWordStr()
            delIter.reset()
            while (delIter.hasNext()) {
                val d = delIter.next()
                val delStr = d.getWordStr()
                if (addStr.length > delStr.length) {
                    val indexOf = addStr.indexOf(delStr)
                    if (indexOf != -1) {
                        addIter.remove()
                        delIter.remove()
                        matched = true
                        a.matched = true
                        d.matched = true
                        val aStartIndex = a.index + indexOf
                        val aEndIndex = aStartIndex + delStr.length
                        addIndexResultList.add(
                            IndexStringPart(
                                start = aStartIndex,
                                end = aEndIndex,
                                modified = false
                            )
                        )
                        delIndexResultList.add(
                            IndexStringPart(
                                start = d.index,
                                end = d.index + delStr.length,
                                modified = false
                            )
                        )
                        val beforeMatched = addStr.substring(0, indexOf)
                        val afterMatched = addStr.substring(indexOf + delStr.length)
                        if (beforeMatched.isNotEmpty()) {
                            addIndexResultList.add(
                                IndexStringPart(
                                    start = a.index,
                                    end = a.index + beforeMatched.length,
                                    modified = true
                                )
                            )
                        }
                        if (afterMatched.isNotEmpty()) {
                            addIndexResultList.add(
                                IndexStringPart(
                                    start = aEndIndex,
                                    end = aEndIndex + afterMatched.length,
                                    modified = true
                                )
                            )
                        }
                        break
                    }
                } else {
                    val indexOf = delStr.indexOf(addStr)
                    if (indexOf != -1) {
                        addIter.remove()
                        delIter.remove()
                        matched = true
                        a.matched = true
                        d.matched = true
                        addIndexResultList.add(
                            IndexStringPart(
                                start = a.index,
                                end = a.index + addStr.length,
                                modified = false
                            )
                        )
                        val dStartIndex = d.index + indexOf
                        val dEndIndex = dStartIndex + addStr.length
                        delIndexResultList.add(
                            IndexStringPart(
                                start = dStartIndex,
                                end = dEndIndex,
                                modified = false
                            )
                        )
                        val beforeMatched = delStr.substring(0, indexOf)
                        val afterMatched = delStr.substring(indexOf + addStr.length)
                        if (beforeMatched.isNotEmpty()) {
                            delIndexResultList.add(
                                IndexStringPart(
                                    start = d.index,
                                    end = d.index + beforeMatched.length,
                                    modified = true
                                )
                            )
                        }
                        if (afterMatched.isNotEmpty()) {
                            delIndexResultList.add(
                                IndexStringPart(
                                    start = dEndIndex,
                                    end = dEndIndex + afterMatched.length,
                                    modified = true
                                )
                            )
                        }
                        break
                    }
                }
            }
            if (delIter.srcIsEmpty()) {
                break
            }
        }
        return matched
    }
    private fun doEqualsMatchWordsOrSpaces(
        addIter: NoCopyIterator<WordAndIndex>,
        delIter: NoCopyIterator<WordAndIndex>,
        addIndexResultList: MutableList<IndexStringPart>,
        delIndexResultList: MutableList<IndexStringPart>
    ): Boolean {
        var matched = false
        while (addIter.hasNext()) {
            val addWord = addIter.next()
            val addStr = addWord.getWordStr()
            delIter.reset()
            while (delIter.hasNext()) {
                val delWord = delIter.next()
                val delStr = delWord.getWordStr()
                if (addStr == delStr) {
                    addIter.remove()
                    delIter.remove()
                    matched = true
                    addWord.matched = true
                    delWord.matched = true
                    addIndexResultList.add(
                        IndexStringPart(
                            start = addWord.index,
                            end = addWord.index + addStr.length,
                            modified = false
                        )
                    )
                    delIndexResultList.add(
                        IndexStringPart(
                            start = delWord.index,
                            end = delWord.index + delStr.length,
                            modified = false
                        )
                    )
                    break
                }
            }
            if (delIter.srcIsEmpty()) {
                break
            }
        }
        return matched
    }
    private fun<T:CharSequence> getWordAndIndexList(compareParam:CompareParam<T>, requireBetterMatching: Boolean):WordsSpacesPair {
        val concatNeighborNonWordChars = !requireBetterMatching
        var wordMatching = false
        var spaceMatching = false
        var wordAndIndex:WordAndIndex? = null
        var spaceAndIndex:WordAndIndex? = null
        val wordAndIndexList= mutableListOf<WordAndIndex>()
        val spaceAndIndexList = mutableListOf<WordAndIndex>()
        for(i in 0 until compareParam.getLen()) {
            val char = compareParam.getChar(i)
            if(isWordSeparator(char)) {
                wordMatching = false
                if(concatNeighborNonWordChars && spaceMatching) {  
                    spaceAndIndex!!.word.append(char)
                }else {
                    spaceAndIndex = WordAndIndex(index = i)
                    spaceAndIndex.word.append(char)
                    spaceAndIndexList.add(spaceAndIndex)
                    spaceMatching = true
                }
            }else {  
                spaceMatching = false
                if(wordMatching) {
                    wordAndIndex!!.word.append(char)
                }else {
                    wordAndIndex = WordAndIndex(index = i)
                    wordAndIndex.word.append(char)
                    wordAndIndexList.add(wordAndIndex)
                    wordMatching = true
                }
            }
        }
        return WordsSpacesPair(words = wordAndIndexList, spaces = spaceAndIndexList)
    }
}
private data class WordAndIndex(
    val index:Int=0,
    val word:StringBuilder=StringBuilder(),
    var matched:Boolean=false,
) {
    private var wordStrCached:String? = null
    fun getWordStr():String {
        if(wordStrCached==null) {
            wordStrCached = word.toString()
        }
        return wordStrCached!!
    }
}
private data class WordsSpacesPair(
    val words:List<WordAndIndex>,
    val spaces:List<WordAndIndex>
)
private val comparator = { o1:IndexStringPart, o2:IndexStringPart ->
    o1.start.compareTo(o2.start)
}
private val wordSeparators = listOf(
    ' ',
    '\n',
    '\r',
    '\t',
    ',',
    '.',
    '<',
    '>',
    '/',
    '?',
    '\\',
    '|',
    '!',
    '(',
    ')',
    '{',
    '}',
    '[',
    ']',
    '`',
    '~',
    '@',
    '#',
    '$',
    '%',
    '^',
    '&',
    '*',
    '+',
    '=',
    '\'',
    '\"',
    ';',
    ':',
    '-',
    '_',
    '，',
    '。',
    '、',
    '；',
    '：',
    '“',
    '”',
    '‘',
    '’',
    '《',
    '》',
    '〈',
    '〉',
    '【',
    '】',
    '（',
    '）',
    '—',
    '！',
    '？',
)
private fun isWordSeparator(char:Char):Boolean {
    return char.isWhitespace() || wordSeparators.contains(char)
}
