package com.volumepilot.data.repository

import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumePreset
import com.volumepilot.domain.model.VolumeState
import com.volumepilot.domain.repository.VolumeRepository
import com.volumepilot.service.volume.VolumeController
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VolumeRepositoryImpl @Inject constructor(
    private val volumeController: VolumeController
) : VolumeRepository {

    override fun getVolumeState(): Flow<VolumeState> {
        return volumeController.observeVolumeState()
    }

    override suspend fun setVolume(streamType: AudioStreamType, level: Int): Result<Unit> {
        return volumeController.setVolume(streamType, level)
    }

    override suspend fun setMute(streamType: AudioStreamType, isMuted: Boolean): Result<Unit> {
        return volumeController.setMute(streamType, isMuted)
    }

    override suspend fun setRingerMode(mode: RingerMode): Result<Unit> {
        return volumeController.setRingerMode(mode)
    }

    override suspend fun applyPreset(preset: VolumePreset): Result<Unit> {
        // Set ringer mode
        volumeController.setRingerMode(preset.ringerMode)

        // Set each stream volume defined in preset
        for ((stream, level) in preset.streamLevels) {
            volumeController.setVolume(stream, level)
        }

        return Result.success(Unit)
    }
}
