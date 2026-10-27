package com.volumepilot.domain.usecase

import com.volumepilot.fakes.FakeDiagnosticRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDiagnosticsUseCaseTest {

    private lateinit var fakeDiagnosticRepository: FakeDiagnosticRepository
    private lateinit var getDiagnosticsUseCase: GetDiagnosticsUseCase

    @Before
    fun setUp() {
        fakeDiagnosticRepository = FakeDiagnosticRepository()
        getDiagnosticsUseCase = GetDiagnosticsUseCase(fakeDiagnosticRepository)
    }

    @Test
    fun `getDiagnostics returns current diagnostic state and evaluates health`() = runTest {
        val state = getDiagnosticsUseCase().first()

        assertTrue(state.isOverlayPermissionGranted)
        assertTrue(state.isHealthy)
        assertEquals("Google", state.deviceManufacturer)
    }

    @Test
    fun `isHealthy returns false when overlay permission is missing`() = runTest {
        fakeDiagnosticRepository.setOverlayPermissionGranted(false)

        val state = getDiagnosticsUseCase().first()

        assertFalse(state.isOverlayPermissionGranted)
        assertFalse(state.isHealthy)
    }
}
