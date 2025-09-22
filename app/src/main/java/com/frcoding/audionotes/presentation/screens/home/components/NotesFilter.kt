package com.frcoding.audionotes.presentation.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.R
import com.frcoding.audionotes.presentation.screens.home.HomeUiState
import com.frcoding.audionotes.presentation.screens.home.handling.HomeUiAction
import com.frcoding.audionotes.presentation.screens.home.HomeUiState.FilterState
import com.frcoding.audionotes.R.string.all_moods
import com.frcoding.audionotes.R.string.clear_filter
import com.frcoding.audionotes.presentation.core.utils.toMoodUiModel

@Composable
fun NotesFilter(
    filterState: HomeUiState.FilterState,
    onUiAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                FilterChip(
                    defaultTitle = stringResource(R.string.all_moods),
                    filterItems = filterState.moodFilterItems,
                    isFilterSelected = filterState.isMoodsOpen,
                    onClick = { onUiAction(HomeUiAction.MoodsFilterToggled) },
                    onClearClick = { onUiAction(HomeUiAction.MoodsFilterClearClicked) },
                    leadingIcon = {
                        if (filterState.moodFilterItems.isNotEmpty()) {
                            SelectedMoodIcons(
                                moodFilterItems = filterState.moodFilterItems
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    defaultTitle: String,
    filterItems: List<FilterState.FilterItem>,
    isFilterSelected: Boolean,
    onClick: () -> Unit,
    onClearClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = when {
                isFilterSelected -> MaterialTheme.colorScheme.surface
                filterItems.isSomeMoodSelected() -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isFilterSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        label = {
            Text(
                text = getFormattedFilterTitle(defaultTitle, filterItems),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            )
        },
        trailingIcon = {
            if (filterItems.isSomeMoodSelected()) {
                IconButton(
                    modifier = Modifier.size(18.dp),
                    onClick = { onClearClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_filter),
                        tint = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon
    )
}

@Composable
fun SelectedMoodIcons(
    moodFilterItems: List<FilterState.FilterItem>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(-4.dp)
    ) {
        moodFilterItems.forEach { filterItem ->
            if (filterItem.isChecked) {
                val mood = filterItem.title.toMoodUiModel()
                Image(
                    modifier = Modifier.height(22.dp),
                    painter = painterResource(mood.moodIcons.stroke),
                    contentDescription = null
                )
            }
        }
    }
}

private fun getFormattedFilterTitle(
    defaultTitle: String,
    filterItems: List<FilterState.FilterItem>
): String {
    val pickedItems = filterItems
        .filter { it.isChecked }
        .map { it.title }

    return when {
        pickedItems.isEmpty() -> defaultTitle
        pickedItems.size == 1 -> pickedItems.first()
        pickedItems.size == 2 -> pickedItems.joinToString( ", " )
        else -> {
            val firstTwo = pickedItems.take(2).joinToString(", ")
            "$firstTwo + ${pickedItems.size - 2}"
        }
    }
}

private fun List<FilterState.FilterItem>.isSomeMoodSelected() = this.any{ it.isChecked }