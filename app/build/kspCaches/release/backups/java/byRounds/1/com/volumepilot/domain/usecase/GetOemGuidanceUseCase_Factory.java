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
public final class GetOemGuidanceUseCase_Factory implements Factory<GetOemGuidanceUseCase> {
  private final Provider<DiagnosticRepository> diagnosticRepositoryProvider;

  public GetOemGuidanceUseCase_Factory(
      Provider<DiagnosticRepository> diagnosticRepositoryProvider) {
    this.diagnosticRepositoryProvider = diagnosticRepositoryProvider;
  }

  @Override
  public GetOemGuidanceUseCase get() {
    return newInstance(diagnosticRepositoryProvider.get());
  }

  public static GetOemGuidanceUseCase_Factory create(
      Provider<DiagnosticRepository> diagnosticRepositoryProvider) {
    return new GetOemGuidanceUseCase_Factory(diagnosticRepositoryProvider);
  }

  public static GetOemGuidanceUseCase newInstance(DiagnosticRepository diagnosticRepository) {
    return new GetOemGuidanceUseCase(diagnosticRepository);
  }
}
