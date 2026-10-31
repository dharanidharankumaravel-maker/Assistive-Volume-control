package com.volumepilot.feature.home

import com.volumepilot.core.common.AppError
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.model.VolumeState
import com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus

data class HomeUiState(
    val isLoading: Boolean = true,
    val volumeState: VolumeState = VolumeState(),
    val settings: UserSettings = UserSettings(),
    val error: AppError? = null,
    val isNotificationPolicyGranted: Boolean = false,
    val isOverlayPermissionGranted: Boolean = false,
    val isAccessibilityGranted: Boolean = false,
    val accessibilityStatus: AccessibilityServiceStatus = AccessibilityServiceStatus.DISABLED
)
