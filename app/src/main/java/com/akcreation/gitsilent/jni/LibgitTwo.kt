package com.akcreation.gitsilent.jni

import com.github.git24j.core.Blob
import java.nio.charset.StandardCharsets

object LibgitTwo {
    external fun jniLibgitTwoInit()
    external fun jniClone(url: String?, local_path: String?, jniCloneOptionsPtr: Long, allowInsecure: Boolean): Long
    external fun jniCreateCloneOptions(version: Int): Long
    external fun jniTestClone(url: String?, local_path: String?, jniCloneOptionsPtr: Long): Long
    var cloneVersion: Int = 1
    external fun hello(a: Int, b: Int): String?
    external fun jniSetCertFileAndOrPath(certFile: String?, certPath: String?): Long
    external fun jniSetCredentialCbTest(remoteCallbacks: Long)
    external fun jniSetCertCheck(remoteCallbacks: Long)
    external fun jniLineGetContent(linePtr: Long): String?
    external fun jniTestAccessExternalStorage()
    external fun jniEntryByName(treePtr: Long, filename: String?): Long?
    external fun jniGetDataOfSshCert(certPtr: Long, hostname:String): SshCert?
    external fun jniGetStatusEntryRawPointers(statusListPtr: Long): LongArray?
    external fun jniGetStatusEntries(statusListPtr: Long): Array<StatusEntryDto>?
    private external fun jniSaveBlobToPath(blobPtr:Long, savePath:String): Int
    fun saveBlobToPath(blob: Blob, savePath: String): SaveBlobRetCode {
        val retCode = jniSaveBlobToPath(blob.rawPointer, savePath)
        return SaveBlobRetCode.fromCode(retCode)!!
    }
    fun getContent(contentLen: Int, content: String): String {
        val src = content.toByteArray(StandardCharsets.UTF_8)
        if (src.size > contentLen) {  
            val dest = ByteArray(contentLen)
            System.arraycopy(src, 0, dest, 0, contentLen)
            return String(dest)
        }
        return content
    }
    fun entryRawPointers(statusListPtr: Long): LongArray {
        val ptrs = jniGetStatusEntryRawPointers(statusListPtr)
        return if(ptrs == null) LongArray(0) else ptrs
    }
    fun getStatusEntries(statusListPtr:Long): Array<StatusEntryDto> {
        val ptrs = jniGetStatusEntries(statusListPtr)
        return ptrs ?: arrayOf()
    }
}
