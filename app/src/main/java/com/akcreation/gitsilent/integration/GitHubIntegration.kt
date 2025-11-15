package com.akcreation.gitsilent.integration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Version 2.0 Feature: GitHub Integration
 * Access GitHub API for repositories, issues, and PRs
 */
class GitHubIntegration(private val token: String) {
    
    private val baseUrl = "https://api.github.com"
    
    data class Repository(
        val id: Long,
        val name: String,
        val fullName: String,
        val description: String?,
        val cloneUrl: String,
        val sshUrl: String,
        val isPrivate: Boolean,
        val stars: Int,
        val forks: Int,
        val language: String?,
        val defaultBranch: String,
        val updatedAt: String
    )
    
    data class PullRequest(
        val number: Int,
        val title: String,
        val state: String,
        val author: String,
        val createdAt: String,
        val updatedAt: String,
        val body: String?,
        val baseBranch: String,
        val headBranch: String,
        val url: String
    )
    
    data class Issue(
        val number: Int,
        val title: String,
        val state: String,
        val author: String,
        val createdAt: String,
        val updatedAt: String,
        val body: String?,
        val labels: List<String>,
        val assignees: List<String>,
        val comments: Int,
        val url: String
    )
    
    data class Comment(
        val id: Long,
        val author: String,
        val body: String,
        val createdAt: String,
        val updatedAt: String
    )
    
    data class User(
        val login: String,
        val name: String?,
        val email: String?,
        val avatarUrl: String,
        val bio: String?,
        val publicRepos: Int,
        val followers: Int,
        val following: Int
    )
    
    /**
     * Get authenticated user info
     */
    suspend fun getAuthenticatedUser(): User? = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest("$baseUrl/user", "GET")
            parseUser(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * List user repositories
     */
    suspend fun listRepositories(
        page: Int = 1,
        perPage: Int = 30,
        sort: String = "updated"
    ): List<Repository> = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest(
                "$baseUrl/user/repos?page=$page&per_page=$perPage&sort=$sort",
                "GET"
            )
            val jsonArray = JSONArray(response)
            val repos = mutableListOf<Repository>()
            
            for (i in 0 until jsonArray.length()) {
                repos.add(parseRepository(jsonArray.getJSONObject(i)))
            }
            
            repos
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get repository details
     */
    suspend fun getRepository(owner: String, repo: String): Repository? = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest("$baseUrl/repos/$owner/$repo", "GET")
            parseRepository(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * List pull requests
     */
    suspend fun listPullRequests(
        owner: String,
        repo: String,
        state: String = "open",
        page: Int = 1
    ): List<PullRequest> = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest(
                "$baseUrl/repos/$owner/$repo/pulls?state=$state&page=$page",
                "GET"
            )
            val jsonArray = JSONArray(response)
            val prs = mutableListOf<PullRequest>()
            
            for (i in 0 until jsonArray.length()) {
                prs.add(parsePullRequest(jsonArray.getJSONObject(i)))
            }
            
            prs
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get pull request details
     */
    suspend fun getPullRequest(
        owner: String,
        repo: String,
        number: Int
    ): PullRequest? = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest("$baseUrl/repos/$owner/$repo/pulls/$number", "GET")
            parsePullRequest(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * List issues
     */
    suspend fun listIssues(
        owner: String,
        repo: String,
        state: String = "open",
        page: Int = 1
    ): List<Issue> = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest(
                "$baseUrl/repos/$owner/$repo/issues?state=$state&page=$page",
                "GET"
            )
            val jsonArray = JSONArray(response)
            val issues = mutableListOf<Issue>()
            
            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)
                // Skip pull requests (they also appear in issues)
                if (!json.has("pull_request")) {
                    issues.add(parseIssue(json))
                }
            }
            
            issues
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get issue details
     */
    suspend fun getIssue(
        owner: String,
        repo: String,
        number: Int
    ): Issue? = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest("$baseUrl/repos/$owner/$repo/issues/$number", "GET")
            parseIssue(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * List comments on issue/PR
     */
    suspend fun listComments(
        owner: String,
        repo: String,
        number: Int,
        type: String = "issues" // or "pulls"
    ): List<Comment> = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest(
                "$baseUrl/repos/$owner/$repo/$type/$number/comments",
                "GET"
            )
            val jsonArray = JSONArray(response)
            val comments = mutableListOf<Comment>()
            
            for (i in 0 until jsonArray.length()) {
                comments.add(parseComment(jsonArray.getJSONObject(i)))
            }
            
            comments
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Create a comment
     */
    suspend fun createComment(
        owner: String,
        repo: String,
        number: Int,
        body: String,
        type: String = "issues"
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("body", body)
            }
            
            makeRequest(
                "$baseUrl/repos/$owner/$repo/$type/$number/comments",
                "POST",
                json.toString()
            )
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Create an issue
     */
    suspend fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String?,
        labels: List<String> = emptyList()
    ): Issue? = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("title", title)
                if (body != null) put("body", body)
                if (labels.isNotEmpty()) put("labels", JSONArray(labels))
            }
            
