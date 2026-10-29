package com.volumepilot.feature.overlay.slider

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.SliderStyle
import com.volumepilot.domain.model.VolumeStream
import kotlin.math.roundToInt

/**
 * Unified stylized volume slider supporting five distinct mobile OS presets:
 * - [SliderStyle.GOOGLE]: Tall Material You pill with top circular speaker badge, inner active pill with music note, and bottom mute button.
 * - [SliderStyle.APPLE]: Sleek, frosted vertical capsule with smooth rounded corners and clean integrated indicator.
 * - [SliderStyle.SAMSUNG]: Slim One UI capsule with top triple-dot indicator and minimal volume level fill.
 * - [SliderStyle.XIAOMI]: MIUI / HyperOS rounded capsule with bottom quick action buttons (Bell & Crescent Moon).
 * - [SliderStyle.ONEPLUS]: Bold OxygenOS capsule with bottom action controls (Three dots & Alert mode).
 */
@Composable
fun StylizedVolumeSlider(
    style: SliderStyle,
    streamType: AudioStreamType,
    stream: VolumeStream?,
    accentColor: Color,
    sliderWidth: Dp,
    sliderHeight: Dp,
    cornerRadius: Dp,
    backgroundAlpha: Float,
    onVolumeChange: (Int) -> Unit,
    onMuteToggle: () -> Unit,
    onSecondaryAction: () -> Unit = {},
    modifier: Modifier = Modifier,
    overrideProgress: Float? = null // For preview mock sliders
) {
    val minVol = stream?.minVolume ?: 0
    val maxVol = (stream?.maxVolume ?: 15).coerceAtLeast(minVol + 1)
    val curVol = stream?.currentVolume ?: 0
    val isMuted = stream?.isMuted == true || (curVol == 0 && overrideProgress == null)

    val range = (maxVol - minVol).toFloat().coerceAtLeast(1f)
    val rawProgress = overrideProgress ?: if (isMuted) 0f else ((curVol - minVol).toFloat() / range).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 100),
        label = "stylizedSliderProgress"
    )

    val streamIcon = getStreamIcon(streamType, isMuted)

    Column(
        modifier = modifier
            .width(sliderWidth)
            .semantics {
                role = Role.Button
                contentDescription = "${streamType.displayName} volume, ${if (isMuted) "Muted" else "${(animatedProgress * 100).toInt()}%"}"
                stateDescription = "$curVol of $maxVol"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
    ) {
        when (style) {
            SliderStyle.GOOGLE -> GoogleSlider(
                progress = animatedProgress,
                isMuted = isMuted,
                accentColor = accentColor,
                sliderWidth = sliderWidth,
                sliderHeight = sliderHeight,
                cornerRadius = cornerRadius,
                backgroundAlpha = backgroundAlpha,
                streamIcon = streamIcon,
                onVolumeChange = { pct ->
                    val newLevel = (minVol + (pct * (maxVol - minVol)).roundToInt()).coerceIn(minVol, maxVol)
                    onVolumeChange(newLevel)
                },
                onMuteToggle = onMuteToggle
            )
            SliderStyle.APPLE -> AppleSlider(
                progress = animatedProgress,
                isMuted = isMuted,
                accentColor = accentColor,
                sliderWidth = sliderWidth,
                sliderHeight = sliderHeight,
                cornerRadius = cornerRadius,
                backgroundAlpha = backgroundAlpha,
                streamIcon = streamIcon,
                onVolumeChange = { pct ->
                    val newLevel = (minVol + (pct * (maxVol - minVol)).roundToInt()).coerceIn(minVol, maxVol)
                    onVolumeChange(newLevel)
                },
                onMuteToggle = onMuteToggle
            )
            SliderStyle.SAMSUNG -> SamsungSlider(
                progress = animatedProgress,
                isMuted = isMuted,
                accentColor = accentColor,
                sliderWidth = sliderWidth,
                sliderHeight = sliderHeight,
                cornerRadius = cornerRadius,
                backgroundAlpha = backgroundAlpha,
                streamIcon = streamIcon,
                onVolumeChange = { pct ->
                    val newLevel = (minVol + (pct * (maxVol - minVol)).roundToInt()).coerceIn(minVol, maxVol)
                    onVolumeChange(newLevel)
                },
                onMuteToggle = onMuteToggle
            )
            SliderStyle.XIAOMI -> XiaomiSlider(
                progress = animatedProgress,
                isMuted = isMuted,
                accentColor = accentColor,
                sliderWidth = sliderWidth,
                sliderHeight = sliderHeight,
                cornerRadius = cornerRadius,
                backgroundAlpha = backgroundAlpha,
                streamIcon = streamIcon,
                onVolumeChange = { pct ->
                    val newLevel = (minVol + (pct * (maxVol - minVol)).roundToInt()).coerceIn(minVol, maxVol)
                    onVolumeChange(newLevel)
                },
                onMuteToggle = onMuteToggle,
                onSecondaryAction = onSecondaryAction
            )
            SliderStyle.ONEPLUS -> OnePlusSlider(
                progress = animatedProgress,
                isMuted = isMuted,
                accentColor = accentColor,
                sliderWidth = sliderWidth,
                sliderHeight = sliderHeight,
                cornerRadius = cornerRadius,
                backgroundAlpha = backgroundAlpha,
                streamIcon = streamIcon,
                onVolumeChange = { pct ->
                    val newLevel = (minVol + (pct * (maxVol - minVol)).roundToInt()).coerceIn(minVol, maxVol)
                    onVolumeChange(newLevel)
                },
                onMuteToggle = onMuteToggle,
                onSecondaryAction = onSecondaryAction
            )
        }
    }
}

