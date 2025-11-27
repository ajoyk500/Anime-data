package com.akcreation.gitsilent.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.StorageDirCons
import com.akcreation.gitsilent.data.entity.common.BaseFields
import com.akcreation.gitsilent.utils.getShortUUID

@Entity(tableName = "storageDir")
data class StorageDirEntity(
    @PrimaryKey
    var id: String= getShortUUID(),  
    var name:String="",  
    var fullPath:String="",  
    var type:Int=StorageDirCons.Type.internal,
    var allowDel:Int= Cons.dbCommonTrue,  
    var parentId:String=StorageDirCons.DefaultStorageDir.rootDir.id, 
    var virtualPath:String="",  
    @Embedded
    var baseFields: BaseFields = BaseFields(
        baseStatus = StorageDirCons.Status.ok
    ),
)
