package com.frcoding.audionotes.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootAppNavigation(
    navigationState: NavigationState,
    isDataLoaded: () -> Unit,
    isLaunchedFromWidget: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navigationState.navHostController,
        startDestination = Screen.Home.route
    ) {
        homeRoute(
            navigationState = navigationState,
            isDataLoaded = isDataLoaded,
            isLaunchedFromWidget = isLaunchedFromWidget,
            modifier = modifier
        )
        entryRoute(
            navigationState = navigationState,
            modifier = modifier
        )
        settingsRoute(
            navigationState = navigationState,
            modifier = modifier
        )
    }
}