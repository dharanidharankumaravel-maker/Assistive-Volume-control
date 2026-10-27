package com.volumepilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volumepilot.core.designsystem.VolumePilotTheme
import com.volumepilot.domain.model.ThemeMode
import com.volumepilot.domain.usecase.GetSettingsUseCase
import com.volumepilot.ui.VolumePilotAppNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val settings by getSettingsUseCase().collectAsStateWithLifecycle(
                initialValue = com.volumepilot.domain.model.UserSettings()
            )

            val darkTheme = when (settings.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            androidx.compose.runtime.LaunchedEffect(settings.isOverlayEnabled) {
                if (settings.isOverlayEnabled) {
                    if (android.provider.Settings.canDrawOverlays(this@MainActivity)) {
                        com.volumepilot.service.overlay.OverlayService.start(this@MainActivity)
                    }
                } else {
                    com.volumepilot.service.overlay.OverlayService.stop(this@MainActivity)
                }
            }

            VolumePilotTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VolumePilotAppNavHost(hasCompletedOnboarding = settings.hasCompletedOnboarding)
                }
            }
        }
    }
}
