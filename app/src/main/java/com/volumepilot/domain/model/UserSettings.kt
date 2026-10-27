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
    val hasCompletedOnboarding: Boolean = false
)
