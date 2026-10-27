package com.volumepilot.domain.repository

import com.volumepilot.domain.model.DiagnosticState
import com.volumepilot.domain.model.OemGuidance
import kotlinx.coroutines.flow.Flow

interface DiagnosticRepository {
    fun getDiagnosticState(): Flow<DiagnosticState>
    fun getOemGuidance(): OemGuidance
    suspend fun repairOverlay(): Result<Unit>
}
