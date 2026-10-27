package com.volumepilot.fakes

import com.volumepilot.core.common.AppError
import com.volumepilot.domain.model.DiagnosticState
import com.volumepilot.domain.model.OemGuidance
import com.volumepilot.domain.model.OemManufacturer
import com.volumepilot.domain.repository.DiagnosticRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeDiagnosticRepository : DiagnosticRepository {

    private val _diagnosticState = MutableStateFlow(
        DiagnosticState(
            isOverlayPermissionGranted = true,
            isModifyAudioGranted = true,
            isPostNotificationGranted = true,
            isBatteryOptimizationIgnored = true,
            isStartOnBootEnabled = false,
            isOverlayServiceRunning = true,
            lastHeartbeatTimestamp = 1000L,
            deviceManufacturer = "Google",
            androidVersion = 35
        )
    )

    var shouldFailRepair: Boolean = false

    override fun getDiagnosticState(): Flow<DiagnosticState> = _diagnosticState.asStateFlow()

    override fun getOemGuidance(): OemGuidance {
        return OemGuidance(
            manufacturer = OemManufacturer.GOOGLE,
            headline = "Google Pixel Setup",
            explanation = "Optimized for Pixel devices.",
            steps = listOf("Check battery usage.")
        )
    }

    override suspend fun repairOverlay(): Result<Unit> {
        if (shouldFailRepair) {
            return Result.failure(AppError.ServiceInitializationError("OverlayService"))
        }
        _diagnosticState.update {
            it.copy(
                isOverlayServiceRunning = true,
                lastHeartbeatTimestamp = System.currentTimeMillis()
            )
        }
        return Result.success(Unit)
    }

    fun setOverlayPermissionGranted(granted: Boolean) {
        _diagnosticState.update { it.copy(isOverlayPermissionGranted = granted) }
    }
}
