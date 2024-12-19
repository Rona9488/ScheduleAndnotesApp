package com.example.proyekakhirpapb.local

import android.content.Context
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun scheduleDeadlineReminder(context: Context, title: String, deadline: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val deadlineDate = sdf.parse(deadline)
    val now = Calendar.getInstance().time
    val delay = deadlineDate?.time?.minus(now.time) ?: return

    if (delay > TimeUnit.HOURS.toMillis(1)) {
        val workRequest = OneTimeWorkRequestBuilder<DeadlineWorker>()
            .setInitialDelay(delay - TimeUnit.HOURS.toMillis(1), TimeUnit.MILLISECONDS) // Notifikasi 1 jam sebelum deadline
            .setInputData(
                workDataOf(
                    "title" to title,
                    "deadline" to deadline
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}