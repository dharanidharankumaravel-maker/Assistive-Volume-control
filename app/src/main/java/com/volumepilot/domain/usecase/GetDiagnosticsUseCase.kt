package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.DiagnosticState
import com.volumepilot.domain.repository.DiagnosticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDiagnosticsUseCase @Inject constructor(
    private val diagnosticRepository: DiagnosticRepository
) {
    operator fun invoke(): Flow<DiagnosticState> {
        return diagnosticRepository.getDiagnosticState()
    }
}
