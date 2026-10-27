package com.volumepilot.domain.usecase

import com.volumepilot.core.common.AppError
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.repository.SettingsRepository
import com.volumepilot.domain.repository.VolumeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetVolumeUseCase @Inject constructor(
    private val volumeRepository: VolumeRepository,
    private val settingsRepository: SettingsRepository,
    private val enforceMaxVolumeCapUseCase: EnforceMaxVolumeCapUseCase
) {
    suspend operator fun invoke(streamType: AudioStreamType, requestedLevel: Int): Result<Unit> {
        val currentState = volumeRepository.getVolumeState().first()
        val stream = currentState.getStream(streamType)
            ?: return Result.failure(AppError.StreamUnavailable(streamType.displayName))

        val settings = settingsRepository.getSettings().first()
        val effectiveMax = enforceMaxVolumeCapUseCase(stream, settings)

        val targetLevel = if (requestedLevel > effectiveMax) {
            // Cap at effective max
            effectiveMax
        } else {
            requestedLevel.coerceIn(stream.minVolume, stream.maxVolume)
        }

        return volumeRepository.setVolume(streamType, targetLevel)
    }
}
