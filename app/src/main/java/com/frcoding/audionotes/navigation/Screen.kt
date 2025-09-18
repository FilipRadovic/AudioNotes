package com.frcoding.audionotes.navigation

sealed class Screen(val route: String) {

    companion object {
        private const val ROUTE_HOME = "home_screen"
        private const val ROUTE_ENTRY = "entry_screen"
        private const val ROUTE_SETTINGS = "settings_screen"
    }

    data object Home : Screen(ROUTE_HOME)

    data object Settings: Screen(ROUTE_SETTINGS)

    data object Entry: Screen(ROUTE_ENTRY)
}