package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.OverlayPosition
import com.volumepilot.domain.model.ThemeMode
import com.volumepilot.fakes.FakeSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateSettingsUseCaseTest {

    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var updateSettingsUseCase: UpdateSettingsUseCase

    @Before
    fun setUp() {
        fakeSettingsRepository = FakeSettingsRepository()
        updateSettingsUseCase = UpdateSettingsUseCase(fakeSettingsRepository)
    }

    @Test
    fun `setOverlayPosition persists coordinates and snapped edge`() = runTest {
        val targetPosition = OverlayPosition(x = 16, y = 450, isSnappedToLeft = true)

        val result = updateSettingsUseCase.setOverlayPosition(targetPosition)

        assertTrue(result.isSuccess)
        val settings = fakeSettingsRepository.getSettings().first()
        assertEquals(targetPosition, settings.overlayPosition)
    }

    @Test
    fun `setOverlayAppearance updates opacity size and auto hide delay`() = runTest {
        val result = updateSettingsUseCase.setOverlayAppearance(
            opacity = 0.65f,
            sizeDp = 56,
            autoHideDelaySeconds = 5
        )

        assertTrue(result.isSuccess)
        val settings = fakeSettingsRepository.getSettings().first()
        assertEquals(0.65f, settings.overlayOpacity, 0.01f)
        assertEquals(56, settings.overlaySizeDp)
        assertEquals(5, settings.autoHideDelaySeconds)
    }

    @Test
    fun `setPreferredStreamType updates active default stream`() = runTest {
        val result = updateSettingsUseCase.setPreferredStreamType(AudioStreamType.NOTIFICATION)

        assertTrue(result.isSuccess)
        val settings = fakeSettingsRepository.getSettings().first()
        assertEquals(AudioStreamType.NOTIFICATION, settings.preferredStreamType)
    }

    @Test
    fun `setStartOnBoot toggles reboot restoration preference`() = runTest {
        val result = updateSettingsUseCase.setStartOnBoot(true)

        assertTrue(result.isSuccess)
        val settings = fakeSettingsRepository.getSettings().first()
        assertTrue(settings.startOnBoot)
    }

    @Test
    fun `setThemeMode updates theme preference`() = runTest {
        val result = updateSettingsUseCase.setThemeMode(ThemeMode.DARK)

        assertTrue(result.isSuccess)
        val settings = fakeSettingsRepository.getSettings().first()
        assertEquals(ThemeMode.DARK, settings.themeMode)
    }
}