/**
 * GOOGLE PRESET:
 * Material You pill with top circular speaker badge, inner active pill with music note, and bottom mute button.
 */
@Composable
private fun GoogleSlider(
    progress: Float,
    isMuted: Boolean,
    accentColor: Color,
    sliderWidth: Dp,
    sliderHeight: Dp,
    cornerRadius: Dp,
    backgroundAlpha: Float,
    streamIcon: ImageVector,
    onVolumeChange: (Float) -> Unit,
    onMuteToggle: () -> Unit
) {
    val trackShape = RoundedCornerShape(cornerRadius)
    val containerBg = Color(0xFFF3F0F6).copy(alpha = backgroundAlpha.coerceIn(0.2f, 1f))
    val activeFillColor by animateColorAsState(
        targetValue = if (isMuted) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f) else accentColor,
        animationSpec = tween(150),
        label = "googleActiveFill"
    )

    // 1. Main Slider Capsule Track
    BoxWithConstraints(
        modifier = Modifier
            .width(sliderWidth)
            .height(sliderHeight)
            .clip(trackShape)
            .background(containerBg)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                    onVolumeChange(pct)
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        val pct = (1f - (change.position.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        val totalHeight = maxHeight

        // Active bottom-up fill with inner pill margin
        val innerPadding = 3.dp
        Box(
            modifier = Modifier
                .padding(horizontal = innerPadding, vertical = innerPadding)
                .fillMaxWidth()
                .height(((totalHeight - (innerPadding * 2)) * progress).coerceAtLeast(0.dp))
                .clip(RoundedCornerShape(cornerRadius))
                .background(activeFillColor),
            contentAlignment = Alignment.Center
        ) {
            if (progress > 0.35f) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        // Top Circular Badge with stream icon
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 5.dp)
                .size((sliderWidth - 8.dp).coerceAtLeast(26.dp))
                .clip(CircleShape)
                .background(Color(0xFF45325A).copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = streamIcon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }

        // Bottom subtle pill dash
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .width(10.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color.White.copy(alpha = 0.6f))
        )
    }

    // 2. Separate Bottom Circular Button (Mute/Ringer toggle)
    Box(
        modifier = Modifier
            .size((sliderWidth - 4.dp).coerceAtLeast(30.dp))
            .clip(CircleShape)
            .background(Color(0xFFF3F0F6).copy(alpha = backgroundAlpha.coerceIn(0.3f, 1f)))
            .clickable { onMuteToggle() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Toggle Mute",
            tint = if (isMuted) MaterialTheme.colorScheme.error else Color(0xFF45325A),
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * APPLE PRESET:
 * Slim, elegant vertical slider with smooth rounded corners, clean fill indicator, and subtle transparency.
 */
@Composable
private fun AppleSlider(
    progress: Float,
    isMuted: Boolean,
    accentColor: Color,
    sliderWidth: Dp,
    sliderHeight: Dp,
    cornerRadius: Dp,
    backgroundAlpha: Float,
    streamIcon: ImageVector,
    onVolumeChange: (Float) -> Unit,
    onMuteToggle: () -> Unit
) {
    val trackShape = RoundedCornerShape(cornerRadius)
    val containerBg = Color(0xFF38264C).copy(alpha = backgroundAlpha.coerceIn(0.2f, 0.95f))
    val activeFillColor by animateColorAsState(
        targetValue = if (isMuted) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f) else Color(0xFFF5EEF8),
        animationSpec = tween(150),
        label = "appleActiveFill"
    )

    BoxWithConstraints(
        modifier = Modifier
            .width(sliderWidth)
            .height(sliderHeight)
            .clip(trackShape)
            .background(containerBg)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                    onVolumeChange(pct)
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        val pct = (1f - (change.position.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        val totalHeight = maxHeight

        // Solid clean bottom-up fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight * progress)
                .clip(trackShape)
                .background(activeFillColor)
        )

        // Integrated volume icon at the lower third
        val iconTint = if (progress > 0.25f && !isMuted) {
            Color(0xFF38264C) // Dark contrasting icon when submerged in light fill
        } else if (isMuted) {
            MaterialTheme.colorScheme.error
        } else {
            Color.White.copy(alpha = 0.85f)
        }

        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(24.dp)
                .clickable { onMuteToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = streamIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

/**
 * SAMSUNG (ONE UI) PRESET:
 * Compact vertical slider with minimal thumb indicator, rounded track, and modern One UI-inspired appearance.
 */
@Composable
private fun SamsungSlider(
    progress: Float,
    isMuted: Boolean,
    accentColor: Color,
    sliderWidth: Dp,
    sliderHeight: Dp,
    cornerRadius: Dp,
    backgroundAlpha: Float,
    streamIcon: ImageVector,
    onVolumeChange: (Float) -> Unit,
    onMuteToggle: () -> Unit
) {
    val trackShape = RoundedCornerShape(cornerRadius)
    val containerBg = Color(0xFF422F55).copy(alpha = backgroundAlpha.coerceIn(0.2f, 0.95f))
    val activeFillColor by animateColorAsState(
        targetValue = if (isMuted) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f) else Color(0xFFF7F4F9),
        animationSpec = tween(150),
        label = "samsungActiveFill"
    )

    BoxWithConstraints(
        modifier = Modifier
            .width(sliderWidth)
            .height(sliderHeight)
            .clip(trackShape)
            .background(containerBg)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                    onVolumeChange(pct)
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        val pct = (1f - (change.position.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        val totalHeight = maxHeight

        // One UI bottom-up active fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight * progress)
                .clip(trackShape)
                .background(activeFillColor)
        )

        // Top One UI Triple-dot indicator (...)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.65f),
                modifier = Modifier.size(14.dp)
            )
        }

        // Bottom stream icon
        val iconTint = if (progress > 0.22f && !isMuted) {
            Color(0xFF422F55)
        } else if (isMuted) {
            MaterialTheme.colorScheme.error
        } else {
            Color.White.copy(alpha = 0.85f)
        }

        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(22.dp)
                .clickable { onMuteToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = streamIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

/**
 * XIAOMI (HYPEROS / MIUI) PRESET:
 * Rounded vertical slider with a distinct filled track, compact proportions, and integrated volume controls.
 */
@Composable
private fun XiaomiSlider(
    progress: Float,
    isMuted: Boolean,
    accentColor: Color,
    sliderWidth: Dp,
    sliderHeight: Dp,
    cornerRadius: Dp,
    backgroundAlpha: Float,
    streamIcon: ImageVector,
    onVolumeChange: (Float) -> Unit,
    onMuteToggle: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    val trackShape = RoundedCornerShape(cornerRadius)
    val containerBg = Color(0xFF3F2D54).copy(alpha = backgroundAlpha.coerceIn(0.2f, 0.95f))
    val activeFillColor by animateColorAsState(
        targetValue = if (isMuted) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f) else Color(0xFFFAF6FC),
        animationSpec = tween(150),
        label = "xiaomiActiveFill"
    )

    // 1. Main Capsule Track
    BoxWithConstraints(
        modifier = Modifier
            .width(sliderWidth)
            .height(sliderHeight)
            .clip(trackShape)
            .background(containerBg)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                    onVolumeChange(pct)
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        val pct = (1f - (change.position.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        val totalHeight = maxHeight

        // Bottom-up fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight * progress)
                .clip(trackShape)
                .background(activeFillColor)
        )

        // Top subtle dash
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .width(12.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color.White.copy(alpha = 0.4f))
        )

        // Bottom sound icon inside fill
        val iconTint = if (progress > 0.22f && !isMuted) {
            Color(0xFF3F2D54)
        } else if (isMuted) {
            MaterialTheme.colorScheme.error
        } else {
            Color.White.copy(alpha = 0.85f)
        }

        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(24.dp)
                .clickable { onMuteToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(17.dp)
            )
        }
    }

    // 2. Dual Bottom Circular Quick-Action Buttons (Bell & Crescent Moon)
    val buttonSize = (sliderWidth - 6.dp).coerceAtLeast(28.dp)
    Box(
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(Color(0xFF4B3864).copy(alpha = 0.85f))
            .clickable { onMuteToggle() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isMuted) Icons.Default.NotificationsOff else Icons.Default.NotificationsActive,
            contentDescription = "Ringer Mode",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
    }

    Box(
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(Color(0xFF38254E).copy(alpha = 0.85f))
            .clickable { onSecondaryAction() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Bedtime,
            contentDescription = "DND Mode",
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(14.dp)
        )
    }
}

/**
 * ONEPLUS (OXYGENOS) PRESET:
 * Sleek vertical slider with a bold filled track, smooth corners, and compact audio-mode controls.
 */
@Composable
private fun OnePlusSlider(
    progress: Float,
    isMuted: Boolean,
    accentColor: Color,
    sliderWidth: Dp,
    sliderHeight: Dp,
    cornerRadius: Dp,
    backgroundAlpha: Float,
    streamIcon: ImageVector,
    onVolumeChange: (Float) -> Unit,
    onMuteToggle: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    val trackShape = RoundedCornerShape(cornerRadius)
    val containerBg = Color(0xFF402E54).copy(alpha = backgroundAlpha.coerceIn(0.2f, 0.95f))
    val activeFillColor by animateColorAsState(
        targetValue = if (isMuted) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f) else Color.White,
        animationSpec = tween(150),
        label = "onePlusActiveFill"
    )

    // 1. Bold Capsule Track
    BoxWithConstraints(
        modifier = Modifier
            .width(sliderWidth)
            .height(sliderHeight)
            .clip(trackShape)
            .background(containerBg)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                    onVolumeChange(pct)
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val pct = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        val pct = (1f - (change.position.y / size.height.toFloat())).coerceIn(0f, 1f)
                        onVolumeChange(pct)
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        val totalHeight = maxHeight

        // Bold white fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight * progress)
                .clip(trackShape)
                .background(activeFillColor)
        )

        // Bottom volume icon
        val iconTint = if (progress > 0.22f && !isMuted) {
            Color(0xFF332044)
        } else if (isMuted) {
            MaterialTheme.colorScheme.error
        } else {
            Color.White.copy(alpha = 0.85f)
        }

        Box(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .size(24.dp)
                .clickable { onMuteToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = streamIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    // 2. Dual Bottom Circular Buttons (More ... & Sound Mode / Vibration)
    val buttonSize = (sliderWidth - 6.dp).coerceAtLeast(28.dp)
    Box(
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(Color(0xFFF5EEF8))
            .clickable { onSecondaryAction() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "More Options",
            tint = Color(0xFF3B2850),
            modifier = Modifier.size(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(Color(0xFFF5EEF8))
            .clickable { onMuteToggle() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.Default.Vibration,
            contentDescription = "Alert Slider",
            tint = Color(0xFF3B2850),
            modifier = Modifier.size(15.dp)
        )
    }
}

/**
 * Returns sleek, recognizable icons for each stream type and mute state.
 */
fun getStreamIcon(streamType: AudioStreamType, isMuted: Boolean): ImageVector {
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

