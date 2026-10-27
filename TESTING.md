# Testing Strategy & Execution: VolumePilot

## 1. Testing Philosophy

VolumePilot emphasizes **predictable, isolated, and fast verification**:
- **Pure Domain Testing**: Domain models and use cases are tested without any Android SDK mocks.
- **In-Memory Fakes**: We prioritize explicit in-memory fakes (`FakeVolumeController`, `FakeSettingsRepository`, `FakePermissionChecker`) over brittle, opaque mocking frameworks.
- **Turbine Flow Testing**: Asynchronous state flows and UI state emissions are validated with Cash App's Turbine library.

---

## 2. Running Automated Tests

### Unit Tests
Execute unit tests for domain, data, and presentation layers:
```bash
./gradlew testDebugUnitTest
```

### Android Lint & Static Analysis
```bash
./gradlew lintDebug
```

### Full Verification Pipeline
```bash
./gradlew check
```

---

## 3. Test Suites Overview

| Suite | Target | Key Scenarios Tested |
|---|---|---|
| `SetVolumeUseCaseTest` | Volume Adjustment | Volume bounding, safe cap limiter enforcement, stream unavailable error handling. |
| `EnforceMaxVolumeCapUseCaseTest` | Safety Engine | Media volume capping at configured percentage; ensuring alarm stream is never suppressed. |
| `MuteUseCaseTest` | Stream Muting | Stream mute toggle, unmuting, state preservation. |
| `ToggleVibrateUseCaseTest` | Ringer Control | Cycling between NORMAL and VIBRATE modes. |
| `UpdateSettingsUseCaseTest` | Preferences & Overlay | Overlay position coordinates persistence, appearance customization, preferred stream, and boot settings. |
| `HomeViewModelTest` | Presentation & UDF | Initial state combination, reactive flow emissions, overlay permission checks, and error dismissal. |
| `GetDiagnosticsUseCaseTest` | System Diagnostics | Diagnostic health evaluation, missing permission detection, and telemetry state aggregation. |
| `OemManufacturerTest` | Hardware Detection | Classification of Samsung, Xiaomi/Poco/Redmi, OnePlus/Oppo/Realme, Vivo, Huawei, Google devices. |
| `RepairOverlayUseCaseTest` | Overlay Self-Healing | Verifies automated restart of overlay service and error handling on permission revocation. |
| `OnboardingViewModelTest` | First-Run Flow | Permission checklist refresh, lifecycle resume observation, and onboarding completion. |
| `DiagnosticsViewModelTest` | Health & Recovery | Diagnostics observation, repair overlay invocation, and user feedback messaging. |
| `ActivateProfileUseCaseTest` | Volume Profiles | Applying profile stream levels, ringer mode overrides, and active profile ID state in DataStore. |
| `HandleDeviceConnectedUseCaseTest` | Hardware Automation | Triggering driving profile on Bluetooth and headphones profile on wired headset connections. |
| `EvaluateTimeAutomationUseCaseTest` | Time Schedules | Inexact AlarmManager time-window evaluations (supporting midnight crossovers and day masks). |
| `ProfilesViewModelTest` | Profile StateFlow | Loading built-in presets, applying custom profiles, and reactive state updates. |
| `AutomationViewModelTest` | Automation UDF | Master automation toggling, device rule updates, safe listening ceiling, and local JSON export/import. |


---

## 4. Manual Testing Checklist for Hardware Streams

When validating on physical test devices:
1. Verify adjusting media volume updates Bluetooth headset volume when connected.
2. Confirm Do Not Disturb (DND) status banner displays when DND is enabled in system quick settings.
3. Test volume cap limiter prevents slider from advancing beyond configured threshold.
4. Verify haptic feedback activates on slider movement when enabled, and stays silent when toggled off.
