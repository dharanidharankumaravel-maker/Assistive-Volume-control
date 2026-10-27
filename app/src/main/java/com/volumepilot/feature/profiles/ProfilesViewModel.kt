package com.volumepilot.feature.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volumepilot.domain.model.VolumeProfile
import com.volumepilot.domain.repository.ProfileRepository
import com.volumepilot.domain.usecase.ActivateProfileUseCase
import com.volumepilot.domain.usecase.DeleteProfileUseCase
import com.volumepilot.domain.usecase.GetProfilesUseCase
import com.volumepilot.domain.usecase.SaveProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val getProfilesUseCase: GetProfilesUseCase,
    private val profileRepository: ProfileRepository,
    private val activateProfileUseCase: ActivateProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfilesUiState(isLoading = true))
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getProfilesUseCase(),
                profileRepository.getActiveProfile()
            ) { profiles, activeProfile ->
                ProfilesUiState(
                    profiles = profiles,
                    activeProfileId = activeProfile.id,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.update { current ->
                    current.copy(
                        profiles = newState.profiles,
                        activeProfileId = newState.activeProfileId,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun activateProfile(profileId: String) {
        viewModelScope.launch {
            val result = activateProfileUseCase(profileId)
            if (result.isFailure) {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Failed to activate profile")
                }
            }
        }
    }

    fun saveProfile(profile: VolumeProfile) {
        viewModelScope.launch {
            val result = saveProfileUseCase(profile)
            if (result.isSuccess) {
                _uiState.update { it.copy(isEditingProfile = false, profileToEdit = null) }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Failed to save profile")
                }
            }
        }
    }

    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            val result = deleteProfileUseCase(profileId)
            if (result.isFailure) {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Failed to delete profile")
                }
            }
        }
    }

    fun onStartEditProfile(profile: VolumeProfile?) {
        _uiState.update {
            it.copy(isEditingProfile = true, profileToEdit = profile)
        }
    }

    fun onDismissEditDialog() {
        _uiState.update {
            it.copy(isEditingProfile = false, profileToEdit = null)
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
