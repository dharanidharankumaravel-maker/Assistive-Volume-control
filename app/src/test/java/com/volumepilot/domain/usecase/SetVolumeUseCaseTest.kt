package com.volumepilot.domain.usecase

import com.volumepilot.core.common.AppError
import com.volumepilot.data.repository.VolumeRepositoryImpl
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.fakes.FakeSettingsRepository
import com.volumepilot.fakes.FakeVolumeController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SetVolumeUseCaseTest {

    private lateinit var fakeVolumeController: FakeVolumeController
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var volumeRepository: VolumeRepositoryImpl
    private lateinit var enforceMaxVolumeCapUseCase: EnforceMaxVolumeCapUseCase
    private lateinit var setVolumeUseCase: SetVolumeUseCase

    @Before
    fun setUp() {
        fakeVolumeController = FakeVolumeController()
        fakeSettingsRepository = FakeSettingsRepository()
        volumeRepository = VolumeRepositoryImpl(fakeVolumeController)
        enforceMaxVolumeCapUseCase = EnforceMaxVolumeCapUseCase()

        setVolumeUseCase = SetVolumeUseCase(
            volumeRepository = volumeRepository,
            settingsRepository = fakeSettingsRepository,
            enforceMaxVolumeCapUseCase = enforceMaxVolumeCapUseCase
        )
    }

    @Test
    fun `setVolume successfully updates stream level within bounds`() = runTest {
        val result = setVolumeUseCase(AudioStreamType.MEDIA, 12)

        assertTrue(result.isSuccess)
        val state = volumeRepository.getVolumeState().first()
        assertEquals(12, state.getStream(AudioStreamType.MEDIA)?.currentVolume)
    }

    @Test
    fun `setVolume clamps to safe cap when volume limiter is active`() = runTest {
        fakeSettingsRepository.updateMaxVolumeLimiter(enabled = true, capPercent = 80)
        // Media max is 15. 80% of 15 is 12.
        val result = setVolumeUseCase(AudioStreamType.MEDIA, 15)

        assertTrue(result.isSuccess)
        val state = volumeRepository.getVolumeState().first()
        assertEquals(12, state.getStream(AudioStreamType.MEDIA)?.currentVolume)
    }

    @Test
    fun `setVolume fails when stream is unavailable`() = runTest {
        val result = setVolumeUseCase(AudioStreamType.BLUETOOTH, 10)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.StreamUnavailable)
    }
}
