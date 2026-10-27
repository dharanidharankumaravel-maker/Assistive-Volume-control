package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.ThemeMode
import com.volumepilot.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun setMaxVolumeLimiter(enabled: Boolean, capPercent: Int): Result<Unit> {
        return settingsRepository.updateMaxVolumeLimiter(enabled, capPercent)
    }

    suspend fun setHapticFeedback(enabled: Boolean): Result<Unit> {
        return settingsRepository.updateHapticFeedback(enabled)
    }

    suspend fun setThemeMode(themeMode: ThemeMode): Result<Unit> {
        return settingsRepository.updateThemeMode(themeMode)
    }

    suspend fun setStartOnBoot(enabled: Boolean): Result<Unit> {
        return settingsRepository.updateStartOnBoot(enabled)
    }

    suspend fun setOverlayEnabled(enabled: Boolean): Result<Unit> {
        return settingsRepository.updateOverlayEnabled(enabled)
    }

    suspend fun setOverlayPosition(position: com.volumepilot.domain.model.OverlayPosition): Result<Unit> {
        return settingsRepository.updateOverlayPosition(position)
    }

    suspend fun setOverlayAppearance(opacity: Float, sizeDp: Int, autoHideDelaySeconds: Int): Result<Unit> {
        return settingsRepository.updateOverlayAppearance(opacity, sizeDp, autoHideDelaySeconds)
    }

    suspend fun setPreferredStreamType(streamType: com.volumepilot.domain.model.AudioStreamType): Result<Unit> {
        return settingsRepository.updatePreferredStreamType(streamType)
    }

    suspend fun setOnboardingCompleted(completed: Boolean): Result<Unit> {
        return settingsRepository.setOnboardingCompleted(completed)
    }
}
