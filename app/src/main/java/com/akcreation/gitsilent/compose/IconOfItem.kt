package com.akcreation.gitsilent.compose

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.akcreation.gitsilent.utils.apkIconOrNull
import com.akcreation.gitsilent.utils.cache.ThumbCache
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.getVideoThumbnail
import com.akcreation.gitsilent.utils.mime.MimeType
import com.akcreation.gitsilent.utils.mime.guessFromFile
import com.akcreation.gitsilent.utils.mime.iconRes
import java.io.File

private const val iconSizeInPx = 100
private val iconModifierSize = 50.dp
private val thumbnailModifierSize = 50.dp
private val defaultIconModifier = Modifier.size(iconModifierSize)
private val defaultThumbnailModifier = Modifier.size(thumbnailModifierSize)
private val contentScale = ContentScale.Crop
@Composable
fun IconOfItem(
    fileName:String,
    filePath:String,
    context: Context,
    contentDescription:String?,
    iconColor: Color = LocalContentColor.current,
    defaultIconWhenLoadFailed: ImageVector? = null,
    iconModifier: Modifier = defaultIconModifier,
    thumbnailModifier: Modifier = defaultThumbnailModifier,
) {
    val file = remember(filePath) { File(filePath) }
    val filePath = remember(file.canonicalPath) { file.canonicalPath }
    val mime = MimeType.guessFromFile(file)
    if(mime.type == "image" && file.let{ it.exists() && it.isFile }) {
        ShowThumbnailForImage(context, filePath, contentDescription, mime.iconRes, iconColor, thumbnailModifier)
        return
    }
    if(mime.type == "video" && file.let{ it.exists() && it.isFile }) {
        ShowThumbnailOrFallback(
            filePath,
            contentDescription,
            mime.iconRes,
            iconColor,
            thumbnailModifier,
            loadThumbnail = { getVideoThumbnail(filePath) }
        )
        return
    }
    if(mime == MimeType.APK) {
        ShowThumbnailOrFallback(
            filePath,
            contentDescription,
            mime.iconRes,
            iconColor,
            thumbnailModifier,
            loadThumbnail = { apkIconOrNull(context, filePath, iconSizeInPx) }
        )
        return
    }
    ShowIcon(defaultIconWhenLoadFailed ?: mime.iconRes, contentDescription, iconColor, iconModifier)
}
@Composable
private fun ShowThumbnailForImage(
    context:Context,
    filePath:String,
    contentDescription: String?,
    fallbackIcon: ImageVector,
    fallbackIconColor: Color,
    modifier: Modifier
) {
    val loadErr = remember { mutableStateOf(false) }
    if(loadErr.value) {
        ShowIcon(fallbackIcon, contentDescription, fallbackIconColor, modifier)
    }else {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(filePath)
                .size(iconSizeInPx)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,  
            placeholder = rememberVectorPainter(fallbackIcon),  
            modifier = modifier,  
            onError = {
                loadErr.value = true
            },
            onLoading = {
                loadErr.value = false
            },
            onSuccess = {
                loadErr.value = false
            }
        )
    }
}
@Composable
private fun ShowThumbnailOrFallback(
    filePath: String,
    contentDescription: String?,
    fallbackIcon: ImageVector,
    fallbackIconColor: Color,
    modifier: Modifier,
    loadThumbnail: suspend ()-> ImageBitmap?,
) {
    val thumbnail = remember { mutableStateOf<ImageBitmap?>(ThumbCache.getThumb(filePath)) }
    thumbnail.value.let {
        if(it == null) {
            ShowIcon(fallbackIcon, contentDescription, fallbackIconColor, modifier)
        }else {
            Image(
                it,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
            )
        }
    }
    DisposableEffect (Unit) {
        val job = if(thumbnail.value == null) {
            doJobThenOffLoading {
                thumbnail.value = loadThumbnail().let {
                    if(it != null) {
                        ThumbCache.cacheIt(filePath, it)
                    }
                    it
                }
            }
        }else {
            null
        }
        onDispose {
            runCatching { job?.cancel() }
        }
    }
}
