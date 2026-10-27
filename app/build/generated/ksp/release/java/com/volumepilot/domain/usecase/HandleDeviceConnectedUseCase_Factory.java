package com.volumepilot.domain.usecase;

import com.volumepilot.domain.repository.AutomationRepository;
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
public final class HandleDeviceConnectedUseCase_Factory implements Factory<HandleDeviceConnectedUseCase> {
  private final Provider<AutomationRepository> automationRepositoryProvider;

  public HandleDeviceConnectedUseCase_Factory(
      Provider<AutomationRepository> automationRepositoryProvider) {
    this.automationRepositoryProvider = automationRepositoryProvider;
  }

  @Override
  public HandleDeviceConnectedUseCase get() {
    return newInstance(automationRepositoryProvider.get());
  }

  public static HandleDeviceConnectedUseCase_Factory create(
      Provider<AutomationRepository> automationRepositoryProvider) {
    return new HandleDeviceConnectedUseCase_Factory(automationRepositoryProvider);
  }

  public static HandleDeviceConnectedUseCase newInstance(
      AutomationRepository automationRepository) {
    return new HandleDeviceConnectedUseCase(automationRepository);
  }
}
