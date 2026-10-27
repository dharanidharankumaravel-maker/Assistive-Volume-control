package com.volumepilot.domain.usecase

import com.volumepilot.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profileId: String): Result<Unit> {
        val profiles = profileRepository.getProfiles().first()
        val target = profiles.find { it.id == profileId }
            ?: return Result.failure(IllegalArgumentException("Profile not found"))

        if (!target.isDeletable) {
            return Result.failure(IllegalStateException("Built-in profiles cannot be deleted"))
        }

        return profileRepository.deleteProfile(profileId)
    }
}
