package com.akcreation.gitsilent.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class GitConfig(
    val id: String,
    val name: String,
    val email: String
)

data class GitCredential(
    val id: String,
    val username: String,
    val token: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    gitName: String,
    gitEmail: String,
    username: String,
    token: String,
    isDarkMode: Boolean,
    onBack: () -> Unit,
    onSaveGitConfig: (String, String) -> Unit,
    onSaveCredentials: (String, String) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit
) {
    var gitConfigList by remember { mutableStateOf(mutableListOf<GitConfig>()) }
    var gitCredentialList by remember { mutableStateOf(mutableListOf<GitCredential>()) }
    
    // Initialize with existing data
    LaunchedEffect(gitName, gitEmail) {
        if (gitName.isNotBlank() && gitEmail.isNotBlank()) {
            if (gitConfigList.isEmpty()) {
                gitConfigList.add(GitConfig("1", gitName, gitEmail))
            }
        }
    }
    
    LaunchedEffect(username, token) {
        if (username.isNotBlank() && token.isNotBlank()) {
            if (gitCredentialList.isEmpty()) {
                gitCredentialList.add(GitCredential("1", username, token))
            }
        }
    }
    
    var showGitConfigList by remember { mutableStateOf(false) }
    var showCredentialList by remember { mutableStateOf(false) }
    var showGitConfigDialog by remember { mutableStateOf(false) }
    var showCredentialDialog by remember { mutableStateOf(false) }
    var editingConfig: GitConfig? by remember { mutableStateOf(null) }
    var editingCredential: GitCredential? by remember { mutableStateOf(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf("") }
    var deleteType by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Git Configuration
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showGitConfigList = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Git Configuration", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${gitConfigList.size} config(s) saved",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }

            // Git Credentials
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCredentialList = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Git Credentials (HTTPS)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${gitCredentialList.size} credential(s) saved",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }

            // Dark Mode
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Enable dark theme",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(checked = isDarkMode, onCheckedChange = onToggleDarkMode)
                }
            }

            // About
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("GitSilent v1.0.0", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "A lightweight, offline-first Git client for Android",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Git Config List Dialog
    if (showGitConfigList) {
        Dialog(onDismissRequest = { showGitConfigList = false }) {
            Card(modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Git Configuration", style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = { showGitConfigList = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    
                    Divider()
                    
                    if (gitConfigList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f).padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "No configuration yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(onClick = {
                                    editingConfig = null
                                    showGitConfigList = false
                                    showGitConfigDialog = true
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Configuration")
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            gitConfigList.forEach { config ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(config.name, style = MaterialTheme.typography.titleMedium)
                                        }
                                        Row {
                                            IconButton(onClick = {
                                                editingConfig = config
                                                showGitConfigList = false
                                                showGitConfigDialog = true
                                            }) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(onClick = {
                                                deleteId = config.id
                                                deleteType = "config"
                                                showDeleteDialog = true
                                            }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Add button at bottom
                            Button(
                                onClick = {
                                    editingConfig = null
                                    showGitConfigList = false
                                    showGitConfigDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Configuration")
                            }
                        }
                    }
                }
            }
        }
    }

    // Credential List Dialog
    if (showCredentialList) {
        Dialog(onDismissRequest = { showCredentialList = false }) {
            Card(modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Git Credentials (HTTPS)", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "Required for push/pull with private repos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showCredentialList = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    
                    Divider()
                    
                    if (gitCredentialList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f).padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "No credentials yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(onClick = {
                                    editingCredential = null
                                    showCredentialList = false
                                    showCredentialDialog = true
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Credentials")
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            gitCredentialList.forEach { credential ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(credential.username, style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                "Token: ${"•".repeat(20)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Row {
                                            IconButton(onClick = {
                                                editingCredential = credential
                                                showCredentialList = false
                                                showCredentialDialog = true
                                            }) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(onClick = {
                                                deleteId = credential.id
                                                deleteType = "credential"
                                                showDeleteDialog = true
                                            }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Add button at bottom
                            Button(
                                onClick = {
                                    editingCredential = null
                                    showCredentialList = false
                                    showCredentialDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Credentials")
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete ${if (deleteType == "config") "Configuration" else "Credentials"}?") },
            text = { Text("Are you sure you want to delete this ${if (deleteType == "config") "configuration" else "credential"}?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (deleteType == "config") {
                            gitConfigList.removeAll { it.id == deleteId }
                            if (gitConfigList.isEmpty()) showGitConfigList = false
                        } else {
                            gitCredentialList.removeAll { it.id == deleteId }
                            if (gitCredentialList.isEmpty()) showCredentialList = false
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add/Edit Config Dialog
    if (showGitConfigDialog) {
        var name by remember { mutableStateOf(editingConfig?.name ?: "") }
        var email by remember { mutableStateOf(editingConfig?.email ?: "") }
        
        AlertDialog(
            onDismissRequest = {
                showGitConfigDialog = false
                editingConfig = null
            },
            title = { Text(if (editingConfig != null) "Edit Configuration" else "Add Configuration") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingConfig != null) {
                            gitConfigList.replaceAll { if (it.id == editingConfig!!.id) GitConfig(it.id, name, email) else it }
                        } else {
                            gitConfigList.add(GitConfig(System.currentTimeMillis().toString(), name, email))
                        }
                        onSaveGitConfig(name, email)
                        showGitConfigDialog = false
                        editingConfig = null
                        showGitConfigList = true
                    },
                    enabled = name.isNotBlank() && email.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showGitConfigDialog = false
                    editingConfig = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add/Edit Credential Dialog
    if (showCredentialDialog) {
        var user by remember { mutableStateOf(editingCredential?.username ?: "") }
        var pass by remember { mutableStateOf(editingCredential?.token ?: "") }
        var showPassword by remember { mutableStateOf(false) }
        
        AlertDialog(
            onDismissRequest = {
                showCredentialDialog = false
                editingCredential = null
            },
            title = { Text(if (editingCredential != null) "Edit Credentials" else "Add Credentials") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Required for push/pull operations with private repositories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Personal Access Token") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showPassword) "Hide" else "Show"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingCredential != null) {
                            gitCredentialList.replaceAll { if (it.id == editingCredential!!.id) GitCredential(it.id, user, pass) else it }
                        } else {
                            gitCredentialList.add(GitCredential(System.currentTimeMillis().toString(), user, pass))
                        }
                        onSaveCredentials(user, pass)
                        showCredentialDialog = false
                        editingCredential = null
                        showCredentialList = true
                    },
                    enabled = user.isNotBlank() && pass.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCredentialDialog = false
                    editingCredential = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}