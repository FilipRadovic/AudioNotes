package com.frcoding.audionotes.presentation.core.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.R
import com.frcoding.audionotes.presentation.core.state.PlayerState
import com.frcoding.audionotes.presentation.core.utils.MoodColor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoodPlayer(
    moodColor: MoodColor,
    playerState: PlayerState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        color = moodColor.background
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .clickable{
                        when (playerState.action) {
                            PlayerState.Action.Initializing -> onPlayClick()
                            PlayerState.Action.Paused -> onResumeClick()
                            PlayerState.Action.Playing -> onPauseClick()
                            PlayerState.Action.Resumed -> onPauseClick()
                        }
                    },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = when (playerState.action) {
                        PlayerState.Action.Initializing -> Icons.Default.PlayArrow
                        PlayerState.Action.Paused -> Icons.Default.PlayArrow
                        //TODO change icons for playing and resumed
                        PlayerState.Action.Playing -> Icons.Filled.Refresh
                        PlayerState.Action.Resumed -> Icons.Filled.Refresh
                    },
                    contentDescription = stringResource(R.string.play_button),
                    modifier = Modifier.padding(4.dp),
                    tint = moodColor.button
                )

                AudioWaveform(
                    amplitudeLogFilePath = playerState.amplitudeLogFilePath,
                    playbackPosition = playerState.currentPosition,
                    totalDuration = playerState.duration,
                    colorPlayed = moodColor.button,
                    colorRemaining = moodColor.track,
                    modifier = Modifier.weight(1f)
                )

                PlayerTimer(
                    duration = playerState.durationText,
                    currentPosition = playerState.currentPositionText,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerTimer(
    duration: String,
    currentPosition: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val placeholderText = if (duration.length > 5) "00:00:00" else "00:00"

        Box(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Text(
                text = currentPosition,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = placeholderText,
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Transparent)
            )
        }

        Box(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Text(
                text = "/$duration",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "/$placeholderText",
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Transparent)
            )
        }
    }
}