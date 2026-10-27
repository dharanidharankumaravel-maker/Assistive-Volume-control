package com.volumepilot.domain.usecase

import com.volumepilot.fakes.FakeDiagnosticRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepairOverlayUseCaseTest {

    private lateinit var fakeDiagnosticRepository: FakeDiagnosticRepository
    private lateinit var repairOverlayUseCase: RepairOverlayUseCase

    @Before
    fun setUp() {
        fakeDiagnosticRepository = FakeDiagnosticRepository()
        repairOverlayUseCase = RepairOverlayUseCase(fakeDiagnosticRepository)
    }

    @Test
    fun `repairOverlay returns success when service recovery succeeds`() = runTest {
        val result = repairOverlayUseCase()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `repairOverlay returns failure when service recovery errors`() = runTest {
        fakeDiagnosticRepository.shouldFailRepair = true
        val result = repairOverlayUseCase()
        assertFalse(result.isSuccess)
    }
}
