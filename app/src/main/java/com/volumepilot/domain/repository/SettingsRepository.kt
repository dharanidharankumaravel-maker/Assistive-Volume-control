package com.volumepilot.domain.repository

import com.volumepilot.domain.model.ThemeMode
import com.volumepilot.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<UserSettings>
    suspend fun updateMaxVolumeLimiter(enabled: Boolean, capPercent: Int): Result<Unit>
    suspend fun updateHapticFeedback(enabled: Boolean): Result<Unit>
    suspend fun updateThemeMode(themeMode: ThemeMode): Result<Unit>
    suspend fun updateStartOnBoot(enabled: Boolean): Result<Unit>
    suspend fun updateOverlayEnabled(enabled: Boolean): Result<Unit>
    suspend fun updateOverlayPosition(position: com.volumepilot.domain.model.OverlayPosition): Result<Unit>
    suspend fun updateOverlayAppearance(opacity: Float, sizeDp: Int, autoHideDelaySeconds: Int): Result<Unit>
    suspend fun updatePreferredStreamType(streamType: com.volumepilot.domain.model.AudioStreamType): Result<Unit>
    suspend fun setOnboardingCompleted(completed: Boolean): Result<Unit>
    suspend fun updateFloatingWidgetCustomization(
        arrangement: com.volumepilot.domain.model.FloatingButtonArrangement,
        color: com.volumepilot.domain.model.FloatingButtonColor,
        powerSingleTap: com.volumepilot.domain.model.PowerSingleTapAction,
        powerLongPress: com.volumepilot.domain.model.PowerLongPressAction
    ): Result<Unit>
    suspend fun updateRgbGlowSettings(enabled: Boolean, intensity: Float): Result<Unit>
    suspend fun updateOverlayHidden(hidden: Boolean): Result<Unit>
}
