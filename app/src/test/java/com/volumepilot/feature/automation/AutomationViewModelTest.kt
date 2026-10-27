package com.volumepilot.feature.automation

import app.cash.turbine.test
import com.volumepilot.domain.model.AudioDeviceRule
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.SafeListeningConfig
import com.volumepilot.domain.model.TimeRule
import com.volumepilot.domain.usecase.ExportSettingsUseCase
import com.volumepilot.domain.usecase.GetAutomationConfigUseCase
import com.volumepilot.domain.usecase.GetProfilesUseCase
import com.volumepilot.domain.usecase.ImportSettingsUseCase
import com.volumepilot.domain.usecase.ToggleMasterAutomationUseCase
import com.volumepilot.fakes.FakeAutomationRepository
import com.volumepilot.fakes.FakeBackupRepository
import com.volumepilot.fakes.FakeProfileRepository
import com.volumepilot.service.automation.TimeAutomationScheduler
import io.mockk.mockk
import io.mockk.verify
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
class AutomationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAutomationRepository: FakeAutomationRepository
    private lateinit var fakeProfileRepository: FakeProfileRepository
    private lateinit var fakeBackupRepository: FakeBackupRepository
    private lateinit var scheduler: TimeAutomationScheduler
    private lateinit var viewModel: AutomationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeAutomationRepository = FakeAutomationRepository()
        fakeProfileRepository = FakeProfileRepository()
        fakeBackupRepository = FakeBackupRepository()
        scheduler = mockk(relaxed = true)

        viewModel = AutomationViewModel(
            getAutomationConfigUseCase = GetAutomationConfigUseCase(fakeAutomationRepository),
            toggleMasterAutomationUseCase = ToggleMasterAutomationUseCase(fakeAutomationRepository),
            automationRepository = fakeAutomationRepository,
            getProfilesUseCase = GetProfilesUseCase(fakeProfileRepository),
            exportSettingsUseCase = ExportSettingsUseCase(fakeBackupRepository),
            importSettingsUseCase = ImportSettingsUseCase(fakeBackupRepository),
            timeAutomationScheduler = scheduler
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState loads automation config and profiles`() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val loaded = if (initial.isLoading) awaitItem() else initial

            assertFalse(loaded.isLoading)
            assertTrue(loaded.config.isMasterAutomationEnabled)
            assertTrue(loaded.profiles.isNotEmpty())
        }
    }

    @Test
    fun `toggleMasterAutomation false cancels scheduler and updates config`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleMasterAutomation(false)
            testDispatcher.scheduler.advanceUntilIdle()

            verify { scheduler.cancelSchedule() }
            val state = expectMostRecentItem()
            assertFalse(state.config.isMasterAutomationEnabled)
        }
    }

    @Test
    fun `saveDeviceRule updates device rules in config`() = runTest {
        val rule = AudioDeviceRule(
            id = "custom_bt",
            deviceType = AudioOutputDevice.BLUETOOTH,
            profileId = "driving",
            isEnabled = true
        )

        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.saveDeviceRule(rule)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            val saved = state.config.deviceRules.find { it.id == "custom_bt" }
            assertNotNull(saved)
            assertEquals("driving", saved?.profileId)
        }
    }

    @Test
    fun `updateSafeListeningConfig updates max volume cap and warning flags`() = runTest {
        val newSafe = SafeListeningConfig(
            isMaxVolumeCapEnabled = true,
            maxVolumeCapPercent = 75,
            isSafeListeningWarningEnabled = true,
            warningThresholdPercent = 70
        )

        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.updateSafeListeningConfig(newSafe)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertEquals(true, state.config.safeListeningConfig.isMaxVolumeCapEnabled)
            assertEquals(75, state.config.safeListeningConfig.maxVolumeCapPercent)
        }
    }

    @Test
    fun `exportSettings stores exportedJson in uiState and feedback message`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.exportSettings()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertNotNull(state.exportedJson)
            assertEquals("Settings and profiles exported successfully!", state.feedbackMessage)
        }
    }

    @Test
    fun `importSettings invokes backup repository and sets feedback message`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.importSettings("{\"version\":1}")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertEquals("Settings and profiles imported successfully!", state.feedbackMessage)
            assertEquals("{\"version\":1}", fakeBackupRepository.lastImportedString)
        }
    }
}
