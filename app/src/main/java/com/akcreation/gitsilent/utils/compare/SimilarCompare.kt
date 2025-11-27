package com.akcreation.gitsilent.utils.compare

import com.akcreation.gitsilent.utils.compare.param.CompareParam
import com.akcreation.gitsilent.utils.compare.result.IndexModifyResult
import com.akcreation.gitsilent.utils.compare.search.Search
import com.akcreation.gitsilent.utils.compare.search.SearchDirection

interface SimilarCompare {
    companion object {
        val INSTANCE: SimilarCompare = SimilarCompareImpl()
    }
    fun<T:CharSequence> doCompare(
        add: CompareParam<T>,
        del: CompareParam<T>,
        emptyAsMatched: Boolean = false,
        emptyAsModified: Boolean = true,
        searchDirection: SearchDirection = SearchDirection.FORWARD_FIRST,
        requireBetterMatching: Boolean = false,
        search: Search = Search.INSTANCE,
        betterSearch: Search = Search.INSTANCE_BETTER_MATCH_BUT_SLOW,
        matchByWords:Boolean,
        ignoreEndOfNewLine:Boolean = true,
        degradeToCharMatchingIfMatchByWordFailed:Boolean = false,
        treatNoWordMatchAsNoMatchedWhenMatchByWord:Boolean = false,
    ): IndexModifyResult
}
