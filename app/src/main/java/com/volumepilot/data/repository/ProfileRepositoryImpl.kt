package com.volumepilot.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.volumepilot.core.common.AppLogger
import com.volumepilot.data.local.JsonAdapters
import com.volumepilot.data.local.PreferencesKeys
import com.volumepilot.domain.model.VolumeProfile
import com.volumepilot.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val logger: AppLogger
) : ProfileRepository {

    override fun getProfiles(): Flow<List<VolumeProfile>> = dataStore.data.map { preferences ->
        val json = preferences[PreferencesKeys.PROFILES_JSON]
        JsonAdapters.jsonStringToProfiles(json)
    }

    override fun getActiveProfile(): Flow<VolumeProfile> = combine(
        getProfiles(),
        dataStore.data.map { it[PreferencesKeys.ACTIVE_PROFILE_ID] ?: VolumeProfile.DEFAULT.id }
    ) { profiles, activeId ->
        profiles.find { it.id == activeId } ?: VolumeProfile.DEFAULT
    }

    override suspend fun saveProfile(profile: VolumeProfile): Result<Unit> {
        return try {
            val currentProfiles = getProfiles().first().toMutableList()
            val existingIndex = currentProfiles.indexOfFirst { it.id == profile.id }
            if (existingIndex >= 0) {
                currentProfiles[existingIndex] = profile
            } else {
                currentProfiles.add(profile)
            }

            val updatedJson = JsonAdapters.profilesToJsonString(currentProfiles)
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.PROFILES_JSON] = updatedJson
            }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e("ProfileRepository", "Failed to save profile", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(profileId: String): Result<Unit> {
        return try {
            val currentProfiles = getProfiles().first().toMutableList()
            val removed = currentProfiles.removeAll { it.id == profileId && it.isDeletable }
            if (removed) {
                val updatedJson = JsonAdapters.profilesToJsonString(currentProfiles)
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.PROFILES_JSON] = updatedJson
                    if (preferences[PreferencesKeys.ACTIVE_PROFILE_ID] == profileId) {
                        preferences[PreferencesKeys.ACTIVE_PROFILE_ID] = VolumeProfile.DEFAULT.id
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e("ProfileRepository", "Failed to delete profile", e)
            Result.failure(e)
        }
    }

    override suspend fun activateProfile(profileId: String): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.ACTIVE_PROFILE_ID] = profileId
            }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e("ProfileRepository", "Failed to set active profile", e)
            Result.failure(e)
        }
    }
}
