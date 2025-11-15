package com.akcreation.gitsilent.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

class RepoManager {
    
    data class RepoStatus(
        val currentBranch: String,
        val added: List<String>,
        val modified: List<String>,
        val removed: List<String>,
        val untracked: List<String>,
        val hasChanges: Boolean
    )
    
    data class BranchInfo(
        val name: String,
        val isActive: Boolean,
        val isRemote: Boolean
    )
    
    fun getRepoStatus(repoPath: String): RepoStatus? {
        return try {
            openRepository(repoPath)?.use { git ->
                val status = git.status().call()
                val branch = git.repository.branch ?: "master"
                
                RepoStatus(
                    currentBranch = branch,
                    added = status.added.toList(),
                    modified = status.modified.toList(),
                    removed = status.removed.toList(),
                    untracked = status.untracked.toList(),
                    hasChanges = !status.isClean
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun getBranches(repoPath: String): List<BranchInfo> {
        return try {
            openRepository(repoPath)?.use { git ->
                val currentBranch = git.repository.branch
                val branches = mutableListOf<BranchInfo>()
                
                // Local branches
                git.branchList().call().forEach { ref ->
                    val name = ref.name.removePrefix("refs/heads/")
                    branches.add(
                        BranchInfo(
                            name = name,
                            isActive = name == currentBranch,
                            isRemote = false
                        )
                    )
                }
                
                // Remote branches
                git.branchList()
                    .setListMode(org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE)
                    .call()
                    .forEach { ref ->
                        val name = ref.name.removePrefix("refs/remotes/")
                        branches.add(
                            BranchInfo(
                                name = name,
                                isActive = false,
                                isRemote = true
                            )
                        )
                    }
                
                branches
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun switchBranch(repoPath: String, branchName: String): Boolean {
        return try {
            openRepository(repoPath)?.use { git ->
                git.checkout()
                    .setName(branchName)
                    .call()
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    fun createBranch(repoPath: String, branchName: String): Boolean {
        return try {
            openRepository(repoPath)?.use { git ->
                git.branchCreate()
                    .setName(branchName)
                    .call()
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    fun getLastCommitInfo(repoPath: String): CommitSummary? {
        return try {
            openRepository(repoPath)?.use { git ->
                val commit = git.log().setMaxCount(1).call().firstOrNull()
                commit?.let {
                    CommitSummary(
                        message = it.shortMessage,
                        author = it.authorIdent.name,
                        date = it.commitTime.toLong() * 1000
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun getFilesInRepo(repoPath: String, path: String = ""): List<FileItem> {
        val files = mutableListOf<FileItem>()
        val directory = if (path.isEmpty()) {
            File(repoPath)
        } else {
            File(repoPath, path)
        }
        
        directory.listFiles()?.forEach { file ->
            if (file.name != ".git") {
                files.add(
                    FileItem(
                        name = file.name,
                        path = file.absolutePath.removePrefix("$repoPath/"),
                        isDirectory = file.isDirectory,
                        size = if (file.isFile) file.length() else 0
                    )
                )
            }
        }
        
        return files.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
    }
    
    private fun openRepository(repoPath: String): Git? {
        return try {
            val builder = FileRepositoryBuilder()
            val repository = builder
                .setGitDir(File(repoPath, ".git"))
                .readEnvironment()
                .findGitDir()
                .build()
            Git(repository)
        } catch (e: Exception) {
            null
        }
    }
    
    data class CommitSummary(
        val message: String,
        val author: String,
        val date: Long
    )
    
    data class FileItem(
        val name: String,
        val path: String,
        val isDirectory: Boolean,
        val size: Long
    )
}