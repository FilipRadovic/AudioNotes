package com.frcoding.audionotes.presentation.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                //BottomSheetHeader()
                //RecordButtons()
            }
        }
    }

}