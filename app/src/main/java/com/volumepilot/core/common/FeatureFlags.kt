package com.volumepilot.core.common

import com.volumepilot.BuildConfig

/**
 * Local-only feature flags.
 * Controlled exclusively by user settings and compile-time defaults.
 * NEVER connects to remote config or server endpoints.
 */
object FeatureFlags {
    /**
     * Allows user to configure an upper bound on volume to protect hearing.
     */
    const val ENABLE_VOLUME_LIMITER = true

    /**
     * Enables active audio output device detection (Speaker, Bluetooth, Wired, USB).
     */
    const val ENABLE_AUDIO_DEVICE_DETECTION = true

    /**
     * Enables in-call volume adjustment when in an active call.
     */
    const val ENABLE_IN_CALL_STREAM = true

    /**
     * Enables DND and ringer mode state observers.
     */
    const val ENABLE_RINGER_MODE_OBSERVER = true

    /**
     * Enables detailed local diagnostics in developer/debug builds.
     */
    val ENABLE_LOCAL_DIAGNOSTICS = BuildConfig.DEBUG
}
