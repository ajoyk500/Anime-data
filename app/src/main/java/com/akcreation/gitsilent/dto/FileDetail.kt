package com.akcreation.gitsilent.dto

import com.akcreation.gitsilent.screen.shared.FuckSafFile
import com.akcreation.gitsilent.utils.FsUtils

data class FileDetail(
    val file: FuckSafFile,
    val shortContent:String = "",
) {
    private var cached_appRelatedPath:String? = null
    fun cachedAppRelatedPath() = FsUtils.getPathWithInternalOrExternalPrefixAndRemoveFileNameAndEndSlash(file.path.ioPath, file.name).let { cached_appRelatedPath = it; it }
}
