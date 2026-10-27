package com.volumepilot.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.volumepilot.core.common.AppLogger;
import com.volumepilot.domain.usecase.ActivateProfileUseCase;
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
public final class AutomationRepositoryImpl_Factory implements Factory<AutomationRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<ActivateProfileUseCase> activateProfileUseCaseProvider;

  private final Provider<AppLogger> loggerProvider;

  public AutomationRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<ActivateProfileUseCase> activateProfileUseCaseProvider,
      Provider<AppLogger> loggerProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.activateProfileUseCaseProvider = activateProfileUseCaseProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public AutomationRepositoryImpl get() {
    return newInstance(dataStoreProvider.get(), activateProfileUseCaseProvider, loggerProvider.get());
  }

  public static AutomationRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<ActivateProfileUseCase> activateProfileUseCaseProvider,
      Provider<AppLogger> loggerProvider) {
    return new AutomationRepositoryImpl_Factory(dataStoreProvider, activateProfileUseCaseProvider, loggerProvider);
  }

  public static AutomationRepositoryImpl newInstance(DataStore<Preferences> dataStore,
      Provider<ActivateProfileUseCase> activateProfileUseCaseProvider, AppLogger logger) {
    return new AutomationRepositoryImpl(dataStore, activateProfileUseCaseProvider, logger);
  }
}
