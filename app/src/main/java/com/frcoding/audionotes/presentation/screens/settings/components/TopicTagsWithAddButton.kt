package com.frcoding.audionotes.presentation.screens.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.R
import com.frcoding.audionotes.presentation.core.components.TopicTag
import com.frcoding.audionotes.presentation.core.components.TopicTextField
import com.frcoding.audionotes.presentation.screens.settings.SettingsUiState
import com.frcoding.audionotes.presentation.screens.settings.handling.SettingsUiAction
import com.frcoding.audionotes.presentation.theme.NotesUltraLightGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicTagsWithAddButton(
    topicState: SettingsUiState.TopicState,
    onUiAction: (SettingsUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        topicState.currentTopics.forEach { topic ->
            TopicTag(
                topic = topic,
                onClearClick = { onUiAction(SettingsUiAction.TagClearClicked(topic)) }
            )
        }

        if (topicState.isAddButtonVisible) {
            TopicAddButton(
                onClick = { onUiAction(SettingsUiAction.AddButtonVisibleToggled) }
            )
        } else {
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(true) {
                focusRequester.requestFocus()
            }

            TopicTextField(
                value = topicState.topicValue,
                onValueChange = { onUiAction(SettingsUiAction.TopicValueChanged(it)) },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                ,
                hintText = "",
                showLeadingIcon = false
            )
        }
    }
}

@Composable
private fun TopicAddButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .size(32.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = NotesUltraLightGray,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Icon(
            modifier = Modifier.padding(4.dp),
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_default_topic)
        )
    }
}