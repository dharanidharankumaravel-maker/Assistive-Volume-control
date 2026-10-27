package com.volumepilot.feature.profiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Work

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.volumepilot.core.designsystem.LocalSpacing
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumeProfile
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    uiState: ProfilesUiState,
    onActivateProfile: (String) -> Unit,
    onSaveProfile: (VolumeProfile) -> Unit,
    onDeleteProfile: (String) -> Unit,
    onStartEditProfile: (VolumeProfile?) -> Unit,
    onDismissEditDialog: () -> Unit,
    onClearError: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Volume Profiles") },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onStartEditProfile(null) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.semantics { contentDescription = "Create new volume profile" }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
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
                item {
                    Text(
                        text = "Switch sound setups instantly or customize custom volume profiles.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(uiState.profiles, key = { it.id }) { profile ->
                    val isActive = profile.id == uiState.activeProfileId
                    ProfileCard(
                        profile = profile,
                        isActive = isActive,
                        onActivate = { onActivateProfile(profile.id) },
                        onEdit = { onStartEditProfile(profile) },
                        onDelete = { onDeleteProfile(profile.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        if (uiState.isEditingProfile) {
            EditProfileDialog(
                initialProfile = uiState.profileToEdit,
                onDismiss = onDismissEditDialog,
                onSave = onSaveProfile
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileCard(
    profile: VolumeProfile,
    isActive: Boolean,
    onActivate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalSpacing.current
    val containerColor = if (isActive) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onActivate)
            .semantics { contentDescription = "Profile ${profile.name}. ${if (isActive) "Active" else "Inactive"}" },
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(spacing.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getProfileIcon(profile.iconName),
                    contentDescription = null,
                    tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(spacing.medium))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ringer: ${profile.ringerMode.name}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isActive) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Active", style = MaterialTheme.typography.labelSmall) },
                        icon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                } else {
                    Button(
                        onClick = onActivate,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Apply")
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${profile.name}",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (profile.isDeletable) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete ${profile.name}",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.small))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                profile.streamLevels.forEach { (stream, level) ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${stream.displayName}: $level", style = MaterialTheme.typography.bodySmall) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    initialProfile: VolumeProfile?,
    onDismiss: () -> Unit,
    onSave: (VolumeProfile) -> Unit
) {
    val spacing = LocalSpacing.current
    var name by remember { mutableStateOf(initialProfile?.name ?: "") }
    var ringerMode by remember { mutableStateOf(initialProfile?.ringerMode ?: RingerMode.NORMAL) }

    var mediaVol by remember {
        mutableFloatStateOf(initialProfile?.streamLevels?.get(AudioStreamType.MEDIA)?.toFloat() ?: 10f)
    }
    var ringVol by remember {
        mutableFloatStateOf(initialProfile?.streamLevels?.get(AudioStreamType.RING)?.toFloat() ?: 5f)
    }
    var notificationVol by remember {
        mutableFloatStateOf(initialProfile?.streamLevels?.get(AudioStreamType.NOTIFICATION)?.toFloat() ?: 5f)
    }
    var alarmVol by remember {
        mutableFloatStateOf(initialProfile?.streamLevels?.get(AudioStreamType.ALARM)?.toFloat() ?: 6f)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialProfile == null) "New Profile" else "Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Profile Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Ringer Mode", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    RingerMode.entries.forEach { mode ->
                        FilterChip(
                            selected = ringerMode == mode,
                            onClick = { ringerMode = mode },
                            label = { Text(mode.name) }
                        )
                    }
                }

                Text("Media Volume: ${mediaVol.toInt()}", style = MaterialTheme.typography.bodySmall)
                Slider(value = mediaVol, onValueChange = { mediaVol = it }, valueRange = 0f..25f)

                Text("Ring Volume: ${ringVol.toInt()}", style = MaterialTheme.typography.bodySmall)
                Slider(value = ringVol, onValueChange = { ringVol = it }, valueRange = 0f..15f)

                Text("Notification Volume: ${notificationVol.toInt()}", style = MaterialTheme.typography.bodySmall)
                Slider(value = notificationVol, onValueChange = { notificationVol = it }, valueRange = 0f..15f)

                Text("Alarm Volume: ${alarmVol.toInt()}", style = MaterialTheme.typography.bodySmall)
                Slider(value = alarmVol, onValueChange = { alarmVol = it }, valueRange = 0f..15f)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val streamMap = mapOf(
                        AudioStreamType.MEDIA to mediaVol.toInt(),
                        AudioStreamType.RING to ringVol.toInt(),
                        AudioStreamType.NOTIFICATION to notificationVol.toInt(),
                        AudioStreamType.ALARM to alarmVol.toInt()
                    )
                    val profile = initialProfile?.copy(
                        name = name.trim(),
                        ringerMode = ringerMode,
                        streamLevels = streamMap
                    ) ?: VolumeProfile(
                        id = UUID.randomUUID().toString(),
                        name = name.trim().ifBlank { "Custom Profile" },
                        iconName = "tune",
                        streamLevels = streamMap,
                        ringerMode = ringerMode,
                        isDeletable = true
                    )
                    onSave(profile)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getProfileIcon(iconName: String): ImageVector {
    return when (iconName) {
        "bedtime" -> Icons.Default.NightlightRound
        "work" -> Icons.Default.Work
        "directions_car" -> Icons.Default.Tune
        "meeting_room" -> Icons.Default.MeetingRoom
        "sports_esports" -> Icons.Default.SportsEsports
        "headphones" -> Icons.Default.Headphones
        else -> Icons.AutoMirrored.Filled.VolumeUp
    }

}
