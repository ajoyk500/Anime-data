package com.akcreation.gitsilent.git

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.dto.ItemKey
import com.akcreation.gitsilent.utils.getHumanReadableSizeStr
import com.akcreation.gitsilent.utils.getParentPathEndsWithSeparator
import com.akcreation.gitsilent.utils.mime.MimeType
import com.akcreation.gitsilent.utils.mime.guessFromFileName
import java.io.File

class StatusTypeEntrySaver(): ItemKey {
    var repoIdFromDb:String="";
    var fileName:String="";
    var relativePathUnderRepo:String="";  
    var fileParentPathOfRelativePath:String="";  
    var repoWorkDirPath:String="";
    var changeType:String?=null;
    var canonicalPath:String="";
    var fileSizeInBytes:Long=0;
    var itemType:Int = Cons.gitItemTypeFile;
    var dirty:Boolean = false  
    private var mime:MimeType? = null
    private var changeTypeAndSuffix:String? = null
    private var itemTypeString:String? = null
    fun getMime():MimeType {
        if(mime==null) {
            mime = MimeType.guessFromFileName(fileName)  
        }
        return mime!!
    }
    fun getSizeStr():String {
        return getHumanReadableSizeStr(fileSizeInBytes)
    }
    fun getChangeTypeAndSuffix(isDiffToLocal:Boolean):String {
        if(changeTypeAndSuffix==null) {
            val item = this
            changeTypeAndSuffix = ((item.changeType?:"") + (if(item.itemType==Cons.gitItemTypeSubmodule) ", ${Cons.gitItemTypeSubmoduleStr+(if(isDiffToLocal && item.dirty) ", ${Cons.gitSubmoduleDirtyStr}" else "")}" else ""))
        }
        return changeTypeAndSuffix ?: ""
    }
    fun getChangeListItemSecondLineText(isDiffToLocal:Boolean):String {
        return getSizeStr() + ", " + getChangeTypeAndSuffix(isDiffToLocal)
    }
    fun getItemTypeString():String {
        if(itemTypeString==null) {
            itemTypeString = if(itemType == Cons.gitItemTypeDir) {
                Cons.gitItemTypeDirStr
            }else if(itemType == Cons.gitItemTypeFile) {
                Cons.gitItemTypeFileStr
            }else if(itemType == Cons.gitItemTypeSubmodule) {
                Cons.gitItemTypeSubmoduleStr
            }else {
                ""
            }
        }
        return itemTypeString ?: ""
    }
    fun getParentDirStr():String{
        return getParentPathEndsWithSeparator(relativePathUnderRepo, trueWhenNoParentReturnEmpty = true)
    }
    fun toFile(): File {
        return File(canonicalPath)
    }
    fun toDiffableItem():DiffableItem {
        return DiffableItem(
            repoIdFromDb = repoIdFromDb,
            relativePath = relativePathUnderRepo,
            itemType = itemType,
            changeType = changeType?:"",
            isChangeListItem = true,
            isFileHistoryItem = false,
            entryId = "",
            commitId = "",
            sizeInBytes = fileSizeInBytes,
            shortCommitId = "",
            repoWorkDirPath = repoWorkDirPath,
            fileName = fileName,
            fullPath = canonicalPath,
            fileParentPathOfRelativePath = fileParentPathOfRelativePath,
        )
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as StatusTypeEntrySaver
        if (repoIdFromDb != other.repoIdFromDb) return false
        if (fileName != other.fileName) return false
        if (relativePathUnderRepo != other.relativePathUnderRepo) return false
        if (changeType != other.changeType) return false
        if (canonicalPath != other.canonicalPath) return false
        if (fileSizeInBytes != other.fileSizeInBytes) return false
        if (itemType != other.itemType) return false
        if (dirty != other.dirty) return false
        return true
    }
    override fun hashCode(): Int {
        var result = repoIdFromDb.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + relativePathUnderRepo.hashCode()
        result = 31 * result + (changeType?.hashCode() ?: 0)
        result = 31 * result + canonicalPath.hashCode()
        result = 31 * result + fileSizeInBytes.hashCode()
        result = 31 * result + dirty.hashCode()
        result = 31 * result + itemType
        return result
    }
    override fun getItemKey():String {
        return generateItemKey(repoIdFromDb, relativePathUnderRepo, changeType, itemType)
    }
    fun maybeIsFileAndExist():Boolean {
        return itemType == Cons.gitItemTypeFile || (itemType != Cons.gitItemTypeDir && itemType != Cons.gitItemTypeSubmodule)
    }
    fun maybeIsDirAndExist():Boolean {
        return itemType == Cons.gitItemTypeDir || itemType == Cons.gitItemTypeSubmodule || itemType != Cons.gitItemTypeFile
    }
    companion object {
        fun generateItemKey(repoId:String, relativePath:String, changeType:String?, itemType:Int):String {
            return repoId+ relativePath+changeType+itemType
        }
    }
}
