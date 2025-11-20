package com.akcreation.gitsilent.server.bean

class NotificationSender (
    val sendErrNotification:((title:String, msg:String, startPage:Int, startRepoId:String)->Unit)?=null,
    val sendSuccessNotification:((title:String?, msg:String?, startPage:Int?, startRepoId:String?)->Unit)? = null,
    val sendProgressNotification:((repoNameOrId:String, progress:String)->Unit)?=null,
)

