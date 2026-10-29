package com.volumepilot.feature.overlay

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.volumepilot.core.designsystem.rgbGlow
import com.volumepilot.domain.model.FloatingButtonArrangement
import com.volumepilot.domain.model.FloatingButtonColor
import com.volumepilot.domain.model.VolumeStream

/**
 * Compact, minimalist floating control widget designed with Basil's visual identity.
 *
 * Dynamically renders the exact controls chosen in settings:
 * - Case 1: Only Volume Up (+) and Volume Down (−) without Power.
 * - Case 2: Power + Volume Up (+) and Volume Down (−) in configured order.
 * - Case 3: Other custom combinations (Power + Vol+, Power + Vol-).
 *
 * All redundant expansion arrows have been completely removed.
 */
@Composable
fun BasilFloatingControlWidget(
    stream: VolumeStream?,
    opacity: Float,
    sizeDp: Int,
    arrangement: FloatingButtonArrangement,
    colorTheme: FloatingButtonColor,
    isExpanded: Boolean,
    isDockedToLeft: Boolean,
    onPowerSingleTap: () -> Unit,
    onPowerLongPress: () -> Unit,
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit,
    onToggleExpand: () -> Unit,
    onMuteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    rgbGlowEnabled: Boolean = true,
    rgbGlowIntensity: Float = 0.6f
) {
    val streamName = stream?.streamType?.displayName ?: "Media"
    val percentage = stream?.percentage ?: 50
    val isMuted = stream?.isMuted == true || (stream?.currentVolume == 0)

    val accentColor = Color(colorTheme.primaryColor)
    val buttonWidth = sizeDp.dp

    // Dynamic dock shape: rounded pill with subtle flattening against docked screen edge
    val pillCornerRadius = 22.dp
    val dockShape = if (isDockedToLeft) {
        RoundedCornerShape(
            topStart = 6.dp,
            bottomStart = 6.dp,
            topEnd = pillCornerRadius,
            bottomEnd = pillCornerRadius
        )
    } else {
        RoundedCornerShape(
            topStart = pillCornerRadius,
            bottomStart = pillCornerRadius,
            topEnd = 6.dp,
            bottomEnd = 6.dp
        )
    }

    val backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = opacity)
    val borderColor = accentColor.copy(alpha = opacity * 0.7f)

    Column(
        modifier = modifier
            .rgbGlow(
                shape = dockShape,
                enabled = rgbGlowEnabled,
                intensity = rgbGlowIntensity,
                strokeWidth = 1.4.dp
            )
            .shadow(
                elevation = 6.dp,
                shape = dockShape,
                spotColor = accentColor.copy(alpha = 0.35f)
            )
            .clip(dockShape)
            .background(backgroundColor)
            .border(
                width = if (rgbGlowEnabled) 0.5.dp else 1.2.dp,
                color = if (rgbGlowEnabled) Color.Transparent else borderColor,
                shape = dockShape
            )
            .width(buttonWidth)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Dynamically render controls based on the configured arrangement
        when (arrangement) {
            // Case 1: Only Volume Up (+) and Volume Down (−) without Power
            FloatingButtonArrangement.UP_DOWN -> {
                VolumeUpControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    percentage = percentage,
                    onSingleTap = onVolumeUp,
                    onLongPress = onToggleExpand,
                    isVolumeOnly = true
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                VolumeDownControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    isMuted = isMuted,
                    onSingleTap = onVolumeDown,
                    onLongPress = onMuteToggle
                )
            }

            // Case 2: Power + Vol+ + Vol-
            FloatingButtonArrangement.POWER_UP_DOWN -> {
                PowerControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    onSingleTap = onPowerSingleTap,
                    onLongPress = onPowerLongPress
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                VolumeUpControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    percentage = percentage,
                    onSingleTap = onVolumeUp,
                    onLongPress = onToggleExpand,
                    isVolumeOnly = false
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                VolumeDownControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    isMuted = isMuted,
                    onSingleTap = onVolumeDown,
                    onLongPress = onMuteToggle
                )
            }

            // Case 2: Vol+ + Vol- + Power
            FloatingButtonArrangement.UP_DOWN_POWER -> {
                VolumeUpControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    percentage = percentage,
                    onSingleTap = onVolumeUp,
                    onLongPress = onToggleExpand,
                    isVolumeOnly = false
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                VolumeDownControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    isMuted = isMuted,
                    onSingleTap = onVolumeDown,
                    onLongPress = onMuteToggle
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                PowerControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    onSingleTap = onPowerSingleTap,
                    onLongPress = onPowerLongPress
                )
            }

            // Case 2: Vol+ + Power + Vol-
            FloatingButtonArrangement.UP_POWER_DOWN -> {
                VolumeUpControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    percentage = percentage,
                    onSingleTap = onVolumeUp,
                    onLongPress = onToggleExpand,
                    isVolumeOnly = false
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                PowerControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    onSingleTap = onPowerSingleTap,
                    onLongPress = onPowerLongPress
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                VolumeDownControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    isMuted = isMuted,
                    onSingleTap = onVolumeDown,
                    onLongPress = onMuteToggle
                )
            }

            // Case 3: Power + Vol+
            FloatingButtonArrangement.POWER_UP -> {
                PowerControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    onSingleTap = onPowerSingleTap,
                    onLongPress = onPowerLongPress
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                VolumeUpControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    percentage = percentage,
                    onSingleTap = onVolumeUp,
                    onLongPress = onToggleExpand,
                    isVolumeOnly = false
                )
            }

            // Case 3: Power + Vol-
            FloatingButtonArrangement.POWER_DOWN -> {
                PowerControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    onSingleTap = onPowerSingleTap,
                    onLongPress = onPowerLongPress
                )
                PillDivider(opacity = opacity, accentColor = accentColor)
                VolumeDownControlButton(
                    sizeDp = sizeDp,
                    accentColor = accentColor,
                    streamName = streamName,
                    isMuted = isMuted,
                    onSingleTap = onVolumeDown,
                    onLongPress = onMuteToggle
                )
            }
        }
    }
}

