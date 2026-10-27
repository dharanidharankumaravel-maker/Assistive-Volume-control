package com.volumepilot.domain.usecase

import com.volumepilot.domain.repository.BackupRepository
import javax.inject.Inject

class ExportSettingsUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(): Result<String> {
        return backupRepository.exportBackupJson()
    }
}
