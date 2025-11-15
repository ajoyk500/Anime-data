package com.akcreation.gitsilent.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class GitHelper {
    
    data class CloneResult(val success: Boolean, val message: String, val repoPath: String = "")
    data class PullResult(val success: Boolean, val message: String)
    data class PushResult(val success: Boolean, val message: String)
    data class CommitResult(val success: Boolean, val message: String)
    
    data class StageResult(val success: Boolean, val message: String, val stagedFiles: List<String> = emptyList())
    
    /**
     * Stage all changes (git add .)
     */
    suspend fun stageAllChanges(repoPath: String): StageResult {
        return try {
            openRepository(repoPath)?.use { git ->
                // Add all changes
                git.add().addFilepattern(".").call()
                
                // Get status to see what was staged
                val status = git.status().call()
                val stagedFiles = mutableListOf<String>()
                stagedFiles.addAll(status.added)
                stagedFiles.addAll(status.changed)
                stagedFiles.addAll(status.removed)
                
                StageResult(
                    success = true,
                    message = "Staged ${stagedFiles.size} files",
                    stagedFiles = stagedFiles
                )
            } ?: StageResult(false, "Failed to open repository")
        } catch (e: Exception) {
            StageResult(false, "Stage failed: ${e.message}")
        }
    }
    
    /**
     * Get list of staged files
     */
    suspend fun getStagedFiles(repoPath: String): List<String> {
        return try {
            openRepository(repoPath)?.use { git ->
                val status = git.status().call()
                val staged = mutableListOf<String>()
                staged.addAll(status.added)
                staged.addAll(status.changed)
                staged.addAll(status.removed)
                staged
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun cloneRepository(
        url: String,
        localPath: String,
        username: String = "",
        token: String = ""
    ): CloneResult {
        return try {
            val directory = File(localPath)
            if (directory.exists()) {
                directory.deleteRecursively()
            }
            directory.mkdirs()
            
            val cloneCommand = Git.cloneRepository()
                .setURI(url)
                .setDirectory(directory)
                .setCloneAllBranches(true)
            
            if (username.isNotEmpty() && token.isNotEmpty()) {
                cloneCommand.setCredentialsProvider(
                    UsernamePasswordCredentialsProvider(username, token)
                )
            }
            
            cloneCommand.call().use {
                CloneResult(true, "Repository cloned successfully", localPath)
            }
        } catch (e: Exception) {
            CloneResult(false, "Clone failed: ${e.message}")
        }
    }
    
    suspend fun pullRepository(
        repoPath: String,
        username: String = "",
        token: String = ""
    ): PullResult {
        return try {
            openRepository(repoPath)?.use { git ->
                val pullCommand = git.pull()
                
                if (username.isNotEmpty() && token.isNotEmpty()) {
                    pullCommand.setCredentialsProvider(
                        UsernamePasswordCredentialsProvider(username, token)
                    )
                }
                
                val result = pullCommand.call()
                
                if (result.isSuccessful) {
                    PullResult(true, "Pull successful")
                } else {
                    PullResult(false, "Pull failed: ${result.mergeResult?.mergeStatus}")
                }
            } ?: PullResult(false, "Failed to open repository")
        } catch (e: Exception) {
            PullResult(false, "Pull failed: ${e.message}")
        }
    }
    
    suspend fun pushRepository(
        repoPath: String,
        username: String = "",
        token: String = ""
    ): PushResult {
        return try {
            openRepository(repoPath)?.use { git ->
                val pushCommand = git.push()
                
                if (username.isNotEmpty() && token.isNotEmpty()) {
                    pushCommand.setCredentialsProvider(
                        UsernamePasswordCredentialsProvider(username, token)
                    )
                }
                
                val results = pushCommand.call()
                
                if (results.any { it.messages.contains("rejected") }) {
                    PushResult(false, "Push rejected")
                } else {
                    PushResult(true, "Push successful")
                }
            } ?: PushResult(false, "Failed to open repository")
        } catch (e: Exception) {
            PushResult(false, "Push failed: ${e.message}")
        }
    }
    
    suspend fun commitChanges(
        repoPath: String,
        message: String,
        author: String,
        email: String
    ): CommitResult {
        return try {
            openRepository(repoPath)?.use { git ->
                // Add all changes
                git.add().addFilepattern(".").call()
                
                // Commit
                git.commit()
                    .setMessage(message)
                    .setAuthor(author, email)
                    .call()
                
                CommitResult(true, "Changes committed successfully")
            } ?: CommitResult(false, "Failed to open repository")
        } catch (e: Exception) {
            CommitResult(false, "Commit failed: ${e.message}")
        }
    }
    
    suspend fun getCommitHistory(repoPath: String, limit: Int = 50): List<CommitInfo> {
        return try {
            openRepository(repoPath)?.use { git ->
                git.log()
                    .setMaxCount(limit)
                    .call()
                    .map { commit ->
                        CommitInfo(
                            hash = commit.name,
                            message = commit.fullMessage,
                            author = commit.authorIdent.name,
                            email = commit.authorIdent.emailAddress,
                            date = commit.commitTime.toLong() * 1000,
                            shortHash = commit.name.substring(0, 7)
                        )
                    }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
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
    
    data class CommitInfo(
        val hash: String,
        val shortHash: String,
        val message: String,
        val author: String,
        val email: String,
        val date: Long
    )
}