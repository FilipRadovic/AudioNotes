package com.frcoding.audionotes.presentation.screens.home.handling

import com.frcoding.audionotes.presentation.core.base.handling.ActionEvent

sealed interface HomeActionEvent : ActionEvent {
    data class NavigateToEntryScreen(
        val audioFilePath: String,
        val amplitudeFilePath: String
    ) : HomeActionEvent

    data object DataLoaded : HomeActionEvent
}