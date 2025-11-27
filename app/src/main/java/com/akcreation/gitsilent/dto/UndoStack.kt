package com.akcreation.gitsilent.dto

import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.syntaxhighlight.codeeditor.MyCodeEditor
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.getSecFromTime
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedList

private const val TAG = "UndoStack"
private const val defaultSizeLimit = 12
private const val defaultSaveIntervalInSec = 60
class UndoStack(
    var filePath:String,
    var sizeLimit: Int = defaultSizeLimit,
    var undoSaveIntervalInSec:Int = defaultSaveIntervalInSec,
    var undoLastSaveAt: Long = 0L,
    private var undoStack:LinkedList<TextEditorState> = LinkedList(),
    private var redoStack:LinkedList<TextEditorState> = LinkedList(),
    private var undoLock: Mutex = Mutex(),
    private var redoLock: Mutex = Mutex(),
    var codeEditor: MyCodeEditor? = null,
) {
    suspend fun reset(filePath:String, force:Boolean, cleanUnusedStyles: Boolean = true) {
        if(cleanUnusedStyles) {
            this.cleanUnusedStyles()
        }
        if(force.not() && filePath == this.filePath) {
            return
        }
        this.filePath = filePath
        sizeLimit = defaultSizeLimit
        undoSaveIntervalInSec = defaultSaveIntervalInSec
        undoLastSaveAt = 0L
        undoStack = LinkedList()
        redoStack = LinkedList()
        undoLock = Mutex()
        redoLock = Mutex()
    }
    private suspend fun cleanUnusedStyles() {
        codeEditor?.let { codeEditor ->
            if(codeEditor.stylesMap.isNotEmpty()) {
                undoLock.withLock {
                    redoLock.withLock {
                        val latestEditorStateFieldsId = codeEditor.editorState?.value?.fieldsId
                        for ((_, v) in codeEditor.stylesMap) {
                            if(!containsNoLock(v.fieldsId) && v.fieldsId != latestEditorStateFieldsId) {
                                codeEditor.cleanStylesByFieldsId(v.fieldsId)
                            }
                        }
                    }
                }
            }
        }
    }
    fun undoStackIsEmpty():Boolean {
        return undoStack.isEmpty()
    }
    fun redoStackIsEmpty():Boolean {
        return redoStack.isEmpty()
    }
    fun undoStackSize():Int {
        return undoStack.size
    }
    fun redoStackSize():Int {
        return redoStack.size
    }
    suspend fun undoStackPush(state: TextEditorState, force: Boolean = false) {
        undoLock.withLock {
            redoLock.withLock {
                undoStackPushNoLock(state, force)
            }
        }
    }
    private fun undoStackPushNoLock(state: TextEditorState, force: Boolean = false):Boolean {
        val now = getSecFromTime()
        if(force || undoStackIsEmpty() || undoSaveIntervalInSec == 0 || undoLastSaveAt == 0L || (now - undoLastSaveAt) > undoSaveIntervalInSec) {
            push(undoStack, state)
            undoLastSaveAt = now
            if(undoStack.size.let { it > 0 && it > sizeLimit }) {
                val lastHead = undoStack.removeAt(0)
                if(lastHead.fieldsId != state.fieldsId
                    && lastHead.fieldsId != codeEditor?.editorState?.value?.fieldsId
                    && !containsNoLock(lastHead.fieldsId)
                ) {
                    codeEditor?.cleanStylesByFieldsId(lastHead.fieldsId)
                }
            }
            return true
        }
        if(state.fieldsId != codeEditor?.editorState?.value?.fieldsId
            && !containsNoLock(state.fieldsId)
        ) {
            codeEditor?.cleanStylesByFieldsId(state.fieldsId)
        }
        return false
    }
    suspend fun undoStackPop(): TextEditorState? {
        return undoLock.withLock {
            undoStackPopNoLock()
        }
    }
    private fun undoStackPopNoLock(): TextEditorState? {
        return pop(undoStack)
    }
    suspend fun redoStackPush(state: TextEditorState):Boolean {
        redoLock.withLock {
            push(redoStack, state)
            return true
        }
    }
    suspend fun redoStackPop(): TextEditorState? {
        redoLock.withLock {
            undoLock.withLock {
                undoLastSaveAt = 0
            }
            return pop(redoStack)
        }
    }
    suspend fun redoStackClear() {
        redoLock.withLock {
            codeEditor?.let { codeEditor ->
                undoLock.withLock {
                    val latestStateFieldsId = codeEditor.editorState?.value?.fieldsId
                    for (i in redoStack) {
                        if(i.fieldsId != latestStateFieldsId && !undoContainsNoLock(i.fieldsId)) {
                            codeEditor.cleanStylesByFieldsId(i.fieldsId)
                        }
                    }
                }
            }
            redoStack.clear()
        }
    }
    private fun push(stack: MutableList<TextEditorState>, state: TextEditorState) {
        try {
            stack.add(state)
        }catch (e:Exception) {
            MyLog.e(TAG, "#push, err: ${e.stackTraceToString()}")
        }
    }
    private fun pop(stack: MutableList<TextEditorState>): TextEditorState? {
        return try {
            stack.removeAt(stack.size - 1)
        }catch (e:Exception) {
            null
        }
    }
    private fun peek(stack: MutableList<TextEditorState>): TextEditorState? {
        return try {
            stack.get(stack.size - 1)
        }catch (e:Exception) {
            null
        }
    }
    suspend fun updateUndoHeadIfNeed(latestState: TextEditorState) {
        if(undoStack.isEmpty()) {
            return
        }
        undoStackPopThenPush(latestState)
    }
    private suspend fun undoStackPopThenPush(state: TextEditorState) {
        undoLock.withLock {
            undoStackPopNoLock()
            undoStackPushNoLock(state)
        }
    }
    suspend fun contains(fieldsId: String):Boolean {
        return undoLock.withLock {
            redoLock.withLock {
                containsNoLock(fieldsId)
            }
        }
    }
    private fun containsNoLock(fieldsId: String):Boolean {
        return undoContainsNoLock(fieldsId) || redoContainsNoLock(fieldsId)
    }
    private fun undoContainsNoLock(fieldsId: String) : Boolean {
        return undoStack.find { it.fieldsId == fieldsId } != null
    }
    private fun redoContainsNoLock(fieldsId:String) : Boolean {
        return redoStack.find { it.fieldsId == fieldsId } != null
    }
    suspend fun clear() {
        undoLock.withLock {
            undoStack.clear()
        }
        redoLock.withLock {
            redoStack.clear()
        }
    }
    suspend fun clearRedoStackThenPushToUndoStack(state: TextEditorState, force: Boolean) {
        redoStackClear()
        undoStackPush(state, force)
    }
    fun makeSureNextChangeMustSave() {
        undoLastSaveAt = 0
    }
}
