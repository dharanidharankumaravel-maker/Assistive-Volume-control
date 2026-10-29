package com.volumepilot.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.volumepilot.feature.diagnostics.DiagnosticsScreen
import com.volumepilot.feature.diagnostics.DiagnosticsViewModel
import com.volumepilot.feature.home.HomeScreen
import com.volumepilot.feature.home.HomeViewModel
import com.volumepilot.feature.navigation.NavDestination
import com.volumepilot.feature.onboarding.OnboardingScreen
import com.volumepilot.feature.onboarding.OnboardingViewModel

@Composable
fun VolumePilotAppNavHost(
    hasCompletedOnboarding: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val startRoute = if (hasCompletedOnboarding) {
        NavDestination.Home.route
    } else {
        NavDestination.Onboarding.route
    }

    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = modifier
    ) {
        composable(NavDestination.Onboarding.route) {
            val onboardingViewModel: OnboardingViewModel = hiltViewModel()
            val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()

            OnboardingScreen(
                uiState = uiState,
                onRefreshPermissions = onboardingViewModel::refreshPermissions,
                onCompleteOnboarding = {
                    onboardingViewModel.completeOnboarding {
                        navController.navigate(NavDestination.Home.route) {
                            popUpTo(NavDestination.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(NavDestination.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = uiState,
                onVolumeChange = homeViewModel::onVolumeChange,
                onMuteToggle = homeViewModel::onMuteToggle,
                onApplyPreset = homeViewModel::onApplyPreset,
                onToggleVibrate = homeViewModel::onToggleVibrate,
                onToggleMaxVolumeLimiter = homeViewModel::onToggleMaxVolumeLimiter,
                onToggleOverlay = homeViewModel::onToggleOverlay,
                onToggleStartOnBoot = homeViewModel::onToggleStartOnBoot,
                onUpdateWidgetArrangement = homeViewModel::onUpdateWidgetArrangement,
                onUpdateWidgetColor = homeViewModel::onUpdateWidgetColor,
                onUpdatePowerSingleTap = homeViewModel::onUpdatePowerSingleTap,
                onUpdatePowerLongPress = homeViewModel::onUpdatePowerLongPress,
                onUpdateOverlayAppearance = homeViewModel::onUpdateOverlayAppearance,
                onUpdateRgbGlow = homeViewModel::onUpdateRgbGlow,
                onNavigateToDiagnostics = {
                    navController.navigate(NavDestination.Diagnostics.route)
                },
                onNavigateToProfiles = {
                    navController.navigate(NavDestination.Profiles.route)
                },
                onNavigateToAutomation = {
                    navController.navigate(NavDestination.Automation.route)
                },
                onNavigateToSliderStyles = {
                    navController.navigate(NavDestination.SliderStyles.route)
                },
                onClearError = homeViewModel::onClearError
            )
        }

        composable(NavDestination.Diagnostics.route) {
            val diagnosticsViewModel: DiagnosticsViewModel = hiltViewModel()
            val uiState by diagnosticsViewModel.uiState.collectAsStateWithLifecycle()

            DiagnosticsScreen(
                uiState = uiState,
                onRepairOverlay = diagnosticsViewModel::onRepairOverlay,
                onClearRepairMessage = diagnosticsViewModel::onClearRepairMessage,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavDestination.Profiles.route) {
            val profilesViewModel: com.volumepilot.feature.profiles.ProfilesViewModel = hiltViewModel()
            val uiState by profilesViewModel.uiState.collectAsStateWithLifecycle()

            com.volumepilot.feature.profiles.ProfilesScreen(
                uiState = uiState,
                onActivateProfile = profilesViewModel::activateProfile,
                onSaveProfile = profilesViewModel::saveProfile,
                onDeleteProfile = profilesViewModel::deleteProfile,
                onStartEditProfile = profilesViewModel::onStartEditProfile,
                onDismissEditDialog = profilesViewModel::onDismissEditDialog,
                onClearError = profilesViewModel::clearErrorMessage,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavDestination.Automation.route) {
            val automationViewModel: com.volumepilot.feature.automation.AutomationViewModel = hiltViewModel()
            val uiState by automationViewModel.uiState.collectAsStateWithLifecycle()

            com.volumepilot.feature.automation.AutomationScreen(
                uiState = uiState,
                onToggleMaster = automationViewModel::toggleMasterAutomation,
                onSaveDeviceRule = automationViewModel::saveDeviceRule,
                onSaveTimeRule = automationViewModel::saveTimeRule,
                onDeleteTimeRule = automationViewModel::deleteTimeRule,
                onUpdateSafeListening = automationViewModel::updateSafeListeningConfig,
                onExportSettings = automationViewModel::exportSettings,
                onImportSettings = automationViewModel::importSettings,
                onClearFeedback = automationViewModel::clearFeedback,
                onClearError = automationViewModel::clearError,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavDestination.SliderStyles.route) {
            val sliderStylesViewModel: com.volumepilot.feature.styles.SliderStylesViewModel = hiltViewModel()
            val uiState by sliderStylesViewModel.uiState.collectAsStateWithLifecycle()

            com.volumepilot.feature.styles.SliderStylesScreen(
                uiState = uiState,
                onSelectStyle = sliderStylesViewModel::onSelectStyle,
                onPreviewVolumeChange = sliderStylesViewModel::onPreviewVolumeChange,
                onUpdateAccentColor = sliderStylesViewModel::onUpdateAccentColor,
                onUpdateWidth = sliderStylesViewModel::onUpdateWidth,
                onUpdateHeight = sliderStylesViewModel::onUpdateHeight,
                onUpdateCornerRadius = sliderStylesViewModel::onUpdateCornerRadius,
                onUpdateBackgroundAlpha = sliderStylesViewModel::onUpdateBackgroundAlpha,
                onUpdateRgbSpeed = sliderStylesViewModel::onUpdateRgbSpeed,
                onUpdateRgbGlow = sliderStylesViewModel::onUpdateRgbGlow,
                onResetDefaults = sliderStylesViewModel::onResetToPresetDefaults,
                onApplyCustomization = sliderStylesViewModel::applyCustomization,
                onClearSuccess = sliderStylesViewModel::clearAppliedSuccess,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

