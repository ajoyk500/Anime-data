package com.akcreation.gitsilent.utils.app.upgrade.migrator

import com.akcreation.gitsilent.utils.AppModel
import java.io.File

object AppVersionMan {
    val currentVersion = AppModel.getAppVersionCode()
    const val filename="AppVer"
    lateinit var verFile: File
    val err_fileNonExists = -1  
    val err_parseVersionFailed = -2  
    suspend fun init(storeDir:File=AppModel.innerDataDir, migrator: suspend (oldVer:Int) ->Boolean) {
        verFile=File(storeDir, filename)
        val oldVer = getVersionFromFile()
        val migrateSuccess = migrator(oldVer)
        if(migrateSuccess) {
            createIfNonExists()
            upgrade(currentVersion)
        }
    }
    fun createIfNonExists() {
        if(!verFile.exists()) {
            verFile.createNewFile()
        }
    }
    fun upgrade(newVer:Int) {
        verFile.writer().use {
            it.write(newVer.toString())
        }
    }
    fun getVersionFromFile():Int {
        try {
            if(!verFile.exists()) {
                return err_fileNonExists
            }
            return verFile.bufferedReader().readLine().toInt()
        }catch (e:Exception) {
            return err_parseVersionFailed
        }
    }
}
