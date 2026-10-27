package com.volumepilot.feature.onboarding

import com.volumepilot.domain.usecase.GetOemGuidanceUseCase
import com.volumepilot.domain.usecase.UpdateSettingsUseCase
import com.volumepilot.fakes.FakeDiagnosticRepository
import com.volumepilot.fakes.FakePermissionChecker
import com.volumepilot.fakes.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePermissionChecker: FakePermissionChecker
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var fakeDiagnosticRepository: FakeDiagnosticRepository
    private lateinit var getOemGuidanceUseCase: GetOemGuidanceUseCase
    private lateinit var updateSettingsUseCase: UpdateSettingsUseCase
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakePermissionChecker = FakePermissionChecker()
        fakeSettingsRepository = FakeSettingsRepository()
        fakeDiagnosticRepository = FakeDiagnosticRepository()

        getOemGuidanceUseCase = GetOemGuidanceUseCase(fakeDiagnosticRepository)
        updateSettingsUseCase = UpdateSettingsUseCase(fakeSettingsRepository)

        viewModel = OnboardingViewModel(
            permissionChecker = fakePermissionChecker,
            getOemGuidanceUseCase = getOemGuidanceUseCase,
            updateSettingsUseCase = updateSettingsUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshPermissions updates UI state permissions`() = runTest {
        fakePermissionChecker.overlayGranted = false
        viewModel.refreshPermissions()
        assertFalse(viewModel.uiState.value.isOverlayPermissionGranted)

        fakePermissionChecker.overlayGranted = true
        viewModel.refreshPermissions()
        assertTrue(viewModel.uiState.value.isOverlayPermissionGranted)
        assertTrue(viewModel.uiState.value.canProceed)
    }

    @Test
    fun `completeOnboarding persists completion and triggers callback`() = runTest {
        var callbackTriggered = false
        viewModel.completeOnboarding {
            callbackTriggered = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(callbackTriggered)
        val settings = fakeSettingsRepository.getSettings().first()
        assertTrue(settings.hasCompletedOnboarding)
        assertTrue(settings.isOverlayEnabled)
    }
}
