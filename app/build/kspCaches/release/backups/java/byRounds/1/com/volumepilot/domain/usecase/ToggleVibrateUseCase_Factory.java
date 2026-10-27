package com.volumepilot.domain.usecase;

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
public final class ToggleVibrateUseCase_Factory implements Factory<ToggleVibrateUseCase> {
  private final Provider<VolumeRepository> volumeRepositoryProvider;

  public ToggleVibrateUseCase_Factory(Provider<VolumeRepository> volumeRepositoryProvider) {
    this.volumeRepositoryProvider = volumeRepositoryProvider;
  }

  @Override
  public ToggleVibrateUseCase get() {
    return newInstance(volumeRepositoryProvider.get());
  }

  public static ToggleVibrateUseCase_Factory create(
      Provider<VolumeRepository> volumeRepositoryProvider) {
    return new ToggleVibrateUseCase_Factory(volumeRepositoryProvider);
  }

  public static ToggleVibrateUseCase newInstance(VolumeRepository volumeRepository) {
    return new ToggleVibrateUseCase(volumeRepository);
  }
}
