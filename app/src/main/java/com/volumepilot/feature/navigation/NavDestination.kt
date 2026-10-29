package com.volumepilot.feature.navigation

sealed class NavDestination(val route: String) {
    object Onboarding : NavDestination("onboarding")
    object Home : NavDestination("home")
    object Settings : NavDestination("settings")
    object Diagnostics : NavDestination("diagnostics")
    object Profiles : NavDestination("profiles")
    object Automation : NavDestination("automation")
    object SliderStyles : NavDestination("slider_styles")
}
