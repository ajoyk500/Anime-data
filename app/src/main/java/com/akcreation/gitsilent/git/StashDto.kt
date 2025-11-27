package com.akcreation.gitsilent.git

import com.akcreation.gitsilent.dto.ItemKey
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.github.git24j.core.Oid

data class StashDto(
    var index: Int = -1,
    var msg: String = "",
    var stashId: Oid? = null
) : ItemKey {

    private var shortStashId:String?=null

    fun getCachedShortStashId():String {
        if(shortStashId == null) {
            shortStashId = Libgit2Helper.getShortOidStrByFull(stashId.toString())
        }

        return shortStashId ?: stashId.toString()
    }

    private var cached_OneLineMsg:String? = null
    fun getCachedOneLineMsg(): String = (cached_OneLineMsg ?: Libgit2Helper.zipOneLineMsg(msg).let { cached_OneLineMsg = it; it });


    override fun getItemKey():String {
        return "$index: $stashId"
    }
}
