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
public final class EvaluateTimeAutomationUseCase_Factory implements Factory<EvaluateTimeAutomationUseCase> {
  private final Provider<AutomationRepository> automationRepositoryProvider;

  public EvaluateTimeAutomationUseCase_Factory(
      Provider<AutomationRepository> automationRepositoryProvider) {
    this.automationRepositoryProvider = automationRepositoryProvider;
  }

  @Override
  public EvaluateTimeAutomationUseCase get() {
    return newInstance(automationRepositoryProvider.get());
  }

  public static EvaluateTimeAutomationUseCase_Factory create(
      Provider<AutomationRepository> automationRepositoryProvider) {
    return new EvaluateTimeAutomationUseCase_Factory(automationRepositoryProvider);
  }

  public static EvaluateTimeAutomationUseCase newInstance(
      AutomationRepository automationRepository) {
    return new EvaluateTimeAutomationUseCase(automationRepository);
  }
}
