package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.repository.VolumeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleVibrateUseCase @Inject constructor(
    private val volumeRepository: VolumeRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val currentState = volumeRepository.getVolumeState().first()
        val nextMode = if (currentState.ringerMode == RingerMode.VIBRATE) {
            RingerMode.NORMAL
        } else {
            RingerMode.VIBRATE
        }
        return volumeRepository.setRingerMode(nextMode)
    }
}
