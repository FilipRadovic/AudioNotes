package com.frcoding.audionotes

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.frcoding.audionotes.navigation.RootAppNavigation
import com.frcoding.audionotes.navigation.rememberNavigationState
import com.frcoding.audionotes.presentation.theme.AudioNotesTheme
import com.frcoding.audionotes.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplashOpened = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { keepSplashOpened = it.getBoolean(KEY_SPLASH) }

        val fromWidget = intent.getBooleanExtra(Constants.KEY_WIDGET_INTENT, false)

        CoroutineScope(Dispatchers.IO).launch {
            deleteTempFiles(this@MainActivity)
        }

        installSplashScreen().setKeepOnScreenCondition { keepSplashOpened }
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(KEY_SPLASH, keepSplashOpened)
    }

    private fun deleteTempFiles(context: Context) {
        val outputDir = context.filesDir
        if (outputDir != null && outputDir.exists()) {
            val temFiles = outputDir.listFiles { file ->
                file.name.startsWith("temp")
            }
            temFiles?.forEach { file ->
                file.delete()
            }
        } else throw IllegalStateException("Music directory does not exist or is not accessible.")
    }

    companion object {
        private const val KEY_SPLASH = "KEY_SPLASH"
    }
}