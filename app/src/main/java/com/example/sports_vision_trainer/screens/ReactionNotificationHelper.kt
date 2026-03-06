package com.example.sports_vision_trainer.screens

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object ReactionNotificationHelper {

    private const val CHANNEL_ID = "reaction_channel"

    fun showResultNotification(
        context: Context,
        title: String,
        message: String
    ) {

        createChannel(context)

        // ✅ REQUIRED — permission check for Android 13+
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return   // permission not granted → skip safely
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createChannel(context: Context) {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Reaction Updates",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reaction training session updates"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        manager.createNotificationChannel(channel)
    }
}
