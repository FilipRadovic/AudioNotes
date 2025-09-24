package com.frcoding.audionotes.presentation.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.frcoding.audionotes.domain.audio.AudioPlayer
import com.frcoding.audionotes.domain.audio.AudioRecorder
import com.frcoding.audionotes.domain.entity.Entry
import com.frcoding.audionotes.domain.entity.MoodType
import com.frcoding.audionotes.domain.repository.EntryRepository
import com.frcoding.audionotes.presentation.core.base.BaseViewModel
import com.frcoding.audionotes.presentation.core.utils.toMoodType
import com.frcoding.audionotes.presentation.core.utils.toMoodUiModel
import com.frcoding.audionotes.presentation.screens.home.handling.HomeActionEvent
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction
import com.frcoding.audionotes.presentation.screens.home.HomeUiState.EntryHolderState
import com.frcoding.audionotes.presentation.screens.home.HomeUiState.FilterState
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
                        filterState = currentState.filterState.copy(topicFilterItems = updatedTopicFilterItems)
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
        }
    }

    /*
    private fun observeFilters() {
        combine(moodFiltersChecked, topicFiltersChecked) { moodFilters, topicFilters ->
            val moodTypes = moodFilters.map { it.title.toMoodUiModel().toMoodType() }
            val topicTitles = topicFilters.map { it.title }
            val isFilterActive = moodFilters.isNotEmpty() || topicFilters.isNotEmpty()

            filteredEntries.value = if (isFilterActive) {
                getFilteredEntries(fetchedEntries, moodTypes, topicTitles)
            } else emptyMap()

            updateState { it.copy(isFilterActive = isFilterActive) }
        }.launchIn(viewModelScope)
    }
     */

    /*
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
     */

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