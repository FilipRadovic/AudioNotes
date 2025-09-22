package com.frcoding.audionotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.frcoding.audionotes.navigation.RootAppNavigation
import com.frcoding.audionotes.navigation.rememberNavigationState
import com.frcoding.audionotes.presentation.theme.AudioNotesTheme
import com.frcoding.audionotes.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fromWidget = intent.getBooleanExtra(Constants.KEY_WIDGET_INTENT, false)



        enableEdgeToEdge()

        setContent {
            AudioNotesTheme {
                val navigationState = rememberNavigationState()
                RootAppNavigation(
                    navigationState = navigationState,
                    isDataLoaded = { keepSplashOpened = false },
                    isLaunchedFromWidget = fromWidget,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }


    }
}