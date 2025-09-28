package com.frcoding.audionotes.presentation.screens.home

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.frcoding.audionotes.domain.audio.AudioPlayer
import com.frcoding.audionotes.domain.audio.AudioRecorder
import com.frcoding.audionotes.domain.entity.Entry
import com.frcoding.audionotes.domain.entity.MoodType
import com.frcoding.audionotes.domain.repository.EntryRepository
import com.frcoding.audionotes.presentation.core.base.BaseViewModel
import com.frcoding.audionotes.presentation.core.state.PlayerState
import com.frcoding.audionotes.presentation.core.utils.toMoodType
import com.frcoding.audionotes.presentation.core.utils.toMoodUiModel
import com.frcoding.audionotes.presentation.screens.home.handling.HomeActionEvent
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction
import com.frcoding.audionotes.presentation.screens.home.HomeUiState.EntryHolderState
import com.frcoding.audionotes.presentation.screens.home.HomeUiState.FilterState
import com.frcoding.audionotes.utils.InstantFormatter
import com.frcoding.audionotes.utils.StopWatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

private typealias HomeBaseViewModel = BaseViewModel<HomeUiState, HomeUiAction, HomeActionEvent>

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val audioPlayer: AudioPlayer,
    private val audioRecorder: AudioRecorder
) : HomeBaseViewModel() {
    override val initialState: HomeUiState
        get() = HomeUiState()

    private val stopWatch = StopWatch()

    private var stopWatchJob: Job? = null

    private val moodFiltersChecked = MutableStateFlow<List<HomeUiState.FilterState.FilterItem>>(emptyList())
    private val topicFilterChecked = MutableStateFlow<List<HomeUiState.FilterState.FilterItem>>(emptyList())

    private val filteredEntries = MutableStateFlow<Map<Instant, List<EntryHolderState>>?>(emptyMap())
    private var fetchedEntries: Map<Instant, List<EntryHolderState>> = emptyMap()

    private var playingEntryId = MutableStateFlow<Long?>(null)

    init {
        observeEntries()
        observeFilters()
        setupAudioPlayerListeners()
        observeAudioPlayerCurrentPosition()
    }

    override fun onUiAction(uiAction: HomeUiAction) {
        when (uiAction) {
            HomeUiAction.ActionButtonStartRecording -> startRecording()

            is HomeUiAction.ActionButtonStopRecording -> stopRecording(uiAction.saveFile)

            is HomeUiAction.EntryPauseClick -> pauseEntry(uiAction.entryId)

            is HomeUiAction.EntryPlayClick -> playEntry(uiAction.entryId)

            is HomeUiAction.EntryResumeClick -> resumeEntry(uiAction.entryId)

            is HomeUiAction.MoodFilterItemClicked -> toggleMoodItemCheckedState(uiAction.title)

            HomeUiAction.MoodsFilterClearClicked -> clearMoodFilter()

            HomeUiAction.MoodsFilterToggled -> toggleMoodFilter()

            HomeUiAction.PauseRecording -> pauseRecording()

            HomeUiAction.ResumeRecording -> resumeRecording()

            HomeUiAction.StartRecording -> {
                toggleSheetState()
                startRecording()
            }

            is HomeUiAction.StopRecording -> {
                toggleSheetState()
                stopRecording(uiAction.saveFile)
            }

            is HomeUiAction.TopicFilterItemClicked -> toggleTopicItemCheckedState(uiAction.title)

            HomeUiAction.TopicsFilterToggled -> toggleTopicFilter()

            HomeUiAction.TopicsFilterClearClicked -> clearTopicFilter()
        }
    }


    private fun observeEntries() {
        var isFirstLoad = true

        entryRepository.getEntries()
            .combine(filteredEntries) { dataEntries, currentFilteredEntries ->
                val topics = mutableSetOf<String>()

                val sortedEntries = if (currentFilteredEntries != null && currentFilteredEntries.isEmpty()) {
                    fetchedEntries = groupEntriesByDate(dataEntries, topics)
                    fetchedEntries
                } else currentFilteredEntries ?: emptyMap()

                val updatedTopicFilterItems = addNewTopicFilterItems(topics.toList())
                updateState {
                    it.copy(
                        entries = sortedEntries,
                        filterState = currentState.filterState.copy(topicFilterItem = updatedTopicFilterItems)
                    )
                }

                if (isFirstLoad) {
                    sendActionEvent(HomeActionEvent.DataLoaded)
                    isFirstLoad = false
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeFilters() {
        combine(moodFiltersChecked, topicFilterChecked) { moodFilters, topicFilters ->
            val moodTypes = moodFilters.map { it.title.toMoodUiModel().toMoodType() }
            val topicTitles = topicFilters.map { it.title }
            val isFilterActive = moodFilters.isNotEmpty() || topicFilters.isNotEmpty()

            filteredEntries.value = if (isFilterActive) {
                getFilteredEntries(fetchedEntries, moodTypes, topicTitles)
            } else emptyMap()

            updateState { it.copy(isFilterActive = isFilterActive) }
        }.launchIn(viewModelScope)
    }

    private fun setupAudioPlayerListeners() {
        audioPlayer.setOnCompleteListener {
            playingEntryId.value?.let { entryId ->
                updatePlayerStateAction(entryId, PlayerState.Action.Initializing)
                audioPlayer.stop()
            }
        }
    }

    private fun observeAudioPlayerCurrentPosition() {
        launch {
            audioPlayer.currentPositionFlow.collect { positionMillis ->
                val currentPositionText =
                    InstantFormatter.formatMillisToTime(positionMillis.toLong())
                playingEntryId.value?.let { entryId ->
                    updatePlayerStateCurrentPosition(
                        entryId = entryId,
                        currentPosition = positionMillis,
                        currentPositionText = currentPositionText
                    )
                }
            }
        }
    }

    private fun toggleMoodFilter() {
        val updatedFilterState = currentState.filterState.copy(
            isMoodsOpen = !currentState.filterState.isMoodsOpen,
            isTopicsOpen = false
        )
        updateState { it.copy(filterState = updatedFilterState) }
    }

    private fun clearMoodFilter() {
        moodFiltersChecked.value = emptyList()
        val updatedMoodItems = currentState.filterState.moodFilterItems.map {
            if (it.isChecked) it.copy(isChecked = false) else it
        }

        updateMoodFilterItems(updatedMoodItems, false)
    }

    private fun toggleMoodItemCheckedState(title: String) {
        val updatedMoodItems = currentState.filterState.moodFilterItems.map {
            if (it.title == title) it.copy(isChecked = !it.isChecked) else it
        }

        moodFiltersChecked.value = updatedMoodItems.filter { it.isChecked }
        updateMoodFilterItems(updatedMoodItems)
    }

    private fun toggleTopicFilter() {
        val updatedFilterState = currentState.filterState.copy(
            isTopicsOpen = !currentState.filterState.isTopicsOpen,
            isMoodsOpen = false
        )
        updateState { it.copy(filterState = updatedFilterState) }
    }

    private fun clearTopicFilter() {
        topicFilterChecked.value = emptyList()
        val updatedTopicItems = currentState.filterState.topicFilterItem.map {
            if (it.isChecked) it.copy(isChecked = false) else it
        }

        updateTopicFilterItems(updatedTopicItems, false)
    }

    private fun toggleTopicItemCheckedState(title: String) {
        val updatedTopicItems = currentState.filterState.topicFilterItem.map {
            if (it.title == title) it.copy(isChecked = !it.isChecked) else it
        }

        topicFilterChecked.value = updatedTopicItems.filter { it.isChecked }
        updateTopicFilterItems(updatedTopicItems)
    }

    private fun startRecording() {
        audioRecorder.start()
        stopWatch.start()
        stopWatchJob = launch {
            stopWatch.formattedTime.collect {
                val updatedSheetState = currentState.homeSheetState.copy(recordingTime = it)
                updateHomeSheetState(updatedSheetState)
            }
        }
    }

    private fun pauseRecording() {
        audioRecorder.pause()
        stopWatch.pause()
        toggleRecordingState()
    }

    private fun resumeRecording() {
        audioRecorder.resume()
        stopWatch.start()
        toggleRecordingState()
    }

    private fun toggleSheetState() {
        val updatedSheetState =
            currentState.homeSheetState.copy(
                isVisible = !currentState.homeSheetState.isVisible,
                isRecording = true
            )

        updateHomeSheetState(updatedSheetState)
    }

    private fun stopRecording(saveFile: Boolean) {
        val audioFilePath = audioRecorder.stop(saveFile)
        stopWatch.reset()
        stopWatchJob?.cancel()

        if (saveFile) {
            val amplitudeLogFilePath = audioRecorder.getAmplitudeLogFilePath()
            stopEntriesPlaying()
            sendActionEvent(
                HomeActionEvent.NavigateToEntryScreen(
                    Uri.encode(audioFilePath) ?: "",
                    Uri.encode(amplitudeLogFilePath) ?: ""
                )
            )
        }
    }

    private fun playEntry(entryId: Long) {
        if (audioPlayer.isPlaying()) {
            stopEntriesPlaying()
            audioPlayer.stop()
        }

        playingEntryId.value = entryId
        updatePlayerStateAction(entryId, PlayerState.Action.Playing)

        val audioFilePath = getCurrentEntryHolderState(entryId).entry.audioFilePath
        audioPlayer.initializeFile(audioFilePath)
        audioPlayer.play()
    }

    private fun pauseEntry(entryId: Long) {
        updatePlayerStateAction(entryId, PlayerState.Action.Paused)
        audioPlayer.pause()
    }

    private fun resumeEntry(entryId: Long) {
        updatePlayerStateAction(entryId, PlayerState.Action.Resumed)
        audioPlayer.resume()
    }

    private fun stopEntriesPlaying() {
        val updatedEntries = currentState.entries.mapValues { (_, entryList) ->
            entryList.map { entryHolderState ->
                if (entryHolderState.playerState.action == PlayerState.Action.Playing
                    || entryHolderState.playerState.action == PlayerState.Action.Paused
                ) {
                    val updatedPlayerState =
                        entryHolderState.playerState.copy(
                            action = PlayerState.Action.Initializing,
                            currentPosition = 0,
                            currentPositionText = "00:00"
                        )
                    entryHolderState.copy(playerState = updatedPlayerState)
                } else entryHolderState
            }
        }
        updateState { it.copy(entries = updatedEntries) }
    }

    private fun toggleRecordingState() {
        val updatedSheetState =
            currentState.homeSheetState.copy(isRecording = !currentState.homeSheetState.isRecording)
        updateHomeSheetState(updatedSheetState)
    }

    private fun updateHomeSheetState(updatedHomeSheetState: HomeUiState.HomeSheetState) {
        updateState { it.copy(homeSheetState = updatedHomeSheetState) }
    }

    private fun updateTopicFilterItems(
        updatedItems: List<FilterState.FilterItem>,
        isOpen: Boolean = true
    ) {
        updateState {
            it.copy(
                filterState = currentState.filterState.copy(
                    topicFilterItem = updatedItems,
                    isTopicsOpen = isOpen
                )
            )
        }
    }

    private fun updateMoodFilterItems(
        updatedItems: List<FilterState.FilterItem>,
        isOpen: Boolean = true
    ) {
        updateState {
            it.copy(
                filterState = currentState.filterState.copy(
                    moodFilterItems = updatedItems,
                    isMoodsOpen = isOpen
                )
            )
        }
    }

    private fun updatePlayerStateCurrentPosition(
        entryId: Long,
        currentPosition: Int,
        currentPositionText: String
    ) {
        val entryHolderState = getCurrentEntryHolderState(entryId)
        val updatedPlayerState = entryHolderState.playerState.copy(
            currentPosition = currentPosition,
            currentPositionText = currentPositionText
        )
        updatePlayerState(entryId, updatedPlayerState)
    }

    private fun updatePlayerState(entryId: Long, newPlayerState: PlayerState) {
        val updatedEntries = currentState.entries.mapValues { (_, entryList) ->
            entryList.map { entryHolderState ->
                if (entryHolderState.entry.id == entryId) {
                    entryHolderState.copy(playerState = newPlayerState)
                } else entryHolderState
            }
        }
        updateState { it.copy(entries = updatedEntries) }
    }

    private fun getCurrentEntryHolderState(entryId: Long): EntryHolderState {
        return currentState.entries.values
            .flatten()
            .find { it.entry.id == entryId }
            ?: throw IllegalArgumentException("Audio file path not found for entry ID: $entryId")
    }

    private fun updatePlayerStateAction(entryId: Long, action: PlayerState.Action) {
        val entryHolderState = getCurrentEntryHolderState(entryId)
        val updatedPlayerState = entryHolderState.playerState.copy(action =  action)
        updatePlayerState(entryId, updatedPlayerState)
    }

    private fun getFilteredEntries(
        entries: Map<Instant, List<EntryHolderState>>,
        moodFilters: List<MoodType>,
        topicFilters: List<String>
    ): Map<Instant, List<EntryHolderState>>? {
        return entries.mapValues { (_, entryList) ->
            entryList.filter { entryHolderState ->
                val entry = entryHolderState.entry
                entry.moodType in moodFilters || entry.topics.any { it in topicFilters }
            }
        }.filterValues { it.isNotEmpty() }.ifEmpty { null }
    }

    private fun addNewTopicFilterItems(topics: List<String>): List<FilterState.FilterItem> {
        val currentTopics = currentState.filterState.topicFilterItem.map { it.title }
        val newTopicItems = currentState.filterState.topicFilterItem.toMutableList()
        topics.forEach { topic ->
            if (!currentTopics.contains(topic)) {
                newTopicItems.add(FilterState.FilterItem(topic))
            }
        }
        return newTopicItems
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupEntriesByDate(
        entries: List<Entry>,
        topics: MutableSet<String>
    ): Map<Instant, List<EntryHolderState>> {
        return entries.groupBy { entry ->
            entry.topics.forEach { topic ->
                if (!topics.contains(topic)) topics.add(topic)
            }
            entry.creationTimestamp
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
        }.mapValues { (_, entryList) ->
            entryList.map { EntryHolderState(it) }
        }
    }

}