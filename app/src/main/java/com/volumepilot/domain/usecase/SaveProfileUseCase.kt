package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.VolumeProfile
import com.volumepilot.domain.repository.ProfileRepository
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: VolumeProfile): Result<Unit> {
        if (profile.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Profile name cannot be empty"))
        }
        return profileRepository.saveProfile(profile)
    }
}
