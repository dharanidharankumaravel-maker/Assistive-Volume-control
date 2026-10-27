package com.volumepilot.domain.model

data class VolumeStream(
    val streamType: AudioStreamType,
    val currentVolume: Int,
    val minVolume: Int = 0,
    val maxVolume: Int,
    val isMuted: Boolean = false
) {
    val percentage: Int
        get() = if (maxVolume > minVolume) {
            ((currentVolume - minVolume).toFloat() / (maxVolume - minVolume) * 100).toInt()
        } else 0

    init {
        require(minVolume >= 0) { "minVolume must be >= 0" }
        require(maxVolume >= minVolume) { "maxVolume must be >= minVolume" }
    }
}
