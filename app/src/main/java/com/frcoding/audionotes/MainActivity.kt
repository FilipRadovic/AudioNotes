package com.frcoding.audionotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.frcoding.audionotes.navigation.RootAppNavigation
import com.frcoding.audionotes.navigation.rememberNavigationState
import com.frcoding.audionotes.presentation.theme.AudioNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioNotesTheme {
                val navigationState = rememberNavigationState()
                RootAppNavigation(
                    navigationState = navigationState
                )
            }
        }
    }
}