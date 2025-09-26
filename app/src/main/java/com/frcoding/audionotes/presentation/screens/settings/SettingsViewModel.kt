package com.frcoding.audionotes.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import com.frcoding.audionotes.domain.entity.Topic
import com.frcoding.audionotes.domain.repository.SettingsRepository
import com.frcoding.audionotes.domain.repository.TopicRepository
import com.frcoding.audionotes.presentation.core.base.BaseViewModel
import com.frcoding.audionotes.presentation.core.utils.MoodUiModel
import com.frcoding.audionotes.presentation.core.utils.toMoodUiModel
import com.frcoding.audionotes.presentation.screens.settings.handling.SettingsActionEvent
import com.frcoding.audionotes.presentation.screens.settings.handling.SettingsUiAction
import com.frcoding.audionotes.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

typealias SettingsBaseViewModel = BaseViewModel<SettingsUiState, SettingsUiAction, SettingsActionEvent>

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val settingsRepository: SettingsRepository
): SettingsBaseViewModel() {
    override val initialState: SettingsUiState
        get() = SettingsUiState()

    private val defaultMood = settingsRepository.getMood(Constants.KEY_MOOD_SETTINGS)
    private val defaultTopicsId = settingsRepository.getTopics(Constants.KEY_TOPIC_SETTINGS)

    private val searchQuery = MutableStateFlow("")
    private val searchResults: StateFlow<List<Topic>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                flow {
                    val foundTopics = topicRepository.searchTopics(query)
                    emit(foundTopics)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        launch {
            initializeDefaultSettings()
            observeTopicSearchResult()
        }
    }

    override fun onUiAction(uiAction: SettingsUiAction) {
        when (uiAction) {
            is SettingsUiAction.TopicValueChanged -> updateTopicValue(uiAction.value)
            is SettingsUiAction.TagClearClicked -> clearTopic(uiAction.topic)
            is SettingsUiAction.TopicClicked -> updateCurrentTopics(uiAction.topic)
            SettingsUiAction.CreateTopicClicked -> addNewTopic()
            SettingsUiAction.AddButtonVisibleToggled -> toggleAddButtonVisibility()
            is SettingsUiAction.MoodSelected -> selectMood(uiAction.mood)
        }
    }

    private suspend fun initializeDefaultSettings() {
        val defaultTopics = topicRepository.getTopicsByIdList(defaultTopicsId)
        updateState {
            it.copy(
                activeMood = defaultMood.toMoodUiModel(),
                topicState = currentState.topicState.copy(currentTopics = defaultTopics)
            )
        }
    }

    private suspend fun observeTopicSearchResult() {
        searchResults.collect {
            updateTopicState {
                it.copy(
                    foundTopics = searchResults.value
                )
            }
        }
    }

    private fun updateTopicValue(topic: String) {
        updateTopicState {
            it.copy(
                topicValue = topic
            )
        }
        searchQuery.value = topic
    }

    private fun clearTopic(topic: Topic) {
        updateTopicState {
            it.copy(
                currentTopics = currentState.topicState.currentTopics - topic
            )
        }
        saveTopicSettings()
    }

    private fun updateCurrentTopics(newTopic: Topic) {
        updateTopicState {
            it.copy(
                currentTopics = it.currentTopics + newTopic,
                topicValue = "",
                isAddButtonVisible = true
            )
        }
    }

    private fun addNewTopic() {
        val newTopic = Topic(name = currentState.topicState.topicValue)
        updateCurrentTopics(newTopic)
        saveTopicSettings()
        launch {
            topicRepository.insertTopic(newTopic)
        }
    }

    private fun toggleAddButtonVisibility() {
        updateTopicState {
            it.copy(isAddButtonVisible = !currentState.topicState.isAddButtonVisible)
        }
    }

    private fun selectMood(mood: MoodUiModel) {
        val updatedMood = if (currentState.activeMood == mood) MoodUiModel.Undefined else mood
        updateState {
            it.copy(activeMood = updatedMood)
        }
        if (!currentState.topicState.isAddButtonVisible) {
            updateTopicState { it.copy(isAddButtonVisible = true) }
        }
        settingsRepository.saveMood(Constants.KEY_MOOD_SETTINGS, updatedMood.title)
    }

    private fun saveTopicSettings() {
        val topicsId = currentState.topicState.currentTopics.map { it.id }
        settingsRepository.saveTopics(Constants.KEY_TOPIC_SETTINGS, topicsId)
    }

    private fun updateTopicState(update: (SettingsUiState.TopicState) -> SettingsUiState.TopicState) {
        updateState { it.copy(topicState = update(it.topicState)) }
    }
}