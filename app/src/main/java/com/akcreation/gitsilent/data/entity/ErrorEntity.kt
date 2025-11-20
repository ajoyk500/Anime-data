package com.akcreation.gitsilent.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.common.BaseFields
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.getShortUUID

@Entity(tableName = "error")
data class ErrorEntity (
        @PrimaryKey
        var id: String= getShortUUID(),
        var date: String="",
        var msg: String="",
        var repoId:String="",
        var isChecked:Int= Cons.dbCommonFalse,

        @Embedded
        var baseFields: BaseFields = BaseFields(),

) {
        @Ignore
        private var cached_OneLineMsg:String? = null
        fun getCachedOneLineMsg(): String = (cached_OneLineMsg ?: Libgit2Helper.zipOneLineMsg(msg).let { cached_OneLineMsg = it; it });

}
