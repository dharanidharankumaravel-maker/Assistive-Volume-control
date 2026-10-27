package com.volumepilot.domain.usecase

import com.volumepilot.data.repository.VolumeRepositoryImpl
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumeProfile
import com.volumepilot.fakes.FakeProfileRepository
import com.volumepilot.fakes.FakeSettingsRepository
import com.volumepilot.fakes.FakeVolumeController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ActivateProfileUseCaseTest {

    private lateinit var fakeProfileRepository: FakeProfileRepository
    private lateinit var fakeVolumeController: FakeVolumeController
    private lateinit var volumeRepository: VolumeRepositoryImpl
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var setVolumeUseCase: SetVolumeUseCase
    private lateinit var activateProfileUseCase: ActivateProfileUseCase

    @Before
    fun setup() {
        fakeProfileRepository = FakeProfileRepository()
        fakeVolumeController = FakeVolumeController()
        volumeRepository = VolumeRepositoryImpl(fakeVolumeController)
        fakeSettingsRepository = FakeSettingsRepository()
        val enforceMaxVolumeCapUseCase = EnforceMaxVolumeCapUseCase()
        setVolumeUseCase = SetVolumeUseCase(
            volumeRepository = volumeRepository,
            settingsRepository = fakeSettingsRepository,
            enforceMaxVolumeCapUseCase = enforceMaxVolumeCapUseCase
        )
        activateProfileUseCase = ActivateProfileUseCase(
            profileRepository = fakeProfileRepository,
            volumeRepository = volumeRepository,
            setVolumeUseCase = setVolumeUseCase,
            settingsRepository = fakeSettingsRepository
        )
    }

    @Test
    fun `activateProfile applies stream levels, ringer mode and updates active id`() = runTest {
        val testProfile = VolumeProfile(
            id = "custom_test",
            name = "Test Profile",
            iconName = "tune",
            streamLevels = mapOf(
                AudioStreamType.MEDIA to 12,
                AudioStreamType.RING to 4,
                AudioStreamType.NOTIFICATION to 3
            ),
            ringerMode = RingerMode.NORMAL,
            isOverlayVisible = true
        )
        fakeProfileRepository.saveProfile(testProfile)

        val result = activateProfileUseCase("custom_test")

        assertTrue(result.isSuccess)
        val activeProfile = fakeProfileRepository.getActiveProfile().first()
        assertEquals("custom_test", activeProfile.id)
        val state = fakeVolumeController.getCurrentVolumeState()

        assertEquals(RingerMode.NORMAL, state.ringerMode)
        assertEquals(12, state.getStream(AudioStreamType.MEDIA)?.currentVolume)
        assertEquals(4, state.getStream(AudioStreamType.RING)?.currentVolume)
        assertEquals(3, state.getStream(AudioStreamType.NOTIFICATION)?.currentVolume)
    }


    @Test
    fun `activateProfile with unknown id fails gracefully`() = runTest {
        val result = activateProfileUseCase("non_existent_id")

        assertTrue(result.isFailure)
    }
}
