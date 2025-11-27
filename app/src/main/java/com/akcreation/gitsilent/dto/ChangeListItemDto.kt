package com.akcreation.gitsilent.dto


@Deprecated("[CHINESE]，[CHINESE] StatusTypeEntrySaver [CHINESE]")
class ChangeListItemDto {
    var fileFullPath:String = ""
    var fileName:String = ""
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ChangeListItemDto
        if (fileFullPath != other.fileFullPath) return false
        return true
    }
    override fun hashCode(): Int {
        return fileFullPath.hashCode()
    }
}