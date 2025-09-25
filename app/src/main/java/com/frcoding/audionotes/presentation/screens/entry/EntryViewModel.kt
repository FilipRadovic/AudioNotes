package com.frcoding.audionotes.presentation.screens.entry

import com.frcoding.audionotes.domain.audio.AudioPlayer
import com.frcoding.audionotes.domain.repository.EntryRepository
import com.frcoding.audionotes.domain.repository.TopicRepository
import com.frcoding.audionotes.presentation.core.base.BaseViewModel
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryActionEvent
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryUiAction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

private typealias EntryBaseViewModel = BaseViewModel<EntryUiState, EntryUiAction, EntryActionEvent>

@HiltViewModel(assistedFactory = EntryViewModel.EntryViewModelFactory::class)
class EntryViewModel @AssistedInject constructor(
    @Assisted("audioFilePath") val audioFilePath: String,
    @Assisted("amplitudeLogFilePath") val amplitudeLogFilePath: String,
    @Assisted() entryId: Long,
    private val entryRepository: EntryRepository,
    private val topicRepository: TopicRepository,
    private val audioPlayer: AudioPlayer
): EntryBaseViewModel() {
    override val initialState: EntryUiState
        get() = EntryUiState()

    override fun onUiAction(uiAction: EntryUiAction) {
        TODO("Not yet implemented")
    }


    @AssistedFactory
    interface EntryViewModelFactory {
        fun create(
            @Assisted("audioFilePath") entryFilePath: String,
            @Assisted("audioLogFilePath") amplitudeLogFilePath: String,
            entryId: Long
        ): EntryViewModel
    }

}