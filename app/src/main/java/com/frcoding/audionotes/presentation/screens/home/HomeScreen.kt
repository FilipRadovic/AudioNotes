package com.frcoding.audionotes.presentation.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frcoding.audionotes.R
import com.frcoding.audionotes.navigation.NavigationState
import com.frcoding.audionotes.navigation.Screen
import com.frcoding.audionotes.presentation.core.base.BaseContentLayout
import com.frcoding.audionotes.presentation.screens.home.components.EmptyHomeScreen
import com.frcoding.audionotes.presentation.screens.home.components.FilterList
import com.frcoding.audionotes.presentation.screens.home.components.HomeFAB
import com.frcoding.audionotes.presentation.screens.home.components.HomeTopBar
import com.frcoding.audionotes.presentation.screens.home.components.NotesEntries
import com.frcoding.audionotes.presentation.screens.home.components.NotesFilter
import com.frcoding.audionotes.presentation.screens.home.components.RecordingBottomSheet
import com.frcoding.audionotes.presentation.screens.home.handling.HomeActionEvent
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction

@RequiresApi(Build.VERSION_CODES.O)
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
            HomeFAB(
                onResult = { isGranted, isLongClicked ->
                    if (isGranted) {
                        if (isLongClicked) {
                            viewModel.onUiAction(HomeUiAction.ActionButtonStartRecording)
                        } else {
                            viewModel.onUiAction(HomeUiAction.StartRecording)
                        }
                    }
                },
                onLongPressedRelease = { isEntryCanceled ->
                    viewModel.onUiAction(HomeUiAction.ActionButtonStopRecording(saveFile = !isEntryCanceled))
                }
            )
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

        RecordingBottomSheet(
            homeSheetState = uiState.homeSheetState,
            onUiAction = viewModel::onUiAction
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onUiAction: (HomeUiAction) -> Unit
) {
    var filterOffset by remember { mutableStateOf(IntOffset.Zero) }

    Column {
        NotesFilter(
            filterState = uiState.filterState,
            onUiAction = onUiAction,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    filterOffset = IntOffset(
                        coordinates.positionInParent().x.toInt(),
                        coordinates.positionInParent().y.toInt() + coordinates.size.height
                    )
                }
        )

        if (uiState.entries.isEmpty() && uiState.isFilterActive) {
            EmptyHomeScreen(
                title = stringResource(R.string.no_entries_found),
                supportingText = stringResource(R.string.no_entries_found_supporting_text)
            )
        }

        NotesEntries(
            entryNotes = uiState.entries,
            onUiAction = onUiAction
        )
    }

    if (uiState.filterState.isMoodsOpen) {
        FilterList(
            filterItems = uiState.filterState.moodFilterItems,
            onItemClick = { onUiAction(HomeUiAction.MoodFilterItemClicked(it)) },
            onDismissClicked = { onUiAction(HomeUiAction.MoodsFilterToggled) },
            startOffset = filterOffset
        )
    }

    if (uiState.filterState.isTopicsOpen) {
        FilterList(
            filterItems = uiState.filterState.topicFilterItem,
            onItemClick = { onUiAction(HomeUiAction.TopicFilterItemClicked(it)) },
            onDismissClicked = { onUiAction(HomeUiAction.TopicsFilterToggled) },
            startOffset = filterOffset
        )
    }
}