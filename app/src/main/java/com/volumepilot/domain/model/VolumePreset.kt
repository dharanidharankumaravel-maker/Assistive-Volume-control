package com.volumepilot.domain.model

sealed class VolumePreset(
    val id: String,
    val name: String,
    val streamLevels: Map<AudioStreamType, Int>,
    val ringerMode: RingerMode
) {
    object Silent : VolumePreset(
        id = "preset_silent",
        name = "Silent",
        streamLevels = mapOf(
            AudioStreamType.MEDIA to 0,
            AudioStreamType.RING to 0,
            AudioStreamType.NOTIFICATION to 0,
            AudioStreamType.ALARM to 5
        ),
        ringerMode = RingerMode.SILENT
    )

    object Vibrate : VolumePreset(
        id = "preset_vibrate",
        name = "Vibrate",
        streamLevels = mapOf(
            AudioStreamType.MEDIA to 0,
            AudioStreamType.RING to 0,
            AudioStreamType.NOTIFICATION to 0,
            AudioStreamType.ALARM to 7
        ),
        ringerMode = RingerMode.VIBRATE
    )

    object Normal : VolumePreset(
        id = "preset_normal",
        name = "Normal",
        streamLevels = mapOf(
            AudioStreamType.MEDIA to 10,
            AudioStreamType.RING to 5,
            AudioStreamType.NOTIFICATION to 5,
            AudioStreamType.ALARM to 8
        ),
        ringerMode = RingerMode.NORMAL
    )

    object Loud : VolumePreset(
        id = "preset_loud",
        name = "Outdoor",
        streamLevels = mapOf(
            AudioStreamType.MEDIA to 15,
            AudioStreamType.RING to 7,
            AudioStreamType.NOTIFICATION to 7,
            AudioStreamType.ALARM to 10
        ),
        ringerMode = RingerMode.NORMAL
    )

    data class Custom(
        val customId: String,
        val customName: String,
        val customLevels: Map<AudioStreamType, Int>,
        val customRingerMode: RingerMode
    ) : VolumePreset(customId, customName, customLevels, customRingerMode)
}
