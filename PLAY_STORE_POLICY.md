# Google Play Policy & Compliance Dossier: VolumePilot

This document contains the official compliance declarations, Play Console questionnaire responses, and review documentation for **VolumePilot** (`com.volumepilot`).

---

## 1. Foreground Service Declaration (Android 14+ / API 34+ Compliance)

Under Google Play's updated Foreground Service policies, apps targeting Android 14+ must declare specific foreground service types and justify why work cannot be performed in the background or via WorkManager.

### Service Declaration Details
- **Service Name**: `com.volumepilot.service.overlay.OverlayService`
- **Foreground Service Type**: `specialUse`
- **Sub-Type Attribute**:
  ```xml
  <property
      android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
      android:value="Floating assistive volume control overlay for accessibility and broken hardware buttons" />
  ```

### Justification for Google Play Reviewers
> **Why is a Foreground Service required?**
> VolumePilot is an accessibility utility designed for users with physically broken volume rocker buttons, motor impairments, or single-handed mobility requirements. The app maintains an interactive floating overlay window (`TYPE_APPLICATION_OVERLAY`) rendered via `WindowManager`.
> 
> Under Android OS architecture, persistent overlay presentation requires an active lifecycle host. A Foreground Service with an ongoing notification is required so that:
> 1. The user retains complete visibility and control over the background process at all times.
> 2. The operating system does not silently terminate the assistive button during active device usage.
> 3. The user can immediately pause, resume, or dismiss the overlay directly from the notification shade without launching the full application.

### Video Demonstration Guide for Google Play Review
Google Play requires a video demonstrating the user-facing necessity of the foreground service.
- **Link format**: YouTube (unlisted) or Google Drive link.
- **Demonstration Flow**:
  1. Open VolumePilot from the app launcher.
  2. Complete the onboarding screen explaining the overlay button purpose.
  3. Grant the "Display over other apps" permission.
  4. Show the floating bubble appearing on the screen.
  5. Pull down the Android notification shade to show the ongoing notification with "Pause" and "Stop" actions.
  6. Tap "Pause" -> show overlay disappears and notification updates to "Resume".
  7. Tap "Resume" -> show overlay reappears.
  8. Tap the floating bubble -> show the volume control panel opening over another application.
  9. Lock the screen and unlock -> show the overlay remains accessible without disappearing.

---

## 2. Google Play Data Safety Form Responses

VolumePilot is built with a **strict zero-telemetry, zero-network architecture**.

| Questionnaire Field | Response | Technical Verification |
|---|---|---|
| **Does your app collect or share any user data?** | **No** | Zero occurrences of `android.permission.INTERNET` in merged `AndroidManifest.xml`. |
| **Is all data collected ephemeral?** | **N/A** (No data collected) | No network socket or HTTP client libraries in project dependencies. |
| **Is data encrypted in transit?** | **N/A** (No network transmission) | Application cannot initiate network connections. |
| **Can users request data deletion?** | **Yes** | All preferences and profiles can be deleted locally in-app or via Android App Info -> "Clear Data". |

### Summary for Store Listing:
> "The developer does not collect or share any data from this app. All settings, volume configurations, and custom profiles are stored exclusively on your device."

---

## 3. WCAG 2.1 Level AA Accessibility Audit

VolumePilot was designed from day one to serve users with assistive needs.

| Criterion | Implementation | Status |
|---|---|---|
| **9.1.3.1 Info and Relationships** | All interactive controls use semantic roles (`Role.Button`, `Role.Switch`). Form fields, chips, and sliders have descriptive labels. | ✅ PASS |
| **9.2.1.1 Keyboard & Switch Access** | All features are accessible via hardware keyboards, DPads, and Switch Access navigation. | ✅ PASS |
| **9.2.5.1 Pointer Gestures** | The floating bubble supports single-tap activation, and TalkBack users can use **Custom Accessibility Actions** (`Open volume panel`, `Mute or unmute`, `Switch active stream`) without dragging. | ✅ PASS |
| **9.2.5.5 Target Size (Enhanced)** | All touch targets meet or exceed the **48x48 dp** standard (`minTouchTarget = 48.dp`). | ✅ PASS |
| **9.1.4.3 Contrast (Minimum)** | Contrast ratios exceed 4.5:1 for standard text and 3:1 for large typography in both Light and Dark themes. | ✅ PASS |
| **9.1.4.4 Resize Text** | Supports dynamic font scaling up to **200%** with zero text clipping. | ✅ PASS |

---

## 4. Privacy & Anti-Deception Statement

VolumePilot strictly adheres to Google Play's Deceptive Behavior policies:
- **No "Booster" Claims**: VolumePilot never claims to increase hardware speaker volume beyond 100% or exceed manufacturer hardware limitations.
- **Hearing Health Protection**: Includes an optional decibel ceiling limiter (`EnforceMaxVolumeCapUseCase`) to guard against dangerous audio spikes.
- **Zero Ads / Zero In-App Subscriptions**: Core functionality is 100% accessible without monetization barriers.
