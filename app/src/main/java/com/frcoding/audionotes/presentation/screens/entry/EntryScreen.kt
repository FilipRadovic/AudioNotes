package com.frcoding.audionotes.presentation.screens.entry

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frcoding.audionotes.R
import com.frcoding.audionotes.navigation.NavigationState
import com.frcoding.audionotes.presentation.core.base.BaseContentLayout
import com.frcoding.audionotes.presentation.core.components.AppTopBar
import com.frcoding.audionotes.presentation.core.components.MoodPlayer
import com.frcoding.audionotes.presentation.core.components.TopicDropdown
import com.frcoding.audionotes.presentation.core.utils.toDp
import com.frcoding.audionotes.presentation.core.utils.toInt
import com.frcoding.audionotes.presentation.screens.entry.components.EntryBottomSheet
import com.frcoding.audionotes.presentation.screens.entry.components.EntryBottomButtons
import com.frcoding.audionotes.presentation.screens.entry.components.EntryTextField
import com.frcoding.audionotes.presentation.screens.entry.components.LeaveDialog
import com.frcoding.audionotes.presentation.screens.entry.components.MoodChooseButton
import com.frcoding.audionotes.presentation.screens.entry.components.TopicTagsRow
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryActionEvent
import com.frcoding.audionotes.presentation.screens.entry.handling.EntryUiAction

@RequiresApi(Build.VERSION_CODES.O)
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
            onUiEvent = viewModel::onUiAction,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EntryScreen(
    uiState: EntryUiState,
    onUiEvent: (EntryUiAction) -> Unit
) {
    Box {
        var topicOffest by remember { mutableStateOf(IntOffset.Zero) }
        val verticalSpace = 16.dp.toInt()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(verticalSpace.toDp())
        ) {
            EntryTextField(
                value = uiState.titleValue,
                onValueChange = { onUiEvent(EntryUiAction.TitleValueChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                hintText = stringResource(R.string.add_title),
                leadingIcon = {
                    MoodChooseButton(
                        mood = uiState.currentMood,
                        onClick = { onUiEvent(EntryUiAction.BottomSheetOpened(uiState.currentMood)) }
                    )
                },
                textStyle = MaterialTheme.typography.titleMedium,
                iconSpacing = 6.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MoodPlayer(
                    moodColor = uiState.currentMood.moodColor,
                    playerState = uiState.playerState,
                    onPlayClick = { onUiEvent(EntryUiAction.PlayClicked) },
                    onPauseClick = { onUiEvent(EntryUiAction.PauseClicked) },
                    onResumeClick = { onUiEvent(EntryUiAction.ResumeClicked) },
                    modifier = Modifier.height(44.dp).weight(1f)
                )
            }

            TopicTagsRow(
                value = uiState.topicValue,
                onValueChange = { onUiEvent(EntryUiAction.TopicValueChanged(it)) },
                topics = uiState.currentTopics,
                onTagClearClick = { onUiEvent(EntryUiAction.TagClearClicked(it)) },
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        topicOffest = IntOffset(
                            coordinates.positionInParent().x.toInt(),
                            coordinates.positionInParent().y.toInt() + coordinates.size.height + verticalSpace
                        )
                    }
                    .onFocusChanged {
                        onUiEvent(EntryUiAction.TopicValueChanged(""))
                    }
            )

            EntryTextField(
                value = uiState.descriptionValue,
                onValueChange = { onUiEvent(EntryUiAction.DescriptionValueChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                hintText = stringResource(R.string.add_description),
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                },
                singleLine = false
            )

        }

        TopicDropdown(
            searchQuery = uiState.topicValue,
            topics = uiState.foundTopics,
            onTopicClick = { onUiEvent(EntryUiAction.TopicClicked(it)) },
            onCreateClick = { onUiEvent(EntryUiAction.CreateTopicClicked) },
            startOffset = topicOffest
        )
    }
}