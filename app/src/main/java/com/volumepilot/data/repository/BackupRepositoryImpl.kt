package com.volumepilot.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.volumepilot.core.common.AppLogger
import com.volumepilot.data.local.JsonAdapters
import com.volumepilot.data.local.PreferencesKeys
import com.volumepilot.domain.repository.AutomationRepository
import com.volumepilot.domain.repository.BackupRepository
import com.volumepilot.domain.repository.ProfileRepository
import com.volumepilot.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val profileRepository: ProfileRepository,
    private val automationRepository: AutomationRepository,
    private val settingsRepository: SettingsRepository,
    private val logger: AppLogger
) : BackupRepository {

    override suspend fun exportBackupJson(): Result<String> {
        return try {
            val profiles = profileRepository.getProfiles().first()
            val automation = automationRepository.getAutomationConfig().first()
            val settings = settingsRepository.getSettings().first()

            val root = JSONObject()
            root.put("version", 1)
            root.put("exportTimestampMs", System.currentTimeMillis())
            root.put("profilesJson", JsonAdapters.profilesToJsonString(profiles))
            root.put("automationJson", JsonAdapters.automationConfigToJson(automation))
            root.put("settingsJson", JsonAdapters.userSettingsToJson(settings).toString())

            Result.success(root.toString(2))
        } catch (e: Exception) {
            logger.e("BackupRepository", "Failed to export backup JSON", e)
            Result.failure(e)
        }
    }

    override suspend fun importBackupJson(jsonString: String): Result<Unit> {
        return try {
            val root = JSONObject(jsonString)
            val version = root.optInt("version", 1)
            if (version < 1) {
                return Result.failure(IllegalArgumentException("Unsupported backup version: $version"))
            }

            val profilesJson = root.optString("profilesJson", "")
            val automationJson = root.optString("automationJson", "")
            val settingsJson = root.optString("settingsJson", "")

            dataStore.edit { preferences ->
                if (profilesJson.isNotBlank()) {
                    preferences[PreferencesKeys.PROFILES_JSON] = profilesJson
                }
                if (automationJson.isNotBlank()) {
                    preferences[PreferencesKeys.AUTOMATION_CONFIG_JSON] = automationJson
                }
                if (settingsJson.isNotBlank()) {
                    val settingsObj = JSONObject(settingsJson)
                    val settings = JsonAdapters.jsonToUserSettings(settingsObj)

                    preferences[PreferencesKeys.MAX_VOLUME_LIMITER_ENABLED] = settings.maxVolumeLimiterEnabled
                    preferences[PreferencesKeys.MAX_VOLUME_CAP_PERCENT] = settings.maxVolumeCapPercent
                    preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] = settings.hapticFeedbackEnabled
                    preferences[PreferencesKeys.THEME_MODE] = settings.themeMode.name
                    preferences[PreferencesKeys.START_ON_BOOT] = settings.startOnBoot
                    preferences[PreferencesKeys.AUTO_HIDE_DELAY_SECONDS] = settings.autoHideDelaySeconds
                    preferences[PreferencesKeys.OVERLAY_OPACITY] = settings.overlayOpacity
                    preferences[PreferencesKeys.OVERLAY_SIZE_DP] = settings.overlaySizeDp
                    preferences[PreferencesKeys.IS_OVERLAY_ENABLED] = settings.isOverlayEnabled
                    preferences[PreferencesKeys.PREFERRED_STREAM_TYPE] = settings.preferredStreamType.name
                    preferences[PreferencesKeys.OVERLAY_POSITION_X] = settings.overlayPosition.x
                    preferences[PreferencesKeys.OVERLAY_POSITION_Y] = settings.overlayPosition.y
                    preferences[PreferencesKeys.OVERLAY_SNAPPED_LEFT] = settings.overlayPosition.isSnappedToLeft
                }
            }

            logger.i("BackupRepository", "Successfully imported backup JSON")
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e("BackupRepository", "Failed to import backup JSON", e)
            Result.failure(e)
        }
    }
}
