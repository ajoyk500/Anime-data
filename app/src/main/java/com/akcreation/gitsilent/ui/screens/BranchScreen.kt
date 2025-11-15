package com.akcreation.gitsilent.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.git.RepoManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchScreen(
    branches: List<RepoManager.BranchInfo>,
    onBack: () -> Unit,
    onBranchSwitch: (String) -> Unit,
    onCreateBranch: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newBranchName by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Branches") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create Branch")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Local branches
            item {
                Text(
                    text = "Local Branches",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            items(branches.filter { !it.isRemote }) { branch ->
                BranchCard(
                    branch = branch,
                    onSwitch = { onBranchSwitch(branch.name) }
                )
            }
            
            // Remote branches
            if (branches.any { it.isRemote }) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Remote Branches",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                items(branches.filter { it.isRemote }) { branch ->
                    BranchCard(
                        branch = branch,
                        onSwitch = null
                    )
                }
            }
        }
        
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create New Branch") },
                text = {
                    OutlinedTextField(
                        value = newBranchName,
                        onValueChange = { newBranchName = it },
                        label = { Text("Branch Name") },
                        placeholder = { Text("feature-name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newBranchName.isNotEmpty()) {
                                onCreateBranch(newBranchName)
                                showCreateDialog = false
                                newBranchName = ""
                            }
                        },
                        enabled = newBranchName.isNotEmpty()
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showCreateDialog = false
                        newBranchName = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun BranchCard(
    branch: RepoManager.BranchInfo,
    onSwitch: (() -> Unit)?
) {
    Card(
        onClick = { onSwitch?.invoke() },
        modifier = Modifier.fillMaxWidth(),
        enabled = onSwitch != null && !branch.isActive,
        colors = if (branch.isActive) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (branch.isRemote) Icons.Default.Cloud else Icons.Default.AccountTree,
                contentDescription = null,
                tint = if (branch.isActive) MaterialTheme.colorScheme.primary 
                      else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = branch.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (branch.isActive) {
                    Text(
                        text = "Current branch",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (branch.isActive) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}