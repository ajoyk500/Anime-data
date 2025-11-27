package com.akcreation.gitsilent.base

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.platform.createLifecycleAwareWindowRecomposer
import androidx.core.view.WindowCompat
import com.akcreation.gitsilent.activity.CrashActivity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.ContextUtil
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.showToast
import kotlinx.coroutines.CoroutineExceptionHandler

open class BaseComposeActivity : ComponentActivity() {
    fun init(
        TAG: String,
        funName: String = "onCreate()",
        requireSetExceptionHandler:Boolean = true,
        requireEnableEdgeToEdge:Boolean = false,
        allowImePadding:Boolean = true,
    ) {
        if(requireEnableEdgeToEdge) {
            enableEdgeToEdge()
        }
        AppModel.init_1(realAppContext = applicationContext, exitApp = { finish() }, initActivity = true)
        if(requireSetExceptionHandler) {
            setExceptionHandler(TAG, funName)
        }
        if(allowImePadding) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextUtil.getLocalizedContext(newBase))
    }
    override fun onDestroy() {
        super.onDestroy()
        AppModel.destroyer()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AppModel.handleActivityConfigurationChanged(newConfig)
    }
    override fun onResume() {
        super.onResume()
        AppModel.updateExitApp { finish() }
    }
    protected fun setExceptionHandler(TAG: String, funName: String) {
        window.decorView.compositionContext = window.decorView.createLifecycleAwareWindowRecomposer(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                try {
                    val errMsg = throwable.stackTraceToString()
                    MyLog.e(TAG, "#$funName err: $errMsg")
                    showToast(applicationContext, getString(R.string.err_restart_app_may_resolve), Toast.LENGTH_LONG)  
                    CrashActivity.start(this, errMsg)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()  
                }
            }, lifecycle
        )
    }
}
