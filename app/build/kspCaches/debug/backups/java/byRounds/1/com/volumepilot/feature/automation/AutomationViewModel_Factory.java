package com.volumepilot.feature.automation;

import com.volumepilot.domain.repository.AutomationRepository;
import com.volumepilot.domain.usecase.ExportSettingsUseCase;
import com.volumepilot.domain.usecase.GetAutomationConfigUseCase;
import com.volumepilot.domain.usecase.GetProfilesUseCase;
import com.volumepilot.domain.usecase.ImportSettingsUseCase;
import com.volumepilot.domain.usecase.ToggleMasterAutomationUseCase;
import com.volumepilot.service.automation.TimeAutomationScheduler;
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
public final class AutomationViewModel_Factory implements Factory<AutomationViewModel> {
  private final Provider<GetAutomationConfigUseCase> getAutomationConfigUseCaseProvider;

  private final Provider<ToggleMasterAutomationUseCase> toggleMasterAutomationUseCaseProvider;

  private final Provider<AutomationRepository> automationRepositoryProvider;

  private final Provider<GetProfilesUseCase> getProfilesUseCaseProvider;

  private final Provider<ExportSettingsUseCase> exportSettingsUseCaseProvider;

  private final Provider<ImportSettingsUseCase> importSettingsUseCaseProvider;

  private final Provider<TimeAutomationScheduler> timeAutomationSchedulerProvider;

  public AutomationViewModel_Factory(
      Provider<GetAutomationConfigUseCase> getAutomationConfigUseCaseProvider,
      Provider<ToggleMasterAutomationUseCase> toggleMasterAutomationUseCaseProvider,
      Provider<AutomationRepository> automationRepositoryProvider,
      Provider<GetProfilesUseCase> getProfilesUseCaseProvider,
      Provider<ExportSettingsUseCase> exportSettingsUseCaseProvider,
      Provider<ImportSettingsUseCase> importSettingsUseCaseProvider,
      Provider<TimeAutomationScheduler> timeAutomationSchedulerProvider) {
    this.getAutomationConfigUseCaseProvider = getAutomationConfigUseCaseProvider;
    this.toggleMasterAutomationUseCaseProvider = toggleMasterAutomationUseCaseProvider;
    this.automationRepositoryProvider = automationRepositoryProvider;
    this.getProfilesUseCaseProvider = getProfilesUseCaseProvider;
    this.exportSettingsUseCaseProvider = exportSettingsUseCaseProvider;
    this.importSettingsUseCaseProvider = importSettingsUseCaseProvider;
    this.timeAutomationSchedulerProvider = timeAutomationSchedulerProvider;
  }

  @Override
  public AutomationViewModel get() {
    return newInstance(getAutomationConfigUseCaseProvider.get(), toggleMasterAutomationUseCaseProvider.get(), automationRepositoryProvider.get(), getProfilesUseCaseProvider.get(), exportSettingsUseCaseProvider.get(), importSettingsUseCaseProvider.get(), timeAutomationSchedulerProvider.get());
  }

  public static AutomationViewModel_Factory create(
      Provider<GetAutomationConfigUseCase> getAutomationConfigUseCaseProvider,
      Provider<ToggleMasterAutomationUseCase> toggleMasterAutomationUseCaseProvider,
      Provider<AutomationRepository> automationRepositoryProvider,
      Provider<GetProfilesUseCase> getProfilesUseCaseProvider,
      Provider<ExportSettingsUseCase> exportSettingsUseCaseProvider,
      Provider<ImportSettingsUseCase> importSettingsUseCaseProvider,
      Provider<TimeAutomationScheduler> timeAutomationSchedulerProvider) {
    return new AutomationViewModel_Factory(getAutomationConfigUseCaseProvider, toggleMasterAutomationUseCaseProvider, automationRepositoryProvider, getProfilesUseCaseProvider, exportSettingsUseCaseProvider, importSettingsUseCaseProvider, timeAutomationSchedulerProvider);
  }

  public static AutomationViewModel newInstance(
      GetAutomationConfigUseCase getAutomationConfigUseCase,
      ToggleMasterAutomationUseCase toggleMasterAutomationUseCase,
      AutomationRepository automationRepository, GetProfilesUseCase getProfilesUseCase,
      ExportSettingsUseCase exportSettingsUseCase, ImportSettingsUseCase importSettingsUseCase,
      TimeAutomationScheduler timeAutomationScheduler) {
    return new AutomationViewModel(getAutomationConfigUseCase, toggleMasterAutomationUseCase, automationRepository, getProfilesUseCase, exportSettingsUseCase, importSettingsUseCase, timeAutomationScheduler);
  }
}
