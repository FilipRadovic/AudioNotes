package com.frcoding.audionotes.presentation.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frcoding.audionotes.R
import com.frcoding.audionotes.presentation.screens.entry.components.EntryTextField

@Composable
fun RowScope.TopicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = stringResource(R.string.topic),
    showLeadingIcon: Boolean = true
) {
    EntryTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .align(Alignment.CenterVertically),
        hintText = hintText,
        leadingIcon = {
            if (showLeadingIcon) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    )
}