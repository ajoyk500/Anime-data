package com.akcreation.gitsilent.constants

import com.akcreation.gitsilent.git.PuppyLine
import com.github.git24j.core.Diff

object LineNum {
    const val invalidButNotEof = -998
    const val max = Int.MAX_VALUE
    const val lastPosition = -2
    object EOF {
        const val LINE_NUM = -1
        const val TEXT = "EOF"
        fun transLineToEofLine(line: PuppyLine, add:Boolean): PuppyLine {
            return line.copy(
                lineNum = LINE_NUM,
                originType = if(add) Diff.Line.OriginType.ADDITION.toString() else Diff.Line.OriginType.DELETION.toString(),
                content = Cons.lineBreak,
                contentLen = 1
            )
        }
    }
    fun shouldRestoreLastPosition(lineNumWillCheck:Int) :Boolean {
        return lineNumWillCheck == lastPosition || (lineNumWillCheck != EOF.LINE_NUM && lineNumWillCheck <= 0)
    }
}
