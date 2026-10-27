package com.volumepilot;

import com.volumepilot.core.common.AppLogger;
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
public final class VolumePilotApp_MembersInjector implements MembersInjector<VolumePilotApp> {
  private final Provider<AppLogger> loggerProvider;

  public VolumePilotApp_MembersInjector(Provider<AppLogger> loggerProvider) {
    this.loggerProvider = loggerProvider;
  }

  public static MembersInjector<VolumePilotApp> create(Provider<AppLogger> loggerProvider) {
    return new VolumePilotApp_MembersInjector(loggerProvider);
  }

  @Override
  public void injectMembers(VolumePilotApp instance) {
    injectLogger(instance, loggerProvider.get());
  }

  @InjectedFieldSignature("com.volumepilot.VolumePilotApp.logger")
  public static void injectLogger(VolumePilotApp instance, AppLogger logger) {
    instance.logger = logger;
  }
}
