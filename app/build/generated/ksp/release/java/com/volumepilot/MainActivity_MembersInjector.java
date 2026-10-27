package com.volumepilot;

import com.volumepilot.domain.usecase.GetSettingsUseCase;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<GetSettingsUseCase> getSettingsUseCaseProvider;

  public MainActivity_MembersInjector(Provider<GetSettingsUseCase> getSettingsUseCaseProvider) {
    this.getSettingsUseCaseProvider = getSettingsUseCaseProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<GetSettingsUseCase> getSettingsUseCaseProvider) {
    return new MainActivity_MembersInjector(getSettingsUseCaseProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectGetSettingsUseCase(instance, getSettingsUseCaseProvider.get());
  }

  @InjectedFieldSignature("com.volumepilot.MainActivity.getSettingsUseCase")
  public static void injectGetSettingsUseCase(MainActivity instance,
      GetSettingsUseCase getSettingsUseCase) {
    instance.getSettingsUseCase = getSettingsUseCase;
  }
}
