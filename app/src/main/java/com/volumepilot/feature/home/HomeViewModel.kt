package com.volumepilot.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volumepilot.core.common.AppError
import com.volumepilot.core.permissions.PermissionChecker
import com.volumepilot.core.permissions.PermissionType
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumePreset
import com.volumepilot.domain.usecase.EnforceMaxVolumeCapUseCase
import com.volumepilot.domain.usecase.GetSettingsUseCase
import com.volumepilot.domain.usecase.GetVolumeStateUseCase
import com.volumepilot.domain.usecase.MuteUseCase
import com.volumepilot.domain.usecase.SetVolumeUseCase
import com.volumepilot.domain.usecase.ToggleVibrateUseCase
import com.volumepilot.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getVolumeStateUseCase: GetVolumeStateUseCase,
    private val setVolumeUseCase: SetVolumeUseCase,
    private val muteUseCase: MuteUseCase,
    private val toggleVibrateUseCase: ToggleVibrateUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val enforceMaxVolumeCapUseCase: EnforceMaxVolumeCapUseCase,
    private val permissionChecker: PermissionChecker
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getVolumeStateUseCase(),
                getSettingsUseCase(),
                com.volumepilot.service.accessibility.VolumeAccessibilityService.isConnected
            ) { volumeState, settings, _ ->
                val overlayGranted = permissionChecker.isPermissionGranted(PermissionType.OVERLAY)
                val dndGranted = permissionChecker.isPermissionGranted(PermissionType.NOTIFICATION_POLICY)
                val status = permissionChecker.getAccessibilityStatus()
                val accessibilityGranted = status == com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.ENABLED

                HomeUiState(
                    isLoading = false,
                    volumeState = volumeState,
                    settings = settings,
                    error = null,
                    isOverlayPermissionGranted = overlayGranted,
                    isNotificationPolicyGranted = dndGranted,
                    isAccessibilityGranted = accessibilityGranted,
                    accessibilityStatus = status
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun refreshPermissions() {
        val overlayGranted = permissionChecker.isPermissionGranted(PermissionType.OVERLAY)
        val dndGranted = permissionChecker.isPermissionGranted(PermissionType.NOTIFICATION_POLICY)
        val status = permissionChecker.getAccessibilityStatus()
        val accessibilityGranted = status == com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.ENABLED

        _uiState.update { current ->
            current.copy(
                isOverlayPermissionGranted = overlayGranted,
                isNotificationPolicyGranted = dndGranted,
                isAccessibilityGranted = accessibilityGranted,
                accessibilityStatus = status
            )
        }
    }

    fun onVolumeChange(streamType: AudioStreamType, requestedLevel: Int) {
        viewModelScope.launch {
            val result = setVolumeUseCase(streamType, requestedLevel)
            result.onFailure { error ->
                _uiState.update { it.copy(error = (error as? AppError) ?: AppError.Unexpected("Failed to change volume", error)) }
            }
        }
    }

    fun onMuteToggle(streamType: AudioStreamType) {
        val stream = _uiState.value.volumeState.getStream(streamType) ?: return
        viewModelScope.launch {
            val nextMuted = !stream.isMuted
            val result = muteUseCase(streamType, nextMuted)
            result.onFailure { error ->
                _uiState.update { it.copy(error = (error as? AppError) ?: AppError.Unexpected("Failed to toggle mute", error)) }
            }
        }
    }

    fun onToggleVibrate() {
        viewModelScope.launch {
            val result = toggleVibrateUseCase()
            result.onFailure { error ->
                _uiState.update { it.copy(error = (error as? AppError) ?: AppError.Unexpected("Failed to toggle vibrate", error)) }
            }
        }
    }

    fun onApplyPreset(preset: VolumePreset) {
        viewModelScope.launch {
            for ((streamType, targetLevel) in preset.streamLevels) {
                setVolumeUseCase(streamType, targetLevel)
            }
        }
    }

    fun onToggleOverlay(enabled: Boolean) {
        if (enabled && !permissionChecker.isPermissionGranted(PermissionType.OVERLAY)) {
            _uiState.update {
                it.copy(
                    error = AppError.AudioPermissionDenied(
                        "Display over other apps permission is required to show the floating volume button"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            updateSettingsUseCase.setOverlayEnabled(enabled)
        }
    }

    fun onToggleStartOnBoot(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.setStartOnBoot(enabled)
        }
    }

    fun onToggleMaxVolumeLimiter(enabled: Boolean) {
        viewModelScope.launch {
            val currentCap = _uiState.value.settings.maxVolumeCapPercent
            updateSettingsUseCase.setMaxVolumeLimiter(enabled, currentCap)
        }
    }

    fun onUpdateWidgetArrangement(arrangement: com.volumepilot.domain.model.FloatingButtonArrangement) {
        val current = _uiState.value.settings
        viewModelScope.launch {
            updateSettingsUseCase.updateFloatingWidgetCustomization(
                arrangement = arrangement,
                color = current.widgetColor,
                powerSingleTap = current.powerSingleTapAction,
                powerLongPress = current.powerLongPressAction
            )
        }
    }

    fun onUpdateWidgetColor(color: com.volumepilot.domain.model.FloatingButtonColor) {
        val current = _uiState.value.settings
        viewModelScope.launch {
            updateSettingsUseCase.updateFloatingWidgetCustomization(
                arrangement = current.buttonArrangement,
                color = color,
                powerSingleTap = current.powerSingleTapAction,
                powerLongPress = current.powerLongPressAction
            )
        }
    }

    fun onUpdatePowerSingleTap(action: com.volumepilot.domain.model.PowerSingleTapAction) {
        val current = _uiState.value.settings
        viewModelScope.launch {
            updateSettingsUseCase.updateFloatingWidgetCustomization(
                arrangement = current.buttonArrangement,
                color = current.widgetColor,
                powerSingleTap = action,
                powerLongPress = current.powerLongPressAction
            )
        }
    }

    fun onUpdatePowerLongPress(action: com.volumepilot.domain.model.PowerLongPressAction) {
        val current = _uiState.value.settings
        viewModelScope.launch {
            updateSettingsUseCase.updateFloatingWidgetCustomization(
                arrangement = current.buttonArrangement,
                color = current.widgetColor,
                powerSingleTap = current.powerSingleTapAction,
                powerLongPress = action
            )
        }
    }

    fun onUpdateOverlayAppearance(opacity: Float, sizeDp: Int) {
        viewModelScope.launch {
            updateSettingsUseCase.setOverlayAppearance(
                opacity = opacity,
                sizeDp = sizeDp,
                autoHideDelaySeconds = _uiState.value.settings.autoHideDelaySeconds
            )
        }
    }

    fun onUpdateRgbGlow(enabled: Boolean, intensity: Float) {
        viewModelScope.launch {
            updateSettingsUseCase.setRgbGlowSettings(enabled, intensity)
        }
    }

    fun onClearError() {
        _uiState.update { it.copy(error = null) }
    }
}
