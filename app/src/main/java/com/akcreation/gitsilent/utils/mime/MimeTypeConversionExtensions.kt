
package com.akcreation.gitsilent.utils.mime

import android.webkit.MimeTypeMap
import com.akcreation.gitsilent.utils.asFileName
import com.akcreation.gitsilent.utils.asPathName
import java.io.File

fun MimeType.Companion.guessFromPath(path: String): MimeType {
    val fileName = path.asPathName().fileName ?: return DIRECTORY
    return guessFromExtension(fileName.asFileName().singleExtension)
}
fun MimeType.Companion.guessFromFile(file: File): MimeType {
    if(file.isDirectory) return DIRECTORY
    val fileName = file.name
    return guessFromExtension(fileName.asFileName().singleExtension)
}
fun MimeType.Companion.guessFromFileName(fileName: String): MimeType {
    return guessFromExtension(fileName.asFileName().singleExtension)
}
fun MimeType.Companion.guessFromExtension(extension: String): MimeType {
    val extension = extension.lowercase()
    return extensionToMimeTypeOverrideMap[extension]
        ?: MimeTypeMap.getSingleton().getMimeTypeFromExtensionCompat(extension)?.asMimeTypeOrNull()
        ?: GENERIC  
}
private val extensionToMimeTypeOverrideMap = mapOf(
    "cab" to "application/vnd.ms-cab-compressed", 
    "csv" to "text/csv", 
    "sh" to "application/x-sh", 
    "otf" to "font/otf", 
    "bz" to "application/x-bzip",
    "bz2" to "application/x-bzip2",
    "z" to "application/x-compress",
    "lzma" to "application/x-lzma",
    "p7b" to "application/x-pkcs7-certificates",
    "spc" to "application/x-pkcs7-certificates", 
    "p7c" to "application/pkcs7-mime",
    "p7s" to "application/pkcs7-signature",
    "ts" to "application/typescript", 
    "py3" to "text/x-python",
    "py3x" to "text/x-python",
    "pyx" to "text/x-python",
    "wsgi" to "text/x-python",
    "yaml" to "text/x-yaml",
    "yml" to "text/x-yaml",
    "asm" to "text/x-asm",
    "s" to "text/x-asm",
    "cs" to "text/x-csharp",
    "azw" to "application/vnd.amazon.ebook",
    "ibooks" to "application/x-ibooks+zip",
    "msg" to "application/vnd.ms-outlook",
    "mkd" to "text/markdown",
    "conf" to "text/plain",
    "ini" to "text/plain",
    "list" to "text/plain",
    "log" to "text/plain",
    "prop" to "text/plain",
    "properties" to "text/plain",
    "rc" to "text/plain"
).mapValues { it.value.asMimeType() }
val MimeType.intentType: String
    get() = intentMimeType.value
private val MimeType.intentMimeType: MimeType
    get() = mimeTypeToIntentMimeTypeMap[this] ?: this
private val mimeTypeToIntentMimeTypeMap = listOf(
    "application/ecmascript" to "text/ecmascript",
    "application/javascript" to "text/javascript",
    "application/json" to "text/json",
    "application/x-go" to "text/x-go",
    "application/x-kotlin" to "text/x-kotlin",
    "application/x-groovy" to "text/x-groovy",
    "application/x-jsx" to "text/x-jsx",
    "application/x-batchfile" to "text/x-batchfile",
    "application/x-powershell" to "text/x-powershell",
    "application/x-less" to "text/x-less",
    "application/x-scss" to "text/x-scss",
    "application/x-rust" to "text/x-rust",
    "application/x-coffee" to "text/x-coffee",
    "application/x-clojure" to "text/x-clojure",
    "application/x-lua" to "text/x-lua",
    "application/x-php" to "text/x-php",
    "application/x-r" to "text/x-r",
    "application/x-sql" to "text/x-sql",
    "application/x-swift" to "text/x-swift",
    "application/x-vb" to "text/x-vb",
    "application/x-dart" to "text/x-dart",
    "application/x-dockerfile" to "text/x-dockerfile",
    "application/x-makefile" to "text/x-makefile",
    "application/x-ruby" to "text/x-ruby",
    "application/x-tsx" to "text/x-tsx",
    "application/typescript" to "text/typescript",
    "application/x-sh" to "text/x-shellscript",
    "application/x-shellscript" to "text/x-shellscript",
    "application/x-raku" to "text/x-raku",
    "application/x-toml" to "text/x-toml",
    "application/x-proguard" to "text/x-proguard",
    "application/x-zig" to "text/x-zig",
    "application/x-vue" to "text/x-vue",
    "application/x-fsharp" to "text/x-fsharp",
    "application/x-julia" to "text/x-julia",
    MimeType.GENERIC.value to MimeType.ANY.value
).associate { it.first.asMimeType() to it.second.asMimeType() }
val Collection<MimeType>.intentType: String
    get() {
        if (isEmpty()) {
            return MimeType.ANY.value
        }
        val intentMimeTypes = map { it.intentMimeType }
        val firstIntentMimeType = intentMimeTypes.first()
        if (intentMimeTypes.all { firstIntentMimeType.match(it) }) {
            return firstIntentMimeType.value
        }
        val wildcardIntentMimeType = MimeType.of(firstIntentMimeType.type, "*", null)
        if (intentMimeTypes.all { wildcardIntentMimeType.match(it) }) {
            return wildcardIntentMimeType.value
        }
        return MimeType.ANY.value
    }
