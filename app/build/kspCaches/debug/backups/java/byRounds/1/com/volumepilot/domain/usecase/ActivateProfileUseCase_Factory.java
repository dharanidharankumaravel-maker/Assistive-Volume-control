package com.volumepilot.domain.usecase;

import com.volumepilot.domain.repository.ProfileRepository;
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
public final class ActivateProfileUseCase_Factory implements Factory<ActivateProfileUseCase> {
  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<VolumeRepository> volumeRepositoryProvider;

  private final Provider<SetVolumeUseCase> setVolumeUseCaseProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public ActivateProfileUseCase_Factory(Provider<ProfileRepository> profileRepositoryProvider,
      Provider<VolumeRepository> volumeRepositoryProvider,
      Provider<SetVolumeUseCase> setVolumeUseCaseProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.volumeRepositoryProvider = volumeRepositoryProvider;
    this.setVolumeUseCaseProvider = setVolumeUseCaseProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public ActivateProfileUseCase get() {
    return newInstance(profileRepositoryProvider.get(), volumeRepositoryProvider.get(), setVolumeUseCaseProvider.get(), settingsRepositoryProvider.get());
  }

  public static ActivateProfileUseCase_Factory create(
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<VolumeRepository> volumeRepositoryProvider,
      Provider<SetVolumeUseCase> setVolumeUseCaseProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new ActivateProfileUseCase_Factory(profileRepositoryProvider, volumeRepositoryProvider, setVolumeUseCaseProvider, settingsRepositoryProvider);
  }

  public static ActivateProfileUseCase newInstance(ProfileRepository profileRepository,
      VolumeRepository volumeRepository, SetVolumeUseCase setVolumeUseCase,
      SettingsRepository settingsRepository) {
    return new ActivateProfileUseCase(profileRepository, volumeRepository, setVolumeUseCase, settingsRepository);
  }
}
