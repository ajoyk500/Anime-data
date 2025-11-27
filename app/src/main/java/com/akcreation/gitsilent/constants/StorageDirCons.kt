package com.akcreation.gitsilent.constants

import com.akcreation.gitsilent.data.entity.StorageDirEntity
import com.akcreation.gitsilent.data.entity.common.BaseFields

object StorageDirCons {
    object Type {
        val root=0  
        val programData=1  
        val internal=2    
        val external=3    
        val other=4
    }
    object Status {
        val ok = Cons.dbCommonBaseStatusOk 
        val disable=2  
        val fakeDel=3  
        val err_dirNonExists=Cons.dbCommonBaseStatusErr+2  
        val err_cantAccess=Cons.dbCommonBaseStatusErr+3  
    }
    private const val separator = "/"
    object DefaultStorageDir {
        val rootDir = StorageDirEntity(
            id="e526cc40419d43d59e466c",  
            name="",  
            fullPath = separator, 
            virtualPath = separator,
            type = Type.root,  
            allowDel = Cons.dbCommonFalse,  
            parentId = "",  
            baseFields = BaseFields(
                baseStatus = Status.ok,
                baseIsDel = Cons.dbCommonFalse,
                baseCreateTime = 0,  
                baseUpdateTime = 0,
            )
        )
        const val puppyGitDirName = "PuppyGitData"
        @Deprecated("[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]puppyGitData[CHINESE] puppyGitRepos[CHINESE]，[CHINESE]StorageDir，[CHINESE]")
        val puppyGitDataDir = StorageDirEntity(
            id="d8b39e2837a84a518dc818",  
            name=puppyGitDirName,
            fullPath = "", 
            virtualPath = "$separator$puppyGitDirName",
            type = Type.programData,  
            allowDel = Cons.dbCommonFalse,
            parentId = rootDir.id,
            baseFields = BaseFields(
                baseStatus = Status.fakeDel,  
                baseIsDel = Cons.dbCommonTrue,  
                baseCreateTime = 1, 
                baseUpdateTime = 1,
            )
        )
        const val puppyGitReposDirName = Cons.defaultAllRepoParentDirName
        val puppyGitRepos = StorageDirEntity(
            id="d6c5b79ca14e42fb811fcc",  
            name=puppyGitReposDirName,
            fullPath = "", 
            virtualPath = "$separator$puppyGitReposDirName",
            type = Type.internal,    
            allowDel = Cons.dbCommonFalse,  
            parentId = rootDir.id,
            baseFields = BaseFields(
                baseStatus = Status.ok,
                baseIsDel = Cons.dbCommonFalse,
                baseCreateTime = 2,  
                baseUpdateTime = 2,
            )
        )
        val otherDirName = "OtherRepos"
        val otherRepos = StorageDirEntity(
            id="3a5a4f7a3e7447fc96a395",  
            name=otherDirName,
            fullPath = "$separator$otherDirName",
            virtualPath = "$separator$otherDirName",
            type = Type.other,  
            allowDel = Cons.dbCommonFalse,  
            parentId = rootDir.id,
            baseFields = BaseFields(
                baseStatus = Status.ok,
                baseIsDel = Cons.dbCommonFalse,
                baseCreateTime = 3,  
                baseUpdateTime = 3,
            )
        )
        val listForMatchPath = listOf(puppyGitDataDir, puppyGitRepos)
        val idListUnusedForMatchPath = listOf(rootDir.id, otherRepos.id)
        val listOfAllDefaultSds = listOf(rootDir,puppyGitDataDir, puppyGitRepos, otherRepos)
    }
}
