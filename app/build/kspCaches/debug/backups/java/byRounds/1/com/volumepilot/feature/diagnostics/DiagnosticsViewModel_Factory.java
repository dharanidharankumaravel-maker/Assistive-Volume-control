package com.volumepilot.feature.diagnostics;

import com.volumepilot.domain.usecase.GetDiagnosticsUseCase;
import com.volumepilot.domain.usecase.GetOemGuidanceUseCase;
import com.volumepilot.domain.usecase.RepairOverlayUseCase;
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
public final class DiagnosticsViewModel_Factory implements Factory<DiagnosticsViewModel> {
  private final Provider<GetDiagnosticsUseCase> getDiagnosticsUseCaseProvider;

  private final Provider<GetOemGuidanceUseCase> getOemGuidanceUseCaseProvider;

  private final Provider<RepairOverlayUseCase> repairOverlayUseCaseProvider;

  public DiagnosticsViewModel_Factory(Provider<GetDiagnosticsUseCase> getDiagnosticsUseCaseProvider,
      Provider<GetOemGuidanceUseCase> getOemGuidanceUseCaseProvider,
      Provider<RepairOverlayUseCase> repairOverlayUseCaseProvider) {
    this.getDiagnosticsUseCaseProvider = getDiagnosticsUseCaseProvider;
    this.getOemGuidanceUseCaseProvider = getOemGuidanceUseCaseProvider;
    this.repairOverlayUseCaseProvider = repairOverlayUseCaseProvider;
  }

  @Override
  public DiagnosticsViewModel get() {
    return newInstance(getDiagnosticsUseCaseProvider.get(), getOemGuidanceUseCaseProvider.get(), repairOverlayUseCaseProvider.get());
  }

  public static DiagnosticsViewModel_Factory create(
      Provider<GetDiagnosticsUseCase> getDiagnosticsUseCaseProvider,
      Provider<GetOemGuidanceUseCase> getOemGuidanceUseCaseProvider,
      Provider<RepairOverlayUseCase> repairOverlayUseCaseProvider) {
    return new DiagnosticsViewModel_Factory(getDiagnosticsUseCaseProvider, getOemGuidanceUseCaseProvider, repairOverlayUseCaseProvider);
  }

  public static DiagnosticsViewModel newInstance(GetDiagnosticsUseCase getDiagnosticsUseCase,
      GetOemGuidanceUseCase getOemGuidanceUseCase, RepairOverlayUseCase repairOverlayUseCase) {
    return new DiagnosticsViewModel(getDiagnosticsUseCase, getOemGuidanceUseCase, repairOverlayUseCase);
  }
}
