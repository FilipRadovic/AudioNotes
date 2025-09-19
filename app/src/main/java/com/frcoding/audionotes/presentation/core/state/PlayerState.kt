package com.frcoding.audionotes.presentation.core.state

import androidx.compose.runtime.Stable

@Stable
data class PlayerState(
    val duration: Int = 0,
    val currentPosition: Int = 0,
    val currentPositionText: String = "00:00",
    val action: Action = Action.Initializing,
    val amplitudeLogFilePath: String = ""
) {

    sealed interface Action {
        data object Initializing : Action
        data object Playing : Action
        data object Paused: Action
        data object Resumed : Action
    }
}