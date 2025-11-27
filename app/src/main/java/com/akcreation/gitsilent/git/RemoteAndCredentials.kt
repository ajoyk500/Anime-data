package com.akcreation.gitsilent.git

import com.akcreation.gitsilent.data.entity.CredentialEntity

class RemoteAndCredentials (
    var remoteName:String="",
    var fetchCredential:CredentialEntity? =null,  //query by remote table `credentialId`
    var pushCredential:CredentialEntity? =null,
    var forcePush:Boolean = false,
//    var fetchUrl:String="",
//    var pushUrl:String="",
//    var fetchCredentialType:Int = Cons.dbCredentialTypeHttp,  //Credential对象里本身就有type，所以这个变量无意义
//    var pushCredentialType:Int = Cons.dbCredentialTypeHttp,  //理由同上
)
