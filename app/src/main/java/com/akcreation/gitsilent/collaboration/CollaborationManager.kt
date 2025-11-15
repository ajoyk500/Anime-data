package com.akcreation.gitsilent.collaboration

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Version 2.0 Feature: Collaborative Features
 * Team collaboration tools for code reviews and discussions
 */
class CollaborationManager(private val context: Context) {
    
    private val collabDir = File(context.filesDir, "collaboration")
    
    init {
        if (!collabDir.exists()) {
            collabDir.mkdirs()
        }
    }
    
    data class CodeReview(
        val id: String,
        val repoPath: String,
        val filePath: String,
        val title: String,
        val description: String,
        val author: String,
        val createdAt: Long,
        var status: ReviewStatus,
        val comments: MutableList<ReviewComment> = mutableListOf(),
        val approvals: MutableList<String> = mutableListOf(),
        val rejections: MutableList<String> = mutableListOf()
    )
    
    enum class ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CHANGES_REQUESTED,
        MERGED
    }
    
    data class ReviewComment(
        val id: String,
        val author: String,
        val content: String,
        val lineNumber: Int?,
        val timestamp: Long,
        val replies: MutableList<ReviewComment> = mutableListOf()
    )
    
    data class TeamMember(
        val username: String,
        val name: String,
        val email: String,
        val role: TeamRole,
        val avatarUrl: String? = null
    )
    
    enum class TeamRole {
        OWNER,
        MAINTAINER,
        CONTRIBUTOR,
        VIEWER
    }
    
    data class Activity(
        val id: String,
        val type: ActivityType,
        val author: String,
        val message: String,
        val timestamp: Long,
        val details: Map<String, String> = emptyMap()
    )
    
    enum class ActivityType {
        COMMIT,
        PUSH,
        PULL,
        BRANCH_CREATE,
        BRANCH_DELETE,
        TAG_CREATE,
        REVIEW_CREATE,
        REVIEW_APPROVE,
        REVIEW_REJECT,
        COMMENT_ADD
    }
    
    /**
     * Create a code review
     */
    suspend fun createCodeReview(
        repoPath: String,
        filePath: String,
        title: String,
        description: String,
        author: String
    ): CodeReview = withContext(Dispatchers.IO) {
        val review = CodeReview(
            id = UUID.randomUUID().toString(),
            repoPath = repoPath,
            filePath = filePath,
            title = title,
            description = description,
            author = author,
            createdAt = System.currentTimeMillis(),
            status = ReviewStatus.PENDING
        )
        
        saveReview(review)
        logActivity(
            ActivityType.REVIEW_CREATE,
            author,
            "Created review: $title"
        )
        
        review
    }
    
    /**
     * Add comment to review
     */
    suspend fun addReviewComment(
        reviewId: String,
        author: String,
        content: String,
        lineNumber: Int? = null,
        parentCommentId: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val review = getReview(reviewId) ?: return@withContext false
        
        val comment = ReviewComment(
            id = UUID.randomUUID().toString(),
            author = author,
            content = content,
            lineNumber = lineNumber,
            timestamp = System.currentTimeMillis()
        )
        
        if (parentCommentId != null) {
            // Add as reply
            val parentComment = findComment(review.comments, parentCommentId)
            parentComment?.replies?.add(comment)
        } else {
            review.comments.add(comment)
        }
        
        saveReview(review)
        logActivity(
            ActivityType.COMMENT_ADD,
            author,
            "Commented on review: ${review.title}"
        )
        
        true
    }
    
    /**
     * Approve review
     */
    suspend fun approveReview(reviewId: String, approver: String): Boolean = withContext(Dispatchers.IO) {
        val review = getReview(reviewId) ?: return@withContext false
        
        if (!review.approvals.contains(approver)) {
            review.approvals.add(approver)
            review.rejections.remove(approver)
            
            // Auto-approve if threshold met (e.g., 2 approvals)
            if (review.approvals.size >= 2) {
                review.status = ReviewStatus.APPROVED
            }
            
            saveReview(review)
            logActivity(
                ActivityType.REVIEW_APPROVE,
                approver,
                "Approved review: ${review.title}"
            )
        }
        
        true
    }
    
    /**
     * Reject review
     */
    suspend fun rejectReview(
        reviewId: String,
        rejector: String,
        reason: String
    ): Boolean = withContext(Dispatchers.IO) {
        val review = getReview(reviewId) ?: return@withContext false
        
        review.rejections.add(rejector)
        review.approvals.remove(rejector)
        review.status = ReviewStatus.REJECTED
        
        addReviewComment(reviewId, rejector, "Rejected: $reason")
        
        saveReview(review)
        logActivity(
            ActivityType.REVIEW_REJECT,
            rejector,
            "Rejected review: ${review.title}"
        )
        
        true
    }
    
    /**
     * Get all reviews for a repository
     */
    fun getReviewsForRepo(repoPath: String): List<CodeReview> {
        val reviewsFile = File(collabDir, "reviews.json")
        if (!reviewsFile.exists()) return emptyList()
        
        return try {
            val json = JSONArray(reviewsFile.readText())
            val reviews = mutableListOf<CodeReview>()
            
            for (i in 0 until json.length()) {
                val review = parseReview(json.getJSONObject(i))
                if (review.repoPath == repoPath) {
                    reviews.add(review)
                }
            }
            
            reviews
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get review by ID
     */
    fun getReview(reviewId: String): CodeReview? {
        val reviewsFile = File(collabDir, "reviews.json")
        if (!reviewsFile.exists()) return null
        
        return try {
            val json = JSONArray(reviewsFile.readText())
            
            for (i in 0 until json.length()) {
                val review = parseReview(json.getJSONObject(i))
                if (review.id == reviewId) {
                    return review
                }
            }
            
            null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Log activity
     */
    fun logActivity(type: ActivityType, author: String, message: String, details: Map<String, String> = emptyMap()) {
        val activity = Activity(
            id = UUID.randomUUID().toString(),
            type = type,
            author = author,
            message = message,
            timestamp = System.currentTimeMillis(),
            details = details
        )
        
        val activitiesFile = File(collabDir, "activities.json")
        val activities = if (activitiesFile.exists()) {
            JSONArray(activitiesFile.readText())
        } else {
            JSONArray()
        }
        
        activities.put(serializeActivity(activity))
        
        // Keep only last 100 activities
        while (activities.length() > 100) {
            activities.remove(0)
        }
        
        activitiesFile.writeText(activities.toString(2))
    }
    
    /**
     * Get recent activities
     */
    fun getRecentActivities(limit: Int = 50): List<Activity> {
        val activitiesFile = File(collabDir, "activities.json")
        if (!activitiesFile.exists()) return emptyList()
        
        return try {
            val json = JSONArray(activitiesFile.readText())
            val activities = mutableListOf<Activity>()
            
            val count = minOf(limit, json.length())
            for (i in (json.length() - count) until json.length()) {
                activities.add(parseActivity(json.getJSONObject(i)))
            }
            
            activities.reversed()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Manage team members
     */
    fun addTeamMember(member: TeamMember) {
        val teamFile = File(collabDir, "team.json")
        val team = if (teamFile.exists()) {
            JSONArray(teamFile.readText())
        } else {
            JSONArray()
        }
        
        team.put(serializeTeamMember(member))
        teamFile.writeText(team.toString(2))
    }
    
    fun getTeamMembers(): List<TeamMember> {
        val teamFile = File(collabDir, "team.json")
        if (!teamFile.exists()) return emptyList()
        
        return try {
            val json = JSONArray(teamFile.readText())
            val members = mutableListOf<TeamMember>()
            
            for (i in 0 until json.length()) {
                members.add(parseTeamMember(json.getJSONObject(i)))
            }
            
            members
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Generate activity report
     */
    fun generateActivityReport(startTime: Long, endTime: Long): String {
        val activities = getRecentActivities(1000)
            .filter { it.timestamp in startTime..endTime }
        
        val report = StringBuilder()
        report.append("Activity Report\n")
        report.append("Period: ${formatDate(startTime)} - ${formatDate(endTime)}\n\n")
        
        // Group by type
        val groupedActivities = activities.groupBy { it.type }
        
        groupedActivities.forEach { (type, acts) ->
            report.append("${type.name}: ${acts.size}\n")
        }
        
        report.append("\n")
        
        // Group by author
        val byAuthor = activities.groupBy { it.author }
        report.append("Top Contributors:\n")
        byAuthor.entries
            .sortedByDescending { it.value.size }
            .take(5)
            .forEach { (author, acts) ->
                report.append("  $author: ${acts.size} activities\n")
            }
        
        return report.toString()
    }
    
    private fun saveReview(review: CodeReview) {
        val reviewsFile = File(collabDir, "reviews.json")
        val reviews = if (reviewsFile.exists()) {
            JSONArray(reviewsFile.readText())
        } else {
            JSONArray()
        }
        
        // Update or add review
        var found = false
        for (i in 0 until reviews.length()) {
            val obj = reviews.getJSONObject(i)
            if (obj.getString("id") == review.id) {
                reviews.put(i, serializeReview(review))
                found = true
                break
            }
        }
        
        if (!found) {
            reviews.put(serializeReview(review))
        }
        
        reviewsFile.writeText(reviews.toString(2))
    }
    
    private fun findComment(comments: List<ReviewComment>, id: String): ReviewComment? {
        for (comment in comments) {
            if (comment.id == id) return comment
            val found = findComment(comment.replies, id)
            if (found != null) return found
        }
        return null
    }
    
    private fun serializeReview(review: CodeReview): JSONObject {
        return JSONObject().apply {
            put("id", review.id)
            put("repoPath", review.repoPath)
            put("filePath", review.filePath)
            put("title", review.title)
            put("description", review.description)
            put("author", review.author)
            put("createdAt", review.createdAt)
            put("status", review.status.name)
            put("comments", JSONArray(review.comments.map { serializeComment(it) }))
            put("approvals", JSONArray(review.approvals))
            put("rejections", JSONArray(review.rejections))
        }
    }
    
    private fun serializeComment(comment: ReviewComment): JSONObject {
        return JSONObject().apply {
            put("id", comment.id)
            put("author", comment.author)
            put("content", comment.content)
            put("lineNumber", comment.lineNumber)
            put("timestamp", comment.timestamp)
            put("replies", JSONArray(comment.replies.map { serializeComment(it) }))
        }
    }
    
    private fun serializeActivity(activity: Activity): JSONObject {
        return JSONObject().apply {
            put("id", activity.id)
            put("type", activity.type.name)
            put("author", activity.author)
            put("message", activity.message)
            put("timestamp", activity.timestamp)
            put("details", JSONObject(activity.details))
        }
    }
    
    private fun serializeTeamMember(member: TeamMember): JSONObject {
        return JSONObject().apply {
            put("username", member.username)
            put("name", member.name)
            put("email", member.email)
            put("role", member.role.name)
            put("avatarUrl", member.avatarUrl)
        }
    }
    
    private fun parseReview(json: JSONObject): CodeReview {
        val comments = mutableListOf<ReviewComment>()
        val commentsArray = json.getJSONArray("comments")
        for (i in 0 until commentsArray.length()) {
            comments.add(parseComment(commentsArray.getJSONObject(i)))
        }
        
        val approvals = mutableListOf<String>()
        val approvalsArray = json.getJSONArray("approvals")
        for (i in 0 until approvalsArray.length()) {
            approvals.add(approvalsArray.getString(i))
        }
        
        val rejections = mutableListOf<String>()
        val rejectionsArray = json.getJSONArray("rejections")
        for (i in 0 until rejectionsArray.length()) {
            rejections.add(rejectionsArray.getString(i))
        }
        
        return CodeReview(
            id = json.getString("id"),
            repoPath = json.getString("repoPath"),
            filePath = json.getString("filePath"),
            title = json.getString("title"),
            description = json.getString("description"),
            author = json.getString("author"),
            createdAt = json.getLong("createdAt"),
            status = ReviewStatus.valueOf(json.getString("status")),
            comments = comments,
            approvals = approvals,
            rejections = rejections
        )
    }
    
    private fun parseComment(json: JSONObject): ReviewComment {
        val replies = mutableListOf<ReviewComment>()
        val repliesArray = json.getJSONArray("replies")
        for (i in 0 until repliesArray.length()) {
            replies.add(parseComment(repliesArray.getJSONObject(i)))
        }
        
        return ReviewComment(
            id = json.getString("id"),
            author = json.getString("author"),
            content = json.getString("content"),
            lineNumber = if (json.has("lineNumber") && !json.isNull("lineNumber")) 
                json.getInt("lineNumber") else null,
            timestamp = json.getLong("timestamp"),
            replies = replies
        )
    }
    
    private fun parseActivity(json: JSONObject): Activity {
        val detailsJson = json.getJSONObject("details")
        val details = mutableMapOf<String, String>()
        detailsJson.keys().forEach { key ->
            details[key] = detailsJson.getString(key)
        }
        
        return Activity(
            id = json.getString("id"),
            type = ActivityType.valueOf(json.getString("type")),
            author = json.getString("author"),
            message = json.getString("message"),
            timestamp = json.getLong("timestamp"),
            details = details
        )
    }
    
    private fun parseTeamMember(json: JSONObject): TeamMember {
        return TeamMember(
            username = json.getString("username"),
            name = json.getString("name"),
            email = json.getString("email"),
            role = TeamRole.valueOf(json.getString("role")),
            avatarUrl = json.optString("avatarUrl")
        )
    }
    
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}