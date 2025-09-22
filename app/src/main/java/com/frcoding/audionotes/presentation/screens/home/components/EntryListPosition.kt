package com.frcoding.audionotes.presentation.screens.home.components

sealed interface EntryListPosition {
    data object Single: EntryListPosition
    data object First: EntryListPosition
    data object Last: EntryListPosition
    data object Middle: EntryListPosition
}