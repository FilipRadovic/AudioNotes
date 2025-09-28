package com.frcoding.audionotes.presentation.screens.home

import androidx.compose.runtime.Stable
import com.frcoding.audionotes.domain.entity.Entry
import com.frcoding.audionotes.presentation.core.base.handling.UiState
import com.frcoding.audionotes.presentation.core.state.PlayerState
import com.frcoding.audionotes.presentation.core.utils.MoodUiModel
import com.frcoding.audionotes.utils.Constants
import java.time.Instant

@Stable
data class HomeUiState(
    val entries: Map<Instant, List<EntryHolderState>> = mapOf(),
    val filterState: FilterState = FilterState(),
    val isFilterActive: Boolean = false,
    val homeSheetState: HomeSheetState = HomeSheetState(),
    val isPermissionDialogOpen: Boolean = false
) : UiState {

    @Stable
    data class EntryHolderState(
        val entry: Entry,
        val playerState: PlayerState = PlayerState(
            duration = entry.audioDuration,
            amplitudeLogFilePath = entry.amplitudeLogFilePath
        )
    )

    data class HomeSheetState(
        val isVisible: Boolean = false,
        val isRecording: Boolean = true,
        val recordingTime: String = Constants.DEFAULT_FORMATTED_TIME
    )

    @Stable
    data class FilterState(
        val isMoodsOpen: Boolean = false,
        val isTopicsOpen: Boolean = false,
        val moodFilterItems: List<FilterItem> = MoodUiModel.allMoods.map { FilterItem(title = it.title) },
        val topicFilterItem: List<FilterItem> = listOf()
    ) {
        data class FilterItem(
            val title: String = "",
            val isChecked: Boolean = false
        )
    }
}