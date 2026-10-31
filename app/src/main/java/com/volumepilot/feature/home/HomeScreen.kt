package com.volumepilot.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Headphones

import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import com.volumepilot.core.designsystem.rgbGlow
import com.volumepilot.domain.model.FloatingButtonArrangement
import com.volumepilot.domain.model.FloatingButtonColor
import com.volumepilot.domain.model.FloatingButtonType
import com.volumepilot.domain.model.PowerLongPressAction
import com.volumepilot.domain.model.PowerSingleTapAction
import com.volumepilot.service.accessibility.VolumeAccessibilityService
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.volumepilot.R
import com.volumepilot.core.designsystem.LocalSpacing
import com.volumepilot.core.designsystem.components.BannerType
import com.volumepilot.core.designsystem.components.ErrorState
import com.volumepilot.core.designsystem.components.SectionHeader
import com.volumepilot.core.designsystem.components.StatusBanner
import com.volumepilot.core.designsystem.components.ToggleRow
import com.volumepilot.core.designsystem.components.VolumeSlider
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumePreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onVolumeChange: (AudioStreamType, Int) -> Unit,
    onMuteToggle: (AudioStreamType) -> Unit,
    onApplyPreset: (VolumePreset) -> Unit,
    onToggleVibrate: () -> Unit,
    onToggleMaxVolumeLimiter: (Boolean) -> Unit,
    onToggleOverlay: (Boolean) -> Unit,
    onToggleStartOnBoot: (Boolean) -> Unit,
    onUpdateWidgetArrangement: (com.volumepilot.domain.model.FloatingButtonArrangement) -> Unit = {},
    onUpdateWidgetColor: (com.volumepilot.domain.model.FloatingButtonColor) -> Unit = {},
    onUpdatePowerSingleTap: (com.volumepilot.domain.model.PowerSingleTapAction) -> Unit = {},
    onUpdatePowerLongPress: (com.volumepilot.domain.model.PowerLongPressAction) -> Unit = {},
    onUpdateOverlayAppearance: (Float, Int) -> Unit = { _, _ -> },
    onUpdateRgbGlow: (Boolean, Float) -> Unit = { _, _ -> },
    onRefreshPermissions: () -> Unit = {},
    onNavigateToDiagnostics: () -> Unit,
    onNavigateToProfiles: () -> Unit = {},
    onNavigateToAutomation: () -> Unit = {},
    onNavigateToSliderStyles: () -> Unit = {},
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Automatically synchronize permission states when returning from Android Settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onRefreshPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { err ->
            snackbarHostState.showSnackbar(err.userMessage)
            onClearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSliderStyles) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Slider Styles",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onNavigateToProfiles) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Volume Profiles",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToAutomation) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = "Automation and Safety",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToDiagnostics) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = "Diagnostics and Health",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },

        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                // Privacy Guarantee Banner
                item {
                    StatusBanner(
                        message = stringResource(R.string.privacy_banner_desc),
                        icon = Icons.Default.Shield,
                        type = BannerType.INFO
                    )
                }

                // Floating Overlay Control Section
                item {
                    SectionHeader(title = "Floating Assistant")
                    if (!uiState.isOverlayPermissionGranted) {
                        com.volumepilot.core.designsystem.components.PermissionCard(
                            title = stringResource(R.string.overlay_permission_title),
                            description = stringResource(R.string.overlay_permission_desc),
                            isGranted = false,
                            onGrantClick = {
                                val intent = android.content.Intent(
                                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    android.net.Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            }
                        )
                    } else {
                        ToggleRow(
                            title = stringResource(R.string.overlay_toggle_title),
                            description = stringResource(R.string.overlay_toggle_desc),
                            checked = uiState.settings.isOverlayEnabled,
                            onCheckedChange = onToggleOverlay
                        )

                        if (uiState.settings.isOverlayEnabled) {
                            Spacer(modifier = Modifier.height(spacing.small))
                            FloatingWidgetCustomizationCard(
                                settings = uiState.settings,
                                accessibilityStatus = uiState.accessibilityStatus,
                                onUpdateArrangement = onUpdateWidgetArrangement,
                                onUpdateColor = onUpdateWidgetColor,
                                onUpdatePowerSingleTap = onUpdatePowerSingleTap,
                                onUpdatePowerLongPress = onUpdatePowerLongPress,
                                onUpdateAppearance = onUpdateOverlayAppearance,
                                onUpdateRgbGlow = onUpdateRgbGlow,
                                onOpenAccessibility = {
                                    VolumeAccessibilityService.openAccessibilitySettings(context)
                                }
                            )
                        }
                    }
                }

                // Volume Slider Styles Showcase Entry Card
                item {
                    SectionHeader(title = "Volume Slider Styles")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSliderStyles() },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF58406E).copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.medium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = Color(uiState.settings.sliderAccentColor),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Slider Styles & Themes",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Active: ${uiState.settings.sliderStyle.displayName} • Google, Apple, Samsung, Xiaomi, OnePlus",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Button(
                                onClick = onNavigateToSliderStyles,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(uiState.settings.sliderAccentColor)
                                )
                            ) {
                                Text("Customize", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Active Audio Output Device Card
                item {
                    AudioOutputDeviceCard(device = uiState.volumeState.activeOutputDevice)
                }

                // Do Not Disturb Warning if active
                if (uiState.volumeState.isDndActive) {
                    item {
                        StatusBanner(
                            message = stringResource(R.string.warning_dnd_active),
                            icon = Icons.Default.NotificationsOff,
                            type = BannerType.WARNING
                        )
                    }
                }

                // Presets Quick Actions
                item {
                    SectionHeader(title = "Quick Presets")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        PresetChip(
                            title = stringResource(R.string.preset_silent),
                            onClick = { onApplyPreset(VolumePreset.Silent) },
                            modifier = Modifier.weight(1f)
                        )
                        PresetChip(
                            title = stringResource(R.string.preset_vibrate),
                            onClick = { onApplyPreset(VolumePreset.Vibrate) },
                            modifier = Modifier.weight(1f)
                        )
                        PresetChip(
                            title = stringResource(R.string.preset_normal),
                            onClick = { onApplyPreset(VolumePreset.Normal) },
                            modifier = Modifier.weight(1f)
                        )
                        PresetChip(
                            title = stringResource(R.string.preset_loud),
                            onClick = { onApplyPreset(VolumePreset.Loud) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Stream Volume Sliders
                item {
                    SectionHeader(title = "Volume Levels")
                }

                val streams = uiState.volumeState.streams.values.toList()
                items(streams, key = { it.streamType.name }) { stream ->
                    val isCapped = uiState.settings.maxVolumeLimiterEnabled &&
                            (stream.streamType == AudioStreamType.MEDIA || stream.streamType == AudioStreamType.BLUETOOTH)

                    val maxCapLevel = if (isCapped) {
                        val range = stream.maxVolume - stream.minVolume
                        stream.minVolume + (range * (uiState.settings.maxVolumeCapPercent / 100f)).toInt()
                    } else null

                    VolumeSlider(
                        streamName = stream.streamType.displayName,
                        currentVolume = stream.currentVolume,
                        maxVolume = stream.maxVolume,
                        minVolume = stream.minVolume,
                        isMuted = stream.isMuted,
                        isCapped = isCapped,
                        maxVolumeCap = maxCapLevel,
                        onVolumeChange = { level ->
                            onVolumeChange(stream.streamType, level)
                        },
                        onMuteToggle = {
                            onMuteToggle(stream.streamType)
                        }
                    )
                }

                // Safe Listening Options & System Preferences
                item {
                    SectionHeader(title = stringResource(R.string.section_audio))
                    ToggleRow(
                        title = stringResource(R.string.setting_max_volume_cap_title),
                        description = stringResource(R.string.setting_max_volume_cap_desc),
                        checked = uiState.settings.maxVolumeLimiterEnabled,
                        onCheckedChange = onToggleMaxVolumeLimiter
                    )
                    ToggleRow(
                        title = "Start on Boot",
                        description = "Automatically restores the floating volume button when the device turns on.",
                        checked = uiState.settings.startOnBoot,
                        onCheckedChange = onToggleStartOnBoot
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(spacing.large))
                }
            }
        }
    }
}

@Composable
private fun AudioOutputDeviceCard(
    device: AudioOutputDevice,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    val icon = when (device) {
        AudioOutputDevice.SPEAKER -> Icons.Default.Speaker
        AudioOutputDevice.WIRED_HEADPHONES,
        AudioOutputDevice.BLUETOOTH,
        AudioOutputDevice.USB_AUDIO -> Icons.Default.Headphones
        AudioOutputDevice.UNKNOWN -> Icons.Default.Speaker
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = "Active Audio Output",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = device.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = spacing.extraSmall)
            )
        },
        modifier = modifier.semantics {
            contentDescription = "Activate $title preset"
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloatingWidgetCustomizationCard(
    settings: com.volumepilot.domain.model.UserSettings,
    accessibilityStatus: VolumeAccessibilityService.AccessibilityServiceStatus,
    onUpdateArrangement: (FloatingButtonArrangement) -> Unit,
    onUpdateColor: (FloatingButtonColor) -> Unit,
    onUpdatePowerSingleTap: (PowerSingleTapAction) -> Unit,
    onUpdatePowerLongPress: (PowerLongPressAction) -> Unit,
    onUpdateAppearance: (Float, Int) -> Unit,
    onUpdateRgbGlow: (Boolean, Float) -> Unit,
    onOpenAccessibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val accentColor = Color(settings.widgetColor.primaryColor)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Floating Controls Setup",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // 1. Accessibility Service Status Component
            AccessibilityStatusCard(
                status = accessibilityStatus,
                onOpenAccessibility = onOpenAccessibility
            )

            // 2. Live Floating Controls Preview
            FloatingControlsLivePreview(
                settings = settings
            )

            // 3. Button Configuration & Ordering UI (Clean 6-option visual selection cards)
            Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                Text(
                    text = "Button Combination",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose the floating buttons to display on your screen edge",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ButtonConfigurationSelectionGrid(
                selectedArrangement = settings.buttonArrangement,
                accentColor = accentColor,
                onSelectArrangement = onUpdateArrangement
            )

            // 2. Widget Color Accent
            Text(
                text = "Widget Accent Color",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingButtonColor.values().forEach { colorTheme ->
                    val isSelected = settings.widgetColor == colorTheme
                    val color = Color(colorTheme.primaryColor)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { onUpdateColor(colorTheme) }
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Power Actions (Only displayed if Power button is enabled in the chosen arrangement)
            if (settings.buttonArrangement.hasPower) {
                // 3. Power Button Single Tap Action
                Text(
                    text = "Power Single Tap",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    PowerSingleTapAction.values().forEach { action ->
                        FilterChip(
                            selected = settings.powerSingleTapAction == action,
                            onClick = { onUpdatePowerSingleTap(action) },
                            label = { Text(action.displayName, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // 4. Power Button Long Press Action
                Text(
                    text = "Power Long Press",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    PowerLongPressAction.values().forEach { action ->
                        FilterChip(
                            selected = settings.powerLongPressAction == action,
                            onClick = { onUpdatePowerLongPress(action) },
                            label = { Text(action.displayName, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // 5. Button Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Widget Size",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${settings.overlaySizeDp} dp",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = settings.overlaySizeDp.toFloat(),
                onValueChange = { newSize ->
                    onUpdateAppearance(settings.overlayOpacity, newSize.toInt())
                },
                valueRange = 38f..56f,
                steps = 8
            )

            // 6. Opacity Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transparency",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(settings.overlayOpacity * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = settings.overlayOpacity,
                onValueChange = { newOpacity ->
                    onUpdateAppearance(newOpacity, settings.overlaySizeDp)
                },
                valueRange = 0.3f..1.0f
            )

            // 7. RGB Glowing Aura Effect
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RGB Glowing Aura",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Animated neon glow around widget and panel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                androidx.compose.material3.Switch(
                    checked = settings.rgbGlowEnabled,
                    onCheckedChange = { enabled ->
                        onUpdateRgbGlow(enabled, settings.rgbGlowIntensity)
                    }
                )
            }

            if (settings.rgbGlowEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Glow Intensity",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(settings.rgbGlowIntensity * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = settings.rgbGlowIntensity,
                    onValueChange = { newIntensity ->
                        onUpdateRgbGlow(settings.rgbGlowEnabled, newIntensity)
                    },
                    valueRange = 0.2f..1.0f
                )
            }
        }
    }
}

@Composable
private fun AccessibilityStatusCard(
    status: VolumeAccessibilityService.AccessibilityServiceStatus,
    onOpenAccessibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    when (status) {
        VolumeAccessibilityService.AccessibilityServiceStatus.ENABLED -> {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Accessibility active",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Accessibility Enabled",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Active • Lock Screen, Power Menu & Screenshot shortcuts ready.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        VolumeAccessibilityService.AccessibilityServiceStatus.CONNECTING -> {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Connecting to Service...",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Permission enabled in Settings. Initializing accessibility link...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        VolumeAccessibilityService.AccessibilityServiceStatus.DISABLED -> {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
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
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Accessibility Disabled",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Required for software Power, Lock Screen, and Screenshot actions without hardware buttons.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onOpenAccessibility,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Enable Accessibility", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingControlsLivePreview(
    settings: com.volumepilot.domain.model.UserSettings,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val accentColor = Color(settings.widgetColor.primaryColor)
    val buttons = settings.buttonArrangement.getButtons()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Live Floating Controls Preview",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            text = if (settings.buttonArrangement.isVolumeOnly) "Compact Bar" else "Full Panel",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = accentColor.copy(alpha = 0.12f),
                        disabledContainerColor = accentColor.copy(alpha = 0.12f),
                        disabledLabelColor = accentColor
                    ),
                    border = null
                )
            }

            // Preview Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF161A22))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Render circular buttons in vertical arrangement exactly as shown on the screen edge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    buttons.forEach { buttonType ->
                        val icon = when (buttonType) {
                            FloatingButtonType.POWER -> Icons.Default.PowerSettingsNew
                            FloatingButtonType.VOLUME_UP -> Icons.Default.Add
                            FloatingButtonType.VOLUME_DOWN -> Icons.Default.Remove
                            FloatingButtonType.TOGGLE_CONTROL -> Icons.Default.Tune
                        }
                        val iconTint = when (buttonType) {
                            FloatingButtonType.POWER -> MaterialTheme.colorScheme.error.copy(alpha = 0.95f)
                            else -> accentColor
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .rgbGlow(
                                    shape = CircleShape,
                                    enabled = settings.rgbGlowEnabled,
                                    intensity = settings.rgbGlowIntensity,
                                    strokeWidth = 1.3.dp,
                                    durationMillis = settings.rgbSpeedMillis
                                )
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = settings.overlayOpacity))
                                .border(
                                    width = if (settings.rgbGlowEnabled) 0.5.dp else 1.2.dp,
                                    color = if (settings.rgbGlowEnabled) Color.Transparent else accentColor.copy(alpha = 0.7f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = when {
                    settings.buttonArrangement.isVolumeOnly ->
                        "• Compact Mode: Touching controls directly slides out the slim single volume bar."
                    settings.buttonArrangement.hasToggle ->
                        "• Panel Mode: Dedicated sliders button toggles the expanded multi-stream volume panel."
                    settings.buttonArrangement == FloatingButtonArrangement.POWER_ONLY ->
                        "• Standalone circular power button for screen lock and screenshot gestures."
                    else ->
                        "• Floating buttons provide direct volume adjustment; power button performs system actions."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ButtonConfigurationSelectionGrid(
    selectedArrangement: FloatingButtonArrangement,
    accentColor: Color,
    onSelectArrangement: (FloatingButtonArrangement) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val configurations = FloatingButtonArrangement.PRIMARY_CONFIGURATIONS

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        configurations.chunked(2).forEach { rowConfigs ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                rowConfigs.forEach { config ->
                    val isSelected = selectedArrangement == config ||
                            (selectedArrangement.getButtons() == config.getButtons())
                    ButtonConfigurationCard(
                        arrangement = config,
                        isSelected = isSelected,
                        accentColor = accentColor,
                        onClick = { onSelectArrangement(config) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowConfigs.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ButtonConfigurationCard(
    arrangement: FloatingButtonArrangement,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttons = arrangement.getButtons()
    val title = when (arrangement) {
        FloatingButtonArrangement.POWER_ONLY -> "Power Only"
        FloatingButtonArrangement.UP_DOWN -> "Vol+ & Vol-"
        FloatingButtonArrangement.POWER_TOGGLE -> "Power + Panel"
        FloatingButtonArrangement.POWER_UP_DOWN -> "Power + Volume"
        FloatingButtonArrangement.TOGGLE_ONLY -> "Panel Only"
        FloatingButtonArrangement.POWER_TOGGLE_UP_DOWN -> "All Controls"
        else -> arrangement.displayName
    }

    val subtitle = when (arrangement) {
        FloatingButtonArrangement.POWER_ONLY -> "Key fallback"
        FloatingButtonArrangement.UP_DOWN -> "Compact slider"
        FloatingButtonArrangement.POWER_TOGGLE -> "Power & panel"
        FloatingButtonArrangement.POWER_UP_DOWN -> "Power & volume"
        FloatingButtonArrangement.TOGGLE_ONLY -> "Volume panel"
        FloatingButtonArrangement.POWER_TOGGLE_UP_DOWN -> "Full suite"
        else -> "Custom"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top: Row of mini circular button icons + Check indicator if selected
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    buttons.forEach { buttonType ->
                        val icon = when (buttonType) {
                            FloatingButtonType.POWER -> Icons.Default.PowerSettingsNew
                            FloatingButtonType.VOLUME_UP -> Icons.Default.Add
                            FloatingButtonType.VOLUME_DOWN -> Icons.Default.Remove
                            FloatingButtonType.TOGGLE_CONTROL -> Icons.Default.Tune
                        }
                        val tint = when (buttonType) {
                            FloatingButtonType.POWER -> MaterialTheme.colorScheme.error
                            else -> if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(tint.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

