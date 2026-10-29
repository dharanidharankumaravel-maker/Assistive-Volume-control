package com.volumepilot.domain.model

data class UserSettings(
    val maxVolumeLimiterEnabled: Boolean = false,
    val maxVolumeCapPercent: Int = 80, // e.g., 80% maximum cap to avoid hearing damage
    val hapticFeedbackEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val startOnBoot: Boolean = false,
    val autoHideDelaySeconds: Int = 3,
    val overlayOpacity: Float = 0.85f,
    val overlaySizeDp: Int = 48,
    val isOverlayEnabled: Boolean = false,
    val overlayPosition: OverlayPosition = OverlayPosition(),
    val preferredStreamType: AudioStreamType = AudioStreamType.MEDIA,
    val hasCompletedOnboarding: Boolean = false,
    val buttonArrangement: FloatingButtonArrangement = FloatingButtonArrangement.POWER_UP_DOWN,
    val widgetColor: FloatingButtonColor = FloatingButtonColor.BASIL,
    val powerSingleTapAction: PowerSingleTapAction = PowerSingleTapAction.LOCK_SCREEN,
    val powerLongPressAction: PowerLongPressAction = PowerLongPressAction.TAKE_SCREENSHOT,
    val rgbGlowEnabled: Boolean = true,
    val rgbGlowIntensity: Float = 0.6f,
    val isOverlayHidden: Boolean = false,
    val sliderStyle: SliderStyle = SliderStyle.GOOGLE,
    val sliderAccentColor: Long = 0xFF7C3AED, // Royal Violet as primary reference color
    val sliderWidthDp: Int = 40,
    val sliderHeightDp: Int = 135,
    val sliderCornerRadiusDp: Int = 20,
    val sliderBackgroundAlpha: Float = 0.5f,
    val rgbSpeedMillis: Int = 3500
)
