package com.volumepilot.feature.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.volumepilot.core.designsystem.rgbGlow
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.FloatingButtonColor
import com.volumepilot.domain.model.SliderStyle
import com.volumepilot.domain.model.VolumeState
import com.volumepilot.feature.overlay.slider.StylizedVolumeSlider

fun calculatePanelWidthDp(sliderWidthDp: Int): Dp = ((sliderWidthDp * 4) + 56).dp

val PANEL_WIDTH_DP: Dp = 210.dp

/**
 * Ultra-minimalist, sleek floating volume panel.
 *
 * Features:
 * - Exactly four vertical sliders: Media, Ring, In-Call, and Alarm.
 * - Supports 5 distinct OS-inspired slider presets (Google, Apple, Samsung, Xiaomi, OnePlus).
 * - Icon-based controls with zero text labels for maximum minimalism.
 * - Horizontal capsule layout with consistent spacing and alignment.
 * - Smooth vertical touch and drag interaction with 1:1 real-time audio response.
 * - Continuously flowing animated RGB perimeter border following panel corners with configurable speed.
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
    rgbGlowIntensity: Float = 0.6f,
    sliderStyle: SliderStyle = SliderStyle.GOOGLE,
    sliderAccentColor: Long = 0xFF7C3AEDL,
    sliderWidthDp: Int = 38,
    sliderHeightDp: Int = 130,
    sliderCornerRadiusDp: Int = 19,
    sliderBackgroundAlpha: Float = 0.5f,
    rgbSpeedMillis: Int = 3500
) {
    val streams = volumeState.streams
    val accentColor = Color(sliderAccentColor)
    val panelShape = RoundedCornerShape(22.dp)
    val totalPanelWidth = calculatePanelWidthDp(sliderWidthDp)

    val targetStreams = listOf(
        AudioStreamType.MEDIA,
        AudioStreamType.RING,
        AudioStreamType.VOICE_CALL,
        AudioStreamType.ALARM
    )

    Card(
        modifier = modifier
            .width(totalPanelWidth)
            .rgbGlow(
                shape = panelShape,
                enabled = rgbGlowEnabled,
                intensity = rgbGlowIntensity,
                strokeWidth = 1.4.dp,
                durationMillis = rgbSpeedMillis
            )
            .shadow(
                elevation = 8.dp,
                shape = panelShape,
                spotColor = accentColor.copy(alpha = 0.35f)
            )
            .semantics { contentDescription = "Minimalist four-stream volume panel (${sliderStyle.displayName} style)" },
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
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            targetStreams.forEach { streamType ->
                val stream = streams[streamType]
                StylizedVolumeSlider(
                    style = sliderStyle,
                    streamType = streamType,
                    stream = stream,
                    accentColor = accentColor,
                    sliderWidth = sliderWidthDp.dp,
                    sliderHeight = sliderHeightDp.dp,
                    cornerRadius = sliderCornerRadiusDp.dp,
                    backgroundAlpha = sliderBackgroundAlpha,
                    onVolumeChange = { level -> onVolumeChange(streamType, level) },
                    onMuteToggle = { onMuteToggle(streamType) },
                    onSecondaryAction = onToggleVibrate
                )
            }
        }
    }
}
