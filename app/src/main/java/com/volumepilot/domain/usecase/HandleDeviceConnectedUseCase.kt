package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.repository.AutomationRepository
import javax.inject.Inject

class HandleDeviceConnectedUseCase @Inject constructor(
    private val automationRepository: AutomationRepository
) {
    suspend operator fun invoke(device: AudioOutputDevice): Result<Unit> {
        return automationRepository.evaluateDeviceConnected(device)
    }
}
