package com.volumepilot.data.local

import com.volumepilot.domain.model.AppRule
import com.volumepilot.domain.model.AudioDeviceRule
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.AutomationConfig
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.SafeListeningConfig
import com.volumepilot.domain.model.TimeRule
import com.volumepilot.domain.model.VolumeProfile
import org.json.JSONArray
import org.json.JSONObject

object JsonAdapters {

    fun profileToJson(profile: VolumeProfile): JSONObject {
        val obj = JSONObject()
        obj.put("id", profile.id)
        obj.put("name", profile.name)
        obj.put("iconName", profile.iconName)
        obj.put("ringerMode", profile.ringerMode.name)
        obj.put("isDndPreferred", profile.isDndPreferred)
        obj.put("isOverlayVisible", profile.isOverlayVisible)
        obj.put("isDeletable", profile.isDeletable)

        val streamsObj = JSONObject()
        for ((stream, level) in profile.streamLevels) {
            streamsObj.put(stream.name, level)
        }
        obj.put("streamLevels", streamsObj)
        return obj
    }

    fun jsonToProfile(obj: JSONObject): VolumeProfile {
        val id = obj.getString("id")
        val name = obj.getString("name")
        val iconName = obj.optString("iconName", "volume_up")
        val ringerMode = try {
            RingerMode.valueOf(obj.optString("ringerMode", RingerMode.NORMAL.name))
        } catch (e: Exception) {
            RingerMode.NORMAL
        }
        val isDnd = obj.optBoolean("isDndPreferred", false)
        val isOverlay = obj.optBoolean("isOverlayVisible", true)
        val isDeletable = obj.optBoolean("isDeletable", true)

        val streamsMap = mutableMapOf<AudioStreamType, Int>()
        val streamsObj = obj.optJSONObject("streamLevels")
        if (streamsObj != null) {
            for (key in streamsObj.keys()) {
                try {
                    val streamType = AudioStreamType.valueOf(key)
                    streamsMap[streamType] = streamsObj.getInt(key)
                } catch (e: Exception) {
                    // ignore unrecognized stream
                }
            }
        }

        return VolumeProfile(
            id = id,
            name = name,
            iconName = iconName,
            streamLevels = streamsMap,
            ringerMode = ringerMode,
            isDndPreferred = isDnd,
            isOverlayVisible = isOverlay,
            isDeletable = isDeletable
        )
    }

    fun profilesToJsonString(profiles: List<VolumeProfile>): String {
        val array = JSONArray()
        for (p in profiles) {
            array.put(profileToJson(p))
        }
        return array.toString()
    }

    fun jsonStringToProfiles(json: String?): List<VolumeProfile> {
        if (json.isNullOrBlank()) return VolumeProfile.BUILT_IN_PROFILES

        return try {
            val array = JSONArray(json)
            val list = mutableListOf<VolumeProfile>()
            for (i in 0 until array.length()) {
                list.add(jsonToProfile(array.getJSONObject(i)))
            }
            if (list.isEmpty()) VolumeProfile.BUILT_IN_PROFILES else list
        } catch (e: Exception) {
            VolumeProfile.BUILT_IN_PROFILES
        }
    }

    fun automationConfigToJson(config: AutomationConfig): String {
        val root = JSONObject()
        root.put("isMasterAutomationEnabled", config.isMasterAutomationEnabled)
        root.put("activeProfileId", config.activeProfileId)

        // Time rules
        val timeRulesArray = JSONArray()
        for (r in config.timeRules) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("name", r.name)
            obj.put("profileId", r.profileId)
            obj.put("startHour", r.startHour)
            obj.put("startMinute", r.startMinute)
            obj.put("endHour", r.endHour)
            obj.put("endMinute", r.endMinute)
            obj.put("isEnabled", r.isEnabled)
            val daysArray = JSONArray()
            r.repeatDays.forEach { daysArray.put(it) }
            obj.put("repeatDays", daysArray)
            timeRulesArray.put(obj)
        }
        root.put("timeRules", timeRulesArray)

