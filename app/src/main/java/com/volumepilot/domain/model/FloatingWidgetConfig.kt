package com.volumepilot.domain.model

enum class FloatingButtonArrangement(val displayName: String) {
    UP_DOWN("Vol+ • Vol-"),
    POWER_UP_DOWN("Power • Vol+ • Vol-"),
    UP_DOWN_POWER("Vol+ • Vol- • Power"),
    UP_POWER_DOWN("Vol+ • Power • Vol-"),
    POWER_UP("Power • Vol+"),
    POWER_DOWN("Power • Vol-");

    /**
     * Case 1: Only Volume Up (+) and Volume Down (-) selected (without Power).
     * Interacting with the overlay displays only the compact volume slider bar
     * rather than the full toggle control panel.
     */
    val isVolumeOnly: Boolean get() = this == UP_DOWN

    /**
     * Case 2: Power + Any Other Button selected.
     * Allows opening the full volume toggle panel while preserving
     * the Power button's tap and long-press screenshot shortcuts.
     */
    val hasPower: Boolean get() = this != UP_DOWN

    val hasVolumeUp: Boolean get() = this != POWER_DOWN
    val hasVolumeDown: Boolean get() = this != POWER_UP
}

enum class FloatingButtonColor(
    val displayName: String,
    val primaryColor: Long,
    val containerColor: Long,
    val onPrimaryColor: Long = 0xFFFFFFFF
) {
    BASIL(
        displayName = "Basil Emerald",
        primaryColor = 0xFF00875A,
        containerColor = 0xFFE6F4EA,
        onPrimaryColor = 0xFFFFFFFF
    ),
    SLATE(
        displayName = "Slate Dark",
        primaryColor = 0xFF22323F,
        containerColor = 0xFF384956,
        onPrimaryColor = 0xFFFFFFFF
    ),
    OCEAN(
        displayName = "Ocean Blue",
        primaryColor = 0xFF006495,
        containerColor = 0xFFCBE6FF,
        onPrimaryColor = 0xFFFFFFFF
    ),
    AMBER(
        displayName = "Sunset Amber",
        primaryColor = 0xFFD97706,
        containerColor = 0xFFFEF3C7,
        onPrimaryColor = 0xFFFFFFFF
    ),
    VIOLET(
        displayName = "Royal Violet",
        primaryColor = 0xFF7C3AED,
        containerColor = 0xFFEDE9FE,
        onPrimaryColor = 0xFFFFFFFF
    )
}

enum class PowerSingleTapAction(val displayName: String) {
    LOCK_SCREEN("Lock Screen"),
    POWER_MENU("Power Menu"),
    TOGGLE_PANEL("Toggle Volume Panel")
}

enum class PowerLongPressAction(val displayName: String) {
    TAKE_SCREENSHOT("Take Screenshot"),
    POWER_MENU("Power Menu"),
    LOCK_SCREEN("Lock Screen")
}
