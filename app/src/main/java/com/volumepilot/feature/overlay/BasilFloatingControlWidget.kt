package com.volumepilot.feature.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.volumepilot.domain.model.FloatingButtonType
import com.volumepilot.domain.model.VolumeStream

/**
 * Modern floating controls featuring independent, perfectly circular floating buttons.
 *
 * Each floating control is rendered as a standalone circular button:
 * - Power: Power icon, tap for power action, long-press for screenshot.
 * - Volume Up: "+" symbol, tap to increase volume, long-press to open panel.
 * - Volume Down: "−" symbol, tap to decrease volume, long-press to mute.
 * - Toggle Control: Distinct sliders/control icon (Tune), tap to expand/collapse panel.
 *
 * Each button features its own animated circular RGB perimeter border, subtle shadow,
 * and maintains consistent geometry on both left and right screen edges without deformation.
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
    rgbGlowIntensity: Float = 0.6f,
    rgbSpeedMillis: Int = 3500
) {
    val streamName = stream?.streamType?.displayName ?: "Media"
    val percentage = stream?.percentage ?: 50
    val isMuted = stream?.isMuted == true || (stream?.currentVolume == 0)

    val accentColor = Color(colorTheme.primaryColor)
    val backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = opacity)
    val borderColor = accentColor.copy(alpha = opacity * 0.7f)

    val buttons = arrangement.getButtons()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        buttons.forEach { buttonType ->
            when (buttonType) {
                FloatingButtonType.POWER -> {
                    CircularFloatingButton(
                        icon = Icons.Default.PowerSettingsNew,
                        contentDescription = "Power button. Tap to perform power action, long press to capture screenshot.",
                        iconTint = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                        onClick = onPowerSingleTap,
                        onLongClick = onPowerLongPress,
                        customActionLabel = "Capture Screenshot",
                        sizeDp = sizeDp,
                        accentColor = accentColor,
                        backgroundColor = backgroundColor,
                        borderColor = borderColor,
                        rgbGlowEnabled = rgbGlowEnabled,
                        rgbGlowIntensity = rgbGlowIntensity,
                        rgbSpeedMillis = rgbSpeedMillis
                    )
                }

                FloatingButtonType.VOLUME_UP -> {
                    val actionLabel = if (arrangement.isVolumeOnly) "Show volume slider" else "Open volume panel"
                    CircularFloatingButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Increase $streamName volume. Currently $percentage percent.",
                        iconTint = accentColor,
                        onClick = onVolumeUp,
                        onLongClick = onToggleExpand,
                        customActionLabel = actionLabel,
                        sizeDp = sizeDp,
                        accentColor = accentColor,
                        backgroundColor = backgroundColor,
                        borderColor = borderColor,
                        rgbGlowEnabled = rgbGlowEnabled,
                        rgbGlowIntensity = rgbGlowIntensity,
                        rgbSpeedMillis = rgbSpeedMillis
                    )
                }

                FloatingButtonType.VOLUME_DOWN -> {
                    CircularFloatingButton(
                        icon = Icons.Default.Remove,
                        contentDescription = "Decrease $streamName volume. ${if (isMuted) "Muted" else ""}",
                        iconTint = if (isMuted) MaterialTheme.colorScheme.error else accentColor,
                        onClick = onVolumeDown,
                        onLongClick = onMuteToggle,
                        customActionLabel = "Mute or unmute",
                        sizeDp = sizeDp,
                        accentColor = accentColor,
                        backgroundColor = backgroundColor,
                        borderColor = borderColor,
                        rgbGlowEnabled = rgbGlowEnabled,
                        rgbGlowIntensity = rgbGlowIntensity,
                        rgbSpeedMillis = rgbSpeedMillis
                    )
                }

                FloatingButtonType.TOGGLE_CONTROL -> {
                    CircularFloatingButton(
                        icon = Icons.Default.Tune,
                        contentDescription = if (isExpanded) "Collapse volume panel" else "Expand volume panel",
                        iconTint = accentColor,
                        onClick = onToggleExpand,
                        onLongClick = null,
                        customActionLabel = if (isExpanded) "Collapse panel" else "Expand panel",
                        sizeDp = sizeDp,
                        accentColor = accentColor,
                        backgroundColor = backgroundColor,
                        borderColor = borderColor,
                        rgbGlowEnabled = rgbGlowEnabled,
                        rgbGlowIntensity = rgbGlowIntensity,
                        rgbSpeedMillis = rgbSpeedMillis
                    )
                }
            }
        }
    }
}

/**
 * Reusable, perfectly circular floating action button with independent animated RGB border.
 */
@Composable
private fun CircularFloatingButton(
    icon: ImageVector,
    contentDescription: String,
    iconTint: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    customActionLabel: String?,
    sizeDp: Int,
    accentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    rgbGlowEnabled: Boolean,
    rgbGlowIntensity: Float,
    rgbSpeedMillis: Int,
    modifier: Modifier = Modifier
) {
    val buttonSize = sizeDp.dp
    val iconSize = (sizeDp * 0.44f).dp.coerceIn(18.dp, 24.dp)

    Box(
        modifier = modifier
            .size(buttonSize)
            .shadow(
                elevation = 5.dp,
                shape = CircleShape,
                spotColor = accentColor.copy(alpha = 0.35f)
            )
            .rgbGlow(
                shape = CircleShape,
                enabled = rgbGlowEnabled,
                intensity = rgbGlowIntensity,
                strokeWidth = 1.4.dp,
                durationMillis = rgbSpeedMillis
            )
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (rgbGlowEnabled) 0.5.dp else 1.2.dp,
                color = if (rgbGlowEnabled) Color.Transparent else borderColor,
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick?.invoke() }
                )
            }
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
                onClick(label = contentDescription) {
                    onClick()
                    true
                }
                if (onLongClick != null && customActionLabel != null) {
                    customActions = listOf(
                        CustomAccessibilityAction(label = customActionLabel) {
                            onLongClick()
                            true
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}
