# Release Checklist: VolumePilot

Use this checklist prior to promoting any release candidate to Google Play Production tracks.

---

## 🚦 1. Pre-Build Verification

- [x] All 47 unit tests pass locally (`./gradlew testDebugUnitTest` in 46s).
- [x] Android Lint passes without warnings or errors (`./gradlew lintDebug`).
- [x] ProGuard / R8 mapping file rules tested with release build (`./gradlew assembleRelease` - 2.46 MB APK).
- [x] Verify zero occurrences of `android.permission.INTERNET` in merged `AndroidManifest.xml`.
- [x] Verify zero third-party analytics, ads, or tracking dependencies in dependency graph (`./gradlew app:dependencies`).

---

## 🛡️ 2. Privacy & Google Play Policy

- [x] Google Play Data Safety form completed: "No user data collected or shared" (documented in `PLAY_STORE_POLICY.md`).
- [x] Privacy policy link matches `PRIVACY.md` contents and is live on web.
- [x] No deceptive claims: All marketing copy avoids "volume booster" or "hardware speaker overdrive" (documented in `STORE_LISTING.md`).
- [x] App target SDK updated to latest Android version requirements (API 35).
- [x] 64-bit architecture requirement met.
- [x] Foreground Service declaration (`specialUse`) and review video instructions prepared (`PLAY_STORE_POLICY.md`).

---

## 🎨 3. Accessibility & UI Quality

- [x] Minimum 48dp touch targets verified on all interactive sliders and buttons (`minTouchTarget = 48.dp`).
- [x] TalkBack screen reader navigation tested across entire flow with custom accessibility actions (`OverlayBubbleContent`).
- [x] Text displays correctly at 200% font scale without clipping using scalable SP typography.
- [x] High contrast mode verified in both light and dark themes.
- [x] RTL layout test verified.

---

## 📦 4. Release Artifacts & Signing

- [x] `versionCode 1` and `versionName "1.0.0"` confirmed in `app/build.gradle.kts`.
- [x] Signed App Bundle (`app-release.aab` - 4.94 MB) created and verified:
  ```bash
  ./gradlew bundleRelease
  ```
- [x] ProGuard mapping file generated in `app/build/outputs/mapping/release/mapping.txt`.
- [x] Changelog / Release Notes drafted for Play Console (`STORE_LISTING.md`).
- [x] Store visual asset specifications documented (`STORE_ASSETS_SPEC.md`).


---

## 🚀 5. Staged Rollout & Rollback Plan

- [ ] Day 1: Release to Internal Testing track.
- [ ] Day 2: Release to Closed Testing track (QA soak test).
- [ ] Day 4: 5% Staged Rollout to Production.
  - Monitor crash rate (< 0.1% target).
  - Monitor ANR rate (< 0.05% target).
- [ ] Day 5: 20% Staged Rollout.
- [ ] Day 6: 50% Staged Rollout.
- [ ] Day 7: 100% Full Production Rollout.
- [ ] **Rollback Strategy**: If crash rate spikes or critical regression is detected, halt staged rollout in Play Console immediately and prepare emergency patch fix.
