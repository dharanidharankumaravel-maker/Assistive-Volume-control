package com.volumepilot.feature.styles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volumepilot.domain.model.SliderStyle
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.usecase.GetSettingsUseCase
import com.volumepilot.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SliderStylesUiState(
    val selectedStyle: SliderStyle = SliderStyle.GOOGLE,
    val accentColor: Long = 0xFF7C3AEDL,
    val widthDp: Int = 40,
    val heightDp: Int = 135,
    val cornerRadiusDp: Int = 20,
    val backgroundAlpha: Float = 0.5f,
    val rgbSpeedMillis: Int = 3500,
    val rgbGlowEnabled: Boolean = true,
    val rgbGlowIntensity: Float = 0.6f,
    val previewVolumeLevels: Map<SliderStyle, Float> = mapOf(
        SliderStyle.GOOGLE to 0.72f,
        SliderStyle.APPLE to 0.55f,
        SliderStyle.SAMSUNG to 0.65f,
        SliderStyle.XIAOMI to 0.60f,
        SliderStyle.ONEPLUS to 0.78f
    ),
    val isAppliedSuccess: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class SliderStylesViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SliderStylesUiState())
    val uiState: StateFlow<SliderStylesUiState> = _uiState.asStateFlow()

    init {
        loadCurrentSettings()
    }

    private fun loadCurrentSettings() {
        viewModelScope.launch {
            val settings = getSettingsUseCase().first()
            _uiState.update { current ->
                current.copy(
                    selectedStyle = settings.sliderStyle,
                    accentColor = settings.sliderAccentColor,
                    widthDp = settings.sliderWidthDp,
                    heightDp = settings.sliderHeightDp,
                    cornerRadiusDp = settings.sliderCornerRadiusDp,
                    backgroundAlpha = settings.sliderBackgroundAlpha,
                    rgbSpeedMillis = settings.rgbSpeedMillis,
                    rgbGlowEnabled = settings.rgbGlowEnabled,
                    rgbGlowIntensity = settings.rgbGlowIntensity,
                    isLoading = false
                )
            }
        }
    }

    fun onSelectStyle(style: SliderStyle) {
        _uiState.update { current ->
            current.copy(
                selectedStyle = style,
                widthDp = style.defaultWidthDp,
                heightDp = style.defaultHeightDp,
                cornerRadiusDp = style.defaultCornerRadiusDp,
                isAppliedSuccess = false
            )
        }
    }

    fun onPreviewVolumeChange(style: SliderStyle, progress: Float) {
        _uiState.update { current ->
            val updatedMap = current.previewVolumeLevels.toMutableMap()
            updatedMap[style] = progress.coerceIn(0f, 1f)
            current.copy(previewVolumeLevels = updatedMap)
        }
    }

    fun onUpdateAccentColor(color: Long) {
        _uiState.update { it.copy(accentColor = color, isAppliedSuccess = false) }
    }

    fun onUpdateWidth(width: Int) {
        _uiState.update { it.copy(widthDp = width.coerceIn(30, 52), isAppliedSuccess = false) }
    }

    fun onUpdateHeight(height: Int) {
        _uiState.update { it.copy(heightDp = height.coerceIn(95, 165), isAppliedSuccess = false) }
    }

    fun onUpdateCornerRadius(radius: Int) {
        _uiState.update { it.copy(cornerRadiusDp = radius.coerceIn(6, 28), isAppliedSuccess = false) }
    }

    fun onUpdateBackgroundAlpha(alpha: Float) {
        _uiState.update { it.copy(backgroundAlpha = alpha.coerceIn(0.2f, 1.0f), isAppliedSuccess = false) }
    }

    fun onUpdateRgbSpeed(speedMillis: Int) {
        _uiState.update { it.copy(rgbSpeedMillis = speedMillis.coerceIn(1500, 6000), isAppliedSuccess = false) }
    }

    fun onUpdateRgbGlow(enabled: Boolean, intensity: Float) {
        _uiState.update { it.copy(rgbGlowEnabled = enabled, rgbGlowIntensity = intensity, isAppliedSuccess = false) }
    }

    fun onResetToPresetDefaults() {
        val currentStyle = _uiState.value.selectedStyle
        _uiState.update {
            it.copy(
                widthDp = currentStyle.defaultWidthDp,
                heightDp = currentStyle.defaultHeightDp,
                cornerRadiusDp = currentStyle.defaultCornerRadiusDp,
                backgroundAlpha = 0.5f,
                rgbSpeedMillis = 3500,
                isAppliedSuccess = false
            )
        }
    }

    fun applyCustomization(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val state = _uiState.value
            updateSettingsUseCase.updateSliderStyleCustomization(
                sliderStyle = state.selectedStyle,
                accentColor = state.accentColor,
                widthDp = state.widthDp,
                heightDp = state.heightDp,
                cornerRadiusDp = state.cornerRadiusDp,
                backgroundAlpha = state.backgroundAlpha,
                rgbSpeedMillis = state.rgbSpeedMillis
            )
            updateSettingsUseCase.setRgbGlowSettings(state.rgbGlowEnabled, state.rgbGlowIntensity)
            _uiState.update { it.copy(isAppliedSuccess = true) }
            onSuccess()
        }
    }

    fun clearAppliedSuccess() {
        _uiState.update { it.copy(isAppliedSuccess = false) }
    }
}

