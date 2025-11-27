package com.akcreation.gitsilent.compose

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.saf.MyOpenDocumentTree
import com.akcreation.gitsilent.utils.saf.SafUtil

private const val TAG = "SystemFolderChooserSaf"
@Deprecated("replace with `InternalFileChooser`")
@Composable
fun SystemFolderChooserSaf(
    activityContext: Context,
    safEnabled:MutableState<Boolean>,
    safPath:MutableState<String>,
    nonSafPath:MutableState<String>,
    path:MutableState<String>,
    pathTextFieldLabel:String=stringResource(R.string.path),
    pathTextFieldPlaceHolder:String=stringResource(R.string.eg_storage_emulate_0_repos),
    showSafSwitchButton:Boolean = false,
    chosenPathCallback:(uri: Uri?)->Unit = {uri->
        if(uri != null) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "uri.toString() == uri.path: ${uri.toString() == uri.path}, uri.toString()=${uri.toString()}, uri.path=${uri.path}")
            }
            safPath.value = SafUtil.uriToDbSupportedFormat(uri)
            nonSafPath.value = FsUtils.getRealPathFromUri(uri)
            path.value = if(safEnabled.value) safPath.value else nonSafPath.value
            MyLog.d(TAG, "#chooseDirLauncher: uri.toString()=${uri.toString()}, uri.path=${uri.path}, safEnabled=${safEnabled.value}, safPath=${safPath.value}, nonSafPath=${nonSafPath.value}")
        }
    }
) {
    val chooseDirLauncher = rememberLauncherForActivityResult(MyOpenDocumentTree()) { uri ->
        if(uri != null){
            SafUtil.takePersistableRWPermission(activityContext.contentResolver, uri)
            chosenPathCallback(uri)
        }
    }
    GrantManageStoragePermissionClickableText(activityContext)
    TextField(
        modifier = Modifier.fillMaxWidth().padding(horizontal = MyStyleKt.defaultHorizontalPadding),
        value = path.value,
        maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
        onValueChange = {
            path.value = it
        },
        label = {
            Text(pathTextFieldLabel)
        },
        placeholder = {
            Text(pathTextFieldPlaceHolder)
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    chooseDirLauncher.launch(null)
                }
            ) {
                Icon(imageVector = Icons.Filled.MoreHoriz, contentDescription = stringResource(R.string.three_dots_icon_for_choose_folder))
            }
        }
    )
    if(showSafSwitchButton) {
        Spacer(Modifier.height(15.dp))
        MyCheckBox(text = stringResource(R.string.saf_mode), value = safEnabled, onValueChange = { newSafEnabledValue ->
            path.value = if (newSafEnabledValue) {
                safPath.value
            } else {
                nonSafPath.value
            }
            safEnabled.value = newSafEnabledValue
        })
        DefaultPaddingText(stringResource(R.string.saf_mode_note))
    }
}
