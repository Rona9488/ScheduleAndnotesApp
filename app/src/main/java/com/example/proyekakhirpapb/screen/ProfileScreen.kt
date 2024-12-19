package com.example.proyekakhirpapb.screen

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyekakhirpapb.HomeViewModel
import com.example.proyekakhirpapb.local.Tugas
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(homeViewModel: HomeViewModel = viewModel()) {
    val completedTasks by homeViewModel.getCompletedTugasThisWeek().observeAsState(listOf())
    val pendingTasks by homeViewModel.getPendingTasks().observeAsState(listOf())
    val upcomingTasks by homeViewModel.getTugasWithUpcomingDeadlines().observeAsState(listOf())
    val overdueTasks by homeViewModel.getOverduePendingTasks().observeAsState(listOf()) // Menambahkan daftar tugas yang terlambat

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Tasks Overview",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TaskOverviewCard(
                title = "Completed Tasks",
                count = completedTasks.size,
                color = Color(0xFFDDF2FF),
                modifier = Modifier.weight(1f)
            )
            TaskOverviewCard(
                title = "Pending Tasks",
                count = pendingTasks.size,
                color = Color(0xFFFFEEDD),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Completion of Daily Tasks Section
        Text(text = "Completion of Daily Tasks", style = MaterialTheme.typography.titleMedium)
        DailyTaskChart(completedTasks)

        Spacer(modifier = Modifier.height(16.dp))

        // Tasks in Next 7 Days Section
        Text(text = "Tasks in Next 7 Days", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(upcomingTasks) { tugas ->
                TaskItem(tugas)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overdue Pending Tasks Section
        Text(text = "Overdue Pending Tasks", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(overdueTasks) { tugas ->
                TaskItem(tugas) // Menampilkan tugas yang overdue
            }
        }
    }
}

@Composable
fun DailyTaskChart(completedTasks: List<Tugas>) {
    // Konversi completedDate ke format hari, dengan memeriksa apakah completedDate tidak null
    val tasksPerDay = completedTasks.groupingBy {
        getDayOfWeek(it.completedDate ?: "")  // Jika completedDate null, gunakan string kosong
    }.eachCount()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color(0xFFF8F9FA)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        val maxCount = tasksPerDay.values.maxOrNull() ?: 1

        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
            val count = tasksPerDay[day] ?: 0
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .height((count * 100 / maxCount).dp)
                        .width(16.dp)
                        .background(Color.Blue, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = day, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}


// Fungsi untuk konversi completedDate menjadi nama hari
fun getDayOfWeek(date: String): String {
    return try {
        // Format tanggal yang diterima (misalnya "yyyy-MM-dd")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)

        // Menggunakan Calendar untuk mengambil hari dalam minggu
        val calendar = Calendar.getInstance().apply {
            time = parsedDate
        }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Mengonversi nomor hari menjadi nama hari dalam singkatan (misalnya "Tue" untuk Tuesday)
        when (dayOfWeek) {
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            else -> ""
        }
    } catch (e: Exception) {
        ""
    }
}

@Composable
fun TaskOverviewCard(title: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "$count", fontSize = 36.sp, color = Color.Black)
            Text(text = title, fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun TaskItem(tugas: Tugas) {
    val currentDate = Calendar.getInstance().time // Mengambil waktu sekarang

    // Mengonversi deadlineTugas ke objek Date
    val taskDeadline = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(tugas.deadlineTugas)

    // Cek apakah deadline sudah lewat
    val isOverdue = taskDeadline?.before(currentDate) == true

    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isOverdue) Color(0xFFFFE5E5) // Warna pastel merah lembut
                    else Color(0xFFDDF2FF)          // Warna pastel biru lembut
                ) // Mengubah warna latar belakang sesuai kondisi
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tugas.matkul, style = MaterialTheme.typography.titleSmall)
                    Text(text = tugas.detailTugas, style = MaterialTheme.typography.bodySmall)
                    Text(text = "Deadline: ${tugas.deadlineTugas}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}