package com.akcreation.gitsilent.notification

import com.akcreation.gitsilent.notification.base.NotifyBase

class AutomationNotify private constructor(
    override val notifyId:Int
): NotifyBase(
    TAG = "AutomationNotify",
    channelId="automation_notify",
    channelName = "Automation",
    channelDesc = "Show Automation Notifications",
) {
    companion object {
        fun create(notifyId:Int):NotifyBase {
            return AutomationNotify(notifyId)
        }
    }
}
