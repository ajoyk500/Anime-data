package com.akcreation.gitsilent.provider

import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.saf.SafUtil
import kotlinx.coroutines.runBlocking

private const val TAG = "MyFileProvider"
class MyFileProvider: FileProvider() {
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        return super.openFile(uri, mode)
    }
    override fun openFile(uri: Uri, mode: String, signal: CancellationSignal?): ParcelFileDescriptor? {
        return openFile(uri, mode)
    }
}
