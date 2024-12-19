package com.example.proyekakhirpapb.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NotesViewModel(private val noteDao: NotesDAO) : ViewModel() {
    val notes = noteDao.getAllNotes()

    fun insert(note: Notes) {
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

    fun delete(note: Notes) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    fun update(note: Notes) {
        viewModelScope.launch {
            noteDao.update(note) // Tambahkan fungsi ini
        }
    }
}
