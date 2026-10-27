package com.volumepilot.domain.usecase

import com.volumepilot.domain.repository.AutomationRepository
import javax.inject.Inject

class ToggleMasterAutomationUseCase @Inject constructor(
    private val automationRepository: AutomationRepository
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> {
        return automationRepository.setMasterAutomationEnabled(enabled)
    }
}
