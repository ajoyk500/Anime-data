package com.akcreation.gitsilent.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.mime.MimeType
import com.akcreation.gitsilent.utils.mime.guessFromFileName
import com.akcreation.gitsilent.utils.mime.intentType
import java.io.File

@Composable
fun OpenAsDialog(
    readOnly: MutableState<Boolean>,
    fileName:String,
    filePath:String,
    showOpenInEditor:Boolean=false,
    openInEditor:(expectReadOnly:Boolean)->Unit={},
    openSuccessCallback:()->Unit={},
    close:()->Unit
) {
    val activityContext = LocalContext.current
    val mimeTypeList = remember(fileName) {
        FsUtils.FileMimeTypes.typeList.toMutableList().let {
            it.add(
                FsUtils.FileMimeTypes.MimeTypeAndDescText(
                    MimeType.guessFromFileName(fileName).intentType
                ) { it.getString(R.string.file_open_as_by_extension) }
            )
            it
        }
    }
    val itemHeight = 50.dp
    val mimeTextWeight = FontWeight.Bold
    val mimeTypeFontSize = 12.sp
    PlainDialogWithPadding(
        onClose = close,
        scrollable = true,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        if(showOpenInEditor) {
            Row(modifier = Modifier
                .height(itemHeight)
                .fillMaxWidth()
                .clickable {
                    val expectReadOnly = readOnly.value  
                    openInEditor(expectReadOnly)
                    openSuccessCallback()
                    close()
                }
                ,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.open_in_editor), fontWeight = mimeTextWeight)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        mimeTypeList.forEachBetter { (mimeType, text) ->
            Row(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
                    .clickable {
                        val openSuccess = FsUtils.openFile(
                            activityContext,
                            File(filePath),
                            mimeType,
                            readOnly.value
                        )
                        if (openSuccess) {
                            openSuccessCallback()
                        } else {
                            Msg.requireShow(activityContext.getString(R.string.open_failed))
                        }
                        close()
                    },
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text(activityContext), fontWeight = mimeTextWeight)
                    Text(mimeType, fontSize = mimeTypeFontSize)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        MyCheckBox(stringResource(R.string.read_only), readOnly)
        Spacer(modifier = Modifier.height(20.dp))
    }
}
