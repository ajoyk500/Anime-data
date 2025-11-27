package com.akcreation.gitsilent.utils

import org.mozilla.universalchardet.Constants
import org.mozilla.universalchardet.UniversalDetector
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object EncodingUtil {
    private const val TAG = "EncodingUtil"
    internal const val UTF8_BOM = "UTF-8 BOM"
    val supportedCharsetList = listOf(
        Constants.CHARSET_BIG5,
        Constants.CHARSET_EUC_JP,
        Constants.CHARSET_EUC_KR,
        Constants.CHARSET_EUC_TW,
        Constants.CHARSET_GB18030,  
        Constants.CHARSET_GBK,  
        Constants.CHARSET_IBM855,
        Constants.CHARSET_IBM866,
        Constants.CHARSET_ISO_2022_JP,
        Constants.CHARSET_ISO_2022_CN,
        Constants.CHARSET_ISO_2022_KR,
        Constants.CHARSET_ISO_8859_5,
        Constants.CHARSET_ISO_8859_7,
        Constants.CHARSET_ISO_8859_8,
        Constants.CHARSET_KOI8_R,
        Constants.CHARSET_MACCYRILLIC,
        Constants.CHARSET_SHIFT_JIS,
        Constants.CHARSET_TIS620,
        Constants.CHARSET_UTF_8,
        UTF8_BOM,
        Constants.CHARSET_UTF_16BE,
        Constants.CHARSET_UTF_16LE,
        Constants.CHARSET_UTF_32BE,
        Constants.CHARSET_UTF_32LE,
        Constants.CHARSET_WINDOWS_1251,
        Constants.CHARSET_WINDOWS_1252,
        Constants.CHARSET_WINDOWS_1253,
        Constants.CHARSET_WINDOWS_1255,
    )
    val defaultCharsetName:String = Constants.CHARSET_UTF_8
    val defaultCharset: Charset = resolveCharset(defaultCharsetName)
    private fun makeSureUseASupportedCharset(originCharset: String?): String {
        if(originCharset == null) {
            return defaultCharsetName
        }
        if(originCharset == Constants.CHARSET_US_ASCII) {
            return Constants.CHARSET_UTF_8
        }
        if(originCharset == Constants.CHARSET_HZ_GB_2312) {
            return Constants.CHARSET_GBK
        }
        if(originCharset == Constants.CHARSET_X_ISO_10646_UCS_4_3412
            || originCharset == Constants.CHARSET_X_ISO_10646_UCS_4_2143
        ) {
            return defaultCharsetName
        }
        if(supportedCharsetList.contains(originCharset)) {
            return originCharset
        }
        return defaultCharsetName
    }
    fun detectEncoding(newInputStream: () -> InputStream): String {
        return try {
            detectEncodingNoCatch(newInputStream)
        }catch (e: Exception) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#detectEncoding() err, will use '$defaultCharsetName', err msg=${e.localizedMessage}")
                e.printStackTrace()
            }
            defaultCharsetName
        }
    }
    private fun detectEncodingNoCatch(newInputStream: () -> InputStream): String {
        val buf = ByteArray(4096)
        val detector = UniversalDetector()
        var nread: Int = -1
        val inputStream = newInputStream()
        while ((inputStream.read(buf).also { nread = it }) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread)
        }
        detector.dataEnd()
        val encoding = detector.getDetectedCharset()
        detector.reset()
        val encodingSupported = makeSureUseASupportedCharset(encoding)
        if(encodingSupported == Constants.CHARSET_UTF_8) {
            if(hasUtf8Bom(newInputStream())) {
                return UTF8_BOM
            }
        }
        return encodingSupported
    }
    fun resolveCharset(charsetName: String?) : Charset {
        try {
            if(charsetName == UTF8_BOM){
                return StandardCharsets.UTF_8
            }
            return Charset.forName(charsetName)
        }catch (e: Exception) {
            MyLog.e(TAG, "#resolveCharset err, will use '$defaultCharsetName', param `charsetName`=$charsetName, err=${e.localizedMessage}")
            e.printStackTrace()
            return defaultCharset
        }
    }
    fun addBomIfNeed(outputStream: OutputStream, charsetName: String?) {
        if(charsetName == UTF8_BOM) {
            outputStream.write(0xEF)
            outputStream.write(0xBB)
            outputStream.write(0xBF)
        }else if(charsetName == Constants.CHARSET_UTF_16LE) {
            outputStream.write(0xFF)
            outputStream.write(0xFE)
        }else if(charsetName == Constants.CHARSET_UTF_32LE) {
            outputStream.write(0xFF)
            outputStream.write(0xFE)
            outputStream.write(0x00)
            outputStream.write(0x00)
        }else if(charsetName == Constants.CHARSET_UTF_16BE) {
            outputStream.write(0xFE)
            outputStream.write(0xFF)
        }else if(charsetName == Constants.CHARSET_UTF_32BE) {
            outputStream.write(0x00)
            outputStream.write(0x00)
            outputStream.write(0xFE)
            outputStream.write(0xFF)
        }
    }
    fun hasUtf8Bom(inputStream: InputStream): Boolean {
        return inputStream.read() == 0xEF && inputStream.read() == 0xBB && inputStream.read() == 0xBF
    }
    fun consumeUtf8Bom(inputStream: InputStream) = hasUtf8Bom(inputStream)
    fun ignoreBomIfNeed(newInputStream: () -> InputStream, charsetName: String?): IgnoreBomResult {
        if(charsetName == UTF8_BOM) {
            val inputStream = newInputStream()
            if(consumeUtf8Bom(inputStream)) {
                return IgnoreBomResult(true, inputStream)
            }
        }
        if(charsetName == Constants.CHARSET_UTF_16LE) {
            val inputStream = newInputStream()
            if(inputStream.read() == 0xFF
                && inputStream.read() == 0xFE
            ) {
                return IgnoreBomResult(true, inputStream)
            }
        }
        if(charsetName == Constants.CHARSET_UTF_32LE) {
            val inputStream = newInputStream()
            if(inputStream.read() == 0xFF
                && inputStream.read() == 0xFE
                && inputStream.read() == 0x00
                && inputStream.read() == 0x00
            ) {
                return IgnoreBomResult(true, inputStream)
            }
        }
        if(charsetName == Constants.CHARSET_UTF_16BE) {
            val inputStream = newInputStream()
            if(inputStream.read() == 0xFE
                && inputStream.read() == 0xFF
            ) {
                return IgnoreBomResult(true, inputStream)
            }
        }
        if(charsetName == Constants.CHARSET_UTF_32BE) {
            val inputStream = newInputStream()
            if(inputStream.read() == 0x00
                && inputStream.read() == 0x00
                && inputStream.read() == 0xFE
                && inputStream.read() == 0xFF
            ) {
                return IgnoreBomResult(true, inputStream)
            }
        }
        return IgnoreBomResult(false, newInputStream())
    }
}
data class IgnoreBomResult(
    val wasHasBom: Boolean,
    val inputStream: InputStream,
)
