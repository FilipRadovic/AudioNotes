package com.frcoding.audionotes.presentation.screens.home

import com.frcoding.audionotes.domain.audio.AudioPlayer
import com.frcoding.audionotes.domain.audio.AudioRecorder
import com.frcoding.audionotes.domain.repository.EntryRepository
import com.frcoding.audionotes.presentation.core.base.BaseViewModel
import com.frcoding.audionotes.presentation.screens.home.handling.HomeActionEvent
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction
import javax.inject.Inject

private typealias HomeBaseViewModel = BaseViewModel<HomeUiState, HomeUiAction, HomeActionEvent>

class HomeViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val audioPlayer: AudioPlayer,
    private val audioRecorder: AudioRecorder
) : HomeBaseViewModel() {
    override val initialState: HomeUiState
        get() = HomeUiState()

    override fun onUiAction(uiAction: HomeUiAction) {

    }

}