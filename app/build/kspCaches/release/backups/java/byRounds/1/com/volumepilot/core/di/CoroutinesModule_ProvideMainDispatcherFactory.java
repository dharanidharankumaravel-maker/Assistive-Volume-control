package com.volumepilot.core.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class CoroutinesModule_ProvideMainDispatcherFactory implements Factory<CoroutineDispatcher> {
  @Override
  public CoroutineDispatcher get() {
    return provideMainDispatcher();
  }

  public static CoroutinesModule_ProvideMainDispatcherFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CoroutineDispatcher provideMainDispatcher() {
    return Preconditions.checkNotNullFromProvides(CoroutinesModule.INSTANCE.provideMainDispatcher());
  }

  private static final class InstanceHolder {
    private static final CoroutinesModule_ProvideMainDispatcherFactory INSTANCE = new CoroutinesModule_ProvideMainDispatcherFactory();
  }
}