@Composable
private fun PowerControlButton(
    sizeDp: Int,
    accentColor: Color,
    onSingleTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size((sizeDp - 4).dp)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onLongPress = { onLongPress() }
                )
            }
            .semantics {
                role = Role.Button
                contentDescription = "Power button. Tap to perform power action, long press to capture screenshot."
                onClick(label = "Power action") {
                    onSingleTap()
                    true
                }
                customActions = listOf(
                    CustomAccessibilityAction(label = "Capture Screenshot") {
                        onLongPress()
                        true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PowerSettingsNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun VolumeUpControlButton(
    sizeDp: Int,
    accentColor: Color,
    streamName: String,
    percentage: Int,
    onSingleTap: () -> Unit,
    onLongPress: () -> Unit,
    isVolumeOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val actionLabel = if (isVolumeOnly) "Show volume slider" else "Open volume panel"

    Box(
        modifier = modifier
            .size((sizeDp - 4).dp)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onLongPress = { onLongPress() }
                )
            }
            .semantics {
                role = Role.Button
                contentDescription = "Increase $streamName volume. Currently $percentage percent."
                onClick(label = "Increase volume") {
                    onSingleTap()
                    true
                }
                customActions = listOf(
                    CustomAccessibilityAction(label = actionLabel) {
                        onLongPress()
                        true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
private fun VolumeDownControlButton(
    sizeDp: Int,
    accentColor: Color,
    streamName: String,
    isMuted: Boolean,
    onSingleTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size((sizeDp - 4).dp)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onLongPress = { onLongPress() }
                )
            }
            .semantics {
                role = Role.Button
                contentDescription = "Decrease $streamName volume. ${if (isMuted) "Muted" else ""}"
                onClick(label = "Decrease volume") {
                    onSingleTap()
                    true
                }
                customActions = listOf(
                    CustomAccessibilityAction(label = "Mute or unmute") {
                        onLongPress()
                        true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeDown,
            contentDescription = null,
            tint = if (isMuted) MaterialTheme.colorScheme.error else accentColor,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
private fun PillDivider(
    opacity: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(20.dp)
            .height(0.8.dp)
            .background(accentColor.copy(alpha = opacity * 0.3f))
    )
}
