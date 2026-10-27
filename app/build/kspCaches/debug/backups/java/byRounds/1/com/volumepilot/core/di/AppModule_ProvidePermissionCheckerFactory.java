package com.volumepilot.core.di;

import android.content.Context;
import com.volumepilot.core.permissions.PermissionChecker;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvidePermissionCheckerFactory implements Factory<PermissionChecker> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvidePermissionCheckerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PermissionChecker get() {
    return providePermissionChecker(contextProvider.get());
  }

  public static AppModule_ProvidePermissionCheckerFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvidePermissionCheckerFactory(contextProvider);
  }

  public static PermissionChecker providePermissionChecker(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePermissionChecker(context));
  }
}
