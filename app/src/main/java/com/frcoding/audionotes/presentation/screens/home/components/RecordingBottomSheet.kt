package com.frcoding.audionotes.presentation.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.R
import com.frcoding.audionotes.presentation.core.utils.GradientScheme
import com.frcoding.audionotes.presentation.screens.home.HomeUiState
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingBottomSheet(
    homeSheetState: HomeUiState.HomeSheetState,
    onUiAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    if (homeSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { onUiAction(HomeUiAction.StopRecording(saveFile = false)) },
            sheetState = sheetState
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetHeader(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isRecording = homeSheetState.isRecording,
                    recordingTime = homeSheetState.recordingTime
                )

                RecordButtons(
                    onCancelClick = {
                        onUiAction(HomeUiAction.StopRecording(saveFile = false))
                    },
                    onPauseClick = {
                        if (homeSheetState.isRecording) {
                            onUiAction(HomeUiAction.PauseRecording)
                        } else {
                            onUiAction(HomeUiAction.StopRecording(saveFile = true))
                        }
                    },
                    onRecordClick = {
                        if (homeSheetState.isRecording) {
                            onUiAction(HomeUiAction.StopRecording(saveFile = true))
                        } else {
                            onUiAction(HomeUiAction.ResumeRecording)
                        }
                    },
                    isRecording = homeSheetState.isRecording,
                    modifier = Modifier.padding(vertical = 42.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun BottomSheetHeader(
    modifier: Modifier = Modifier,
    isRecording: Boolean,
    recordingTime: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (isRecording) {
                stringResource(R.string.recording_your_memories)
            } else {
                stringResource(R.string.recording_paused)
            },
            style = MaterialTheme.typography.titleSmall
        )

        Box(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
           Text(
               text = if (recordingTime.length > 5) recordingTime else "00:$recordingTime",
               style = MaterialTheme.typography.labelMedium
           )

            Text(
                text = "00:00:00",
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Transparent)
            )
        }
    }
}

@Composable
fun RecordButtons(
    onCancelClick: () -> Unit,
    onPauseClick: () -> Unit,
    onRecordClick: () -> Unit,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = modifier.size(48.dp),
            onClick = onCancelClick,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.cancel_recording)
            )
        }

        Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        brush = GradientScheme.PrimaryGradient,
                        shape = CircleShape
                    )
                    .clickable{ onRecordClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = if (isRecording) {
                        painterResource(R.drawable.icon_play_mic)
                    } else {
                        painterResource(R.drawable.icon_mic)
                    },
                    contentDescription = stringResource(R.string.recording_button)
                )
            }

            if (isRecording) {
                ButtonPulsatingCircle()
            }
        }

        IconButton(
            modifier = modifier.size(48.dp),
            onClick = onPauseClick,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primary
            )

        ) {
            Image(
                painter = if (isRecording){
                    painterResource(R.drawable.icon_play)
                } else {
                    painterResource(R.drawable.icon_pause)
                },
                contentDescription = stringResource(R.string.pause_button)
            )
        }

    }
}