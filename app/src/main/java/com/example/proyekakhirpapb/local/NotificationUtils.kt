package com.example.proyekakhirpapb.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun showNotification(context: Context, title: String, message: String) {
    val channelId = "deadline_notification_channel"

    // Periksa izin notifikasi (Untuk Android 13 ke atas)
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        // Bisa meminta izin atau memberi tahu pengguna
        return
    }

    // Membuat Notification Channel untuk API 26+
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Deadline Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for approaching deadlines"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    // Konfigurasi notifikasi
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true) // Automatically dismiss the notification when tapped
        .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE) // Play default sound and vibration

    // Kirim notifikasi tanpa ID
    NotificationManagerCompat.from(context).notify(0, builder.build()) // 0 is used here as a default ID
}
