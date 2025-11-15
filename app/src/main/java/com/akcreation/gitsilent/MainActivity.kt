package com.akcreation.gitsilent

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.akcreation.gitsilent.data.AppDatabase
import com.akcreation.gitsilent.data.repository.RepoRepository
import com.akcreation.gitsilent.git.CredentialManager
import com.akcreation.gitsilent.git.GitHelper
import com.akcreation.gitsilent.git.RepoManager
import com.akcreation.gitsilent.ui.navigation.NavGraph
import com.akcreation.gitsilent.ui.theme.GitSilentTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
    
    private lateinit var repoRepository: RepoRepository
    private lateinit var gitHelper: GitHelper
    private lateinit var repoManager: RepoManager
    private lateinit var credentialManager: CredentialManager
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (!allGranted) {
            Toast.makeText(
                this,
                "Storage permission is required to clone repositories",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize components
        val database = AppDatabase.getInstance(applicationContext)
        repoRepository = RepoRepository(database.repoDao())
        gitHelper = GitHelper()
        repoManager = RepoManager()
        credentialManager = CredentialManager(applicationContext)
        
        // Request permissions
        checkAndRequestPermissions()
        
        setContent {
            var isDarkMode by remember { 
                mutableStateOf(getSharedPreferences("settings", MODE_PRIVATE)
                    .getBoolean("dark_mode", false))
            }
            
            GitSilentTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        repoRepository = repoRepository,
                        gitHelper = gitHelper,
                        repoManager = repoManager,
                        credentialManager = credentialManager,
                        getReposDir = { getReposDirectory() },
                        showToast = { message -> showToast(message) },
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = { enabled ->
                            isDarkMode = enabled
                            getSharedPreferences("settings", MODE_PRIVATE)
                                .edit()
                                .putBoolean("dark_mode", enabled)
                                .apply()
                        }
                    )
                }
            }
        }
    }
    
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+
            if (!android.os.Environment.isExternalStorageManager()) {
                // Request All Files Access permission
                try {
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    )
                    intent.data = android.net.Uri.parse("package:$packageName")
                    startActivity(intent)
                    showToast("Please grant 'All Files Access' permission to save repositories in GitHub folder")
                } catch (e: Exception) {
                    val intent = android.content.Intent()
                    intent.action = android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivity(intent)
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-10
            val permissions = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            
            if (permissions.isNotEmpty()) {
                requestPermissionLauncher.launch(permissions.toTypedArray())
            }
        }
    }
    
    private fun getReposDirectory(): File {
        // Use external storage (accessible via file manager)
        val externalStorage = android.os.Environment.getExternalStorageDirectory()
        val githubDir = File(externalStorage, "GitHub")
        
        if (!githubDir.exists()) {
            githubDir.mkdirs()
        }
        
        return githubDir
    }
    
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}