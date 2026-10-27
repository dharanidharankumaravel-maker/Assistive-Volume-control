package com.volumepilot.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volumepilot.core.permissions.PermissionChecker
import com.volumepilot.core.permissions.PermissionType
import com.volumepilot.domain.usecase.GetOemGuidanceUseCase
import com.volumepilot.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val permissionChecker: PermissionChecker,
    private val getOemGuidanceUseCase: GetOemGuidanceUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OnboardingUiState(
            oemGuidance = getOemGuidanceUseCase()
        )
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        refreshPermissions()
    }

    fun refreshPermissions() {
        val overlay = permissionChecker.isPermissionGranted(PermissionType.OVERLAY)
        val audio = permissionChecker.isPermissionGranted(PermissionType.MODIFY_AUDIO)
        val notifications = permissionChecker.isPermissionGranted(PermissionType.POST_NOTIFICATIONS)

        _uiState.update {
            it.copy(
                isOverlayPermissionGranted = overlay,
                isModifyAudioGranted = audio,
                isPostNotificationGranted = notifications
            )
        }
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCompleting = true) }
            updateSettingsUseCase.setOnboardingCompleted(true)
            updateSettingsUseCase.setOverlayEnabled(true)
            onSuccess()
        }
    }
}
