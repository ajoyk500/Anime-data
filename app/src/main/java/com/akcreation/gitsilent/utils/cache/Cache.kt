package com.akcreation.gitsilent.utils.cache

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import com.akcreation.gitsilent.utils.getShortUUID
import com.akcreation.gitsilent.utils.state.Saver.rememberSaveableString
import kotlinx.coroutines.sync.Mutex

object Cache:CacheStoreImpl(){
    const val keySeparator = ":"
    object Key {
        const val filesListStateKeyPrefix = "FilesPageListState"
        const val changeListInnerPage_requireDoActFromParent = "changeListInnerPage_requireDoActFromParent";
        const val repoTmpStatusPrefix = "repo_tmp_status"  
        const val editorPageSaveLockPrefix = "editor_save_lock"
        const val subPagesStateKeyPrefix = "sub_page_state"
        val diffableList_of_fromDiffScreenBackToWorkTreeChangeList = "diffableList_of_fromDiffScreenBackToWorkTreeChangeList"
    }
    private fun getKeyOfSaveLock(filePath:String):String {
        return Key.editorPageSaveLockPrefix + keySeparator + filePath
    }
    fun getSaveLockOfFile(filePath:String):Mutex {
        return getOrPutByType(getKeyOfSaveLock(filePath), default = { Mutex() })
    }
    fun clearFileSaveLock() {
        clearByKeyPrefix(Key.editorPageSaveLockPrefix + keySeparator)
    }
    private fun getFilesListStateKey(path:String):String {
        return Key.filesListStateKeyPrefix+keySeparator+path
    }
    fun clearFilesListStates() {
        clearByKeyPrefix(Key.filesListStateKeyPrefix+keySeparator)
    }
    fun getFilesListStateByPath(path:String): LazyListState {
        val key = getFilesListStateKey(path)
        val restoreListState = Cache.getByType<LazyListState>(key)
        return if(restoreListState == null) {
            val newListState = LazyListState(0,0)
            Cache.set(key, newListState)
            newListState
        }else{
            restoreListState
        }
    }
    fun clearAllSubPagesStates() {
        clearByKeyPrefix(Key.subPagesStateKeyPrefix+keySeparator)
    }
    @Composable
    fun getSubPageKey(stateKeyTag:String):String {
        return rememberSaveableString {
            StringBuilder(Key.subPagesStateKeyPrefix)
                .append(keySeparator)
                .append(stateKeyTag)
                .append(keySeparator)
                .append(getShortUUID())
                .toString()
        }
    }
    @Composable
    fun getComponentKey(parentKey:String, stateKeyTag:String): String {
        return rememberSaveableString {
            StringBuilder(parentKey)
                .append(keySeparator)
                .append(stateKeyTag)
                .append(keySeparator)
                .append(getShortUUID())
                .toString()
        }
    }
    fun combineKeys(vararg keys: String):String {
        val ret = StringBuilder()
        for (key in keys) {
            ret.append(key).append(keySeparator)
        }
        return ret.removeSuffix(keySeparator).toString()
    }
}
