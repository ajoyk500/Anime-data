package com.akcreation.gitsilent.utils.cert

import android.content.Context
import com.akcreation.gitsilent.etc.CertSaver
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.readIntVersionFromFile
import com.akcreation.gitsilent.utils.writeIntVersionToFile
import com.github.git24j.core.Libgit2
import java.io.File

object CertMan {
    private const val TAG = "CertMan"
    const val currentVersion = 8
    private val certRawId = R.raw.cert_bundle_pem
    const val defaultCertBundleDirName = "cert-bundle"
    const val defaultCertUserDirName = "cert-user"  
    const val certBundleFileName = "cacertpem"
    const val certBundleVersionFileName = "cacertpem-version"
    private lateinit var certBundleFile:File;
    private lateinit var certBundleVersionFile:File;
    val sysCertList = listOf<CertSaver>(
        CertSaver(file=null, path="/system/etc/security/cacerts"),
        CertSaver(file=null, path="/apex/com.android.conscrypt/cacerts"),
    );
    fun init(appContext: Context, certBundleDir:File, certUserDir:File) {
        loadAppCert(appContext, certBundleDir)
        loadUserCerts(certUserDir)
    }
    fun loadAppCert(appContext: Context, certBundleDir:File) {
        try {
            if(!certBundleDir.exists()) {
                certBundleDir.mkdirs()
            }
            createCertBundlePemFileIfNeed(appContext, certBundleDir)
            loadCert(CertSaver(file = certBundleFile.canonicalPath))
        }catch (e:Exception) {
            MyLog.e(TAG, "#loadAppCert err: ${e.stackTraceToString()}")
            Msg.requireShowLongDuration("err:load cert-bundle err! app may not work!")
        }
    }
    fun createCertBundlePemFileIfNeed(appContext: Context, certBundleDir:File) {
        certBundleVersionFile = File(certBundleDir, certBundleVersionFileName)
        val verInFile = readIntVersionFromFile(certBundleVersionFile)
        certBundleFile = File(certBundleDir, certBundleFileName)
        if(verInFile == currentVersion && certBundleFile.exists()) {
            return
        }
        if(certBundleVersionFile.exists()) {
            certBundleVersionFile.delete()
        }
        if(certBundleFile.exists()) {
            certBundleFile.delete()
        }
        FsUtils.copy(appContext.resources.openRawResource(certRawId), certBundleFile.outputStream())
        writeIntVersionToFile(certBundleVersionFile, currentVersion)
    }
    fun loadCert(certSaver:CertSaver) {
        loadCerts(listOf(certSaver))
    }
    fun loadCerts(certSaverList:List<CertSaver>) {
        for (certSaver in certSaverList) {
            try {
                Libgit2.optsGitOptSetSslCertLocations(certSaver.file, certSaver.path)
            } catch (e: Exception) {
                MyLog.e(TAG, "#loadCerts err: cert=$certSaver, err=${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }
    @Deprecated("[CHINESE]app[CHINESE]，[CHINESE]app[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]")
    fun loadSysCerts() {
        loadCerts(sysCertList)
    }
    fun loadUserCerts(certUserDir:File) {
        val funName = "loadUserCerts"
        try {
            if(!certUserDir.exists()) {
                certUserDir.mkdirs()
                return
            }
            val userCerts = obtainUserCerts(certUserDir)
            loadCerts(userCerts)
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
        }
    }
    private fun obtainUserCerts(certUserDir: File): List<CertSaver> {
        val certs = mutableListOf<CertSaver>()
        certUserDir.listFiles { it:File ->
            if(it.isFile) {
                certs.add(CertSaver(file = it.canonicalPath))
            }
            false
        }
        return certs
    }
}
