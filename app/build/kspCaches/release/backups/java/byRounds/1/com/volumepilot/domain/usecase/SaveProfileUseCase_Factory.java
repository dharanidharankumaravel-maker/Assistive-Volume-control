package com.volumepilot.domain.usecase;

import com.volumepilot.domain.repository.ProfileRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SaveProfileUseCase_Factory implements Factory<SaveProfileUseCase> {
  private final Provider<ProfileRepository> profileRepositoryProvider;

  public SaveProfileUseCase_Factory(Provider<ProfileRepository> profileRepositoryProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public SaveProfileUseCase get() {
    return newInstance(profileRepositoryProvider.get());
  }

  public static SaveProfileUseCase_Factory create(
      Provider<ProfileRepository> profileRepositoryProvider) {
    return new SaveProfileUseCase_Factory(profileRepositoryProvider);
  }

  public static SaveProfileUseCase newInstance(ProfileRepository profileRepository) {
    return new SaveProfileUseCase(profileRepository);
  }
}
