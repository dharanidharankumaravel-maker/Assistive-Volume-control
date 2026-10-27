package com.volumepilot.domain.usecase

import com.volumepilot.domain.repository.BackupRepository
import javax.inject.Inject

class ImportSettingsUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(jsonString: String): Result<Unit> {
        if (jsonString.isBlank()) {
            return Result.failure(IllegalArgumentException("Backup content cannot be empty"))
        }
        return backupRepository.importBackupJson(jsonString)
    }
}
