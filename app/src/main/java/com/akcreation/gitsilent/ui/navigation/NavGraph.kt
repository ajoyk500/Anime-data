package com.akcreation.gitsilent.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.data.repository.RepoRepository
import com.akcreation.gitsilent.git.CredentialManager
import com.akcreation.gitsilent.git.GitHelper
import com.akcreation.gitsilent.git.RepoManager
import com.akcreation.gitsilent.ui.screens.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RepoDetail : Screen("repo_detail/{repoId}") {
        fun createRoute(repoId: Int) = "repo_detail/$repoId"
    }
    object FileEditor : Screen("file_editor/{repoId}/{filePath}") {
        fun createRoute(repoId: Int, filePath: String) = "file_editor/$repoId/${filePath.replace("/", "|")}"
    }
    object Branches : Screen("branches/{repoId}") {
        fun createRoute(repoId: Int) = "branches/$repoId"
    }
    object History : Screen("history/{repoId}") {
        fun createRoute(repoId: Int) = "history/$repoId"
    }
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    repoRepository: RepoRepository,
    gitHelper: GitHelper,
    repoManager: RepoManager,
    credentialManager: CredentialManager,
    getReposDir: () -> File,
    showToast: (String) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()
    val repos by repoRepository.allRepos.collectAsState(initial = emptyList())
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            var isCloning by remember { mutableStateOf(false) }
            var cloneMessage by remember { mutableStateOf("") }
            
            HomeScreen(
                repos = repos,
                onRepoClick = { repoId ->
                    navController.navigate(Screen.RepoDetail.createRoute(repoId))
                },
                onCloneClick = { url, name ->
                    isCloning = true
                    scope.launch {
                        try {
                            val reposDir = getReposDir()
                            val repoPath = File(reposDir, name).absolutePath
                            
                            showToast("Cloning $name...")
                            
                            val result = withContext(Dispatchers.IO) {
                                gitHelper.cloneRepository(
                                    url = url,
                                    localPath = repoPath,
                                    username = credentialManager.getUsername(),
                                    token = credentialManager.getToken()
                                )
                            }
                            
                            if (result.success) {
                                // Get current branch
                                val branch = withContext(Dispatchers.IO) {
                                    val status = repoManager.getRepoStatus(repoPath)
                                    status?.currentBranch ?: "main"
                                }
                                
                                // Save to database
                                val repoEntity = RepoEntity(
                                    name = name,
                                    path = repoPath,
                                    url = url,
                                    currentBranch = branch,
                                    lastSyncTime = System.currentTimeMillis()
                                )
                                
                                withContext(Dispatchers.IO) {
                                    repoRepository.insertRepo(repoEntity)
                                }
                                
                                showToast("✅ Repository cloned successfully!")
                            } else {
                                showToast("❌ Clone failed: ${result.message}")
                            }
                        } catch (e: Exception) {
                            showToast("❌ Error: ${e.message}")
                        } finally {
                            isCloning = false
                        }
                    }
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onDeleteRepo = { repo ->
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            // Delete local files
                            val repoDir = File(repo.path)
                            if (repoDir.exists()) {
                                repoDir.deleteRecursively()
                            }
                            // Delete from database
                            repoRepository.deleteRepo(repo)
                        }
                        showToast("Repository deleted")
                    }
                }
            )
            
            // Show loading dialog while cloning
            if (isCloning) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Cloning Repository") },
                    text = { 
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Please wait...")
                        }
                    },
                    confirmButton = { }
                )
            }
        }
        
        // Repo Detail Screen
        composable(
            route = Screen.RepoDetail.route,
            arguments = listOf(navArgument("repoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getInt("repoId") ?: return@composable
            
            var repo by remember { mutableStateOf<RepoEntity?>(null) }
            var currentDirectory by remember { mutableStateOf("") }
            var files by remember { mutableStateOf<List<RepoManager.FileItem>>(emptyList()) }
            var stagedFiles by remember { mutableStateOf<List<String>>(emptyList()) }
            var hasChanges by remember { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(false) }
            var showCommitDialog by remember { mutableStateOf(false) }
            var commitMessage by remember { mutableStateOf("") }
            
            fun loadFiles() {
                scope.launch {
                    repo?.let { r ->
                        withContext(Dispatchers.IO) {
                            files = repoManager.getFilesInRepo(r.path, currentDirectory)
                            val status = repoManager.getRepoStatus(r.path)
                            hasChanges = status?.hasChanges ?: false
                            stagedFiles = gitHelper.getStagedFiles(r.path)
                        }
                    }
                }
            }
            
            LaunchedEffect(repoId) {
                repo = withContext(Dispatchers.IO) {
                    repoRepository.getRepoById(repoId)
                }
                loadFiles()
            }
            
            repo?.let { r ->
                RepoDetailScreen(
                    repoName = r.name,
                    repoPath = r.path,
                    currentBranch = r.currentBranch,
                    currentDirectory = currentDirectory,
                    files = files,
                    stagedFiles = stagedFiles,
                    hasChanges = hasChanges,
                    onBack = { navController.popBackStack() },
                    onPull = {
                        scope.launch {
                            isLoading = true
                            val result = withContext(Dispatchers.IO) {
                                gitHelper.pullRepository(
                                    r.path,
                                    credentialManager.getUsername(),
                                    credentialManager.getToken()
                                )
                            }
                            isLoading = false
                            showToast(result.message)
                            if (result.success) {
                                loadFiles()
                            }
                        }
                    },
                    onPush = {
                        scope.launch {
                            isLoading = true
                            val result = withContext(Dispatchers.IO) {
                                gitHelper.pushRepository(
                                    r.path,
                                    credentialManager.getUsername(),
                                    credentialManager.getToken()
                                )
                            }
                            isLoading = false
                            showToast(result.message)
                        }
                    },
                    onCommit = {
                        showCommitDialog = true
                    },
                    onStage = {
                        scope.launch {
                            val result = withContext(Dispatchers.IO) {
                                gitHelper.stageAllChanges(r.path)
                            }
                            showToast(result.message)
                            if (result.success) {
                                loadFiles()
                            }
                        }
                    },
                    onBranches = {
                        navController.navigate(Screen.Branches.createRoute(repoId))
                    },
                    onHistory = {
                        navController.navigate(Screen.History.createRoute(repoId))
                    },
                    onFileClick = { file ->
                        navController.navigate(
                            Screen.FileEditor.createRoute(repoId, file.path)
                        )
                    },
                    onDirectoryClick = { newPath ->
                        currentDirectory = newPath
                        loadFiles()
                    },
                    onRefresh = {
                        loadFiles()
                    }
                )
                
                // Commit Dialog
                if (showCommitDialog) {
                    AlertDialog(
                        onDismissRequest = { showCommitDialog = false },
                        title = { Text("Commit Changes") },
                        text = {
                            Column {
                                Text(
                                    "${stagedFiles.size} files staged",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = commitMessage,
                                    onValueChange = { commitMessage = it },
                                    label = { Text("Commit Message") },
                                    placeholder = { Text("Update files") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (commitMessage.isNotEmpty()) {
                                        scope.launch {
                                            isLoading = true
                                            val result = withContext(Dispatchers.IO) {
                                                gitHelper.commitChanges(
                                                    r.path,
                                                    commitMessage,
                                                    credentialManager.getGitName(),
                                                    credentialManager.getGitEmail()
                                                )
                                            }
                                            isLoading = false
                                            showToast(result.message)
                                            if (result.success) {
                                                commitMessage = ""
                                                showCommitDialog = false
                                                loadFiles()
                                            }
                                        }
                                    }
                                },
                                enabled = commitMessage.isNotEmpty()
                            ) {
                                Text("Commit")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { 
                                showCommitDialog = false
                                commitMessage = ""
                            }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
        
        // File Editor Screen
        composable(
            route = Screen.FileEditor.route,
            arguments = listOf(
                navArgument("repoId") { type = NavType.IntType },
                navArgument("filePath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getInt("repoId") ?: return@composable
            val filePath = backStackEntry.arguments?.getString("filePath")?.replace("|", "/") ?: return@composable
            
            var repo by remember { mutableStateOf<RepoEntity?>(null) }
            var fileContent by remember { mutableStateOf("") }
            var fileName by remember { mutableStateOf("") }
            
            LaunchedEffect(repoId, filePath) {
                repo = withContext(Dispatchers.IO) {
                    repoRepository.getRepoById(repoId)
                }
                repo?.let { r ->
                    withContext(Dispatchers.IO) {
                        val file = File(r.path, filePath)
                        fileName = file.name
                        fileContent = if (file.exists()) file.readText() else ""
                    }
                }
            }
            
            repo?.let { r ->
                FileEditorScreen(
                    fileName = fileName,
                    fileContent = fileContent,
                    isEditable = true,
                    onBack = { navController.popBackStack() },
                    onSave = { content ->
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                val file = File(r.path, filePath)
                                file.writeText(content)
                            }
                            showToast("File saved")
                        }
                    }
                )
            }
        }
        
        // Branches Screen
        composable(
            route = Screen.Branches.route,
            arguments = listOf(navArgument("repoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getInt("repoId") ?: return@composable
            
            var repo by remember { mutableStateOf<RepoEntity?>(null) }
            var branches by remember { mutableStateOf<List<RepoManager.BranchInfo>>(emptyList()) }
            
            LaunchedEffect(repoId) {
                repo = withContext(Dispatchers.IO) {
                    repoRepository.getRepoById(repoId)
                }
                repo?.let { r ->
                    branches = withContext(Dispatchers.IO) {
                        repoManager.getBranches(r.path)
                    }
                }
            }
            
            repo?.let { r ->
                BranchScreen(
                    branches = branches,
                    onBack = { navController.popBackStack() },
                    onBranchSwitch = { branchName ->
                        scope.launch {
                            val success = withContext(Dispatchers.IO) {
                                repoManager.switchBranch(r.path, branchName)
                            }
                            if (success) {
                                showToast("Switched to $branchName")
                                navController.popBackStack()
                            } else {
                                showToast("Failed to switch branch")
                            }
                        }
                    },
                    onCreateBranch = { branchName ->
                        scope.launch {
                            val success = withContext(Dispatchers.IO) {
                                repoManager.createBranch(r.path, branchName)
                            }
                            if (success) {
                                showToast("Branch created: $branchName")
                                branches = withContext(Dispatchers.IO) {
                                    repoManager.getBranches(r.path)
                                }
                            } else {
                                showToast("Failed to create branch")
                            }
                        }
                    }
                )
            }
        }
        
        // Commit History Screen
        composable(
            route = Screen.History.route,
            arguments = listOf(navArgument("repoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getInt("repoId") ?: return@composable
            
            var repo by remember { mutableStateOf<RepoEntity?>(null) }
            var commits by remember { mutableStateOf<List<GitHelper.CommitInfo>>(emptyList()) }
            
            LaunchedEffect(repoId) {
                repo = withContext(Dispatchers.IO) {
                    repoRepository.getRepoById(repoId)
                }
                repo?.let { r ->
                    commits = withContext(Dispatchers.IO) {
                        gitHelper.getCommitHistory(r.path, 100)
                    }
                }
            }
            
            CommitHistoryScreen(
                commits = commits,
                onBack = { navController.popBackStack() }
            )
        }
        
        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                gitName = credentialManager.getGitName(),
                gitEmail = credentialManager.getGitEmail(),
                username = credentialManager.getUsername(),
                token = credentialManager.getToken(),
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() },
                onSaveGitConfig = { name, email ->
                    credentialManager.saveGitConfig(name, email)
                    showToast("Git configuration saved")
                },
                onSaveCredentials = { username, token ->
                    credentialManager.saveCredentials(username, token)
                    showToast("Credentials saved")
                },
                onToggleDarkMode = onDarkModeToggle
            )
        }
    }
}