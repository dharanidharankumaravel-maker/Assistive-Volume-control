package com.volumepilot.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.volumepilot.core.common.AppError
import com.volumepilot.core.common.AppLogger
import com.volumepilot.data.local.PreferencesKeys
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.OverlayPosition
import com.volumepilot.domain.model.ThemeMode
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger
) : SettingsRepository {

    override fun getSettings(): Flow<UserSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                logger.w("SettingsRepository", "IOException reading DataStore preferences", exception)
                emit(emptyPreferences())
            } else {
                logger.e("SettingsRepository", "Unexpected error reading DataStore", exception)
                throw exception
            }
        }
        .map { preferences ->
            val themeString = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeString)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }

            val streamTypeString = preferences[PreferencesKeys.PREFERRED_STREAM_TYPE] ?: AudioStreamType.MEDIA.name
            val preferredStream = try {
                AudioStreamType.valueOf(streamTypeString)
            } catch (e: Exception) {
                AudioStreamType.MEDIA
            }

            val posX = preferences[PreferencesKeys.OVERLAY_POSITION_X] ?: 0
            val posY = preferences[PreferencesKeys.OVERLAY_POSITION_Y] ?: 200
            val isSnappedLeft = preferences[PreferencesKeys.OVERLAY_SNAPPED_LEFT] ?: false

            val arrangementStr = preferences[PreferencesKeys.BUTTON_ARRANGEMENT] ?: com.volumepilot.domain.model.FloatingButtonArrangement.POWER_UP_DOWN.name
            val arrangement = try {
                com.volumepilot.domain.model.FloatingButtonArrangement.valueOf(arrangementStr)
            } catch (e: Exception) {
                com.volumepilot.domain.model.FloatingButtonArrangement.POWER_UP_DOWN
            }

            val colorStr = preferences[PreferencesKeys.WIDGET_COLOR] ?: com.volumepilot.domain.model.FloatingButtonColor.BASIL.name
            val color = try {
                com.volumepilot.domain.model.FloatingButtonColor.valueOf(colorStr)
            } catch (e: Exception) {
                com.volumepilot.domain.model.FloatingButtonColor.BASIL
            }

            val singleTapStr = preferences[PreferencesKeys.POWER_SINGLE_TAP_ACTION] ?: com.volumepilot.domain.model.PowerSingleTapAction.LOCK_SCREEN.name
            val singleTap = try {
                com.volumepilot.domain.model.PowerSingleTapAction.valueOf(singleTapStr)
            } catch (e: Exception) {
                com.volumepilot.domain.model.PowerSingleTapAction.LOCK_SCREEN
            }

            val longPressStr = preferences[PreferencesKeys.POWER_LONG_PRESS_ACTION] ?: com.volumepilot.domain.model.PowerLongPressAction.TAKE_SCREENSHOT.name
            val longPress = try {
                com.volumepilot.domain.model.PowerLongPressAction.valueOf(longPressStr)
            } catch (e: Exception) {
                com.volumepilot.domain.model.PowerLongPressAction.TAKE_SCREENSHOT
            }

            val rgbGlowEnabled = preferences[PreferencesKeys.RGB_GLOW_ENABLED] ?: true
            val rgbGlowIntensity = preferences[PreferencesKeys.RGB_GLOW_INTENSITY] ?: 0.6f
            val isOverlayHidden = preferences[PreferencesKeys.IS_OVERLAY_HIDDEN] ?: false

            val sliderStyleStr = preferences[PreferencesKeys.SLIDER_STYLE] ?: com.volumepilot.domain.model.SliderStyle.GOOGLE.name
            val sliderStyle = try {
                com.volumepilot.domain.model.SliderStyle.valueOf(sliderStyleStr)
            } catch (e: Exception) {
                com.volumepilot.domain.model.SliderStyle.GOOGLE
            }
            val sliderAccentColor = preferences[PreferencesKeys.SLIDER_ACCENT_COLOR] ?: 0xFF7C3AEDL
            val sliderWidthDp = preferences[PreferencesKeys.SLIDER_WIDTH_DP] ?: sliderStyle.defaultWidthDp
            val sliderHeightDp = preferences[PreferencesKeys.SLIDER_HEIGHT_DP] ?: sliderStyle.defaultHeightDp
            val sliderCornerRadiusDp = preferences[PreferencesKeys.SLIDER_CORNER_RADIUS_DP] ?: sliderStyle.defaultCornerRadiusDp
            val sliderBackgroundAlpha = preferences[PreferencesKeys.SLIDER_BACKGROUND_ALPHA] ?: 0.5f
            val rgbSpeedMillis = preferences[PreferencesKeys.RGB_SPEED_MILLIS] ?: 3500

            UserSettings(
                maxVolumeLimiterEnabled = preferences[PreferencesKeys.MAX_VOLUME_LIMITER_ENABLED] ?: false,
                maxVolumeCapPercent = preferences[PreferencesKeys.MAX_VOLUME_CAP_PERCENT] ?: 80,
                hapticFeedbackEnabled = preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] ?: true,
                themeMode = themeMode,
                startOnBoot = preferences[PreferencesKeys.START_ON_BOOT] ?: false,
                autoHideDelaySeconds = preferences[PreferencesKeys.AUTO_HIDE_DELAY_SECONDS] ?: 3,
                overlayOpacity = preferences[PreferencesKeys.OVERLAY_OPACITY] ?: 0.85f,
                overlaySizeDp = preferences[PreferencesKeys.OVERLAY_SIZE_DP] ?: 48,
                isOverlayEnabled = preferences[PreferencesKeys.IS_OVERLAY_ENABLED] ?: false,
                overlayPosition = com.volumepilot.domain.model.OverlayPosition(posX, posY, isSnappedLeft),
                preferredStreamType = preferredStream,
                hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false,
                buttonArrangement = arrangement,
                widgetColor = color,
                powerSingleTapAction = singleTap,
                powerLongPressAction = longPress,
                rgbGlowEnabled = rgbGlowEnabled,
                rgbGlowIntensity = rgbGlowIntensity,
                isOverlayHidden = isOverlayHidden,
                sliderStyle = sliderStyle,
                sliderAccentColor = sliderAccentColor,
                sliderWidthDp = sliderWidthDp,
                sliderHeightDp = sliderHeightDp,
                sliderCornerRadiusDp = sliderCornerRadiusDp,
                sliderBackgroundAlpha = sliderBackgroundAlpha,
                rgbSpeedMillis = rgbSpeedMillis
            )
        }

    override suspend fun updateMaxVolumeLimiter(enabled: Boolean, capPercent: Int): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.MAX_VOLUME_LIMITER_ENABLED] = enabled
            preferences[PreferencesKeys.MAX_VOLUME_CAP_PERCENT] = capPercent
        }

    override suspend fun updateHapticFeedback(enabled: Boolean): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] = enabled
        }

    override suspend fun updateThemeMode(themeMode: ThemeMode): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }

    override suspend fun updateStartOnBoot(enabled: Boolean): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.START_ON_BOOT] = enabled
        }

    override suspend fun updateOverlayEnabled(enabled: Boolean): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.IS_OVERLAY_ENABLED] = enabled
        }

    override suspend fun updateOverlayPosition(position: com.volumepilot.domain.model.OverlayPosition): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.OVERLAY_POSITION_X] = position.x
            preferences[PreferencesKeys.OVERLAY_POSITION_Y] = position.y
            preferences[PreferencesKeys.OVERLAY_SNAPPED_LEFT] = position.isSnappedToLeft
        }

    override suspend fun updateOverlayAppearance(
        opacity: Float,
        sizeDp: Int,
        autoHideDelaySeconds: Int
    ): Result<Unit> = safeEdit { preferences ->
        preferences[PreferencesKeys.OVERLAY_OPACITY] = opacity
        preferences[PreferencesKeys.OVERLAY_SIZE_DP] = sizeDp
        preferences[PreferencesKeys.AUTO_HIDE_DELAY_SECONDS] = autoHideDelaySeconds
    }

    override suspend fun updatePreferredStreamType(streamType: com.volumepilot.domain.model.AudioStreamType): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.PREFERRED_STREAM_TYPE] = streamType.name
        }

    override suspend fun setOnboardingCompleted(completed: Boolean): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = completed
        }

    override suspend fun updateFloatingWidgetCustomization(
        arrangement: com.volumepilot.domain.model.FloatingButtonArrangement,
        color: com.volumepilot.domain.model.FloatingButtonColor,
        powerSingleTap: com.volumepilot.domain.model.PowerSingleTapAction,
        powerLongPress: com.volumepilot.domain.model.PowerLongPressAction
    ): Result<Unit> = safeEdit { preferences ->
        preferences[PreferencesKeys.BUTTON_ARRANGEMENT] = arrangement.name
        preferences[PreferencesKeys.WIDGET_COLOR] = color.name
        preferences[PreferencesKeys.POWER_SINGLE_TAP_ACTION] = powerSingleTap.name
        preferences[PreferencesKeys.POWER_LONG_PRESS_ACTION] = powerLongPress.name
    }

    override suspend fun updateRgbGlowSettings(enabled: Boolean, intensity: Float): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.RGB_GLOW_ENABLED] = enabled
            preferences[PreferencesKeys.RGB_GLOW_INTENSITY] = intensity
        }

    override suspend fun updateOverlayHidden(hidden: Boolean): Result<Unit> =
        safeEdit { preferences ->
            preferences[PreferencesKeys.IS_OVERLAY_HIDDEN] = hidden
        }

    override suspend fun updateSliderStyleCustomization(
        sliderStyle: com.volumepilot.domain.model.SliderStyle,
        accentColor: Long,
        widthDp: Int,
        heightDp: Int,
        cornerRadiusDp: Int,
        backgroundAlpha: Float,
        rgbSpeedMillis: Int
    ): Result<Unit> = safeEdit { preferences ->
        preferences[PreferencesKeys.SLIDER_STYLE] = sliderStyle.name
        preferences[PreferencesKeys.SLIDER_ACCENT_COLOR] = accentColor
        preferences[PreferencesKeys.SLIDER_WIDTH_DP] = widthDp
        preferences[PreferencesKeys.SLIDER_HEIGHT_DP] = heightDp
        preferences[PreferencesKeys.SLIDER_CORNER_RADIUS_DP] = cornerRadiusDp
        preferences[PreferencesKeys.SLIDER_BACKGROUND_ALPHA] = backgroundAlpha
        preferences[PreferencesKeys.RGB_SPEED_MILLIS] = rgbSpeedMillis
    }

    private suspend fun safeEdit(action: (androidx.datastore.preferences.core.MutablePreferences) -> Unit): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                action(preferences)
            }
            Result.success(Unit)
        } catch (e: IOException) {
            logger.e("SettingsRepository", "Failed to write to DataStore", e)
            Result.failure(AppError.SettingsStorageError("Disk I/O error", e))
        } catch (e: Exception) {
            logger.e("SettingsRepository", "Unexpected error updating settings", e)
            Result.failure(AppError.Unexpected("Settings update error", e))
        }
    }
}
