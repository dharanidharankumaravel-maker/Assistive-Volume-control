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
public final class ExportSettingsUseCase_Factory implements Factory<ExportSettingsUseCase> {
  private final Provider<BackupRepository> backupRepositoryProvider;

  public ExportSettingsUseCase_Factory(Provider<BackupRepository> backupRepositoryProvider) {
    this.backupRepositoryProvider = backupRepositoryProvider;
  }

  @Override
  public ExportSettingsUseCase get() {
    return newInstance(backupRepositoryProvider.get());
  }

  public static ExportSettingsUseCase_Factory create(
      Provider<BackupRepository> backupRepositoryProvider) {
    return new ExportSettingsUseCase_Factory(backupRepositoryProvider);
  }

  public static ExportSettingsUseCase newInstance(BackupRepository backupRepository) {
    return new ExportSettingsUseCase(backupRepository);
  }
}
