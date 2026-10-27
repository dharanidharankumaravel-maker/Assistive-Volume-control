package com.volumepilot.domain.usecase;

import com.volumepilot.domain.repository.BackupRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ImportSettingsUseCase_Factory implements Factory<ImportSettingsUseCase> {
  private final Provider<BackupRepository> backupRepositoryProvider;

  public ImportSettingsUseCase_Factory(Provider<BackupRepository> backupRepositoryProvider) {
    this.backupRepositoryProvider = backupRepositoryProvider;
  }

  @Override
  public ImportSettingsUseCase get() {
    return newInstance(backupRepositoryProvider.get());
  }

  public static ImportSettingsUseCase_Factory create(
      Provider<BackupRepository> backupRepositoryProvider) {
    return new ImportSettingsUseCase_Factory(backupRepositoryProvider);
  }

  public static ImportSettingsUseCase newInstance(BackupRepository backupRepository) {
    return new ImportSettingsUseCase(backupRepository);
  }
}
