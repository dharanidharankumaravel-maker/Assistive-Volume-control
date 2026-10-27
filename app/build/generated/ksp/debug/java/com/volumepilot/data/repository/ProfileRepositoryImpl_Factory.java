package com.volumepilot.data.repository;

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
public final class ProfileRepositoryImpl_Factory implements Factory<ProfileRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<AppLogger> loggerProvider;

  public ProfileRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<AppLogger> loggerProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public ProfileRepositoryImpl get() {
    return newInstance(dataStoreProvider.get(), loggerProvider.get());
  }

  public static ProfileRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider, Provider<AppLogger> loggerProvider) {
    return new ProfileRepositoryImpl_Factory(dataStoreProvider, loggerProvider);
  }

  public static ProfileRepositoryImpl newInstance(DataStore<Preferences> dataStore,
      AppLogger logger) {
    return new ProfileRepositoryImpl(dataStore, logger);
  }
}
