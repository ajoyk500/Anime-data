
package com.akcreation.gitsilent.utils


@JvmInline
value class PathName(val value: String) {
    val fileName: String?
        get() = value.substringAfterLast(SEPARATOR).takeIfNotEmpty()
    val directoryName: String?
        get() = value.substringBeforeLastOrNull(SEPARATOR)?.dropLastWhile { it == SEPARATOR }
    private fun String.substringBeforeLastOrNull(delimiter: Char): String? {
        val index = lastIndexOf(delimiter)
        return if (index != -1) substring(0, index) else null
    }
    companion object {
        const val SEPARATOR = '/'
    }
}
fun String.asPathName(): PathName {
    require(isValidPathName)
    return PathName(this)
}
fun String.asPathNameOrNull(): PathName? = if (isValidPathName) PathName(this) else null
private val String.isValidPathName: Boolean
    get() = isNotEmpty() && !contains('\u0000')
@JvmInline
value class FileName(val value: String) {
    val singleExtension: String
        get() = value.substringAfterLast(EXTENSION_SEPARATOR, "")
    val extensions: String
        get() {
            val lastExtension = singleExtension
            if (DOUBLE_EXTENSIONS.any { lastExtension.equals(it, true) }) {
                val secondLastExtension = value.dropLast(lastExtension.length + 1)
                    .substringAfterLast(EXTENSION_SEPARATOR, "")
                if (secondLastExtension.isNotEmpty()) {
                    return "$secondLastExtension$EXTENSION_SEPARATOR$lastExtension"
                }
            }
            return lastExtension
        }
    val baseName: String
        get() {
            val extensions = extensions
            return if (extensions.isNotEmpty()) value.dropLast(extensions.length + 1) else value
        }
    companion object {
        const val EXTENSION_SEPARATOR = '.'
        private val DOUBLE_EXTENSIONS = listOf("bz", "bz2", "gz", "sit", "xz", "Z")
    }
}
fun String.asFileName(): FileName {
    require(isValidFileName)
    return FileName(this)
}
fun String.asFileNameOrNull(): FileName? = if (isValidFileName) FileName(this) else null
private val String.isValidFileName: Boolean
    get() = isNotEmpty() && !contains('\u0000') && !contains(PathName.SEPARATOR)
