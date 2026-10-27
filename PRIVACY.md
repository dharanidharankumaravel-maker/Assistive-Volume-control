# Privacy Policy & Transparency: VolumePilot

**Effective Date:** October 2026  
**Product:** VolumePilot for Android (`com.volumepilot`)

---

## 🔒 1. Core Privacy Pledge

VolumePilot is designed with a **privacy-by-default, zero-knowledge architecture**:
- **No Internet Access:** VolumePilot does **not** declare or request `android.permission.INTERNET`. It is technically incapable of sending data off your device.
- **No Analytics or Trackers:** No Firebase, Google Analytics, telemetry SDKs, crashlytics networks, or advertising identifiers.
- **No Account Required:** No login, registration, email collection, or cloud profiling.
- **No Screen Recording:** VolumePilot never captures, reads, records, or streams screen pixels or accessibility content.

---

## 🛡️ 2. Minimal Permissions & Plain-Language Explanations

| Permission | Purpose | Plain-Language Explanation |
|---|---|---|
| `SYSTEM_ALERT_WINDOW` | Special | Enables drawing the floating volume bubble and control panel over apps, full-screen videos, and games. |
| `FOREGROUND_SERVICE` | Normal | Keeps the assistive volume control service active and reliable in the background without being killed by the OS. |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Special (API 34+) | Declares the foreground service type for floating assistive tools per Android 14+ requirements. |
| `POST_NOTIFICATIONS` | Runtime (API 33+) | Displays the low-priority status notification allowing the user to pause, resume, or stop the floating overlay at any time. |
| `RECEIVE_BOOT_COMPLETED` | Normal | Allows VolumePilot to automatically restore the floating volume button after a device reboot if enabled by the user. |
| `MODIFY_AUDIO_SETTINGS` | Normal | Allows changing stream volume levels, muting streams, and adjusting output routing when requested by the user. |
| `VIBRATE` | Normal | Enables subtle tactile haptic feedback when dragging sliders or toggling mute. Can be disabled in Settings. |
| `ACCESS_NOTIFICATION_POLICY` | Special (Optional) | Allows the app to detect Do Not Disturb (DND) state so volume changes do not unintentionally override silent mode. |

---

## 💾 3. Data Storage & Local Retention

- All user settings (volume caps, theme preference, haptics) are stored locally in private application sandbox storage via Android Jetpack DataStore Preferences.
- No user data is backed up to external cloud endpoints.
- Uninstalling the application completely deletes all application preferences and configuration files immediately.

---

## 📋 4. Google Play Data Safety Disclosure

When completing Google Play Data Safety declaration:
- **Data collected:** **No** (Zero user data collected).
- **Data shared with third parties:** **No** (Zero third-party sharing).
- **Data encrypted in transit:** **Not applicable** (Zero network transmission).
- **Data deletion requests:** Uninstalling the app purges 100% of local data.
