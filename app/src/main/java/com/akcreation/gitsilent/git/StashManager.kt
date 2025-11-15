package com.akcreation.gitsilent.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

/**
 * Version 1.2 Feature: Git Stash Support
 * Stash and restore work in progress
 */
class StashManager {
    
    data class StashEntry(
        val index: Int,
        val message: String,
        val branch: String,
        val timestamp: Long,
        val hash: String
    )
    
    data class StashResult(
        val success: Boolean,
        val message: String
    )
    
    /**
     * Stash current changes
     */
    suspend fun stashChanges(
        repoPath: String,
        message: String = "WIP on ${getCurrentBranch(repoPath)}",
        includeUntracked: Boolean = false
    ): StashResult {
        val repository = openRepository(repoPath) ?: return StashResult(
            success = false,
            message = "Failed to open repository"
        )
        
        val git = Git(repository)
        
        return try {
            val stashCommand = git.stashCreate()
                .setWorkingDirectoryMessage(message)
                .setIncludeUntracked(includeUntracked)
            
            val stashCommit = stashCommand.call()
            
            if (stashCommit != null) {
                StashResult(success = true, message = "Changes stashed successfully")
            } else {
                StashResult(success = false, message = "No changes to stash")
            }
        } catch (e: Exception) {
            StashResult(success = false, message = "Stash failed: ${e.message}")
        } finally {
            git.close()
        }
    }
    
    /**
     * List all stashes
     */
    fun listStashes(repoPath: String): List<StashEntry> {
        val repository = openRepository(repoPath) ?: return emptyList()
        val git = Git(repository)
        
        return try {
            val stashList = git.stashList().call()
            val entries = mutableListOf<StashEntry>()
            
            stashList.forEachIndexed { index, revCommit ->
                entries.add(
                    StashEntry(
                        index = index,
                        message = revCommit.fullMessage,
                        branch = extractBranchFromStash(revCommit.fullMessage),
                        timestamp = revCommit.commitTime.toLong() * 1000,
                        hash = revCommit.name
                    )
                )
            }
            
            entries
        } catch (e: Exception) {
            emptyList()
        } finally {
            git.close()
        }
    }
    
    /**
     * Apply stash (keep in stash list)
     */
    suspend fun applyStash(
        repoPath: String,
        stashIndex: Int = 0
    ): StashResult {
        val repository = openRepository(repoPath) ?: return StashResult(
            success = false,
            message = "Failed to open repository"
        )
        
        val git = Git(repository)
        
        return try {
            git.stashApply()
                .setStashRef("stash@{$stashIndex}")
                .call()
            
            StashResult(success = true, message = "Stash applied successfully")
        } catch (e: Exception) {
            StashResult(success = false, message = "Apply failed: ${e.message}")
        } finally {
            git.close()
        }
    }
    
    /**
     * Pop stash (apply and remove from list)
     */
    suspend fun popStash(
        repoPath: String,
        stashIndex: Int = 0
    ): StashResult {
        val repository = openRepository(repoPath) ?: return StashResult(
            success = false,
            message = "Failed to open repository"
        )
        
        val git = Git(repository)
        
        return try {
            // Apply the stash
            git.stashApply()
                .setStashRef("stash@{$stashIndex}")
                .call()
            
            // Drop the stash
            git.stashDrop()
                .setStashRef(stashIndex)
                .call()
            
            StashResult(success = true, message = "Stash popped successfully")
        } catch (e: Exception) {
            StashResult(success = false, message = "Pop failed: ${e.message}")
        } finally {
            git.close()
        }
    }
    
    /**
     * Drop/delete a stash
     */
    suspend fun dropStash(
        repoPath: String,
        stashIndex: Int
    ): StashResult {
        val repository = openRepository(repoPath) ?: return StashResult(
            success = false,
            message = "Failed to open repository"
        )
        
        val git = Git(repository)
        
        return try {
            git.stashDrop()
                .setStashRef(stashIndex)
                .call()
            
            StashResult(success = true, message = "Stash dropped")
        } catch (e: Exception) {
            StashResult(success = false, message = "Drop failed: ${e.message}")
        } finally {
            git.close()
        }
    }
    
