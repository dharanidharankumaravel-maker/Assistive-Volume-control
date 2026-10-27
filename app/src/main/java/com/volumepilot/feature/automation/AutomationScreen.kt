package com.volumepilot.feature.automation

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.volumepilot.core.designsystem.LocalSpacing
import com.volumepilot.core.designsystem.components.SectionHeader
import com.volumepilot.core.designsystem.components.ToggleRow
import com.volumepilot.domain.model.AudioDeviceRule
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.SafeListeningConfig
import com.volumepilot.domain.model.TimeRule
import com.volumepilot.domain.model.VolumeProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationScreen(
    uiState: AutomationUiState,
    onToggleMaster: (Boolean) -> Unit,
    onSaveDeviceRule: (AudioDeviceRule) -> Unit,
    onSaveTimeRule: (TimeRule) -> Unit,
    onDeleteTimeRule: (String) -> Unit,
    onUpdateSafeListening: (SafeListeningConfig) -> Unit,
    onExportSettings: () -> Unit,
    onImportSettings: (String) -> Unit,
    onClearFeedback: () -> Unit,
    onClearError: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearFeedback()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { err ->
            snackbarHostState.showSnackbar(err)
            onClearError()
        }
    }

    LaunchedEffect(uiState.exportedJson) {
        if (uiState.exportedJson != null) {
            showExportDialog = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Automation & Safety") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
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
                // Master automation toggle
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.config.isMasterAutomationEnabled)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        ToggleRow(
                            title = "Master Automation",
                            description = "Automatically trigger profiles when audio devices connect or scheduled time windows begin.",
                            checked = uiState.config.isMasterAutomationEnabled,
                            onCheckedChange = onToggleMaster
                        )

                    }
                }

                // Audio Device Rules
                item {
                    SectionHeader(title = "Device Connection Automation")
                    Text(
                        text = "Switch profiles automatically when specific audio output hardware connects.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(uiState.config.deviceRules, key = { it.id }) { rule ->
                    DeviceRuleCard(
                        rule = rule,
                        profiles = uiState.profiles,
                        isEnabled = uiState.config.isMasterAutomationEnabled,
                        onUpdate = onSaveDeviceRule
                    )
                }

                // Time Rules
                item {
                    Spacer(modifier = Modifier.height(spacing.small))
                    SectionHeader(title = "Scheduled Time Rules")
                    Text(
                        text = "Profiles applied automatically during designated hours of the day.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(uiState.config.timeRules, key = { it.id }) { rule ->
                    TimeRuleCard(
                        rule = rule,
                        profiles = uiState.profiles,
                        isEnabled = uiState.config.isMasterAutomationEnabled,
                        onToggle = { onSaveTimeRule(rule.copy(isEnabled = it)) },
                        onDelete = { onDeleteTimeRule(rule.id) }
                    )
                }

                // Safe Listening & Hearing Health
                item {
                    Spacer(modifier = Modifier.height(spacing.small))
                    SectionHeader(title = "Safe Listening & Hearing Protection")
                    SafeListeningCard(
                        config = uiState.config.safeListeningConfig,
                        onUpdate = onUpdateSafeListening
                    )
                }

                // Privacy-first local backup and restore
                item {
                    Spacer(modifier = Modifier.height(spacing.small))
                    SectionHeader(title = "Privacy & Local Data")
                    LocalBackupCard(
                        onExport = onExportSettings,
                        onImport = { showImportDialog = true }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        // Export dialog
        if (showExportDialog && uiState.exportedJson != null) {
            AlertDialog(
                onDismissRequest = {
                    showExportDialog = false
                    onClearFeedback()
                },
                title = { Text("Exported Settings JSON") },
                text = {
                    Column {
                        Text(
                            text = "All profiles and rules serialized locally with zero cloud upload.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                        OutlinedTextField(
                            value = uiState.exportedJson,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 8
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("VolumePilot Backup", uiState.exportedJson))
                            showExportDialog = false
                            onClearFeedback()
                        }
                    ) {
                        Text("Copy to Clipboard")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showExportDialog = false
                            onClearFeedback()
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }

        // Import dialog
        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Import Settings JSON") },
                text = {
                    Column {
                        Text(
                            text = "Paste a valid VolumePilot JSON export to restore your profiles and automation configuration.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                        OutlinedTextField(
                            value = importJsonText,
                            onValueChange = { importJsonText = it },
                            label = { Text("Backup JSON") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 8
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onImportSettings(importJsonText)
                            showImportDialog = false
                            importJsonText = ""
                        },
                        enabled = importJsonText.isNotBlank()
                    ) {
                        Text("Restore")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceRuleCard(
    rule: AudioDeviceRule,
    profiles: List<VolumeProfile>,
    isEnabled: Boolean,
    onUpdate: (AudioDeviceRule) -> Unit
) {
    val spacing = LocalSpacing.current
    var expanded by remember { mutableStateOf(false) }
    val selectedProfile = profiles.find { it.id == rule.profileId } ?: VolumeProfile.DEFAULT

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(spacing.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getDeviceIcon(rule.deviceType),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(spacing.medium))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rule.deviceType.name.replace("_", " "),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Activates: ${selectedProfile.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = { onUpdate(rule.copy(isEnabled = it)) },
                    enabled = isEnabled,
                    modifier = Modifier.semantics {
                        contentDescription = "Toggle ${rule.deviceType.name} rule"
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedProfile.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Profile to Activate") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    profiles.forEach { profile ->
                        DropdownMenuItem(
                            text = { Text(profile.name) },
                            onClick = {
                                onUpdate(rule.copy(profileId = profile.id))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeRuleCard(
    rule: TimeRule,
    profiles: List<VolumeProfile>,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalSpacing.current
    val selectedProfile = profiles.find { it.id == rule.profileId } ?: VolumeProfile.DEFAULT
    val timeRangeStr = String.format("%02d:%02d - %02d:%02d", rule.startHour, rule.startMinute, rule.endHour, rule.endMinute)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = rule.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$timeRangeStr -> ${selectedProfile.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = rule.isEnabled,
                onCheckedChange = onToggle,
                enabled = isEnabled,
                modifier = Modifier.semantics { contentDescription = "Toggle ${rule.name}" }
            )

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete ${rule.name}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SafeListeningCard(
    config: SafeListeningConfig,
    onUpdate: (SafeListeningConfig) -> Unit
) {
    val spacing = LocalSpacing.current
    var capPercent by remember(config.maxVolumeCapPercent) {
        mutableFloatStateOf(config.maxVolumeCapPercent.toFloat())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(spacing.medium)) {
            ToggleRow(
                title = "Max Volume Limiter",
                description = "Prevent media or Bluetooth audio from exceeding a healthy decibel ceiling.",
                checked = config.isMaxVolumeCapEnabled,
                onCheckedChange = { onUpdate(config.copy(isMaxVolumeCapEnabled = it)) }
            )

            if (config.isMaxVolumeCapEnabled) {
                Spacer(modifier = Modifier.height(spacing.small))
                Text(
                    text = "Maximum Volume Ceiling: ${capPercent.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = capPercent,
                    onValueChange = { capPercent = it },
                    onValueChangeFinished = {
                        onUpdate(config.copy(maxVolumeCapPercent = capPercent.toInt()))
                    },
                    valueRange = 50f..100f,
                    modifier = Modifier.semantics {
                        contentDescription = "Maximum volume cap slider: ${capPercent.toInt()}%"
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            ToggleRow(
                title = "Safe Listening Warnings",
                description = "Alert user when volume approaches unsafe prolonged exposure levels.",
                checked = config.isSafeListeningWarningEnabled,
                onCheckedChange = { onUpdate(config.copy(isSafeListeningWarningEnabled = it)) }
            )
        }
    }
}

@Composable
private fun LocalBackupCard(
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(spacing.medium)) {
            Text(
                text = "Backup & Restore (100% Offline)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "VolumePilot operates completely offline without network permissions. You can export and import your full profile and automation setup as plain text JSON.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Button(
                    onClick = onExport,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text("Export")
                }

                OutlinedButton(
                    onClick = onImport,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text("Import")
                }
            }
        }
    }
}

private fun getDeviceIcon(device: AudioOutputDevice): ImageVector {
    return when (device) {
        AudioOutputDevice.BLUETOOTH -> Icons.Default.Bluetooth
        AudioOutputDevice.WIRED_HEADPHONES -> Icons.Default.Headphones
        AudioOutputDevice.USB_AUDIO -> Icons.Default.Usb
        AudioOutputDevice.SPEAKER -> Icons.Default.Speaker
        else -> Icons.Default.Speaker
    }
}

