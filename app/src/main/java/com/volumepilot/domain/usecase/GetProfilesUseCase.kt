package com.volumepilot.domain.usecase

import com.volumepilot.domain.model.VolumeProfile
import com.volumepilot.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<List<VolumeProfile>> {
        return profileRepository.getProfiles()
    }

    fun getActiveProfile(): Flow<VolumeProfile> {
        return profileRepository.getActiveProfile()
    }
}
