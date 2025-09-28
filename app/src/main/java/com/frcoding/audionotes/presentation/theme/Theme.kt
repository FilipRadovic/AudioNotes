package com.frcoding.audionotes.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = NotesBlue,
    background = NotesLightBlue,
    surface = Color.White,
    surfaceVariant = NotesSofBlue,
    onPrimary = Color.White,
    onSurface = NotesDark,
    onSurfaceVariant = NotesGrayBlue,
    secondary = NotesDarkSteel,
    outline = NotesMutedGray,
    outlineVariant = NotesLightGray,
    errorContainer = NotesSoftPeach,
    onErrorContainer = NotesRed,
    onPrimaryContainer = NotesPaleBlue,
    surfaceTint = NotesDeepBlue,
    secondaryContainer = NotesDustyBlue
)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */

@Composable
fun AudioNotesTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}