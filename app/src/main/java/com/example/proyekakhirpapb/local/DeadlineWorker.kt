package com.example.proyekakhirpapb.local

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DeadlineWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val tugasTitle = inputData.getString("title") ?: "Tugas"
        val deadline = inputData.getString("deadline") ?: return Result.failure()

        // Cek jika deadline kurang dari 24 jam
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val deadlineDate = sdf.parse(deadline)
        val now = Calendar.getInstance().time
        val difference = deadlineDate?.time?.minus(now.time) ?: Long.MAX_VALUE

        if (difference in 0..86400000) { // 24 jam dalam milidetik
            showNotification(
                context,
                "Pengingat Deadline",
                "Tugas \"$tugasTitle\" mendekati deadline ($deadline)!"
            )
        }

        return Result.success()
    }
}