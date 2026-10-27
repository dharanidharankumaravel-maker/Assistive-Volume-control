package com.volumepilot.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.volumepilot.core.common.AppLogger
import com.volumepilot.data.local.JsonAdapters
import com.volumepilot.data.local.PreferencesKeys
import com.volumepilot.domain.model.AppRule
import com.volumepilot.domain.model.AudioDeviceRule
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.AutomationConfig
import com.volumepilot.domain.model.SafeListeningConfig
import com.volumepilot.domain.model.TimeRule
import com.volumepilot.domain.repository.AutomationRepository
import com.volumepilot.domain.usecase.ActivateProfileUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AutomationRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val activateProfileUseCaseProvider: Provider<ActivateProfileUseCase>,
    private val logger: AppLogger
) : AutomationRepository {

    override fun getAutomationConfig(): Flow<AutomationConfig> = dataStore.data.map { preferences ->
        val json = preferences[PreferencesKeys.AUTOMATION_CONFIG_JSON]
        JsonAdapters.jsonStringToAutomationConfig(json)
    }

    override suspend fun setMasterAutomationEnabled(enabled: Boolean): Result<Unit> {
        return safeUpdate { config ->
            config.copy(isMasterAutomationEnabled = enabled)
        }
    }

    override suspend fun saveTimeRule(rule: TimeRule): Result<Unit> {
        return safeUpdate { config ->
            val rules = config.timeRules.toMutableList()
            val index = rules.indexOfFirst { it.id == rule.id }
            if (index >= 0) {
                rules[index] = rule
            } else {
                rules.add(rule)
            }
            config.copy(timeRules = rules)
        }
    }

    override suspend fun deleteTimeRule(ruleId: String): Result<Unit> {
        return safeUpdate { config ->
            val rules = config.timeRules.filterNot { it.id == ruleId }
            config.copy(timeRules = rules)
        }
    }

    override suspend fun saveDeviceRule(rule: AudioDeviceRule): Result<Unit> {
        return safeUpdate { config ->
            val rules = config.deviceRules.toMutableList()
            val index = rules.indexOfFirst { it.id == rule.id }
            if (index >= 0) {
                rules[index] = rule
            } else {
                rules.add(rule)
            }
            config.copy(deviceRules = rules)
        }
    }

    override suspend fun saveAppRule(rule: AppRule): Result<Unit> {
        return safeUpdate { config ->
            val rules = config.appRules.toMutableList()
            val index = rules.indexOfFirst { it.id == rule.id }
            if (index >= 0) {
                rules[index] = rule
            } else {
                rules.add(rule)
            }
            config.copy(appRules = rules)
        }
    }

    override suspend fun deleteAppRule(ruleId: String): Result<Unit> {
        return safeUpdate { config ->
            val rules = config.appRules.filterNot { it.id == ruleId }
            config.copy(appRules = rules)
        }
    }

    override suspend fun updateSafeListeningConfig(config: SafeListeningConfig): Result<Unit> {
        return safeUpdate { current ->
            current.copy(safeListeningConfig = config)
        }
    }

    override suspend fun evaluateDeviceConnected(device: AudioOutputDevice): Result<Unit> {
        val config = getAutomationConfig().first()
        if (!config.isMasterAutomationEnabled) {
            logger.d("AutomationRepository", "Master automation disabled; skipping device change evaluation.")
            return Result.success(Unit)
        }

        val rule = config.deviceRules.find { it.deviceType == device && it.isEnabled }
        if (rule != null) {
            logger.i("AutomationRepository", "Device rule matched for $device: activating profile ${rule.profileId}")
            activateProfileUseCaseProvider.get().invoke(rule.profileId)
        }
        return Result.success(Unit)
    }

    override suspend fun evaluateTimeRules(hour: Int, minute: Int, dayOfWeek: Int): Result<Unit> {
        val config = getAutomationConfig().first()
        if (!config.isMasterAutomationEnabled) {
            return Result.success(Unit)
        }

        val matchingRule = config.timeRules.find { it.matchesTime(hour, minute, dayOfWeek) }
        if (matchingRule != null) {
            logger.i("AutomationRepository", "Time rule matched: ${matchingRule.name}, activating ${matchingRule.profileId}")
            activateProfileUseCaseProvider.get().invoke(matchingRule.profileId)
        }
        return Result.success(Unit)
    }

    private suspend fun safeUpdate(transform: (AutomationConfig) -> AutomationConfig): Result<Unit> {
        return try {
            val current = getAutomationConfig().first()
            val updated = transform(current)
            val json = JsonAdapters.automationConfigToJson(updated)
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.AUTOMATION_CONFIG_JSON] = json
            }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e("AutomationRepository", "Failed to update automation config", e)
            Result.failure(e)
        }
    }
}
