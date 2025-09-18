package com.frcoding.audionotes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost

@Composable
fun RootAppNavigation(
    navigationState: NavigationState
) {
    NavHost(
        navController = navigationState.navHostController,
        startDestination = Screen.Home.route
    ) {

    }
}