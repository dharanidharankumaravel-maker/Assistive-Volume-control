package com.volumepilot.fakes

import com.volumepilot.domain.model.VolumeProfile
import com.volumepilot.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProfileRepository(
    initialProfiles: List<VolumeProfile> = VolumeProfile.BUILT_IN_PROFILES,
    initialActiveId: String = VolumeProfile.DEFAULT.id
) : ProfileRepository {

    private val profilesFlow = MutableStateFlow(initialProfiles)
    private val activeIdFlow = MutableStateFlow(initialActiveId)

    override fun getProfiles(): Flow<List<VolumeProfile>> = profilesFlow.asStateFlow()

    override fun getActiveProfile(): Flow<VolumeProfile> = activeIdFlow.map { activeId ->
        profilesFlow.value.find { it.id == activeId } ?: VolumeProfile.DEFAULT
    }

    override suspend fun saveProfile(profile: VolumeProfile): Result<Unit> {
        val current = profilesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == profile.id }
        if (index >= 0) {
            current[index] = profile
        } else {
            current.add(profile)
        }
        profilesFlow.value = current
        return Result.success(Unit)
    }

    override suspend fun deleteProfile(profileId: String): Result<Unit> {
        val current = profilesFlow.value.toMutableList()
        current.removeAll { it.id == profileId && it.isDeletable }
        profilesFlow.value = current
        if (activeIdFlow.value == profileId) {
            activeIdFlow.value = VolumeProfile.DEFAULT.id
        }
        return Result.success(Unit)
    }

    override suspend fun activateProfile(profileId: String): Result<Unit> {
        activeIdFlow.value = profileId
        return Result.success(Unit)
    }
}
