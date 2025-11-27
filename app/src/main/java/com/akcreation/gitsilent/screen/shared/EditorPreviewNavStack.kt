package com.akcreation.gitsilent.screen.shared

import androidx.compose.foundation.ScrollState
import com.akcreation.gitsilent.datastruct.Stack
import com.akcreation.gitsilent.screen.functions.newScrollState
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.generateRandomString
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

private const val TAG = "EditorPreviewNavStack"
class EditorPreviewNavStackItem (val path: String = "", val scrollState: ScrollState = newScrollState()) {
    private val uid = generateRandomString()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EditorPreviewNavStackItem
        if (path != other.path) return false
        if (uid != other.uid) return false
        return true
    }
    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + uid.hashCode()
        return result
    }
}
class EditorPreviewNavStack internal constructor(var root:String) {
    private val lock = Mutex()
    var editingPath = root
    var previewingPath = root
    var rootNavStackItem = EditorPreviewNavStackItem(root, newScrollState())
    private val backStack = Stack<EditorPreviewNavStackItem>()
    private val aheadStack = Stack<EditorPreviewNavStackItem>()
    suspend fun reset(newRoot: String) {
        lock.withLock {
            root = newRoot
            editingPath = newRoot
            previewingPath = newRoot
            rootNavStackItem = EditorPreviewNavStackItem(newRoot, newScrollState())
            backStack.clear()
            aheadStack.clear()
        }
    }
    suspend fun push(path:String):Boolean {
        lock.withLock {
            val f = try {
                File(path)
            }catch (e:Exception) {
                MyLog.e(TAG, "push path: create File err! path=$path, err=${e.stackTraceToString()}")
                return false
            }
            try {
                if(f.canRead().not()) {
                    return false
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "push path: can't read file! path=$path, err=${e.stackTraceToString()}")
                return false
            }
            val path = f.canonicalPath
            val current = aheadStack.getFirst()?.path
            if(current != path) { 
                aheadStack.clear()
            }
            aheadStack.push(EditorPreviewNavStackItem(path))
            return true
        }
    }
    suspend fun backToHome() {
        lock.withLock {
            val size = backStack.size()
            if(size > 1) {
                for(i in 0..size-2) {
                    backStack.pop()?.let { aheadStack.push(it) }
                }
            }
        }
    }
    suspend fun ahead():EditorPreviewNavStackItem? {
        lock.withLock {
            val target = aheadStack.pop() ?: return null;
            backStack.push(target)
            return target
        }
    }
    suspend fun back():EditorPreviewNavStackItem? {
        lock.withLock {
            val target = backStack.pop() ?: return null;
            aheadStack.push(target)
            return target
        }
    }
    suspend fun getCurrent():EditorPreviewNavStackItem {
        lock.withLock {
            return getCurrentNoLock()
        }
    }
    private suspend fun getCurrentNoLock():EditorPreviewNavStackItem {
        return backStack.getFirst() ?: rootNavStackItem
    }
    suspend fun getAheadStackFirst():EditorPreviewNavStackItem? {
        lock.withLock {
            return aheadStack.getFirst()
        }
    }
    suspend fun getCurrentScrollState():ScrollState {
        lock.withLock {
            return getCurrentNoLock().scrollState
        }
    }
    suspend fun backStackIsNotEmpty():Boolean {
        lock.withLock {
            return backStack.isNotEmpty()
        }
    }
    suspend fun backStackIsEmpty():Boolean {
        lock.withLock {
            return backStack.isEmpty()
        }
    }
    suspend fun aheadStackIsNotEmpty():Boolean {
        lock.withLock {
            return aheadStack.isNotEmpty()
        }
    }
    suspend fun aheadStackIsEmpty():Boolean {
        lock.withLock {
            return aheadStack.isEmpty()
        }
    }
    suspend fun backStackOrAheadStackIsNotEmpty():Boolean {
        lock.withLock {
            return backStack.isNotEmpty() || aheadStack.isNotEmpty()
        }
    }
    suspend fun currentIsRoot():Boolean {
        lock.withLock {
            return getCurrentNoLock() == rootNavStackItem
        }
    }
}
