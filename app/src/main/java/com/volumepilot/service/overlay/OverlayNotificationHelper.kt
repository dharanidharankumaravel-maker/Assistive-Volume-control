package com.volumepilot.service.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.volumepilot.MainActivity
import com.volumepilot.R

object OverlayNotificationHelper {

    const val CHANNEL_ID = "volume_pilot_overlay_channel"
    const val NOTIFICATION_ID = 1001

    const val ACTION_PAUSE = "com.volumepilot.action.PAUSE_OVERLAY"
    const val ACTION_RESUME = "com.volumepilot.action.RESUME_OVERLAY"
    const val ACTION_STOP = "com.volumepilot.action.STOP_SERVICE"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildForegroundNotification(context: Context, isPaused: Boolean): Notification {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        // Tap notification to open MainActivity
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, flags)

        // Pause / Resume Action
        val pauseResumeActionIntent = Intent(context, OverlayService::class.java).apply {
            action = if (isPaused) ACTION_RESUME else ACTION_PAUSE
        }
        val pauseResumePendingIntent = PendingIntent.getService(context, 1, pauseResumeActionIntent, flags)

        // Stop Service Action
        val stopActionIntent = Intent(context, OverlayService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(context, 2, stopActionIntent, flags)

        val pauseResumeTitle = context.getString(
            if (isPaused) R.string.notification_action_resume else R.string.notification_action_pause
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(
                if (isPaused) "Overlay is paused" else context.getString(R.string.notification_text)
            )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(openAppPendingIntent)
            .addAction(0, pauseResumeTitle, pauseResumePendingIntent)
            .addAction(0, context.getString(R.string.notification_action_stop), stopPendingIntent)
            .build()
    }
}
