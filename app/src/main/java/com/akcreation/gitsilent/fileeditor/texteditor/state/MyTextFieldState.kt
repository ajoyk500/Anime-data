package com.akcreation.gitsilent.fileeditor.texteditor.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.getRandomUUID

private val lineChangeType_NEW_dark =  Color(0xFF33691E)
private val lineChangeType_NEW =  Color(0xFF8BC34A)
private val lineChangeType_UPDATED_dark =  Color(0xFF0277BD)
private val lineChangeType_UPDATED = Color(0xFF03A9F4)
@Stable
class MyTextFieldState(
    val value: TextFieldValue = TextFieldValue(),
    var changeType:LineChangeType = LineChangeType.NONE,
    val syntaxHighlightId: String = getRandomUUID(),
) {
    fun copy(
        value: TextFieldValue = this.value,
        changeType: LineChangeType = this.changeType,
    ) = MyTextFieldState(
        value = value,
        changeType = changeType,
        syntaxHighlightId = if(value.text != this.value.text || this.syntaxHighlightId.isBlank()) getRandomUUID() else this.syntaxHighlightId,
    )
    override fun toString(): String {
        return "TextFieldState(syntaxHighlightId=$syntaxHighlightId, value=$value, changeType=$changeType)"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MyTextFieldState
        if (syntaxHighlightId != other.syntaxHighlightId) return false
        if (changeType != other.changeType) return false
        if (value != other.value) return false
        return true
    }
    override fun hashCode(): Int {
        var result = syntaxHighlightId.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + changeType.hashCode()
        return result
    }
    fun updateLineChangeTypeIfNone(targetChangeType: LineChangeType) {
        if(changeType == LineChangeType.NONE) {
            changeType = targetChangeType
        }
    }
    fun updateLineChangeType(targetChangeType: LineChangeType) {
        changeType = targetChangeType
    }
    fun getColorOfChangeType(inDarkTheme: Boolean):Color {
        return if(changeType == LineChangeType.NEW) {
            if(inDarkTheme) lineChangeType_NEW_dark else lineChangeType_NEW
        }else if(changeType == LineChangeType.UPDATED) {
            if(inDarkTheme) lineChangeType_UPDATED_dark else lineChangeType_UPDATED
        }else if(changeType == LineChangeType.ACCEPT_OURS) {
            MyStyleKt.ConflictBlock.getAcceptOursIconColor(inDarkTheme)
        }else if(changeType == LineChangeType.ACCEPT_THEIRS) {
            MyStyleKt.ConflictBlock.getAcceptTheirsIconColor(inDarkTheme)
        }else {
            Color.Transparent
        }
    }
}
enum class LineChangeType {
    NONE,
    NEW,
    UPDATED,
    ACCEPT_OURS,
    ACCEPT_THEIRS,
}
