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
public final class GetProfilesUseCase_Factory implements Factory<GetProfilesUseCase> {
  private final Provider<ProfileRepository> profileRepositoryProvider;

  public GetProfilesUseCase_Factory(Provider<ProfileRepository> profileRepositoryProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public GetProfilesUseCase get() {
    return newInstance(profileRepositoryProvider.get());
  }

  public static GetProfilesUseCase_Factory create(
      Provider<ProfileRepository> profileRepositoryProvider) {
    return new GetProfilesUseCase_Factory(profileRepositoryProvider);
  }

  public static GetProfilesUseCase newInstance(ProfileRepository profileRepository) {
    return new GetProfilesUseCase(profileRepository);
  }
}
