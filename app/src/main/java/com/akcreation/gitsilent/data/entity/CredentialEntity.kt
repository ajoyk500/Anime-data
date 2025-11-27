package com.akcreation.gitsilent.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.common.BaseFields
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.encrypt.PassEncryptHelper
import com.akcreation.gitsilent.utils.getShortUUID

@Entity(tableName = "credential")
data class CredentialEntity(
    @PrimaryKey
    var id: String = getShortUUID(),
    var name:String = "",  
    var value: String = "",  
    var pass: String = "",  
    @Deprecated("planning to deprecate type of credential, in future credential will no type defined in itself, it will defined by it's proposal, e.g. if you use same credential to http and ssh respectively, it's value and pass will treat as username+password and privateKey+passphrase respectively, and which type of credential will be create by libgit2, it's will depending by type of git url")
    var type: Int= Cons.dbCredentialTypeHttp,  
    var encryptVer:Int = PassEncryptHelper.passEncryptCurrentVer,
    @Embedded
    var baseFields: BaseFields = BaseFields(),
){
    fun maybeIsValid() = value.isNotEmpty() || pass.isNotEmpty();
}
