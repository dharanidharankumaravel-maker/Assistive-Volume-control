package com.volumepilot.domain.repository

import com.volumepilot.domain.model.VolumeProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfiles(): Flow<List<VolumeProfile>>
    fun getActiveProfile(): Flow<VolumeProfile>
    suspend fun saveProfile(profile: VolumeProfile): Result<Unit>
    suspend fun deleteProfile(profileId: String): Result<Unit>
    suspend fun activateProfile(profileId: String): Result<Unit>
}
