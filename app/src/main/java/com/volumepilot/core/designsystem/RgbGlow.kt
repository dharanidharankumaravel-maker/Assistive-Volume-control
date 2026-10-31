package com.volumepilot.core.designsystem

import android.graphics.Matrix
import android.graphics.SweepGradient
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Curated RGB neon palette for the ambient perimeter aura
val RgbNeonSpectrum = listOf(
    Color(0xFF00F5D4), // Neon Cyan
    Color(0xFF00BBF9), // Electric Azure
    Color(0xFF7209B7), // Deep Violet
    Color(0xFFF72585), // Vivid Neon Pink
    Color(0xFFFFB703), // Radiant Amber
    Color(0xFF70E000), // Basil Lime
    Color(0xFF00F5D4)  // Seamless loop
)

/**
 * Creates a ShaderBrush wrapping an Android SweepGradient with an animated rotation matrix.
 * This produces a continuous, perimeter-tracing flowing neon light effect around any Shape.
 */
class RotatingSweepGradientBrush(
    private val colors: IntArray,
    private val rotationAngle: Float,
    private val center: Offset
) : ShaderBrush() {
    override fun createShader(size: Size): android.graphics.Shader {
        val cx = center.x
        val cy = center.y
        val shader = SweepGradient(cx, cy, colors, null)
        val matrix = Matrix()
        matrix.postRotate(rotationAngle, cx, cy)
        shader.setLocalMatrix(matrix)
        return shader
    }
}

/**
 * Helper to draw an Outline with a Brush, handling Rectangle, RoundedRect, and Generic Path.
 */
private fun DrawScope.drawOutlineCompat(
    outline: Outline,
    brush: Brush,
    stroke: Stroke,
    alpha: Float = 1f
) {
    when (outline) {
        is Outline.Rectangle -> {
            drawRect(
                brush = brush,
                topLeft = Offset(outline.rect.left, outline.rect.top),
                size = Size(outline.rect.width, outline.rect.height),
                style = stroke,
                alpha = alpha
            )
        }
        is Outline.Rounded -> {
            val rr = outline.roundRect
            val tr = rr.topLeftCornerRadius
            val uniform = tr.x == rr.topRightCornerRadius.x &&
                    tr.x == rr.bottomLeftCornerRadius.x &&
                    tr.x == rr.bottomRightCornerRadius.x &&
                    tr.x == tr.y
            if (uniform) {
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(rr.left, rr.top),
                    size = Size(rr.width, rr.height),
                    cornerRadius = CornerRadius(tr.x, tr.y),
                    style = stroke,
                    alpha = alpha
                )
            } else {
                val path = androidx.compose.ui.graphics.Path().apply {
                    addRoundRect(rr)
                }
                drawPath(
                    path = path,
                    brush = brush,
                    style = stroke,
                    alpha = alpha
                )
            }
        }
        is Outline.Generic -> {
            drawPath(
                path = outline.path,
                brush = brush,
                style = stroke,
                alpha = alpha
            )
        }
    }
}

/**
 * Applies a smooth, continuously flowing animated RGB lighting effect along the borders
 * of components, following their exact shape and rounded corners.
 *
 * Highlights:
 * - Uses hardware-accelerated SweepGradient with matrix rotation for true perimeter color flow.
 * - Layered with a subtle outer ambient neon halo synchronized with the moving colors.
 * - Extremely battery-efficient: updates only during the GPU draw phase without recomposing child views.
 * - Dynamically adapts to user's enabled toggle and intensity customization.
 */
@Composable
fun Modifier.rgbGlow(
    shape: Shape,
    enabled: Boolean = true,
    intensity: Float = 0.6f,
    strokeWidth: Dp = 1.4.dp,
    durationMillis: Int = 3500
): Modifier {
    if (!enabled || intensity <= 0.05f) {
        return this
    }

    val safeDuration = durationMillis.coerceIn(1000, 10000)
    val infiniteTransition = rememberInfiniteTransition(label = "rgbPerimeterTransition")
    val animatedAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = safeDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rgbPerimeterAngle"
    )

    val colorInts = RgbNeonSpectrum.map { it.toArgb() }.toIntArray()

    return this.drawWithContent {
        val outline = shape.createOutline(size, layoutDirection, this)
        val center = Offset(size.width / 2f, size.height / 2f)

        val rotatingBrush = RotatingSweepGradientBrush(
            colors = colorInts,
            rotationAngle = animatedAngle,
            center = center
        )

        val mainStrokePx = strokeWidth.toPx()
        val glowStrokePx = mainStrokePx * 2.8f
        val glowAlpha = (intensity * 0.38f).coerceIn(0.08f, 0.65f)
        val borderAlpha = (intensity * 0.95f).coerceIn(0.2f, 1f)

        // 1. Draw subtle outer glowing aura following the moving RGB colors
        drawOutlineCompat(
            outline = outline,
            brush = rotatingBrush,
            stroke = Stroke(width = glowStrokePx),
            alpha = glowAlpha
        )

        // 2. Draw inner content (widget background, sliders, icons)
        drawContent()

        // 3. Draw crisp neon perimeter border following exact corners and shape
        drawOutlineCompat(
            outline = outline,
            brush = rotatingBrush,
            stroke = Stroke(width = mainStrokePx),
            alpha = borderAlpha
        )
    }
}
