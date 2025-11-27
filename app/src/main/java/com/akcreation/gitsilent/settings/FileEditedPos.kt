package com.akcreation.gitsilent.settings

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

private const val defaultRestoreOffset = 16
@Serializable
data class FileEditedPos (
    var firstVisibleLineIndex:Int=0,
    var lineIndex:Int=0,
    var columnIndex:Int=0,
    var lastUsedTime:Long= 0,
) {
    fun getLineIdxForRestoreView() = if((firstVisibleLineIndex - lineIndex).absoluteValue < defaultRestoreOffset) firstVisibleLineIndex else lineIndex
}
