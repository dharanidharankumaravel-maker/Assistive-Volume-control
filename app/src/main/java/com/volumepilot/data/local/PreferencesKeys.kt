package com.volumepilot.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val MAX_VOLUME_LIMITER_ENABLED = booleanPreferencesKey("max_volume_limiter_enabled")
    val MAX_VOLUME_CAP_PERCENT = intPreferencesKey("max_volume_cap_percent")
    val HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val START_ON_BOOT = booleanPreferencesKey("start_on_boot")
    val AUTO_HIDE_DELAY_SECONDS = intPreferencesKey("auto_hide_delay_seconds")
    val OVERLAY_OPACITY = floatPreferencesKey("overlay_opacity")
    val OVERLAY_SIZE_DP = intPreferencesKey("overlay_size_dp")
    val IS_OVERLAY_ENABLED = booleanPreferencesKey("is_overlay_enabled")
    val OVERLAY_POSITION_X = intPreferencesKey("overlay_position_x")
    val OVERLAY_POSITION_Y = intPreferencesKey("overlay_position_y")
    val OVERLAY_SNAPPED_LEFT = booleanPreferencesKey("overlay_snapped_left")
    val PREFERRED_STREAM_TYPE = stringPreferencesKey("preferred_stream_type")
    val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    val PROFILES_JSON = stringPreferencesKey("profiles_json")
    val ACTIVE_PROFILE_ID = stringPreferencesKey("active_profile_id")
    val AUTOMATION_CONFIG_JSON = stringPreferencesKey("automation_config_json")
    val BUTTON_ARRANGEMENT = stringPreferencesKey("button_arrangement")
    val WIDGET_COLOR = stringPreferencesKey("widget_color")
    val POWER_SINGLE_TAP_ACTION = stringPreferencesKey("power_single_tap_action")
    val POWER_LONG_PRESS_ACTION = stringPreferencesKey("power_long_press_action")
    val RGB_GLOW_ENABLED = booleanPreferencesKey("rgb_glow_enabled")
    val RGB_GLOW_INTENSITY = floatPreferencesKey("rgb_glow_intensity")
    val IS_OVERLAY_HIDDEN = booleanPreferencesKey("is_overlay_hidden")
    val SLIDER_STYLE = stringPreferencesKey("slider_style")
    val SLIDER_ACCENT_COLOR = longPreferencesKey("slider_accent_color")
    val SLIDER_WIDTH_DP = intPreferencesKey("slider_width_dp")
    val SLIDER_HEIGHT_DP = intPreferencesKey("slider_height_dp")
    val SLIDER_CORNER_RADIUS_DP = intPreferencesKey("slider_corner_radius_dp")
    val SLIDER_BACKGROUND_ALPHA = floatPreferencesKey("slider_background_alpha")
    val RGB_SPEED_MILLIS = intPreferencesKey("rgb_speed_millis")
}
