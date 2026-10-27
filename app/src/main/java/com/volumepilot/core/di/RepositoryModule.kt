package com.volumepilot.core.di

import com.volumepilot.data.repository.VolumeRepositoryImpl
import com.volumepilot.data.settings.SettingsRepositoryImpl
import com.volumepilot.domain.repository.SettingsRepository
import com.volumepilot.domain.repository.VolumeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVolumeRepository(
        impl: VolumeRepositoryImpl
    ): VolumeRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindDiagnosticRepository(
        impl: com.volumepilot.data.repository.DiagnosticRepositoryImpl
    ): com.volumepilot.domain.repository.DiagnosticRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: com.volumepilot.data.repository.ProfileRepositoryImpl
    ): com.volumepilot.domain.repository.ProfileRepository

    @Binds
    @Singleton
    abstract fun bindAutomationRepository(
        impl: com.volumepilot.data.repository.AutomationRepositoryImpl
    ): com.volumepilot.domain.repository.AutomationRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        impl: com.volumepilot.data.repository.BackupRepositoryImpl
    ): com.volumepilot.domain.repository.BackupRepository
}

