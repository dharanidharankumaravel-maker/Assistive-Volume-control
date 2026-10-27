package com.volumepilot.feature.onboarding;

import com.volumepilot.core.permissions.PermissionChecker;
import com.volumepilot.domain.usecase.GetOemGuidanceUseCase;
import com.volumepilot.domain.usecase.UpdateSettingsUseCase;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<PermissionChecker> permissionCheckerProvider;

  private final Provider<GetOemGuidanceUseCase> getOemGuidanceUseCaseProvider;

  private final Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider;

  public OnboardingViewModel_Factory(Provider<PermissionChecker> permissionCheckerProvider,
      Provider<GetOemGuidanceUseCase> getOemGuidanceUseCaseProvider,
      Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider) {
    this.permissionCheckerProvider = permissionCheckerProvider;
    this.getOemGuidanceUseCaseProvider = getOemGuidanceUseCaseProvider;
    this.updateSettingsUseCaseProvider = updateSettingsUseCaseProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(permissionCheckerProvider.get(), getOemGuidanceUseCaseProvider.get(), updateSettingsUseCaseProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<PermissionChecker> permissionCheckerProvider,
      Provider<GetOemGuidanceUseCase> getOemGuidanceUseCaseProvider,
      Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider) {
    return new OnboardingViewModel_Factory(permissionCheckerProvider, getOemGuidanceUseCaseProvider, updateSettingsUseCaseProvider);
  }

  public static OnboardingViewModel newInstance(PermissionChecker permissionChecker,
      GetOemGuidanceUseCase getOemGuidanceUseCase, UpdateSettingsUseCase updateSettingsUseCase) {
    return new OnboardingViewModel(permissionChecker, getOemGuidanceUseCase, updateSettingsUseCase);
  }
}
