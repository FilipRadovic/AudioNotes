package com.frcoding.audionotes.presentation.screens.entry

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.frcoding.audionotes.domain.audio.AudioPlayer
import com.frcoding.audionotes.domain.entity.Entry
import com.frcoding.audionotes.domain.entity.Topic
import com.frcoding.audionotes.domain.repository.EntryRepository
import com.frcoding.audionotes.domain.repository.SettingsRepository
import com.frcoding.audionotes.domain.repository.TopicRepository
import com.frcoding.audionotes.presentation.core.base.BaseViewModel
import com.frcoding.audionotes.presentation.core.state.PlayerState
import com.frcoding.audionotes.presentation.core.utils.MoodUiModel
import com.frcoding.audionotes.presentation.core.utils.toMoodType
import com.frcoding.audionotes.presentation.core.utils.toMoodUiModel
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryActionEvent
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryUiAction
import com.frcoding.audionotes.utils.Constants
import com.frcoding.audionotes.utils.InstantFormatter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.io.File
import java.lang.IllegalStateException

private typealias EntryBaseViewModel = BaseViewModel<EntryUiState, EntryUiAction, EntryActionEvent>

@OptIn(ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel(assistedFactory = EntryViewModel.EntryViewModelFactory::class)
class EntryViewModel @AssistedInject constructor(
    @Assisted("audioFilePath") val audioFilePath: String,
    @Assisted("amplitudeLogFilePath") val amplitudeLogFilePath: String,
    @Assisted() entryId: Long,
    private val entryRepository: EntryRepository,
    private val topicRepository: TopicRepository,
    private val audioPlayer: AudioPlayer,
    settingsRepository: SettingsRepository
): EntryBaseViewModel() {
    override val initialState: EntryUiState
        get() = EntryUiState()

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
        initializeAudioPlayer()
        setupDefaultSettings()
        subscribeToTopicSearchResults()
        setupAudioPlayerListeners()
        observeAudioPlayerCurrentPosition()
    }


    override fun onUiAction(uiAction: EntryUiAction) {
        when (uiAction) {
            EntryUiAction.BottomSheetClosed -> toggleSheetState()
            is EntryUiAction.BottomSheetOpened -> toggleSheetState(uiAction.mood)
            is EntryUiAction.SheetConfirmedClicked -> setCurrentMood(uiAction.mood)

            is EntryUiAction.MoodSelected -> updateActiveMood(uiAction.mood)
            is EntryUiAction.TitleValueChanged -> updateState { it.copy(titleValue = uiAction.value) }
            is EntryUiAction.DescriptionValueChanged -> updateState { it.copy(descriptionValue = uiAction.value) }

            is EntryUiAction.TopicValueChanged -> updateTopic(uiAction.value)
            is EntryUiAction.TagClearClicked -> updateState {
                it.copy(currentTopics = currentState.currentTopics - uiAction.topic)
            }

            is EntryUiAction.TopicClicked -> updateCurrentTopics(uiAction.topic)
            EntryUiAction.CreateTopicClicked -> addNewTopic()

            EntryUiAction.PlayClicked -> playAudio()
            EntryUiAction.PauseClicked -> pauseAudio()
            EntryUiAction.ResumeClicked -> resumeAudio()

            is EntryUiAction.SaveButtonClicked -> saveEntry(uiAction.outputDir)

            EntryUiAction.LeaveDialogToggled -> toggleLeaveDialog()
            EntryUiAction.LeaveDialogConfirmClicked -> {
                toggleLeaveDialog()
                audioPlayer.stop()
                sendActionEvent(EntryActionEvent.NavigateBack)
            }
        }
    }

    private fun toggleSheetState(activeMood: MoodUiModel = MoodUiModel.Undefined) {
        updateSheetState {
            it.copy(
                isOpen = !it.isOpen,
                activeMood = activeMood
            )
        }
    }

    private fun updateSheetState(update: (EntryUiState.EntrySheetState) -> EntryUiState.EntrySheetState) {
        updateState { it.copy(entrySheetState = update(it.entrySheetState)) }
    }

    private fun updateActiveMood(mood: MoodUiModel) {
        updateState {
            it.copy(
                entrySheetState = currentState.entrySheetState.copy(activeMood = mood)
            )
        }
    }

    private fun setCurrentMood(mood: MoodUiModel) {
        updateState { it.copy(currentMood = mood) }
        toggleSheetState()
    }

    private fun updateTopic(topic: String) {
        updateState { it.copy(topicValue = topic) }
        searchQuery.value = topic
    }

    private fun updateCurrentTopics(newTopic: Topic) {
        updateState {
            it.copy(
                currentTopics = currentState.currentTopics + newTopic,
                topicValue = ""
            )
        }
    }

    private fun addNewTopic() {
        val newTopic = Topic(name = currentState.topicValue)
        updateCurrentTopics(newTopic)
        launch {
            topicRepository.insertTopic(newTopic)
        }
    }

    private fun playAudio() {
        updatePlayerStateAction(PlayerState.Action.Playing)
        audioPlayer.play()
    }

    private fun pauseAudio() {
        updatePlayerStateAction(PlayerState.Action.Paused)
        audioPlayer.pause()
    }

    private fun resumeAudio() {
        updatePlayerStateAction(PlayerState.Action.Resumed)
        audioPlayer.resume()
    }

    private fun updatePlayerStateAction(action: PlayerState.Action) {
        val updatedPlayerState = currentState.playerState.copy(action = action)
        updateState { it.copy(playerState = updatedPlayerState) }
    }

    private fun saveEntry(outputDir: File) {
        val newAudioFilePath = renameFile(outputDir, audioFilePath, "audio")
        val newAmplitudeLogFilePath = renameFile(outputDir, amplitudeLogFilePath, "amplitude")
        val topics = currentState.currentTopics.map { it.name }

        val newEntry = Entry(
            title = currentState.titleValue,
            moodType = currentState.currentMood.toMoodType(),
            audioFilePath = newAudioFilePath,
            audioDuration = currentState.playerState.duration,
            amplitudeLogFilePath = newAmplitudeLogFilePath,
            description = currentState.descriptionValue,
            topics = topics
        )

        launch {
            entryRepository.upsertEntry(newEntry)
            sendActionEvent(EntryActionEvent.NavigateBack)
        }
    }

    private fun toggleLeaveDialog() {
        updateState { it.copy(showLeaveDialog = !currentState.showLeaveDialog) }
    }

    private fun renameFile(outputDir: File, filePath: String, newValue: String): String {
        val file = File(filePath)
        val newFileName = file.name.replace("temp", newValue)
        val newFile = File(outputDir, newFileName)
        val isRenamed = file.renameTo(newFile)

        return if (isRenamed) newFile.absolutePath else throw IllegalStateException("Failed to rename ${file.name}.")
    }

    private fun initializeAudioPlayer() {
        audioPlayer.initializeFile((audioFilePath))
        updateState {
            it.copy(
                playerState = currentState.playerState.copy(
                    duration = audioPlayer.getDuration(),
                    amplitudeLogFilePath = amplitudeLogFilePath
                )
            )
        }
    }

    private fun setupDefaultSettings() {
        launch {
            val defaultTopics = topicRepository.getTopicsByIdList(defaultTopicsId)
            updateState {
                it.copy(
                    entrySheetState = currentState.entrySheetState.copy(
                        activeMood = defaultMood.toMoodUiModel()
                    ),
                    currentTopics = defaultTopics
                )
            }
        }
    }

    private fun subscribeToTopicSearchResults() {
        launch {
            searchResults.collect {
                updateState { it.copy(foundTopics = searchResults.value) }
            }
        }
    }

    private fun setupAudioPlayerListeners() {
        audioPlayer.setOnCompleteListener {
            updatePlayerStateAction(PlayerState.Action.Initializing)
        }
    }

    private fun observeAudioPlayerCurrentPosition() {
        launch {
            audioPlayer.currentPositionFlow.collect { positionMillis ->
                val currentPositionText =
                    InstantFormatter.formatMillisToTime(positionMillis.toLong())

                updateState {
                    it.copy(
                        playerState = currentState.playerState.copy(
                            currentPosition = positionMillis,
                            currentPositionText = currentPositionText
                        )
                    )
                }
            }
        }
    }

    @AssistedFactory
    interface EntryViewModelFactory {
        fun create(
            @Assisted("audioFilePath") entryFilePath: String,
            @Assisted("amplitudeLogFilePath") amplitudeLogFilePath: String,
            entryId: Long
        ): EntryViewModel
    }
}