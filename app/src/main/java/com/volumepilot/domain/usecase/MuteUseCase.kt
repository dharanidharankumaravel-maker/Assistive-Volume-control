package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.repository.VolumeRepository
import javax.inject.Inject

class MuteUseCase @Inject constructor(
    private val volumeRepository: VolumeRepository
) {
    suspend operator fun invoke(streamType: AudioStreamType, isMuted: Boolean): Result<Unit> {
        return volumeRepository.setMute(streamType, isMuted)
    }
}
