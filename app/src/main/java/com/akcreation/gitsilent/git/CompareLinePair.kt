package com.akcreation.gitsilent.git

import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.compare.CmpUtil
import com.akcreation.gitsilent.utils.compare.param.StringCompareParam
import com.akcreation.gitsilent.utils.compare.result.IndexModifyResult
import com.akcreation.gitsilent.utils.compare.result.IndexStringPart
import com.akcreation.gitsilent.utils.getShortUUID
import com.github.git24j.core.Diff.Line.OriginType

private const val TAG = "CompareLinePair"
private const val invalidLineNum = 0
object CompareLinePairHelper {
    const val clipboardLineNum:Int = -10
    const val clipboardLineOriginType:String="clipboard_origin_type"
    const val clipboardLineKey:String="clipboard_key"
}
data class CompareLinePair (
    var key:String = getShortUUID(),
    var line1Num:Int=invalidLineNum,
    var line1:String?=null,
    var line1OriginType:String="",
    var line2Num:Int=invalidLineNum,
    var line2:String?=null,
    var line2OriginType:String="",
    var line1Key:String="",
    var line2Key:String="",
    var compareResult:IndexModifyResult?=null,
) {
    fun isEmpty():Boolean {
        return line1Num == invalidLineNum
    }
    fun line1ReadyForCompare():Boolean {
        return line1 != null
    }
    fun line2ReadyForCompare():Boolean {
        return line2 != null
    }
    fun readyForCompare():Boolean {
        return  line1ReadyForCompare() && line2ReadyForCompare()
    }
    fun isCompared():Boolean {
        return compareResult != null
    }
    fun compare(betterCompare:Boolean, matchByWords:Boolean, map:MutableMap<String, CompareLinePairResult>) {
        if(readyForCompare().not()) {
            return
        }
        if(isCompared()) {
            return
        }
        if(line1OriginType == OriginType.CONTEXT.toString() &&
            line2OriginType == OriginType.CONTEXT.toString()
        ) {
            MyLog.w(TAG, "compare both Context type lines are nonsense: line1OriginType=$line1OriginType, line2OriginType=$line2OriginType")
            return
        }
        val line1 = line1 ?: ""
        val line2 = line2 ?: ""
        val cmpResult = CmpUtil.compare(
            add = StringCompareParam(line1, line1.length),
            del = StringCompareParam(line2, line2.length),
            requireBetterMatching = betterCompare,
            matchByWords = matchByWords
        )
        if(cmpResult.matched) {
            map.put(line1Key, CompareLinePairResult(cmpResult.add))
            map.put(line2Key, CompareLinePairResult(cmpResult.del))
        }else {
            map.put(line1Key, CompareLinePairResult(null))
            map.put(line2Key, CompareLinePairResult(null))
        }
        compareResult = cmpResult
    }
    fun clear() {
        line1Num=invalidLineNum
        line2Num=invalidLineNum
        line1 = ""
        line2 = ""
        line1OriginType = ""
        line2OriginType = ""
        line1Key=""
        line2Key=""
        compareResult = null
    }
}
data class CompareLinePairResult (
    val stringPartList:List<IndexStringPart>?=null
)
