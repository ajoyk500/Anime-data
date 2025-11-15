package com.akcreation.gitsilent.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.util.io.DisabledOutputStream
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Version 1.1 Feature: File Diff Viewer
 * Shows differences between file versions
 */
class DiffViewer {
    
    data class DiffLine(
        val lineNumber: Int,
        val content: String,
        val type: DiffType
    )
    
    enum class DiffType {
        UNCHANGED,
        ADDED,
        REMOVED,
        MODIFIED
    }
    
    data class FileDiff(
        val oldPath: String,
        val newPath: String,
        val changeType: DiffEntry.ChangeType,
        val lines: List<DiffLine>,
        val addedLines: Int,
        val removedLines: Int
    )
    
    /**
     * Get diff between two commits
     */
    fun getDiffBetweenCommits(
        repoPath: String,
        oldCommitHash: String,
        newCommitHash: String
    ): List<FileDiff> {
        val repository = openRepository(repoPath) ?: return emptyList()
        val git = Git(repository)
        
        try {
            val oldCommit = repository.resolve(oldCommitHash)
            val newCommit = repository.resolve(newCommitHash)
            
            return getDiffBetweenTrees(repository, oldCommit, newCommit)
        } finally {
            git.close()
        }
    }
    
    /**
     * Get diff for uncommitted changes
     */
    fun getDiffForUncommittedChanges(repoPath: String): List<FileDiff> {
        val repository = openRepository(repoPath) ?: return emptyList()
        val git = Git(repository)
        
        try {
            val head = repository.resolve("HEAD^{tree}")
            val diffs = mutableListOf<FileDiff>()
            
            val diffFormatter = DiffFormatter(DisabledOutputStream.INSTANCE)
            diffFormatter.setRepository(repository)
            
            val oldTreeParser = CanonicalTreeParser()
            val reader = repository.newObjectReader()
            oldTreeParser.reset(reader, head)
            
            val diffEntries = git.diff()
                .setOldTree(oldTreeParser)
                .setShowNameAndStatusOnly(false)
                .call()
            
            for (entry in diffEntries) {
                val fileDiff = parseDiffEntry(repository, entry)
                if (fileDiff != null) {
                    diffs.add(fileDiff)
                }
            }
            
            return diffs
        } finally {
            git.close()
        }
    }
    
    /**
     * Get diff for a specific file
     */
    fun getFileDiff(
        repoPath: String,
        filePath: String,
        commitHash: String? = null
    ): FileDiff? {
        val repository = openRepository(repoPath) ?: return null
        val git = Git(repository)
        
        try {
            val diffFormatter = DiffFormatter(ByteArrayOutputStream())
            diffFormatter.setRepository(repository)
            
            val head = if (commitHash != null) {
                repository.resolve(commitHash)
            } else {
                repository.resolve("HEAD^{tree}")
            }
            
            val oldTreeParser = CanonicalTreeParser()
            val reader = repository.newObjectReader()
            oldTreeParser.reset(reader, head)
            
            val diffEntries = git.diff()
                .setOldTree(oldTreeParser)
                .setPathFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(filePath))
                .call()
            
            return if (diffEntries.isNotEmpty()) {
                parseDiffEntry(repository, diffEntries[0])
            } else null
        } finally {
            git.close()
        }
    }
    
    /**
     * Compare two file versions side by side
     */
    fun compareTwoVersions(
        repoPath: String,
        filePath: String,
        version1: String,
        version2: String
    ): Pair<String, String>? {
        val repository = openRepository(repoPath) ?: return null
        
        try {
            val content1 = getFileContentAtCommit(repository, filePath, version1)
            val content2 = getFileContentAtCommit(repository, filePath, version2)
            
            return Pair(content1 ?: "", content2 ?: "")
        } finally {
            repository.close()
        }
    }
    
    private fun getDiffBetweenTrees(
        repository: Repository,
        oldTree: ObjectId,
        newTree: ObjectId
    ): List<FileDiff> {
        val diffs = mutableListOf<FileDiff>()
        val diffFormatter = DiffFormatter(DisabledOutputStream.INSTANCE)
        diffFormatter.setRepository(repository)
        
        val oldTreeParser = CanonicalTreeParser()
        val newTreeParser = CanonicalTreeParser()
        val reader = repository.newObjectReader()
        
        oldTreeParser.reset(reader, oldTree)
        newTreeParser.reset(reader, newTree)
        
        val diffEntries = diffFormatter.scan(oldTreeParser, newTreeParser)
        
        for (entry in diffEntries) {
            val fileDiff = parseDiffEntry(repository, entry)
            if (fileDiff != null) {
                diffs.add(fileDiff)
            }
        }
        
        return diffs
    }
    
    private fun parseDiffEntry(repository: Repository, entry: DiffEntry): FileDiff? {
        try {
            val outputStream = ByteArrayOutputStream()
            val diffFormatter = DiffFormatter(outputStream)
            diffFormatter.setRepository(repository)
            diffFormatter.format(entry)
            
            val diffText = outputStream.toString()
            val lines = parseDiffText(diffText)
            
            val addedLines = lines.count { it.type == DiffType.ADDED }
            val removedLines = lines.count { it.type == DiffType.REMOVED }
            
            return FileDiff(
                oldPath = entry.oldPath ?: "",
                newPath = entry.newPath ?: "",
                changeType = entry.changeType,
                lines = lines,
                addedLines = addedLines,
                removedLines = removedLines
            )
        } catch (e: Exception) {
            return null
        }
    }
    
    private fun parseDiffText(diffText: String): List<DiffLine> {
        val lines = mutableListOf<DiffLine>()
        var lineNumber = 0
        
        diffText.lines().forEach { line ->
            val type = when {
                line.startsWith("+") && !line.startsWith("+++") -> DiffType.ADDED
                line.startsWith("-") && !line.startsWith("---") -> DiffType.REMOVED
                line.startsWith("@@") -> return@forEach
                else -> DiffType.UNCHANGED
            }
            
            if (type != DiffType.UNCHANGED || !line.startsWith("diff")) {
                lines.add(DiffLine(lineNumber++, line, type))
            }
        }
        
        return lines
    }
    
    private fun getFileContentAtCommit(
        repository: Repository,
        filePath: String,
        commitHash: String
    ): String? {
        return try {
            val revWalk = RevWalk(repository)
            val commit = revWalk.parseCommit(repository.resolve(commitHash))
            val treeWalk = org.eclipse.jgit.treewalk.TreeWalk.forPath(
                repository,
                filePath,
                commit.tree
            )
            
            if (treeWalk != null) {
                val objectId = treeWalk.getObjectId(0)
                val loader = repository.open(objectId)
                String(loader.bytes)
            } else null
        } catch (e: Exception) {
            null
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