package com.volumepilot.service.overlay

import android.annotation.SuppressLint
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.volumepilot.core.common.AppLogger
import com.volumepilot.core.designsystem.VolumePilotTheme
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.OverlayPosition
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.model.VolumeState
import com.volumepilot.domain.usecase.GetSettingsUseCase
import com.volumepilot.domain.usecase.GetVolumeStateUseCase
import com.volumepilot.domain.usecase.HandleDeviceConnectedUseCase
import com.volumepilot.domain.usecase.MuteUseCase
import com.volumepilot.domain.usecase.SetVolumeUseCase
import com.volumepilot.domain.usecase.ToggleVibrateUseCase
import com.volumepilot.domain.usecase.UpdateSettingsUseCase
import com.volumepilot.feature.overlay.OverlayBubbleContent
import com.volumepilot.feature.overlay.OverlayPanelContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class OverlayService : Service() {

    @Inject lateinit var getVolumeStateUseCase: GetVolumeStateUseCase
    @Inject lateinit var setVolumeUseCase: SetVolumeUseCase
    @Inject lateinit var muteUseCase: MuteUseCase
    @Inject lateinit var toggleVibrateUseCase: ToggleVibrateUseCase
    @Inject lateinit var getSettingsUseCase: GetSettingsUseCase
    @Inject lateinit var updateSettingsUseCase: UpdateSettingsUseCase
    @Inject lateinit var handleDeviceConnectedUseCase: HandleDeviceConnectedUseCase
    @Inject lateinit var logger: AppLogger

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val lifecycleOwner = OverlayLifecycleOwner()

    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private var isExpanded by mutableStateOf(false)
    private var isPaused by mutableStateOf(false)
    private var selectedStreamType by mutableStateOf(AudioStreamType.MEDIA)

    private var currentPosition = OverlayPosition()
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        logger.i(TAG, "OverlayService onCreate")
        ServiceStateTracker.setRunning(true)

        OverlayNotificationHelper.createNotificationChannel(this)
        startServiceAsForeground()

        lifecycleOwner.onCreate()
        lifecycleOwner.onStart()

        if (!Settings.canDrawOverlays(this)) {
            logger.w(TAG, "SYSTEM_ALERT_WINDOW permission not granted. Stopping OverlayService.")
            stopSelf()
            return
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        initOverlay()
        observeDeviceChanges()
    }

    private fun observeDeviceChanges() {
        serviceScope.launch {
            var previousDevice: com.volumepilot.domain.model.AudioOutputDevice? = null
            getVolumeStateUseCase().collect { state ->
                val currentDevice = state.activeOutputDevice
                if (previousDevice != null && previousDevice != currentDevice) {
                    logger.d(TAG, "Audio device changed from $previousDevice to $currentDevice, triggering automation")
                    handleDeviceConnectedUseCase(currentDevice)
                }
                previousDevice = currentDevice
            }
        }
    }


    private fun startServiceAsForeground() {
        val notification = OverlayNotificationHelper.buildForegroundNotification(this, isPaused)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    OverlayNotificationHelper.NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(OverlayNotificationHelper.NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e is ForegroundServiceStartNotAllowedException) {
                logger.e(TAG, "ForegroundServiceStartNotAllowedException starting overlay service", e)
            } else {
                logger.e(TAG, "Error starting foreground service", e)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            OverlayNotificationHelper.ACTION_PAUSE -> {
                isPaused = true
                overlayView?.visibility = View.GONE
                updateNotification()
            }
            OverlayNotificationHelper.ACTION_RESUME -> {
                isPaused = false
                overlayView?.visibility = View.VISIBLE
                updateNotification()
            }
            OverlayNotificationHelper.ACTION_STOP -> {
                serviceScope.launch {
                    updateSettingsUseCase.setOverlayEnabled(false)
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initOverlay() {
        serviceScope.launch {
            val settings = getSettingsUseCase().first()
            currentPosition = settings.overlayPosition
            selectedStreamType = settings.preferredStreamType

            val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = currentPosition.x
                y = currentPosition.y
            }

            overlayView = ComposeView(this@OverlayService).apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)
                setViewTreeViewModelStoreOwner(lifecycleOwner)

                setContent {
                    val volumeState by getVolumeStateUseCase().collectAsState(initial = VolumeState())
                    val currentSettings by getSettingsUseCase().collectAsState(initial = UserSettings())

                    VolumePilotTheme {
                        if (isExpanded) {
                            OverlayPanelContent(
                                volumeState = volumeState,
                                selectedStreamType = selectedStreamType,
                                onStreamSelected = { type ->
                                    selectedStreamType = type
                                    serviceScope.launch {
                                        updateSettingsUseCase.setPreferredStreamType(type)
                                    }
                                },
                                onVolumeChange = { type, level ->
                                    serviceScope.launch {
                                        setVolumeUseCase(type, level)
                                    }
                                },
                                onMuteToggle = { type ->
                                    val stream = volumeState.getStream(type)
                                    if (stream != null) {
                                        serviceScope.launch {
                                            muteUseCase(type, !stream.isMuted)
                                        }
                                    }
                                },
                                onToggleVibrate = {
                                    serviceScope.launch {
                                        toggleVibrateUseCase()
                                    }
                                },
                                onClose = {
                                    isExpanded = false
                                },
                                opacity = currentSettings.overlayOpacity
                            )
                        } else {
                            OverlayBubbleContent(
                                stream = volumeState.getStream(selectedStreamType),
                                opacity = currentSettings.overlayOpacity,
                                sizeDp = currentSettings.overlaySizeDp,
                                onSingleTap = {
                                    isExpanded = true
                                    triggerHaptic(currentSettings.hapticFeedbackEnabled)
                                },
                                onDoubleTap = {
                                    val stream = volumeState.getStream(selectedStreamType)
                                    if (stream != null) {
                                        serviceScope.launch {
                                            muteUseCase(selectedStreamType, !stream.isMuted)
                                        }
                                        triggerHaptic(currentSettings.hapticFeedbackEnabled)
                                    }
                                },
                                onLongPress = {
                                    // Quick action: cycle to next stream
                                    val streamTypes = AudioStreamType.values()
                                    val nextIndex = (selectedStreamType.ordinal + 1) % streamTypes.size
                                    selectedStreamType = streamTypes[nextIndex]
                                    triggerHaptic(currentSettings.hapticFeedbackEnabled)
                                }
                            )
                        }
                    }
                }

                setOnTouchListener { _, event ->
                    handleTouchEvent(event)
                }
            }

            try {
                windowManager?.addView(overlayView, layoutParams)
            } catch (e: Exception) {
                logger.e(TAG, "Failed to add overlay view to WindowManager", e)
            }
        }
    }

    private fun handleTouchEvent(event: MotionEvent): Boolean {
        val params = layoutParams ?: return false
        val wm = windowManager ?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                isDragging = false
                return false // allow Compose child to receive tap gestures
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - initialTouchX).toInt()
                val dy = (event.rawY - initialTouchY).toInt()

                if (!isDragging && (abs(dx) > 10 || abs(dy) > 10)) {
                    isDragging = true
                }

                if (isDragging) {
                    params.x = initialX + dx
                    params.y = initialY + dy
                    wm.updateViewLayout(overlayView, params)
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    // Snap to nearest screen edge
                    val screenWidth = resources.displayMetrics.widthWidth()
                    val isLeft = event.rawX < screenWidth / 2f
                    params.x = if (isLeft) 16 else screenWidth - (overlayView?.width ?: 140) - 16
                    wm.updateViewLayout(overlayView, params)

                    currentPosition = OverlayPosition(params.x, params.y, isLeft)
                    serviceScope.launch {
                        updateSettingsUseCase.setOverlayPosition(currentPosition)
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun android.util.DisplayMetrics.widthWidth(): Int {
        return this.widthPixels
    }

    private fun triggerHaptic(enabled: Boolean) {
        if (!enabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(25)
                }
            }
        } catch (e: Exception) {
            logger.w(TAG, "Haptic feedback unavailable", e)
        }
    }

    private fun updateNotification() {
        val notification = OverlayNotificationHelper.buildForegroundNotification(this, isPaused)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.notify(OverlayNotificationHelper.NOTIFICATION_ID, notification)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Screen rotated - re-clamp position to stay safely on screen
        layoutParams?.let { params ->
            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels
            params.x = params.x.coerceIn(0, (screenWidth - 100).coerceAtLeast(0))
            params.y = params.y.coerceIn(50, (screenHeight - 150).coerceAtLeast(50))
            windowManager?.updateViewLayout(overlayView, params)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.i(TAG, "OverlayService onDestroy")
        ServiceStateTracker.setRunning(false)

        lifecycleOwner.onStop()
        lifecycleOwner.onDestroy()
        serviceScope.cancel()

        overlayView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (e: Exception) {
                logger.w(TAG, "Error removing overlay view from WindowManager", e)
            }
        }
        overlayView = null
    }

    companion object {
        private const val TAG = "OverlayService"

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            context.stopService(intent)
        }
    }
}
