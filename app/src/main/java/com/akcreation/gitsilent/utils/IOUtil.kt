package com.akcreation.gitsilent.utils

import java.io.IOException
import java.io.InputStream
import java.util.Objects

object IOUtil {
    const val ioBufSize = 8192; 
    fun createByteBuffer():ByteArray {
        return ByteArray(ioBufSize)
    }
    @Throws(IOException::class)
    fun readNBytes(inputStream: InputStream, b: ByteArray, off: Int, len: Int): Int {
        Objects.checkFromIndexSize(off, len, b.size)
        var n = 0
        while (n < len) {
            val count: Int = inputStream.read(b, off + n, len - n)
            if (count < 0) break
            n += count
        }
        return n
    }
    @Throws(IOException::class)
    fun readBytes(inputStream: InputStream, b: ByteArray): Int {
        return readNBytes(inputStream, b, 0, b.size)
    }
    fun bytesAreEquals(left:ByteArray, right:ByteArray, startIndex:Int, len: Int) :Boolean {
        for (idx in startIndex..getEndIndex(startIndex, len)) {
            if(left[idx] != right[idx]) {
                return false
            }
        }
        return true
    }
    fun bytesAreNotEquals(left:ByteArray, right:ByteArray, startIndex:Int, len: Int) :Boolean {
        return !bytesAreEquals(left, right, startIndex, len)
    }
    fun getEndIndex(startIndex:Int, len:Int):Int {
        return startIndex + len - 1
    }
    fun getLen(startIndex:Int, endIndex:Int):Int {
        return endIndex - startIndex + 1
    }
}
