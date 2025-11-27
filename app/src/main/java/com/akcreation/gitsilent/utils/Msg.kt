package com.akcreation.gitsilent.utils

import android.widget.Toast

class Msg {
    companion object {
        val requireShow = { msg:String ->
            doJobWithMainContext {
                showToast(AppModel.realAppContext, msg)
            }
        }
        val requireShowShortDuration = { msg:String ->
            doJobWithMainContext {
                showToast(AppModel.realAppContext, msg, Toast.LENGTH_SHORT)
            }
        }
        val requireShowLongDuration = { msg:String ->
            doJobWithMainContext {
                showToast(AppModel.realAppContext, msg, Toast.LENGTH_LONG)
            }
        }
    }
}
