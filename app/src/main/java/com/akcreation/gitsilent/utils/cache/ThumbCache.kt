package com.akcreation.gitsilent.utils.cache

import androidx.compose.ui.graphics.ImageBitmap

object ThumbCache:CacheStoreImpl() {
    fun cacheIt(filePath:String, thumb: ImageBitmap) {
        set(filePath, thumb)
    }
    fun getThumb(filePath: String): ImageBitmap? {
        return getByType<ImageBitmap>(filePath)
    }
}
