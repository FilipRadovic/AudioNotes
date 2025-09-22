package com.frcoding.audionotes.presentation.screens.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.presentation.screens.home.HomeUiState
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction
import com.frcoding.audionotes.utils.InstantFormatter
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesEntries(
    entryNotes: Map<Instant, List<HomeUiState.EntryHolderState>>,
    onUiAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        entryNotes.forEach { (instant, entries) ->
            item {
                //DataHeader
                Text(
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                    text = InstantFormatter.formatRelativeToDay(instant),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            items(
                items = entries,
                key = { entryState -> entryState.entry.id }
            ) { entryHolderState ->
                EntryHolder(
                    entryState = entryHolderState,
                    entryPosition = when {
                        entries.size == 1 -> EntryListPosition.Single
                        entryHolderState == entries.first() -> EntryListPosition.First
                        entryHolderState == entries.last() -> EntryListPosition.Last
                        else -> EntryListPosition.Middle
                    },
                    onUiAction = onUiAction
                )

            }
        }
    }
}