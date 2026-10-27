package com.volumepilot.service.automation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.volumepilot.core.common.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeAutomationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: AppLogger
) {

    fun scheduleRecurringCheck() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TimeAutomationReceiver::class.java).apply {
            action = TimeAutomationReceiver.ACTION_TIME_AUTOMATION_TRIGGER
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)

        // Inexact 15-minute interval aligned with Android Doze to maximize battery efficiency (zero CPU wakelock)
        val triggerAtMillis = System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
        logger.d("TimeAutomationScheduler", "Scheduled inexact repeating time automation check")
    }

    fun cancelSchedule() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TimeAutomationReceiver::class.java).apply {
            action = TimeAutomationReceiver.ACTION_TIME_AUTOMATION_TRIGGER
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }

        val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            logger.d("TimeAutomationScheduler", "Cancelled time automation schedule")
        }
    }

    companion object {
        private const val REQUEST_CODE = 4040
    }
}
