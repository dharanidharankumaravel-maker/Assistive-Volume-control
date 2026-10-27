package com.volumepilot.domain.repository

interface BackupRepository {
    suspend fun exportBackupJson(): Result<String>
    suspend fun importBackupJson(jsonString: String): Result<Unit>
}
