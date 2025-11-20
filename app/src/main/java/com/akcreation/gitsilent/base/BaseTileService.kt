package com.akcreation.gitsilent.base

import android.content.Context
import android.service.quicksettings.TileService
import com.akcreation.gitsilent.utils.ContextUtil

open class BaseTileService : TileService() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextUtil.getLocalizedContext(newBase))
    }
}
