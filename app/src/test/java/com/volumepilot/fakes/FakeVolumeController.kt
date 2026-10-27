package com.volumepilot.fakes

import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumeState
import com.volumepilot.domain.model.VolumeStream
import com.volumepilot.service.volume.VolumeController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeVolumeController : VolumeController {

    private val initialStreams = mapOf(
        AudioStreamType.MEDIA to VolumeStream(AudioStreamType.MEDIA, currentVolume = 10, minVolume = 0, maxVolume = 15, isMuted = false),
        AudioStreamType.RING to VolumeStream(AudioStreamType.RING, currentVolume = 5, minVolume = 0, maxVolume = 7, isMuted = false),
        AudioStreamType.NOTIFICATION to VolumeStream(AudioStreamType.NOTIFICATION, currentVolume = 4, minVolume = 0, maxVolume = 7, isMuted = false),
        AudioStreamType.ALARM to VolumeStream(AudioStreamType.ALARM, currentVolume = 6, minVolume = 1, maxVolume = 7, isMuted = false),
        AudioStreamType.VOICE_CALL to VolumeStream(AudioStreamType.VOICE_CALL, currentVolume = 3, minVolume = 1, maxVolume = 5, isMuted = false)
    )

    private val _volumeState = MutableStateFlow(
        VolumeState(
            streams = initialStreams,
            activeOutputDevice = AudioOutputDevice.SPEAKER,
            ringerMode = RingerMode.NORMAL,
            isDndActive = false
        )
    )

    var shouldFailVolumeChange: Boolean = false
    var shouldFailMute: Boolean = false

    override fun observeVolumeState(): Flow<VolumeState> = _volumeState.asStateFlow()

    override fun getCurrentVolumeState(): VolumeState = _volumeState.value

    override suspend fun setVolume(streamType: AudioStreamType, level: Int): Result<Unit> {
        if (shouldFailVolumeChange) {
            return Result.failure(RuntimeException("Simulated volume failure"))
        }

        val currentStream = _volumeState.value.getStream(streamType)
            ?: return Result.failure(IllegalArgumentException("Unknown stream $streamType"))

        val clampedLevel = level.coerceIn(currentStream.minVolume, currentStream.maxVolume)
        val updatedStream = currentStream.copy(currentVolume = clampedLevel)

        val updatedMap = _volumeState.value.streams.toMutableMap()
        updatedMap[streamType] = updatedStream

        _volumeState.update { it.copy(streams = updatedMap) }
        return Result.success(Unit)
    }

    override suspend fun setMute(streamType: AudioStreamType, isMuted: Boolean): Result<Unit> {
        if (shouldFailMute) {
            return Result.failure(RuntimeException("Simulated mute failure"))
        }

        val currentStream = _volumeState.value.getStream(streamType)
            ?: return Result.failure(IllegalArgumentException("Unknown stream $streamType"))

        val updatedStream = currentStream.copy(isMuted = isMuted)
        val updatedMap = _volumeState.value.streams.toMutableMap()
        updatedMap[streamType] = updatedStream

        _volumeState.update { it.copy(streams = updatedMap) }
        return Result.success(Unit)
    }

    override suspend fun setRingerMode(mode: RingerMode): Result<Unit> {
        _volumeState.update { it.copy(ringerMode = mode) }
        return Result.success(Unit)
    }

    fun setActiveOutputDevice(device: AudioOutputDevice) {
        _volumeState.update { it.copy(activeOutputDevice = device) }
    }

    fun setDndActive(isDnd: Boolean) {
        _volumeState.update { it.copy(isDndActive = isDnd) }
    }
}
