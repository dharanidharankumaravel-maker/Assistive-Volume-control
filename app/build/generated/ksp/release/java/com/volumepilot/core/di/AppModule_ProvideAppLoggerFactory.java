package com.volumepilot.core.di;

import com.volumepilot.core.common.AppLogger;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideAppLoggerFactory implements Factory<AppLogger> {
  @Override
  public AppLogger get() {
    return provideAppLogger();
  }

  public static AppModule_ProvideAppLoggerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AppLogger provideAppLogger() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAppLogger());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideAppLoggerFactory INSTANCE = new AppModule_ProvideAppLoggerFactory();
  }
}
