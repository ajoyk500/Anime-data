package com.akcreation.gitsilent.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val path: String,
    val url: String,
    val currentBranch: String,
    val lastCommitMessage: String = "",
    val lastCommitAuthor: String = "",
    val lastCommitDate: Long = 0L,
    val lastSyncTime: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)