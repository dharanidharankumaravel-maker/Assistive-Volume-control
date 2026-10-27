package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.model.VolumeStream
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EnforceMaxVolumeCapUseCaseTest {

    private lateinit var useCase: EnforceMaxVolumeCapUseCase

    @Before
    fun setUp() {
        useCase = EnforceMaxVolumeCapUseCase()
    }

    @Test
    fun `when limiter is disabled, returns stream max volume`() {
        val stream = VolumeStream(
            streamType = AudioStreamType.MEDIA,
            currentVolume = 10,
            minVolume = 0,
            maxVolume = 100
        )
        val settings = UserSettings(maxVolumeLimiterEnabled = false, maxVolumeCapPercent = 70)

        val cappedMax = useCase(stream, settings)

        assertEquals(100, cappedMax)
    }

    @Test
    fun `when limiter is enabled on media stream, caps maximum volume to configured percent`() {
        val stream = VolumeStream(
            streamType = AudioStreamType.MEDIA,
            currentVolume = 10,
            minVolume = 0,
            maxVolume = 100
        )
        val settings = UserSettings(maxVolumeLimiterEnabled = true, maxVolumeCapPercent = 80)

        val cappedMax = useCase(stream, settings)

        assertEquals(80, cappedMax)
    }

    @Test
    fun `when limiter is enabled on alarm stream, does not cap to preserve alarm safety`() {
        val stream = VolumeStream(
            streamType = AudioStreamType.ALARM,
            currentVolume = 5,
            minVolume = 1,
            maxVolume = 15
        )
        val settings = UserSettings(maxVolumeLimiterEnabled = true, maxVolumeCapPercent = 50)

        val cappedMax = useCase(stream, settings)

        assertEquals(15, cappedMax)
    }
}
