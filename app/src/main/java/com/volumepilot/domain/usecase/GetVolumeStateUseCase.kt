package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.VolumeState
import com.volumepilot.domain.repository.VolumeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVolumeStateUseCase @Inject constructor(
    private val volumeRepository: VolumeRepository
) {
    operator fun invoke(): Flow<VolumeState> {
        return volumeRepository.getVolumeState()
    }
}