            val response = makeRequest(
                "$baseUrl/repos/$owner/$repo/issues",
                "POST",
                json.toString()
            )
            parseIssue(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Star a repository
     */
    suspend fun starRepository(owner: String, repo: String): Boolean = withContext(Dispatchers.IO) {
        try {
            makeRequest("$baseUrl/user/starred/$owner/$repo", "PUT")
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Unstar a repository
     */
    suspend fun unstarRepository(owner: String, repo: String): Boolean = withContext(Dispatchers.IO) {
        try {
            makeRequest("$baseUrl/user/starred/$owner/$repo", "DELETE")
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Fork a repository
     */
    suspend fun forkRepository(owner: String, repo: String): Repository? = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest("$baseUrl/repos/$owner/$repo/forks", "POST")
            parseRepository(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    private fun makeRequest(
        urlString: String,
        method: String,
        body: String? = null
    ): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        
        connection.requestMethod = method
        connection.setRequestProperty("Authorization", "Bearer $token")
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
        connection.setRequestProperty("Content-Type", "application/json")
        
        if (body != null && (method == "POST" || method == "PUT" || method == "PATCH")) {
            connection.doOutput = true
            connection.outputStream.use { it.write(body.toByteArray()) }
        }
        
        val responseCode = connection.responseCode
        if (responseCode !in 200..299) {
            throw Exception("HTTP $responseCode")
        }
        
        return BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
    }
    
    private fun parseUser(json: JSONObject): User {
        return User(
            login = json.getString("login"),
            name = json.optString("name"),
            email = json.optString("email"),
            avatarUrl = json.getString("avatar_url"),
            bio = json.optString("bio"),
            publicRepos = json.getInt("public_repos"),
            followers = json.getInt("followers"),
            following = json.getInt("following")
        )
    }
    
    private fun parseRepository(json: JSONObject): Repository {
        return Repository(
            id = json.getLong("id"),
            name = json.getString("name"),
            fullName = json.getString("full_name"),
            description = json.optString("description"),
            cloneUrl = json.getString("clone_url"),
            sshUrl = json.getString("ssh_url"),
            isPrivate = json.getBoolean("private"),
            stars = json.getInt("stargazers_count"),
            forks = json.getInt("forks_count"),
            language = json.optString("language"),
            defaultBranch = json.getString("default_branch"),
            updatedAt = json.getString("updated_at")
        )
    }
    
    private fun parsePullRequest(json: JSONObject): PullRequest {
        return PullRequest(
            number = json.getInt("number"),
            title = json.getString("title"),
            state = json.getString("state"),
            author = json.getJSONObject("user").getString("login"),
            createdAt = json.getString("created_at"),
            updatedAt = json.getString("updated_at"),
            body = json.optString("body"),
            baseBranch = json.getJSONObject("base").getString("ref"),
            headBranch = json.getJSONObject("head").getString("ref"),
            url = json.getString("html_url")
        )
    }
    
    private fun parseIssue(json: JSONObject): Issue {
        val labels = mutableListOf<String>()
        val labelsArray = json.getJSONArray("labels")
        for (i in 0 until labelsArray.length()) {
            labels.add(labelsArray.getJSONObject(i).getString("name"))
        }
        
        val assignees = mutableListOf<String>()
        val assigneesArray = json.optJSONArray("assignees")
        if (assigneesArray != null) {
            for (i in 0 until assigneesArray.length()) {
                assignees.add(assigneesArray.getJSONObject(i).getString("login"))
            }
        }
        
        return Issue(
            number = json.getInt("number"),
            title = json.getString("title"),
            state = json.getString("state"),
            author = json.getJSONObject("user").getString("login"),
            createdAt = json.getString("created_at"),
            updatedAt = json.getString("updated_at"),
            body = json.optString("body"),
            labels = labels,
            assignees = assignees,
            comments = json.getInt("comments"),
            url = json.getString("html_url")
        )
    }
    
    private fun parseComment(json: JSONObject): Comment {
        return Comment(
            id = json.getLong("id"),
            author = json.getJSONObject("user").getString("login"),
            body = json.getString("body"),
            createdAt = json.getString("created_at"),
            updatedAt = json.getString("updated_at")
        )
    }
}