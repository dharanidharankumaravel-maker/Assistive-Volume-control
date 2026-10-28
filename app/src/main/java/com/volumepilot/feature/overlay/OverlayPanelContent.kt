package com.volumepilot.feature.overlay

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PhoneMissed
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PhoneMissed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.volumepilot.core.designsystem.rgbGlow
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.FloatingButtonColor
import com.volumepilot.domain.model.VolumeState
import com.volumepilot.domain.model.VolumeStream
import kotlin.math.roundToInt

val PANEL_WIDTH_DP: Dp = 210.dp

/**
 * Ultra-minimalist, sleek floating volume panel.
 *
 * Features:
 * - Exactly four vertical sliders: Media, Ring, In-Call, and Alarm.
 * - Icon-based controls with zero text labels for maximum minimalism.
 * - Horizontal capsule layout with consistent spacing and alignment.
 * - Smooth vertical touch and drag interaction with 1:1 real-time audio response.
 * - Continuously flowing animated RGB perimeter border following panel corners.
 */
@Composable
fun OverlayPanelContent(
    volumeState: VolumeState,
    selectedStreamType: AudioStreamType = AudioStreamType.MEDIA,
    onStreamSelected: (AudioStreamType) -> Unit = {},
    onVolumeChange: (AudioStreamType, Int) -> Unit,
    onMuteToggle: (AudioStreamType) -> Unit,
    onToggleVibrate: () -> Unit = {},
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier,
    opacity: Float = 0.92f,
    colorTheme: FloatingButtonColor = FloatingButtonColor.BASIL,
    rgbGlowEnabled: Boolean = true,
    rgbGlowIntensity: Float = 0.6f
) {
    val streams = volumeState.streams
    val accentColor = Color(colorTheme.primaryColor)
    val panelShape = RoundedCornerShape(22.dp)

    val targetStreams = listOf(
        AudioStreamType.MEDIA,
        AudioStreamType.RING,
        AudioStreamType.VOICE_CALL,
        AudioStreamType.ALARM
    )

    Card(
        modifier = modifier
            .width(PANEL_WIDTH_DP)
            .rgbGlow(
                shape = panelShape,
                enabled = rgbGlowEnabled,
                intensity = rgbGlowIntensity,
                strokeWidth = 1.4.dp
            )
            .shadow(
                elevation = 8.dp,
                shape = panelShape,
                spotColor = accentColor.copy(alpha = 0.35f)
            )
            .semantics { contentDescription = "Minimalist four-stream volume panel" },
        shape = panelShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = opacity)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            targetStreams.forEach { streamType ->
                val stream = streams[streamType]
                VerticalVolumeSlider(
                    streamType = streamType,
                    stream = stream,
                    accentColor = accentColor,
                    onVolumeChange = { level -> onVolumeChange(streamType, level) },
                    onMuteToggle = { onMuteToggle(streamType) }
                )
            }
        }
    }
}

/**
 * Modern vertical capsule volume slider.
 *
 * Supports intuitive vertical dragging, tap-to-level, and tap-icon-to-mute.
 */
@Composable
private fun VerticalVolumeSlider(
    streamType: AudioStreamType,
    stream: VolumeStream?,
    accentColor: Color,
    onVolumeChange: (Int) -> Unit,
    onMuteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minVol = stream?.minVolume ?: 0
    val maxVol = (stream?.maxVolume ?: 15).coerceAtLeast(minVol + 1)
    val curVol = stream?.currentVolume ?: 0
    val isMuted = stream?.isMuted == true || curVol == 0

    val range = (maxVol - minVol).toFloat().coerceAtLeast(1f)
    val normalizedProgress = if (isMuted) 0f else ((curVol - minVol).toFloat() / range).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = normalizedProgress,
        animationSpec = tween(durationMillis = 100),
        label = "sliderProgress"
    )

    val sliderShape = RoundedCornerShape(19.dp)
    val icon = getStreamIcon(streamType, isMuted)

    val activeFillColor by animateColorAsState(
        targetValue = if (isMuted) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f) else accentColor,
        animationSpec = tween(150),
        label = "activeFillColor"
    )

    Column(
        modifier = modifier
            .width(38.dp)
            .semantics {
                role = Role.Button
                contentDescription = "${streamType.displayName} volume, ${if (isMuted) "Muted" else "${(normalizedProgress * 100).toInt()}%"}"
                stateDescription = "$curVol of $maxVol"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .width(38.dp)
                .height(120.dp)
                .clip(sliderShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                .pointerInput(streamType, minVol, maxVol) {
                    detectTapGestures(
                        onTap = { offset ->
                            val heightPx = size.height.toFloat()
                            val pct = (1f - (offset.y / heightPx)).coerceIn(0f, 1f)
                            val newLevel = (minVol + (pct * (maxVol - minVol)).roundToInt()).coerceIn(minVol, maxVol)
                            onVolumeChange(newLevel)
                        }
                    )
                }
                .pointerInput(streamType, minVol, maxVol) {
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            val heightPx = size.height.toFloat()
                            val pct = (1f - (offset.y / heightPx)).coerceIn(0f, 1f)
                            val newLevel = (minVol + (pct * (maxVol - minVol)).roundToInt()).coerceIn(minVol, maxVol)
                            onVolumeChange(newLevel)
                        },
                        onVerticalDrag = { change, _ ->
                            change.consume()
                            val heightPx = size.height.toFloat()
                            val pct = (1f - (change.position.y / heightPx)).coerceIn(0f, 1f)
                            val newLevel = (minVol + (pct * (maxVol - minVol)).roundToInt()).coerceIn(minVol, maxVol)
                            onVolumeChange(newLevel)
                        }
                    )
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            val totalHeight = maxHeight

            // 1. Bottom-up active volume fill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(totalHeight * animatedProgress)
                    .clip(sliderShape)
                    .background(activeFillColor)
            )

            // 2. Stream Icon positioned at the bottom of the capsule
            val iconTint = if (animatedProgress > 0.22f && !isMuted) {
                Color.White
            } else if (isMuted) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .size(24.dp)
                    .clickable { onMuteToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Returns sleek, recognizable icons for each stream type and mute state.
 */
private fun getStreamIcon(streamType: AudioStreamType, isMuted: Boolean): ImageVector {
    return when (streamType) {
        AudioStreamType.MEDIA -> {
            if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp
        }
        AudioStreamType.RING -> {
            if (isMuted) Icons.Default.NotificationsOff else Icons.Default.NotificationsActive
        }
        AudioStreamType.VOICE_CALL -> {
            if (isMuted) Icons.AutoMirrored.Filled.PhoneMissed else Icons.Default.Call
        }
        AudioStreamType.ALARM -> {
            Icons.Default.Alarm
        }
        else -> Icons.AutoMirrored.Filled.VolumeUp
    }
}
