package com.akcreation.gitsilent.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.AppIcon
import com.akcreation.gitsilent.compose.ClearMasterPasswordDialog
import com.akcreation.gitsilent.compose.ClickableText
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PasswordTextFiled
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.HashUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.encrypt.MasterPassUtil

private const val TAG = "RequireMasterPasswordScreen"
@Composable
fun RequireMasterPasswordScreen(
    requireMasterPassword:MutableState<Boolean>
) {
    val activityContext = LocalContext.current
    val errMsg = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val showClearMasterPasswordDialog = rememberSaveable { mutableStateOf(false) }
    val focusRequest = remember {FocusRequester()}
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    if(showClearMasterPasswordDialog.value) {
        ClearMasterPasswordDialog(
            onCancel = {showClearMasterPasswordDialog.value = false},
            onOk = {
                showClearMasterPasswordDialog.value = false
                requireMasterPassword.value = false
            }
        )
    }
    val initLoadingText = stringResource(R.string.loading)
    val loading = rememberSaveable { mutableStateOf(false) }
    val loadingText = rememberSaveable { mutableStateOf(initLoadingText) }
    val loadingOn = { text:String->
        loadingText.value = text
        loading.value = true
    }
    val loadingOff = {
        loading.value = false
        loadingText.value = initLoadingText
    }
    val inputPassCallback = {
        val pass = password.value
        doJobThenOffLoading(loadingOn, loadingOff, loadingText.value) {
            try {
                loadingText.value = activityContext.getString(R.string.verifying)
                val verified = HashUtil.verify(pass, settings.masterPasswordHash)
                if(verified) {
                    loadingText.value = activityContext.getString(R.string.checking_creds_migration)
                    AppModel.dbContainer.credentialRepository.migrateEncryptVerIfNeed(pass)
                    loadingText.value = activityContext.getString(R.string.updating_master_password)
                    AppModel.masterPassword.value = pass
                    MasterPassUtil.save(AppModel.realAppContext, pass)
                    requireMasterPassword.value = false
                }else {
                    errMsg.value = activityContext.getString(R.string.wrong_password)
                }
            }catch (e:Exception) {
                errMsg.value = e.localizedMessage ?: (activityContext.getString(R.string.wrong_password) + ", (err msg is null)")
            }
        }
        Unit
    }
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
                .padding(MyStyleKt.defaultItemPadding)
                .imePadding()
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIcon()
            Spacer(modifier = Modifier.height(30.dp))
            PasswordTextFiled(
                password = password,
                passwordVisible = passwordVisible,
                label = stringResource(R.string.master_password),
                placeholder = stringResource(R.string.input_your_master_password),
                focusRequest = focusRequest,
                paddingValues = PaddingValues(vertical = 10.dp),
                errMsg = errMsg,
                enabled = loading.value.not(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    inputPassCallback()
                }),
                enterPressedCallback = inputPassCallback
            )
            ClickableText(
                text = stringResource(R.string.i_forgot_my_master_password),
                color = if(loading.value) UIHelper.getDisableTextColor() else MyStyleKt.ClickableText.getColor(),
                fontWeight = FontWeight.Light,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable(
                    enabled = loading.value.not(),
                    onClick = { showClearMasterPasswordDialog.value = true }
                ),
            )
            Spacer(Modifier.height(20.dp))
            Button(
                enabled = loading.value.not(),
                onClick = {
                    inputPassCallback()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
            Spacer(Modifier.height(20.dp))
            if(loading.value) {
                MySelectionContainer {
                    Text(loadingText.value, fontWeight = FontWeight.Light, color = MyStyleKt.TextColor.getHighlighting())
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        try {
            focusRequest.requestFocus()
        }catch (e:Exception) {
            MyLog.d(TAG, "request focus failed: ${e.stackTraceToString()}")
        }
    }
}
