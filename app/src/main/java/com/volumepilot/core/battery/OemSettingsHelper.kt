package com.volumepilot.core.battery

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.volumepilot.domain.model.OemGuidance
import com.volumepilot.domain.model.OemManufacturer

object OemSettingsHelper {

    fun getGuidance(): OemGuidance {
        val manufacturer = OemManufacturer.detect(Build.MANUFACTURER)
        return when (manufacturer) {
            OemManufacturer.SAMSUNG -> OemGuidance(
                manufacturer = manufacturer,
                headline = "Samsung Device Care Guidance",
                explanation = "Samsung One UI may put VolumePilot to sleep if 'Put unused apps to sleep' is active.",
                steps = listOf(
                    "Open Device Care > Battery.",
                    "Tap 'Background usage limits'.",
                    "Add VolumePilot to 'Never sleeping apps'."
                )
            )

            OemManufacturer.XIAOMI -> OemGuidance(
                manufacturer = manufacturer,
                headline = "Xiaomi / MIUI / HyperOS Autostart Guidance",
                explanation = "MIUI aggressively restricts background services and overlay draw permissions.",
                steps = listOf(
                    "Open Security app > Manage apps > VolumePilot.",
                    "Enable 'Autostart'.",
                    "Under Battery saver, select 'No restrictions'."
                )
            )

            OemManufacturer.ONEPLUS_OPPO_REALME -> OemGuidance(
                manufacturer = manufacturer,
                headline = "OnePlus / OxygenOS / ColorOS Guidance",
                explanation = "OxygenOS/ColorOS may freeze the floating overlay after closing recent apps.",
                steps = listOf(
                    "Go to Settings > Battery > More battery settings.",
                    "Tap 'Optimize battery use' > VolumePilot > select 'Don't optimize'.",
                    "Lock VolumePilot in the Recent Apps switcher."
                )
            )

            OemManufacturer.VIVO -> OemGuidance(
                manufacturer = manufacturer,
                headline = "Vivo / Funtouch OS Guidance",
                explanation = "Funtouch OS restricts high background power consumption by default.",
                steps = listOf(
                    "Open Settings > Battery > High background power consumption.",
                    "Toggle on for VolumePilot to allow uninterrupted volume control."
                )
            )

            OemManufacturer.HUAWEI -> OemGuidance(
                manufacturer = manufacturer,
                headline = "Huawei / EMUI Guidance",
                explanation = "EMUI PowerGenie stops non-whitelisted background services.",
                steps = listOf(
                    "Open Settings > Battery > App launch.",
                    "Find VolumePilot, turn off 'Manage automatically'.",
                    "Enable 'Auto-launch', 'Secondary launch', and 'Run in background'."
                )
            )

            OemManufacturer.GOOGLE,
            OemManufacturer.GENERIC -> OemGuidance(
                manufacturer = manufacturer,
                headline = "Standard Android Battery Optimization",
                explanation = "Android battery saver may occasionally delay service recovery if battery optimization is turned on.",
                steps = listOf(
                    "Open App info for VolumePilot.",
                    "Tap 'App battery usage' / 'Battery'.",
                    "Choose 'Unrestricted' for maximum reliability."
                )
            )
        }
    }

    fun createOemSettingsIntent(context: Context): Intent {
        val oem = OemManufacturer.detect(Build.MANUFACTURER)
        val candidateIntents = when (oem) {
            OemManufacturer.XIAOMI -> listOf(
                Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
                Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.powercenter.PowerSettings"))
            )
            OemManufacturer.HUAWEI -> listOf(
                Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
                Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"))
            )
            OemManufacturer.ONEPLUS_OPPO_REALME -> listOf(
                Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
                Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"))
            )
            OemManufacturer.VIVO -> listOf(
                Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
                Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity"))
            )
            OemManufacturer.SAMSUNG -> listOf(
                Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")),
                Intent().setComponent(ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity"))
            )
            else -> emptyList()
        }

        val pm = context.packageManager
        for (intent in candidateIntents) {
            if (intent.resolveActivity(pm) != null) {
                return intent
            }
        }

        // Fallback: standard App Details settings
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }
}
