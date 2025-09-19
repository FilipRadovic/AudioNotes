package com.frcoding.audionotes.presentation.core.base

import androidx.lifecycle.ViewModel
import com.frcoding.audionotes.presentation.core.base.handling.ActionEvent
import com.frcoding.audionotes.presentation.core.base.handling.UiAction
import com.frcoding.audionotes.presentation.core.base.handling.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S: UiState, U: UiAction, A: ActionEvent> : ViewModel() {
    protected abstract val initialState: S

    private val _uiState: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _actionEvent = Channel<A>()
    val actionEvent = _actionEvent.receiveAsFlow()

    protected fun updateState(block: (currentState: S) -> S) {
        _uiState.update(block)
    }

    abstract fun onUiAction(uiAction: U)
}