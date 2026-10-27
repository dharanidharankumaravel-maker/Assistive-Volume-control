package com.volumepilot.domain.model

data class DiagnosticState(
    val isOverlayPermissionGranted: Boolean = false,
    val isModifyAudioGranted: Boolean = true,
    val isPostNotificationGranted: Boolean = true,
    val isBatteryOptimizationIgnored: Boolean = false,
    val isStartOnBootEnabled: Boolean = false,
    val isOverlayServiceRunning: Boolean = false,
    val lastHeartbeatTimestamp: Long = 0L,
    val deviceManufacturer: String = "",
    val androidVersion: Int = 0
) {
    val isHealthy: Boolean
        get() = isOverlayPermissionGranted &&
                isModifyAudioGranted &&
                (!isOverlayServiceRunning || lastHeartbeatTimestamp > 0)
}
