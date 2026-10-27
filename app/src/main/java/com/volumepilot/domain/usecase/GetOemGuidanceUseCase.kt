package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.OemGuidance
import com.volumepilot.domain.repository.DiagnosticRepository
import javax.inject.Inject

class GetOemGuidanceUseCase @Inject constructor(
    private val diagnosticRepository: DiagnosticRepository
) {
    operator fun invoke(): OemGuidance {
        return diagnosticRepository.getOemGuidance()
    }
}
