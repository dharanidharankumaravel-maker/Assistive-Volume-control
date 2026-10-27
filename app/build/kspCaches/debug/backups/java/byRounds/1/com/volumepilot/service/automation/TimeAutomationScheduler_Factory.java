package com.volumepilot.service.automation;

import android.content.Context;
import com.volumepilot.core.common.AppLogger;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class TimeAutomationScheduler_Factory implements Factory<TimeAutomationScheduler> {
  private final Provider<Context> contextProvider;

  private final Provider<AppLogger> loggerProvider;

  public TimeAutomationScheduler_Factory(Provider<Context> contextProvider,
      Provider<AppLogger> loggerProvider) {
    this.contextProvider = contextProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public TimeAutomationScheduler get() {
    return newInstance(contextProvider.get(), loggerProvider.get());
  }

  public static TimeAutomationScheduler_Factory create(Provider<Context> contextProvider,
      Provider<AppLogger> loggerProvider) {
    return new TimeAutomationScheduler_Factory(contextProvider, loggerProvider);
  }

  public static TimeAutomationScheduler newInstance(Context context, AppLogger logger) {
    return new TimeAutomationScheduler(context, logger);
  }
}
