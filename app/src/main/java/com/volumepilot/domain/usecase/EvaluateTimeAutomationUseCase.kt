package com.volumepilot.domain.usecase

import com.volumepilot.domain.repository.AutomationRepository
import javax.inject.Inject

class EvaluateTimeAutomationUseCase @Inject constructor(
    private val automationRepository: AutomationRepository
) {
    suspend operator fun invoke(hour: Int, minute: Int, dayOfWeek: Int): Result<Unit> {
        return automationRepository.evaluateTimeRules(hour, minute, dayOfWeek)
    }
}
