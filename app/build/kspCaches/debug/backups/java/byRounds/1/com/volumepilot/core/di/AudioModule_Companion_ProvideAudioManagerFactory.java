package com.volumepilot.core.di;

import android.content.Context;
import android.media.AudioManager;
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
public final class AudioModule_Companion_ProvideAudioManagerFactory implements Factory<AudioManager> {
  private final Provider<Context> contextProvider;

  public AudioModule_Companion_ProvideAudioManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AudioManager get() {
    return provideAudioManager(contextProvider.get());
  }

  public static AudioModule_Companion_ProvideAudioManagerFactory create(
      Provider<Context> contextProvider) {
    return new AudioModule_Companion_ProvideAudioManagerFactory(contextProvider);
  }

  public static AudioManager provideAudioManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AudioModule.Companion.provideAudioManager(context));
  }
}
