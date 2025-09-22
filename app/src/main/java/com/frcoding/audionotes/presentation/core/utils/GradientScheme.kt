package com.frcoding.audionotes.presentation.core.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.frcoding.audionotes.presentation.theme.SofBlue

object GradientScheme {
    val PrimaryGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF578CFF),
            Color(0xFF1F70F5)
        )
    )
    val DisabledSolidColor = Brush.linearGradient(
        colors = listOf(
            SofBlue,
            SofBlue
        )
    )
    val FABRecordingBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3982F6).copy(alpha = 0.2f),
            Color(0xFF0E5FE0).copy(alpha = 0.2f)
        )
    )
    val FABPulsatingBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3982F6).copy(alpha = 0.1f),
            Color(0xFF0E5FE0).copy(alpha = 0.1f)
        )
    )
}