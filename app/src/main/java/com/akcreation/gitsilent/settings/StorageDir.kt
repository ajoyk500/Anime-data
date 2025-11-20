package com.akcreation.gitsilent.settings

import com.akcreation.gitsilent.constants.StorageDirCons
import kotlinx.serialization.Serializable

@Serializable
data class StorageDir (
    var defaultStorageDirId:String = StorageDirCons.DefaultStorageDir.puppyGitRepos.id
)
