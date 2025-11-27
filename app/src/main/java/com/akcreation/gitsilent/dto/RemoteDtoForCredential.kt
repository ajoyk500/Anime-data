package com.akcreation.gitsilent.dto

import android.content.Context
import androidx.room.Ignore
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.AppModel

class RemoteDtoForCredential (
    var remoteId: String="",
    var remoteName:String="",
    var repoId:String="",
    var repoName:String="",
    var credentialId:String?="",  
    var credentialName:String?="",
    var credentialType:Int= Cons.dbCredentialTypeHttp,
    var pushCredentialId:String?="",
    var pushCredentialName:String?="",
    var pushCredentialType:Int= Cons.dbCredentialTypeHttp,
) {
    @Ignore
    var remoteFetchUrl:String=""
    @Ignore
    var remotePushUrl:String=""
    fun getCredentialNameOrNone():String {
        return getFetchOrPushCredentialNameOrNone(isFetch = true)
    }
    fun getPushCredentialNameOrNone():String {
        return getFetchOrPushCredentialNameOrNone(isFetch = false)
    }
    private fun getFetchOrPushCredentialNameOrNone(isFetch:Boolean):String {
        return getCredentialName(isFetch, credentialId, credentialName, pushCredentialId, pushCredentialName)
    }
}
