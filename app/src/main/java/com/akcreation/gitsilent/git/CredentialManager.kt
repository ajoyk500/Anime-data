package com.akcreation.gitsilent.git

import android.content.Context
import android.content.SharedPreferences

class CredentialManager(context: Context) {
    
    // Using regular SharedPreferences for simplicity
    // In production, consider using EncryptedSharedPreferences if available
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("git_credentials", Context.MODE_PRIVATE)
    
    fun saveCredentials(username: String, token: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_TOKEN, token)
            apply()
        }
    }
    
    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }
    
    fun getToken(): String {
        return sharedPreferences.getString(KEY_TOKEN, "") ?: ""
    }
    
    fun saveGitConfig(name: String, email: String) {
        sharedPreferences.edit().apply {
            putString(KEY_GIT_NAME, name)
            putString(KEY_GIT_EMAIL, email)
            apply()
        }
    }
    
    fun getGitName(): String {
        return sharedPreferences.getString(KEY_GIT_NAME, "GitSilent User") ?: "GitSilent User"
    }
    
    fun getGitEmail(): String {
        return sharedPreferences.getString(KEY_GIT_EMAIL, "user@gitsilent.app") ?: "user@gitsilent.app"
    }
    
    fun hasCredentials(): Boolean {
        return getUsername().isNotEmpty() && getToken().isNotEmpty()
    }
    
    fun clearCredentials() {
        sharedPreferences.edit().apply {
            remove(KEY_USERNAME)
            remove(KEY_TOKEN)
            apply()
        }
    }
    
    companion object {
        private const val KEY_USERNAME = "git_username"
        private const val KEY_TOKEN = "git_token"
        private const val KEY_GIT_NAME = "git_name"
        private const val KEY_GIT_EMAIL = "git_email"
    }
}