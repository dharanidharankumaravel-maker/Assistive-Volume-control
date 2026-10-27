package com.volumepilot.domain.model

enum class AudioStreamType(val displayName: String) {
    MEDIA("Media"),
    RING("Ring"),
    NOTIFICATION("Notification"),
    ALARM("Alarm"),
    VOICE_CALL("In-Call"),
    BLUETOOTH("Bluetooth")
}
