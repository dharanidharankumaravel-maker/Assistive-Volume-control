package com.volumepilot.service.overlay;

import com.volumepilot.core.common.AppLogger;
import com.volumepilot.domain.usecase.GetSettingsUseCase;
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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<GetSettingsUseCase> getSettingsUseCaseProvider;

  private final Provider<AppLogger> loggerProvider;

  public BootReceiver_MembersInjector(Provider<GetSettingsUseCase> getSettingsUseCaseProvider,
      Provider<AppLogger> loggerProvider) {
    this.getSettingsUseCaseProvider = getSettingsUseCaseProvider;
    this.loggerProvider = loggerProvider;
  }

  public static MembersInjector<BootReceiver> create(
      Provider<GetSettingsUseCase> getSettingsUseCaseProvider, Provider<AppLogger> loggerProvider) {
    return new BootReceiver_MembersInjector(getSettingsUseCaseProvider, loggerProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectGetSettingsUseCase(instance, getSettingsUseCaseProvider.get());
    injectLogger(instance, loggerProvider.get());
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.BootReceiver.getSettingsUseCase")
  public static void injectGetSettingsUseCase(BootReceiver instance,
      GetSettingsUseCase getSettingsUseCase) {
    instance.getSettingsUseCase = getSettingsUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.BootReceiver.logger")
  public static void injectLogger(BootReceiver instance, AppLogger logger) {
    instance.logger = logger;
  }
}
