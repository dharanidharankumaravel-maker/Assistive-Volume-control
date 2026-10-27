package com.volumepilot.service.automation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.volumepilot.core.common.AppLogger
import com.volumepilot.domain.usecase.EvaluateTimeAutomationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class TimeAutomationReceiver : BroadcastReceiver() {

    @Inject lateinit var evaluateTimeAutomationUseCase: EvaluateTimeAutomationUseCase
    @Inject lateinit var logger: AppLogger

    override fun onReceive(context: Context?, intent: Intent?) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 7=Saturday

        logger.d(TAG, "TimeAutomationReceiver fired for $hour:$minute, day $dayOfWeek")
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                evaluateTimeAutomationUseCase(hour, minute, dayOfWeek)
            } catch (e: Exception) {
                logger.e(TAG, "Error evaluating time rules", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "TimeAutomationReceiver"
        const val ACTION_TIME_AUTOMATION_TRIGGER = "com.volumepilot.ACTION_TIME_AUTOMATION_TRIGGER"
    }
}
