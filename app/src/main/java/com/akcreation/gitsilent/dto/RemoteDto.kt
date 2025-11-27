package com.akcreation.gitsilent.dto

import androidx.room.Ignore
import com.akcreation.gitsilent.constants.Cons

class RemoteDto {
    var remoteId=""
    var remoteName=""
    var remoteUrl=""
    var credentialId:String?=""  
    var credentialName:String?=""
    var credentialVal:String?=""
    var credentialPass:String?=""  
    var credentialType:Int = Cons.dbCredentialTypeHttp
    var pushUrl=""
    var pushCredentialId:String?=""   
    var pushCredentialName:String?=""
    var pushCredentialVal:String?=""
    var pushCredentialPass:String?=""  
    var pushCredentialType:Int = Cons.dbCredentialTypeHttp
    var repoId=""
    var repoName=""
    @Ignore
    var actuallyCredentialIdWhenCredentialIdIsMatchByDomain:String=""
    @Ignore
    var actuallyPushCredentialIdWhenCredentialIdIsMatchByDomain:String=""
    @Ignore
    var branchMode:Int= Cons.dbRemote_Fetch_BranchMode_All
    @Ignore
    var branchListForFetch:List<String> = listOf()
    @Ignore
    var pushUrlTrackFetchUrl:Boolean = false
    fun getLinkedFetchCredentialName():String {
        return getLinkedFetchOrPushCredentialName(true)
    }
    fun getLinkedPushCredentialName():String {
        return getLinkedFetchOrPushCredentialName(false)
    }
    private fun getLinkedFetchOrPushCredentialName(isFetch:Boolean):String {
        return getCredentialName(isFetch, credentialId, credentialName, pushCredentialId, pushCredentialName)
    }
}
