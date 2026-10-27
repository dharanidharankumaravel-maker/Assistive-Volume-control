package com.volumepilot.feature.profiles

import com.volumepilot.domain.model.VolumeProfile

data class ProfilesUiState(
    val profiles: List<VolumeProfile> = emptyList(),
    val activeProfileId: String = VolumeProfile.DEFAULT.id,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditingProfile: Boolean = false,
    val profileToEdit: VolumeProfile? = null
)
