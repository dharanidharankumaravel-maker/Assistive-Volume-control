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
public final class GetVolumeStateUseCase_Factory implements Factory<GetVolumeStateUseCase> {
  private final Provider<VolumeRepository> volumeRepositoryProvider;

  public GetVolumeStateUseCase_Factory(Provider<VolumeRepository> volumeRepositoryProvider) {
    this.volumeRepositoryProvider = volumeRepositoryProvider;
  }

  @Override
  public GetVolumeStateUseCase get() {
    return newInstance(volumeRepositoryProvider.get());
  }

  public static GetVolumeStateUseCase_Factory create(
      Provider<VolumeRepository> volumeRepositoryProvider) {
    return new GetVolumeStateUseCase_Factory(volumeRepositoryProvider);
  }

  public static GetVolumeStateUseCase newInstance(VolumeRepository volumeRepository) {
    return new GetVolumeStateUseCase(volumeRepository);
  }
}
