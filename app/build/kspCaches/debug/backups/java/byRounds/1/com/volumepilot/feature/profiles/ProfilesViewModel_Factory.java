package com.volumepilot.feature.profiles;

import com.volumepilot.domain.repository.ProfileRepository;
import com.volumepilot.domain.usecase.ActivateProfileUseCase;
import com.volumepilot.domain.usecase.DeleteProfileUseCase;
import com.volumepilot.domain.usecase.GetProfilesUseCase;
import com.volumepilot.domain.usecase.SaveProfileUseCase;
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
public final class ProfilesViewModel_Factory implements Factory<ProfilesViewModel> {
  private final Provider<GetProfilesUseCase> getProfilesUseCaseProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<ActivateProfileUseCase> activateProfileUseCaseProvider;

  private final Provider<SaveProfileUseCase> saveProfileUseCaseProvider;

  private final Provider<DeleteProfileUseCase> deleteProfileUseCaseProvider;

  public ProfilesViewModel_Factory(Provider<GetProfilesUseCase> getProfilesUseCaseProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<ActivateProfileUseCase> activateProfileUseCaseProvider,
      Provider<SaveProfileUseCase> saveProfileUseCaseProvider,
      Provider<DeleteProfileUseCase> deleteProfileUseCaseProvider) {
    this.getProfilesUseCaseProvider = getProfilesUseCaseProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.activateProfileUseCaseProvider = activateProfileUseCaseProvider;
    this.saveProfileUseCaseProvider = saveProfileUseCaseProvider;
    this.deleteProfileUseCaseProvider = deleteProfileUseCaseProvider;
  }

  @Override
  public ProfilesViewModel get() {
    return newInstance(getProfilesUseCaseProvider.get(), profileRepositoryProvider.get(), activateProfileUseCaseProvider.get(), saveProfileUseCaseProvider.get(), deleteProfileUseCaseProvider.get());
  }

  public static ProfilesViewModel_Factory create(
      Provider<GetProfilesUseCase> getProfilesUseCaseProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<ActivateProfileUseCase> activateProfileUseCaseProvider,
      Provider<SaveProfileUseCase> saveProfileUseCaseProvider,
      Provider<DeleteProfileUseCase> deleteProfileUseCaseProvider) {
    return new ProfilesViewModel_Factory(getProfilesUseCaseProvider, profileRepositoryProvider, activateProfileUseCaseProvider, saveProfileUseCaseProvider, deleteProfileUseCaseProvider);
  }

  public static ProfilesViewModel newInstance(GetProfilesUseCase getProfilesUseCase,
      ProfileRepository profileRepository, ActivateProfileUseCase activateProfileUseCase,
      SaveProfileUseCase saveProfileUseCase, DeleteProfileUseCase deleteProfileUseCase) {
    return new ProfilesViewModel(getProfilesUseCase, profileRepository, activateProfileUseCase, saveProfileUseCase, deleteProfileUseCase);
  }
}
