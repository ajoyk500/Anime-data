package com.akcreation.gitsilent.msg

import androidx.room.concurrent.AtomicBoolean
import com.akcreation.gitsilent.utils.Msg

// if need more other class, create an interface
class OneTimeToast {
    private val showed = AtomicBoolean(false)

    fun show(msg:String) {
        if(showed.compareAndSet(false, true)) {
            Msg.requireShowLongDuration(msg)
        }
    }

    fun reset() {
        showed.set(false)
    }
}
