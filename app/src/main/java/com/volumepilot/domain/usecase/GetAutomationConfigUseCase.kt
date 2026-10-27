package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.AutomationConfig
import com.volumepilot.domain.repository.AutomationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAutomationConfigUseCase @Inject constructor(
    private val automationRepository: AutomationRepository
) {
    operator fun invoke(): Flow<AutomationConfig> {
        return automationRepository.getAutomationConfig()
    }
}