    /**
     * Clear all stashes
     */
    suspend fun clearStashes(repoPath: String): StashResult {
        val repository = openRepository(repoPath) ?: return StashResult(
            success = false,
            message = "Failed to open repository"
        )
        
        val git = Git(repository)
        
        return try {
            // Get all stashes
            val stashList = git.stashList().call()
            
            // Drop each stash
            stashList.indices.forEach { _ ->
                git.stashDrop().setStashRef(0).call()
            }
            
            StashResult(success = true, message = "All stashes cleared")
        } catch (e: Exception) {
            StashResult(success = false, message = "Clear failed: ${e.message}")
        } finally {
            git.close()
        }
    }
    
    /**
     * Create branch from stash
     */
    suspend fun createBranchFromStash(
        repoPath: String,
        branchName: String,
        stashIndex: Int = 0
    ): StashResult {
        val repository = openRepository(repoPath) ?: return StashResult(
            success = false,
            message = "Failed to open repository"
        )
        
        val git = Git(repository)
        
        return try {
            // Get stash commit
            val stashList = git.stashList().call().toList()
            if (stashIndex >= stashList.size) {
                return StashResult(success = false, message = "Stash not found")
            }
            
            val stashCommit = stashList[stashIndex]
            
            // Create and checkout new branch
            git.checkout()
                .setCreateBranch(true)
                .setName(branchName)
                .setStartPoint(stashCommit.parents[0])
                .call()
            
            // Apply stash
            git.stashApply()
                .setStashRef("stash@{$stashIndex}")
                .call()
            
            // Drop the stash
            git.stashDrop()
                .setStashRef(stashIndex)
                .call()
            
            StashResult(success = true, message = "Branch created from stash")
        } catch (e: Exception) {
            StashResult(success = false, message = "Failed: ${e.message}")
        } finally {
            git.close()
        }
    }
    
    /**
     * Show stash contents
     */
    fun showStash(repoPath: String, stashIndex: Int = 0): String? {
        val repository = openRepository(repoPath) ?: return null
        val git = Git(repository)
        
        return try {
            val stashList = git.stashList().call().toList()
            if (stashIndex >= stashList.size) return null
            
            val stashCommit = stashList[stashIndex]
            
            // Get diff
            val outputStream = java.io.ByteArrayOutputStream()
            val formatter = org.eclipse.jgit.diff.DiffFormatter(outputStream)
            formatter.setRepository(repository)
            
            // Compare stash with its parent
            val parents = stashCommit.parents
            if (parents.isEmpty()) return null
            
            val parent = parents[0]
            val parentTree = parent.tree
            val stashTree = stashCommit.tree
            
            formatter.format(parentTree, stashTree)
            formatter.close()
            
            outputStream.toString()
        } catch (e: Exception) {
            null
        } finally {
            git.close()
        }
    }
    
    /**
     * Check if there are stashes
     */
    fun hasStashes(repoPath: String): Boolean {
        val repository = openRepository(repoPath) ?: return false
        val git = Git(repository)
        
        return try {
            val stashList = git.stashList().call()
            stashList.iterator().hasNext()
        } catch (e: Exception) {
            false
        } finally {
            git.close()
        }
    }
    
    /**
     * Get stash count
     */
    fun getStashCount(repoPath: String): Int {
        val repository = openRepository(repoPath) ?: return 0
        val git = Git(repository)
        
        return try {
            git.stashList().call().count()
        } catch (e: Exception) {
            0
        } finally {
            git.close()
        }
    }
    
    private fun getCurrentBranch(repoPath: String): String {
        val repository = openRepository(repoPath) ?: return "unknown"
        return try {
            repository.branch ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        } finally {
            repository.close()
        }
    }
    
    private fun extractBranchFromStash(message: String): String {
        // Extract branch name from stash message like "WIP on main: abc123 commit message"
        val regex = "WIP on ([^:]+):".toRegex()
        val match = regex.find(message)
        return match?.groupValues?.get(1)?.trim() ?: "unknown"
    }
    
    private fun openRepository(repoPath: String): Repository? {
        return try {
            FileRepositoryBuilder()
                .setGitDir(File(repoPath, ".git"))
                .readEnvironment()
                .findGitDir()
                .build()
        } catch (e: Exception) {
            null
        }
    }
}