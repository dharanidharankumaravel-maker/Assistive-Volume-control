package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.VolumeProfile
import com.volumepilot.domain.repository.ProfileRepository
import com.volumepilot.domain.repository.SettingsRepository
import com.volumepilot.domain.repository.VolumeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ActivateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val volumeRepository: VolumeRepository,
    private val setVolumeUseCase: SetVolumeUseCase,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(profileId: String): Result<Unit> {
        val profiles = profileRepository.getProfiles().first()
        val profile = profiles.find { it.id == profileId }
            ?: return Result.failure(IllegalArgumentException("Profile with ID '$profileId' not found"))

        return activate(profile)
    }

    suspend fun activate(profile: VolumeProfile): Result<Unit> {
        // Set ringer mode
        volumeRepository.setRingerMode(profile.ringerMode)

        // Set each stream volume using SetVolumeUseCase to enforce safety limits
        for ((streamType, targetLevel) in profile.streamLevels) {
            setVolumeUseCase(streamType, targetLevel)
        }

        // Apply overlay visibility preference
        settingsRepository.updateOverlayEnabled(profile.isOverlayVisible)

        // Record active profile ID
        profileRepository.activateProfile(profile.id)

        return Result.success(Unit)
    }
}
