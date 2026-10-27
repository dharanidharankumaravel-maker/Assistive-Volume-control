package com.volumepilot.service.overlay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.volumepilot.core.common.AppLogger
import com.volumepilot.domain.usecase.GetSettingsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var getSettingsUseCase: GetSettingsUseCase
    @Inject lateinit var logger: AppLogger

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            logger.i("BootReceiver", "Boot completed event received. Checking overlay restore state.")

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val settings = getSettingsUseCase().first()
                    if (settings.startOnBoot && settings.isOverlayEnabled) {
                        if (Settings.canDrawOverlays(context)) {
                            logger.i("BootReceiver", "Restoring VolumePilot overlay service post-reboot.")
                            OverlayService.start(context)
                        } else {
                            logger.w("BootReceiver", "Overlay permission revoked or missing post-reboot.")
                        }
                    }
                } catch (e: Exception) {
                    logger.e("BootReceiver", "Failed to restore overlay on boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
