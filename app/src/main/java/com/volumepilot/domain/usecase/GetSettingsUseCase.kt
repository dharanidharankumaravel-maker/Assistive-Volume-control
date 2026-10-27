package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> {
        return settingsRepository.getSettings()
    }
}
