package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.model.VolumeStream
import javax.inject.Inject

class EnforceMaxVolumeCapUseCase @Inject constructor() {
    /**
     * Calculates the capped maximum level for a stream if the limiter is enabled.
     * Only applies to MEDIA or BLUETOOTH streams by default to ensure emergency/alarm streams remain audible.
     */
    operator fun invoke(stream: VolumeStream, settings: UserSettings): Int {
        if (!settings.maxVolumeLimiterEnabled) {
            return stream.maxVolume
        }

        if (stream.streamType != AudioStreamType.MEDIA && stream.streamType != AudioStreamType.BLUETOOTH) {
            return stream.maxVolume
        }

        val range = stream.maxVolume - stream.minVolume
        val allowedIncrement = (range * (settings.maxVolumeCapPercent / 100f)).toInt()
        return (stream.minVolume + allowedIncrement).coerceIn(stream.minVolume, stream.maxVolume)
    }
}
