package com.akcreation.gitsilent.utils.storagepaths

import android.content.Context
import com.akcreation.gitsilent.screen.functions.getFilesScreenTitle
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.JsonUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.encodeToStream
import java.io.File

object StoragePathsMan {
    private val TAG = "StoragePathsMan"
    private val fileName = "storage_paths.json"
    private lateinit var _file: File
    private lateinit var _saveDir: File
    private lateinit var paths: StoragePaths
    private val lock = Mutex()
    fun init(saveDir:File, oldSettingsStoragePaths:List<String>?, oldSettingsLastSelectedPath:String?) {
        val saveDirPath = saveDir.canonicalPath
        _saveDir = File(saveDirPath)
        _file = File(saveDirPath, fileName)
        readFromFile()
        if(oldSettingsStoragePaths!=null || oldSettingsLastSelectedPath!=null) {
            migrateFromOldSettings(oldSettingsStoragePaths ?: listOf(), oldSettingsLastSelectedPath ?: "")
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
    private fun readFromFile() {
        val f = getFile()
        try {
            paths = JsonUtil.j.decodeFromString<StoragePaths>(f.readText())
        }catch (e:Exception) {
            reset()
            MyLog.e(TAG, "#readFromFile: read err, file content empty or corrupted, will return a new object, err is: ${e.localizedMessage}")
        }
    }
    fun get():StoragePaths {
        return paths.copy()
    }
    @OptIn(ExperimentalSerializationApi::class)
    fun save(newPaths:StoragePaths) {
        doJobThenOffLoading {
            try {
                paths = newPaths
                lock.withLock {
                    JsonUtil.j.encodeToStream(newPaths, getFile().outputStream())
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#save: save storage paths err: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }
    private fun migrateFromOldSettings(oldSettingsStoragePaths: List<String>, oldSettingsLastSelectedPath: String) {
        try {
            val p = get()
            val needMigratePaths = oldSettingsStoragePaths.isNotEmpty()
            if(needMigratePaths) {
                p.storagePaths.addAll(oldSettingsStoragePaths)
            }
            val needMigrateSelectedPaths = oldSettingsLastSelectedPath.isNotBlank()
            if(needMigrateSelectedPaths) {
                if(p.storagePathLastSelected.isBlank()) {
                    p.storagePathLastSelected = oldSettingsLastSelectedPath
                }
            }
            if(needMigratePaths || needMigrateSelectedPaths) {
                SettingsUtil.update {
                    it.storagePaths.clear()
                    it.storagePathLastSelected=""
                }
                save(p)
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#migrateFromOldSettings err: ${e.localizedMessage}")
        }
    }
    fun update(modify:(storagePaths: StoragePaths)->Unit) {
        val tmp = get()
        modify(tmp)
        save(tmp)
    }
    fun reset() {
        paths = StoragePaths()
        save(paths)
    }
    fun allowAddPath(path:String): Boolean {
        return path != FsUtils.getInternalStorageRootPathNoEndsWithSeparator()
    }
    fun getItemName(path: String, context: Context) = getFilesScreenTitle(path, context)
}
