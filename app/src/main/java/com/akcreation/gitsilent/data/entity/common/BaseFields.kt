package com.akcreation.gitsilent.data.entity.common

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.utils.getSecFromTime

data class BaseFields (
    var baseStatus:Int=Cons.dbCommonBaseStatusOk,  
    var baseCreateTime:Long=getSecFromTime(),
    var baseUpdateTime:Long=getSecFromTime(),
    var baseIsDel:Int=Cons.dbCommonFalse,
)
