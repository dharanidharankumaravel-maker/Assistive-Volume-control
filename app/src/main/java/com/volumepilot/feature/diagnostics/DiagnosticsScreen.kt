package com.volumepilot.feature.diagnostics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.volumepilot.core.common.LocalCrashRecorder
import kotlinx.coroutines.launch


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.volumepilot.core.battery.OemSettingsHelper
import com.volumepilot.core.designsystem.LocalSpacing
import com.volumepilot.core.designsystem.components.BannerType
import com.volumepilot.core.designsystem.components.SectionHeader
import com.volumepilot.core.designsystem.components.StatusBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    uiState: DiagnosticsUiState,
    onRepairOverlay: () -> Unit,
    onClearRepairMessage: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.repairMessage) {
        uiState.repairMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearRepairMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Diagnostics & Health") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            // Overall Health Status Banner
            item {
                val isHealthy = uiState.diagnosticState.isHealthy
                StatusBanner(
                    message = if (isHealthy) {
                        "All systems optimal. Overlay engine and permissions are operational."
                    } else {
                        "Attention required: one or more permissions or services need repair."
                    },
                    type = if (isHealthy) BannerType.SUCCESS else BannerType.WARNING,
                    icon = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning
                )
            }

            // Diagnostic Metrics
            item {
                SectionHeader(title = "Component Status")
                Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                    DiagnosticRow(
                        title = "Overlay Permission (SYSTEM_ALERT_WINDOW)",
                        status = if (uiState.diagnosticState.isOverlayPermissionGranted) "Active" else "Missing",
                        isOk = uiState.diagnosticState.isOverlayPermissionGranted
                    )
                    DiagnosticRow(
                        title = "Audio Controls (MODIFY_AUDIO_SETTINGS)",
                        status = if (uiState.diagnosticState.isModifyAudioGranted) "Active" else "Denied",
                        isOk = uiState.diagnosticState.isModifyAudioGranted
                    )
                    DiagnosticRow(
                        title = "Foreground Notification",
                        status = if (uiState.diagnosticState.isPostNotificationGranted) "Enabled" else "Disabled",
                        isOk = uiState.diagnosticState.isPostNotificationGranted
                    )
                    DiagnosticRow(
                        title = "Battery Optimization",
                        status = if (uiState.diagnosticState.isBatteryOptimizationIgnored) "Unrestricted" else "Optimized (May sleep)",
                        isOk = uiState.diagnosticState.isBatteryOptimizationIgnored
                    )
                    DiagnosticRow(
                        title = "Reboot Restoration",
                        status = if (uiState.diagnosticState.isStartOnBootEnabled) "Enabled" else "Off",
                        isOk = true
                    )
                    DiagnosticRow(
                        title = "Overlay Service State",
                        status = if (uiState.diagnosticState.isOverlayServiceRunning) "Running (Active)" else "Stopped",
                        isOk = uiState.diagnosticState.isOverlayServiceRunning
                    )
                }
            }

            // Quick Actions: Repair & Manufacturer Settings
            item {
                SectionHeader(title = "Recovery & Management")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Button(
                        onClick = onRepairOverlay,
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isRepairing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.padding(end = spacing.extraSmall)
                        )
                        Text(if (uiState.isRepairing) "Repairing..." else "Repair Overlay")
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = OemSettingsHelper.createOemSettingsIntent(context)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.padding(end = spacing.extraSmall)
                        )
                        Text("Battery Settings")
                    }
                }
            }

            // Device Telemetry (Local-only)
            item {
                SectionHeader(title = "Device Context (Local-only)")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        Text(
                            text = "Manufacturer: ${uiState.diagnosticState.deviceManufacturer.uppercase()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Android API Level: ${uiState.diagnosticState.androidVersion}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "OEM Profile: ${uiState.oemGuidance.manufacturer.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Local Crash & Incident Diagnostics
            item {
                SectionHeader(title = "Crash & Incident Diagnostics (100% Offline)")
                LocalCrashReportCard(context = context, snackbarHostState = snackbarHostState)
            }

            item {
                Spacer(modifier = Modifier.height(spacing.large))
            }
        }
    }
}

@Composable
private fun LocalCrashReportCard(
    context: Context,
    snackbarHostState: SnackbarHostState
) {
    val spacing = LocalSpacing.current
    var crashLog by remember { mutableStateOf(LocalCrashRecorder.getCrashLogs(context)) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(spacing.medium)) {
            if (crashLog == null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Zero unhandled crashes recorded. All systems operating normally.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Text(
                    text = "A local crash was recorded on this device:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(spacing.extraSmall))
                Text(
                    text = crashLog ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 6
                )
                Spacer(modifier = Modifier.height(spacing.medium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("VolumePilot Crash Report", crashLog))
                            scope.launch {
                                snackbarHostState.showSnackbar("Crash report copied to clipboard.")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.padding(start = spacing.extraSmall))
                        Text("Copy")
                    }

                    Button(
                        onClick = {
                            LocalCrashRecorder.clearCrashLogs(context)
                            crashLog = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Local crash log cleared.")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.padding(start = spacing.extraSmall))
                        Text("Clear")
                    }
                }
            }
        }
    }
}


@Composable
private fun DiagnosticRow(
    title: String,
    status: String,
    isOk: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
            ) {
                Icon(
                    imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (isOk) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOk) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
