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
public final class ToggleMasterAutomationUseCase_Factory implements Factory<ToggleMasterAutomationUseCase> {
  private final Provider<AutomationRepository> automationRepositoryProvider;

  public ToggleMasterAutomationUseCase_Factory(
      Provider<AutomationRepository> automationRepositoryProvider) {
    this.automationRepositoryProvider = automationRepositoryProvider;
  }

  @Override
  public ToggleMasterAutomationUseCase get() {
    return newInstance(automationRepositoryProvider.get());
  }

  public static ToggleMasterAutomationUseCase_Factory create(
      Provider<AutomationRepository> automationRepositoryProvider) {
    return new ToggleMasterAutomationUseCase_Factory(automationRepositoryProvider);
  }

  public static ToggleMasterAutomationUseCase newInstance(
      AutomationRepository automationRepository) {
    return new ToggleMasterAutomationUseCase(automationRepository);
  }
}
