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
public final class GetDiagnosticsUseCase_Factory implements Factory<GetDiagnosticsUseCase> {
  private final Provider<DiagnosticRepository> diagnosticRepositoryProvider;

  public GetDiagnosticsUseCase_Factory(
      Provider<DiagnosticRepository> diagnosticRepositoryProvider) {
    this.diagnosticRepositoryProvider = diagnosticRepositoryProvider;
  }

  @Override
  public GetDiagnosticsUseCase get() {
    return newInstance(diagnosticRepositoryProvider.get());
  }

  public static GetDiagnosticsUseCase_Factory create(
      Provider<DiagnosticRepository> diagnosticRepositoryProvider) {
    return new GetDiagnosticsUseCase_Factory(diagnosticRepositoryProvider);
  }

  public static GetDiagnosticsUseCase newInstance(DiagnosticRepository diagnosticRepository) {
    return new GetDiagnosticsUseCase(diagnosticRepository);
  }
}
