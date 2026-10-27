package com.volumepilot.domain.model

data class TimeRule(
    val id: String,
    val name: String,
    val profileId: String,
    val startHour: Int, // 0..23
    val startMinute: Int, // 0..59
    val endHour: Int,
    val endMinute: Int,
    val repeatDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7), // 1=Sunday, 7=Saturday
    val isEnabled: Boolean = true
) {
    fun matchesTime(hour: Int, minute: Int, dayOfWeek: Int): Boolean {
        if (!isEnabled || !repeatDays.contains(dayOfWeek)) return false

        val currentMins = hour * 60 + minute
        val startMins = startHour * 60 + startMinute
        val endMins = endHour * 60 + endMinute

        return if (startMins <= endMins) {
            currentMins in startMins..endMins
        } else {
            // Crosses midnight (e.g. 22:00 to 07:00)
            currentMins >= startMins || currentMins <= endMins
        }
    }
}

data class AudioDeviceRule(
    val id: String,
    val deviceType: AudioOutputDevice,
    val profileId: String,
    val isEnabled: Boolean = true
)

data class AppRule(
    val id: String,
    val packageName: String,
    val appName: String,
    val profileId: String,
    val isEnabled: Boolean = true
)

data class SafeListeningConfig(
    val isMaxVolumeCapEnabled: Boolean = false,
    val maxVolumeCapPercent: Int = 80,
    val isSafeListeningWarningEnabled: Boolean = true,
    val warningThresholdPercent: Int = 75
)

data class AutomationConfig(
    val isMasterAutomationEnabled: Boolean = true,
    val activeProfileId: String = VolumeProfile.DEFAULT.id,
    val timeRules: List<TimeRule> = listOf(
        TimeRule(
            id = "rule_night_sleep",
            name = "Night Sleep (10 PM - 7 AM)",
            profileId = VolumeProfile.SLEEP.id,
            startHour = 22,
            startMinute = 0,
            endHour = 7,
            endMinute = 0,
            isEnabled = false
        )
    ),
    val deviceRules: List<AudioDeviceRule> = listOf(
        AudioDeviceRule(
            id = "rule_headphones_connect",
            deviceType = AudioOutputDevice.WIRED_HEADPHONES,
            profileId = VolumeProfile.HEADPHONES.id,
            isEnabled = true
        ),
        AudioDeviceRule(
            id = "rule_bluetooth_connect",
            deviceType = AudioOutputDevice.BLUETOOTH,
            profileId = VolumeProfile.HEADPHONES.id,
            isEnabled = true
        )
    ),
    val appRules: List<AppRule> = emptyList(),
    val safeListeningConfig: SafeListeningConfig = SafeListeningConfig()
)
