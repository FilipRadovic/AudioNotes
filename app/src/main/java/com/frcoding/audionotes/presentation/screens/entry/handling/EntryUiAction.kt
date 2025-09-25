package com.frcoding.audionotes.presentation.screens.entry.handling

import com.frcoding.audionotes.domain.entity.Topic
import com.frcoding.audionotes.presentation.core.base.handling.UiAction
import com.frcoding.audionotes.presentation.core.utils.MoodUiModel
import java.io.File

interface EntryUiAction: UiAction {
    data object BottomSheetClosed : EntryUiAction
    data class BottomSheetOpened(val mood: MoodUiModel) : EntryUiAction
    data class SheetConfirmedClicked(val mood: MoodUiModel) : EntryUiAction

    data class MoodSelected(val mood: MoodUiModel) : EntryUiAction
    data class TitleValueChanged(val value: String) : EntryUiAction
    data class DescriptionValueChanged(val value: String) : EntryUiAction

    data class TopicValueChanged(val value: String) : EntryUiAction
    data class TagClearClicked(val topic: Topic) : EntryUiAction

    data class TopicClicked(val topic: Topic) : EntryUiAction
    data object CreateTopicClicked : EntryUiAction

    data object PlayClicked : EntryUiAction
    data object PauseClicked : EntryUiAction
    data object ResumeClicked : EntryUiAction

    data class SaveButtonClicked(val outputDir: File) : EntryUiAction

    data object LeaveDialogToggled : EntryUiAction
    data object LeaveDialogConfirmClicked : EntryUiAction
    data object TranscribeButtonClicked : EntryUiAction
}