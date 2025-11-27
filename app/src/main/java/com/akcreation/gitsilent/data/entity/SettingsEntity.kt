package com.akcreation.gitsilent.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akcreation.gitsilent.data.entity.common.BaseFields

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    var usedFor: Int,  
    var jsonVal:String="",
    @Embedded
    var baseFields: BaseFields = BaseFields(),
)
