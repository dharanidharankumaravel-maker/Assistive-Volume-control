package com.volumepilot.core.permissions;

import android.content.Context;
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
public final class AndroidPermissionChecker_Factory implements Factory<AndroidPermissionChecker> {
  private final Provider<Context> contextProvider;

  public AndroidPermissionChecker_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AndroidPermissionChecker get() {
    return newInstance(contextProvider.get());
  }

  public static AndroidPermissionChecker_Factory create(Provider<Context> contextProvider) {
    return new AndroidPermissionChecker_Factory(contextProvider);
  }

  public static AndroidPermissionChecker newInstance(Context context) {
    return new AndroidPermissionChecker(context);
  }
}
