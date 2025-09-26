package com.frcoding.audionotes.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.frcoding.audionotes.presentation.core.base.BaseContentLayout
import com.frcoding.audionotes.presentation.core.components.AppTopBar
import com.frcoding.audionotes.presentation.core.components.MoodsRow
import com.frcoding.audionotes.presentation.core.components.TopicDropdown
import com.frcoding.audionotes.presentation.core.utils.toInt
import com.frcoding.audionotes.presentation.screens.settings.components.SettingsItem
import com.frcoding.audionotes.presentation.screens.settings.components.TopicTagsWithAddButton
import com.frcoding.audionotes.presentation.screens.settings.handling.SettingsUiAction

@Composable
fun SettingsScreenRoot(
    modifier: Modifier = Modifier,
    navigationState: NavigationState
) {
    val viewModel: SettingsViewModel = hiltViewModel()

    BaseContentLayout(
        viewModel = viewModel,
        modifier = modifier.padding(top = 8.dp),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.settings),
                onBackClick = { navigationState.popBackStack() }
            )
        }
    ) { uiState ->
        SettingsScreen(
            uiState = uiState,
            onUiAction = viewModel::onUiAction
        )
    }
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onUiAction: (SettingsUiAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsItem(
            title = stringResource(R.string.my_mood),
            description = stringResource(R.string.mood_header_description)
        ) {
            MoodsRow(
                moods = uiState.moods,
                activeMood = uiState.activeMood,
                onMoodClick = { onUiAction(SettingsUiAction.MoodSelected(it)) }
            )
        }

        Box {
            var topicOffset by remember { mutableStateOf(IntOffset.Zero) }
            val verticalSpace = 16.dp.toInt()

            SettingsItem(
                title = stringResource(R.string.my_topics),
                description = stringResource(R.string.topic_header_description)
            ) {
                TopicTagsWithAddButton(
                    topicState = uiState.topicState,
                    onUiAction = onUiAction,
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            topicOffset = IntOffset(
                                coordinates.positionInParent().x.toInt(),
                                coordinates.positionInParent().y.toInt() + coordinates.size.height + verticalSpace
                            )
                        },
                )
            }

            TopicDropdown(
                searchQuery = uiState.topicState.topicValue,
                topics = uiState.topicState.foundTopics,
                onTopicClick = { onUiAction(SettingsUiAction.TopicClicked(it)) },
                onCreateClick = { onUiAction(SettingsUiAction.CreateTopicClicked) },
                startOffset = topicOffset,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
        }
    }
}