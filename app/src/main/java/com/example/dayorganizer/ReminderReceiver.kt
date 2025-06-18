package com.example.dayorganizer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Напоминание"
        val desc = intent.getStringExtra("desc") ?: ""

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "card_reminder_channel"

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channel = NotificationChannel(
            channelId,
            "Card Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            setSound(soundUri, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            )
        }

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.calendar_round_icon)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }
}