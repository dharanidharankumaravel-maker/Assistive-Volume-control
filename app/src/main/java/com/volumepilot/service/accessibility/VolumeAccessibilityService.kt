package com.volumepilot.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
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

    override fun onUnbind(intent: Intent?): Boolean {
        if (instance == this) {
            instance = null
            _isConnected.value = false
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
            _isConnected.value = false
        }
    }

    enum class AccessibilityServiceStatus {
        DISABLED,
        CONNECTING,
        ENABLED
    }

    companion object {
        private var instance: VolumeAccessibilityService? = null
        private val _isConnected = MutableStateFlow(false)
        val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

        fun isServiceConnected(): Boolean {
            return instance != null && _isConnected.value
        }

        fun isAccessibilityEnabledInSettings(context: Context): Boolean {
            // 1. Check via AccessibilityManager (standard, official API)
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            val isEnabledInManager = am?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
                ?.any { service ->
                    val serviceInfo = service.resolveInfo?.serviceInfo
                    serviceInfo != null &&
                    serviceInfo.packageName == context.packageName &&
                    (serviceInfo.name == VolumeAccessibilityService::class.java.name ||
                     serviceInfo.name.endsWith(VolumeAccessibilityService::class.java.simpleName))
                } == true

            if (isEnabledInManager) return true

            // 2. Fallback check via Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            return try {
                val enabledServices = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: return false

                val expectedFull = "${context.packageName}/${VolumeAccessibilityService::class.java.name}"
                val expectedShort = "${context.packageName}/.${VolumeAccessibilityService::class.java.simpleName}"
                val expectedCanonical = "${context.packageName}/${VolumeAccessibilityService::class.java.canonicalName}"

                enabledServices.split(':').any { entry ->
                    val trimmed = entry.trim()
                    trimmed.equals(expectedFull, ignoreCase = true) ||
                    trimmed.equals(expectedShort, ignoreCase = true) ||
                    trimmed.equals(expectedCanonical, ignoreCase = true) ||
                    (trimmed.startsWith("${context.packageName}/") && trimmed.contains(VolumeAccessibilityService::class.java.simpleName))
                }
            } catch (e: Exception) {
                false
            }
        }

        fun getServiceStatus(context: Context): AccessibilityServiceStatus {
            return when {
                isServiceConnected() -> AccessibilityServiceStatus.ENABLED
                isAccessibilityEnabledInSettings(context) -> AccessibilityServiceStatus.CONNECTING
                else -> AccessibilityServiceStatus.DISABLED
            }
        }

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            return getServiceStatus(context) != AccessibilityServiceStatus.DISABLED
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
