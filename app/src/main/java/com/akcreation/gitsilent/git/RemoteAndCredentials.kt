package com.akcreation.gitsilent.git

import com.akcreation.gitsilent.data.entity.CredentialEntity

class RemoteAndCredentials (
    var remoteName:String="",
    var fetchCredential:CredentialEntity? =null,  
    var pushCredential:CredentialEntity? =null,
    var forcePush:Boolean = false,
)
