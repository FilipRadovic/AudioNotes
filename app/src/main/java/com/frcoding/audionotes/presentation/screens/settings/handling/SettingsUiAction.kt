package com.frcoding.audionotes.presentation.screens.settings.handling

import com.frcoding.audionotes.domain.entity.Topic
import com.frcoding.audionotes.presentation.core.base.handling.UiAction
import com.frcoding.audionotes.presentation.core.utils.MoodUiModel

interface SettingsUiAction: UiAction {
    data class TopicValueChanged(val value: String) : SettingsUiAction
    data class TopicClicked(val topic: Topic) : SettingsUiAction
    data class TagClearClicked(val topic: Topic) : SettingsUiAction
    data object CreateTopicClicked : SettingsUiAction
    data object AddButtonVisibleToggled : SettingsUiAction
    data class MoodSelected(val mood: MoodUiModel) : SettingsUiAction
}