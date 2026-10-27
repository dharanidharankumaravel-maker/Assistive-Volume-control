package com.volumepilot.data.repository;

import android.content.Context;
import com.volumepilot.core.common.AppLogger;
import com.volumepilot.core.permissions.PermissionChecker;
import com.volumepilot.domain.repository.SettingsRepository;
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
public final class DiagnosticRepositoryImpl_Factory implements Factory<DiagnosticRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<PermissionChecker> permissionCheckerProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<AppLogger> loggerProvider;

  public DiagnosticRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<PermissionChecker> permissionCheckerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<AppLogger> loggerProvider) {
    this.contextProvider = contextProvider;
    this.permissionCheckerProvider = permissionCheckerProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public DiagnosticRepositoryImpl get() {
    return newInstance(contextProvider.get(), permissionCheckerProvider.get(), settingsRepositoryProvider.get(), loggerProvider.get());
  }

  public static DiagnosticRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<PermissionChecker> permissionCheckerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<AppLogger> loggerProvider) {
    return new DiagnosticRepositoryImpl_Factory(contextProvider, permissionCheckerProvider, settingsRepositoryProvider, loggerProvider);
  }

  public static DiagnosticRepositoryImpl newInstance(Context context,
      PermissionChecker permissionChecker, SettingsRepository settingsRepository,
      AppLogger logger) {
    return new DiagnosticRepositoryImpl(context, permissionChecker, settingsRepository, logger);
  }
}
