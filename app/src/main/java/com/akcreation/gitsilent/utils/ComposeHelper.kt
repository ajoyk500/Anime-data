package com.akcreation.gitsilent.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.constants.Cons

object ComposeHelper {
    @Composable
    fun getDoubleClickBackHandler(
        context: Context,
        openDrawer:() -> Unit,
        exitApp: () -> Unit
    ): () -> Unit {
        val backStartSec = rememberSaveable { mutableLongStateOf(0) }
        val pressBackAgainForExitText = stringResource(R.string.press_back_again_to_exit);
        val showTextAndUpdateTimeForPressBackBtn = {
            openDrawer()
            showToast(context, pressBackAgainForExitText, Toast.LENGTH_SHORT)
            backStartSec.longValue = getSecFromTime() + Cons.pressBackDoubleTimesInThisSecWillExit
        }
        val backHandlerOnBack = {
            if (backStartSec.longValue > 0 && getSecFromTime() <= backStartSec.longValue) {  
                exitApp()
            } else {
                showTextAndUpdateTimeForPressBackBtn()
            }
        }
        return backHandlerOnBack
    }
}
