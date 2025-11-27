package com.akcreation.gitsilent.utils.compare

import com.akcreation.gitsilent.dev.DevFeature
import com.akcreation.gitsilent.dto.Box
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.compare.param.CompareParam
import com.akcreation.gitsilent.utils.compare.result.IndexModifyResult
import com.akcreation.gitsilent.utils.compare.result.IndexStringPart

object CmpUtil {
    private const val TAG = "CmpUtil"
    fun <T: CharSequence> compare(
        add: CompareParam<T>,
        del: CompareParam<T>,
        requireBetterMatching: Boolean,
        matchByWords: Boolean,
        swap: Boolean = false,
        degradeMatchByWordsToMatchByCharsIfNonMatched: Boolean = DevFeature.degradeMatchByWordsToMatchByCharsIfNonMatched.state.value,
        treatNoWordMatchAsNoMatchedWhenMatchByWord: Boolean = DevFeature.treatNoWordMatchAsNoMatchedForDiff.state.value,
    ): IndexModifyResult {
        if(!SettingsUtil.isEnabledDetailsCompareForDiff()) {
            return createEmptyIndexModifyResult(add, del)
        }
        return try {
            val (add, del) = if(swap) {
                Pair(del, add)
            }else {
                Pair(add, del)
            }
            val result = SimilarCompare.INSTANCE.doCompare(
                add = add,
                del = del,
                requireBetterMatching = requireBetterMatching,
                matchByWords = matchByWords,
                degradeToCharMatchingIfMatchByWordFailed = degradeMatchByWordsToMatchByCharsIfNonMatched,
                treatNoWordMatchAsNoMatchedWhenMatchByWord = treatNoWordMatchAsNoMatchedWhenMatchByWord,
            )
            if(swap) {
                result.copy(add = result.del, del = result.add)
            }else {
                result
            }
        }catch (e: Exception) {
            MyLog.e(TAG, "$TAG#compare() err: ${e.localizedMessage}")
            e.printStackTrace()
            createEmptyIndexModifyResult(add, del)
        }
    }
    private fun <T: CharSequence> createEmptyIndexModifyResult(
        add: CompareParam<T>,
        del: CompareParam<T>,
    ) = IndexModifyResult(
        matched = false,
        matchedByReverseSearch = false,
        add = listOf(IndexStringPart(0, add.getLen(), modified = false)),
        del = listOf(IndexStringPart(0, del.getLen(), modified = false)),
    )
    fun roughlyMatch(str1NoLineBreak:String, str2NoLineBreak:String, targetMatchCount:Int): Int {
        if(targetMatchCount < 1) {
            throw RuntimeException("`targetMatchCount` should be greater than 0")
        }
        if(str1NoLineBreak.isEmpty() || str2NoLineBreak.isEmpty()) {
            return 0
        }
        var str1NonBlankStartIndex = getFirstNonBlankIndexOfStr(str1NoLineBreak, false)
        if(str1NonBlankStartIndex == -1){
            return 0
        }
        var str1NonBlankEndIndex = getFirstNonBlankIndexOfStr(str1NoLineBreak, true)
        if(str1NonBlankEndIndex == -1){
            return 0
        }
        var str2NonBlankStartIndex = getFirstNonBlankIndexOfStr(str2NoLineBreak, false)
        if(str2NonBlankStartIndex == -1){
            return 0
        }
        var str2NonBlankEndIndex = getFirstNonBlankIndexOfStr(str2NoLineBreak, true)
        if(str2NonBlankEndIndex == -1){
            return 0
        }
        val containsCount = longerContainsPartOfShorter(str1NoLineBreak, str2NoLineBreak, targetMatchCount, IntRange(str1NonBlankStartIndex, str1NonBlankEndIndex), IntRange(str2NonBlankStartIndex, str2NonBlankEndIndex))
        val startsMatchedCount = matchStartsOrEndsWith(str1NoLineBreak, str2NoLineBreak, false, targetMatchCount, str1NonBlankStartIndex, str2NonBlankStartIndex)
        val endsMatchedCount = matchStartsOrEndsWith(str1NoLineBreak, str2NoLineBreak, true, targetMatchCount, str1NonBlankEndIndex, str2NonBlankEndIndex)
        return containsCount.coerceAtLeast(startsMatchedCount).coerceAtLeast(endsMatchedCount)
    }
    private fun matchStartsOrEndsWith(str1:String, str2:String, reverse: Boolean, targetMatchCount:Int, str1InitIndex:Int, str2InitIndex:Int): Int {
        val str1Index = Box(str1InitIndex)
        val str2Index = Box(str2InitIndex)
        var matchCount = 0
        val condition = if(reverse) {
            { str1Index.value >= 0 && str2Index.value >= 0 }
        }else {
            { str1Index.value < str1.length && str2Index.value < str2.length }
        }
        val updateStr1Index = if(reverse) {
            { str1Index.value-- }
        }else {
            { str1Index.value++ }
        }
        val updateStr2Index = if(reverse) {
            { str2Index.value-- }
        }else {
            { str2Index.value++ }
        }
        while (condition()) {
            if(str1[updateStr1Index()] == str2[updateStr2Index()]) {
                matchCount++
                if(matchCount >= targetMatchCount) {
                    break
                }
            }else {
                break
            }
        }
        return matchCount
    }
    private fun getFirstNonBlankIndexOfStr(str:String, reverse: Boolean):Int {
        val endIndex = str.length-1
        if(endIndex < 0) {
            return -1
        }
        val idxRange = IntRange(0, endIndex).let { if(reverse) it.reversed() else it }
        for(idx in idxRange) {
            if(str[idx].isWhitespace().not()) {
                return idx
            }
        }
        return -1
    }
    private fun longerContainsPartOfShorter(str1: String, str2:String, targetMatchCount:Int, str1NonBlankRange: IntRange, str2NonBlankRange: IntRange): Int {
        var longerRange = str1NonBlankRange
        var shorterRange = str2NonBlankRange
        val (longer, shorter) = if(str1NonBlankRange.let { it.endInclusive - it.start } >= str2NonBlankRange.let { it.endInclusive - it.start }) {
            Pair(str1, str2)
        }else {
            longerRange = str2NonBlankRange
            shorterRange = str1NonBlankRange
            Pair(str2, str1)
        }
        val start = (shorterRange.start + 4).coerceAtMost(shorterRange.endInclusive)
        val end = (shorterRange.endInclusive - 4).coerceAtLeast(shorterRange.start)
        if((end - start) < 0) {
            return 0
        }
        val subShorter = shorter.substring(IntRange(start, end))
        return if(longer.contains(subShorter)) (end - start).coerceAtLeast(1) else 0
    }
}
