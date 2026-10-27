package com.volumepilot.domain.model

data class VolumeState(
    val streams: Map<AudioStreamType, VolumeStream> = emptyMap(),
    val activeOutputDevice: AudioOutputDevice = AudioOutputDevice.SPEAKER,
    val ringerMode: RingerMode = RingerMode.NORMAL,
    val isDndActive: Boolean = false
) {
    fun getStream(streamType: AudioStreamType): VolumeStream? = streams[streamType]
}
