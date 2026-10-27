# Google Play Store Visual Assets Specification: VolumePilot

This guide defines the visual identity, dimensions, copy, and layout guidelines for Google Play graphics.

---

## 🎨 1. App Icon Specification

- **Dimensions**: `512 x 512 px`
- **Format**: 32-bit PNG (with alpha channel)
- **Max File Size**: 1024 KB
- **Shape**: Square (Google Play automatically applies rounded rectangle mask with 20% corner radius).
- **Design Concept**:
  - **Background**: Deep Indigo / Dark Slate (#0D1B2A or #121824) with a subtle radial gradient.
  - **Foreground Symbol**: A sleek, modern floating circular sound wave / volume controller dial in electric cyan (#00D2FF) and deep teal (#0083B0) gradient.
  - **Accent**: A clean, minimalist slider dot with a subtle inner glow representing precision and assistive guidance.
  - **Style**: Flat Material 3 geometry with subtle elevation shadow; no photographic elements or excessive skeumorphism.

---

## 🖼️ 2. Feature Graphic Specification

- **Dimensions**: `1024 x 500 px`
- **Format**: JPEG or 24-bit PNG (no alpha)
- **Max File Size**: 15 MB
- **Safe Zone**: Keep all critical text, logos, and focal elements within the center `800 x 360 px` to avoid cutoff on small mobile screens.
- **Composition**:
  - **Left Section (Text & Value Prop)**:
    - Main Header: `VolumePilot` (Bold typography, 48pt, White).
    - Subtitle: `Private, Floating Sound Control That Never Disappears` (22pt, Soft Gray #E0E1DD).
    - Trust Badges (Horizontal row with icons):
      - 🛡️ 100% Offline (No Internet)
      - 🔋 Battery-Friendly (Zero Polling)
      - 🚫 100% Ad-Free
  - **Right Section (Visual Showcase)**:
    - Tilted phone mockup displaying the floating overlay bubble magnetically docked to the edge with the expanded volume panel open over a sleek dark background.

---

## 📱 3. Phone Screenshots (Set of 6)

- **Recommended Resolution**: `1080 x 2400 px` (20:9 Aspect Ratio)
- **Design Pattern**: Top 25% for prominent banner headline + subtitle; bottom 75% for high-resolution device mockup running VolumePilot.

### Screenshot 1: Hero & Floating Assistive Control
- **Top Banner Headline**: `Floating Control for Broken Buttons`
- **Sub-Banner Text**: `Always accessible on your screen edge. Smooth magnetic snapping.`
- **Screen UI Displayed**: Device home screen or reading app with the translucent VolumePilot floating bubble docked on the right edge, showing volume level ring.

### Screenshot 2: Expanded Volume Control Panel
- **Top Banner Headline**: `Effortless One-Handed Adjustment`
- **Sub-Banner Text**: `Control Media, Ring, Notification & Alarm streams in one tap.`
- **Screen UI Displayed**: Expanded `OverlayPanelContent` open over the screen, showing stream tabs, responsive sliders, quick ringer toggle, and stepped +/- buttons.

### Screenshot 3: Intelligent Sound Profiles
- **Top Banner Headline**: `Instant Presets & Custom Profiles`
- **Sub-Banner Text**: `Switch between Work, Sleep, Driving & Headphones modes instantly.`
- **Screen UI Displayed**: `ProfilesScreen` showing the cards for `DEFAULT`, `SLEEP`, `WORK`, `DRIVING`, and a custom profile with audio level tags.

### Screenshot 4: Smart Hardware & Time Automation
- **Top Banner Headline**: `Automate Audio Without Battery Drain`
- **Sub-Banner Text**: `Triggers on Bluetooth & Headphone connection or scheduled hours.`
- **Screen UI Displayed**: `AutomationScreen` displaying Master Automation enabled, Device Rules (Bluetooth -> Driving, Headphones -> Music), and Scheduled Time Rules.

### Screenshot 5: Safe Listening & Hearing Health
- **Top Banner Headline**: `Protect Your Hearing`
- **Sub-Banner Text**: `Configurable maximum volume ceiling keeps sound at healthy levels.`
- **Screen UI Displayed**: Safe Listening card with the Max Volume Limiter enabled and the slider set to 80% ceiling with warning threshold indicators.

### Screenshot 6: Privacy By Default & Self-Healing Health
- **Top Banner Headline**: `100% Private & Built to Last`
- **Sub-Banner Text**: `Zero ads, zero accounts, zero internet permission. Real-time diagnostics.`
- **Screen UI Displayed**: `DiagnosticsScreen` showing all permissions green, 100% Offline badge, service uptime, and one-tap "Repair Overlay" self-healing button.
