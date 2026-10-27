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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration

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
    onNavigateToDiagnostics: () -> Unit,
    onNavigateToProfiles: () -> Unit = {},
    onNavigateToAutomation: () -> Unit = {},
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

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
