package com.akcreation.gitsilent.notification.base

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.notification.util.NotifyUtil

open class ServiceNotify(val notify: NotifyBase) {
    private fun sendNotification(title:String, msg:String, startPage:Int, startRepoId:String) {
        NotifyUtil.sendNotificationClickGoToSpecifiedPage(notify, title, msg, startPage, startRepoId)
    }
    fun sendErrNotification(title:String, msg:String, startPage:Int, startRepoId:String) {
        sendNotification(title, msg, startPage, startRepoId)
    }
    fun sendSuccessNotification(title:String?, msg:String?, startPage:Int?, startRepoId:String?) {
        sendNotification(title ?: "PuppyGit", msg ?: "Success", startPage ?: Cons.selectedItem_Never, startRepoId ?: "")
    }
    fun sendProgressNotification(repoNameOrId:String, progress:String) {
        sendNotification(repoNameOrId, progress, Cons.selectedItem_Never, "")
    }
}
