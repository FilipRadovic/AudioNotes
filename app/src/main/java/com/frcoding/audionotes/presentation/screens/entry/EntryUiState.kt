package com.frcoding.audionotes.presentation.screens.entry

import androidx.compose.runtime.Stable
import com.frcoding.audionotes.domain.entity.Topic
import com.frcoding.audionotes.presentation.core.base.handling.UiState
import com.frcoding.audionotes.presentation.core.state.PlayerState
import com.frcoding.audionotes.presentation.core.utils.MoodUiModel

data class EntryUiState(
    val currentMood: MoodUiModel = MoodUiModel.Undefined,
    val titleValue: String = "",
    val topicValue: String = "",
    val descriptionValue: String = "",
    val playerState: PlayerState = PlayerState(),
    val currentTopics: List<Topic> = emptyList(),
    val foundTopics: List<Topic> = emptyList(),
    val entrySheetState: EntrySheetState = EntrySheetState(),
    val showLeaveDialog: Boolean = false
) : UiState {
    val isSaveButtonEnabled: Boolean
        get() = titleValue.isNotBlank() && currentMood != MoodUiModel.Undefined

    @Stable
    data class EntrySheetState (
        val isOpen: Boolean = true,
        val activeMood: MoodUiModel = MoodUiModel.Undefined,
        val moods: List<MoodUiModel> = MoodUiModel.allMoods
    )
}
