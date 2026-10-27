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
public final class MuteUseCase_Factory implements Factory<MuteUseCase> {
  private final Provider<VolumeRepository> volumeRepositoryProvider;

  public MuteUseCase_Factory(Provider<VolumeRepository> volumeRepositoryProvider) {
    this.volumeRepositoryProvider = volumeRepositoryProvider;
  }

  @Override
  public MuteUseCase get() {
    return newInstance(volumeRepositoryProvider.get());
  }

  public static MuteUseCase_Factory create(Provider<VolumeRepository> volumeRepositoryProvider) {
    return new MuteUseCase_Factory(volumeRepositoryProvider);
  }

  public static MuteUseCase newInstance(VolumeRepository volumeRepository) {
    return new MuteUseCase(volumeRepository);
  }
}
