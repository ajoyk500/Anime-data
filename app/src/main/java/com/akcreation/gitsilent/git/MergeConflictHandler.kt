package com.akcreation.gitsilent.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.merge.MergeStrategy
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

/**
 * Version 1.1 Feature: Merge Conflict Resolution
 * Handles and resolves merge conflicts
 */
class MergeConflictHandler {
    
    data class ConflictFile(
        val path: String,
        val content: String,
        val conflicts: List<ConflictSection>
    )
    
    data class ConflictSection(
        val startLine: Int,
        val endLine: Int,
        val oursContent: String,
        val theirsContent: String,
        val baseContent: String? = null
    )
    
    data class MergeStatus(
        val hasConflicts: Boolean,
        val conflictFiles: List<String>,
        val successfulFiles: List<String>,
        val message: String
    )
    
    /**
     * Check for merge conflicts in repository
     */
    fun checkForConflicts(repoPath: String): MergeStatus {
        val repository = openRepository(repoPath) ?: return MergeStatus(
            hasConflicts = false,
            conflictFiles = emptyList(),
            successfulFiles = emptyList(),
            message = "Failed to open repository"
        )
        
        val git = Git(repository)
        
        try {
            val status = git.status().call()
            val conflicting = status.conflicting.toList()
            
            return MergeStatus(
                hasConflicts = conflicting.isNotEmpty(),
                conflictFiles = conflicting,
                successfulFiles = status.modified.toList(),
                message = if (conflicting.isEmpty()) "No conflicts" else "${conflicting.size} files with conflicts"
            )
        } finally {
            git.close()
        }
    }
    
    /**
     * Get conflict details for a file
     */
    fun getConflictDetails(repoPath: String, filePath: String): ConflictFile? {
        val file = File(repoPath, filePath)
        if (!file.exists()) return null
        
        val content = file.readText()
        val conflicts = parseConflictMarkers(content)
        
        return ConflictFile(
            path = filePath,
            content = content,
            conflicts = conflicts
        )
    }
    
    /**
     * Parse conflict markers from file content
     */
    private fun parseConflictMarkers(content: String): List<ConflictSection> {
        val conflicts = mutableListOf<ConflictSection>()
        val lines = content.lines()
        var i = 0
        
        while (i < lines.size) {
            if (lines[i].startsWith("<<<<<<<")) {
                val startLine = i
                var middleLine = -1
                var baseLine = -1
                var endLine = -1
                
                // Find conflict markers
                for (j in i + 1 until lines.size) {
                    when {
                        lines[j].startsWith("|||||||") -> baseLine = j
                        lines[j].startsWith("=======") -> middleLine = j
                        lines[j].startsWith(">>>>>>>") -> {
                            endLine = j
                            break
                        }
                    }
                }
                
                if (middleLine > 0 && endLine > 0) {
                    // Extract content
                    val oursStart = startLine + 1
                    val oursEnd = if (baseLine > 0) baseLine else middleLine
                    val theirsStart = middleLine + 1
                    val theirsEnd = endLine
                    
                    val oursContent = lines.subList(oursStart, oursEnd).joinToString("\n")
                    val theirsContent = lines.subList(theirsStart, theirsEnd).joinToString("\n")
                    
                    val baseContent = if (baseLine > 0) {
                        lines.subList(baseLine + 1, middleLine).joinToString("\n")
                    } else null
                    
                    conflicts.add(
                        ConflictSection(
                            startLine = startLine,
                            endLine = endLine,
                            oursContent = oursContent,
                            theirsContent = theirsContent,
                            baseContent = baseContent
                        )
                    )
                    
                    i = endLine + 1
                } else {
                    i++
                }
            } else {
                i++
            }
        }
        
        return conflicts
    }
    
