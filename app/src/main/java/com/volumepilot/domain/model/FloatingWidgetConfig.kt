package com.volumepilot.domain.model

enum class FloatingButtonType {
    POWER,
    VOLUME_UP,
    VOLUME_DOWN,
    TOGGLE_CONTROL
}

enum class FloatingButtonArrangement(val displayName: String) {
    // 6 Primary Recommended Combinations
    POWER_ONLY("Power Only"),
    UP_DOWN("Volume Up & Down"),
    POWER_TOGGLE("Power + Panel"),
    POWER_UP_DOWN("Power + Volume Up & Down"),
    TOGGLE_ONLY("Panel Only"),
    POWER_TOGGLE_UP_DOWN("Power + Panel + Volume"),

    // Legacy combinations retained for DataStore backward compatibility
    POWER_UP_DOWN_TOGGLE("Power • Vol+ • Vol- • Panel"),
    UP_DOWN_POWER("Vol+ • Vol- • Power"),
    UP_POWER_DOWN("Vol+ • Power • Vol-"),
    POWER_UP("Power • Vol+"),
    POWER_DOWN("Power • Vol-"),
    UP_DOWN_TOGGLE("Vol+ • Vol- • Panel"),
    TOGGLE_UP_DOWN("Panel • Vol+ • Vol-");

    /**
     * Special behavior for Volume Up (+) and Volume Down (-) combination.
     * Interacting with the overlay displays only the compact volume slider bar
     * rather than the full toggle control panel.
     */
    val isVolumeOnly: Boolean get() = this == UP_DOWN

    val hasPower: Boolean get() = getButtons().contains(FloatingButtonType.POWER)
    val hasVolumeUp: Boolean get() = getButtons().contains(FloatingButtonType.VOLUME_UP)
    val hasVolumeDown: Boolean get() = getButtons().contains(FloatingButtonType.VOLUME_DOWN)
    val hasToggle: Boolean get() = getButtons().contains(FloatingButtonType.TOGGLE_CONTROL)

    fun getButtons(): List<FloatingButtonType> {
        return when (this) {
            POWER_ONLY -> listOf(FloatingButtonType.POWER)
            UP_DOWN -> listOf(FloatingButtonType.VOLUME_UP, FloatingButtonType.VOLUME_DOWN)
            POWER_TOGGLE -> listOf(FloatingButtonType.POWER, FloatingButtonType.TOGGLE_CONTROL)
            POWER_UP_DOWN -> listOf(FloatingButtonType.POWER, FloatingButtonType.VOLUME_UP, FloatingButtonType.VOLUME_DOWN)
            TOGGLE_ONLY -> listOf(FloatingButtonType.TOGGLE_CONTROL)
            POWER_TOGGLE_UP_DOWN -> listOf(
                FloatingButtonType.POWER,
                FloatingButtonType.TOGGLE_CONTROL,
                FloatingButtonType.VOLUME_UP,
                FloatingButtonType.VOLUME_DOWN
            )

            // Legacy backward-compatibility mappings
            POWER_UP_DOWN_TOGGLE -> listOf(
                FloatingButtonType.POWER,
                FloatingButtonType.VOLUME_UP,
                FloatingButtonType.VOLUME_DOWN,
                FloatingButtonType.TOGGLE_CONTROL
            )
            UP_DOWN_POWER -> listOf(FloatingButtonType.VOLUME_UP, FloatingButtonType.VOLUME_DOWN, FloatingButtonType.POWER)
            UP_POWER_DOWN -> listOf(FloatingButtonType.VOLUME_UP, FloatingButtonType.POWER, FloatingButtonType.VOLUME_DOWN)
            POWER_UP -> listOf(FloatingButtonType.POWER, FloatingButtonType.VOLUME_UP)
            POWER_DOWN -> listOf(FloatingButtonType.POWER, FloatingButtonType.VOLUME_DOWN)
            UP_DOWN_TOGGLE -> listOf(FloatingButtonType.VOLUME_UP, FloatingButtonType.VOLUME_DOWN, FloatingButtonType.TOGGLE_CONTROL)
            TOGGLE_UP_DOWN -> listOf(FloatingButtonType.TOGGLE_CONTROL, FloatingButtonType.VOLUME_UP, FloatingButtonType.VOLUME_DOWN)
        }
    }

    companion object {
        /**
         * Curated list of primary functional button configurations.
         */
        val PRIMARY_CONFIGURATIONS: List<FloatingButtonArrangement> = listOf(
            POWER_ONLY,
            UP_DOWN,
            POWER_TOGGLE,
            POWER_UP_DOWN,
            TOGGLE_ONLY,
            POWER_TOGGLE_UP_DOWN
        )
    }
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
