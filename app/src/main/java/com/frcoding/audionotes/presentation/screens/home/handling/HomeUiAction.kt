package com.frcoding.audionotes.presentation.screens.home.handling

import com.frcoding.audionotes.presentation.core.base.handling.UiAction

sealed interface HomeUiAction : UiAction {
    data object StartRecording: HomeUiAction
    data object PauseRecording: HomeUiAction
    data object ResumeRecording: HomeUiAction
    data class StopRecording(val saveFile: Boolean) : HomeUiAction


    data object MoodsFilterToggled : HomeUiAction
    data object MoodsFilterClearClicked : HomeUiAction

}