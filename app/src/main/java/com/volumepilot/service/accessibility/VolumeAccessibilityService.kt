package com.volumepilot.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Accessibility service powering assistive hardware shortcuts (Lock Screen, Screenshot, Power Dialog).
 *
 * Adheres to accessibility best practices:
 * - 100% offline, zero network requests, zero event logging.
 * - Does not retrieve window content or inspect user input.
 * - Solely invokes system `performGlobalAction` for motor-accessibility and broken hardware keys.
 */
class VolumeAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isConnected.value = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No-op: privacy-first service does not observe or inspect screen content
    }

    override fun onInterrupt() {
        // No-op
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
            _isConnected.value = false
        }
    }

    companion object {
        private var instance: VolumeAccessibilityService? = null
        private val _isConnected = MutableStateFlow(false)
        val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            if (instance != null) return true
            // Also check via system settings for immediate detection before binding callback
            val expectedServiceName = "${context.packageName}/${VolumeAccessibilityService::class.java.canonicalName}"
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return enabledServices.contains(expectedServiceName) ||
                    enabledServices.contains(VolumeAccessibilityService::class.java.simpleName)
        }

        fun performLockScreen(context: Context): Boolean {
            val service = instance
            if (service != null) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
                } else {
                    service.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
                }
            } else {
                openAccessibilitySettings(context)
                return false
            }
        }

        fun performTakeScreenshot(context: Context): Boolean {
            val service = instance
            if (service != null) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    service.performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
                } else {
                    false
                }
            } else {
                openAccessibilitySettings(context)
                return false
            }
        }

        fun performPowerDialog(context: Context): Boolean {
            val service = instance
            if (service != null) {
                return service.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
            } else {
                openAccessibilitySettings(context)
                return false
            }
        }

        fun openAccessibilitySettings(context: Context) {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // Ignore if unable to launch settings intent
            }
        }
    }
}
