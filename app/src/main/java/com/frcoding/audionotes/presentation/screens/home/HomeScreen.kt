package com.frcoding.audionotes.presentation.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frcoding.audionotes.R
import com.frcoding.audionotes.navigation.NavigationState
import com.frcoding.audionotes.navigation.Screen
import com.frcoding.audionotes.presentation.core.base.BaseContentLayout
import com.frcoding.audionotes.presentation.screens.home.components.EmptyHomeScreen
import com.frcoding.audionotes.presentation.screens.home.components.HomeFAB
import com.frcoding.audionotes.presentation.screens.home.components.HomeTopBar
import com.frcoding.audionotes.presentation.screens.home.handling.HomeActionEvent
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction

@Composable
fun HomeScreenRoot(
    navigationState: NavigationState,
    isDataLoaded: () -> Unit,
    isLaunchedFromWidget: Boolean,
    modifier: Modifier
) {
    val viewModel: HomeViewModel = hiltViewModel()

    BaseContentLayout(
        modifier = modifier,
        viewModel = viewModel,
        topBar = {
            HomeTopBar(
                title = stringResource(R.string.top_bar_title),
                onSettingsClick = {
                    navigationState.navigateTo(Screen.Settings.route)
                }
            )
        },
        floatingActionButton = {
            HomeFAB()
        },
        actionsEventHandler = { _, actionEvent ->
            when(actionEvent) {
                is HomeActionEvent.NavigateToEntryScreen ->
                    navigationState.navigateToEntry(
                        audioFilePath = actionEvent.audioFilePath,
                        amplitudeLogFilePath = actionEvent.amplitudeFilePath
                    )

                is HomeActionEvent.DataLoaded -> {
                    isDataLoaded()
                    if (isLaunchedFromWidget) viewModel.onUiAction(HomeUiAction.StartRecording)
                }
            }
        }
    ) { uiState ->
        if (uiState.entries.isEmpty() && !uiState.isFilterActive) {
            EmptyHomeScreen(modifier = modifier.padding(16.dp))
        } else {
            HomeScreen(
                uiState = uiState,
                onUiAction = viewModel::onUiAction
            )
        }

        RecordingBottomSheet()
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onUiAction: (HomeUiAction) -> Unit
) {

}