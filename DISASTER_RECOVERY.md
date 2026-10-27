# Disaster Recovery & Hotfix Protocol: VolumePilot

This document outlines the standard operating procedures for responding to production incidents, emergency hotfixes, and data recovery for **VolumePilot**.

---

## 🚨 1. Incident Severity Classification

| Severity Level | Definition | Response SLA | Action Required |
|---|---|---|---|
| **SEV-1 (Critical)** | App crashes on launch for > 1% of users, causes system ANR/freeze, or suppresses emergency alarms/calls. | **Immediate (< 1 hr)** | Halt staged rollout in Play Console immediately. Initiate hotfix patch. |
| **SEV-2 (High)** | Overlay disappears persistently on major OEM (Samsung/Xiaomi) or volume adjustments fail silently. | **< 4 hours** | Halt staged rollout if on < 50%. Triage root cause and prepare patch release. |
| **SEV-3 (Medium)** | Automation rule fails to evaluate on specific Bluetooth headset or visual cosmetic glitch. | **< 24 hours** | Schedule for next regular minor release. |

---

## 🛠️ 2. Emergency Hotfix Procedure (Step-by-Step)

When a SEV-1 or SEV-2 incident requires an emergency update:

### Step 1: Halt Rollout in Google Play Console
1. Open **Google Play Console** -> Select **VolumePilot**.
2. Navigate to **Release** -> **Production**.
3. Under the active release, click **Halt rollout**.
4. Existing users remain on their current version; no new users will receive the faulty build.

### Step 2: Create Hotfix Branch from Tag
```bash
# Fetch latest tags
git fetch --tags

# Create hotfix branch directly from the production release tag
git checkout -b hotfix/1.0.1 v1.0.0
```

### Step 3: Implement & Verify the Patch
1. Apply the minimal targeted fix.
2. Add a reproduction unit test in `app/src/test/java/` to prevent regression.
3. Run the complete test suite:
   ```bash
   ./gradlew testDebugUnitTest
   ./gradlew lintDebug
   ```

### Step 4: Bump Version Code & Name
In [`app/build.gradle.kts`](file:///d:/assitivePilot/app/build.gradle.kts):
```kotlin
defaultConfig {
    versionCode = 2      // Increment by 1
    versionName = "1.0.1" // Patch version bump
}
```

### Step 5: Build & Sign Release Bundle
```bash
./gradlew bundleRelease
```
Verify generated artifact: `app/build/outputs/bundle/release/app-release.aab`.

### Step 6: Deploy to Production
1. Upload the new `.aab` to **Production** track.
2. Release Notes:
   ```text
   Version 1.0.1:
   - Fixed an issue causing the overlay service to restart unexpectedly on certain devices.
   - Performance and stability improvements.
   ```
3. If SEV-1: Request **Expedited Review** through Google Play Console support form.

---

## 🔄 3. Corrupt Data Recovery & Fallback Protections

To protect users against data corruption caused by abrupt OS shutdowns or battery depletion:

### DataStore Resilience
- In [`SettingsRepositoryImpl.kt`](file:///d:/assitivePilot/app/src/main/java/com/volumepilot/data/settings/SettingsRepositoryImpl.kt), all disk reads are protected by `Flow.catch { if (it is IOException) emit(emptyPreferences()) }`.
- If local DataStore preferences become corrupted, VolumePilot automatically falls back to safe default preferences (`ThemeMode.SYSTEM`, `AudioStreamType.MEDIA`, `OverlayPosition(0, 200)`) rather than crashing on launch.

### Profile Fallback
- If custom profiles JSON cannot be parsed, [`JsonAdapters.jsonStringToProfiles`](file:///d:/assitivePilot/app/src/main/java/com/volumepilot/data/local/JsonAdapters.kt) automatically falls back to the immutable `VolumeProfile.BUILT_IN_PROFILES` presets (`DEFAULT`, `SLEEP`, `WORK`, `DRIVING`, `MEETING`, `GAMING`, `HEADPHONES`).

### User Self-Healing
Users experiencing issues can always:
1. Open **DiagnosticsScreen** -> Tap **Repair Overlay** to restart and re-bind the foreground service.
2. If custom profiles are corrupted: Tap **Restore** in **AutomationScreen** using their saved JSON backup.
3. System Fallback: Open Android **App Info** -> **Storage** -> **Clear Storage** to restore factory default configuration.
