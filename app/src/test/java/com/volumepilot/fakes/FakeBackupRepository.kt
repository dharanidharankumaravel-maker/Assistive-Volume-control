package com.volumepilot.fakes

import com.volumepilot.domain.repository.BackupRepository

class FakeBackupRepository : BackupRepository {

    var exportedString = "{\"version\":1,\"profilesJson\":\"[]\"}"
    var lastImportedString: String? = null
    var shouldFail = false

    override suspend fun exportBackupJson(): Result<String> {
        return if (shouldFail) {
            Result.failure(RuntimeException("Export error"))
        } else {
            Result.success(exportedString)
        }
    }

    override suspend fun importBackupJson(jsonString: String): Result<Unit> {
        lastImportedString = jsonString
        return if (shouldFail) {
            Result.failure(RuntimeException("Import error"))
        } else {
            Result.success(Unit)
        }
    }
}
