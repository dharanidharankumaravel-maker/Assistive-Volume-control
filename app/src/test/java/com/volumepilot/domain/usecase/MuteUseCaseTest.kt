package com.volumepilot.domain.usecase

import com.volumepilot.data.repository.VolumeRepositoryImpl
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.fakes.FakeVolumeController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MuteUseCaseTest {

    private lateinit var fakeVolumeController: FakeVolumeController
    private lateinit var volumeRepository: VolumeRepositoryImpl
    private lateinit var muteUseCase: MuteUseCase

    @Before
    fun setUp() {
        fakeVolumeController = FakeVolumeController()
        volumeRepository = VolumeRepositoryImpl(fakeVolumeController)
        muteUseCase = MuteUseCase(volumeRepository)
    }

    @Test
    fun `muteUseCase sets stream isMuted to true and false`() = runTest {
        val muteResult = muteUseCase(AudioStreamType.RING, true)
        assertTrue(muteResult.isSuccess)
        val mutedState = volumeRepository.getVolumeState().first()
        assertTrue(mutedState.getStream(AudioStreamType.RING)?.isMuted == true)

        val unmuteResult = muteUseCase(AudioStreamType.RING, false)
        assertTrue(unmuteResult.isSuccess)
        val unmutedState = volumeRepository.getVolumeState().first()
        assertFalse(unmutedState.getStream(AudioStreamType.RING)?.isMuted == true)
    }
}
