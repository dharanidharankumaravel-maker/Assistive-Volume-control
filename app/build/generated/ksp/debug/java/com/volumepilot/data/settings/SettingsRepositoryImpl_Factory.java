package com.volumepilot.data.settings;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.volumepilot.core.common.AppLogger;
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
public final class SettingsRepositoryImpl_Factory implements Factory<SettingsRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<AppLogger> loggerProvider;

  public SettingsRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<AppLogger> loggerProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public SettingsRepositoryImpl get() {
    return newInstance(dataStoreProvider.get(), loggerProvider.get());
  }

  public static SettingsRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider, Provider<AppLogger> loggerProvider) {
    return new SettingsRepositoryImpl_Factory(dataStoreProvider, loggerProvider);
  }

  public static SettingsRepositoryImpl newInstance(DataStore<Preferences> dataStore,
      AppLogger logger) {
    return new SettingsRepositoryImpl(dataStore, logger);
  }
}
