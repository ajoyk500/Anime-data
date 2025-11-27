package com.akcreation.gitsilent.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.common.BaseFields
import com.akcreation.gitsilent.utils.getShortUUID

@Entity(tableName = "remote")
data class RemoteEntity(
    @PrimaryKey
    var id: String = getShortUUID(),
    var remoteName: String = "",
    var remoteUrl: String = "",  
    var isForPull:Int= Cons.dbCommonFalse,
    var isForPush:Int=Cons.dbCommonFalse,
    var credentialId:String="",
    var repoId:String="",
    @Deprecated("[CHINESE]git[CHINESE]fetch refspecs[CHINESE]，[CHINESE]，[CHINESE]：Libgit2Helper.getRemoteFetchBranchList(remote)")
    var fetchMode:Int=Cons.dbRemote_Fetch_BranchMode_All,
    @Deprecated("[CHINESE]git[CHINESE]，[CHINESE]all[CHINESE]custom[CHINESE]，[CHINESE]clone[CHINESE]，[CHINESE]custom[CHINESE]")
    var singleBranch:String="",
    @Deprecated("[CHINESE]git[CHINESE]")
    var customBranches:String="",  
    var pushUrl:String="",  
    var pushCredentialId:String="",  
    @Embedded
    var baseFields: BaseFields = BaseFields(),
){
    @Ignore
    var pushUrlTrackFetchUrl:Boolean = false
}
