package com.volumepilot.service.volume;

import android.content.Context;
import android.media.AudioManager;
import com.volumepilot.core.common.AppLogger;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "dagger.hilt.android.qualifiers.ApplicationContext",
    "javax.inject.Named"
})
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
public final class AndroidVolumeController_Factory implements Factory<AndroidVolumeController> {
  private final Provider<Context> contextProvider;

  private final Provider<AudioManager> audioManagerProvider;

  private final Provider<AppLogger> loggerProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public AndroidVolumeController_Factory(Provider<Context> contextProvider,
      Provider<AudioManager> audioManagerProvider, Provider<AppLogger> loggerProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.contextProvider = contextProvider;
    this.audioManagerProvider = audioManagerProvider;
    this.loggerProvider = loggerProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public AndroidVolumeController get() {
    return newInstance(contextProvider.get(), audioManagerProvider.get(), loggerProvider.get(), ioDispatcherProvider.get());
  }

  public static AndroidVolumeController_Factory create(Provider<Context> contextProvider,
      Provider<AudioManager> audioManagerProvider, Provider<AppLogger> loggerProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new AndroidVolumeController_Factory(contextProvider, audioManagerProvider, loggerProvider, ioDispatcherProvider);
  }

  public static AndroidVolumeController newInstance(Context context, AudioManager audioManager,
      AppLogger logger, CoroutineDispatcher ioDispatcher) {
    return new AndroidVolumeController(context, audioManager, logger, ioDispatcher);
  }
}
