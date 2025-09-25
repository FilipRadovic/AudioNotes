package com.frcoding.audionotes.presentation.screens.entry.handling

import com.frcoding.audionotes.presentation.core.base.handling.ActionEvent

interface EntryActionEvent: ActionEvent {
    data object NavigateBack : EntryActionEvent
}