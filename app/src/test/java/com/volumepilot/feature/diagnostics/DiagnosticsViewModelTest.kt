package com.volumepilot.feature.diagnostics

import com.volumepilot.domain.usecase.GetDiagnosticsUseCase
import com.volumepilot.domain.usecase.GetOemGuidanceUseCase
import com.volumepilot.domain.usecase.RepairOverlayUseCase
import com.volumepilot.fakes.FakeDiagnosticRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiagnosticsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDiagnosticRepository: FakeDiagnosticRepository
    private lateinit var getDiagnosticsUseCase: GetDiagnosticsUseCase
    private lateinit var getOemGuidanceUseCase: GetOemGuidanceUseCase
    private lateinit var repairOverlayUseCase: RepairOverlayUseCase
    private lateinit var viewModel: DiagnosticsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeDiagnosticRepository = FakeDiagnosticRepository()
        getDiagnosticsUseCase = GetDiagnosticsUseCase(fakeDiagnosticRepository)
        getOemGuidanceUseCase = GetOemGuidanceUseCase(fakeDiagnosticRepository)
        repairOverlayUseCase = RepairOverlayUseCase(fakeDiagnosticRepository)

        viewModel = DiagnosticsViewModel(
            getDiagnosticsUseCase = getDiagnosticsUseCase,
            getOemGuidanceUseCase = getOemGuidanceUseCase,
            repairOverlayUseCase = repairOverlayUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState observes diagnostic state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.diagnosticState.isOverlayPermissionGranted)
        assertTrue(viewModel.uiState.value.diagnosticState.isHealthy)
    }

    @Test
    fun `onRepairOverlay executes repair and emits success message`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onRepairOverlay()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRepairing)
        assertNotNull(viewModel.uiState.value.repairMessage)
        assertTrue(viewModel.uiState.value.repairMessage?.contains("successfully") == true)

        viewModel.onClearRepairMessage()
        assertNull(viewModel.uiState.value.repairMessage)
    }

    @Test
    fun `onRepairOverlay failure emits failure message`() = runTest {
        fakeDiagnosticRepository.shouldFailRepair = true
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onRepairOverlay()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRepairing)
        assertNotNull(viewModel.uiState.value.repairMessage)
        assertTrue(viewModel.uiState.value.repairMessage?.contains("failed") == true)
    }
}
