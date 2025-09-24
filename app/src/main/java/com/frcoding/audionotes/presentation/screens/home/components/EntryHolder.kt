package com.frcoding.audionotes.presentation.screens.home.components

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.presentation.core.components.MoodPlayer
import com.frcoding.audionotes.presentation.core.utils.toUiModel
import com.frcoding.audionotes.presentation.screens.home.HomeUiState
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction
import com.frcoding.audionotes.presentation.theme.NotesUltraLightGray
import com.frcoding.audionotes.utils.InstantFormatter

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntryHolder(
    entryState: HomeUiState.EntryHolderState,
    entryPosition: EntryListPosition,
    onUiAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val entry = entryState.entry
    val moodUiModel = entry.moodType.toUiModel()

    Row(
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        var isHolderCollapsed by remember { mutableStateOf(false) }
        var holderHeight by remember { mutableIntStateOf(0) }

        //Mood icon and timeline
        MoodTimeline(
            moodRes = moodUiModel.moodIcons.fill,
            entryPosition = entryPosition,
            isHolderCollapsed = isHolderCollapsed,
            holderHeight = holderHeight,
            modifier = Modifier.fillMaxHeight(),
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    val currentHeight = size.height
                    if (currentHeight != holderHeight) {
                        isHolderCollapsed = currentHeight < holderHeight
                        holderHeight = currentHeight
                    }
                }
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .padding(top = 12.dp, bottom = 14.dp)
            ) {
                EntryHeader(
                    title = entry.title,
                    creationTime = InstantFormatter.formatHoursAndMinutes(entry.creationTimestamp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                MoodPlayer(
                    moodColor = moodUiModel.moodColor,
                    playerState = entryState.playerState,
                    onPlayClick = { onUiAction(HomeUiAction.EntryPlayClick(entry.id)) },
                    onPauseClick = { onUiAction(HomeUiAction.EntryPauseClick(entry.id)) },
                    onResumeClick = { onUiAction(HomeUiAction.EntryResumeClick(entry.id)) },
                )

                if (entry.topics.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        entry.topics.forEach { topic ->
                            TopicChip(
                                title = topic
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MoodTimeline(
    @DrawableRes moodRes: Int,
    entryPosition: EntryListPosition,
    isHolderCollapsed: Boolean,
    holderHeight: Int,
    modifier: Modifier = Modifier,
    iconTopPadding: Dp = 16.dp,
    iconEndPadding: Dp = 12.dp
) {
    var elementHeight by remember { mutableIntStateOf(0) }
    var moodSize by remember { mutableStateOf(IntSize.Zero) }

    val dividerOffsetX by remember { derivedStateOf { moodSize.width / 2 } }

    val middleMoodOffsetY by remember {
        derivedStateOf { moodSize.height / 2 + iconTopPadding.value.toInt() }
    }

    val dividerOffsetY by remember(entryPosition) {
        derivedStateOf {
            if (entryPosition == EntryListPosition.Last ||
                entryPosition == EntryListPosition.Middle
            ) 0 else middleMoodOffsetY
        }
    }

    val dividerHeight by remember(holderHeight, entryPosition) {
        derivedStateOf {
            when (entryPosition) {
                EntryListPosition.First -> elementHeight - middleMoodOffsetY
                EntryListPosition.Last -> middleMoodOffsetY
                EntryListPosition.Middle -> if (isHolderCollapsed) holderHeight else elementHeight
                EntryListPosition.Single -> 0
            }
        }
    }

    Box(
        modifier = modifier.onSizeChanged { elementHeight = it.height }
    ) {
        VerticalDivider(
            modifier = Modifier
                .offset{
                    IntOffset(dividerOffsetX, dividerOffsetY)
                }
                .height(dividerHeight.toDp()),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
        Image(
            modifier = Modifier
                .padding(top = iconTopPadding, end = iconEndPadding)
                .width(32.dp)
                .onSizeChanged{ moodSize = it },
            painter = painterResource(moodRes),
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )
    }
}

@Composable
private fun EntryHeader(
    modifier: Modifier = Modifier,
    title: String,
    creationTime: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = creationTime,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun TopicChip(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(22.dp)
            .clip(CircleShape)
            .background(
                color = NotesUltraLightGray,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.5f
                        )
                    )
                ) {
                    append("# ")
                }
                append(title)
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun Int.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { toDp() }
}