package com.example.proyekakhirpapb.local

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TugasRepository(application: Application) {
    private val mTugasDAO: TugasDAO
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = TugasDB.getDatabase(application)
        mTugasDAO = db.tugasDao()
    }

    fun getAllTugas(): LiveData<List<Tugas>> = mTugasDAO.getAllTugas()

    fun insert(tugas: Tugas) {
        executorService.execute { mTugasDAO.insertTugas(tugas) }
    }

    fun updateStatus(id: Int, selesai: Boolean) {
        executorService.execute { mTugasDAO.updateStatus(id, selesai) }
    }

    fun updateCompletedDate(id: Int, completedDate: String) {
        executorService.execute { mTugasDAO.updateCompletedDate(id, completedDate) }
    }

    fun undoTask(id: Int) {
        executorService.execute {
            mTugasDAO.updateStatus(id, false) // Set selesai ke false
            mTugasDAO.updateCompletedDate(id, null) // Set completedDate ke null
        }
    }

    fun getTugasByDeadline(deadline: String): LiveData<List<Tugas>> {
        return mTugasDAO.getTugasByDeadline(deadline)
    }

    fun getAllCategories(): LiveData<List<String>> = mTugasDAO.getAllCategories()

    fun updateFotoDeskripsi(id: Int, fotoUri: String?, deskripsiFoto: String?) {
        executorService.execute {
            mTugasDAO.updateFotoDeskripsi(id, fotoUri, deskripsiFoto)
        }
    }

    fun getCompletedTugasThisWeek(startOfWeek: String, endOfWeek: String): LiveData<List<Tugas>> {
        return mTugasDAO.getCompletedTugasThisWeek(startOfWeek, endOfWeek)
    }

    fun getCompletedCountThisWeek(startOfWeek: String, endOfWeek: String): LiveData<Int> {
        return mTugasDAO.getCompletedCountThisWeek(startOfWeek, endOfWeek)
    }

    // Menambahkan fungsi untuk menghapus tugas
    fun deleteTugas(id: Int) {
        executorService.execute { mTugasDAO.deleteTugas(id) }
    }
}
