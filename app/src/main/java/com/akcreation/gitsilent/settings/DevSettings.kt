package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable

@Serializable
data class DevSettings (
    var singleDiffOn: Boolean = false,
    var degradeMatchByWordsToMatchByCharsIfNonMatched: Boolean = false,
    var showMatchedAllAtDiff: Boolean = true,
    var legacyChangeListLoadMethod: Boolean = true,
    var treatNoWordMatchAsNoMatchedForDiff: Boolean = false,
)
