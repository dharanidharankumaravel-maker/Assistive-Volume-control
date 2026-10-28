package com.volumepilot.fakes

import com.volumepilot.domain.model.ThemeMode
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeSettingsRepository(
    initialSettings: UserSettings = UserSettings()
) : SettingsRepository {

    private val _settings = MutableStateFlow(initialSettings)

    override fun getSettings(): Flow<UserSettings> = _settings.asStateFlow()

    override suspend fun updateMaxVolumeLimiter(enabled: Boolean, capPercent: Int): Result<Unit> {
        _settings.update { it.copy(maxVolumeLimiterEnabled = enabled, maxVolumeCapPercent = capPercent) }
        return Result.success(Unit)
    }

    override suspend fun updateHapticFeedback(enabled: Boolean): Result<Unit> {
        _settings.update { it.copy(hapticFeedbackEnabled = enabled) }
        return Result.success(Unit)
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode): Result<Unit> {
        _settings.update { it.copy(themeMode = themeMode) }
        return Result.success(Unit)
    }

    override suspend fun updateStartOnBoot(enabled: Boolean): Result<Unit> {
        _settings.update { it.copy(startOnBoot = enabled) }
        return Result.success(Unit)
    }

    override suspend fun updateOverlayEnabled(enabled: Boolean): Result<Unit> {
        _settings.update { it.copy(isOverlayEnabled = enabled) }
        return Result.success(Unit)
    }

    override suspend fun updateOverlayPosition(position: com.volumepilot.domain.model.OverlayPosition): Result<Unit> {
        _settings.update { it.copy(overlayPosition = position) }
        return Result.success(Unit)
    }

    override suspend fun updateOverlayAppearance(
        opacity: Float,
        sizeDp: Int,
        autoHideDelaySeconds: Int
    ): Result<Unit> {
        _settings.update { it.copy(overlayOpacity = opacity, overlaySizeDp = sizeDp, autoHideDelaySeconds = autoHideDelaySeconds) }
        return Result.success(Unit)
    }

    override suspend fun updatePreferredStreamType(streamType: com.volumepilot.domain.model.AudioStreamType): Result<Unit> {
        _settings.update { it.copy(preferredStreamType = streamType) }
        return Result.success(Unit)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean): Result<Unit> {
        _settings.update { it.copy(hasCompletedOnboarding = completed) }
        return Result.success(Unit)
    }

    override suspend fun updateFloatingWidgetCustomization(
        arrangement: com.volumepilot.domain.model.FloatingButtonArrangement,
        color: com.volumepilot.domain.model.FloatingButtonColor,
        powerSingleTap: com.volumepilot.domain.model.PowerSingleTapAction,
        powerLongPress: com.volumepilot.domain.model.PowerLongPressAction
    ): Result<Unit> {
        _settings.update {
            it.copy(
                buttonArrangement = arrangement,
                widgetColor = color,
                powerSingleTapAction = powerSingleTap,
                powerLongPressAction = powerLongPress
            )
        }
        return Result.success(Unit)
    }

    override suspend fun updateRgbGlowSettings(enabled: Boolean, intensity: Float): Result<Unit> {
        _settings.update { it.copy(rgbGlowEnabled = enabled, rgbGlowIntensity = intensity) }
        return Result.success(Unit)
    }

    override suspend fun updateOverlayHidden(hidden: Boolean): Result<Unit> {
        _settings.update { it.copy(isOverlayHidden = hidden) }
        return Result.success(Unit)
    }
}
