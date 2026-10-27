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
public final class GetAutomationConfigUseCase_Factory implements Factory<GetAutomationConfigUseCase> {
  private final Provider<AutomationRepository> automationRepositoryProvider;

  public GetAutomationConfigUseCase_Factory(
      Provider<AutomationRepository> automationRepositoryProvider) {
    this.automationRepositoryProvider = automationRepositoryProvider;
  }

  @Override
  public GetAutomationConfigUseCase get() {
    return newInstance(automationRepositoryProvider.get());
  }

  public static GetAutomationConfigUseCase_Factory create(
      Provider<AutomationRepository> automationRepositoryProvider) {
    return new GetAutomationConfigUseCase_Factory(automationRepositoryProvider);
  }

  public static GetAutomationConfigUseCase newInstance(AutomationRepository automationRepository) {
    return new GetAutomationConfigUseCase(automationRepository);
  }
}
