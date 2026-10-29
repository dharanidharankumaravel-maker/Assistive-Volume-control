package com.volumepilot.feature.styles

import app.cash.turbine.test
import com.volumepilot.domain.model.SliderStyle
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.usecase.GetSettingsUseCase
import com.volumepilot.domain.usecase.UpdateSettingsUseCase
import com.volumepilot.fakes.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SliderStylesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var getSettingsUseCase: GetSettingsUseCase
    private lateinit var updateSettingsUseCase: UpdateSettingsUseCase
    private lateinit var viewModel: SliderStylesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeSettingsRepository = FakeSettingsRepository(
            initialSettings = UserSettings(
                sliderStyle = SliderStyle.GOOGLE,
                sliderAccentColor = 0xFF7C3AEDL,
                sliderWidthDp = 40,
                sliderHeightDp = 140,
                sliderCornerRadiusDp = 20,
                sliderBackgroundAlpha = 0.5f,
                rgbSpeedMillis = 3500
            )
        )
        getSettingsUseCase = GetSettingsUseCase(fakeSettingsRepository)
        updateSettingsUseCase = UpdateSettingsUseCase(fakeSettingsRepository)
        viewModel = SliderStylesViewModel(getSettingsUseCase, updateSettingsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads correctly from settings`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(SliderStyle.GOOGLE, state.selectedStyle)
            assertEquals(0xFF7C3AEDL, state.accentColor)
            assertEquals(40, state.widthDp)
            assertEquals(140, state.heightDp)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSelectStyle updates style and defaults`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSelectStyle(SliderStyle.APPLE)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(SliderStyle.APPLE, state.selectedStyle)
            assertEquals(SliderStyle.APPLE.defaultWidthDp, state.widthDp)
            assertEquals(SliderStyle.APPLE.defaultHeightDp, state.heightDp)
            assertEquals(SliderStyle.APPLE.defaultCornerRadiusDp, state.cornerRadiusDp)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPreviewVolumeChange updates live preview level`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onPreviewVolumeChange(SliderStyle.SAMSUNG, 0.90f)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0.90f, state.previewVolumeLevels[SliderStyle.SAMSUNG] ?: 0f, 0.01f)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `custom geometry and color updates adjust state properly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onUpdateAccentColor(0xFF00F5D4L)
        viewModel.onUpdateWidth(46)
        viewModel.onUpdateHeight(150)
        viewModel.onUpdateCornerRadius(24)
        viewModel.onUpdateBackgroundAlpha(0.75f)
        viewModel.onUpdateRgbSpeed(2000)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0xFF00F5D4L, state.accentColor)
            assertEquals(46, state.widthDp)
            assertEquals(150, state.heightDp)
            assertEquals(24, state.cornerRadiusDp)
            assertEquals(0.75f, state.backgroundAlpha, 0.01f)
            assertEquals(2000, state.rgbSpeedMillis)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applyCustomization persists changes to repository and updates uiState`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSelectStyle(SliderStyle.ONEPLUS)
        viewModel.onUpdateAccentColor(0xFF00875AL)
        var callbackInvoked = false
        viewModel.applyCustomization {
            callbackInvoked = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(callbackInvoked)
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isAppliedSuccess)
            cancelAndIgnoreRemainingEvents()
        }

        fakeSettingsRepository.getSettings().test {
            val savedSettings = awaitItem()
            assertEquals(SliderStyle.ONEPLUS, savedSettings.sliderStyle)
            assertEquals(0xFF00875AL, savedSettings.sliderAccentColor)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