    /**
     * Resolve conflict by choosing a version
     */
    fun resolveConflict(
        repoPath: String,
        filePath: String,
        conflictIndex: Int,
        chooseOurs: Boolean
    ): Boolean {
        val conflictFile = getConflictDetails(repoPath, filePath) ?: return false
        
        if (conflictIndex >= conflictFile.conflicts.size) return false
        
        val conflict = conflictFile.conflicts[conflictIndex]
        val lines = conflictFile.content.lines().toMutableList()
        
        // Remove conflict markers and choose content
        val chosenContent = if (chooseOurs) conflict.oursContent else conflict.theirsContent
        
        // Replace conflict section with chosen content
        val newLines = mutableListOf<String>()
        newLines.addAll(lines.subList(0, conflict.startLine))
        newLines.addAll(chosenContent.lines())
        newLines.addAll(lines.subList(conflict.endLine + 1, lines.size))
        
        // Write back to file
        val file = File(repoPath, filePath)
        file.writeText(newLines.joinToString("\n"))
        
        return true
    }
    
    /**
     * Resolve conflict with custom content
     */
    fun resolveConflictWithCustomContent(
        repoPath: String,
        filePath: String,
        conflictIndex: Int,
        customContent: String
    ): Boolean {
        val conflictFile = getConflictDetails(repoPath, filePath) ?: return false
        
        if (conflictIndex >= conflictFile.conflicts.size) return false
        
        val conflict = conflictFile.conflicts[conflictIndex]
        val lines = conflictFile.content.lines().toMutableList()
        
        // Replace conflict section with custom content
        val newLines = mutableListOf<String>()
        newLines.addAll(lines.subList(0, conflict.startLine))
        newLines.addAll(customContent.lines())
        newLines.addAll(lines.subList(conflict.endLine + 1, lines.size))
        
        // Write back to file
        val file = File(repoPath, filePath)
        file.writeText(newLines.joinToString("\n"))
        
        return true
    }
    
    /**
     * Mark file as resolved (stage it)
     */
    fun markAsResolved(repoPath: String, filePath: String): Boolean {
        val repository = openRepository(repoPath) ?: return false
        val git = Git(repository)
        
        return try {
            git.add().addFilepattern(filePath).call()
            true
        } catch (e: Exception) {
            false
        } finally {
            git.close()
        }
    }
    
    /**
     * Abort merge
     */
    fun abortMerge(repoPath: String): Boolean {
        val repository = openRepository(repoPath) ?: return false
        val git = Git(repository)
        
        return try {
            git.reset()
                .setMode(org.eclipse.jgit.api.ResetCommand.ResetType.HARD)
                .call()
            
            // Clean merge state files
            val gitDir = File(repoPath, ".git")
            File(gitDir, "MERGE_HEAD").delete()
            File(gitDir, "MERGE_MSG").delete()
            File(gitDir, "MERGE_MODE").delete()
            
            true
        } catch (e: Exception) {
            false
        } finally {
            git.close()
        }
    }
    
    /**
     * Continue merge after resolving conflicts
     */
    fun continueMerge(repoPath: String, commitMessage: String): Boolean {
        val repository = openRepository(repoPath) ?: return false
        val git = Git(repository)
        
        return try {
            // Check if all conflicts are resolved
            val status = git.status().call()
            if (status.conflicting.isNotEmpty()) {
                return false
            }
            
            // Commit the merge
            git.commit()
                .setMessage(commitMessage)
                .call()
            
            true
        } catch (e: Exception) {
            false
        } finally {
            git.close()
        }
    }
    
    /**
     * Get merge strategy options
     */
    fun getMergeStrategies(): List<String> {
        return listOf(
            "Recursive (Default)",
            "Ours",
            "Theirs",
            "Simple Two-Way"
        )
    }
    
    /**
     * Perform merge with specific strategy
     */
    fun mergeWithStrategy(
        repoPath: String,
        branchName: String,
        strategyName: String
    ): MergeResult? {
        val repository = openRepository(repoPath) ?: return null
        val git = Git(repository)
        
        val strategy = when (strategyName) {
            "Ours" -> MergeStrategy.OURS
            "Theirs" -> MergeStrategy.THEIRS
            "Simple Two-Way" -> MergeStrategy.SIMPLE_TWO_WAY_IN_CORE
            else -> MergeStrategy.RECURSIVE
        }
        
        return try {
            val ref = repository.findRef(branchName)
            git.merge()
                .include(ref)
                .setStrategy(strategy)
                .call()
        } catch (e: Exception) {
            null
        } finally {
            git.close()
        }
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