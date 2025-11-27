package com.akcreation.gitsilent.utils.fileopenhistory

import com.akcreation.gitsilent.settings.FileEditedPos
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.JsonUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.getSecFromTime
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.encodeToStream
import java.io.File

object FileOpenHistoryMan {
    const val defaultHistoryMaxCount = 50
    private const val TAG = "FileOpenHistoryMan"
    private var _limit = defaultHistoryMaxCount  
    private const val fileName = "file_open_history.json"
    private lateinit var _file: File
    private lateinit var _saveDir: File
    private var curHistory: FileOpenHistory = FileOpenHistory()
    private val lock = Mutex()
    fun init(saveDir:File, limit:Int, requireClearOldSettingsEditedHistory:Boolean) {
        _limit = limit
        val saveDirPath = saveDir.canonicalPath
        _saveDir = File(saveDirPath)
        _file = File(saveDirPath, fileName)
        readHistoryFromFile()
        if(requireClearOldSettingsEditedHistory) {
            clearOldSettingsHistory()
        }
    }
    private fun getFile():File {
        if(_saveDir.exists().not()) {
            _saveDir.mkdirs()
        }
        if(_file.exists().not()) {
            _file.createNewFile()
        }
        return _file
    }
    private fun readHistoryFromFile() {
        val f = getFile()
        try {
            curHistory = JsonUtil.j.decodeFromString<FileOpenHistory>(f.readText())
        }catch (e:Exception) {
            reset()
            MyLog.e(TAG, "#readHistoryFromFile: read err, file content empty or corrupted, will return a new history, err is: ${e.localizedMessage}")
        }
    }
    fun getHistory():FileOpenHistory {
        return curHistory.copy()
    }
    fun get(path:String):FileEditedPos {
        return getHistory().storage.get(path)?.copy() ?: FileEditedPos()
    }
    fun set(path:String, lastEditedPos: FileEditedPos) {
        updateLastUsedTime(lastEditedPos)
        val h = getHistory()
        h.storage.set(path, lastEditedPos)
        if(h.storage.size > _limit) {
            removeOldHistory(h)
        }
        update(h)
    }
    fun remove(path:String) {
        val h = getHistory()
        h.storage.remove(path)
        update(h)
    }
    fun touch(path:String) {
        set(path, updateLastUsedTime(get(path)))
    }
    private fun updateLastUsedTime(lastEditedPos: FileEditedPos):FileEditedPos {
        lastEditedPos.lastUsedTime = getSecFromTime()
        return lastEditedPos
    }
    @OptIn(ExperimentalSerializationApi::class)
    private fun saveHistory(newHistory:FileOpenHistory) {
        doJobThenOffLoading {
            try {
                curHistory = newHistory
                lock.withLock {
                    JsonUtil.j.encodeToStream(newHistory, getFile().outputStream())
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#saveHistory: save file opened history err: ${e.localizedMessage}")
            }
        }
    }
    private fun removeOldHistory(history: FileOpenHistory) {
        val copy:Map<String, FileEditedPos> = history.storage.toMap()
        val sortedKeys = copy.keys.toSortedSet {k1, k2 ->
            val v1 = copy[k1]
            val v2 = copy[k2]
            if(v1 == null) {
                1
            }else if(v2 == null) {
                -1
            }else {
                v2.lastUsedTime.compareTo(v1.lastUsedTime)
            }
        }
        var count = 0
        val newStorage = mutableMapOf<String, FileEditedPos>()
        for(k in sortedKeys) {
            if(count >= _limit) {
                break
            }
            val v = copy[k]
            if(v!=null) {
                newStorage.set(k, v)
                count++
            }
        }
        history.storage = newStorage
    }
    private fun clearOldSettingsHistory() {
        try {
            SettingsUtil.update {
                it.editor.filesLastEditPosition.clear()
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#clearOldSettingsHistory err: ${e.stackTraceToString()}")
        }
    }
    fun reset() {
        update(FileOpenHistory())
    }
    private fun update(newHistory: FileOpenHistory) {
        curHistory = newHistory
        saveHistory(newHistory)
    }
    fun subtractTimeOffset(offsetInSec:Long) {
        val newStorage = mutableMapOf<String, FileEditedPos>()
        getHistory().storage.forEachBetter { k, v ->
            newStorage[k] = v.copy(lastUsedTime = v.lastUsedTime - (offsetInSec))
        }
        update(FileOpenHistory(newStorage))
    }
}
