package com.volumepilot.data.repository;

import com.volumepilot.service.volume.VolumeController;
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
public final class VolumeRepositoryImpl_Factory implements Factory<VolumeRepositoryImpl> {
  private final Provider<VolumeController> volumeControllerProvider;

  public VolumeRepositoryImpl_Factory(Provider<VolumeController> volumeControllerProvider) {
    this.volumeControllerProvider = volumeControllerProvider;
  }

  @Override
  public VolumeRepositoryImpl get() {
    return newInstance(volumeControllerProvider.get());
  }

  public static VolumeRepositoryImpl_Factory create(
      Provider<VolumeController> volumeControllerProvider) {
    return new VolumeRepositoryImpl_Factory(volumeControllerProvider);
  }

  public static VolumeRepositoryImpl newInstance(VolumeController volumeController) {
    return new VolumeRepositoryImpl(volumeController);
  }
}
