package com.volumepilot.service.volume

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.volumepilot.core.common.AppError
import com.volumepilot.core.common.AppLogger
import com.volumepilot.domain.model.AudioOutputDevice
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.RingerMode
import com.volumepilot.domain.model.VolumeState
import com.volumepilot.domain.model.VolumeStream
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AndroidVolumeController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioManager: AudioManager,
    private val logger: AppLogger,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher
) : VolumeController {

    private val streamMapping = mapOf(
        AudioStreamType.MEDIA to AudioManager.STREAM_MUSIC,
        AudioStreamType.RING to AudioManager.STREAM_RING,
        AudioStreamType.NOTIFICATION to AudioManager.STREAM_NOTIFICATION,
        AudioStreamType.ALARM to AudioManager.STREAM_ALARM,
        AudioStreamType.VOICE_CALL to AudioManager.STREAM_VOICE_CALL
    )

    override fun observeVolumeState(): Flow<VolumeState> = callbackFlow {
        // Emit initial state
        trySend(getCurrentVolumeState())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(getCurrentVolumeState())
            }
        }

        val filter = IntentFilter().apply {
            addAction("android.media.VOLUME_CHANGED_ACTION")
            addAction(AudioManager.RINGER_MODE_CHANGED_ACTION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        val deviceCallback = object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                trySend(getCurrentVolumeState())
            }

            override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                trySend(getCurrentVolumeState())
            }
        }

        audioManager.registerAudioDeviceCallback(deviceCallback, null)

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                logger.w("AndroidVolumeController", "Error unregistering receiver", e)
            }
            audioManager.unregisterAudioDeviceCallback(deviceCallback)
        }
    }.flowOn(ioDispatcher)

    override fun getCurrentVolumeState(): VolumeState {
        val streamMap = mutableMapOf<AudioStreamType, VolumeStream>()

        for ((streamType, streamId) in streamMapping) {
            try {
                val max = audioManager.getStreamMaxVolume(streamId)
                val min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    audioManager.getStreamMinVolume(streamId)
                } else {
                    0
                }
                val current = audioManager.getStreamVolume(streamId)
                val isMuted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    audioManager.isStreamMute(streamId)
                } else {
                    current == 0
                }

                streamMap[streamType] = VolumeStream(
                    streamType = streamType,
                    currentVolume = current,
                    minVolume = min,
                    maxVolume = max,
                    isMuted = isMuted
                )
            } catch (e: Exception) {
                logger.w("AndroidVolumeController", "Stream $streamType unreadable", e)
            }
        }

        val activeDevice = detectActiveOutputDevice()
        val ringer = when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> RingerMode.SILENT
            AudioManager.RINGER_MODE_VIBRATE -> RingerMode.VIBRATE
            else -> RingerMode.NORMAL
        }

        val isDnd = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val filter = nm?.currentInterruptionFilter ?: NotificationManager.INTERRUPTION_FILTER_ALL
            filter != NotificationManager.INTERRUPTION_FILTER_ALL
        } else {
            false
        }

        return VolumeState(
            streams = streamMap,
            activeOutputDevice = activeDevice,
            ringerMode = ringer,
            isDndActive = isDnd
        )
    }

    override suspend fun setVolume(streamType: AudioStreamType, level: Int): Result<Unit> =
        withContext(ioDispatcher) {
            val streamId = streamMapping[streamType]
                ?: return@withContext Result.failure(AppError.StreamUnavailable(streamType.displayName))

            try {
                audioManager.setStreamVolume(streamId, level, 0)
                Result.success(Unit)
            } catch (e: SecurityException) {
                logger.w("AndroidVolumeController", "Security exception setting volume for $streamType", e)
                Result.failure(AppError.AudioPermissionDenied("Do Not Disturb or audio policy restriction"))
            } catch (e: Exception) {
                logger.e("AndroidVolumeController", "Failed to set volume for $streamType", e)
                Result.failure(AppError.Unexpected("Failed to change volume", e))
            }
        }

    override suspend fun setMute(streamType: AudioStreamType, isMuted: Boolean): Result<Unit> =
        withContext(ioDispatcher) {
            val streamId = streamMapping[streamType]
                ?: return@withContext Result.failure(AppError.StreamUnavailable(streamType.displayName))

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val direction = if (isMuted) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE
                    audioManager.adjustStreamVolume(streamId, direction, 0)
                } else {
                    val targetVolume = if (isMuted) 0 else audioManager.getStreamMaxVolume(streamId) / 2
                    audioManager.setStreamVolume(streamId, targetVolume, 0)
                }
                Result.success(Unit)
            } catch (e: SecurityException) {
                logger.w("AndroidVolumeController", "Security exception muting $streamType", e)
                Result.failure(AppError.AudioPermissionDenied("DND policy restriction"))
            } catch (e: Exception) {
                logger.e("AndroidVolumeController", "Failed to mute $streamType", e)
                Result.failure(AppError.Unexpected("Failed to toggle mute", e))
            }
        }

    override suspend fun setRingerMode(mode: RingerMode): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val targetMode = when (mode) {
                    RingerMode.SILENT -> AudioManager.RINGER_MODE_SILENT
                    RingerMode.VIBRATE -> AudioManager.RINGER_MODE_VIBRATE
                    RingerMode.NORMAL -> AudioManager.RINGER_MODE_NORMAL
                }
                audioManager.ringerMode = targetMode
                Result.success(Unit)
            } catch (e: SecurityException) {
                logger.w("AndroidVolumeController", "Security exception setting ringer mode", e)
                Result.failure(AppError.AudioPermissionDenied("Notification Policy (DND) permission"))
            } catch (e: Exception) {
                logger.e("AndroidVolumeController", "Failed to set ringer mode", e)
                Result.failure(AppError.Unexpected("Failed to set ringer mode", e))
            }
        }

    private fun detectActiveOutputDevice(): AudioOutputDevice {
        try {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (device in devices) {
                when (device.type) {
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                    AudioDeviceInfo.TYPE_BLE_HEADSET,
                    AudioDeviceInfo.TYPE_BLE_SPEAKER -> return AudioOutputDevice.BLUETOOTH

                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                    AudioDeviceInfo.TYPE_WIRED_HEADSET -> return AudioOutputDevice.WIRED_HEADPHONES

                    AudioDeviceInfo.TYPE_USB_DEVICE,
                    AudioDeviceInfo.TYPE_USB_HEADSET -> return AudioOutputDevice.USB_AUDIO

                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> return AudioOutputDevice.SPEAKER
                }
            }
        } catch (e: Exception) {
            logger.w("AndroidVolumeController", "Error detecting audio output device", e)
        }
        return AudioOutputDevice.SPEAKER
    }
}
