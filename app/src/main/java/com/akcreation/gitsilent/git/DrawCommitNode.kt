package com.akcreation.gitsilent.git

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import com.akcreation.gitsilent.dto.Box
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.forEachBetter
import io.ktor.util.collections.ConcurrentMap

private const val alpha = 80;
private const val alphaFloat = alpha / 255f;
private val unPushedColorLight = Color.Gray.copy(alpha = alphaFloat)
private val unPushedColorDark = Color.LightGray.copy(alpha = alphaFloat)
private val cachedColors = ConcurrentMap<Int, Color>().apply {
    put(0, Color(red = 0xF, green = 0xA2, blue = 0x72, alpha = alpha))
    put(1, Color(red = 0xE0, green = 0x62, blue = 0x56, alpha = alpha))
    put(2, Color(red = 0xB6, green = 0x9F, blue = 0xB, alpha = alpha))
    put(3, Color(red = 0xB6, green = 0xB, blue = 0x69, alpha = alpha))
    put(4, Color(red = 0xB6, green = 0x55, blue = 0xB, alpha = alpha))
    put(5, Color(red = 0x5C, green = 0x18, blue = 0x8C, alpha = alpha))
    put(6, Color(red = 0xB, green = 0xB0, blue = 0xB6, alpha = alpha))
    put(7, Color(red = 0xA7, green = 0x3B, blue = 0xAB, alpha = alpha))
    put(8, Color(red = 0x66, green = 0xA, blue = 0x88, alpha = alpha))
    put(9, Color(red = 0x1C, green = 0x4C, blue = 0xC7, alpha = alpha))
    put(10, Color(red = 0x25, green = 0xB6, blue = 0xB, alpha = alpha))
    put(11, Color(red = 0xB6, green = 0x91, blue = 0xB, alpha = alpha))
    put(12, Color(red = 0xB6, green = 0xB, blue = 0x91, alpha = alpha))
    put(13, Color(red = 0xB, green = 0xB6, blue = 0x9C, alpha = alpha))
    put(14, Color(red = 0x9C, green = 0xB6, blue = 0xB, alpha = alpha))
    put(15, Color(red = 0xB8, green = 0x25, blue = 0xC2, alpha = alpha))
    put(16, Color(red = 0xB, green = 0x9C, blue = 0xB6, alpha = alpha))
    put(17, Color(red = 0x5B, green = 0xB, blue = 0xB6, alpha = alpha))
    put(18, Color(red = 0xB6, green = 0xB, blue = 0x4F, alpha = alpha))
    put(19, Color(red = 0xB, green = 0xB6, blue = 0xB0, alpha = alpha))
    put(20, Color(red = 0xB6, green = 0x4F, blue = 0xB, alpha = alpha))
}
@Stable
@Immutable
data class DrawCommitNode (
    val outputIsEmpty:Boolean,
    val inputIsEmpty:Boolean,
    val circleAtHere:Boolean,
    val endAtHere:Boolean,
    val startAtHere:Boolean,
    val mergedList:List<DrawCommitNode>,
    val fromCommitHash:String,
    val toCommitHash:String,
) {
    fun toStringForView():String {
        return "from: $fromCommitHash\nto: $toCommitHash\ncircleAtHere: $circleAtHere\nstartAtHere: $startAtHere\nendAtHere: $endAtHere\ninputIsEmpty: $inputIsEmpty\noutputIsEmpty: $outputIsEmpty\n\n"+(
                if(mergedList.isEmpty()) "" else {
                    val sb = StringBuilder()
                    sb.append("\nConfluences:\n")
                    mergedList.forEachBetter {
                        sb.append(it.toStringForView())
                    }
                    sb.toString()
                }
        )
    }
    companion object {
        val colorBlendMode = BlendMode.SrcAtop
        fun localAheadUpstreamColor(inDarkTheme:Boolean = Theme.inDarkTheme) = if(inDarkTheme) unPushedColorDark else unPushedColorLight;
        fun getAnInsertableIndex(list:List<DrawCommitNode>, toCommitHash: String):InsertablePosition {
            var index = -1
            var afterTheCircle = false
            var isMergedToPrevious = false
            for((idx, node) in list.withIndex()) {
                if(afterTheCircle.not()) {
                    afterTheCircle = node.circleAtHere
                    continue
                }
                if(node.outputIsEmpty
                    || (node.toCommitHash == toCommitHash).let { isMergedToPrevious = it; it }
                ) {
                    index = idx
                    break
                }
            }
            return InsertablePosition(
                isMergedToPrevious,
                index
            )
        }
        fun getNodeColorByIndex(i: Int): Color {
            return cachedColors.get(i) ?: UIHelper.getRandomColorForDrawNode(alpha = alpha).let {
                cachedColors.set(i, it)
                it
            }
        }
        fun transOutputNodesToInputs(node: DrawCommitNode, currentCommitOidStr:String, idx:Int, circleAt:Box<Int>): DrawCommitNode {
            return if(node.outputIsEmpty) {  
                node.copy(inputIsEmpty = true, startAtHere = false)
            }else if(node.toCommitHash == currentCommitOidStr) {  
                if(circleAt.value == -1) {  
                    circleAt.value = idx
                    node.copy(circleAtHere = true, endAtHere = false, outputIsEmpty = false, startAtHere = false)
                }else {  
                    node.copy(circleAtHere = false, endAtHere = true, outputIsEmpty = true, startAtHere = false)
                }
            }else {  
                node.copy(circleAtHere = false, endAtHere = false, outputIsEmpty = false, startAtHere = false)
            }
        }
    }
}
class InsertablePosition(
    val isMergedToPrevious:Boolean,
    val index:Int,
)
