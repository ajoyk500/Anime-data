package com.akcreation.gitsilent.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Deprecated("[CHINESE]")
@Parcelize
class TmpStatus(var beforeStatus: String = "",
                var curStatus: String = "",
                var nextStatus: String = ""
) : Parcelable {
}
