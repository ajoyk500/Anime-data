package com.akcreation.gitsilent.search

import com.akcreation.gitsilent.git.GitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

/**
 * Version 1.2 Feature: Search Functionality
 * Search files, commits, and content in repositories
 */
class SearchEngine {
    
    data class SearchResult(
        val type: SearchType,
        val title: String,
        val subtitle: String,
        val path: String,
        val lineNumber: Int? = null,
        val matchedText: String? = null
    )
    
    enum class SearchType {
        FILE,
        COMMIT,
        CONTENT,
        BRANCH
    }
    
    data class ContentMatch(
        val filePath: String,
        val lineNumber: Int,
        val lineContent: String,
        val matchStart: Int,
        val matchEnd: Int
    )
    
    /**
     * Search files by name
     */
    suspend fun searchFilesByName(
        repoPath: String,
        query: String,
        caseSensitive: Boolean = false
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        val pattern = if (caseSensitive) {
            Pattern.compile(Pattern.quote(query))
        } else {
            Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE)
        }
        
        val repoDir = File(repoPath)
        searchFilesRecursive(repoDir, repoDir, pattern, results)
        
        results
    }
    
    /**
     * Search content inside files
     */
    suspend fun searchContent(
        repoPath: String,
        query: String,
        caseSensitive: Boolean = false,
        regex: Boolean = false,
        fileExtensions: List<String> = emptyList()
    ): List<ContentMatch> = withContext(Dispatchers.IO) {
        val matches = mutableListOf<ContentMatch>()
        val pattern = if (regex) {
            if (caseSensitive) Pattern.compile(query)
            else Pattern.compile(query, Pattern.CASE_INSENSITIVE)
        } else {
            if (caseSensitive) Pattern.compile(Pattern.quote(query))
            else Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE)
        }
        
        val repoDir = File(repoPath)
        searchContentRecursive(repoDir, repoDir, pattern, fileExtensions, matches)
        
        matches
    }
    
    /**
     * Search commits
     */
    suspend fun searchCommits(
        repoPath: String,
        query: String,
        searchAuthor: Boolean = true,
        searchMessage: Boolean = true
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val gitHelper = GitHelper()
        val commits = gitHelper.getCommitHistory(repoPath, 1000)
        val results = mutableListOf<SearchResult>()
        
        val lowerQuery = query.lowercase()
        
        for (commit in commits) {
            val matchAuthor = searchAuthor && commit.author.lowercase().contains(lowerQuery)
            val matchMessage = searchMessage && commit.message.lowercase().contains(lowerQuery)
            
            if (matchAuthor || matchMessage) {
                results.add(
                    SearchResult(
                        type = SearchType.COMMIT,
                        title = commit.message.lines().first(),
                        subtitle = "${commit.author} • ${commit.shortHash}",
                        path = commit.hash
                    )
                )
            }
        }
        
        results
    }
    
    /**
     * Search branches
     */
    suspend fun searchBranches(
        repoPath: String,
        query: String
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        val gitDir = File(repoPath, ".git/refs/heads")
        
        if (gitDir.exists()) {
            searchBranchesRecursive(gitDir, "", query.lowercase(), results)
        }
        
        results
    }
    
    /**
     * Unified search (files, commits, content)
     */
    suspend fun unifiedSearch(
        repoPath: String,
        query: String,
        includeFiles: Boolean = true,
        includeCommits: Boolean = true,
        includeContent: Boolean = true
    ): Map<SearchType, List<SearchResult>> = withContext(Dispatchers.IO) {
        val results = mutableMapOf<SearchType, List<SearchResult>>()
        
        if (includeFiles) {
            val fileResults = searchFilesByName(repoPath, query)
            results[SearchType.FILE] = fileResults
        }
        
        if (includeCommits) {
            val commitResults = searchCommits(repoPath, query)
            results[SearchType.COMMIT] = commitResults
        }
        
        if (includeContent) {
            val contentMatches = searchContent(repoPath, query)
            val contentResults = contentMatches.map { match ->
                SearchResult(
                    type = SearchType.CONTENT,
                    title = match.filePath,
                    subtitle = "Line ${match.lineNumber}: ${match.lineContent.trim()}",
                    path = match.filePath,
                    lineNumber = match.lineNumber,
                    matchedText = match.lineContent
                )
            }
            results[SearchType.CONTENT] = contentResults
        }
        
        results
    }
    
    private fun searchFilesRecursive(
        currentDir: File,
        rootDir: File,
        pattern: Pattern,
        results: MutableList<SearchResult>
    ) {
        currentDir.listFiles()?.forEach { file ->
            if (file.name == ".git") return@forEach
            
            if (file.isDirectory) {
                searchFilesRecursive(file, rootDir, pattern, results)
            } else {
                val matcher = pattern.matcher(file.name)
                if (matcher.find()) {
                    val relativePath = file.absolutePath.removePrefix(rootDir.absolutePath + "/")
                    results.add(
                        SearchResult(
                            type = SearchType.FILE,
                            title = file.name,
                            subtitle = relativePath,
                            path = relativePath
                        )
                    )
                }
            }
        }
    }
    
    private fun searchContentRecursive(
        currentDir: File,
        rootDir: File,
        pattern: Pattern,
        extensions: List<String>,
        matches: MutableList<ContentMatch>
    ) {
        currentDir.listFiles()?.forEach { file ->
            if (file.name == ".git") return@forEach
            
            if (file.isDirectory) {
                searchContentRecursive(file, rootDir, pattern, extensions, matches)
            } else {
                // Check file extension if filter is provided
                if (extensions.isNotEmpty()) {
                    val fileExt = file.extension.lowercase()
                    if (fileExt !in extensions) return@forEach
                }
                
                // Check if file is text
                if (!isTextFile(file)) return@forEach
                
                try {
                    val relativePath = file.absolutePath.removePrefix(rootDir.absolutePath + "/")
                    val lines = file.readLines()
                    
                    lines.forEachIndexed { index, line ->
                        val matcher = pattern.matcher(line)
                        if (matcher.find()) {
                            matches.add(
                                ContentMatch(
                                    filePath = relativePath,
                                    lineNumber = index + 1,
                                    lineContent = line,
                                    matchStart = matcher.start(),
                                    matchEnd = matcher.end()
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Skip files that can't be read as text
                }
            }
        }
    }
    
    private fun searchBranchesRecursive(
        dir: File,
        prefix: String,
        query: String,
        results: MutableList<SearchResult>
    ) {
        dir.listFiles()?.forEach { file ->
            val branchName = if (prefix.isEmpty()) file.name else "$prefix/${file.name}"
            
            if (file.isDirectory) {
                searchBranchesRecursive(file, branchName, query, results)
            } else {
                if (branchName.lowercase().contains(query)) {
                    results.add(
                        SearchResult(
                            type = SearchType.BRANCH,
                            title = branchName,
                            subtitle = "Branch",
                            path = branchName
                        )
                    )
                }
            }
        }
    }
    
    private fun isTextFile(file: File): Boolean {
        // Check file size (skip files larger than 1MB)
        if (file.length() > 1024 * 1024) return false
        
        // Check if file appears to be text
        try {
            val bytes = file.readBytes().take(512).toByteArray()
            val text = String(bytes)
            
            // Check for null bytes (binary files)
            return !text.contains('\u0000')
        } catch (e: Exception) {
            return false
        }
    }
}

/**
 * Search history manager
 */
class SearchHistoryManager(private val context: android.content.Context) {
    
    private val prefs = context.getSharedPreferences("search_history", android.content.Context.MODE_PRIVATE)
    private val maxHistory = 20
    
    fun addToHistory(query: String) {
        val history = getHistory().toMutableList()
        
        // Remove if already exists
        history.remove(query)
        
        // Add to front
        history.add(0, query)
        
        // Limit size
        if (history.size > maxHistory) {
            history.removeAt(history.size - 1)
        }
        
        // Save
        prefs.edit()
            .putStringSet("history", history.toSet())
            .apply()
    }
    
    fun getHistory(): List<String> {
        return prefs.getStringSet("history", emptySet())?.toList() ?: emptyList()
    }
    
    fun clearHistory() {
        prefs.edit().remove("history").apply()
    }
    
    fun removeFromHistory(query: String) {
        val history = getHistory().toMutableList()
        history.remove(query)
        prefs.edit()
            .putStringSet("history", history.toSet())
            .apply()
    }
}