package com.akcreation.gitsilent.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.akcreation.gitsilent.MainActivity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.data.AppDatabase
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.data.repository.RepoRepository
import com.akcreation.gitsilent.git.CredentialManager
import com.akcreation.gitsilent.git.GitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Version 1.2 Feature: Background Sync Service
 * Automatically syncs repositories in background
 */
class BackgroundSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val repoRepository = RepoRepository(AppDatabase.getInstance(context).repoDao())
    private val gitHelper = GitHelper()
    private val credentialManager = CredentialManager(context)
    
    companion object {
        const val WORK_NAME = "git_sync_worker"
        const val CHANNEL_ID = "git_sync_channel"
        const val NOTIFICATION_ID = 1001
        
        /**
         * Schedule periodic sync (every 1 hour)
         */
        fun schedulePeriodic(context: Context, intervalHours: Long = 1) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val syncRequest = PeriodicWorkRequestBuilder<BackgroundSyncWorker>(
                intervalHours, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
        }
        
        /**
         * Schedule one-time sync
         */
        fun scheduleOneTime(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val syncRequest = OneTimeWorkRequestBuilder<BackgroundSyncWorker>()
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context).enqueue(syncRequest)
        }
        
        /**
         * Cancel all sync work
         */
        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            createNotificationChannel()
            setForeground(createForegroundInfo("Syncing repositories..."))
            
            val username = credentialManager.getUsername()
            val token = credentialManager.getToken()
            
            if (username.isEmpty() || token.isEmpty()) {
                return@withContext Result.failure()
            }
            
            // Get all repositories (get first emission from flow)
            var successCount = 0
            var failCount = 0
            val reposList = repoRepository.allRepos.first()
            
            // Sync each repository
            for (repo in reposList) {
                try {
                    // Pull changes
                    val pullResult = gitHelper.pullRepository(repo.path, username, token)
                    
                    if (pullResult.success) {
                        successCount++
                        
                        // Update last sync time - create new entity
                        val updatedRepo = RepoEntity(
                            id = repo.id,
                            name = repo.name,
                            path = repo.path,
                            url = repo.url,
                            currentBranch = repo.currentBranch,
                            lastCommitMessage = repo.lastCommitMessage,
                            lastCommitAuthor = repo.lastCommitAuthor,
                            lastCommitDate = repo.lastCommitDate,
                            lastSyncTime = System.currentTimeMillis(),
                            isFavorite = repo.isFavorite
                        )
                        repoRepository.updateRepo(updatedRepo)
                    } else {
                        failCount++
                    }
                } catch (e: Exception) {
                    failCount++
                }
            }
            
            // Show completion notification
            val message = "Synced: $successCount success, $failCount failed"
            showCompletionNotification(message)
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Git Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background repository synchronization"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createForegroundInfo(message: String): ForegroundInfo {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("GitSilent")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
        
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }
    
    private fun showCompletionNotification(message: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Sync Complete")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }
}

/**
 * Manager class for sync preferences
 */
class SyncManager(private val context: Context) {
    
    private val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
    
    fun enableAutoSync(enabled: Boolean) {
        prefs.edit().putBoolean("auto_sync_enabled", enabled).apply()
        
        if (enabled) {
            val interval = getSyncInterval()
            BackgroundSyncWorker.schedulePeriodic(context, interval)
        } else {
            BackgroundSyncWorker.cancelSync(context)
        }
    }
    
    fun isAutoSyncEnabled(): Boolean {
        return prefs.getBoolean("auto_sync_enabled", false)
    }
    
    fun setSyncInterval(hours: Long) {
        prefs.edit().putLong("sync_interval", hours).apply()
        
        if (isAutoSyncEnabled()) {
            BackgroundSyncWorker.cancelSync(context)
            BackgroundSyncWorker.schedulePeriodic(context, hours)
        }
    }
    
    fun getSyncInterval(): Long {
        return prefs.getLong("sync_interval", 1)
    }
    
    fun setSyncOnWifiOnly(wifiOnly: Boolean) {
        prefs.edit().putBoolean("sync_wifi_only", wifiOnly).apply()
    }
    
    fun isSyncOnWifiOnly(): Boolean {
        return prefs.getBoolean("sync_wifi_only", true)
    }
    
    fun syncNow() {
        BackgroundSyncWorker.scheduleOneTime(context)
    }
    
    fun getLastSyncTime(): Long {
        return prefs.getLong("last_sync_time", 0)
    }
    
    fun setLastSyncTime(time: Long) {
        prefs.edit().putLong("last_sync_time", time).apply()
    }
}