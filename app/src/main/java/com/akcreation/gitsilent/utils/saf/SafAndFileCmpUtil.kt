package com.akcreation.gitsilent.utils.saf

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile
import com.akcreation.gitsilent.utils.IOUtil
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

object SafAndFileCmpUtil {
    class SafAndFileCompareResult (
        val onlyInSaf:MutableList<DocumentFile> = mutableListOf(),
        val onlyInFiles:MutableList<File> = mutableListOf(),
        val bothAndNotSame:MutableList<SafAndFileDiffPair> = mutableListOf(),
    ) {
        override fun toString(): String {
            val splitLine = "\n\n------------\n\n"
            return "onlyInSaf.size=${onlyInSaf.size}, \nonlyInFiles.size=${onlyInFiles.size}, \nbothAndNotSame.size=${bothAndNotSame.size}" +
                    splitLine +
                    "onlyInSaf=${onlyInSaf.map { it.uri.toString() + "\n\n" }}" +
                    splitLine +
                    "onlyInFiles=${onlyInFiles.map { it.canonicalPath + "\n\n" }}" +
                    splitLine +
                    "bothAndNotSame=${bothAndNotSame.map { 
                        "safFile=${it.safFile.uri}, \nfile=${it.file.canonicalPath}, \ndiffType=${it.diffType}\n\n"
                    }}"
        }
    }
    class SafAndFileDiffPair(
        val safFile: DocumentFile,
        val file: File,
        val diffType:SafAndFileDiffType
    )
    enum class SafAndFileDiffType(val code: Int) {
        NONE(0),
        CONTENT(1),
        TYPE(2)
        ;
        companion object {
            fun fromCode(code: Int): SafAndFileDiffType? {
                return SafAndFileDiffType.entries.find { it.code == code }
            }
        }
    }
    class OpenInputStreamFailed(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause) {
    }
    fun recursiveCompareFiles_Saf(
        contentResolver: ContentResolver,
        safFiles:Array<DocumentFile>,
        files:Array<File>,
        result:SafAndFileCompareResult,
        canceled:()->Boolean,
    ) {
        if(canceled()) {
            throw CancellationException()
        }
        if(safFiles.isEmpty() && files.isEmpty()) {
            return
        }
        val safFiles = safFiles.toMutableList()
        val files = files.toMutableList()
        for(f in files) {
            if(canceled()) {
                throw CancellationException()
            }
            val saffIndex = safFiles.indexOfFirst { f.name == it.name }
            if(saffIndex != -1) {
                val saff = safFiles[saffIndex]
                if((f.isFile.not() && saff.isFile) || (f.isFile && saff.isFile.not()) ) {
                    result.bothAndNotSame.add(
                        SafAndFileDiffPair(
                            safFile = saff,
                            file = f,
                            diffType = SafAndFileDiffType.TYPE
                        )
                    )
                }else if(f.isFile && saff.isFile) {
                    val diffType = if(f.length() != saff.length()) {
                        SafAndFileDiffType.CONTENT
                    }else { 
                        val saffis = contentResolver.openInputStream(saff.uri) ?: throw OpenInputStreamFailed(message = "open InputStream for uri failed: uri = '${saff.uri}'")
                        val fiBuf = IOUtil.createByteBuffer()
                        val saffiBuf = IOUtil.createByteBuffer()
                        var type = SafAndFileDiffType.NONE
                        f.inputStream().use { fis ->
                            saffis.use { saffis ->
                                while (true) {
                                    val fb = IOUtil.readBytes(fis, fiBuf)
                                    if(fb < 1) {  
                                        break
                                    }
                                    IOUtil.readBytes(saffis, saffiBuf)
                                    if(IOUtil.bytesAreNotEquals(fiBuf, saffiBuf, 0, fb)) {
                                        type = SafAndFileDiffType.CONTENT
                                        break
                                    }
                                }
                            }
                        }
                        type
                    }
                    if(diffType != SafAndFileDiffType.NONE) {
                        result.bothAndNotSame.add(
                            SafAndFileDiffPair(
                                safFile = saff,
                                file = f,
                                diffType = diffType
                            )
                        )
                    }  
                }else if(f.isDirectory && saff.isDirectory) {
                    recursiveCompareFiles_Saf(contentResolver, saff.listFiles() ?: arrayOf(), f.listFiles() ?: arrayOf(), result, canceled)
                }
                safFiles.removeAt(saffIndex)
            }else {  
                result.onlyInFiles.add(f)
            }
        }
        result.onlyInSaf.addAll(safFiles)
    }
}
