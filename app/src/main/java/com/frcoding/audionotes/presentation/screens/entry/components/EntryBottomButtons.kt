package com.frcoding.audionotes.presentation.screens.entry.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.R
import com.frcoding.audionotes.presentation.core.components.PrimaryButton
import com.frcoding.audionotes.presentation.core.components.SecondaryButton

@Composable
fun EntryBottomButtons(
    primaryButtonText: String,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryButtonEnabled: Boolean = true,
    primaryLeadingIcon: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SecondaryButton(
            text = stringResource(R.string.cancel),
            onClick = onCancelClick,
            modifier = Modifier.fillMaxHeight()
        )
        PrimaryButton(
            text = primaryButtonText,
            onClick = onConfirmClick,
            modifier = Modifier.weight(1f),
            enabled = primaryButtonEnabled,
            leadingIcon = primaryLeadingIcon
        )
    }
}