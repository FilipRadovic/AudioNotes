package com.frcoding.audionotes.presentation.screens.entry

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frcoding.audionotes.R
import com.frcoding.audionotes.navigation.NavigationState
import com.frcoding.audionotes.presentation.core.base.BaseContentLayout
import com.frcoding.audionotes.presentation.core.components.AppTopBar
import com.frcoding.audionotes.presentation.screens.entry.components.EntryBottomSheet
import com.frcoding.audionotes.presentation.screens.entry.components.EntryBottomButtons
import com.frcoding.audionotes.presentation.screens.entry.components.LeaveDialog
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryActionEvent
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryUiAction

@Composable
fun EntryScreenRoot(
    modifier: Modifier = Modifier,
    navigationState: NavigationState,
    audioFilePath: String,
    amplitudeLogFilePath: String,
    entryId: Long
) {
    val viewModel : EntryViewModel =
        hiltViewModel<EntryViewModel, EntryViewModel.EntryViewModelFactory> { factory ->
            factory.create(audioFilePath, amplitudeLogFilePath, entryId)
    }

    BaseContentLayout(
        modifier = modifier,
        viewModel = viewModel,
        actionsEventHandler = { _, actionEvent ->
            when(actionEvent) {
                EntryActionEvent.NavigateBack -> navigationState.popBackStack()
            }
        },
        topBar = {
            AppTopBar(
                title = if (entryId < 0) {
                    stringResource(R.string.new_entry)
                } else {
                    stringResource(R.string.edit_entry)
                },
                onBackClick = { viewModel.onUiAction(EntryUiAction.LeaveDialogToggled) }
            )
        },
        bottomBar = { uiState ->
            val context = LocalContext.current
            EntryBottomButtons(
                primaryButtonText = stringResource(R.string.save),
                onCancelClick = { viewModel.onUiAction(EntryUiAction.LeaveDialogToggled) },
                onConfirmClick = {
                    val outputDir = context.filesDir
                    viewModel.onUiAction(EntryUiAction.SaveButtonClicked(outputDir!!))
                },
                modifier = Modifier.padding(16.dp),
                primaryButtonEnabled = uiState.isSaveButtonEnabled
            )
        },
        onBackPressed = { viewModel.onUiAction(EntryUiAction.LeaveDialogToggled) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { uiState ->
        EntryScreen(
            uiState = uiState,
            onEvent = viewModel::onUiAction
        )
        EntryBottomSheet(
            entrySheetState = uiState.entrySheetState,
            onEvent = viewModel::onUiAction
        )
        if (uiState.showLeaveDialog) {
            LeaveDialog(
                headline = stringResource(R.string.cancel_recording),
                onConfirm = { viewModel.onUiAction(EntryUiAction.LeaveDialogConfirmClicked) },
                onDismissRequest = { viewModel.onUiAction(EntryUiAction.LeaveDialogToggled) },
                supportingText = stringResource(R.string.leave_dialog_supporting_text),
                cancelButtonName = stringResource(R.string.cancel),
                confirmButtonName = stringResource(R.string.leave)
            )
        }
    }
}

@Composable
private fun EntryScreen(
    uiState: EntryUiState,
    onEvent: (EntryUiAction) -> Unit
) {

}