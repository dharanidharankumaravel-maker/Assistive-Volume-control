package com.volumepilot.service.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.volumepilot.MainActivity
import com.volumepilot.R

/**
 * Helper for managing VolumePilot's persistent foreground notification.
 *
 * Implements a compliant compact custom RemoteViews notification layout exposing
 * a single-tap directly visible Show / Hide toggle button in the default collapsed view.
 */
object OverlayNotificationHelper {

    const val CHANNEL_ID = "volume_pilot_overlay_channel"
    const val NOTIFICATION_ID = 1001

    const val ACTION_HIDE = "com.volumepilot.action.HIDE_OVERLAY"
    const val ACTION_SHOW = "com.volumepilot.action.SHOW_OVERLAY"
    const val ACTION_TOGGLE = "com.volumepilot.action.TOGGLE_OVERLAY"
    const val ACTION_PAUSE = "com.volumepilot.action.PAUSE_OVERLAY"
    const val ACTION_RESUME = "com.volumepilot.action.RESUME_OVERLAY"
    const val ACTION_STOP = "com.volumepilot.action.STOP_SERVICE"

    private const val REQUEST_CODE_OPEN = 100
    private const val REQUEST_CODE_STOP = 103

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

    fun buildForegroundNotification(context: Context, isHidden: Boolean): Notification {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        // Tap notification body to open MainActivity
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val openAppPendingIntent = PendingIntent.getActivity(context, REQUEST_CODE_OPEN, openAppIntent, flags)

        // Single-Tap Show / Hide Action: Distinct request codes ensure fresh, instantaneous dispatch
        val toggleTargetAction = if (isHidden) ACTION_SHOW else ACTION_HIDE
        val toggleActionIntent = Intent(context, OverlayService::class.java).apply {
            action = toggleTargetAction
        }
        val toggleRequestCode = if (isHidden) 111 else 112
        val togglePendingIntent = PendingIntent.getService(
            context,
            toggleRequestCode,
            toggleActionIntent,
            flags
        )

        // Stop Service Action
        val stopActionIntent = Intent(context, OverlayService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(context, REQUEST_CODE_STOP, stopActionIntent, flags)

        val toggleButtonTitle = if (isHidden) "Show" else "Hide"
        val statusText = if (isHidden) {
            "Floating controls hidden • Tap Show to restore"
        } else {
            "Floating controls active • Tap to adjust volume"
        }

        // 1. Custom Collapsed RemoteView: Show / Hide button is directly visible with 1 tap in collapsed view
        val compactView = RemoteViews(context.packageName, R.layout.notification_overlay_compact).apply {
            setTextViewText(R.id.notification_title, context.getString(R.string.notification_title))
            setTextViewText(R.id.notification_subtitle, statusText)
            setTextViewText(R.id.btn_notification_toggle, toggleButtonTitle)
            setOnClickPendingIntent(R.id.btn_notification_toggle, togglePendingIntent)
            setOnClickPendingIntent(R.id.notification_icon, openAppPendingIntent)
            setOnClickPendingIntent(R.id.notification_title, openAppPendingIntent)
            setOnClickPendingIntent(R.id.notification_subtitle, openAppPendingIntent)
        }

        // 2. Custom Expanded RemoteView: Visible when user pulls down notification card
        val expandedView = RemoteViews(context.packageName, R.layout.notification_overlay_expanded).apply {
            setTextViewText(R.id.notification_title_expanded, context.getString(R.string.notification_title))
            setTextViewText(R.id.notification_subtitle_expanded, statusText)
            setTextViewText(R.id.btn_notification_toggle_expanded, toggleButtonTitle)
            setOnClickPendingIntent(R.id.btn_notification_toggle_expanded, togglePendingIntent)
            setOnClickPendingIntent(R.id.btn_notification_stop, stopPendingIntent)
            setOnClickPendingIntent(R.id.notification_icon_expanded, openAppPendingIntent)
            setOnClickPendingIntent(R.id.notification_title_expanded, openAppPendingIntent)
            setOnClickPendingIntent(R.id.notification_subtitle_expanded, openAppPendingIntent)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(statusText)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(compactView)
            .setCustomBigContentView(expandedView)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .addAction(0, toggleButtonTitle, togglePendingIntent)
            .addAction(0, context.getString(R.string.notification_action_stop), stopPendingIntent)
            .build()
    }
}
