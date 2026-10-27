package com.volumepilot.data.repository

import android.content.Context
import android.os.Build
import com.volumepilot.core.battery.BatteryOptimizationHelper
import com.volumepilot.core.battery.OemSettingsHelper
import com.volumepilot.core.common.AppError
import com.volumepilot.core.common.AppLogger
import com.volumepilot.core.permissions.PermissionChecker
import com.volumepilot.core.permissions.PermissionType
import com.volumepilot.domain.model.DiagnosticState
import com.volumepilot.domain.model.OemGuidance
import com.volumepilot.domain.repository.DiagnosticRepository
import com.volumepilot.domain.repository.SettingsRepository
import com.volumepilot.service.overlay.OverlayService
import com.volumepilot.service.overlay.ServiceStateTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosticRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionChecker: PermissionChecker,
    private val settingsRepository: SettingsRepository,
    private val logger: AppLogger
) : DiagnosticRepository {

    override fun getDiagnosticState(): Flow<DiagnosticState> {
        return combine(
            settingsRepository.getSettings(),
            ServiceStateTracker.isServiceRunning,
            ServiceStateTracker.lastHeartbeat
        ) { settings, isRunning, lastHeartbeat ->
            DiagnosticState(
                isOverlayPermissionGranted = permissionChecker.isPermissionGranted(PermissionType.OVERLAY),
                isModifyAudioGranted = permissionChecker.isPermissionGranted(PermissionType.MODIFY_AUDIO),
                isPostNotificationGranted = permissionChecker.isPermissionGranted(PermissionType.POST_NOTIFICATIONS),
                isBatteryOptimizationIgnored = BatteryOptimizationHelper.isBatteryOptimizationIgnored(context),
                isStartOnBootEnabled = settings.startOnBoot,
                isOverlayServiceRunning = isRunning,
                lastHeartbeatTimestamp = lastHeartbeat,
                deviceManufacturer = Build.MANUFACTURER,
                androidVersion = Build.VERSION.SDK_INT
            )
        }
    }

    override fun getOemGuidance(): OemGuidance {
        return OemSettingsHelper.getGuidance()
    }

    override suspend fun repairOverlay(): Result<Unit> {
        return try {
            val hasOverlayPermission = permissionChecker.isPermissionGranted(PermissionType.OVERLAY)
            if (!hasOverlayPermission) {
                return Result.failure(
                    AppError.AudioPermissionDenied("Display over other apps permission is required to repair the overlay")
                )
            }

            logger.i("DiagnosticRepository", "Attempting overlay service repair and restart.")
            OverlayService.stop(context)
            // Small pause or immediate restart
            settingsRepository.updateOverlayEnabled(true)
            OverlayService.start(context)
            ServiceStateTracker.recordHeartbeat()

            Result.success(Unit)
        } catch (e: Exception) {
            logger.e("DiagnosticRepository", "Failed to repair overlay", e)
            Result.failure(AppError.ServiceInitializationError("OverlayService", e))
        }
    }
}
