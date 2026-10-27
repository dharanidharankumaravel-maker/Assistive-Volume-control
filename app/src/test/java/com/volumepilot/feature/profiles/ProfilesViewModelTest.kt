package com.volumepilot.feature.profiles

import app.cash.turbine.test
import com.volumepilot.data.repository.VolumeRepositoryImpl
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumeProfile
import com.volumepilot.domain.usecase.ActivateProfileUseCase
import com.volumepilot.domain.usecase.DeleteProfileUseCase
import com.volumepilot.domain.usecase.EnforceMaxVolumeCapUseCase
import com.volumepilot.domain.usecase.GetProfilesUseCase
import com.volumepilot.domain.usecase.SaveProfileUseCase
import com.volumepilot.domain.usecase.SetVolumeUseCase
import com.volumepilot.fakes.FakeProfileRepository
import com.volumepilot.fakes.FakeSettingsRepository
import com.volumepilot.fakes.FakeVolumeController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeProfileRepository: FakeProfileRepository
    private lateinit var fakeVolumeController: FakeVolumeController
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var volumeRepository: VolumeRepositoryImpl
    private lateinit var activateProfileUseCase: ActivateProfileUseCase
    private lateinit var getProfilesUseCase: GetProfilesUseCase
    private lateinit var saveProfileUseCase: SaveProfileUseCase
    private lateinit var deleteProfileUseCase: DeleteProfileUseCase
    private lateinit var viewModel: ProfilesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeProfileRepository = FakeProfileRepository()
        fakeVolumeController = FakeVolumeController()
        fakeSettingsRepository = FakeSettingsRepository()
        volumeRepository = VolumeRepositoryImpl(fakeVolumeController)
        val enforceMaxVolumeCapUseCase = EnforceMaxVolumeCapUseCase()
        val setVolumeUseCase = SetVolumeUseCase(volumeRepository, fakeSettingsRepository, enforceMaxVolumeCapUseCase)

        activateProfileUseCase = ActivateProfileUseCase(
            profileRepository = fakeProfileRepository,
            volumeRepository = volumeRepository,
            setVolumeUseCase = setVolumeUseCase,
            settingsRepository = fakeSettingsRepository
        )
        getProfilesUseCase = GetProfilesUseCase(fakeProfileRepository)
        saveProfileUseCase = SaveProfileUseCase(fakeProfileRepository)
        deleteProfileUseCase = DeleteProfileUseCase(fakeProfileRepository)

        viewModel = ProfilesViewModel(
            getProfilesUseCase = getProfilesUseCase,
            profileRepository = fakeProfileRepository,
            activateProfileUseCase = activateProfileUseCase,
            saveProfileUseCase = saveProfileUseCase,
            deleteProfileUseCase = deleteProfileUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState loads default profiles and default active id`() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val loaded = if (initial.isLoading) awaitItem() else initial

            assertFalse(loaded.isLoading)
            assertTrue(loaded.profiles.isNotEmpty())
            assertEquals(VolumeProfile.DEFAULT.id, loaded.activeProfileId)
        }
    }

    @Test
    fun `activateProfile updates activeProfileId in uiState`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.activateProfile(VolumeProfile.SLEEP.id)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertEquals(VolumeProfile.SLEEP.id, state.activeProfileId)
        }
    }


    @Test
    fun `saveProfile adds new custom profile to list`() = runTest {
        val newProfile = VolumeProfile(
            id = "study",
            name = "Study Mode",
            iconName = "work",
            streamLevels = mapOf(AudioStreamType.MEDIA to 3),
            ringerMode = RingerMode.VIBRATE,
            isDeletable = true
        )

        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.saveProfile(newProfile)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            val added = state.profiles.find { it.id == "study" }
            assertNotNull(added)
            assertEquals("Study Mode", added?.name)
        }
    }

    @Test
    fun `deleteProfile removes custom profile`() = runTest {
        val newProfile = VolumeProfile(
            id = "temp_delete",
            name = "Temp",
            iconName = "tune",
            streamLevels = emptyMap(),
            isDeletable = true
        )
        fakeProfileRepository.saveProfile(newProfile)

        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.deleteProfile("temp_delete")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            val found = state.profiles.find { it.id == "temp_delete" }
            assertEquals(null, found)
        }
    }
}
