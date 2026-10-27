package com.volumepilot.service.overlay;

import com.volumepilot.core.common.AppLogger;
import com.volumepilot.domain.usecase.GetSettingsUseCase;
import com.volumepilot.domain.usecase.GetVolumeStateUseCase;
import com.volumepilot.domain.usecase.HandleDeviceConnectedUseCase;
import com.volumepilot.domain.usecase.MuteUseCase;
import com.volumepilot.domain.usecase.SetVolumeUseCase;
import com.volumepilot.domain.usecase.ToggleVibrateUseCase;
import com.volumepilot.domain.usecase.UpdateSettingsUseCase;
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
public final class OverlayService_MembersInjector implements MembersInjector<OverlayService> {
  private final Provider<GetVolumeStateUseCase> getVolumeStateUseCaseProvider;

  private final Provider<SetVolumeUseCase> setVolumeUseCaseProvider;

  private final Provider<MuteUseCase> muteUseCaseProvider;

  private final Provider<ToggleVibrateUseCase> toggleVibrateUseCaseProvider;

  private final Provider<GetSettingsUseCase> getSettingsUseCaseProvider;

  private final Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider;

  private final Provider<HandleDeviceConnectedUseCase> handleDeviceConnectedUseCaseProvider;

  private final Provider<AppLogger> loggerProvider;

  public OverlayService_MembersInjector(
      Provider<GetVolumeStateUseCase> getVolumeStateUseCaseProvider,
      Provider<SetVolumeUseCase> setVolumeUseCaseProvider,
      Provider<MuteUseCase> muteUseCaseProvider,
      Provider<ToggleVibrateUseCase> toggleVibrateUseCaseProvider,
      Provider<GetSettingsUseCase> getSettingsUseCaseProvider,
      Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider,
      Provider<HandleDeviceConnectedUseCase> handleDeviceConnectedUseCaseProvider,
      Provider<AppLogger> loggerProvider) {
    this.getVolumeStateUseCaseProvider = getVolumeStateUseCaseProvider;
    this.setVolumeUseCaseProvider = setVolumeUseCaseProvider;
    this.muteUseCaseProvider = muteUseCaseProvider;
    this.toggleVibrateUseCaseProvider = toggleVibrateUseCaseProvider;
    this.getSettingsUseCaseProvider = getSettingsUseCaseProvider;
    this.updateSettingsUseCaseProvider = updateSettingsUseCaseProvider;
    this.handleDeviceConnectedUseCaseProvider = handleDeviceConnectedUseCaseProvider;
    this.loggerProvider = loggerProvider;
  }

  public static MembersInjector<OverlayService> create(
      Provider<GetVolumeStateUseCase> getVolumeStateUseCaseProvider,
      Provider<SetVolumeUseCase> setVolumeUseCaseProvider,
      Provider<MuteUseCase> muteUseCaseProvider,
      Provider<ToggleVibrateUseCase> toggleVibrateUseCaseProvider,
      Provider<GetSettingsUseCase> getSettingsUseCaseProvider,
      Provider<UpdateSettingsUseCase> updateSettingsUseCaseProvider,
      Provider<HandleDeviceConnectedUseCase> handleDeviceConnectedUseCaseProvider,
      Provider<AppLogger> loggerProvider) {
    return new OverlayService_MembersInjector(getVolumeStateUseCaseProvider, setVolumeUseCaseProvider, muteUseCaseProvider, toggleVibrateUseCaseProvider, getSettingsUseCaseProvider, updateSettingsUseCaseProvider, handleDeviceConnectedUseCaseProvider, loggerProvider);
  }

  @Override
  public void injectMembers(OverlayService instance) {
    injectGetVolumeStateUseCase(instance, getVolumeStateUseCaseProvider.get());
    injectSetVolumeUseCase(instance, setVolumeUseCaseProvider.get());
    injectMuteUseCase(instance, muteUseCaseProvider.get());
    injectToggleVibrateUseCase(instance, toggleVibrateUseCaseProvider.get());
    injectGetSettingsUseCase(instance, getSettingsUseCaseProvider.get());
    injectUpdateSettingsUseCase(instance, updateSettingsUseCaseProvider.get());
    injectHandleDeviceConnectedUseCase(instance, handleDeviceConnectedUseCaseProvider.get());
    injectLogger(instance, loggerProvider.get());
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.OverlayService.getVolumeStateUseCase")
  public static void injectGetVolumeStateUseCase(OverlayService instance,
      GetVolumeStateUseCase getVolumeStateUseCase) {
    instance.getVolumeStateUseCase = getVolumeStateUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.OverlayService.setVolumeUseCase")
  public static void injectSetVolumeUseCase(OverlayService instance,
      SetVolumeUseCase setVolumeUseCase) {
    instance.setVolumeUseCase = setVolumeUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.OverlayService.muteUseCase")
  public static void injectMuteUseCase(OverlayService instance, MuteUseCase muteUseCase) {
    instance.muteUseCase = muteUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.OverlayService.toggleVibrateUseCase")
  public static void injectToggleVibrateUseCase(OverlayService instance,
      ToggleVibrateUseCase toggleVibrateUseCase) {
    instance.toggleVibrateUseCase = toggleVibrateUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.OverlayService.getSettingsUseCase")
  public static void injectGetSettingsUseCase(OverlayService instance,
      GetSettingsUseCase getSettingsUseCase) {
    instance.getSettingsUseCase = getSettingsUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.OverlayService.updateSettingsUseCase")
  public static void injectUpdateSettingsUseCase(OverlayService instance,
      UpdateSettingsUseCase updateSettingsUseCase) {
    instance.updateSettingsUseCase = updateSettingsUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.OverlayService.handleDeviceConnectedUseCase")
  public static void injectHandleDeviceConnectedUseCase(OverlayService instance,
      HandleDeviceConnectedUseCase handleDeviceConnectedUseCase) {
    instance.handleDeviceConnectedUseCase = handleDeviceConnectedUseCase;
  }

  @InjectedFieldSignature("com.volumepilot.service.overlay.OverlayService.logger")
  public static void injectLogger(OverlayService instance, AppLogger logger) {
    instance.logger = logger;
  }
}
