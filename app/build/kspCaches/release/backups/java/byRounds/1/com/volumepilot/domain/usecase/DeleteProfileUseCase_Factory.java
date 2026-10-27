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
public final class DeleteProfileUseCase_Factory implements Factory<DeleteProfileUseCase> {
  private final Provider<ProfileRepository> profileRepositoryProvider;

  public DeleteProfileUseCase_Factory(Provider<ProfileRepository> profileRepositoryProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public DeleteProfileUseCase get() {
    return newInstance(profileRepositoryProvider.get());
  }

  public static DeleteProfileUseCase_Factory create(
      Provider<ProfileRepository> profileRepositoryProvider) {
    return new DeleteProfileUseCase_Factory(profileRepositoryProvider);
  }

  public static DeleteProfileUseCase newInstance(ProfileRepository profileRepository) {
    return new DeleteProfileUseCase(profileRepository);
  }
}
