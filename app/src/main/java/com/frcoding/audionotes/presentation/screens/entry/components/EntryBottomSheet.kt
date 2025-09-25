package com.frcoding.audionotes.presentation.screens.entry.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.R
import com.frcoding.audionotes.presentation.core.components.MoodsRow
import com.frcoding.audionotes.presentation.core.utils.MoodUiModel.Undefined
import com.frcoding.audionotes.presentation.screens.entry.EntryUiState
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryUiAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryBottomSheet(
    entrySheetState: EntryUiState.EntrySheetState,
    onEvent: (EntryUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    val isPrimaryButtonEnabled by remember(entrySheetState.activeMood) {
        derivedStateOf { entrySheetState.activeMood != Undefined }
    }

    if (entrySheetState.isOpen) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(EntryUiAction.BottomSheetClosed) },
            sheetState = sheetState
        ) {
            Column(
                modifier = modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                Text(
                    text = stringResource(R.string.how_are_you_doing),
                    style = MaterialTheme.typography.titleMedium
                )

                MoodsRow(
                    moods = entrySheetState.moods,
                    activeMood = entrySheetState.activeMood,
                    onMoodClick = { onEvent(EntryUiAction.MoodSelected(it)) }
                )

                EntryBottomButtons(
                    primaryButtonText = stringResource(R.string.confirm),
                    onCancelClick = { onEvent(EntryUiAction.BottomSheetClosed) },
                    onConfirmClick = {
                        onEvent(EntryUiAction.SheetConfirmedClicked(entrySheetState.activeMood))
                    },
                    primaryButtonEnabled = isPrimaryButtonEnabled,
                    primaryLeadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isPrimaryButtonEnabled) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                    }
                )
            }
        }
    }
}