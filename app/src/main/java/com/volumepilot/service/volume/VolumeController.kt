package com.volumepilot.service.volume

import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumeState
import kotlinx.coroutines.flow.Flow

interface VolumeController {
    fun observeVolumeState(): Flow<VolumeState>
    fun getCurrentVolumeState(): VolumeState
    suspend fun setVolume(streamType: AudioStreamType, level: Int): Result<Unit>
    suspend fun setMute(streamType: AudioStreamType, isMuted: Boolean): Result<Unit>
    suspend fun setRingerMode(mode: RingerMode): Result<Unit>
}
