package com.example.proyekakhirpapb
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.proyekakhirpapb.local.Tugas
import com.example.proyekakhirpapb.local.TugasRepository
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TugasRepository = TugasRepository(application)
    val tugasList: LiveData<List<Tugas>> = repository.getAllTugas()

    // Fungsi untuk mengambil jumlah tugas yang selesai
    fun getCompletedTasks(): LiveData<List<Tugas>> {
        return tugasList.map { list -> list.filter { it.selesai } }
    }

    // Fungsi untuk mengambil jumlah tugas yang belum selesai
    fun getPendingTasks(): LiveData<List<Tugas>> {
        return tugasList.map { list -> list.filter { !it.selesai } }
    }

    // Fungsi untuk mengambil tugas yang deadline-nya dalam 7 hari
    fun getTugasWithUpcomingDeadlines(): LiveData<List<Tugas>> {
        val currentDate = Calendar.getInstance()  // Mengambil waktu sekarang
        val sevenDaysLater = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }  // Menambahkan 7 hari

        return tugasList.map { list ->
            list.filter { task ->
                val taskDeadline = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.deadlineTugas)
                val taskCalendar = Calendar.getInstance().apply { time = taskDeadline }

                // Memeriksa apakah deadline tugas berada dalam jangka waktu 7 hari dan tugas belum selesai
                !task.selesai &&
                        taskCalendar.after(currentDate) &&
                        taskCalendar.before(sevenDaysLater)
            }
        }
    }

    // Fungsi untuk mengambil tugas yang selesai dalam minggu ini
    fun getCompletedTugasThisWeek(): LiveData<List<Tugas>> {
        val dates = getCurrentWeekRange()
        return tugasList.map { list ->
            list.filter { task ->
                val completedDate = task.completedDate // Asumsi bahwa ada completedDate di objek Tugas
                completedDate?.isNotEmpty() == true && isDateWithinRange(completedDate, dates.first, dates.second)
            }
        }
    }
    // Fungsi untuk mengambil tugas yang deadline-nya sudah lewat tetapi belum selesai
    fun getOverduePendingTasks(): LiveData<List<Tugas>> {
        val currentDate = Calendar.getInstance().time  // Mengambil waktu sekarang

        return tugasList.map { list ->
            list.filter { task ->
                val taskDeadline = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.deadlineTugas)
                // Memeriksa apakah deadline tugas sudah lewat dan status tugas belum selesai
                taskDeadline != null && taskDeadline.before(currentDate) && !task.selesai
            }
        }
    }

    // Fungsi untuk mendapatkan rentang tanggal minggu ini
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

    // Fungsi untuk memeriksa apakah tanggal berada dalam rentang
    private fun isDateWithinRange(date: String, startDate: String, endDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateToCheck = sdf.parse(date)
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)

        return dateToCheck.after(start) && dateToCheck.before(end)
    }
}
