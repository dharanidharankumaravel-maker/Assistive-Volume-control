# VolumePilot 🎚️

> **A privacy-first, battery-efficient Android volume assistant that never disappears, survives reboot, respects battery life, and works without unnecessary permissions.**

VolumePilot is built specifically for users with broken physical volume buttons, accessibility needs, or a preference for fast one-handed control. It is **not** a generic "volume booster" that risks hardware damage or plays intrusive full-screen ads.

---

## 💎 Non-Negotiable Product Principles

- **Reliability first:** Designed to remain available during normal use, handle configuration changes gracefully, and recover without silent failures.
- **Privacy by default:** **Zero ads, zero account creation, zero analytics SDKs, zero tracking, zero remote config, and ZERO internet permission in the core app.**
- **Minimal permissions:** Requests only the permissions required for the user's enabled features, explaining every permission in plain language beforehand.
- **Battery efficiency:** Event-driven architecture using Kotlin Flows, no CPU polling loops, and zero wake locks.
- **Accessibility:** Full TalkBack support, 48dp+ touch targets, dynamic type scaling, high contrast, and haptic feedback.
- **No dark patterns:** No full-screen popups, no deceptive booster claims, no subscriptions required for core controls.

---

## 🏛️ Architecture & Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0+ with Coroutines & StateFlow |
| **UI** | Jetpack Compose + Material 3 (Dynamic Color support) |
| **Architecture** | Clean Architecture (Domain / Data / Presentation) + MVVM |
| **Dependency Injection** | Hilt |
| **Settings Storage** | Jetpack DataStore Preferences (100% local) |
| **Audio Engine** | Android `AudioManager`, `AudioDeviceCallback`, `AudioDeviceInfo` |
| **Testing** | JUnit 4, Turbine, MockK, Robolectric |
| **Minimum SDK** | Android 8.0 (API 26) |
| **Target SDK** | Android 15 (API 35) |

---

## 🚀 Building & Running

### Prerequisites
- JDK 17
- Android SDK (API 35 platform & build-tools)
- Gradle 8.11+ / Gradle Wrapper

### Command Line
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run lint
./gradlew lintDebug

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

---

## 📁 Repository Structure

```
VolumePilot/
├── app/
│   ├── src/main/java/com/volumepilot/
│   │   ├── core/
│   │   │   ├── common/         # AppError, local AppLogger, FeatureFlags
│   │   │   ├── designsystem/   # Material 3 Theme, Tokens, Components (VolumeSlider, PrimaryButton, etc.)
│   │   │   ├── permissions/    # PermissionChecker gateway
│   │   │   └── di/             # Hilt modules (AudioModule, RepositoryModule, AppModule, CoroutinesModule)
│   │   ├── data/
│   │   │   ├── local/          # DataStore PreferencesKeys
│   │   │   ├── repository/     # VolumeRepositoryImpl
│   │   │   └── settings/       # SettingsRepositoryImpl
│   │   ├── domain/             # Pure Kotlin domain (zero Android framework dependencies)
│   │   │   ├── model/          # VolumeStream, VolumeState, AudioOutputDevice, UserSettings, VolumePreset
│   │   │   ├── repository/     # VolumeRepository, SettingsRepository contracts
│   │   │   └── usecase/        # GetVolumeState, SetVolume, Mute, EnforceMaxVolumeCap, ToggleVibrate
│   │   ├── feature/
│   │   │   ├── onboarding/     # OnboardingScreen, OnboardingViewModel, permission checklist
│   │   │   ├── home/           # HomeScreen, HomeViewModel, HomeUiState
│   │   │   ├── diagnostics/    # DiagnosticsScreen, DiagnosticsViewModel, overlay self-healing
│   │   │   ├── profiles/       # ProfilesScreen, ProfilesViewModel, custom profile editor
│   │   │   ├── automation/     # AutomationScreen, AutomationViewModel, device/time rules, local JSON backup
│   │   │   ├── overlay/        # OverlayBubbleContent, OverlayPanelContent
│   │   │   └── navigation/     # NavDestination
│   │   ├── service/
│   │   │   ├── volume/         # VolumeController interface and AndroidVolumeController
│   │   │   ├── overlay/        # OverlayService (FGS), OverlayNotificationHelper, BootReceiver
│   │   │   └── automation/     # TimeAutomationReceiver, TimeAutomationScheduler

│   │   ├── ui/                 # VolumePilotAppNavHost
│   │   ├── MainActivity.kt
│   │   └── VolumePilotApp.kt
│   └── src/test/java/com/volumepilot/
│       ├── fakes/              # FakeVolumeController, FakeSettingsRepository, FakePermissionChecker
│       ├── domain/usecase/     # SetVolumeUseCaseTest, EnforceMaxVolumeCapUseCaseTest, MuteUseCaseTest, etc.
│       └── feature/home/       # HomeViewModelTest with Turbine
├── .github/workflows/ci.yml    # Continuous Integration pipeline (build, test, AAB packaging)
├── ARCHITECTURE.md             # System design, boundaries & data flow
├── PRIVACY.md                  # Privacy guarantees & permission safety
├── TESTING.md                  # Testing guidelines & execution
├── RELEASE_CHECKLIST.md        # Pre-launch and Google Play deployment verification
├── PLAY_STORE_POLICY.md        # FGS specialUse justification, Data Safety answers, review video guide
├── STORE_LISTING.md            # Play Store title, short/full descriptions, keywords, rating guide
├── STORE_ASSETS_SPEC.md        # App icon, 1024x500 feature graphic, and 6-panel screenshot copy
├── ROLLOUT_RUNBOOK.md          # Staged rollout cadence (1-100%), Android Vitals SLAs, and monitoring
└── DISASTER_RECOVERY.md        # Incident severity, rollout halt procedures, and emergency hotfix guide
```


