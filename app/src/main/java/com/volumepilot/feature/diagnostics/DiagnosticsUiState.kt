package com.volumepilot.feature.diagnostics

import com.volumepilot.domain.model.DiagnosticState
import com.volumepilot.domain.model.OemGuidance
import com.volumepilot.domain.model.OemManufacturer

data class DiagnosticsUiState(
    val diagnosticState: DiagnosticState = DiagnosticState(),
    val oemGuidance: OemGuidance = OemGuidance(
        manufacturer = OemManufacturer.GENERIC,
        headline = "",
        explanation = "",
        steps = emptyList()
    ),
    val isRepairing: Boolean = false,
    val repairMessage: String? = null,
    val crashLog: String? = null
)

