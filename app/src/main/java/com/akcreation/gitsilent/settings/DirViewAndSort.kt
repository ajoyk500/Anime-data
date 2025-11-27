package com.akcreation.gitsilent.settings

import com.akcreation.gitsilent.settings.enums.dirviewandsort.ViewType
import com.akcreation.gitsilent.settings.enums.dirviewandsort.SortMethod
import kotlinx.serialization.Serializable

@Serializable
data class DirViewAndSort (
    var viewType:Int = ViewType.LIST.code,
    var sortMethod:Int = SortMethod.NAME.code,
    var ascend:Boolean=true,
    var folderFirst:Boolean = true,
)

