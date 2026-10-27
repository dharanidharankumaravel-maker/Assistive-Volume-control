package com.volumepilot.domain.repository

import com.volumepilot.domain.model.AppRule
import com.volumepilot.domain.model.AudioDeviceRule
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.AutomationConfig
import com.volumepilot.domain.model.SafeListeningConfig
import com.volumepilot.domain.model.TimeRule
import kotlinx.coroutines.flow.Flow

interface AutomationRepository {
    fun getAutomationConfig(): Flow<AutomationConfig>
    suspend fun setMasterAutomationEnabled(enabled: Boolean): Result<Unit>
    suspend fun saveTimeRule(rule: TimeRule): Result<Unit>
    suspend fun deleteTimeRule(ruleId: String): Result<Unit>
    suspend fun saveDeviceRule(rule: AudioDeviceRule): Result<Unit>
    suspend fun saveAppRule(rule: AppRule): Result<Unit>
    suspend fun deleteAppRule(ruleId: String): Result<Unit>
    suspend fun updateSafeListeningConfig(config: SafeListeningConfig): Result<Unit>
    suspend fun evaluateDeviceConnected(device: AudioOutputDevice): Result<Unit>
    suspend fun evaluateTimeRules(hour: Int, minute: Int, dayOfWeek: Int): Result<Unit>
}
