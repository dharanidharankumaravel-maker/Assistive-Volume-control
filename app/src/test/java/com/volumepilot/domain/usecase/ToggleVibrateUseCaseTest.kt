package com.volumepilot.domain.usecase

import com.volumepilot.data.repository.VolumeRepositoryImpl
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.fakes.FakeVolumeController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleVibrateUseCaseTest {

    private lateinit var fakeVolumeController: FakeVolumeController
    private lateinit var volumeRepository: VolumeRepositoryImpl
    private lateinit var toggleVibrateUseCase: ToggleVibrateUseCase

    @Before
    fun setUp() {
        fakeVolumeController = FakeVolumeController()
        volumeRepository = VolumeRepositoryImpl(fakeVolumeController)
        toggleVibrateUseCase = ToggleVibrateUseCase(volumeRepository)
    }

    @Test
    fun `toggleVibrate switches between NORMAL and VIBRATE`() = runTest {
        // Initial mode is NORMAL
        assertEquals(RingerMode.NORMAL, volumeRepository.getVolumeState().first().ringerMode)

        val result1 = toggleVibrateUseCase()
        assertTrue(result1.isSuccess)
        assertEquals(RingerMode.VIBRATE, volumeRepository.getVolumeState().first().ringerMode)

        val result2 = toggleVibrateUseCase()
        assertTrue(result2.isSuccess)
        assertEquals(RingerMode.NORMAL, volumeRepository.getVolumeState().first().ringerMode)
    }
}
