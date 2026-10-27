package com.volumepilot.domain.model

enum class AudioOutputDevice(val displayName: String) {
    SPEAKER("Phone Speaker"),
    WIRED_HEADPHONES("Wired Headphones"),
    BLUETOOTH("Bluetooth Audio"),
    USB_AUDIO("USB Audio"),
    UNKNOWN("Audio Device")
}
