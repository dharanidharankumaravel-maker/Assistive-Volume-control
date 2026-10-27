package com.volumepilot.feature.onboarding

import com.volumepilot.domain.model.OemGuidance
import com.volumepilot.domain.model.OemManufacturer

data class OnboardingUiState(
    val isOverlayPermissionGranted: Boolean = false,
    val isModifyAudioGranted: Boolean = true,
    val isPostNotificationGranted: Boolean = true,
    val oemGuidance: OemGuidance = OemGuidance(
        manufacturer = OemManufacturer.GENERIC,
        headline = "Standard Setup",
        explanation = "VolumePilot works out of the box with zero configuration.",
        steps = emptyList()
    ),
    val isCompleting: Boolean = false
) {
    val canProceed: Boolean
        get() = isOverlayPermissionGranted
}
