package com.volumepilot.domain.model

/**
 * Five selectable volume slider style presets inspired by major mobile operating systems.
 *
 * Each preset defines distinct visual proportions, layout hierarchy, and interaction details:
 * - [GOOGLE]: Material You pill with top circular speaker badge, inner active pill with music note, and bottom mute button.
 * - [APPLE]: Sleek, frosted vertical capsule with smooth rounded corners and clean integrated indicator.
 * - [SAMSUNG]: Slim One UI capsule with top triple-dot indicator and minimal volume level fill.
 * - [XIAOMI]: MIUI / HyperOS rounded capsule with bottom quick action buttons (Bell & Crescent Moon).
 * - [ONEPLUS]: Bold OxygenOS capsule with bottom action controls (Three dots & Alert mode).
 */
enum class SliderStyle(
    val displayName: String,
    val defaultWidthDp: Int,
    val defaultHeightDp: Int,
    val defaultCornerRadiusDp: Int
) {
    GOOGLE(
        displayName = "Google",
        defaultWidthDp = 40,
        defaultHeightDp = 140,
        defaultCornerRadiusDp = 20
    ),
    APPLE(
        displayName = "Apple",
        defaultWidthDp = 44,
        defaultHeightDp = 112,
        defaultCornerRadiusDp = 18
    ),
    SAMSUNG(
        displayName = "Samsung",
        defaultWidthDp = 32,
        defaultHeightDp = 135,
        defaultCornerRadiusDp = 16
    ),
    XIAOMI(
        displayName = "Xiaomi",
        defaultWidthDp = 40,
        defaultHeightDp = 130,
        defaultCornerRadiusDp = 20
    ),
    ONEPLUS(
        displayName = "ONEPLUS",
        defaultWidthDp = 42,
        defaultHeightDp = 145,
        defaultCornerRadiusDp = 21
    )
}

