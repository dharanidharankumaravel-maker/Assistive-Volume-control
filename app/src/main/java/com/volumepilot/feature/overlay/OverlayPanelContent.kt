package com.volumepilot.feature.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.volumepilot.core.designsystem.LocalSpacing
import com.volumepilot.core.designsystem.components.VolumeSlider
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumeState

@Composable
fun OverlayPanelContent(
    volumeState: VolumeState,
    selectedStreamType: AudioStreamType,
    onStreamSelected: (AudioStreamType) -> Unit,
    onVolumeChange: (AudioStreamType, Int) -> Unit,
    onMuteToggle: (AudioStreamType) -> Unit,
    onToggleVibrate: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    opacity: Float = 0.95f
) {
    val spacing = LocalSpacing.current
    val streams = volumeState.streams
    val currentStream = streams[selectedStreamType]

    Card(
        modifier = modifier
            .widthIn(min = 280.dp, max = 340.dp)
            .semantics { contentDescription = "Floating volume panel" },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = opacity)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationRaised)
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            // Header: Title, Output Device, Ringer Mode, Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Volume",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    IconButton(
                        onClick = onToggleVibrate,
                        modifier = Modifier
                            .size(spacing.minTouchTarget)
                            .semantics {
                                contentDescription = "Ringer mode: currently ${volumeState.ringerMode.name}. Tap to toggle."
                            }
                    ) {
                        Icon(
                            imageVector = when (volumeState.ringerMode) {
                                RingerMode.VIBRATE -> Icons.Default.Vibration
                                RingerMode.SILENT -> Icons.Default.NotificationsOff
                                RingerMode.NORMAL -> Icons.AutoMirrored.Filled.VolumeUp
                            },
                            contentDescription = null,
                            tint = if (volumeState.ringerMode != RingerMode.NORMAL) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(spacing.minTouchTarget)
                            .semantics { contentDescription = "Close volume panel" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Stream Tabs Row
            val availableStreamTypes = listOf(
                AudioStreamType.MEDIA,
                AudioStreamType.RING,
                AudioStreamType.NOTIFICATION,
                AudioStreamType.ALARM
            )

            ScrollableTabRow(
                selectedTabIndex = availableStreamTypes.indexOf(selectedStreamType).coerceAtLeast(0),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                availableStreamTypes.forEach { type ->
                    Tab(
                        selected = type == selectedStreamType,
                        onClick = { onStreamSelected(type) },
                        text = {
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                }
            }

            // Slider for the selected stream
            if (currentStream != null) {
                VolumeSlider(
                    streamName = currentStream.streamType.displayName,
                    currentVolume = currentStream.currentVolume,
                    maxVolume = currentStream.maxVolume,
                    minVolume = currentStream.minVolume,
                    isMuted = currentStream.isMuted,
                    onVolumeChange = { newVolume ->
                        onVolumeChange(currentStream.streamType, newVolume)
                    },
                    onMuteToggle = {
                        onMuteToggle(currentStream.streamType)
                    }
                )
            }
        }
    }
}
