
@file:JvmName("Comparators")
package io.github.rosemoe.sora.lang.completion

import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.util.CharCode

private fun CharSequence?.asString(): String {
    return if (this == null) " " else if (this is String) this else this.toString()
}
fun defaultComparator(a: CompletionItem, b: CompletionItem): Int {
    val p1Score = (a.extra as? SortedCompletionItem)?.score?.score ?: 0
    val p2Score = (b.extra as? SortedCompletionItem)?.score?.score ?: 0
    if (p1Score < p2Score) {
        return 1;
    } else if (p1Score > p2Score) {
        return -1;
    }
    var p1 = a.sortText.asString()
    var p2 = b.sortText.asString()
    if (p1 < p2) {
        return -1;
    } else if (p1 > p2) {
        return 1;
    }
    p1 = a.label.asString()
    p2 = b.label.asString()
    if (p1 < p2) {
        return -1;
    } else if (p1 > p2) {
        return 1;
    }
    val kind = (b.kind?.value ?: 0) - (a.kind?.value ?: 0)
    return kind
}
fun snippetUpComparator(a: CompletionItem, b: CompletionItem): Int {
    if (a.kind != b.kind) {
        if (a.kind == CompletionItemKind.Snippet) {
            return 1;
        } else if (b.kind == CompletionItemKind.Snippet) {
            return -1;
        }
    }
    return defaultComparator(a, b);
}
fun getCompletionItemComparator(
    source: ContentReference,
    cursorPosition: CharPosition,
    completionItemList: Collection<CompletionItem>
): Comparator<CompletionItem> {
    source.validateAccess()
    val sourceLine = source.reference.getLine(cursorPosition.line)
    var word = ""
    var wordLow = ""
    val scoreFn = FuzzyScorer { pattern,
                                lowPattern,
                                patternPos,
                                wordText,
                                lowWord,
                                wordPos,
                                options ->
        if (sourceLine.length > 2000) {
            fuzzyScore(pattern, lowPattern, patternPos, wordText, lowWord, wordPos, options)
        } else {
            fuzzyScoreGracefulAggressive(
                pattern,
                lowPattern,
                patternPos,
                wordText,
                lowWord,
                wordPos,
                options
            )
        }
    }
    for (originItem in completionItemList) {
        source.validateAccess()
        val overwriteBefore = originItem.prefixLength
        val wordLen = overwriteBefore
        if (word.length != wordLen) {
            word = if (wordLen == 0) "" else sourceLine.substring(
                sourceLine.length - wordLen
            )
            wordLow = word.lowercase()
        }
        val item = SortedCompletionItem(originItem, FuzzyScore.default)
        if (wordLen == 0) {
            item.score = FuzzyScore.default
        } else {
            var wordPos = 0;
            while (wordPos < overwriteBefore) {
                val ch = word[wordPos].code
                if (ch == CharCode.Space || ch == CharCode.Tab) {
                    wordPos += 1;
                } else {
                    break;
                }
            }
            if (wordPos >= wordLen) {
                item.score = FuzzyScore.default;
            } else if (originItem.sortText?.isNotEmpty() == true) {
                val match = scoreFn.calculateScore(
                    word,
                    wordLow,
                    wordPos,
                    originItem.sortText.asString(),
                    originItem.sortText.asString().lowercase(),
                    0,
                    FuzzyScoreOptions.default
                ) ?: continue; 
                if (originItem.sortText === originItem.label) {
                    item.score = match;
                } else {
                    val labelMatch = scoreFn.calculateScore(
                        word,
                        wordLow,
                        wordPos,
                        originItem.label.asString(),
                        originItem.label.asString().lowercase(),
                        0,
                        FuzzyScoreOptions.default
                    ) ?: continue; 
                    item.score = labelMatch
                    labelMatch.matches[0] = match.matches[0]
                }
            } else {
                val match = scoreFn.calculateScore(
                    word,
                    wordLow,
                    wordPos,
                    originItem.label.asString(),
                    originItem.label.asString().lowercase(),
                    0,
                    FuzzyScoreOptions.default
                ) ?: continue; 
                item.score = match;
            }
            originItem.extra = item
        }
    }
    return Comparator { o1, o2 ->
        snippetUpComparator(o1, o2)
    }
}
data class SortedCompletionItem(
    val completionItem: CompletionItem,
    var score: FuzzyScore
)
