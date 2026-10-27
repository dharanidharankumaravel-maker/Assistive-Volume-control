package com.volumepilot.fakes

import com.volumepilot.domain.model.AppRule
import com.volumepilot.domain.model.AudioDeviceRule
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.AutomationConfig
import com.volumepilot.domain.model.SafeListeningConfig
import com.volumepilot.domain.model.TimeRule
import com.volumepilot.domain.repository.AutomationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAutomationRepository(
    initialConfig: AutomationConfig = AutomationConfig()
) : AutomationRepository {

    private val configFlow = MutableStateFlow(initialConfig)

    var lastEvaluatedDevice: AudioOutputDevice? = null
    var lastEvaluatedTime: Triple<Int, Int, Int>? = null
    var activatedProfileId: String? = null

    override fun getAutomationConfig(): Flow<AutomationConfig> = configFlow.asStateFlow()

    override suspend fun setMasterAutomationEnabled(enabled: Boolean): Result<Unit> {
        configFlow.value = configFlow.value.copy(isMasterAutomationEnabled = enabled)
        return Result.success(Unit)
    }

    override suspend fun saveTimeRule(rule: TimeRule): Result<Unit> {
        val rules = configFlow.value.timeRules.toMutableList()
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) rules[index] = rule else rules.add(rule)
        configFlow.value = configFlow.value.copy(timeRules = rules)
        return Result.success(Unit)
    }

    override suspend fun deleteTimeRule(ruleId: String): Result<Unit> {
        val rules = configFlow.value.timeRules.filterNot { it.id == ruleId }
        configFlow.value = configFlow.value.copy(timeRules = rules)
        return Result.success(Unit)
    }

    override suspend fun saveDeviceRule(rule: AudioDeviceRule): Result<Unit> {
        val rules = configFlow.value.deviceRules.toMutableList()
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) rules[index] = rule else rules.add(rule)
        configFlow.value = configFlow.value.copy(deviceRules = rules)
        return Result.success(Unit)
    }

    override suspend fun saveAppRule(rule: AppRule): Result<Unit> {
        val rules = configFlow.value.appRules.toMutableList()
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) rules[index] = rule else rules.add(rule)
        configFlow.value = configFlow.value.copy(appRules = rules)
        return Result.success(Unit)
    }

    override suspend fun deleteAppRule(ruleId: String): Result<Unit> {
        val rules = configFlow.value.appRules.filterNot { it.id == ruleId }
        configFlow.value = configFlow.value.copy(appRules = rules)
        return Result.success(Unit)
    }

    override suspend fun updateSafeListeningConfig(config: SafeListeningConfig): Result<Unit> {
        configFlow.value = configFlow.value.copy(safeListeningConfig = config)
        return Result.success(Unit)
    }

    override suspend fun evaluateDeviceConnected(device: AudioOutputDevice): Result<Unit> {
        lastEvaluatedDevice = device
        val rule = configFlow.value.deviceRules.find { it.deviceType == device && it.isEnabled }
        if (rule != null && configFlow.value.isMasterAutomationEnabled) {
            activatedProfileId = rule.profileId
        }
        return Result.success(Unit)
    }

    override suspend fun evaluateTimeRules(hour: Int, minute: Int, dayOfWeek: Int): Result<Unit> {
        lastEvaluatedTime = Triple(hour, minute, dayOfWeek)
        val rule = configFlow.value.timeRules.find { it.matchesTime(hour, minute, dayOfWeek) && it.isEnabled }
        if (rule != null && configFlow.value.isMasterAutomationEnabled) {
            activatedProfileId = rule.profileId
        }
        return Result.success(Unit)
    }
}
