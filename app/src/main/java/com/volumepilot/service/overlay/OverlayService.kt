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
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.volumepilot.core.common.AppLogger
import com.volumepilot.core.designsystem.VolumePilotTheme
import com.volumepilot.domain.model.AudioStreamType
import com.volumepilot.domain.model.OverlayPosition
import com.volumepilot.domain.model.PowerLongPressAction
import com.volumepilot.domain.model.PowerSingleTapAction
import com.volumepilot.domain.model.UserSettings
import com.volumepilot.domain.model.VolumeState
import com.volumepilot.domain.usecase.GetSettingsUseCase
import com.volumepilot.domain.usecase.GetVolumeStateUseCase
import com.volumepilot.domain.usecase.HandleDeviceConnectedUseCase
import com.volumepilot.domain.usecase.MuteUseCase
import com.volumepilot.domain.usecase.SetVolumeUseCase
import com.volumepilot.domain.usecase.ToggleVibrateUseCase
import com.volumepilot.domain.usecase.UpdateSettingsUseCase
import androidx.compose.ui.graphics.Color
import com.volumepilot.feature.overlay.BasilFloatingControlWidget
import com.volumepilot.feature.overlay.CompactVolumeSliderBar
import com.volumepilot.feature.overlay.OverlayPanelContent
import com.volumepilot.service.accessibility.VolumeAccessibilityService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private var containerView: OverlayContainerView? = null
    private var overlayView: ComposeView? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private var isExpanded by mutableStateOf(false)
    private var isWidgetHidden by mutableStateOf(false)
    private var isDockedToLeft by mutableStateOf(false)
    private var currentPosition by mutableStateOf(OverlayPosition())
    private var selectedStreamType by mutableStateOf(AudioStreamType.MEDIA)
    private var currentWidgetSizeDp by mutableStateOf(48)
    private var isVolumeOnlyMode by mutableStateOf(false)
    private var currentSliderWidthDp by mutableStateOf(40)
    private var lastActionTime = 0L

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
        val notification = OverlayNotificationHelper.buildForegroundNotification(this, isWidgetHidden)
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
            OverlayNotificationHelper.ACTION_HIDE,
            OverlayNotificationHelper.ACTION_PAUSE -> {
                hideWidget()
            }
            OverlayNotificationHelper.ACTION_SHOW,
            OverlayNotificationHelper.ACTION_RESUME -> {
                showWidget()
            }
            OverlayNotificationHelper.ACTION_TOGGLE -> {
                if (isWidgetHidden) showWidget() else hideWidget()
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

    private fun hideWidget() {
        val now = android.os.SystemClock.elapsedRealtime()
        if (now - lastActionTime < 200L && isWidgetHidden) return
        lastActionTime = now

        isWidgetHidden = true
        containerView?.visibility = View.GONE
        updateNotification()
        serviceScope.launch {
            updateSettingsUseCase.setOverlayHidden(true)
        }
    }

    private fun showWidget() {
        val now = android.os.SystemClock.elapsedRealtime()
        if (now - lastActionTime < 200L && !isWidgetHidden) return
        lastActionTime = now

        isWidgetHidden = false
        containerView?.visibility = View.VISIBLE
        updateNotification()
        serviceScope.launch {
            updateSettingsUseCase.setOverlayHidden(false)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initOverlay() {
        serviceScope.launch {
            val settings = getSettingsUseCase().first()
            currentPosition = settings.overlayPosition
            isDockedToLeft = currentPosition.isSnappedToLeft
            isWidgetHidden = settings.isOverlayHidden
            selectedStreamType = settings.preferredStreamType
            currentWidgetSizeDp = settings.overlaySizeDp
            isVolumeOnlyMode = settings.buttonArrangement.isVolumeOnly
            currentSliderWidthDp = settings.sliderWidthDp

            val screenWidth = resources.displayMetrics.widthPixels
            val density = resources.displayMetrics.density
            val bubbleSizePx = (settings.overlaySizeDp * density).toInt()
            val marginPx = (4 * density).toInt()

            val initialXPos = if (currentPosition.x == 0 && !currentPosition.isSnappedToLeft) {
                (screenWidth - bubbleSizePx - marginPx).coerceAtLeast(marginPx)
            } else {
                currentPosition.x
            }

            val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = initialXPos
                y = currentPosition.y
            }
            layoutParams = params

            val wm = windowManager ?: return@launch

            val composeView = ComposeView(this@OverlayService).apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)
                setViewTreeViewModelStoreOwner(lifecycleOwner)

                setContent {
                    val volumeState by getVolumeStateUseCase().collectAsState(initial = VolumeState())
                    val currentSettings by getSettingsUseCase().collectAsState(initial = UserSettings())

                    VolumePilotTheme {
                        currentWidgetSizeDp = currentSettings.overlaySizeDp
                        isVolumeOnlyMode = currentSettings.buttonArrangement.isVolumeOnly
                        currentSliderWidthDp = currentSettings.sliderWidthDp

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isDockedToLeft) {
                                // Docked to Left Edge: [Floating Pill Widget] [Spacer(6.dp)] [Expanded Slider or Panel]
                                BasilFloatingControlWidget(
                                    stream = volumeState.getStream(selectedStreamType),
                                    opacity = currentSettings.overlayOpacity,
                                    sizeDp = currentSettings.overlaySizeDp,
                                    arrangement = currentSettings.buttonArrangement,
                                    colorTheme = currentSettings.widgetColor,
                                    isExpanded = isExpanded,
                                    isDockedToLeft = true,
                                    onPowerSingleTap = { handlePowerSingleTap(currentSettings) },
                                    onPowerLongPress = { handlePowerLongPress(currentSettings) },
                                    onVolumeUp = {
                                        handleVolumeStep(1, currentSettings.hapticFeedbackEnabled)
                                        if (currentSettings.buttonArrangement.isVolumeOnly) {
                                            setPanelExpanded(true)
                                        }
                                    },
                                    onVolumeDown = {
                                        handleVolumeStep(-1, currentSettings.hapticFeedbackEnabled)
                                        if (currentSettings.buttonArrangement.isVolumeOnly) {
                                            setPanelExpanded(true)
                                        }
                                    },
                                    onToggleExpand = {
                                        setPanelExpanded(!isExpanded)
                                        triggerHaptic(currentSettings.hapticFeedbackEnabled)
                                    },
                                    onMuteToggle = { handleMuteToggle(currentSettings.hapticFeedbackEnabled) },
                                    rgbGlowEnabled = currentSettings.rgbGlowEnabled,
                                    rgbGlowIntensity = currentSettings.rgbGlowIntensity
                                )

                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = slideInHorizontally(
                                        initialOffsetX = { -it / 2 },
                                        animationSpec = tween(220)
                                    ) + fadeIn(tween(220)),
                                    exit = slideOutHorizontally(
                                        targetOffsetX = { -it / 2 },
                                        animationSpec = tween(200)
                                    ) + fadeOut(tween(200))
                                ) {
                                    if (currentSettings.buttonArrangement.isVolumeOnly) {
                                        // Case 1: Only Vol+ and Vol- -> Compact single volume slider bar
                                        CompactVolumeSliderBar(
                                            stream = volumeState.getStream(selectedStreamType),
                                            accentColor = Color(currentSettings.sliderAccentColor),
                                            onVolumeChange = { level ->
                                                serviceScope.launch {
                                                    setVolumeUseCase(selectedStreamType, level)
                                                }
                                            },
                                            onMuteToggle = {
                                                val stream = volumeState.getStream(selectedStreamType)
                                                if (stream != null) {
                                                    serviceScope.launch {
                                                        muteUseCase(selectedStreamType, !stream.isMuted)
                                                    }
                                                }
                                            },
                                            onSecondaryAction = {
                                                serviceScope.launch {
                                                    toggleVibrateUseCase()
                                                }
                                            },
                                            opacity = currentSettings.overlayOpacity,
                                            colorTheme = currentSettings.widgetColor,
                                            rgbGlowEnabled = currentSettings.rgbGlowEnabled,
                                            rgbGlowIntensity = currentSettings.rgbGlowIntensity,
                                            sliderStyle = currentSettings.sliderStyle,
                                            sliderWidthDp = currentSettings.sliderWidthDp,
                                            sliderHeightDp = currentSettings.sliderHeightDp,
                                            sliderCornerRadiusDp = currentSettings.sliderCornerRadiusDp,
                                            sliderBackgroundAlpha = currentSettings.sliderBackgroundAlpha,
                                            rgbSpeedMillis = currentSettings.rgbSpeedMillis
                                        )
                                    } else {
                                        // Case 2 & 3: Power + Other Controls -> Full toggle volume panel
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
                                                setPanelExpanded(false)
                                            },
                                            opacity = currentSettings.overlayOpacity,
                                            colorTheme = currentSettings.widgetColor,
                                            rgbGlowEnabled = currentSettings.rgbGlowEnabled,
                                            rgbGlowIntensity = currentSettings.rgbGlowIntensity,
                                            sliderStyle = currentSettings.sliderStyle,
                                            sliderAccentColor = currentSettings.sliderAccentColor,
                                            sliderWidthDp = currentSettings.sliderWidthDp,
                                            sliderHeightDp = currentSettings.sliderHeightDp,
                                            sliderCornerRadiusDp = currentSettings.sliderCornerRadiusDp,
                                            sliderBackgroundAlpha = currentSettings.sliderBackgroundAlpha,
                                            rgbSpeedMillis = currentSettings.rgbSpeedMillis
                                        )
                                    }
                                }
                            } else {
                                // Docked to Right Edge: [Expanded Slider or Panel to the left] [Spacer(6.dp)] [Floating Pill Widget]
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = slideInHorizontally(
                                        initialOffsetX = { it / 2 },
                                        animationSpec = tween(220)
                                    ) + fadeIn(tween(220)),
                                    exit = slideOutHorizontally(
                                        targetOffsetX = { it / 2 },
                                        animationSpec = tween(200)
                                    ) + fadeOut(tween(200))
                                ) {
                                    if (currentSettings.buttonArrangement.isVolumeOnly) {
                                        // Case 1: Only Vol+ and Vol- -> Compact single volume slider bar
                                        CompactVolumeSliderBar(
                                            stream = volumeState.getStream(selectedStreamType),
                                            accentColor = Color(currentSettings.sliderAccentColor),
                                            onVolumeChange = { level ->
                                                serviceScope.launch {
                                                    setVolumeUseCase(selectedStreamType, level)
                                                }
                                            },
                                            onMuteToggle = {
                                                val stream = volumeState.getStream(selectedStreamType)
                                                if (stream != null) {
                                                    serviceScope.launch {
                                                        muteUseCase(selectedStreamType, !stream.isMuted)
                                                    }
                                                }
                                            },
                                            onSecondaryAction = {
                                                serviceScope.launch {
                                                    toggleVibrateUseCase()
                                                }
                                            },
                                            opacity = currentSettings.overlayOpacity,
                                            colorTheme = currentSettings.widgetColor,
                                            rgbGlowEnabled = currentSettings.rgbGlowEnabled,
                                            rgbGlowIntensity = currentSettings.rgbGlowIntensity,
                                            sliderStyle = currentSettings.sliderStyle,
                                            sliderWidthDp = currentSettings.sliderWidthDp,
                                            sliderHeightDp = currentSettings.sliderHeightDp,
                                            sliderCornerRadiusDp = currentSettings.sliderCornerRadiusDp,
                                            sliderBackgroundAlpha = currentSettings.sliderBackgroundAlpha,
                                            rgbSpeedMillis = currentSettings.rgbSpeedMillis
                                        )
                                    } else {
                                        // Case 2 & 3: Power + Other Controls -> Full toggle volume panel
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
                                                setPanelExpanded(false)
                                            },
                                            opacity = currentSettings.overlayOpacity,
                                            colorTheme = currentSettings.widgetColor,
                                            rgbGlowEnabled = currentSettings.rgbGlowEnabled,
                                            rgbGlowIntensity = currentSettings.rgbGlowIntensity,
                                            sliderStyle = currentSettings.sliderStyle,
                                            sliderAccentColor = currentSettings.sliderAccentColor,
                                            sliderWidthDp = currentSettings.sliderWidthDp,
                                            sliderHeightDp = currentSettings.sliderHeightDp,
                                            sliderCornerRadiusDp = currentSettings.sliderCornerRadiusDp,
                                            sliderBackgroundAlpha = currentSettings.sliderBackgroundAlpha,
                                            rgbSpeedMillis = currentSettings.rgbSpeedMillis
                                        )
                                    }
                                }

                                BasilFloatingControlWidget(
                                    stream = volumeState.getStream(selectedStreamType),
                                    opacity = currentSettings.overlayOpacity,
                                    sizeDp = currentSettings.overlaySizeDp,
                                    arrangement = currentSettings.buttonArrangement,
                                    colorTheme = currentSettings.widgetColor,
                                    isExpanded = isExpanded,
                                    isDockedToLeft = false,
                                    onPowerSingleTap = { handlePowerSingleTap(currentSettings) },
                                    onPowerLongPress = { handlePowerLongPress(currentSettings) },
                                    onVolumeUp = {
                                        handleVolumeStep(1, currentSettings.hapticFeedbackEnabled)
                                        if (currentSettings.buttonArrangement.isVolumeOnly) {
                                            setPanelExpanded(true)
                                        }
                                    },
                                    onVolumeDown = {
                                        handleVolumeStep(-1, currentSettings.hapticFeedbackEnabled)
                                        if (currentSettings.buttonArrangement.isVolumeOnly) {
                                            setPanelExpanded(true)
                                        }
                                    },
                                    onToggleExpand = {
                                        setPanelExpanded(!isExpanded)
                                        triggerHaptic(currentSettings.hapticFeedbackEnabled)
                                    },
                                    onMuteToggle = { handleMuteToggle(currentSettings.hapticFeedbackEnabled) },
                                    rgbGlowEnabled = currentSettings.rgbGlowEnabled,
                                    rgbGlowIntensity = currentSettings.rgbGlowIntensity
                                )
                            }
                        }
                    }
                }
            }
            overlayView = composeView

            val container = OverlayContainerView(
                context = this@OverlayService,
                windowManager = wm,
                layoutParams = params,
                isExpandedProvider = { isExpanded },
                onDismissOutside = {
                    setPanelExpanded(false)
                },
                onPositionChanged = { newPosition ->
                    currentPosition = newPosition
                    isDockedToLeft = newPosition.isSnappedToLeft
                    serviceScope.launch {
                        updateSettingsUseCase.setOverlayPosition(newPosition)
                    }
                }
            ).apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)
                setViewTreeViewModelStoreOwner(lifecycleOwner)
                addView(composeView)
            }
            containerView = container

            if (isWidgetHidden) {
                container.visibility = View.GONE
            }

            try {
                wm.addView(container, params)
            } catch (e: Exception) {
                logger.e(TAG, "Failed to add overlay container view to WindowManager", e)
            }

            updateNotification()
        }
    }

    private fun handlePowerSingleTap(settings: UserSettings) {
        triggerHaptic(settings.hapticFeedbackEnabled)
        when (settings.powerSingleTapAction) {
            PowerSingleTapAction.LOCK_SCREEN -> {
                VolumeAccessibilityService.performLockScreen(this)
            }
            PowerSingleTapAction.POWER_MENU -> {
                VolumeAccessibilityService.performPowerDialog(this)
            }
            PowerSingleTapAction.TOGGLE_PANEL -> {
                setPanelExpanded(!isExpanded)
            }
        }
    }

    private fun handlePowerLongPress(settings: UserSettings) {
        triggerHaptic(settings.hapticFeedbackEnabled)
        when (settings.powerLongPressAction) {
            PowerLongPressAction.TAKE_SCREENSHOT -> {
                VolumeAccessibilityService.performTakeScreenshot(this)
            }
            PowerLongPressAction.POWER_MENU -> {
                VolumeAccessibilityService.performPowerDialog(this)
            }
            PowerLongPressAction.LOCK_SCREEN -> {
                VolumeAccessibilityService.performLockScreen(this)
            }
        }
    }

    private fun handleVolumeStep(delta: Int, hapticEnabled: Boolean) {
        triggerHaptic(hapticEnabled)
        serviceScope.launch {
            val state = getVolumeStateUseCase().first()
            val stream = state.getStream(selectedStreamType) ?: return@launch
            val newLevel = (stream.currentVolume + delta).coerceIn(stream.minVolume, stream.maxVolume)
            setVolumeUseCase(selectedStreamType, newLevel)
        }
    }

    private fun handleMuteToggle(hapticEnabled: Boolean) {
        triggerHaptic(hapticEnabled)
        serviceScope.launch {
            val state = getVolumeStateUseCase().first()
            val stream = state.getStream(selectedStreamType) ?: return@launch
            muteUseCase(selectedStreamType, !stream.isMuted)
        }
    }

    private fun setPanelExpanded(expanded: Boolean) {
        if (isExpanded == expanded) return
        isExpanded = expanded

        val wm = windowManager ?: return
        val params = layoutParams ?: return
        val container = containerView ?: return

        val screenWidth = resources.displayMetrics.widthPixels
        val density = resources.displayMetrics.density
        val marginPx = (4 * density).toInt()

        val panelWidthDp = if (isVolumeOnlyMode) {
            currentSliderWidthDp + 14
        } else {
            (currentSliderWidthDp * 4) + 56
        }
        val widgetWidthPx = (currentWidgetSizeDp * density).toInt()
        val panelWidthPx = (panelWidthDp * density).toInt()
        val spacerPx = (6 * density).toInt()
        val totalWidthPx = widgetWidthPx + panelWidthPx + spacerPx

        if (expanded) {
            container.cancelAnimation()
            // Enable WATCH_OUTSIDE_TOUCH so tapping outside dismisses the panel automatically
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            if (isDockedToLeft) {
                // Docked on Left: Slider stays anchored at marginPx, panel opens adjacent on the right
                params.x = marginPx
            } else {
                // Docked on Right: Slider stays anchored at (screenWidth - widgetWidthPx - marginPx),
                // panel opens adjacent on the left without switching sides or jumping
                val targetX = (screenWidth - totalWidthPx - marginPx).coerceAtLeast(marginPx)
                params.x = targetX
            }
            try {
                wm.updateViewLayout(container, params)
            } catch (e: Exception) {
                logger.w(TAG, "Error updating overlay layout on expand", e)
            }
        } else {
            // Restore collapsed state
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            params.x = if (isDockedToLeft) {
                marginPx
            } else {
                (screenWidth - widgetWidthPx - marginPx).coerceAtLeast(marginPx)
            }
            try {
                wm.updateViewLayout(container, params)
            } catch (e: Exception) {
                logger.w(TAG, "Error updating overlay layout on collapse", e)
            }
        }
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
        val notification = OverlayNotificationHelper.buildForegroundNotification(this, isWidgetHidden)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.notify(OverlayNotificationHelper.NOTIFICATION_ID, notification)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        layoutParams?.let { params ->
            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels
            val viewWidth = containerView?.width ?: (52 * resources.displayMetrics.density).toInt()
            val viewHeight = containerView?.height ?: (140 * resources.displayMetrics.density).toInt()
            params.x = params.x.coerceIn(0, (screenWidth - viewWidth).coerceAtLeast(0))
            params.y = params.y.coerceIn(50, (screenHeight - viewHeight).coerceAtLeast(50))
            containerView?.let { container ->
                try {
                    windowManager?.updateViewLayout(container, params)
                } catch (e: Exception) {
                    logger.w(TAG, "Error updating overlay layout on config change", e)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.i(TAG, "OverlayService onDestroy")
        ServiceStateTracker.setRunning(false)

        lifecycleOwner.onStop()
        lifecycleOwner.onDestroy()
        serviceScope.cancel()

        containerView?.let { view ->
            view.cancelAnimation()
            try {
                windowManager?.removeView(view)
            } catch (e: Exception) {
                logger.w(TAG, "Error removing overlay container view from WindowManager", e)
            }
        }
        containerView = null
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
