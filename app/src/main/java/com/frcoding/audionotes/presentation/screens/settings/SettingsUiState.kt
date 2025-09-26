package com.frcoding.audionotes.presentation.screens.settings

import androidx.compose.ui.unit.IntOffset
import com.frcoding.audionotes.domain.entity.Topic
import com.frcoding.audionotes.presentation.core.base.handling.UiState
import com.frcoding.audionotes.presentation.core.utils.MoodUiModel

data class SettingsUiState(
    val activeMood: MoodUiModel = MoodUiModel.Undefined,
    val moods: List<MoodUiModel> = MoodUiModel.allMoods,
    val topicState: TopicState = TopicState()
): UiState {

    data class TopicState(
        val topicValue: String = "",
        val currentTopics: List<Topic> = listOf(),
        val foundTopics: List<Topic> = listOf(),
        val topicDropdownOffset: IntOffset = IntOffset.Zero,
        val isAddButtonVisible: Boolean = true
    )
}