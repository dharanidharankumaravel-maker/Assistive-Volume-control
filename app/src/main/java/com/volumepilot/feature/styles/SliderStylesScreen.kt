package com.volumepilot.feature.styles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volumepilot.core.designsystem.LocalSpacing
import com.volumepilot.core.designsystem.components.SectionHeader
import com.volumepilot.core.designsystem.rgbGlow
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.SliderStyle
import com.volumepilot.domain.model.VolumeStream
import com.volumepilot.feature.overlay.slider.StylizedVolumeSlider

// Curated accent color palette for sliders
private val AccentColorPalette = listOf(
    0xFF7C3AEDL to "Royal Violet",
    0xFF00F5D4L to "Neon Cyan",
    0xFF00875AL to "Basil Emerald",
    0xFF006495L to "Ocean Blue",
    0xFFD97706L to "Sunset Amber",
    0xFFF72585L to "Vivid Pink",
    0xFF22323FL to "Slate Dark"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderStylesScreen(
    uiState: SliderStylesUiState,
    onSelectStyle: (SliderStyle) -> Unit,
    onPreviewVolumeChange: (SliderStyle, Float) -> Unit,
    onUpdateAccentColor: (Long) -> Unit,
    onUpdateWidth: (Int) -> Unit,
    onUpdateHeight: (Int) -> Unit,
    onUpdateCornerRadius: (Int) -> Unit,
    onUpdateBackgroundAlpha: (Float) -> Unit,
    onUpdateRgbSpeed: (Int) -> Unit,
    onUpdateRgbGlow: (Boolean, Float) -> Unit,
    onResetDefaults: () -> Unit,
    onApplyCustomization: () -> Unit,
    onClearSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isAppliedSuccess) {
        if (uiState.isAppliedSuccess) {
            snackbarHostState.showSnackbar("Slider style successfully applied to floating panel!")
            onClearSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Slider Styles",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onResetDefaults) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset to Defaults"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = spacing.medium, vertical = spacing.small)
            ) {
                Button(
                    onClick = onApplyCustomization,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(uiState.accentColor)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = "Apply ${uiState.selectedStyle.displayName} Style",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 70.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                // 1. PRIMARY SHOWCASE: Visual Preview Cards inspired by the reference image
                item {
                    StyleShowcaseSection(
                        selectedStyle = uiState.selectedStyle,
                        accentColor = Color(uiState.accentColor),
                        previewLevels = uiState.previewVolumeLevels,
                        widthDp = uiState.widthDp,
                        heightDp = uiState.heightDp,
                        cornerRadiusDp = uiState.cornerRadiusDp,
                        backgroundAlpha = uiState.backgroundAlpha,
                        onSelectStyle = onSelectStyle,
                        onPreviewVolumeChange = onPreviewVolumeChange
                    )
                }

                // 2. ADDITIONAL CUSTOMIZATION SECTION
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        SectionHeader(title = "Appearance & Geometry")

                        // Accent Color Palette
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.medium),
                                verticalArrangement = Arrangement.spacedBy(spacing.small)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ColorLens,
                                        contentDescription = null,
                                        tint = Color(uiState.accentColor),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Slider Accent Color",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                                ) {
                                    AccentColorPalette.forEach { (colorVal, name) ->
                                        val isSelected = uiState.accentColor == colorVal
                                        val color = Color(colorVal)

                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .clickable { onUpdateAccentColor(colorVal) }
                                                .border(
                                                    width = if (isSelected) 3.dp else 1.dp,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .semantics { contentDescription = name },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Dimensions & Proportions Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.medium),
                                verticalArrangement = Arrangement.spacedBy(spacing.medium)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = Color(uiState.accentColor),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Slider Thickness & Proportions",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }

                                // Thickness (Width) Slider
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Slider Width", style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${uiState.widthDp} dp",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(uiState.accentColor)
                                        )
                                    }
                                    Slider(
                                        value = uiState.widthDp.toFloat(),
                                        onValueChange = { onUpdateWidth(it.toInt()) },
                                        valueRange = 30f..50f,
                                        steps = 9
                                    )
                                }

                                // Height Slider
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Slider Height", style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${uiState.heightDp} dp",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(uiState.accentColor)
                                        )
                                    }
                                    Slider(
                                        value = uiState.heightDp.toFloat(),
                                        onValueChange = { onUpdateHeight(it.toInt()) },
                                        valueRange = 100f..160f,
                                        steps = 11
                                    )
                                }

                                // Corner Radius Slider
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Corner Radius", style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${uiState.cornerRadiusDp} dp",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(uiState.accentColor)
                                        )
                                    }
                                    Slider(
                                        value = uiState.cornerRadiusDp.toFloat(),
                                        onValueChange = { onUpdateCornerRadius(it.toInt()) },
                                        valueRange = 8f..28f,
                                        steps = 9
                                    )
                                }

                                // Background Transparency Slider
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Background Opacity", style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${(uiState.backgroundAlpha * 100).toInt()}%",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(uiState.accentColor)
                                        )
                                    }
                                    Slider(
                                        value = uiState.backgroundAlpha,
                                        onValueChange = onUpdateBackgroundAlpha,
                                        valueRange = 0.2f..1.0f
                                    )
                                }
                            }
                        }

                        // RGB Border Animation & Effects Card
                        SectionHeader(title = "RGB Lighting & Animation")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.medium),
                                verticalArrangement = Arrangement.spacedBy(spacing.medium)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("RGB Border Glow", style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            "Flowing perimeter neon illumination",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = uiState.rgbGlowEnabled,
                                        onCheckedChange = { onUpdateRgbGlow(it, uiState.rgbGlowIntensity) }
                                    )
                                }

                                if (uiState.rgbGlowEnabled) {
                                    // Intensity
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Glow Intensity", style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                "${(uiState.rgbGlowIntensity * 100).toInt()}%",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(uiState.accentColor)
                                            )
                                        }
                                        Slider(
                                            value = uiState.rgbGlowIntensity,
                                            onValueChange = { onUpdateRgbGlow(uiState.rgbGlowEnabled, it) },
                                            valueRange = 0.2f..1.0f
                                        )
                                    }

                                    // Speed Slider
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Speed,
                                                    contentDescription = null,
                                                    tint = Color(uiState.accentColor),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text("Animation Speed", style = MaterialTheme.typography.bodyMedium)
                                            }
                                            val speedLabel = when {
                                                uiState.rgbSpeedMillis <= 2000 -> "Fast (${(uiState.rgbSpeedMillis / 1000f)}s)"
                                                uiState.rgbSpeedMillis <= 3800 -> "Medium (${(uiState.rgbSpeedMillis / 1000f)}s)"
                                                else -> "Smooth (${(uiState.rgbSpeedMillis / 1000f)}s)"
                                            }
                                            Text(
                                                speedLabel,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(uiState.accentColor)
                                            )
                                        }
                                        Slider(
                                            value = uiState.rgbSpeedMillis.toFloat(),
                                            onValueChange = { onUpdateRgbSpeed(it.toInt()) },
                                            valueRange = 1500f..5500f,
                                            steps = 7
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Visual Showcase Section matching the reference image layout and aesthetics:
 * - Rendered on rich purple canvas (#553E6B).
 * - Displays all 5 presets side by side with style names above each preview.
 * - Entirely interactive: drag or tap any slider to see dynamic volume level changes.
 * - Highlights the currently selected preset with a sleek indicator badge and border.
 */
@Composable
private fun StyleShowcaseSection(
    selectedStyle: SliderStyle,
    accentColor: Color,
    previewLevels: Map<SliderStyle, Float>,
    widthDp: Int,
    heightDp: Int,
    cornerRadiusDp: Int,
    backgroundAlpha: Float,
    onSelectStyle: (SliderStyle) -> Unit,
    onPreviewVolumeChange: (SliderStyle, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val showcaseBg = Color(0xFF58406E) // Authentic rich violet tone from reference image

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = showcaseBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap any preset to select • Drag sliders to test live",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Horizontal scrollable or wrapped row displaying the 5 presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top
            ) {
                SliderStyle.values().forEach { style ->
                    val isSelected = style == selectedStyle
                    val progress = previewLevels[style] ?: 0.65f

                    // Dedicated Card for each preset
                    Column(
                        modifier = Modifier
                            .width((widthDp + 24).dp.coerceAtLeast(68.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) Color(0xFF6B5184) else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectStyle(style) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Style Name above each preview (Google, Apple, Samsung, Xiaomi, ONEPLUS)
                        Text(
                            text = style.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        // 2. Interactive stylized volume slider
                        val isPreviewMuted = progress <= 0.02f
                        val previewStream = VolumeStream(
                            streamType = AudioStreamType.MEDIA,
                            currentVolume = (progress * 15).toInt(),
                            minVolume = 0,
                            maxVolume = 15,
                            isMuted = isPreviewMuted
                        )

                        // If selected, use custom user width/height/radius; if unselected, use preset default proportions
                        val activeWidth = if (isSelected) widthDp else style.defaultWidthDp
                        val activeHeight = if (isSelected) heightDp else style.defaultHeightDp
                        val activeRadius = if (isSelected) cornerRadiusDp else style.defaultCornerRadiusDp

                        StylizedVolumeSlider(
                            style = style,
                            streamType = AudioStreamType.MEDIA,
                            stream = previewStream,
                            accentColor = accentColor,
                            sliderWidth = activeWidth.dp,
                            sliderHeight = activeHeight.dp,
                            cornerRadius = activeRadius.dp,
                            backgroundAlpha = backgroundAlpha,
                            onVolumeChange = { level ->
                                val pct = level / 15f
                                onPreviewVolumeChange(style, pct)
                            },
                            onMuteToggle = {
                                onPreviewVolumeChange(style, if (isPreviewMuted) 0.65f else 0f)
                            },
                            onSecondaryAction = {
                                onPreviewVolumeChange(style, if (isPreviewMuted) 0.65f else 0f)
                            },
                            onMoreOptions = {
                                onSelectStyle(style)
                            },
                            overrideProgress = progress
                        )

                        // 3. Selection indicator badge
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Selected",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF452D5A),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

