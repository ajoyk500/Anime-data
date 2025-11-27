package com.akcreation.gitsilent.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akcreation.gitsilent.utils.encrypt.PassEncryptHelper

@Deprecated("[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]")
@Entity(tableName = "passEncrypt")
data class PassEncryptEntity (
    @PrimaryKey
    var id: Int= 1,
    var ver: Int= PassEncryptHelper.passEncryptCurrentVer,
    var reserve1:String="",  
    var reserve2:String="",  
)
