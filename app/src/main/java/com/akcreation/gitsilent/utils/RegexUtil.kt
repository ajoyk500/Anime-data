package com.akcreation.gitsilent.utils

import com.akcreation.gitsilent.constants.Cons

object RegexUtil {
    private const val extMatchFlag = "*."
    private const val extFlagLen = extMatchFlag.length
    private const val extFlagLenSubOne = extFlagLen-1  
    private const val slash = Cons.slashChar
    private const val spaceChar = ' '
    fun matchWildcard(target: String, keyword: String, ignoreCase: Boolean = true): Boolean {
        if(target.isEmpty() || keyword.isEmpty()) {
            return false
        }
        if(target == keyword) {
            return true
        }
        val splitKeyword = keyword.split(spaceChar)
        if(splitKeyword.isEmpty()) {
            return false
        }
        var needMatchExt = false
        var extMatched = false
        var validKeyword = false  
        for (k in splitKeyword) {
            if(k.isEmpty()) {
                continue
            }
            validKeyword = true
            if(k.length > extFlagLen && k.startsWith(extMatchFlag, ignoreCase = ignoreCase)) {  
                needMatchExt = true
                if(!extMatched) {  
                    extMatched = target.endsWith(k.substring(extFlagLenSubOne), ignoreCase = ignoreCase)  
                }
            }else {  
                if(target.contains(k, ignoreCase = ignoreCase).not()) {  
                    return false
                }
            }
        }
        return if(needMatchExt) extMatched else validKeyword
    }
    fun matchWildcardList(target: String, keywordList:List<String>, ignoreCase:Boolean = true):Boolean {
        return matchByPredicate(target, keywordList) { target, keyword ->
            matchWildcard(target, keyword, ignoreCase)
        }
    }
    fun matchByPredicate(target: String, keywordList:List<String>, predicate:(target:String, keyword:String)->Boolean):Boolean {
        if(target.isEmpty() || keywordList.isEmpty()) {
            return false
        }
        for (keyword in keywordList) {
            if(predicate(target, keyword)) {
                return true
            }
        }
        return false
    }
    fun equalsOrEndsWithExt(target: String, keywordList: List<String>, ignoreCase: Boolean = true): Boolean {
        for(k in keywordList) {
            if(k.equals(target, ignoreCase = ignoreCase)) {
                return true
            }
            if(k.startsWith(extMatchFlag)) {
                val suffix = k.substring(extMatchFlag.length - 1)
                if(target.endsWith(suffix, ignoreCase = ignoreCase)) {
                    return true
                }
            }
        }
        return false
    }
}
