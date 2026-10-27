package com.volumepilot.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class EnforceMaxVolumeCapUseCase_Factory implements Factory<EnforceMaxVolumeCapUseCase> {
  @Override
  public EnforceMaxVolumeCapUseCase get() {
    return newInstance();
  }

  public static EnforceMaxVolumeCapUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EnforceMaxVolumeCapUseCase newInstance() {
    return new EnforceMaxVolumeCapUseCase();
  }

  private static final class InstanceHolder {
    private static final EnforceMaxVolumeCapUseCase_Factory INSTANCE = new EnforceMaxVolumeCapUseCase_Factory();
  }
}
