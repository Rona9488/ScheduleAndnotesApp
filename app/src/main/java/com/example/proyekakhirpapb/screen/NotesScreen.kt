package com.example.proyekakhirpapb.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyekakhirpapb.local.Notes
import com.example.proyekakhirpapb.local.NotesDatabase
import com.example.proyekakhirpapb.local.NotesViewModel
import com.example.proyekakhirpapb.local.NotesViewModelFactory

import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NotesScreen() {
    val context = LocalContext.current
    val notesDao = NotesDatabase.getDatabase(context).notesDao()

    val noteViewModel: NotesViewModel = viewModel(
        factory = NotesViewModelFactory(notesDao)
    )

    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showEditNoteDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Notes?>(null) }
    var editNote by remember { mutableStateOf<Notes?>(null) }

    val notes: List<Notes> by noteViewModel.notes.observeAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kosong",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notes.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            pair.forEach { note ->
                                NoteItem(
                                    note = note,
                                    onClick = { selectedNote = note },
                                    onEdit = {
                                        editNote = it
                                        noteTitle = it.title
                                        noteContent = it.content
                                        showEditNoteDialog = true
                                    },
                                    onDelete = { noteViewModel.delete(it) }
                                )
                            }

                            if (pair.size == 1) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(150.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button di pojok kanan bawah
        FloatingActionButton(
            onClick = { showAddNoteDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Note", tint = Color.White)
        }
    }

    // Dialog untuk menambahkan catatan
    if (showAddNoteDialog) {
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text(text = "Add Note") },
            text = {
                Column {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Content") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                            noteViewModel.insert(Notes(title = noteTitle, content = noteContent))
                            noteTitle = ""
                            noteContent = ""
                            showAddNoteDialog = false
                        }
                    }
                ) {
                    Text("Add Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog untuk mengedit catatan
    if (showEditNoteDialog) {
        AlertDialog(
            onDismissRequest = { showEditNoteDialog = false },
            title = { Text(text = "Edit Note") },
            text = {
                Column {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Content") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        editNote?.let {
                            noteViewModel.update(it.copy(title = noteTitle, content = noteContent))
                            editNote = null
                        }
                        noteTitle = ""
                        noteContent = ""
                        showEditNoteDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditNoteDialog = false
                    editNote = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog untuk pratinjau catatan
    selectedNote?.let { note ->
        AlertDialog(
            onDismissRequest = { selectedNote = null },
            title = { Text(text = note.title) },
            text = { Text(text = note.content) },
            confirmButton = {
                TextButton(onClick = { selectedNote = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun NoteItem(
    note: Notes,
    onClick: () -> Unit,
    onEdit: (Notes) -> Unit,
    onDelete: (Notes) -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp) // Menentukan lebar tetap untuk card
            .padding(horizontal = 8.dp, vertical = 4.dp) // Menambahkan jarak kecil antara card
            .height(150.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onEdit(note) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                }
                IconButton(onClick = { onDelete(note) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}