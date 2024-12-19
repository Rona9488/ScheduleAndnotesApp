package com.example.proyekakhirpapb.local

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TugasViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TugasRepository = TugasRepository(application)

    val tugasList: LiveData<List<Tugas>> = repository.getAllTugas()
    fun insert(tugas: Tugas) {
        viewModelScope.launch {
            repository.insert(tugas)
        }
    }

    fun updateStatus(id: Int, selesai: Boolean) {
        viewModelScope.launch {
            repository.updateStatus(id, selesai)
        }
    }

    fun updateCompletedDate(id: Int, completedDate: String) {
        viewModelScope.launch {
            repository.updateCompletedDate(id, completedDate)
        }
    }

    fun undoTask(id: Int) {
        viewModelScope.launch {
            repository.undoTask(id)
        }
    }

    // Menambahkan fungsi hapus tugas
    fun deleteTugas(id: Int) {
        repository.deleteTugas(id)
    }


    private fun getCurrentWeekRange(): Pair<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Start of the week (Sunday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startOfWeek = sdf.format(calendar.time)

        // End of the week (Saturday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = sdf.format(calendar.time)

        return Pair(startOfWeek, endOfWeek)
    }
}
