package com.volumepilot.domain.repository

import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumePreset
import com.volumepilot.domain.model.VolumeState
import kotlinx.coroutines.flow.Flow

interface VolumeRepository {
    fun getVolumeState(): Flow<VolumeState>
    suspend fun setVolume(streamType: AudioStreamType, level: Int): Result<Unit>
    suspend fun setMute(streamType: AudioStreamType, isMuted: Boolean): Result<Unit>
    suspend fun setRingerMode(mode: RingerMode): Result<Unit>
    suspend fun applyPreset(preset: VolumePreset): Result<Unit>
}
