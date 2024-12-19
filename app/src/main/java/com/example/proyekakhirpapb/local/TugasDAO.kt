package com.example.proyekakhirpapb.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TugasDAO {

    @Query("SELECT * FROM tugas")
    fun getAllTugas(): LiveData<List<Tugas>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTugas(tugas: Tugas)

    @Query("SELECT * FROM tugas WHERE deadline_tugas = :deadline")
    fun getTugasByDeadline(deadline: String): LiveData<List<Tugas>>

    @Query("UPDATE tugas SET selesai = :selesai WHERE id = :id")
    fun updateStatus(id: Int, selesai: Boolean)

    @Query("UPDATE tugas SET completedDate = :completedDate WHERE id = :id")
    fun updateCompletedDate(id: Int, completedDate: String?)

    @Query("SELECT DISTINCT kategori FROM tugas")
    fun getAllCategories(): LiveData<List<String>>

    @Query("UPDATE tugas SET foto_uri = :fotoUri, deskripsi_foto = :deskripsiFoto WHERE id = :id")
    fun updateFotoDeskripsi(id: Int, fotoUri: String?, deskripsiFoto: String?)

    @Query("DELETE FROM tugas WHERE id = :id")
    fun deleteTugas(id: Int)

    @Query(""" 
        SELECT * FROM tugas 
        WHERE selesai = 1 
        AND deadline_tugas BETWEEN :startOfWeek AND :endOfWeek
    """)
    fun getCompletedTugasThisWeek(startOfWeek: String, endOfWeek: String): LiveData<List<Tugas>>

    @Query(""" 
        SELECT COUNT(*) FROM tugas 
        WHERE selesai = 1 
        AND deadline_tugas BETWEEN :startOfWeek AND :endOfWeek
    """)
    fun getCompletedCountThisWeek(startOfWeek: String, endOfWeek: String): LiveData<Int>
}
