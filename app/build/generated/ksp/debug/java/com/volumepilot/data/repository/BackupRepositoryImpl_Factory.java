package com.volumepilot.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.volumepilot.core.common.AppLogger;
import com.volumepilot.domain.repository.AutomationRepository;
import com.volumepilot.domain.repository.ProfileRepository;
import com.volumepilot.domain.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class BackupRepositoryImpl_Factory implements Factory<BackupRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<AutomationRepository> automationRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<AppLogger> loggerProvider;

  public BackupRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<AutomationRepository> automationRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<AppLogger> loggerProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.automationRepositoryProvider = automationRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public BackupRepositoryImpl get() {
    return newInstance(dataStoreProvider.get(), profileRepositoryProvider.get(), automationRepositoryProvider.get(), settingsRepositoryProvider.get(), loggerProvider.get());
  }

  public static BackupRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<AutomationRepository> automationRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<AppLogger> loggerProvider) {
    return new BackupRepositoryImpl_Factory(dataStoreProvider, profileRepositoryProvider, automationRepositoryProvider, settingsRepositoryProvider, loggerProvider);
  }

  public static BackupRepositoryImpl newInstance(DataStore<Preferences> dataStore,
      ProfileRepository profileRepository, AutomationRepository automationRepository,
      SettingsRepository settingsRepository, AppLogger logger) {
    return new BackupRepositoryImpl(dataStore, profileRepository, automationRepository, settingsRepository, logger);
  }
}
