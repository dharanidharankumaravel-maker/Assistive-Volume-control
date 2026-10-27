package com.volumepilot.domain.usecase;

import com.volumepilot.domain.repository.DiagnosticRepository;
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
public final class RepairOverlayUseCase_Factory implements Factory<RepairOverlayUseCase> {
  private final Provider<DiagnosticRepository> diagnosticRepositoryProvider;

  public RepairOverlayUseCase_Factory(Provider<DiagnosticRepository> diagnosticRepositoryProvider) {
    this.diagnosticRepositoryProvider = diagnosticRepositoryProvider;
  }

  @Override
  public RepairOverlayUseCase get() {
    return newInstance(diagnosticRepositoryProvider.get());
  }

  public static RepairOverlayUseCase_Factory create(
      Provider<DiagnosticRepository> diagnosticRepositoryProvider) {
    return new RepairOverlayUseCase_Factory(diagnosticRepositoryProvider);
  }

  public static RepairOverlayUseCase newInstance(DiagnosticRepository diagnosticRepository) {
    return new RepairOverlayUseCase(diagnosticRepository);
  }
}
