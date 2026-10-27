package com.volumepilot.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.volumepilot.core.designsystem.LocalSpacing

@Composable
fun VolumeSlider(
    streamName: String,
    currentVolume: Int,
    maxVolume: Int,
    minVolume: Int,
    isMuted: Boolean,
    onVolumeChange: (Int) -> Unit,
    onMuteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isCapped: Boolean = false,
    maxVolumeCap: Int? = null,
    enabled: Boolean = true
) {
    val spacing = LocalSpacing.current
    val percentage = if (maxVolume > minVolume) {
        ((currentVolume - minVolume).toFloat() / (maxVolume - minVolume) * 100).toInt()
    } else 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {
            // Header Row: Stream Name, Current Level %, Mute Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    IconButton(
                        onClick = onMuteToggle,
                        enabled = enabled,
                        modifier = Modifier
                            .size(spacing.minTouchTarget)
                            .semantics {
                                contentDescription = if (isMuted) "Unmute $streamName" else "Mute $streamName"
                            }
                    ) {
                        Icon(
                            imageVector = if (isMuted || currentVolume == 0) {
                                Icons.AutoMirrored.Filled.VolumeMute
                            } else if (percentage < 50) {
                                Icons.AutoMirrored.Filled.VolumeDown
                            } else {
                                Icons.AutoMirrored.Filled.VolumeUp
                            },
                            contentDescription = null,
                            tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = streamName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    if (isCapped && maxVolumeCap != null) {
                        Text(
                            text = "Cap: $maxVolumeCap",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Text(
                        text = if (isMuted) "Muted" else "$percentage%",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Slider Controls with - / + buttons for accessibility & precision
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                IconButton(
                    onClick = {
                        val next = (currentVolume - 1).coerceAtLeast(minVolume)
                        onVolumeChange(next)
                    },
                    enabled = enabled && currentVolume > minVolume,
                    modifier = Modifier
                        .size(spacing.minTouchTarget)
                        .semantics { contentDescription = "Decrease $streamName volume" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Slider(
                    value = currentVolume.toFloat(),
                    onValueChange = { onVolumeChange(it.toInt()) },
                    valueRange = minVolume.toFloat()..maxVolume.toFloat(),
                    steps = (maxVolume - minVolume - 1).coerceAtLeast(0),
                    enabled = enabled && !isMuted,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = spacing.minTouchTarget)
                        .semantics {
                            contentDescription = "$streamName volume slider, current value $percentage percent"
                        }
                )

                IconButton(
                    onClick = {
                        val limit = maxVolumeCap ?: maxVolume
                        val next = (currentVolume + 1).coerceAtMost(limit)
                        onVolumeChange(next)
                    },
                    enabled = enabled && currentVolume < (maxVolumeCap ?: maxVolume),
                    modifier = Modifier
                        .size(spacing.minTouchTarget)
                        .semantics { contentDescription = "Increase $streamName volume" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
