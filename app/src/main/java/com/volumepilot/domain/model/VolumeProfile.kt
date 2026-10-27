package com.volumepilot.domain.model

data class VolumeProfile(
    val id: String,
    val name: String,
    val iconName: String,
    val streamLevels: Map<AudioStreamType, Int>,
    val ringerMode: RingerMode = RingerMode.NORMAL,
    val isDndPreferred: Boolean = false,
    val isOverlayVisible: Boolean = true,
    val isDeletable: Boolean = true
) {
    companion object {
        val DEFAULT = VolumeProfile(
            id = "profile_default",
            name = "Default",
            iconName = "volume_up",
            streamLevels = mapOf(
                AudioStreamType.MEDIA to 10,
                AudioStreamType.RING to 5,
                AudioStreamType.NOTIFICATION to 5,
                AudioStreamType.ALARM to 7
            ),
            ringerMode = RingerMode.NORMAL,
            isDeletable = false
        )

        val SLEEP = VolumeProfile(
            id = "profile_sleep",
            name = "Sleep",
            iconName = "bedtime",
            streamLevels = mapOf(
                AudioStreamType.MEDIA to 0,
                AudioStreamType.RING to 0,
                AudioStreamType.NOTIFICATION to 0,
                AudioStreamType.ALARM to 5
            ),
            ringerMode = RingerMode.SILENT,
            isDndPreferred = true,
            isDeletable = false
        )

        val WORK = VolumeProfile(
            id = "profile_work",
            name = "Work",
            iconName = "work",
            streamLevels = mapOf(
                AudioStreamType.MEDIA to 2,
                AudioStreamType.RING to 0,
                AudioStreamType.NOTIFICATION to 0,
                AudioStreamType.ALARM to 6
            ),
            ringerMode = RingerMode.VIBRATE,
            isDeletable = false
        )

        val DRIVING = VolumeProfile(
            id = "profile_driving",
            name = "Driving",
            iconName = "directions_car",
            streamLevels = mapOf(
                AudioStreamType.MEDIA to 12,
                AudioStreamType.RING to 7,
                AudioStreamType.NOTIFICATION to 7,
                AudioStreamType.ALARM to 7
            ),
            ringerMode = RingerMode.NORMAL,
            isDeletable = false
        )

        val MEETING = VolumeProfile(
            id = "profile_meeting",
            name = "Meeting",
            iconName = "groups",
            streamLevels = mapOf(
                AudioStreamType.MEDIA to 0,
                AudioStreamType.RING to 0,
                AudioStreamType.NOTIFICATION to 0,
                AudioStreamType.ALARM to 4
            ),
            ringerMode = RingerMode.SILENT,
            isDndPreferred = true,
            isDeletable = false
        )

        val GAMING = VolumeProfile(
            id = "profile_gaming",
            name = "Gaming",
            iconName = "sports_esports",
            streamLevels = mapOf(
                AudioStreamType.MEDIA to 11,
                AudioStreamType.RING to 2,
                AudioStreamType.NOTIFICATION to 1,
                AudioStreamType.ALARM to 7
            ),
            ringerMode = RingerMode.VIBRATE,
            isDeletable = false
        )

        val HEADPHONES = VolumeProfile(
            id = "profile_headphones",
            name = "Headphones",
            iconName = "headphones",
            streamLevels = mapOf(
                AudioStreamType.MEDIA to 8,
                AudioStreamType.RING to 4,
                AudioStreamType.NOTIFICATION to 4,
                AudioStreamType.ALARM to 6
            ),
            ringerMode = RingerMode.NORMAL,
            isDeletable = false
        )

        val BUILT_IN_PROFILES = listOf(
            DEFAULT,
            SLEEP,
            WORK,
            DRIVING,
            MEETING,
            GAMING,
            HEADPHONES
        )
    }
}
