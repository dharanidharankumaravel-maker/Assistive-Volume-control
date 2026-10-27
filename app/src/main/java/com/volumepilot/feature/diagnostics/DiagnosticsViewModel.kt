package com.volumepilot.feature.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volumepilot.domain.usecase.GetDiagnosticsUseCase
import com.volumepilot.domain.usecase.GetOemGuidanceUseCase
import com.volumepilot.domain.usecase.RepairOverlayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val getDiagnosticsUseCase: GetDiagnosticsUseCase,
    private val getOemGuidanceUseCase: GetOemGuidanceUseCase,
    private val repairOverlayUseCase: RepairOverlayUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DiagnosticsUiState(
            oemGuidance = getOemGuidanceUseCase()
        )
    )
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()

    init {
        observeDiagnostics()
    }

    private fun observeDiagnostics() {
        viewModelScope.launch {
            getDiagnosticsUseCase().collect { state ->
                _uiState.update { it.copy(diagnosticState = state) }
            }
        }
    }

    fun onRepairOverlay() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRepairing = true) }
            val result = repairOverlayUseCase()
            _uiState.update {
                it.copy(
                    isRepairing = false,
                    repairMessage = if (result.isSuccess) {
                        "Overlay service successfully restarted and verified."
                    } else {
                        "Repair failed: ${result.exceptionOrNull()?.message}"
                    }
                )
            }
        }
    }

    fun onClearRepairMessage() {
        _uiState.update { it.copy(repairMessage = null) }
    }
}
