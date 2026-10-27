package com.volumepilot.service.automation;

import com.volumepilot.core.common.AppLogger;
import com.volumepilot.domain.usecase.EvaluateTimeAutomationUseCase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TimeAutomationReceiver_MembersInjector implements MembersInjector<TimeAutomationReceiver> {
  private final Provider<EvaluateTimeAutomationUseCase> evaluateTimeAutomationUseCaseProvider;

  private final Provider<AppLogger> loggerProvider;

  public TimeAutomationReceiver_MembersInjector(
      Provider<EvaluateTimeAutomationUseCase> evaluateTimeAutomationUseCaseProvider,
      Provider<AppLogger> loggerProvider) {
    this.evaluateTimeAutomationUseCaseProvider = evaluateTimeAutomationUseCaseProvider;
    this.loggerProvider = loggerProvider;
  }

  public static MembersInjector<TimeAutomationReceiver> create(
      Provider<EvaluateTimeAutomationUseCase> evaluateTimeAutomationUseCaseProvider,
      Provider<AppLogger> loggerProvider) {
    return new TimeAutomationReceiver_MembersInjector(evaluateTimeAutomationUseCaseProvider, loggerProvider);
  }

  @Override
  public void injectMembers(TimeAutomationReceiver instance) {
    injectEvaluateTimeAutomationUseCase(instance, evaluateTimeAutomationUseCaseProvider.get());
    injectLogger(instance, loggerProvider.get());
  }

  @InjectedFieldSignature("com.volumepilot.service.automation.TimeAutomationReceiver.evaluateTimeAutomationUseCase")
  public static void injectEvaluateTimeAutomationUseCase(TimeAutomationReceiver instance,
      EvaluateTimeAutomationUseCase evaluateTimeAutomationUseCase) {
    instance.evaluateTimeAutomationUseCase = evaluateTimeAutomationUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.automation.TimeAutomationReceiver.logger")
  public static void injectLogger(TimeAutomationReceiver instance, AppLogger logger) {
    instance.logger = logger;
  }
}
