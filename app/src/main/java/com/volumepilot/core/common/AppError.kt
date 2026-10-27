package com.volumepilot.core.common

/**
 * Sealed hierarchy of domain and application errors.
 * Guarantees zero sensitive data leakage and provides user-actionable error messages.
 */
sealed class AppError(
    val userMessage: String,
    val recoveryHint: String? = null,
    cause: Throwable? = null
) : Exception(userMessage, cause) {

    data class StreamUnavailable(val streamName: String) : AppError(
        userMessage = "Audio stream '$streamName' is currently unavailable on this device.",
        recoveryHint = "Try adjusting the volume while media or call is active."
    )

    data class AudioPermissionDenied(val permissionName: String) : AppError(
        userMessage = "Permission required: $permissionName.",
        recoveryHint = "Grant this permission in device settings to enable this feature."
    )

    data class VolumeLimitExceeded(val requestedLevel: Int, val maxAllowed: Int) : AppError(
        userMessage = "Volume level ($requestedLevel) exceeds your configured safe listening limit ($maxAllowed).",
        recoveryHint = "Adjust the maximum volume cap in Settings if you need louder audio."
    )

    data class SettingsStorageError(override val message: String, val throwable: Throwable? = null) : AppError(
        userMessage = "Failed to save settings locally: $message",
        recoveryHint = "Check if device storage is full.",
        cause = throwable
    )

    data class ServiceInitializationError(val serviceName: String, val throwable: Throwable? = null) : AppError(
        userMessage = "Failed to initialize $serviceName.",
        recoveryHint = "Restart the application or verify battery optimization settings.",
        cause = throwable
    )

    data class HardwareButtonFault(val detail: String) : AppError(
        userMessage = "Physical volume control error: $detail",
        recoveryHint = "Use the on-screen volume slider instead."
    )

    data class Unexpected(val detail: String, val throwable: Throwable? = null) : AppError(
        userMessage = "An unexpected error occurred ($detail).",
        recoveryHint = "Please report this issue if it persists.",
        cause = throwable
    )
}
