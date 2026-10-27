package com.volumepilot.feature.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.volumepilot.domain.model.VolumeStream

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription

@Composable
fun OverlayBubbleContent(
    stream: VolumeStream?,
    opacity: Float,
    sizeDp: Int,
    onSingleTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = stream?.percentage ?: 50
    val isMuted = stream?.isMuted == true || (stream?.currentVolume == 0)

    val icon = when {
        isMuted -> Icons.AutoMirrored.Filled.VolumeMute
        percentage < 50 -> Icons.AutoMirrored.Filled.VolumeDown
        else -> Icons.AutoMirrored.Filled.VolumeUp
    }

    val streamName = stream?.streamType?.displayName ?: "Audio"

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = opacity)
            )
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = opacity),
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onDoubleTap = { onDoubleTap() },
                    onLongPress = { onLongPress() }
                )
            }
            .semantics {
                role = Role.Button
                contentDescription = "$streamName volume assistant"
                stateDescription = if (isMuted) "Muted" else "$percentage percent"
                onClick(label = "Open volume control panel") {
                    onSingleTap()
                    true
                }
                customActions = listOf(
                    CustomAccessibilityAction(label = "Mute or unmute") {
                        onDoubleTap()
                        true
                    },
                    CustomAccessibilityAction(label = "Switch active stream") {
                        onLongPress()
                        true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {

        // Subtle circular indicator showing volume progress
        CircularProgressIndicator(
            progress = { (percentage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier.size((sizeDp - 4).dp),
            color = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            trackColor = Color.Transparent,
            strokeWidth = 2.dp
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size((sizeDp * 0.5f).dp)
        )
    }
}