        // Device rules
        val deviceRulesArray = JSONArray()
        for (r in config.deviceRules) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("deviceType", r.deviceType.name)
            obj.put("profileId", r.profileId)
            obj.put("isEnabled", r.isEnabled)
            deviceRulesArray.put(obj)
        }
        root.put("deviceRules", deviceRulesArray)

        // App rules
        val appRulesArray = JSONArray()
        for (r in config.appRules) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("packageName", r.packageName)
            obj.put("appName", r.appName)
            obj.put("profileId", r.profileId)
            obj.put("isEnabled", r.isEnabled)
            appRulesArray.put(obj)
        }
        root.put("appRules", appRulesArray)

        // Safe listening
        val safeObj = JSONObject()
        safeObj.put("isMaxVolumeCapEnabled", config.safeListeningConfig.isMaxVolumeCapEnabled)
        safeObj.put("maxVolumeCapPercent", config.safeListeningConfig.maxVolumeCapPercent)
        safeObj.put("isSafeListeningWarningEnabled", config.safeListeningConfig.isSafeListeningWarningEnabled)
        safeObj.put("warningThresholdPercent", config.safeListeningConfig.warningThresholdPercent)
        root.put("safeListeningConfig", safeObj)

        return root.toString()
    }

    fun jsonStringToAutomationConfig(json: String?): AutomationConfig {
        if (json.isNullOrBlank()) return AutomationConfig()

        return try {
            val root = JSONObject(json)
            val isMaster = root.optBoolean("isMasterAutomationEnabled", true)
            val activeId = root.optString("activeProfileId", VolumeProfile.DEFAULT.id)

            val timeRules = mutableListOf<TimeRule>()
            val timeArray = root.optJSONArray("timeRules")
            if (timeArray != null) {
                for (i in 0 until timeArray.length()) {
                    val obj = timeArray.getJSONObject(i)
                    val days = mutableSetOf<Int>()
                    val daysArr = obj.optJSONArray("repeatDays")
                    if (daysArr != null) {
                        for (d in 0 until daysArr.length()) {
                            days.add(daysArr.getInt(d))
                        }
                    } else {
                        days.addAll(listOf(1, 2, 3, 4, 5, 6, 7))
                    }
                    timeRules.add(
                        TimeRule(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            profileId = obj.getString("profileId"),
                            startHour = obj.getInt("startHour"),
                            startMinute = obj.getInt("startMinute"),
                            endHour = obj.getInt("endHour"),
                            endMinute = obj.getInt("endMinute"),
                            repeatDays = days,
                            isEnabled = obj.optBoolean("isEnabled", true)
                        )
                    )
                }
            }

            val deviceRules = mutableListOf<AudioDeviceRule>()
            val deviceArray = root.optJSONArray("deviceRules")
            if (deviceArray != null) {
                for (i in 0 until deviceArray.length()) {
                    val obj = deviceArray.getJSONObject(i)
                    val devType = try {
                        AudioOutputDevice.valueOf(obj.getString("deviceType"))
                    } catch (e: Exception) {
                        AudioOutputDevice.SPEAKER
                    }
                    deviceRules.add(
                        AudioDeviceRule(
                            id = obj.getString("id"),
                            deviceType = devType,
                            profileId = obj.getString("profileId"),
                            isEnabled = obj.optBoolean("isEnabled", true)
                        )
                    )
                }
            }

            val safeObj = root.optJSONObject("safeListeningConfig")
            val safeConfig = if (safeObj != null) {
                SafeListeningConfig(
                    isMaxVolumeCapEnabled = safeObj.optBoolean("isMaxVolumeCapEnabled", false),
                    maxVolumeCapPercent = safeObj.optInt("maxVolumeCapPercent", 80),
                    isSafeListeningWarningEnabled = safeObj.optBoolean("isSafeListeningWarningEnabled", true),
                    warningThresholdPercent = safeObj.optInt("warningThresholdPercent", 75)
                )
            } else {
                SafeListeningConfig()
            }

            AutomationConfig(
                isMasterAutomationEnabled = isMaster,
                activeProfileId = activeId,
                timeRules = if (timeRules.isNotEmpty()) timeRules else AutomationConfig().timeRules,
                deviceRules = if (deviceRules.isNotEmpty()) deviceRules else AutomationConfig().deviceRules,
                appRules = emptyList(),
                safeListeningConfig = safeConfig
            )
        } catch (e: Exception) {
            AutomationConfig()
        }
    }

    fun userSettingsToJson(settings: com.volumepilot.domain.model.UserSettings): JSONObject {
        val obj = JSONObject()
        obj.put("maxVolumeLimiterEnabled", settings.maxVolumeLimiterEnabled)
        obj.put("maxVolumeCapPercent", settings.maxVolumeCapPercent)
        obj.put("hapticFeedbackEnabled", settings.hapticFeedbackEnabled)
        obj.put("themeMode", settings.themeMode.name)
        obj.put("startOnBoot", settings.startOnBoot)
        obj.put("autoHideDelaySeconds", settings.autoHideDelaySeconds)
        obj.put("overlayOpacity", settings.overlayOpacity.toDouble())
        obj.put("overlaySizeDp", settings.overlaySizeDp)
        obj.put("isOverlayEnabled", settings.isOverlayEnabled)
        obj.put("preferredStreamType", settings.preferredStreamType.name)
        obj.put("hasCompletedOnboarding", settings.hasCompletedOnboarding)

        val posObj = JSONObject()
        posObj.put("x", settings.overlayPosition.x)
        posObj.put("y", settings.overlayPosition.y)
        posObj.put("isSnappedToLeft", settings.overlayPosition.isSnappedToLeft)
        obj.put("overlayPosition", posObj)

        return obj
    }

    fun jsonToUserSettings(obj: JSONObject): com.volumepilot.domain.model.UserSettings {
        val themeMode = try {
            com.volumepilot.domain.model.ThemeMode.valueOf(obj.optString("themeMode", "SYSTEM"))
        } catch (e: Exception) {
            com.volumepilot.domain.model.ThemeMode.SYSTEM
        }
        val streamType = try {
            AudioStreamType.valueOf(obj.optString("preferredStreamType", "MEDIA"))
        } catch (e: Exception) {
            AudioStreamType.MEDIA
        }

        val posObj = obj.optJSONObject("overlayPosition")
        val position = if (posObj != null) {
            com.volumepilot.domain.model.OverlayPosition(
                x = posObj.optInt("x", 0),
                y = posObj.optInt("y", 200),
                isSnappedToLeft = posObj.optBoolean("isSnappedToLeft", false)
            )
        } else {
            com.volumepilot.domain.model.OverlayPosition()
        }

        return com.volumepilot.domain.model.UserSettings(
            maxVolumeLimiterEnabled = obj.optBoolean("maxVolumeLimiterEnabled", false),
            maxVolumeCapPercent = obj.optInt("maxVolumeCapPercent", 80),
            hapticFeedbackEnabled = obj.optBoolean("hapticFeedbackEnabled", true),
            themeMode = themeMode,
            startOnBoot = obj.optBoolean("startOnBoot", false),
            autoHideDelaySeconds = obj.optInt("autoHideDelaySeconds", 3),
            overlayOpacity = obj.optDouble("overlayOpacity", 0.85).toFloat(),
            overlaySizeDp = obj.optInt("overlaySizeDp", 48),
            isOverlayEnabled = obj.optBoolean("isOverlayEnabled", false),
            overlayPosition = position,
            preferredStreamType = streamType,
            hasCompletedOnboarding = obj.optBoolean("hasCompletedOnboarding", false)
        )
    }
}

