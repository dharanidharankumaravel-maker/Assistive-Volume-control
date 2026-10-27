package com.volumepilot.domain.usecase

import com.volumepilot.domain.repository.DiagnosticRepository
import javax.inject.Inject

class RepairOverlayUseCase @Inject constructor(
    private val diagnosticRepository: DiagnosticRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return diagnosticRepository.repairOverlay()
    }
}
