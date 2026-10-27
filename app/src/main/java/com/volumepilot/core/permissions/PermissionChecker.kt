package com.volumepilot.core.permissions

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface PermissionChecker {
    fun isPermissionGranted(permission: PermissionType): Boolean
}

@Singleton
class AndroidPermissionChecker @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionChecker {

    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return when (permission) {
            PermissionType.OVERLAY -> {
                Settings.canDrawOverlays(context)
            }
            PermissionType.MODIFY_AUDIO -> {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.MODIFY_AUDIO_SETTINGS
                ) == PackageManager.PERMISSION_GRANTED
            }
            PermissionType.NOTIFICATION_POLICY -> {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.isNotificationPolicyAccessGranted == true
            }
            PermissionType.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }
            }
        }
    }
}
