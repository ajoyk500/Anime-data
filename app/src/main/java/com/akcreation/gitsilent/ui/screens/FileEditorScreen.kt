package com.akcreation.gitsilent.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileEditorScreen(
    fileName: String,
    fileContent: String,
    isEditable: Boolean,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var content by remember { mutableStateOf(fileContent) }
    var isModified by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(fileContent) {
        content = fileContent
        isModified = false
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileName, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isModified) {
                            showSaveDialog = true
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditable && isModified) {
                        IconButton(
                            onClick = {
                                onSave(content)
                                isModified = false
                            }
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isEditable) {
                CodeEditor(
                    content = content,
                    onContentChange = { 
                        content = it
                        isModified = true
                    }
                )
            } else {
                CodeViewer(content = content)
            }
        }
        
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Unsaved Changes") },
                text = { Text("Do you want to save your changes before leaving?") },
                confirmButton = {
                    Row {
                        TextButton(
                            onClick = {
                                showSaveDialog = false
                                onBack()
                            }
                        ) {
                            Text("Discard")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                onSave(content)
                                showSaveDialog = false
                                onBack()
                            }
                        ) {
                            Text("Save")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CodeEditor(
    content: String,
    onContentChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Line numbers
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(end = 8.dp)
            ) {
                content.lines().forEachIndexed { index, _ ->
                    Text(
                        text = "${index + 1}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            
            // Code content
            BasicTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .horizontalScroll(horizontalScrollState),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun CodeViewer(content: String) {
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Line numbers
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(end = 8.dp)
            ) {
                content.lines().forEachIndexed { index, _ ->
                    Text(
                        text = "${index + 1}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            
            // Code content
            Text(
                text = content,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .horizontalScroll(horizontalScrollState)
            )
        }
    }
}