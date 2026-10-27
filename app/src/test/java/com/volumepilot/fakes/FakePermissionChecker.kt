package com.volumepilot.fakes

import com.volumepilot.core.permissions.PermissionChecker
import com.volumepilot.core.permissions.PermissionType

class FakePermissionChecker : PermissionChecker {
    var overlayGranted: Boolean = true
    var modifyAudioGranted: Boolean = true
    var notificationPolicyGranted: Boolean = true
    var postNotificationsGranted: Boolean = true

    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return when (permission) {
            PermissionType.OVERLAY -> overlayGranted
            PermissionType.MODIFY_AUDIO -> modifyAudioGranted
            PermissionType.NOTIFICATION_POLICY -> notificationPolicyGranted
            PermissionType.POST_NOTIFICATIONS -> postNotificationsGranted
        }
    }
}
