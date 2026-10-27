package com.volumepilot.core.di;

import android.content.Context;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvidePreferencesDataStoreFactory implements Factory<DataStore<Preferences>> {
  private final Provider<Context> contextProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public AppModule_ProvidePreferencesDataStoreFactory(Provider<Context> contextProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.contextProvider = contextProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public DataStore<Preferences> get() {
    return providePreferencesDataStore(contextProvider.get(), ioDispatcherProvider.get());
  }

  public static AppModule_ProvidePreferencesDataStoreFactory create(
      Provider<Context> contextProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new AppModule_ProvidePreferencesDataStoreFactory(contextProvider, ioDispatcherProvider);
  }

  public static DataStore<Preferences> providePreferencesDataStore(Context context,
      CoroutineDispatcher ioDispatcher) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePreferencesDataStore(context, ioDispatcher));
  }
}
