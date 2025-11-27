package com.akcreation.gitsilent.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.base.BaseComposeActivity
import com.akcreation.gitsilent.compose.CopyableDialog2
import com.akcreation.gitsilent.compose.LoadingText
import com.akcreation.gitsilent.compose.SshUnknownHostDialog
import com.akcreation.gitsilent.jni.SshAskUserUnknownHostRequest
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.AppScreenNavigator
import com.akcreation.gitsilent.screen.RequireMasterPasswordScreen
import com.akcreation.gitsilent.screen.functions.KnownHostRequestStateMan
import com.akcreation.gitsilent.screen.shared.IntentHandler
import com.akcreation.gitsilent.screen.shared.MainActivityLifeCycle
import com.akcreation.gitsilent.screen.shared.setByPredicate
import com.akcreation.gitsilent.screen.shared.setMainActivityLifeCycle
import com.akcreation.gitsilent.ui.theme.InitContent
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Lg2HomeUtils
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

private const val TAG = "MainActivity"
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
class MainActivity : BaseComposeActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val funName = "onCreate"
        MyLog.d(TAG, "#onCreate called")
        init(TAG)
        setMainActivityLifeCycle(MainActivityLifeCycle.ON_CREATE)
        IntentHandler.setNewIntent(intent)
        setContent {
            InitContent(applicationContext) {
                MainCompose()
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        MyLog.d(TAG, "#onNewIntent() called")
        super.onNewIntent(intent)
        if(IntentHandler.needConsume(intent)) {
            setMainActivityLifeCycle(MainActivityLifeCycle.IGNORE_ONCE_ON_RESUME)
            MyLog.d(TAG, "has new intent need consume, will cancel ON_RESUME event once for Editor")
        }
        IntentHandler.setNewIntent(intent)
    }
    override fun onPause() {
        MyLog.d(TAG, "#onPause: called")
        super.onPause()
        setMainActivityLifeCycle(MainActivityLifeCycle.ON_PAUSE)
    }
    override fun onResume() {
        MyLog.d(TAG, "#onResume: called")
        super.onResume()
        setByPredicate(MainActivityLifeCycle.ON_RESUME) {
            it != MainActivityLifeCycle.ON_CREATE && it != MainActivityLifeCycle.IGNORE_ONCE_ON_RESUME
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AppModel.handleActivityConfigurationChanged(newConfig)
    }
}
@Composable
private fun MainCompose() {
    val stateKeyTag = remember { "MainCompose" }
    val funName = remember { "MainCompose" }
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current
    val loadingText = rememberSaveable { mutableStateOf(activityContext.getString(R.string.launching))}
    val sshCertRequestListenerChannel = remember { Channel<Int>() }
    val isInitDone = rememberSaveable { mutableStateOf(false) };
    val requireMasterPassword = rememberSaveable { mutableStateOf(false) };
    val isProState = rememberSaveable { mutableStateOf(false) }
    UserUtil.updateUserStateToRememberXXXForPage(newIsProState = isProState)
    val sshAskUserUnknownHostRequestList = mutableCustomStateListOf(stateKeyTag, "sshAskUserUnknownHostRequestList", listOf<SshAskUserUnknownHostRequest>())
    val currentSshAskUserUnknownHostRequest = mutableCustomStateOf<SshAskUserUnknownHostRequest?>(stateKeyTag, "currentSshAskUserUnknownHostRequest", null)
    val iTrustTheHost = rememberSaveable { mutableStateOf(false) }
    val showSshDialog = rememberSaveable { mutableStateOf(false) }
    val closeSshDialog ={
        showSshDialog.value=false
    }
    val allowOrRejectSshDialogCallback={
        currentSshAskUserUnknownHostRequest.value = null
        iTrustTheHost.value = false
    }
    if(showSshDialog.value) {
        SshUnknownHostDialog(
            currentSshAskUserUnknownHostRequest = currentSshAskUserUnknownHostRequest,
            iTrustTheHost = iTrustTheHost,
            closeSshDialog = closeSshDialog,
            allowOrRejectSshDialogCallback = allowOrRejectSshDialogCallback,
            appContext = activityContext
        )
    }
    val showRandomLoadingTextDialog = rememberSaveable { mutableStateOf(false) }
    if(showRandomLoadingTextDialog.value) {
        CopyableDialog2(
            requireShowTextCompose = true,
            textCompose = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(text = loadingText.value, fontSize = 20.sp)
                }
            },
            onCancel = { showRandomLoadingTextDialog.value = false }
        ) {
            showRandomLoadingTextDialog.value=false
            clipboardManager.setText(AnnotatedString(loadingText.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }
    if(isInitDone.value) {
        if(requireMasterPassword.value) {
            RequireMasterPasswordScreen(requireMasterPassword)
        }else {
            AppScreenNavigator()
        }
    }else {
        Scaffold { contentPadding ->
            LoadingText(
                contentPadding = contentPadding,
                text = {
                    Text(
                        text = loadingText.value,
                        modifier = Modifier.clickable { showRandomLoadingTextDialog.value = true },
                    )
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        try {
            doJobThenOffLoading {
                isInitDone.value = false
                AppModel.init_2()
                requireMasterPassword.value = AppModel.requireMasterPassword()
                if(requireMasterPassword.value.not()) {
                    AppModel.dbContainer.credentialRepository.migrateEncryptVerIfNeed(AppModel.masterPassword.value)
                }
                isInitDone.value = true
            }
            KnownHostRequestStateMan.init(sshAskUserUnknownHostRequestList.value)
            doJobThenOffLoading {
                while (true) {
                    if(sshCertRequestListenerChannel.tryReceive().isClosed) {
                        break
                    }
                    if(showSshDialog.value.not() && currentSshAskUserUnknownHostRequest.value==null) {
                        val item = KnownHostRequestStateMan.getFirstThenRemove()
                        if(item != null) {
                            if(Lg2HomeUtils.itemInUserKnownHostsFile(item.sshCert).not()) {
                                currentSshAskUserUnknownHostRequest.value = item
                                iTrustTheHost.value = false
                                showSshDialog.value = true
                            }
                        }
                    }
                    delay(1000)
                }
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#$funName err: "+e.stackTraceToString())
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            sshCertRequestListenerChannel.close()
        }
    }
}
fun startMainActivity(fromActivity: Activity) {
    val intent = Intent(ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        setClass(fromActivity, MainActivity::class.java)
    }
    fromActivity.startActivity(intent)
}
