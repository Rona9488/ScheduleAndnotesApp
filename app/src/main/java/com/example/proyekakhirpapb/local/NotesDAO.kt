package com.example.proyekakhirpapb.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Notes)

    @Query("SELECT * FROM notes_table")
    fun getAllNotes(): LiveData<List<Notes>>

    @Delete
    suspend fun delete(note: Notes)

    @Update
    suspend fun update(note: Notes)
}
