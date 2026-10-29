package com.volumepilot.feature.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.volumepilot.core.designsystem.rgbGlow
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.FloatingButtonColor
import com.volumepilot.domain.model.SliderStyle
import com.volumepilot.domain.model.VolumeStream
import com.volumepilot.feature.overlay.slider.StylizedVolumeSlider

fun calculateCompactWidthDp(sliderWidthDp: Int): Dp = (sliderWidthDp + 14).dp

val COMPACT_SLIDER_WIDTH_DP: Dp = 52.dp

/**
 * Compact, minimalist single volume slider bar.
 *
 * Rendered when only Volume Up and Volume Down buttons are selected (without Power).
 * Displays ONLY the active stream's volume slider in the user's chosen [SliderStyle],
 * without any toggle controls or extra clutter, positioned sleekly adjacent to the floating widget.
 */
@Composable
fun CompactVolumeSliderBar(
    stream: VolumeStream?,
    accentColor: Color,
    onVolumeChange: (Int) -> Unit,
    onMuteToggle: () -> Unit,
    onSecondaryAction: () -> Unit = {},
    modifier: Modifier = Modifier,
    opacity: Float = 0.92f,
    colorTheme: FloatingButtonColor = FloatingButtonColor.BASIL,
    rgbGlowEnabled: Boolean = true,
    rgbGlowIntensity: Float = 0.6f,
    sliderStyle: SliderStyle = SliderStyle.GOOGLE,
    sliderWidthDp: Int = 38,
    sliderHeightDp: Int = 130,
    sliderCornerRadiusDp: Int = 19,
    sliderBackgroundAlpha: Float = 0.5f,
    rgbSpeedMillis: Int = 3500
) {
    val sliderShape = RoundedCornerShape(22.dp)
    val cardWidth = calculateCompactWidthDp(sliderWidthDp)
    val streamType = stream?.streamType ?: AudioStreamType.MEDIA

    Card(
        modifier = modifier
            .width(cardWidth)
            .rgbGlow(
                shape = sliderShape,
                enabled = rgbGlowEnabled,
                intensity = rgbGlowIntensity,
                strokeWidth = 1.4.dp,
                durationMillis = rgbSpeedMillis
            )
            .shadow(
                elevation = 8.dp,
                shape = sliderShape,
                spotColor = accentColor.copy(alpha = 0.35f)
            )
            .semantics {
                role = Role.Button
                contentDescription = "Compact volume slider (${sliderStyle.displayName})"
            },
        shape = sliderShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = opacity)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StylizedVolumeSlider(
                style = sliderStyle,
                streamType = streamType,
                stream = stream,
                accentColor = accentColor,
                sliderWidth = sliderWidthDp.dp,
                sliderHeight = sliderHeightDp.dp,
                cornerRadius = sliderCornerRadiusDp.dp,
                backgroundAlpha = sliderBackgroundAlpha,
                onVolumeChange = onVolumeChange,
                onMuteToggle = onMuteToggle,
                onSecondaryAction = onSecondaryAction
            )
        }
    }
}
