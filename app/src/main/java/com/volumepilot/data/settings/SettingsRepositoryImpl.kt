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
                hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
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
