package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.AutomationConfig
import com.volumepilot.domain.model.TimeRule
import com.volumepilot.fakes.FakeAutomationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EvaluateTimeAutomationUseCaseTest {

    private lateinit var fakeAutomationRepository: FakeAutomationRepository
    private lateinit var evaluateTimeAutomationUseCase: EvaluateTimeAutomationUseCase

    @Before
    fun setup() {
        fakeAutomationRepository = FakeAutomationRepository(
            initialConfig = AutomationConfig(
                isMasterAutomationEnabled = true,
                timeRules = listOf(
                    TimeRule(
                        id = "sleep_rule",
                        name = "Night Sleep",
                        profileId = "sleep",
                        startHour = 22,
                        startMinute = 0,
                        endHour = 7,
                        endMinute = 0,
                        repeatDays = setOf(1, 2, 3, 4, 5, 6, 7),
                        isEnabled = true
                    ),
                    TimeRule(
                        id = "work_rule",
                        name = "Work Hours",
                        profileId = "work",
                        startHour = 9,
                        startMinute = 0,
                        endHour = 17,
                        endMinute = 0,
                        repeatDays = setOf(2, 3, 4, 5, 6), // Mon-Fri
                        isEnabled = true
                    )
                )
            )
        )
        evaluateTimeAutomationUseCase = EvaluateTimeAutomationUseCase(fakeAutomationRepository)
    }

    @Test
    fun `time within sleep window triggers sleep profile`() = runTest {
        // 23:30 on Wednesday (day 4)
        val result = evaluateTimeAutomationUseCase(23, 30, 4)

        assertTrue(result.isSuccess)
        assertEquals("sleep", fakeAutomationRepository.activatedProfileId)
    }

    @Test
    fun `time within work window on weekday triggers work profile`() = runTest {
        // 14:00 on Tuesday (day 3)
        val result = evaluateTimeAutomationUseCase(14, 0, 3)

        assertTrue(result.isSuccess)
        assertEquals("work", fakeAutomationRepository.activatedProfileId)
    }
}
