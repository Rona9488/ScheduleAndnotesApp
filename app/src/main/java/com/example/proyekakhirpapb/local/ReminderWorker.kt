package com.example.proyekakhirpapb.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Retrieve input data
        val taskTitle = inputData.getString("taskTitle") ?: "Tugas"
        val deadline = inputData.getString("deadline") ?: "Besok"

        // Check for notification permission
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.failure() // Permission not granted, return failure
        }

        // Define notification channel details
        val channelId = "reminder_channel"
        val channelName = "Task Reminders"
        val channelDescription = "Notifications for task deadlines"

        // Create notification channel if it does not exist (API 26+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    importance
                ).apply {
                    description = channelDescription
                }
                val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            } catch (e: SecurityException) {
                // Handle SecurityException if the permission is not granted
                return Result.failure()
            }
        }

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Reminder Tugas")
            .setContentText("$taskTitle harus selesai sebelum $deadline!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically dismiss the notification when tapped
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE) // Play default sound and vibration
            .build()

        // Generate a unique notification ID
        val notificationId = System.currentTimeMillis().toInt()

        // Show the notification
        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)

        return Result.success()
    }
}
