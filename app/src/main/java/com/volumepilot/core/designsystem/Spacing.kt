package com.volumepilot.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val minTouchTarget: Dp = 48.dp, // WCAG 2.1 AA compliant target
    val cardCornerRadius: Dp = 16.dp,
    val elevationDefault: Dp = 2.dp,
    val elevationRaised: Dp = 6.dp
)

val LocalSpacing = androidx.compose.runtime.staticCompositionLocalOf { Spacing() }
