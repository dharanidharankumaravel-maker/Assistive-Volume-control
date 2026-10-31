package com.volumepilot.feature.home

import app.cash.turbine.test
import com.volumepilot.data.repository.VolumeRepositoryImpl
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.VolumePreset
import com.volumepilot.domain.usecase.EnforceMaxVolumeCapUseCase
import com.volumepilot.domain.usecase.GetSettingsUseCase
import com.volumepilot.domain.usecase.GetVolumeStateUseCase
import com.volumepilot.domain.usecase.MuteUseCase
import com.volumepilot.domain.usecase.SetVolumeUseCase
import com.volumepilot.domain.usecase.ToggleVibrateUseCase
import com.volumepilot.domain.usecase.UpdateSettingsUseCase
import com.volumepilot.fakes.FakePermissionChecker
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeVolumeController: FakeVolumeController
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var fakePermissionChecker: FakePermissionChecker
    private lateinit var volumeRepository: VolumeRepositoryImpl
    private lateinit var enforceMaxVolumeCapUseCase: EnforceMaxVolumeCapUseCase
    private lateinit var getVolumeStateUseCase: GetVolumeStateUseCase
    private lateinit var setVolumeUseCase: SetVolumeUseCase
    private lateinit var muteUseCase: MuteUseCase
    private lateinit var toggleVibrateUseCase: ToggleVibrateUseCase
    private lateinit var getSettingsUseCase: GetSettingsUseCase
    private lateinit var updateSettingsUseCase: UpdateSettingsUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeVolumeController = FakeVolumeController()
        fakeSettingsRepository = FakeSettingsRepository()
        fakePermissionChecker = FakePermissionChecker()
        volumeRepository = VolumeRepositoryImpl(fakeVolumeController)
        enforceMaxVolumeCapUseCase = EnforceMaxVolumeCapUseCase()

        getVolumeStateUseCase = GetVolumeStateUseCase(volumeRepository)
        setVolumeUseCase = SetVolumeUseCase(volumeRepository, fakeSettingsRepository, enforceMaxVolumeCapUseCase)
        muteUseCase = MuteUseCase(volumeRepository)
        toggleVibrateUseCase = ToggleVibrateUseCase(volumeRepository)
        getSettingsUseCase = GetSettingsUseCase(fakeSettingsRepository)
        updateSettingsUseCase = UpdateSettingsUseCase(fakeSettingsRepository)

        viewModel = HomeViewModel(
            getVolumeStateUseCase = getVolumeStateUseCase,
            setVolumeUseCase = setVolumeUseCase,
            muteUseCase = muteUseCase,
            toggleVibrateUseCase = toggleVibrateUseCase,
            getSettingsUseCase = getSettingsUseCase,
            updateSettingsUseCase = updateSettingsUseCase,
            enforceMaxVolumeCapUseCase = enforceMaxVolumeCapUseCase,
            permissionChecker = fakePermissionChecker
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState combines volume state and settings`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(10, state.volumeState.getStream(AudioStreamType.MEDIA)?.currentVolume)
            assertTrue(state.isOverlayPermissionGranted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onVolumeChange successfully updates state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onVolumeChange(AudioStreamType.MEDIA, 14)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(14, viewModel.uiState.value.volumeState.getStream(AudioStreamType.MEDIA)?.currentVolume)
    }

    @Test
    fun `onMuteToggle toggles stream mute state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onMuteToggle(AudioStreamType.MEDIA)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.volumeState.getStream(AudioStreamType.MEDIA)?.isMuted == true)
    }

    @Test
    fun `onApplyPreset adjusts stream levels`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onApplyPreset(VolumePreset.Silent)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.volumeState.getStream(AudioStreamType.MEDIA)?.currentVolume)
        assertEquals(0, viewModel.uiState.value.volumeState.getStream(AudioStreamType.RING)?.currentVolume)
    }

    @Test
    fun `onVolumeChange failure produces AppError in state and onClearError clears it`() = runTest {
        fakeVolumeController.shouldFailVolumeChange = true
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onVolumeChange(AudioStreamType.MEDIA, 15)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)

        viewModel.onClearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onToggleOverlay enables overlay when permission is granted`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        fakePermissionChecker.overlayGranted = true

        viewModel.onToggleOverlay(true)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.settings.isOverlayEnabled)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onToggleOverlay fails with AudioPermissionDenied when overlay permission is denied`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        fakePermissionChecker.overlayGranted = false

        viewModel.onToggleOverlay(true)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.settings.isOverlayEnabled)
        assertTrue(viewModel.uiState.value.error is com.volumepilot.core.common.AppError.AudioPermissionDenied)
    }

    @Test
    fun `onToggleStartOnBoot updates start on boot setting`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onToggleStartOnBoot(true)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.settings.startOnBoot)
    }

    @Test
    fun `refreshPermissions updates accessibilityStatus dynamically`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(
            com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.ENABLED,
            viewModel.uiState.value.accessibilityStatus
        )
        assertTrue(viewModel.uiState.value.isAccessibilityGranted)

        // Simulate user revoking accessibility permission in Android Settings
        fakePermissionChecker.accessibilityGranted = false
        fakePermissionChecker.customAccessibilityStatus =
            com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.DISABLED

        // Trigger onResume refresh
        viewModel.refreshPermissions()

        assertEquals(
            com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.DISABLED,
            viewModel.uiState.value.accessibilityStatus
        )
        assertFalse(viewModel.uiState.value.isAccessibilityGranted)

        // Simulate service in CONNECTING state
        fakePermissionChecker.customAccessibilityStatus =
            com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.CONNECTING
        viewModel.refreshPermissions()

        assertEquals(
            com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.CONNECTING,
            viewModel.uiState.value.accessibilityStatus
        )
        assertFalse(viewModel.uiState.value.isAccessibilityGranted)
    }

    @Test
    fun `onUpdateWidgetArrangement updates button arrangement setting`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onUpdateWidgetArrangement(com.volumepilot.domain.model.FloatingButtonArrangement.UP_DOWN)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            com.volumepilot.domain.model.FloatingButtonArrangement.UP_DOWN,
            viewModel.uiState.value.settings.buttonArrangement
        )
        assertTrue(viewModel.uiState.value.settings.buttonArrangement.isVolumeOnly)

        viewModel.onUpdateWidgetArrangement(com.volumepilot.domain.model.FloatingButtonArrangement.POWER_ONLY)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            com.volumepilot.domain.model.FloatingButtonArrangement.POWER_ONLY,
            viewModel.uiState.value.settings.buttonArrangement
        )
        assertTrue(viewModel.uiState.value.settings.buttonArrangement.hasPower)
    }
}
