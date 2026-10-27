package com.volumepilot.domain.usecase;

import com.volumepilot.domain.repository.SettingsRepository;
import com.volumepilot.domain.repository.VolumeRepository;
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
public final class SetVolumeUseCase_Factory implements Factory<SetVolumeUseCase> {
  private final Provider<VolumeRepository> volumeRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<EnforceMaxVolumeCapUseCase> enforceMaxVolumeCapUseCaseProvider;

  public SetVolumeUseCase_Factory(Provider<VolumeRepository> volumeRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<EnforceMaxVolumeCapUseCase> enforceMaxVolumeCapUseCaseProvider) {
    this.volumeRepositoryProvider = volumeRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.enforceMaxVolumeCapUseCaseProvider = enforceMaxVolumeCapUseCaseProvider;
  }

  @Override
  public SetVolumeUseCase get() {
    return newInstance(volumeRepositoryProvider.get(), settingsRepositoryProvider.get(), enforceMaxVolumeCapUseCaseProvider.get());
  }

  public static SetVolumeUseCase_Factory create(Provider<VolumeRepository> volumeRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<EnforceMaxVolumeCapUseCase> enforceMaxVolumeCapUseCaseProvider) {
    return new SetVolumeUseCase_Factory(volumeRepositoryProvider, settingsRepositoryProvider, enforceMaxVolumeCapUseCaseProvider);
  }

  public static SetVolumeUseCase newInstance(VolumeRepository volumeRepository,
      SettingsRepository settingsRepository,
      EnforceMaxVolumeCapUseCase enforceMaxVolumeCapUseCase) {
    return new SetVolumeUseCase(volumeRepository, settingsRepository, enforceMaxVolumeCapUseCase);
  }
}
