package com.akcreation.gitsilent.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akcreation.gitsilent.data.entity.common.BaseFields
import com.akcreation.gitsilent.utils.getShortUUID

@Entity(tableName = "domain_credential")
data class DomainCredentialEntity (
        @PrimaryKey
        var id: String= getShortUUID(),

        var domain:String="",
        var credentialId:String="",  // credential id used to http/https url
        var sshCredentialId:String="",  // credential id used to ssh url


        @Embedded
        var baseFields: BaseFields = BaseFields(),
)
