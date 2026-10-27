package com.volumepilot.feature.automation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volumepilot.domain.model.AudioDeviceRule
import com.volumepilot.domain.model.SafeListeningConfig
import com.volumepilot.domain.model.TimeRule
import com.volumepilot.domain.repository.AutomationRepository
import com.volumepilot.domain.usecase.ExportSettingsUseCase
import com.volumepilot.domain.usecase.GetAutomationConfigUseCase
import com.volumepilot.domain.usecase.GetProfilesUseCase
import com.volumepilot.domain.usecase.ImportSettingsUseCase
import com.volumepilot.domain.usecase.ToggleMasterAutomationUseCase
import com.volumepilot.service.automation.TimeAutomationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutomationViewModel @Inject constructor(
    private val getAutomationConfigUseCase: GetAutomationConfigUseCase,
    private val toggleMasterAutomationUseCase: ToggleMasterAutomationUseCase,
    private val automationRepository: AutomationRepository,
    private val getProfilesUseCase: GetProfilesUseCase,
    private val exportSettingsUseCase: ExportSettingsUseCase,
    private val importSettingsUseCase: ImportSettingsUseCase,
    private val timeAutomationScheduler: TimeAutomationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(AutomationUiState(isLoading = true))
    val uiState: StateFlow<AutomationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getAutomationConfigUseCase(),
                getProfilesUseCase()
            ) { config, profiles ->
                AutomationUiState(
                    config = config,
                    profiles = profiles,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.update { current ->
                    current.copy(
                        config = newState.config,
                        profiles = newState.profiles,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleMasterAutomation(enabled: Boolean) {
        viewModelScope.launch {
            toggleMasterAutomationUseCase(enabled)
            if (enabled) {
                timeAutomationScheduler.scheduleRecurringCheck()
            } else {
                timeAutomationScheduler.cancelSchedule()
            }
        }
    }

    fun saveTimeRule(rule: TimeRule) {
        viewModelScope.launch {
            val result = automationRepository.saveTimeRule(rule)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = "Failed to save time rule") }
            }
        }
    }

    fun deleteTimeRule(ruleId: String) {
        viewModelScope.launch {
            val result = automationRepository.deleteTimeRule(ruleId)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = "Failed to delete time rule") }
            }
        }
    }

    fun saveDeviceRule(rule: AudioDeviceRule) {
        viewModelScope.launch {
            val result = automationRepository.saveDeviceRule(rule)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = "Failed to save device rule") }
            }
        }
    }

    fun updateSafeListeningConfig(config: SafeListeningConfig) {
        viewModelScope.launch {
            val result = automationRepository.updateSafeListeningConfig(config)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = "Failed to update safe listening") }
            }
        }
    }

    fun exportSettings() {
        viewModelScope.launch {
            val result = exportSettingsUseCase()
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        exportedJson = result.getOrNull(),
                        feedbackMessage = "Settings and profiles exported successfully!"
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessage = "Export failed: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun importSettings(jsonString: String) {
        viewModelScope.launch {
            val result = importSettingsUseCase(jsonString)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(feedbackMessage = "Settings and profiles imported successfully!")
                }
            } else {
                _uiState.update { it.copy(errorMessage = "Import failed: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null, exportedJson = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
