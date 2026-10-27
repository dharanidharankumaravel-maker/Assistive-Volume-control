package com.volumepilot.domain.usecase;

import com.volumepilot.domain.repository.SettingsRepository;
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
public final class GetSettingsUseCase_Factory implements Factory<GetSettingsUseCase> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public GetSettingsUseCase_Factory(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public GetSettingsUseCase get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static GetSettingsUseCase_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new GetSettingsUseCase_Factory(settingsRepositoryProvider);
  }

  public static GetSettingsUseCase newInstance(SettingsRepository settingsRepository) {
    return new GetSettingsUseCase(settingsRepository);
  }
}
