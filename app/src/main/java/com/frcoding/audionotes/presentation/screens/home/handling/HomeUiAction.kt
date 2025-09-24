package com.frcoding.audionotes.presentation.screens.home.handling

import com.frcoding.audionotes.presentation.core.base.handling.UiAction

sealed interface HomeUiAction : UiAction {
    data object StartRecording: HomeUiAction
    data object PauseRecording: HomeUiAction
    data object ResumeRecording: HomeUiAction
    data class StopRecording(val saveFile: Boolean) : HomeUiAction

    data object ActionButtonStartRecording : HomeUiAction
    data class ActionButtonStopRecording(val saveFile: Boolean = true) : HomeUiAction


    data object MoodsFilterToggled : HomeUiAction
    data object TopicsFilterToggled : HomeUiAction
    data object TopicsFilterClearClicked : HomeUiAction
    data object MoodsFilterClearClicked : HomeUiAction
    data class MoodFilterItemClicked(val title: String) : HomeUiAction
    data class TopicFilterItemClicked(val title: String) : HomeUiAction

    data class EntryPlayClick(val entryId: Long) : HomeUiAction
    data class EntryPauseClick(val entryId: Long) : HomeUiAction
    data class EntryResumeClick(val entryId: Long) : HomeUiAction

}