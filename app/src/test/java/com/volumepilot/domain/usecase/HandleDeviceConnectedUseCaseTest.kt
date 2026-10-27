package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.AudioDeviceRule
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.AutomationConfig
import com.volumepilot.fakes.FakeAutomationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HandleDeviceConnectedUseCaseTest {

    private lateinit var fakeAutomationRepository: FakeAutomationRepository
    private lateinit var handleDeviceConnectedUseCase: HandleDeviceConnectedUseCase

    @Before
    fun setup() {
        fakeAutomationRepository = FakeAutomationRepository(
            initialConfig = AutomationConfig(
                isMasterAutomationEnabled = true,
                deviceRules = listOf(
                    AudioDeviceRule(
                        id = "bt_rule",
                        deviceType = AudioOutputDevice.BLUETOOTH,
                        profileId = "driving",
                        isEnabled = true
                    ),
                    AudioDeviceRule(
                        id = "headphones_rule",
                        deviceType = AudioOutputDevice.WIRED_HEADPHONES,
                        profileId = "headphones",
                        isEnabled = true
                    )
                )
            )
        )
        handleDeviceConnectedUseCase = HandleDeviceConnectedUseCase(fakeAutomationRepository)
    }

    @Test
    fun `connecting bluetooth activates driving profile`() = runTest {
        val result = handleDeviceConnectedUseCase(AudioOutputDevice.BLUETOOTH)

        assertTrue(result.isSuccess)
        assertEquals(AudioOutputDevice.BLUETOOTH, fakeAutomationRepository.lastEvaluatedDevice)
        assertEquals("driving", fakeAutomationRepository.activatedProfileId)
    }

    @Test
    fun `connecting headphones activates headphones profile`() = runTest {
        val result = handleDeviceConnectedUseCase(AudioOutputDevice.WIRED_HEADPHONES)

        assertTrue(result.isSuccess)
        assertEquals(AudioOutputDevice.WIRED_HEADPHONES, fakeAutomationRepository.lastEvaluatedDevice)
        assertEquals("headphones", fakeAutomationRepository.activatedProfileId)
    }
}
