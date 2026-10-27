package com.volumepilot.feature.home;

import com.volumepilot.core.permissions.PermissionChecker;
import com.volumepilot.domain.usecase.EnforceMaxVolumeCapUseCase;
import com.volumepilot.domain.usecase.GetSettingsUseCase;
import com.volumepilot.domain.usecase.GetVolumeStateUseCase;
import com.volumepilot.domain.usecase.MuteUseCase;
import com.volumepilot.domain.usecase.SetVolumeUseCase;
import com.volumepilot.domain.usecase.ToggleVibrateUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetVolumeStateUseCase> getVolumeStateUseCaseProvider;

  private final Provider<SetVolumeUseCase> setVolumeUseCaseProvider;

  private final Provider<MuteUseCase> muteUseCaseProvider;

  private final Provider<ToggleVibrateUseCase> toggleVibrateUseCaseProvider;

  private final Provider<GetSettingsUseCase> getSettingsUseCaseProvider;

  private final Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider;

  private final Provider<EnforceMaxVolumeCapUseCase> enforceMaxVolumeCapUseCaseProvider;

  private final Provider<PermissionChecker> permissionCheckerProvider;

  public HomeViewModel_Factory(Provider<GetVolumeStateUseCase> getVolumeStateUseCaseProvider,
      Provider<SetVolumeUseCase> setVolumeUseCaseProvider,
      Provider<MuteUseCase> muteUseCaseProvider,
      Provider<ToggleVibrateUseCase> toggleVibrateUseCaseProvider,
      Provider<GetSettingsUseCase> getSettingsUseCaseProvider,
      Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider,
      Provider<EnforceMaxVolumeCapUseCase> enforceMaxVolumeCapUseCaseProvider,
      Provider<PermissionChecker> permissionCheckerProvider) {
    this.getVolumeStateUseCaseProvider = getVolumeStateUseCaseProvider;
    this.setVolumeUseCaseProvider = setVolumeUseCaseProvider;
    this.muteUseCaseProvider = muteUseCaseProvider;
    this.toggleVibrateUseCaseProvider = toggleVibrateUseCaseProvider;
    this.getSettingsUseCaseProvider = getSettingsUseCaseProvider;
    this.updateSettingsUseCaseProvider = updateSettingsUseCaseProvider;
    this.enforceMaxVolumeCapUseCaseProvider = enforceMaxVolumeCapUseCaseProvider;
    this.permissionCheckerProvider = permissionCheckerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getVolumeStateUseCaseProvider.get(), setVolumeUseCaseProvider.get(), muteUseCaseProvider.get(), toggleVibrateUseCaseProvider.get(), getSettingsUseCaseProvider.get(), updateSettingsUseCaseProvider.get(), enforceMaxVolumeCapUseCaseProvider.get(), permissionCheckerProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetVolumeStateUseCase> getVolumeStateUseCaseProvider,
      Provider<SetVolumeUseCase> setVolumeUseCaseProvider,
      Provider<MuteUseCase> muteUseCaseProvider,
      Provider<ToggleVibrateUseCase> toggleVibrateUseCaseProvider,
      Provider<GetSettingsUseCase> getSettingsUseCaseProvider,
      Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider,
      Provider<EnforceMaxVolumeCapUseCase> enforceMaxVolumeCapUseCaseProvider,
      Provider<PermissionChecker> permissionCheckerProvider) {
    return new HomeViewModel_Factory(getVolumeStateUseCaseProvider, setVolumeUseCaseProvider, muteUseCaseProvider, toggleVibrateUseCaseProvider, getSettingsUseCaseProvider, updateSettingsUseCaseProvider, enforceMaxVolumeCapUseCaseProvider, permissionCheckerProvider);
  }

  public static HomeViewModel newInstance(GetVolumeStateUseCase getVolumeStateUseCase,
      SetVolumeUseCase setVolumeUseCase, MuteUseCase muteUseCase,
      ToggleVibrateUseCase toggleVibrateUseCase, GetSettingsUseCase getSettingsUseCase,
      UpdateSettingsUseCase updateSettingsUseCase,
      EnforceMaxVolumeCapUseCase enforceMaxVolumeCapUseCase, PermissionChecker permissionChecker) {
    return new HomeViewModel(getVolumeStateUseCase, setVolumeUseCase, muteUseCase, toggleVibrateUseCase, getSettingsUseCase, updateSettingsUseCase, enforceMaxVolumeCapUseCase, permissionChecker);
  }
}
