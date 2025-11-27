package com.akcreation.gitsilent.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.core.app.ShareCompat
import com.akcreation.gitsilent.utils.mime.MimeType
import com.akcreation.gitsilent.utils.mime.intentType
import kotlinx.coroutines.delay

fun <T : CharSequence> T.takeIfNotBlank(): T? = if (isNotBlank()) this else null
fun <T : CharSequence> T.takeIfNotEmpty(): T? = if (isNotEmpty()) this else null
fun SoftwareKeyboardController.hideForAWhile(timeoutInMillSec: Long = 200L) {
    doJobThenOffLoading {
        val hideKbJob = doJobThenOffLoading {
            runCatching {
                while (true) {
                    delay(30)
                    hide()
                }
            }
        }
        delay(timeoutInMillSec)
        hideKbJob?.cancel()
    }
}
fun String.appendCutSuffix() = "$this (CUT)"
fun String.countSub(sub: String) = this.split(sub).size - 1
fun String.pairClosed(openSign: String, closeSign:String) = (this.countSub(openSign).let { open ->
    this.countSub(closeSign).let { close ->
        if(open == 0) {
            true
        }else if(close == 0) {
            false
        }else if(closeSign.contains(openSign)) {
            open == close || open / close == 2
        }else {
            open == close
        }
    }
})
fun Collection<Uri>.createSendStreamIntent(context: Context, mimeTypes: Collection<MimeType>): Intent =
    ShareCompat.IntentBuilder(context)
        .setType(mimeTypes.intentType)
        .apply { forEach { addStream(it) } }
        .intent
        .apply {
            @Suppress("DEPRECATION")
            removeFlagsCompat(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        }
fun Intent.removeFlagsCompat(flags: Int) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        removeFlags(flags)
    } else {
        setFlags(this.flags andInv flags)
    }
}
fun Intent.withChooser(title: CharSequence? = null, vararg initialIntents: Intent): Intent =
    Intent.createChooser(this, title).apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents)
    }
fun Intent.withChooser(vararg initialIntents: Intent) = withChooser(null, *initialIntents)
fun Int.hasBits(bits: Int): Boolean = this and bits == bits
infix fun Int.andInv(other: Int): Int = this and other.inv()
