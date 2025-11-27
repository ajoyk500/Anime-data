package com.akcreation.gitsilent.base


import android.accessibilityservice.AccessibilityService
import android.content.Context
import com.akcreation.gitsilent.utils.ContextUtil

abstract class BaseAccessibilityService : AccessibilityService() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextUtil.getLocalizedContext(newBase))
    }
}
