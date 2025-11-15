package com.akcreation.gitsilent.integration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Version 2.0 Feature: GitLab Integration
 * Access GitLab API for repositories, merge requests, and issues
 */
class GitLabIntegration(
    private val token: String,
    private val baseUrl: String = "https://gitlab.com/api/v4"
) {
    
    data class Project(
        val id: Long,
        val name: String,
        val nameWithNamespace: String,
        val description: String?,
        val httpUrlToRepo: String,
        val sshUrlToRepo: String,
        val visibility: String,
        val starCount: Int,
        val forksCount: Int,
        val defaultBranch: String,
        val lastActivityAt: String
    )
    
    data class MergeRequest(
        val iid: Int,
        val title: String,
        val state: String,
        val author: String,
        val createdAt: String,
        val updatedAt: String,
        val description: String?,
        val sourceBranch: String,
        val targetBranch: String,
        val webUrl: String,
        val upvotes: Int,
        val downvotes: Int
    )
    
    data class Issue(
        val iid: Int,
        val title: String,
        val state: String,
        val author: String,
        val createdAt: String,
        val updatedAt: String,
        val description: String?,
        val labels: List<String>,
        val assignees: List<String>,
        val webUrl: String,
        val upvotes: Int,
        val downvotes: Int
    )
    
    data class Note(
        val id: Long,
        val author: String,
        val body: String,
        val createdAt: String,
        val updatedAt: String
    )
    
    data class User(
        val id: Long,
        val username: String,
        val name: String,
        val email: String?,
        val avatarUrl: String?,
        val bio: String?,
        val publicEmail: String?
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
     * List user projects
     */
    suspend fun listProjects(
        page: Int = 1,
        perPage: Int = 20,
        orderBy: String = "last_activity_at"
    ): List<Project> = withContext(Dispatchers.IO) {
        try {
            val response = makeRequest(
                "$baseUrl/projects?page=$page&per_page=$perPage&order_by=$orderBy&owned=true",
                "GET"
            )
            val jsonArray = JSONArray(response)
            val projects = mutableListOf<Project>()
            
            for (i in 0 until jsonArray.length()) {
                projects.add(parseProject(jsonArray.getJSONObject(i)))
            }
            
            projects
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get project details
     */
    suspend fun getProject(projectId: String): Project? = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val response = makeRequest("$baseUrl/projects/$encodedId", "GET")
            parseProject(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * List merge requests
     */
    suspend fun listMergeRequests(
        projectId: String,
        state: String = "opened",
        page: Int = 1
    ): List<MergeRequest> = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val response = makeRequest(
                "$baseUrl/projects/$encodedId/merge_requests?state=$state&page=$page",
                "GET"
            )
            val jsonArray = JSONArray(response)
            val mrs = mutableListOf<MergeRequest>()
            
            for (i in 0 until jsonArray.length()) {
                mrs.add(parseMergeRequest(jsonArray.getJSONObject(i)))
            }
            
            mrs
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get merge request details
     */
    suspend fun getMergeRequest(
        projectId: String,
        mrIid: Int
    ): MergeRequest? = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val response = makeRequest(
                "$baseUrl/projects/$encodedId/merge_requests/$mrIid",
                "GET"
            )
            parseMergeRequest(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * List issues
     */
    suspend fun listIssues(
        projectId: String,
        state: String = "opened",
        page: Int = 1
    ): List<Issue> = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val response = makeRequest(
                "$baseUrl/projects/$encodedId/issues?state=$state&page=$page",
                "GET"
            )
            val jsonArray = JSONArray(response)
            val issues = mutableListOf<Issue>()
            
            for (i in 0 until jsonArray.length()) {
                issues.add(parseIssue(jsonArray.getJSONObject(i)))
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
        projectId: String,
        issueIid: Int
    ): Issue? = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val response = makeRequest(
                "$baseUrl/projects/$encodedId/issues/$issueIid",
                "GET"
            )
            parseIssue(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * List notes (comments) on issue/MR
     */
    suspend fun listNotes(
        projectId: String,
        noteableId: Int,
        noteableType: String = "issues" // or "merge_requests"
    ): List<Note> = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val response = makeRequest(
                "$baseUrl/projects/$encodedId/$noteableType/$noteableId/notes",
                "GET"
            )
            val jsonArray = JSONArray(response)
            val notes = mutableListOf<Note>()
            
            for (i in 0 until jsonArray.length()) {
                notes.add(parseNote(jsonArray.getJSONObject(i)))
            }
            
            notes
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Create a note
     */
    suspend fun createNote(
        projectId: String,
        noteableId: Int,
        body: String,
        noteableType: String = "issues"
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val json = JSONObject().apply {
                put("body", body)
            }
            
            makeRequest(
                "$baseUrl/projects/$encodedId/$noteableType/$noteableId/notes",
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
        projectId: String,
        title: String,
        description: String?,
        labels: String = ""
    ): Issue? = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val json = JSONObject().apply {
                put("title", title)
                if (description != null) put("description", description)
                if (labels.isNotEmpty()) put("labels", labels)
            }
            
            val response = makeRequest(
                "$baseUrl/projects/$encodedId/issues",
                "POST",
                json.toString()
            )
            parseIssue(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Create a merge request
     */
    suspend fun createMergeRequest(
        projectId: String,
        sourceBranch: String,
        targetBranch: String,
        title: String,
        description: String?
    ): MergeRequest? = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val json = JSONObject().apply {
                put("source_branch", sourceBranch)
                put("target_branch", targetBranch)
                put("title", title)
                if (description != null) put("description", description)
            }
            
            val response = makeRequest(
                "$baseUrl/projects/$encodedId/merge_requests",
                "POST",
                json.toString()
            )
            parseMergeRequest(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Fork a project
     */
    suspend fun forkProject(projectId: String): Project? = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            val response = makeRequest("$baseUrl/projects/$encodedId/fork", "POST")
            parseProject(JSONObject(response))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Star a project
     */
    suspend fun starProject(projectId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            makeRequest("$baseUrl/projects/$encodedId/star", "POST")
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Unstar a project
     */
    suspend fun unstarProject(projectId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val encodedId = URLEncoder.encode(projectId, "UTF-8")
            makeRequest("$baseUrl/projects/$encodedId/unstar", "POST")
            true
        } catch (e: Exception) {
            false
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
        connection.setRequestProperty("PRIVATE-TOKEN", token)
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
            id = json.getLong("id"),
            username = json.getString("username"),
            name = json.getString("name"),
            email = json.optString("email"),
            avatarUrl = json.optString("avatar_url"),
            bio = json.optString("bio"),
            publicEmail = json.optString("public_email")
        )
    }
    
    private fun parseProject(json: JSONObject): Project {
        return Project(
            id = json.getLong("id"),
            name = json.getString("name"),
            nameWithNamespace = json.getString("name_with_namespace"),
            description = json.optString("description"),
            httpUrlToRepo = json.getString("http_url_to_repo"),
            sshUrlToRepo = json.getString("ssh_url_to_repo"),
            visibility = json.getString("visibility"),
            starCount = json.getInt("star_count"),
            forksCount = json.getInt("forks_count"),
            defaultBranch = json.optString("default_branch", "main"),
            lastActivityAt = json.getString("last_activity_at")
        )
    }
    
    private fun parseMergeRequest(json: JSONObject): MergeRequest {
        return MergeRequest(
            iid = json.getInt("iid"),
            title = json.getString("title"),
            state = json.getString("state"),
            author = json.getJSONObject("author").getString("username"),
            createdAt = json.getString("created_at"),
            updatedAt = json.getString("updated_at"),
            description = json.optString("description"),
            sourceBranch = json.getString("source_branch"),
            targetBranch = json.getString("target_branch"),
            webUrl = json.getString("web_url"),
            upvotes = json.getInt("upvotes"),
            downvotes = json.getInt("downvotes")
        )
    }
    
    private fun parseIssue(json: JSONObject): Issue {
        val labels = mutableListOf<String>()
        val labelsArray = json.optJSONArray("labels")
        if (labelsArray != null) {
            for (i in 0 until labelsArray.length()) {
                labels.add(labelsArray.getString(i))
            }
        }
        
        val assignees = mutableListOf<String>()
        val assigneesArray = json.optJSONArray("assignees")
        if (assigneesArray != null) {
            for (i in 0 until assigneesArray.length()) {
                assignees.add(assigneesArray.getJSONObject(i).getString("username"))
            }
        }
        
        return Issue(
            iid = json.getInt("iid"),
            title = json.getString("title"),
            state = json.getString("state"),
            author = json.getJSONObject("author").getString("username"),
            createdAt = json.getString("created_at"),
            updatedAt = json.getString("updated_at"),
            description = json.optString("description"),
            labels = labels,
            assignees = assignees,
            webUrl = json.getString("web_url"),
            upvotes = json.getInt("upvotes"),
            downvotes = json.getInt("downvotes")
        )
    }
    
    private fun parseNote(json: JSONObject): Note {
        return Note(
            id = json.getLong("id"),
            author = json.getJSONObject("author").getString("username"),
            body = json.getString("body"),
            createdAt = json.getString("created_at"),
            updatedAt = json.getString("updated_at")
        )
    }
}