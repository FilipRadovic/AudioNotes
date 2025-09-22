package com.frcoding.audionotes.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.frcoding.audionotes.presentation.screens.home.HomeScreenRoot

fun NavGraphBuilder.homeRoute(
    navigationState: NavigationState,
    isDataLoaded: () -> Unit,
    isLaunchedFromWidget: Boolean,
    modifier: Modifier = Modifier
) {
    composable(route = Screen.Home.route) {
        HomeScreenRoot(
            navigationState = navigationState,
            isDataLoaded = isDataLoaded,
            isLaunchedFromWidget = isLaunchedFromWidget,
            modifier = modifier
        )
    }
}