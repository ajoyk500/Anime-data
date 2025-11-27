package com.akcreation.gitsilent.utils

import android.content.Context
import com.akcreation.gitsilent.jni.SshCert
import com.akcreation.gitsilent.R
import com.github.git24j.core.Libgit2
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

private const val TAG = "Lg2HomeUtils"
object Lg2HomeUtils {
    private const val sshKnownHostsLatestVer = 2 
    private const val libgit2HomeDirName = "lg2home"
    private const val sshDirName = ".ssh"
    private const val sshKnownHostsFileName = "known_hosts" 
    private const val sshKnownHostsVersionFileName = "version_known_hosts" 
    private val known_hostsRawId = R.raw.known_hosts
    private lateinit var lg2Home: File
    private lateinit var sshDir: File
    private lateinit var knownHostsFile:File
    private lateinit var knownHostsVersionFile:File
    private const val userSshKnownHostsFileName = "user_known_hosts"  
    private lateinit var userKnownHostsFile:File
    private val userKnownHostItems:MutableSet<SshCert> = mutableSetOf()
    private val userKnownHostsFileLock:Mutex = Mutex()
    fun init(homeBaseDirPath:File, appContext: Context) {
        lg2Home=createDirIfNonexists(homeBaseDirPath, libgit2HomeDirName)
        sshDir=createDirIfNonexists(lg2Home, sshDirName)
        knownHostsFile = File(sshDir.canonicalPath, sshKnownHostsFileName)
        knownHostsVersionFile = File(sshDir.canonicalPath, sshKnownHostsVersionFileName)
        userKnownHostsFile = File(sshDir.canonicalPath, userSshKnownHostsFileName)
        createKnownHostsIfNonExists(appContext)
        Libgit2.optsGitOptSetHomedir(lg2Home.canonicalPath)
        createUserKnownHostsIfNonExists()
        readItemFromUserKnownHostsFile()
    }
    fun getLg2Home():File {
        if(lg2Home.exists().not()) {
            lg2Home.mkdirs()
        }
        return lg2Home
    }
    private fun createKnownHostsIfNonExists(appContext: Context) {
        val verFromFile = readIntVersionFromFile(knownHostsVersionFile)
        if(verFromFile == sshKnownHostsLatestVer && knownHostsFile.exists()) {
            return
        }
        knownHostsFile.parentFile?.mkdirs()
        knownHostsFile.createNewFile()
        FsUtils.copy(appContext.resources.openRawResource(known_hostsRawId), knownHostsFile.outputStream())
        writeIntVersionToFile(knownHostsVersionFile, sshKnownHostsLatestVer)
    }
    private fun createUserKnownHostsIfNonExists() {
        if(userKnownHostsFile.exists()) {
            return
        }
        userKnownHostsFile.parentFile?.mkdirs()
        userKnownHostsFile.createNewFile()
        userKnownHostItems.clear()
    }
    private fun readItemFromUserKnownHostsFile(){
        doJobThenOffLoading {
            userKnownHostsFileLock.withLock {
                userKnownHostItems.clear()
                val f = getUserKnownHostsFile()
                f.bufferedReader().use {
                    while(true) {
                        val line = it.readLine() ?: break
                        if(line.isBlank()) {
                            continue
                        }
                        val sshCert = SshCert.parseDbString(line)
                        if(sshCert != null) {  
                            userKnownHostItems.add(sshCert)
                        }
                    }
                }
            }
            MyLog.d(TAG, "read user SshCertList from file: size=${userKnownHostItems.size} items=$userKnownHostItems")
        }
    }
    private fun resetKnownHostFile(appContext: Context){
        knownHostsFile.delete()
        createKnownHostsIfNonExists(appContext)
    }
    fun resetUserKnownHostFile(){
        doJobThenOffLoading {
            userKnownHostsFileLock.withLock {
                userKnownHostsFile.delete()
                createUserKnownHostsIfNonExists()
            }
        }
    }
    private fun getUserKnownHostsFile():File {
        return userKnownHostsFile
    }
    fun getUserKnownHostsFileItems():Set<SshCert> {
        return userKnownHostItems.toSet()
    }
    fun itemInUserKnownHostsFile(item:SshCert):Boolean{
        val set = getUserKnownHostsFileItems()
        val contained = set.contains(item)
        MyLog.d(TAG, "sshCert already contained: $contained, sshCertNeedCheck=$item, allCertCount=${set.size}, allCert=$set")
        return contained
    }
    private fun writeItemsToUserKnownHostsFile() {
        if(userKnownHostItems.isEmpty()) {
            resetUserKnownHostFile()
            return
        }
        doJobThenOffLoading {
            userKnownHostsFileLock.withLock {
                userKnownHostsFile.outputStream().bufferedWriter().use { writer->
                    userKnownHostItems.forEach {
                        writer.write("${it.toDbString()}\n")
                    }
                }
            }
        }
    }
    fun addItemToUserKnownHostsFile(item:SshCert) {
        if(userKnownHostItems.add(item)) {
            writeItemsToUserKnownHostsFile()
        }
    }
}
