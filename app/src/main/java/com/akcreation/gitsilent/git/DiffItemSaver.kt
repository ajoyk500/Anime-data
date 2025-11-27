package com.akcreation.gitsilent.git

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.SpanStyle
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.StrCons
import com.akcreation.gitsilent.dto.Box
import com.akcreation.gitsilent.msg.OneTimeToast
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.syntaxhighlight.base.PLScope
import com.akcreation.gitsilent.syntaxhighlight.base.PLTheme
import com.akcreation.gitsilent.syntaxhighlight.hunk.HunkSyntaxHighlighter
import com.akcreation.gitsilent.syntaxhighlight.hunk.LineStylePart
import com.akcreation.gitsilent.utils.compare.CmpUtil
import com.akcreation.gitsilent.utils.compare.param.StringCompareParam
import com.akcreation.gitsilent.utils.compare.result.IndexModifyResult
import com.akcreation.gitsilent.utils.compare.result.IndexStringPart
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.getFileNameFromCanonicalPath
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.noMoreHeapMemThenDoAct
import com.github.git24j.core.Diff
import java.util.EnumSet
import java.util.TreeMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

private const val targetRoughlyMatchedCount = 6
object PuppyLineOriginType{
    const val HUNK_HDR = Diff.Line.OriginType.HUNK_HDR.toString()
    const val ADDITION = Diff.Line.OriginType.ADDITION.toString()
    const val DELETION = Diff.Line.OriginType.DELETION.toString()
    const val CONTEXT = Diff.Line.OriginType.CONTEXT.toString()
    const val CONTEXT_EOFNL = Diff.Line.OriginType.CONTEXT_EOFNL.toString()
    const val ADD_EOFNL = Diff.Line.OriginType.ADD_EOFNL.toString()
    const val DEL_EOFNL = Diff.Line.OriginType.DEL_EOFNL.toString()
    fun isEofLine(line: PuppyLine) = line.originType.let { it == CONTEXT_EOFNL || it == ADD_EOFNL || it == DEL_EOFNL }
}
data class DiffItemSaver (
    var relativePathUnderRepo:String="",  
    var keyForRefresh:String= getShortUUID(),
    var fromTo:String= Cons.gitDiffFromIndexToWorktree,
    var oldFileOid:String="",
    var newFileOid:String="",
    var newFileSize:Long=0L,
    var oldFileSize:Long=0L,
    var isContentSizeOverLimit:Boolean=false,
    var flags: EnumSet<Diff.FlagT> = EnumSet.of(Diff.FlagT.NOT_BINARY),
    var hunks:MutableList<PuppyHunkAndLines> = mutableListOf(),  
    var isFileModified:Boolean=false,
    var addedLines:Int=0,  
    var deletedLines:Int=0,  
    var allLines:Int=0,  
    var maxLineNum:Int=0,
    var hasEofLine:Boolean = false,
    var oldFileType: DiffItemSaverType = DiffItemSaverType.TEXT,
    var newFileType: DiffItemSaverType = DiffItemSaverType.TEXT,
    var oldBlobSavePath:String="",
    var newBlobSavePath:String="",
    var changeType:String = Cons.gitStatusUnmodified,
    private val stylesMapLock: ReentrantReadWriteLock = ReentrantReadWriteLock(),
    private val stylesMap: MutableMap<String, List<LineStylePart>> = mutableStateMapOf(),
    internal val languageScope:Box<PLScope> = if(SettingsUtil.isDiffSyntaxHighlightEnabled()) Box(PLScope.AUTO) else Box(PLScope.NONE)
) {
    fun getAndUpdateScopeIfIsAuto(fileNameForGuessLangScope: String, scope: PLScope = languageScope.value): PLScope {
        val scope = if(scope == PLScope.AUTO) {
            PLScope.guessScopeType(fileNameForGuessLangScope)
        } else {
            scope
        }
        languageScope.value = scope
        return scope
    }
    fun changeScope(newScope: PLScope) : Boolean? {
        if(languageScope.value == newScope) {
            return false
        }
        languageScope.value = newScope
        return if(isLanguageScopeInvalid(newScope)) null else true
    }
    private fun isLanguageScopeInvalid(languageScope: PLScope) : Boolean {
        if(PLScope.scopeTypeInvalid(languageScope)) {
            operateStylesMapWithWriteLock { it.clear() }
            hunks.forEachBetter { it.hunkSyntaxHighlighter.release() }
            return true
        }
        return false
    }
    private var cachedFileName:String? = null
    fun fileName() = cachedFileName ?: getFileNameFromCanonicalPath(relativePathUnderRepo).let { cachedFileName = it; it }
    fun startAnalyzeSyntaxHighlight(noMoreMemToaster: OneTimeToast) {
        if(syntaxDisabledOrNoMoreMem(noMoreMemToaster)) {
            return
        }
        PLTheme.updateThemeByAppTheme()
        val languageScope = getAndUpdateScopeIfIsAuto(fileName())
        if(isLanguageScopeInvalid(languageScope)) {
            return
        }
        for(h in hunks) {
            h.hunkSyntaxHighlighter.analyze(languageScope, noMoreMemToaster)
        }
    }
    fun syntaxDisabledOrNoMoreMem(noMoreMemToaster: OneTimeToast): Boolean {
        if(
            noMoreHeapMemThenDoAct {
                noMoreMemToaster.show(StrCons.syntaxHightDisabledDueToNoMoreMem)
            }
        ) {
            operateStylesMapWithWriteLock { it.clear() }
            return true
        }
        return false
    }
    fun <T> operateStylesMapWithWriteLock(act: (MutableMap<String, List<LineStylePart>>) -> T):T {
        return stylesMapLock.write { act(stylesMap) }
    }
    fun <T> operateStylesMapWithReadLock(act: (MutableMap<String, List<LineStylePart>>) -> T):T {
        return stylesMapLock.read { act(stylesMap) }
    }
    fun getEfficientFileSize():Long {
        return if(newFileSize>0) newFileSize else oldFileSize
    }
    fun generateFakeIndexForGroupedLines() {
        for(h in hunks) {
            var index = -1
            for((_, lines) in h.groupedLines) {
                lines.get(Diff.Line.OriginType.CONTEXT.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.CONTEXT_EOFNL.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.DELETION.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.DEL_EOFNL.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.ADDITION.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.ADD_EOFNL.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
            }
        }
    }
}
class PuppyHunkAndLines(
    val diffItemSaver: DiffItemSaver
) {
    var hunk:PuppyHunk=PuppyHunk();
    var lines:MutableList<PuppyLine> = mutableListOf()
    var addedLinesCount:Int = 0
    var deletedLinesCount:Int = 0
    val keyAndLineMap: MutableMap<String, PuppyLine> = mutableMapOf()
    var groupedLines:TreeMap<Int, Map<String, PuppyLine>> = TreeMap()
    private val modifyResultMap:MutableMap<String, IndexModifyResult> = mutableMapOf()
    private val mergedAddDelLine:MutableSet<Int> = mutableSetOf()
    val hunkSyntaxHighlighter = HunkSyntaxHighlighter(this)
    class MergeAddDelLineResult (
        val needShowAsContext:Boolean,
        val line:PuppyLine?=null,
    )
    fun clearCachesForShown() {
        mergedAddDelLine.clear()
        modifyResultMap.clear()
    }
    private var cachedLinesString:String? = null
    fun linesToString(forceRefreshCache:Boolean = false) : String {
        if(forceRefreshCache.not() && cachedLinesString != null) {
            return cachedLinesString!!
        }
        val sb = StringBuilder()
        for (i in lines) {
            sb.append(i.getContentNoLineBreak()).append('\n')
        }
        return sb.toString().let { cachedLinesString = it; it }
    }
    fun addLine(puppyLine: PuppyLine, changeType:String) {
        lines.add(puppyLine)
        addLineToGroup(puppyLine)
        linkCompareTargetForLine(puppyLine, changeType)
    }
    fun addLineToGroup(puppyLine: PuppyLine) {
        val lineNum = puppyLine.lineNum
        val line = groupedLines.get(lineNum)
        if(line==null) {
            val map = mutableMapOf<String, PuppyLine>()
            map.put(puppyLine.originType, puppyLine)
            groupedLines.put(lineNum, map)
        }else {
            (line as MutableMap).put(puppyLine.originType, puppyLine)
        }
    }
    @Deprecated("this method has better performance but bad matching, recommend use `linkCompareTargetForLine` to instead of")
    fun linkCompareTargetForLineByContextOffset(puppyLine: PuppyLine, changeType:String) {
        if(changeType == Cons.gitStatusModified && puppyLine.originType == PuppyLineOriginType.ADDITION) {
            var foundDel = false
            var size = lines.size
            while (--size >= 0) {
                val ppLine = lines[size]
                if(ppLine.originType == PuppyLineOriginType.CONTEXT) {
                    if(foundDel) {
                        val guessedRelatedLineNum = ppLine.oldLineNum - ppLine.newLineNum + puppyLine.newLineNum
                        val guessedLine = groupedLines.get(guessedRelatedLineNum)?.get(PuppyLineOriginType.DELETION)
                        if(guessedLine != null && guessedLine.compareTargetLineKey.isBlank()) {
                            guessedLine.compareTargetLineKey = puppyLine.key
                            puppyLine.compareTargetLineKey = guessedLine.key
                        }
                    }
                    break
                }else if(ppLine.originType == PuppyLineOriginType.DELETION) {
                    foundDel = true
                }
            }
        }
        keyAndLineMap.put(puppyLine.key, puppyLine)
    }
    fun linkCompareTargetForLine(puppyLine: PuppyLine, changeType:String) {
        if(changeType == Cons.gitStatusModified && deletedLinesCount > 0 && puppyLine.originType == PuppyLineOriginType.ADDITION) {
            var maxMatchedLine: PuppyLine? = null
            var maxRoughMatchCnt = 0
            for(line in lines) {
                if(line.originType == PuppyLineOriginType.DELETION && line.roughlyMatchedCount < targetRoughlyMatchedCount) {
                    val roughMatchCnt = CmpUtil.roughlyMatch(puppyLine.getContentNoLineBreak(), line.getContentNoLineBreak(), targetRoughlyMatchedCount)
                    if((roughMatchCnt > maxRoughMatchCnt && roughMatchCnt > line.roughlyMatchedCount)
                        || (maxMatchedLine == null && line.compareTargetLineKey.isBlank())
                    ) {
                        maxMatchedLine = line
                        maxRoughMatchCnt = roughMatchCnt
                        if(maxRoughMatchCnt >= targetRoughlyMatchedCount) {
                            break
                        }
                    }
                }
            }
            maxMatchedLine?.let { line ->
                val oldCompareTargetLineKey = line.compareTargetLineKey
                if(oldCompareTargetLineKey.isNotBlank()) {
                    keyAndLineMap.get(oldCompareTargetLineKey)?.let {
                        it.compareTargetLineKey = ""
                        it.roughlyMatchedCount = 0
                    }
                }
                line.compareTargetLineKey = puppyLine.key
                puppyLine.compareTargetLineKey = line.key
                line.roughlyMatchedCount = maxRoughMatchCnt
                puppyLine.roughlyMatchedCount = maxRoughMatchCnt
            }
        }
        keyAndLineMap.put(puppyLine.key, puppyLine)
    }
    fun needShowAddOrDelLineAsContext(lineNum: Int):MergeAddDelLineResult {
        val groupedLine = groupedLines.get(lineNum)
        val add = groupedLine?.get(PuppyLineOriginType.ADDITION)
        val del = groupedLine?.get(PuppyLineOriginType.DELETION)
        if(add!=null && del!=null && add.getContentNoLineBreak().equals(del.getContentNoLineBreak())) {
            val alreadyShowed = mergedAddDelLine.add(lineNum).not()
            return MergeAddDelLineResult(
                needShowAsContext = true,
                line = if(alreadyShowed) null else del.copy(originType = Diff.Line.OriginType.CONTEXT.toString()),
            )
        }else {
            return MergeAddDelLineResult(needShowAsContext = false)
        }
    }
    fun getModifyResult(line: PuppyLine, requireBetterMatchingForCompare:Boolean, matchByWords:Boolean):IndexModifyResult? {
        if(line.originType == PuppyLineOriginType.CONTEXT) {
            return null
        }
        val r = modifyResultMap.get(line.key)
        if(r != null) {
            return r
        }
        if(line.compareTargetLineKey.isBlank()) {
            return null
        }
        val cmpTarget = keyAndLineMap.get(line.compareTargetLineKey) ?: return null
        val add = if(line.originType == PuppyLineOriginType.ADDITION) line else cmpTarget
        val del = if(line.originType == PuppyLineOriginType.ADDITION) cmpTarget else line
        val modifyResult2 = CmpUtil.compare(
            add = StringCompareParam(add.getContentNoLineBreak(), add.getContentNoLineBreak().length),
            del = StringCompareParam(del.getContentNoLineBreak(), del.getContentNoLineBreak().length),
            requireBetterMatching = requireBetterMatchingForCompare,
            matchByWords = matchByWords,
        )
        modifyResultMap.put(line.key, modifyResult2)
        modifyResultMap.put(line.compareTargetLineKey, modifyResult2)
        return modifyResult2
    }
    fun clearStyles() {
        diffItemSaver.operateStylesMapWithWriteLock { styleMap ->
            lines.forEachBetter {
                styleMap.remove(it.key)
            }
        }
    }
}
class PuppyHunk {
    var header:String=""
    private var cachedHeader:String? = null
    fun cachedNoLineBreakHeader() = (cachedHeader ?: header.trimEnd().let { cachedHeader = it; it });
}
data class PuppyLine(
    var key:String = getShortUUID(),
    var compareTargetLineKey:String = "",
    var roughlyMatchedCount:Int=0,
    var fakeIndexOfGroupedLine:Int = 0,
    var originType:String="",  
    var oldLineNum:Int=-1,
    var newLineNum:Int=-1,
    var contentLen:Int=0,
    var content:String="",  
    var lineNum:Int=1,  
    var howManyLines:Int=0  
) {
    companion object {
        fun mergeStringAndStylePartList(stringPartList: List<IndexStringPart>, stylePartList: List<LineStylePart>, modifiedBgColorSpanStyle: SpanStyle): List<LineStylePart> {
            val retStylePartList = mutableListOf<LineStylePart>()
            val stringPartMutableList = stringPartList.toMutableList()
            stylePartList.forEachBetter { stylePart ->
                var start = stylePart.start
                val iterator = stringPartMutableList.iterator()
                while (iterator.hasNext()) {
                    val stringPart = iterator.next()
                    val reachedEnd = stringPart.end >= stylePart.end
                    val end = if(reachedEnd) stylePart.end else stringPart.end
                    retStylePartList.add(
                        LineStylePart(
                            start = start,
                            end = end,
                            style = if(stringPart.modified) stylePart.style.merge(modifiedBgColorSpanStyle) else stylePart.style
                        )
                    )
                    start = end
                    iterator.remove()
                    if(reachedEnd) {
                        if(start < stringPart.end) {
                            stringPartMutableList.add(0, IndexStringPart(start, stringPart.end, stringPart.modified))
                        }
                        break
                    }
                }
            }
            val retStyleLastEndIndex = retStylePartList.last().end
            val lastStringPart = stringPartList.last()
            val stringPartLastEndIndex = lastStringPart.end
            if(retStyleLastEndIndex < stringPartLastEndIndex) {
                retStylePartList.add(
                    LineStylePart(
                        start = retStyleLastEndIndex,
                        end = stringPartLastEndIndex,
                        style = if(lastStringPart.modified) modifiedBgColorSpanStyle else MyStyleKt.emptySpanStyle
                    )
                )
            }
            return retStylePartList
        }
    }
    fun isEOF() = PuppyLineOriginType.isEofLine(this);
    private var contentNoBreak:String? = null
    fun getContentNoLineBreak():String {  
        return contentNoBreak ?: (if(!isEOF()) content.removeSuffix("\n").removeSuffix("\r") else "").let { contentNoBreak = it; it }
    }
    fun getAValidLineNum():Int {
        return if(newLineNum < 0) oldLineNum else newLineNum
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PuppyLine
        if (originType != other.originType) return false
        if (oldLineNum != other.oldLineNum) return false
        if (newLineNum != other.newLineNum) return false
        if (contentLen != other.contentLen) return false
        if (content != other.content) return false
        if (lineNum != other.lineNum) return false
        if (howManyLines != other.howManyLines) return false
        return true
    }
    override fun hashCode(): Int {
        var result = originType.hashCode()
        result = 31 * result + oldLineNum
        result = 31 * result + newLineNum
        result = 31 * result + contentLen
        result = 31 * result + content.hashCode()
        result = 31 * result + lineNum
        result = 31 * result + howManyLines
        return result
    }
}
enum class DiffItemSaverType {
    TEXT,
    IMG,
}
