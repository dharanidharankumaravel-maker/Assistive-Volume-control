package com.volumepilot.fakes

import com.volumepilot.core.permissions.PermissionChecker
import com.volumepilot.core.permissions.PermissionType

class FakePermissionChecker : PermissionChecker {
    var overlayGranted: Boolean = true
    var modifyAudioGranted: Boolean = true
    var notificationPolicyGranted: Boolean = true
    var postNotificationsGranted: Boolean = true
    var accessibilityGranted: Boolean = true
    var customAccessibilityStatus: com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus? = null

    override fun getAccessibilityStatus(): com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus {
        return customAccessibilityStatus ?: if (accessibilityGranted) {
            com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.ENABLED
        } else {
            com.volumepilot.service.accessibility.VolumeAccessibilityService.AccessibilityServiceStatus.DISABLED
        }
    }

    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return when (permission) {
            PermissionType.OVERLAY -> overlayGranted
            PermissionType.MODIFY_AUDIO -> modifyAudioGranted
            PermissionType.NOTIFICATION_POLICY -> notificationPolicyGranted
            PermissionType.POST_NOTIFICATIONS -> postNotificationsGranted
            PermissionType.ACCESSIBILITY -> accessibilityGranted
        }
    }
}
